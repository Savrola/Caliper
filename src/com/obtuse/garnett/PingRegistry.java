package com.obtuse.garnett;

import java.util.*;

/**
 * Track ping listeners in some context.
 * <p/>
 * Copyright © 2007, 2008 Daniel Boulet.
 */

public class PingRegistry implements PingListener {

    private final List<PingListener> _pingListeners = new LinkedList<PingListener>();

    private boolean _done = false;

    public PingRegistry() {

        super();

    }

    public synchronized void addListener( PingListener pingListener ) {

        for ( PingListener listeners : _pingListeners ) {
            //noinspection ObjectEquality
            if ( pingListener == listeners ) {
                return;
            }
        }
        _pingListeners.add( pingListener );

    }

    public synchronized void rememberRequestId( long requestId ) {

        for ( PingListener pingListener : _pingListeners ) {

            pingListener.rememberRequestId( requestId );

        }

    }

    public synchronized boolean gotPingReply( long requestId ) {

        //noinspection ForLoopWithMissingComponent
        for ( Iterator<PingListener> it = _pingListeners.iterator(); it.hasNext(); ) {

            PingListener pingListener = it.next();

            if ( !pingListener.gotPingReply( requestId ) ) {

                it.remove();

            }

        }

        return !_done;

    }

    public void done() {

        _done = true;

    }

    public synchronized String toString() {

        return "PingRegistry( " + _pingListeners.size() + " listeners )";

    }

}
