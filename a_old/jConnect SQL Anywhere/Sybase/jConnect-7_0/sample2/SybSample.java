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
import com.sybase.jdbcx.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.awt.event.*;

/** 
 * SybSample Class is a sample driver, allowing 
 * the user to run any of the sybase samples except
 * IsqlApp and IsqlApplet from the command line 
 * or from an applet.  This launches a GUI application 
 * that runs a sample, displays the output, and gives
 * the option to open and run another sample.
 */
public class SybSample extends Frame implements Runnable, ActionListener
{
    //MEMBERS
    static CommandLine _cmdline = new CommandLine();
    static SybDriver _sybDriver = null;
    static String _filename;
    static String _dir = null; //directory for sample sources 
    Thread _fileViewThread;

    // GUI members
    static String _sampleName;
    static Panel _connectionPanel, _samplePanel, _outputPanel;
    static TextArea _output, _status;
    TextArea  _sampleSource;
    static Label _userLbl, _passwordLbl, _urlLbl, _runnLbl, _outputLbl, _statusLbl;
    static TextField _userField, _passwordField, _urlField, _sampleField;
    static Button _newRun, _close;
    static MenuBar _menubar;
    static Menu _fileM, _helpM;
    static FileDialog _fileDialog;
    static boolean _calledByApplet = false;

    //commandline options to include into SybSample
    /**
     * Called when running as an application.
     * Processes the commandline options and starts the driver 
     * by creating the frame.
     * @param args  will be processed as commanline args.
     */
    public SybSample(String args[])
    {
        this();
        _cmdline.processCommandline(args, _sybDriver);
        createAFrame();   
    }
    /**
     * Called when running as an applet.  Sets some variables,
     * processes the commandline options, and starts the driver 
     * by creating the frame.
     * @param args  will be processed as commanline args.
     * @param proxy  to be added to the props and sent for connection
     * @param dir  the document base for the fileviewer as well as 
     *     those samples that need to open files for reading.
     */
    public SybSample(String args[], String proxy, String dir)
    {
        this();
        _calledByApplet = true;
        _dir = dir;
        _cmdline.processCommandline(args, _sybDriver);
        _cmdline._props.put("proxy", proxy);
        createAFrame();
    }
    public SybSample()
    {
        super("SybSample");
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        // Load the Driver
        try 
        {
            _sybDriver = (SybDriver)Class.forName(
                "com.sybase.jdbc4.jdbc.SybDriver").newInstance();
            DriverManager.registerDriver((Driver)_sybDriver);
        }
        catch (java.lang.Exception ex)
        {
            // Got some other type of exception.  Dump it.
            ex.printStackTrace ();
        }
    }
    //STATIC METHODS

    /**
     * Main method that instantiates a sample based upon the 
     * commandline args passed or the defaults set within CommandLine.java
     * This method also instantiates the SybSample class to invoke
     * the GUI front end as a standalone application.
     * @param args commandline arguments
     */
    public static void main(String args[])
    {
        //get the commandline ops if any
        SybSample ss = new SybSample(args);
    }

    /**
     * Works like a System.out.println, except that the 
     * string passed will go to the front end,
     * which in this case is a gui java application.
     * NOTE: This does not print a \n at the end. 
     * This will need to be explicitly inserted by 
     * the user. <br>
     * Output is sent to the Sample output textArea.
     * @param message  the string you want sent to the output area
     */
    public static void output(String message)
    {
        _output.append(message);
    }
    /**
     * Print out a message similar to System.err.println
     * and send it the the java application
     * in the status text area
     * @param message  the string you want sent to the status area
     */
    public static void error(String message)
    {
        _status.append(message);
    }

    /**
     * Checks for the existence of the classfile based on the _sampleName
     * passed in as an argument to the commandline.
     * Instantiates and runs the sample.
     */
    public void instantiateSample()
    {
        error("Executing Sample\n");       
        try
        {
            Class s = Class.forName("sample2."+_sampleName);
            Sample aSample = (Sample)s.newInstance();
            aSample.settings(this, _sybDriver, _dir, _calledByApplet, _cmdline);
            aSample.start();  //Start the sample Thread.
        }
        catch(java.lang.Exception e)
        {
            error("\n Sample Program "+ _sampleName +" not found\n");
        }
    }

