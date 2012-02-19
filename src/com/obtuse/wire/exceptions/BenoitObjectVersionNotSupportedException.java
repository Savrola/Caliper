package com.obtuse.wire.exceptions;

/*
 * Copyright © 2011 Daniel Boulet.
 */

import com.obtuse.wire.BenoitObjectRestorerRegistry;
import com.obtuse.wire.BenoitTypeName;

/**
 * Thrown if {@link com.obtuse.wire.BenoitObjectInputStreamInterface#checkVersion(int, int)} determines
 * that the version of the object which has just been received is not supported.
 */

public class BenoitObjectVersionNotSupportedException extends BenoitTypeRegistryException {

    public BenoitObjectVersionNotSupportedException() {
        super();

    }

    /**
     * Create a fully-described exception instance.
     * @param why what went wrong in human-readable form.
     * @param benoitObjectRestorerRegistry the registry which threw this exception.
     * @param bogusBenoitTypeName the name of the Benoit type which unknown to the registry.
     */

    public BenoitObjectVersionNotSupportedException(
            String why,
            BenoitObjectRestorerRegistry benoitObjectRestorerRegistry,
            BenoitTypeName bogusBenoitTypeName
    ) {
        super( benoitObjectRestorerRegistry, bogusBenoitTypeName, why );

    }

}
