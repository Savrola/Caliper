package com.obtuse.garnett;

/*
 * Copyright © 2011 Daniel Boulet.
 */

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.garnett.exceptions.GarnettIllegalArgumentException;
import com.obtuse.garnett.exceptions.GarnettSerializationFailedException;
import com.obtuse.util.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.*;

/**
 * Serialize Garnett data to an output stream.
 */

public class GarnettObjectOutputStream extends OutputStream implements GarnettObjectOutputStreamInterface {

    private final OutputStream        _outStream;
    private final WritableByteChannel _outChannel;

    private final GarnettSessionPrefix _sessionPrefix;

    private int _serializationDepth = 0;

    private ByteBuffer _buffer;

    private SortedMap<GarnettTypeName,Integer> _knownGarnettTypes;
    private int _nextGarnettTypeIndex = 0;

    // This is the largest value that we will ever call needRoom for.

    public static final int INTERNAL_BUFFER_SIZE = Math.max(
            ( (int)Short.MAX_VALUE + 1 ) * 2,       // 65536
            GarnettConstants.PRIMITIVE_TAG_SIZE + GarnettConstants.GARNETT_TYPE_INDEX_SIZE + GarnettConstants.SHORT_SIZE
            + GarnettConstants.MAX_GARNETT_TYPE_NAME_LENGTH // slightly over 32K
    );

    /**
     * Create an output stream that can send {@link GarnettObject}s.
     * @param outStream the underlying stream over which the {@link GarnettObject}s should be sent.
     * @param sessionPrefix the prefix for this session.
     * @throws GarnettIllegalArgumentException if the sessionPrefix parameter is null or otherwise invalid.
     * @throws IOException if something goes wrong.
     */

    public GarnettObjectOutputStream( OutputStream outStream, GarnettSessionPrefix sessionPrefix )
            throws IOException, GarnettIllegalArgumentException {

        super();

        _outStream = outStream;

        _sessionPrefix = sessionPrefix;

        _outChannel = Channels.newChannel( outStream );

        _buffer = ByteBuffer.allocate( INTERNAL_BUFFER_SIZE );

        _buffer.putInt( GarnettConstants.GARNETT_OBJECT_STREAM_MAGIC_NUMBER );
        reset();

        if ( sessionPrefix == null ) {

            throw new GarnettIllegalArgumentException( "session prefix is null" );

        } else {

            writePrimitiveTag( GarnettConstants.PREFIX_TAG );
            writeByteArray( sessionPrefix.getSessionPrefixBytes() );

        }

    }

    @Override
    public void close()
            throws IOException {

        writePrimitiveTag( GarnettConstants.END_OF_STREAM_TAG );
        flush();
        _outStream.close();

        if ( _serializationDepth > 0 ) {

            throw new IOException( "stream closed while serializing an object" );

        }

    }

    public void drain()
            throws IOException {

        _buffer.flip();

        int totalBytesWritten = 0;
        while ( _buffer.remaining() > 0 ) {

            int bytesWritten = _outChannel.write( _buffer );
            if ( bytesWritten == 0 ) {

                Logger.logMsg( "BADNEWS:  drain failed to write any bytes on a write attempt" );
                throw new HowDidWeGetHereError( "drain failed to write any bytes on a write attempt" );

            }

            totalBytesWritten += bytesWritten;

            if ( _buffer.remaining() > 0 ) {

                Logger.logMsg( "notice:  drain did not get everything out in one go" );

            } else {

                break;

            }

        }

        _buffer.compact();

        Logger.logMsg( "" + totalBytesWritten + " bytes written by drain" );

    }

    @Override
    public void write( int byteValue )
            throws IOException {

        if ( _buffer.remaining() == 0 ) {

            drain();

        }

        _buffer.put( (byte)byteValue );

    }

    public void needRoom( int neededRoom )
            throws IOException {

        if ( neededRoom > _buffer.remaining() ) {

            drain();

            if ( neededRoom > _buffer.remaining() ) {

                throw new HowDidWeGetHereError( "request for more room than exists in our (empty) ByteBuffer" );

            }

        }

    }

    @Override
    public void flush()
            throws IOException {

        drain();
        _outStream.flush();

    }

    public void reset()
            throws IOException {

        _knownGarnettTypes = new TreeMap<GarnettTypeName, Integer>();
        _nextGarnettTypeIndex = 0;
        writePrimitiveTag( GarnettConstants.RESET_TAG );

    }

