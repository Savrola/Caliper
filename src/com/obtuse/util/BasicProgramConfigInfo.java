package com.obtuse.util;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.text.DateFormat;
import java.util.prefs.Preferences;

/**
 * Fundamental program configuration information and such.
 * <p/>
 * Copyright © 2012 Daniel Boulet.
 */

@SuppressWarnings( { "ClassNamingConvention" } )
public class BasicProgramConfigInfo {

    private static boolean _initialized = false;

    private static File _workingDirectory = null;

    private static String _vendorName;

    private static String _applicationName;

    private static String _componentName;

    private static String _logFileNameFormat;

    private static Preferences _preferences;

    private static DateFormat _dateFormat;

    /**
     * Initialize this program's basic configuration info.
     * <p/>
     * Note that the three names passed to this method are used to determine file and/or directory names.
     * Consequently, they may only contain characters allowed in file and directory names across whatever range
     * of operating systems the calling application component is likely to run on.
     * It is probably safest to restrict yourself to letters, digits, spaces, underscores and hyphens.
     *  Using non-printable ASCII characters or either forward or backward slashes would be a VERY bad idea.
     * <p/>
     * This method MUST be called before using the {@link Logger} or the {@link Trace} facilities.
     * @param vendorName the program's vendor's name.
     * @param applicationName the application's name.
     * @param componentName this component's name (within the larger application).
     * @param preferences this application's preferences object (may be null if application has no use for preferences).
     * This value may be <tt>null</tt> in which case the application name will generally be used.
     */

    public static void init( String vendorName, String applicationName, String componentName, @Nullable Preferences preferences ) {

        _vendorName = vendorName;
        _applicationName = applicationName;
        _componentName = componentName;
        _preferences = preferences;

        if ( _initialized ) {

            throw new IllegalArgumentException( "BasicProgramConfigInfo already initialized" );

        }

        String home = System.getProperty( "user.home" );
        if ( home != null ) {

            // The Mac OS has a convention as to where these sorts of things go.
            // Follow the convention if we are running on the Mac OS.

            if ( OSLevelCustomizations.onMacOsX() ) {

                File dirLocation = new File( new File( home, "Library" ), "Application Support" );
                if ( vendorName == null ) {

                    _workingDirectory = new File( dirLocation, applicationName );

                } else {

                    _workingDirectory = new File( new File( dirLocation, vendorName ), applicationName );

                }

            } else {

                _workingDirectory = new File( new File( home ), "." + applicationName );

            }

            _workingDirectory.mkdirs();

        } else {

            _workingDirectory = null;

        }

        _initialized = true;

    }

    public static boolean isInitialized() {

        return _initialized;

    }

    private BasicProgramConfigInfo() {
        super();

    }

    public static File getWorkingDirectory() {

        if ( !_initialized ) {

            throw new IllegalArgumentException( "BasicProgramConfigInfo not yet initialized" );

        }

        return _workingDirectory;

    }

    public static String getVendorName() {

        return _vendorName;

    }

    public static String getApplicationName() {

        return _applicationName;

    }

    public static String getComponentName() {

        return _componentName;

    }

    public static Preferences getPreferences() {

        return _preferences;

    }

    public static String getLogFileNameFormat() {

        return _logFileNameFormat;

    }

    public static DateFormat getDateFormat() {

        return _dateFormat;

    }

    public static void setLogFileNameFormat( String logFileNameFormat ) {

        _logFileNameFormat = logFileNameFormat;

    }

    public static void setDateFormat( DateFormat dateFormat ) {

        _dateFormat = dateFormat;

    }

}
