package com.obtuse.wire;

/*
 * Copyright © 2011 Daniel Boulet.
 */

/**
 * Encapsulate the name of a Benoit-aware type.
 * <p/>
 * This class primarily exists to ensure that method parameters whose value is supposed to be a Benoit-aware type's name
 * actually are a Benoit-aware type's name (i.e. it finds bugs).
 */

public class BenoitTypeName implements Comparable<BenoitTypeName> {

    private final String _typeName;
    public static final BenoitTypeName UNKNOWN = new BenoitTypeName( "*** UNKNOWN ***" );

    public BenoitTypeName( String typeName ) {
        super();

        _typeName = typeName;

    }

    public String getTypeName() {

        return _typeName;
    }

    public int hashCode() {

        return _typeName.hashCode();

    }

    public boolean equals( Object rhs ) {

        return rhs instanceof BenoitTypeName && _typeName.equals( ( (BenoitTypeName)rhs ).getTypeName() );

    }

    public int compareTo( BenoitTypeName rhs ) {

        return _typeName.compareTo( rhs.getTypeName() );

    }

    public String toString() {

        return _typeName;

    }

}
