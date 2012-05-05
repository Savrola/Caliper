package com.obtuse.garnett.stdmsgs;

import com.obtuse.garnett.*;
import com.obtuse.garnett.exceptions.GarnettInvalidAccountNameException;

import java.io.IOException;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * Login to a Garnett-aware server.
 */

public class GarnettPingResponseMessage extends GarnettResponseMessage {

    public static final int VERSION = 1;

    @SuppressWarnings("UnusedDeclaration")
    public GarnettPingResponseMessage( long requestId )
            throws GarnettInvalidAccountNameException {
        super( requestId );

    }

    @SuppressWarnings("UnusedDeclaration")
    public GarnettPingResponseMessage( GarnettObjectInputStreamInterface gois )
            throws IOException {
        super( gois );

        gois.checkVersion(
                GarnettPingResponseMessage.class,
                GarnettPingResponseMessage.VERSION,
                GarnettPingResponseMessage.VERSION
        );

    }

    public boolean worked() {

        return true;

    }

    public GarnettTypeName getGarnettTypeName() {

        return new GarnettTypeName(
                GarnettPingResponseMessage.class.getCanonicalName()
        );

    }

    public void serializeContents( GarnettObjectOutputStreamInterface goos )
            throws IOException {

        goos.writeVersion( GarnettPingResponseMessage.VERSION );
        super.serializeContents( goos );

    }

    public String toString() {

        return "GarnettPingRequestMessage()";

    }

}
