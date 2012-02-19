package com.obtuse.wire;

/*
 * Copyright © 2011 Daniel Boulet.
 */

import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil5;
import com.obtuse.util.exceptions.HowDidWeGetHereError;
import com.obtuse.wire.exceptions.BenoitDeserializationFailedException;
import com.obtuse.wire.exceptions.BenoitObjectVersionNotSupportedException;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Deserialize Benoit data from an input stream.
 */

public class BenoitObjectInputStream extends InputStream implements BenoitObjectInputStreamInterface {

    private static byte[] generateExpectedTags( byte mandatoryTag ) {

        return new byte[] {
                mandatoryTag,
                (byte)( (int)mandatoryTag | (int)BenoitConstants.MISSING_TAG_MODIFIER )
        };

    }

    private static final byte[] EXPECTED_BOOLEAN_TAGS = generateExpectedTags( BenoitConstants.BOOLEAN_TAG );
    private static final byte[] EXPECTED_BYTE_TAGS = generateExpectedTags( BenoitConstants.BYTE_TAG );
    private static final byte[] EXPECTED_STRING_TAGS = generateExpectedTags( BenoitConstants.STRING_TAG );
    private static final byte[] EXPECTED_CHAR_TAGS = generateExpectedTags( BenoitConstants.CHAR_TAG );
    private static final byte[] EXPECTED_DOUBLE_TAGS = generateExpectedTags( BenoitConstants.DOUBLE_TAG );
    private static final byte[] EXPECTED_FLOAT_TAGS = generateExpectedTags( BenoitConstants.FLOAT_TAG );
    private static final byte[] EXPECTED_INTEGER_TAGS = generateExpectedTags( BenoitConstants.INT_TAG );
    private static final byte[] EXPECTED_LONG_TAGS = generateExpectedTags( BenoitConstants.LONG_TAG );
    private static final byte[] EXPECTED_SHORT_TAGS = generateExpectedTags( BenoitConstants.SHORT_TAG );
    private static final byte[] EXPECTED_INET_ADDRESS_TAGS = generateExpectedTags( BenoitConstants.INET_ADDRESS_TAG );
    private static final byte[] EXPECTED_INET_SOCKET_ADDRESS_TAGS = generateExpectedTags( BenoitConstants.INET_SOCKET_ADDRESS_TAG );
    private static final byte[] EXPECTED_OPTIONAL_BENOIT_OBJECT_TAGS =
            new byte[] {
                    BenoitConstants.BENOIT_OBJECT_TAG,
                    BenoitConstants.MISSING_BENOIT_OBJECT_TAG,
                    BenoitConstants.FIRST_BENOIT_OBJECT_TAG
            };
    private static final byte[] EXPECTED_MANDATORY_BENOIT_OBJECT_TAGS =
            new byte[] {
                    BenoitConstants.BENOIT_OBJECT_TAG,
                    BenoitConstants.FIRST_BENOIT_OBJECT_TAG
            };

    private final int _maxBenoitObjectSize;
    private Integer _byteOffsetBeforeExtractingBenoitObject = null;
    private BenoitTypeName _topLevelBenoitTypeName;

    private int _totalBytesRead = 0;
    private final InputStream _inStream;
    private final ReadableByteChannel _inChannel;

    private int _serializationDepth = 0;

    private ByteBuffer _buffer;
    private boolean _done = false;

    private SortedMap<Integer,BenoitTypeName> _knownBenoitTypes;
    private final BenoitObjectRestorerRegistry _restorerRegistry;
    private int _nextBenoitTypeIndex = 0;

//    public static final int PRIMITIVE_TAG_SIZE = Byte.SIZE;
//    public static final int BENOIT_TYPE_INDEX_SIZE = Integer.SIZE / Byte.SIZE;
//    public static final int CHAR_SIZE    = Character.SIZE / Byte.SIZE;
//    public static final int SHORT_SIZE   = Short.SIZE / Byte.SIZE;
//    public static final int INTEGER_SIZE = Integer.SIZE / Byte.SIZE;
//    public static final int LONG_SIZE    = Long.SIZE / Byte.SIZE;
//    public static final int FLOAT_SIZE   = Float.SIZE / Byte.SIZE;
//    public static final int DOUBLE_SIZE  = Double.SIZE / Byte.SIZE;
//
//    public static final int MAX_BENOIT_TYPE_NAME_LENGTH = (int)Short.MAX_VALUE;

