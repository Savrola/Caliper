package com.obtuse.util;

/*
 * Copyright © 2011 Obtuse Systems Corporation
 */

public abstract class ArgLong extends Arg {

    protected ArgLong( String keyword ) {
        super( keyword );

    }

    public void process( String keyword, String arg ) {

        try {

            process( keyword, Long.parseLong( arg ) );

        } catch ( NumberFormatException e ) {

            throw new IllegalArgumentException( "invalid argument (" + arg + ") - must be a long", e );

        }

    }

    public abstract void process( String keyword, long arg );

    public String toString() {

        return "ArgLong( " + getKeyword() + " )";

    }

}
