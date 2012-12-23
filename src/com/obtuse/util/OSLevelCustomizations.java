package com.obtuse.util;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/*
 * Copyright © 2012 Daniel Boulet.
 * Copyright © 2012 Daniel Boulet.
 */

/**
 * Describe something which does OS-specific customizations.
 */

@SuppressWarnings({ "UnusedDeclaration" })
public abstract class OSLevelCustomizations {

    private static boolean _gotOSLevelCustomizations = false;
    private static OSLevelCustomizations _osLevelCustomizations;

    public abstract void setPreferencesHandler( PreferencesHandler prefsHandler );

    public static boolean onMacOsX() {

        String lcOSName = System.getProperty( "os.name" ).toLowerCase();
        @SuppressWarnings("UnnecessaryLocalVariable")
        boolean onMacOSX = lcOSName.startsWith( "mac os x" );

        return onMacOSX;

    }

    public static boolean onWindows() {

        String lcOSName = System.getProperty( "os.name" ).toLowerCase();
        @SuppressWarnings("UnnecessaryLocalVariable")
        boolean onWindows = lcOSName.startsWith( "windows" );

        return onWindows;
    }

    public abstract void setDockBadge( String msg );

    public abstract void setDockIconImage( Image icon );

    public static OSLevelCustomizations getCustomizer( AboutWindowHandler aboutWindowHandler ) {

        if ( !OSLevelCustomizations._gotOSLevelCustomizations ) {

            if ( OSLevelCustomizations.onMacOsX() ) {

    //            Logger.logMsg( "we're on a mac!" );
                String methodName = null;

                try {

                    //noinspection RawUseOfParameterizedType
                    Class macSpecificCode =
                            OSLevelCustomizations.class.getClassLoader().loadClass( "com.obtuse.util.MacCustomization" );
                    methodName = "createInstance";
                    //noinspection RedundantArrayCreation
                    Method createInstance =
                            macSpecificCode.getDeclaredMethod( methodName, new Class[] { AboutWindowHandler.class, QuitCatcher.class } );
                    createInstance.setAccessible( true );
                    //noinspection RedundantArrayCreation
                    OSLevelCustomizations._osLevelCustomizations = (OSLevelCustomizations)createInstance.invoke( null, new Object[] { aboutWindowHandler, null } );

                } catch ( ClassNotFoundException e ) {

                    Logger.logErr( "unable to find MacCustomization class - assuming customizations are not available" );

                } catch ( NoSuchMethodException e ) {

                    Logger.logErr( "unable to find " + methodName + " method in MacCustomization class - assuming customizations are not available" );

                } catch ( IllegalAccessException e ) {

                    Logger.logErr( "unable to invoke " + methodName + " method in MacCustomization class - assuming customizations are not available" );

                } catch ( InvocationTargetException e ) {

                    Logger.logErr(
                            "caught an exception while invoking " + methodName + " method in MacCustomization class - assuming customizations are not available"
                    );

                }

            }

            OSLevelCustomizations._gotOSLevelCustomizations = true;

        }

        return OSLevelCustomizations._osLevelCustomizations;

    }

}
