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
 * %%% something clever goes here.
 */

public abstract class MinimalGarnettSession extends Thread {

    private static final SortedMap<Long,MinimalGarnettSession> _sessions = new TreeMap<Long, MinimalGarnettSession>();
    private static int _maxSessionsActive;
    private static int _totalSessionCount;
    private static final long INACTIVITY_TIMEOUT = DebugUtilities.inIntelliJIDEA() ? Timer.ONE_HOUR : Timer.ONE_MINUTE;
    public static final int PROXIED_PROTOCOL_VERSION_CODE = 0x10;

    private Socket _socket;
    private final Long _socketLock = 0L;
    private GarnettObjectInputStreamInterface _inbound;
    private GarnettObjectOutputStreamInterface _outbound;
    private final Long _inboundLock = 0L;
    private final String _sessionName;
    private final long _sessionId;
    private GarnettSessionPrefix _sessionPrefix;
    private boolean _done = false;
    private long _lastActivityTime;
    private final boolean _isServerSession;
    private GarnettSessionType _sessionType = GarnettSessionType.VIRGIN;
    private boolean _authenticated = false;

    private MinimalGarnettSession( String sessionName, boolean isServerSession )
            /*throws GarnettIllegalArgumentException*/ {
        super( sessionName );

        _sessionName = sessionName;
        _isServerSession = isServerSession;

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

    /**
     * Create a minimal server session.
     * @param sessionName the name of this session.
     * @param sessionPrefix this session's session prefix
     *                      (if null then the actual session prefix must be set before we start sending data).
     */

    protected MinimalGarnettSession( String sessionName, GarnettSessionPrefix sessionPrefix ) {
        this( sessionName, true );

        _sessionPrefix = sessionPrefix;

    }

    /**
     * Create a minimal client session.
     * @param sessionName the name of this session.
     * @param sessionPrefix this session's prefix (must not be null).
     * @param intendedSessionType this session's intended type.
     * @throws GarnettIllegalArgumentException if the session prefix is null.
     */

    protected MinimalGarnettSession(
            String sessionName,
            GarnettSessionPrefix sessionPrefix,
            GarnettSessionType intendedSessionType
    )
            throws GarnettIllegalArgumentException {
        this( sessionName, false );

        _sessionPrefix = sessionPrefix;
        if ( sessionPrefix == null ) {

            throw new GarnettIllegalArgumentException( "client session's session prefix is null" );

        }

        _sessionType = intendedSessionType;

    }

    /**
     * Set this session's session prefix.
     * @param sessionPrefix the session prefix for this session.
     * @throws GarnettIllegalArgumentException if the session prefix has already been set to a non-null value.
     */

    protected void setSessionPrefix( GarnettSessionPrefix sessionPrefix )
            throws GarnettIllegalArgumentException {

        if ( _sessionPrefix == null ) {

            _sessionPrefix = sessionPrefix;

        } else {

            throw new GarnettIllegalArgumentException( "session prefix already set" );

        }

    }

    static {

        //noinspection ClassWithoutToString,RefusedBequest
        new Thread( "GarnettSession DMS" ) {

            public void run() {

                runDms();

            }

        }.start();

    }

    private static void runDms() {

        //noinspection InfiniteLoopStatement
        while ( true ) {

            // Build a list of doomed sessions and then end them in a separate loop.
            // This two-phase approach avoids concurrent modification exceptions
            // (triggered when endSession removes the session from the _sessions map).

            List<MinimalGarnettSession> doomedSessions = new LinkedList<MinimalGarnettSession>();

            synchronized ( _sessions ) {

                for ( MinimalGarnettSession session : _sessions.values() ) {

                    if ( System.currentTimeMillis() - session.lastActivityTime() > INACTIVITY_TIMEOUT ) {

                        Trace.event( "session " + session.getSessionName() + " is doomed" );
                        doomedSessions.add( session );

                    }

                }

            }

            for ( MinimalGarnettSession session : doomedSessions ) {

                Trace.event( "forcibly terminating session " + session.getSessionName() );

                session.sessionEnds();

            }

            ObtuseUtil5.safeSleepMillis( Timer.ONE_SECOND );

        }

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

            // If we are the server end then we open our input stream first since we may eventually need to see
            // the client's session prefix before we generate our session prefix in response.
            //
            // If we are the client end then we open our output stream first since the server end may need to
            // see our session prefix before they generate their session prefix in response.


            if ( isServerSession() ) {

                _inbound = new GarnettObjectInputStream( socket.getInputStream() );
                _outbound = new GarnettObjectOutputStream( socket.getOutputStream(), _sessionPrefix );

            } else {

                _outbound = new GarnettObjectOutputStream( socket.getOutputStream(), _sessionPrefix );
                _inbound = new GarnettObjectInputStream( socket.getInputStream() );

            }
            // respective input sides first and then hang waiting for the other side to send the session prefix.

            _outbound = new GarnettObjectOutputStream( socket.getOutputStream(), _sessionPrefix );
            _inbound = new GarnettObjectInputStream( socket.getInputStream() );

//            // Note that we ***MUST*** open the output stream first to avoid deadlocking if both ends open their
//            // respective input sides first and then hang waiting for the other side to send the session prefix.
//
//            _outbound = new GarnettObjectOutputStream( socket.getOutputStream(), _sessionPrefix );
//            _inbound = new GarnettObjectInputStream( socket.getInputStream() );

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

    public Long getSocketLock() {

        return _socketLock;

    }

    public boolean isDone() {

        return _done;

    }

    public final void run() {

        doRun();

    }

    protected abstract void doRun();

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

        majorSessionStateChange();

        synchronized ( getSocketLock() ) {

            Trace.event( "closing sockets in sessionEnds" );
            ObtuseUtil5.closeQuietly( getSocket() );
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

    protected abstract void majorSessionStateChange();

    protected void logClose() {

        Trace.event( "logClose on " + getSessionName() );

    }

    public String getSessionName() {

        return _sessionName;

    }

    public long getSessionId() {

        return _sessionId;

    }

    protected void didSomething() {

        _lastActivityTime = System.currentTimeMillis();

    }

    public long lastActivityTime() {

        return _lastActivityTime;

    }

    public synchronized void sendMessage( GarnettMessage message )
            throws IOException {

        sendObjectNoFlush( message );

    }

    public synchronized void sendObjectNoFlush( GarnettObject obj )
            throws IOException {

        if ( getSocket() == null ) {

            throw new GarnettNotConnectedException( "attempt to send object before session is connected" );

        }

        getOutboundObjectStream().writeOptionalGarnettObject( obj );

    }

    protected GarnettObject getNextObject()
            throws IOException {

        if ( getSocket() == null ) {

            throw new GarnettNotConnectedException( "attempt to receive message before session is connected" );

        }

        @SuppressWarnings("UnnecessaryLocalVariable")
        GarnettObject nextObject = getInboundObjectStream().readGarnettObject();

        return nextObject;

    }

    protected GarnettMessage getNextMessage()
            throws IOException {

        return (GarnettMessage)getNextObject();

    }

    public boolean isServerSession() {

        return _isServerSession;

    }

    public GarnettSessionType getSessionType() {

        return _sessionType;

    }

    public void setSessionType( GarnettSessionType sessionType )
            throws GarnettIllegalArgumentException {

        if ( sessionType.ordinal() >= _sessionType.ordinal() ) {

            _sessionType = sessionType;

        } else {

            throw new GarnettIllegalArgumentException( "unable to 'lower' session type (is " + _sessionType + ", new " + sessionType + ")" );

        }

    }

    protected void setAuthenticated( boolean authenticated ) {

        _authenticated = true;

    }

    protected boolean isAuthenticated() {

        return _authenticated;

    }
}
