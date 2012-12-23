package com.holub.ui;

import com.obtuse.util.ImmutableDate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/************************************************************************
 *  This class is a GoF "Decorator" that augements the "raw"
 *  </code>Date_selector_panel</code> with
 *  a title that displays the month name and year.
 *  The title updates automatically as the user navigates.
 *  Here's a picture:
 *  <blockquote>
 *	<img style="border: 0 0 0 0;" src="../../../images/Date_selector_panel.gif">
 *  </blockquote>
 *  Create a titled date selector like this:
 *  <pre>
 *  Date_selector selector = new Date_selector_panel(); // or other constructor.
 *  selector = new Titled_date_selector(selector);
 *  </pre>
 *  This wrapper absorbs the {@link Date_selector#CHANGE_ACTION}
 *  events: listeners that you register on the wrapper will be sent
 *  only {@link Date_selector#SELECT_ACTION} events.
 *  (Listeners that are registered on the wrapped
 *  <code>Date_selector</code> object will be notified of all events,
 *  however.
 *
 *  @see Date_selector
 *  @see Date_selector_panel
 *  @see Date_selector_dialog
 *  @see Navigable_date_selector
 */

public class Titled_date_selector extends JPanel implements Date_selector {
    private Date_selector selector;
    private final JLabel title = new JLabel( "XXXX" );

    /** Wrap an existing Date_selector to add a title bar showing
     *  the displayed month and year. The title changes as the
     *  user navigates.
     */

    public Titled_date_selector( Date_selector selector ) {
        this.selector = selector;

        title.setHorizontalAlignment( SwingConstants.CENTER );
        title.setOpaque( true );
        title.setBackground( com.holub.ui.Colors.LIGHT_YELLOW );
        title.setFont( title.getFont().deriveFont( Font.BOLD ) );

        selector.addActionListener
                (
                        new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                if ( e.getID() == CHANGE_ACTION ) {
                                    title.setText( e.getActionCommand() );
                                } else {
                                    my_subscribers.actionPerformed( e );
                                }
                            }
                        }
                );

        setOpaque( false );
        setLayout( new BorderLayout() );
        add( title, BorderLayout.NORTH );
        add( (JPanel)selector, BorderLayout.CENTER );
    }

    /** This constructor lets you specify the background color of the
     *  title strip that holds the month name and year (the default
     *  is light yellow).
     *
     *  @param label_background_color the color of the title bar, or
     *  	null to make it transparent.
     */
    public Titled_date_selector( Date_selector selector, Color label_background_color ) {
        this( selector );
        if ( label_background_color == null ) {
            title.setOpaque( false );
        } else {
            title.setBackground( label_background_color );
        }
    }

    private ActionListener my_subscribers = null;

    public synchronized void addActionListener( ActionListener l ) {
        my_subscribers = AWTEventMulticaster.add( my_subscribers, l );
    }

    public synchronized void removeActionListener( ActionListener l ) {
        my_subscribers = AWTEventMulticaster.remove( my_subscribers, l );
    }

    public ImmutableDate get_selected_date() {
        return selector.get_selected_date();
    }

    public ImmutableDate get_current_date() {
        return selector.get_current_date();
    }

    public void roll( int f, boolean up ) {
        selector.roll( f, up );
    }

    public int get( int f ) {
        return selector.get( f );
    }
}
