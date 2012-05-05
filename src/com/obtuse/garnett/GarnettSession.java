package com.obtuse.garnett;

/*
 * Copyright © 2012 Daniel Boulet
 */

import com.obtuse.garnett.exceptions.*;
import com.obtuse.util.Logger;
import com.obtuse.util.Trace;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Manage the sending and receiving of messages through a Garnett session.
 */

public abstract class GarnettSession extends MinimalGarnettSession {

    private final Queue<GarnettMessage> _outboundMessageQueue = new LinkedList<GarnettMessage>();

    private boolean _outboundQueueDone = false;

    private boolean _isNullSession = false;

    private GarnettObject _augmentedLoginData = null;

    protected GarnettSession( String sessionName, GarnettSessionPrefix sessionPrefix, GarnettSessionType intendedSessionType )
            throws GarnettIllegalArgumentException {
        super( sessionName, sessionPrefix, intendedSessionType );

        didSomething();

    }

    protected void majorSessionStateChange() {

        synchronized ( _outboundMessageQueue ) {

            _outboundMessageQueue.notifyAll();

        }

    }

    //    public void run() {
//
//        _inboundDone = false;
//
//        while ( !_inboundDone ) {
//
//            synchronized ( _inboundLock ) {
//
//                while ( _socket == null ) {
//
//                    try {
//
//                        _inboundLock.wait();
//
//                    } catch ( InterruptedException e ) {
//
//                        // Just go around again
//
//                    }
//
//                }
//
//                try {
//
//                    GarnettObject obj = _inbound.readOptionalGarnettObject();
//                    processInboundObject( obj );
//
//                } catch ( IOException e ) {
//
//                    Logger.logErr( "GarnettSession:  I/O exception reading from socket", e );
//                    _inboundDone = true;
//
//                }
//
//            }
//
//        }
//
//        // Wakeup anyone waiting on our instance in case they are waiting for input to complete.
//
//        synchronized ( this ) {
//
//            notifyAll();
//
//        }
//
//    }

    public GarnettObject getAugmentedLoginData() {

        return _augmentedLoginData;

    }

    @SuppressWarnings("UnusedDeclaration")
    public void setAugmentedLoginData( GarnettObject augmentedLoginData ) {

        _augmentedLoginData = augmentedLoginData;

    }


    public void outBoundQueueDone() {

        synchronized ( _outboundMessageQueue ) {

            _outboundQueueDone = true;
            _outboundMessageQueue.notifyAll();

        }

    }

//    protected void sendFirstProxyingMessage(
//            byte code,
//            String componentTypeName,
//            int podNumber,
//            byte proxiedCode,
//            OutputStream outputStream,
//            GarnettMessage message
//    )
//            throws IOException {
//
//        byte[] componentTypeNameBytes = componentTypeName.getBytes();
//        if ( componentTypeNameBytes.length > 255 ) {
//
//            throw new IllegalArgumentException(
//                    "target component type name too long (max 255, is " + componentTypeNameBytes.length +
//                    ", name is \"" + componentTypeName + "\")"
//            );
//
//        }
//
//        byte[] prefix = new byte[1 + 1 + 1 + componentTypeNameBytes.length];
//        prefix[0] = code;
//        prefix[1] = (byte)podNumber;
//        prefix[2] = (byte)componentTypeNameBytes.length;
//        System.arraycopy( componentTypeNameBytes, 0, prefix, 3, componentTypeNameBytes.length );
//
//        sendMessage( prefix, message );
//
//    }

