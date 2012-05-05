package com.obtuse.garnett.examples;

import com.obtuse.garnett.*;
import com.obtuse.garnett.exceptions.GarnettObjectVersionNotSupportedException;
import com.obtuse.garnett.exceptions.GarnettSerializationFailedException;

import java.io.IOException;

@SuppressWarnings("UnusedDeclaration")
public class SimpleTestClass implements GarnettObject {

    public static final int VERSION = 5;

    private Double _dd = null;
    private int _i1 = 0;
    private Integer _i2 = null;

    public SimpleTestClass() {
        super();

    }

    public SimpleTestClass(
            Double dd,
            int i1,
            Integer i2
    ) {
        super();

        if ( dd == null ) {

            _dd = null;

        } else {

            _dd = dd;

        }

        _i1 = i1;

        if ( i2 == null ) {

            _i2 = null;

        } else {

            _i2 = i2;

        }

    }

    @SuppressWarnings({ "UnusedDeclaration", "DuplicateThrows" })
    public SimpleTestClass( GarnettObjectInputStreamInterface gois )
            throws GarnettSerializationFailedException, IOException, GarnettObjectVersionNotSupportedException {
        super();

        gois.checkVersion(
                SimpleTestClass.class,
                SimpleTestClass.VERSION,
                SimpleTestClass.VERSION
        );

        _dd = gois.readOptionalDouble();
        _i1 = gois.readInt();
        _i2 = gois.readOptionalInteger();

    }

    @SuppressWarnings("DuplicateThrows")
    public void serializeContents( GarnettObjectOutputStreamInterface goos )
            throws GarnettSerializationFailedException, IOException, GarnettObjectVersionNotSupportedException {

        goos.writeVersion( SimpleTestClass.VERSION );

        goos.writeOptionalDouble( _dd );
        goos.writeInt( _i1 );
        goos.writeOptionalInteger( _i2 );

    }

    public GarnettTypeName getGarnettTypeName() {

        return new GarnettTypeName( SimpleTestClass.class.getCanonicalName() );

    }

}