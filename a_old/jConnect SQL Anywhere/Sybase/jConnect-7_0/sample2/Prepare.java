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
import java.math.*;
import java.util.*;

/**
 * Prepare class demonstrates how a PreparedStatement is handled.
 * <UL>
 * <LI>It prepares the statement (with one parameter for each datatype,
 * convering NULL and notNULL).
 * <LI>Then the values are filled in.
 * <LI>The returned values are compared with the original
 *  values. If necessary, an error message is displayed.
 * </UL>
 *
 * Prepare may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *  @see java.sql.PreparedStatement
 *  @see java.sql.Statement
 *  @see Sample
 */
public class Prepare extends Sample
{


    Prepare()
    {
        super();
    }

    public void sampleCode()
    {
        boolean returnval = false;
        SQLException lastSQE;

        String stmtString =
            "select " +
            "? 'Int', " +
            "? 'NullInt', " +
            "? 'String', " +
            "? 'NullString', " +
            "? 'Numeric', " +
            "? 'NullNumeric', " +
            "? 'Datetime', " +
            "? 'NullDatetime', " +
            "? 'Bit', " +
            "? 'Tiny', " +
            "? 'NullTiny', " +
            "? 'Short', " +
            "? 'NullShort', " +
            // We should test Long - but 8-byte ints are not
            // uniformly supported by SQL Servers
            "? 'Float', " +
            "? 'NullFloat', " +
            "? 'Double', " +
            "? 'NullDouble', " +
            "? 'Binary', " +
            "? 'NullBinary', " +
            "? 'NegativeByte'";
        // We should test the LongChar and LongBinary
        // types you get with InputAsciiStream/InputBinaryStream
        // but need a DB-backend that supports these types.



        try 
        {

            // Display info about the jConnect Driver
            printDriverInfo();

            // Prepare the statement
            output("Preparing the statement...\n");
            PreparedStatement stmt = _con.prepareStatement(stmtString);
            try
            {
                lastSQE = null;
                ResultSet rs = stmt.executeQuery();
            }
            catch (SQLException sqe)
            {
                lastSQE = sqe;
            }
            if (lastSQE == null)
            {
                output("Expected an exception for unset params\n");
                returnval = true;
            }
            else if (!lastSQE.getSQLState().equals("JZ0SA"))
            {
                output("Expected SQLstate JZ0SA, got " +
                    lastSQE.getSQLState());
                returnval = true;
            }

            // Set the values
            output("Setting the values...\n");
            stmt.setInt(1, 47);
            stmt.setNull(2, Types.INTEGER);
            String sendString = "Test";
            stmt.setString(3, sendString);
            stmt.setNull(4, Types.CHAR);
            BigDecimal sendNum = new BigDecimal("123456.7890123");
            stmt.setBigDecimal(5, sendNum);
            stmt.setNull(6, Types.NUMERIC);
            Timestamp sendTS = Timestamp.valueOf("1975-3-13 12:34:56");
            stmt.setTimestamp(7, sendTS);
            stmt.setNull(8, Types.TIMESTAMP);
            stmt.setBoolean(9, true);
            stmt.setByte(10, (byte) 47);
            stmt.setNull(11, Types.TINYINT);
            stmt.setShort(12, (short) -4700);
            stmt.setNull(13, Types.SMALLINT);
            stmt.setFloat(14, (float) -470.0);
            stmt.setNull(15, Types.FLOAT);
            double sendDouble = 470000.047;
            stmt.setDouble(16, sendDouble);
            stmt.setNull(17, Types.DOUBLE);
            byte sendBytes[] = 
            {
                0, 1, 2, 3, 4, 5 
            }
            ;
            stmt.setBytes(18, sendBytes);
            stmt.setNull(19, Types.BINARY);
            // 123533 - sending a Byte in range -127 - -1 should get
            // implicitly upsized to a SMALLINT - TinyInt is > 0
            stmt.setByte(20, (byte) -47);

            // Send the query
            output("Executing the query...\n");
            ResultSet rs = stmt.executeQuery();
            if (rs == null) 
            {
                output("Expected results\n");
                returnval = true;
            }
            else
            {
                // Get the next row
                if (! rs.next())
                {
                    output("Expected rows\n");
                    returnval = true;
                }
                else
                {
                    int gotInt = rs.getInt(1);
                    if (gotInt != 47)
                    {
                        output(
                            "Expected a 47 returned in Column1, got "
                            + gotInt + "\n");
                        returnval = true;
                    }
                    gotInt = rs.getInt(2);
                    if (gotInt != 0)
                    {
                        output(
                            "Expected a 0 returned because it was NULL, got "
                            + gotInt + "\n");
                        returnval = true;
                    }
                    if (!rs.wasNull())
                    {
                        output(
                            "Expected a NULL\n");
                        returnval = true;
                    }
                    String gotString = rs.getString(3);
                    if (gotString == null)
                    {
                        output(
                            "Expected a String\n");
                        returnval = true;
                    }
                    else if (!gotString.equals(sendString))
                    {
                        output(
                            "Expected '" + sendString + "' , got "
                            + gotString + "\n");
                        returnval = true;
                    }
                    gotString = rs.getString(4);
                    if (gotString != null)
                    {
                        output("Expected a null\n");
                        returnval = true;
                    }
                    if (!rs.wasNull())
                    {
                        output("Expected a NULL\n");
                        returnval = true;
                    }
                    BigDecimal gotNum = rs.getBigDecimal(5, 7);
                    if (gotNum == null)
                    {
                        output(
                            "Expected a Numeric\n");
                        returnval = true;
                    }
                    else if (!gotNum.equals(sendNum))
                    {
                        output(
                            "Expected " + sendNum + " but got " + gotNum + "\n");
                        returnval = true;
                    }
                    gotNum = rs.getBigDecimal(6, 1);
                    if (gotNum != null)
                    {
                        output("Expected a null\n");
                        returnval = true;
                    }
                    if (!rs.wasNull())
                    {
                        output("Expected a NULL\n");
                        returnval = true;
                    }
                    Timestamp gotTS = rs.getTimestamp(7);
                    if (gotTS == null)
                    {
                        output("Expected a Timestamp\n");
                        returnval = true;
                    }
                    else if (!gotTS.equals(sendTS))
                    {
                        output(
                            "Expected " + sendTS + " but got " + gotTS + "\n");
                        returnval = true;
                    }
                    gotTS = rs.getTimestamp(8);
                    if (gotTS != null)
                    {
                        output("Expected a null\n");
                        returnval = true;
                    }
                    if (!rs.wasNull())
                    {
                        output("Expected a NULL\n");
                        returnval = true;
                    }
                    boolean gotBool = rs.getBoolean(9);
                    if (!gotBool)
                    {
                        output("Expected a 'true'\n");
                        returnval = true;
                    }
                    byte gotByte = rs.getByte(10);
                    if (gotByte != 47)
                    {
                        output("Expected 47, got " + gotByte + "\n");
                        returnval = true;
                    }
                    gotByte = rs.getByte(11);
                    if (gotByte != 0)
                    {
                        output(
                            "Expected a 0 returned because it was NULL\n");
                        returnval = true;
                    }
                    if (!rs.wasNull())
                    {
                        output("Expected a NULL\n");
                        returnval = true;
                    }
                    short gotShort = rs.getShort(12);
                    if (gotShort != -4700)
                    {
                        output("Expected -4700, got " + gotShort
                            + "\n");
                        returnval = true;
                    }
                    gotShort = rs.getShort(13);
                    if (gotShort != 0)
                    {
                        output(
                            "Expected a 0 returned because it was NULL\n");
                        returnval = true;
                    }
                    if (!rs.wasNull())
                    {
                        output("Expected a NULL\n");
                        returnval = true;
                    }
                    float gotFloat = rs.getFloat(14);
                    if (gotFloat != -470.0)
                    {
                        output("Expected -470.0, got " + gotFloat
                            + "\n");
                        returnval = true;
                    }
                    gotFloat = rs.getFloat(15);
                    if (gotFloat != 0)
                    {
                        output(
                            "Expected a 0 returned because it was NULL\n");
                        returnval = true;
                    }
                    if (!rs.wasNull())
                    {
                        output("Expected a NULL\n");
                        returnval = true;
                    }
                    double gotDouble = rs.getDouble(16);
                    if (gotDouble != sendDouble)
                    {
                        output("Expected " + sendDouble + 
                            ", got " + gotDouble + "\n");
                        returnval = true;
                    }
                    gotDouble = rs.getDouble(17);
                    if (gotDouble != 0)
                    {
                        output(
                            "Expected a 0 returned because it was NULL\n");
                        returnval = true;
                    }
                    if (!rs.wasNull())
                    {
                        output("Expected a NULL\n");
                        returnval = true;
                    }
                    byte gotBytes[] = rs.getBytes(18);
                    if (gotBytes.length != sendBytes.length)
                    {
                        output("byte[] lengths differ\n");
                        returnval = true;
                    }
                    for (int i = 0; i < gotBytes.length; i++)
                    {
                        if (gotBytes[i] != sendBytes[i])
                        {
                            output("byte values at index " + i +
                                "differ (" + gotBytes[i] + ") vs. (" +
                                sendBytes[i] + ")\n");
                            returnval = true;
                        }
                    }
                    gotBytes = rs.getBytes(19);
                    if (gotBytes != null)
                    {
                        output(
                            "Expected a 0 returned because it was NULL\n");
                        returnval = true;
                    }
                    if (!rs.wasNull())
                    {
                        output("Expected a NULL\n");
                        returnval = true;
                    }
                    byte negByte = rs.getByte(20);
                    if (negByte != -47)
                    {
                        output("Expected -47, got " + negByte + "\n");
                        returnval = true;
                    }
                }
            }
            stmt.close();
        }
        catch (SQLException ex)   
        {
            displaySQLEx(ex);
        }
        if (returnval == true)
        {
            output("Error\n");
        }
        else
        {
            output("All returned values correct.\n");
        }

    }

}
