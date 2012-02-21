package com.obtuse.db.raw.ti;

import com.obtuse.util.ObtuseUtil5;

import java.sql.*;

/**
 * Carry around a byte array value from the database.
 * <p/>
 * Copyright © 2012 Daniel Boulet.
 */

public class DBByteArray extends DBValue {

    private byte[] _byteArrayValue;

    public DBByteArray() {
        super();

        _byteArrayValue = null;
    }

    public DBByteArray( byte[] byteArrayValue ) {
        super( DBType.LONG );

        _byteArrayValue = byteArrayValue == null ? null : byteArrayValue.clone();

    }

    public void setValueInPreparedStatement( int columnIndex, PreparedStatement ps )
            throws
            SQLException {

        ps.setObject( columnIndex, getByteArrayValue() );

    }

    public byte[] getByteArrayValue() {

        return _byteArrayValue == null ? null : _byteArrayValue.clone();

    }

    public void updateValueInResultSet( int columnIndex, ResultSet rs )
            throws
            SQLException {

        rs.updateBytes( columnIndex, getByteArrayValue() );

    }

    public void setValueInThis( int columnIndex, ResultSet rs )
            throws
            SQLException {

        _byteArrayValue = rs.getBytes( columnIndex );

    }

    public void setObjectValue( Object value ) {

        _byteArrayValue = value == null ? null : ( (byte[])value ).clone();

    }

    public Object getObjectValue() {

        return _byteArrayValue == null ? null : _byteArrayValue.clone();

    }

    public String toString() {

        return ObtuseUtil5.hexvalue( _byteArrayValue );

    }

}