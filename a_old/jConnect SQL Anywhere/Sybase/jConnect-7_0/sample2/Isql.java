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

import java.awt.*;
import java.net.*;
import java.io.*;
import java.sql.*;
import com.sybase.jdbcx.*;
import java.util.*; // Properties
import java.awt.event.*;

public class Isql extends java.applet.Applet  implements ActionListener
{
    TextField _query;
    TextField _status;
    TextField _host, _port, _uid, _pass;
    Label     _hostLabel, _portLabel, _uidLabel, _passLabel;
    Label     _usedbLabel;
    TextField _usedb;
    Label     _queryLbl;
    Label     _resultLbl;
    TextArea  _result;
    Button    _goButton;
    Panel     _p;
    Color     _bkgrdColor = new Color(0,255,0);

    // Remember previous url so we know whether to reConnect
    String  _previousUrl = "";

    Connection _con;
    Statement _stmt;
    boolean   _queryInProgress = false;

    public Isql()
    {
        setLayout(new BorderLayout());
        setBackground(_bkgrdColor);
        _p = new Panel();
        add("Center", _p);
        _p.setBackground(_bkgrdColor);
        _p.setFont(new Font("Dialog", Font.PLAIN, 12));

        _queryLbl = new Label("Query:");
        _query = new TextField("none");
        _query.setBackground(Color.white);
        _hostLabel = new Label("SQL Server host name:");
        _portLabel = new Label("SQL Server port number:");
        _uidLabel = new Label("Username:");
        _passLabel = new Label("Password:");
        _usedbLabel = new Label("Database:");
        _host = new TextField(30);
        _host.setBackground(Color.white);
        _port = new TextField(30);
        _port.setBackground(Color.white);
        _uid = new TextField(30);
        _uid.setBackground(Color.white);
        _pass = new TextField(30);
        _pass.setBackground(Color.white);
        _usedb = new TextField(30);
        _usedb.setBackground(Color.white);
        _goButton = new Button("Go");
        _goButton.addActionListener(this);
        _resultLbl = new Label("Results:");
        _result = new TextArea(15,60);
        _result.setEditable(false);
        _result.setBackground(Color.white);
        /*
        **      GridBagLayout version
        */
        GridBagLayout g = new GridBagLayout();
        GridBagConstraints gbCon =  new GridBagConstraints();
        gbCon.anchor = GridBagConstraints.CENTER;
        gbCon.fill = GridBagConstraints.BOTH;
        gbCon.gridheight = 1;
        gbCon.gridwidth = 1;
        gbCon.ipadx = 0;
        gbCon.ipady = 0;
        gbCon.weightx = 1.0;
        gbCon.weighty = 1.0;

        gbCon.gridx = 0;
        gbCon.gridy = 0;
        gbCon.insets = new Insets(5,5,0,5);
        g.setConstraints(_hostLabel, gbCon);
        gbCon.gridy = 1;
        gbCon.insets = new Insets(0,5,5,5);
        g.setConstraints(_host, gbCon);

        gbCon.gridx = 1;
        gbCon.gridy = 0;
        gbCon.insets = new Insets(5,5,0,5);
        g.setConstraints(_portLabel, gbCon);
        gbCon.gridy = 1;
        gbCon.insets = new Insets(0,5,5,5);
        g.setConstraints(_port, gbCon);

        gbCon.gridx = 2;
        gbCon.gridy = 0;
        gbCon.insets = new Insets(5,5,0,5);
        g.setConstraints(_uidLabel, gbCon);
        gbCon.gridy = 1;
        gbCon.insets = new Insets(0,5,5,5);
        g.setConstraints(_uid, gbCon);

        gbCon.gridx = 3;
        gbCon.gridy = 0;
        gbCon.insets = new Insets(5,5,0,5);
        g.setConstraints(_passLabel, gbCon);
        gbCon.gridy = 1;
        gbCon.insets = new Insets(0,5,5,5);
        g.setConstraints(_pass, gbCon);

        gbCon.gridx = 4;
        gbCon.gridy = 0;
        gbCon.insets = new Insets(5,5,0,5);
        g.setConstraints(_usedbLabel, gbCon);
        gbCon.gridy = 1;
        gbCon.insets = new Insets(0,5,5,5);
        g.setConstraints(_usedb, gbCon);

        gbCon.gridx = 0;
        gbCon.gridy = 2;
        gbCon.gridwidth = 5;
        gbCon.insets = new Insets(5,5,0,5);
        g.setConstraints(_queryLbl, gbCon);

        gbCon.gridy = 3;
        gbCon.insets = new Insets(0,5,5,5);
        g.setConstraints(_query, gbCon);

        gbCon.gridy = 4;
        gbCon.gridx = 1;
        gbCon.gridwidth = 3;
        gbCon.insets = new Insets(5,50,0,50);
        g.setConstraints(_goButton, gbCon);

        gbCon.gridx = 0;
        gbCon.gridwidth = 5;
        gbCon.gridy = 5;
        gbCon.insets = new Insets(5,5,0,5);
        g.setConstraints(_resultLbl, gbCon);

        gbCon.gridy = 6;
        gbCon.insets = new Insets(0,5,5,5);
        g.setConstraints(_result, gbCon);

        _p.setLayout(g);

        /*
        **      GridLayout version
        */
        //        _p.setLayout(new GridLayout(8,1,5,5));

        _p.add( _hostLabel);
        _p.add( _host);
        _p.add( _portLabel);
        _p.add( _port);
        _p.add( _uidLabel);
        _p.add( _uid);
        _p.add( _passLabel);
        _p.add( _pass);
        _p.add( _usedbLabel);
        _p.add( _usedb);
        _p.add( _queryLbl);
        _p.add( _query);
        _p.add( _goButton);
        _p.add( _resultLbl);
        _p.add( _result);
    }

