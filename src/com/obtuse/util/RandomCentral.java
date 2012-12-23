package com.obtuse.util;

import java.security.SecureRandom;

/*
 * Copyright © 2012 Daniel Boulet.
 */

/**
 * A thread-safe centralized random number generator.
 */

@SuppressWarnings({ "AccessingNonPublicFieldOfAnotherObject", "UnusedDeclaration" })
public class RandomCentral {

    private final SecureRandom _generator;

    private static RandomCentral _ourInstance = new RandomCentral();

    private RandomCentral() {
        super();

        _generator = new SecureRandom();

    }

    public static RandomCentral getInstance() {

        return RandomCentral._ourInstance;

    }

    public static long nextLong() {

        synchronized ( RandomCentral.getInstance() ) {

            return RandomCentral.getInstance()._generator.nextLong();

        }

    }

    public static int nextInt() {

        synchronized ( RandomCentral.getInstance() ) {

            return RandomCentral.getInstance()._generator.nextInt();

        }

    }

    public static int nextInt( int n ) {

        synchronized ( RandomCentral.getInstance() ) {

            return RandomCentral.getInstance()._generator.nextInt( n );

        }

    }

    public String toString() {

        return "RandomCentral( <<singleton>> )";

    }

}
