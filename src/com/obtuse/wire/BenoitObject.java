package com.obtuse.wire;

/*
 * Copyright © 2011 Daniel Boulet.
 */

import java.io.IOException;

/**
 * Something which is 'serializable' Benoit-style.
 * <p/>
 * In order to be 'de-serializable' Benoit-style, an implementing class must also have a public constructor
 * which has the following signature:
 * <blockquote><tt>public BenoitObjectConstructor( BenoitByteBuffer bbs )<br>
            &nbsp;&nbsp;&nbsp;&nbsp;throws BenoitSerializationFailedException</tt></blockquote>
 */

public interface BenoitObject {

    /**
     * Get the name under which this type has been registered with a {@link BenoitObjectRestorerRegistry}.
     * Note that each non-abstract level of an inheritance hierarchy which implements this interface
     * must also implement this method since a call to this method must return the name of the fully derived
     * class as opposed to any of the classes which it is derived from.
     * @return the name under which the implementing class has been registered.
     */

    BenoitTypeName getBenoitTypeName();

    /**
     * Write a serialized version of this object to the specified {@link BenoitObjectOutputStreamInterface}.
     * @param boos where to write the serialized version of this object.
     */

    void serializeContents( BenoitObjectOutputStreamInterface boos )
            throws IOException;

}
