package com.obtuse.util;

/*
 * Copyright © 2011 Obtuse Systems Corporation
 */

/**
 * A yes/no {@link ArgParser} argument.
 * <p/>Valid values are:
 * <ul>
 *     <li>y</li>
 *     <li>yes</li>
 *     <li>n</li>
 *     <li>no</li>
 * </ul>
 * in any mixture of upper and/or lower case.
 */

public abstract class ArgYesNo extends Arg {

    protected ArgYesNo( String keyword ) {
        super( keyword );

    }

    public void process( String keyword, String arg ) {

        if ( "yes".equalsIgnoreCase( arg ) || "y".equalsIgnoreCase( arg ) ) {

            process( keyword, true );

        } else if ( "no".equalsIgnoreCase( arg ) || "n".equalsIgnoreCase( arg ) ) {

            process( keyword, false );

        } else {

            throw new IllegalArgumentException( "invalid argument (" + arg + ") - must be YES, Y, NO or N (in any mixture of upper and lower case)" );

        }

    }

    public abstract void process( String keyword, boolean arg );

    public String toString() {

        return "ArgInt( " + getKeyword() + " )";

    }

}