    // Helper Methods
    /** 
     * Creates three panels, connectionPanel, samplePanel, and outputPanel,
     * to simplify the frame creation process.<br>
     * <ul>
     *   <li>connectionPanel uses FlowLayout<br>
     *   <li>samplePanel uses GridBagLayout<br>
     *   <li>outputPanel uses GridBagLayout <br>
     *   <li>frame uses BorderLayout<br>
     * </ul>
     * Each panel's components are instantiated and added to the panel.
     * Finally the 3 panels are then added to the main frame
     * to complete the SybSample window.
     */
    public void createAFrame()
    {
        //CREATING GUI

        this.setSize(550, 500);
        //should be setSize(550,500);
        _sampleName = _cmdline._props.getProperty("progName");
        _filename = _sampleName+".java";

        // MENU - on frame

        _menubar = new MenuBar();
        setMenuBar(_menubar);
        //create the file menu bar
        _fileM = new Menu("File");
        MenuItem openSample = new MenuItem("Open Sample");
        openSample.addActionListener(this);
        _fileM.add(openSample);
        _fileM.addSeparator();
        MenuItem closeApp = new MenuItem("Close");
        closeApp.addActionListener(this);
        _fileM.add(closeApp);
        _helpM = new Menu("Help");
        MenuItem aboutApp = new MenuItem("About");
        aboutApp.addActionListener(this);
        _helpM.add(aboutApp);
        _menubar.add(_fileM);
        _menubar.add(_helpM);
        _menubar.setHelpMenu(_helpM);

        _fileDialog = new FileDialog(this, "File Dialog", FileDialog.LOAD);

        // CONNECTION PANEL
        // connection panel used to house connection information
        // user, password, and url

        _connectionPanel = new Panel();
        _connectionPanel.setBackground(new Color(0, 130, 230));
        _userLbl = new Label("user");
        _userLbl.setFont(new Font("Dialog", Font.BOLD, 12));
        _passwordLbl = new Label("password");
        _passwordLbl.setFont(new Font("Dialog", Font.BOLD, 12));
        _urlLbl = new Label("url");
        _urlLbl.setFont(new Font("Dialog", Font.BOLD, 12));
        _userField = new TextField(10);
        _userField.setBackground(Color.white);
        _userField.setText(_cmdline._props.getProperty("user"));
        _passwordField = new TextField(10);
        _passwordField.setBackground(Color.white);
        _passwordField.setText(_cmdline._props.getProperty("password"));
        _urlField = new TextField(30);
        _urlField.setBackground(Color.white);
        _urlField.setText(_cmdline._props.getProperty("server"));

        // set the layout to be used for the connectionPanel
        // and add components to that panel

        _connectionPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        _connectionPanel.add(_userLbl);
        _connectionPanel.add(_userField);
        _connectionPanel.add(_passwordLbl);
        _connectionPanel.add(_passwordField);
        _connectionPanel.add(_urlLbl);
        _connectionPanel.add(_urlField);

        // SAMPLE PANEL
        // sample panel displaying sample information
        //  - name of current sample running
        //  - show source button to give the option of
        //   showing the source code of the current sample
        //  - text field to display the source code if selected

        _samplePanel = new Panel();
        _samplePanel.setBackground(new Color(0, 130, 230));
        _runnLbl = new Label("Running Sybase Sample", Label.CENTER);
        _runnLbl.setFont(new Font("Dialog", Font.BOLD, 12));
        _sampleField = new TextField(15);
        _sampleField.setText(_sampleName);
        _sampleField.setBackground(Color.white);
        _sampleSource = new TextArea(15, 20);
        _sampleSource.setBackground(Color.white);

        // set layout for samplePanel
        // using GridBagLayout in order to have better control
        // of where the different components are placed

        GridBagLayout g = new GridBagLayout();
        GridBagConstraints gbCon = new GridBagConstraints();
        gbCon.anchor = GridBagConstraints.CENTER;
        gbCon.fill = GridBagConstraints.BOTH;
        gbCon.gridheight = 1;
        gbCon.gridwidth = 1;
        gbCon.ipadx = 0;
        gbCon.ipady = 0;
        gbCon.weightx = 1.0;
        gbCon.weighty = 1.0;

        //add new gui grids here
        gbCon.gridx = 1; gbCon.gridy = 0;
        gbCon.insets = new Insets(0,0,5,0);
        g.setConstraints(_runnLbl, gbCon);
        gbCon.gridx = 2;
        gbCon.insets = new Insets(5,2,5,5);
        g.setConstraints(_sampleField, gbCon);

        gbCon.gridx = 1; gbCon.gridy = 3;
        gbCon.gridwidth = 5;
        gbCon.insets = new Insets(5,5,8,8);
        g.setConstraints(_sampleSource, gbCon);

        //add components to the sample panel
        _samplePanel.setLayout(g);
        _samplePanel.add(_runnLbl);
        _samplePanel.add(_sampleField);
        _samplePanel.add(_sampleSource);

        // OUTPUT PANEL
        // output panel, includes the Sample output and status
        // plus run new sample button and close button 

        _outputPanel = new Panel();
        _outputPanel.setBackground(new Color(0, 130, 230));
        _outputLbl = new Label("Sample Ouput");
        _outputLbl.setFont(new Font("Dialog", Font.BOLD, 12));
        _output = new TextArea(10,20);
        _output.setBackground(Color.white);
        _statusLbl = new Label("Status");
        _statusLbl.setFont(new Font("Dialog", Font.BOLD, 12));
        _status = new TextArea(4,20);
        _newRun = new Button("RUN SAMPLE");
        _newRun.addActionListener(this);
        _close = new Button("CLOSE");
        _close.addActionListener(this);

        // set layout for outputPanel - GridBagLayout

        GridBagLayout g2 = new GridBagLayout();
        GridBagConstraints gbCon2 = new GridBagConstraints();
        gbCon2.anchor = GridBagConstraints.CENTER;
        gbCon2.fill = GridBagConstraints.BOTH;
        gbCon2.gridheight = 1;
        gbCon2.gridwidth = 1;
        gbCon2.ipadx = 0;
        gbCon2.ipady = 0;
        gbCon2.weightx = 1.0;
        gbCon2.weighty = 1.0;

        //add new gui grids here
        gbCon2.gridx = 1; gbCon2.gridy = 0;
        gbCon2.insets = new Insets(0,0,0,0);
        g2.setConstraints(_outputLbl, gbCon2);
        gbCon2.gridy = 1;
        gbCon2.gridwidth = 5;
        gbCon2.insets = new Insets(2,5,0,8);
        g2.setConstraints(_output, gbCon2);

        gbCon2.gridx = 1; gbCon2.gridy = 3;
        gbCon2.insets = new Insets(2,2,0,2);
        g2.setConstraints(_statusLbl, gbCon2);
        gbCon2.gridy = 4;
        gbCon2.gridwidth = 5;
        gbCon2.insets = new Insets(2,5,5,8);
        g2.setConstraints(_status, gbCon2);

        gbCon2.gridx = 3; gbCon2.gridy = 6;
        gbCon2.gridwidth = 1;
        gbCon2.insets = new Insets(5,2,5,8);
        g2.setConstraints(_newRun, gbCon2);
        gbCon2.gridx = 4;
        gbCon2.insets = new Insets(5,2,5,5);
        g2.setConstraints(_close, gbCon2);

        //add components to the sample panel
        _outputPanel.setLayout(g2);
        _outputPanel.add(_outputLbl);
        _outputPanel.add(_output);
        _outputPanel.add(_statusLbl);
        _outputPanel.add(_status);
        _outputPanel.add(_newRun);
        _outputPanel.add(_close);

        // MAIN FRAME WINDOW
        // adding the three different panels to 
        // create the window frame
        // The frame uses the BorderLayout

        add("North",_connectionPanel);
        add("Center",_samplePanel);
        add("South",_outputPanel);
        pack();
        setVisible(true);

        runNewSample();
    }
    /** 
     * will load the new sample code into the sample source text area,
     * instantiate the new sample, and run it.
     */
    public void runNewSample()
    {
        //and start a new thread for this new open sample
        _fileViewThread = new Thread(this);
        _fileViewThread.start();

        //fileViewer();
        error("Instantiating new sample\n");
        instantiateSample();
    }
    /** 
     * start the sample thread
     */
    public void run()
    {
        fileViewer();
        //stop the current thread from executing, 
        if(_fileViewThread.isAlive())
        {
            error("FileView Done.\n");
            _fileViewThread.stop();
        }
        error("If I get here, the thread was not stopped\n");
    }

