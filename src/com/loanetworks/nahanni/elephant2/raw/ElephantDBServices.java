package com.loanetworks.nahanni.elephant2.raw;

import com.loanetworks.nahanni.elephant2.raw.dbdata.Tuple2;
import com.loanetworks.nahanni.elephant2.raw.exceptions.LoaNetworksElephantMoreThanOneFoundException;
import com.loanetworks.nahanni.elephant2.raw.ti.DBValue;
import com.loanetworks.nahanni.elephant2.raw.ti.TableInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.exceptions.HowDidWeGetHereError;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Provide database access services to the Elephant RMI level.
 * <p/>
 * Copyright © 2007, 2008 Loa Corporation.
 */

@SuppressWarnings({ "UnusedDeclaration" })
public abstract class ElephantDBServices {

    protected ElephantDBServices() {
        super();
    }

    public void commitTransaction( String where ) {

        try {

            getConnection().c().commit();

        } catch ( SQLException e ) {

            Logger.logErr( "unable to commit after " + where, e );

            rollbackTransaction( "failed commit of " + where );

        }

    }

    public void rollbackTransaction( String where ) {

        try {

            getConnection().c().rollback();
//            Logger.logErr( "INFO:  transaction rolled back for " + where + " in " + this.getClass() );

        } catch ( SQLException ee ) {

            Logger.logErr( "unable to rollback after " + where + " - bye!", ee );
            System.exit( 1 );

        }

    }

    protected abstract ElephantConnection getConnection();

    protected void jdbcError( SQLException e ) {

        throw new HowDidWeGetHereError( "JDBC problem", e );

    }

    public Tuple2 findOneTuple( ElephantConnection elephantConnection, TableInfo ti, String columnName, DBValue key ) {

        try {

            return ti.findOne( elephantConnection, ti.getColumnIndex( columnName ), key );

        } catch ( SQLException e ) {

            jdbcError( e );
            return null;

        } catch ( LoaNetworksElephantMoreThanOneFoundException e ) {

            throw new HowDidWeGetHereError( "duplicate " + columnName + " " + key + " found in " + ti.getTableName() + " table", e );

        }

    }

    public Tuple2 findOneTuple( TableInfo ti, PreparedStatement queryStatement, String criteria ) {

        try {

            return ti.findOne( queryStatement, criteria );

        } catch ( SQLException e ) {

            jdbcError( e );
            return null;

        } catch ( LoaNetworksElephantMoreThanOneFoundException e ) {

            throw new HowDidWeGetHereError(
                    "duplicate " + criteria + " found in " + ti.getTableName() + " table using " + queryStatement, e
            );

        }

    }

}
