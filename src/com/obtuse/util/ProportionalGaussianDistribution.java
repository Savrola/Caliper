package com.obtuse.util;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * A gaussian distribution with a weighting factor.
 */

public class ProportionalGaussianDistribution extends GaussianDistribution {

    private final double _weight;

    public ProportionalGaussianDistribution( double weight, double center, double standardDeviation ) {
        super( center, standardDeviation );

        if ( weight < 0.0 ) {

            throw new IllegalArgumentException( "negative weight (" + weight + ") not allowed in ProportionalGaussianDistribution" );

        }

        _weight = weight;

    }

    public double getWeight() {

        return _weight;

    }

}
