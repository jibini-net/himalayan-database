package edu.uwlax.himal;

import edu.uwlax.himal.data.DataBootstrapper;
import edu.uwlax.himal.data.Database;
import edu.uwlax.himal.data.SwapDatabase;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Himalayan database initialization and entrypoint
 *
 * @author Zach Goethel
 */
public class Himalayan
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final File configFile = new File("config.json");

    private JSONObject config;

    private SwapDatabase expeditions;
    private SwapDatabase members;
    private SwapDatabase peaks;

    private DataBootstrapper bootstrapper = null;

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
                log.debug("Configuration file does not exist; creating default");

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

            log.debug("Loading configuration file");

            StringBuilder configText = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(configFile)));

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

    /**
     * Sets up the automated loading of Himalayan Database content
     */
    private void initBootstrapper()
    {
        log.debug("Initializing automated data updates");

        expeditions = Database.createEmptySwap(config.getString("table-expeditions"));
        members = Database.createEmptySwap(config.getString("table-members"));
        peaks = Database.createEmptySwap(config.getString("table-peaks"));

        //TODO INIT BOOTSTRAPPER TO NON-NULL
        bootstrapper.linkDatabase(expeditions, false);
        bootstrapper.linkDatabase(members, false);
        bootstrapper.linkDatabase(peaks, false);

        long millis = config.getLong("sync-interval-millis");
        log.debug(String.format("Scheduling automated update every %d milliseconds", millis));

        bootstrapper.syncAtInterval(millis);
    }

    /**
     * Application non-static entrypoint; configures the application and begins main processes
     *
     * @throws RuntimeException If any initialization step catches or detects an error
     */
    public void start()
    {
        log.info(String.format("Initializing application version %s", Himalayan.VERSION));

        initConfig();
        initBootstrapper();

        log.info("Goodbye!");
    }

    /**
     * @return Primary configuration file loaded from the application working directory
     */
    public JSONObject getConfig()
    {
        return config;
    }


    public static final String VERSION = "1.0-SNAPSHOT";

    private static Himalayan instance = null;

    /**
     * @return Singleton {@link Himalayan} instance
     */
    public static Himalayan getInstance()
    {
        if (instance == null)
            instance = new Himalayan();

        return instance;
    }

    public static void main(String[] args)
    {
        try
        {
            Class.forName("com.dbschema.xbase.DbfJdbcDriver");
        } catch (ClassNotFoundException ex)
        {
            throw new RuntimeException("Could not find DBF JDBC driver; please ensure it is on the classpath", ex);
        }

        Himalayan.getInstance().start();
    }
}
