package com.obtuse.garnett;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * Something capable of dealing with in-bound {@link GarnettMessage}s.
 */

public interface GarnettMessageHandler {

    void processMessage( GarnettSession session, GarnettMessage message );

}
