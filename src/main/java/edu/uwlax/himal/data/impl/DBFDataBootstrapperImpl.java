package edu.uwlax.himal.data.impl;

import edu.uwlax.himal.data.Database;
import edu.uwlax.himal.data.SwapDatabase;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.dbf.DBFParser;
import org.apache.tika.sax.BodyContentHandler;

import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.xml.sax.ContentHandler;

import java.io.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the data bootstrapper which loads data via Apache Tika's DBF parser
 *
 * @author Zach Goethel
 */
public class DBFDataBootstrapperImpl extends AbstractDataBootstrapper
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final String rootDir;

    public DBFDataBootstrapperImpl(String rootDir, Runnable[] preTasks)
    {
        super(preTasks);

        this.rootDir = rootDir;
    }

    @Override
    public void populateDatabase(SwapDatabase database)
    {
        PrintWriter writer;

        try
        {
            writer = new PrintWriter(new File("parsed.out"));
        } catch (FileNotFoundException ex)
        {
            throw new RuntimeException("Failed to open print writer to output file", ex);
        }

        Parser parser = new DBFParser();
        ContentHandler handler = new BodyContentHandler(writer);

        List<String> headers;

        try
        {
            log.debug(String.format("Parsing DBF for '%s' to temporary text file . . .", database.getTableName()));

            String fileName = String.format("%s/%s.DBF", rootDir, database.getTableName());

            headers = DBFParsingUtil.getHeaders(new File(fileName));

            InputStream stream = new FileInputStream(fileName);
            parser.parse(stream, handler, new Metadata(), new ParseContext());

            writer.flush();
            writer.close();

            stream.close();
        } catch (Exception ex)
        {
            log.error("Failed to parse DBF database file(s); sync failure", ex);
            return;
        }

        try
        {
            log.debug("Processing parsed document table values . . .");

            BufferedReader input = new BufferedReader(new FileReader("parsed.out"));

            String firstLine = input.readLine();
            String[] firstLineValues = firstLine.substring(1).split("\t");

//            for (String h : headers)
//                System.out.print(h + "; ");
//            System.out.println();

            // Grab list of table row nodes
            ArrayList<JSONObject> rows = new ArrayList<>();

            String line;

            // Iterate through table body rows
            while ((line = input.readLine()) != null)
            {
                if (line.length() == 0)
                    continue;

                JSONObject rowValues = new JSONObject();
                String[] split = line.substring(1).split("\t");

                for (int i = 0; i < split.length; i++)
                    try
                    {
                        rowValues.put(firstLineValues[i], split[i]);
                    } catch (IndexOutOfBoundsException ex)
                    {
                        log.error("Failed to process line values (skipping)", ex);
                    }

                rows.add(rowValues);
            }

            // Dirty workaround for Tika issue (Tika writes the first line inline
            // with the headers); see also DBFParsingUtil
            JSONObject firstEntry = new JSONObject();
            for (int i = 0; i < headers.size() && headers.size() + i < firstLineValues.length; i++)
                    firstEntry.put(headers.get(i), firstLineValues[headers.size() + i]);
            rows.add(0, firstEntry);

            input.close();

            database.swap(Database.createImmutable(database.getTableName(), rows));
        } catch (Exception ex)
        {
            log.error("Failed to parse XHTML document source", ex);
        }
    }
}
