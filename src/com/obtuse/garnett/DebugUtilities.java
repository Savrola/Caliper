package com.obtuse.garnett;

/*
 * Copyright © 2012 Daniel Boulet
 */

import com.obtuse.util.ImmutableDate;
import com.sun.net.ssl.internal.ssl.Provider;

import javax.management.timer.Timer;
import java.security.Security;
import java.sql.Date;
import java.util.*;

/**
 * %%% something clever goes here.
 */
@SuppressWarnings("UnusedDeclaration")
public class DebugUtilities {

    private static final Properties _systemProperties = System.getProperties();

    private static final int DEFAULT_PORT = 443;

    private static final int INTELLIJ_PORT = 11223;

    private static final int FIVE_MINUTES = 5 * (int)Timer.ONE_MINUTE;

    private static final int _alfredListenPort;

    private static final int _socketSessionInactivityTimeout;

    private static final boolean _inIntelliJIDEA;

    static {

        if ( checkIntellijIDE() ) {

            _inIntelliJIDEA = true;

            System.out.println( "we're running within the IntelliJ IDEA environment" );

            _alfredListenPort = INTELLIJ_PORT + 3000;

            _socketSessionInactivityTimeout = (int)Timer.ONE_HOUR;

        } else {

            _inIntelliJIDEA = false;

//            _nevilleSlcListenPort = DEFAULT_PORT;
//            _mackenzieSlcListenPort = DEFAULT_PORT;
//            _vanHorneSlcListenPort = DEFAULT_PORT;
            _alfredListenPort = DEFAULT_PORT;

            _socketSessionInactivityTimeout = FIVE_MINUTES;

        }
        Security.addProvider( new Provider() );

    }

    /*
    * Make sure that nobody can instantiate one of these.
    */

    private DebugUtilities() {

        super();

    }

    /**
     * Get the UTC date part from a specified {@link java.sql.Timestamp}.
     *
     * @param specifiedTime the specified timestamp time.
     * @return the UTC date for the specified timestamp time.
     */

    public static Date getUtcDate( Date specifiedTime ) {

        Calendar now = new GregorianCalendar( TimeZone.getTimeZone( "UTC" ) );
        now.setTimeInMillis( specifiedTime.getTime() );
        now.set( Calendar.HOUR, 0 );
        now.set( Calendar.MINUTE, 0 );
        now.set( Calendar.SECOND, 0 );
        now.set( Calendar.MILLISECOND, 0 );

        return new Date( now.getTimeInMillis() );

    }

    /**
     * Return today's date in the UTC timezone as a {@link Date}.
     *
     * @return today's date in the UTC timezone.
     */

    public static ImmutableDate getTodaysDate() {

        return new ImmutableDate( System.currentTimeMillis() );

    }

    private static boolean checkIntellijIDE() {

        String libpath = _systemProperties.getProperty( "java.library.path" );
        //noinspection RedundantIfStatement
        if ( libpath.contains( "IntelliJ IDEA" ) ) {

            return true;

        } else {

            return false;

        }

    }

    public static boolean inIntelliJIDEA() {

        return _inIntelliJIDEA;

    }

    public static String getMackenzieHostname( String deploymentName, int podNumber ) {

        if ( _inIntelliJIDEA ) {

            return "localhost";

        } else {

//            Logger.logErr( "request for Mackenzie hostname for " + deploymentName + " pod " + podNumber, new IllegalArgumentException( "who called this?" ) );
            return "m-" + deploymentName + '-' + podNumber + ".savrola.com";

        }

    }

//    public static String getMackenzieHostname( int podNumber ) {
//
//        return getMackenzieHostname( GenericAutomagicUpgrader.getDeploymentName(), podNumber );
//
//    }
//
//    public static String getMackenzieHostname() {
//
//        return getMackenzieHostname( GenericAutomagicUpgrader.getPodNumber() );
//
//    }

    public static String getAlfredHostname( String deploymentName, int podNumber ) {

        if ( _inIntelliJIDEA ) {

            return "localhost";

        } else {

            return "a-" + deploymentName + "-" + podNumber + ".savrola.com";

        }

    }

    public static String getAlfredHostname( int podNumber ) {

        return getAlfredHostname( GenericAutomagicUpgrader.getDeploymentName(), podNumber );

    }

    public static String getAlfredHostname() {

        return getAlfredHostname( GenericAutomagicUpgrader.getPodNumber() );

    }

    public static String getThumbNailMailHttpdHostname( int podNumber ) {

        if ( _inIntelliJIDEA ) {

            return "localhost";

        } else {

            return "tnm-" + GenericAutomagicUpgrader.getDeploymentName() + '-' + podNumber + ".savrola.com";

        }

    }

    public static boolean testSubscriptionMode() {

        return !GenericAutomagicUpgrader.isDefaultDeployment() || inIntelliJIDEA();

//        return false;

    }

    @SuppressWarnings({ "MagicNumber" })
    public static long getSubscriptionDurationMillis() {

        if ( testSubscriptionMode() ) {

            return Timer.ONE_DAY * ( (long)getSubscriptionDurationInRenewalUnits() + 2L );      // two days grace

        } else {

            return Timer.ONE_DAY * 35L;     // between four and seven days grace

        }

    }

    public static char getSubscriptionDurationCode() {

        if ( testSubscriptionMode() ) {

            return 'D';

        } else {

            return 'M';

        }

    }

    private static long getSubscriptionPlanRenewalTimeMillis(
            long startTimeMillis,
            char durationCode,
            int renewalUnits
    ) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis( startTimeMillis );

        if ( durationCode == 'D' ) {

            cal.add( Calendar.DAY_OF_MONTH, renewalUnits );

        } else if ( durationCode == 'M' ) {

            cal.add( Calendar.MONTH, renewalUnits );

        } else {

            throw new IllegalArgumentException(
                    "invalid duration code '" + durationCode + "' - only monthly and daily duration codes are supported"
            );

        }

        return cal.getTimeInMillis();

    }

    public static long getSubscriptionPlanRenewalTimeMillis( long startTimeMillis ) {

        return getSubscriptionPlanRenewalTimeMillis(
                startTimeMillis,
                getSubscriptionDurationCode(),
                getSubscriptionDurationInRenewalUnits()
        );

    }

    public static int getSubscriptionDurationInRenewalUnits() {

        if ( testSubscriptionMode() ) {

            return 2;

        } else {

            return 1;

        }

    }

    public static String getPayPalSubscriptionPeriodString() {

        return "" + getSubscriptionDurationInRenewalUnits() + ' ' + getSubscriptionDurationCode();

//        if ( testSubscriptionMode() ) {
//
//            return "3 D";
//
//        } else {
//
//            return "1 M";
//        }

    }

    public static int getAlfredListenPort() {

        return _alfredListenPort;
    }

    public static int getSocketSessionInactivityTimeout() {

        return _socketSessionInactivityTimeout;
    }
}
