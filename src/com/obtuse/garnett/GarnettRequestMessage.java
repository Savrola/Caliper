package com.obtuse.garnett;

/*
 * Copyright © 2012 Daniel Boulet
 */

import com.obtuse.garnett.stdmsgs.GarnettResponseMessage;

import java.io.IOException;

/**
 * A Garnett message which requires a response.
 */

public abstract class GarnettRequestMessage extends GarnettMessage {

//    public static final GarnettTypeName GARNETT_REQUEST_MESSAGE_NAME = new GarnettTypeName(
//            GarnettRequestMessage.class.getCanonicalName()
//    );
//
//    public static final int VERSION = 1;

    protected GarnettRequestMessage() {
        super();
    }

    protected GarnettRequestMessage( GarnettObjectInputStreamInterface gois )
            throws IOException {
        super( gois );

    }

    public long getRequestId() {

        return getId();

    }

    public abstract Class<? extends GarnettResponseMessage> getResponseClass();

}
