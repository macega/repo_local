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

import java.sql.*;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Map;
import java.net.URL;

import com.sybase.jdbc4.utils.Debug;
import com.sybase.jdbc4.jdbc.ErrorMessage;

/**
* <P>This is an implementation of the <CODE>sample2.ScrollableResultSet</CODE>
* interface which is little more than a wrapper around a 
* <CODE>java.sql.ResultSet</CODE>. The implementation is scroll-insensitive
* and read-only.
* 
* <P> Users of this class should simply pass their
* <CODE>java.sql.ResultSet</CODE> to this class' only constructor.  This is
* the only public method defined here; all other methods are defined by the
* ScrollableResultSet interface.
*
* <P> Rows are cached "on demand".  That is, when the cursor visits a row for
* the first time, that row is cached.  Once the end of the result set is
* reached, the entire result set is in cache.  
*
* <P> One could choose to cache all rows initially.  To achieve this behavior,
* users should just execute <CODE>last()</CODE> immediately after constructing
* the ExtendedResultSet. 
*
* <P> <B>Note:</B> This implementation does not remove rows from its cache.
* Therefore, users of this class should exercise caution if very large
* ResultSets are to be used.
*
* @see sample2.ScrollableResultSet
*/
public class ExtendedResultSet implements ScrollableResultSet
{

    /** holds a reference to the result set that
	     created this ExtendedResultSet */  
    private ResultSet _parentRS;		

    /** holds a reference to the meta data for _parentRS */
    private ResultSetMetaData _rsmd; 

    /** the row where the cursor is currently positioned */
    private int _currentIndex;			

    /** the number/index of the last row of the result set */
    private int _lastIndex;				

    /** the number/index of the deepest row visited. */
    private int _upperBound;		

    /** where cached rows are maintained */
    private DataRowCache _cache;	

    /** the row currently pointed to by the cursor */
    private DataRow _currentRow;		

    /** stores the user's hint for read direction (ignored) */
    private int _fetchDirection;  

    /** stores the user's hint for number of rows to pull at a time (ignored) */
    private int _fetchSize;		  

    /** number of columns in this result set (from meta data) */
    private int _numColumns;				

    /** constant used by _lastIndex.  It means that
    * the last row in the result set has not yet been read */
    final static private int NOT_ASSIGNED = -9; 

    /** error message to display when a method is called
    * with the cursor in an inappropriate state
    * (e.g., not pointing to a readable row) */
    final static private String ROW_INVALID = "Cursor not positioned on a valid row";

    /**
  *
  * <P>Construct a new ExtendedResultSet using a standard JDBC 1.0 ResultSet
  *
  */
    public ExtendedResultSet(ResultSet rs)
    {
        _parentRS = rs;
        try
        {
            _rsmd = rs.getMetaData();
            _numColumns = _rsmd.getColumnCount();
        }
        catch (SQLException sqlE)
        {
            doAssert(false, "MetaData was unavailable");
        }
        _fetchDirection = FETCH_FORWARD; // default direction is FORWARD

        _currentRow = null;
        _currentIndex = 0;
        _upperBound = _currentIndex; // indicates the "deepest" row that cursor has read
        _lastIndex = NOT_ASSIGNED;   // indicates the index of the last valid row 

        _cache = new SimpleRowCache(); // holds the rows after they're read
    }

    /**
      * The no-arg constructor is purposefully hidden from users of this class.
      */
    private ExtendedResultSet()
    {
    }

    public boolean previous() throws SQLException
    {
        if (isBeforeFirst()) // cursor is before the first row
        {
            return false;		  // previous() makes no sense here -- don't do anything
        }
        else if (isFirst())  // cursor is on the first row
        {
            _currentIndex--;     // position the index before first
            _currentRow = null;  // make the current row invalid
            return false;		  
        }
        else // this is a typical previous() -- cursor is within the result set 
        {
            _currentIndex--;
            doAssert( _cache.isCached(_currentIndex), "Row was not cached"); 
            _currentRow = readCurrentRow(); // this will always pull from the cache	  
            return true;
        }
    }

