package com.obtuse.garnett;

/*
 * Copyright © 2012 Daniel Boulet
 */

import com.obtuse.util.NamedEntity;

/**
 * The name of something which is at least vaguely related to Garnett.
 * <p/>
 * Although this class does little more than encapsulate a string, it finds bugs by ensuring that methods which
 * expect to get the name of something which is at least vaguely related to Garnett actually get such a critter
 * (i.e. it finds bugs).
 */

@SuppressWarnings("UnusedDeclaration")
public class GarnettName implements NamedEntity, Comparable<GarnettName> {

    private final String _name;

    public GarnettName( String name ) {
        super();

        _name = name;

    }

    public String getName() {

        return _name;

    }

    public int compareTo( GarnettName rhs ) {

        return _name.compareTo( rhs.getName() );

    }

    public boolean equals( Object rhs ) {

        //noinspection OverlyStrongTypeCast
        return rhs instanceof GarnettName && _name.equals(((GarnettName)rhs).getName());

    }

    public int hashCode() {

        return _name.hashCode();

    }

    public String toString() {

        return getName();

    }

}
