package com.obtuse.garnett;

/*
 * Copyright © 2012 Daniel Boulet
 */

import java.util.Arrays;

/**
 * The name of something that can be the target of a {@link com.obtuse.garnett.client.GarnettClientSession}.
 * <p/>
 * Instances of this class are immutable.
 * <p/>
 * This class exists to make it harder to accidentally provide the wrong component instance name
 * by allowing for the easy creation of strongly typed component instance name constants.
 */

public class GarnettComponentInstanceName {

    public static final int MAX_INSTANCE_NAME_LENGTH = 256;

    private final String _instanceName;
    private final byte[] _instanceNameBytes;

    /**
     * Define a component instance's name.
     *
     * @param instanceName the component instance's name.
     * @throws IllegalArgumentException if the byte array representation of the component instance's
     * name exceeds 256 bytes (certain uses of component instance names assume that the length can
     * be represented in a byte).
     */

    public GarnettComponentInstanceName( String instanceName ) {
        super();

        _instanceName = instanceName;
        _instanceNameBytes = instanceName.getBytes();

        if ( _instanceNameBytes.length > GarnettComponentInstanceName.MAX_INSTANCE_NAME_LENGTH ) {

            throw new IllegalArgumentException(
                    "component instance name too long (max length in bytes is " +
                    GarnettComponentInstanceName.MAX_INSTANCE_NAME_LENGTH +", " +
                    "this name's length is " + _instanceNameBytes.length + "," +
                    " this name is \"" + _instanceName + "\")"
            );

        } else if ( _instanceNameBytes.length == 0 ) {

            throw new IllegalArgumentException(
                    "component instance name is zero bytes long"
            );

        }

    }

    public String getInstanceName() {

        return _instanceName;

    }

    public byte[] getInstanceNameBytes() {

        return Arrays.copyOf( _instanceNameBytes, _instanceNameBytes.length );

    }

    public int getInstanceNameBytesLength() {

        return _instanceNameBytes.length;

    }

    public String toString() {

        return "[[" + _instanceName + "]]";

    }

}
