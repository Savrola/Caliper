package com.obtuse.util;

/*
 * Copyright © 2011 Obtuse Systems Corporation
 */

public abstract class ArgInt extends Arg {

    protected ArgInt( String keyword ) {
        super( keyword );

    }

    public void process( String keyword, String arg ) {

        try {

            process( keyword, Integer.decode( arg ) );

        } catch ( NumberFormatException e ) {

            throw new IllegalArgumentException( "invalid argument (" + arg + ") - must be an integer", e );

        }

    }

    public abstract void process( String keyword, int arg );

    public String toString() {

        return "ArgInt( " + getKeyword() + " )";

    }

}
