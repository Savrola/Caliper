package com.obtuse.garnett.exceptions;

/*
 * Copyright © 2011 Daniel Boulet.
 */

import com.obtuse.garnett.GarnettObjectRestorerRegistry;
import com.obtuse.garnett.GarnettTypeName;

/**
 * Thrown if {@link com.obtuse.garnett.GarnettObjectInputStreamInterface#checkVersion(com.obtuse.garnett.GarnettTypeName, int, int)} determines
 * that the version of the object which has just been received is not supported.
 */

public class GarnettObjectVersionNotSupportedException extends GarnettTypeRegistryException {

    public GarnettObjectVersionNotSupportedException() {
        super();

    }

    /**
     * Create a fully-described exception instance.
     * @param why what went wrong in human-readable form.
     * @param garnettObjectRestorerRegistry the registry which threw this exception.
     * @param bogusGarnettTypeName the name of the Garnett type which unknown to the registry.
     */

    public GarnettObjectVersionNotSupportedException(
            String why,
            GarnettObjectRestorerRegistry garnettObjectRestorerRegistry,
            GarnettTypeName bogusGarnettTypeName
    ) {
        super( garnettObjectRestorerRegistry, bogusGarnettTypeName, why );

    }

}
