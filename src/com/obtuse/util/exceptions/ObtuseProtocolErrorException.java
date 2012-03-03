package com.obtuse.util.exceptions;

import com.obtuse.util.Trace;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * Thrown if a protocol error is detected.
 */

@SuppressWarnings({ "ClassWithoutToString", "UnusedDeclaration" })
public class ObtuseProtocolErrorException extends Exception {

    public ObtuseProtocolErrorException() {
        super();

        Trace.event( this );

    }

    public ObtuseProtocolErrorException( String why ) {
        super( why );

        Trace.event( this );

    }

    public ObtuseProtocolErrorException( String why, Throwable e ) {
        super( why, e );

        Trace.event( this );

    }

}
