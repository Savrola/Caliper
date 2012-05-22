package com.obtuse.util;

import org.jetbrains.annotations.Nullable;

/*
 * Copyright © 2012 Daniel Boulet
 */

public interface MessageProxy {

    void fatal( String msg );

    void fatal( String msg1, String msg2 );

    void fatal( String msg1, Throwable e );

    void fatal(
            String msg1,
            @Nullable
            String msg2, String buttonContents
    );

    void error( String msg );

    void error( String msg1, String msg2 );

    void error( String msg1, Throwable e );

    void error(
            String msg1,
            @Nullable
            String msg2, String buttonContents
    );

}