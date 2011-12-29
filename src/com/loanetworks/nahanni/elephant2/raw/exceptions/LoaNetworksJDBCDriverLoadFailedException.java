package com.loanetworks.nahanni.elephant2.raw.exceptions;

/**
 * Thrown if we are unable to load a suitable JDBC driver.
 * <p/>
 * Copyright © 2006 Loa Corporation.
 */

@SuppressWarnings({"ClassWithoutToString"})
public class LoaNetworksJDBCDriverLoadFailedException extends Exception {

    public LoaNetworksJDBCDriverLoadFailedException() {
        super();
    }

    public LoaNetworksJDBCDriverLoadFailedException(String why) {
        super(why);
    }

    public LoaNetworksJDBCDriverLoadFailedException(String why, Throwable e) {
        super(why,e);
    }
}
