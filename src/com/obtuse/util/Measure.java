package com.obtuse.util;

import java.io.PrintStream;
import java.util.*;

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
    private static final SortedMap<String,CategoryStats> STATS = new TreeMap<String, CategoryStats>();

    private static int s_maxCategoryNameLength = 0;

    private static long s_measuringSinceMillis = System.currentTimeMillis();

    public Measure( String categoryName ) {
        super();

        _categoryName = categoryName;
        _startTimeMillis = System.currentTimeMillis();

    }

    public void done() {

        long now = System.currentTimeMillis();
        long delta = now - _startTimeMillis;

        synchronized ( Measure.STATS ) {

            CategoryStats stats = Measure.STATS.get( _categoryName );
            if ( stats == null ) {

                stats = new CategoryStats();

                Measure.STATS.put( _categoryName, stats );

                if ( _categoryName.length() > Measure.s_maxCategoryNameLength ) {

                    //noinspection AssignmentToStaticFieldFromInstanceMethod
                    Measure.s_maxCategoryNameLength = _categoryName.length();

                }

            }

            //noinspection MagicNumber
            stats.datum( (double)delta / 1.0e3 );

        }

    }

    @SuppressWarnings("UnusedDeclaration")
    public static void showStats( PrintStream where ) {

        Measure.showStats( where, false );

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

        for ( String categoryName : Measure.STATS.keySet() ) {

            CategoryStats stats = Measure.STATS.get( categoryName );

            double value = (double)stats.n() * stats.mean();
            while ( sorted.containsKey( value ) ) {

                //noinspection MagicNumber
                value += 0.000001;

            }

            sorted.put( value, categoryName );

        }

        if ( showTitle ) {

                where.println(
                    ObtuseUtil5.rpad( "category", Measure.s_maxCategoryNameLength + 2 )
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

            CategoryStats stats = Measure.STATS.get( categoryName );

            where.println(
                    ObtuseUtil5.rpad( categoryName, Measure.s_maxCategoryNameLength + 2 )
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

        //noinspection MagicNumber
        where.println("Measuring for " + String.format( "%20.0f", (double)(
                System.currentTimeMillis() - Measure.s_measuringSinceMillis
        ) / 1e3 ).trim() + " seconds" );

    }

    @SuppressWarnings("UnusedDeclaration")
    public static void restart() {

        Measure.STATS.clear();
        Measure.s_maxCategoryNameLength = 0;
        Measure.s_measuringSinceMillis = System.currentTimeMillis();

    }

    @SuppressWarnings("UnusedDeclaration")
    public static void measure( String categoryName, Runnable runnable ) {

        Measure measure = new Measure( categoryName );
        runnable.run();
        measure.done();

    }

}
