package com.loanetworks.nahanni.elephant2.raw.devutils;

import java.io.Serializable;

/**
 * Describe the various was that a database type will need to be 'described' in various contexts.
 * <p/>
 * Copyright © 2007, 2008 Loa Corporation.
 */

public class LoaTypeInfo implements Serializable {

    private final String _dbType;

    private final String _loaType;

    private final String _javaType;

    private final String _nullableJavaType;

    private final String _resultSetType;

    private final String _preparedStatementType;

    private final boolean _assignmentRequiresCloning;

    private final boolean _requiresCast;

    private final boolean _isSerialType;

    public LoaTypeInfo() {
        super();

        _dbType = null;
        _loaType = null;
        _javaType = null;
        _nullableJavaType = null;
        _resultSetType = null;
        _preparedStatementType = null;
        _assignmentRequiresCloning = false;
        _requiresCast = false;
        _isSerialType = false;
    }

    public LoaTypeInfo(
            String dbType,
            String loaType,
            String javaType,
            String nullableJavaType,
            String resultSetType,
            String preparedStatementType,
            boolean assignmentRequiresCloning,
            boolean requiresCast,
            boolean isSerialType
    ) {
        super();

        _dbType = dbType;
        _loaType = loaType;
        _javaType = javaType;
        _nullableJavaType = nullableJavaType;
        _resultSetType = resultSetType;
        _preparedStatementType = preparedStatementType;
        _assignmentRequiresCloning = assignmentRequiresCloning;
        _requiresCast = requiresCast;
        _isSerialType = isSerialType;
    }

    public String getDBType() {
        return _dbType;
    }

    public String getLoaType() {
        return _loaType;
    }

    public String getJavaType( int nullable ) {

        if ( nullable == 1 ) {
            return _nullableJavaType;
        } else {
            return _javaType;
        }

    }

    public String getResultSetType() {
        return _resultSetType;
    }

    public String getPreparedStatementType() {
        return _preparedStatementType;
    }

    public boolean doesAssignmentRequireCloning() {
        return _assignmentRequiresCloning;
    }

    public boolean isSerialType() {
        return _isSerialType;
    }

    public boolean requiresCast() {
        return _requiresCast;
    }

    public String toString() {
        return "LoaTypeInfo( " +
               "dbType = " + _dbType + ", " +
               "loaType = " + _loaType + ", " +
               "javaType = " + _javaType + ", " +
               "resultSetType = " + _resultSetType + ", " +
               "preparedStatementType = " + _preparedStatementType + ", " +
               "assignmentRequiresCloning = " + _assignmentRequiresCloning + ", " +
               "isSerialType = " + _isSerialType +
               " )";
    }
}
