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

import javax.servlet.*;
import javax.servlet.http.*;

import com.sybase.jdbc4.timedio.URLDbio;
import com.sybase.jdbc4.tds.Tds;
import com.sybase.jdbc4.IConstants;

/**
 * This class handles HTTP Tunnelling of TDS between jConnect clients
 * and any TDS conversant Server, and additionally trims out as many
 * DONE_IN_PROC tokens as it can
 */
public class TDSTunnelServlet extends HttpServlet 
{
    // Constants:
    private static final int LOW_BYTE = 0x00FF; 
    private static final byte DONEPROC = (byte) 0xFE; 
    private static final byte DONEINPROC = (byte) 0xFF; 
    private static final String MAX_RESPONSE_SIZE = "TdsResponseSize";
    private static final int DEFAULT_MAX_RESPONSE_SIZE = 1000;
    private static final String SKIP_DONE_PROC = "SkipDoneProc";
    private static final boolean DEFAULT_SKIP_DONE_PROC = false;
    private static final String TDS_SESSION_TIMEOUT = "TdsSessionIdleTimeout";
    private static final int DEFAULT_IDLE_TIMEOUT = 600000; // 10 minutes
    private static final String DEBUG = "Debug";

    // set this next boolean to true and recompile for verbose debug output
    private static final boolean VERBOSE = false; 

    // Some administrative operations
    public static final String LIST = "list";
    public static final String KILL = "kill";

    // Members:
    // _connections is a group of connections, lookup by session-id
    private static Hashtable _connections = new Hashtable();
    private static int _maxResponseLength = DEFAULT_MAX_RESPONSE_SIZE;
    private static int _idleTimeout = DEFAULT_IDLE_TIMEOUT;
    private static boolean _skipDoneProc = DEFAULT_SKIP_DONE_PROC;
    private static TdsSessionManager _tsm = null;
    private boolean _debug;

    public void init(ServletConfig conf) throws ServletException 
    {
        super.init(conf);

        // determine if we should print debug info
        String debug = getInitParameter(DEBUG);
        System.out.println(DEBUG + " parameter is " + debug);
        _debug = (debug != null && debug.equals("true"));
        log("TDSTunnelServlet.initialize: enter");

        _maxResponseLength = intValue(getInitParameter(MAX_RESPONSE_SIZE),
            DEFAULT_MAX_RESPONSE_SIZE);
        log("TdsResponseSize set to: " + _maxResponseLength);
        _idleTimeout = intValue(getInitParameter(TDS_SESSION_TIMEOUT),
            DEFAULT_IDLE_TIMEOUT);
        log("TdsSessionIdleTimeout set to: " + _idleTimeout);
        _skipDoneProc = booleanValue(getInitParameter(SKIP_DONE_PROC),
            DEFAULT_SKIP_DONE_PROC);
        log("SkipDoneProc set to: " + _skipDoneProc);

        // start the TdsSessionManager to time out idle sessions
        _tsm = new TdsSessionManager(_idleTimeout, _connections, _debug);
        _tsm.start();
    }

