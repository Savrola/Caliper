package com.obtuse.garnett.exceptions;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * Thrown in various contexts if an illegal argument is provided to a Garnett method.
 * Note that unexpected null parameter values are illegal in many situations.
 * See the documentation for the method that threw the exception for more info.
 */

@SuppressWarnings("SameParameterValue")
public class GarnettIllegalArgumentException extends Exception {

    public GarnettIllegalArgumentException() {
        super();
    }

    public GarnettIllegalArgumentException( String message ) {
        super( message );

    }

    public GarnettIllegalArgumentException( Throwable cause ) {
        super( cause );

    }

    public GarnettIllegalArgumentException( String message, Throwable cause ) {
        super( message, cause );

    }

}