package com.obtuse.exceptions;

/**
 * Thrown if something truly unexpected happens.
 * <p>
 * Copyright © 2008 Obtuse Systems Corporation
 */

public class HowDidWeGetHereError
        extends RuntimeException {

    public HowDidWeGetHereError() {
        super();
    }

    public HowDidWeGetHereError( String msg ) {
        super(msg);
    }

    public HowDidWeGetHereError( String msg, Throwable e ) {
        super(msg, e);
    }

}
