package com.obtuse.ui;

import com.obtuse.util.ImageIconUtils;
import com.obtuse.util.ObtuseUtil5;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * Describe a label on the slider line for an {@link MultiPointSlider}.
 */

@SuppressWarnings("UnusedDeclaration")
public class MpsLabel {

    private final String _textLabel;
    private final Image _imageLabel;
    private BufferedImage _generatedImage = null;
    private Font _generatedImageFont = null;

    public MpsLabel( String text ) {

        super();

        if ( text == null ) {

            throw new IllegalArgumentException( "text must not be null" );

        }

        _textLabel = text;
        _imageLabel = null;

    }

    public MpsLabel( int value ) {

        this( "" + value );

    }

    public MpsLabel( double value, int digits ) {

        this( ObtuseUtil5.lpad( value, 0, digits ) );

    }

    public MpsLabel( Image image ) {

        super();

        if ( image == null ) {

            throw new IllegalArgumentException( "image must not be null" );

        }

        _textLabel = null;
        _imageLabel = image;

//        if ( image instanceof BufferedImage ) {
//
//            _imageLabel = (BufferedImage)image;
//
//        } else {
//
//            _imageLabel = ImageIconUtils.toBufferedImage( image );
//
//        }
//
//        _generatedImage = _imageLabel;

    }

    /**
     * Make a dictionary / hash table of axis labels suitable for use with {@link MultiPointSlider}.
     * <p/>
     * For example, <tt>makeLabel( 0, 100, 25, 2 )</tt> would construct a dictionary with the following mappings:
     * <blockquote>
     * 0 -> "0"
     * <br>25 -> "0.25"
     * <br>50 -> "0.5"
     * <br>75 -> "0.75"
     * <br>100 -> "1"
     * </blockquote>
     * Note that the digits parameter also determines the scaling factor used to generate the mapping.  For example,
     * <tt>makeLabel( 0, 100, 25, 1 )</tt> would construct a dictionary with the following mappings:
     * <blockquote>
     * 0 -> "0"
     * <br>250 -> "2.5"
     * <br>500 -> "5"
     * <br>750 -> "7.5"
     * <br>1000 -> "10"
     * </blockquote>
     *
     * @param min    the minimum value for which a label is needed.
     * @param max    the maximum value for which a label is needed.
     * @param incr   the increment between each value for which a label is needed.
     * @param digits the maximum number of digits after the decimal place in each label (trailing zeros are omitted).
     * @return a dictionary of axis labels constructed according to the above criteria.
     * @throws IllegalArgumentException if digits is negative.
     */

    public static Hashtable<Integer, MpsLabel> makeLabels( int min, int max, int incr, int digits ) {

        if ( digits < 0 ) {

            throw new IllegalArgumentException( "digits must be non-negative" );

        }

        Hashtable<Integer, MpsLabel> ht = new Hashtable<Integer, MpsLabel>();
        int scalingFactor = 1;
        for ( int i = 0; i < digits; i += 1 ) {

            scalingFactor *= 10;

        }
        for ( int v = min; v <= max; v += incr ) {

            if ( digits == 0 ) {

                ht.put( v, new MpsLabel( "" + v ) );

            } else {

                ht.put( v, new MpsLabel( ObtuseUtil5.lpad( v / (double)scalingFactor, 0, digits ) ) );

            }

        }

        return ht;

    }

