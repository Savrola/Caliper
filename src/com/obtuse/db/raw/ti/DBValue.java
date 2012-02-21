package com.obtuse.db.raw.ti;

import java.io.Serializable;
import java.sql.*;

/**
 * Abstract representation of a column's value in a tuple.
 * <p/>
 * Copyright © 2012 Daniel Boulet.
 */

public abstract class DBValue implements Serializable {

    private final DBType _dbType;

    protected DBValue() {
        super();

        _dbType = null;
    }

    /**
     * Save the value's {@link DBType}.
     *
     * @param dbType this value's {@link DBType}.
     */

    protected DBValue( DBType dbType ) {
        super();

        _dbType = dbType;
    }

    /**
     * Get this value's {@link DBType}.
     *
     * @return this value's {@link DBType}.
     */

    public DBType getDBType() {

        return _dbType;

    }

    /**
     * Get this value represented as an object whose class is appropriate for this value's {@link DBType}. For example,
     * {@link DBInt} values are returned as instances of the {@link Integer} class.
     *
     * @return this value represented as an {@link Object}
     */

    public abstract Object getObjectValue();

    /**
     * Set the value of the specified column in a {@link PreparedStatement} to this instance's value. The implementation
     * of this method invokes the appropriate set method on the {@link PreparedStatement} instance. For example, the
     * {@link DBInt#setValueInPreparedStatement} implementation uses {@link PreparedStatement#setInt(int, int)} to set
     * the <tt>columnIndex</tt> value in the {@link PreparedStatement} to this instance's value.
     *
     * @param columnIndex the one-origin indication of which parameter in the {@link PreparedStatement} is to be set.
     * @param ps          the prepared statement whose parameter is to be set.
     *
     * @throws SQLException the SQLException thrown by the {@link PreparedStatement}'s setter method.
     */

    public abstract void setValueInPreparedStatement( int columnIndex, PreparedStatement ps )
            throws
            SQLException;

    /**
     * Update a value in a result set's current row to this instance's value. The implementation of this method invokes
     * the appropriate updated method on the {@link ResultSet} instance. For example, the {@link
     * DBInt#updateValueInResultSet} implementation uses {@link ResultSet#updateInt(int, int)} to update the
     * <tt>columnIndex</tt> value in the {@link ResultSet} to this instance's value.
     *
     * @param columnIndex the one-origin indication of which column of the {@link ResultSet}'s current row is to be
     *                    updated.
     * @param rs          the result set whose current row's column is to be update.
     *
     * @throws SQLException the SQLException thrown by the {@link ResultSet}'s update method.
     */

    public abstract void updateValueInResultSet( int columnIndex, ResultSet rs )
            throws
            SQLException;

    /**
     * Set this instance's value to the specified {@link ResultSet}'s current row's specified column's value. The
     * implementation of this method sets this instance's value to the value returned by the appropriate getter method
     * on the {@link ResultSet} instance.  For example, the {@link DBInt#updateValueInResultSet} implementation sets its
     * instance's value to that which is returned by {@link ResultSet#getInt(int)}.
     *
     * @param columnIndex the one-origin indication of which column of the {@link ResultSet}'s current row is to be used
     *                    to set this instance's value.
     * @param rs          the result set whose current row's column's value is to be used to set this instance's value.
     *
     * @throws SQLException the SQLException thrown by the {@link ResultSet}'s getter method.
     */

    public abstract void setValueInThis( int columnIndex, ResultSet rs )
            throws
            SQLException;

    public abstract void setObjectValue( Object value );

    public abstract String toString();

}