    public void service (HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        if (VERBOSE) log("Path: " + req.getServletPath());
        if (VERBOSE) log("QueryString: " + req.getQueryString());
        Hashtable ht = HttpUtils.parseQueryString(req.getQueryString());
        if (ht == null)
        {
            connectError(res, "Error reading query parameters");
        }

        // get the necessary info from the query parameters
        String session = getParam(res, URLDbio.TDS_SESSION, ht);
        String operation = getParam(res, URLDbio.TDS_OPERATION, ht);

        if (VERBOSE) log("session is: " + session + ", and operation = >" + operation +
        "<");
        if (operation != null && operation.equals(LIST))
        {
            // this is administrative, just list the active sessions
            listSessions(session, req, res);
            return;
        }
        if (operation != null && operation.equals(KILL))
        {
            // this is administrative, terminate the specified sessions
            killSessions(session, req, res);
            return;
        }

        TdsSession ts = null;
        if (session == null)
        {
            // create a new session, or get the existing one
            ts = newSession();
            connectBackend(res, ht, ts);
            session = ts._sessionID;
            ts._clientAddr = req.getRemoteAddr();
            ts._protocol = req.getScheme();
            ts._skippingDIPs = _skipDoneProc;
            setSession(ts);
        }
        ts = getSession(session);
        if (ts == null)
        {
            connectError(res, "Unable to find session: " + session);
        }

        int length = req.getContentLength();
        if (VERBOSE) log("reading " + length + " bytes.");
        int outLength = 0; 
        Vector packets = new Vector();
        try
        {
            // read everything from the input
            if (length > 0)
            {
                DataInputStream dis = new DataInputStream(req.getInputStream());
                byte[] request = new byte[length];
                dis.readFully(request);

                // send the request on to the server
                OutputStream serverOS = ts._socket.getOutputStream();
                serverOS.write(request);
                serverOS.flush();
            }

            if (operation != null)
            {
                // read the response stream, keep track of length
                DataInputStream serverIS = new DataInputStream(
                    ts._socket.getInputStream());
                if ((operation.equals(URLDbio.OPERATION_POLL) &&
                (serverIS.available() > 0)) || 
                    (operation.equals(URLDbio.OPERATION_MORE)))
                {
                    // tds comes in packets with fixed size headers, which
                    // tell the length of the remainder of the packet, and
                    // contain status that indicates if it is the last packet
                    // in the stream -- read until that last packet
                    int junkToSkip = 0;
                    NEXT_PDU:
                    while(outLength < _maxResponseLength)
                    {
                        int skippedLength = junkToSkip;
                        byte[] header = new byte[Tds.NETHDRSIZE];
                        serverIS.readFully(header);
                        int status = header[1] & LOW_BYTE;
                        int packetLen = ((header[2] & LOW_BYTE) << 8) + 
                            (LOW_BYTE & header[3]);

                        log("packetlen = " + packetLen + "skipping " + skippedLength);
                        byte[] packetBody = new byte[packetLen - 
                            Tds.NETHDRSIZE];
                        serverIS.readFully(packetBody);
                        if (ts._skippingDIPs)
                        {
                            SKIP_DIP:
                            while (skippedLength < packetBody.length)
                            {
                                byte tokenType = packetBody[skippedLength];
                                switch (tokenType)
                                {
                                    case DONEPROC:
                                    case DONEINPROC:
                                        skippedLength += 9;
                                        continue SKIP_DIP;
                                    default:
                                        ts._skippingDIPs = false;
                                        break;
                                }
                                // un-skipable token seen
                                break;
                            }
                            if (ts._skippingDIPs)
                            {
                                // we ran off the end of the PDU w/out
                                // seeing anything but DONEPROCs, ignore
                                // the whole PDU -- remember any partial
                                // DONEPROC
                                junkToSkip = skippedLength - 
                                    (packetLen - Tds.NETHDRSIZE);
                                log("complete packet of dips");
                                continue NEXT_PDU;
                            }
                            else
                            {
                                // we hit an un-skippable token somewhere
                                // in the middle of the stream, adjust the
                                // stream so that it appears to start there.
                                if (skippedLength > 0)
                                {
                                    int remainingLength = packetLen -
                                        Tds.NETHDRSIZE - skippedLength;
                                    byte[] tmp = new byte[remainingLength];
                                    System.arraycopy(packetBody, skippedLength,
                                        tmp, 0, remainingLength);
                                    packetBody = tmp;
                                    packetLen -= skippedLength;
                                    header[2] = (byte) ((packetLen & 0xFF00) >> 8);
                                    header[3] = (byte) (packetLen & 0x00FF);
                                    log("saved the last " + remainingLength);
                                }
                            }
                        }
                        packets.addElement(header);
                        packets.addElement(packetBody);
                        outLength += packetLen;

                        if ((status & Tds.BUFSTAT_EOM) != 0) 
                        {
                            // next stream we should start looking for DIPs
                            ts._skippingDIPs = _skipDoneProc;
                            break;
                        }
                    }
                }
            }
        }
        catch (IOException ioe)
        {
            connectError(res, "IOError " + ioe.toString());
        }
        if (VERBOSE) log("response content length is " + outLength);
        res.setStatus(res.SC_OK);
        res.setContentType("application/binary");

        try
        {
            ServletOutputStream out = res.getOutputStream();
            if (outLength > 0)
            {
                res.setHeader(URLDbio.TDS_SESSION, session);
                res.setContentLength(outLength);
                // write entire packet sequence back to client
                Enumeration e = packets.elements();
                while(e.hasMoreElements())
                {
                    out.write((byte[]) e.nextElement());
                }
            }
            else
            {
                // the content is garbage, let jConnect know this,
                // some browsers don't like 0-contentLength responses
                res.setHeader(URLDbio.TDS_SESSION, URLDbio.IGNORE_SESSION);
                res.setContentLength(1);
                out.write(0);
            }
            out.close();
        }
        catch (IOException ioe)
        {
            // too late to do anything ...
            log("IOException writing tds body to client: " +
                ioe.getMessage());
        }
        if (operation != null && operation.equals(URLDbio.OPERATION_CLOSE))
        {
            if (VERBOSE) log("closing " + session);
            ts.close();
        }
        else
        {
            // Return this connection back to the idle session pool
            setSession(ts);
        }
    }