    public boolean next() throws SQLException
    {
        if (isAfterLast()) // cursor is one after last row 
        {
            return false;  // next() makes no sense here -- don't do anything
        }

        else if (isLast()) // cursor is on the last row
        {
            _currentIndex++;    // position the index after last row
            _currentRow = null; // make the current row invalid
            return false;		  
        }

        else if (_cache.isCached(_currentIndex+1)) // cursor has seen this row before
        {
            _currentIndex++;					   
            _currentRow = readCurrentRow();	// this will always read from cache
            return true;
        }

        // this is uncharted territory... (unseen rows)

        else if (!_parentRS.next()) // advance the "real" result set, check for end
        {
            // this *must* be the first time cursor has reached the end
            doAssert( (_lastIndex == NOT_ASSIGNED), "End of result set was not recognized during next()" );

            _lastIndex = _currentIndex; // record the index of the last row
            _currentIndex++; 				// set current index one after the result set
            _currentRow = null;			// make the current row invalid

            return false; 
        }
        else  // cursor is within the result set
        {
            _upperBound = ++_currentIndex;  // advance the index, and then update the upperBound
            _currentRow = readCurrentRow(); // read this row from the result set, and add to cache	  
            return true;
        }
    }
    // end next()

    public boolean first() throws SQLException
    {
        return absolute(1);
    }

    public boolean last() throws SQLException
    {
        // absolute(-1) doesn't do us much good here.  Cursor has either read and
        // cached the last row, or has no idea where (what index) the last row is.

        if (_lastIndex == NOT_ASSIGNED) // cursor has not reached the end before
        {
            _currentIndex = _upperBound; // jump to the deepest row that cursor has seen
            while (next());  // send the cursor through the table, row by row,
            previous();  // but since that puts the cursor one beyond, back up one.
        }
        else			// cursor has reached end before, so row is in cache
        {
            doAssert(_cache.isCached(_lastIndex), "Last row not in cache.");
            _currentIndex = _lastIndex;	   // jump to the end
            _currentRow = readCurrentRow(); // this will always pull from cache
        }

        return (_upperBound != 0); // _uB is zero when cursor has not read a single row
        // which means this must be an empty result set.
    }
    // end last()

    public void beforeFirst() throws SQLException
    {
        if (first())   // if cursor can get to the first row,
        {
            previous();	// then bump it back one
        }
    }

    public void afterLast() throws SQLException
    {
        if (last())	 // if cursor can get to the last row,
        {
            next(); 	// then bump it forward one
        }
    }

    public boolean relative(int rows) throws SQLException
    {
        if (rows == 0) // trivial case
        {
            return true;
        }

        int targetRow = _currentIndex + rows; // calc the absolute row number
        if (targetRow <= 0) // this row is invalid
        {
            return false;
        }
        else
        {
            return absolute(targetRow);
        }
    }

    public boolean absolute(int rowNum) throws SQLException
    {
        if (rowNum == 0) // this is an invalid argument -- abort
        {
            throw new SQLException("0 is not a valid row number.");
        }

        else if (rowNum < 0) // negative index
        {
            if (_lastIndex == NOT_ASSIGNED) // cursor has not reached end
            {
                if (!last()) 					   // must force cursor to end to get last index
                {
                    return false;					// unable to reach end
                }
                doAssert( (_lastIndex != NOT_ASSIGNED), "Could not reach last row");
            }
            rowNum += _lastIndex;  // final index is known, convert neg to positive
            if (rowNum <= 0)
            {
                return false;  // negative offset was out of range		 
            }
        }

        if (rowNum <= _upperBound) // positive index, and cursor has seen this row
        {
            if ((rowNum < 1) ||  // check bounds
            ((_lastIndex != NOT_ASSIGNED) && (rowNum > _lastIndex))) 
            {
                return false;
            }

            doAssert(_cache.isCached(rowNum), "Failed to find row "+rowNum+" in cache.");
            _currentIndex = rowNum;			 // jump to the row
            _currentRow = readCurrentRow();  // this will always pull from cache
            return true;  
        }
        else  // positive index, and cursor hasn't seen this row
        {
            _currentIndex = _upperBound;   // skip to the deepest row, before looping
            while (_currentIndex < rowNum) // walk through the rows
            {
                if (!next()) // cursor caches each row as it goes
                break;
            }
            return (_currentIndex >= rowNum); // if we reached the correct row,
            // else loop was broken, and rowNum was invalid
        }
    }
    // end absolute(int)

    public boolean isFirst() throws SQLException
    {
        return _currentIndex == 1;
    }

    public boolean isLast() throws SQLException
    {
        // safe because _currentIndex != NOT_ASSIGNED
        return (_currentIndex == _lastIndex); 
    }

