package com.obtuse.garnett;

/*
 * Copyright © 2011 Daniel Boulet.
 */

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.garnett.exceptions.*;
import com.obtuse.util.*;

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
 * Deserialize Garnett data from an input stream.
 */

public class GarnettObjectInputStream extends InputStream implements GarnettObjectInputStreamInterface {

    private static byte[] generateExpectedTags( byte mandatoryTag ) {

        return new byte[] {
                mandatoryTag,
                (byte)( (int)mandatoryTag | (int)GarnettConstants.MISSING_TAG_MODIFIER )
        };

    }

    private static final byte[] EXPECTED_PREFIX_TAGS = generateExpectedTags( GarnettConstants.PREFIX_TAG );
    private static final byte[] EXPECTED_BOOLEAN_TAGS = generateExpectedTags( GarnettConstants.BOOLEAN_TAG );
    private static final byte[] EXPECTED_BYTE_TAGS = generateExpectedTags( GarnettConstants.BYTE_TAG );
    private static final byte[] EXPECTED_BYTE_ARRAY_TAGS = generateExpectedTags( GarnettConstants.BYTE_ARRAY_TAG );
    private static final byte[] EXPECTED_STRING_TAGS = generateExpectedTags( GarnettConstants.STRING_TAG );
    private static final byte[] EXPECTED_CHAR_TAGS = generateExpectedTags( GarnettConstants.CHAR_TAG );
    private static final byte[] EXPECTED_DOUBLE_TAGS = generateExpectedTags( GarnettConstants.DOUBLE_TAG );
    private static final byte[] EXPECTED_FLOAT_TAGS = generateExpectedTags( GarnettConstants.FLOAT_TAG );
    private static final byte[] EXPECTED_INTEGER_TAGS = generateExpectedTags( GarnettConstants.INT_TAG );
    private static final byte[] EXPECTED_LONG_TAGS = generateExpectedTags( GarnettConstants.LONG_TAG );
    private static final byte[] EXPECTED_SHORT_TAGS = generateExpectedTags( GarnettConstants.SHORT_TAG );
    private static final byte[] EXPECTED_INET_ADDRESS_TAGS = generateExpectedTags( GarnettConstants.INET_ADDRESS_TAG );
    private static final byte[] EXPECTED_INET_SOCKET_ADDRESS_TAGS = generateExpectedTags( GarnettConstants.INET_SOCKET_ADDRESS_TAG );
    private static final byte[] EXPECTED_DATE_TAGS = generateExpectedTags( GarnettConstants.DATE_TAG );
    private static final byte[] EXPECTED_GARNETT_OBJECT_ARRAY_TAGS = generateExpectedTags( GarnettConstants.GARNETT_OBJECT_ARRAY_TAG );
    private static final byte[] EXPECTED_OPTIONAL_GARNETT_OBJECT_TAGS =
            new byte[] {
                    GarnettConstants.GARNETT_OBJECT_TAG,
                    GarnettConstants.MISSING_GARNETT_OBJECT_TAG,
                    GarnettConstants.FIRST_GARNETT_OBJECT_TAG
            };
    private static final byte[] EXPECTED_MANDATORY_GARNETT_OBJECT_TAGS =
            new byte[] {
                    GarnettConstants.GARNETT_OBJECT_TAG,
                    GarnettConstants.FIRST_GARNETT_OBJECT_TAG
            };

    private final int _maxGarnettObjectSize;
    private Integer _byteOffsetBeforeExtractingGarnettObject = null;
    private GarnettTypeName _topLevelGarnettTypeName;

    private int _totalBytesRead = 0;
    private final InputStream _inStream;
    private final ReadableByteChannel _inChannel;

    private int _serializationDepth = 0;

    private ByteBuffer _buffer;
    private boolean _done = false;

    private SortedMap<Integer,GarnettTypeName> _knownGarnettTypes;
    private final GarnettObjectRestorerRegistry _restorerRegistry;
    private int _nextGarnettTypeIndex = 0;

