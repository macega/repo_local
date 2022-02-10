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
 * AsciiStream class demonstrates how to use the setAsciiStream
 * and getAsciiStream methods.<p>
 *
 * AsciiStream may be invoked with the optional parameters:<br>
 * -f filename        (default is atextfile)<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *  @see Sample
 */

public class AsciiStream extends Sample
{
    static String _setTextSize = "set textsize   100000"; // Default text size
    static String _extraCmdOption = "-f";
    static String _textFile = null;        


    AsciiStream()
    {
        super();
    }
    /**
     * Location where you can add commandline properties to the connection
     * The Super class will call this function before creating the
     * connection
     * @param cmdLine command line operations
     * @see CommandLine
     */
    public void addMoreProps(CommandLine cmdLine)
    {
        Enumeration extras  = cmdLine._extraArgs.elements();
        Enumeration options = cmdLine._extraOptions.elements();
        while( extras.hasMoreElements())
        {

            String option = (String) extras.nextElement();
            String value = (String) options.nextElement();
            error("Extra options= " + option + " " + value + "\n");

            if(option.equals(_extraCmdOption))
            {
                _textFile = value;
                break;
            }
        }
    }

    /**
     * Demonstate AsciiStreams
     */
    public void sampleCode()
    {
        if (_textFile == null)
        {
            _textFile = _dir+"Ping.java";
        }
        String au_id = "409-56-7008";
        String createQuery = "select * into #blurbs from blurbs";
        String selectQuery = "select au_id, copy from #blurbs";
        String selectQuery2 = "select au_id, copy from #blurbs where au_id= '"
            + au_id + "'";
        String updateQuery = "update #blurbs set copy = ? where au_id = ?";

        try
        {

            // set the the text column size for retrieval
            error("\nExecuting setTextSize\n");
            execDDL(_setTextSize);

            // Create our table
            error("Executing createQuery\n");
            execDDL(createQuery); 

            // Read the data from our table display the text column
            error("Executing selectQuery\n");
            displayText(selectQuery);

            // Read in text and store it in our table
            // Note that the file must exist   
            InputStream instream = null;
            if(!_anApplet)
            {
                error("Opening file: " +_textFile + "\n");
                File fd = new File(_textFile);
                instream = (InputStream)new FileInputStream(fd);
            }
            else
            {
                error("Opening url: "+ _textFile + "\n");  
                java.net.URL source = new java.net.URL(_textFile);
                instream = source.openStream(); 
            }
            PreparedStatement pstmt =
                _con.prepareStatement(updateQuery );

            output("\n****Now updating text column where au_id= "
                + au_id + "****\n");
            output("Executing " + updateQuery + "\n" );
            pstmt.setAsciiStream(1, instream, (int) instream.available());
            pstmt.setString(2, au_id);
            pstmt.execute();
            pstmt.close();

            // Display the data so that we can demonstrate that we changed
            // the contents of the text column
            displayText(selectQuery2);


        }
        catch (SQLException ex)
        {
            displaySQLEx(ex);
        }
        catch (FileNotFoundException ex)
        {
            error("****Cannot open: " + _textFile + "\n");
        }
        catch (java.lang.Exception ex)
        {

            // Got some other type of exception.  Dump it.
            ex.printStackTrace ();
        }
        error("AsciiStream is done");
    }

    /**
    * Retrieve the contents of a textcolumn using getAsciiStream()
    * @param query sql statement to query to server
    * @exception SQLException .
    * @exception IOException .
    */
    public void displayText(String query)
        throws SQLException, IOException
    {
        Statement stmt = _con.createStatement();;
        output("Executing: " + query + "\n");
        ResultSet rs = stmt.executeQuery(query);

        int numRead= 0;
        int numtot= 0;
        int tot = 0;
        output("\n****Now Retrieving Text(s)****\n");
        while(rs.next())
        {
            byte stuff[] = new byte[10000];
            numRead= 0;
            numtot= 0;
            output("\nau_id= " + rs.getString(1) + "\n");
            StringBuffer resultBuffer = new StringBuffer();
            InputStream is = rs.getAsciiStream(2);
            for(;;)
            {
                numRead= is.read(stuff);
                if(numRead == -1)
                {
                    break;
                }
                numtot += numRead;
                resultBuffer.append(new String(stuff));
            }
            tot++;
            output("copy = " + resultBuffer.toString() + "\n");
            output("\nBytes read for copy " + tot + "= " + numtot +"\n");
        }

        rs.close();
        stmt.close();
    }


}
