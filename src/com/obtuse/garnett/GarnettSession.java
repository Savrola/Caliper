package com.obtuse.garnett;

/*
 * Copyright © 2012 Daniel Boulet
 */

import com.obtuse.garnett.exceptions.*;
import com.obtuse.util.*;

import javax.management.timer.Timer;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

/**
 * Manage the sending and receiving of messages through a Garnett session.
 */

public abstract class GarnettSession extends Thread {

//    private Queue<GarnettObject> _outboundQueue = new LinkedList<GarnettObject>();

    private Socket _socket;

    private final Long _socketLock = 0L;

    private GarnettObjectInputStreamInterface _inbound;
    private GarnettObjectOutputStreamInterface _outbound;

    private boolean _inboundDone = false;

    private final Long _inboundLock = 0L;
    private final String _sessionName;

    private final long _sessionId;

    private boolean _done = false;

    private final Queue<GarnettMessage> _outboundMessageQueue = new LinkedList<GarnettMessage>();

    private boolean _outboundQueueDone = false;

    private GarnettSessionType _sessionType;

    private boolean _isNullSession;

    private GarnettObject _augmentedLoginData;

    private final GarnettSessionPrefix _sessionPrefix;

    public static final SortedMap<Long,GarnettSession> _sessions = new TreeMap<Long, GarnettSession>();
    private static int _maxSessionsActive;
    private static int _totalSessionCount;
    private long _lastActivityTime;

    private static final long INACTIVITY_TIMEOUT = DebugUtilities.inIntelliJIDEA() ? Timer.ONE_HOUR : Timer.ONE_MINUTE;

    public static final int PROXIED_PROTOCOL_VERSION_CODE = 0x10;
    private boolean _authenticated = false;

    static {

        //noinspection ClassWithoutToString,RefusedBequest
        new Thread( "GarnettSession DMS" ) {

            public void run() {

                runDms();

            }

        }.start();

    }

    protected GarnettSession( String sessionName, GarnettSessionPrefix sessionPrefix )
            throws GarnettIllegalArgumentException {
        super();

        _sessionName = sessionName;
        _sessionPrefix = sessionPrefix;

        if ( sessionPrefix == null ) {

            throw new GarnettIllegalArgumentException( "session prefix is null" );

        }

        didSomething();

        synchronized ( _sessions ) {

            long sessionId;

            while ( _sessions.containsKey( sessionId = RandomCentral.nextLong() ) ) {

                // Just keep spinning.

                // Simple probability theory for a 64-bit random value says that the likelihood
                // that we'll get a collision on even the first attempt is vanishingly small.

                // IMPORTANT:  the code in this loop needs to be VERY simple as it is pretty much impossible to test.

                Logger.logMsg( "SMTPServerManager:  going around again on cookie generation (informational)" );

                // We've been hit by lightning (colloquially speaking).
                // Celebrate by sleeping for a second (throttle log file growth a bit if things get stupid).

                ObtuseUtil5.safeSleepMillis( Timer.ONE_SECOND );

            }

            _sessions.put( sessionId, this );

            _totalSessionCount += 1;

            if ( _sessions.size() > _maxSessionsActive ) {

                _maxSessionsActive = _sessions.size();

                String msg = "new session " + getSessionName() +
                             " brings historical maximum number of sessions to " + _sessions.size();

                Logger.logMsg( msg );

            }

            Trace.event( "new session " + getSessionName() );

            _sessionId = sessionId;

        }

    }

    public boolean isDone() {

        return _done;

    }

    protected void setSocket( Socket socket )
            throws IOException, GarnettIllegalArgumentException, GarnettUnsupportedProtocolVersionException {

        synchronized ( getSocketLock() ) {

            if ( socket == _socket ) {

                return;

            }

            //noinspection ConstantConditions
            if ( socket == null && _socket != null ) {

                throw new GarnettIllegalArgumentException( "unable to set socket to null once it has been set to non-null" );

            }

            ObtuseUtil5.closeQuietly( _inbound );
            ObtuseUtil5.closeQuietly( _outbound );
            ObtuseUtil5.closeQuietly( _socket );

            _outbound = null;
            _inbound = null;
            _socket = null;

            _socket = socket;
            _inbound = new GarnettObjectInputStream( socket.getInputStream() );
            _outbound = new GarnettObjectOutputStream( socket.getOutputStream(), _sessionPrefix );

        }

        synchronized ( _inboundLock ) {

            _inboundLock.notifyAll();

        }

    }

    protected Socket getSocket() {

        return _socket;

    }

    protected GarnettObjectInputStreamInterface getInboundObjectStream() {

        return _inbound;

    }

    protected GarnettObjectOutputStreamInterface getOutboundObjectStream() {

        return _outbound;

    }

    public GarnettSessionType getSessionType() {

        return _sessionType;

    }

