package com.obtuse.wire;

/*
 * Copyright © 2011 Daniel Boulet.
 */

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * How a Benoit object output stream behaves.
 */

@SuppressWarnings({ "UnusedDeclaration" })
public interface BenoitObjectOutputStreamInterface extends Closeable {

    void close()
            throws IOException;

    void drain()
            throws IOException;

    void flush()
            throws IOException;

    void reset()
            throws IOException;

    void writeVersion( int version )
            throws IOException;

    void writeOptionalBoolean( Boolean val )
            throws IOException;

    void writeBoolean( boolean val )
            throws IOException;

    void writeOptionalByte( Byte val )
            throws IOException;

    void writeByte( byte val )
            throws IOException;

    void writeOptionalString( String val )
            throws IOException;

    void writeString( String val )
            throws IOException;

    void writeOptionalCharacter( Character val )
            throws IOException;

    void writeChar( char val )
            throws IOException;

    void writeOptionalDouble( Double val )
            throws IOException;

    void writeDouble( double val )
            throws IOException;

    void writeOptionalFloat( Float val )
            throws IOException;

    void writeFloat( float val )
            throws IOException;

    void writeOptionalInteger( Integer val )
            throws IOException;

    void writeInt( int val )
            throws IOException;

    void writeOptionalLong( Long val )
            throws IOException;

    void writeLong( long val )
            throws IOException;

    void writeOptionalShort( Short val )
            throws IOException;

    void writeShort( short val )
            throws IOException;

    void writeOptionalBenoitObject( BenoitObject obj )
            throws IOException;

    void writeMandatoryBenoitObject( BenoitObject obj )
            throws IOException;

    void writeOptionalInetAddress( InetAddress val )
            throws IOException;

    void writeInetAddress( InetAddress val )
            throws IOException;

    void writeOptionalInetSocketAddress( InetSocketAddress val )
            throws IOException;

    void writeInetSocketAddress( InetSocketAddress val )
            throws IOException;

    void needRoom( int neededRoom )
            throws IOException;
}
