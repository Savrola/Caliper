package com.obtuse.util;

import java.io.Serializable;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Count occurrences of things using a sorted mapping.
 * <p/>
 * Instances of this class are serializable if the key objects used to create the instance are serializable.
 * <p/>
 * Copyright © 2009 Obtuse Systems Corporation
 */

@SuppressWarnings("UnusedDeclaration")
public class TreeCounter<K extends Comparable<K>> implements Counter<K>, Serializable {

    private SortedMap<K,Integer> _counts = new TreeMap<K,Integer>();

    public TreeCounter() {
        super();
    }

    public void count( K thing ) {

        if ( _counts.containsKey( thing ) ) {

            _counts.put( thing, _counts.get( thing ).intValue() + 1 );

        } else {

            _counts.put( thing, 1 );

        }

    }

    public Set<K> keySet() {

        return _counts.keySet();

    }

    public boolean containsKey( K thing ) {

        return _counts.containsKey( thing );

    }

    public int getCount( K thing ) {

        Integer count = _counts.get( thing );

        return count == null ? 0 : count.intValue();

    }

    public String toString() {

        StringBuilder counts = new StringBuilder( "TreeCounter( " );
        String comma = "";
        int count = 0;
        for ( K key : _counts.keySet() ) {

            counts.append( comma ).append( key ).append( '=' ).append( getCount( key ) );
            comma = ", ";

        }

        return counts.append( " )" ).toString();

    }

}
