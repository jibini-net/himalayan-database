package edu.uwlax.himal.data.impl;

import edu.uwlax.himal.data.Database;
import edu.uwlax.himal.data.SwapDatabase;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.dbf.DBFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ToXMLContentHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

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
        Parser parser = new DBFParser();
        ContentHandler handler = new BodyContentHandler(new ToXMLContentHandler());

        try
        {
            log.debug(String.format("Parsing DBF for '%s' to XHTML text . . .", database.getTableName()));

            InputStream stream = new FileInputStream(String.format("%s/%s.dbf", rootDir,
                    database.getTableName()));
            parser.parse(stream, handler, new Metadata(), new ParseContext());

            stream.close();
        } catch (Exception ex)
        {
            log.error("Failed to parse DBF database file(s); sync failure", ex);
            return;
        }

        try
        {
            log.debug("Processing XHTML document table values . . .");

            // Parse XHTML from content handler
            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(new StringReader(handler.toString())));

            // Grab list of table header values
            NodeList thNodes = document.getElementsByTagName("thead")
                    .item(0)
                    .getChildNodes();
            ArrayList<String> headers = new ArrayList<>();

            // Populate list with text contents of th elements
            for (int i = 0; i < thNodes.getLength(); i++)
            {
                Node node = thNodes.item(i);

                if (node.getNodeName().equals("th"))
                    headers.add(node.getTextContent());
            }

            // Grab list of table row nodes
            NodeList trNodes = document.getElementsByTagName("tbody")
                    .item(0)
                    .getChildNodes();
            ArrayList<JSONObject> rows = new ArrayList<>();

            // Iterate through table body rows
            for (int row = 0; row < trNodes.getLength(); row++)
                if (trNodes.item(row).getNodeName().equals("tr"))
                {
                    // Keep independent count of cell vs. node number
                    int cell = 0;

                    NodeList rowCells = trNodes.item(row)
                            .getChildNodes();
                    JSONObject rowValues = new JSONObject();

                    for (int i = 0; i < rowCells.getLength(); i++)
                    {
                        if (rowCells.item(i).getNodeName().equals("td"))
                            rowValues.put(headers.get(cell++), rowCells.item(i).getTextContent());
                    }

                    rows.add(rowValues);
                }

            database.swap(Database.createImmutable(database.getTableName(), rows));
        } catch (Exception ex)
        {
            log.error("Failed to parse XHTML document source", ex);
        }
    }
}
