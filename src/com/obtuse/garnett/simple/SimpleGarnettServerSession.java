package com.obtuse.garnett.simple;

/*
 * Copyright © 2012 Daniel Boulet
 */

import com.obtuse.garnett.*;
import com.obtuse.garnett.exceptions.GarnettIllegalArgumentException;
import com.obtuse.garnett.exceptions.GarnettUnsupportedProtocolVersionException;

import java.io.IOException;
import java.net.Socket;

/**
 * A simple Garnett-style server session.
 */

public class SimpleGarnettServerSession extends MinimalGarnettSession {

    @SuppressWarnings({ "FieldCanBeLocal", "UnusedDeclaration" })
    private final SimpleGarnettServerManager _serverManager;

    public SimpleGarnettServerSession(
            String sessionName,
            GarnettSessionPrefix sessionPrefix,
            SimpleGarnettServerManager serverManager,
            Socket socket
    )
            throws IOException, GarnettIllegalArgumentException, GarnettUnsupportedProtocolVersionException {
        super( sessionName, sessionPrefix );

        _serverManager = serverManager;
        setSocket( socket );

    }

    protected void doRun() {

        while ( !isDone() ) {

            try {

                @SuppressWarnings("UnusedDeclaration")
                GarnettMessage nextMessage = getNextMessage();

            } catch ( IOException e ) {

                //noinspection CallToPrintStackTrace
                e.printStackTrace();

            }

        }

    }

    @Override
    protected void majorSessionStateChange() {

    }

}