    public boolean isBeforeFirst() throws SQLException
    {
        return _currentIndex == 0;
    }

    public boolean isAfterLast() throws SQLException
    {
        // safe because _currentIndex != NOT_ASSIGNED + 1
        return (_currentIndex == (_lastIndex+1)); 
    }

    public int getRow() throws SQLException
    {
        // if cursor is positioned before result set,
        // OR cursor is positioned after result set
        // OR cursor has not read a single row (due to empty result set)
        if ( (_currentIndex == 0)
        || ((_lastIndex != NOT_ASSIGNED) && (_currentIndex > _lastIndex))
            || (_upperBound == 0) )
        {
            return 0; // according to JDBC 2.0 API, bad current rows return '0'
        }
        else // current row is valid, return index
        {
            return _currentIndex;
        }
    }

    public void setFetchDirection(int dir) throws SQLException
    {
        if ((dir == FETCH_UNKNOWN) || (dir == FETCH_FORWARD))
        {
            _fetchDirection = FETCH_FORWARD;
        }
        else if (dir == FETCH_REVERSE)
        {
            _fetchDirection = FETCH_REVERSE;
        }
        else
        {
            throw new SQLException("Invalid fetch direction ("+dir+")");
        }
    }

    public int getFetchDirection() throws SQLException
    {
        return _fetchDirection;
    }

    public int getFetchSize() throws SQLException
    {
        return _fetchSize;
    }

    public void setFetchSize(int size) throws SQLException
    {
        _fetchSize = size;
    }

    public int getType() throws SQLException
    {
        return TYPE_SCROLL_INSENSITIVE;
    }

    public int getConcurrency() throws SQLException
    {
        return CONCUR_READ_ONLY;
    }

    public void close() throws SQLException
    {
        _cache.clear();     // clear the cache
        _parentRS.close(); // close the "real" result set
    }

    public boolean wasNull() throws SQLException
    {
        return _parentRS.wasNull();
    }

    public final void clearWarnings() throws SQLException
    {
        _parentRS.clearWarnings();
    }

    public final int findColumn(String s)  throws SQLException
    {
        return _parentRS.findColumn(s);
    }

    public InputStream getAsciiStream(int col) throws SQLException
    {
        checkCurrentRow();
        return _currentRow.getValueAt(col).getAsciiStream();
    }

    public InputStream getAsciiStream(String colName)  throws SQLException
    {
        return getAsciiStream(findColumn(colName));
    }

    public BigDecimal getBigDecimal(int col, int scale)  throws SQLException
    {
        checkCurrentRow();
        return _currentRow.getValueAt(col).getBigDecimal(scale);
    }

    public BigDecimal getBigDecimal(String colName, int scale)  throws SQLException
    {
        return getBigDecimal(findColumn(colName), scale);
    }

    public InputStream getBinaryStream(int col)  throws SQLException
    {
        checkCurrentRow();
        return _currentRow.getValueAt(col).getBinaryStream();
    }

    public InputStream getBinaryStream(String colName)  throws SQLException
    {
        return getBinaryStream(findColumn(colName));
    }

    public boolean getBoolean(int col)  throws SQLException
    {
        checkCurrentRow();
        return _currentRow.getValueAt(col).getBoolean();
    }

    public boolean getBoolean(String colName)  throws SQLException
    {
        return getBoolean(findColumn(colName));
    }

    public byte getByte(int col)  throws SQLException
    {
        checkCurrentRow();
        return _currentRow.getValueAt(col).getByte();
    }

    public byte getByte(String colName)  throws SQLException
    {
        return getByte(findColumn(colName));
    }

    public byte[] getBytes(int col)  throws SQLException
    {
        checkCurrentRow();
        return _currentRow.getValueAt(col).getBytes();
    }

    public byte[] getBytes(String colName)  throws SQLException
    {
        return getBytes(findColumn(colName));
    }

    public String getCursorName()  throws SQLException
    {
        return _parentRS.getCursorName();
    }

    public java.sql.Date getDate(int col)  throws SQLException
    {
        checkCurrentRow();
        return _currentRow.getValueAt(col).getDate();
    }

    public java.sql.Date getDate(String colName)  throws SQLException
    {
        return getDate(findColumn(colName));
    }

    public double getDouble(int col)  throws SQLException
    {
        checkCurrentRow();
        return _currentRow.getValueAt(col).getDouble();
    }

