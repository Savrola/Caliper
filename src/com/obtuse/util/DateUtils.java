package com.obtuse.util;

import com.obtuse.util.exceptions.ParsingException;

import javax.management.timer.Timer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Various methods for parsing and just generally managing dates.
 * <p/>
 * Copyright © 2009 Invidi Technologies Corporation
 * Copyright © 2012 Daniel Boulet
 */

@SuppressWarnings({ "UnnecessaryLocalVariable", "StaticMethodNamingConvention", "UnusedDeclaration" })
public class DateUtils {

    public static final TimeZone EASTERN_TIME  = TimeZone.getTimeZone( "America/New_York" );
    public static final TimeZone CENTRAL_TIME  = TimeZone.getTimeZone( "America/Chicago" );
    public static final TimeZone MOUNTAIN_TIME = TimeZone.getTimeZone( "America/Denver" );
    public static final TimeZone PACIFIC_TIME  = TimeZone.getTimeZone( "America/Los_Angeles" );
    public static final TimeZone ARIZONA_TIME  = TimeZone.getTimeZone( "America/Arizona" );
    @SuppressWarnings({ "ConstantNamingConvention" })
    public static final TimeZone UTC           = TimeZone.getTimeZone( "UTC" );

    // IMPORTANT:  keep these date formats private as we fiddle with their embedded timezone in methods within this
    // class.

    private static final SimpleDateFormat HHMMSSS                = new SimpleDateFormat( "hh:mm.SSS" );
    private static final SimpleDateFormat MMDDYYYY               = new SimpleDateFormat( "MM/dd/yyyy" );
    private static final SimpleDateFormat MMDDYYYY_HHMM          = new SimpleDateFormat( "MM/dd/yyyy hh:mmaa" );
    private static final SimpleDateFormat YYYYMMDD_HHMM          = new SimpleDateFormat( "yyyy-MM-dd hh:mm" );
    @SuppressWarnings({ "ConstantNamingConvention" })
    private static final SimpleDateFormat HHMM                   = new SimpleDateFormat( "hh:mmaa" );
    private static final SimpleDateFormat MM_DD_YYYY             = new SimpleDateFormat( "MM/dd/yyyy" );
    private static final SimpleDateFormat MM_DD_YYYY_HH_MM       = new SimpleDateFormat( "MM/dd/yyyy hh:mmaa" );
    private static final SimpleDateFormat YYYY_MM_DD_HH_MM       = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
    private static final SimpleDateFormat YYYY_MM_DD_HH_MM_SS    = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
    private static final SimpleDateFormat YYYY_MM_DD_HH_MM_SS_SSS = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS" );
    private static final SimpleDateFormat YYYY_MM_DD_HH_MM_ZZZ   = new SimpleDateFormat( "yyyy-MM-dd HH:mm zzz" );
    private static final SimpleDateFormat HH_MM                  = new SimpleDateFormat( "hh:mmaa" );
    private static final SimpleDateFormat WWW_MMM_DD_HHMMSS_YYYY = new SimpleDateFormat( "EEE MMM dd HH:mm:ss yyyy" );
    private static final SimpleDateFormat YYYY_MM_DD             = new SimpleDateFormat( "yyyy-MM-dd" );
    private static final SimpleDateFormat YYYYMMDD               = new SimpleDateFormat( "yyyyMMdd" );
    private static final SimpleDateFormat YYMMDD                 = new SimpleDateFormat( "yyMMdd" );

    /**
     * Make it impossible to instantiate this class.
     */

    private DateUtils() {

        super();

    }

    /**
     * Parse an MMDDYYYY_HHMM format date and time string in a specified timezone.
     *
     * @param timezone   the specified timezone.
     * @param token      the date and time string.
     * @param lineNumber where the date was found.
     * @return the result in UTC.
     * @throws ParsingException if the token does not contain a valid date and time string.
     */

