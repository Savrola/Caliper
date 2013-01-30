package com.obtuse.util;

/**
 * Count unique occurrences of triplets of things.
 * <p>
 * Copyright © 2009 Obtuse Systems Corporation
 */

public interface ThreeDimensionalCounter<K1,K2,K3> {

    void count( K1 key1, K2 key2, K3 key3 );

    int getCount( K1 key1, K2 key2, K3 key3 );

    boolean containsKeys( K1 key1, K2 key2, K3 key3 );

    ThreeDimensionalSortedMap<K1,K2,K3,Integer> getThreeDimensionalSortedMap();

}