    // This is the largest value that we will ever call needRoom for.

    public static final int INTERNAL_BUFFER_SIZE = Math.max(
            ( (int)Short.MAX_VALUE + 1 ) * 2,       // 65536
            BenoitConstants.PRIMITIVE_TAG_SIZE + BenoitConstants.BENOIT_TYPE_INDEX_SIZE + BenoitConstants.SHORT_SIZE + BenoitConstants.MAX_BENOIT_TYPE_NAME_LENGTH // slightly over 32K
    );

    public BenoitObjectInputStream( int maxBenoitObjectSize, InputStream inStream, BenoitObjectRestorerRegistry restorerRegistry )
            throws IOException {

        super();

        _maxBenoitObjectSize = maxBenoitObjectSize;

        _inStream = inStream;

        _restorerRegistry = restorerRegistry;

        _inChannel = Channels.newChannel( inStream );

        _buffer = ByteBuffer.allocate( INTERNAL_BUFFER_SIZE );
        _buffer.clear();
        _buffer.flip();

        needBytes( BenoitConstants.INTEGER_SIZE );
        int magic = _buffer.getInt();
        if ( magic != BenoitConstants.BENOIT_OBJECT_STREAM_MAGIC_NUMBER ) {

            throw new IllegalArgumentException(
                    "BenoitObjectInputStream:  input stream does not start with 0x" +
                    ObtuseUtil5.hexvalue( BenoitConstants.BENOIT_OBJECT_STREAM_MAGIC_NUMBER )
            );

        }

        gotReset();

    }

    public BenoitObjectInputStream(
            InputStream inStream,
            BenoitObjectRestorerRegistry restorerRegistry
    )
            throws IOException {
        this( 0, inStream, restorerRegistry );

    }

    public BenoitObjectInputStream( InputStream inStream )
            throws IOException {
        this( 0, inStream );

    }

    public BenoitObjectInputStream( int maxBenoitObjectSize, InputStream inStream )
            throws IOException {
        this( maxBenoitObjectSize, inStream, new BenoitObjectRestorerRegistry( "anonymous BOR registry" ) );

    }

    @SuppressWarnings({ "RefusedBequest" })
    @Override
    public void close()
            throws IOException {

        int streamTerminator = -1;
        if ( _serializationDepth == 0 ) {

            streamTerminator = read();

        }

        _inStream.close();

        if ( _serializationDepth > 0 ) {

            throw new IOException( "Benoit input stream closed while de-serializing an object" );

        } else if ( streamTerminator == -1 ) {

            throw new IOException( "Benoit input stream not properly terminated (EOF encountered first)" );

        } else if ( streamTerminator != (int)BenoitConstants.END_OF_STREAM_TAG ) {

            throw new IOException(
                    "Benoit input stream not properly terminated (got 0x" +
                    ObtuseUtil5.hexvalue( (byte)streamTerminator ) +
                    " instead of expected 0x" +
                    ObtuseUtil5.hexvalue( (byte)BenoitConstants.END_OF_STREAM_TAG ) +
                    ")"
            );

        }

    }

    @Override
    public int read()
            throws IOException {

        if ( _done ) {

            return -1;

        }

        needBytes( 1 );
        
        if ( _buffer.hasRemaining() ) {

            return (int)_buffer.get();

        } else {

            return -1;

        }

    }

    public int getTotalBytesConsumed() {

        return _totalBytesRead - _buffer.remaining();

    }

    public int getTotalBytesRead() {

        return _totalBytesRead;

    }

