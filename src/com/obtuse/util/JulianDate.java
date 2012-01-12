package com.obtuse.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class JulianDate {

    private static GregorianCalendar _gregorianCalendar = new GregorianCalendar();

    /**
     * Returns the Julian day number that begins at noon of
     * the specified day.
     * Positive year signifies A.D., negative year B.C.
     * Remember that the year after 1 B.C. was 1 A.D.
     * <p/>
     * ref :
     * Numerical Recipes in C, 2nd ed., Cambridge University Press 1992
     * <p/>
     * Source:  http://www.rgagnon.com/javadetails/java-0506.html
     */

    // Gregorian Calendar adopted Oct. 15, 1582 (2299161)

    public static final int JGREG = 15 + 31 * ( 10 + 12 * 1582 );

    private JulianDate() {

        super();

    }

    /**
     * Return the Julian day number for a Gregorian date specified in yy-mm-dd form.
     *
     * @param ymd the Gregorian date where ymd[0] is the year, ymd[1] is the month (one origin)
     *            and ymd[2] is the day of the month.
     * @return the Julian day for the specified Gregorian date.
     */

    public static long toJulian( int[] ymd ) {

        int year = ymd[0];
        int month = ymd[1]; // jan=1, feb=2,...
        int day = ymd[2];
        int julianYear = year;
        if ( year < 0 ) {

            julianYear++;

        }

        int julianMonth = month;
        if ( month > 2 ) {

            julianMonth++;

        } else {

            julianYear--;
            julianMonth += 13;

        }

        double julian = Math.floor( 365.25 * julianYear ) +
                        Math.floor( 30.6001 * julianMonth ) +
                        day + 1720995.0;
        if ( day + 31 * ( month + 12 * year ) >= JGREG ) {

            // change over to Gregorian calendar
            int ja = (int)( 0.01 * julianYear );
            julian += 2 - ja + 0.25 * ja;

        }

        return (long)Math.floor( julian );

    }

    /**
     * Return the Julian day number for a date encapsulated in a {@link java.util.Date} object.
     *
     * @param d the input date.
     * @return the Julian day for the specified {@link java.util.Date}.
     */

    public static synchronized long toJulian( Date d ) {

        _gregorianCalendar.setTime( d );
        int yy = _gregorianCalendar.get( Calendar.YEAR );
        int mm = _gregorianCalendar.get( Calendar.MONDAY ) + 1;
        int dd = _gregorianCalendar.get( Calendar.DAY_OF_MONTH );

        return toJulian( new int[] { yy, mm, dd } );

    }

    /**
     * Converts a Julian day to a calendar date.
     * <p/>
     * ref :
     * Numerical Recipes in C, 2nd ed., Cambridge University Press 1992
     * <p/>
     *
     * @param inJulian the Julian date which is to be converted to a Gregorian calendar date.
     * @return the equivalent Gregorian date as a three element int array
     *         (first element is the year, second is the month (1 origin) and third is the day of month).
     */

    public static int[] fromJulian( double inJulian ) {

        int ja = (int)inJulian;
        if ( ja >= JGREG ) {

            int jAlpha = (int)( ( ( ja - 1867216 ) - 0.25 ) / 36524.25 );
            ja = ja + 1 + jAlpha - jAlpha / 4;

        }

        int jb = ja + 1524;
        int jc = (int)( 6680.0 + ( ( jb - 2439870 ) - 122.1 ) / 365.25 );
        int jd = 365 * jc + jc / 4;
        int je = (int)( ( jb - jd ) / 30.6001 );
        int day = jb - jd - (int)( 30.6001 * je );
        int month = je - 1;
        if ( month > 12 ) {

            month -= 12;

        }
        int year = jc - 4715;
        if ( month > 2 ) {

            year -= 1;

        }
        if ( year <= 0 ) {

            year -= 1;

        }

        return new int[] { year, month, day };
    }

    public static void main( String[] args ) {

        // FIRST TEST reference point
        Logger.logMsg(
                "Julian date for May 23, 1968 : "
                + toJulian( new int[] { 1968, 5, 23 } )
        );
        // output : 2440000
        int[] results = fromJulian( toJulian( new int[] { 1968, 5, 23 } ) );
        Logger.logMsg(
                "... back to calendar : " + results[0] + " "
                + results[1] + " " + results[2]
        );

        // SECOND TEST today
        Calendar today = Calendar.getInstance();
        double todayJulian = toJulian(
                new int[] {
                        today.get( Calendar.YEAR ), today.get( Calendar.MONTH ) + 1,
                        today.get( Calendar.DATE )
                }
        );
        Logger.logMsg( "Julian date for today : " + todayJulian );
        results = fromJulian( todayJulian );
        Logger.logMsg(
                "... back to calendar : " + results[0] + " " + results[1]
                + " " + results[2]
        );

        // THIRD TEST
        double date1 = toJulian( new int[] { 2005, 1, 1 } );
        double date2 = toJulian( new int[] { 2005, 1, 31 } );
        Logger.logMsg(
                "Between 2005-01-01 and 2005-01-31 : "
                + ( date2 - date1 ) + " days"
        );

        /*
           expected output :
              Julian date for May 23, 1968 : 2440000.0
              ... back to calendar 1968 5 23
              Julian date for today : 2453487.0
              ... back to calendar 2005 4 26
              Between 2005-01-01 and 2005-01-31 : 30.0 days
        */
    }

}