package com.loanetworks.nahanni.elephant2.raw.ti;

import com.loanetworks.nahanni.elephant2.raw.ElephantConnection;
import com.loanetworks.nahanni.elephant2.raw.dbdata.Tuple2;
import com.loanetworks.nahanni.elephant2.raw.exceptions.LoaNetworksElephantMoreThanOneFoundException;
import com.obtuse.util.exceptions.HowDidWeGetHereError;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Describe a database table.
 * <p/>
 * Copyright © 2012 Daniel Boulet.
 */

public abstract class TableInfo implements Serializable {

    private final String _schemaName;
    private final String _tableName;

    private SortedMap<Integer,String> _columnNames = new TreeMap<Integer, String>();
    private Map<String,Integer> _columnName2columnIndex = new HashMap<String, Integer>();
    private SortedMap<Integer,DBType> _columnTypes = new TreeMap<Integer, DBType>();
    private String[] _columnNamesArray;
    private String[] _sequenceNamesArray;
    private int[] _serialColumnNumbersArray;
    private DBType[] _columnTypesArray;
    private int _nextIx = 0;
    private boolean _frozen = false;

    private static final Map<String,Class<? extends TableInfo>> _knownTables = new HashMap<String, Class<? extends TableInfo>>();

    protected TableInfo() {
        super();

        _schemaName = null;
        _tableName = null;
    }

    protected TableInfo( String schemaName, String tableName ) {
        super();

        synchronized ( _knownTables ) {

            String ucTableName;
            if ( schemaName != null ) {

                ucTableName = schemaName.toUpperCase() + '.' + tableName.toUpperCase();

            } else {

                ucTableName = tableName.toUpperCase();

            }

            if ( _knownTables.containsKey( ucTableName ) ) {

                throw new HowDidWeGetHereError(
                        "table name \"" + ucTableName + "\" already defined by " + _knownTables.get( ucTableName )
                );

            }

            _knownTables.put( ucTableName, getClass() );

        }

        _schemaName = schemaName;
        _tableName = tableName;

    }

    public String getTableName() {
        return _tableName;
    }

    public String getSchemaName() {
        return _schemaName;
    }

    public String getSchemaTableName() {

        if ( _schemaName == null ) {

            return _tableName;

        } else {

            return _schemaName + '.' + _tableName;

        }

    }

    public String[] getSequenceNames() {
        return _sequenceNamesArray.clone();
    }

    public int[] getSerialColumnNumbers() {
        return _serialColumnNumbersArray.clone();
    }

    public String[] getColumnNames() {
        return _columnNamesArray.clone();
    }

    public DBType[] getColumnTypes() {
        return _columnTypesArray.clone();
    }

    private void dc( String columnName, DBType dbType ) {

        if ( _frozen ) {
            throw new HowDidWeGetHereError( "column info for \"" + getClass() + "\" already frozen" );
        }

        mustBeAbsent( columnName );
        _columnNames.put( _nextIx, columnName.toUpperCase() );
        _columnName2columnIndex.put( columnName.toUpperCase(), _nextIx );
        _columnTypes.put( _nextIx, dbType );

        _nextIx += 1;

    }

    protected void dcSerial( String columnName ) {

        dc( columnName, DBType.SERIAL );

    }

    protected void dcSerial8( String columnName ) {

        dc( columnName, DBType.SERIAL8 );

    }

    protected void dcInt( String columnName ) {

        dc( columnName, DBType.INT );

    }

    protected void dcDouble( String columnName ) {

        dc( columnName, DBType.DOUBLE );

    }

    protected void dcIntArray( String columnName ) {

        dc( columnName, DBType.INTARRAY );

    }

    protected void dcShort( String columnName ) {

        dc( columnName, DBType.SHORT );

    }

    protected void dcFloat( String columnName ) {

        dc( columnName, DBType.FLOAT );

    }

    protected void dcMoney( String columnName ) {

        dc( columnName, DBType.MONEY );
    }

    protected void dcLong( String columnName ) {

        dc( columnName, DBType.LONG );

    }

