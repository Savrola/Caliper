package com.obtuse.db.raw.exceptions;

/**
 * Thrown if we are unable to load a suitable JDBC driver.
 * <p/>
 * Copyright © 2012 Daniel Boulet.
 */

@SuppressWarnings({"ClassWithoutToString"})
public class ObtuseJDBCDriverLoadFailedException extends Exception {

    public ObtuseJDBCDriverLoadFailedException() {
        super();
    }

    public ObtuseJDBCDriverLoadFailedException( String why ) {
        super(why);
    }

    public ObtuseJDBCDriverLoadFailedException( String why, Throwable e ) {
        super(why,e);
    }
}
