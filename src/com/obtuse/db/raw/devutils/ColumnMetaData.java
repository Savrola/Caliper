package com.obtuse.db.raw.devutils;

/**
 * Describe a single column in a table.
* <p/>
* Copyright © 2012 Daniel Boulet.
*/

public class ColumnMetaData {

    private final String _dbColumnName;
    private final String _savrolaColumnName;
    private final String _dbType;
    private final SavrolaTypeName _savrolaType;
    private final int _columnSize;
    private final int _decimalDigits;
    private final int _nullable;

    public ColumnMetaData( String dbColumnName, String dbType, SavrolaTypeName savrolaType, int columnSize, int decimalDigits, int nullable ) {
        super();

        _dbColumnName = dbColumnName;
        _savrolaColumnName = DbClassGeneratorV2.convertToCamelHumps( dbColumnName );
        _dbType = dbType;

        if ( savrolaType == null ) {

            throw new IllegalArgumentException( "the savrolaType parameter is null" );

        }

        _savrolaType = savrolaType;
        _columnSize = columnSize;
        _decimalDigits = decimalDigits;
        _nullable = nullable;

    }

    public String getDbColumnName() {

        return _dbColumnName;

    }

    public String getSavrolaColumnName() {

        return _savrolaColumnName;

    }

    public String getDbType() {

        return _dbType;

    }

    public SavrolaTypeName getSavrolaTypeInfo() {

        return _savrolaType;

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
               "loaColumnName = " + _savrolaColumnName + ", " +
               "dbType = " + _dbType + ", " +
               "loaType = " + _savrolaType + ", " +
               "columnSize = " + _columnSize + ", " +
               "decimalDigits = " + _decimalDigits + ", " +
               "nullable = " + _nullable +
                " )";

    }

}