    public static ImmutableDate parseMMDDYYYY_HHMM( TimeZone timezone, String token, int lineNumber )
            throws ParsingException {

        synchronized ( MMDDYYYY_HHMM ) {

            MMDDYYYY_HHMM.setTimeZone( timezone );
            ImmutableDate date = dateParse( MMDDYYYY_HHMM, token, lineNumber );
            return date;

        }

    }

    /**
     * Parse an MMDDYYYY format date string.
     *
     * @param token      the date string.
     * @param lineNumber where the date was found.
     * @return the result in UTC.
     * @throws ParsingException if the token does not contain a valid date string.
     */

    public static ImmutableDate parseMMDDYYYY( String token, int lineNumber )
            throws ParsingException {

        synchronized ( MMDDYYYY ) {

            MMDDYYYY.setTimeZone( TimeZone.getDefault() );
            ImmutableDate date = dateParse( MMDDYYYY, token, lineNumber );
            return date;

        }

    }

    /**
     * Format a time string in our local time.
     * This method exists primarily for symmetry with the three date and time parsing methods in this class.
     * By hiding the {@link java.text.SimpleDateFormat} objects, this also ensures that nobody 'accidentally'
     * parses a date and time string without properly taking into account the market's timezone.
     *
     * @param dateTime the date and time to be formatted.
     * @return the formatted time string.
     */

    public static String formatHHMM( Date dateTime ) {

        synchronized ( HHMM ) {

            HHMM.setTimeZone( TimeZone.getDefault() );
            String s = HHMM.format( dateTime );
            return s;

        }

    }

//    public static String formatDuration( long duration ) {
//
//        String rval = "";
//
//
//        synchronized ( HHMMSSS ) {
//
//            HHMMSSS.setTimeZone( UTC );
//            String s = HHMMSSS.format( dateTime );
//            return s;
//
//        }
//
//    }

    /**
     * Format a date and time string in our local time.
     * This method exists primarily for symmetry with the three date and time parsing methods in this class.
     * By hiding the {@link java.text.SimpleDateFormat} objects, this also ensures that nobody 'accidentally'
     * parses a date and time string without properly taking into account the market's timezone.
     *
     * @param dateTime the date and time to be formatted.
     * @return the formatted date and time string.
     */

    public static String formatMMDDYYYY_HHMM( Date dateTime ) {

        synchronized ( MMDDYYYY_HHMM ) {

            MMDDYYYY_HHMM.setTimeZone( TimeZone.getDefault() );
            String s = MMDDYYYY_HHMM.format( dateTime );
            return s;

        }

    }

    /**
     * Format a date and time string in our local time.
     * This method exists primarily for symmetry with the three date and time parsing methods in this class.
     * By hiding the {@link java.text.SimpleDateFormat} objects, this also ensures that nobody 'accidentally'
     * parses a date and time string without properly taking into account the market's timezone.
     *
     * @param dateTime the date and time to be formatted.
     * @return the formatted date and time string.
     */

    public static String formatYYYYMMDD_HHMM( Date dateTime ) {

        synchronized ( YYYYMMDD_HHMM ) {

            YYYYMMDD_HHMM.setTimeZone( TimeZone.getDefault() );
            String s = YYYYMMDD_HHMM.format( dateTime );
            return s;

        }

    }

    /**
     * Format a date string in our local time.
     * This method exists primarily for symmetry with the three date and time parsing methods in this class.
     * By hiding the {@link java.text.SimpleDateFormat} objects, this also ensures that nobody 'accidentally'
     * parses a date and time string without properly taking into account the market's timezone.
     *
     * @param dateTime the date to be formatted.
     * @return the formatted date string.
     */

    public static String formatMMDDYYYY( Date dateTime ) {

        synchronized ( MMDDYYYY ) {

            MMDDYYYY.setTimeZone( TimeZone.getDefault() );
            String s = MMDDYYYY.format( dateTime );
            return s;

        }

    }

    /**
     * Parse an "WWW MMM DD YYYY HH:MM:SS" style date string.
     *
     * @param timezone   the date and time's timezone.
     * @param token      the date string.
     * @param lineNumber where the date was found.
     * @return the result in UTC.
     * @throws ParsingException if the token does not contain a valid date string.
     */

