package com.obtuse.db.raw.ti;

import java.sql.*;

/**
 * Carry around an int value from the database.
 * <p/>
 * Copyright © 2012 Daniel Boulet.
 */

public class DBInt extends DBValue {

    private Integer _intValue;

    public DBInt() {
        super();
    }

    /**
     * Intended for use by the DBSerial class.
     *
     * @param type the type of the derived class.
     * @param i    the initial value of this instance.
     */

    @SuppressWarnings({ "SameParameterValue" })
    protected DBInt( DBType type, int i ) {
        super( type );

        _intValue = i;
    }

    /**
     * Define an INT value.
     *
     * @param i the initial value of this instance.
     */

    public DBInt( int i ) {
        super( DBType.INT );

        _intValue = i;
    }

    /**
     * Set a parameter value in a {@link PreparedStatement} to an INT.
     *
     * @param columnIndex which parameter value to set.
     * @param ps          the prepared statement in which the parameter value is to be set.
     *
     * @throws SQLException if something goes wrong in JDBC-land.
     */

    public void setValueInPreparedStatement( int columnIndex, PreparedStatement ps )
            throws
            SQLException {

        ps.setInt( columnIndex, getIntValue() );

    }

    /**
     * Get the value of this instance.
     *
     * @return the value of this instance.
     */

    public int getIntValue() {

        return _intValue;

    }

    public void updateValueInResultSet( int columnIndex, ResultSet rs )
            throws
            SQLException {

        rs.updateInt( columnIndex, _intValue );

    }

    public void setValueInThis( int columnIndex, ResultSet rs )
            throws
            SQLException {

        _intValue = rs.getInt( columnIndex );

    }

    public void setObjectValue( Object value ) {

        _intValue = ( (Integer)value ).intValue();

    }

    public Object getObjectValue() {

        return _intValue;

    }

    public String toString() {
        return _intValue.toString();
    }

}
