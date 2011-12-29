package com.loanetworks.nahanni.elephant2.raw.devutils;

import java.io.FileNotFoundException;

/**
 * Describe something capable of generating the Java source code for database support files.
 * <p/>
 * Copyright © 2007, 2008 Loa Corporation.
 */

public interface DbClassGenerator {

    void generateTableInfoClass( TableMetaData tableMetaData )
            throws
            FileNotFoundException;

    void generateTupleCarrierClass( TableMetaData tableMetaData )
            throws
            FileNotFoundException;

    @SuppressWarnings( { "BooleanMethodIsAlwaysInverted" } )
    boolean validateTableInfoClasses( TableMetaData tableMetaData );

}
