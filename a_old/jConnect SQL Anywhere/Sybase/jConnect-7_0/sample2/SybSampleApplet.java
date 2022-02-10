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
import java.awt.*;
import java.net.*;
import java.io.*;
import java.sql.*;
import java.util.*; //Properties
import java.awt.event.*;

/** 
 * SybSampleApplet Class creates an applet
 * which consists of a run button.  When selected 
 * it instantiates the SybSample class, 
 * which is a sample driver application. 
 *
 */

public class SybSampleApplet extends java.applet.Applet implements ActionListener
{
    String _iHost = "";
    int    _iPort = 8000;
    String _sampleName;
    String _docBase;
    Button _run;

    public SybSampleApplet()
    {
        super();   
    }
    public void init()
    {
        super.init();
        createAppletForm();
    }
    /**
     * The interface for this applet is one
     * run button. It also retrieves the
     * host and port number in order to access 
     * the sql server.
    */   
    public void createAppletForm()
    {
        _iHost = getCodeBase().getHost();
        _iPort = getCodeBase().getPort();

        // When you are accessing the samples from an applet, 
        // in order to access and open the files without security
        // restrictions, you need to open up as a URL.  
        // Therefore the _docBase should be in http://localhost:8000/sample2/

        _docBase = getDocumentBase().toString();  
        _docBase = _docBase.substring(0, _docBase.lastIndexOf('/')+1);
        _run = new Button("Run");
        _run.addActionListener(this);
        setBackground(new Color(0, 130, 230));
        add(_run);
        setVisible(true);
    }
    /** 
     * Controls the event for the run button
     * which is to instantiate the SybSample Driver
     * @param evt    the java ActionEvent
     */
    public void actionPerformed (ActionEvent evt)
    {
        String arg = evt.getActionCommand(); // get the button that we used
        if ("Run".equals(arg))
        {
            //getting the sample name from the html file parameter
            _sampleName = getParameter("sample");
            try
            {

                //setting it as an arg, so we can pass it like a 
                //commandline option to SybSample.
                String args[] = 
                {
                    _sampleName 
                }
                ;

                // instantiate the SybSample class,
                SybSample ss = new SybSample(args, _iHost+":"+_iPort,_docBase);               
            }
            catch (Exception ex)
            {
                System.out.println("Exception in Applet");
                showStatus(ex.toString());
                ex.printStackTrace();          
            }
            return ;
        }
        return ;
    }
}




