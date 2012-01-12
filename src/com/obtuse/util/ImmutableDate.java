package com.obtuse.util;

import java.util.Date;

/**
 * A derivation of the {@link Date} class whose instances are immutable.
 * <p/>Attempts to call any of the various setters defined in the {@link Date} class result in an {@link UnsupportedOperationException} being thrown.
 * Also, none of the deprecated constructors in the {@link Date} class exist in this class.
 * <p>
 * Copyright © 2011 Obtuse Systems Corporation
 */

@SuppressWarnings({ "deprecation", "UnusedDeclaration" })
public class ImmutableDate extends Date {

    public ImmutableDate() {
        super();

    }

    public ImmutableDate( Date date ) {
        super( date.getTime() );

    }

    public ImmutableDate( long date ) {
        super( date );

    }

    public void setDate( int date ) {

        throw new UnsupportedOperationException( "instances of ImmutableDate are immutable" );

    }

    public void setHours( int hours ) {

        throw new UnsupportedOperationException( "instances of ImmutableDate are immutable" );

    }

    public void setMinutes( int minutes ) {

        throw new UnsupportedOperationException( "instances of ImmutableDate are immutable" );

    }

    public void setMonth( int month ) {

        throw new UnsupportedOperationException( "instances of ImmutableDate are immutable" );

    }

    public void setSeconds( int seconds ) {

        throw new UnsupportedOperationException( "instances of ImmutableDate are immutable" );

    }

    public void setTime( long time ) {

        throw new UnsupportedOperationException( "instances of ImmutableDate are immutable" );

    }

    public void setYear( int year ) {

        throw new UnsupportedOperationException( "instances of ImmutableDate are immutable" );

    }

}
