package com.obtuse.util.exceptions;

/**
 * Thrown if a syntax error is encountered somewhere.
 * <p/>
 * Copyright © 2006 Invidi Technologies Corporation
 * Copyright © 2012 Daniel Boulet
 */

public class SyntaxErrorException extends Exception {

    public SyntaxErrorException( String msg ) {
        super( msg );

    }

    public SyntaxErrorException( String msg, Throwable e ) {
        super( msg, e );

    }
}

