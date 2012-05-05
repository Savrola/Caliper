package com.obtuse.db.raw.ti;

import java.sql.*;

/**
 * Carry around a timestamptz value from the database (represented as a long in all meaningful respects).
 * <p/>
 * Copyright © 2012 Daniel Boulet.
 */

public class DBTimestamptz extends DBValue {

    private java.sql.Timestamp _ts;

    public DBTimestamptz() {
        super();
    }

    public DBTimestamptz( Timestamp ts ) {
        super( DBType.TIMESTAMPTZ );

        _ts = (Timestamp)ts.clone();
    }

    public void setValueInPreparedStatement( int columnIndex, PreparedStatement ps )
            throws
            SQLException {

        ps.setTimestamp( columnIndex, _ts );

    }

    public Timestamp getTimestampTZValue() {

        return _ts;

    }

    public void updateValueInResultSet( int columnIndex, ResultSet rs )
            throws
            SQLException {

        rs.updateTimestamp( columnIndex, _ts );

    }

    public void setValueInThis( int columnIndex, ResultSet rs )
            throws
            SQLException {

        _ts = (Timestamp)rs.getTimestamp(columnIndex).clone();

    }

    public void setObjectValue( Object value ) {

        _ts = (Timestamp)( (java.util.Date)value ).clone();

    }

    public Object getObjectValue() {

        return _ts;

    }

    public String toString() {
        return _ts.toString();
    }

}
