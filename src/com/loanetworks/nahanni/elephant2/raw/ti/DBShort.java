package com.loanetworks.nahanni.elephant2.raw.ti;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Carry around a short value from the database.
 * <p/>
 * Copyright © 2007, 2008 Loa Corporation.
 */

public class DBShort extends DBValue {

    private Short _intValue;

    public DBShort() {
        super();
    }

    /**
     * Intended for use by the DBSerial class.
     *
     * @param type the type of the derived class.
     * @param i    the initial value of this instance.
     */

    protected DBShort( DBType type, short i ) {
        super( type );

        _intValue = i;
    }

    /**
     * Define an INT value.
     *
     * @param i the initial value of this instance.
     */

    public DBShort( short i ) {
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

        return _intValue.shortValue();

    }

    public void updateValueInResultSet( int columnIndex, ResultSet rs )
            throws
            SQLException {

        rs.updateInt( columnIndex, _intValue.shortValue() );

    }

    public void setValueInThis( int columnIndex, ResultSet rs )
            throws
            SQLException {

        _intValue = rs.getShort( columnIndex );

    }

    public void setObjectValue( Object value ) {

        _intValue = ( (Short)value ).shortValue();

    }

    public Object getObjectValue() {

        return _intValue;

    }

    public String toString() {
        return _intValue.toString();
    }

}
