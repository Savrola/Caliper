package com.obtuse.garnett.client;

import com.obtuse.garnett.*;
import com.obtuse.garnett.exceptions.*;
import com.obtuse.garnett.stdmsgs.GarnettLoginRequestMessage;
import com.obtuse.garnett.stdmsgs.GarnettLoginResponseMessage;
import com.obtuse.util.*;

import javax.management.timer.Timer;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.Arrays;

/*
 * Copyright © 2012 Daniel Boulet
 */

public class GarnettClientSession extends GarnettSession {

    private final GarnettClientSessionManager _garnettClientSessionManager;

    private final InetSocketAddress _serverAddress;

    private final GarnettComponentInstanceName _serverInstanceName;

    private final String _userName;

    private final byte[] _obfuscatedPassword;

    @SuppressWarnings({ "FieldCanBeLocal", "CanBeFinal" })
    private boolean _fromServerDone = false;

    @SuppressWarnings({ "FieldCanBeLocal", "UnusedDeclaration" })
    private boolean _toServerDone = false;

    private static final long INACTIVITY_TIMEOUT =
            DebugUtilities.inIntelliJIDEA() ? Timer.ONE_HOUR : Timer.ONE_MINUTE * 5L;

    @SuppressWarnings({ "FieldCanBeLocal", "CanBeFinal" })
    private int _activationCode = 0;

    protected GarnettClientSession(
            GarnettClientSessionManager garnettClientSessionManager,
            InetSocketAddress serverAddress,
            GarnettComponentInstanceName serverInstanceName,
            @SuppressWarnings("SameParameterValue") int podNumber,
            GarnettSessionType intendedGarnettSessionType,
            @SuppressWarnings("SameParameterValue") String userName,
            byte[] obfuscatedPassword
    )
            throws GarnettIllegalArgumentException {
        super(
                "to " + serverInstanceName + "@" + serverAddress + " by " + userName,
                new GarnettSessionPrefix( serverInstanceName, podNumber ),
                intendedGarnettSessionType
        );

        _garnettClientSessionManager = garnettClientSessionManager;
        _serverAddress = serverAddress;
        _serverInstanceName = serverInstanceName;
        _userName = userName;
        _obfuscatedPassword = Arrays.copyOf( obfuscatedPassword, obfuscatedPassword.length );

        // We're the client so we are by definition authenticated.

        setSessionIsAuthenticated();

    }

//    /**
//     * Construct the prefix used by Alfred to redirect this session to the right component.
//     * @param componentInstanceName the name of the target component.
//     * @param podNumber the target pod.
//     * @return the constructed prefix.
//     */
//
//    private static byte[] constructPrefix( GarnettComponentInstanceName componentInstanceName, int podNumber ) {
//
//        ByteBuffer bb = ByteBuffer.allocate( 100 + componentInstanceName.getInstanceNameBytesLength() );
//        bb.put( (byte)PROXIED_PROTOCOL_VERSION_CODE );
//        bb.putInt( podNumber );
//        bb.put( (byte)componentInstanceName.getInstanceNameBytesLength() );
//        bb.put( componentInstanceName.getInstanceNameBytes() );
//
//        byte[] prefix = new byte[bb.position()];
//        bb.flip();
//        bb.get( prefix );
//
//        return prefix;
//
//    }

    @SuppressWarnings("UnusedDeclaration")
    protected GarnettClientSessionManager getClientSessionManager() {

        return _garnettClientSessionManager;

    }

    @SuppressWarnings( { "RefusedBequest" } )
    public void doRun() {

        try {

            runSegmentedGarnettSession();
            sessionEnds( 5 );
            Trace.event( getClass().getName() + " ends" );

        } catch ( Throwable e ) {

            Logger.logErr( getClass().getName() + " session ended abruptly", e );
            sessionEnds();
        }

    }

