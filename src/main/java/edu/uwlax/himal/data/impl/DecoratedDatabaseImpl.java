package edu.uwlax.himal.data.impl;

import edu.uwlax.himal.data.Database;
import edu.uwlax.himal.data.SwapDatabase;
import org.json.JSONObject;

/**
 * Decorated {@link Database} which allows swapping of backing data
 *
 * @author Zach Goethel
 */
public class DecoratedDatabaseImpl implements SwapDatabase
{
    /**
     * Decorated instance
     */
    private Database database = null;

    @Override
    public String getTableName()
    {
        if (database == null)
            throw new IllegalStateException("Backing swap database cannot be null");

        return database.getTableName();
    }

    @Override
    public Iterable<JSONObject> getContents()
    {
        if (database == null)
            throw new IllegalStateException("Backing swap database cannot be null");

        return database.getContents();
    }

    @Override
    public void swap(Database database)
    {
        this.database = database;
    }
}
