package com.obtuse.util;

import com.obtuse.ui.OkPopupMessageWindow;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Log messages window.
 * <p/>
 * Copyright © 2006, 2007 Daniel Boulet.
 */

@SuppressWarnings( { "ClassWithoutToString" } )
public class LogsWindow extends WindowWithMenus {

    private JPanel _contentPane;

    @SuppressWarnings( { "UnusedDeclaration" } )
    private JScrollPane _messageWindowScrollPane;

    private JList _messageWindowList;

    private JButton _closeButton;

    private DefaultListModel _messagesList = new DefaultListModel();

    private static final Long WINDOW_LOCK = new Long( 0L );

    @SuppressWarnings( { "FieldAccessedSynchronizedAndUnsynchronized" } )
    private static LogsWindow s_logsWindow;

    private static DateFormat s_dateFormatter = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

    private static boolean s_useHTML = false;

    public static final int MAX_LINES_IN_MESSAGE_WINDOW = 1000;

//    public static final int LOGO_SIZE = 80;

    private JMenu _editMenu;

    private JMenuItem _copyMenuItem;

    private Clipboard _systemClipboard;

    private static final String WINDOW_NAME = "LogsWindow";

//    private static ImageIcon _loaLogo = BasicIconLogoHandler.fetchIconImage("loa_logo.png", LOGO_SIZE);

