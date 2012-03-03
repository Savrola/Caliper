package com.obtuse.garnett;

import com.obtuse.garnett.client.GarnettClientSession;
import com.obtuse.garnett.exceptions.*;
import com.obtuse.garnett.stdmsgs.*;
import com.obtuse.util.Logger;
import com.obtuse.util.Trace;

import java.net.InetSocketAddress;
import java.util.*;

/*
 * Copyright © 2012 Daniel Boulet
 */

@SuppressWarnings( { "ClassWithoutToString" } )
public abstract class GarnettSessionManager extends Thread {

    private final SortedMap<String, GarnettRequestResponseHandler> _requestResponseHandlers =
            new TreeMap<String, GarnettRequestResponseHandler>();

    private final SortedMap<String, GarnettMessageHandler> _messageHandlers = new TreeMap<String, GarnettMessageHandler>();

    private final static List<LoginResponseMessageHandler> _globalLoginResponseHandlers =
            new LinkedList<LoginResponseMessageHandler>();

    private boolean _authenticatedSessionManager;

    protected GarnettSessionManager( String name ) {
        super( name );
    }

    public void processMessage( GarnettSession garnettSession, GarnettMessage garnettMessage )
            throws
            GarnettNoMessageHandlerDefinedException {

        GarnettRequestResponseHandler requestResponseHandler = null;
        GarnettMessageHandler messageHandler = null;
        synchronized ( _messageHandlers ) {

            if ( garnettMessage instanceof GarnettRequestMessage ) {
                //noinspection RedundantCast
                requestResponseHandler =
                        _requestResponseHandlers.get( ( (GarnettRequestMessage)garnettMessage ).getClass().getCanonicalName() );
            }
            if ( requestResponseHandler == null ) {
                messageHandler = _messageHandlers.get( garnettMessage.getClass().getName() );
            }

        }

        if ( !( garnettMessage instanceof SlcUnauthenticatedMessage ) && !garnettSession.isAuthenticated() ) {

            // A message requiring authentication has arrived via an unauthenticated channel.
            // Log and drop the message.

            Logger.logErr(
                    "Message requiring authentication arrived on unauthenticated channel \"" +
                    garnettSession.getSessionName() + "\" - dropped"
            );
            return;

        }

        if ( garnettMessage instanceof GarnettLoginResponseMessage ) {

            List<LoginResponseMessageHandler> handlers = new LinkedList<LoginResponseMessageHandler>();
            synchronized ( _globalLoginResponseHandlers ) {

                handlers.addAll( _globalLoginResponseHandlers );

            }

            for ( LoginResponseMessageHandler handler : handlers ) {

                handler.processMessage( garnettSession, garnettMessage );

            }

        }

        if ( requestResponseHandler != null ) {

            String[] requiredLoaCapabilities = requestResponseHandler.requiredCapabilities();
            String[] forbiddenLoaCapabilities = requestResponseHandler.forbiddenCapabilities();

            boolean authenticationRequired = requiredLoaCapabilities != null && requiredLoaCapabilities.length > 0 ||
                                             forbiddenLoaCapabilities != null && forbiddenLoaCapabilities.length > 0 ||
                                             requestResponseHandler.authenticationRequired();

            // While arguably not necessary given the existence of the SlcUnauthenticatedMessage
            // interface, being a bit more paranoid just seems to make sense.

            if ( authenticationRequired && !garnettSession.isAuthenticated() ) {

                Logger.logErr(
                        "Request requiring authentication arrived on unauthenticated channel \"" +
                        garnettSession.getSessionName() + "\" - dropped"
                );
                return;

            }

            GarnettResponseMessage garnettResponseMessage = null;

            if ( garnettSession instanceof KnowsCapabilities ) {

                if ( !( (KnowsCapabilities)garnettSession ).checkCapabilities(
                        requiredLoaCapabilities, forbiddenLoaCapabilities
                ) ) {

                    // The type of this response might not be acceptable to the requestor.
                    // Should that be the case, the SlcRequestManager will replace it with a suitable error response.

                    garnettResponseMessage = new GarnettShortResponse();

                }

            }

            if ( garnettResponseMessage == null ) {

                garnettResponseMessage = requestResponseHandler.processMessage(
                        garnettSession, (GarnettRequestMessage)garnettMessage
                );

            }

            // Make sure that there is a response.
            // The type of this auto-generated response might not be acceptable to the requestor.
            // Should that be the case, the SlcRequestManager will replace it with a suitable error response.

            if ( garnettResponseMessage == null ) {

                garnettResponseMessage = new GarnettShortResponse();

            }

            try {

                garnettSession.queueOutBoundMessage( garnettResponseMessage );
                if ( requestResponseHandler.isOutBoundQueueDone() ) {
                    garnettSession.outBoundQueueDone();
                }

            } catch ( GarnettOutBoundQueueDoneException e ) {

                Logger.logMsg(
                        "outbound queue for session " + garnettSession.getSessionName() +
                        " closed when sending response to " + garnettMessage.getClass()
                );

            }

        } else {

            if ( messageHandler == null ) {

                // Message handlers are optional for login response messages.

                if ( garnettMessage instanceof GarnettLoginResponseMessage ) {
                    return;
                }

                Trace.event( "no handler for " + garnettMessage.getClass().getName() + " messages" );
                throw new GarnettNoMessageHandlerDefinedException(
                        "no handler for " + garnettMessage.getClass().getName() + " messages"
                );

            }

            Trace.event( "passing " + garnettMessage.getClass() + " message to handler" );

            messageHandler.processMessage( garnettSession, garnettMessage );

        }

    }

