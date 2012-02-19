package com.loanetworks.nahanni.elephant2.raw.ti;

import java.io.Serializable;

/**
 * Describe the type of a (normally) database-resident entity.
* <p/>
* Copyright © 2012 Daniel Boulet.
*/

public enum DBType implements Serializable {
    SERIAL,
    SERIAL8,
    INT,
    SHORT,
    LONG,
    FLOAT,
    TEXT,
    BYTES,
    TIMESTAMPTZ,
    DATE,
    FOREIGN,
    MONEY,
    INET,
    MACADDR,
    TEXTARRAY,
    INTARRAY,
    BOOLEAN,
    DOUBLE,
    TIMESTAMP
}