    private final GarnettSessionPrefix _sessionPrefix;

//    public static final int PRIMITIVE_TAG_SIZE = Byte.SIZE;
//    public static final int GARNETT_TYPE_INDEX_SIZE = Integer.SIZE / Byte.SIZE;
//    public static final int CHAR_SIZE    = Character.SIZE / Byte.SIZE;
//    public static final int SHORT_SIZE   = Short.SIZE / Byte.SIZE;
//    public static final int INTEGER_SIZE = Integer.SIZE / Byte.SIZE;
//    public static final int LONG_SIZE    = Long.SIZE / Byte.SIZE;
//    public static final int FLOAT_SIZE   = Float.SIZE / Byte.SIZE;
//    public static final int DOUBLE_SIZE  = Double.SIZE / Byte.SIZE;
//
//    public static final int MAX_GARNETT_TYPE_NAME_LENGTH = (int)Short.MAX_VALUE;

    // This is the largest value that we will ever call needRoom for.

    public static final int INTERNAL_BUFFER_SIZE = Math.max(
            ( (int)Short.MAX_VALUE + 1 ) * 2,       // 65536
            GarnettConstants.PRIMITIVE_TAG_SIZE + GarnettConstants.GARNETT_TYPE_INDEX_SIZE + GarnettConstants.SHORT_SIZE + GarnettConstants.MAX_GARNETT_TYPE_NAME_LENGTH // slightly over 32K
    );

    public GarnettObjectInputStream(
            int maxGarnettObjectSize,
            InputStream inStream,
            GarnettObjectRestorerRegistry restorerRegistry
    )
            throws IOException, GarnettUnsupportedProtocolVersionException, GarnettIllegalArgumentException {

        super();

        _maxGarnettObjectSize = maxGarnettObjectSize;

        _inStream = inStream;

        _restorerRegistry = restorerRegistry;

        _inChannel = Channels.newChannel( inStream );

        _buffer = ByteBuffer.allocate( INTERNAL_BUFFER_SIZE );
        _buffer.clear();
        _buffer.flip();

        needBytes( GarnettConstants.INTEGER_SIZE );
        int magic = _buffer.getInt();
        if ( magic != GarnettConstants.GARNETT_OBJECT_STREAM_MAGIC_NUMBER ) {

            throw new IllegalArgumentException(
                    "GarnettObjectInputStream:  input stream does not start with 0x" +
                    ObtuseUtil5.hexvalue( GarnettConstants.GARNETT_OBJECT_STREAM_MAGIC_NUMBER )
            );

        }

        gotReset();

        expectPrimitiveTag( GarnettConstants.PREFIX_TAG );
        byte[] sessionPrefixBytes = readByteArray();
        _sessionPrefix = new GarnettSessionPrefix( sessionPrefixBytes );

    }

    public GarnettObjectInputStream(
            InputStream inStream,
            GarnettObjectRestorerRegistry restorerRegistry
    )
            throws IOException, GarnettUnsupportedProtocolVersionException, GarnettIllegalArgumentException {
        this( 0, inStream, restorerRegistry );

    }

    public GarnettObjectInputStream( InputStream inStream )
            throws IOException, GarnettUnsupportedProtocolVersionException, GarnettIllegalArgumentException {
        this( 0, inStream );

    }

