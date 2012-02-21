package com.obtuse.garnett;

/*
 * Copyright © 2011 Daniel Boulet.
 */

/**
 * Encapsulate the name of a Garnett-aware type.
 * <p/>
 * This class primarily exists to ensure that method parameters whose value is supposed to be a Garnett-aware type's name
 * actually are a Garnett-aware type's name (i.e. it finds bugs).
 */

public class GarnettTypeName implements Comparable<GarnettTypeName> {

    private final String _typeName;
    public static final GarnettTypeName UNKNOWN = new GarnettTypeName( "*** UNKNOWN ***" );

    public GarnettTypeName( String typeName ) {
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

        return rhs instanceof GarnettTypeName && _typeName.equals( ( (GarnettTypeName)rhs ).getTypeName() );

    }

    public int compareTo( GarnettTypeName rhs ) {

        return _typeName.compareTo( rhs.getTypeName() );

    }

    public String toString() {

        return _typeName;

    }

}
