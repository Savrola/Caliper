package com.obtuse.wire;

/*
 * Copyright © 2011 Daniel Boulet.
 */

import com.obtuse.wire.exceptions.BenoitDeserializationFailedException;
import com.obtuse.wire.exceptions.BenoitObjectVersionNotSupportedException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * How a Benoit object input stream behaves.
 */

public interface BenoitObjectInputStreamInterface {

    void close()
            throws IOException;

    int checkVersion( BenoitTypeName benoitTypeName, int oldestSupportedVersion, int newestSupportedVersion )
            throws IOException, BenoitObjectVersionNotSupportedException;

    Boolean readOptionalBoolean()
            throws IOException;

    Byte readOptionalByte()
            throws IOException;

    String readOptionalString()
            throws IOException;

    Character readOptionalCharacter()
            throws IOException;

    Double readOptionalDouble()
            throws IOException;

    Float readOptionalFloat()
            throws IOException;

    Integer readOptionalInteger()
            throws IOException;

    Long readOptionalLong()
            throws IOException;

    Short readOptionalShort()
            throws IOException;

    BenoitObject readOptionalBenoitObject()
            throws IOException;

    boolean readBoolean()
            throws IOException, BenoitDeserializationFailedException;

    byte readByte()
            throws IOException;

    String readString()
            throws IOException;

    char readChar()
            throws IOException;

    double readDouble()
            throws IOException;

    float readFloat()
            throws IOException;

    int readInt()
            throws IOException;

    long readLong()
            throws IOException;

    short readShort()
            throws IOException;

    BenoitObject readBenoitObject()
            throws IOException;

    InetAddress readOptionalInetAddress()
            throws IOException;

    InetAddress readInetAddress()
            throws IOException;

    InetSocketAddress readOptionalInetSocketAddress()
            throws IOException;

    InetSocketAddress readInetSocketAddress()
            throws IOException;

    int needBytes( int neededRoom )
            throws IOException;

    BenoitObjectRestorerRegistry getRestorerRegistry();
}