    public double getDouble(String colName)  throws SQLException
    {
        return getDouble(findColumn(colName));
    }

    public float getFloat(int col)  throws SQLException
    {
        checkCurrentRow();
        return _currentRow.getValueAt(col).getFloat();
    }

    public float getFloat(String colName)  throws SQLException
    {
        return getFloat(findColumn(colName));
    }

    public int getInt(int col)  throws SQLException
    {
        checkCurrentRow();
        return _currentRow.getValueAt(col).getInt();
    }

    public int getInt(String colName)  throws SQLException
    {
        return getInt(findColumn(colName));
    }

    public long getLong(int col)  throws SQLException
    {
        checkCurrentRow();
        return _currentRow.getValueAt(col).getLong();
    }

    public long getLong(String colName)  throws SQLException
    {
        return getLong(findColumn(colName));
    }

    public ResultSetMetaData getMetaData()  throws SQLException
    {
        // Warning: Some drivers may not provide a fully-functional
        // ResultSetMetaData if the end of the ResultSet has been reached.
        return _rsmd;
    }

    public Object getObject(int col)  throws SQLException
    {
        checkCurrentRow();
        return _currentRow.getValueAt(col).getObject();
    }

    public Object getObject(String colName)  throws SQLException
    {
        return getObject(findColumn(colName));
    }

    public short getShort(int col)  throws SQLException
    {
        checkCurrentRow();
        return _currentRow.getValueAt(col).getShort();
    }

    public short getShort(String colName)  throws SQLException
    {
        return getShort(findColumn(colName));
    }

    public String getString(int col)  throws SQLException
    {
        checkCurrentRow();
        return _currentRow.getValueAt(col).getString();
    }

    public String getString(String colName)  throws SQLException
    {
        return getString(findColumn(colName));
    }

    public Time getTime(int col)  throws SQLException
    {
        checkCurrentRow();
        return _currentRow.getValueAt(col).getTime();
    }

    public Time getTime(String colName)  throws SQLException
    {
        return getTime(findColumn(colName));
    }

    public Timestamp getTimestamp(int col)  throws SQLException
    {
        checkCurrentRow();
        return _currentRow.getValueAt(col).getTimestamp();
    }

    public Timestamp getTimestamp(String colName)  throws SQLException
    {
        return getTimestamp(findColumn(colName));
    }

    public InputStream getUnicodeStream(int col)  throws SQLException
    {
        //return _currentRow.getValueAt(col).getUnicodeStream();
        doAssert(false, "getUnicodeStream() not implemented");
        return null;
    }

    public InputStream getUnicodeStream(String colName)  throws SQLException
    {
        return getUnicodeStream(findColumn(colName));
    }

    public SQLWarning getWarnings()  throws SQLException
    {
        return _parentRS.getWarnings();
    }

    // 
    // Private methods
    //