    /**
    * handleEvent may be overridden by subclasses that want to get
    * notified when AWT events that are sent by the gui components.
    * The return value should be true for handled events, and
    * super.handleEvent should be called for unhandled events.
    * If super.handleEvent is not called, then the specific event
    * handling methods will not be called.
    */
    public void actionPerformed (ActionEvent e)
    {
        String arg = e.getActionCommand(); // get the button that we used
        if ("Go".equals(arg))
        {
            if (!_queryInProgress)
            {
                _queryInProgress = true;
                _result.setForeground(Color.black);
                doQuery();
                _queryInProgress = false;
            }
            else
            {
                showStatus("Query in progress.  Please wait...");
            }
            return ;
        }
        return ;
    }

    public void init() 
    {
        SybDriver sybDriver = null;
        // Force the Sybase jdbc driver to be loaded
        try
        {
            Class c = Class.forName("com.sybase.jdbc4.jdbc.SybDriver");
            sybDriver = (SybDriver) c.newInstance();
            DriverManager.registerDriver((Driver) sybDriver);
        }
        catch (Exception e)
        {
            displayError("Unable to load the Sybase JDBC driver. " 
                + e.toString());
            e.printStackTrace(System.out);
        }
        // turn on debugging 
        String debugClasses = getParameter("debug");
        if (debugClasses != null)
        {
            try
            {
                Debug debug = sybDriver.getDebug();
                debug.debug(true, debugClasses, System.out);
            }
            catch (Exception e)
            {
                displayError("Unable enable debugging: "
                    + e.toString());
                e.printStackTrace(System.out);
            }
        }
    }

    public void start()
    {
        String s;
        s = getParameter("host");
        if (s != null)
        {
            _host.setText(s);
        }
        s = getParameter("port");
        if (s != null)
        {
            _port.setText(s);
        }
        s = getParameter("uid");
        if (s != null)
        {
            _uid.setText(s);
        }
        s = getParameter("pass");
        if (s != null)
        {
            _pass.setText(s);
        }
        s = getParameter("usedb");
        if (s != null)
        {
            _usedb.setText(s);
        }
        s = getParameter("query");
        if (s != null)
        {
            _query.setText(s);
        }
    }

    public void stop()
    {
        try
        {
            _previousUrl = "";
            showStatus("Closing statement");
            if (_stmt != null)
            {
                _stmt.close();
                _stmt = null;
            }
            showStatus("Closing connection");
            if (_con != null)
            {
                _con.close();
                _con = null;
            }
        }
        catch (Exception e)
        {
            catchEx(e);
        }
    }

