package com.obtuse.garnett.exceptions;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * Thrown if a message arrives (at a client or a server) for which there is no handler defined.
 */

public class GarnettNoMessageHandlerDefinedException extends Exception {

    public GarnettNoMessageHandlerDefinedException() {
        super();
    }

    public GarnettNoMessageHandlerDefinedException( String message ) {
        super( message );

    }

    public GarnettNoMessageHandlerDefinedException( Throwable cause ) {
        super( cause );

    }

    public GarnettNoMessageHandlerDefinedException( String message, Throwable cause ) {
        super( message, cause );

    }

}