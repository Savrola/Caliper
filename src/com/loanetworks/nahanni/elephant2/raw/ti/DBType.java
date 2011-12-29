package com.loanetworks.nahanni.elephant2.raw.ti;

import java.io.Serializable;

/**
 * Describe the type of a (normally) database-resident entity.
* <p/>
* Copyright © 2007, 2008 Loa Corporation.
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
