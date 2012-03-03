package com.obtuse.garnett;

/*
 * Copyright © 2012 Daniel Boulet
 */

import java.io.IOException;

/**
 * A keyword value pair that can travel Garnett-style.
 */

public class GarnettKeywordValue implements GarnettObject {

    public static final GarnettTypeName GARNETT_LOGIN_RESPONSE_MESSAGE_NAME = new GarnettTypeName(
            GarnettKeywordValue.class.getCanonicalName()
    );

    public static int VERSION = 1;

    public final String _keyword;
    public String _value;

    public GarnettKeywordValue( String keyword, String value ) {
        super();

        _keyword = keyword;
        _value = value;

    }

    public GarnettKeywordValue( GarnettObjectInputStreamInterface gois )
            throws IOException {
        super();

        gois.checkVersion(
                GARNETT_LOGIN_RESPONSE_MESSAGE_NAME,
                VERSION,
                VERSION
        );

        _keyword = gois.readString();
        _value = gois.readOptionalString();

    }

    public GarnettTypeName getGarnettTypeName() {

        return GARNETT_LOGIN_RESPONSE_MESSAGE_NAME;

    }

    public void serializeContents( GarnettObjectOutputStreamInterface goos )
            throws IOException {

        goos.writeVersion( VERSION );

        goos.writeString( _keyword );
        goos.writeOptionalString( _value );

    }

    public String toString() {

        return _keyword + " = " + _value;

    }

}
