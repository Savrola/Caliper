package com.obtuse.util;



/*
 * Copyright © 2011 Obtuse Systems Corporation
 */

public abstract class ArgString extends Arg {

    protected ArgString( String keyword ) {
        super( keyword );
    }

    public String toString() {

        return "ArgString( " + getKeyword() + " )";

    }

}