    /**
     * Displays the current Sample that you wish to run in _sampleSource TextArea.
     * <ul>In order to locate the sample file, 
     *    <li> if you open the sample using the File, Open Sample option, it obviously
     *      knows exactly the directory in which to find the sample.
     *    <li> tries finding the sample in two locations:
     *       $JDBC_HOME, and $JDBC_HOME/sample2
     *    <li> if the file cannot be found, it will display a message in the status 
     *      box suggesting that the user open the file using the File, Open Sample menu.
     * </ul>
     */
    public void fileViewer()
    {
        File f = null;
        BufferedReader br = null;

        try
        {
            _sampleSource.setText("");
            //The following code sets the Buffered Reader for either an
            //applet(URL) or an application(File).
            if(_calledByApplet)
            {
                try
                {
                    //Use the URL object to be able to view the file over 
                    //the network.  "http://localhost:8000/sample2/Sample.java"
                    URL source = new URL(_dir+_filename);
                    br = new BufferedReader(new BufferedReader(
                        new InputStreamReader(source.openStream())));
                }
                catch(Exception e)
                {
                    error(e.toString()+"\n");
                }
            }
            else //running as an application
            {
                if (_dir == null) //assume you are running this from $JDBC_HOME
                {
                    _dir = "sample2"+File.separatorChar;
                }
                //otherwise use the current directory or codebase set: set 
                //by a previous call to fileviewer or by the applet 

                for(int tryDir = 0;;tryDir++)
                {
                    try
                    {
                        f = new File(_dir+_filename);
                        br= new BufferedReader(new FileReader(f));
                        break;  //found the file
                    }
                    catch (Exception e)
                    {
                        //now look in the current dir for the sample source
                        _dir = ""; 
                        if (tryDir > 0)
                        {
                            error("Assumed you where running from either $JDBC_HOME"
                                +" or $JDBC_HOME"+File.separatorChar+"sample2.\n"
                                +" SybSample is searching for .."+File.separatorChar
                                +"sample2"+File.separatorChar+_sampleName+".java\n"
                                +" and can not find it.  Try opening the sample using the "
                                +" File, Open Sample option\n");
                            throw e;
                        }
                    }
                }
            }

            //Now that you got the file, spit it out to the
            //sample source TextArea
            String line;
            int i = 1;
            _sampleSource.append(_filename+"\n");
            //print file out to sampleSource TextArea
            while ((line = br.readLine()) != null) 
            {
                //also print out line #'s
                _sampleSource.append(i++ +"\t");
                _sampleSource.append(line+"\n");
            }
            _sampleSource.append("\n end of "+_sampleName); 
        }
        catch(Exception e)
        {
            error(e.toString()+"\n");
        }
    }
    /**
     * close the SybSample Frame
     * and stop execution of SybSample Driver.
     */
    public void closeWindow()
    {
        dispose(); //close frame 
        try
        {
            System.exit(0); //stop the application
        }
        catch(Exception e)
        {
            //if this is called by an applet, it complains, 
            //because it doesn't like be terminated
            if(!_calledByApplet)
            error("\n"+e.toString()+"\n");
        }
    }
    /**
     *  Process the WindowEvent and if we are closing the window, make sure
     *  it happens
     *   @param e  event
     */
    protected void processWindowEvent(WindowEvent e)
    {
        if(e.getID() == WindowEvent.WINDOW_CLOSING)
        {
            dispose();
            if(!_calledByApplet)
            {
                System.exit(0);
            }
        }
        super.processWindowEvent(e);
    }

