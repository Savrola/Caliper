package com.loanetworks.nahanni.elephant2.raw.dbdata;

import com.loanetworks.nahanni.elephant2.raw.ElephantConnection;
import com.loanetworks.nahanni.elephant2.raw.ti.*;
import com.obtuse.util.ObtuseUtil5;
import com.obtuse.util.exceptions.HowDidWeGetHereError;

import java.io.Serializable;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * General purpose management of something that (normally) exists as a tuple in the database.
 * <p/>
 * Copyright © 2007, 2008 Loa Corporation.
 */

public abstract class Tuple implements Serializable {

    private final TableInfo _tableInfo;

    private final DBValue[] _values;

    private final boolean[] _dirty;

    private boolean _allDirty;

    private static final Map<String, Class<? extends Tuple>> _knownTables = new HashMap<String, Class<? extends Tuple>>();

    /**
     * Describe a {@link PreparedStatement} used in an insertion operation.
     */

    @SuppressWarnings( { "ClassWithoutToString" } )
    private static class BundledPreparedStatement {

        private final PreparedStatement _ps;

        private final int[] _serialColumnNumbers;

        private final String[] _sequenceNames;

        private BundledPreparedStatement( PreparedStatement ps, int[] serialColumnNumbers, String[] sequenceNames ) {
            super();

            _ps = ps;
            _serialColumnNumbers = serialColumnNumbers;
            _sequenceNames = sequenceNames;
        }

        private PreparedStatement getPs() {
            return _ps;
        }

        private int[] getSerialColumnNumbers() {
            return _serialColumnNumbers;
        }

        private String[] getSequenceNames() {
            return _sequenceNames;
        }
    }

    protected Tuple() {
        super();

        _tableInfo = null;
        _values = null;
        _dirty = null;
    }

    protected Tuple( TableInfo tableInfo ) {
        super();

        synchronized ( _knownTables ) {
            String ucTableName = tableInfo.getTableName().toUpperCase();
            Class<? extends Tuple> correctClass = _knownTables.get( ucTableName );
            if ( correctClass == null ) {
                _knownTables.put( ucTableName, getClass() );
            } else //noinspection ObjectEquality
                if ( correctClass != getClass() ) {
                    throw new HowDidWeGetHereError(
                            "table \"" + ucTableName + "\" already being used by " + correctClass + ", use with " +
                            _knownTables.get( ucTableName ) + " not allowed"
                    );
                }

        }

        _tableInfo = tableInfo;
        _values = new DBValue[_tableInfo.getColumnCount()];
        _dirty = new boolean[_tableInfo.getColumnCount()];

    }

    /**
     * Initialize a SERIAL column's value.
     *
     * @param columnIx the column's index within the table (0-origin here).
     * @param value    the initial value of the column.
     */

    protected void initializeSerialValue( int columnIx, int value ) {

        _values[columnIx] = new DBSerial( value );

    }

    /**
     * Initialize an INT column's value.
     *
     * @param columnIx the column's index within the table (0-origin here).
     * @param value    the initial value of the column.
     */

    protected void initializeIntValue( int columnIx, int value ) {

        _values[columnIx] = new DBInt( value );

    }

    /**
     * Initialize a BOOLEAN column's value.
     *
     * @param columnIx the column's index within the table (0-origin here).
     * @param value    the initial value of the column.
     */

    protected void initializeBooleanValue( int columnIx, boolean value ) {

        _values[columnIx] = new DBBoolean( value );

    }

    /**
     * Initialize a TIMESTAMPTZ column's value.
     *
     * @param columnIx the column's index within the table (0-origin here).
     * @param value    the initial value of the column.
     */

    protected void initializeTimestampTZValue( int columnIx, Timestamp value ) {

        _values[columnIx] = new DBTimestamptz( value );

    }

    /**
     * Initialize a TIMESTAMPTZ column's value.
     *
     * @param columnIx the column's index within the table (0-origin here).
     * @param value    the initial value of the column.
     *
     * @deprecated should use the java.sql.Timestamp type to pass timestamps around.
     */

    protected void initializeTimestampTZValue( int columnIx, long value ) {

        throw new HowDidWeGetHereError( "unsupported method - bye!" );

    }

    /**
     * Initialize a TEXT column's value.
     *
     * @param columnIx the column's index within the table (0-origin here).
     * @param value    the initial value of the column.
     */

    protected void initializeTextValue( int columnIx, String value ) {

        _values[columnIx] = new DBText( value );

    }

    /**
     * Initialize a LONG column's value.
     *
     * @param columnIx the column's index within the table (0-origin here).
     * @param value    the initial value of the column.
     */

