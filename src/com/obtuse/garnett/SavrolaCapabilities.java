package com.obtuse.garnett;

/*
 * Copyright © 2012 Daniel Boulet
 */

import java.io.IOException;
import java.util.*;

@SuppressWarnings( { "ClassWithoutToString" } )
public class SavrolaCapabilities implements GarnettObject {

    public static final int VERSION = 1;

    private final SortedMap<String, SavrolaCapability> _universalSavrolaCapabilitiesByName =
            new TreeMap<String, SavrolaCapability>();

    private final SortedMap<Integer, SavrolaCapability> _universalSavrolaCapabilitiesById =
            new TreeMap<Integer, SavrolaCapability>();

//    private final SortedMap<String, UniversalSavrolaAppCapability> _universalSavrolaAppCapabilitiesByName =
//            new TreeMap<String, UniversalSavrolaAppCapability>();
//
//    private final SortedMap<Integer, UniversalSavrolaAppCapability> _universalSavrolaAppCapabilitiesById =
//            new TreeMap<Integer, UniversalSavrolaAppCapability>();

    @SuppressWarnings("UnusedDeclaration")
    public SavrolaCapabilities(
            SortedMap<String, SavrolaCapability> savrolaCapabilities
    ) {
        super();

        addCapabilities( savrolaCapabilities );

    }

    @SuppressWarnings("UnusedDeclaration")
    public SavrolaCapabilities( GarnettObjectInputStreamInterface gois )
            throws IOException {
        super();

        gois.checkVersion(
                SavrolaCapabilities.class,
                SavrolaCapabilities.VERSION,
                SavrolaCapabilities.VERSION
        );

        int count = gois.readInt();
        for ( int i = 0; i < count; i += 1 ) {

            addCapability( SavrolaCapability.valueOf( gois.readString() ) );

        }

    }

    @SuppressWarnings("UnusedDeclaration")
    public String[] getAllCapabilityNames() {

        SortedSet<String> names = new TreeSet<String>();
        for ( String name : _universalSavrolaCapabilitiesByName.keySet() ) {

            names.add( name );

        }

        return names.toArray( new String[names.size()] );

    }

    @SuppressWarnings("UnusedDeclaration")
    public boolean hasSavrolaCapability( String capabilityName ) {

        if ( !capabilityName.startsWith( "SAVROLA_" ) ) {

            throw new IllegalArgumentException(
                    "purported Savrola capability name doesn't start with SAVROLA_ (name was \"" + capabilityName + "\")"
            );

        }

        return _universalSavrolaCapabilitiesByName.containsKey( capabilityName );

    }

    @SuppressWarnings("UnusedDeclaration")
    public boolean hasSavrolaCapability( int capabilityId ) {

        return _universalSavrolaCapabilitiesById.containsKey( capabilityId );

    }

    public final void addCapability( SavrolaCapability savrolaCapability ) {

        _universalSavrolaCapabilitiesByName.put( savrolaCapability.name(), savrolaCapability );
        _universalSavrolaCapabilitiesById.put( savrolaCapability.ordinal(), savrolaCapability );

    }

    public final void addCapabilities( SortedMap<String, SavrolaCapability> capabilities ) {

        for ( SavrolaCapability capability : capabilities.values() ) {

            addCapability( capability );

        }

    }

    @SuppressWarnings("UnusedDeclaration")
    boolean hasCapability( SavrolaCapability savrolaCapability ) {

        return _universalSavrolaCapabilitiesByName.containsKey( savrolaCapability.name() );

    }

    public GarnettTypeName getGarnettTypeName() {

        return new GarnettTypeName( SavrolaCapabilities.class.getCanonicalName() );

    }

    public void serializeContents( GarnettObjectOutputStreamInterface goos )
            throws IOException {

        goos.writeVersion( SavrolaCapabilities.VERSION );

        goos.writeInt( _universalSavrolaCapabilitiesById.size() );
        for ( SavrolaCapability capability : _universalSavrolaCapabilitiesById.values() ) {

            goos.writeString( capability.name() );

        }

    }

}
