package com.obtuse.garnett;

/**
 * Implemented by Garnett sessions that can check the user's capabilities.
 * <p/>
 * Copyright © 2007, 2008 Daniel Boulet.
 */

public interface KnowsCapabilities {

    /**
     * Checks that the authenticated 'owner' of the session has all of the required capabilities and none of the
     * forbidden capabilities.
     *
     * @param requiredCapabilities  the required capability names or null if there aren't any.
     * @param forbiddenCapabilities the forbidden capability names or null if there aren't any.
     * @return true if both the arrays of capabilities are null or if the authenticated 'owner' of the session has all
     *         of the required capabilities and none of the forbidden capabilities.
     */

    @SuppressWarnings({ "BooleanMethodIsAlwaysInverted" })
    boolean checkCapabilities( String[] requiredCapabilities, String[] forbiddenCapabilities );

}