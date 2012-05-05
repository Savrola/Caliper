package com.obtuse.garnett;

/*
 * Copyright © 2011 Daniel Boulet.
 */

import com.obtuse.garnett.exceptions.GarnettDeserializationFailedException;
import com.obtuse.garnett.exceptions.GarnettObjectVersionNotSupportedException;
import com.obtuse.util.ImmutableDate;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * How a Garnett object input stream behaves.
 */

@SuppressWarnings("UnusedDeclaration")
public interface GarnettObjectInputStreamInterface extends Closeable {

    void close()
            throws IOException;

    @SuppressWarnings("DuplicateThrows")
    int checkVersion( Class<? extends GarnettObject> garnettObjectClass, int oldestSupportedVersion, int newestSupportedVersion )
            throws IOException, GarnettObjectVersionNotSupportedException;

    Boolean readOptionalBoolean()
            throws IOException;

    Byte readOptionalByte()
            throws IOException;

    byte[] readOptionalByteArray()
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

    GarnettObject readOptionalGarnettObject()
            throws IOException;

    GarnettObject[] readGarnettObjectArray()
            throws IOException;

    GarnettObject[] readOptionalGarnettObjectArray()
            throws IOException;

    @SuppressWarnings("DuplicateThrows")
    boolean readBoolean()
            throws IOException, GarnettDeserializationFailedException;

    byte readByte()
            throws IOException;

    byte[] readByteArray()
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

    GarnettObject readGarnettObject()
            throws IOException;

    InetAddress readOptionalInetAddress()
            throws IOException;

    InetAddress readInetAddress()
            throws IOException;

    InetSocketAddress readOptionalInetSocketAddress()
            throws IOException;

    InetSocketAddress readInetSocketAddress()
            throws IOException;

    ImmutableDate readOptionalDate()
            throws IOException;

    ImmutableDate readDate()
            throws IOException;

    int needBytes( int neededRoom )
            throws IOException;

    GarnettObjectRestorerRegistry getRestorerRegistry();

    GarnettSessionPrefix getSessionPrefix();
}