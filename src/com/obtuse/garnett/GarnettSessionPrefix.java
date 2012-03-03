package com.obtuse.garnett;

/*
 * Copyright © 2012 Daniel Boulet
 */

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.garnett.exceptions.GarnettIllegalArgumentException;
import com.obtuse.garnett.exceptions.GarnettUnsupportedProtocolVersionException;
import com.obtuse.util.ObtuseUtil5;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Manage session prefixes.
 * <p/>
 * Instances of this class are immutable.
 */

public class GarnettSessionPrefix implements Comparable<GarnettSessionPrefix> {

    private final byte _protocolVersionCode;

    private final GarnettComponentInstanceName _componentInstanceName;

    private final int _podNumber;

    private final byte[] _sessionPrefix;

    private final String _toString;

    /**
     * Construct a session prefix from a component instance's name and its pod number.
     * @param componentInstanceName the component instance's name.
     * @param podNumber the component instance's pod number.
     */

    public GarnettSessionPrefix( GarnettComponentInstanceName componentInstanceName, int podNumber ) {
        super();

        _protocolVersionCode = GarnettSession.PROXIED_PROTOCOL_VERSION_CODE;
        _componentInstanceName = componentInstanceName;
        _podNumber = podNumber;

        ByteBuffer bb = ByteBuffer.allocate( 100 + componentInstanceName.getInstanceNameBytesLength() );

        bb.put( _protocolVersionCode );
        bb.putInt( podNumber );
        bb.put( (byte)( componentInstanceName.getInstanceNameBytesLength() - 1 ) );
        bb.put( componentInstanceName.getInstanceNameBytes() );

        _sessionPrefix = new byte[bb.position()];

        bb.flip();
        bb.get( _sessionPrefix );

        _toString = makeToString();

        // Verify that the prefix is completely valid by trying to parse it.

        try {

            GarnettSessionPrefix testPrefix = new GarnettSessionPrefix( _sessionPrefix );
            if ( !equals( testPrefix ) ) {

                throw new HowDidWeGetHereError( "constructed GSP (" + toString() + ") does not match parsed GSP (" + testPrefix.toString() + ")" );

            }

        } catch ( Throwable e ) {

            throw new HowDidWeGetHereError( "caught an exception constructing parsed GSP from constructed GSP (" + toString() + ")", e );

        }

    }

    /**
     * Create a session prefix by parsing a session prefix byte array.
     * @param sessionPrefix the session prefix byte array.
     * @throws GarnettUnsupportedProtocolVersionException if the session prefix byte array contains an unsupported
     * protocol version code.
     * @throws GarnettIllegalArgumentException if the session prefix byte array is otherwise invalid (too short,
     * too long, negative pod number or a zero length component instance name).
     */

    public GarnettSessionPrefix( byte[] sessionPrefix )
            throws GarnettIllegalArgumentException, GarnettUnsupportedProtocolVersionException {
        super();

        _sessionPrefix = sessionPrefix;

        try {

            ByteBuffer bb = ByteBuffer.wrap( sessionPrefix );

            _protocolVersionCode = bb.get();
            if ( _protocolVersionCode != GarnettSession.PROXIED_PROTOCOL_VERSION_CODE ) {

                throw new GarnettUnsupportedProtocolVersionException(
                        "protocol version code 0x" +
                        ObtuseUtil5.hexvalue( (byte)_protocolVersionCode ) +
                        " is not supported"
                );
                
            }
            
            _podNumber = bb.getInt();
            
            int nameLen = ( bb.get() & 0xff ) + 1;
            
            byte[] componentInstanceNameBytes = new byte[nameLen];
            bb.get( componentInstanceNameBytes );

            _componentInstanceName = new GarnettComponentInstanceName( new String( componentInstanceNameBytes ) );

            if ( bb.position() != bb.limit() ) {

                throw new GarnettIllegalArgumentException( "session prefix contains unknown junk at the end" );

            }

        } catch ( BufferUnderflowException e ) {

            throw new GarnettIllegalArgumentException( "session prefix too short", e );

        }

        _toString = makeToString();

    }

    private String makeToString() {

        return "GSP( 0x" + ObtuseUtil5.hexvalue( (byte)_protocolVersionCode ) + ", " +
                    _podNumber + ", " + _componentInstanceName + " )";

    }

    public byte getProtocolVersionCode() {

        return _protocolVersionCode;

    }

    public GarnettComponentInstanceName getComponentInstanceName() {

        return _componentInstanceName;

    }

    public int getPodNumber() {

        return _podNumber;

    }

    public byte[] getSessionPrefixBytes() {

        return Arrays.copyOf( _sessionPrefix, _sessionPrefix.length );

    }

    public boolean equals( Object rhs ) {

        return rhs instanceof GarnettSessionPrefix && _toString.equals( ((GarnettSessionPrefix)rhs).toString() );

    }

    public int hashCode() {

        return _toString.hashCode();

    }

    public String toString() {

        return _toString;

    }

    public int compareTo( GarnettSessionPrefix rhs ) {

        return _toString.compareTo( rhs.toString() );

    }

    public static void main( String[] args ) {

        GarnettSessionPrefix gsp = new GarnettSessionPrefix( new GarnettComponentInstanceName( "hello world" ), 17 );

        System.exit( 0 );
    }


}
