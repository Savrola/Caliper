package com.loanetworks.nahanni.elephant2.raw.dbdata;

/**
 * Encapsulate the return value of an insertion operation.
 * <p/>
 * Copyright © 2007, 2008 Loa Corporation.
 */

public class BundledKeys {

    private final long[] _keys;
    private final int[] _keyColumnIxs;

    public BundledKeys( long[] keys, int[] keyColumnIxs ) {
        super();

        _keys = keys;
        _keyColumnIxs = keyColumnIxs;

    }

    public long[] getKeys() {

        return _keys;

    }

    public int[] getColumnIxs() {

        return _keyColumnIxs;

    }

    private String formatKeys() {

        String rval = "";
        String comma = "";
        for ( int i = 0; i < _keys.length; i += 1 ) {
            rval += comma + "(" + _keys[i] + "@" + _keyColumnIxs[i] + ")";
            comma = ", ";
        }

        return rval;
    }

    public String toString() {

        return "BundledKeys( " + formatKeys() + " )";

    }

    /**
     * Fetch a key by its column number.
     *
     * @param keyColumnNumber the key's column number.
     * @return the key's value.
     * @throws IllegalArgumentException if there is no key with the specified column number.
     */

    public long getKey( int keyColumnNumber ) {

        for ( int i = 0; i < _keyColumnIxs.length; i += 1 ) {

            if ( _keyColumnIxs[i] == keyColumnNumber ) {

                return _keys[i];

            }

        }

        throw new IllegalArgumentException( "no key found for column " + keyColumnNumber );

    }

}