    protected void runSegmentedGarnettSession() {

        try {

//            OutputStream toServer;
//            InputStream fromServer;

            // When did we start trying to make this work?

            long attemptStartedTime = System.currentTimeMillis();

            for ( int attempt = 1; true; attempt += 1 ) {

                if ( attempt > 1 ) {

                    ObtuseUtil5.safeSleepMillis( Timer.ONE_SECOND * (long)attempt );

                }

                // Limit the "let's get this thing going" game to five minutes.

                if ( System.currentTimeMillis() - attemptStartedTime > GarnettClientSession.INACTIVITY_TIMEOUT ) {

                    Logger.logMsg(
                            "Unable to launch proxy session " + getSessionName() + " after " +
                            GarnettClientSession.INACTIVITY_TIMEOUT / Timer.ONE_MINUTE + " minutes - giving up",
                            null
                    );

                    return;

                }

                // Our first task is to connect to our server's SLC listen port.

                try {

                    synchronized ( getSocketLock() ) {

                        if ( isDone() ) {

                            Logger.logMsg("we're done");
                            return;

                        }

                        setSocket( createSocket( _serverAddress ) );

                    }

                } catch ( ConnectException e ) {

                    Logger.logErr( "GenericClientSlcSession:  unable to connect to " + _serverAddress + " (" + e.getMessage() + ")" );

                    continue;

                } catch ( IOException e ) {

                    Logger.logErr( "GenericClientSlcSession:  unable to connect to " + _serverAddress, e );

                    continue;

                } catch ( GarnettUnsupportedProtocolVersionException e ) {

                    Logger.logErr( "GarnettClientSession:  server @ " + _serverAddress + " sent unsupported protocol version code ", e );

                    return;

                } catch ( GarnettIllegalArgumentException e ) {

                    Logger.logErr( "GarnettClientSession:  unable to set socket", e );

                    return;

                }

//                // Open the stream to our server.
//
//                try {
//
//                    synchronized ( getSocketLock() ) {
//
//                        if ( isDone() ) {
//
//                            Logger.logMsg("we're done #2");
//                            return;
//
//                        }
//
//                        toServer = getSocket().getOutputStream();
//
//                    }
//
//                } catch ( IOException e ) {
//
//                    Trace.emitTrace( "Unable to get output stream to " + _serverAddress, e );
//                    Logger.logFriendly( "Unable to get output stream to " + _serverAddress + " - " + e );
//                    Logger.logErr( "Unable to get output stream to " + _serverAddress, e );
//                    return;
//
//                }

                // Get and log the session prefix sent by the server.

                GarnettSessionPrefix sessionPrefix = getInboundObjectStream().getSessionPrefix();
                Logger.logMsg( "session prefix:  " + sessionPrefix );

                // Build a segmented login header.

                GarnettLoginRequestMessage loginMessage;
                try {

                    loginMessage = _activationCode == 0 ?
                       new GarnettLoginRequestMessage(
                               _userName,
                               _obfuscatedPassword
                        )
                        :
                        new GarnettLoginRequestMessage(
                                _userName,
                                _obfuscatedPassword,
                                _activationCode
                        );

                    if ( getAugmentedLoginData() != null ) {

                        loginMessage.setAugmentedData( getAugmentedLoginData() );

                    }

                } catch ( GarnettInvalidAccountNameException e ) {

                    Logger.logMsg( "unable to create generic proxy session - invalid user name", null );
                    return;

                }

                try {

                    sendMessage( loginMessage );

                } catch ( IOException e ) {

                    Logger.logFriendly(
                            "Unable to send login message to " + _serverInstanceName +
                            " on " + _serverAddress + " (retrying)"
                    );
                    Logger.logErr(
                            "Unable to send login message to " + _serverInstanceName +
                            " on " + _serverAddress,
                            e
                    );

                    continue;

                }

                // Get the response message.

                GarnettMessage responseMessage;
                try {

                    responseMessage = getNextMessage();

                } catch ( IOException e ) {

                    Logger.logFriendly( "Unable to get login response from " + _serverAddress + " (retrying)" );
                    Logger.logErr( "Unable to get login response from " + _serverAddress, e );

                    continue;

                }

                if ( responseMessage == null ) {

                    Logger.logFriendly( "Null login response from " + _serverAddress + " (retrying)" );
                    Logger.logErr( "Null login response from " + _serverAddress );

                    continue;

                }
                GarnettLoginResponseMessage response;
                try {

                    response = (GarnettLoginResponseMessage)responseMessage;

                } catch ( ClassCastException e ) {

                    Logger.logFriendly( "Bogus login response from " + _serverAddress + " (retrying)" );
                    Logger.logErr( "Bogus login response from " + _serverAddress, e );

                    continue;

                }

                try {

                    Trace.event(
                            "processing " +
                            (
                                    response == null
                                    ?
                                    null
                                    :
                                    response.getClass()
                            ) +
                              " message"
                    );

                    processInboundMessage( response );

                } catch ( GarnettNoMessageHandlerDefinedException e ) {

                    // this is ok since the login response is mostly for our benefit.

                }

                // If this is a null session then we're done.

                if ( isNullSession() ) {

                    sessionEnds();

                }

                if ( !response.worked() && getSessionType() != GarnettSessionType.NO_AUTH ) {

                    Logger.logErr(
                            "Unable to create Garnett session to " + _serverAddress + " (authentication failed)"
                    );
                    return;
                }

                // We're still here - that means that the authentication worked or was not needed.

                break;

            }

//            // If this is an actual SMTP session then there won't be any
//            // outbound messages.
//
//            if ( getUpstreamSession() != null ) {
//                outBoundQueueDone();
//            }

            // Time to launch the other i/o thread.

            //noinspection ClassWithoutToString
            new Thread( "proxy from " + getServerComponentName() + " for session " + getSessionName() ) {

                @SuppressWarnings( { "RefusedBequest" } )
                public void run() {

                    receiveAndProcessInboundMessages();

                }

            }.start();

            // Just process the outbound queue until we are told that it is done.

            processOutboundQueue();

        } finally {

            // We're done - is the other i/o thread also done?

            synchronized ( getSocketLock() ) {

                _toServerDone = true;

                if ( _fromServerDone ) {

                    sessionEnds( 5 );

                }

                didSomething();

            }

        }

    }

