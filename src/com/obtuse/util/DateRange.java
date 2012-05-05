package com.obtuse.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A single date range from a starting date through to an ending date inclusive.
 * <p/>
 * Intended for use with the {@link Range} and {@link Ranges} classes.
 * <p/>
 * Copyright © 2009 Obtuse Systems Corporation.
 */

@SuppressWarnings("UnusedDeclaration")
public class DateRange extends Range<Date> {

    private static final SimpleDateFormat YYYYMMDD = new SimpleDateFormat( "yyyy-MM-dd" );

    public DateRange( Date startDate, Date endDate ) {
        super( startDate, endDate, JulianDate.toJulian( startDate ), JulianDate.toJulian( endDate ) );

    }

    public int hashCode() {

        return getStartValue().hashCode() ^ getEndValue().hashCode();

    }

    public boolean equals( Object rhs ) {

        //noinspection OverlyStrongTypeCast
        return rhs instanceof DateRange &&
               ( (DateRange)rhs ).getStartValue().equals( getStartValue() ) &&
               ( (DateRange)rhs ).getEndValue().equals( getEndValue() );

    }

    @SuppressWarnings( { "RefusedBequest" } )
    public String format( Date value ) {

        return DateRange.YYYYMMDD.format( value );

    }

}
