package edu.uwlax.himal;

import edu.uwlax.himal.data.DataBootstrapper;
import edu.uwlax.himal.data.Database;
import edu.uwlax.himal.data.SwapDatabase;

import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.*;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Himalayan database initialization and entrypoint
 *
 * @author Zach Goethel
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
@SpringBootApplication
@Component
@Controller
public class Himalayan implements CommandLineRunner, WebMvcConfigurer
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final File configFile = new File("config.json");

    private JSONObject config;

    public SwapDatabase members;
    public SwapDatabase peaks;
    public SwapDatabase expeditions;

    public SwapDatabase auxPeaks;

    /**
     * Loads the configuration file from the working directory; copies default values if the file
     * does not yet exist
     *
     * @throws RuntimeException If the default config could not be found or an error occurs while
     *                          reading/writing configuration files
     */
    //TODO ACCOUNT FOR CHANGES IN CONFIG SCHEMA (MERGE IN NEW DEFAULTS)
    private void initConfig()
    {
        try
        {
            // If not exists, load default.  Does not account for updated config schemas.
            if (!configFile.exists())
            {
                log.debug("Configuration file does not exist; creating default . . .");

                InputStream defaultStream = Himalayan.class.getClassLoader().getResourceAsStream(
                        "default-config.json");
                if (defaultStream == null)
                    throw new RuntimeException("Could not load default config file from classpath");

                BufferedReader reader = new BufferedReader(new InputStreamReader(defaultStream));
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(configFile));

                // Directly copy from default classpath resource to file
                String line;
                while ((line = reader.readLine()) != null)
                    writer.write(String.format("%s\n", line));
                reader.close();
                writer.close();
            }

            log.info("Loading configuration file . . .");

            StringBuilder configText = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile)));

            String line;
            while ((line = reader.readLine()) != null)
                configText.append(String.format("%s\n", line));
            reader.close();

            config = new JSONObject(configText.toString());
        } catch (IOException ex)
        {
            throw new RuntimeException("Failed to initialize the configuration file", ex);
        }
    }

    private void recursiveDelete(File file)
    {
        if (file.exists())
        {
            if (file.isDirectory())
            {
                for (File child : Objects.requireNonNull(file.listFiles()))
                    recursiveDelete(child);
            }

            file.delete();
        }
    }

    /*
     * CREDIT:
     * https://www.codejava.net/java-se/file-io/programmatically-extract-a-zip-file-using-java
     */
    private void unzip(String zipFilePath, String destDirectory) throws IOException
    {
        File destDir = new File(destDirectory);
        if (!destDir.exists())
            destDir.mkdir();

        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();

        // Iterates over entries in the zip file
        while (entry != null)
        {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory())
                // If the entry is a file, extracts it
                extractFile(zipIn, filePath);
            else
            {
                // If the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdirs();
            }

            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }

        zipIn.close();
    }

    /*
     * CREDIT:
     * https://www.codejava.net/java-se/file-io/programmatically-extract-a-zip-file-using-java
     */
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException
    {
        File f = new File(filePath);

        if (!f.exists())
        {
            if (f.getParentFile() != null)
                f.getParentFile().mkdirs();
            f.createNewFile();
        }

        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));

        byte[] bytesIn = new byte[4096];
        int read;
        while ((read = zipIn.read(bytesIn)) != -1)
            bos.write(bytesIn, 0, read);

        bos.close();
    }

    //TODO MOVE TO OWN CLASS/CREATE ABSTRACT VERSION
    private void downloadPrerequisites()
    {
        // Development mode does not download the packages every time (saves time)
        if (config.getBoolean("dev-mode"))
            return;

        try
        {
            log.info("Downloading latest database packages . . .");

            File packageRoot = new File(getConfig().getString("package-root-dir"));
            recursiveDelete(packageRoot);

            InputStream in = null;

            // Himalayan Database site returns error 503 randomly; try multiple times
            for (int i = 0; i < 5; i ++)
                try
                {
                    in = new URL(getConfig().getString("sync-remote")).openStream();
                    break;
                } catch (IOException ex)
                {
                    log.error(String.format("Connection attempt %d/5 failed", i + 1), ex);
                }
            if (in == null)
                throw new RuntimeException("Failed to connect to Himalayan Database; no more retries");

            Files.copy(in, Paths.get(getConfig().getString("sync-local")),
                    StandardCopyOption.REPLACE_EXISTING);

            log.info("Extracting latest database packages . . .");
            unzip(getConfig().getString("sync-local"), System.getProperty("user.dir"));

            new File(getConfig().getString("sync-local")).delete();
        } catch (IOException ex)
        {
            throw new RuntimeException("Failed to download latest database package", ex);
        }
    }

    /**
     * Sets up the automated loading of Himalayan Database content
     */
    private void initBootstrapper()
    {
        log.info("Initializing automated data updates . . .");

        // PRIMARY DATABASES
        members = Database.createEmptySwap(getConfig().getString("table-members"));
        peaks = Database.createEmptySwap(getConfig().getString("table-peaks"));
        expeditions = Database.createEmptySwap(getConfig().getString("table-expeditions"));

        DataBootstrapper bootstrapper = DataBootstrapper.createFromDBFDirectory(getConfig()
                .getString("table-root-dir"), this::downloadPrerequisites);

        bootstrapper.linkDatabase(members, false);
        bootstrapper.linkDatabase(peaks, false);
        bootstrapper.linkDatabase(expeditions, false);

        // AUXILIARY DATA SOURCES
        auxPeaks = Database.createEmptySwap("peak-coords");

        DataBootstrapper auxBootstrapper = DataBootstrapper.createFromCSVDirectory("aux-data");

        auxBootstrapper.linkDatabase(auxPeaks, false);

        // GENERAL OPERATIONS
        long millis = getConfig().getLong("sync-interval-millis");
        log.debug(String.format("Databases set to update every %d milliseconds", millis));

        bootstrapper.syncAtInterval(millis);
        auxBootstrapper.syncAtInterval(millis);
    }

    /**
     * Application non-static entrypoint; configures the application and begins main processes
     *
     * @throws RuntimeException If any initialization step catches or detects an error
     */
    @Override
    public void run(String ... args)
    {
        log.info(String.format("Initializing application version %s . . .", Himalayan.VERSION));

        initConfig();
        initBootstrapper();

        log.info("Initialization complete!\n");
    }

    @GetMapping("/")
    public String indexPage()
    {
        return "index";
    }

    /**
     * @return Primary configuration file loaded from the application working directory
     */
    public JSONObject getConfig()
    {
        return config;
    }


    public static final String VERSION = "1.0-SNAPSHOT";

//    private static Himalayan instance = null;
//
//    /**
//     * @return Singleton {@link Himalayan} instance
//     */
//    public static Himalayan getInstance()
//    {
//        if (instance == null)
//            instance = new Himalayan();
//
//        return instance;
//    }

    public static void main(String[] args)
    {
//        Himalayan.getInstance().start();

        SpringApplication.run(Himalayan.class, args);
    }
}
