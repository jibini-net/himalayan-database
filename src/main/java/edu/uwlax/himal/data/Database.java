package edu.uwlax.himal.data;

import edu.uwlax.himal.data.impl.DecoratedDatabaseImpl;
import edu.uwlax.himal.data.impl.ImmutableDatabaseImpl;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Simple collection of JSON data loaded from a backing source
 *
 * @author Zach Goethel
 */
public interface Database
{
    /**
     * @return SQL-like name of data collection
     */
    String getTableName();

    /**
     * @return Entire contents of the database
     */
    Iterable<JSONObject> getContents();

    /**
     * Performs a linear search of the contents of this database
     *
     * @param keyName JSON element name to search by
     * @param keyValue Value to search for
     *
     * @return First matching entry with the given key/value pair; null if no match is found
     */
    default JSONObject get(String keyName, Object keyValue)
    {
        for (JSONObject object : getContents())
        {
            if (object.get(keyName).equals(keyValue))
                return object;
        }

        return null;
    }


    /*=======================================*
     *                FACTORY                *
     *=======================================*/

    /**
     * Creates an immutable database backed by a given collection of data
     *
     * @param tableName {@link #getTableName}
     * @param contents {@link #getContents}
     *
     * @return Created instance
     */
    static Database createImmutable(String tableName, Iterable<JSONObject> contents)
    {
        return new ImmutableDatabaseImpl(tableName, contents);
    }

    /**
     * Creates a swappable database from the given backing data
     *
     * @param database Initial backing database
     *
     * @return Created instance
     */
    static SwapDatabase wrap(Database database)
    {
        SwapDatabase result = new DecoratedDatabaseImpl();
        result.swap(database);

        return result;
    }

    /**
     * Creates an empty swappable database backed by an empty database
     *
     * @param tableName {@link #getTableName}
     *
     * @return Created instance
     */
    static SwapDatabase createEmptySwap(String tableName)
    {
        return Database.wrap(Database.createImmutable(tableName, new ArrayList<>()));
    }
}