    protected void dcBoolean( String columnName ) {

        dc( columnName, DBType.BOOLEAN );

    }

    protected void dcForeign( String columnName ) {

        dc( columnName, DBType.FOREIGN );

    }

    protected void dcTimestampTZ( String columnName ) {

        dc( columnName, DBType.TIMESTAMPTZ );

    }

    protected void dcTimestamp( String columnName ) {

        dc( columnName, DBType.TIMESTAMP );

    }

    protected void dcDate( String columnName ) {

        dc( columnName, DBType.DATE );

    }

    protected void dcText( String columnName ) {

        dc( columnName, DBType.TEXT );

    }

    protected void dcTextArray( String columnName ) {

        dc( columnName, DBType.TEXTARRAY );

    }

    protected void dcInet( String columnName ) {

        dc( columnName, DBType.INET );
    }

    protected void dcMacaddr( String columnName ) {

        dc( columnName, DBType.MACADDR );
    }

    protected void dcBytes( String columnName ) {

        dc( columnName, DBType.BYTES );

    }

    public DBType getColumnType( String columnName ) {
        return _columnTypes.get( getColumnIndex( columnName ) );
    }

    public DBType getColumnType( int columnIndex ) {
        return _columnTypes.get( columnIndex );
    }

    public int getColumnIndex( String columnName ) {
        return _columnName2columnIndex.get( mustBePresent( columnName ) ).intValue();
    }

    protected void freeze() {
        if ( _frozen ) {
            throw new HowDidWeGetHereError( "table info for \"" + getSchemaTableName() + "\" already frozen" );
        }

        _columnNamesArray = new String[_nextIx];
        _columnTypesArray = new DBType[_nextIx];
        for ( int i = 0; i < _nextIx; i += 1 ) {
            _columnNamesArray[i] = _columnNames.get(i);
            _columnTypesArray[i] = _columnTypes.get(i);
        }

        int count = 0;

        for ( int i = 0; i < _columnTypes.size(); i += 1 ) {
            if ( _columnTypes.get(i) == DBType.SERIAL || _columnTypes.get(i) == DBType.SERIAL8 ) {
                count += 1;
            }
        }

        _sequenceNamesArray = new String[count];
        _serialColumnNumbersArray = new int[count];
        count = 0;

        for ( int i = 0; i < _columnTypes.size(); i += 1 ) {

            if ( _columnTypes.get(i) == DBType.SERIAL || _columnTypes.get(i) == DBType.SERIAL8 ) {
                _sequenceNamesArray[count] = _columnNamesArray[i];
                _sequenceNamesArray[count] = getSchemaTableName() + "_" + _columnNamesArray[i] + "_seq";
                _serialColumnNumbersArray[count] = i + 1;
                count += 1;
            }

        }

        _frozen = true;
    }

    public int getColumnCount() {
        if ( !_frozen ) {
            throw new HowDidWeGetHereError( "table info for \"" + getSchemaTableName() + "\" not yet frozen" );
        }

        return _nextIx;
    }

    public String getColumnName( int columnIx ) {
        return _columnNamesArray[columnIx];
    }

    private String mustBePresent( String columnName ) {

        if ( !_columnName2columnIndex.containsKey( columnName.toUpperCase() ) ) {
            throw new HowDidWeGetHereError( "column name \"" + columnName + "\" does not exist in \"" + getSchemaTableName() + "\" table" );
        }

        return columnName.toUpperCase();

    }

    private void mustBeAbsent( String columnName ) {

        if ( _columnName2columnIndex.containsKey( columnName.toUpperCase() ) ) {
            throw new HowDidWeGetHereError( "column name \"" + columnName + "\" appears in \"" + getTableName() + "\" table more than once" );
        }

    }

    /**
     * Find at most one instance of something.
     * @param elephantConnection the connection to the database.
     * @param columnIx which column to search on.
     * @param key which value to look for.
     * @return the found instance or null if nothing was found.
     * @throws SQLException if something went wrong in JDBC land.
     * @throws LoaNetworksElephantMoreThanOneFoundException if more than one instance was found.
     */

