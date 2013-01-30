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

    public GaussianDistribution( double center, double standardDeviation ) {

        super();

        _center = center;
        _standardDeviation = standardDeviation;
        _variance = _standardDeviation * _standardDeviation;

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

    public double getY( double x ) {

        return Math.pow(
                Math.exp( -( ( x - _center ) * ( x - _center ) / ( 2 * _variance ) ) ),
                1 / ( _standardDeviation * Math.sqrt( 2 * Math.PI ) )
        );

    }

    public double generateValue( Random rng ) {

        return _center + rng.nextGaussian() * _standardDeviation;

    }

    public void emitAsXml( NestedXMLPrinter ps ) {

        ps.emitTag(
                "GD",
                new String[] {
                        "center=" + getCenter(),
                        "stdev=" + getStandardDeviation()
                }
        );

    }

    public String toString() {

        return "GaussianDistribution( " + _center + ", " + _standardDeviation + " )";

    }

    @SuppressWarnings("MagicNumber")
    private static void doit( Random rng, double center, double standardDeviation, int nTrials ) {

        GaussianDistribution dp = new GaussianDistribution( center, standardDeviation );
        Stats stats = new Stats();
        int[] buckets = new int[3];
        for ( int i = 0; i < nTrials; i += 1 ) {

            double datum = dp.generateValue( rng );
            stats.datum( datum );
            double absDatum = Math.abs( datum - center );
            if ( absDatum < standardDeviation ) {

                buckets[0] += 1;

            }
            if ( absDatum < standardDeviation * 2.0 ) {

                buckets[1] += 1;

            }
            if ( absDatum < standardDeviation * 3.0 ) {

                buckets[2] += 1;

            }

        }

        //noinspection UseOfSystemOutOrSystemErr
        System.out.println(
                "center = " + ObtuseUtil.lpad( center, 0, 4 ) +
                ", expected standard deviation = " + ObtuseUtil.lpad( standardDeviation, 0, 4 ) +
                ", mean = " + ObtuseUtil.lpad( stats.mean(), 0, 4 ) +
                ", actual standardDeviation = " + ObtuseUtil.lpad( stats.sampleStdev(), 0, 4 ) +
                ", expected / actual standard deviation = " +
                ObtuseUtil.lpad( standardDeviation / stats.sampleStdev(), 0, 4 ) +
                ", " + ObtuseUtil.lpad( 100 * buckets[ 0 ] / (double) nTrials, 0, 1 ) + "% <= one stdev" +
                ", " + ObtuseUtil.lpad( 100 * buckets[ 1 ] / (double) nTrials, 0, 1 ) + "% <= two stdevs" +
                ", " + ObtuseUtil.lpad( 100 * buckets[ 2 ] / (double) nTrials, 0, 1 ) + "% <= three stdevs"
        );

    }

    private static final int NTRIALS = 1000000;

    @SuppressWarnings("MagicNumber")
    public static void main( String[] args ) {

        Random rng = new MersenneTwister();
        GaussianDistribution.doit( rng, 0.0, 1.0, GaussianDistribution.NTRIALS );
        GaussianDistribution.doit( rng, 0.0, 2.0, GaussianDistribution.NTRIALS );
        GaussianDistribution.doit( rng, 10.0, 3.0, GaussianDistribution.NTRIALS );
        GaussianDistribution.doit( rng, 0.0, 0.1, GaussianDistribution.NTRIALS );

    }

}
