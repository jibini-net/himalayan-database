package edu.uwlax.himal.data.impl;

import edu.uwlax.himal.data.Database;
import org.json.JSONObject;

/**
 * Basic constructable and immutable database implementation
 *
 * @author Zach Goethel
 */
public class ImmutableDatabaseImpl implements Database
{
    private final String tableName;
    private final Iterable<JSONObject> contents;

    public ImmutableDatabaseImpl(String tableName, Iterable<JSONObject> contents)
    {
        this.tableName = tableName;
        this.contents = contents;
    }

    @Override
    public String getTableName()
    {
        return tableName;
    }

    @Override
    public Iterable<JSONObject> getContents()
    {
        return contents;
    }
}
