package com.obtuse.util;

import java.io.PrintStream;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Measure how long things take.
 * <p/>
 * Copyright © 2008 Obtuse Systems Corporation.
 */

public class Measure {

    private final String _categoryName;

    private final long _startTimeMillis;

    public static class CategoryStats extends Stats {

    }

    @SuppressWarnings( { "ConstantNamingConvention" })
    private static final SortedMap<String,CategoryStats> _stats = new TreeMap<String, CategoryStats>();

    private static int _maxCategoryNameLength = 0;

    private static long _measuringSinceMillis = System.currentTimeMillis();

    public Measure( String categoryName ) {
        super();

        _categoryName = categoryName;
        _startTimeMillis = System.currentTimeMillis();

    }

    public void done() {

        long now = System.currentTimeMillis();
        long delta = now - _startTimeMillis;

        synchronized ( _stats ) {

            CategoryStats stats = _stats.get( _categoryName );
            if ( stats == null ) {

                stats = new CategoryStats();

                _stats.put( _categoryName, stats );

                if ( _categoryName.length() > _maxCategoryNameLength ) {

                    _maxCategoryNameLength = _categoryName.length();

                }

            }

            //noinspection MagicNumber
            stats.datum( (double)delta / 1.0e3 );

        }

    }

    public static void showStats( PrintStream where ) {

        showStats( where, false );

    }

    @SuppressWarnings({ "SameParameterValue" })
    public static void showStats( PrintStream where, boolean showTitle ) {

        SortedMap<Double,String> sorted = new TreeMap<Double, String>(
                new Comparator<Double>() {
                    public int compare( Double lhs, Double rhs ) {
                        return rhs.compareTo( lhs );
                    }
                }
        );

        for ( String categoryName : _stats.keySet() ) {

            CategoryStats stats = _stats.get( categoryName );

            double value = (double)stats.n() * stats.mean();
            while ( sorted.containsKey( value ) ) {

                value += 0.000001;

            }

            sorted.put( value, categoryName );

        }

        if ( showTitle ) {

                where.println(
                    ObtuseUtil5.rpad( "category", _maxCategoryNameLength + 2 )
                    + "   " +
                    ObtuseUtil5.lpad( "count", 10 )
                    + "   " +
                    ObtuseUtil5.lpad( "mean", 10 )
                    + "   " +
                    ObtuseUtil5.lpad( "stdev", 10 )
                    + "   " +
                    ObtuseUtil5.lpad( "total", 10 )
            );

        }

        for ( String categoryName : sorted.values() ) {

            CategoryStats stats = _stats.get( categoryName );

            where.println(
                    ObtuseUtil5.rpad( categoryName, _maxCategoryNameLength + 2 )
                    + " : " +
                    ObtuseUtil5.lpad( (long)stats.n(), 10 )
                    + " : " +
                    String.format( "%10.6f", stats.mean() )
                    + " : " +
                    String.format( "%10.6f", stats.populationStdev() )
                    + " : " +
                    String.format( "%10.3f", (double)stats.n() * stats.mean() )
            );

        }

        where.println("Measuring for " + String.format( "%20.0f", (double)(
                System.currentTimeMillis() - _measuringSinceMillis
        ) / 1e3 ).trim() + " seconds" );

    }

    public static void restart() {

        _stats.clear();
        _maxCategoryNameLength = 0;
        _measuringSinceMillis = System.currentTimeMillis();

    }

    public static void measure( String categoryName, Runnable runnable ) {

        Measure measure = new Measure( categoryName );
        runnable.run();
        measure.done();

    }

}
