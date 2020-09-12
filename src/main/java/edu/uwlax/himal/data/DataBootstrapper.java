package edu.uwlax.himal.data;

import edu.uwlax.himal.data.impl.DecoratedDatabaseImpl;

import java.util.Timer;
import java.util.TimerTask;
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
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(this::syncLinkedDatabases,
                0, intervalMillis, TimeUnit.MILLISECONDS);
    }
}
