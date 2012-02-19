package com.obtuse.wire;

/*
 * Copyright © 2011 Daniel Boulet.
 */

import com.obtuse.util.Logger;
import com.obtuse.util.exceptions.HowDidWeGetHereError;
import com.obtuse.wire.exceptions.BenoitSerializationFailedException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Serialize Benoit data to an output stream.
 */

public class BenoitObjectOutputStream extends OutputStream implements BenoitObjectOutputStreamInterface {

    private final OutputStream        _outStream;
    private final WritableByteChannel _outChannel;

    private int _serializationDepth = 0;

    private ByteBuffer _buffer;

    private SortedMap<BenoitTypeName,Integer> _knownBenoitTypes;
    private int _nextBenoitTypeIndex = 0;

    // This is the largest value that we will ever call needRoom for.

    public static final int INTERNAL_BUFFER_SIZE = Math.max(
            ( (int)Short.MAX_VALUE + 1 ) * 2,       // 65536
            BenoitConstants.PRIMITIVE_TAG_SIZE + BenoitConstants.BENOIT_TYPE_INDEX_SIZE + BenoitConstants.SHORT_SIZE
            + BenoitConstants.MAX_BENOIT_TYPE_NAME_LENGTH // slightly over 32K
    );

    public BenoitObjectOutputStream( OutputStream outStream )
            throws IOException {

        super();

        _outStream = outStream;

        _outChannel = Channels.newChannel( outStream );

        _buffer = ByteBuffer.allocate( INTERNAL_BUFFER_SIZE );

        _buffer.putInt( BenoitConstants.BENOIT_OBJECT_STREAM_MAGIC_NUMBER );
        reset();

    }

    @Override
    public void close()
            throws IOException {

        writePrimitiveTag( BenoitConstants.END_OF_STREAM_TAG );
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

        _knownBenoitTypes = new TreeMap<BenoitTypeName, Integer>();
        _nextBenoitTypeIndex = 0;
        writePrimitiveTag( BenoitConstants.RESET_TAG );

    }

    private void writePrimitiveTag( byte tag )
            throws IOException {

        _serializationDepth += 1;
        try {

            needRoom( BenoitConstants.PRIMITIVE_TAG_SIZE );
            _buffer.put( tag );

        } finally {

            _serializationDepth -= 1;

        }

    }

