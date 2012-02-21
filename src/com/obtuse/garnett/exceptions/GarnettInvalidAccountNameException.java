package com.obtuse.garnett.exceptions;

/*
 * Copyright © 2012 Daniel Boulet
 */

import com.obtuse.util.Trace;

/**
 * Something is wrong with an account name.
 */

@SuppressWarnings( { "ClassWithoutToString" } )
public class GarnettInvalidAccountNameException extends Exception {

    public GarnettInvalidAccountNameException() {
        super();

        Trace.event( this );

    }

    public GarnettInvalidAccountNameException( String why ) {
        super( why );

        Trace.event( this );

    }

    public GarnettInvalidAccountNameException( String why, Throwable e ) {
        super( why, e );

        Trace.event( this );

    }

}
