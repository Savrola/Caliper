package com.obtuse.util;

/**
 * Something which handles quit events on Mac OS X.
 * <p/>
 * See the {@link MacCustomization} class for more information.
 * <p/>
 * Copyright © 2007, 2008 Loa Corporation.
 * Copyright © 2011 Daniel Boulet.
 */

@SuppressWarnings( { "UnusedDeclaration" } )
public interface QuitCatcher {

    boolean quitAttempted();

}
