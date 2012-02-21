package com.obtuse.garnett.exceptions;

import java.io.IOException;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * Thrown if an attempt is made to send a {@link com.obtuse.garnett.GarnettObject}
 * across a {@link com.obtuse.garnett.GarnettSession} before the session is connected.
 */

public class GarnettNotConnectedException extends IOException {

    public GarnettNotConnectedException() {
        super();
    }

    public GarnettNotConnectedException( String message ) {
        super( message );

    }

    public GarnettNotConnectedException( Throwable cause ) {
        super( cause );

    }

    public GarnettNotConnectedException( String message, Throwable cause ) {
        super( message, cause );

    }

}