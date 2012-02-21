package com.obtuse.db.raw.exceptions;

/**
 * Thrown if a search for what was supposed to be a singleton yields more than one entity.
 * <p/>
 * Copyright © 2012 Daniel Boulet.
 */

@SuppressWarnings({"ClassWithoutToString"})
public class ObtuseMoreThanOneFoundException extends Exception {

    @SuppressWarnings({ "UnusedDeclaration" })
    public ObtuseMoreThanOneFoundException() {
        super();
    }

    @SuppressWarnings({ "UnusedDeclaration" })
    public ObtuseMoreThanOneFoundException( String why ) {
        super(why);
    }

    @SuppressWarnings({ "UnusedDeclaration" })
    public ObtuseMoreThanOneFoundException( String why, Throwable e ) {
        super(why,e);
    }
}
