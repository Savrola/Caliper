package com.obtuse.util;

/*
 * Copyright © 2006 Loa Corporation.
 * Copyright © 2011 Daniel Boulet.
 */

/**
 * Something capable of launching the preferences panel.
 * <p/>Used by our Mac OS X customizations to handle Apple's special approach to launching a preferences panel.
 */

@SuppressWarnings( { "UnusedDeclaration" } )
public interface PreferencesHandler {

    void handlePreferences();

}