    public void setSessionType( GarnettSessionType sessionType ) {

        if ( _sessionType != null ) {

            throw new IllegalArgumentException( "session type already" );

        }

        _sessionType = sessionType;

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

    public void setAugmentedLoginData( GarnettObject augmentedLoginData ) {

        _augmentedLoginData = augmentedLoginData;

    }

    public void sessionEnds( int delaySeconds ) {

        long waitStartTime = System.currentTimeMillis();

        //noinspection MagicNumber
        while ( System.currentTimeMillis() - waitStartTime < Timer.ONE_SECOND * (long)delaySeconds ) {

            ObtuseUtil5.safeSleepMillis( Timer.ONE_SECOND );

        }

        sessionEnds();

    }

    public void sessionEnds() {

        // Say we're done.

        _done = true;

        // Remove ourselves from the sessions table and notify anyone waiting on the table.

        boolean knownSession = false;
        int remaining = 0;
        synchronized ( _sessions ) {

            if ( _sessions.containsKey( getSessionId() ) ) {

                // Remove it from our table.

                _sessions.remove( getSessionId() );
                remaining = _sessions.size();
                Trace.event( "dropping session " + getSessionName() + " (" + remaining + " left)" );

                knownSession = true;

            }

            //noinspection NotifyWithoutCorrespondingWait
            _sessions.notifyAll();

        }

        // This session is done.

        _done = true;

        // Get the word out to anyone waiting on the outbound queue.

        synchronized ( _outboundMessageQueue ) {

            _outboundMessageQueue.notifyAll();

        }

        synchronized ( getSocketLock() ) {

            Trace.event( "closing sockets in sessionEnds" );
            ObtuseUtil5.closeQuietly( _socket );
            _socket = null;

        }

        logClose();

        // Now that we're out of the critical section, force the session to terminate
        // if it isn't already dead and then report on its death.

        if ( knownSession ) {

            String msg = "SLC session " + getSessionName() + " ends (" + remaining + " sessions still active, " +
                         _maxSessionsActive + " max sessions active)";

            Trace.event( msg );

            // The next line is debug output so don't delete the Trace.event() call above!

//            Logger.logMsg( msg );

        } else {

            Trace.event( "unknown session ends " + getSessionName() );

        }

    }

    protected void logClose() {

        Trace.event( "logClose on " + getSessionName() );

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

//    /**
//     * Send an SLC message with an optional proxying prefix.
//     * The proxying prefix only appears for the SLC login message at the start of a session
//     * which is being proxied via Alfred.  The proxying prefix is sent prior to the regular
//     * segment header and the payload.  Care is taken to ensure that the optional prefix,
//     * the segment header and the payload are sent using a single write() call to ensure that
//     * everything goes out via a single TCP/IP message (unless the message is quite long).
//     *
//     * @param prefix       the optional proxying prefix.
//     * @param message      the SLC message to send.
//     * @throws IOException if something goes wrong in socket-land.
//     */
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

    protected synchronized void sendObject( GarnettObject obj )
            throws IOException {

        sendObjectNoFlush( obj );
        flush();

    }

    protected synchronized void sendMessage( GarnettMessage message )
            throws IOException {

        sendObjectNoFlush( message );

    }

    /**
     * Mark this as a null session. A null session is a session which does nothing other than login to the target
     * server. In other words, whatever side-effects result from the login attempt fulfil the entire purpose of the
     * session.
     * <p/>
     * This method should be called before the session is started.
     */
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

    public synchronized void sendObjectNoFlush( GarnettObject obj )
            throws IOException {

        if ( _socket == null ) {

            throw new GarnettNotConnectedException( "attempt to send object before session is connected" );

        }

        _outbound.writeOptionalGarnettObject( obj );

    }
    
    public GarnettObject getNextObject()
            throws IOException {

        if ( _socket == null ) {

            throw new GarnettNotConnectedException( "attempt to receive message before session is connected" );

        }

        @SuppressWarnings("UnnecessaryLocalVariable") 
        GarnettObject nextObject = _inbound.readGarnettObject();
        
        return nextObject;
        
    }
    
    public GarnettMessage getNextMessage()
            throws IOException {
        
        return (GarnettMessage)getNextObject();

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

                        if ( _done ) {

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

    public synchronized void flush()
            throws IOException {

        if ( _outbound != null ) {

            _outbound.flush();

        }

    }

    public String getSessionName() {

        return _sessionName;

    }

    public long getSessionId() {

        return _sessionId;

    }

    public Long getSocketLock() {

        return _socketLock;

    }

    protected void didSomething() {

        _lastActivityTime = System.currentTimeMillis();

    }

    public long lastActivityTime() {

        return _lastActivityTime;

    }

    private static void runDms() {

        //noinspection InfiniteLoopStatement
        while ( true ) {

            // Build a list of doomed sessions and then end them in a separate loop.
            // This two-phase approach avoids concurrent modification exceptions
            // (triggered when endSession removes the session from the _sessions map).

            List<GarnettSession> doomedSessions = new LinkedList<GarnettSession>();

            synchronized ( _sessions ) {

                for ( GarnettSession session : _sessions.values() ) {

                    if ( System.currentTimeMillis() - session.lastActivityTime() > INACTIVITY_TIMEOUT ) {

                        Trace.event( "session " + session.getSessionName() + " is doomed" );
                        doomedSessions.add( session );

                    }

                }

            }

            for ( GarnettSession session : doomedSessions ) {

                Trace.event( "forcibly terminating session " + session.getSessionName() );

                session.sessionEnds();

            }

            ObtuseUtil5.safeSleepMillis( Timer.ONE_SECOND );

        }

    }

    protected void setAuthenticated( boolean authenticated ) {

        _authenticated = true;

    }

    protected boolean isAuthenticated() {

        return _authenticated;

    }

}
