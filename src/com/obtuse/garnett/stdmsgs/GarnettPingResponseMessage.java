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

    public static final GarnettTypeName GARNETT_PING_RESPONSE_MESSAGE_NAME = new GarnettTypeName(
            GarnettPingResponseMessage.class.getCanonicalName()
    );

    public static final int VERSION = 1;

    public GarnettPingResponseMessage( long requestId )
            throws GarnettInvalidAccountNameException {
        super( requestId );

    }

    public GarnettPingResponseMessage( GarnettObjectInputStreamInterface gois )
            throws IOException {
        super( gois );

        gois.checkVersion(
                GARNETT_PING_RESPONSE_MESSAGE_NAME,
                VERSION,
                VERSION
        );

    }

    public boolean worked() {

        return true;

    }

    public GarnettTypeName getGarnettTypeName() {

        return GARNETT_PING_RESPONSE_MESSAGE_NAME;

    }

    public void serializeContents( GarnettObjectOutputStreamInterface goos )
            throws IOException {

        goos.writeVersion( VERSION );
        super.serializeContents( goos );

    }

    public String toString() {

        return "GarnettPingRequestMessage()";

    }

}
