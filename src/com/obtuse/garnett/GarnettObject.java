package com.obtuse.garnett;

/*
 * Copyright © 2011 Daniel Boulet.
 */

import java.io.IOException;

/**
 * Something which is 'serializable' Garnett-style.
 * <p/>
 * In order to be 'de-serializable' Garnett-style, an implementing class must also have a public constructor
 * which has the following signature:
 * <blockquote><tt>public GarnettObjectConstructor( GarnettByteBuffer bbs )<br>
            &nbsp;&nbsp;&nbsp;&nbsp;throws GarnettSerializationFailedException</tt></blockquote>
 */

public interface GarnettObject {

    /**
     * Get the name under which this type has been registered with a {@link GarnettObjectRestorerRegistry}.
     * Note that each non-abstract level of an inheritance hierarchy which implements this interface
     * must also implement this method since a call to this method must return the name of the fully derived
     * class as opposed to any of the classes which it is derived from.
     * @return the name under which the implementing class has been registered.
     */

    GarnettTypeName getGarnettTypeName();

    /**
     * Write a serialized version of this object to the specified {@link GarnettObjectOutputStreamInterface}.
     * @param boos where to write the serialized version of this object.
     */

    void serializeContents( GarnettObjectOutputStreamInterface boos )
            throws IOException;

}
