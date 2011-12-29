package com.loanetworks.nahanni.elephant2.raw;

import com.loanetworks.db.PostgresConnection;

/**
 * A connection to a database that is managed by Elephant.
 * <p/>
 * Copyright © 2007, 2008 Loa Corporation.
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
