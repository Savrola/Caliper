package com.obtuse.garnett.client;

/*
 * Copyright © 2012 Daniel Boulet
 */

import com.obtuse.garnett.*;
import com.obtuse.garnett.exceptions.GarnettIllegalArgumentException;
import com.obtuse.util.Logger;

import java.net.InetSocketAddress;
import java.net.ServerSocket;

/**
 * %%% something clever goes here.
 */

public class GarnettClientSessionManager extends GarnettSessionManager {

    private ServerSocket _newSmtpProxyListenSocket;

    @SuppressWarnings({ "FieldAccessedSynchronizedAndUnsynchronized" })
    private ServerSocket _currentSmtpProxyListenSocket = null;

    private InetSocketAddress _cachedServerAddress = null;

    private final Long _cachedServerAddressLock = new Long( 0L );

    private boolean _done = false;

//    private boolean _usingAlfred;

    private final GarnettComponentInstanceName _componentInstanceName;

    public GarnettClientSessionManager( GarnettComponentInstanceName componentInstanceName ) {

        super( "Tungsten's SMTP proxy manager" );
        _componentInstanceName = componentInstanceName;

        setAuthenticatedSessionManager( true );
    }

    @SuppressWarnings({ "RefusedBequest" })
    public void run() {

        // don't need this for simple clients

    }

//    public GarnettClientSession createSession( GarnettSessionType garnettSessionType ) {
//
//        //noinspection ObjectToString
//        Trace.event(
//                "GarnettClientSessionManager creating " + garnettSessionType + " command session to " + getServerAddress()
//        );
//
//        return createSession( garnettSessionType );
//
//    }

    public GarnettClientSession createSession( GarnettSessionType garnettSessionType )
            throws GarnettIllegalArgumentException {

        GarnettClientSession garnettClientSession = new GarnettClientSession(
                this,
                getServerAddress(),
                getServerInstanceName(),
                0,
                "fred",
                new byte[] { 1, 2, 3 }
        );
        garnettClientSession.setSessionType( garnettSessionType );
        return garnettClientSession;

    }

//    public GarnettClientSession createInboundMailGarnettSession(
//            TungstenInboundMailServerConfiguration config,
//            Socket newInboundSocket
//    ) {
//
//        // We've got a new in-bound SMTP session - launch a session proxy.
//
//        GarnettClientSession garnettClientSession = new GarnettClientSession(
//                newInboundSocket,
//                this
//        );
//
//        garnettClientSession.setChannelType( GarnettSessionType.INBOUND_PROXY );
//        garnettClientSession.setAugmentedLoginData( config );
//
//        return garnettClientSession;
//
//    }

    public InetSocketAddress getServerAddress() {

        synchronized ( _cachedServerAddressLock ) {

            if ( _cachedServerAddress == null ) {

                _cachedServerAddress = new InetSocketAddress(
                        DebugUtilities.getAlfredHostname(),
                        DebugUtilities.getAlfredListenPort()
                );
                Logger.logMsg(
                        "allocating a new socket address to Mackenzie via Alfred @ " + _cachedServerAddress
                );

            }

            return _cachedServerAddress;

        }

    }

    public void flushServerAddress() {

        synchronized ( _cachedServerAddressLock ) {

            Logger.logMsg( "flushing socket address to Mackenzie" );

            _cachedServerAddress = null;

        }

    }

    public GarnettComponentInstanceName getServerInstanceName() {

        return _componentInstanceName;

    }

    public String toString() {
        //noinspection ObjectToString
        return "TungstenSmtpProxyManager( SMTP proxy socket channel " +
               (
                       _currentSmtpProxyListenSocket == null ? "null" :
                       _currentSmtpProxyListenSocket.getLocalSocketAddress()
               ) +
               ", Mackenzie's SMTP address " + getServerAddress() + ", " + super.toString() + " )";
    }

}
