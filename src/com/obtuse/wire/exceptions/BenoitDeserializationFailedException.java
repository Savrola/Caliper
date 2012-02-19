package com.obtuse.wire.exceptions;

/*
 * Copyright © 2011 Daniel Boulet.
 */

import com.obtuse.wire.BenoitTypeName;

/**
 * Thrown if an attempt to de-serialize a Benoit object or a primitive Java type fails.
 */

@SuppressWarnings({ "UnusedDeclaration" })
public class BenoitDeserializationFailedException extends BenoitTypeRegistryException {

    public BenoitDeserializationFailedException() {
        super();
    }

    public BenoitDeserializationFailedException( BenoitTypeName benoitTypeName, String message ) {
        super( null, benoitTypeName, message );

    }

    public BenoitDeserializationFailedException( BenoitTypeName benoitTypeName, Throwable cause ) {
        super( null, benoitTypeName, cause );

    }

    public BenoitDeserializationFailedException( BenoitTypeName benoitTypeName, String message, Throwable cause ) {
        super( null, benoitTypeName, message, cause );

    }

}
