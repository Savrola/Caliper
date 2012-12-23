package com.obtuse.garnett.exceptions;

import java.io.IOException;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * Thrown if an attempt is made to send a {@link com.obtuse.garnett.GarnettObject}
 * across a {@link com.obtuse.garnett.GarnettSession} before the session is connected.
 */

@SuppressWarnings("SameParameterValue")
public class GarnettOutBoundQueueDoneException extends IOException {

    public GarnettOutBoundQueueDoneException( String message ) {
        super( message );

    }

}