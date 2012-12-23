package com.obtuse.generic;

import com.obtuse.util.ObtuseUtil5;

import javax.swing.*;
import java.awt.event.*;

/*
 * Copyright © 2010 Daniel Boulet.
 */

@SuppressWarnings({ "UnusedDeclaration" })
public abstract class YesNoPopupMessageWindow
        extends JDialog {

    private JPanel _contentPane;

    private JButton _alternativeButton;

    private JButton _defaultButton;

    private JLabel _firstMessageField;

    private JLabel _secondMessageField;

    private boolean _answer;

    private boolean _gotAnswer;

    protected YesNoPopupMessageWindow(
            String line1,
            String line2,
            String defaultLabel,
            String alternativeLabel
    ) {

        super();

        setContentPane( _contentPane );
        setModal( true );
//        setUndecorated( true );

        getRootPane().setDefaultButton( _defaultButton );

        _alternativeButton.setText( alternativeLabel );
        _alternativeButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed( ActionEvent e ) {

                        onAlternativeChoice();
                    }
                }
        );

        _defaultButton.setText( defaultLabel );
        _defaultButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed( ActionEvent e ) {

                        onDefaultChoice();
                    }
                }
        );

        _firstMessageField.setText( "<html>" + line1 );
        if ( line2 == null ) {
            _secondMessageField.setVisible( false );
        } else {
            _secondMessageField.setText( "<html>" + line2 );
        }

        // call onCancel() when cross is clicked
        setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );

        //noinspection RefusedBequest
        addWindowListener(
                new WindowAdapter() {

                    public void windowClosing( WindowEvent e ) {
//                        onAlternativeChoice();
                    }
                }
        );

        // call onCancel() on ESCAPE
        _contentPane.registerKeyboardAction(
                new ActionListener() {

                    public void actionPerformed( ActionEvent e ) {
//                        onAlternativeChoice();
                    }
                },
                KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );

        pack();
        setResizable( false );

    }

    protected YesNoPopupMessageWindow(
            String line1, String defaultLabel, String alternativeLabel
    ) {

        this( line1, null, defaultLabel, alternativeLabel );
    }

    @SuppressWarnings({ "InstanceMethodNamingConvention" })
    public void go() {

        _answer = false;
        _gotAnswer = false;

//        Logger.logMsg( "we are " + ( SwingUtilities.isEventDispatchThread() ? "" : "not " ) + "the event queue" );

        setVisible( true );

//        synchronized ( this ) {
//            while ( !_gotAnswer ) {
//                try {
//                    wait();
//                } catch ( InterruptedException e ) {
//
//                    // just ignore it
//
//                }
//            }
//
//            dispose();
//
//            return _answer;
//        }
    }

    @SuppressWarnings({ "SameParameterValue" })
    public void fakeAnswer( final boolean answer ) {

        SwingUtilities.invokeLater(
                new Runnable() {

                    public void run() {

                        if ( answer ) {
                            onDefaultChoice();
                        } else {
                            onAlternativeChoice();
                        }
                    }
                }
        );
    }

    private synchronized void onDefaultChoice() {

        setVisible( false );

        if ( !_gotAnswer ) {
            _answer = true;
            _gotAnswer = true;
            defaultChoice();
        }
        notifyAll();

        dispose();
    }

    private synchronized void onAlternativeChoice() {

        setVisible( false );

        if ( !_gotAnswer ) {
            _answer = false;
            _gotAnswer = true;
            alternativeChoice();
        }
        notifyAll();

        dispose();
    }

    /**
     * Determine if an answer has been selected yet.
     *
     * @return true if an answer has been selected, false otherwise.
     */

    public boolean hasAnswer() {

        return _gotAnswer;
    }

    /**
     * Gets the answer to the question.
     *
     * @return true if the default was selected, false otherwise.
     * @throws IllegalArgumentException if no answer has been selected yet (see {@link #hasAnswer}).
     */

    public boolean getAnswer() {

        if ( hasAnswer() ) {
            return _answer;
        } else {
            throw new IllegalArgumentException( "no answer yet" );
        }
    }

    protected abstract void defaultChoice();

    protected abstract void alternativeChoice();

    @SuppressWarnings({ "SameParameterValue" })
    public static void doit(
            final String line1,
            final String line2,
            final String defaultLabel,
            final String alternativeLabel,
            final Runnable defaultRunnable,
            final Runnable alternativeRunnable
    ) {

        SwingUtilities.invokeLater(

                new Runnable() {

                    public void run() {

                        //noinspection ClassWithoutToString
                        YesNoPopupMessageWindow maybe = new YesNoPopupMessageWindow(
                                line1,
                                line2,
                                defaultLabel,
                                alternativeLabel
                        ) {

                            protected void defaultChoice() {

                                if ( defaultRunnable != null ) {
                                    defaultRunnable.run();
                                }
                            }

                            protected void alternativeChoice() {

                                if ( alternativeRunnable != null ) {
                                    alternativeRunnable.run();
                                }
                            }
                        };

                        maybe.go();

                    }

                }

        );

    }

    public static void doit(
            final String line1,
            final String defaultLabel,
            final String alternativeLabel,
            final Runnable defaultRunnable,
            final Runnable alternativeRunnable
    ) {

        YesNoPopupMessageWindow.doit( line1, null, defaultLabel, alternativeLabel, defaultRunnable, alternativeRunnable );

    }

    public static void main( String[] args ) {

        final YesNoPopupMessageWindow dialog = new YesNoPopupMessageWindow(
                "Are we having fun yet?<br>More words<br>Even more words<br>Still more words",
                "You have five seconds to decide!",
                "Yes",
                "No"
        ) {

            public void defaultChoice() {
                //noinspection UseOfSystemOutOrSystemErr
                System.out.println( "go said yes" );
            }

            public void alternativeChoice() {
                //noinspection UseOfSystemOutOrSystemErr
                System.out.println( "go said no" );
            }
        };
        //noinspection RefusedBequest
        new Thread() {

            public void run() {
                //noinspection MagicNumber
                ObtuseUtil5.safeSleepMillis( javax.management.timer.Timer.ONE_SECOND * 50L );
                dialog.fakeAnswer( true );
            }
        }.start();
        dialog.go();
    }

}
