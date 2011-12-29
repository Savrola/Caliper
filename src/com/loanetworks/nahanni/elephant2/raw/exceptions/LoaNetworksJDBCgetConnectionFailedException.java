package com.loanetworks.nahanni.elephant2.raw.exceptions;

/**
 * Thrown if we are unable to connect to the database server.
 * <p/>
 * Copyright © 2006 Loa Corporation.
 */

@SuppressWarnings({"ClassWithoutToString"})
public class LoaNetworksJDBCgetConnectionFailedException extends Exception {

    public LoaNetworksJDBCgetConnectionFailedException() {
        super();
    }

    @SuppressWarnings({ "UnusedDeclaration" })
    public LoaNetworksJDBCgetConnectionFailedException(String why) {
        super(why);
    }

    public LoaNetworksJDBCgetConnectionFailedException(String why, Throwable e) {
        super(why,e);
    }
}
