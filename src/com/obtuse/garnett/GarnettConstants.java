package com.obtuse.garnett;

/*
 * Copyright © 2011 Daniel Boulet.
 */

/**
 * Various Garnett implementation constants.
 */

@SuppressWarnings({ "UnusedDeclaration" })
public class GarnettConstants {

    public static final int GARNETT_OBJECT_STREAM_PROTOCOL_VERSION    = 1;
    public static final int GARNETT_RECORD_PROTOCOL_VERSION = 1;
    public static final int GARNETT_OBJECT_STREAM_MAGIC_NUMBER = 0xdab01000 + GARNETT_OBJECT_STREAM_PROTOCOL_VERSION;
    public static final int GARNETT_RECORD_ORIENTED_STREAM_MAGIC_NUMBER = 0xdab02000 + GARNETT_RECORD_PROTOCOL_VERSION;

    public static final byte MISSING_TAG_MODIFIER = (byte)0x80;
    public static final byte ARRAY_MODIFIER = (byte)0x40;
    public static final int PRIMITIVE_TAG_SIZE = Byte.SIZE;
    public static final int GARNETT_TYPE_INDEX_SIZE = Integer.SIZE / Byte.SIZE;
    public static final int CHAR_SIZE    = Character.SIZE / Byte.SIZE;
    public static final int SHORT_SIZE   = Short.SIZE / Byte.SIZE;
    public static final int INTEGER_SIZE = Integer.SIZE / Byte.SIZE;
    public static final int LONG_SIZE    = Long.SIZE / Byte.SIZE;
    public static final int FLOAT_SIZE   = Float.SIZE / Byte.SIZE;
    public static final int DOUBLE_SIZE  = Double.SIZE / Byte.SIZE;
    public static final int MAX_GARNETT_TYPE_NAME_LENGTH = (int)Short.MAX_VALUE;

    private static byte MISSING( byte tag ) {

        return (byte)( (int)tag | (int)MISSING_TAG_MODIFIER );

    }

    private static byte ARRAY( byte tag ) {

        return (byte)( (int)tag | (int)ARRAY_MODIFIER );

    }

    // The primitive tag values (must be in the range 0x00 through 0x3f inclusive).

    public static final byte VERSION_TAG                      = (byte)0x3f;
    public static final byte BOOLEAN_TAG                      = (byte)0x1;
    public static final byte MISSING_BOOLEAN_TAG              = MISSING( BOOLEAN_TAG );
    public static final byte INT_TAG                          = (byte)0x2;
    public static final byte MISSING_INT_TAG                  = MISSING( INT_TAG );
    public static final byte SHORT_TAG                        = (byte)0x3;
    public static final byte MISSING_SHORT_TAG                = MISSING( SHORT_TAG );
    public static final byte BYTE_TAG                         = (byte)0x4;
    public static final byte MISSING_BYTE_TAG                 = MISSING( BYTE_TAG );
    public static final byte BYTE_ARRAY_TAG                   = ARRAY( BYTE_TAG );
    public static final byte MISSING_BYTE_ARRAY_TAG           = MISSING( BYTE_ARRAY_TAG );
    public static final byte LONG_TAG                         = (byte)0x5;
    public static final byte MISSING_LONG_TAG                 = MISSING( LONG_TAG );
    public static final byte DOUBLE_TAG                       = (byte)0x6;
    public static final byte MISSING_DOUBLE_TAG               = MISSING( DOUBLE_TAG );
    public static final byte FLOAT_TAG                        = (byte)0x7;
    public static final byte MISSING_FLOAT_TAG                = MISSING( FLOAT_TAG );
    public static final byte STRING_TAG                       = (byte)0x8;
    public static final byte MISSING_STRING_TAG               = MISSING( STRING_TAG );
    public static final byte CHAR_TAG                         = (byte)0x9;
    public static final byte MISSING_CHAR_TAG                 = MISSING( CHAR_TAG );
    public static final byte GARNETT_OBJECT_TAG               = (byte)0xa;
    public static final byte MISSING_GARNETT_OBJECT_TAG       = MISSING( GARNETT_OBJECT_TAG );
    public static final byte GARNETT_OBJECT_ARRAY_TAG         = ARRAY( GARNETT_OBJECT_TAG );
    public static final byte MISSING_GARNETT_OBJECT_ARRAY_TAG = MISSING( GARNETT_OBJECT_ARRAY_TAG );
    public static final byte FIRST_GARNETT_OBJECT_TAG         = (byte)0xb;
    public static final byte INET_ADDRESS_TAG                 = (byte)0xc;
    public static final byte MISSING_INET_ADDRESS_TAG         = MISSING( INET_ADDRESS_TAG );
    public static final byte INET_SOCKET_ADDRESS_TAG          = (byte)0xd;
    public static final byte MISSING_INET_SOCKET_ADDRESS_TAG  = MISSING( INET_SOCKET_ADDRESS_TAG );
    public static final byte DATE_TAG                         = (byte)0xe;
    public static final byte MISSING_DATE_TAG                 = MISSING( DATE_TAG );
    public static final byte PREFIX_TAG                       = (byte)0xf;
    public static final byte MISSING_PREFIX_TAG               = MISSING( PREFIX_TAG );
    //    public static final byte NULL_TAG = (byte)0x;
    public static final byte RESET_TAG                        = (byte)0x3d;
    public static final byte END_OF_STREAM_TAG                = (byte)0x3e;

    private GarnettConstants() {

        super();
    }

}