    /** 
     * Captures the actions for the 2 buttons and 3 menu items
     * <ul> 
     *   <li>_close - closes the frame and stops execution of SybSample<br>
     *   <li>_newRun - Runs the sample currently sample, that is displayed in Running 
     *           Sample, and in the sampleSource window to allow you to 
     *           run it again, if you desire.<br>
     *   <li>Open Sample - allows you to choose the sample you wish to 
     *      open by bring up a file dialog, which will display the files
     *       in the current directory <br>
     *   <li>Close -  will close the current frame and the SybSample Application<br>
     *   <li>About Box -  simply gives some identifying info<br>
     *  </ul>
     *   @param e  Actionevent
     */
    public void actionPerformed (ActionEvent e)
    {
        String arg = e.getActionCommand(); // get the button that we used

        if ("CLOSE".equals(arg))
        {
            closeWindow();
        }
        else if ("RUN SAMPLE".equals(arg))
        {
            //check if commandline ops have changed
            if (! _cmdline._props.getProperty("server").equals(_urlField.getText())) 
            {
                //changed, need to set new _cmdline._props for connection
                _cmdline._props.put("user", _userField.getText());
                _cmdline._props.put("password", _passwordField.getText());
                _cmdline._props.put("server", _urlField.getText());
                error("\n Connection Panel has changed");
            }
            _output.setText("");
            //otherwise, they have not changed and use them again
            instantiateSample(); //execute sample
        }
        if("Open Sample".equals(arg))
        {
            if(_calledByApplet && (_dir.indexOf("localhost") == -1))
            {
                //connected remotely
                error("You are logging in remotely. Therefore, the file\n"+
                    "dialog will be unable to display the directory\n"+
                    "list of the remote location, but if you know the\n"+
                    "name of the sample file, including .java, simply\n"+
                    "enter it in the file name field.\n");
            }
            _fileDialog.pack();
            _fileDialog.setVisible(true);  //blocks until user selects a file
            _filename = _fileDialog.getFile();
            if(!_calledByApplet)
            _dir = _fileDialog.getDirectory();
            error("Directory of file: "+_dir+"\n");
            _sampleName = _filename.substring(0,_filename.indexOf("."));
            _sampleField.setText(_sampleName);
            error("You selected file:"+_filename+"\n"); 
            _output.setText("");
            _sampleSource.setText("");
            runNewSample();                 
        }
        else if("About".equals(arg))
        {
            error("Selected About\n");
            InfoDialog d;
            d = new InfoDialog(this, "About SybSample", 
                "SybSample Driver", 
                "Samples' Author: Lance Andersen\n"+
                "SybSample Driver Author: Maria Chavez\n"+
                "Owning Group: jConnect Engineering\n"+
                "Owning Company: Sybase, Inc.\n\n"+
                "The SybSample Driver is a harness for sample programs\n"+
                "which will enable the user to run the sample from the \n"+
                "commandline or from an applet, by simply calling the \n"+
                "SybSample application or the SybSampleApplet,which instantiates \n"+
                "the SybSample class, and executes each sample individually,\n"+
                "displaying them with a GUI interface.\n", _calledByApplet);
            d.setVisible(true);
        }
        else if("Close".equals(arg))
        {
            closeWindow();
        }

    }

}
// #############################################################################
// Support class for dialog boxes
class InfoDialog extends Dialog implements ActionListener
{
    protected Button _button;
    protected Label _label;
    protected TextArea _aboutMe;
    protected boolean _calledByApplet;
    public InfoDialog(Frame parent, String title, String label, String message,
        boolean calledByApplet)
    {
        //Create a dialog with the specified title
        super(parent, title, false);
        //Create and use a BorderLayout managers with specified margins
        this.setLayout(new BorderLayout(15,15));
        //Create the message component and add it to the window
        _label = new Label(label);
        this.add("North", _label);
        //TEXT PANEL
        Panel textPanel = new Panel();
        TextArea aboutMe = new TextArea();
        aboutMe.setText(message);
        textPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        textPanel.add(aboutMe);
        this.add("Center", textPanel);
        //BUTTON PANEL
        //create an ok button in a Panel; add the panel to the window
        //use a flowlayout to center the button and give it margins
        _button = new Button("OK");
        _button.addActionListener(this);
        Panel p = new Panel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        p.add(_button);
        this.add("South",p);
        this.pack();
        _calledByApplet = calledByApplet;
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    }
    public void actionPerformed(ActionEvent e)
    {
        String arg = e.getActionCommand();
        if ("OK".equals(arg))
        {
            this.setVisible(false);
            this.dispose();
        }
    }
    //give keyboard focus to button
    public boolean getFocus(Event e, Object arg)
    {
        _button.requestFocus();
        return true;
    }

    /**
     *  Process the WindowEvent and if we are closing the window, make sure
     *  it happens
     *   @param e  event
     */
    protected void processWindowEvent(WindowEvent e)
    {
        if(e.getID() == WindowEvent.WINDOW_CLOSING)
        {
            dispose();
        }
        super.processWindowEvent(e);
    }


}
//end of InfoDialog