    public String getServletInfo() 
    {
        return "This servlet Tunnels TDS through HTTP for jConnect.";
    }

    // Private methods
    // parse the DNS address/port of target backend server
    protected void connectBackend(HttpServletResponse res, Hashtable h,
        TdsSession ts) throws ServletException
    {
        String host = (String) getParam(res, URLDbio.HOST, h);
        String port = (String) getParam(res, URLDbio.PORT, h);
        Socket socket = null;
        if (host == null || port == null)
        {
            connectError(res,
                "Host/Port information for destination database is missing");
        }

        Integer portI = null;
        try
        {
            portI = new Integer(port);
        }
        catch (NumberFormatException nfe)
        {
            connectError(res, "Port number " + port + " was invalid: " +
                nfe.toString());
        }
        // attempt to connect
        try
        {
            socket = new Socket(host, portI.intValue());
        }
        catch (IOException ioe)
        {
            connectError(res, "IO Error: " + ioe.toString());
        }
        ts._socket = socket;
        ts._timeout = intValue(getParam(res, URLDbio.TDS_TIMEOUT, h),
            _idleTimeout);
        // record some administrative info on this session
        ts._serverAddr = host + ":" + portI.toString();
    }
    // make up a new sessionID
    private synchronized TdsSession newSession()
    {
        TdsSession ts = new TdsSession();
        long now = System.currentTimeMillis();
        ts._sessionID = "Tds" + now;
        ts._expiration = now + _idleTimeout;
        ts._socket = null;
        return(ts);
    }
    // remove the given session from the list (it is busy again)
    protected static TdsSession getSession(String session)
    {
        return((TdsSession) _connections.remove(session));
    }
    // put an idle session into the list
    protected static void setSession(TdsSession ts)
    {
        ts._expiration = System.currentTimeMillis() + ts._timeout;
        _connections.put(ts._sessionID, ts);
    }
    // throw an exception for errors during tunnelling
    private void connectError(HttpServletResponse res, String s) 
        throws ServletException
    {
        log("connectError, " + s);
        ServletException e = new ServletException("Connect Request Error");
        if (_debug)
        {
            e.printStackTrace();
        }
        res.setStatus(res.SC_BAD_GATEWAY);
        throw(e);
    }
    // look up a parameter from the query-parameters hash table
    private String getParam(HttpServletResponse res, String key, Hashtable h)
        throws ServletException
    {
        Object o = h.get(key);
        if (o == null) return(null);
        String s = null;
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
        return(s);
    }
    public void log(String msg)
    {
        if (_debug)
        {
            System.out.println("TDSTunnelServlet: " + msg);
        }
    }

