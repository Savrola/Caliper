package com.obtuse.util;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * A {@link NamedEntity} which has a unique name within some namespace.
 */

public interface UniquelyNamedEntity extends NamedEntity {

    public String getUniqueName();

}
