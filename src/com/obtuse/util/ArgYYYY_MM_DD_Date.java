package com.obtuse.util;

/*
 * Copyright © 2011 Obtuse Systems Corporation
 */

import com.obtuse.util.exceptions.ParsingException;

import java.util.Date;

/**
 * A date {@link ArgParser} argument.
 * <p/>The only supported format is "yyyy-MM-dd".
 * See {@link DateUtils#parseYYYY_MM_DD} for more information.
 */

public abstract class ArgYYYY_MM_DD_Date extends Arg {

    protected ArgYYYY_MM_DD_Date( String keyword ) {
        super( keyword );

    }

    public void process( String keyword, String arg ) {

        try {

            process( keyword, DateUtils.parseYYYY_MM_DD( arg, 0 ) );

        } catch ( ParsingException e ) {

            throw new IllegalArgumentException( "invalid argument (" + arg + ") - must be a date in the format YYYY-MM-DD", e );

        }

    }

    public abstract void process( String keyword, Date arg );

    public String toString() {

        return "ArgLong( " + getKeyword() + " )";

    }

}