    // compose an html response that lists all active sessions
    private void listSessions(String session, HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException
    {
        res.setStatus(res.SC_OK);
        res.setContentType("text/html");
        ServletOutputStream out = res.getOutputStream();
        if (session == null || session.equals("ALL"))
        {
            out.println("<P>There are currently " + _connections.size() + 
                " active sessions.<P><HR><P>");
            out.println("<TABLE BORDER=\"1\"><TH>Session<TH>Server<TR>");
            Enumeration e = _connections.elements();
            while (e.hasMoreElements())
            {
                out.print("<P>");
                TdsSession ts = (TdsSession) e.nextElement();
                out.println("<TD><A HREF=\"" + req.getServletPath() + "?Operation=list&Tds-Session="
                    + ts._sessionID + "\">"
                    + ts._sessionID + "</A>" +
                    "<TD>" + ts._serverAddr +
                    "<TR>");
            }
            out.println("</TABLE>");
            out.println("<P><HR><A HREF=\"" + req.getServletPath() + "?Operation=kill&Tds-Session=ALL\">"
                + "Click here to terminate all sessions</A>");
        }
        else
        {
            TdsSession ts = (TdsSession) _connections.get(session);
            if (ts == null)
            {
                out.println("<P>Looks like that session has been closed");
            }
            else
            {
                Date d = new Date();
                d.setTime(ts._expiration);
                out.println("<P>Expires: " + d.toString());
                out.println("<P>Server: " + ts._serverAddr);
                out.println("<P>Client IP address: " + ts._clientAddr);
                out.println("<P>Protocol: " + ts._protocol);
                out.println("<P><HR><A HREF=\"" + req.getServletPath() + "?Operation=kill&Tds-Session="
                    + ts._sessionID + "\">"
                    + "Click here to kill " + ts._sessionID + "</A>");
            }
            out.println(
                "<P><CENTER><A HREF=\"" + req.getServletPath() + "?Operation=list\">Back to list" +
                "</CENTER>");
        }
        out.close();
    }
    // close one or ALL current sessions
    private void killSessions(String session, HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException
    {
        ServletOutputStream out = res.getOutputStream();
        int killed = 0;
        TdsSession ts = null;
        if (session.equals("ALL"))
        {
            Enumeration e = _connections.keys();
            while (e.hasMoreElements())
            {
                session = (String) e.nextElement();
                ts = getSession(session);
                ts.close();
                out.print("<P>");
                out.println("<P>" + ts._sessionID);
                killed++;
            }
        }
        else
        {
            ts = getSession(session);
            if (ts != null)
            {
                ts.close();
                out.println("<P>" + ts._sessionID);
                killed++;
            }
            else
            {
                out.println("<P>Unable to find session " + session);
            }
        }
        out.println("<P>" + killed + " sessions closed");
        listSessions("ALL", req, res);
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
                if (VERBOSE) log("Error: " + s + "is not an integer. " + nfe.toString());
            }
        }
        return(def);
    }
    // convert the given string to a boolean, or return the default value
    // if the string is null
    private boolean booleanValue(String s, boolean def)
    {
        if (s != null)
        {
            return(Boolean.valueOf(s).booleanValue());
        }
        return(def);
    }
}
/*
** Define a class which represents an active TDS session.
** It is this class which is stored/retrieved from the hash-table
** of sessions.
*/
class TdsSession
{
    // the connection to the backend-server
    protected Socket _socket;
    // when this connection should be closed because it has been idle too long
    protected long _expiration;
    // #milliseconds to allow the session to be idle between operations
    protected long _timeout;
    protected String _sessionID;
    protected String _clientAddr; // IP address of the client
    protected String _serverAddr; // hostname:port# of the backend server
    protected String _protocol; // http, https, etc.
    // maintain state on whether we are still scanning for DONEINPROC
    // and DONEPROC tokens in the current response stream
    protected boolean _skippingDIPs; // if true, look for and ignore DONEINPROCs

    // provide a methods for closing the server-side of this session
    protected void close() throws IOException
    {
        _socket.close();
    }
}

/*
** Define an internal class which monitors TdsSessions and closes
** them if they expire
*/
class TdsSessionManager extends Thread
{
    protected long _timeout;
    private Hashtable _connections;
    private boolean _debug;
    // set this next boolean to true and recompile for verbose debug output
    private static final boolean VERBOSE = false; 
    TdsSessionManager(long timeout, Hashtable connections, boolean debug)
    {
        _timeout = timeout;
        _connections = connections;
        _debug = debug;
    }
    public void run()
    {
        while(true)
        {
            long now = System.currentTimeMillis();
            if (VERBOSE) log("running TdsSessionManager at " + now);
            long nextExpiration = now + _timeout;
            // scan the table of connections looking for expired sessions
            Enumeration e = _connections.elements();
            while (e.hasMoreElements())
            {
                TdsSession ts = (TdsSession) e.nextElement();
                if (ts._expiration < now)
                {
                    // remove the element from the list
                    ts = TDSTunnelServlet.getSession(ts._sessionID);
                    if (ts != null)
                    {
                        if (VERBOSE) log("closing expired session " + ts._sessionID);
                        try
                        {
                            ts.close();
                        }
                        catch (IOException ioe)
                        {
                            if (VERBOSE) log("Caught unexpected exception: " +
                            ioe.getMessage());
                        }
                    }
                }
                else
                {
                    // keep track of the next expiration
                    if (ts._expiration < nextExpiration)
                    {
                        nextExpiration = ts._expiration;
                    }
                }
            }
            // sleep until the next possible timeout
            try
            {
                Thread.sleep(nextExpiration - now);
            }
            catch (InterruptedException ie)
            {
                // whatever
            }
        }
    }
    private void log(String msg)
    {
        if (VERBOSE)
        {
            if (_debug)
            {
                System.out.println("TDSTunnelServlet: " + msg);
            }
        }
    }
}
