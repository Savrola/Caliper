package com.obtuse.util;

/**
 * Allow the application to intercept quit attempts. Works on Mac OS X.  No idea what happens on Windows.
 * <p/>
 * Copyright © 2007, 2008 Loa Corporation.
 * Copyright © 2011 Daniel Boulet.
 */

@SuppressWarnings( { "UnusedDeclaration" } )
public interface QuitCatcher {

    boolean quitAttempted();

}
