package com.obtuse.util;

import com.obtuse.ui.OkPopupMessageWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Manage the menus for windows which need to have our 'standard' menus active.
 * <p/>
 * Copyright © 2006, 2007, 2011 Daniel Boulet.
 */

@SuppressWarnings( { "FieldCanBeLocal" } )
public abstract class WindowWithMenus extends TrackedWindow implements PreferencesHandler {

    private JMenuBar _menuBar;

    private JMenu _fileMenu;

    private JMenuItem _preferencesMenuItem;

    private JCheckBoxMenuItem _showLogsMenuItem;

    private static Collection<WindowWithMenus> s_allWindowsWithLogsMenuItem = new LinkedList<WindowWithMenus>();
    private static boolean s_showLogsMode;

    @SuppressWarnings( { "ClassWithoutToString" } )
    protected WindowWithMenus( String appName, String windowName, boolean includeLogsMenuItem ) {
        super( appName, windowName );

        _menuBar = new JMenuBar();

        _fileMenu = new JMenu( "File" );

        _preferencesMenuItem = new JMenuItem( "Preferences" );
        OSLevelCustomizations osLevelCustomizations = OSLevelCustomizations.getCustomizer(
                new AboutWindowHandler() {

                    public void makeVisible() {

                        Logger.logMsg( "about window launch request ignored" );

                    }

                }
        );

        if ( osLevelCustomizations == null ) {

            _fileMenu.add( _preferencesMenuItem );

        } else {

            osLevelCustomizations.setPreferencesHandler( this );

        }

        _preferencesMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_COMMA, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                )
        );

        _preferencesMenuItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed( ActionEvent actionEvent ) {
                        handlePreferences();
                    }
                }
        );

        if ( includeLogsMenuItem ) {

            _showLogsMenuItem = new JCheckBoxMenuItem( "Show Log Messages" );

            _showLogsMenuItem.addActionListener(
                    new ActionListener() {
                        public void actionPerformed( ActionEvent actionEvent ) {

                            if ( _showLogsMenuItem.getState() ) {

                                LogsWindow.launch();

                            } else {

                                LogsWindow.getInstance().setVisible( false );

                            }

                            WindowWithMenus.setAllShowLogsModeInMenu( _showLogsMenuItem.getState() );

                        }
                    }
            );

            _showLogsMenuItem.setState( WindowWithMenus.s_showLogsMode );

            _fileMenu.add( _showLogsMenuItem );
            _showLogsMenuItem.setAccelerator(
                    KeyStroke.getKeyStroke(
                            KeyEvent.VK_L, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                    )
            );

            WindowWithMenus.s_allWindowsWithLogsMenuItem.add( this );

        }

        if ( !OSLevelCustomizations.onMacOsX() ) {

            JMenuItem exitItem = new JCheckBoxMenuItem( "Exit" );

            exitItem.addActionListener(
                    new ActionListener() {
                        public void actionPerformed( ActionEvent actionEvent ) {

                            System.exit( 0 );

                        }
                    }
            );

            _fileMenu.add( exitItem );
        }

        _menuBar.add( _fileMenu );

        _menuBar.add( defineEditMenu() );

//        // Replace the coffee cup icon in Windows XP JFrames
//
////        Trace.event( "inserting logo into JFrames" );
//
//        ImageIcon icon = new ImageIcon( LoaLogo.LOA_LOGO_16x16 );
//
//        setIconImage( icon.getImage() );

//        Trace.event( "setting the menu" );

        setJMenuBar( _menuBar );

//        Trace.event( "constructed WindowWithMenus" );

    }

    public static void setAllShowLogsModeInMenu( boolean value ) {

        WindowWithMenus.s_showLogsMode = value;
        for ( WindowWithMenus window : WindowWithMenus.s_allWindowsWithLogsMenuItem ) {

            window.setShowLogsModeInMenu( value );

        }

    }

    public void setShowLogsModeInMenu( boolean value ) {

        if ( _showLogsMenuItem != null ) {

            _showLogsMenuItem.setState( value );

        }

    }

    /**
     * Create an Edit menu that has Cut, Copy and Paste items which are always disabled to ensure that the menu exists and 'looks nice' if
     * this window does not actually need an Edit menu.
     * <p/>
     * Override this method in your derived class if you want an Edit menu that actually accomplishes something.
     * @return this window's Edit menu.
     */

    protected JMenu defineEditMenu() {

        JMenu skeletalEditMenu = new JMenu( "Edit" );

        JMenuItem selectAllMenuItem = new JMenuItem( "Select All" );
        selectAllMenuItem.setEnabled( false );
        selectAllMenuItem.setAccelerator(

                KeyStroke.getKeyStroke(
                        KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                )

        );

        JMenuItem cutMenuItem = new JMenuItem( "Cut" );
        cutMenuItem.setEnabled( false );
        cutMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                )
        );


        JMenuItem copyMenuItem = new JMenuItem( "Copy" );
        copyMenuItem.setEnabled( false );
        copyMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                )
        );

        JMenuItem pasteMenuItem = new JMenuItem( "Paste" );
        pasteMenuItem.setEnabled( false );
        pasteMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                )
        );


        skeletalEditMenu.add( cutMenuItem );
        skeletalEditMenu.add( copyMenuItem );
        skeletalEditMenu.add( pasteMenuItem );
        skeletalEditMenu.add( selectAllMenuItem );

        return skeletalEditMenu;

    }

    public void handlePreferences() {

        OkPopupMessageWindow.doit( "Preference menu not (yet?) implemented", "OK" );

    }

}
