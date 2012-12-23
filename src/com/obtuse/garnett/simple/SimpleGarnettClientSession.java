package com.obtuse.garnett.simple;

/*
 * Copyright © 2012 Daniel Boulet
 */

import com.obtuse.garnett.*;
import com.obtuse.garnett.client.GarnettClientSslTools;
import com.obtuse.garnett.exceptions.*;
import com.obtuse.garnett.stdmsgs.GarnettLoginRequestMessage;
import com.obtuse.garnett.stdmsgs.GarnettLoginResponseMessage;
import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * An easy to use Garnett client facility.
 */

@SuppressWarnings("SameParameterValue")
public class SimpleGarnettClientSession extends MinimalGarnettSession {

    private final InetSocketAddress _serverAddress;
    @SuppressWarnings({ "FieldCanBeLocal", "UnusedDeclaration" })
    private final GarnettComponentInstanceName _serverInstanceName;
    @SuppressWarnings({ "FieldCanBeLocal", "UnusedDeclaration" })
    private final int _pod;
    private SSLSocketFactory _sslSocketFactory;
    private static final int DEFAULT_LOGIN_RETRY_COUNT = 5;
    private static final int MAXIMUM_LOGIN_RETRY_COUNT = 20;

    public SimpleGarnettClientSession(
            InetSocketAddress serverAddress,
            GarnettComponentInstanceName serverInstanceName,
            GarnettSessionType intendedSessionType,
            int pod
    )
            throws GarnettIllegalArgumentException {
        super( "to " + serverAddress, new GarnettSessionPrefix( serverInstanceName, pod ), intendedSessionType );

        _serverAddress = serverAddress;
        _serverInstanceName = serverInstanceName;
        _pod = pod;

        _sslSocketFactory = GarnettClientSslTools.getSslClientSocketFactory();

    }

    public void connect()
            throws IOException, GarnettUnsupportedProtocolVersionException, GarnettIllegalArgumentException {

        Socket socket = _sslSocketFactory.createSocket( _serverAddress.getAddress(),
                                                        SimpleGarnettServerManager.DEBUG_LISTEN_PORT
        );
        setSocket( socket );

    }

    public GarnettLoginResponseMessage doLogin(
            String userName,
            byte[] obfuscatedPassword
    )
            throws GarnettInvalidAccountNameException, IOException {

        return doLogin( userName, obfuscatedPassword, null, null, SimpleGarnettClientSession.DEFAULT_LOGIN_RETRY_COUNT );

    }

    public GarnettLoginResponseMessage doLogin(
            String userName,
            byte[] obfuscatedPassword,
            @Nullable Integer optionalActivationCode,
            @Nullable GarnettObject optionalAugmentedLoginData,
            int maxAttemptCount
    )
            throws GarnettInvalidAccountNameException, IOException {

        int attempt = 0;
        boolean moreRetriesAllowed = true;
        //noinspection UnnecessaryParentheses
        while ( moreRetriesAllowed ) {

            attempt += 1;
            //noinspection UnnecessaryParentheses
            moreRetriesAllowed = attempt < (
                        maxAttemptCount == 1
                        ?
                        1
                        :
                        (
                                maxAttemptCount > SimpleGarnettClientSession.MAXIMUM_LOGIN_RETRY_COUNT
                                ?
                                SimpleGarnettClientSession.MAXIMUM_LOGIN_RETRY_COUNT
                                :
                                maxAttemptCount
                        )
                );

            GarnettLoginRequestMessage loginMessage = optionalActivationCode == null ?
                                                      new GarnettLoginRequestMessage(
                                                              userName,
                                                              obfuscatedPassword
                                                      )
                                                                                     :
                                                      new GarnettLoginRequestMessage(
                                                              userName,
                                                              obfuscatedPassword,
                                                              optionalActivationCode.intValue()
                                                      );

            if ( optionalAugmentedLoginData != null ) {

                loginMessage.setAugmentedData( optionalAugmentedLoginData );

            }

            sendMessage( loginMessage );

//        Logger.logFriendly(
//                "Unable to send login message to " + _serverInstanceName +
//                " on " + _serverAddress + " (retrying)"
//        );
//        Logger.logErr(
//                "Unable to send login message to " + _serverInstanceName +
//                " on " + _serverAddress,
//                e
//        );

            // Get the response message.

            GarnettMessage responseMessage;
            try {

                responseMessage = getNextMessage();

            } catch ( IOException e ) {

                Logger.logFriendly(
                        "Unable to get login response from " + _serverAddress +
                        ( moreRetriesAllowed ? " (retrying)" : "" )
                );
                Logger.logErr( "Unable to get login response from " + _serverAddress, e );

                continue;

            }

            if ( responseMessage == null ) {

                Logger.logFriendly(
                        "Null login response from " + _serverAddress +
                        ( moreRetriesAllowed ? " (retrying)" : "" )
                );
                Logger.logErr( "Null login response from " + _serverAddress );

                continue;

            }
            GarnettLoginResponseMessage response;
            try {

                response = (GarnettLoginResponseMessage)responseMessage;

            } catch ( ClassCastException e ) {

                Logger.logFriendly(
                        "Bogus login response from " + _serverAddress +
                        ( moreRetriesAllowed ? " (retrying)" : "" )
                );
                Logger.logErr( "Bogus login response from " + _serverAddress, e );

                continue;

            }

            return response;

        }

        return null;
    }

    public void majorSessionStateChange() {

    }

    public void doRun() {

        Logger.logMsg( "this should not be called" );

    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Caliper", "Garnett", null );

        SimpleGarnettClientSession session = null;

        try {

            session = new SimpleGarnettClientSession(
                    new InetSocketAddress( "localhost", SimpleGarnettServerManager.DEBUG_LISTEN_PORT ),
                    new GarnettComponentInstanceName( "Fred" ),
                    GarnettSessionType.COMMAND,
                    1
            );

        } catch ( GarnettIllegalArgumentException e ) {

            Logger.logErr( "unable to create session", e );
            System.exit( 1 );

        }

        try {

            session.connect();

        } catch ( IOException e ) {

            Logger.logErr( "unable to connect to server", e );
            System.exit( 1 );

        } catch ( GarnettUnsupportedProtocolVersionException e ) {

            Logger.logErr( "unable to connect to server", e );
            System.exit( 1 );

        } catch ( GarnettIllegalArgumentException e ) {

            Logger.logErr( "unable to connect to server", e );
            System.exit( 1 );

        }

        Logger.logMsg( "connected to server" );

        try {

            session.doLogin( "danny", new byte[] { 1, 2, 3, 4 } );

        } catch ( IOException e ) {

            Logger.logErr( "unable to connect to server", e );
            System.exit( 1 );

        } catch ( GarnettInvalidAccountNameException e ) {

            Logger.logErr( "unable to connect to server", e );
            System.exit( 1 );

        }

        Logger.logMsg( "logged in" );

    }

}
