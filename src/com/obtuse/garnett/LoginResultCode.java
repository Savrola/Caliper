package com.obtuse.garnett;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * %%% something clever goes here.
 */
public enum LoginResultCode {

    /**
     * Brute force password cracking pays off at last.
     */

    SUCCESS,

    /**
     * The username does not satisfy the username syntax rules.
     */

    INVALID_ACCOUNT_NAME,

    /**
     * Unknown username, incorrect password or bogus password.
     */

    AUTHENTICATION_FAILURE,

    /**
     * Network error probably related to a server being down or flakey network.
     */

    NETWORK_ERROR,

    /**
     * Something truly unexpected happened.
     */

    PROTOCOL_ERROR

}
