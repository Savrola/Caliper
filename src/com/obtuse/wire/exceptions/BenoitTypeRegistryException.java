package com.obtuse.wire.exceptions;

/*
 * Copyright © 2011 Daniel Boulet.
 */

import com.obtuse.wire.BenoitObjectRestorerRegistry;
import com.obtuse.wire.BenoitTypeName;

import java.io.IOException;

/**
 * Helper class for the various BenoitObjectRestorerRegistry-aware Benoit exceptions.
 */

public abstract class BenoitTypeRegistryException extends IOException {

    private final BenoitObjectRestorerRegistry _benoitObjectRestorerRegistry;
    private final BenoitTypeName _benoitTypeName;

    protected BenoitTypeRegistryException() {
        super();

        _benoitObjectRestorerRegistry = null;

        _benoitTypeName = null;

    }

    protected BenoitTypeRegistryException( BenoitObjectRestorerRegistry benoitObjectRestorerRegistry, BenoitTypeName benoitTypeName ) {
        super();

        _benoitObjectRestorerRegistry = benoitObjectRestorerRegistry;

        _benoitTypeName = benoitTypeName;
        
    }

    protected BenoitTypeRegistryException( BenoitObjectRestorerRegistry benoitObjectRestorerRegistry, BenoitTypeName benoitTypeName, String message ) {
        super( message );
        
        _benoitObjectRestorerRegistry = benoitObjectRestorerRegistry;

        _benoitTypeName = benoitTypeName;

    }

    protected BenoitTypeRegistryException( BenoitObjectRestorerRegistry benoitObjectRestorerRegistry, BenoitTypeName benoitTypeName, String message, Throwable cause ) {
        super( message, cause );

        _benoitObjectRestorerRegistry = benoitObjectRestorerRegistry;

        _benoitTypeName = benoitTypeName;

    }

    protected BenoitTypeRegistryException( BenoitObjectRestorerRegistry benoitObjectRestorerRegistry, BenoitTypeName benoitTypeName, Throwable cause ) {
        super( cause );

        _benoitObjectRestorerRegistry = benoitObjectRestorerRegistry;

        _benoitTypeName = benoitTypeName;

    }

    public BenoitObjectRestorerRegistry getBenoitObjectRestorerRegistry() {

        return _benoitObjectRestorerRegistry;

    }

    public BenoitTypeName getBenoitTypeName() {

        return _benoitTypeName;

    }

}
