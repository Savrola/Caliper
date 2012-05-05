package com.obtuse.garnett;

import java.util.Random;

/*
 * Copyright © 2012 Daniel Boulet.
 */

/**
 * This is java.util.Random's implementation as documented in Java 1.6 javadoc.  We use this to obfuscated
 * account names and passwords so that developers and maintainers do not accidentally see someone's password.
 */

@SuppressWarnings("MagicNumber")
public class GarnettObfuscationRandom extends Random {

    public static final int portableSeed = 326483;

    private long _seed;

    public GarnettObfuscationRandom( long seed ) {

        super( seed );
        //noinspection UnnecessaryParentheses
        _seed = ( seed ^ 0x5DEECE66DL ) & ( ( 1L << 48 ) - 1 );

    }

    protected int next( int bits ) {

        //noinspection UnnecessaryParentheses
        _seed = ( _seed * 0x5DEECE66DL + 0xBL ) & ( ( 1L << 48 ) - 1 );

        //noinspection UnnecessaryParentheses
        return ( (int)( _seed >>> ( 48 - bits ) ) );

    }

    public void nextBytes( byte[] bytes ) {

        if ( bytes != null ) {

            for ( int i = 0; i < bytes.length; i++ ) {

                int n = next( 8 );
                bytes[i] = (byte)( n & 0x000000FF );

            }

        }

    }

    public int nextInt( int n ) {

        if ( n <= 0 ) {

            throw new IllegalArgumentException( "n must be positive" );

        }

        if ( ( n & -n ) == n ) { // i.e., n is a power of 2

            //noinspection UnnecessaryParentheses
            return (int)( ( n * (long)next( 31 ) ) >> 31 );

        }

        int bits, val;
        //noinspection UnnecessaryParentheses
        do {

            bits = next( 31 );
            val = bits % n;

        } while ( bits - val + ( n - 1 ) < 0 );

        return val;

    }

    public int nextInt() {

        return next( 32 );

    }

    public long nextLong() {

        return ( (long)next( 32 ) << 32 ) + next( 32 );

    }

    public boolean nextBoolean() {

        return next( 1 ) != 0;

    }

}
