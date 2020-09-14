package edu.uwlax.himal.data.impl;

import edu.uwlax.himal.data.Database;
import edu.uwlax.himal.data.SwapDatabase;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Implementation of the data bootstrapper which loads data via a JDBC driver
 *
 * @author Zach Goethel
 */
public class JDBCDataBootstrapperImpl extends AbstractDataBootstrapper
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Connection connection;

    public JDBCDataBootstrapperImpl(Connection connection, Runnable[] preTasks)
    {
        super(preTasks);

        this.connection = connection;
    }

    @Override
    public void populateDatabase(SwapDatabase database)
    {
        try
        {
            log.debug(String.format("Querying table '%s' for database updates . . .",
                    database.getTableName()));

            ResultSet results = connection.createStatement() .executeQuery(String.format(
                    "select * from %s", database.getTableName()));
            ArrayList<JSONObject> rows = new ArrayList<>();

            log.debug("Query complete; processing . . .");

            while (results.next())
            {
                JSONObject row = new JSONObject();
                for (int c = 0; c < results.getMetaData().getColumnCount(); c++)
                    row.put(results.getMetaData().getColumnName(c), results.getObject(c));

                rows.add(row);
            }

            log.debug(String.format("Ready to swap database '%s' with new data", database.getTableName()));

            database.swap(Database.createImmutable(database.getTableName(), rows));
        } catch (SQLException ex)
        {
            log.error(String.format("Failed to query table '%s' for database updates",
                    database.getTableName()), ex);
        }
    }
}