    public static ImmutableDate parseLongDateTime( TimeZone timezone, String token, int lineNumber )
            throws ParsingException {

        synchronized ( WWW_MMM_DD_HHMMSS_YYYY ) {

            WWW_MMM_DD_HHMMSS_YYYY.setTimeZone( timezone );
            ImmutableDate date = dateParse( WWW_MMM_DD_HHMMSS_YYYY, token, lineNumber );
            return date;

        }

    }

    private static ImmutableDate dateParse( SimpleDateFormat format, String token, int lineNumber )
            throws ParsingException {

        try {

            return new ImmutableDate( format.parse( token ) );

        } catch ( ParseException e ) {

            throw new ParsingException(
                    e.getMessage(), lineNumber, e.getErrorOffset(), ParsingException.ErrorType.DATE_FORMAT_ERROR
            );

        }

    }

    /**
     * Parse an HHMM format time string in a specified timezone.
     *
     * @param timezone   the specified timezone.
     * @param token      the time string.
     * @param lineNumber where the date was found.
     * @return the result in UTC.
     * @throws ParsingException if the token does not contain a valid time string.
     */

    public static ImmutableDate parseHHMM( TimeZone timezone, String token, int lineNumber )
            throws ParsingException {

        synchronized ( DateUtils.HH_MM ) {

            DateUtils.HH_MM.setTimeZone( timezone );
            ImmutableDate date = dateParse( DateUtils.HH_MM, token, lineNumber );
            return date;

        }

    }

    /**
     * Parse an MMDDYYYY_HHMM format date and time string in a specified timezone.
     *
     * @param timezone   the specified timezone.
     * @param token      the date and time string.
     * @param lineNumber where the date was found.
     * @return the result in UTC.
     * @throws ParsingException if the token does not contain a valid date and time string.
     */

    public static ImmutableDate parseMM_DD_YYYY_HH_MM( TimeZone timezone, String token, int lineNumber )
            throws ParsingException {

        synchronized ( MM_DD_YYYY_HH_MM ) {

            MM_DD_YYYY_HH_MM.setTimeZone( timezone );
            ImmutableDate date = dateParse( MM_DD_YYYY_HH_MM, token, lineNumber );
            return date;

        }

    }

    /**
     * Parse an "MM/dd/yyyy" format date string.
     *
     * @param token      the date string.
     * @param lineNumber where the date was found.
     * @return the result in UTC.
     * @throws ParsingException if the token does not contain a valid date string.
     */

    public static ImmutableDate parseMM_DD_YYYY( String token, int lineNumber )
            throws ParsingException {

        synchronized ( MM_DD_YYYY ) {

            MM_DD_YYYY.setTimeZone( TimeZone.getDefault() );
            ImmutableDate date = dateParse( MM_DD_YYYY, token, lineNumber );
            return date;

        }

    }

    /**
     * Parse a "yyyy-MM-dd" format date string.
     *
     * @param token      the date string.
     * @param lineNumber where the date was found.
     * @return the result in UTC.
     * @throws ParsingException if the token does not contain a valid date string.
     */

    public static ImmutableDate parseYYYY_MM_DD( String token, int lineNumber )
            throws ParsingException {

        synchronized ( YYYY_MM_DD ) {

            YYYY_MM_DD.setTimeZone( TimeZone.getDefault() );
            ImmutableDate date = dateParse( YYYY_MM_DD, token, lineNumber );
            return date;

        }

    }

    /**
     * Parse a "yyyyMMdd" format date string.
     *
     * @param token      the date string.
     * @param lineNumber where the date was found.
     * @return the result in UTC.
     * @throws ParsingException if the token does not contain a valid date string.
     */

    public static ImmutableDate parseYYYYMMDD( String token, int lineNumber )
            throws ParsingException {

        synchronized ( YYYYMMDD ) {

            YYYYMMDD.setTimeZone( TimeZone.getDefault() );
            ImmutableDate date = dateParse( YYYYMMDD, token, lineNumber );
            return date;

        }

    }

