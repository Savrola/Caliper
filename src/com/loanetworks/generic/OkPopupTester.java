package com.loanetworks.generic;

import com.obtuse.util.Logger;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Test the OkPopupMessageWindow
 */

@SuppressWarnings({ "UtilityClassWithoutPrivateConstructor" })
public class OkPopupTester {
    public static void main( String[] args ) {

        System.setProperty( "apple.laf.useScreenMenuBar", "true" );
        System.setProperty( "com.apple.mrj.application.growbox.intrudes", "false" );
        System.setProperty( "apple.awt.window.position.forceSafeCreation", "true" );
        System.setProperty( "apple.awt.window.position.forceSafeProgrammaticPositioning", "true" );
        System.setProperty( "apple.awt.window.position.forceSafeUserPositioning", "true" );
        System.setProperty( "com.apple.mrj.application.apple.menu.about.name", "Loa PowerTools" );

        // Get the right look-and-feel before we load any Swing classes.

        String laf;
        String lcOSName = System.getProperty( "os.name" ).toLowerCase();
        boolean onMacOSX = lcOSName.startsWith( "mac os x" );
        if ( onMacOSX ) {
            laf = "apple.laf.AquaLookAndFeel";
        } else {
            laf = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
        }

        try {

            UIManager.setLookAndFeel( laf );

        } catch ( Throwable e ) {

            Logger.logErr( "unable to find " + laf + " look and feel - using system default", e );

        }

        // Call LoaPostMain via reflection (to avoid loading it before we've got our look-and-feel set).

        try {

            ClassLoader classLoader = OkPopupTester.class.getClassLoader();

            //noinspection RawUseOfParameterizedType
            Class popupClass =
                    classLoader.loadClass( "com.loanetworks.generic.OkPopupMessageWindow" );

            //noinspection RedundantArrayCreation
            Method testitMethod = popupClass.getMethod( "testIt", new Class[] { } );
            //noinspection UnusedDeclaration,RedundantArrayCreation
            Object instance = testitMethod.invoke( null, new Object[] { } );

            Logger.logMsg( "launch thread finished" );

        } catch ( ClassNotFoundException e ) {

            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            System.exit( 1 );

        } catch ( NoSuchMethodException e ) {

            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            System.exit( 1 );

        } catch ( InvocationTargetException e ) {

            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            System.exit( 1 );

        } catch ( IllegalAccessException e ) {

            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            System.exit( 1 );

        }

    }

}