    public GarnettObjectInputStream( int maxGarnettObjectSize, InputStream inStream )
            throws IOException, GarnettUnsupportedProtocolVersionException, GarnettIllegalArgumentException {
        this( maxGarnettObjectSize, inStream, new GarnettObjectRestorerRegistry( "anonymous BOR registry" ) );

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

            throw new IOException( "Garnett input stream closed while de-serializing an object" );

        } else if ( streamTerminator == -1 ) {

            throw new IOException( "Garnett input stream not properly terminated (EOF encountered first)" );

        } else if ( streamTerminator != (int)GarnettConstants.END_OF_STREAM_TAG ) {

            throw new IOException(
                    "Garnett input stream not properly terminated (got 0x" +
                    ObtuseUtil5.hexvalue( (byte)streamTerminator ) +
                    " instead of expected 0x" +
                    ObtuseUtil5.hexvalue( (byte)GarnettConstants.END_OF_STREAM_TAG ) +
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

        if ( _maxGarnettObjectSize > 0 && _byteOffsetBeforeExtractingGarnettObject != null ) {

            int objectSizeSoFar = getTotalBytesConsumed() - _byteOffsetBeforeExtractingGarnettObject.intValue();
            if ( objectSizeSoFar > _maxGarnettObjectSize ) {

                throw new GarnettDeserializationFailedException(
                        _topLevelGarnettTypeName,
                        "partial object size (" + objectSizeSoFar +
                        " bytes) exceeds limit (" + _maxGarnettObjectSize + " bytes)"
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

        _knownGarnettTypes = new TreeMap<Integer, GarnettTypeName>();
        _nextGarnettTypeIndex = 0;

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

                if ( nextByte == (int)GarnettConstants.RESET_TAG ) {

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

    private int readGarnettTypeIndex()
            throws IOException {

        _serializationDepth += 1;
        try {

            needBytes( GarnettConstants.GARNETT_TYPE_INDEX_SIZE );
            return _buffer.getInt();

        } finally {

            _serializationDepth -= 1;

        }

    }

    public int checkVersion( GarnettTypeName garnettTypeName, int oldestSupportedVersion, int newestSupportedVersion )
            throws IOException, GarnettObjectVersionNotSupportedException {

        _serializationDepth += 1;
        try {

            expectPrimitiveTag( GarnettConstants.VERSION_TAG );
            needBytes( GarnettConstants.INTEGER_SIZE );

            int version = _buffer.getInt();
            if ( version < oldestSupportedVersion || version > newestSupportedVersion ) {

                throw new GarnettObjectVersionNotSupportedException(
                        "version " + version + " found in stream for " + garnettTypeName + " not supported " +
                        "(oldest supported version is " + oldestSupportedVersion + ", " +
                        "newest supported version is " + newestSupportedVersion + ")",
                        _restorerRegistry,
                        garnettTypeName

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
            if ( (int)tag == (int)GarnettConstants.BOOLEAN_TAG ) {

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

            expectPrimitiveTag( GarnettConstants.BOOLEAN_TAG );

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
            if ( (int)tag == (int)GarnettConstants.BYTE_TAG ) {

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

            expectPrimitiveTag( GarnettConstants.BYTE_TAG );

            needBytes( 1 );
            byte b = _buffer.get();

            return b;

        } finally {

            _serializationDepth -= 1;

        }

    }

    public byte[] readOptionalByteArray()
            throws IOException {

        _serializationDepth += 1;
        try {

            byte tag = expectPrimitiveTags( EXPECTED_BYTE_ARRAY_TAGS );
            if ( (int)tag == (int)GarnettConstants.BYTE_ARRAY_TAG ) {

                return readByteArray();

            } else {

                return null;

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public byte[] readByteArray()
            throws IOException {

        _serializationDepth += 1;
        try {

            expectPrimitiveTag( GarnettConstants.BYTE_TAG );

            needBytes( GarnettConstants.INTEGER_SIZE );
            int len = _buffer.getInt();
            byte[] bytes = new byte[len];
            int totalBytesRead = 0;
            while ( totalBytesRead < len ) {

                int bytesRead = read( bytes, totalBytesRead, len - totalBytesRead );
                if ( bytesRead < 0 ) {

                    throw new IOException(
                            "readOptionalString:  EOF reading byte array at offset (within bytes) of " +
                            totalBytesRead
                    );

                }

                totalBytesRead += bytesRead;

            }

            return bytes;

        } finally {

            _serializationDepth -= 1;

        }

    }

    public String readOptionalString()
            throws IOException {

        _serializationDepth += 1;
        try {

            byte tag = expectPrimitiveTags( EXPECTED_STRING_TAGS );
            if ( (int)tag == (int)GarnettConstants.STRING_TAG ) {

                needBytes( GarnettConstants.INTEGER_SIZE );
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

            expectPrimitiveTag( GarnettConstants.STRING_TAG );

            needBytes( GarnettConstants.INTEGER_SIZE );
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
            if ( (int)tag == (int)GarnettConstants.CHAR_TAG ) {

                needBytes( GarnettConstants.CHAR_SIZE );
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

            expectPrimitiveTag( GarnettConstants.CHAR_TAG );

            needBytes( GarnettConstants.CHAR_SIZE );
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
            if ( (int)tag == (int)GarnettConstants.DOUBLE_TAG ) {

                needBytes( GarnettConstants.DOUBLE_SIZE );
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

            expectPrimitiveTag( GarnettConstants.DOUBLE_TAG );

            needBytes( GarnettConstants.DOUBLE_SIZE );
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
            if ( (int)tag == (int)GarnettConstants.FLOAT_TAG ) {

                needBytes( GarnettConstants.FLOAT_SIZE );
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

            expectPrimitiveTag( GarnettConstants.FLOAT_TAG );

            needBytes( GarnettConstants.FLOAT_SIZE );
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
            if ( (int)tag == (int)GarnettConstants.INT_TAG ) {

                needBytes( GarnettConstants.INTEGER_SIZE );
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

            expectPrimitiveTag( GarnettConstants.INT_TAG );

            needBytes( GarnettConstants.INTEGER_SIZE );
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
            if ( (int)tag == (int)GarnettConstants.LONG_TAG ) {

                needBytes( GarnettConstants.LONG_SIZE );
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

            expectPrimitiveTag( GarnettConstants.LONG_TAG );

            needBytes( GarnettConstants.LONG_SIZE );
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
            if ( (int)tag == (int)GarnettConstants.SHORT_TAG ) {

                needBytes( GarnettConstants.SHORT_SIZE );
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

            expectPrimitiveTag( GarnettConstants.SHORT_TAG );

            needBytes( GarnettConstants.SHORT_SIZE );
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
            if ( (int)tag == (int)GarnettConstants.INET_ADDRESS_TAG ) {

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

            expectPrimitiveTag( GarnettConstants.INET_ADDRESS_TAG );

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
            if ( (int)tag == (int)GarnettConstants.INET_SOCKET_ADDRESS_TAG ) {

                InetAddress inetAddress = readOptionalInetAddress();
                needBytes( GarnettConstants.SHORT_SIZE );
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

            expectPrimitiveTag( GarnettConstants.INET_SOCKET_ADDRESS_TAG );

            InetAddress inetAddress = readOptionalInetAddress();
            needBytes( GarnettConstants.SHORT_SIZE );
            short port = _buffer.getShort();

            return new InetSocketAddress( inetAddress, (int)port );

        } finally {

            _serializationDepth -= 1;

        }

    }

    public ImmutableDate readOptionalDate()
            throws IOException {

        _serializationDepth += 1;
        try {

            byte tag = expectPrimitiveTags( EXPECTED_DATE_TAGS );
            if ( (int)tag == (int)GarnettConstants.DATE_TAG ) {

                return readDate();

            } else {

                return null;

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public ImmutableDate readDate()
            throws IOException {

        _serializationDepth += 1;
        try {

            expectPrimitiveTag( GarnettConstants.DATE_TAG );
            long dateValue = readLong();

            return new ImmutableDate( dateValue );

        } finally {

            _serializationDepth -= 1;

        }

    }

    public GarnettObject readOptionalGarnettObject()
            throws IOException {

        _serializationDepth += 1;
        try {

            byte tag = expectPrimitiveTags( EXPECTED_OPTIONAL_GARNETT_OBJECT_TAGS );
            if ( (int)tag == (int)GarnettConstants.MISSING_GARNETT_OBJECT_TAG ) {

                return null;

            }

            return extractMandatoryGarnettObject( "readOptionalGarnettObject", tag );

        } finally {

            _serializationDepth -= 1;

        }

    }

    public GarnettObject[] readOptionalGarnettObjectArray()
            throws IOException {

        _serializationDepth += 1;
        try {

            byte tag = expectPrimitiveTags( EXPECTED_GARNETT_OBJECT_ARRAY_TAGS );
            if ( tag == GarnettConstants.MISSING_GARNETT_OBJECT_ARRAY_TAG ) {

                return null;

            }

            int len = readInt();
            GarnettObject[] rval = new GarnettObject[len];
            for ( int i = 0; i < len; i += 1 ) {

                rval[i] = readOptionalGarnettObject();

            }

            return rval;

        } finally {

            _serializationDepth -= 1;

        }

    }

    public GarnettObject[] readGarnettObjectArray()
            throws IOException {

        _serializationDepth += 1;
        try {

            byte tag = expectPrimitiveTag( GarnettConstants.GARNETT_OBJECT_ARRAY_TAG );

            int len = readInt();
            GarnettObject[] rval = new GarnettObject[len];
            for ( int i = 0; i < len; i += 1 ) {

                rval[i] = readOptionalGarnettObject();

            }

            return rval;

        } finally {

            _serializationDepth -= 1;

        }

    }

    public GarnettObject readGarnettObject()
            throws IOException {

        _serializationDepth += 1;
        try {

            byte tag = expectPrimitiveTags( EXPECTED_MANDATORY_GARNETT_OBJECT_TAGS );

            return extractMandatoryGarnettObject( "readGarnettObject", tag );

        } finally {

            _serializationDepth -= 1;

        }

    }

    private GarnettObject extractMandatoryGarnettObject( String who, byte tag )
            throws IOException {

        //
        // We want to enforce a limit on the number of bytes required to extract a Garnett object (primarily because
        // this allows us to defeat a denial of service attack caused by someone sending a truly humongous object to
        // us over the garnett).  We set ourselves up to be able to enforce this limit by noting the bytes
        // consumed value if we do not already have said value noted (we will already have it noted if this is a
        // nested call to this method).
        // We also remember (in a local variable) if this call is the top level call in a possibly recursive
        // nest of calls so that we can:
        //
        //     - remember the GarnettTypeName of the object that we are restoring once we have figured that out
        //       because having this type name is useful in certain error messages (i.e. certain thrown exceptions).
        //
        //     - remember to clear the bytes consumed value when this call is about to return so that
        //       the next call can realize that it is the new top level recursive call.
        //

        boolean weAreTopLevelRecursiveCall = false;
        try {

            if ( _topLevelGarnettTypeName == null ) {

                _byteOffsetBeforeExtractingGarnettObject = getTotalBytesConsumed();
                weAreTopLevelRecursiveCall = true;

            } else {

                weAreTopLevelRecursiveCall = false;

            }

            int garnettTypeIndex = readGarnettTypeIndex();

            if ( (int)tag == (int)GarnettConstants.FIRST_GARNETT_OBJECT_TAG ) {

                needBytes( GarnettConstants.SHORT_SIZE );
                int garnettTypeNameLength = (int)_buffer.getShort();
                byte[] garnettTypeNameBytes = new byte[garnettTypeNameLength];
                needBytes( garnettTypeNameLength );
                _buffer.get( garnettTypeNameBytes );

                GarnettTypeName newGarnettTypeName = new GarnettTypeName( new String( garnettTypeNameBytes ) );
                if ( garnettTypeIndex != _nextGarnettTypeIndex ) {

                    throw new GarnettDeserializationFailedException(
                            newGarnettTypeName,
                            who + ":  newly discovered Garnett type \"" + newGarnettTypeName + "\" " +
                            "requests type index " + garnettTypeIndex +
                            " when next type index should be " + _nextGarnettTypeIndex
                    );

                }

                _knownGarnettTypes.put( garnettTypeIndex, newGarnettTypeName );
                _nextGarnettTypeIndex += 1;

            }

            if ( garnettTypeIndex < 0 || garnettTypeIndex >= _nextGarnettTypeIndex ) {

                throw new GarnettDeserializationFailedException(
                        GarnettTypeName.UNKNOWN,
                        who + ":  specified type index " + garnettTypeIndex +
                        " is undefined (current valid range is 0 through " + ( _nextGarnettTypeIndex - 1 ) + " inclusive)"
                );

            }

            GarnettTypeName garnettTypeName = _knownGarnettTypes.get( garnettTypeIndex );
            if ( garnettTypeName == null ) {

                // We have already verified that the name is in the as-yet known range and yet there
                // is no name.  Something is VERY wrong.  Checking this avoids a NPT being thrown when
                // we use the name to get the Garnett object restorer for the type.

                throw new HowDidWeGetHereError(
                        "got a null BTN when fetching a known-to-be-valid entry from index " +
                        garnettTypeIndex + " our known Garnett types table"
                );

            }

            if ( weAreTopLevelRecursiveCall ) {

                _topLevelGarnettTypeName = garnettTypeName;

            }

            // Let's restore the object.
            // Note that this will throw an GarnettTypeNameUnknownException if the specified name is
            // not known to our restorer registry.
            // Of course, all sorts of other things could go wrong once the actual instantiation
            // begins . . .

            GarnettObject obj = _restorerRegistry.instantiateInstance( this, garnettTypeName );

            return obj;

        } finally {

            if ( weAreTopLevelRecursiveCall ) {

                _byteOffsetBeforeExtractingGarnettObject = null;
                _topLevelGarnettTypeName = null;

            }

        }

    }

    public GarnettObjectRestorerRegistry getRestorerRegistry() {

        return _restorerRegistry;

    }

    public GarnettSessionPrefix getSessionPrefix() {

        return _sessionPrefix;

    }

}
