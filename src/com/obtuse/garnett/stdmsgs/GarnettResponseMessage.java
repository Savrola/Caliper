package com.obtuse.garnett.stdmsgs;

/*
 * Copyright © 2012 Daniel Boulet
 */

import com.obtuse.garnett.*;

import java.io.IOException;

/**
 * %%% something clever goes here.
 */

public abstract class GarnettResponseMessage extends GarnettMessage {

    public static final GarnettTypeName GARNETT_RESPONSE_MESSAGE_NAME = new GarnettTypeName(
            GarnettResponseMessage.class.getCanonicalName()
    );

    public static final int VERSION = 1;

    private final long _requestId;

    protected GarnettResponseMessage( long requestId ) {
        super();

        _requestId = requestId;

    }

    public GarnettResponseMessage( GarnettObjectInputStreamInterface gois )
            throws IOException {
        super( gois );

        gois.checkVersion(
                GARNETT_RESPONSE_MESSAGE_NAME,
                VERSION,
                VERSION
        );

        _requestId = gois.readLong();

    }

    public long getRequestId() {

        return _requestId;
    }

    public abstract boolean worked();

    public void serializeContents( GarnettObjectOutputStreamInterface goos )
            throws IOException {

        goos.writeVersion( VERSION );

        goos.writeLong( _requestId );

    }

    public String toString() {

        return "GarnettResponseMessage( reqId = " + _requestId + " )";

    }

}