    /**
     * Format a time string in our local time.
     * This method exists primarily for symmetry with the three date and time parsing methods in this class.
     * By hiding the {@link java.text.SimpleDateFormat} objects, this also ensures that nobody 'accidentally'
     * parses a date and time string without properly taking into account the market's timezone.
     *
     * @param dateTime the date and time to be formatted.
     * @return the formatted time string.
     */

    public static String formatHH_MM( Date dateTime ) {

        synchronized ( HH_MM ) {

            HH_MM.setTimeZone( TimeZone.getDefault() );
            String s = HH_MM.format( dateTime );
            return s;

        }

    }

//    public static String formatDuration( long duration ) {
//
//        String rval = "";
//
//
//        synchronized ( HHMMSSS ) {
//
//            HHMMSSS.setTimeZone( UTC );
//            String s = HHMMSSS.format( dateTime );
//            return s;
//
//        }
//
//    }

    /**
     * Format a date and time string in our local time.
     * This method exists primarily for symmetry with the three date and time parsing methods in this class.
     * By hiding the {@link java.text.SimpleDateFormat} objects, this also ensures that nobody 'accidentally'
     * parses a date and time string without properly taking into account the market's timezone.
     *
     * @param dateTime the date and time to be formatted.
     * @return the formatted date and time string.
     */

    public static String formatMM_DD_YYYY_HH_MM( Date dateTime ) {

        synchronized ( MM_DD_YYYY_HH_MM ) {

            MM_DD_YYYY_HH_MM.setTimeZone( TimeZone.getDefault() );
            String s = MM_DD_YYYY_HH_MM.format( dateTime );
            return s;

        }

    }

    /**
     * Format a date and time string in our local time.
     * This method exists primarily for symmetry with the three date and time parsing methods in this class.
     * By hiding the {@link java.text.SimpleDateFormat} objects, this also ensures that nobody 'accidentally'
     * parses a date and time string without properly taking into account the market's timezone.
     *
     * @param dateTime the date and time to be formatted.
     * @return the formatted date and time string.
     */

    public static String formatYYYY_MM_DD_HH_MM( Date dateTime ) {

        return formatYYYY_MM_DD_HH_MM( dateTime, TimeZone.getDefault() );

    }

    /**
     * Format a date and time string in a specified timezone.
     * This method exists primarily for symmetry with the three date and time parsing methods in this class.
     * By hiding the {@link java.text.SimpleDateFormat} objects, this also ensures that nobody 'accidentally'
     * parses a date and time string without properly taking into account the market's timezone.
     *
     * @param dateTime the date and time to be formatted.
     * @param timeZone the timezone for which the date is to be formatted.
     * @return the formatted date and time string.
     */

    public static String formatYYYY_MM_DD_HH_MM( Date dateTime, TimeZone timeZone ) {

        synchronized ( YYYY_MM_DD_HH_MM ) {

            YYYY_MM_DD_HH_MM.setTimeZone( timeZone );
            String s = YYYY_MM_DD_HH_MM.format( dateTime );
            return s;

        }

    }

    /**
     * Format a date and time string in our local time.
     * This method exists primarily for symmetry with the three date and time parsing methods in this class.
     * By hiding the {@link java.text.SimpleDateFormat} objects, this also ensures that nobody 'accidentally'
     * parses a date and time string without properly taking into account the market's timezone.
     *
     * @param dateTime the date and time to be formatted.
     * @return the formatted date and time string.
     */

    public static String formatYYYY_MM_DD_HH_MM_SS( Date dateTime ) {

        return formatYYYY_MM_DD_HH_MM_SS( dateTime, TimeZone.getDefault() );

    }

    /**
     * Format a date and time string in a specified timezone.
     * This method exists primarily for symmetry with the three date and time parsing methods in this class.
     * By hiding the {@link java.text.SimpleDateFormat} objects, this also ensures that nobody 'accidentally'
     * parses a date and time string without properly taking into account the market's timezone.
     *
     * @param dateTime the date and time to be formatted.
     * @param timeZone the timezone for which the date is to be formatted.
     * @return the formatted date and time string.
     */

