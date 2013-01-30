package com.obtuse.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * Record window location changes in the users preferences object.
 * <p/>
 * Copyright © 2007, 2008, 2011 Daniel Boulet.
 */

public class TrackedWindow extends JFrame {

    private final String _appName;

    private final String _windowName;

    private final String _windowGeometryPrefsKey;

    private boolean _inToString = false;

    public TrackedWindow( String appName, String windowName ) {
        super();

        _appName = appName;
        _windowName = windowName;
        _windowGeometryPrefsKey = _windowName + ".geometry";

        //noinspection ClassWithoutToString
        addComponentListener(
                new ComponentListener() {

                    public void componentResized( ComponentEvent componentEvent ) {

                        saveWindowGeometry();

                    }

                    public void componentMoved( ComponentEvent componentEvent ) {

                        saveWindowGeometry();

                    }

                    public void componentShown( ComponentEvent componentEvent ) {

                    }

                    public void componentHidden( ComponentEvent componentEvent ) {

                    }

                }
        );

//        restoreWindowGeometry();

    }

    @SuppressWarnings({ "BooleanMethodNameMustStartWithQuestion" })
    public void saveWindowGeometry() {

        Rectangle windowGeometry = getBounds();
        saveWindowGeometry( windowGeometry );

    }

    private void saveWindowGeometry( Rectangle windowGeometry ) {

//        Logger.logMsg( _windowGeometryPrefsKey + ":  saving window geometry \"" + windowGeometry + "\"" );
        if ( BasicProgramConfigInfo.getPreferences() != null ) {

            BasicProgramConfigInfo.getPreferences().putByteArray(
                    _windowGeometryPrefsKey,
                    ObtuseUtil.getSerializedVersion( windowGeometry, false )
            );

        }

    }

    @SuppressWarnings({ "UnusedDeclaration" })
    protected void restoreWindowLocation() {

        Rectangle windowGeometry = getSavedGeometry();
        Point windowLocation = new Point( windowGeometry.x, windowGeometry.y );
//        Logger.logMsg( _windowGeometryPrefsKey + ":  restoring window location \"" + windowLocation + "\"" );
        setLocation( windowLocation );

    }

    @SuppressWarnings({ "UnusedDeclaration" })
    protected void restoreWindowGeometry( int width, int height ) {

        Rectangle windowGeometry = getSavedGeometry();
        if ( windowGeometry.width < width ) {

            windowGeometry.width = width;

        }
        if ( windowGeometry.height < height ) {

            windowGeometry.height = height;

        }

        setBounds( windowGeometry );
        setPreferredSize( new Dimension( windowGeometry.width, windowGeometry.height ) );

    }

    public Rectangle getSavedGeometry() {

        byte[] savedLocationBytes = BasicProgramConfigInfo.getPreferences() == null ? null : BasicProgramConfigInfo.getPreferences().getByteArray(
                _windowGeometryPrefsKey,
                null
        );
        Rectangle savedGeometry;
        if ( savedLocationBytes == null ) {

            savedGeometry = getBounds();
            saveWindowGeometry( savedGeometry );

        } else {

            // De-serialize the saved window geometry.
            // If de-serialization fails then save the current window geometry as the saved window geometry.

            savedGeometry = (Rectangle) ObtuseUtil.recoverSerializedVersion( savedLocationBytes, false );
            if ( savedGeometry == null ) {

                savedGeometry = getBounds();
                saveWindowGeometry( savedGeometry );

            }

        }

        if ( !_inToString ) {

//            Logger.logMsg( _windowGeometryPrefsKey + ":  fetching window geometry " + savedGeometry + "\"" );

        }

        return savedGeometry;

    }

    @SuppressWarnings({ "UnusedDeclaration" })
    public String getTrackedWindowName() {

        return _windowName;

    }

    @SuppressWarnings({ "UnusedDeclaration" })
    public String getTrackedWindowLocationPrefsKey() {

        return _windowGeometryPrefsKey;

    }

    @SuppressWarnings( { "RefusedBequest" } )
    public String toString() {

        _inToString = true;
        String rval = "TrackedWindow( " + _appName + ", " + _windowName + ", " + getSavedGeometry() + " )";
        _inToString = false;
        return rval;

    }

}
