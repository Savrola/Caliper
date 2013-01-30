package com.obtuse.util;

/**
 * A two-dimension version of {@link TreeCounter}.
 * <p/>
 * Copyright © 2011 Obtuse Systems Corporation
 */

public class TwoDimensionalTreeCounter<K1,K2> implements TwoDimensionalCounter<K1, K2> {

    private final TwoDimensionalSortedMap<K1,K2,Integer> _counter = new TwoDimensionalTreeMap<K1,K2,Integer>();

    public TwoDimensionalTreeCounter() {
        super();

    }

    public void count( K1 key1, K2 key2 ) {

        Integer count = _counter.get( key1, key2 );
        if ( count == null ) {

            _counter.put( key1, key2, 1 );

        } else {

            _counter.put( key1, key2, 1 + count.intValue() );

        }

    }

    public int getCount( K1 key1, K2 key2 ) {

        Integer count = _counter.get( key1, key2 );
        if ( count == null ) {

            return 0;

        } else {

            return count.intValue();

        }

    }

    public boolean containsKeys( K1 key1, K2 key2 ) {

        return _counter.get( key1, key2 ) != null;

    }

    public TwoDimensionalSortedMap<K1,K2,Integer> getTwoDimensionalSortedMap() {

        return _counter;

    }

}
