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
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * RSMetadata class demonstrates how to use the ResultSetMetaData class<br>
 *
 * <P>RSMetadata may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *  @see Sample
 */
public class RSMetadata extends Sample
{
    // Available ResultSetMetaData Methods 
    static String _methods[] =
    {
        "getCatalogName", "getColumnCount", "getColumnDisplaySize",
            "getColumnLabel", "getColumnName",  "getColumnType",
            "getColumnTypeName",
            "getPrecision",   "getScale",       "getSchemaName",
            "getTableName",   "isAutoIncrement","isCaseSensitive",
            "isCurrency",     "isDefinitelyWritable", "isNullable",
            "isReadOnly",     "isSearchable",   "isSigned", "isWritable"
    }
    ;


    RSMetadata()
    {
        super();
    }

    public void sampleCode()
    {
        String query = "select title_id, price from titles";

        try
        {

            // Execute the Query so that we can examine the ResultSetMetaData
            Statement stmt = _con.createStatement();
            output("Executing: " + query + "\n");
            ResultSet rs = stmt.executeQuery(query);

            // Display the ResultSetMetaData Fields

            output("ResultSetMetaData.columnNoNulls= " + 
                ResultSetMetaData.columnNoNulls  + "\n");
            output("ResultSetMetaData.columnNullable= " + 
                ResultSetMetaData.columnNullable  + "\n");
            output("ResultSetMetaData.columnNullableUnknown= " + 
                ResultSetMetaData.columnNullableUnknown  + "\n");

            // Get the ResultSetMetaData.  This will be used for
            // the column headings

            ResultSetMetaData rsmd = rs.getMetaData ();
            output(
                "Display ResultSetMetaData for query using column 1\n\n");


            for(int offset= 0; offset < _methods.length; offset++)
            {
                output("method= " + _methods[offset] +
                    ", offset= " + offset + "\n");
                try
                {
                    switch(offset)
                    {
                        case 0:
                            output("getCatalogName(1)= " +
                                rsmd.getCatalogName(1) + "\n");
                            break;
                        case 1:
                            output("getColumnCount()= " +
                                rsmd.getColumnCount ()  + "\n");
                            break;
                        case 2:
                            output("getColumnDisplaySize(1)= " +
                                rsmd.getColumnDisplaySize(1) + "\n");
                            break;
                        case 3:
                            output("getColumnLabel(1)= " +
                                rsmd.getColumnLabel(1) + "\n");
                            break;
                        case 4:
                            output("getColumnName(1)= " +
                                rsmd.getColumnName(1) + "\n");
                            break;
                        case 5:
                            output("getColumnType(1)= " +
                                rsmd.getColumnType(1) + "\n");
                            break;
                        case 6:
                            output("getColumnTypeName(1)= " +
                                rsmd.getColumnTypeName(1) + "\n");
                            break;
                        case 7:
                            output("getPrecision(1)= " +
                                rsmd.getPrecision(1) + "\n");
                            break;
                        case 8:
                            output("getScale(1)= " +
                                rsmd.getScale(1) + "\n");
                            break;
                        case 9:
                            output("getSchemaName(1)= " +
                                rsmd.getSchemaName(1) + "\n");
                            break;
                        case 10:
                            output("getTableName(1)= " +
                                rsmd.getTableName(1) + "\n");
                            break;
                        case 11:
                            output("isAutoIncrement(1)= " +
                                rsmd.isAutoIncrement(1) + "\n");
                            break;
                        case 12:
                            output("isCaseSensitive(1)= " +
                                rsmd.isCaseSensitive(1) + "\n");
                            break;
                        case 13:
                            output("isCurrency(1)= " +
                                rsmd.isCurrency(1) + "\n");
                            break;
                        case 14:
                            output("isDefinitelyWritable(1)= " +
                                rsmd.isDefinitelyWritable(1) + "\n");
                            break;
                        case 15:
                            output("isNullable(1)= " +
                                rsmd.isNullable(1) + "\n");
                            break;
                        case 16:
                            output("isReadOnly(1)= " +
                                rsmd.isReadOnly(1) + "\n");
                            break;
                        case 17:
                            output("isSearchable(1)= " +
                                rsmd.isSearchable(1) + "\n");
                            break;
                        case 18:
                            output("isSigned(1)= " +
                                rsmd.isSigned(1) + "\n");
                            break;
                        case 19:
                            output("isWritable(1)= " +
                                rsmd.isWritable(1) + "\n");
                            break;
                    }
                }
                catch (SQLException sqe)
                {
                    String sqlstate = sqe.getSQLState();
                    String message = sqe.toString();
                    output("*** " + _methods[offset] +
                        "() not suported ***\n");

                }


            }

            stmt.close();
            rs.close();
        }
        catch (SQLException ex)
        {
            displaySQLEx(ex);
        }
    }
}
