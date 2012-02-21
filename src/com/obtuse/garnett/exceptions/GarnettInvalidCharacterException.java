package com.obtuse.garnett.exceptions;

/*
 * Copyright © 2012 Daniel Boulet
 */

import com.obtuse.util.Trace;

/**
 * Thrown if an invalid character is found in a password or a user name.
 */

@SuppressWarnings( { "ClassWithoutToString" } )
public class GarnettInvalidCharacterException extends Exception {

    public GarnettInvalidCharacterException() {
        super();

        Trace.event( this );

    }

    public GarnettInvalidCharacterException( String why ) {
        super( why );

        Trace.event( this );

    }

    public GarnettInvalidCharacterException( String why, Throwable e ) {
        super( why, e );

        Trace.event( this );

    }

}
