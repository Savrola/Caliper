package com.obtuse.util;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.exceptions.ParsingException;

import javax.management.timer.Timer;
import java.util.Calendar;
import java.util.Date;

/**
 * Represent a calendar date.
 * <p/>
 * This class represents dates as actual calendar dates.
 * This avoids the alternative of using a Java Date object
 * with the time-within-the-date part of the object's value
 * set to something which is always a compromise of some sort.
 * <p/>
 * Instances of this class are immutable.
 */

@SuppressWarnings("UnusedDeclaration")
public class CalendarDate implements Comparable<CalendarDate> {

    private final String _dateString;
    private final long _dateStartTimeMs;
    private final long _dateEndTimeMs;
    private final long _midnightUtcMs;

    public CalendarDate( String dateString )
            throws ParsingException {
        super();

        _dateString = dateString;

        if ( dateString.length() != "2012-10-05".length() ) {

            throw new ParsingException(
                    "date \"" + dateString + "\" is wrong length (must be _exactly_ " + "2012-10-05".length() + " characters)",
                    0,
                    0,
                    ParsingException.ErrorType.DATE_FORMAT_ERROR
            );

        }

        _midnightUtcMs = DateUtils.parseYYYY_MM_DD_utc( dateString, 0 ).getTime();

        Calendar cal = Calendar.getInstance();
        cal.setTime( DateUtils.parseYYYY_MM_DD( dateString, 0 ) );
        cal.set( Calendar.HOUR_OF_DAY, 0 );
        cal.set( Calendar.MINUTE, 0 );
        cal.set( Calendar.SECOND, 0 );
        _dateStartTimeMs = cal.getTimeInMillis();
        cal.add( Calendar.DAY_OF_YEAR, 1 );
        _dateEndTimeMs = cal.getTimeInMillis() - 1;

    }

    public CalendarDate( Date date ) {
        super();

        _dateString = DateUtils.formatYYYY_MM_DD( date );
        try {

            _midnightUtcMs = DateUtils.parseYYYY_MM_DD_utc( _dateString, 0 ).getTime();

        } catch ( ParsingException e ) {

            throw new HowDidWeGetHereError( "unable to parse date \"" + _dateString + "\" which we formatted", e );

        }

        Calendar cal = Calendar.getInstance();
        cal.setTime( date );
        cal.set( Calendar.HOUR_OF_DAY, 0 );
        cal.set( Calendar.MINUTE, 0 );
        cal.set( Calendar.SECOND, 0 );
        _dateStartTimeMs = cal.getTimeInMillis();
        cal.add( Calendar.DAY_OF_YEAR, 1 );
        _dateEndTimeMs = cal.getTimeInMillis() - 1;

    }

    public static int computeDurationDays( CalendarDate from, CalendarDate to ) {

        if ( from.compareTo( to ) > 0 ) {

            throw new HowDidWeGetHereError( "probable bug:  from (" + from + ") > to (" + to + ")" );

        }

        long durationMs = to._midnightUtcMs - from._midnightUtcMs;
        if ( durationMs % Timer.ONE_DAY != 0L ) {

            throw new HowDidWeGetHereError( "days are not 24 hours long" );

        }

        int durationDays = 1 + (int)( durationMs / Timer.ONE_DAY );

//        Logger.logMsg( "duration between " + this + " and " + rhs + " is " + computeDurationDays + " days" );

        return durationDays;

    }

    public static CalendarDate addDays( CalendarDate date, int days ) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis( date._dateStartTimeMs );
        cal.add( Calendar.DAY_OF_YEAR, days );

        return new CalendarDate( cal.getTime() );

    }

    public String getDateString() {

        return _dateString;

    }

    public long getDateStartTimeMs() {

        return _dateStartTimeMs;

    }

    public long getDateEndTimeMs() {

        return _dateEndTimeMs;

    }

    public boolean equals( Object rhs ) {

        //noinspection ChainOfInstanceofChecks
        if ( rhs instanceof CalendarDate ) {

            return _dateString.equals( ((CalendarDate) rhs).getDateString() );

        } else if ( rhs instanceof Date ) {

            Date rhsDate = (Date)rhs;
            return _dateStartTimeMs <= rhsDate.getTime() && rhsDate.getTime() <= _dateEndTimeMs;

        } else {

            return false;

        }

    }

    public int hashCode() {

        return _dateString.hashCode();

    }

    public int compareTo( CalendarDate rhs ) {

        return _dateString.compareTo( rhs._dateString );

    }

    public boolean containsDate( Date rhs ) {

        return _dateStartTimeMs <= rhs.getTime() && rhs.getTime() <= _dateEndTimeMs;

    }

    public String toString() {

        return _dateString;

    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Shared", "CalendarDate", null );

        try {

            CalendarDate start = new CalendarDate( "2009-02-28" );
            for ( int i = 0; i < 20; i += 1 ) {

                CalendarDate end = CalendarDate.addDays( start, i );

                Logger.logMsg( "from " + start + " to " + end + " is " + CalendarDate.computeDurationDays( start, end ) + " days" );

            }

        } catch ( ParsingException e ) {

            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

        } catch ( HowDidWeGetHereError e ) {

            e.printStackTrace();

        }

    }

}
