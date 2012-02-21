package com.obtuse.garnett;

/*
 * Copyright © 2012 Daniel Boulet
 */

import com.obtuse.garnett.exceptions.GarnettIllegalArgumentException;
import com.obtuse.garnett.exceptions.GarnettNotConnectedException;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil5;

import java.io.IOException;
import java.net.Socket;

/**
 * Manage the sending and receiving of messages through a Garnett session.
 */

public abstract class GarnettSession extends Thread {

//    private Queue<GarnettObject> _outboundQueue = new LinkedList<GarnettObject>();

    private Socket _socket;
    private GarnettObjectInputStreamInterface _inbound;
    private GarnettObjectOutputStreamInterface _outbound;

    private boolean _inboundDone = false;

    private final Long _inboundLock = 0L;

    protected GarnettSession() {
        super();

    }

    protected void setSocket( Socket socket )
            throws IOException {

        synchronized ( this ) {

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
            _outbound = new GarnettObjectOutputStream( socket.getOutputStream() );

        }

        synchronized ( _inboundLock ) {

            _inboundLock.notifyAll();

        }

    }

    public void run() {

        _inboundDone = false;

        while ( !_inboundDone ) {

            synchronized ( _inboundLock ) {

                while ( _socket == null ) {

                    try {

                        _inboundLock.wait();

                    } catch ( InterruptedException e ) {

                        // Just go around again

                    }

                }

                try {

                    GarnettObject obj = _inbound.readOptionalGarnettObject();
                    processInboundObject( obj );

                } catch ( IOException e ) {

                    Logger.logErr( "GarnettSession:  I/O exception reading from socket", e );
                    _inboundDone = true;

                }

            }

        }

        // Wakeup anyone waiting on our instance in case they are waiting for input to complete.

        synchronized ( this ) {

            notifyAll();

        }

    }

    protected abstract void processInboundObject( GarnettObject obj );

    public synchronized void sendObject( GarnettObject obj )
            throws IOException {

        sendObjectNoFlush( obj );
        flush();

    }

    public synchronized void sendObjectNoFlush( GarnettObject obj )
            throws IOException {

        if ( _socket == null ) {

            throw new GarnettNotConnectedException( "attempt to send object before session is connected" );

        }

        _outbound.writeOptionalGarnettObject( obj );

    }

    public synchronized void flush()
            throws IOException {

        if ( _outbound != null ) {

            _outbound.flush();

        }

    }

}
