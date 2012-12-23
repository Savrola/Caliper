package com.obtuse.util;

import java.util.Map;
import java.util.TreeMap;

/*
 * Copyright © 2011 Obtuse Systems Corporation
 */

/**
 * A (relatively) simple command line argument parser facility.
 */

@SuppressWarnings({"ClassWithoutToString"})
public class ArgParser {

    private final Map<String, Arg> _args = new TreeMap<String, Arg>();

    public ArgParser( Arg[] args ) {
        super();

        for ( Arg pDesc : args ) {

            if ( pDesc.getKeyword().startsWith("-") && !pDesc.getKeyword().startsWith("--") ) {

                _args.put( pDesc.getKeyword(), pDesc );

            } else {

                throw new IllegalArgumentException( "keyword must start with a single minus sign" );

            }

        }

    }

    @SuppressWarnings( { "BooleanMethodIsAlwaysInverted" } )
    public boolean parse( String[] args ) {
        int i = 0;

        while ( i < args.length ) {

            String keyword = args[i];

            Arg pDesc = _args.get( keyword );
            if ( pDesc == null ) {

                Logger.logErr( "unexpected argument \"" + keyword + "\"" );
                return false;

            } else {

                try {

                    pDesc.process( keyword, args[i + 1]);

                    i += 2;

                } catch ( ArrayIndexOutOfBoundsException e ) {

                    Logger.logErr( "operand for " + pDesc.getKeyword() + " is missing" );
                    return false;

                } catch ( RuntimeException e ) {

                    Logger.logErr( e.getMessage() );
                    return false;

                }

            }

        }

        return true;

    }

}
