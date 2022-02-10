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
 * DBMetadata class demonstrates how to use  DatabaseMetaData methods.
 * By default output for all of the DatabaseMetaData methods is displayed.
 * The -M option may be used to specify a specific method.<p>
 *
 *
 * DBMetadata may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 * -M DatabaseMetaDataMethod
 *
 *  @see Sample
 */
public class DBMetadata extends Sample
{

    static String _extraCmdOption = "-M";
    static String _dbMethod = "";
    static boolean _displayAMethod = false;

    // All of the DatabaseMetaData methods
    static String _methods [] =
    {
        "allProceduresAreCallable", 
            "allTablesAreSelectable", 
            "getURL", 
            "getUserName", 
            "getDriverVersion", 
            "supportsColumnAliasing", 
            "supportsTableCorrelationNames",
            "isReadOnly"            , 
            "nullsAreSortedHigh",
            "nullsAreSortedLow"     ,
            "nullsAreSortedAtStart",
            "nullsAreSortedAtEnd"   , 
            "getDatabaseProductName",
            "getDatabaseProductVersion", 
            "getDriverName",
            "getDriverVersion",
            "getDriverMajorVersion",
            "getDriverMinorVersion", 
            "useLocalFiles",
            "useLocalFilesPerTable", 
            "supportsMixedCaseIdentifiers",
            "storesUpperCaseIdentifiers", 
            "storesLowerCaseIdentifiers",
            "storesMixedCaseIdentifiers", 
            "supportsMixedCaseQuotedIdentifiers",
            "storesUpperCaseQuotedIdentifiers", 
            "storesLowerCaseQuotedIdentifiers",
            "storesMixedCaseQuotedIdentifiers",
            "getIdentifierQuoteString", 
            "getSQLKeywords",
            "getNumericFunctions", 
            "getStringFunctions",
            "getSystemFunctions", 
            "getTimeDateFunctions",
            "getSearchStringEscape", 
            "getExtraNameCharacters",
            "supportsAlterTableWithAddColumn",
            "supportsAlterTableWithDropColumn",
            "supportsColumnAliasing",
            "nullPlusNonNullIsNull",
            "supportsConvert", 
            "supportsTableCorrelationNames",
            "supportsDifferentTableCorrelationNames", 
            "supportsExpressionsInOrderBy",
            "supportsOrderByUnrelated",
            "supportsGroupBy",
            "supportsGroupByUnrelated", 
            "supportsGroupByBeyondSelect",
            "supportsLikeEscapeClause", 
            "supportsMultipleResultSets",
            "supportsMultipleTransactions", 
            "supportsNonNullableColumns",
            "supportsMinimumSQLGrammar ", 
            "supportsExtendedSQLGrammer",
            "supportsANSI92EntryLevelSQL",
            "supportsANSI92IntermediateSQL",
            "supportsANSI92FullSQL", 
            "supportsIntegrityEnhancementFacility",
            "supportsOuterJoins", 
            "supportsFullOuterJoins",
            "supportsLimitedOuterJoins", 
            "getSchemaTerm",
            "getProcedureTerm", 
            "getCatalogTerm", 
            "isCatalogAtStart",
            "getCatalogSeparator", 
            "supportsSchemasInDataManipulation ",
            "supportsSchemasInProcedureCalls ", 
            "supportsSchemasInTableDefinitions ",
            "supportsSchemasInIndexDefinitions",
            "supportsSchemasInPrivilegeDefinitions",
            "supportsCatalogsInDataManipulation", 
            "supportsCatalogsInProcedureCalls",
            "supportsCatalogsInTableDefinitions", 
            "supportsCatalogsInIndexDefinitions",
            "supportsCatalogsInPrivilegeDefinitions", 
            "supportsPositionedDelete",
            "supportsPositionedUpdate",
            "supportsSelectForUpdate",
            "supportsStoredProcedures",
            "supportsSubqueriesInComparisons",
            "supportsSubqueriesInExists",
            "supportsSubqueriesInIns",
            "supportsSubqueriesInQuantifieds",
            "supportsCorrelatedSubqueries", 
            "supportsUnion",
            "supportsUnionAll",
            "supportsOpenCursorsAcrossCommit",
            "supportsOpenCursorsAcrossRollback",
            "supportsOpenStatementsAcrossCommit",
            "supportsOpenStatementsAcrossRollback",
            "getMaxBinaryLiteralLength",
            "getMaxCharLiteralLength",
            "getMaxColumnNameLength",
            "getMaxColumnsInGroupBy",
            "getMaxColumnsInIndex",
            "getMaxColumnsInOrderBy",
            "getMaxColumnsInSelect",
            "getMaxColumnsInTable",
            "getMaxConnections",
            "getMaxCursorNameLength",
            "getMaxIndexLength",
            "getMaxSchemaNameLength",
            "getMaxProcedureNameLength",
            "getMaxCatalogNameLength",
            "getMaxRowSize",
            "doesMaxRowSizeIncludeBlobs",
            "getMaxStatementLength",
            "getMaxStatements",
            "getMaxTableNameLength",
            "getMaxTablesInSelect",
            "getMaxUserNameLength",
            "getDefaultTransactionIsolation",
            "supportsTransactions",
            "supportsTransactionIsolationLevel",
            " supportsDataDefinitionAndDataManipulationTransactions ",
            "supportsDataManipulationTransactionsOnly ",
            "dataDefinitionCausesTransactionCommit",
            "dataDefinitionIgnoredInTransactions ",
            "getProcedures",
            "getProcedureColumns", 
            "getTables", 
            "getSchemas",
            "getCatalogs ",
            "getTableTypes",
            "getColumns",
            "getColumnPrivileges",
            "getTablePrivileges",
            "getBestRowIdentifier",
            "getVersionColumns",
            "getPrimaryKeys",
            "getImportedKeys",
            "getExportedKeys",
            "getCrossReference",
            "getTypeInfo",
            "getIndexInfo",
            "supportsResultSetType",
            "ownUpdatesAreVisible",
            "ownDeletesAreVisible",
            "ownInsertsAreVisible",
            "othersUpdatesAreVisible",
            "othersDeletesAreVisible",
            "othersInsertsAreVisible",
            "updatesAreDetected",
            "deletesAreDetected",
            "insertsAreDetected",
            "supportsResultSetConcurrency",
            "getUDTs",
            "supportsBatchUpdates",
            "getConnection"
    }
    ; 


