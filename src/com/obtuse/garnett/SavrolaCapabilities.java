package com.obtuse.garnett;

/*
 * Copyright © 2012 Daniel Boulet
 */

import java.io.IOException;
import java.util.*;

@SuppressWarnings( { "ClassWithoutToString" } )
public class SavrolaCapabilities implements GarnettObject {

    public static final GarnettTypeName SAVROLA_CAPABILITIES_NAME = new GarnettTypeName(
            SavrolaCapabilities.class.getCanonicalName()
    );

    public static int VERSION = 1;

    private final SortedMap<String, SavrolaCapability> _universalSavrolaCapabilitiesByName =
            new TreeMap<String, SavrolaCapability>();

    private final SortedMap<Integer, SavrolaCapability> _universalSavrolaCapabilitiesById =
            new TreeMap<Integer, SavrolaCapability>();

//    private final SortedMap<String, UniversalSavrolaAppCapability> _universalSavrolaAppCapabilitiesByName =
//            new TreeMap<String, UniversalSavrolaAppCapability>();
//
//    private final SortedMap<Integer, UniversalSavrolaAppCapability> _universalSavrolaAppCapabilitiesById =
//            new TreeMap<Integer, UniversalSavrolaAppCapability>();

    private static final byte UNIVERSALCAPABILITIES_FORMAT_VERSION = (byte)1;

    public SavrolaCapabilities() {
        super();

    }

    public SavrolaCapabilities(
            SortedMap<String, SavrolaCapability> savrolaCapabilities
    ) {
        super();

        addCapabilities( savrolaCapabilities );

    }

    public SavrolaCapabilities(
            GarnettObjectInputStreamInterface gois
    )
            throws IOException {
        super();

        gois.checkVersion(
                SAVROLA_CAPABILITIES_NAME,
                VERSION,
                VERSION
        );

        int count = gois.readInt();
        for ( int i = 0; i < count; i += 1 ) {

            addCapability( SavrolaCapability.valueOf( gois.readString() ) );

        }

    }

    public String[] getAllCapabilityNames() {

        SortedSet<String> names = new TreeSet<String>();
        for ( String name : _universalSavrolaCapabilitiesByName.keySet() ) {

            names.add( name );

        }

        return names.toArray( new String[names.size()] );

    }

    public boolean hasSavrolaCapability( String capabilityName ) {

        if ( !capabilityName.startsWith( "SAVROLA_" ) ) {

            throw new IllegalArgumentException(
                    "purported Savrola capability name doesn't start with SAVROLA_ (name was \"" + capabilityName + "\")"
            );

        }

        return _universalSavrolaCapabilitiesByName.containsKey( capabilityName );

    }

    public boolean hasSavrolaCapability( int capabilityId ) {

        return _universalSavrolaCapabilitiesById.containsKey( capabilityId );

    }

    public void addCapability( SavrolaCapability savrolaCapability ) {

        _universalSavrolaCapabilitiesByName.put( savrolaCapability.name(), savrolaCapability );
        _universalSavrolaCapabilitiesById.put( savrolaCapability.ordinal(), savrolaCapability );

    }

    public void addCapabilities( SortedMap<String, SavrolaCapability> capabilities ) {

        for ( SavrolaCapability capability : capabilities.values() ) {

            addCapability( capability );

        }

    }

    boolean hasCapability( SavrolaCapability savrolaCapability ) {

        return _universalSavrolaCapabilitiesByName.containsKey( savrolaCapability.name() );

    }

    public GarnettTypeName getGarnettTypeName() {

        return SAVROLA_CAPABILITIES_NAME;

    }

    public void serializeContents( GarnettObjectOutputStreamInterface goos )
            throws IOException {

        goos.writeVersion( VERSION );

        goos.writeInt( _universalSavrolaCapabilitiesById.size() );
        for ( SavrolaCapability capability : _universalSavrolaCapabilitiesById.values() ) {

            goos.writeString( capability.name() );

        }

    }

}