    private void writePrimitiveTag( byte tag )
            throws IOException {

        _serializationDepth += 1;
        try {

            needRoom( GarnettConstants.PRIMITIVE_TAG_SIZE );
            _buffer.put( tag );

        } finally {

            _serializationDepth -= 1;

        }

    }

    private void writeGarnettTypeIndex( int tag )
            throws IOException {

        _serializationDepth += 1;
        try {

            needRoom( GarnettConstants.GARNETT_TYPE_INDEX_SIZE );
            _buffer.putInt( tag );

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeVersion( int version )
            throws IOException {

        _serializationDepth += 1;
        try {

            needRoom( GarnettConstants.PRIMITIVE_TAG_SIZE + GarnettConstants.INTEGER_SIZE );
            writePrimitiveTag( GarnettConstants.VERSION_TAG );
            _buffer.putInt( version );

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeOptionalBoolean( Boolean val )
            throws IOException {

        _serializationDepth += 1;
        try {

            if ( val == null ) {

                writePrimitiveTag( GarnettConstants.MISSING_BOOLEAN_TAG );

            } else {

                writeBoolean( val.booleanValue() );

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeBoolean( boolean val )
            throws IOException {

        _serializationDepth += 1;
        try {

            needRoom( GarnettConstants.PRIMITIVE_TAG_SIZE + 1 );
            writePrimitiveTag( GarnettConstants.BOOLEAN_TAG );
            _buffer.put( val ? (byte)1 : (byte)0 );

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeOptionalByte( Byte val )
            throws IOException {

        _serializationDepth += 1;
        try {

            if ( val == null ) {

                writePrimitiveTag( GarnettConstants.MISSING_BYTE_TAG );

            } else {

                writeByte( val.byteValue() );

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeByte( byte val )
            throws IOException {

        _serializationDepth += 1;
        try {

            needRoom( GarnettConstants.PRIMITIVE_TAG_SIZE + 1 );
            writePrimitiveTag( GarnettConstants.BYTE_TAG );
            write( (int)val );

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeOptionalByteArray( byte[] val )
            throws IOException {

        _serializationDepth += 1;
        try {

            if ( val == null ) {

                writePrimitiveTag( GarnettConstants.MISSING_BYTE_ARRAY_TAG );

            } else {

                writeByteArray( val );

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeByteArray( byte[] val )
            throws IOException {

        _serializationDepth += 1;
        try {

            needRoom( GarnettConstants.PRIMITIVE_TAG_SIZE + GarnettConstants.INTEGER_SIZE + val.length );
            writePrimitiveTag( GarnettConstants.BYTE_ARRAY_TAG );
            _buffer.putInt( val.length );
            write( val );

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeOptionalString( String val )
            throws IOException {

        _serializationDepth += 1;
        try {

            if ( val == null ) {

                writePrimitiveTag( GarnettConstants.MISSING_STRING_TAG );

            } else {

                writeString( val );

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeString( String val )
            throws IOException {

        _serializationDepth += 1;
        try {

            byte[] bytes = val.getBytes();
            needRoom( GarnettConstants.PRIMITIVE_TAG_SIZE + GarnettConstants.INTEGER_SIZE );
            writePrimitiveTag( GarnettConstants.STRING_TAG );
            _buffer.putInt( bytes.length );
            if ( bytes.length > GarnettConstants.MAX_GARNETT_TYPE_NAME_LENGTH ) {

                flush();
                write( bytes, 0, bytes.length );

            } else {

                needRoom( bytes.length );
                _buffer.put( bytes );

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeOptionalCharacter( Character val )
            throws IOException {

        _serializationDepth += 1;
        try {

            if ( val == null ) {

                writePrimitiveTag( GarnettConstants.MISSING_CHAR_TAG );

            } else {

                writeChar( val.charValue() );

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeChar( char val )
            throws IOException {

        _serializationDepth += 1;
        try {

            needRoom( GarnettConstants.PRIMITIVE_TAG_SIZE + GarnettConstants.CHAR_SIZE );
            writePrimitiveTag( GarnettConstants.CHAR_TAG );
            _buffer.putChar( val );

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeOptionalDouble( Double val )
            throws IOException {

        _serializationDepth += 1;
        try {

            if ( val == null ) {

                writePrimitiveTag( GarnettConstants.MISSING_DOUBLE_TAG );

            } else {

                writeDouble( val.doubleValue() );

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeDouble( double val )
            throws IOException {

        _serializationDepth += 1;
        try {

            needRoom( GarnettConstants.PRIMITIVE_TAG_SIZE + GarnettConstants.DOUBLE_SIZE );
            writePrimitiveTag( GarnettConstants.DOUBLE_TAG );
            _buffer.putDouble( val );

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeOptionalFloat( Float val )
            throws IOException {

        _serializationDepth += 1;
        try {

            if ( val == null ) {

                writePrimitiveTag( GarnettConstants.MISSING_FLOAT_TAG );

            } else {

                writeFloat( val.floatValue() );

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeFloat( float val )
            throws IOException {

        _serializationDepth += 1;
        try {

            needRoom( GarnettConstants.PRIMITIVE_TAG_SIZE + GarnettConstants.FLOAT_SIZE );
            writePrimitiveTag( GarnettConstants.FLOAT_TAG );
            _buffer.putFloat( val );

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeOptionalInteger( Integer val )
            throws IOException {

        _serializationDepth += 1;
        try {

            if ( val == null ) {

                writePrimitiveTag( GarnettConstants.MISSING_INT_TAG );

            } else {

                writeInt( val.intValue() );

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeInt( int val )
            throws IOException {

        _serializationDepth += 1;
        try {

            needRoom( GarnettConstants.PRIMITIVE_TAG_SIZE + GarnettConstants.INTEGER_SIZE );
            writePrimitiveTag( GarnettConstants.INT_TAG );
            _buffer.putInt( val );

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeOptionalLong( Long val )
            throws IOException {

        _serializationDepth += 1;
        try {

            if ( val == null ) {

                writePrimitiveTag( GarnettConstants.MISSING_LONG_TAG );

            } else {

                writeLong( val.longValue() );

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeLong( long val )
            throws IOException {

        _serializationDepth += 1;
        try {

            needRoom( GarnettConstants.PRIMITIVE_TAG_SIZE + GarnettConstants.LONG_SIZE );
            writePrimitiveTag( GarnettConstants.LONG_TAG );
            _buffer.putLong( val );

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeOptionalShort( Short val )
            throws IOException {

        _serializationDepth += 1;
        try {

            if ( val == null ) {

                writePrimitiveTag( GarnettConstants.MISSING_SHORT_TAG );

            } else {

                writeShort( val.shortValue() );

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeShort( short val )
            throws IOException {

        _serializationDepth += 1;
        try {

            needRoom( GarnettConstants.PRIMITIVE_TAG_SIZE + GarnettConstants.SHORT_SIZE );
            writePrimitiveTag( GarnettConstants.SHORT_TAG );
            _buffer.putShort( val );

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeOptionalInetAddress( InetAddress val )
            throws IOException {

        _serializationDepth += 1;
        try {

            if ( val == null ) {

                writePrimitiveTag( GarnettConstants.MISSING_INET_ADDRESS_TAG );

            } else {

                writeInetAddress( val );

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeInetAddress( InetAddress val )
            throws IOException {

        _serializationDepth += 1;
        try {

            if ( val == null ) {

                throw new GarnettSerializationFailedException( "mandatory InetAddress instance missing" );

            }

            byte[] bytes = val.getAddress();
            needRoom( GarnettConstants.PRIMITIVE_TAG_SIZE + bytes.length );
            writePrimitiveTag( GarnettConstants.INET_ADDRESS_TAG );
            _buffer.put( (byte)bytes.length );
            _buffer.put( bytes );

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeOptionalInetSocketAddress( InetSocketAddress val )
            throws IOException {

        _serializationDepth += 1;
        try {

            if ( val == null ) {

                writePrimitiveTag( GarnettConstants.MISSING_INET_SOCKET_ADDRESS_TAG );

            } else {

                writeInetSocketAddress( val );

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeInetSocketAddress( InetSocketAddress val )
            throws IOException {

        _serializationDepth += 1;
        try {

            if ( val == null ) {

                throw new GarnettSerializationFailedException( "mandatory InetAddress instance missing" );

            }

            writePrimitiveTag( GarnettConstants.INET_SOCKET_ADDRESS_TAG );
            writeOptionalInetAddress( val.getAddress() );
            needRoom( GarnettConstants.SHORT_SIZE );
            _buffer.putShort( (short)val.getPort() );

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeOptionalDate( Date val )
            throws IOException {

        _serializationDepth += 1;
        try {

            if ( val == null ) {

                writePrimitiveTag( GarnettConstants.MISSING_DATE_TAG );

            } else {

                writeDate( val );

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeDate( Date val )
            throws IOException {

        _serializationDepth += 1;
        try {

            if ( val == null ) {

                throw new GarnettSerializationFailedException( "mandatory Date instance missing" );

            }

            writePrimitiveTag( GarnettConstants.DATE_TAG );
            writeLong( val.getTime() );

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeGarnettObjectArray( GarnettObject[] objs )
            throws IOException {

        _serializationDepth += 1;
        try {

            writePrimitiveTag( GarnettConstants.GARNETT_OBJECT_ARRAY_TAG );
            writeInt( objs.length );
            for ( GarnettObject obj : objs ) {

                writeOptionalGarnettObject( obj );

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeOptionalGarnettObjectArray( GarnettObject[] objs )
            throws IOException {

        _serializationDepth += 1;
        try {

            if ( objs == null ) {

                writePrimitiveTag( GarnettConstants.MISSING_GARNETT_OBJECT_ARRAY_TAG );

            } else {

                writeGarnettObjectArray( objs );

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeOptionalGarnettObject( GarnettObject obj )
            throws IOException {

        _serializationDepth += 1;
        try {

            if ( obj == null ) {

                writePrimitiveTag( GarnettConstants.MISSING_GARNETT_OBJECT_TAG );

            } else {

                writeGarnettObject( obj );

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeGarnettObject( GarnettObject obj )
            throws IOException {

        _serializationDepth += 1;
        try {

            if ( obj == null ) {

                throw new GarnettSerializationFailedException( "mandatory object missing" );

            }

            GarnettTypeName garnettTypeName = obj.getGarnettTypeName();

            Integer garnettTypeIndex = _knownGarnettTypes.get( garnettTypeName );
            if ( garnettTypeIndex == null ) {

                byte[] garnettTypeNameBytes = garnettTypeName.getTypeName().getBytes();
                int garnettTypeNameLength = garnettTypeNameBytes.length;
                if ( garnettTypeNameLength > GarnettConstants.MAX_GARNETT_TYPE_NAME_LENGTH ) {

                    throw new GarnettSerializationFailedException(
                            "Garnett type name \"" +
                            garnettTypeName.getTypeName().substring( 0, 20 ) +
                            "..." +
                            garnettTypeName.getTypeName().substring( garnettTypeName.getTypeName().length() - 20 ) +
                            "\" is too long (max length is " + GarnettConstants.MAX_GARNETT_TYPE_NAME_LENGTH + ")"
                    );

                }

                needRoom( GarnettConstants.PRIMITIVE_TAG_SIZE + GarnettConstants.GARNETT_TYPE_INDEX_SIZE + GarnettConstants.SHORT_SIZE + garnettTypeNameLength );
                writePrimitiveTag( GarnettConstants.FIRST_GARNETT_OBJECT_TAG );
                writeGarnettTypeIndex( _nextGarnettTypeIndex );
                _buffer.putShort( (short)garnettTypeNameLength );
                _buffer.put( garnettTypeNameBytes );

                _knownGarnettTypes.put( garnettTypeName, _nextGarnettTypeIndex );
                _nextGarnettTypeIndex += 1;

            } else {

                needRoom( GarnettConstants.PRIMITIVE_TAG_SIZE + GarnettConstants.GARNETT_TYPE_INDEX_SIZE );
                writePrimitiveTag( GarnettConstants.GARNETT_OBJECT_TAG );
                writeGarnettTypeIndex( garnettTypeIndex.intValue() );

            }

            obj.serializeContents( this );

        } finally {

            _serializationDepth -= 1;

        }

        //needRoom( PRIMITIVE_TAG_SIZE + INTEGER_SIZE );
        //writePrimitiveTag( GarnettConstants.GARNETT_OBJECT_TAG );
        //_buffer.putInt(  )
        //obj.serializeContents( this );

    }

    public GarnettSessionPrefix getSessionPrefix() {

        return _sessionPrefix;
    }
}
