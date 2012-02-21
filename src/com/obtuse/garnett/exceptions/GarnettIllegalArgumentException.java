package com.obtuse.garnett.exceptions;

import java.io.IOException;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * Thrown if an attempt is made to send a {@link com.obtuse.garnett.GarnettObject}
 * across a {@link com.obtuse.garnett.GarnettSession} before the session is connected.
 */

public class GarnettIllegalArgumentException extends IOException {

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