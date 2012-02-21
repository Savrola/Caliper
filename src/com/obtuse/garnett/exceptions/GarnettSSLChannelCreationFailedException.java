package com.obtuse.garnett.exceptions;

import com.obtuse.util.Trace;

/*
 * Copyright © 2012 Daniel Boulet
 */

@SuppressWarnings( { "ClassWithoutToString" } )
public class GarnettSSLChannelCreationFailedException extends Exception {

    public GarnettSSLChannelCreationFailedException() {
        super();
        Trace.event( this );
    }

    public GarnettSSLChannelCreationFailedException( String why ) {
        super( why );
        Trace.event( this );
    }

    public GarnettSSLChannelCreationFailedException( String why, Throwable e ) {
        super( why, e );
        Trace.event( this );
    }

}