    protected void initializeLongValue( int columnIx, long value ) {

        _values[columnIx] = new DBLong( value );

    }

    /**
     * Get the value of a SERIAL column.
     *
     * @param columnIx the 0-origin index of the column within the tuple.
     *
     * @return the value of the column.
     */

    protected int getSerialValue( int columnIx ) {

        return (Integer)_values[columnIx].getObjectValue();

    }

    /**
     * Get the value of an INT column.
     *
     * @param columnIx the 0-origin index of the column within the tuple.
     *
     * @return the value of the column.
     */

    protected int getIntValue( int columnIx ) {

        return (Integer)_values[columnIx].getObjectValue();

    }

    /**
     * Get the value of a BOOLEAN column.
     *
     * @param columnIx the 0-origin index of the column within the tuple.
     *
     * @return the value of the column.
     */

    protected boolean getBooleanValue( int columnIx ) {

        return ( (Boolean)_values[columnIx].getObjectValue() ).booleanValue();

    }

    /**
     * Get the value of a LONG column.
     *
     * @param columnIx the 0-origin index of the column within the tuple.
     *
     * @return the value of the column.
     */

    protected long getLongValue( int columnIx ) {

        return ( (Long)_values[columnIx].getObjectValue() ).longValue();

    }

    /**
     * Get the value of a TIMESTAMPTZ column.
     *
     * @param columnIx the 0-origin index of the column within the tuple.
     *
     * @return the value of the column.
     */

    protected Timestamp getTimestampTZValue( int columnIx ) {

        return (Timestamp)_values[columnIx].getObjectValue();

    }

    /**
     * Get the value of a TEXT column.
     *
     * @param columnIx the 0-origin index of the column within the tuple.
     *
     * @return the value of the column.
     */

    protected String getTextValue( int columnIx ) {

        return (String)_values[columnIx].getObjectValue();

    }

    /**
     * Set the value of a column. The value must be of the appropriate type for the column's type: <ul> <li>{@link
     * Integer} for INT columns. <li>{@link Integer} for SERIAL columns (probably a waste of time except when an
     * instance is loaded from the database). <li>{@link Long} for LONG columns. <li>{@link String} for TEXT columns.
     * </ul>
     *
     * @param columnIx the 0-origin index of the column within the tuple.
     * @param value    the value to be assigned to the column.
     */

    protected void setObjectValue( int columnIx, Object value ) {

        _values[columnIx].setObjectValue( value );
        _dirty[columnIx] = true;

    }

    /**
     * Get this Tuple's metadata in the form of a {@link TableInfo} instance.
     *
     * @return this Tuple's metadata.
     */

    public TableInfo getTableInfo() {

        return _tableInfo;

    }

    /**
     * Determine if a column's value is dirty.
     *
     * @param columnName the column's name.
     *
     * @return true if the column's value has been set via {@link #setObjectValue(int, Object)} since this instance was
     *         created; false otherwise.
     */

    public boolean isDirty( String columnName ) {

        return _allDirty || _dirty[_tableInfo.getColumnIndex( columnName )];

    }

    /**
     * Mark all column values as dirty.
     */

    protected void setAllDirty() {

        _allDirty = true;

    }

    /**
     * Determine if all the columns been explicitly marked as dirty.
     *
     * @return true if {@link #setAllDirty()} has been called on this instance.
     */

    public boolean allDirty() {

        return _allDirty;

    }

    /**
     * Create a {@link PreparedStatement} which is suitable for inserting this instance into its table. The insert
     * statement will initialize all non-SERIAL columns of the new tuple once it has been initialized (typically via
     * {@link #initializePreparedInsert(PreparedStatement)} or {@link #initializePreparedInsert(BundledPreparedStatement)})
     * and then executed.
     *
     * @param c the connection to the database containing the table in question.
     *
     * @return the INSERT statement.
     *
     * @throws SQLException if something goes wrong in JDBC-land.
     */

    protected BundledPreparedStatement prepareInsert( ElephantConnection c )
            throws
            SQLException {

        String stmt = "INSERT INTO " + _tableInfo.getTableName() + " (";

        String comma = "";
        int nSerialColumns = 0;
        for ( int i = 0; i < _tableInfo.getColumnCount(); i += 1 ) {
            if ( _tableInfo.getColumnType( i ) == DBType.SERIAL ) {
                nSerialColumns += 1;
            } else {
                stmt += comma + " " + _tableInfo.getColumnName( i );
                comma = ", ";
            }
        }

        stmt += " ) VALUES (";

        comma = "";
        int[] autoGeneratedColumns = new int[nSerialColumns];
        String[] sequenceNames = new String[nSerialColumns];
        nSerialColumns = 0;
        for ( int i = 0; i < _tableInfo.getColumnCount(); i += 1 ) {
            if ( _tableInfo.getColumnType( i ) == DBType.SERIAL ) {
                autoGeneratedColumns[nSerialColumns] = i + 1;
                sequenceNames[nSerialColumns] =
                        _tableInfo.getTableName() + "_" + _tableInfo.getColumnName( i ) + "_seq";
                nSerialColumns += 1;
            } else {
                stmt += comma + " ?";
                comma = ", ";
            }
        }

        stmt += " )";

        PreparedStatement ps = c.c().prepareStatement( stmt );

        return new BundledPreparedStatement( ps, autoGeneratedColumns, sequenceNames );

    }

