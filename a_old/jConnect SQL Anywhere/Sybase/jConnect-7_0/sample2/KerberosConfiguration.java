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

import java.util.Hashtable;

import javax.security.auth.login.Configuration;
import javax.security.auth.login.AppConfigurationEntry;

/**
 * This class illustrates how to programatically set the values that would
 * otherwise be set in a JAAS login configuration file
 */

public class KerberosConfiguration extends Configuration
{

    static AppConfigurationEntry []ace;

    static
    {
        Hashtable map = new Hashtable();
       
        // By setting useTicketCache to true, we are telling Java to try and
        // locate a Keberos location in a well-defined location (for example,
        // in the /tmp/krb5cc_{userId} file on a Solaris machine or in the
        // in-memory cache on a Windows 2000 client machine which has 
        // authenticated to Active Directory Server).
        map.put("useTicketCache", "true");
        AppConfigurationEntry ac = new AppConfigurationEntry(
            "com.sun.security.auth.module.Krb5LoginModule",
            AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
            map);
        ace = new AppConfigurationEntry[1];
        ace[0] = ac;
    }

    public KerberosConfiguration()
    {
        super();
    }

    public AppConfigurationEntry[] getAppConfigurationEntry (String
        applicationName)
    {

        return ace;
    }

    public void refresh()
    {
    }

    static public void setConfiguration() 
    {
        Configuration.setConfiguration(new KerberosConfiguration());
    }
}