    DBMetadata()
    {
        super();
    }
    /**
     * Location where you can add commandline properties to the connection
     * The Super class will call this function before creating the
     * connection
     * @param  cmdLine    CommandLine settings
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
                _dbMethod = value;
                _displayAMethod = true;
                break;
            }
        }
    }

    public void sampleCode()
    {

        try
        {

            DatabaseMetaData dma = _con.getMetaData ();
            String createTab1 = 
                "CREATE TABLE tab1 ( id1 INTEGER PRIMARY KEY, name VARCHAR(10))";
            String createTab2 = 
                "CREATE TABLE tab2 ( id2 INTEGER PRIMARY KEY, name VARCHAR(10))";
            String createFKey =
                "ALTER TABLE tab1 ADD CONSTRAINT fkeytab FOREIGN KEY(id1) " +
                " REFERENCES tab2 (id2)";
            String dropFKey =
                "ALTER TABLE tab1 drop CONSTRAINT fkeytab";
            String dropTab1 =
                "drop table tab1";
            String dropTab2 =
                "drop table tab2";

            // the Primary and Foreign Keys needed to test getImportedKeys()
            // and getPrimarykeys().  This is done in tempdb as pubs2 is read only
            execDDL("use tempdb");
            execDDL(createTab1);
            execDDL(createTab2);
            execDDL(createFKey);
            // back to pubs2
            execDDL("use pubs2");

            for(int offset= 0; offset < _methods.length; offset++)
            {
                if(_displayAMethod)
                {
                    // Only display a specific method
                    if(_dbMethod.equals(_methods[offset]))
                    {
                        displayAMethod(dma, offset);
                        break;
                    }

                }
                else
                {
                    // Displaying all Methods
                    displayAMethod(dma, offset);
                }
            }

            //Drop the Foreign Key. Must go back to tempdb
            execDDL("use tempdb");
            execDDL(dropFKey);
            execDDL(dropTab1);
            execDDL(dropTab2);
        }
        catch (SQLException ex)
        {
            displaySQLEx(ex);
        }
        catch (java.lang.Exception ex)
        {

            // Got some other type of exception.  Dump it.
            ex.printStackTrace ();
        }

    }

    /**
     * Display the output from all of the DatabaseMetaData methods
     * This method will also  display which methods are not currently supported
     * @param dma    DatabaseMetaData object
     * @param offset Offset in _methods to display
     * @exception  SQLException .
     */

