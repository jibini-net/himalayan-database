package edu.uwlax.himal.data;

/**
 * Decorated {@link Database} which allows the backing data to be swapped out without affecting
 * normal operation of the program
 *
 * @author Zach Goethel
 */
public interface SwapDatabase extends Database
{
    /**
     * Swaps out the backing database (decorated instance) with new data
     *
     * @param database Replacement backing database
     */
    void swap(Database database);
}
