package com.holub.ui;

import com.jhlabs.image.HSBAdjustFilter;
import com.obtuse.util.ImageIconUtils;
import com.obtuse.util.ImmutableDate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Calendar;

/** This class is wrapper for a {@link Date_selector} that adds a
 *  navigation bar to manipulate the wrapped selector.
 *  See {@link Date_selector_panel} for a description and picture
 *  of date selectors.
 * <DL>
 * <DT><b>Images</b>
 * <DD>
 *	<P>
 *  The navigation-bar arrows in the current implementation are images
 *  loaded as a "resource" from the CLASSPATH. Four files are used:
 *	<blockquote>
 *	$CLASSPATH/com/holub/images/10px.red.arrow.right.double.gif<br>
 *	$CLASSPATH/com/holub/images/10px.red.arrow.left.double.gif<br>
 *	$CLASSPATH/com/holub/images/10px.red.arrow.right.gif<br>
 *	$CLASSPATH/com/holub/images/10px.red.arrow.left.gif
 *	</blockquote>
 *	where <em>$CLASSPATH</em> is any directory on your CLASSPATH.
 *  If the <code>Date_selector_panel</code>
 *  can't find the image file, it uses character representations
 *  (<code>"&gt;"</code>, <code>"&gt;&gt;"</code>,
 *  <code>"&lt;"</code>, <code>"&lt;&lt;"</code>).
 *  The main problem with this approach is that you can't change
 *  the color of the arrows without changing the image files. On
 *  the plus side, arbitrary images can be used for the movement
 *  icons.
 *  Future versions of this class will provide some way for you
 *  to specify that the arrows be rendered internally in colors
 *  that you specify at run time.
 * </DD>
 * </DL>
 *  @see Date_selector
 *  @see Date_selector_panel
 *  @see Date_selector_dialog
 *  @see Titled_date_selector
 */

@SuppressWarnings({ "ALL" })
public class Navigable_date_selector extends JPanel implements Date_selector {
    private Date_selector selector;

    // Names of images files used for the navigator bar.
    private static final String
            NEXT_YEAR = "com/holub/images/10px.red.arrow.right.double.gif",
            NEXT_MONTH = "com/holub/images/10px.red.arrow.right.gif",
            PREVIOUS_YEAR = "com/holub/images/10px.red.arrow.left.double.gif",
            PREVIOUS_MONTH = "com/holub/images/10px.red.arrow.left.gif";

    // These constants are used both to identify the button, and
    // as the button caption in the event that the appropriate
    // immage file can't be located.

    private static final String FORWARD_MONTH = ">",
            FORWARD_YEAR = ">>",
            BACK_MONTH = "<",
            BACK_YEAR = "<<";


    private JPanel navigation = null;

    /** Wrap an existing Date_selector to add a a navigation bar
     *  modifies the wrapped Date_selector.
     */

    public Navigable_date_selector( Date_selector selector ) {
        this.selector = selector;
        setBorder( null );
        setOpaque( false );
        setLayout( new BorderLayout() );
        add( (JPanel)selector, BorderLayout.CENTER );

        navigation = new JPanel();
        navigation.setLayout( new FlowLayout() );
        navigation.setBorder( null );
        navigation.setBackground( Popup_dialog.TITLE_BAR_COLOR );
        navigation.add( make_navigation_button( BACK_YEAR ) );
        navigation.add( make_navigation_button( BACK_MONTH ) );
        navigation.add( make_navigation_button( FORWARD_MONTH ) );
        navigation.add( make_navigation_button( FORWARD_YEAR ) );

        add( navigation, BorderLayout.SOUTH );
    }

    /**
     * Create a navigable date selector by wrapping the indicated one.
     * @param selector the raw date selector to wrap;
     * @param background_color the background color of the navigation
     * 		bar (or null for transparent). The default color is
     * 		{@link com.holub.ui.Colors#LIGHT_YELLOW}.
     * @see #setBackground
     */

    public Navigable_date_selector( Date_selector selector, Color background_color ) {
        this( selector );
        navigation.setBackground( background_color );
    }

    /** Convenience constructor. Creates the wrapped Date_selector
     *  for you. (It creates a {@link Date_selector_panel} using
     *  the no-arg constructor.
     */

    public Navigable_date_selector() {
        this( new Date_selector_panel() );
    }

    public void change_navigation_bar_color( Color background_color ) {
        if ( background_color != null ) {
            navigation.setBackground( background_color );
        } else {
            navigation.setOpaque( false );
        }
    }

    private final Navigation_handler navigation_listener
            = new Navigation_handler();

    /** Handle clicks from the navigation-bar buttons. */

    private class Navigation_handler implements ActionListener {
        public void actionPerformed( ActionEvent e ) {
            String direction = e.getActionCommand();

            if ( direction == FORWARD_YEAR ) {
                selector.roll( Calendar.YEAR, true );
            } else if ( direction == BACK_YEAR ) {
                selector.roll( Calendar.YEAR, false );
            } else if ( direction == FORWARD_MONTH ) {
                selector.roll( Calendar.MONTH, true );
                if ( selector.get( Calendar.MONTH ) == Calendar.JANUARY ) {
                    selector.roll( Calendar.YEAR, true );
                }
            } else if ( direction == BACK_MONTH ) {
                selector.roll( Calendar.MONTH, false );
                if ( selector.get( Calendar.MONTH ) == Calendar.DECEMBER ) {
                    selector.roll( Calendar.YEAR, false );
                }
            } else {
                assert false : "Unexpected direction";
            }
        }
    }

    private JButton make_navigation_button( String caption ) {
        ClassLoader loader = getClass().getClassLoader();
        URL image =
                ( caption == FORWARD_YEAR ) ? loader.getResource( NEXT_YEAR ) :
                        ( caption == BACK_YEAR ) ? loader.getResource( PREVIOUS_YEAR ) :
                                ( caption == FORWARD_MONTH ) ? loader.getResource( NEXT_MONTH ) :
                                        loader.getResource( PREVIOUS_MONTH );

        JButton b = ( image != null ) ? new JButton(
                transmogrifyImage( new ImageIcon( image ) )
        )
                : new JButton( caption );
        b.setBorder( new EmptyBorder( 0, 4, 0, 4 ) );
        b.setFocusPainted( false );
        b.setActionCommand( caption );
        b.addActionListener( navigation_listener );
        b.setOpaque( false );
        return b;
    }

    public static ImageIcon transmogrifyImage( ImageIcon image ) {

        BufferedImage src = ImageIconUtils.copyToBufferedImage( image.getImage() );
        BufferedImage dst = new BufferedImage( src.getWidth(), src.getHeight(), src.getType() );

        HSBAdjustFilter filter = new HSBAdjustFilter( 0.6f, 0.0f, -0.01f );
        filter.filter( src, dst );

        return new ImageIcon( dst );

//        return ImageIconUtils.changeImageIconBrightness( new ImageIcon( image ), 0.2f );

    }

    public synchronized void addActionListener( ActionListener l ) {
        selector.addActionListener( l );
    }

    public synchronized void removeActionListener( ActionListener l ) {
        selector.removeActionListener( l );
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
