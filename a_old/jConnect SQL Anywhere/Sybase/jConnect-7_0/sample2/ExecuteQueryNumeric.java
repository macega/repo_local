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
 * ExecuteQueryNumeric class demonstrates how to use the executeQuery method when Numeric Overflow errors<br>
 * occur.  3 scenarios are tested:<br>
 * 1. Arithmetic overflow - select power(2,31)<br>
 * 2. Divide by Zero - select 1/0<br>
 * 3. Domain error - select sqrt(-3)<br>
 *
 * <P>ExecuteQuery may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br
 * -S server<p>
 *
 *  @see Sample
 */

public class ExecuteQueryNumeric extends Sample
{


    ExecuteQueryNumeric()
    {
        super();
    }

    public void sampleCode()
    {
	String arithOverflowQuery = "select power(2,31)";
	String divideByZeroQuery = "select 1/0";
	String domainErrorQuery= "select sqrt(-1)";
	
        try 
        {


		// Execute the desired DML statement and then call dispResultSet to
		// display the rows and columns
		// Arithmetic overflow sample
		Statement stmt = _con.createStatement();;
		output("Executing: " + arithOverflowQuery + "\n");
		ResultSet rs = stmt.executeQuery (arithOverflowQuery);
		dispResultSet(rs);

		// Check for the Warning.  We need to check after checking
		// for the ResultSet - these types of numeric errors are 
		// sent back from server in TDS_EED as severity 10 in most cases
		// Since the query first sends back an empty result set we need to 
		// check for the SQLWarning after we have checked for ResultSet
		checkForWarning(rs.getWarnings());

		// close result set and statement and do another...
		rs.close();
		stmt.close();

		// Divide by Zero
		stmt = _con.createStatement();;
		output("Executing: " + divideByZeroQuery + "\n");
		rs = stmt.executeQuery (divideByZeroQuery);
		dispResultSet(rs);
		checkForWarning(rs.getWarnings());

		// close result set and statement and do another...
		rs.close();
		stmt.close();

		// Domain Error
		stmt = _con.createStatement();;
		output("Executing: " + domainErrorQuery + "\n");
		rs = stmt.executeQuery (domainErrorQuery);
		dispResultSet(rs);
		checkForWarning(rs.getWarnings());

		// close result set and statement
		rs.close();
		stmt.close();
	}
	catch (SQLException ex)   
	{
		displaySQLEx(ex);
	}
    }
}

