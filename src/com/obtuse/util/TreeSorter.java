package com.obtuse.util;

import java.io.Serializable;
import java.util.*;

/**
 * Much like a SortedMap except that duplicate entries are supported.
 * <p/>
 * There is no <tt>get()</tt> method.  See the {@link #getValues} method for the presumably obvious analogous method.
 * <p/>
 * Duplicate entries are supported in the sense that if two
 * equal entries are placed into the sorter than these two equal entries will each
 * appear when the sorter is traversed using its iterator (equal entries appear
 * via the iterator in the same order that they were added to the sorter).
 * <p/>
 * Instances of this class are serializable if both the key and content objects
 * used to create the instance are serializable.
 * <p/>
 * Copyright © 2010 Obtuse Systems Corporation
 */

@SuppressWarnings({ "UnusedDeclaration" })
public class TreeSorter<K extends Comparable<? super K>, V> implements Iterable<V>, Serializable {

    private final SortedMap<K, Collection<V>> _sortedData;

    private class TreeSorterIterator<V> implements Iterator<V> {

        private Iterator<K> _outerIterator;

        private K _currentKey;

        private Iterator<V> _innerIterator;
        private List<V> _currentList;

        private TreeSorterIterator() {
            super();

            _outerIterator = keySet().iterator();

        }

        public boolean hasNext() {

            while ( _innerIterator == null || !_innerIterator.hasNext() ) {

                if ( _outerIterator.hasNext() ) {

                    _currentKey = _outerIterator.next();
                    //noinspection unchecked
                    _currentList = (List<V>)_sortedData.get( _currentKey );

                    _innerIterator = _currentList.iterator();

                } else {

                    return false;

                }

            }

            return _innerIterator.hasNext();

        }

        public V next() {

            return _innerIterator.next();

        }

        public void remove() {

            throw new UnsupportedOperationException( "remove not supported by this iterator" );

        }

    }

    public TreeSorter() {
        super();

        _sortedData = new TreeMap<K, Collection<V>>();

    }

    public TreeSorter( Comparator<? super K> comparator ) {
        super();

        _sortedData = new TreeMap<K, Collection<V>>( comparator );

    }

    public TreeSorter( Map<K, V> map ) {
        super();

        _sortedData = new TreeMap<K, Collection<V>>();
        for ( K key : map.keySet() ) {

            add( key, map.get(key) );

        }

    }

    private TreeSorter( SortedMap<K,Collection<V>> map ) {
        super();

        _sortedData = map;

    }

    /**
     * Returns a view of the portion of this tree sorter whose keys are strictly less than toKey.
     * Analogous to {@link SortedMap#headMap(Object)}.
     * @param toKey high endpoint (exclusive) of the headSorter.
     * @return a view of the specified range of this tree sorter.
     */

    public TreeSorter<K,V> headSorter( K toKey ) {

        return new TreeSorter<K,V>( _sortedData.headMap( toKey ) );

    }

    public TreeSorter<K,V> tailSorter( K fromKey ) {

        return new TreeSorter<K,V>( _sortedData.tailMap( fromKey ) );

    }

    public TreeSorter<K,V> subSorter( K fromKey, K toKey ) {

        return new TreeSorter<K,V>( _sortedData.subMap( fromKey, toKey ) );

    }

    public boolean containsKey( K key ) {

        return _sortedData.containsKey( key );

    }

    public Collection<V> getValues( K key ) {

        return _sortedData.get( key );

    }

    public void add( K key, V value ) {

        Collection<V> values = _sortedData.get( key );
        if ( values == null ) {

            values = new LinkedList<V>();
            _sortedData.put( key, values );

        }

        values.add( value );

    }

    public void addAll( Map<? extends K,? extends V> map ) {

        for ( K key : map.keySet() ) {

            add( key, map.get( key ) );

        }

    }

    public void addAll( TreeSorter<K,V> sorter ) {

        for ( K key : sorter.keySet() ) {

            addAll( key, sorter.getValues( key ) );

        }

    }

    public void addAll( K key, Collection<V> values ) {

        for ( V value : values ) {

            add( key, value );

        }

    }

    /**
     * Returns a Set view of the keys contained in this TreeSorter instance.
     * The set's iterator will return the keys in ascending order.
     * The map is backed by this TreeSorter instance, so changes to
     * this map are reflected in the Set, and vice-versa. The Set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
     * <tt>Set.removeAll</tt>, <tt>Set.retainAll</tt>, and <tt>Set.clear</tt> operations
     * It does not support the <tt>Set.add</tt> or <tt>Set.addAll</tt> operations.
     * @return a set view of the keys in this TreeSorter.
     */

    public Set<K> keySet() {

        return _sortedData.keySet();

    }

    public Collection<V> removeKeyAndValues( K key ) {

        return _sortedData.remove( key );

    }

    public Iterator<V> iterator() {

        @SuppressWarnings({ "UnnecessaryLocalVariable" })
        Iterator<V> iter = new TreeSorterIterator<V>();

        return iter;

    }

    public int size() {

        int totalSize = 0;
        for ( Collection<V> subList : _sortedData.values() ) {

            totalSize += subList.size();

        }

        return totalSize;

    }

    public String toString() {

        return "size = " + size();

    }

    public static void main( String[] args ) {

        TreeSorter<Integer,String> sorter = new TreeSorter<Integer, String>();

        sorter.add( 1, "one" );
        sorter.add( 2, "two" );
        sorter.add( 3, "three" );
        sorter.add( 1, "I" );

        for ( String v : sorter ) {

            System.out.println( v );

        }

    }

}