    private void doQuery()
    {
        int timeout;
        boolean needsReconnect = false;

        _result.setText("");
        String urlBase = "jdbc:sybase:Tds:";
        String host = _host.getText();
        String port = _port.getText();
        String url = urlBase + host + ":" + port;
        if (!_previousUrl.equals(url))
        {
            _previousUrl = url;
            needsReconnect = true;
        }

        String timeStr = getParameter("timeout");
        if (null == timeStr)
        {
            timeout = 0;
        }
        else
        {
            timeout = (new Integer(timeStr)).intValue();
        }

        String proxy = getParameter("proxy");
        Properties info = new Properties();
        if (info.get("user") == null || 
        !info.get("user").equals(_uid.getText()))
        {
            info.put("user", _uid.getText());
            needsReconnect = true;
        }
        if (info.get("password") == null || 
        !info.get("password").equals(_pass.getText()))
        {
            info.put("password", _pass.getText());
            needsReconnect = true;
        }
        String charset = getParameter("CHARSET");
        if (charset != null) info.put("CHARSET", charset);
        String language = getParameter("LANGUAGE");
        if (language != null) info.put("LANGUAGE", language);
        String connectProtocol = getParameter("CONNECT_PROTOCOL");
        if (connectProtocol != null) info.put("CONNECT_PROTOCOL", 
        connectProtocol);
        if (null == timeStr)
        {
            timeout = 0;
        }
        if (proxy != null)
        {
            if (proxy.indexOf("localhost") >= 0)
            {
                // the sample applet's gateway.html initially contains
                // localhost:8000 as a proxy -- if you run the applet
                // from ANY OTHER machine except the one which our
                // HTTP/JDBC Gateway (httpd.sh or httpd.bat) is on,
                // then localhost make no sense.  Assume that this
                // applet was just downloaded from that Gateway, and
                // that we really just want to connect back to that 
                // same gateway.
                host = getCodeBase().getHost();
                int iPort = getCodeBase().getPort();
                proxy = host + ":" + iPort;
            }

            info.put("proxy", proxy);
        }
        if (needsReconnect)
        {
            try 
            {

                if (_con != null)
                {
                    _con.close();
                }
                // Connect to the database at that URL.
                showStatus("Trying to connect to: " + url);
                DriverManager.setLoginTimeout(timeout);            
                _con = DriverManager.getConnection(url, info);
                _stmt = _con.createStatement();

            }
            catch (SQLException sqle)
            {
                displayError(sqle.toString() + " Restart connection.");
                return;
            }
            catch (Exception e)
            {
                catchEx(e);
                return;
            }
        }
        try
        {
            _stmt.setQueryTimeout(timeout);
            // try to use this db
            showStatus("Going to database: " + _usedb.getText());
            boolean restype = _stmt.execute("use " + _usedb.getText());
            if (restype)
            {
                // not expecting results!
                showStatus("Unexpected results from USE");
                _stmt.cancel();
            }
        }
        catch (SQLException sqle)
        {
            catchSQLEx(sqle);
        }
        catch (Exception e)
        {
            catchEx(e);
        }
        // Even if we caught an error while doing the USE, 
        // we're going to keep trying.  We don't want the query
        // results in red.
        _result.setForeground(Color.black);
        try
        {
            showStatus("Sending query: " + _query.getText());
            boolean results = _stmt.execute(_query.getText());
            int rsnum = 0;
            int rowsAffected = 0;
            do
            {
                if (results)
                {
                    rsnum++;
                    ResultSet rs = _stmt.getResultSet();
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int numColumns = rsmd.getColumnCount();
                    _result.append("\n------------------ Result set " 
                        + rsnum + " -----------------------\n");
                    StringBuffer column = new StringBuffer("Columns:");
                    for (int i = 1; i <= numColumns; i++)
                    {
                        column.append("\t" + rsmd.getColumnName(i));
                    }
                    _result.append(column.toString());
                    for(int rowNum = 1; rs.next(); rowNum++)
                    {
                        column = new StringBuffer("[ " + rowNum + "]");
                        for (int i = 1; i <= numColumns; i++)
                        {
                            column.append("\t" + rs.getString(i));
                        }
                        _result.append("\n" + column.toString());
                    }
                }
                else
                {
                    rowsAffected = _stmt.getUpdateCount();
                    if (rowsAffected >= 0)
                    _result.append("\n" + rowsAffected 
                        + " rows Affected.");
                }
                results = _stmt.getMoreResults();
            }
            while (results || rowsAffected != -1);
        }
        catch (SQLException sqle)
        {
            catchSQLEx(sqle);
        }
        catch (Exception e)
        {
            catchEx(e);
        }

    }
    private void catchSQLEx(SQLException sqle)    
    {
        displayError(sqle.toString() + " Cancelling...");
        try
        {
            _stmt.cancel();
        }
        catch (SQLException sqle2)
        {
            displayError(sqle2.toString() + " after " + sqle.toString());
        }
    }

    private void catchEx(Exception e)
    {
        e.printStackTrace();
        displayError("Unexpected Exception: " + e.toString());
    }

    private void displayError(String str)
    {
        _result.setText(str);
        _result.setForeground(Color.red);
    }
}