    /**
     * Make a dictionary / hash table of axis labels suitable for use with {@link MultiPointSlider}.
     * <p/>
     * For example, <tt>makeLabel( 0, 100, 25, 2 )</tt> would construct a dictionary with the following mappings:
     * <blockquote>
     * 0 -> "0"
     * <br>25 -> "0.25"
     * <br>50 -> "0.5"
     * <br>75 -> "0.75"
     * <br>100 -> "1"
     * </blockquote>
     * Note that the digits parameter also determines the scaling factor used to generate the mapping.  For example,
     * <tt>makeLabel( 0, 100, 25, 1 )</tt> would construct a dictionary with the following mappings:
     * <blockquote>
     * 0 -> "0"
     * <br>250 -> "2.5"
     * <br>500 -> "5"
     * <br>750 -> "7.5"
     * <br>1000 -> "10"
     * </blockquote>
     *
     * @param min    the minimum value for which a label is needed.
     * @param max    the maximum value for which a label is needed.
     * @param incr   the increment between each value for which a label is needed.
     * @param digits the maximum number of digits after the decimal place in each label (trailing zeros are omitted).
     * @return a dictionary of axis labels constructed according to the above criteria.
     */

    public static Hashtable<Integer, MpsLabel> makeLabels( int min, int max, double incr, int digits ) {

        Hashtable<Integer, MpsLabel> ht = new Hashtable<Integer, MpsLabel>();
        int scalingFactor = 1;
        for ( int i = 0; i < digits; i += 1 ) {

            scalingFactor *= 10;

        }
        for ( double v = min; v <= max; v += incr ) {

            ht.put( (int)Math.round( v ), new MpsLabel( ObtuseUtil5.lpad( v / (double)scalingFactor, 0, digits ) ) );

        }

        return ht;

    }

    public String getTextLabel() {

        return _textLabel;

    }

    public Image getImageLabel() {

        return _imageLabel;

    }

    public BufferedImage getGeneratedImage( Graphics2D g2d ) {

        // Punt any cached image if it was produced using a font which is different
        // than the current font.
        // Note that the following if will always fail for MpsLabel instances constructed
        // from images since such instances will never have a font cached.

        if ( _generatedImage != null && _generatedImageFont != null ) {

            if ( !_generatedImageFont.equals( g2d.getFont() ) ) {

                _generatedImage = null;
                _generatedImageFont = null;

            }

        }

        if ( _generatedImage == null ) {

            if ( _imageLabel == null ) {

                // Need to use the text variant

//                TextLayout textLayout = new TextLayout( _textLabel, g2d.getFont(), g2d.getFontRenderContext() );
//                Rectangle2D bounds = textLayout.getBounds();
//                Logger.logMsg( "bounds is " + bounds );
//                _generatedImage = new BufferedImage( bounds.getWidth(), bounds.getHeight(), BufferedImage.TYPE_INT_ARGB );

                FontMetrics fontMetrics = g2d.getFontMetrics( g2d.getFont() );
                Rectangle2D bounds = fontMetrics.getStringBounds( _textLabel, g2d );
//                Logger.logMsg( "bounds for \"" + _textLabel + "\" is " + bounds );
                int width = fontMetrics.stringWidth( _textLabel );
                int height = fontMetrics.getHeight();

                // The space that this text will take rounded up to the next integer is:
                //
                // width = -Math.ceil( -bounds.getX() + bounds.getWidth() )
                // height = -Math.ceil( -bounds.getY() + bounds.getHeight() )
                _generatedImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
                Graphics tg = _generatedImage.getGraphics();
                tg.setColor( g2d.getColor() );
                tg.drawString( _textLabel, -(int)Math.ceil( bounds.getX() ), -(int)Math.ceil( bounds.getY() ) );
//                tg.drawString( _textLabel, 5, 5 );

                _generatedImageFont = g2d.getFont();

            } else {

                // Need to use the image variant

                if ( _imageLabel instanceof BufferedImage ) {

                    _generatedImage = (BufferedImage)_imageLabel;

                } else {

                    _generatedImage = ImageIconUtils.toBufferedImage( _imageLabel );

                }

                _generatedImageFont = null;

            }

        }

        return _generatedImage;

    }

    public String toString() {

        if ( _textLabel == null ) {

            return "MpsLabel( <image> (no text label) )";

        } else {

            return "MpsLabel( \"" + _textLabel + "\" )";

        }

    }

}
