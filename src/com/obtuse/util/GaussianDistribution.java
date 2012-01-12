package com.obtuse.util;

/*
 * Copyright © 2011 Obtuse Systems Corporation
 */

import ec.util.MersenneTwister;

import java.util.Random;

/**
 * Describe and implement a gaussian distribution.
 * <p/>
 * Instances of this class are immutable.
 */

@SuppressWarnings("UnusedDeclaration")
public class GaussianDistribution {

    private final double _center;
    private final double _variance;
    private final double _standardDeviation;

    public GaussianDistribution( double center, double variance ) {
        super();

        _center = center;
        _variance = variance;
        _standardDeviation = Math.sqrt( variance );

    }

    public double getCenter() {

        return _center;

    }

    public double getVariance() {

        return _variance;

    }

    public double getStandardDeviation() {

        return _standardDeviation;

    }

    public double generateValue( Random rng ) {

        return _center + rng.nextGaussian() * _standardDeviation;

    }

    public String toString() {

        return "GaussianDistribution( " + _center + ", " + _variance + " )";

    }

    private static void doit( Random rng, double center, double variance, int nTrials ) {

        GaussianDistribution dp = new GaussianDistribution( center, variance );
        Stats stats = new Stats();
        for ( int i = 0; i < nTrials; i += 1 ) {

            stats.datum( dp.generateValue( rng ) );

        }

        System.out.println(
                "center = " + center +
                ", expected variance = " + variance +
                ", mean = " + stats.mean() +
                ", actual variance = " + stats.sampleVariance() +
                ", expected / actual variance = " + variance / stats.sampleVariance()
        );

    }

    private static final int NTRIALS = 10000000;

    public static void main( String[] args ) {

        Random rng = new MersenneTwister();
        doit( rng, 0.0, 1.0, NTRIALS );
        doit( rng, 0.0, 2.0, NTRIALS );
        doit( rng, 10.0, 3.0, NTRIALS );
        doit( rng, 0.0, 0.1, NTRIALS );

    }

}