    public static String formatYYYY_MM_DD_HH_MM_SS( Date dateTime, TimeZone timeZone ) {

        synchronized ( YYYY_MM_DD_HH_MM_SS ) {

            YYYY_MM_DD_HH_MM_SS.setTimeZone( timeZone );
            String s = YYYY_MM_DD_HH_MM_SS.format( dateTime );
            return s;

        }

    }

    /**
     * Format a date and time string in our local time.
     * This method exists primarily for symmetry with the three date and time parsing methods in this class.
     * By hiding the {@link java.text.SimpleDateFormat} objects, this also ensures that nobody 'accidentally'
     * parses a date and time string without properly taking into account the market's timezone.
     *
     * @param dateTime the date and time to be formatted.
     * @return the formatted date and time string.
     */

    public static String formatYYYY_MM_DD_HH_MM_SS_SSS( Date dateTime ) {

        return formatYYYY_MM_DD_HH_MM_SS_SSS( dateTime, TimeZone.getDefault() );

    }

    /**
     * Format a date and time string in a specified timezone.
     * This method exists primarily for symmetry with the three date and time parsing methods in this class.
     * By hiding the {@link java.text.SimpleDateFormat} objects, this also ensures that nobody 'accidentally'
     * parses a date and time string without properly taking into account the market's timezone.
     *
     * @param dateTime the date and time to be formatted.
     * @param timeZone the timezone for which the date is to be formatted.
     * @return the formatted date and time string.
     */

    public static String formatYYYY_MM_DD_HH_MM_SS_SSS( Date dateTime, TimeZone timeZone ) {

        synchronized ( YYYY_MM_DD_HH_MM_SS_SSS ) {

            YYYY_MM_DD_HH_MM_SS_SSS.setTimeZone( timeZone );
            String s = YYYY_MM_DD_HH_MM_SS_SSS.format( dateTime );
            return s;

        }

    }

    /**
     * Format a date and time string in a specified timezone with the timezone shown.
     *
     * @param dateTime the date and time to be formatted.
     * @param timeZone the date and time's timezone.
     * @return the formatted date and time string.
     */

    public static String formatYYYY_MM_DD_HH_MM_ZZZ( Date dateTime, TimeZone timeZone ) {

        synchronized ( YYYY_MM_DD_HH_MM_ZZZ ) {

            YYYY_MM_DD_HH_MM_ZZZ.setTimeZone( timeZone );
            String s = YYYY_MM_DD_HH_MM_ZZZ.format( dateTime );
            return s;

        }

    }

    /**
     * Format a date string in our local time.
     * This method exists primarily for symmetry with the three date and time parsing methods in this class.
     * By hiding the {@link java.text.SimpleDateFormat} objects, this also ensures that nobody 'accidentally'
     * parses a date and time string without properly taking into account the market's timezone.
     *
     * @param dateTime the date to be formatted.
     * @return the formatted date string.
     */

    public static String formatMM_DD_YYYY( Date dateTime ) {

        synchronized ( MM_DD_YYYY ) {

            MM_DD_YYYY.setTimeZone( TimeZone.getDefault() );
            String s = MM_DD_YYYY.format( dateTime );
            return s;

        }

    }

    /**
     * Format a date string in our local time.
     * This method exists primarily for symmetry with the three date and time parsing methods in this class.
     * By hiding the {@link java.text.SimpleDateFormat} objects, this also ensures that nobody 'accidentally'
     * parses a date and time string without properly taking into account the market's timezone.
     *
     * @param dateTime the date to be formatted.
     * @return the formatted date string.
     */

    public static String formatYYYY_MM_DD( Date dateTime ) {

        synchronized ( YYYY_MM_DD ) {

            YYYY_MM_DD.setTimeZone( TimeZone.getDefault() );
            String s = YYYY_MM_DD.format( dateTime );
            return s;

        }

    }

