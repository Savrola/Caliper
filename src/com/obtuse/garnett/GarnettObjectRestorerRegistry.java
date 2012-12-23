package com.obtuse.garnett;

/*
 * Copyright © 2011 Daniel Boulet.
 */

import com.obtuse.garnett.exceptions.*;

import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Manage the various {@link com.obtuse.garnett.GarnettObject}s which a {@link GarnettObjectInputStreamInterface} must be able to instantiate.
 */

@SuppressWarnings("SameParameterValue")
public class GarnettObjectRestorerRegistry {

    private final SortedMap<GarnettTypeName,GarnettObjectFactory> _factories = new TreeMap<GarnettTypeName, GarnettObjectFactory>();
    private final String _registryName;

    public interface GarnettObjectFactory {

        GarnettObject instantiateInstance( GarnettObjectInputStreamInterface garnettObjectInputStream )
                throws GarnettSerializationFailedException, IOException, GarnettObjectVersionNotSupportedException;

    }

    public GarnettObjectRestorerRegistry( String registryName ) {
        super();

        _registryName = registryName;

    }

    public GarnettObject instantiateInstance(
            GarnettObjectInputStreamInterface garnettObjectInputStream,
            GarnettTypeName registeredGarnettTypeName
    )
            throws GarnettSerializationFailedException, IOException, GarnettObjectVersionNotSupportedException {

        GarnettObjectFactory factory = _factories.get( registeredGarnettTypeName );

        if ( factory == null ) {

            throw new GarnettTypeNameUnknownException(
                    "GarnettObjectRestorerRegistry(\"" + _registryName + "\"):  " +
                    "type \"" + registeredGarnettTypeName + "\" not registered with this registry",
                    this,
                    registeredGarnettTypeName
            );

        }

        return factory.instantiateInstance( garnettObjectInputStream );

    }

    public void addGarnettObjectFactory( GarnettTypeName garnettTypeName, GarnettObjectFactory garnettObjectFactory )
            throws GarnettTypeNameAlreadyRegisteredException, GarnettTypeNameInvalidException {

        if ( _factories.containsKey( garnettTypeName ) ) {

            throw new GarnettTypeNameAlreadyRegisteredException( "type \"" + garnettTypeName + "\" already registered", this,

                                                                 garnettTypeName
            );

        } else if ( GarnettTypeName.UNKNOWN.equals( garnettTypeName ) ) {

            throw new GarnettTypeNameInvalidException(
                    "type name \"" + garnettTypeName + "\" is not allowed in a Garnett object restorer registry",
                    this,
                    garnettTypeName
            );

        } else {

            _factories.put( garnettTypeName, garnettObjectFactory );

        }

    }

    public String getRegistryName() {

        return _registryName;

    }

}
