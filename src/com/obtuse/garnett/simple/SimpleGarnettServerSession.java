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

                GarnettMessage nextMessage = getNextMessage();

            } catch ( IOException e ) {

                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

            }

        }

    }

    @Override
    protected void majorSessionStateChange() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