    private void writeBenoitTypeIndex( int tag )
            throws IOException {

        _serializationDepth += 1;
        try {

            needRoom( BenoitConstants.BENOIT_TYPE_INDEX_SIZE );
            _buffer.putInt( tag );

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeVersion( int version )
            throws IOException {

        _serializationDepth += 1;
        try {

            needRoom( BenoitConstants.PRIMITIVE_TAG_SIZE + BenoitConstants.INTEGER_SIZE );
            writePrimitiveTag( BenoitConstants.VERSION_TAG );
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

                writePrimitiveTag( BenoitConstants.MISSING_BOOLEAN_TAG );

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

            needRoom( BenoitConstants.PRIMITIVE_TAG_SIZE + 1 );
            writePrimitiveTag( BenoitConstants.BOOLEAN_TAG );
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

                writePrimitiveTag( BenoitConstants.MISSING_BYTE_TAG );

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

            needRoom( BenoitConstants.PRIMITIVE_TAG_SIZE + 1 );
            writePrimitiveTag( BenoitConstants.BYTE_TAG );
            write( (int)val );

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeOptionalString( String val )
            throws IOException {

        _serializationDepth += 1;
        try {

            if ( val == null ) {

                writePrimitiveTag( BenoitConstants.MISSING_STRING_TAG );

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
            needRoom( BenoitConstants.PRIMITIVE_TAG_SIZE + BenoitConstants.INTEGER_SIZE );
            writePrimitiveTag( BenoitConstants.STRING_TAG );
            _buffer.putInt( bytes.length );
            if ( bytes.length > BenoitConstants.MAX_BENOIT_TYPE_NAME_LENGTH ) {

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

                writePrimitiveTag( BenoitConstants.MISSING_CHAR_TAG );

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

            needRoom( BenoitConstants.PRIMITIVE_TAG_SIZE + BenoitConstants.CHAR_SIZE );
            writePrimitiveTag( BenoitConstants.CHAR_TAG );
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

                writePrimitiveTag( BenoitConstants.MISSING_DOUBLE_TAG );

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

            needRoom( BenoitConstants.PRIMITIVE_TAG_SIZE + BenoitConstants.DOUBLE_SIZE );
            writePrimitiveTag( BenoitConstants.DOUBLE_TAG );
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

                writePrimitiveTag( BenoitConstants.MISSING_FLOAT_TAG );

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

            needRoom( BenoitConstants.PRIMITIVE_TAG_SIZE + BenoitConstants.FLOAT_SIZE );
            writePrimitiveTag( BenoitConstants.FLOAT_TAG );
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

                writePrimitiveTag( BenoitConstants.MISSING_INT_TAG );

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

            needRoom( BenoitConstants.PRIMITIVE_TAG_SIZE + BenoitConstants.INTEGER_SIZE );
            writePrimitiveTag( BenoitConstants.INT_TAG );
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

                writePrimitiveTag( BenoitConstants.MISSING_LONG_TAG );

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

            needRoom( BenoitConstants.PRIMITIVE_TAG_SIZE + BenoitConstants.LONG_SIZE );
            writePrimitiveTag( BenoitConstants.LONG_TAG );
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

                writePrimitiveTag( BenoitConstants.MISSING_SHORT_TAG );

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

            needRoom( BenoitConstants.PRIMITIVE_TAG_SIZE + BenoitConstants.SHORT_SIZE );
            writePrimitiveTag( BenoitConstants.SHORT_TAG );
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

                writePrimitiveTag( BenoitConstants.MISSING_INET_ADDRESS_TAG );

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

                throw new BenoitSerializationFailedException( "mandatory InetAddress instance missing" );

            }

            byte[] bytes = val.getAddress();
            needRoom( BenoitConstants.PRIMITIVE_TAG_SIZE + bytes.length );
            writePrimitiveTag( BenoitConstants.INET_ADDRESS_TAG );
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

                writePrimitiveTag( BenoitConstants.MISSING_INET_SOCKET_ADDRESS_TAG );

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

                throw new BenoitSerializationFailedException( "mandatory InetAddress instance missing" );

            }

            writePrimitiveTag( BenoitConstants.INET_SOCKET_ADDRESS_TAG );
            writeOptionalInetAddress( val.getAddress() );
            needRoom( BenoitConstants.SHORT_SIZE );
            _buffer.putShort( (short)val.getPort() );

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeOptionalBenoitObject( BenoitObject obj )
            throws IOException {

        _serializationDepth += 1;
        try {

            if ( obj == null ) {

                writePrimitiveTag( BenoitConstants.MISSING_BENOIT_OBJECT_TAG );

            } else {

                writeMandatoryBenoitObject( obj );

            }

        } finally {

            _serializationDepth -= 1;

        }

    }

    public void writeMandatoryBenoitObject( BenoitObject obj )
            throws IOException {

        _serializationDepth += 1;
        try {

            if ( obj == null ) {

                throw new BenoitSerializationFailedException( "mandatory object missing" );

            }

            BenoitTypeName benoitTypeName = obj.getBenoitTypeName();

            Integer benoitTypeIndex = _knownBenoitTypes.get( benoitTypeName );
            if ( benoitTypeIndex == null ) {

                byte[] benoitTypeNameBytes = benoitTypeName.getTypeName().getBytes();
                int benoitTypeNameLength = benoitTypeNameBytes.length;
                if ( benoitTypeNameLength > BenoitConstants.MAX_BENOIT_TYPE_NAME_LENGTH ) {

                    throw new BenoitSerializationFailedException(
                            "Benoit type name \"" +
                            benoitTypeName.getTypeName().substring( 0, 20 ) +
                            "..." +
                            benoitTypeName.getTypeName().substring( benoitTypeName.getTypeName().length() - 20 ) +
                            "\" is too long (max length is " + BenoitConstants.MAX_BENOIT_TYPE_NAME_LENGTH + ")"
                    );

                }

                needRoom( BenoitConstants.PRIMITIVE_TAG_SIZE + BenoitConstants.BENOIT_TYPE_INDEX_SIZE + BenoitConstants.SHORT_SIZE + benoitTypeNameLength );
                writePrimitiveTag( BenoitConstants.FIRST_BENOIT_OBJECT_TAG );
                writeBenoitTypeIndex( _nextBenoitTypeIndex );
                _buffer.putShort( (short)benoitTypeNameLength );
                _buffer.put( benoitTypeNameBytes );

                _knownBenoitTypes.put( benoitTypeName, _nextBenoitTypeIndex );
                _nextBenoitTypeIndex += 1;

            } else {

                needRoom( BenoitConstants.PRIMITIVE_TAG_SIZE + BenoitConstants.BENOIT_TYPE_INDEX_SIZE );
                writePrimitiveTag( BenoitConstants.BENOIT_OBJECT_TAG );
                writeBenoitTypeIndex( benoitTypeIndex.intValue() );

            }

            obj.serializeContents( this );

        } finally {

            _serializationDepth -= 1;

        }

        //needRoom( PRIMITIVE_TAG_SIZE + INTEGER_SIZE );
        //writePrimitiveTag( BenoitConstants.BENOIT_OBJECT_TAG );
        //_buffer.putInt(  )
        //obj.serializeContents( this );

    }

}