    /**
     * Send an SLC message with an optional proxying prefix.
     * The proxying prefix only appears for the SLC login message at the start of a session
     * which is being proxied via Alfred.  The proxying prefix is sent prior to the regular
     * segment header and the payload.  Care is taken to ensure that the optional prefix,
     * the segment header and the payload are sent using a single write() call to ensure that
     * everything goes out via a single TCP/IP message (unless the message is quite long).
     *
     * @param prefix       the optional proxying prefix.
     * @param message      the SLC message to send.
     * @throws IOException if something goes wrong in socket-land.
     */
//
//    protected void sendMessage( byte[] prefix, GarnettMessage message )
//            throws IOException {
//
//        if ( prefix != null ) {
//
//            _outbound.writeOptionalPrefix( prefix );
//
//        }
//
//        byte[] compactedMessage = PureXD.serialize( message );
//        byte[] segment = new byte[prefixLength + SEGMENT_HEADER_LENGTH + compactedMessage.length];
//        if ( prefix != null ) {
//
//            System.arraycopy( prefix, 0, segment, 0, prefix.length );
//
//        }
//
//        System.arraycopy( compactedMessage, 0, segment, prefixLength + SEGMENT_HEADER_LENGTH, compactedMessage.length );
//        Trace.event( "sending " + message.getClass() + " SLC message" );
//        Logger.logMsg( "sending " + message.getClass() + " SLC message" );
//        sendSegment( outputStream, segment, prefixLength, code, compactedMessage.length );
//        didSomething();
//
//        if ( message instanceof SlcMonsterMessage ) {
//
//            SlcMonsterMessage monsterMessage = (SlcMonsterMessage)message;
//            byte[] nextSegment;
//
//            while ( ( nextSegment = monsterMessage.getNextSegment( MAXIMUM_SEGMENT_SIZE ) ) != null ) {
//
//                segment = new byte[nextSegment.length + SEGMENT_HEADER_LENGTH];
//                System.arraycopy( nextSegment, 0, segment, SEGMENT_HEADER_LENGTH, nextSegment.length );
//                Trace.event( "sending monster segment with length " + nextSegment.length );
//                sendSegment( outputStream, segment, 0, MONSTER_MESSAGE_SEGMENT_CODE, nextSegment.length );
//
//            }
//
//            segment = new byte[SEGMENT_HEADER_LENGTH];
//            sendSegment( outputStream, segment, 0, MONSTER_MESSAGE_SEGMENT_CODE, 0 );
//
//        }
//
//    }

//    protected synchronized void sendObject( GarnettObject obj )
//            throws IOException {
//
//        sendObjectNoFlush( obj );
//        flush();
//
//    }

    /**
     * Mark this as a null session. A null session is a session which does nothing other than login to the target
     * server. In other words, whatever side-effects result from the login attempt fulfil the entire purpose of the
     * session.
     * <p/>
     * This method should be called before the session is started.
     */

    @SuppressWarnings("UnusedDeclaration")
    public void setNullSession() {

        outBoundQueueDone();
        _isNullSession = true;

    }

    public boolean isNullSession() {

        return _isNullSession;

    }

    public void queueOutBoundMessage( GarnettMessage garnettMessage )
            throws
            GarnettOutBoundQueueDoneException {

        synchronized ( _outboundMessageQueue ) {

            if ( _outboundQueueDone ) {

                throw new GarnettOutBoundQueueDoneException( "out-bound queue is done" );

            }

            Trace.event( "queueing " + garnettMessage.getClass() + " SLC message" );
            _outboundMessageQueue.add( garnettMessage );
            _outboundMessageQueue.notifyAll();

        }

    }

    protected void receiveAndProcessInboundMessages() {

        while ( true ) {

            GarnettMessage nextMessage = null;
            try {

                nextMessage = getNextMessage();
                if ( nextMessage == null ) {

                    return;

                }

            } catch ( Throwable e ) {

                Logger.logErr(
                        "caught an exception reading the next message for session " + getSessionName() +
                        " - session ends in five seconds", e
                );
                sessionEnds( 5 );

            }

            try {

                processInboundMessage( nextMessage );

            } catch ( Throwable e ) {

                Logger.logErr(
                        "caught an exception processing " + nextMessage + " for session " + getSessionName() +
                        " - session ends in five seconds", e
                );
                sessionEnds( 5 );
            }

        }

    }

    protected abstract void processInboundMessage( GarnettMessage nextMessage )
            throws GarnettNoMessageHandlerDefinedException;

    protected void processOutboundQueue() {

        try {
            while ( true ) {

                synchronized ( _outboundMessageQueue ) {

                    if ( _outboundQueueDone ) {

                        return;

                    }

                    while ( _outboundMessageQueue.isEmpty() ) {

                        try {

                            _outboundMessageQueue.wait();

                        } catch ( InterruptedException e ) {

                            // Just ignore these.

                        }

                        if ( isDone() ) {

                            return;

                        }

                    }

                    GarnettMessage nextMessage = _outboundMessageQueue.remove();

                    try {

                        sendMessage( nextMessage );

                    } catch ( IOException e ) {

                        Logger.logErr(
                                "unable to send queued message \"" + nextMessage + "\" on session " + getSessionName() +
                                " - session ends in five seconds",
                                e
                        );

                        sessionEnds( 5 );

                        return;

                    }

                }

            }

        } finally {

            if ( !_outboundMessageQueue.isEmpty() ) {

                Logger.logMsg(
                        "" + _outboundMessageQueue.size() + " messages abandoned on outbound queue for session " +
                        getSessionName()
                );

            }

        }

    }

    @SuppressWarnings("UnusedDeclaration")
    public synchronized void flush()
            throws IOException {

        if ( getOutboundObjectStream() != null ) {

            getOutboundObjectStream().flush();

        }

    }

}
