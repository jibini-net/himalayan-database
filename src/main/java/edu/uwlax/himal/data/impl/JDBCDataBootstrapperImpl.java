package edu.uwlax.himal.data.impl;

import edu.uwlax.himal.data.DataBootstrapper;
import edu.uwlax.himal.data.Database;
import edu.uwlax.himal.data.SwapDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of the data bootstrapper which loads data via a JDBC driver
 *
 * @author Zach Goethel
 */
public class JDBCDataBootstrapperImpl implements DataBootstrapper
{
    private final List<Database> linked = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void linkDatabase(SwapDatabase database, boolean load)
    {
        linked.add(database);

        if (load)
        {
            //TODO
        }
    }

    @Override
    public void syncLinkedDatabases()
    {
        //TODO
    }
}
