package com.obtuse.db.raw;

import com.obtuse.db.PostgresConnection;

/**
 * A connection to a database that is managed by Elephant.
 * <p/>
 * Copyright © 2012 Daniel Boulet.
 */

@SuppressWarnings( { "ClassWithoutToString" } )
public class ElephantConnection extends PostgresConnection {

    @SuppressWarnings({ "SameParameterValue" })
    public ElephantConnection( String hostname, String dbName ) {
        super(
                hostname,
                dbName
        );
    }

}
