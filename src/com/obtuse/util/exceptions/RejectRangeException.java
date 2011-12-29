package com.obtuse.util.exceptions;

import com.obtuse.util.Ranges;
import com.obtuse.util.Range;

/**
 * Thrown by {@link Ranges} if a {@link Range} passed to add() ultimately proves to be unacceptable.
 * <p/>
 * Copyright © 2009 Obtuse Systems Corporation.
 */

public class RejectRangeException extends Exception {

    public RejectRangeException() {
        super();

    }

    public RejectRangeException( String why ) {
        super( why );

    }

    public RejectRangeException( String why, Throwable e ) {
        super( why, e );

    }

    public String toString() {

        return super.toString();

    }

}
