package com.obtuse.garnett.exceptions;

import com.obtuse.util.Trace;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * Thrown if an invalid password is used to authenticate a user.
 */

@SuppressWarnings( { "ClassWithoutToString" } )
public class GarnettInvalidPasswordException extends Exception {

    public GarnettInvalidPasswordException() {
        super();

        Trace.event( this );

    }

    public GarnettInvalidPasswordException( String why ) {
        super( why );

        Trace.event( this );

    }

    public GarnettInvalidPasswordException( String why, Throwable e ) {
        super( why, e );

        Trace.event( this );

    }

}
