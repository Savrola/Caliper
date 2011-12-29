package com.loanetworks.nahanni.elephant2.raw.exceptions;

/**
 * Thrown if a search for what was supposed to be a singleton yields more than one entity.
 * <p/>
 * Copyright © 2007, 2008 Loa Corporation.
 */

@SuppressWarnings({"ClassWithoutToString"})
public class LoaNetworksElephantMoreThanOneFoundException extends Exception {

    @SuppressWarnings({ "UnusedDeclaration" })
    public LoaNetworksElephantMoreThanOneFoundException() {
        super();
    }

    @SuppressWarnings({ "UnusedDeclaration" })
    public LoaNetworksElephantMoreThanOneFoundException(String why) {
        super(why);
    }

    @SuppressWarnings({ "UnusedDeclaration" })
    public LoaNetworksElephantMoreThanOneFoundException(String why, Throwable e) {
        super(why,e);
    }
}
