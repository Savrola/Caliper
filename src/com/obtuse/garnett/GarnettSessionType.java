package com.obtuse.garnett;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * The various Garnett session types.
 * <p/>
 * Session typing is mostly relevant for server sessions since they use the session type to vet the types
 * of messages that they can receive based on what sort of authentication has been performed by the client.
 * Client sessions may but do not necessarily use their session type to track what they expect the type of
 * their corresponding server session type to be.
 * <p/>
 * Note that the session types are sorted according to the rule that a session of type X may be changed to a session
 * of type Y if and only iff X &lt;= Y.
 */

public enum GarnettSessionType {

    /**
     * A session which can only receive messages which have been tagged by the {@link GarnettUnauthenticatedMessage}
     * interface.
     */

    NO_AUTH,

    /**
     * A session which can only receive messages which have been tagged by either the {@link
     * GarnettUnauthenticatedMessage} interface or the {@link GarnettIndividuallyAuthenticatedMessage} interface.
     */

    SEMI_AUTH,

    /**
     * A session which has been authenticated by a {@link com.obtuse.garnett.stdmsgs.GarnettLoginRequestMessage}.
     * Any message type may be received via this session.
     */

    COMMAND;

    /**
     * Sessions start out life as virgins which is actually identical to NOAUTH.
     */

    public static final GarnettSessionType VIRGIN = NO_AUTH;

}
