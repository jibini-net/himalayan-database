package edu.uwlax.himal.data.impl;

import edu.uwlax.himal.data.Database;
import edu.uwlax.himal.data.SwapDatabase;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CSVDataBootstrapper extends AbstractDataBootstrapper
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final String rootDir;

    public CSVDataBootstrapper(String rootDir, Runnable[] preTasks)
    {
        super(preTasks);

        this.rootDir = rootDir;
    }

    @Override
    public void populateDatabase(SwapDatabase database)
    {
        try
        {
            String fileName = String.format("%s/%s.csv", rootDir, database.getTableName());
            log.info(String.format("Populating database auxiliary CSV data ('%s') . . .", fileName));

            Scanner scanner = new Scanner(new File(fileName));

            if (!scanner.hasNextLine())
                throw new RuntimeException("No headers are specified in this CSV");
            String headersLine = scanner.nextLine();
            String[] headers = headersLine.split(",");

            List<JSONObject> contents = new ArrayList<>();

            while (scanner.hasNextLine())
            {
                String line = scanner.nextLine();
                String[] split = line.split(",");

                JSONObject lineData = new JSONObject();

                for (int i = 0; i < split.length; i ++)
                    lineData.put(headers[i].trim(), split[i].trim());

                contents.add(lineData);
            }

            database.swap(Database.createImmutable(database.getTableName(), contents));

            scanner.close();
        } catch (FileNotFoundException ex)
        {
            log.error("Failed to collect data from CSV file location", ex);
        } catch (Exception ex)
        {
            log.error("An internal or parsing error has occurred with a CSV file", ex);
        }
    }
}
