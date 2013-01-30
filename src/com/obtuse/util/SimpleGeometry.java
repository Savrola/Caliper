package com.obtuse.util;

/**
 * %%% Clever words go here!
 * <p/>
 * Copyright © 2011 Obtuse Systems Corporation
 */

public class SimpleGeometry {

    private SimpleGeometry() {

        super();

    }

    /**
     * Perform a straight line interpolation between (0,y1) and (1,y2).
     * <p/>More precisely, for a given value of <tt>x</tt>, compute the value of <tt>y</tt> on a line defined by the two points <tt>(0,y1)</tt> and <tt>(1,y2)</tt>.
     * For example, if <tt>x</tt> is 0.5, <tt>y1</tt> is 10 and <tt>y2</tt> is 20 then <tt>y</tt> is 15.
     * Note that there is no requirement that <tt>x</tt> be between 0 and 1.
     * @param y1 the <tt>y</tt> coordinate of the start of the line.
     * @param y2 the <tt>y</tt> coordinate of the end of the line.
     * @param x the <tt>x</tt> coordinate of the point of interest on the line.
     * @return the <tt>y</tt> coordinate of the point on the line for the specified value of <tt>x</tt>.
     * The actual formula is
     * <blockquote>y1 + ( y2 - y1 ) * x</blockquote>
     */

    public static double interpolate( double y1, double y2, double x ) {

        return SimpleGeometry.interpolate( 0.0, y1, 1.0, y2, x );

    }

    /**
     * Perform a straight line interpolation between (x1,y1) and (x2,y2).
     * <p/>More precisely, for a given value of <tt>x</tt>, compute the value of <tt>y</tt> on a line defined by the two points <tt>(x1,y1)</tt> and <tt>(x2,y2)</tt>.
     * For example, if <tt>x</tt> is 450, <tt>x1</tt> is 100, <tt>y1</tt> is 10, <tt>x2</tt> is 1000 <tt>y2</tt> is 20 then <tt>y</tt> is 15.
     * Note that there is no requirement that <tt>x</tt> be between <tt>x1</tt> and <tt>x2</tt>.
     * @param x1 the <tt>x</tt> coordinate of the start of the line.
     * @param y1 the <tt>y</tt> coordinate of the start of the line.
     * @param x2 the <tt>x</tt> coordinate of the end of the line.
     * @param y2 the <tt>y</tt> coordinate of the end of the line.
     * @param x the <tt>x</tt> coordinate of the point of interest on the line.
     * @return the <tt>y</tt> coordinate of the point on the line for the specified value of <tt>x</tt>.
     * The actual formula is
     * <blockquote>y1 + ( y2 - y1 ) * x</blockquote>
     * @throws IllegalArgumentException if <tt>x1</tt> exactly equals <tt>x2</tt>.
     */

        public static double interpolate( double x1, double y1, double x2, double y2, double x ) {

        @SuppressWarnings({ "UnnecessaryLocalVariable" })
        double rval = y1 + ( y2 - y1 ) * ( x - x1 ) / ( x2 - x1 );
        return rval;

    }

}
