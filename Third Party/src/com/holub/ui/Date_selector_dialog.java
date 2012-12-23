package com.holub.ui;

import com.obtuse.util.ImmutableDate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

/**
 *  The Date_selector_dialog, shown below,
 *  combines a {@link Date_selector} and
 *  a {@link Popup_dialog} to provide a standalone, popup dialog
 *  for choosing dates.
 *  <blockquote>
 * 	<img style="border_style:none" src="../images/Date_selector_dialog.gif">
 *  </blockquote>
 *  The dialog is a free-floating top-level window. You can drag it around by the
 *  title bar and  close it by clicking on the "close" icon.
 *  <p>
 *  The class does implement the {@link Date_selector} interface, but
 *  bear in mind that the window closes when the user selects a date.
 *  Unlike the {@link Titled_date_selector} wrapper class,
 *  both of the action events are sent to listeners, however.
 * 	Create one the hard way like this:
 * 	<pre>
 Date_selector calendar = new Date_selector_panel( selector );
 calendar = new Navigable_date_selector( calendar ); // add navigation
 Date_selector_dialog chooser = new Date_selector_dialog(parent_frame, calendar);
 //...
 Date d = chooser.select();	// Pops up chooser; returns selected Date.
 </pre>
 *	You can leave out the navigation bar by omitting the second line of the
 *	previous example. The following convenience constructor has exactly
 *	the same effect as the earlier code:
 * 	<pre>
 Date_selector_dialog chooser = new Date_selector_dialog(parent_frame);
 *	<pre>
 *	You can also pop up the dialog like this:
 *	<pre>
 chooser.setVisible(true);		// blocks until dialog closed
 Date d = chooser.get_selected_date();
 *	</pre>
 * This class is a stand-alone dialog. For a version
 * that you can embed into another window, see {@link Date_selector_panel}.
 *
 * @see Date_selector
 * @see Date_selector_panel
 * @see Navigable_date_selector
 * @see Titled_date_selector
 * @see Popup_dialog
 */

@SuppressWarnings({ "ALL" })
public class Date_selector_dialog extends Popup_dialog implements Date_selector {
    private Date_selector selector = new Date_selector_panel();

    /** Creates a dialog box with the indicated parent that holds
     *  a standard {@link Date_selector_panel Date_selector_panel}
     *  (as created using the no-arg constructor).
     */
    public Date_selector_dialog( Frame parent ) {
        super( parent );
        selector = new Navigable_date_selector( new Date_selector_panel() );
        init();
    }

    /* Like {@link #Date_selector_dialog(Frame),
      * but for a {@link Dialog} parent.
      */
    public Date_selector_dialog( Dialog parent ) {
        super( parent );
        selector = new Navigable_date_selector( new Date_selector_panel() );
        init();
    }

    /** Creates a dialog box with the indicated parent that holds
     *  the indicated Date_selector.
     *  Note that the current month and year is displayed in the
     *  dialog-box title bar, so there's no need to display it in
     *  the selector too.
     */
    public Date_selector_dialog( Frame parent, Date_selector to_wrap ) {
        super( parent );
        selector = to_wrap;
        init();
    }

    /* Like {@link #Date_selector_dialog(Frame,Date_selector),
      * but for a {@link Dialog} parent.
      */

    public Date_selector_dialog( Dialog parent, Date_selector to_wrap ) {
        super( parent );
        selector = to_wrap;
        init();
    }

    /** Code comon to all constructors
     */
    private void init() {
        getContentPane().add( (Container)selector, BorderLayout.CENTER );
        selector.addActionListener
                (
                        new ActionListener() {
                            public void actionPerformed( ActionEvent event ) {
                                if ( event.getID() == CHANGE_ACTION ) {
                                    setTitle( event.getActionCommand() );
                                } else {
                                    setVisible( false );
                                    dispose();
                                }
                            }
                        }
                );
        ( (Container)selector ).setVisible( true );
        pack();
    }

    /** For use when you pop up a dialog using
     * <code>setVisible(true)</code> rather than {@link #select}.
     * @return the selected date or null if the dialog was closed
     * 			without selecting anything.
     */
    public ImmutableDate get_selected_date() {
        return selector.get_selected_date();
    }

    /** Get the current date. The dialog stays in existance
     *  until the user closes it or selects a date, so this
     *  method can be used to see what month the user has
     *  scrolled to.
     *  @return the date currently displayed on the calendar.
     */
    public ImmutableDate get_current_date() {
        return selector.get_current_date();
    }

    /** Add an action listner for both
     *  {@link Date_selector#CHANGE_ACTION} and
     *  {@link Date_selector#SELECT_ACTION} action events.
     */
    public void addActionListener( ActionListener l ) {
        selector.addActionListener( l );
    }

    /** Remove a previously-added listener */
    public void removeActionListener( ActionListener l ) {
        selector.removeActionListener( l );
    }

    /** Pops up the chooser and blocks until the user selects
     *  a date.
     * @return the selected date or null if the dialog was closed
     * 			without selecting anything.
     */
    public Date select() {
        setVisible( true );
        return selector.get_selected_date();
    }

    public void roll( int f, boolean up ) {
        selector.roll( f, up );
    }

    public int get( int f ) {
        return selector.get( f );
    }

    //----------------------------------------------------------------------
    private static class Test {
        public static void main( String[] args )
                throws Exception {
            final JFrame frame = new JFrame();
            frame.getContentPane().add( new JLabel( "Main Frame" ) );
            frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            frame.pack();
            frame.setVisible( true );

            Date_selector_dialog chooser = new Date_selector_dialog( frame );
            chooser.setLocation( 10, 10 );
            System.out.println( "Displaying Selector" );

            System.out.println( chooser.select() );

            // No navigation bar
            chooser = new Date_selector_dialog(
                    frame,
                    new Date_selector_panel( 1900, 1, 2 )
            );

            chooser.setLocation( 10, 10 );
            System.out.println( "Displaying Selector" );

            System.out.println( chooser.select() );

            System.exit( -1 );
        }
    }
}
