Sybase Rollup EBF 20686
=======================

--------------------------------------------------------------------------------

November 27, 2012

Sybase, an SAP Company
One Sybase Drive
Dublin, CA 94568
1-800-8-SYBASE (North America)
Web: http://www.sybase.com


Dear Customer:

Enclosed please find:

  EBF 20686

for the following platform:

  Not Platform Specific

This EBF is part of this release:

  jConnect 7.07 ESD #5

This EBF contains updates for the following product(s):

  jConnect for JDBC

We recommend that you install this EBF as soon as possible.

We appreciate your patience while we resolve problems in the product and your 
willingness to install this EBF. Please contact Technical Support if you
encounter any problems using this product and let them know that you are using
this EBF. 

This documentation includes these sections:

  * General Information and Technical Notes

  * Loading Instructions

  * New Fixes in this EBF

  * All Fixes Included in this EBF (EBF20686_Buglist.txt)

  * List of Files in this EBF (EBF20686_Filelist.txt)

Sincerely,

Sybase Engineering

--------------------------------------------------------------------------------


General Information and Technical Notes
---------------------------------------

This section contains general and technical information about this EBF.

* Updating the metadata stored procedures required by jConnect Driver
  on Adaptive Server Enterprise

  Certain new features and bug fixes in jConnect driver require you to 
  modify the metadata stored procedures in Adaptive Server. These stored
  procedures may not be up-to-date on your host ASE server due to various
  reasons.
  
  If the metadata stored procedures are outdated, you may not be able to use 
  some of the fixes that are implemented in this ESD, so you will need to 
  manually install the updated metadata stored procedures.

  To update the metadata stored procedures in Adaptive Server, first determine 
  the version of the meta-data scripts installed on the ASE. To do so, execute 
  the following command:

  sp_version
  go

  The version number of the jConnect meta-data script will be displayed.

  Review the version number column and compare it against the version of the  
  driver being used. 

  If the metadata scripts are outdated, the version installed will be older 
  than the driver build number or you will encounter one of the following:

  - stored procedure sp_version is not found
  - the stored procedure output does not print a row for the driver

  The updated scripts are included with the driver and can be installed as 
  follows:

  - Go to the "sp" directory under the jConnect installation directory. Based 
  on your host ASE server version, choose the appropriate SQL script. 
  - Use isql or another tool of your choice to execute the selected script. 
  This will install the current meta-data stored procedures.


Loading Instructions
--------------------

This section contains instructions for loading the new software onto your system.

NOTE: Before installing this EBF, back up your Sybase installation.

For installation instructions, see the "jConnect for JDBC 7.07 Installation 
Guide" documentation at http://sybooks.sybase.com/. Select "jConnect for JDBC" 
from the product list, then the language, and click Go.

NOTE: The path to the installer program has changed. It is now located under 
Disk1/InstData directory. So please add this directory ahead of the setup 
program name when invoking the installer. 


New Fixes in this EBF
---------------------

The following is a list of fixes which are specific to this EBF. The list is sorted
by the component in which the bug was fixed.

For a complete list of fixes in this EBF, see EBF20686_Buglist.txt.

   Component         BugID     Description
   ----------------  --------  ---------------------------------------------------
   JDBC Driver       714096    jConnect: Executing a batch update
                               PreparedStatement with DYNAMIC_PREPARE=false with
                               numeric values may cause numeric overflow, JZ0BE
                               exception.

   JDBC Driver       712354    jConnect: Request support for setClientInfo() and
                               getClientInfo()API for SybConnectionProxy class.

   JDBC Driver       712293    jConnect: Using jConnect 7.07 to bulk insert rows
                               in Adaptive Server versions earlier than 15.7
                               results in JZBK7 exception.

   JDBC Driver       711929    jConnect: Performance regressions observed in Bulk
                               Insert code path for jConnect 7.07 GA driver.

   JDBC Driver       711303    jConnect: Failed to insert into table with 663
                               columns using PreparedStatement.

   JDBC Driver       710528    jConnect: Using qualified table names like
                               dbo.table_name for Bulk Inserts raises exceptions.

   JDBC Driver       709763    jConnect: Bulk Insert writes wrong data value in
                               non-APL tables if last column in the table is a
                               variable length column.

   JDBC Driver       707235    jConnect: Clients redirected to another Cluster
                               Edition instance not establishing the connection
                               with server's default network packet size.

   JDBC Driver       705099    jConnect: Syntax error in metadata stored
                               procedures raises Msg 105, "Unclosed quote before
                               the character string..."

   JDBC Driver       701464    jConnect: Executing PreparedStatement with the
                               QUERY that contains more than 500 parameters fails
                               with: java.sql.SQLException: Message empty.

   JDBC Driver       699038    jConnect: Request to support timestamp data type
                               for Bulk Insert.

   JDBC Driver       675144    jConnect: Using large number of parameters in
                               PreparedStatement causes SQLExceptions like
                               "Message empty." and/or "The token datastream
                               length was not correct. This is an internal
                               protocol error."

