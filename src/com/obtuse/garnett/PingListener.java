package com.obtuse.garnett;

/**
 * Something that is interested in hearing about recent ping replies.
 * <p/>
 * Copyright © 2007, 2008 Daniel Boulet.
 */

public interface PingListener {

    /**
     * Tell the ping listener of an out-bound ping's request id just before it is sent.
     *
     * @param requestId the about to be sent ping's request id.
     */

    void rememberRequestId( long requestId );

    /**
     * Tell the ping listener of just-received ping reply.
     *
     * @param requestId the request id of the just-received ping reply.
     * @return true if the listener wishes to be told of future ping replies; false otherwise (a return value of false
     *         inhibits all future calls to any of the methods of this interface for this listener instance).
     */

    boolean gotPingReply( long requestId );

}
