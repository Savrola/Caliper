package com.obtuse.wire.exceptions;

/*
 * Copyright © 2011 Daniel Boulet.
 */

import com.obtuse.wire.BenoitObjectRestorerRegistry;
import com.obtuse.wire.BenoitTypeName;

/**
 * Thrown if a {@link BenoitObjectRestorerRegistry} if asked to instantiate an instance of a Benoit type whose
 * type name has never been registered with the registry.
 */

public class BenoitTypeNameInvalidException extends BenoitTypeRegistryException {

    public BenoitTypeNameInvalidException() {
        super();

    }

    /**
     * Create a fully-described exception instance.
     * @param why what went wrong in human-readable form.
     * @param benoitObjectRestorerRegistry the registry which threw this exception.
     * @param bogusBenoitTypeName the name of the Benoit type which unknown to the registry.
     */

    public BenoitTypeNameInvalidException(
            String why,
            BenoitObjectRestorerRegistry benoitObjectRestorerRegistry,
            BenoitTypeName bogusBenoitTypeName
    ) {
        super( benoitObjectRestorerRegistry, bogusBenoitTypeName, why );

    }

}
