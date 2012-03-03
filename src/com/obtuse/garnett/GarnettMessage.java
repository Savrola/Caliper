package com.obtuse.garnett;

/*
 * Copyright © 2012 Daniel Boulet
 */

import java.io.IOException;

/**
 * A {@link GarnettObject} which also happens to be a message.
 */

public abstract class GarnettMessage extends GarnettUniqueEntity {

    protected GarnettMessage() {
        super();

    }

    protected GarnettMessage( GarnettObjectInputStreamInterface gois )
            throws IOException {
        super( gois );

    }

}
