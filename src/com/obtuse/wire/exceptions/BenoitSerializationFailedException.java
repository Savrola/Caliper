package com.obtuse.wire.exceptions;

/*
 * Copyright © 2011 Daniel Boulet.
 */

import java.io.IOException;

/**
 * Thrown if an attempt to serialize a Benoit object fails.
 */

public class BenoitSerializationFailedException extends IOException {

    public BenoitSerializationFailedException() {
        super();
    }

    public BenoitSerializationFailedException( String message ) {
        super( message );

    }

    public BenoitSerializationFailedException( Throwable cause ) {
        super( cause );

    }

    public BenoitSerializationFailedException( String message, Throwable cause ) {
        super( message, cause );

    }

}
