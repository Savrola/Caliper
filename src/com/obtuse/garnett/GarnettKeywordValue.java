package com.obtuse.garnett;

/*
 * Copyright © 2012 Daniel Boulet
 */

import java.io.IOException;

/**
 * A keyword value pair that can travel Garnett-style.
 */

public class GarnettKeywordValue implements GarnettObject {

    public static final int VERSION = 1;

    public final String _keyword;
    public String _value;

    @SuppressWarnings("UnusedDeclaration")
    public GarnettKeywordValue( String keyword, String value ) {
        super();

        _keyword = keyword;
        _value = value;

    }

    @SuppressWarnings("UnusedDeclaration")
    public GarnettKeywordValue( GarnettObjectInputStreamInterface gois )
            throws IOException {
        super();

        gois.checkVersion(
                GarnettKeywordValue.class,
                GarnettKeywordValue.VERSION,
                GarnettKeywordValue.VERSION
        );

        _keyword = gois.readString();
        _value = gois.readOptionalString();

    }

    public GarnettTypeName getGarnettTypeName() {

        return new GarnettTypeName( GarnettKeywordValue.class.getCanonicalName() );

    }

    public void serializeContents( GarnettObjectOutputStreamInterface goos )
            throws IOException {

        goos.writeVersion( GarnettKeywordValue.VERSION );

        goos.writeString( _keyword );
        goos.writeOptionalString( _value );

    }

    public String toString() {

        return _keyword + " = " + _value;

    }

}