    public int needBytes( int neededRoom )
            throws IOException {

        if ( _maxBenoitObjectSize > 0 && _byteOffsetBeforeExtractingBenoitObject != null ) {

            int objectSizeSoFar = getTotalBytesConsumed() - _byteOffsetBeforeExtractingBenoitObject.intValue();
            if ( objectSizeSoFar > _maxBenoitObjectSize ) {

                throw new BenoitDeserializationFailedException(
                        _topLevelBenoitTypeName,
                        "partial object size (" + objectSizeSoFar +
                        " bytes) exceeds limit (" + _maxBenoitObjectSize + " bytes)"
                );

            }

        }

        if ( neededRoom > _buffer.capacity() ) {

            throw new HowDidWeGetHereError( "request for more room than exists in our (empty) ByteBuffer" );

        }

        if ( neededRoom <= _buffer.remaining() ) {

            return _buffer.remaining();

        }

        _buffer.compact();
        int bytesReadThisCall = 0;
        while ( neededRoom > _buffer.position() ) {

            int bytesRead = _inChannel.read( _buffer );
            if ( bytesRead == 0 ) {

                Logger.logMsg( "BADNEWS:  needBytes failed to read any bytes on a read attempt" );
                throw new HowDidWeGetHereError( "needBytes failed to read any bytes on a read attempt" );

            } else if ( bytesRead < 0 ) {

                _done = true;
                _buffer.flip();

                return _buffer.remaining();

            }

            bytesReadThisCall += bytesRead;

        }

        _totalBytesRead += bytesReadThisCall;

        Logger.logMsg( "" + bytesReadThisCall + " bytes read by needBytes" );

        _buffer.flip();
        return _buffer.remaining();

    }

    private void gotReset()
            throws IOException {

        _knownBenoitTypes = new TreeMap<Integer, BenoitTypeName>();
        _nextBenoitTypeIndex = 0;

    }

    private byte expectPrimitiveTag( byte expectedTag )
            throws IOException {

        return expectPrimitiveTags( new byte[] { expectedTag } );

    }

