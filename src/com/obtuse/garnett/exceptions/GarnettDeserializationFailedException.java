package com.obtuse.garnett.exceptions;

/*
 * Copyright © 2011 Daniel Boulet.
 */

import com.obtuse.garnett.GarnettTypeName;

/**
 * Thrown if an attempt to de-serialize a Garnett object or a primitive Java type fails.
 */

@SuppressWarnings({ "UnusedDeclaration" })
public class GarnettDeserializationFailedException extends GarnettTypeRegistryException {

    public GarnettDeserializationFailedException() {
        super();
    }

    public GarnettDeserializationFailedException( GarnettTypeName garnettTypeName, String message ) {
        super( null, garnettTypeName, message );

    }

    public GarnettDeserializationFailedException( GarnettTypeName garnettTypeName, Throwable cause ) {
        super( null, garnettTypeName, cause );

    }

    public GarnettDeserializationFailedException( GarnettTypeName garnettTypeName, String message, Throwable cause ) {
        super( null, garnettTypeName, message, cause );

    }

}
