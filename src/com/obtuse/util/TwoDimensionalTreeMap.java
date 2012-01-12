package com.obtuse.util;

/*
 * Copyright © 2011 Obtuse Systems Corporation
 */

import java.util.Collection;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A sorted map with two dimensions/indices.
 * <p/>
 * The map is sorted in the sense that each dimension's keys are sorted.
 * <p/>
 * The map is actually a double layer {@link TreeMap} dressed up in fancy clothes.  This double layer map is
 * declared as follows:
 *
 * <blockquote><tt>SortedMap&lt;T1,SortedMap&lt;T2,V>> _map = new TreeMap&tl;T1,SortedMap&lt;T2,V>>();</tt></blockquote>
 *
 * @param <T1> The type of the first dimension.
 * @param <T2> The type of the second dimension.
 * @param <V>  The type of the values stored within the map.
 */

public class TwoDimensionalTreeMap<T1,T2,V> implements TwoDimensionalSortedMap<T1,T2,V> {

    private SortedMap<T1,SortedMap<T2,V>> _map = new TreeMap<T1,SortedMap<T2,V>>();

    public TwoDimensionalTreeMap() {
        super();

    }

    public TwoDimensionalTreeMap( TwoDimensionalSortedMap<T1,T2,V> map ) {
        super();

        for ( T1 t1 : map.outerKeys() ) {

            SortedMap<T2,V> innerMap = map.getInnerMap( t1, false );
            for ( T2 t2 : innerMap.keySet() ) {

                put( t1, t2, innerMap.get( t2 ) );

            }

        }

    }

    public void put( T1 key1, T2 key2, V value ) {

        SortedMap<T2,V> innerMap = getInnerMap( key1, true );

        innerMap.put( key2, value );

    }

    public SortedMap<T2,V> getInnerMap( T1 key1, boolean forceCreate ) {

        SortedMap<T2,V> innerMap = _map.get( key1 );
        if ( innerMap == null ) {

            innerMap = new TreeMap<T2,V>();
            _map.put( key1, innerMap );

        }

        return innerMap;

    }

    public V get( T1 key1, T2 key2 ) {

        SortedMap<T2,V> innerMap = _map.get( key1 );
        if ( innerMap == null ) {

            return null;

        }

        return innerMap.get( key2 );

    }

    public Set<T1> outerKeys() {

        return _map.keySet();

    }

    public Collection<SortedMap<T2,V>> innerMaps() {

        return _map.values();

    }

}
