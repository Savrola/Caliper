package com.obtuse.garnett.client;

/*
 * Copyright © 2012 Daniel Boulet
 */

import com.obtuse.garnett.*;
import com.obtuse.garnett.exceptions.GarnettSSLChannelCreationFailedException;
import com.obtuse.util.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;

/**
 * %%% something clever goes here.
 */

public class GarnettClientSession extends GarnettSession {

    private final GarnettClientSessionManager _garnettClientSessionManager;

    private final InetSocketAddress _serverAddress;

    private final String _serverTypeName;

    private final String _userName;

    private final byte[] _obfuscatedPassword;

    private static final SSLContext _sslVanHorneClientContext;

    private static final SSLSocketFactory _sslVanHorneClientSocketFactory;

//    private static final SSLContext _sslAlfredClientContext;
//
//    private static final SSLSocketFactory _sslAlfredClientSocketFactory;

    static {

        // Create the SSL context and socket factory for connecting either directly or via Alfred.

        SSLContext sslContext = null;
        try {

            sslContext = SSLUtilities.getOurClientSSLContext();

        } catch ( GarnettSSLChannelCreationFailedException e ) {

            Logger.logErr( "initializing direct VanHorne SSL client socket factory", e );
            System.exit( 1 );

        } catch ( IOException e ) {

            Logger.logErr( "initializing Garnett client socket factory", e );
            System.exit( 1 );

        }

        _sslVanHorneClientContext = sslContext;

        _sslVanHorneClientSocketFactory = _sslVanHorneClientContext.getSocketFactory();

    }

    protected GarnettClientSession(
            GarnettClientSessionManager garnettClientSessionManager,
            InetSocketAddress serverAddress,
            String serverTypeName,
            String userName,
            byte[] obfuscatedPassword
    ) {
        super();

        _garnettClientSessionManager = garnettClientSessionManager;
        _serverAddress = serverAddress;
        _serverTypeName = serverTypeName;
        _userName = userName;
        _obfuscatedPassword = Arrays.copyOf( obfuscatedPassword, obfuscatedPassword.length );

    }

    public void connect() {


    }

    @Override
    protected void processInboundObject( GarnettObject obj ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