    /**
      * <P>This method is used to assign the <CODE>currentRow</CODE> to the row
      * in the ResultSet located at the <CODE>currentIndex</CODE> position.
      * If the row is in cache, this is a simple call to cache.getDataRow().
      * If the row is not in cache, a new DataRow is constructed and populated.
      * <P> Note that all of the cursor movement methods call this method.
      * This is to insure that no rows are skipped when trying to satisfy a
      * user request.
     */
    private DataRow readCurrentRow()
    {
        if (_cache.isCached(_currentIndex)) // if it's already in the cache,
        {
            return _cache.getDataRow(_currentIndex); // then retrieve it 
        }

        DataRow row = new DataRow(_numColumns);  // else, must create a row

        for (int i=1; i <= _numColumns; i++) 
        {
            // and populate each column entry
            try 
            {
                SQLValue entry;
                int sqlType = _rsmd.getColumnType(i);
                switch(sqlType) // mappings are based on JDBC 1.2 spec
                {

                    case Types.BIGINT:	 	entry = new SQLValue(_parentRS.getLong(i)); break;
                    case Types.BINARY:  	entry = new SQLValue(_parentRS.getBytes(i)); break;
                    case Types.BIT:		entry = new SQLValue(_parentRS.getBoolean(i)); break;
                    case Types.CHAR:    	entry = new SQLValue(_parentRS.getString(i)); break;
                    case Types.DATE:    	entry = new SQLValue(_parentRS.getDate(i)); break;
                    case Types.DECIMAL: 	entry = new SQLValue(_parentRS.getBigDecimal(i,10)); break;
                    case Types.DOUBLE:  	entry = new SQLValue(_parentRS.getDouble(i)); break;
                    case Types.FLOAT:         	entry = new SQLValue(_parentRS.getFloat(i)); break;
                    case Types.INTEGER:       	entry = new SQLValue(_parentRS.getInt(i)); break;
                    case Types.LONGVARBINARY: 	entry = new SQLValue(_parentRS.getBytes(i)); break;
                    case Types.LONGVARCHAR:   	entry = new SQLValue(_parentRS.getString(i)); break;
                    case Types.NULL:		entry = new SQLValue(); break;
                    case Types.NUMERIC:		entry = new SQLValue(_parentRS.getBigDecimal(i,10)); break;
                    case Types.OTHER:		entry = new SQLValue(_parentRS.getObject(i)); break;
                    case Types.REAL:		entry = new SQLValue(_parentRS.getFloat(i)); break;
                    case Types.SMALLINT:	entry = new SQLValue(_parentRS.getShort(i)); break;
                    case Types.TIME:		entry = new SQLValue(_parentRS.getTime(i)); break;
                    case Types.TIMESTAMP:	entry = new SQLValue(_parentRS.getTimestamp(i)); break;
                    case Types.TINYINT:		entry = new SQLValue(_parentRS.getByte(i)); break;
                    case Types.VARBINARY:	entry = new SQLValue(_parentRS.getBytes(i)); break;
                    case Types.VARCHAR:		entry = new SQLValue(_parentRS.getString(i)); break;
                    default:
                        doAssert(false,"Unrecognized SQL Type = "+sqlType);
                        entry = null; // never reached
                }
                // end switch

                row.setValueAt(i, entry);
            }
            catch (SQLException sqlE)
            {
                doAssert(false, "ResultSetMetaData failed to provide info for column #"+i);
            }
        }
        // end for
        boolean addSuccessful = _cache.addDataRow(row);
        doAssert(addSuccessful, "Row could not be added to the cache.");
        return row;
    }
    // end addRowToCache

    /**
     * <P> This method simply verifies that the <CODE>currentRow</CODE>, or cursor
     * pointer, is set to a valid value before columns are requested.
     * <P> Invalid values are allowed when the cursor is "before" or "after" the
     * ResultSet.  It is expected that the index is sufficient to determine 
     * validity, but it's better to double check the row before handing back a
     * null value.
   */
    private void checkCurrentRow() throws SQLException
    {
        if (_currentIndex == 0) // cursor is positioned before the ResultSet
        {
            throw new SQLException(ROW_INVALID);
        }
        // Cursor knows it is positioned after the ResultSet
        else if ((_lastIndex != NOT_ASSIGNED) && (_currentIndex > _lastIndex))
        {
            throw new SQLException(ROW_INVALID);
        }
        // Cursor is valid, so the row had better be valid
        doAssert(_currentRow != null, "Current Index is: "+_currentIndex+", but row is null.");
    }

    /**
      * <P> This is a convenience method that provides a central place for
      * "bombing".
     */
    private void doAssert(boolean test, String msg)
    {
        if (!test)
        {
            throw new RuntimeException(msg);
        }
    }
    // 2.0 methods
    /**
    * JDBC 2.0 feature - Returns same scale as was sent from database, avoids
    * the performance cost of rescaling.
    * @see java.sql.CallableStatement#getBigDecimal
    */
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException
    {
        //* DONE
        return(getBigDecimal(columnIndex, -1));
    }
    /**
    * JDBC 2.0 feature - Returns same scale as was sent from database, avoids
    * the performance cost of rescaling.
    * @see java.sql.ResultSet#getBigDecimal
    */
    public BigDecimal getBigDecimal(String columnName) throws SQLException
    {
        //* DONE
        return(getBigDecimal(columnName, -1));
    }


