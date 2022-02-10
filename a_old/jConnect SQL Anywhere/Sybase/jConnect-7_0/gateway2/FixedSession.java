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

package gateway2;
import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.sybase.jdbc4.timedio.URLDbio;
import com.sybase.jdbc4.timedio.InStreamMgr;
import com.sybase.jdbc4.tds.Tds;
import com.sybase.jdbc4.IConstants;
import com.sybase.jdbc4.jdbc.SybDriver;
import com.sybase.jdbc4.jdbc.SybConnection;

/**
 * This class multiplexes HTTP Tunnelling of TDS between jConnect clients
 * and a pool of connections to a database.  There is no overhead per
 * client (an infinite number of logically connected clients), 
 * <P> If you want to provide multiple connection pools to different
 * servers, or differently configured connections, you would change this
 * servlet to keep multiple pools which are separately identified by
 * different SESSION_ID's
 */
public class FixedSession extends HttpServlet 
{
    // Constants:
    private static final int LOW_BYTE = 0x00FF; 
    private static final String URL = "CONNECT_URL";
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final String POOL_SIZE = "POOL_SIZE";
    private static final String DEBUG = "Debug";

    // This servlet can be used directly from a browser to get
    // html formatted query results by specifying the language
    // command as a QUERY parameter
    private static final String QUERY = "QUERY";

    // set this next boolean to true and recompile for verbose debug output
    private static final boolean VERBOSE = false; 
    private static final String DEFAULT_URL = 
        "jdbc:sybase:Tds:jdbc.sybase.com:4444";
    private static final String DEFAULT_USER = "guest";
    private static final String DEFAULT_PASSWORD = "sybase";
    private static final int DEFAULT_POOL_SIZE = 2;

    // Members:
    private Stack _connections = new Stack(); // pool of connections
    private String _url;
    private String _user;
    private String _password;
    private boolean _debug;
    private Properties _props;
    private SybDriver _driver;