    protected final String getServerComponentName() {

        return _serverInstanceName.toString();

    }

    @SuppressWarnings("UnusedDeclaration")
    protected GarnettComponentInstanceName getServerType() {

        return _serverInstanceName;

    }

    @SuppressWarnings("UnusedDeclaration")
    public static boolean loggableException( IOException e ) {

        String msg = e.getMessage().toLowerCase();

        return !"socket closed".equals( msg ) &&
               !"socket is closed".equals( msg ) &&
               !"broken pipe".equals( msg ) &&
               !"connection closed by remote host".equals( msg );

    }

    @SuppressWarnings( { "CaughtExceptionImmediatelyRethrown" } )
    private SSLSocket createSocket( InetSocketAddress targetAddress )
            throws
            IOException {

        try {

            return (SSLSocket)getSslClientSocketFactory().createSocket(
                    targetAddress.getAddress(),
                    targetAddress.getPort()
            );

        } catch ( IOException e ) {

            // Just a place for a breakpoint.

            throw e;

        }

    }

    protected SSLSocketFactory getSslClientSocketFactory() {

        return GarnettClientSslTools.getSslClientSocketFactory();

    }

    @SuppressWarnings( { "RefusedBequest" } )
    public String toString() {

        return "GenericClientSlcSession( " + getSessionName() + " )";

    }

    protected void processInboundMessage( GarnettMessage message )
            throws
            GarnettNoMessageHandlerDefinedException {

        _garnettClientSessionManager.processMessage( this, message );

    }

    @SuppressWarnings("UnusedDeclaration")
    public GarnettComponentInstanceName getServerInstanceName() {

        return _serverInstanceName;

    }

}
