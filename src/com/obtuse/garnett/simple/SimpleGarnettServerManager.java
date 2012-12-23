package com.obtuse.garnett.simple;

/*
 * Copyright © 2012 Daniel Boulet
 */

import com.obtuse.garnett.GarnettComponentInstanceName;
import com.obtuse.garnett.GarnettSessionPrefix;
import com.obtuse.garnett.exceptions.*;
import com.obtuse.garnett.server.GarnettServerSslTools;
import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;

import java.io.*;
import java.net.*;

/**
 * A relatively easy to use Garnett server facility.
 */

@SuppressWarnings("SameParameterValue")
public class SimpleGarnettServerManager extends Thread {

    public static final int DEBUG_LISTEN_PORT = 1234;
    @SuppressWarnings("FieldCanBeLocal")
    private final InetSocketAddress _listenSocketAddress;
    @SuppressWarnings("FieldCanBeLocal")
    private final GarnettServerSslTools _serverSslTools;
    private final ServerSocket _listenSocket;
    private final String _what;
    private GarnettSessionPrefix _sessionPrefix;

    public SimpleGarnettServerManager(
            String what,
            GarnettComponentInstanceName serverInstanceName,
            int podNumber,
            GarnettServerSslTools serverSslTools,
            InetSocketAddress listenSocketAddress
    )
            throws IOException {
        super();

        _what = what;
        _serverSslTools = serverSslTools;
        _listenSocketAddress = listenSocketAddress;

        _listenSocket = _serverSslTools.getSslServerSocketFactory().createServerSocket();
        _listenSocket.setReuseAddress( true );
        _listenSocket.bind( listenSocketAddress );

        _sessionPrefix = new GarnettSessionPrefix( serverInstanceName, podNumber );

        Logger.logMsg( "listening for " + what + " on " + _listenSocketAddress );

    }

    public void run() {

        //noinspection InfiniteLoopStatement
        while ( true ) {

            Socket sessionSocket;

            try {

                sessionSocket = _listenSocket.accept();

            } catch ( IOException e ) {

                Logger.logErr( _what + ":  I/O exception on accept - session lost", e );
                continue;

            }

            String sessionName = "from " + sessionSocket.getRemoteSocketAddress();
            try {

                SimpleGarnettServerSession session = new SimpleGarnettServerSession( sessionName, _sessionPrefix, this, sessionSocket );
                session.start();

            } catch ( IOException e ) {

                Logger.logErr( "Unable to launch session " + sessionName, e );

            } catch ( GarnettIllegalArgumentException e ) {

                Logger.logErr( "Unable to launch session " + sessionName, e );

            } catch ( GarnettUnsupportedProtocolVersionException e ) {

                Logger.logErr( "Unable to launch session " + sessionName, e );

            }

        }

    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Caliper", "Garnett", null );

        GarnettServerSslTools serverSslTools = null;
        try {
            String keystoreFile = "Certs/GarnettServer.keystore";
            serverSslTools = new GarnettServerSslTools(
                    keystoreFile,
                    new FileInputStream( keystoreFile ),
                    "RumGrumble".toCharArray(),
                    "RumGrumble".toCharArray()
            );

        } catch ( GarnettSSLChannelCreationFailedException e ) {

            Logger.logErr( "unable to create SSL channel", e );
            System.exit( 1 );

        } catch ( FileNotFoundException e ) {

            Logger.logErr( "unable to open SSL cert file", e );
            System.exit( 1 );

        }

        SimpleGarnettServerManager manager = null;
        try {

            manager = new SimpleGarnettServerManager(
                    "test manager",
                    new GarnettComponentInstanceName( "Fred" ),
                    0,
                    serverSslTools,
                    new InetSocketAddress( "localhost", SimpleGarnettServerManager.DEBUG_LISTEN_PORT )

            );

        } catch ( IOException e ) {

            Logger.logErr( "unable to create server manager", e );
            System.exit( 1 );

        }

        manager.start();

    }

}
