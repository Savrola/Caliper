package com.obtuse.garnett.exceptions;

import com.obtuse.util.Trace;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * Thrown if an attempt is made to manipulate a password which is too short.
 */

@SuppressWarnings({ "ClassWithoutToString", "SameParameterValue" })
public class GarnettPasswordTooShortException extends GarnettInvalidPasswordException {

    public GarnettPasswordTooShortException() {
        super();

        Trace.event( this );

    }

    public GarnettPasswordTooShortException( String why ) {
        super( why );

        Trace.event( this );

    }

    public GarnettPasswordTooShortException( String why, Throwable e ) {
        super( why, e );

        Trace.event( this );

    }

}
