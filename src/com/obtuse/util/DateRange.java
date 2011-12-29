package com.obtuse.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A single date range from a starting date through to an ending date inclusive.
 * <p/>
 * Copyright © 2009 Obtuse Systems Corporation.
 */

public class DateRange extends Range<Date> {

    private static final SimpleDateFormat YYYYMMDD = new SimpleDateFormat( "yyyy-MM-dd" );

    public DateRange( Date startDate, Date endDate ) {
        super( startDate, endDate, JulianDate.toJulian( startDate ), JulianDate.toJulian( endDate ) );

    }

    public int hashCode() {

        return getStartValue().hashCode() ^ getEndValue().hashCode();

    }

    public boolean equals( Object rhs ) {

        return rhs instanceof DateRange && ((DateRange)rhs).getStartValue().equals( getStartValue() ) && ((DateRange)rhs).getEndValue().equals( getEndValue() );

    }

    @SuppressWarnings( { "RefusedBequest" } )
    public String format( Date value ) {

        return YYYYMMDD.format( value );

    }

}
