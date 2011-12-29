package com.obtuse.util;

/*
 * Copyright © 2011 Obtuse Systems Corporation
 */

public abstract class Arg {

    private final String _keyword;

    protected Arg( String keyword ) {
        super();

        _keyword = keyword;
    }

    public abstract void process( String keyword, String arg );

    public String getKeyword() {

        return _keyword;

    }

}
