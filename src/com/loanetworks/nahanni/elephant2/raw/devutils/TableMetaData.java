package com.loanetworks.nahanni.elephant2.raw.devutils;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Manage the metadata that the {@link MetaDataParserUtils} discovers about a DB table.
* <p/>
* Copyright © 2007, 2008 Loa Corporation.
*/

public class TableMetaData {

    private ExpectedTableInfo _expectedTableInfo;
    private SortedMap<Integer,ColumnMetaData> _columnMetaData = new TreeMap<Integer, ColumnMetaData>();

    public TableMetaData( ExpectedTableInfo expectedTableInfo ) {
        super();

        _expectedTableInfo = expectedTableInfo;
    }

    public ColumnMetaData[] getColumnMetaDataArray() {
        ColumnMetaData[] array = new ColumnMetaData[ _columnMetaData.size() ];
        for ( int i = 1; i <= array.length; i += 1 ) {
            array[i - 1] = _columnMetaData.get( i );
        }
        return array;
    }

    public void addColumnMetaData( ColumnMetaData columnMetaData ) {
        _columnMetaData.put( _columnMetaData.size() + 1, columnMetaData );
    }

    public String getTableName() {
        return _expectedTableInfo.getExpectedTableName();
    }

    public String getSchemaName() {

        return _expectedTableInfo.getSchemaName();

    }

    public String getSchemaTableName() {

        if ( _expectedTableInfo.getSchemaName() == null ) {

            return getTableName();

        } else {

            return _expectedTableInfo.getSchemaName() + "." + getTableName();

        }

    }

    public ExpectedTableInfo getExpectedTableInfo() {
        return _expectedTableInfo;
    }

    public String toString() {
        return "TableMetaData( expected info = " + _expectedTableInfo + " )";
    }

}
