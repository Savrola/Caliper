package com.obtuse.util;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * Generate unique ids within some id-space.
 */

public interface UniqueIdGenerator {

    /**
     * Generate an id which is different than any other ids generated by separate invocations of this method on
     * this instance.
     * <p/>Implementations of this method should be thread safe.
     * @return an id which is unique from the perspective of this instance.
     */

    long getUniqueId();

}
