package com.obtuse.db.raw.dbdata;

import org.postgresql.util.PGobject;

import java.sql.SQLException;

/**
 * Static helper methods for Postgres' inet and macaddr types.
 * <p/>
 * Copyright © 2012 Daniel Boulet.
 */

public class DBInet {

    private DBInet() {
        super();
    }

    public static PGobject makeInet( String addr )
            throws
            SQLException {

        PGobject obj = new PGobject();
        obj.setType( "inet" );
        obj.setValue( addr );

        return obj;

    }

    public static PGobject makeMacaddr( String addr )
            throws
            SQLException {

        PGobject obj = new PGobject();
        obj.setType( "macaddr" );
        obj.setValue( addr );

        return obj;

    }

}
