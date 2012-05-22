package com.obtuse.util;

import java.util.Date;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * An immutable date which caches certain formatted versions.
 */

@SuppressWarnings({ "InstanceVariableNamingConvention", "InstanceMethodNamingConvention", "UnusedDeclaration" })
public class FormattedImmutableDate extends ImmutableDate {

    private String _yyyy_mm_dd = null;
    private String _yyyy_mm_dd_hh_mm_ss = null;
    private String _yyyy_mm_dd_hh_mm = null;

    public FormattedImmutableDate() {
        super();

    }

    public FormattedImmutableDate( Date date ) {
        super( date );

    }

    public FormattedImmutableDate( long timeMs ) {
        super( timeMs );

    }

    public String getYYYY_MM_DD() {

        if ( _yyyy_mm_dd == null ) {

            _yyyy_mm_dd = DateUtils.formatYYYY_MM_DD( this );

        }

        return _yyyy_mm_dd;

    }

    public String getYYYY_MM_DD_HH_MM_SS() {

        if ( _yyyy_mm_dd_hh_mm_ss == null ) {

            _yyyy_mm_dd_hh_mm_ss = DateUtils.formatYYYY_MM_DD_HH_MM_SS( this );

        }

        return _yyyy_mm_dd_hh_mm_ss;

    }

    public String getYYYY_MM_DD_HH_MM() {

            if ( _yyyy_mm_dd_hh_mm == null ) {

                _yyyy_mm_dd_hh_mm = DateUtils.formatYYYY_MM_DD_HH_MM( this );

            }

            return _yyyy_mm_dd_hh_mm;

        }

}