    /**
     * Initialize a {@link PreparedStatement} created using {@link #prepareInsert(ElephantConnection)} with the values
     * of this instance.
     *
     * @param ps the prepared statement.
     *
     * @return the prepared statement passed to this method.
     *
     * @throws SQLException if something goes wrong in JDBC-land.
     */

    protected PreparedStatement initializePreparedInsert( PreparedStatement ps )
            throws
            SQLException {

        int columnIx = 1;
        for ( int i = 0; i < _tableInfo.getColumnCount(); i += 1 ) {

            if ( _values[i].getDBType() != DBType.SERIAL ) {
                _values[i].setValueInPreparedStatement( columnIx, ps );
                columnIx += 1;
            }

        }

        return ps;
    }

    /**
     * Initialize a {@link PreparedStatement} contained within a {@link BundledPreparedStatement} created using {@link
     * #prepareInsert(ElephantConnection)} with the values of this instance.
     *
     * @param bundledPs the bundled prepared statement.
     *
     * @return the bundled prepared statement passed to this method.
     *
     * @throws SQLException if something goes wrong in JDBC-land.
     */

    protected BundledPreparedStatement initializePreparedInsert( BundledPreparedStatement bundledPs )
            throws
            SQLException {

        initializePreparedInsert( bundledPs.getPs() );

        return bundledPs;
    }

    /**
     * Construct a string representation of a SELECT statement that will return the current values of the sequences
     * associated with this tuple type's SERIAL columns.
     * <p/>
     * <b>IMPORTANT:</b> this method is inherently Postgres dependent in its use of the <tt>CURRVAL()</tt>.
     *
     * @param sequenceNames the sequence names associated with this tuple type's SERIAL columns.
     *
     * @return the SELECT statement.
     */

    protected String constructCurvalSelect( String[] sequenceNames ) {
        String selectQuery = "SELECT ";
        for ( String sequenceName : sequenceNames ) {
            selectQuery += "CURRVAL('" + sequenceName + "') ";
        }
        // Testing time
        // selectQuery += ", CURRVAL('" + sequenceNames[0] + "')";
        return selectQuery;
    }

    /**
     * Insert this instance into its table.
     *
     * @param elephantConnection             the connection to the database containing this instance's table.
     * @param retrieveAutogeneratedKeys true if the values of any autogenerated keys (i.e. SERIAL columns) is to be
     *                                  returned.
     *
     * @return a bundle containing the values of any autogenerated keys if <tt>retrieveAutogeneratedKeys</tt> was true;
     *         null otherwise.
     *
     * @throws SQLException if anything goes wrong in JDBC-land.
     */

    public BundledKeys insert( ElephantConnection elephantConnection, boolean retrieveAutogeneratedKeys )
            throws
            SQLException {

        BundledPreparedStatement bundle = null;
        try {
            bundle = prepareInsert( elephantConnection );
            initializePreparedInsert( bundle );
            bundle.getPs().executeUpdate();

            long[] keys = null;

            if ( retrieveAutogeneratedKeys && bundle.getSerialColumnNumbers().length > 0 ) {

                // System.out.println("getting auto-generated keys");
                keys = new long[bundle.getSerialColumnNumbers().length];

                String queryString = constructCurvalSelect( bundle.getSequenceNames() );

                Statement queryStatement = elephantConnection.c().createStatement();

                try {
                    ResultSet rs = queryStatement.executeQuery( queryString );
                    try {
                        if ( rs.next() ) {
                            // System.out.println("getting row");
                            for ( int i = 0; i < keys.length; i += 1 ) {
                                keys[i] = rs.getLong( i + 1 );
                            }
                        }
                    } finally {
                        ObtuseUtil5.closeQuietly( rs );
                    }

                } finally {
                    queryStatement.close();
                }
            }

            return new BundledKeys( keys, bundle.getSerialColumnNumbers() );

        } finally {

            if ( bundle != null ) {
                ObtuseUtil5.closeQuietly( bundle.getPs() );
            }

        }
    }

}
