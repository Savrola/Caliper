package com.obtuse.garnett.exceptions;

/*
 * Copyright © 2011 Daniel Boulet.
 */

import java.io.IOException;

/**
 * Thrown if an attempt to serialize a Garnett object fails.
 */

public class GarnettSerializationFailedException extends IOException {

    public GarnettSerializationFailedException() {
        super();
    }

    public GarnettSerializationFailedException( String message ) {
        super( message );

    }

    public GarnettSerializationFailedException( Throwable cause ) {
        super( cause );

    }

    public GarnettSerializationFailedException( String message, Throwable cause ) {
        super( message, cause );

    }

}
