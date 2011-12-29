package com.loanetworks.nahanni.elephant2.raw.ti;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Carry around a boolean value from the database.
 * <p/>
 * Copyright © 2007, 2008 Loa Corporation.
 */

public class DBBoolean extends DBValue {

    private Boolean _booleanValue;

    public DBBoolean() {
        super();
    }

    /**
     * Define a BOOLEAN value.
     *
     * @param value the initial value of this instance.
     */

    public DBBoolean( boolean value ) {
        super( DBType.BOOLEAN );

        _booleanValue = value;
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

        ps.setBoolean( columnIndex, getBooleanValue() );

    }

    /**
     * Get the value of this instance.
     *
     * @return the value of this instance.
     */

    public boolean getBooleanValue() {

        return _booleanValue;

    }

    public void updateValueInResultSet( int columnIndex, ResultSet rs )
            throws
            SQLException {

        rs.updateBoolean( columnIndex, _booleanValue );

    }

    public void setValueInThis( int columnIndex, ResultSet rs )
            throws
            SQLException {

        _booleanValue = rs.getBoolean( columnIndex );

    }

    public void setObjectValue( Object value ) {

        _booleanValue = ( (Boolean)value ).booleanValue();

    }

    public Object getObjectValue() {

        return _booleanValue;

    }

    public String toString() {

        return _booleanValue.toString();

    }

}
