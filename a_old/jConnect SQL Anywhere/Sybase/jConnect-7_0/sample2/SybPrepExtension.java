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
import java.math.BigDecimal;
import com.sybase.jdbcx.SybPreparedStatement;

/**
 * SybPrepExtension class demonstrates how to use the Sybase extensions
 * to the PreparedStatement interface. Specifically it shows how you can
 * explicitly control the precision and scale of a NUMERIC parameter that
 * is sent to the server through setBigDecimal.
 * <P> There are situations where the precision and scale of the parameter
 * must precisely match the precision/scale of the corresponding SQL object,
 * whether it be a stored procedure parameter or a column.
 *
 * <UL>
 *   <LI> Prepares a parameterized select statement
 *   <LI> Passes parameters to the Statement
 *   <LI> Executes the Callable Statement
 *   <LI> Retrieves and displays the returned result set
 * </UL> 
 *
 * <P>SybPrepExtension may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *  @see Sample
 */

public class SybPrepExtension extends Sample
{

    SybPrepExtension()
    {
        super();
    }

    public void sampleCode()
    {
        String query = "select * from discounts where discount > ?";

        try 
        {
            PreparedStatement cstmt = _con.prepareStatement(query);
            // Downcast this to a SybPreparedStatement so we can access
            // the extension
            SybPreparedStatement sps = (SybPreparedStatement) cstmt;
            BigDecimal discountThreshold = new BigDecimal("8.5");
            // force jConnect to send this big decimal with a precision of
            // 5 digits, and a scale of 2 digits
            sps.setBigDecimal(1, discountThreshold, 5, 2);
            displayRows(sps);
            cstmt.close();
        }
        catch (SQLException ex)
        {
            displaySQLEx(ex);
        }
    }


    /**
     * Execute the desired Callable statement and then call dispResultSet to
     * display the rows and columns
     * @param  stmt  PreparedStatement object to be processed
     * @exception SQLException .
     */
    public void displayRows( PreparedStatement stmt)
        throws SQLException
    {

        boolean results = stmt.execute();
        int rsnum = 0;                   // Number of Result Sets processed
        int rowsAffected = 0;       
        do
        {
            if(results)
            {
                ResultSet rs = stmt.getResultSet();
                output("\n\nDisplaying ResultSet: " + rsnum + "\n");
                dispResultSet(rs);
                rsnum++;
                rs.close();
            }
            else
            {
                rowsAffected = stmt.getUpdateCount();
                if (rowsAffected >= 0)
                output(rowsAffected + " rows Affected.\n");
            }
            results = stmt.getMoreResults();
        }
        while (results || rowsAffected != -1);

    }

}