    public void  displayAMethod( DatabaseMetaData dma, int offset)
        throws SQLException
    {

        ResultSet  rs = null;


        error("method= "+_methods[offset]+", offset= " + 
            offset +"\n");
        try
        {
            switch(offset)
            {
                case 0:
                    output("allProceduresAreCallable()= " +
                        dma.allProceduresAreCallable() + "\n");
                    break;

                case 1:
                    output("allTablesAreSelectable()= " +
                        dma.allTablesAreSelectable() + "\n");
                    break;
                case 2:
                    output("getURL()= " + dma.getURL() + "\n");
                    break;
                case 3:
                    output("getUserName()= " + dma.getUserName() + "\n");
                    break;
                case 4:
                    output("getDriverVersion()= " +
                        dma.getDriverVersion() + "\n");
                    break;
                case 5:
                    output("supportsColumnAliasing()= " +
                        dma.supportsColumnAliasing() + "\n");
                    break;
                case 6:
                    output("supportsTableCorrelationNames()= " +
                        dma.supportsTableCorrelationNames() + "\n");
                    break;
                case 7:
                    output("isReadOnly()= " + dma.isReadOnly() + "\n");
                    break;
                case 8:
                    output("nullsAreSortedHigh()= " + 
                        dma.nullsAreSortedHigh() + "\n");
                    break;
                case 9:
                    output("nullsAreSortedLow()= " + 
                        dma.nullsAreSortedLow() + "\n");
                    break;
                case 10:
                    output("nullsAreSortedAtStart()= " + 
                        dma.nullsAreSortedAtStart() + "\n");
                    break;
                case 11:
                    output("nullsAreSortedAtEnd()= " + 
                        dma.nullsAreSortedAtEnd() + "\n");
                    break;
                case 12:
                    output("getDatabaseProductName()= " + 
                        dma.getDatabaseProductName() + "\n");
                    break;
                case 13:
                    output("getDatabaseProductVersion()= " + 
                        dma.getDatabaseProductVersion() + "\n");
                    break;
                case 14:
                    output("getDriverName()= " + 
                        dma.getDriverName() + "\n");
                    break;
                case 15:
                    output("getDriverVersion()= " + 
                        dma.getDriverVersion() + "\n");
                    break;
                case 16:
                    output("getDriverMajorVersion()= " + 
                        dma.getDriverMajorVersion() + "\n");
                    break;
                case 17:
                    output("getDriverMinorVersion()= " + 
                        dma.getDriverMinorVersion() + "\n");
                    break;
                case 18:
                    output("usesLocalFiles()= " + 
                        dma.usesLocalFiles() + "\n");
                    break;
                case 19:
                    output("usesLocalFilePerTable()= " + 
                        dma.usesLocalFilePerTable() + "\n");
                    break;
                case 20:
                    output("supportsMixedCaseIdentifiers()= " + 
                        dma.supportsMixedCaseIdentifiers() + "\n");
                    break;
                case 21:
                    output("storesUpperCaseIdentifiers()= " + 
                        dma.storesUpperCaseIdentifiers() + "\n");
                    break;
                case 22:
                    output("storesLowerCaseIdentifiers()= " + 
                        dma.storesLowerCaseIdentifiers() + "\n");
                    break;
                case 23:
                    output("storesMixedCaseIdentifiers()= " + 
                        dma.storesMixedCaseIdentifiers() + "\n");
                    break;
                case 24:
                    output("supportssMixedCaseQuotedIdentifiers()= " + 
                        dma.supportsMixedCaseQuotedIdentifiers() + "\n");
                    break;
                case 25:
                    output("storesUpperCaseQuotedIdentifiers()= " + 
                        dma.storesUpperCaseQuotedIdentifiers() + "\n");
                    break;
                case 26:
                    output("storesLowerCaseQuotedIdentifiers()= " + 
                        dma.storesLowerCaseQuotedIdentifiers() + "\n");
                    break;
                case 27:
                    output("storesMixedCaseQuotedIdentifiers()= " + 
                        dma.storesMixedCaseQuotedIdentifiers() + "\n");
                    break;
                case 28:
                    output("getIdentifierQuoteString ()= " + 
                        dma.getIdentifierQuoteString () + "\n");
                    break;
                case 29:
                    output("getSQLKeywords ()= " + 
                        dma.getSQLKeywords () + "\n");
                    break;
                case 30:
                    output("getNumericFunctions ()= " + 
                        dma.getNumericFunctions () + "\n");
                    break;
                case 31:
                    output("getStringFunctions ()= " + 
                        dma.getStringFunctions () + "\n");
                    break;
                case 32:
                    output("getSystemFunctions ()= " + 
                        dma.getSystemFunctions () + "\n");
                    break;
                case 33:
                    output("getTimeDateFunctions ()= " + 
                        dma.getTimeDateFunctions () + "\n");
                    break;
                case 34:
                    output("getSearchStringEscape ()= " + 
                        dma.getSearchStringEscape () + "\n");
                    break;
                case 35:
                    output("getExtraNameCharacters ()= " + 
                        dma.getExtraNameCharacters () + "\n");
                    break;
                case 36:
                    output("supportsAlterTableWithAddColumn ()= " + 
                        dma.supportsAlterTableWithAddColumn () + "\n");
                    break;
                case 37:
                    output("supportsAlterTableWithDropColumn()= " + 
                        dma.supportsAlterTableWithDropColumn () + "\n");
                    break;
                case 38:
                    output("supportsColumnAliasing()= " + 
                        dma.supportsColumnAliasing () + "\n");
                    break;
                case 39:
                    output("nullPlusNonNullIsNull()= " + 
                        dma.nullPlusNonNullIsNull () + "\n");
                    break;
                case 40:
                    output("supportsConvert ()= " + 
                        dma.supportsConvert  () + "\n");
                    break;
                case 41:
                    output("supportsTableCorrelationNames ()= " + 
                        dma.supportsTableCorrelationNames() + "\n");
                    break;
                case 42:
                    output("supportsDifferentTableCorrelationNames ()= " + 
                        dma.supportsDifferentTableCorrelationNames() + "\n");
                    break;
                case 43:
                    output("supportsExpressionsInOrderBy  ()= " + 
                        dma.supportsExpressionsInOrderBy () + "\n");
                    break;
                case 44:
                    output("supportsOrderByUnrelated  ()= " + 
                        dma.supportsOrderByUnrelated () + "\n");
                    break;
                case 45:
                    output("supportsGroupBy  ()= " + 
                        dma.supportsGroupBy () + "\n");
                    break;
                case 46:
                    output("supportsGroupByUnrelated  ()= " + 
                        dma.supportsGroupByUnrelated () + "\n");
                    break;
                case 47:
                    output("supportsGroupByBeyondSelect   ()= " + 
                        dma.supportsGroupByBeyondSelect  () + "\n");
                    break;
                case 48:
                    output("supportsLikeEscapeClause ()= " + 
                        dma.supportsLikeEscapeClause  () + "\n");
                    break;
                case 49:
                    output("supportsMultipleResultSets ()= " + 
                        dma.supportsMultipleResultSets  () + "\n");
                    break;
                case 50:
                    output("supportsMultipleTransactions ()= " + 
                        dma.supportsMultipleTransactions  () + "\n");
                    break;
                case 51:
                    output("supportsNonNullableColumns ()= " + 
                        dma.supportsNonNullableColumns  () + "\n");
                    break;
                case 52:
                    output("supportsMinimumSQLGrammar  ()= " + 
                        dma.supportsMinimumSQLGrammar   () + "\n");
                    break;
                case 53:
                    output("supportsExtendedSQLGrammar  ()= " + 
                        dma.supportsExtendedSQLGrammar   () + "\n");
                    break;
                case 54:
                    output("supportsANSI92EntryLevelSQL()= " + 
                        dma.supportsANSI92EntryLevelSQL() + "\n");
                    break;
                case 55:
                    output("supportsANSI92IntermediateSQL()= " + 
                        dma.supportsANSI92IntermediateSQL() + "\n");
                    break;
                case 56:
                    output("supportsANSI92FullSQL()= " + 
                        dma.supportsANSI92FullSQL() + "\n");
                    break;
                case 57:
                    output("supportsIntegrityEnhancementFacility ()= " + 
                        dma.supportsIntegrityEnhancementFacility () + "\n");
                    break;
                case 58:
                    output("supportsOuterJoins  ()= " + 
                        dma.supportsOuterJoins () + "\n");
                    break;
                case 59:
                    output("supportsFullOuterJoins  ()= " + 
                        dma.supportsFullOuterJoins () + "\n");
                    break;
                case 60:
                    output("supportsLimitedOuterJoins()= " + 
                        dma.supportsLimitedOuterJoins() + "\n");
                    break;
                case 61:
                    output("getSchemaTerm()= " + 
                        dma.getSchemaTerm() + "\n");
                    break;
                case 62:
                    output("getProcedureTerm()= " + 
                        dma.getProcedureTerm() + "\n");
                    break;
                case 63:
                    output("getCatalogTerm()= " + 
                        dma.getCatalogTerm() + "\n");
                    break;
                case 64:
                    output("isCatalogAtStart ()= " + 
                        dma.isCatalogAtStart () + "\n");
                    break;
                case 65:
                    output("getCatalogSeparator ()= " + 
                        dma.getCatalogSeparator () + "\n");
                    break;
                case 66:
                    output("supportsSchemasInDataManipulation()= " + 
                        dma.supportsSchemasInDataManipulation() + "\n");
                    break;
                case 67:
                    output("supportsSchemasInProcedureCalls()= " + 
                        dma.supportsSchemasInProcedureCalls() + "\n");
                    break;
                case 68:
                    output("supportsSchemasInTableDefinitions()= " + 
                        dma.supportsSchemasInTableDefinitions() + "\n");
                    break;
                case 69:
                    output("supportsSchemasInIndexDefinitions()= " + 
                        dma.supportsSchemasInIndexDefinitions() + "\n");
                    break;
                case 70:
                    output("supportsSchemasInPrivilegeDefinitions()= " + 
                        dma.supportsSchemasInPrivilegeDefinitions() + "\n");
                    break;
                case 71:
                    output("supportsCatalogsInDataManipulation()= " + 
                        dma.supportsCatalogsInDataManipulation() + "\n");
                    break;
                case 72:
                    output(" supportsCatalogsInProcedureCalls ()= " + 
                        dma.supportsCatalogsInProcedureCalls() + "\n");
                    break;
                case 73:
                    output("supportsCatalogsInTableDefinitions()= " + 
                        dma.supportsCatalogsInTableDefinitions() + "\n");
                    break;
                case 74:
                    output("supportsCatalogsInIndexDefinitions()= " + 
                        dma.supportsCatalogsInIndexDefinitions() + "\n");
                    break;
                case 75:
                    output(" supportsCatalogsInPrivilegeDefinitions()= " + 
                        dma.supportsCatalogsInPrivilegeDefinitions() + "\n");
                    break;
                case 76:
                    output("supportsPositionedDelete()= " + 
                        dma.supportsPositionedDelete() + "\n");
                    break;
                case 77:
                    output("supportsPositionedUpdate()= " + 
                        dma.supportsPositionedUpdate() + "\n");
                    break;
                case 78:
                    output("supportsSelectForUpdate()= " + 
                        dma.supportsSelectForUpdate() + "\n");
                    break;
                case 79:
                    output("supportsStoredProcedures()= " + 
                        dma.supportsStoredProcedures() + "\n");
                    break;
                case 80:
                    output("supportsSubqueriesInComparisons()= " + 
                        dma.supportsSubqueriesInComparisons() + "\n");
                    break;
                case 81:
                    output("supportsSubqueriesInExists()= " + 
                        dma.supportsSubqueriesInExists() + "\n");
                    break;
                case 82:
                    output("supportsSubqueriesInIns()= " + 
                        dma.supportsSubqueriesInIns() + "\n");
                    break;
                case 83:
                    output("supportsSubqueriesInQuantifieds()= " + 
                        dma.supportsSubqueriesInQuantifieds() + "\n");
                    break;
                case 84:
                    output("supportsCorrelatedSubqueries()= " + 
                        dma.supportsCorrelatedSubqueries() + "\n");
                    break;
                case 85:
                    output("supportsUnion()= " + 
                        dma.supportsUnion() + "\n");
                    break;
                case 86:
                    output("supportsUnionAll()= " + 
                        dma.supportsUnionAll() + "\n");
                    break;
                case 87:
                    output("supportsOpenCursorsAcrossCommit()= " + 
                        dma.supportsOpenCursorsAcrossCommit() + "\n");
                    break;
                case 88:
                    output("supportsOpenCursorsAcrossRollback()= " + 
                        dma.supportsOpenCursorsAcrossRollback() + "\n");
                    break;
                case 89:
                    output("supportsOpenStatementsAcrossCommit()= " + 
                        dma.supportsOpenStatementsAcrossCommit() + "\n");
                    break;
                case 90:
                    output("supportsOpenStatementsAcrossRollback()= " + 
                        dma.supportsOpenStatementsAcrossRollback() + "\n");
                    break;
                case 91:
                    output("getMaxBinaryLiteralLength()= " + 
                        dma.getMaxBinaryLiteralLength() + "\n");
                    break;
                case 92:
                    output("getMaxCharLiteralLength()= " + 
                        dma.getMaxCharLiteralLength() + "\n");
                    break;
                case 93:
                    output("getMaxColumnNameLength()= " + 
                        dma.getMaxColumnNameLength() + "\n");
                    break;
                case 94:
                    output("getMaxColumnsInGroupBy()= " + 
                        dma.getMaxColumnsInGroupBy() + "\n");
                    break;
                case 95:
                    output("getMaxColumnsInIndex ()= " + 
                        dma.getMaxColumnsInIndex () + "\n");
                    break;
                case 96:
                    output("getMaxColumnsInOrderBy()= " + 
                        dma.getMaxColumnsInOrderBy () + "\n");
                    break;
                case 97:
                    output("getMaxColumnsInSelect()= " + 
                        dma.getMaxColumnsInSelect() + "\n");
                    break;
                case 98:
                    output("getMaxColumnsInTable()= " + 
                        dma.getMaxColumnsInTable() + "\n");
                case 99:
                    output("getMaxConnections()= " + 
                        dma.getMaxConnections () + "\n");
                    break;
                case 100:
                    output("getMaxCursorNameLength()= " + 
                        dma.getMaxCursorNameLength () + "\n");
                    break;
                case 101:
                    output("getMaxIndexLength ()= " + 
                        dma.getMaxIndexLength() + "\n");
                    break;
                case 102:
                    output("getMaxSchemaNameLength  ()= " + 
                        dma.getMaxSchemaNameLength () + "\n");
                    break;
                case 103:
                    output("getMaxProcedureNameLength()= " + 
                        dma.getMaxProcedureNameLength() + "\n");
                    break;
                case 104:
                    output("getMaxCatalogNameLength()= " + 
                        dma.getMaxCatalogNameLength() + "\n");
                    break;
                case 105:
                    output("getMaxRowSize()= " + 
                        dma.getMaxRowSize() + "\n");
                    break;
                case 106:
                    output("doesMaxRowSizeIncludeBlobs ()= " + 
                        dma.doesMaxRowSizeIncludeBlobs () + "\n");
                    break;
                case 107:
                    output("getMaxStatementLength ()= " + 
                        dma.getMaxStatementLength () + "\n");
                    break;
                case 108:
                    output("getMaxStatements()= " + 
                        dma.getMaxStatements() + "\n");
                    break;
                case 109:
                    output("getMaxTableNameLength ()= " + 
                        dma.getMaxTableNameLength () + "\n");
                    break;
                case 110:
                    output(" getMaxTablesInSelect()= " + 
                        dma.getMaxTablesInSelect() + "\n");
                    break;
                case 111:
                    output("getMaxUserNameLength()= " + 
                        dma.getMaxUserNameLength () + "\n");
                    break;
                case 112:
                    output("getDefaultTransactionIsolation()= " + 
                        dma.getDefaultTransactionIsolation () + "\n");
                    break;
                case 113:
                    output("supportsTransactions()= " + 
                        dma.supportsTransactions() + "\n");
                    break;
                case 114:
                    output(
                        "supportsTransactionIsolationLevel()= " + 
                        dma.supportsTransactionIsolationLevel(
                        Connection.TRANSACTION_READ_COMMITTED));
                    break;
                case 115:
                    output(
                        "supportsDataDefinitionAndDataManipulationTransactions ()= "
                        + 
                        dma.supportsDataDefinitionAndDataManipulationTransactions()
                        + "\n");
                    break;
                case 116:
                    output(
                        "supportsDataManipulationTransactionsOnly()= " + 
                        dma.supportsDataManipulationTransactionsOnly() + "\n");
                    break;
                case 117:
                    output("dataDefinitionCausesTransactionCommit()= " + 
                        dma.dataDefinitionCausesTransactionCommit() + "\n");
                    break;
                case 118:
                    output("dataDefinitionIgnoredInTransactions()= " + 
                        dma.dataDefinitionIgnoredInTransactions() + "\n");
                    break;
                case 119:
                    rs = dma.getProcedures(null,null,"storeid_proc");
                    output("getProcedures()=\n");
                    dispResultSet(rs);
                    rs.close();
                    break;
                case 120:
                    rs = dma.getProcedureColumns("","", "storeid_proc","stor_id");
                    output("getProcedureColumns()=\n ");
                    dispResultSet(rs);
                    rs.close();
                    break;
                case 121:
                    rs = dma.getTables(null,null,null,null);
                    output("getTables()=\n");
                    dispResultSet(rs);
                    rs.close();
                    break;
                case 122:
                    rs = dma.getSchemas();
                    output("getSchemas()=\n");
                    dispResultSet(rs);
                    rs.close();
                    break;
                case 123:
                    rs = dma.getCatalogs();
                    output("getCatalogs()=\n");
                    dispResultSet(rs);
                    rs.close();
                    break;
                case 124:
                    rs = dma.getTableTypes();
                    output("getTableTypes()=\n");
                    dispResultSet(rs);
                    rs.close();
                    break;
                case 125:
                    rs = dma.getColumns (null,null, "titles", "%");
                    output("getColumns ()=\n");
                    dispResultSet(rs);
                    rs.close();
                    break;
                case 126:
                    rs = dma.getColumnPrivileges (null,null, 
                        "titles", "title_id");
                    output("getColumnPrivileges ()=\n");
                    dispResultSet(rs);
                    rs.close();
                    break;
                case 127:
                    rs = dma.getTablePrivileges (null,null, 
                        "titles");
                    output("getTablePrivileges ()= \n");
                    dispResultSet(rs);
                    rs.close();
                    break;
                case 128:
                    rs = dma.getBestRowIdentifier (null,null, 
                        "titles",1,true);
                    output("getBestRowIdentifier ()=\n");
                    dispResultSet(rs);
                    rs.close();
                    break;
                case 129:
                    rs = dma.getVersionColumns (null,null, 
                        "titles");
                    output("getVersionColumns ()=\n");
                    dispResultSet(rs);
                    rs.close();
                    break;
                case 130:

                    //need to do this in tempdb as pubs2 is read only
                    execDDL("use tempdb");
                    rs = dma.getPrimaryKeys (null, null, 
                        "tab1");
                    output("getPrimaryKeys()=i\n");
                    dispResultSet(rs);
                    rs.close();
                    execDDL("use pubs2");
                    break;
                case 131:

                    //need to do this in tempdb as pubs2 is read only
                    execDDL("use tempdb");
                    output("getImportedKeys()=\n");
                    rs = dma.getImportedKeys (null,null,
                        "tab1");
                    dispResultSet(rs);
                    rs.close();
                    execDDL("use pubs2");
                    break;
                case 132:
                    rs = dma.getExportedKeys(null,null,"titles");
                    output("getExportedKeys()=\n");
                    dispResultSet(rs);
                    rs.close();
                    break;
                case 133:

                    //need to do this in tempdb as pubs2 is read only
                    execDDL("use tempdb");
                    rs = dma.getCrossReference(null,null, 
                        "tab1", null, null, "tab2");
                    output("getCrossReference()=\n");
                    dispResultSet(rs);
                    rs.close();
                    execDDL("use pubs2");
                    break;
                case 134:
                    rs = dma.getTypeInfo();
                    output("getTypeInfo()=\n");
                    dispResultSet(rs);
                    rs.close();
                    break;
                case 135:
                    rs = dma.getIndexInfo(null,null, 
                        "titles",false, false); 
                    output("getIndexInfo()=\n ");
                    dispResultSet(rs);
                    rs.close();
                    break;
                case 136:
                    output("supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY)= " +
                        dma.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY) + "\n");
                    output("supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE)=\n " +
                        dma.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE));
                    output("supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE)=\n " +
                        dma.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE));
                    break;
                case 137:
                    output("ownUpdatesAreVisible(ResultSet.TYPE_FORWARD_ONLY)= " +
                        dma.ownUpdatesAreVisible(ResultSet.TYPE_FORWARD_ONLY) + "\n" );
                    output("ownUpdatesAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE)= " +
                        dma.ownUpdatesAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE) + "\n" );
                    output("ownUpdatesAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE)= " +
                        dma.ownUpdatesAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE) + "\n" );
                    break;
                case 138:
                    output("ownDeletesAreVisible(ResultSet.TYPE_FORWARD_ONLY)= " +
                        dma.ownDeletesAreVisible(ResultSet.TYPE_FORWARD_ONLY) + "\n" );
                    output("ownDeletesAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE)= " +
                        dma.ownDeletesAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE) + "\n" );
                    output("ownDeletesAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE)= " +
                        dma.ownDeletesAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE) + "\n" );
                    break;
                case 139:
                    output("ownInsertsAreVisible(ResultSet.TYPE_FORWARD_ONLY)= " +
                        dma.ownInsertsAreVisible(ResultSet.TYPE_FORWARD_ONLY) + "\n" );
                    output("ownInsertsAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE)= " +
                        dma.ownInsertsAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE) + "\n" );
                    output("ownInsertsAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE)= " +
                        dma.ownInsertsAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE) + "\n" );
                    break;
                case 140:
                    output("othersUpdatesAreVisible(ResultSet.TYPE_FORWARD_ONLY)= " +
                        dma.othersUpdatesAreVisible(ResultSet.TYPE_FORWARD_ONLY) + "\n" );
                    output("othersUpdatesAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE)= " +
                        dma.othersUpdatesAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE) + "\n" );
                    output("othersUpdatesAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE)= " +
                        dma.othersUpdatesAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE) + "\n" );
                    break;
                case 141:
                    output("othersDeletesAreVisible(ResultSet.TYPE_FORWARD_ONLY)= " +
                        dma.othersDeletesAreVisible(ResultSet.TYPE_FORWARD_ONLY) + "\n" );
                    output("othersDeletesAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE)= " +
                        dma.othersDeletesAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE) + "\n" );
                    output("othersDeletesAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE)= " +
                        dma.othersDeletesAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE) + "\n" );
                    break;
                case 142:
                    output("othersInsertsAreVisible(ResultSet.TYPE_FORWARD_ONLY)= " +
                        dma.othersInsertsAreVisible(ResultSet.TYPE_FORWARD_ONLY) + "\n" );
                    output("othersInsertsAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE)= " +
                        dma.othersInsertsAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE) + "\n" );
                    output("othersInsertsAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE)= " +
                        dma.othersInsertsAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE) + "\n" );
                    break;
                case 143:
                    output("updatesAreDetected(ResultSet.TYPE_FORWARD_ONLY)= " +
                        dma.updatesAreDetected(ResultSet.TYPE_FORWARD_ONLY) + "\n" );
                    output("updatesAreDetected(ResultSet.TYPE_SCROLL_INSENSITIVE)= " +
                        dma.updatesAreDetected(ResultSet.TYPE_SCROLL_INSENSITIVE) + "\n" );
                    output("updatesAreDetected(ResultSet.TYPE_SCROLL_SENSITIVE)= " +
                        dma.updatesAreDetected(ResultSet.TYPE_SCROLL_SENSITIVE) + "\n" );
                    break;
                case 144:
                    output("deletesAreDetected(ResultSet.TYPE_FORWARD_ONLY)= " +
                        dma.deletesAreDetected(ResultSet.TYPE_FORWARD_ONLY) + "\n" );
                    output("deletesAreDetected(ResultSet.TYPE_SCROLL_INSENSITIVE)= " +
                        dma.deletesAreDetected(ResultSet.TYPE_SCROLL_INSENSITIVE) + "\n" );
                    output("deletesAreDetected(ResultSet.TYPE_SCROLL_SENSITIVE)= " +
                        dma.deletesAreDetected(ResultSet.TYPE_SCROLL_SENSITIVE) + "\n" );
                    break;
                case 145:
                    output("insertsAreDetected(ResultSet.TYPE_FORWARD_ONLY)= " +
                        dma.insertsAreDetected(ResultSet.TYPE_FORWARD_ONLY) + "\n" );
                    output("insertsAreDetected(ResultSet.TYPE_SCROLL_INSENSITIVE)= " +
                        dma.insertsAreDetected(ResultSet.TYPE_SCROLL_INSENSITIVE) + "\n" );
                    output("insertsAreDetected(ResultSet.TYPE_SCROLL_SENSITIVE)= " +
                        dma.insertsAreDetected(ResultSet.TYPE_SCROLL_SENSITIVE) + "\n" );
                    break;
                case 146:
                    output("supportsResultSetConcurrency(" +
                        "ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)= " +
                        dma.supportsResultSetConcurrency(
                        ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY) + "\n" );
                    output("supportsResultSetConcurrency(" +
                        "ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)= " +
                        dma.supportsResultSetConcurrency(
                        ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE) + "\n" );
                    output("supportsResultSetConcurrency(" +
                        "ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)= " +
                        dma.supportsResultSetConcurrency(
                        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY) + "\n" );
                    output("supportsResultSetConcurrency(" +
                        "ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)= " +
                        dma.supportsResultSetConcurrency(
                        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE) + "\n" );
                    output("supportsResultSetConcurrency(" +
                        "ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)= " +
                        dma.supportsResultSetConcurrency(
                        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY) + "\n" );
                    output("supportsResultSetConcurrency(" +
                        "ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)= " +
                        dma.supportsResultSetConcurrency(
                        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE) + "\n" );
                    break;
                case 147:
                    // "getUDTs",
                    break;
                case 148:
                    output("supportsBatchUpdates()= " + 
                        dma.supportsBatchUpdates() + "\n");
                    break;
                case 149:
                    Connection mdaConnection = dma.getConnection();
                    String compStatus = null;
                    if (mdaConnection.equals(_con))
                    {
                        compStatus = "succeeded";
                    }
                    else
                    {
                        compStatus = "failed";
                    }
                    output("getConnection()= " + compStatus + "\n");
                    break;
            }
        }
        catch (SQLException sqe)
        {
            System.out.println("SQL State= " + sqe.getSQLState() );
            error("*** " + _methods[offset] +"() not suported ***\n");

        }


    }

}
