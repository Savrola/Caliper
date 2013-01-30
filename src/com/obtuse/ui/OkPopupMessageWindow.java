package com.obtuse.ui;

import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.*;

/**
 * Popup a window with a message and a single button (which disposes of the window).
 * <p/>
 * Copyright © 2012 Daniel Boulet.
 *
 * @noinspection ClassWithoutToString, UnusedDeclaration
 */

public abstract class OkPopupMessageWindow extends JDialog {

    @SuppressWarnings({ "InstanceVariableNamingConvention" })

    private JPanel _contentPane;

    private JButton _okButton;

    private JLabel _firstMessageField;

    private JLabel _secondMessageField;

    protected OkPopupMessageWindow(
            String firstMessage,
            @Nullable String secondMessage,
            String buttonLabel
    ) {
        super();

        setContentPane( _contentPane );
        setModal( true );
//        setUndecorated( true );
        getRootPane().setDefaultButton( _okButton );

        _okButton.setText( buttonLabel );
        _okButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        onOK();
                        ok();
                    }
                }
        );

        _firstMessageField.setText( "<html>" + firstMessage );
        if ( secondMessage == null || secondMessage.trim().isEmpty() ) {

            _secondMessageField.setVisible( false );

        } else {

            _secondMessageField.setText( "<html>" + secondMessage );

        }

        // call onCancel() when cross is clicked
        setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );

        //noinspection RefusedBequest
        addWindowListener(
                new WindowAdapter() {

                    public void windowClosing( WindowEvent e ) {

                        onOK();
                        ok();

                    }

                }
        );

        // call onCancel() on ESCAPE
        _contentPane.registerKeyboardAction(
                new ActionListener() {

                    public void actionPerformed( ActionEvent e ) {

                        onOK();
                        ok();

                    }

                },
                KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );

        pack();
        setResizable( false );

    }

    @SuppressWarnings({ "SameParameterValue" })
    protected OkPopupMessageWindow( String firstMessage, String buttonLabel ) {
        this( firstMessage, null, buttonLabel );

    }

    private void onOK() {

        dispose();

    }

    /**
     * Called when someone clicks the ok button. This method is provided so that users of this class can deal with
     * clicks of the ok button themselves. The default implementation of this method does nothing.
     */

    @SuppressWarnings({ "InstanceMethodNamingConvention" })
    protected abstract void ok();

    /**
     * Wait for the human to click the button or otherwise dispose of the window.
     */

    @SuppressWarnings({ "InstanceMethodNamingConvention" })
    public void go() {

        pack();
        setVisible( true );
        dispose();

    }

    /**
     * Abort the popup.
     * <p/>
     * This method may be called from any thread.  It causes the popup to vanish without its button
     * being clicked (i.e. the popup's {@link #ok} method is never called.  Instead, the popup's
     * {@link #aborted} method is called.  Depending on what events happen to be queued at the moment that
     * this method is called, it is possible for the popup's {@link #aborted} and {@link #ok} methods to
     * both be called (it should not be possible for the {@link #ok} method to be called after the {@link #aborted}
     * method is called but one never knows).
     *
     * @param why the reason that the popup was aborted.  This value is passed to the popup's {@link
     *            #aborted} method.
     */

    public void abortPopup( final String why ) {

        SwingUtilities.invokeLater(
                new Runnable() {

                    public void run() {

                        setVisible( false );
                        aborted( why );

                    }

                }
        );

    }

    /**
     * This method is invoked if the popup's {@link #abortPopup} method is called.
     * The default implementation does nothing.
     * @param why the string passed to the popup's {@link #abortPopup} method.
     */

    @SuppressWarnings({ "EmptyMethod", "NoopMethodInAbstractClass" })
    public void aborted( String why ) {

        // Do nothing by default.

    }

    @SuppressWarnings({ "SameParameterValue" })
    public static void doit( final String line1,
                             @org.jetbrains.annotations.Nullable
                             final String line2,
                             final String button,
                             @Nullable
                             final Runnable runnable
    ) {

        SwingUtilities.invokeLater(
                new Runnable() {

                    public void run() {

                        //noinspection ClassWithoutToString
                        OkPopupMessageWindow ok = new OkPopupMessageWindow(
                                line1,
                                line2,
                                button
                        ) {

                            protected void ok() {

                                if ( runnable != null ) {

                                    runnable.run();

                                }

                            }

                        };

                        ok.go();

                    }

                }
        );

    }

    @SuppressWarnings({ "SameParameterValue" })
    public static void doit( String line1, String button ) {

        OkPopupMessageWindow.doit( line1, null, button, null );

    }

    @SuppressWarnings({ "SameParameterValue" })
    public static void doit( String line1, String line2, String button ) {

        OkPopupMessageWindow.doit( line1, line2, button, null );

    }

    public static void fatal( String line1 ) {

        OkPopupMessageWindow.fatal( line1, null, "Sorry" );

    }

    public static void fatal( String line1, String line2 ) {

        OkPopupMessageWindow.fatal( line1, line2, "Sorry" );

    }

    @SuppressWarnings({ "SameParameterValue" })
    public static void fatal(
            final String line1,
            @Nullable final String line2,
            @Nullable final String buttonText
    ) {

        Runnable runnable = new Runnable() {

            public void run() {

                //noinspection ClassWithoutToString
                OkPopupMessageWindow ok = new OkPopupMessageWindow(
                        line1,
                        line2 == null
                                ?
                                "Please click the button to terminate."
                                :
                                line2,
                        buttonText == null ? "Sorry" : buttonText
                ) {

                    protected void ok() {

                        System.exit( 1 );

                    }

                };

                ok.go();

                // Try to keep the window in the foreground.
                //noinspection InfiniteLoopStatement
                while ( true ) {

                    ObtuseUtil.safeSleepMillis( javax.management.timer.Timer.ONE_SECOND );
                    ok.setVisible( true );

                }

            }

        };

        if ( SwingUtilities.isEventDispatchThread() ) {

            runnable.run();

        } else {

            SwingUtilities.invokeLater(

                    runnable

            );

        }

        // We aren't allowed to return from this method!

        //noinspection InfiniteLoopStatement
        while ( true ) {

            ObtuseUtil.safeSleepMillis( javax.management.timer.Timer.ONE_MINUTE );

        }

    }

    public static void testIt() {

        OkPopupMessageWindow dialog = new OkPopupMessageWindow(
                "123456789.123456789.123456789.123456789.12345<br>123456789.123456789.123456789.123456789.12345",
                "123456789.123456789.123456789.123456789.12345",
                "OK"
        ) {

            public void ok() {

                // just ignore it

            }

        };

        Logger.logMsg( "size is " + dialog.getSize() );
        dialog.go();
        dialog = new OkPopupMessageWindow(
                "Looks like a nice day today", "Although I suppose it could rain", "Sigh"
        ) {

            protected void ok() {

            }

        };

        dialog.go();
        dialog = new OkPopupMessageWindow( "How are you today?", "Fine Thanks" ) {

            protected void ok() {

            }

        };

        dialog.go();
        System.exit( 0 );

    }

}