    public void init(ServletConfig conf) throws ServletException 
    {
        super.init(conf);

        // determine if we should print debug info
        String debug = getInitParameter(DEBUG);
        System.out.println(DEBUG + " parameter is " + debug);
        _debug = (debug != null && debug.equals("true"));
        // _debug = true;
        log("FixedSession.initialize: enter");
        int poolSize = intValue(getInitParameter(POOL_SIZE),
            DEFAULT_POOL_SIZE);
        log("poolSize set to: " + poolSize);
        _url = stringValue(URL, DEFAULT_URL);
        _user = stringValue(USERNAME, DEFAULT_USER);
        _password = stringValue(PASSWORD, DEFAULT_PASSWORD);

        // load the jConnect JDBC driver - set it to version 3
        try
        {
            _driver = new SybDriver();
            _driver.setVersion(SybDriver.VERSION_3);
            _props = new Properties();
            _props.put("user", _user);
            _props.put("password", _password);
            // create a pool of connections
            for (int i = 0; i < poolSize; i++)
            {
                anotherConnection();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Thread.dumpStack();
            ServletException se = new ServletException("Initialization Error " +
                e.getMessage());
            throw(se);
        }
        log("FixedSession initialized");
    }

    private void anotherConnection() throws Exception
    {
        SybConnection c = (SybConnection) _driver.connect(_url, _props);
        _connections.push(c);
    }

    public void service (HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        if (VERBOSE) log("Path: " + req.getServletPath());
        String qs = req.getQueryString();
        if (VERBOSE) log("QueryString: " + qs);
        Hashtable ht = null;
        if (qs != null)
        {
            ht = HttpUtils.parseQueryString(qs);
            if (ht == null)
            {
                connectError(res, "Error reading query parameters");
            }
        }

        String operation = getParam(res, req, URLDbio.TDS_OPERATION, ht, false);
        // if operation was set, this is a jConnect client, not a html
        // client, do not try to read the POST data and thus loose the TDS
        String query = getParam(res, req, QUERY, ht, (operation == null));

        if (VERBOSE) log("query is: " + query + ", and operation = >" +
        operation + "<");

        // get a connection from the pool
        SybConnection c = getConnection();
        try
        {
            if (query == null)
            {
                // this is a jConnect client w/TDS in the POST content
                if (operation == null || operation.equals(URLDbio.OPERATION_CLOSE))
                {
                    // ignore this operation -- we don't close connections
                    // in the pool.
                    res.setContentLength(0);
                    putConnection(c);
                    res.setStatus(res.SC_OK);
                    return;
                }
                InStreamMgr ism = null;
                try
                {
                    ism = (InStreamMgr) c.getEndpoint();
                }
                catch (SQLException sqe)
                {
                    // XXX this should never happen
                    connectError(res, sqe.toString());
                }
                int length = req.getContentLength();
                if (VERBOSE) log("reading " + length + " bytes.");
                int outLength = 0; 
                Vector packets = new Vector();
                res.setContentType("application/binary");
                res.setStatus(res.SC_OK);
                res.setHeader(URLDbio.TDS_SESSION, 
                    getParam(res, req, URLDbio.TDS_SESSION, ht, false));
                try
                {
                    // read everything from the input
                    if (length > 0)
                    {
                        DataInputStream dis = new DataInputStream(
                            req.getInputStream());
                        if (VERBOSE) log("available = " + dis.available());
                        byte[] request = new byte[length];
                        dis.readFully(request);

                        // send the request on to the server
                        OutputStream serverOS = ism.getOutputStream();
                        serverOS.write(request);
                        serverOS.flush();
                    }

                    // read the response stream, keep track of length
                    DataInputStream serverIS = new DataInputStream(
                        ism.getInputStream());
                    // tds comes in packets with fixed size headers, which
                    // tell the length of the remainder of the packet, and
                    // contain status that indicates if it is the last packet
                    // in the stream -- read until that last packet
                    while(true)
                    {
                        byte[] header = new byte[Tds.NETHDRSIZE];
                        serverIS.readFully(header);
                        packets.addElement(header);

                        int status = header[1] & LOW_BYTE;
                        int packetLen = ((header[2] & LOW_BYTE) << 8) + 
                            (LOW_BYTE & header[3]);

                        byte[] packetBody = new byte[packetLen - 
                            Tds.NETHDRSIZE];
                        serverIS.readFully(packetBody);
                        packets.addElement(packetBody);
                        outLength += packetLen;

                        if ((status & Tds.BUFSTAT_EOM) != 0) break;
                    }
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                    putConnection(c);
                    connectError(res, "IOError " + ioe.toString());
                }
                if (VERBOSE) log("response content length is " + outLength);
                res.setContentLength(outLength);
                ServletOutputStream out = res.getOutputStream();
                // write entire packet sequence back to client
                Enumeration e = packets.elements();
                try
                {
                    while(e.hasMoreElements())
                    {
                        out.write((byte[]) e.nextElement());
                    }
                }
                catch (IOException ioe)
                {
                    // too late to do anything ...
                    log("IOException writing tds body to client: " +
                        ioe.getMessage());
                }
            }
            else
            {
                // this is just a text query from a browser.  Execute the
                // query as a language request, format the response as html
                htmlQuery(c, query, res);
                res.setStatus(res.SC_OK);
            }
        }
        catch (Exception e)
        {
            // whenever anything bad happens to the connection, close it
            try
            {
                c.close();
            }
            catch (SQLException sqe)
            {
                log("Exception closing connection: " + sqe.toString());
            }
            // and open another for the pool
            try
            {
                anotherConnection();
            }
            catch (Exception e2)
            {
                log("Exception opening new connection: " + e2.toString());
            }
            // and report the error
            if (e instanceof ServletException)
            {
                throw((ServletException) e);
            }
            if (e instanceof IOException)
            {
                throw((IOException) e);
            }
            connectError(res, "Unexpected exception type: " + e.toString());
        }
        // Return this connection back to the pool
        putConnection(c);
    }

    public String getServletInfo() 
    {
        return "This servlet multiplexes SQL requests from HTTP.";
    }

    // Private methods
    // return a Connection from the pool.  If none are available, wait
    private SybConnection getConnection()
    {
        while(true)
        {
            synchronized(_connections)
            {
                try
                {
                    SybConnection c = (SybConnection) _connections.pop();
                    return c;
                }
                catch (EmptyStackException ese)
                {
                    try
                    {
                        _connections.wait();
                    }
                    catch (InterruptedException ie)
                    {
                    }
                }
            }
        }
    }
    // return a connection to the pool, notify any waiting servlet threads
    // that one has become available
    private void putConnection(SybConnection c)
    {
        synchronized(_connections)
        {
            _connections.push(c);
            _connections.notify();
        }
    }
    // throw an exception for errors during tunnelling
    private void connectError(HttpServletResponse res, String s) 
        throws ServletException
    {
        log("connectError, " + s);
        Thread.dumpStack();
        ServletException e = new ServletException("Connect Request Error");
        res.setStatus(res.SC_BAD_GATEWAY);
        throw(e);
    }
    // look up a parameter from the query-parameters hash table
    private String getParam(HttpServletResponse res, HttpServletRequest req,
        String key, Hashtable h, boolean tryParams)
        throws ServletException
    {
        Object o = null;
        if (h != null)
        {
            o = h.get(key);
        }
        String s = null;
        if (VERBOSE) log("Looking up param value for: " + key);
        if (o != null)
        {
            if (o instanceof String)
            {
                s = (String) o;
            }
            else if (o instanceof String[])
            {
                // just return the first of a multi-valued parameter?
                s = ((String[]) o)[0];
            }
            else
            {
                // hmmm
                connectError(res,
                    "Unknown object type returned from query parameter " +
                    o.toString());
            }
        }
        else
        {
            if (tryParams)
            {
                // try post body
                String[] ss = req.getParameterValues(key);
                if (ss != null)
                {
                    s = ss[0];
                }
            }
        }
        if (VERBOSE) log("Returning " + key + " value as " + s);
        return(s);
    }
    public void log(String msg)
    {
        if (_debug)
        {
            System.out.println("FixedSession: " + msg);
        }
    }

    // compose an html response to execution of the given query
    private void htmlQuery(Connection c, String query, HttpServletResponse res) 
        throws ServletException, IOException
    {
        res.setContentType("text/html");
        ServletOutputStream out = res.getOutputStream();
        out.println("<HTML><HEAD><TITLE>jConnect ISQL Servlet</TITLE>");
        out.println("</HEAD><BODY BGCOLOR=#eeeeff>");
        out.println("<FORM ACTION=\"fixed\" METHOD=\"POST\">");
        Statement s = null;
        try
        {
            s = c.createStatement();
            boolean haveRS = s.execute(query);
            int rows = 0;
            int resultCount = 0;
            while (haveRS || rows != -1)
            {
                if (haveRS)
                {
                    resultCount++;
                    out.println("<P><CENTER> Result Set # " + resultCount +
                        "</CENTER><HR>");
                    ResultSet rs = s.getResultSet();
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int numColumns = rsmd.getColumnCount();
                    out.println("<TABLE BORDER=\"" + (numColumns + 1) + 
                        "\"><TH>Row#");
                    for (int i = 1; i <= numColumns; i++)
                    {
                        out.println("<TH>" + rsmd.getColumnName(i));
                    }
                    out.println("<TR>");
                    for(int row = 1; rs.next(); row++)
                    {
                        out.println("<TH>" + row);
                        for (int i = 1; i <= numColumns; i++)
                        {
                            out.println("<TH>" + rs.getString(i));
                        }
                        out.println("<TR>");
                    }
                    out.println("</TABLE><HR>");
                }
                else
                {
                    rows = s.getUpdateCount();
                    if (rows >= 0)
                    {
                        out.println("<P>" + rows + 
                            ((rows == 1)?" row":" rows") + " affected");
                    }
                }
                SQLException sqe = s.getWarnings();
                while (sqe != null)
                {
                    out.println("<P><B>Warning:</B> " + sqe.getMessage());
                    sqe = sqe.getNextException();
                }
                s.clearWarnings();
                haveRS = s.getMoreResults();
            }
        }
        catch (SQLException sqe)
        {
            while (sqe != null)
            {
                out.println("<P><B>Exception:</B> " + sqe.getMessage());
                sqe = sqe.getNextException();
            }
        }
        finally
        {
            if (s != null) 
            {
                try
                {
                    s.close();
                }
                catch (SQLException sqe)
                {
                    connectError(res, sqe.toString());
                }
            }
        }
        out.println("<P><CENTER>Enter another query</CENTER>");
        out.println("<P><TEXTAREA name=\"QUERY\" cols=79 rows=5>" +
            query + "</TEXTAREA>");
        out.println(
            "<P><CENTER><INPUT type=\"submit\" Value=\"Done\"></CENTER>");
        out.println("</FORM></HTML>");
        out.flush();
        out.close();
    }
    // convert the given string to an integer, or return the default value
    // if the string is null, or does not represent an integer value
    private int intValue(String s, int def)
    {
        if (s != null)
        {
            try
            {
                return(Integer.valueOf(s).intValue());
            }
            catch (NumberFormatException nfe)
            {
                if (_debug) log("Error: " + s + "is not an integer. " + nfe.toString());
            }
        }
        return(def);
    }
    // Look up a value in the initialization properties, return the value
    // or the default.
    private String stringValue(String key, String def)
    {
        String s = getInitParameter(key);
        if (s == null)
        {
            s = def;
        }
        if (_debug) log("Init Parameter: " + key + " set to " + s);
        return s;
    }
}
