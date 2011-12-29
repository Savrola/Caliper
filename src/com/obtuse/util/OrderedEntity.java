package com.obtuse.util;

/*
 * Copyright © 2011 Obtuse Systems Corporation
 */

/**
 * Make it possible to apply a strict ordering to arbitrary instances of some class.
 */

public class OrderedEntity<T> implements Comparable<OrderedEntity<T>> {

    private final Integer _ordering;

    private final T _entity;

    /**
     * Define an instance of this class with a defined ordering.
     * @param ordering an integer value which represents the defined ordering (two instances of this class are ordered
     * using their respective <tt>ordering</tt> values.
     * @param entity the entity to assign the specified ordering to.
     */

    public OrderedEntity( int ordering, T entity ) {
        super();

        _ordering = ordering;
        _entity = entity;

    }

    public int getOrdering() {

        return _ordering.intValue();

    }

    @SuppressWarnings( { "UnusedDeclaration" })
    public T getEntity() {

        return _entity;

    }

    /**
     * Compare this instance with another instance for equality using the two instance's defined ordering values.
     * @param rhs the other instance.
     * @return true if the other instance is a {@link OrderedEntity} and if the two instance's defined orderings are equal; false otherwise.
     */

    public boolean equals( Object rhs ) {

        //noinspection RawUseOfParameterizedType
        return rhs instanceof OrderedEntity && getOrdering() == ((OrderedEntity)rhs).getOrdering();

    }

    public int hashCode() {

        return _ordering.hashCode();

    }

    public int compareTo( OrderedEntity<T> rhs ) {

        return _ordering.compareTo( rhs.getOrdering() );

    }

    public String toString() {

        return "OrderedEntity( ordering = " + _ordering + ", entity = " + _entity + " )";

    }

}
