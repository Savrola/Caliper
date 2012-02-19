package com.obtuse.wire;

/*
 * Copyright © 2011 Daniel Boulet.
 */

import com.obtuse.wire.exceptions.*;

import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Manage the various {@link com.obtuse.benoit2.util.BenoitObject}s which a {@link BenoitObjectInputStreamInterface} must be able to instantiate.
 */

public class BenoitObjectRestorerRegistry {

    private final SortedMap<BenoitTypeName,BenoitObjectFactory> _factories = new TreeMap<BenoitTypeName, BenoitObjectFactory>();
    private final String _registryName;

    public interface BenoitObjectFactory {

        BenoitObject instantiateInstance( BenoitObjectInputStreamInterface benoitObjectInputStream )
                throws BenoitSerializationFailedException, IOException, BenoitObjectVersionNotSupportedException;

    }

    public BenoitObjectRestorerRegistry( String registryName ) {
        super();

        _registryName = registryName;

    }

    public BenoitObject instantiateInstance(
            BenoitObjectInputStreamInterface benoitObjectInputStream,
            BenoitTypeName registeredBenoitTypeName
    )
            throws BenoitSerializationFailedException, IOException, BenoitObjectVersionNotSupportedException {

        BenoitObjectFactory factory = _factories.get( registeredBenoitTypeName );

        if ( factory == null ) {

            throw new BenoitTypeNameUnknownException(
                    "BenoitObjectRestorerRegistry(\"" + _registryName + "\"):  " +
                    "type \"" + registeredBenoitTypeName + "\" not registered with this registry",
                    this,
                    registeredBenoitTypeName
            );

        }

        return factory.instantiateInstance( benoitObjectInputStream );

    }

    public void addBenoitObjectFactory( BenoitTypeName benoitTypeName, BenoitObjectFactory benoitObjectFactory )
            throws BenoitTypeNameAlreadyRegisteredException, BenoitTypeNameInvalidException {

        if ( _factories.containsKey( benoitTypeName ) ) {

            throw new BenoitTypeNameAlreadyRegisteredException( "type \"" + benoitTypeName + "\" already registered", this, benoitTypeName );

        } else if ( BenoitTypeName.UNKNOWN.equals( benoitTypeName ) ) {

            throw new BenoitTypeNameInvalidException(
                    "type name \"" + benoitTypeName + "\" is not allowed in a Benoit object restorer registry",
                    this,
                    benoitTypeName
            );

        } else {

            _factories.put( benoitTypeName, benoitObjectFactory );

        }

    }

    public String getRegistryName() {

        return _registryName;

    }

}
