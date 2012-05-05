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
                GarnettResponseMessage.class,
                GarnettResponseMessage.VERSION,
                GarnettResponseMessage.VERSION
        );

        _requestId = gois.readLong();

    }

    public long getRequestId() {

        return _requestId;
    }

    public abstract boolean worked();

    public void serializeContents( GarnettObjectOutputStreamInterface goos )
            throws IOException {

        goos.writeVersion( GarnettResponseMessage.VERSION );

        goos.writeLong( _requestId );

    }

    public String toString() {

        return "GarnettResponseMessage( reqId = " + _requestId + " )";

    }

}
