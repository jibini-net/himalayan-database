package edu.uwlax.himal.data;

import edu.uwlax.himal.data.impl.CSVDataBootstrapper;
import edu.uwlax.himal.data.impl.DecoratedDatabaseImpl;
import edu.uwlax.himal.data.impl.DBFDataBootstrapperImpl;
import edu.uwlax.himal.data.impl.JDBCDataBootstrapperImpl;

import java.sql.Connection;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Loads data from the online database distribution into local stores
 *
 * @author Zach Goethel
 */
public interface DataBootstrapper
{
    /**
     * Links the given database to be managed by this object
     *
     * @param database Database whose data should be synced
     * @param load Whether or not to load the data immediately
     */
    void linkDatabase(SwapDatabase database, boolean load);

    /**
     * Iterates through linked databases and updates/swaps their contents
     *
     * @see #linkDatabase
     * @see #syncAtInterval
     * @see DecoratedDatabaseImpl
     */
    void syncLinkedDatabases();

    /**
     * Schedules a task to sync all linked databases at a given interval
     *
     * @param intervalMillis Time in milliseconds between database updates
     *
     * @see #linkDatabase
     * @see #syncLinkedDatabases
     * @see DecoratedDatabaseImpl
     */
    default void syncAtInterval(long intervalMillis)
    {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(this::syncLinkedDatabases, 0,
                intervalMillis, TimeUnit.MILLISECONDS);
    }


    /*=======================================*
     *                FACTORY                *
     *=======================================*/

    /**
     * Creates a sync management object to sync data from a JDBC connection; allows runnable tasks
     * in order to download requisite files, etc.
     *
     * @param connection Existing open JDBC connection from which to load data
     * @param preTasks Runnable tasks to run before querying the connection
     *
     * @return Created instance
     */
    static DataBootstrapper createFromConnection(Connection connection, Runnable ... preTasks)
    {
        return new JDBCDataBootstrapperImpl(connection, preTasks);
    }

    /**
     * Creates a sync management object to sync data from a folder of DBF files; allows runnable
     * tasks in order to download requisite files, etc.
     *
     * @param rootDir Relative path to root directory containing DBF files
     * @param preTasks Runnable tasks to run before querying the connection
     *
     * @return Created instance
     */
    static DataBootstrapper createFromDBFDirectory(String rootDir, Runnable ... preTasks)
    {
        return new DBFDataBootstrapperImpl(rootDir, preTasks);
    }

    /**
     * Creates a sync management object to sync data from a folder of CSV files; allows runnable
     * tasks in order to download requisite files, etc.
     *
     * @param rootDir Relative path to root directory containing CSV files
     * @param preTasks Runnable tasks to run before querying the connection
     *
     * @return Created instance
     */
    static DataBootstrapper createFromCSVDirectory(String rootDir, Runnable ... preTasks)
    {
        return new CSVDataBootstrapper(rootDir, preTasks);
    }
}
