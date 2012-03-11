package com.obtuse.garnett.client;

/*
 * Copyright © 2012 Daniel Boulet
 */

import com.obtuse.garnett.SSLUtilities;
import com.obtuse.garnett.exceptions.GarnettSSLChannelCreationFailedException;
import com.obtuse.util.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;

/**
 * Gather the Garnett SSL client tools into one place.
 */

public class GarnettClientSslTools {

    private static final SSLContext _sslClientContext;

    private static final SSLSocketFactory _sslClientSocketFactory;

//    private static final SSLContext _sslAlfredClientContext;
//
//    private static final SSLSocketFactory _sslAlfredClientSocketFactory;

    static {

        // Create the SSL context and socket factory for connecting either directly or via Alfred.

        SSLContext sslContext = null;
        try {

            sslContext = SSLUtilities.getOurClientSSLContext();

        } catch ( GarnettSSLChannelCreationFailedException e ) {

            Logger.logErr( "initializing Garnett SSL client socket factory", e );
            System.exit( 1 );

        } catch ( IOException e ) {

            Logger.logErr( "initializing Garnett client socket factory", e );
            System.exit( 1 );

        }

        _sslClientContext = sslContext;

        _sslClientSocketFactory = _sslClientContext.getSocketFactory();

    }

    private GarnettClientSslTools() {
        super();
    }

    public static SSLSocketFactory getSslClientSocketFactory() {

        return _sslClientSocketFactory;

    }

}
