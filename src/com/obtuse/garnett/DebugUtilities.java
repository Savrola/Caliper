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

    private static final Properties s_systemProperties = System.getProperties();

    private static final int DEFAULT_PORT = 443;

    private static final int INTELLIJ_PORT = 11223;

    private static final int FIVE_MINUTES = 5 * (int)Timer.ONE_MINUTE;

    private static final int ALFRED_LISTEN_PORT;

    private static final int SOCKET_SESSION_INACTIVITY_TIMEOUT;

    private static final boolean IN_INTELLIJ_IDEA;

    static {

        if ( DebugUtilities.checkIntellijIDE() ) {

            IN_INTELLIJ_IDEA = true;

            //noinspection UseOfSystemOutOrSystemErr
            System.out.println( "we're running within the IntelliJ IDEA environment" );

            ALFRED_LISTEN_PORT = DebugUtilities.INTELLIJ_PORT + 3000;

            SOCKET_SESSION_INACTIVITY_TIMEOUT = (int)Timer.ONE_HOUR;

        } else {

            IN_INTELLIJ_IDEA = false;

//            _nevilleSlcListenPort = DEFAULT_PORT;
//            _mackenzieSlcListenPort = DEFAULT_PORT;
//            _vanHorneSlcListenPort = DEFAULT_PORT;
            ALFRED_LISTEN_PORT = DebugUtilities.DEFAULT_PORT;

            SOCKET_SESSION_INACTIVITY_TIMEOUT = DebugUtilities.FIVE_MINUTES;

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

        String libpath = DebugUtilities.s_systemProperties.getProperty( "java.library.path" );
        //noinspection RedundantIfStatement
        if ( libpath.contains( "IntelliJ IDEA" ) ) {

            return true;

        } else {

            return false;

        }

    }

    public static boolean inIntelliJIDEA() {

        return DebugUtilities.IN_INTELLIJ_IDEA;

    }

    public static String getMackenzieHostname( String deploymentName, int podNumber ) {

        if ( DebugUtilities.IN_INTELLIJ_IDEA ) {

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

        if ( DebugUtilities.IN_INTELLIJ_IDEA ) {

            return "localhost";

        } else {

            return "a-" + deploymentName + "-" + podNumber + ".savrola.com";

        }

    }

    public static String getAlfredHostname( int podNumber ) {

        return DebugUtilities.getAlfredHostname( GenericAutomagicUpgrader.getDeploymentName(), podNumber );

    }

    public static String getAlfredHostname() {

        return DebugUtilities.getAlfredHostname( GenericAutomagicUpgrader.getPodNumber() );

    }

    public static String getThumbNailMailHttpdHostname( int podNumber ) {

        if ( DebugUtilities.IN_INTELLIJ_IDEA ) {

            return "localhost";

        } else {

            return "tnm-" + GenericAutomagicUpgrader.getDeploymentName() + '-' + podNumber + ".savrola.com";

        }

    }

    public static boolean testSubscriptionMode() {

        return !GenericAutomagicUpgrader.isDefaultDeployment() || DebugUtilities.inIntelliJIDEA();

//        return false;

    }

    @SuppressWarnings({ "MagicNumber" })
    public static long getSubscriptionDurationMillis() {

        if ( DebugUtilities.testSubscriptionMode() ) {

            return Timer.ONE_DAY * ( (long)DebugUtilities.getSubscriptionDurationInRenewalUnits() + 2L );      // two days grace

        } else {

            return Timer.ONE_DAY * 35L;     // between four and seven days grace

        }

    }

    public static char getSubscriptionDurationCode() {

        if ( DebugUtilities.testSubscriptionMode() ) {

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

        return DebugUtilities.getSubscriptionPlanRenewalTimeMillis(
                startTimeMillis,
                DebugUtilities.getSubscriptionDurationCode(),
                DebugUtilities.getSubscriptionDurationInRenewalUnits()
        );

    }

    public static int getSubscriptionDurationInRenewalUnits() {

        if ( DebugUtilities.testSubscriptionMode() ) {

            return 2;

        } else {

            return 1;

        }

    }

    public static String getPayPalSubscriptionPeriodString() {

        return "" + DebugUtilities.getSubscriptionDurationInRenewalUnits() + ' ' +
               DebugUtilities.getSubscriptionDurationCode();

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

        return DebugUtilities.ALFRED_LISTEN_PORT;

    }

    public static int getSocketSessionInactivityTimeout() {

        return DebugUtilities.SOCKET_SESSION_INACTIVITY_TIMEOUT;

    }

}
