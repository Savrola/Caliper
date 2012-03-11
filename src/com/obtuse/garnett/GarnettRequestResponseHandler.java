package com.obtuse.garnett;

/*
 * Copyright © 2012 Daniel Boulet
 */

import com.obtuse.garnett.stdmsgs.GarnettRequestMessage;
import com.obtuse.garnett.stdmsgs.GarnettResponseMessage;

/**
 * %%% something clever goes here.
 */
public interface GarnettRequestResponseHandler {

    /**
     * Process an {@link GarnettRequestMessage} and respond with an {@link com.obtuse.garnett.stdmsgs.GarnettResponseMessage}.
     *
     * @param session the session that the request was received via.
     * @param slcMessage   the request message.
     * @return the response which is to be returned to the sender (an empty SlcResponseMessage is returned
     *         to the sender if this method returns null).
     */

    GarnettResponseMessage processMessage( GarnettSession session, GarnettRequestMessage slcMessage );

    /**
     * Indicates whether this handler requires that its requests arrive via authenticated sessions.
     * If this method returns true for a request that arrived via an unauthenticated session then
     * the request is discarded (i.e. this handler's {@link #processMessage} method will not see the request).
     *
     * @return true if authentication is required; false otherwise.
     */

    boolean authenticationRequired();

    /**
     * Returns a list of capabilities that this handler insists that the requesting user has.
     * If this method returns a non-null value then {@link #authenticationRequired} is presumed to return
     * true (i.e. it is not called).
     *
     * @return the list of required capabilities or null if the handler does not blanket require any particular
     * capabilities.
     */

    String[] requiredCapabilities();

    /**
     * Returns a list of capabilities that this handler insists that the requesting user not have.
     * If this method returns a non-null value then {@link #authenticationRequired} is presumed to return
     * true (i.e. it is not called).
     *
     * @return the list of forbidden capabilities or null if the handler does not blanket forbid any particular
     * capabilities.
     */

    String[] forbiddenCapabilities();

    /**
     * Determine if the session should be marked as done once the response is sent.
     *
     * @return true if the session should be marked done; false otherwise.
     */

    boolean isOutBoundQueueDone();

}
