package com.obtuse.garnett.exceptions;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * Thrown if an attempt is made to create a {@link com.obtuse.garnett.GarnettSessionPrefix} using a session prefix
 * byte array containing an unsupported protocol version code.
 */

public class GarnettUnsupportedProtocolVersionException extends Exception {

    public GarnettUnsupportedProtocolVersionException() {
        super();

    }

    public GarnettUnsupportedProtocolVersionException( String message ) {
        super( message );

    }

    public GarnettUnsupportedProtocolVersionException( Throwable cause ) {
        super( cause );

    }

    public GarnettUnsupportedProtocolVersionException( String message, Throwable cause ) {
        super( message, cause );

    }

}