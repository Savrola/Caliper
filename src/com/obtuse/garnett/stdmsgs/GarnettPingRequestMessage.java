package com.obtuse.garnett.stdmsgs;

import com.obtuse.garnett.*;

import java.io.IOException;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * Ping a Garnett-aware server.
 */

public class GarnettPingRequestMessage extends GarnettRequestMessage {

    public static final int VERSION = 1;

    public GarnettPingRequestMessage() {
        super();

    }

    public GarnettPingRequestMessage( GarnettObjectInputStreamInterface gois )
            throws IOException {
        super();

        gois.checkVersion(
                getGarnettTypeName(),
                VERSION,
                VERSION
        );

    }

    @Override
    public Class<? extends GarnettResponseMessage> getResponseClass() {

        return GarnettPingResponseMessage.class;

    }

    public GarnettTypeName getGarnettTypeName() {

        return new GarnettTypeName(
                GarnettPingRequestMessage.class.getCanonicalName()
        );

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
