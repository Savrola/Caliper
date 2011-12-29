package com.obtuse.util;

import java.util.Collection;
import java.util.Set;

/**
 * Describe how a three dimensional sorted map behaves.
 * <p>
 * Copyright © 2010 Obtuse Systems Corporation
 */

public interface ThreeDimensionalSortedMap<T1,T2,T3,V> {

    void put( T1 key1, T2 key2, T3 key3, V value );

    TwoDimensionalSortedMap<T2,T3,V> getInnerMap( T1 key1, boolean forceCreate );

    V get( T1 key1, T2 key2, T3 key3 );

    Set<T1> outerKeys();

    Collection<TwoDimensionalSortedMap<T2,T3,V>> innerMaps();

}
