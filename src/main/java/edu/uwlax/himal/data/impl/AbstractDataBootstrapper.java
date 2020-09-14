package edu.uwlax.himal.data.impl;

import edu.uwlax.himal.data.DataBootstrapper;
import edu.uwlax.himal.data.SwapDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractDataBootstrapper implements DataBootstrapper
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final List<SwapDatabase> linked = Collections.synchronizedList(new ArrayList<>());
    private final Runnable[] preTasks;

    public AbstractDataBootstrapper(Runnable[] preTasks)
    {
        this.preTasks = preTasks;
    }

    private void runPreTasks()
    {
        for (Runnable task : preTasks)
            try
            {
                task.run();
            } catch (Throwable thrown)
            {
                log.error("Data bootstrap prerequisite task failed with an error", thrown);
            }
    }

    public abstract void populateDatabase(SwapDatabase database);

    @Override
    public void linkDatabase(SwapDatabase database, boolean load)
    {
        linked.add(database);

        if (load)
        {
            runPreTasks();
            populateDatabase(database);
        }
    }

    @Override
    public void syncLinkedDatabases()
    {
        log.info("Starting update sync of all databases . . .");
        long startTime = System.nanoTime();

        runPreTasks();
        for (SwapDatabase database : linked)
            populateDatabase(database);

        double seconds = ((double)System.nanoTime() - startTime) / 1000000000;
        log.info(String.format("Update complete! (%fs)", seconds));
    }
}
