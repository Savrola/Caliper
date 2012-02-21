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

    public static final GarnettTypeName GARNETT_UNIQUE_ENTITY_NAME = new GarnettTypeName(
            GarnettUniqueEntity.class.getCanonicalName()
    );

    public static int VERSION = 1;

    private final Long _id;

    GarnettUniqueEntity() {
        super();

        _id = RandomCentral.nextLong();

    }

    protected GarnettUniqueEntity( GarnettObjectInputStreamInterface gois )
            throws IOException {
        super();

        gois.checkVersion( GARNETT_UNIQUE_ENTITY_NAME, VERSION, VERSION );

        _id = gois.readLong();

    }

    public void serializeContents( GarnettObjectOutputStreamInterface boos )
            throws IOException {

        boos.writeVersion( VERSION );

        boos.writeLong( _id );

    }

    public final long getId() {

        return _id;

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
