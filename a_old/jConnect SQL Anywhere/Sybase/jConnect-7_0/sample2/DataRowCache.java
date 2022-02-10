/*
Confidential property of Sybase, Inc.
Copyright 2001, 2012
Sybase, Inc.  All rights reserved.
Unpublished rights reserved under U.S. copyright laws.

This software contains confidential and trade secret information of Sybase,
Inc.   Use,  duplication or disclosure of the software and documentation by
the  U.S.  Government  is  subject  to  restrictions set forth in a license
agreement  between  the  Government  and  Sybase,  Inc.  or  other  written
agreement  specifying  the  Government's rights to use the software and any
applicable FAR provisions, for example, FAR 52.227-19.
Sybase, Inc. One Sybase Drive, Dublin, CA 94568, USA
*/

package sample2;
import java.util.Enumeration;

/**
 * An interface for manipulating a cache of DataRow objects.
 * Used internally by the Query and ExtendedResultSet objects.
 * @author Eric Giguere 
 * @version 1.0, 06 Jun 1997
 */
public interface DataRowCache
{

    /**
     * Determines if the given DataRow has been cached.
     * @return true if it is currently in this cache, else false.
     */
    boolean isCached(int index);

    /**
     * Gets the number of DataRow elements in the cache.
     * @return The number of elements.
     */
    int getDataRowCount();

    /**
     * Gets a specific DataRow element from the cache.
     *
     * @param index A value from 0 to <tt>getDataRowCount()</tt>-1.
     *              Do not confuse this value with the row number,
     *              it is simply the index into the cache.
     * @return The desired row or <tt>null</tt> if the row does
     *         not exist.
     */
    DataRow getDataRow(int index);

    /**
     * Gets the index of a DataRow element from the cache.
     *
     * @param row The DataRow object to find.
     * @return The index of the element in the cache, or -1 if
     *         the element is not in the cache.
     */
    int getDataRowIndex(DataRow row);

    /** @return An enumeration of DataRow elements in the cache. */
    Enumeration getDataRows();

    /**
     * Adds a DataRow object to the cache.
     *
     * @param row The object to add.
     * @return If the row was added, returns <code>true</code>,
     *         otherwise returns <code>false</code>.
     */
    boolean addDataRow(DataRow row);

    /**
     * Inserts a DataRow object into the cache.
     *
     * @param index The index at which to insert the object.
     * @param row The object to insert.
     * @return If the row was inserted, returns <code>true</code>,
     *         otherwise returns <code>false</code>.
     */
    boolean insertDataRow(int index, DataRow row);

    /**
     * Removes a DataRow object from the cache.
     *
     * @param row The object to remove.
     * @return If the row was removed, returns <code>true</code>,
     *         otherwise returns <code>false</code>.
     */
    boolean removeDataRow(DataRow row);

    /**
     * Removes a DataRow object from the cache.
     *
     * @param index The index of the object to remove.
     * @return If the row was removed, returns <code>true</code>,
     *         otherwise returns <code>false</code>.
     */
    boolean removeDataRow(int index);

    /**
     * Replaces a DataRow object with another.
     *
     * @param oldRow The current object in the cache.
     * @param newRow The new object that will take its place.
     * @return If the row was replaced, returns <code>true</code>,
     *         otherwise returns <code>false</code>.
     */
    boolean replaceDataRow(DataRow oldRow, DataRow newRow);

    /**
     * Replaces a DataRow object with another.
     *
     * @param index The index of the object in the cache.
     * @param newRow The new object that will take its place.
     *
     * @return If the row was replaced, returns <code>true</code>,
     *         otherwise returns <code>false</code>.
     */
    boolean replaceDataRow(int index, DataRow newRow);

    /** Removes all DataRow objects from the cache. */
    void clear();
}
