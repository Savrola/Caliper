package com.obtuse.db.raw.devutils;

import java.io.Serializable;

/**
 * Describe the various was that a database type will need to be 'described' in various contexts.
 * <p/>
 * Copyright © 2012 Daniel Boulet.
 */

public class SavrolaTypeName implements Serializable {

    private final String _dbType;

    private final String _savrolaType;

    private final String _javaType;

    private final String _nullableJavaType;

    private final String _resultSetType;

    private final String _preparedStatementType;

    private final boolean _assignmentRequiresCloning;

    private final boolean _requiresCast;

    private final boolean _isSerialType;

//    @SuppressWarnings("UnusedDeclaration")
//    @Deprecated
//    public SavrolaTypeName() {
//        super();
//
//        _dbType = null;
//        _savrolaType = null;
//        _javaType = null;
//        _nullableJavaType = null;
//        _resultSetType = null;
//        _preparedStatementType = null;
//        _assignmentRequiresCloning = false;
//        _requiresCast = false;
//        _isSerialType = false;
//
//    }

    public SavrolaTypeName(
            String dbType,
            String savrolaType,
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
        _savrolaType = savrolaType;
        _javaType = javaType;
        _nullableJavaType = nullableJavaType;
        _resultSetType = resultSetType;
        _preparedStatementType = preparedStatementType;
        _assignmentRequiresCloning = assignmentRequiresCloning;
        _requiresCast = requiresCast;
        _isSerialType = isSerialType;

    }

    @SuppressWarnings("UnusedDeclaration")
    public String getDBType() {

        return _dbType;

    }

    public String getSavrolaType() {

        return _savrolaType;

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

        return "SavrolaTypeName( " +
               "dbType = " + _dbType + ", " +
               "loaType = " + _savrolaType + ", " +
               "javaType = " + _javaType + ", " +
               "resultSetType = " + _resultSetType + ", " +
               "preparedStatementType = " + _preparedStatementType + ", " +
               "assignmentRequiresCloning = " + _assignmentRequiresCloning + ", " +
               "isSerialType = " + _isSerialType +
               " )";

    }

}