    /**
     * Format a date string in our local time.
     * This method exists primarily for symmetry with the three date and time parsing methods in this class.
     * By hiding the {@link java.text.SimpleDateFormat} objects, this also ensures that nobody 'accidentally'
     * parses a date and time string without properly taking into account the market's timezone.
     *
     * @param dateTime the date to be formatted.
     * @return the formatted date string.
     */

    public static String formatYYMMDD( Date dateTime ) {

        synchronized ( YYMMDD ) {

            YYMMDD.setTimeZone( TimeZone.getDefault() );
            String s = YYMMDD.format( dateTime );
            return s;

        }

    }

    public static String formatDuration( long millis, boolean shortForm ) {

        return formatDuration( millis, 3, shortForm );
    }

    public static String formatDuration( long millis ) {

        return formatDuration( millis, 3 );

    }

    public static String formatDuration( long millis, int digits ) {

        return formatDuration( millis, digits, false );

    }

    @SuppressWarnings({ "UnnecessaryParentheses", "NestedConditionalExpression" })
    public static String formatDuration( long xmillis, int digits, boolean shortForm ) {

        long millis = xmillis;
        String rval = "";
        String spacing = "";

        if ( millis >= Timer.ONE_WEEK ) {

            long weeks = millis / Timer.ONE_WEEK;
            rval += spacing + weeks + ( shortForm ? "w" : ( " week" + ( weeks == 1L ? "" : "s" ) ) );
            millis %= Timer.ONE_WEEK;
            spacing = " ";

        }

        if ( millis >= Timer.ONE_DAY ) {

            long days = millis / Timer.ONE_DAY;
            rval += spacing + days + ( shortForm ? "d" : ( " day" + ( days == 1L ? "" : "s" ) ) );
            millis %= Timer.ONE_DAY;
            spacing = " ";

        }

        if ( millis >= Timer.ONE_HOUR ) {

            long hours = millis / Timer.ONE_HOUR;
            rval += spacing + hours + ( shortForm ? "h" : ( " hour" + ( hours == 1L ? "" : "s" ) ) );
            millis %= Timer.ONE_HOUR;
            spacing = " ";

        }

        if ( millis >= Timer.ONE_MINUTE ) {

            long minutes = millis / Timer.ONE_MINUTE;
            rval += spacing + minutes + ( shortForm ? "m" : ( " minute" + ( minutes == 1L ? "" : "s" ) ) );
            millis %= Timer.ONE_MINUTE;
            spacing = " ";

        }

        if ( millis > 0L ) {

            long seconds = millis / Timer.ONE_SECOND;
            if ( shortForm && millis % Timer.ONE_SECOND == 0L ) {

                rval += spacing + seconds + "s";

            } else {

                rval += spacing + ObtuseUtil5.lpad( millis / (double)Timer.ONE_SECOND, 0, digits ) +
                        ( shortForm ? "s " : ( " second" + ( seconds == 1L ? "" : "s" ) ) );

            }
            millis %= Timer.ONE_SECOND;

        }

        return rval.trim();

    }

    /**
     * Extract the day of the week from a {@link Date} instance.
     *
     * @param date the date instance from which the day of week is to be extracted.
     * @return the day of the week from the specified {@link Date} instance.
     *         Sunday is represented by 1, Monday by 2, through to Saturday by 7.
     *         These correspond to the values returned by {@link java.util.Calendar#get(int)}.
     *         The constants {@link java.util.Calendar#SUNDAY}, {@link java.util.Calendar#MONDAY} ... may prove useful.
     */

    public static int extractDayOfWeek( Date date ) {

        Calendar cal = Calendar.getInstance();
        cal.setTime( date );

        return cal.get( Calendar.DAY_OF_WEEK );

    }

    public static Date addDays( Date date, int delta ) {

        Calendar cal = Calendar.getInstance();
        cal.setTime( date );
        cal.add( Calendar.DAY_OF_MONTH, delta );
        return cal.getTime();

    }


}
