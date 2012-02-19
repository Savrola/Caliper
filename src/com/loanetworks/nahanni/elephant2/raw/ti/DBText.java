package com.loanetworks.nahanni.elephant2.raw.ti;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Carry around a text value from the database.
 * <p/>
 * Copyright © 2012 Daniel Boulet.
 */

public class DBText extends DBValue {

    private String _stringValue;

    public DBText() {
        super();
    }

    public DBText( String s ) {
        super( DBType.TEXT );

        _stringValue = s;
    }

    public void setValueInPreparedStatement( int columnIndex, PreparedStatement ps )
            throws
            SQLException {

        ps.setString( columnIndex, getStringValue() );

    }

    public void updateValueInResultSet( int columnIndex, ResultSet rs )
            throws
            SQLException {

        rs.updateString( columnIndex, _stringValue );

    }

    public void setValueInThis( int columnIndex, ResultSet rs )
            throws
            SQLException {

        _stringValue = rs.getString( columnIndex );

    }

    public void setObjectValue( Object value ) {
        _stringValue = (String)value;
    }

    public Object getObjectValue() {
        return _stringValue;
    }

    public String getStringValue() {

        return _stringValue;

    }

    public String toString() {

        return "\"" + _stringValue + "\"";  // %%% should escape double quotes and such

    }
}
