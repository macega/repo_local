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
import java.lang.*;
import java.sql.*;
import java.util.*;

/**
 * SybTimestamp class demonstrates how to process  a Sybase timestamp
 * datatype via an Output Parameter of a CallableStatement<p>
 *
 * A Sybase timestamp datatype is a structure which is stored as a 
 * VARBINARY(8).<p>  
 * The Structure looks like:
 * <PRE>fieldname     size     Description
  ts_high        2       High portion of timestamp
  pad            2       Bytes for alignment
  ts_low         4       Low portion of timestamp
  </PRE>
 * <P>Note:  Sybase timestamp is a unique datatype to Sybase<br>
 *
 * Due to permissions restricting a guest from creating procedures, 
 * we will not be creating a stored procedure, or the tables that it
 * requires to be there, only executing them. 
 * The required stored procedures and tables have been pre-loaded
 * onto our demo server, and included in pubs2_sql.sql or pubs2_any.sql
 * for you to be able to run them from your server.
 * We have included comments on how you would actually execute the
 * creation of a stored procedure and drop within the *CREATE PROCEDURE*
 * comments throughout the sample.
 *
 * <P>SybTimestamp may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *  @see Sample
 */
public class SybTimestamp extends Sample 
{


    static final int SIZE = 2;
    static final String ZEROS = "00000000";
    SybTimestamp()
    {
        super();
    }

    public void sampleCode()
    {

        //  Variables for simple test case
        String procname = "sp_timestampSample";

        /* *CREATE PROCEDURE*
           String createQuery1 =
               "create table spt_timestampSample(f1 int, f2 char(5), f3 timestamp )";
           String insertQuery1 =
               "insert spt_timestampSample(f1,f2) values(1, 'Hello')";

           // Sample Stored Procedure
           String dropProc = "drop proc " + procname;
           String createProc =
               "create proc " + procname + 
               "(@p1 int, @p2 timestamp out)" +
               " as " +
               "select 'p1='  + convert(varchar(10),@p1) "  +
               "select @p2 = f3 from spt_timestampSample where f1=1"   +
               "select * from spt_timestampSample " +
               "return 21";
        */
        String sproc = "{? = call "+procname+"(?,?)}";
        try
        {
            /* *CREATE PROCEDURE*
               // We will create a temp table which contains a timestamp column
               // and we will insert a row.  We will then execute a stored
               // procedure which will returnt the timestamp column as an OUTPUT
               // parameter

               // Create our table
               execDDL( createQuery1);

               // Insert our row
               execDDL( insertQuery1);

               // Now create the Proc
               execDDL( createProc);
            */

            // Now execute our Sproc
            CallableStatement cstmt = _con.prepareCall(sproc);
            output("Executing: " + sproc  +"\n");

            // Declare the IN Params.  Note, you must skip the Return Status
            cstmt.setInt(2, 1961);

            // Now declare our OUT Params
            cstmt.registerOutParameter(1, Types.INTEGER);
            cstmt.registerOutParameter(3, Types.VARBINARY);

            boolean results = cstmt.execute ();
            int rsnum = 0;                   // Number of Result Sets processed
            int rowsAffected = 0;

            do
            {
                if(results)
                {
                    ResultSet rs = cstmt.getResultSet();
                    output("\n\nDisplaying ResultSet: " + rsnum + "\n");
                    dispResultSet(rs);
                    rsnum++;
                    rs.close();
                }
                else
                {
                    rowsAffected = cstmt.getUpdateCount();
                    if (rowsAffected >= 0)
                    output(rowsAffected + " rows Affected.\n");
                }
                results = cstmt.getMoreResults();
            }
            while (results || rowsAffected != -1);


            String s = cstmt.getString(1);
            String s2 = cstmt.getString(3);

            // Now grab the same output parameter as VARBINARY
            byte [] ts =    cstmt.getBytes(3);

            // Display the Output Parameters
            output("OUT Param1=" + s + "\n");
            output("OUT Param2 as String=" + s2   +"\n");
            output("OUT Param2 as byte[]=" + toHexString(ts )  +"\n");

            cstmt.close();

            /* *CREATE PROCEDURE*
               // Drop our sproc
               execDDL( dropProc);
            */
        }
        catch (SQLException ex) 
        {
            displaySQLEx(ex);
        }
        catch (java.lang.Exception ex)
        {
            // Got some other type of exception.  Dump it.
            ex.printStackTrace ();
        }
    }

    /**
     * Convert a byte array to a hex string
     * @param bytes  byte array to convert to a hex string
     * @return       return a String in hex representing the byte array
     */
    public String toHexString(byte[] bytes )
    {
        StringBuffer result = new StringBuffer("0x");

        for(int i= 0; i < bytes.length; i++)
        {
            Byte aByte = new Byte(bytes[i]);
            Integer anInt  = new Integer(aByte.intValue());
            String hexVal  = Integer.toHexString(anInt.intValue());

            if(hexVal.length() > SIZE)
            hexVal = hexVal.substring(hexVal.length() - SIZE);

            result.append( (SIZE > hexVal.length() ?
                ZEROS.substring(0,SIZE - hexVal.length()) : "") + hexVal);
        }
        return(result.toString()); 
    }


}
