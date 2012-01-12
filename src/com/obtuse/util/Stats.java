package com.obtuse.util;

/**
 * Compute various statistics.
 * <p/>
 * Copyright © 2006 Obtuse Systems Corporation
 */

@SuppressWarnings("UnusedDeclaration")
public class Stats {

    @SuppressWarnings({ "InstanceVariableNamingConvention" })
    private double _sum;
    private double _sumSq;
    private double _maxValue;
    private double _minValue;
    @SuppressWarnings({ "InstanceVariableNamingConvention" })
    private int _n;

    /**
     * Create a new instance.
     */

    public Stats() {
        super();

        _sum = 0;
        _sumSq = 0;
        _n = 0;

    }

    /**
     * Make a copy of an instance.
     * @param x the instance to be copied.
     */

    public Stats( Stats x ) {
        super();

        _sum = x._sum;
        _sumSq = x._sumSq;
        _n = x._n;
        _maxValue = x._maxValue;
        _minValue = x._minValue;

    }

    /**
     * Provide the next value in the sequence.
     * @param v the next value.
     */

    public void datum( double v ) {

        _sum += v;
        _sumSq += v * v;
        if ( _n == 0 ) {

            _maxValue = v;
            _minValue = v;

        } else {

            if ( v > _maxValue ) {
                _maxValue = v;
            }

            if ( v < _minValue ) {
                _minValue = v;
            }

        }

        _n += 1;

    }

    /**
     * Provide a separately accumulated set of values in the form of their sum and sumSq's.
     * @param sum the sum of the separately accumulated values.
     * @param sumsq the sum of the squares of the separately accumulated values.
     * @param n the number of separately accumulated values.
     */

    public void datum( double sum, double sumsq, int n ) {

        _sum += sum;
        _sumSq += sumsq;
        _n += n;

    }

    /**
     * Compute the arithmetic mean of the sequence.
     * @return the arithmetic mean of the sequence.
     */

    public double mean() {

        return _sum / _n;

    }

    /**
     * Compute the variance of the sequence assuming that the sequence
     * represents a sample of the population.
     * @return the sample variance.
     */

    public double sampleVariance() {

        //noinspection UnnecessaryParentheses
        return ( _sumSq - ( _sum * _sum ) / _n ) / ( _n - 1 );

    }

    /**
     * Compute the variance of the sequence assuming that the sequence
     * represents the entire population.
     * @return the population variance.
     */

    public double populationVariance() {

        //noinspection UnnecessaryParentheses
        return ( _sumSq - ( _sum * _sum ) / _n ) / _n;

    }

    /**
     * Compute the standard deviation of the sequence assuming that the sequence
     * represents the entire population.
     * @return the population standard deviation.
     */

    public double populationStdev() {

        return Math.sqrt( populationVariance() );

    }

    /**
     * Compute the standard of the sequence assuming that the sequence
     * represents a sample of the population.
     * @return the sample standard deviation.
     */

    public double sampleStdev() {

        return Math.sqrt( sampleVariance() );

    }

    /**
     * Return the smallest value in the data set.
     * @return the smallest value in the data set (undefined if data set is empty).
     */

    public double getMinValue() {

        return _minValue;

    }

    /**
     * Return the largest value in the data set.
     * @return the largest value in the data set (undefined if data set is empty).
     */

    public double getMaxValue() {

        return _maxValue;

    }

    /**
     * Return just the sum of the values.
     * @return the sum of the values.
     */

    public double sum() {

        return _sum;

    }

    /**
     * Return just the sum of the square of the values.
     * @return the sum of the squares of the values.
     */

    public double sumSq() {

        return _sumSq;

    }

    /**
     * Return the number of datusm in the sequence.
     * @return the number of datums in the sequence.
     */

    public int n() {

        return _n;

    }

    public String toString() {

        return "Stats(" + " n = " + _n + ( _n > 0 ? ", mean = " + mean() : "" ) +
               ( _n > 0 ? ", popStdDev = " + populationStdev() : "" ) +
               ( _n > 1 ? ", sampleStdDev = " + sampleStdev() : "" ) + " )";

    }

}