    public abstract GarnettClientSession createSession( GarnettSessionType garnettSessionType )
            throws GarnettIllegalArgumentException;

    public abstract InetSocketAddress getServerAddress();

    public abstract GarnettComponentInstanceName getServerInstanceName();

    public static void registerGlobalLoginResponseHandler( LoginResponseMessageHandler handler ) {

        synchronized ( _globalLoginResponseHandlers ) {

            removeGlobalLoginResponseHandler( handler );
            _globalLoginResponseHandlers.add( handler );

        }

    }

    public static void removeGlobalLoginResponseHandler( LoginResponseMessageHandler handler ) {

        synchronized ( _globalLoginResponseHandlers ) {

            _globalLoginResponseHandlers.remove( handler );

        }

    }

    public void registerMessageHandler(
            Class<? extends GarnettMessage> messageClass,
            GarnettMessageHandler messageHandler
    ) {

        synchronized ( _messageHandlers ) {

            if ( messageHandler == null ) {

                _messageHandlers.remove( messageClass.getName() );

            } else {

                _messageHandlers.put( messageClass.getName(), messageHandler );

            }

        }

    }

    public void registerRequestResponseHandler(
            Class<? extends GarnettRequestMessage> messageClass,
            GarnettRequestResponseHandler requestResponseHandler
    ) {

        synchronized ( _messageHandlers ) {

            if ( requestResponseHandler == null ) {

                _requestResponseHandlers.remove( messageClass.getCanonicalName() );

            } else {

                _requestResponseHandlers.put( messageClass.getCanonicalName(), requestResponseHandler );

            }

        }

    }

    protected void setAuthenticatedSessionManager( boolean authenticatedSessionManager ) {

        _authenticatedSessionManager = authenticatedSessionManager;

    }

    public void sendPingRequest( final PingRegistry pingRegistry, PingListener pingListener )
            throws GarnettIllegalArgumentException {

        pingRegistry.addListener( pingListener );

        GarnettPingRequestMessage request = new GarnettPingRequestMessage();

        pingRegistry.rememberRequestId( request.getRequestId() );

        registerMessageHandler(
                GarnettPingResponseMessage.class,
                new GarnettMessageHandler() {

                    public void processMessage( GarnettSession garnettSession, GarnettMessage garnettMessage ) {

                        pingRegistry.gotPingReply( ( (GarnettPingResponseMessage)garnettMessage ).getRequestId() );

                        garnettSession.sessionEnds();

                    }

                }
        );

        // Use a command session or a no-auth session depending on how we were created.

        GarnettSession garnettSession =
                createSession(
                        isAuthenticatedSessionManager()
                        ? GarnettSessionType.COMMAND
                        : GarnettSessionType.NOAUTH
                );

        try {

            garnettSession.queueOutBoundMessage( request );

        } catch ( GarnettOutBoundQueueDoneException e ) {

            Trace.emitTrace(
                    "Neville client session created to send ping is done before we had a chance to use it", e
            );
            return;

        }

        garnettSession.outBoundQueueDone();

        garnettSession.start();

    }

    public boolean isAuthenticatedSessionManager() {

        return _authenticatedSessionManager;

    }

}
