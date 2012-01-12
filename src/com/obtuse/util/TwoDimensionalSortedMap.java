package com.obtuse.util;

import java.util.Collection;
import java.util.Set;
import java.util.SortedMap;

/**
 * Describe how a two dimensional sorted map behaves.
 * <p>
 * Copyright © 2010 Obtuse Systems Corporation
 */

public interface TwoDimensionalSortedMap<T1,T2,V> {

    void put( T1 key1, T2 key2, V value );

    SortedMap<T2,V> getInnerMap( T1 key1, boolean forceCreate );

    V get( T1 key1, T2 key2 );

    Set<T1> outerKeys();

    @SuppressWarnings("UnusedDeclaration")
    Collection<SortedMap<T2,V>> innerMaps();

}
