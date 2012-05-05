package com.obtuse.garnett;

import com.obtuse.garnett.stdmsgs.GarnettResponseMessage;

import java.io.IOException;

/**
 * A very compact header used to reply to requests that only require an {@link GarnettErrorCode} response.
 * <p/>
 * IMPORTANT:  we want to keep this VERY SIMPLE or we'll eventually defeat the point of having a lightweight protocol
 * operating over the proxied SMTP channels.
 * <p/>
 * Copyright © 2007, 2008 Daniel Boulet.
 */

public class GarnettShortResponse extends GarnettResponseMessage {

    private final GarnettErrorCode _errorCode;

    private static final int VERSION = 1;

    public GarnettShortResponse() {
        super( 0L );

        _errorCode = GarnettErrorCode.FAILURE;

    }

    public GarnettShortResponse( long requestId, GarnettErrorCode errorCode ) {
        super( requestId );

        _errorCode = errorCode;

    }

    public GarnettShortResponse( GarnettObjectInputStreamInterface gois )
            throws IOException {
        super( gois );

        gois.checkVersion(
                GarnettShortResponse.class,
                GarnettShortResponse.VERSION,
                GarnettShortResponse.VERSION
        );

        _errorCode = GarnettErrorCode.valueOf( gois.readString() );

    }

    public GarnettErrorCode getErrorCode() {

        return _errorCode;
    }

    public boolean worked() {

        return _errorCode == GarnettErrorCode.SUCCESS;

    }

    public String toString() {

        return "GarnettShortResponse( error code = " + _errorCode + ", " + super.toString() + " )";
    }

    public GarnettTypeName getGarnettTypeName() {

        return new GarnettTypeName(
                GarnettShortResponse.class.getCanonicalName()
        );

    }

    public void serializeContents( GarnettObjectOutputStreamInterface goos )
            throws IOException {

        super.serializeContents( goos );
        goos.writeVersion( VERSION );

        goos.writeString( _errorCode.name() );

    }

}
