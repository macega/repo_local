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
import com.sybase.jdbcx.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 *  Handles command line options for all samples.<br>
 *  Possible arguments include:
 *  <ul> 
 *     <li>  &ltSample Name&gt
 *     <li>  -U &ltusername&gt
 *     <li>  -P &ltpassword&gt 
 *     <li>  -S &ltservername&gt
 *     <li>  -G &ltgateway&gt
 *     <li>  -D &ltdebug-class-list&gt
 *     <li>  -H &lthostname&gt
 *     <li>  -J &ltcharacter set&gt
 *     <li>  -z &ltlanguage&gt
 *  </ul>
 */
public class CommandLine
{
    // Stash properties such as "user", "password", etc. into _props
    public static Properties _props;
    // If there are any extra arguments save off the argument and value
    public static Vector _extraArgs;
    public static Vector _extraOptions;
    private static String _propValue = null; // Current property value

    public CommandLine()
    {
        _props = new Properties();
        _extraArgs = new Vector();
        _extraOptions = new Vector();
        _propValue = null;
    }
    static public void processCommandline(String args[], SybDriver sybDriver)
    {
        String arg;
        int errorCount = 0;
        Debug debug = sybDriver.getDebug();

        _props.put("user", "guest");
        _props.put("password", "sybase");
        _props.put("server", "jdbc:sybase:Tds:jdbc.sybase.com:4444");

        //Need to get first argument which is now
        //going to be the Sample name with no "-" 
        //required (ie SybSample Sample -....)
        if (args.length == 0)
        errorCount++;
        else
        {
            _props.put("progName", args[0]);
            //System.out.println(args[0]);
        }
        for (int i = 1; i < args.length; i++)
        {
            arg = args[i];
            //System.out.println(args[i]);
            if (arg.regionMatches(0, "-", 0, 1))
            {
                try 
                {
                    switch(arg.charAt(1))
                    {
                        case 'D':
                            i += parseArguments(args, i);
                            try
                            {
                                if(_propValue != null)
                                {
                                    debug.debug(true, _propValue);
                                }
                                else
                                {
                                    errorCount++;
                                }
                            }
                            catch (IOException ioe)
                            {
                                // ignore
                            }
                            debug.println(null, "this is a test");
                            break;
                        case 'U':
                            i += parseArguments(args, i);
                            if(_propValue != null)
                            {
                                _props.put("user", _propValue);
                            }
                            else
                            {
                                errorCount++;
                            }
                            break;

                        case 'P':
                            i += parseArguments(args, i);
                            _props.put("password",
                                (_propValue == null ? "" : _propValue));
                            break;

                        case 'G':
                            i += parseArguments(args, i);
                            _props.put("proxy",
                                (_propValue == null ? "" : _propValue));
                            break;

                        case 'S':
                            i += parseArguments(args, i);
                            if(_propValue != null)
                            {
                                _props.put("server",  _propValue);
                            }
                            else
                            {
                                errorCount++;
                            }
                            break;
                        case 'J':
                            i += parseArguments(args, i);
                            if(_propValue != null)
                            {
                                _props.put("CHARSET", _propValue);
                            }
                            else
                            {
                                errorCount++;
                            }
                            break;
                        case 'H':
                            i += parseArguments(args, i);
                            if(_propValue != null)
                            {
                                _props.put("HOSTNAME",  _propValue);
                            }
                            else
                            {
                                errorCount++;
                            }
                            break;
                        case 'z':
                            i += parseArguments(args, i);
                            if(_propValue != null)
                            {
                                _props.put("LANGUAGE", _propValue);
                            }
                            else
                            {
                                errorCount++;
                            }
                            break;

                        default:
                            // Save off the Extra arguments that are passed
                            _extraArgs.addElement(args[i]);
                            i++;
                            _extraOptions.addElement(args[i]);
                            break;
                    }
                }
                catch (ArrayIndexOutOfBoundsException aioobe)
                {
                    System.out.println("missing option argument");
                    errorCount++;
                }
            }
            else
            {
                // The syntax has no non "-" arguments
                errorCount++;
            }
        }

        if (errorCount != 0)
        {
            System.out.println(
                "Syntax:\n\tsample2.SybSample <Sample Name>"+ 
                " [-U <username>] [-P <password>] [-S <servername>]" + 
                "\n\t\t[-G <gateway>] [-D <debug-class-list>] [-H <hostname>]"  +
                "\n\t\t[-J <character set>] [-z <language>]");
            System.exit(1);
        }
    }
    // end of processCommandline

    /**
    *  Parse a command line argument.  Arguments may be supplied in
    *  2 different ways:<p>
    *  -Uusername<br>
    *  -U username<br>
    *  @param    argv     Array of command line arguments
    *  @param    pos      Current argument argv position
    *  @return            A value of 1 or 0 which is used to update our loop
    *                     counter.
    */
    private static int parseArguments(String argv[],  int pos)
    {
        int argc = argv.length-1; // # arguments specified
        String   arg = argv[pos].substring(1);  // Current argument minus the '-'
        int argLen  = arg.length(); // Length of arg
        int incrementValue = 0;

        if(argLen > 1)
        {
            // The argument value follows (i.e.  -Uusername)
            _propValue = arg.substring(1);
        }
        else
        {
            if( pos == argc || argv[pos+1].regionMatches(0, "-", 0, 1) )
            {
                // We are either at the last argument or the next option
                // starts with '-'.
                _propValue= null;
            }
            else
            {
                // The argument value is the next argument (i.e.  -U username)
                _propValue = argv[pos+1];
                incrementValue= 1;
            }
        }

        return(incrementValue);
    }
    // END -- parseArguments()

}