    private byte expectPrimitiveTags( byte[] expectedTags )
            throws IOException {

        _serializationDepth += 1;
        try {

            while ( true ) {

                int nextByte = read();

                if ( nextByte == (int)BenoitConstants.RESET_TAG ) {

                    gotReset();


                } else {

                    for ( byte expectedTag : expectedTags ) {

                        if ( nextByte == (int)expectedTag ) {

                            return (byte)nextByte;

                        }

                    }

                    if ( nextByte == -1 ) {

                        throw new IOException(
                                "EOF encountered when trying to read expected primitive tag " +
                                formatPrimitiveTagValues( "value", "values", expectedTags )
                        );

                    } else {

                        throw new IOException(
                                "Expected primitive tag " +
                                formatPrimitiveTagValues( "value", "values", expectedTags ) +
                                " but got 0x" +
                                ObtuseUtil5.hexvalue( (byte)nextByte ) +
                                " instead"
                        );

                    }

                }

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    private String formatPrimitiveTagValues( String firstWord, String firstWordPluralForm, byte[] tagValues ) {

        StringBuilder rval = new StringBuilder();
        if ( firstWord != null ) {

            if ( tagValues.length > 1 ) {

                rval.append( firstWordPluralForm );

            } else {

                rval.append( firstWord );

            }

        }

        if ( tagValues.length != 1 ) {

            rval.append( " {" );

        }

        String comma = " 0x";
        for ( byte tagValue : tagValues ) {

            //noinspection MagicNumber
            rval.append( comma ).
                    append( "0123456789abcdef".charAt( ( (int)tagValue & 0xf0 ) >> 4 ) ).
                    append( "0123456789abcdef".charAt( (int)tagValue & 0x0f ) );

            comma = ", 0x";

        }

        if ( tagValues.length > 1 ) {

            rval.append( " }" );

        }

        return rval.toString();

    }

    private int readBenoitTypeIndex()
            throws IOException {

        _serializationDepth += 1;
        try {

            needBytes( BenoitConstants.BENOIT_TYPE_INDEX_SIZE );
            return _buffer.getInt();

        } finally {

            _serializationDepth -= 1;

        }

    }

    public int checkVersion( BenoitTypeName benoitTypeName, int oldestSupportedVersion, int newestSupportedVersion )
            throws IOException, BenoitObjectVersionNotSupportedException {

        _serializationDepth += 1;
        try {

            expectPrimitiveTag( BenoitConstants.VERSION_TAG );
            needBytes( BenoitConstants.INTEGER_SIZE );

            int version = _buffer.getInt();
            if ( version < oldestSupportedVersion || version > newestSupportedVersion ) {

                throw new BenoitObjectVersionNotSupportedException(
                        "version " + version + " found in stream for " + benoitTypeName + " not supported " +
                        "(oldest supported version is " + oldestSupportedVersion + ", " +
                        "newest supported version is " + newestSupportedVersion + ")",
                        _restorerRegistry,
                        benoitTypeName

                );

            }

            return version;

        } finally {

            _serializationDepth -= 1;

        }

    }

    public Boolean readOptionalBoolean()
            throws IOException {

        _serializationDepth += 1;
        try {

            byte tag = expectPrimitiveTags( EXPECTED_BOOLEAN_TAGS );
            if ( (int)tag == (int)BenoitConstants.BOOLEAN_TAG ) {

                needBytes( 1 );
                byte b = _buffer.get();

                if ( (int)b == 0 ) {

                    return Boolean.FALSE;

                } else if ( (int)b == 1 ) {

                    return Boolean.TRUE;

                } else {

                    throw new IOException(
                            "readOptionalBoolean:  expected 0x00 or 0x01 but got 0x" +
                            ObtuseUtil5.hexvalue( (byte)b )
                    );

                }

            } else {

                return null;

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public boolean readBoolean()
            throws IOException {

        _serializationDepth += 1;
        try {

            expectPrimitiveTag( BenoitConstants.BOOLEAN_TAG );

            needBytes( 1 );
            byte b = _buffer.get();

            if ( (int)b == 0 ) {

                return false;

            } else if ( (int)b == 1 ) {

                return true;

            } else {

                throw new IOException(
                        "readBoolean:  expected 0x00 or 0x01 but got 0x" +
                        ObtuseUtil5.hexvalue( (byte)b )
                );

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public Byte readOptionalByte()
            throws IOException {

        _serializationDepth += 1;
        try {

            byte tag = expectPrimitiveTags( EXPECTED_BYTE_TAGS );
            if ( (int)tag == (int)BenoitConstants.BYTE_TAG ) {

                needBytes( 1 );
                byte b = _buffer.get();

                return new Byte( b );

            } else {

                return null;

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public byte readByte()
            throws IOException {

        _serializationDepth += 1;
        try {

            expectPrimitiveTag( BenoitConstants.BYTE_TAG );

            needBytes( 1 );
            byte b = _buffer.get();

            return b;

        } finally {

            _serializationDepth -= 1;

        }

    }

    public String readOptionalString()
            throws IOException {

        _serializationDepth += 1;
        try {

            byte tag = expectPrimitiveTags( EXPECTED_STRING_TAGS );
            if ( (int)tag == (int)BenoitConstants.STRING_TAG ) {

                needBytes( BenoitConstants.INTEGER_SIZE );
                int stringLength = _buffer.getInt();

                byte[] stringBytes = new byte[stringLength];
                int totalBytesRead = 0;
                while ( totalBytesRead < stringLength ) {

                    int bytesRead = read( stringBytes, totalBytesRead, stringLength - totalBytesRead );
                    if ( bytesRead < 0 ) {

                        throw new IOException( "readOptionalString:  EOF reading string at offset (within string bytes) of " + totalBytesRead );

                    }

                    totalBytesRead += bytesRead;

                }

                return new String( stringBytes );

            } else {

                return null;

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public String readString()
            throws IOException {

        _serializationDepth += 1;
        try {

            expectPrimitiveTag( BenoitConstants.STRING_TAG );

            needBytes( BenoitConstants.INTEGER_SIZE );
            int stringLength = _buffer.getInt();

            byte[] stringBytes = new byte[stringLength];
            int totalBytesRead = 0;
            while ( totalBytesRead < stringLength ) {

                int bytesRead = read( stringBytes, totalBytesRead, stringLength - totalBytesRead );
                if ( bytesRead < 0 ) {

                    throw new IOException(
                            "readString:  EOF reading string at offset (within string bytes) of " +
                            totalBytesRead
                    );

                }

                totalBytesRead += bytesRead;

            }

            return new String( stringBytes );

        } finally {

            _serializationDepth -= 1;

        }

    }

    public Character readOptionalCharacter()
            throws IOException {

        _serializationDepth += 1;
        try {

            byte tag = expectPrimitiveTags( EXPECTED_CHAR_TAGS );
            if ( (int)tag == (int)BenoitConstants.CHAR_TAG ) {

                needBytes( BenoitConstants.CHAR_SIZE );
                char ch = _buffer.getChar();

                return new Character( ch );

            } else {

                return null;

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public char readChar()
            throws IOException {

        _serializationDepth += 1;
        try {

            expectPrimitiveTag( BenoitConstants.CHAR_TAG );

            needBytes( BenoitConstants.CHAR_SIZE );
            char ch = _buffer.getChar();

            return ch;

        } finally {

            _serializationDepth -= 1;

        }

    }

    public Double readOptionalDouble()
            throws IOException {

        _serializationDepth += 1;
        try {

            byte tag = expectPrimitiveTags( EXPECTED_DOUBLE_TAGS );
            if ( (int)tag == (int)BenoitConstants.DOUBLE_TAG ) {

                needBytes( BenoitConstants.DOUBLE_SIZE );
                double d = _buffer.getDouble();

                return new Double( d );

            } else {

                return null;

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public double readDouble()
            throws IOException {

        _serializationDepth += 1;
        try {

            expectPrimitiveTag( BenoitConstants.DOUBLE_TAG );

            needBytes( BenoitConstants.DOUBLE_SIZE );
            double d = _buffer.getDouble();

            return d;

        } finally {

            _serializationDepth -= 1;

        }

    }

    public Float readOptionalFloat()
            throws IOException {

        _serializationDepth += 1;
        try {

            byte tag = expectPrimitiveTags( EXPECTED_FLOAT_TAGS );
            if ( (int)tag == (int)BenoitConstants.FLOAT_TAG ) {

                needBytes( BenoitConstants.FLOAT_SIZE );
                float f = _buffer.getFloat();

                return new Float( f );

            } else {

                return null;

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public float readFloat()
            throws IOException {

        _serializationDepth += 1;
        try {

            expectPrimitiveTag( BenoitConstants.FLOAT_TAG );

            needBytes( BenoitConstants.FLOAT_SIZE );
            float f = _buffer.getFloat();

            return f;

        } finally {

            _serializationDepth -= 1;

        }

    }

    public Integer readOptionalInteger()
            throws IOException {

        _serializationDepth += 1;
        try {

            byte tag = expectPrimitiveTags( EXPECTED_INTEGER_TAGS );
            if ( (int)tag == (int)BenoitConstants.INT_TAG ) {

                needBytes( BenoitConstants.INTEGER_SIZE );
                int i = _buffer.getInt();

                return new Integer( i );

            } else {

                return null;

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public int readInt()
            throws IOException {

        _serializationDepth += 1;
        try {

            expectPrimitiveTag( BenoitConstants.INT_TAG );

            needBytes( BenoitConstants.INTEGER_SIZE );
            int i = _buffer.getInt();

            return i;

        } finally {

            _serializationDepth -= 1;

        }

    }

    public Long readOptionalLong()
            throws IOException {

        _serializationDepth += 1;
        try {

            byte tag = expectPrimitiveTags( EXPECTED_LONG_TAGS );
            if ( (int)tag == (int)BenoitConstants.LONG_TAG ) {

                needBytes( BenoitConstants.LONG_SIZE );
                long l = _buffer.getLong();

                return new Long( l );

            } else {

                return null;

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public long readLong()
            throws IOException {

        _serializationDepth += 1;
        try {

            expectPrimitiveTag( BenoitConstants.LONG_TAG );

            needBytes( BenoitConstants.LONG_SIZE );
            long l = _buffer.getLong();

            return l;

        } finally {

            _serializationDepth -= 1;

        }

    }

    public Short readOptionalShort()
            throws IOException {

        _serializationDepth += 1;
        try {

            byte tag = expectPrimitiveTags( EXPECTED_SHORT_TAGS );
            if ( (int)tag == (int)BenoitConstants.SHORT_TAG ) {

                needBytes( BenoitConstants.SHORT_SIZE );
                short sh = _buffer.getShort();

                return new Short( sh );

            } else {

                return null;

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public short readShort()
            throws IOException {

        _serializationDepth += 1;
        try {

            expectPrimitiveTag( BenoitConstants.SHORT_TAG );

            needBytes( BenoitConstants.SHORT_SIZE );
            short sh = _buffer.getShort();

            return sh;

        } finally {

            _serializationDepth -= 1;

        }

    }

    public InetAddress readOptionalInetAddress()
            throws IOException {

        _serializationDepth += 1;
        try {

            byte tag = expectPrimitiveTags( EXPECTED_INET_ADDRESS_TAGS );
            if ( (int)tag == (int)BenoitConstants.INET_ADDRESS_TAG ) {

                needBytes( 1 );
                int inetAddressLength = (int)_buffer.get();

                byte[] inetAddressBytes = new byte[inetAddressLength];
                needBytes( inetAddressLength );
                _buffer.get( inetAddressBytes );

                return InetAddress.getByAddress( inetAddressBytes );

            } else {

                return null;

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public InetAddress readInetAddress()
            throws IOException {

        _serializationDepth += 1;
        try {

            expectPrimitiveTag( BenoitConstants.INET_ADDRESS_TAG );

            needBytes( 1 );
            int inetAddressLength = (int)_buffer.get();

            byte[] inetAddressBytes = new byte[inetAddressLength];
            needBytes( inetAddressLength );
            _buffer.get( inetAddressBytes );

            return InetAddress.getByAddress( inetAddressBytes );

        } finally {

            _serializationDepth -= 1;

        }

    }

    public InetSocketAddress readOptionalInetSocketAddress()
            throws IOException {

        _serializationDepth += 1;
        try {

            byte tag = expectPrimitiveTags( EXPECTED_INET_SOCKET_ADDRESS_TAGS );
            if ( (int)tag == (int)BenoitConstants.INET_SOCKET_ADDRESS_TAG ) {

                InetAddress inetAddress = readOptionalInetAddress();
                needBytes( BenoitConstants.SHORT_SIZE );
                short port = _buffer.getShort();

                return new InetSocketAddress( inetAddress, (int)port );

            } else {

                return null;

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public InetSocketAddress readInetSocketAddress()
            throws IOException {

        _serializationDepth += 1;
        try {

            expectPrimitiveTag( BenoitConstants.INET_SOCKET_ADDRESS_TAG );

            InetAddress inetAddress = readOptionalInetAddress();
            needBytes( BenoitConstants.SHORT_SIZE );
            short port = _buffer.getShort();

            return new InetSocketAddress( inetAddress, (int)port );

        } finally {

            _serializationDepth -= 1;

        }

    }

    public BenoitObject readOptionalBenoitObject()
            throws IOException {

        _serializationDepth += 1;
        try {

            byte tag = expectPrimitiveTags( EXPECTED_OPTIONAL_BENOIT_OBJECT_TAGS );
            if ( (int)tag == (int)BenoitConstants.MISSING_BENOIT_OBJECT_TAG ) {

                return null;

            }

            return extractMandatoryBenoitObject( "readOptionalBenoitObject", tag );

        } finally {

            _serializationDepth -= 1;

        }

    }

    public BenoitObject readBenoitObject()
            throws IOException {

        _serializationDepth += 1;
        try {

            byte tag = expectPrimitiveTags( EXPECTED_MANDATORY_BENOIT_OBJECT_TAGS );

            return extractMandatoryBenoitObject( "readBenoitObject", tag );

        } finally {

            _serializationDepth -= 1;

        }

    }

    private BenoitObject extractMandatoryBenoitObject( String who, byte tag )
            throws IOException {

        //
        // We want to enforce a limit on the number of bytes required to extract a Benoit object (primarily because
        // this allows us to defeat a denial of service attack caused by someone sending a truly humongous object to
        // us over the wire).  We set ourselves up to be able to enforce this limit by noting the bytes
        // consumed value if we do not already have said value noted (we will already have it noted if this is a
        // nested call to this method).
        // We also remember (in a local variable) if this call is the top level call in a possibly recursive
        // nest of calls so that we can:
        //
        //     - remember the BenoitTypeName of the object that we are restoring once we have figured that out
        //       because having this type name is useful in certain error messages (i.e. certain thrown exceptions).
        //
        //     - remember to clear the bytes consumed value when this call is about to return so that
        //       the next call can realize that it is the new top level recursive call.
        //

        boolean weAreTopLevelRecursiveCall = false;
        try {

            if ( _topLevelBenoitTypeName == null ) {

                _byteOffsetBeforeExtractingBenoitObject = getTotalBytesConsumed();
                weAreTopLevelRecursiveCall = true;

            } else {

                weAreTopLevelRecursiveCall = false;

            }

            int benoitTypeIndex = readBenoitTypeIndex();

            if ( (int)tag == (int)BenoitConstants.FIRST_BENOIT_OBJECT_TAG ) {

                needBytes( BenoitConstants.SHORT_SIZE );
                int benoitTypeNameLength = (int)_buffer.getShort();
                byte[] benoitTypeNameBytes = new byte[benoitTypeNameLength];
                needBytes( benoitTypeNameLength );
                _buffer.get( benoitTypeNameBytes );

                BenoitTypeName newBenoitTypeName = new BenoitTypeName( new String( benoitTypeNameBytes ) );
                if ( benoitTypeIndex != _nextBenoitTypeIndex ) {

                    throw new BenoitDeserializationFailedException(
                            newBenoitTypeName,
                            who + ":  newly discovered Benoit type \"" + newBenoitTypeName + "\" " +
                            "requests type index " + benoitTypeIndex +
                            " when next type index should be " + _nextBenoitTypeIndex
                    );

                }

                _knownBenoitTypes.put( benoitTypeIndex, newBenoitTypeName );
                _nextBenoitTypeIndex += 1;

            }

            if ( benoitTypeIndex < 0 || benoitTypeIndex >= _nextBenoitTypeIndex ) {

                throw new BenoitDeserializationFailedException(
                        BenoitTypeName.UNKNOWN,
                        who + ":  specified type index " + benoitTypeIndex +
                        " is undefined (current valid range is 0 through " + ( _nextBenoitTypeIndex - 1 ) + " inclusive)"
                );

            }

            BenoitTypeName benoitTypeName = _knownBenoitTypes.get( benoitTypeIndex );
            if ( benoitTypeName == null ) {

                // We have already verified that the name is in the as-yet known range and yet there
                // is no name.  Something is VERY wrong.  Checking this avoids a NPT being thrown when
                // we use the name to get the Benoit object restorer for the type.

                throw new HowDidWeGetHereError(
                        "got a null BTN when fetching a known-to-be-valid entry from index " +
                        benoitTypeIndex + " our known Benoit types table"
                );

            }

            if ( weAreTopLevelRecursiveCall ) {

                _topLevelBenoitTypeName = benoitTypeName;

            }

            // Let's restore the object.
            // Note that this will throw an BenoitTypeNameUnknownException if the specified name is
            // not known to our restorer registry.
            // Of course, all sorts of other things could go wrong once the actual instantiation
            // begins . . .

            BenoitObject obj = _restorerRegistry.instantiateInstance( this, benoitTypeName );

            return obj;

        } finally {

            if ( weAreTopLevelRecursiveCall ) {

                _byteOffsetBeforeExtractingBenoitObject = null;
                _topLevelBenoitTypeName = null;

            }

        }

    }

    public BenoitObjectRestorerRegistry getRestorerRegistry() {

        return _restorerRegistry;

    }

}
