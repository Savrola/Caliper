package com.obtuse.garnett.exceptions;

/*
 * Copyright © 2011 Daniel Boulet.
 */

import com.obtuse.garnett.GarnettObjectRestorerRegistry;
import com.obtuse.garnett.GarnettTypeName;

import java.io.IOException;

/**
 * Helper class for the various GarnettObjectRestorerRegistry-aware Garnett exceptions.
 */

public abstract class GarnettTypeRegistryException extends IOException {

    private final GarnettObjectRestorerRegistry _garnettObjectRestorerRegistry;
    private final GarnettTypeName _garnettTypeName;

    protected GarnettTypeRegistryException() {
        super();

        _garnettObjectRestorerRegistry = null;

        _garnettTypeName = null;

    }

    protected GarnettTypeRegistryException(
            GarnettObjectRestorerRegistry garnettObjectRestorerRegistry,
            GarnettTypeName garnettTypeName
    ) {
        super();

        _garnettObjectRestorerRegistry = garnettObjectRestorerRegistry;

        _garnettTypeName = garnettTypeName;
        
    }

    protected GarnettTypeRegistryException(
            GarnettObjectRestorerRegistry garnettObjectRestorerRegistry,
            GarnettTypeName garnettTypeName,
            String message
    ) {
        super( message );
        
        _garnettObjectRestorerRegistry = garnettObjectRestorerRegistry;

        _garnettTypeName = garnettTypeName;

    }

    protected GarnettTypeRegistryException(
            GarnettObjectRestorerRegistry garnettObjectRestorerRegistry,
            GarnettTypeName garnettTypeName,
            String message,
            Throwable cause
    ) {
        super( message, cause );

        _garnettObjectRestorerRegistry = garnettObjectRestorerRegistry;

        _garnettTypeName = garnettTypeName;

    }

    protected GarnettTypeRegistryException(
            GarnettObjectRestorerRegistry garnettObjectRestorerRegistry,
            GarnettTypeName garnettTypeName,
            Throwable cause
    ) {
        super( cause );

        _garnettObjectRestorerRegistry = garnettObjectRestorerRegistry;

        _garnettTypeName = garnettTypeName;

    }

    public GarnettObjectRestorerRegistry getGarnettObjectRestorerRegistry() {

        return _garnettObjectRestorerRegistry;

    }

    public GarnettTypeName getGarnettTypeName() {

        return _garnettTypeName;

    }

}