    public Tuple2 findOne( ElephantConnection elephantConnection, int columnIx, DBValue key )
            throws
            SQLException,
            LoaNetworksElephantMoreThanOneFoundException {

        String queryString = "SELECT * FROM " + getSchemaTableName() + " WHERE " +
                             getColumnName( columnIx ) + " = ?";

        PreparedStatement queryStatement = elephantConnection.c().prepareStatement( queryString );
        key.setValueInPreparedStatement( 1, queryStatement );

        try {

            return findOne( queryStatement, getColumnName( columnIx ) + " = " + key );

        } finally {

            queryStatement.close();

        }

    }

    public Tuple2 findOne( PreparedStatement queryStatement, String criteria )
            throws
            SQLException,
            LoaNetworksElephantMoreThanOneFoundException {

        ResultSet rs = queryStatement.executeQuery();

        try {

            if ( rs.next() ) {

                Tuple2 firstResult = makeNewInstance( rs );

                if ( rs.next() ) {

                    throw new LoaNetworksElephantMoreThanOneFoundException(
                            "more than one tuple found with \"" + criteria + "\" using " + queryStatement
                    );

                }

                return firstResult;

            } else {

                return null;

            }

        } finally {

            rs.close();

        }
    }

    /**
     * Find as many instances as match.
     * @param elephantConnection the connection to the database.
     * @param columnIx which column to search on.
     * @param key which value to look for.
     * @return the found instance or null if nothing was found.
     * @throws SQLException if something went wrong in JDBC land.
     */

    public List<Tuple2> findMultiple( ElephantConnection elephantConnection, int columnIx, DBValue key )
            throws
            SQLException {

        String queryString = "SELECT * FROM " + getSchemaTableName() + " WHERE " +
                             getColumnName( columnIx ) + " = ?";

        PreparedStatement queryStatement = elephantConnection.c().prepareStatement( queryString );
        key.setValueInPreparedStatement( 1, queryStatement );

        try {

            return findMultiple( queryStatement, getColumnName( columnIx ) + " = " + key );

        } finally {

            queryStatement.close();

        }

    }

    public List<Tuple2> findMultiple( PreparedStatement queryStatement, String criteria )
            throws
            SQLException {

        ResultSet rs = queryStatement.executeQuery();

        List<Tuple2> results = new LinkedList<Tuple2>();

        try {

            while ( rs.next() ) {

                Tuple2 tuple = makeNewInstance( rs );

                results.add( tuple );

            }

            if ( results.isEmpty() ) {
                return null;
            } else {
                return results;
            }

        } finally {

            rs.close();

        }

    }

    /**
     * Start a scan of tuples with a specified column having a specified value.
     * @param elephantConnection the connection to the database.
     * @param columnIx which column to search on.
     * @param key which value to look for.
     * @return the found instance or null if nothing was found.
     * @throws SQLException if something went wrong in JDBC land.
     */

    public ResultSet startMultipleScan( ElephantConnection elephantConnection, int columnIx, DBValue key )
            throws
            SQLException {

        String queryString = "SELECT * FROM " + getSchemaTableName() + " WHERE " +
                             getColumnName( columnIx ) + " = ?";

        PreparedStatement queryStatement = elephantConnection.c().prepareStatement( queryString );
        key.setValueInPreparedStatement( 1, queryStatement );

        try {

            //noinspection UnnecessaryLocalVariable
            ResultSet rs = queryStatement.executeQuery();

            return rs;

        } finally {

            queryStatement.close();

        }

    }

    /**
     * Make a new instance of a tuple carrier for a tuple from the table that this class describes.
     * @param rs the result set holding the tuple
     * @return the tuple in its in-memory form.
     * @throws SQLException if something goes wrong in JDBC-land.
     */

    public abstract Tuple2 makeNewInstance( ResultSet rs ) throws SQLException;

    protected String formatColumnNames() {
        String rval = "";
        String comma = "";

        for ( String columnName : _columnNamesArray ) {
            rval += comma + columnName;
            comma = ", ";
        }

        return rval;
    }
}
