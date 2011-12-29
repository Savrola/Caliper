package com.obtuse.util;

import com.obtuse.util.exceptions.RejectRangeException;

import java.io.Serializable;

/**
 * Something which describes a range.
 * See the {@link Ranges} class for an example of how these critters might be useful.
 * <p/>
 * Copyright © 2009 Obtuse Systems Corporation.
 */

public class Range<T extends Comparable<T>> implements Serializable {

    private final T _startValue;
    private final T _endValue;
    private final long _longStartValue;
    private final long _longEndValue;

    public Range( T startValue, T endValue, long longStartValue, long longEndValue ) {
        super();

        if ( startValue.compareTo( endValue ) > 0 ) {

            throw new IllegalArgumentException( "start value " + format( startValue ) + " is after end value " + format( endValue ) );

        }

        if ( longStartValue > longEndValue ) {

            // This is a MAJOR bug in the user's Range<T> implementation since the long equivalents to each of the endpoint
            // values simply MUST order identically to the endpoints themselves.

            throw new IllegalArgumentException(
                    "long start value " + longStartValue + " is after long end value " + longEndValue +
                    " even though start value " + format( startValue ) + " is NOT after end value " + format( endValue )
            );

        }

        //noinspection UnnecessaryParentheses,ChainedEqualityComparisons
        if ( longStartValue == longEndValue != ( startValue.compareTo( endValue ) == 0 ) ) {

            // This is a MAJOR bug in the user's Range<T> implementation since the long equivalents to each of the endpoint
            // values simply MUST order identically to the endpoints themselves.

            throw new IllegalArgumentException(
                    "long start value " + longStartValue + " is equal to long end value " + longEndValue +
                    " even though start value " + format( startValue ) + " is NOT equal to end value " + format( endValue )
            );

        }

        _startValue = startValue;
        _endValue = endValue;
        _longStartValue = longStartValue;
        _longEndValue = longEndValue;

    }

    /**
     * Determines if the specified range is completely contained within this range.
     * <tt>a.completelyContains( b )</tt> is equivalent to <tt>b.completelyInside( a )</tt>.
     * @param rhs the specified range.
     * @return true if the specified range is completely contained within this range; false otherwise.
     * @throws RejectRangeException if this range is somehow incompatible with the specified range.
     * Note that the default implementation of this class never throws this exception.  It could be used by
     * derived classes' implementations of this method to detect who knows what sort of inconsistencies.
     */

    public boolean completelyContains( Range<T> rhs )
            throws RejectRangeException {

        if ( _startValue.compareTo( rhs.getStartValue() ) <= 0 && rhs.getEndValue().compareTo( _endValue ) <= 0 ) {

            return true;

        } else {

            return false;

        }

    }

    /**
     * Determines if this range is completely inside the specified range.
     * <tt>a.completelyInside( b )</tt> is equivalent to <tt>b.completelyContains( a )</tt>.
     * @param rhs the specified range.
     * @return true if this range is completely inside the specified range.
     * @throws RejectRangeException if this range is somehow incompatible with the specified range.
     * Note that the default implementation of this class never throws this exception.  It could be used by
     * derived classes' implementations of this method to detect who knows what sort of inconsistencies.
     */

    public boolean completelyInside( Range<T> rhs )
            throws RejectRangeException {

        return rhs.completelyContains( this );

    }

    /**
     * Determines if this range overlaps the specified range.
     * @param rhs the specified range.
     * @return true if this range overlaps with the specified range; false otherwise.
     * @throws RejectRangeException if this range is somehow incompatible with the specified range.
     * Note that the default implementation of this class never throws this exception.  It could be used by
     * derived classes' implementations of this method to detect who knows what sort of inconsistencies.
     */

    public boolean overlaps( Range<? extends T> rhs )
            throws RejectRangeException {

//        Logger.logMsg( "ls=" + getLongStartValue() + ", le=" + getLongEndValue() + ", rhs.ls=" + rhs.getLongStartValue() + ", rhs.le=" + rhs.getLongEndValue() );
        if ( getLongStartValue() <= rhs.getLongStartValue() ) {

            return getLongEndValue() >= rhs.getLongStartValue();

        } else {

            return getLongStartValue() <= rhs.getLongEndValue();

        }

    }

    /**
     * Determine if two ranges are right next to each other (no gap between them).
     * @param rhs the other range.
     * @return true if they touch; false otherwise.
     * @throws RejectRangeException if this range is somehow incompatible with the specified range.
     * Note that the default implementation of this class never throws this exception.  It could be used by
     * derived classes' implementations of this method to detect who knows what sort of inconsistencies.
     */

    public boolean touches( Range<? extends T> rhs )
            throws RejectRangeException {

        if ( _longEndValue + 1L == rhs.getLongStartValue() ) {

            return true;

        }

        return _longStartValue - 1L == rhs.getLongEndValue();

    }

    protected String format( T value ) {

        return "" + value;

    }

    public T getStartValue() {

        return _startValue;

    }

    public T getEndValue() {

        return _endValue;

    }

    public long getLongStartValue() {

        return _longStartValue;

    }

    public long getLongEndValue() {

        return _longEndValue;

    }

    public String toString() {

        if ( getStartValue().equals( getEndValue() ) ) {

            return format( getStartValue() );

        } else {

            return format( getStartValue() ) + " to " + format( getEndValue() );

        }

    }

    public void dump() {

        dump( "range " + "from " + formatRange() );

    }

    public void dump( String why ) {

        Logger.logMsg( why );

    }

    public String formatRange() {

        return format( getStartValue() ) + " to " + format( getEndValue() );

    }

}
