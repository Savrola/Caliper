package com.loanetworks.db;

import com.loanetworks.nahanni.elephant2.raw.exceptions.LoaNetworksJDBCDriverLoadFailedException;
import com.loanetworks.nahanni.elephant2.raw.exceptions.LoaNetworksJDBCgetConnectionFailedException;
import com.obtuse.util.Logger;

import java.sql.*;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * Base class to manage Postgres-specific aspects of JDBC connection management.
 * <p/>
 * Copyright © 2007, 2008 Loa Corporation.
 */

public abstract class PostgresConnection {

    private final String _urlString;

    private final String _hostname;

    private final String _dbName;

    private Connection _jdbcConnection = null;

    protected PostgresConnection( String hostname, String dbName ) {
        super();

        _hostname = hostname;
        _dbName = dbName;
        _urlString = MessageFormat.format( "jdbc:postgresql://{0}/{1}", hostname, dbName );

    }

    @SuppressWarnings( { "UnusedDeclaration", "unchecked", "UnusedAssignment" } )
    public void connect( Properties props )
            throws
            LoaNetworksJDBCDriverLoadFailedException,
            LoaNetworksJDBCgetConnectionFailedException {

        try {

            Class<Driver> driver = (Class<Driver>)Class.forName( "org.postgresql.Driver" );
            // System.out.println("driver's class is " + driver.getCanonicalName());

        } catch ( ClassCastException e ) {

            throw new LoaNetworksJDBCDriverLoadFailedException(
                    "Postgres driver is not an instance of java.sql.driver", e
            );

        } catch ( ClassNotFoundException e ) {

            throw new LoaNetworksJDBCDriverLoadFailedException( "unable to load Postgres JDBC driver", e );

        }

        try {

            Logger.logMsg( "connecting to " + _urlString );

            _jdbcConnection = DriverManager.getConnection( _urlString, props );

        } catch ( SQLException e ) {

            throw new LoaNetworksJDBCgetConnectionFailedException( "unable to connect to " + _urlString, e );

        }

        try {

            _jdbcConnection.setAutoCommit( false );

        } catch ( SQLException e ) {

            throw new LoaNetworksJDBCgetConnectionFailedException(
                    "unable to turn off auto-commit for " + _urlString, e
            );

        }

        // Set the session's timezone to UTC as we MUST store all date and time values in UTC to avoid massive confusion later.

        try {

            Statement stmt = _jdbcConnection.createStatement();
            stmt.execute( "set time zone 'UTC'" );

            _jdbcConnection.commit();

        } catch ( SQLException e ) {

            throw new LoaNetworksJDBCgetConnectionFailedException( "unable to set session time zone to UTC", e );

        }

    }

    public void close()
            throws SQLException {

        if ( _jdbcConnection != null ) {

            _jdbcConnection.close();

        }
    }

    public String getHostName() {
        return _hostname;
    }

    @SuppressWarnings({ "UnusedDeclaration" })
    public String getDBName() {
        return _dbName;
    }

    public Connection c() {
        return _jdbcConnection;
    }
}
