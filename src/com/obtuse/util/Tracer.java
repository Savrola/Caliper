package com.obtuse.util;

/**
 * Something capable of acting like a trace facility.
 * <p/>
 * Copyright © 2012 Daniel Boulet.
 * Copyright © 2012 Daniel Boulet.
 */

@SuppressWarnings( { "UnusedDeclaration" } )
public interface Tracer {

    void event( String description );

    void event( String description, Throwable e );

    String emitTrace( String description );

    String emitTrace( String description, Throwable e );

    String emitTrace( Throwable e );

}