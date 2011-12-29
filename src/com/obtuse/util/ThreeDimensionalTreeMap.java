package com.obtuse.util;

import java.util.Collection;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/*
 * Copyright © 2011 Obtuse Systems Corporation
 */

public class ThreeDimensionalTreeMap<T1,T2,T3,V> implements ThreeDimensionalSortedMap<T1,T2,T3,V> {

    private SortedMap<T1,TwoDimensionalSortedMap<T2,T3,V>> _map = new TreeMap<T1,TwoDimensionalSortedMap<T2,T3,V>>();

    public ThreeDimensionalTreeMap() {
        super();

    }

    public ThreeDimensionalTreeMap( ThreeDimensionalSortedMap<T1,T2,T3,V> map ) {
        super();

        for ( T1 t1 : map.outerKeys() ) {

            TwoDimensionalSortedMap<T2,T3,V> innerMap = map.getInnerMap( t1, false );
            _map.put( t1, new TwoDimensionalTreeMap<T2,T3,V>( innerMap ) );

        }

    }

    public void put( T1 key1, T2 key2, T3 key3, V value ) {

        TwoDimensionalSortedMap<T2,T3,V> innerMap = getInnerMap( key1, true );

        innerMap.put( key2, key3, value );

    }

    public TwoDimensionalSortedMap<T2,T3,V> getInnerMap( T1 key1, boolean forceCreate ) {

        TwoDimensionalSortedMap<T2,T3,V> innerMap = _map.get( key1 );
        if ( innerMap == null ) {

            innerMap = new TwoDimensionalTreeMap<T2,T3,V>();
            _map.put( key1, innerMap );

        }

        return innerMap;

    }

    public V get( T1 key1, T2 key2, T3 key3 ) {

        TwoDimensionalSortedMap<T2,T3,V> innerMap = _map.get( key1 );
        if ( innerMap == null ) {

            return null;

        }

        return innerMap.get( key2, key3 );

    }

    public Set<T1> outerKeys() {

        return _map.keySet();

    }

    public Collection<TwoDimensionalSortedMap<T2,T3,V>> innerMaps() {

        return _map.values();

    }

}
