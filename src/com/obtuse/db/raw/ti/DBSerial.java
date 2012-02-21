package com.obtuse.db.raw.ti;

/**
 * Carry around a serial value from the database.
 * <p/>
 * Copyright © 2012 Daniel Boulet.
 */

public class DBSerial extends DBInt {

    private Integer _intValue;

    public DBSerial() {
        super( DBType.SERIAL, 0 );
    }

    public DBSerial( int i ) {
        super( DBType.SERIAL, i );

        _intValue = i;
    }

    @SuppressWarnings( { "RefusedBequest" } )
    public String toString() {

        return _intValue.toString();

    }

}
