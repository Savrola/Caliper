package com.loanetworks.nahanni.elephant2.raw.devutils;

/**
 * Describe the tables that the {@link MetaDataParserUtils} should expect to find described by
 * the database that it gets pointed at.
* <p/>
* Copyright © 2007, 2008 Loa Corporation.
*/

public class ExpectedTableInfo {

    private final String _schemaName;
    private final String _expectedTableName;
    private final String _tupleCarrierName;
    private boolean _isDone = false;

    public ExpectedTableInfo( String schemaName, String expectedTableName, String tupleCarrierName ) {
        super();

        _schemaName = schemaName;
        _expectedTableName = expectedTableName;
        _tupleCarrierName = tupleCarrierName;
    }

    public ExpectedTableInfo( String expectedTableName, String tupleCarrierName ) {
        this( null, expectedTableName, tupleCarrierName );
    }

    public String getExpectedTableName() {
        return _expectedTableName;
    }

    public String getTupleCarrierName() {
        return _tupleCarrierName;
    }

    public boolean isDone() {
        return _isDone;
    }

    public void markDone() {
        _isDone = true;
    }

    public String toString() {
        return "ExpectedTableInfo( table name = " + _expectedTableName + ", carrier name = " + _tupleCarrierName + " )";
    }

    public String getSchemaName() {
        return _schemaName;
    }
}
