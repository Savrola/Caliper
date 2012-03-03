package com.obtuse.garnett.server;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.garnett.*;
import com.obtuse.garnett.client.GarnettClientSession;

import java.net.InetSocketAddress;

/*
 * Copyright © 2012 Daniel Boulet
 */

public abstract class GarnettSessionServerManager extends GarnettSessionManager {

    protected GarnettSessionServerManager( String name ) {
        super( name );

    }

    /**
     * Calls to this method are turned into calls to {@link #doRun}.
     */

    @SuppressWarnings( { "RefusedBequest" } )
    public final void run() {

        doRun();

    }

    /**
     * Effectively makes our run method abstract to ensure that implementations implement this method.
     */

    public abstract void doRun();

    public GarnettClientSession createSession( GarnettSessionType garnettSessionType ) {

        throw new HowDidWeGetHereError( "server's don't create command sessions (yet?)" );

    }

    public final InetSocketAddress getServerAddress() {

        return null;

    }

    public final GarnettComponentInstanceName getServerInstanceName() {

        return null;

    }

}
