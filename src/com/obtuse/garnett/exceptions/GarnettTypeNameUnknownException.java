package com.obtuse.garnett.exceptions;

/*
 * Copyright © 2011 Daniel Boulet.
 */

import com.obtuse.garnett.GarnettObjectRestorerRegistry;
import com.obtuse.garnett.GarnettTypeName;

/**
 * Thrown if a {@link com.obtuse.garnett.GarnettObjectRestorerRegistry} if asked to instantiate an instance of a Garnett type whose
 * type name has never been registered with the registry.
 */

public class GarnettTypeNameUnknownException extends GarnettTypeRegistryException {

    public GarnettTypeNameUnknownException() {
        super();

    }

    /**
     * Create a fully-described exception instance.
     * @param why what went wrong in human-readable form.
     * @param garnettObjectRestorerRegistry the registry which threw this exception.
     * @param bogusGarnettTypeName the name of the Garnett type which unknown to the registry.
     */

    public GarnettTypeNameUnknownException(
            String why,
            GarnettObjectRestorerRegistry garnettObjectRestorerRegistry,
            GarnettTypeName bogusGarnettTypeName
    ) {
        super( garnettObjectRestorerRegistry, bogusGarnettTypeName, why );

    }

}
