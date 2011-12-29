package com.loanetworks.nahanni.elephant2.raw.ti;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Carry around a long value from the database.
 * <p/>
 * Copyright © 2007, 2008 Loa Corporation.
 */

public class DBLong extends DBValue {

    private Long _longValue;

    public DBLong() {
        super();
    }

    public DBLong( long i ) {
        super( DBType.LONG );

        _longValue = i;
    }

    public void setValueInPreparedStatement( int columnIndex, PreparedStatement ps )
            throws
            SQLException {

        ps.setLong( columnIndex, getLongValue() );

    }

    public long getLongValue() {

        return _longValue.longValue();

    }

    public void updateValueInResultSet( int columnIndex, ResultSet rs )
            throws
            SQLException {

        rs.updateLong( columnIndex, _longValue.longValue() );

    }

    public void setValueInThis( int columnIndex, ResultSet rs )
            throws
            SQLException {

        _longValue = rs.getLong( columnIndex );

    }

    public void setObjectValue( Object value ) {

        _longValue = ( (Long)value ).longValue();

    }

    public Object getObjectValue() {

        return _longValue;

    }

    public String toString() {
        return _longValue.toString();
    }

}