    public boolean rowUpdated() throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public boolean rowInserted() throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public boolean rowDeleted() throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }


    public void updateNull(int columnIndex) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }


    public void updateArray(int columnIndex, Array x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }


    public void updateBlob(int columnIndex, Blob x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }


    public void updateBoolean(int columnIndex, boolean x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateByte(int columnIndex, byte x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }


    public void updateClob(int columnIndex, Clob x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }


    public void updateShort(int columnIndex, short x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateInt(int columnIndex, int x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateLong(int columnIndex, long x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateFloat(int columnIndex, float x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateDouble(int columnIndex, double x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateString(int columnIndex, String x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }

    public void updateRef(int columnIndex, Ref x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }


    public void updateBytes(int columnIndex, byte x[]) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateDate(int columnIndex, java.sql.Date x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateTime(int columnIndex, java.sql.Time x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateTimestamp(int columnIndex, java.sql.Timestamp x)
        throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateAsciiStream(int columnIndex, 
        java.io.InputStream x, 
        int length) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateBinaryStream(int columnIndex, 
        java.io.InputStream x,
        int length) throws SQLException 
    {


        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateCharacterStream(int columnIndex,
        java.io.Reader x,
        int length) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateObject(int columnIndex, Object x, int scale)
        throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateObject(int columnIndex, Object x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateNull(String columnName) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }


    public void updateArray(String columnName, Array x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }

    public void updateBlob(String columnName, Blob x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }


    public void updateBoolean(String columnName, boolean x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }


    public void updateByte(String columnName, byte x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }


    public void updateClob(String columnName, Clob x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }


    public void updateShort(String columnName, short x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateInt(String columnName, int x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateLong(String columnName, long x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateFloat(String columnName, float x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateDouble(String columnName, double x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateString(String columnName, String x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateBytes(String columnName, byte x[]) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }


    public void updateRef(String columnName, Ref x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }


    public void updateDate(String columnName, java.sql.Date x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateTime(String columnName, java.sql.Time x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateTimestamp(String columnName, java.sql.Timestamp x)
        throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateAsciiStream(String columnName, 
        java.io.InputStream x, 
        int length) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateBinaryStream(String columnName, 
        java.io.InputStream x,
        int length) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateCharacterStream(String columnName,
        java.io.Reader reader,
        int length) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateObject(String columnName, Object x, int scale)
        throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void updateObject(String columnName, Object x) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }

    public void insertRow() throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }


    public void updateRow() throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void deleteRow() throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void refreshRow() throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void cancelRowUpdates () throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void moveToInsertRow() throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public void moveToCurrentRow() throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public Statement getStatement() throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public Object getObject(int i, java.util.Map map) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public Ref getRef(int i) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public Blob getBlob(int i) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public Clob getClob(int i) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public Array getArray(int i) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }

    public URL getURL(int i) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }


    public Object getObject(String colName, java.util.Map map) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public  Ref getRef(String colName) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public Blob getBlob(String colName) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public  Clob getClob(String colName) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public  Array getArray(String colName) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }

    public  URL getURL(String colName) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }


    public java.sql.Date getDate(int columnIndex, Calendar cal) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public java.sql.Date getDate(String columnName, Calendar cal) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public java.sql.Time getTime(int columnIndex, Calendar cal) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public java.sql.Time getTime(String columnName, Calendar cal) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public java.sql.Timestamp getTimestamp(int columnIndex, Calendar cal) 
        throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public java.sql.Timestamp getTimestamp(String columnName, Calendar cal)	
        throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }

    public java.io.Reader getCharacterStream(int columnIndex) throws SQLException
    {

        throw new RuntimeException("Method not implemented yet.");
    }



    public java.io.Reader getCharacterStream(String columnName) throws SQLException 
    {

        throw new RuntimeException("Method not implemented yet.");
    }


    
    public int getHoldability() throws SQLException
    {
        Debug.notImplemented(this, "getHoldability()");
        return 0;
    }

    
    public Reader getNCharacterStream(int columnIndex) throws SQLException
    {
        Debug.notImplemented(this, "getNCharacterStream(int columnIndex)");
        return null;
    }

    
    public Reader getNCharacterStream(String columnLabel) throws SQLException
    {
        Debug.notImplemented(this, "getNCharacterStream(String columnLabel)");
        return null;
    }

    
    public NClob getNClob(int columnIndex) throws SQLException
    {
        Debug.notImplemented(this, "getNClob(int columnIndex)");
        return null;
    }

    
    public NClob getNClob(String columnLabel) throws SQLException
    {
        Debug.notImplemented(this, "getNClob(String columnLabel)");
        return null;
    }

    
    public String getNString(int columnIndex) throws SQLException
    {
        Debug.notImplemented(this, "getNString(int columnIndex)");
        return null;
    }

    
    public String getNString(String columnLabel) throws SQLException
    {
        Debug.notImplemented(this, "getNString(String columnLabel)");
        return null;
    }

    
    public RowId getRowId(int columnIndex) throws SQLException
    {
        Debug.notImplemented(this, "getRowId(int columnIndex)");
        return null;
    }

    
    public RowId getRowId(String columnLabel) throws SQLException
    {
        Debug.notImplemented(this, "getRowId(String columnLabel)");
        return null;
    }

    
    public SQLXML getSQLXML(int columnIndex) throws SQLException
    {
        Debug.notImplemented(this, "getSQLXML(int columnIndex)");
        return null;
    }

    
    public SQLXML getSQLXML(String columnLabel) throws SQLException
    {
        Debug.notImplemented(this, "getSQLXML(String columnLabel)");
        return null;
    }

    
    public boolean isClosed() throws SQLException
    {
        Debug.notImplemented(this, "isClosed()");
        return false;
    }

    
    public void updateAsciiStream(int columnIndex, InputStream x)
        throws SQLException
    {
        Debug.notImplemented(this, "updateAsciiStream(int columnIndex, InputStream x)");   
    }

    
    public void updateAsciiStream(String columnLabel, InputStream x)
        throws SQLException
    {
        Debug.notImplemented(this, "updateAsciiStream(String columnLabel, InputStream x)");        
    }

    
    public void updateAsciiStream(int columnIndex, InputStream x, long length)
        throws SQLException
    {
        Debug.notImplemented(this, "updateAsciiStream(int columnIndex, InputStream x, long length)");
        
    }

    
    public void updateAsciiStream(String columnLabel, InputStream x, long length)
        throws SQLException
    {
        Debug.notImplemented(this, "updateAsciiStream(String columnLabel, InputStream x, long length)");
        
    }

    
    public void updateBinaryStream(int columnIndex, InputStream x)
        throws SQLException
    {
        Debug.notImplemented(this, "updateBinaryStream(int columnIndex, InputStream x)");
        
    }

    
    public void updateBinaryStream(String columnLabel, InputStream x)
        throws SQLException
    {
        Debug.notImplemented(this, "updateBinaryStream(String columnLabel, InputStream x)");
        
    }

    
    public void updateBinaryStream(int columnIndex, InputStream x, long length)
        throws SQLException
    {
        Debug.notImplemented(this, "updateBinaryStream(int columnIndex, InputStream x, long length)");
        
    }

    
    public void updateBinaryStream(String columnLabel, InputStream x,
        long length) throws SQLException
    {
        Debug.notImplemented(this, "updateBinaryStream(String columnLabel, InputStream x, long length)");
        
    }

    
    public void updateBlob(int columnIndex, InputStream inputStream)
        throws SQLException
    {
        Debug.notImplemented(this, "updateBlob(int columnIndex, InputStream inputStream)");        
    }

    
    public void updateBlob(String columnLabel, InputStream inputStream)
        throws SQLException
    {
        Debug.notImplemented(this, "updateBlob(String columnLabel, InputStream inputStream)");
        
    }

    
    public void updateBlob(int columnIndex, InputStream inputStream, long length)
        throws SQLException
    {
        Debug.notImplemented(this, "updateBlob(int columnIndex, InputStream inputStream, long length)");
        
    }

    
    public void updateBlob(String columnLabel, InputStream inputStream,
        long length) throws SQLException
    {
        Debug.notImplemented(this, "updateBlob(String columnLabel, InputStream inputStream, long length)");
        
    }

    
    public void updateCharacterStream(int columnIndex, Reader x)
        throws SQLException
    {
        Debug.notImplemented(this, "updateCharacterStream(int columnIndex, Reader x)");
        
    }

    
    public void updateCharacterStream(String columnLabel, Reader reader)
        throws SQLException
    {
        Debug.notImplemented(this, "updateCharacterStream(String columnLabel, Reader reader)");
        
    }

    
    public void updateCharacterStream(int columnIndex, Reader x, long length)
        throws SQLException
    {
        Debug.notImplemented(this, "updateCharacterStream(int columnIndex, Reader x, long length)");
        
    }

    
    public void updateCharacterStream(String columnLabel, Reader reader,
        long length) throws SQLException
    {
        Debug.notImplemented(this, "updateCharacterStream(String columnLabel, Reader reader, long length)");
        
    }

    
    public void updateClob(int columnIndex, Reader reader) throws SQLException
    {
        Debug.notImplemented(this, "updateClob(int columnIndex, Reader reader)");
        
    }

    
    public void updateClob(String columnLabel, Reader reader)
        throws SQLException
    {
        Debug.notImplemented(this, "updateClob(String columnLabel, Reader reader)");
        
    }

    
    public void updateClob(int columnIndex, Reader reader, long length)
        throws SQLException
    {
        Debug.notImplemented(this, "updateClob(int columnIndex, Reader reader, long length)");
        
    }

    
    public void updateClob(String columnLabel, Reader reader, long length)
        throws SQLException
    {
        Debug.notImplemented(this, "updateClob(String columnLabel, Reader reader, long length)");
        
    }

    
    public void updateNCharacterStream(int columnIndex, Reader x)
        throws SQLException
    {
        Debug.notImplemented(this, "updateNCharacterStream(int columnIndex, Reader x)");
        
    }

    
    public void updateNCharacterStream(String columnLabel, Reader reader)
        throws SQLException
    {
        Debug.notImplemented(this, "updateNCharacterStream(String columnLabel, Reader reader)");
        
    }

    
    public void updateNCharacterStream(int columnIndex, Reader x, long length)
        throws SQLException
    {
        Debug.notImplemented(this, "updateNCharacterStream(int columnIndex, Reader x, long length)");
        
    }

    
    public void updateNCharacterStream(String columnLabel, Reader reader,
        long length) throws SQLException
    {
        Debug.notImplemented(this, "updateNCharacterStream(String columnLabel, Reader reader, long length)");
        
    }

    
    public void updateNClob(int columnIndex, NClob clob) throws SQLException
    {
        Debug.notImplemented(this, "updateNClob(int columnIndex, NClob clob)");
        
    }

    
    public void updateNClob(String columnLabel, NClob clob) throws SQLException
    {
        Debug.notImplemented(this, "updateNClob(String columnLabel, NClob clob)");
        
    }

    
    public void updateNClob(int columnIndex, Reader reader) throws SQLException
    {
        Debug.notImplemented(this, "updateNClob(int columnIndex, Reader reader)");
        
    }

    
    public void updateNClob(String columnLabel, Reader reader)
        throws SQLException
    {
        Debug.notImplemented(this, "updateNClob(String columnLabel, Reader reader)");
        
    }

    
    public void updateNClob(int columnIndex, Reader reader, long length)
        throws SQLException
    {
        Debug.notImplemented(this, "updateNClob(int columnIndex, Reader reader, long length)");
        
    }

    
    public void updateNClob(String columnLabel, Reader reader, long length)
        throws SQLException
    {
        Debug.notImplemented(this, "updateNClob(String columnLabel, Reader reader, long length)");
        
    }

    
    public void updateNString(int columnIndex, String string)
        throws SQLException
    {
        Debug.notImplemented(this, "updateNString(int columnIndex, String string)");
        
    }

    
    public void updateNString(String columnLabel, String string)
        throws SQLException
    {
        Debug.notImplemented(this, "updateNString(String columnLabel, String string)");
        
    }

    
    public void updateRowId(int columnIndex, RowId x) throws SQLException
    {
        Debug.notImplemented(this, "updateRowId(int columnIndex, RowId x)");
        
    }

    
    public void updateRowId(String columnLabel, RowId x) throws SQLException
    {
        Debug.notImplemented(this, "updateRowId(String columnLabel, RowId x)");
        
    }

    
    public void updateSQLXML(int columnIndex, SQLXML xmlObject)
        throws SQLException
    {
        Debug.notImplemented(this, "updateSQLXML(int columnIndex, SQLXML xmlObject)");
        
    }

    
    public void updateSQLXML(String columnLabel, SQLXML xmlObject)
        throws SQLException
    {
        Debug.notImplemented(this, "updateSQLXML(String columnLabel, SQLXML xmlObject)");
        
    }
    
    /**
     * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
     */
    public boolean isWrapperFor(Class<?> iface) throws SQLException
    {
        return false;
    }
    
    /**
     * @see java.sql.Wrapper#unwrap(java.lang.Class)
     */
    public <T> T unwrap(Class<T> iface) throws SQLException
    {
        return null;
    }
}
// end ExtendedResultSet 

