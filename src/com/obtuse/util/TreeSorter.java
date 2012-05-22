package com.obtuse.util;

import java.io.Serializable;
import java.util.*;

/**
 * Provide a way to sort data while automatically dealing with duplicate keys.
 * Analogous to a {@link TreeMap} except that the key-value associates are one-to-many.
 * For example, a single tree sorter can contain both <tt>fred</tt>-><tt>hello</tt> and <tt>fred</tt>-><tt>world</tt> associations.
 * <p/>
 * There is no <tt>get()</tt> method.  See the {@link #getValues} method for the presumably obvious analogous method.
 * <p/>
 * Duplicate entries are supported in the sense that if two
 * identical associations are placed into the sorter than the value associated with these two equal entries will
 * appear twice when the sorter is traversed using its iterator (equal entries appear
 * via the iterator in the same order that they were added to the sorter).
 * For example, if the association <tt>fred</tt>-><tt>hello</tt> is already in the tree sorter when the association
 * <tt>fred</tt>-><tt>hello</tt> is added to the tree sorter then a scan through all of the values associated with
 * <tt>fred</tt> will yield <tt>hello</tt> more than once.
 * <p/>
 * If two or more different associations which both use the same key are added to the tree sorter then a scan
 * through all of the values associated with the key will yield the values in the same order that their associations
 * were added to the tree sorter.
 * For example, if the associations <tt>fred</tt>-><tt>how</tt>, <tt>fred</tt>-><tt>are</tt>,
 * <tt>fred</tt>-><tt>you</tt> and <tt>fred</tt>-><tt>today</tt>
 * are added to a previously empty tree sorter in the specified order then a scan of all of the values associated with
 * the key <tt>fred</tt> will yield <tt>how</tt>, <tt>are</tt>, <tt>you</tt> and <tt>today</tt> in that order.
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

        private K _currentKey = null;

        private Iterator<V> _innerIterator = null;
        private Collection<V> _currentList;

        private TreeSorterIterator() {

            super();

            _outerIterator = keySet().iterator();

        }

        public boolean hasNext() {

            while ( _innerIterator == null || !_innerIterator.hasNext() ) {

                if ( _outerIterator.hasNext() ) {

                    _currentKey = _outerIterator.next();
                    //noinspection unchecked
                    _currentList = (Collection<V>)_sortedData.get( _currentKey );

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

    /**
     * Construct a new, empty tree sorter, using the natural ordering of its keys.
     * All keys inserted in this tree sorter must implement the {@link Comparable} interface and must be
     * <i>mutually comparable</i>({@link TreeMap#TreeMap()} for a discussion of what this means).
     */

    public TreeSorter() {

        super();

        _sortedData = new TreeMap<K, Collection<V>>();

    }

    /**
     * Construct a new, empty tree sorter ordered according to the specified comparator.
     * All keys inserted into this tree sorter must be mutually comparable by the the specified comparator
     * ({@link TreeMap#TreeMap(java.util.Comparator)} for a discussion of what this means).
     *
     * @param comparator the comparator that will be used to order this tree sorter.
     */

    public TreeSorter( Comparator<? super K> comparator ) {

        super();

        _sortedData = new TreeMap<K, Collection<V>>( comparator );

    }

    /**
     * Constructs a new tree sorter containing the same mappings as the specified map.
     * The keys will be the natural sorted order of <tt>K</tt>.
     *
     * @param map the map whose mappings are to be used to create the new tree sorter.
     */

    public TreeSorter( Map<K, V> map ) {

        super();

        _sortedData = new TreeMap<K, Collection<V>>();
        for ( K key : map.keySet() ) {

            add( key, map.get( key ) );

        }

    }

    /**
     * Constructs a new tree sorter containing the same mappings as the specified sorted map.
     * The new tree sorter will use the same comparator as the specified sorted map.
     *
     * @param map the map whose mappings are to be used to create the new tree sorter.
     */

    public TreeSorter( SortedMap<K, V> map ) {

        super();

        _sortedData = new TreeMap<K, Collection<V>>( map.comparator() );
        for ( K key : map.keySet() ) {

            add( key, map.get( key ) );

        }

    }

    /**
     * Construct a new tree sorter which is a copy of an existing tree sorter.
     * <p/>This method is equivalent to constructing a new tree sorter called <tt>newSorter</tt> using the following
     * procedure:
     * <pre>
     * TreeSorter&lt;K,V&gt; newSorter = new TreeSorter&lt;K,V&gt;();
     * for ( K key : sorter.keySet() ) {
     *
     *     newSorter.addAll( key, sorter.getValues( key ) );
     *
     * }
     * </pre>
     *
     * @param sorter the tree sorter whose key associations are to be copied into the newly created tree sorter.
     */

    public TreeSorter( TreeSorter<K, V> sorter ) {

        this();

        for ( K key : sorter.keySet() ) {

            addAll( key, sorter.getValues( key ) );

        }

    }

    /**
     * Construct a new tree sorter which is backed by a different tree sorter.
     * <p/>This method is used internally to implement the
     * {@link #headSorter}, {@link #tailSorter} and {@link #subSorter} methods.  It is not intended to be used for any
     * other purpose and probably should not be exposed to the general public.
     *
     * @param map     the map which is to form the basis of this tree sorter instance.
     * @param ignored an extra parameter to ensure that the signature of this constructor is different than
     *                that of one or more of the other publically available constructors.  This parameter is totally
     *                ignored.
     */

    private TreeSorter( SortedMap<K, Collection<V>> map, int ignored ) {

        super();

        _sortedData = map;

    }

    /**
     * Returns a view of the portion of this tree sorter whose keys are strictly less than toKey.
     * <p/>
     * The returned tree sorter is backed by this tree sorter, so changes in the returned tree sorter are reflected
     * in this
     * tree sorter, and vice-versa. The returned tree sorter will throw an <tt>IllegalArgumentException</tt> on an
     * attempt
     * to insert a key outside its range.
     * <p/>
     * Analogous to {@link SortedMap#headMap(Object)}.
     *
     * @param toKey high endpoint (exclusive) of the headSorter.
     * @return a view of this tree sorter whose keys are strictly less than <tt>toKey</tt>.
     * @throws IllegalArgumentException if this tree sorter
     *                                  itself has a restricted range, and <tt>toKey</tt> lies outside the bounds of the range.
     */

    public TreeSorter<K, V> headSorter( K toKey ) {

        return new TreeSorter<K, V>( _sortedData.headMap( toKey ), 0 );

    }

    /**
     * Returns a view of the portion of this tree sorter whose keys are strictly greater than or equal to fromKey.
     * <p/>
     * The returned tree sorter is backed by this tree sorter, so changes in the returned tree sorter are reflected
     * in this
     * tree sorter, and vice-versa. The returned tree sorter will throw an <tt>IllegalArgumentException</tt> on an
     * attempt
     * to insert a key outside its range.
     * <p/>
     * Analogous to {@link SortedMap#tailMap(Object)}.
     *
     * @param fromKey low endpoint (inclusive) of the headSorter.
     * @return a view of this tree sorter whose keys are greater than or equal to <tt>fromKey</tt>.
     * @throws IllegalArgumentException this tree sorter
     *                                  itself has a restricted range, and <tt>fromKey</tt> lies outside the bounds of the range.
     */

    public TreeSorter<K, V> tailSorter( K fromKey ) {

        return new TreeSorter<K, V>( _sortedData.tailMap( fromKey ), 0 );

    }

    /**
     * Returns a view of this tree sorter from <tt>fromKey</tt>, inclusive, to <tt>toKey</tt>,
     * exclusive (if <tt>fromKey</tt>
     * and <tt>toKey</tt> are equal then the returned tree sorter is empty).
     * <p/>
     * The returned tree sorter is backed by this tree sorter, so changes in the returned tree sorter are reflected
     * in this
     * tree sorter, and vice-versa. The returned tree sorter will throw an <tt>IllegalArgumentException</tt> on an
     * attempt
     * to insert a key outside its range.
     * <p/>
     * Analogous to {@link SortedMap#subMap(Object, Object)}.
     *
     * @param fromKey low endpoint (inclusive) of the keys in the returned tree sorter.
     * @param toKey   high endpoint (exclusive) of the keys in the returned tree sorter.
     * @return a view of the portion of this tree sorter specified by the keys.
     * @throws IllegalArgumentException if <tt>fromKey</tt> is greater than <tt>toKey</tt>; or if this tree sorter
     *                                  itself has a restricted range, and <tt>fromKey</tt> or <tt>toKey</tt> lies outside the bounds of the range.
     */

    public TreeSorter<K, V> subSorter( K fromKey, K toKey ) {

        return new TreeSorter<K, V>( _sortedData.subMap( fromKey, toKey ), 0 );

    }

    /**
     * Determines if the specified key exists within this tree sorter.
     * Analogous to {@link SortedMap#containsKey(Object)}.
     *
     * @param key the specified key.
     * @return true if this tree sorter includes this key.
     */

    public boolean containsKey( K key ) {

        return _sortedData.containsKey( key );

    }

    /**
     * Return the values associated with a specified key.
     * The values in the returned collection appear in the order that they were added to this tree sorter.
     * The returned collection of values is immutable although the contents of the collection could change if
     * more data is added to this tree sorter.
     * <p/>This operation is always quite fast as it returns a view into this tree sorter's data
     * rather than a copy of this tree sorter's data.
     *
     * @param key the specified key.
     * @return the values associated with the specified key.
     */

    public Collection<V> getValues( K key ) {

        return Collections.unmodifiableCollection( _sortedData.get( key ) );

    }

    /**
     * Return all the values in this tree sorter in key order.
     * Values with unequal keys are returned in key-sorted order.
     * Values with equal keys are returned in the order that they were added to this tree sorter.
     * <p/>Every call to this method returns a distinct collection of values.  The caller is free to do
     * whatever they like to the returned collection.
     * <p/>This is potentially a rather expensive operation depending upon how much data is in this tree sorter instance.
     *
     * @return all the values in this tree sorter.
     */

    public Collection<V> getAllValues() {

        Collection<V> allValues = new LinkedList<V>();
        for ( K key : _sortedData.keySet() ) {

            allValues.addAll( getValues( key ) );

        }

        return allValues;

    }

    /**
     * Add a new key-value pair to this tree sorter.
     * Each tree sorter instance is capable of maintaining an arbitrary number of one to many key to value associations.
     * For example, if the key-value pair <tt>fred=hello</tt> and <tt>fred=world</tt> are added to a previously
     * empty tree sorter then the key <tt>fred</tt> will have both <tt>hello</tt> and <tt>world</tt> associated
     * with it in the tree sorter.
     * Analogous to {@link SortedMap#put(Object, Object)}.
     *
     * @param key   with which the specified value is to be associated.
     * @param value the value to be associated with the specified key.
     */

    public final void add( K key, V value ) {

        Collection<V> values = _sortedData.get( key );
        if ( values == null ) {

            values = new LinkedList<V>();
            _sortedData.put( key, values );

        }

        values.add( value );

    }

    /**
     * Add all of the key value associations from a {@link Map} to this tree sorter.
     * This method is exactly equivalent to
     * <pre>
     * for ( K key : map.keySet() ) {
     *     treeSorter.add( key, map.get( key ) );
     * }
     * </pre>
     *
     * @param map the map whose contents are to be added to this tree sorter.
     */

    public void addAll( Map<? extends K, ? extends V> map ) {

        for ( K key : map.keySet() ) {

            add( key, map.get( key ) );

        }

    }

    /**
     * Add all of the associations from a different tree sorter to this tree sorter.
     * This method is exactly equivalent to
     * <pre>
     * for ( K key : sorter.keySet() ) {
     *     treeSorter.addAll( key, sorter.getValues( key ) );
     * }
     * </pre>
     *
     * @param sorter the tree sorter whose contents are to be added to this tree sorter.
     * @throws IllegalArgumentException if an attempt is made to add the contents of a tree sorter to itself.
     */

    public void addAll( TreeSorter<K, V> sorter ) {

        if ( this == sorter ) {

            throw new IllegalArgumentException( "attempt to add a tree sorter to itself" );

        }

        for ( K key : sorter.keySet() ) {

            addAll( key, sorter.getValues( key ) );

        }

    }

    /**
     * Associate all of the values in a collection with a specified key.
     * This method is exactly equivalent to
     * <pre>
     * for ( V value : values ) {
     *     treeSorter.add( key, value );
     * }
     * </pre>
     * Truly disconcerting things will probably happen if the following is attempted:
     * <pre>
     *  treeSorter.addAll( key, treeSorter.getAll( key ) )
     * </pre>
     *
     * @param key    the key that all of the values in the specfied collection are to be associated with.
     * @param values the values which are to be associated with the specified key.
     */

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
     *
     * @return a set view of the keys in this TreeSorter.
     */

    public Set<K> keySet() {

        return _sortedData.keySet();

    }

    /**
     * Removes all of the values associated with a specified key.
     *
     * @param key the key for which all associated values are to be removed.
     * @return a collection of the values which were removed. The caller is free to do whatever they wish with the
     *         returned collection as it is no longer associated with this tree sorter instance once it has been returned.
     */

    public Collection<V> removeKeyAndValues( K key ) {

        return _sortedData.remove( key );

    }

    /**
     * Get an iterator which iterates across all of the values.
     * Changes to the tree sorter while the iterator is in use could invalidate the iterator resulting in all sorts of
     * strange things happening.
     *
     * @return an iterator which iterates across all of the values in this tree sorter.
     */

    public Iterator<V> iterator() {

        @SuppressWarnings({ "UnnecessaryLocalVariable" })
        Iterator<V> iter = new TreeSorterIterator<V>();

        return iter;

    }

    /**
     * Returns the number of values in this tree sorter.
     * <p/>This method could be fairly expensive if there are a lot of values in this tree sorter.
     * <p/>This method is equivalent to:
     * <pre>
     * int totalSize = 0;
     * for ( K key : treeSorter.keySet() ) {
     *     totalSize += treeSorter.getValues().size();
     * }
     * return totalSize;
     * </pre>
     *
     * @return the number of values in this tree sorter.
     */

    public int size() {

        int totalSize = 0;
        for ( Collection<V> subList : _sortedData.values() ) {

            totalSize += subList.size();

        }

        return totalSize;

    }

    /**
     * Determine if this tree sorter has any key value associations in it.
     * <p/>This method is always very fast.
     *
     * @return true if this tree sorter is empty; false otherwise.
     */

    public boolean isEmpty() {

        return _sortedData.isEmpty();

    }

    /**
     * Returns a string which states the current size of this tree sorter.
     * <p/>The returned string is of the form
     * <pre>
     * size = <i>n</i>
     * </pre>
     * where <tt><i>n</i></tt> is the current size of this tree sorter.
     * This method calls {@link #size()} which means that it could be somewhat expensive if there are a lot of values in
     * this tree sorter.
     *
     * @return a string which states the current size of this tree sorter.
     */

    public String toString() {

        return "size = " + size();

    }

    public static void main( String[] args ) {

        TreeSorter<Integer, String> sorter = new TreeSorter<Integer, String>();

        sorter.add( 1, "one" );
        sorter.add( 2, "two" );
        sorter.add( 3, "three" );
        sorter.add( 1, "I" );

        for ( String v : sorter ) {

            //noinspection UseOfSystemOutOrSystemErr
            System.out.println( v );

        }

    }

}
