package com.obtuse.garnett.server;

/*
 * Copyright © 2012 Daniel Boulet
 */

import com.obtuse.garnett.SSLUtilities;
import com.obtuse.garnett.exceptions.GarnettSSLChannelCreationFailedException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.InputStream;

/**
 * Gather the Garnett SSL server tools into one place.
 */

public class GarnettServerSslTools {

    private final SSLContext _sslContext;

    private final SSLServerSocketFactory _sslServerSocketFactory;

    public GarnettServerSslTools( String keystoreFile, InputStream keystoreInputStream, char[] keystorePassword, char[] keyPassword )

            throws GarnettSSLChannelCreationFailedException {
        super();

        _sslContext = SSLUtilities.getSSLContext( false, keystoreFile, keystoreInputStream, keystorePassword, keyPassword );
        _sslServerSocketFactory = _sslContext.getServerSocketFactory();

    }

    public SSLContext getSslContext() {

        return _sslContext;

    }

    public SSLServerSocketFactory getSslServerSocketFactory() {

        return _sslServerSocketFactory;

    }

}
