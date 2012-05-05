package com.obtuse.garnett;

import com.obtuse.util.RandomCentral;

import java.io.IOException;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * Something which is unique.
 */

public abstract class GarnettUniqueEntity implements GarnettObject {

    public static final int VERSION = 1;

    private final Long _id;

    GarnettUniqueEntity() {
        super();

        _id = RandomCentral.nextLong();

    }

    protected GarnettUniqueEntity( GarnettObjectInputStreamInterface gois )
            throws IOException {
        super();

        gois.checkVersion( GarnettUniqueEntity.class, GarnettUniqueEntity.VERSION, GarnettUniqueEntity.VERSION );

        _id = gois.readLong();

    }

    public void serializeContents( GarnettObjectOutputStreamInterface goos )
            throws IOException {

        goos.writeVersion( GarnettUniqueEntity.VERSION );

        goos.writeLong( _id.longValue() );

    }

    public final long getId() {

        return _id.longValue();

    }

    public abstract String toString();

    protected final int compareToUniqueEntity( GarnettUniqueEntity entity ) {

        return _id.compareTo( entity.getId() );

    }

    public final boolean equals( Object rhs ) {

        return rhs instanceof GarnettUniqueEntity && ((GarnettUniqueEntity)rhs).getId() == getId();

    }

    public final int hashCode() {

        return _id.hashCode();

    }

}