    public LogsWindow( String appName ) {
        super( appName, LogsWindow.WINDOW_NAME, true );

        _systemClipboard = getToolkit().getSystemClipboard();

        setContentPane( _contentPane );

        // Handle the close button.

        //noinspection ClassWithoutToString
        _closeButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed( ActionEvent actionEvent ) {

                        WindowWithMenus.setAllShowLogsModeInMenu( false );
                        setVisible( false );

                    }
                }
        );

        setTitle( BasicProgramConfigInfo.getApplicationName() + " Log Messages" );

        // call onCancel() when cross is clicked
        setDefaultCloseOperation( WindowConstants.HIDE_ON_CLOSE );

        //noinspection RefusedBequest,ClassWithoutToString
        addWindowListener(
                new WindowAdapter() {
                    public void windowClosing( WindowEvent e ) {

                        WindowWithMenus.setAllShowLogsModeInMenu( false );
                    }
                }
        );

        _messageWindowList.setModel( _messagesList );
        _messageWindowList.setSelectionMode( ListSelectionModel.SINGLE_INTERVAL_SELECTION );
        _messageWindowList.addListSelectionListener(
                new ListSelectionListener() {

                    public void valueChanged( ListSelectionEvent listSelectionEvent ) {

                        if ( !listSelectionEvent.getValueIsAdjusting() ) {

                            try {

                                JList list = (JList)listSelectionEvent.getSource();
                                int[] selectedIndices = list.getSelectedIndices();

                                if ( selectedIndices.length > 0 ) {
                                    _copyMenuItem.setEnabled( true );
                                } else {
                                    _copyMenuItem.setEnabled( false );
                                }

                            } catch ( ClassCastException e ) {

                                Logger.logErr(
                                        "unexpected object type in log message window's selection listener (" +
                                        listSelectionEvent.getSource().getClass() + ") - selection ignored"
                                );

                            }

                        }

                    }

                }
        );

        WindowWithMenus.setAllShowLogsModeInMenu( false );
        setVisible( false );

        pack();

        restoreWindowGeometry( getWidth(), getHeight() );

    }

    @SuppressWarnings({ "UnusedDeclaration" })
    public static void addMessage( String msg ) {

        LogsWindow.addMessage( new Date(), msg );

    }

    public static void addMessage( Date when, final String msg ) {

        final String timeStampedMessage = LogsWindow.s_dateFormatter.format( when ) + ":  " + msg;
        if ( SwingUtilities.isEventDispatchThread() ) {

            LogsWindow.getInstance().insertMessageAtEnd( timeStampedMessage );

        } else {

        //noinspection ClassWithoutToString
            SwingUtilities.invokeLater(

                    new Runnable() {

                        public void run() {
                            try {

                                LogsWindow.getInstance().insertMessageAtEnd( timeStampedMessage );

                            } catch ( RuntimeException e ) {

                                Logger.logErr( "unable to insert message \"" + msg + "\" into log messages", e );

                            }

                        }

                    }

            );

        }

    }

    private void insertMessageAtEnd( String timeStampedMessage ) {

        int listSize = _messagesList.getSize();
        if ( listSize >= LogsWindow.MAX_LINES_IN_MESSAGE_WINDOW ) {

            _messagesList.remove( 0 );
            listSize -= 1;

        }

        int lastVisibleIx = _messageWindowList.getLastVisibleIndex();

        if ( LogsWindow.s_useHTML ) {

            _messagesList.addElement( "<html><tt>" + ObtuseUtil.htmlEscape( timeStampedMessage ) + "</tt></html>" );

        } else {

            _messagesList.addElement( timeStampedMessage );

        }

        if ( lastVisibleIx + 1 == listSize ) {

            _messageWindowList.ensureIndexIsVisible( lastVisibleIx + 1 );

        }

        WindowWithMenus.setAllShowLogsModeInMenu( true );
        setVisible( true );

    }

    @SuppressWarnings({ "UnusedDeclaration" })
    public void setUseHTML( boolean useHTML ) {

        LogsWindow.s_useHTML = useHTML;

    }

    @SuppressWarnings({ "UnusedDeclaration" })
    public boolean useHTML() {

        return LogsWindow.s_useHTML;

    }

    public static LogsWindow getInstance() {

        synchronized ( LogsWindow.WINDOW_LOCK ) {

            if ( LogsWindow.s_logsWindow == null ) {

                if ( BasicProgramConfigInfo.getApplicationName() == null ) {

                    OkPopupMessageWindow.fatal( "Application has not registered its name using BasicProgramConfigInfo.", "Unable to continue.", "I Will Submit A Bug Report" );

                }

                if ( BasicProgramConfigInfo.getPreferences() == null ) {

                    OkPopupMessageWindow.fatal( "Application has not registered its preferences object using BasicProgramConfigInfo.", "Unable to continue.", "I Will Submit A Bug Report" );

                }

                LogsWindow.s_logsWindow = new LogsWindow( BasicProgramConfigInfo.getApplicationName() );

            }

        }

        return LogsWindow.s_logsWindow;

    }

    public static void launch() {

        WindowWithMenus.setAllShowLogsModeInMenu( true );
        LogsWindow.getInstance().setVisible( true );

    }

    protected JMenu defineEditMenu() {

        _editMenu = new JMenu( "Edit" );

        JMenuItem selectAllMenuItem = new JMenuItem( "Select All" );
        selectAllMenuItem.setEnabled( true );
        selectAllMenuItem.addActionListener(

                new ActionListener() {

                    public void actionPerformed( ActionEvent actionEvent ) {

                        _messageWindowList.getSelectionModel().setSelectionInterval(
                                0,
                                _messagesList.size() - 1
                        );

                    }

                }

        );

        selectAllMenuItem.setAccelerator(

                KeyStroke.getKeyStroke(
                        KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                )

        );

        JMenuItem cutMenuItem = new JMenuItem( "Cut" );

        cutMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                )
        );

        cutMenuItem.setEnabled( false );

        _copyMenuItem = new JMenuItem( "Copy" );

        _copyMenuItem.setEnabled( false );

        _copyMenuItem.addActionListener(

                new ActionListener() {

                    public void actionPerformed( ActionEvent actionEvent ) {

                        StringWriter lines = new StringWriter();
                        PrintWriter writer = new PrintWriter( lines );
                        int[] selectedIndices = _messageWindowList.getSelectedIndices();
                        Object[] selectedValues = _messageWindowList.getSelectedValues();

                        for ( int ix = 0; ix < selectedIndices.length; ix += 1 ) {

                            if ( ix < selectedIndices.length - 1 ) {

                                writer.println( selectedValues[ix] );

                            } else {

                                writer.print( selectedValues[ix] );

                            }

                        }

                        writer.flush();
                        StringSelection selection = new StringSelection( lines.getBuffer().toString() );
                        _systemClipboard.setContents( selection, selection );

                    }

                }

        );

        _copyMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                )
        );

        JMenuItem pasteMenuItem = new JMenuItem( "Paste" );

        pasteMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                )
        );

        pasteMenuItem.setEnabled( false );

        _editMenu.add( cutMenuItem );   // never enabled (yet?)
        _editMenu.add( _copyMenuItem );
        _editMenu.add( pasteMenuItem ); // never enabled (yet?)
        _editMenu.add( selectAllMenuItem );

        return _editMenu;

    }

}
