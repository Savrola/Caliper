package com.obtuse.garnett.examples;

import com.obtuse.garnett.*;
import com.obtuse.garnett.exceptions.GarnettObjectVersionNotSupportedException;
import com.obtuse.garnett.exceptions.GarnettSerializationFailedException;

import java.io.IOException;

public class SimpleTestClass implements GarnettObject {

    public static final GarnettTypeName SIMPLETESTCLASS_CANONICAL_NAME = new GarnettTypeName(
        SimpleTestClass.class.getCanonicalName()
    );
    public static final int SIMPLETESTCLASS_VERSION = 5;

    private Double _d;
    private int _i1;
    private Integer _i2;

    public SimpleTestClass() {
        super();

    }

    public SimpleTestClass(
            Double d,
            int i1,
            Integer i2
    ) {
        super();

        if ( d == null ) {

            _d = null;

        } else {

            _d = new Double( d );

        }

        _i1 = i1;

        if ( i2 == null ) {

            _i2 = null;

        } else {

            _i2 = new Integer( i2 );

        }

    }

    public SimpleTestClass( GarnettObjectInputStreamInterface gois )
            throws GarnettSerializationFailedException, IOException, GarnettObjectVersionNotSupportedException {
        super();

        gois.checkVersion( SIMPLETESTCLASS_CANONICAL_NAME, SIMPLETESTCLASS_VERSION, SIMPLETESTCLASS_VERSION );

        _d = gois.readOptionalDouble();
        _i1 = gois.readInt();
        _i2 = gois.readOptionalInteger();

    }

    public void serializeContents( GarnettObjectOutputStreamInterface goos )
            throws GarnettSerializationFailedException, IOException, GarnettObjectVersionNotSupportedException {

        goos.writeVersion( SIMPLETESTCLASS_VERSION );

        goos.writeOptionalDouble( _d );
        goos.writeInt( _i1 );
        goos.writeOptionalInteger( _i2 );

    }

    public GarnettTypeName getGarnettTypeName() {

        return SIMPLETESTCLASS_CANONICAL_NAME;

    }

}