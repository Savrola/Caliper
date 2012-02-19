package com.loanetworks.nahanni.elephant2.raw.devutils;

/**
 * Describe a single column in a table.
* <p/>
* Copyright © 2012 Daniel Boulet.
*/

public class ColumnMetaData {

    private final String _dbColumnName;
    private final String _loaColumnName;
    private final String _dbType;
    private final LoaTypeInfo _loaType;
    private final int _columnSize;
    private final int _decimalDigits;
    private final int _nullable;

    public ColumnMetaData( String dbColumnName, String dbType, LoaTypeInfo loaType, int columnSize, int decimalDigits, int nullable ) {
        super();

        _dbColumnName = dbColumnName;
        _loaColumnName = DbClassGeneratorV2.convertToCamelHumps( dbColumnName );
        _dbType = dbType;
        if ( loaType == null ) {
            throw new IllegalArgumentException( "the loaType parameter is null" );
        }
        _loaType = loaType;
        _columnSize = columnSize;
        _decimalDigits = decimalDigits;
        _nullable = nullable;
    }

    public String getDbColumnName() {
        return _dbColumnName;
    }

    public String getLoaColumnName() {
        return _loaColumnName;
    }

    public String getDbType() {
        return _dbType;
    }

    public LoaTypeInfo getLoaTypeInfo() {
        return _loaType;
    }

    public int getColumnSize() {
        return _columnSize;
    }

    public int getDecimalDigits() {
        return _decimalDigits;
    }

    public int getNullable() {
        return _nullable;
    }

    public String toString() {
        return "ColumnMetaData( " +
               "dbColumnName = " + _dbColumnName + ", " +
               "loaColumnName = " + _loaColumnName + ", " +
               "dbType = " + _dbType + ", " +
               "loaType = " + _loaType + ", " +
               "columnSize = " + _columnSize + ", " +
               "decimalDigits = " + _decimalDigits + ", " +
               "nullable = " + _nullable +
                " )";
    }

}
