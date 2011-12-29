package com.obtuse.util;

/**
 * A clock event.
 */

public abstract class SimpleEvent {

    private final String _description;

    public SimpleEvent( String description ) {
        super();

        _description = description;

    }

    public abstract void run( long when );

    public String toString() {

        return "ClockEvent( \"" + _description + "\" )";

    }

}
