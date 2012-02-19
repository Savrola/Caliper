package com.obtuse.ui;

import com.obtuse.util.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * Along the lines of {@link javax.swing.JSlider} but capable of being configured to use
 * considerably less screen real estate and supports more than one slider point.
 */

@SuppressWarnings("UnusedDeclaration")
public class MultiPointSlider extends JComponent {

    public static final int MINIMUM_LINE_LENGTH = 25;
    public static final int MINIMUM_LABEL_GAP_SPACE = 2;
    private boolean _drawSliderLine = true;
    public static final boolean REAL_MODE = true;

    private static boolean _traceSizeChanges = false;

    private Dictionary<Integer, MpsLabel> _labelTable;
    private Dictionary<Integer, BufferedImage> _cachedLabelTable = new Hashtable<Integer, BufferedImage>();
    private BoundedRangeModel _brm;
    private MpsKnobSize _knobSize;
    private PositionOnLine _positionOnLine;
    private boolean _isSelected = false;
    private Point _startingPoint;
    private int _startingValue;
    private int _minorTickSpacing;
    private int _majorTickSpacing;
    private boolean _drawTickMarks;
    private boolean _drawLabels;
    private MpsKnob _knob;
    private ChangeListener _myChangeListener = new ChangeListener() {

        public void stateChanged( ChangeEvent changeEvent ) {

            Logger.logMsg( "repainting due to state change" );
            repaint();
            notifyListeners( changeEvent );

//            _knob.setLocation(
//                    new Point(
//                            left + ( length * _brm.getValue() ) / ( _brm.getMaximum() - _brm.getMinimum() ) - 6, 10
//                    )
//            );

        }

    };

    private static final MpsKnob _defaultKnob;
    private Collection<ChangeListener> _changeListeners = new LinkedList<ChangeListener>();
    private int _minimumBreadth;
    private int _minimumLength;
    private static final int TIC_GAP = 2;

    private Dimension _minimumSize;
    private final String _name;
    private static final int BORDER_SIZE = 10;
    private static final int MINIMUM_TIC_ROOM = 5;
    private static final int GAP_BETWEEN_TICK_MARKS_AND_LABELS = 2;
    private int _linePosition;

    static {

        ImageIcon imageIcon = ImageIconUtils.fetchIconImage(
                "slider_knob_13x13.png",
                0,
                "com/obtuse/ui/resources"
        );

        _defaultKnob = new DefaultMpsKnob( imageIcon.getImage() );

    }

    public static enum PositionOnLine {

        ABOVE,
        BELOW,
        LEFT,
        RIGHT

    }

    public MultiPointSlider( String name, BoundedRangeModel brm ) {
        super();

        _name = name;

        setModel( brm );

        _knob = _defaultKnob;

//        setLayout( new BoxLayout( this, Box ) );
//        add( _knob );

        _drawTickMarks = false;
        _drawLabels = false;

        setPositionOnLine( PositionOnLine.ABOVE );

        _knobSize = MpsKnobSize.SIZE_13x13;

        setOpaque( false );
        setVisible( true );

        addMouseListener(
                new MouseListener() {

                    public void mouseClicked( MouseEvent mouseEvent ) {

//                        Point hotSpot = mapValueToPoint( _brm.getValue() );
//                        boolean insideKnob = _knob.isPointOnKnob(
//                                hotSpot,
//                                _knobSize,
//                                _isSelected,
//                                _positionOnLine,
//                                mouseEvent.getPoint()
//                        );

//                        Logger.logMsg(
//                                "mouse clicked:  hotspot is " + hotSpot +
//                                ", point is " + mouseEvent.getPoint() +
//                                ", isSelected is " + _isSelected +
//                                ", pol is " + _positionOnLine +
//                                ", insideKnob is " + insideKnob
//                        );

                    }

                    public void mousePressed( MouseEvent mouseEvent ) {

                        Point hotSpot = mapValueToPoint( _brm.getValue() );
                        boolean insideKnob = _knob.isPointOnKnob(
                                hotSpot,
                                _knobSize,
                                _isSelected,
                                _positionOnLine,
                                mouseEvent.getPoint()
                        );

                        if ( insideKnob ) {

                            _isSelected = true;
                            _startingPoint = mouseEvent.getPoint();
                            _startingValue = _brm.getValue();
                            Logger.logMsg( "repainting because mouse pressed inside knob" );
                            repaint();

//                            Logger.logMsg( "mouse pressed:  inside, sP = " + _startingPoint + ", sV = " + _startingValue );

                        } else {

                            _isSelected = false;

//                            Logger.logMsg( "mouse pressed:  outside" );

                        }

//                        repaint();

                    }

                    public void mouseReleased( MouseEvent mouseEvent ) {

                        if ( _isSelected ) {

//                            Logger.logMsg( "mouse released:  adjusting" );
                            adjustValue( mouseEvent.getPoint() );

                            _isSelected = false;
                            Logger.logMsg( "repainting because mouse released while selected" );
                            repaint();

                        } else {

//                            Logger.logMsg( "mouse released:  ignored" );

                        }

                    }

                    public void mouseEntered( MouseEvent mouseEvent ) {
                    }

                    public void mouseExited( MouseEvent mouseEvent ) {
                    }

                }
        );

        addMouseMotionListener(
                new MouseMotionListener() {

                    public void mouseDragged( MouseEvent mouseEvent ) {

                        if ( _isSelected ) {


//                            Logger.logMsg( "mouse dragged:  adjusting" );
                            adjustValue( mouseEvent.getPoint() );

//                            Logger.logMsg( "repainting because mouse dragged" );
//                            repaint();

                        } else {

//                            Logger.logMsg( "mouse dragged:  ignored" );

                        }

                    }

                    public void mouseMoved( MouseEvent mouseEvent ) {
                    }

                }
        );

    }

    public static boolean traceSizeChanges() {

        return _traceSizeChanges;

    }

    public static void setTraceSizeChanges( boolean traceSizeChanges ) {

        _traceSizeChanges = traceSizeChanges;

    }

    public MpsKnobSize getKnobSize() {

        return _knobSize;

    }

    public void setKnobSize( MpsKnobSize knobSize ) {

        _knobSize = knobSize;

    }

    public MpsKnob getKnob() {

        return _knob;

    }

    public static MpsKnob getDefaultKnob() {

        return _defaultKnob;

    }

    public void setKnob( MpsKnob knob ) {

        if ( knob == null ) {

            throw new IllegalArgumentException( "knob is null" );

        }

        _knob = knob;

        _minimumSize = null;

    }

    private void notifyListeners( ChangeEvent changeEvent ) {

        for ( ChangeListener listener : _changeListeners ) {

            listener.stateChanged( changeEvent );

        }

    }

    public void addChangeListener( ChangeListener listener ) {

        removeChangeListener( listener );
        _changeListeners.add( listener );

    }

    public void removeChangeListener( ChangeListener listener ) {

        _changeListeners.remove( listener );

    }

    public boolean isVerticalOrientation() {

        return _positionOnLine == PositionOnLine.LEFT || _positionOnLine == PositionOnLine.RIGHT;

    }

    private void adjustValue( Point clickPoint ) {

        computeDrawingParameters();

        int moveDistance = isVerticalOrientation() ? clickPoint.y - _startingPoint.y : clickPoint.x - _startingPoint.x;

        Point startingHotSpot = mapValueToPoint( _startingValue );

        Point newPoint = isVerticalOrientation() ?
                new Point(
                        startingHotSpot.x,
                        startingHotSpot.y + moveDistance
                ) :
                new Point(
                        startingHotSpot.x + moveDistance,
                        startingHotSpot.y
                );

        int newValue = mapPointToValue( newPoint );

        // Set the value in our model.
        // The change listener that we have attached to this model will be triggered
        // if this setValue called actually changes the model's value.

        _brm.setValue( newValue );

//        Logger.logMsg(
//                "adjust value: " +
//                        " md = " + moveDistance + "," +
//                        " _sV = " + _startingValue + "," +
//                        " sHP = " + startingHotSpot + "," +
//                        " nP = " + newPoint + "," +
//                        " nV = " + _brm.getValue() + "," +
//                        " _brm = " + _brm
//        );

    }

    @SuppressWarnings("UnusedDeclaration")
    public void showPixels() {

        for ( int x = 0; x < _knob.getImage().getWidth( null ); x += 1 ) {

            for ( int y = 0; y < _knob.getImage().getHeight( null ); y += 1 ) {

                System.out.print( ObtuseUtil5.hexvalue( _knob.getImage().getRGB( x, y ) ) + " " );

            }

            System.out.println();

        }

    }

    public MultiPointSlider( String name, int min, int max ) {

        this( name, min, max, min );

    }

    public MultiPointSlider( String name, int min, int max, int value ) {

        this(
                name,
                new DefaultBoundedRangeModel( value, 0, min, max )
        );

    }

    public BoundedRangeModel getModel() {

        return _brm;

    }

    private Point mapValueToPoint( int value ) {

        if ( _positionOnLine == PositionOnLine.ABOVE || _positionOnLine == PositionOnLine.BELOW ) {

            return new Point( _endSpace + ( _length * ( value - _brm.getMinimum() ) / ( _brm.getMaximum() - _brm.getMinimum() ) ), _linePosition );

        } else {

            return new Point( _linePosition, _endSpace + ( _length * ( value - _brm.getMinimum() ) / ( _brm.getMaximum() - _brm.getMinimum() ) ) );

        }

    }

    private int mapPointToValue( Point p ) {

        return _brm.getMinimum() +
                Math.round(
                        (
                                ( ( isVerticalOrientation() ? p.y : p.x ) - _endSpace ) *
                                        ( _brm.getMaximum() - _brm.getMinimum() )
                        ) / (float) _length
                );

    }

    private int _width = -1;
    private int _height = -1;
    private int _length = -1;
    private int _endSpace = -1;

    private void computeDrawingParameters() {

        int width = getWidth();
        int height = getHeight();

        _width = width;
        _height = height;

        computeMinimumSize();

    }

    public BufferedImage getLabel( Graphics2D g2d, int value ) {

        BufferedImage labelImage = _cachedLabelTable.get( value );
        if ( labelImage == null ) {

            MpsLabel sliderLabel = null;
            if ( _labelTable != null ) {

                sliderLabel = _labelTable.get( value );

            }

            if ( sliderLabel == null ) {

                if ( _majorTickSpacing != 0 && ( value - _brm.getMinimum() ) % _majorTickSpacing == 0 ) {

                    sliderLabel = new MpsLabel( value );

                }

            }

            if ( sliderLabel == null ) {

                return null;

            }

            labelImage = sliderLabel.getGeneratedImage( g2d );
            _cachedLabelTable.put( value, labelImage );

        }

        return labelImage;

    }

//    int _s1Count = 0;
    public Dimension computeMinimumSize() {

        Logger.logMsg( _name + ":  call to computeMinimumSize()" );

        OrientedImage orientedImage = _knob.getOrientedImage( _knobSize, _positionOnLine, _isSelected );
//        if ( "s3s7".contains( _name ) )
//        Logger.logMsg( _name + ":  " + orientedImage );

        int breadth = 0;
        int length = 0;
        int knobBreadth;
        if ( isVerticalOrientation() ) {

            knobBreadth = orientedImage.getScreenWidth();

        } else {

            knobBreadth = orientedImage.getScreenHeight();

        }

        breadth += knobBreadth;

        if ( _drawTickMarks && ( _minorTickSpacing > 0 || _majorTickSpacing < 0 ) ) {

            int ticSpace = 0;
            if ( _minorTickSpacing > 0 ) {

                ticSpace = drawTickMarks( null, _minorTickSpacing, 2 );

            }

            if ( _majorTickSpacing > 0 ) {

                ticSpace = Math.max( ticSpace, drawTickMarks( null, _majorTickSpacing, 6 ) );

            }

            breadth += ticSpace;

        }

        // Part of the knob will protrude out before the start of the line if it is
        // moved to the very start of the line.  Similarly, part of the knob will
        // protrude out after the end of the line if the knob is moved to the very
        // end of the line.  We need to know just how much the knob could protrude
        // out each end.  Note that the knob is not necessarily symmetric or, more
        // to the point, the hot spot is not necessarily located at the midpoint
        // of the knob.

//        String what;
        int minValueOverhang;
        int maxValueOverhang;
        if ( isVerticalOrientation() ) {

//            if ( "s3s7".contains( _name ) )
//            Logger.logMsg( "oIsh = " + orientedImage.getScreenHeight() + ", oIhs = " + orientedImage.getHotSpotWithinImage() );
//            what = "a";

            minValueOverhang = orientedImage.getHotSpotWithinImage().y;
            maxValueOverhang = orientedImage.getScreenHeight() - orientedImage.getHotSpotWithinImage().y;

        } else {

//            if ( "s3s7".contains( _name ) )
//                Logger.logMsg( "oIsh = " + orientedImage.getScreenHeight() + ", oIhs = " + orientedImage.getHotSpotWithinImage() );
//            what = "b";

            minValueOverhang = orientedImage.getHotSpotWithinImage().x;
            maxValueOverhang = orientedImage.getScreenWidth() - orientedImage.getHotSpotWithinImage().x;

        }

        // Don't let the overhangs go negative (seems unlikely but the consequences would be pretty confusing).

        minValueOverhang = Math.max( 0, minValueOverhang );
        maxValueOverhang = Math.max( 0, maxValueOverhang );

//        if ( "s3s7".contains( _name ) )
//        Logger.logMsg( _name + ":  what = " + what + "; minVO = " + _minValueOverhang + "; maxVO = " + _maxValueOverhang );

        /*
         * Do we need to account for the space consumed by labels?
         */

        if ( _drawLabels ) {

            int maxLabelBreadth = 0;
            int maxLabelLength = 0;
            int labelCount = 0;
            BufferedImage firstLabel = null;
            BufferedImage lastLabel = null;

            Graphics2D g2d = (Graphics2D)getGraphics();
            if ( g2d == null ) {

                g2d = (Graphics2D)orientedImage.getImage().getGraphics();

            }
            g2d.setFont( getFont() );

            for ( int value = _brm.getMinimum(); value <= _brm.getMaximum(); value += _majorTickSpacing ) {

                BufferedImage labelImage = getLabel( g2d, value );

                if ( firstLabel == null ) {

                    firstLabel = labelImage;

                }

                lastLabel = labelImage;

                labelCount += 1;

                if ( isVerticalOrientation() ) {

                    maxLabelBreadth = Math.max( maxLabelBreadth, labelImage.getWidth() );
                    maxLabelLength = Math.max( maxLabelLength, labelImage.getHeight() );

                } else {

                    maxLabelBreadth = Math.max( maxLabelBreadth, labelImage.getHeight() );
                    maxLabelLength = Math.max( maxLabelLength, labelImage.getWidth() );

                }

            }

            breadth += GAP_BETWEEN_TICK_MARKS_AND_LABELS + maxLabelBreadth;

            // Half of the first label and (sometimes) half of the knob protrude out before the start
            // of the line.  Similarly, half of the last label and (sometimes) half of the knob
            // protrudes out past the end of the line.  We need to remember the larger of
            // half the width of the first label and however much of the knob could protrude
            // out before the start of the line as well as the larger of half the width of the last
            // label and however much of the knob could protrude out before the end of the line.
            // We computed how much of the knob might protrude out each end earlier so we just
            // need to remember the max of each pair.

//            Logger.logMsg( "before:  minVO = " + _minValueOverhang + ", maxVO = " + _maxValueOverhang + ", fL.h = " + firstLabel.getHeight() + ", fL.w = " + firstLabel.getWidth() + ", lL.h = " + lastLabel.getHeight() + ", lL.w = " + lastLabel.getWidth() );

//            String how;
            if ( isVerticalOrientation() ) {

//                how = "a";
                if ( firstLabel != null ) {

//                    how = "v1";
                    minValueOverhang = Math.max( ( firstLabel.getHeight() + 1 ) / 2, minValueOverhang );

                }

                if ( lastLabel != null ) {

//                    how = "v2";
                    maxValueOverhang = Math.max( ( lastLabel.getHeight() + 1 ) / 2, maxValueOverhang );

                }

            } else {

//                how = "b";
                if ( firstLabel != null ) {

//                    how = "v3";
                    minValueOverhang = Math.max( ( firstLabel.getWidth() + 1 ) / 2, minValueOverhang );

                }

                if ( lastLabel != null ) {

//                    how = "v4";
                    maxValueOverhang = Math.max( ( lastLabel.getWidth() + 1 ) / 2, maxValueOverhang );

                }

            }

//            Logger.logMsg( "how = " + how );

            // Compute the minimum line length while making sure we don't end up with a teensy tiny line.

            int minLabelSpace = labelCount * maxLabelLength + ( labelCount - 1 ) * MINIMUM_LABEL_GAP_SPACE;

            length = Math.max( MINIMUM_LINE_LENGTH, minLabelSpace );
//            length = minLabelSpace < MINIMUM_LINE_LENGTH ? MINIMUM_LINE_LENGTH : minLabelSpace;

        } else {

            String when;
            if ( _drawTickMarks ) {

                when = "1";
                if ( _minorTickSpacing > 0 ) {

                    when = "2";
                    length = ( ( _brm.getMaximum() - _brm.getMinimum() ) / _minorTickSpacing ) * MINIMUM_TIC_ROOM;

                }

                if ( _majorTickSpacing > 0 ) {

                    when = "3";
                    length = Math.max( ( ( _brm.getMaximum() - _brm.getMinimum() ) / _majorTickSpacing ) * MINIMUM_TIC_ROOM, length );

                }

            } else {

                when = "4";
                length = MINIMUM_LINE_LENGTH;

            }

            if ( "s3s7".contains( _name ) )
                Logger.logMsg( "when = " + when );

        }

        _length = length;
        _endSpace = BORDER_SIZE + minValueOverhang;

        length = minValueOverhang + length + maxValueOverhang;

        if ( "s3s7".contains( _name ) )
        Logger.logMsg( _name + "  minVO = " + minValueOverhang + ", maxVO = " + maxValueOverhang + ", es = " + _endSpace + ", l = " + _length );

        // We've got it!

        if ( isVerticalOrientation() ) {

            _minimumSize = new Dimension( 2 * BORDER_SIZE + breadth, 2 *  BORDER_SIZE + length );

        } else {

            _minimumSize = new Dimension( 2 * BORDER_SIZE + length, 2 * BORDER_SIZE + breadth );

        }

        switch ( _positionOnLine ) {

            case ABOVE:
                _linePosition = BORDER_SIZE + orientedImage.getHotSpotWithinImage().y;
                break;

            case BELOW:
                _linePosition = BORDER_SIZE + breadth - orientedImage.getImage().getHeight( null ) +
                                orientedImage.getHotSpotWithinImage().y;
                break;

            case LEFT:
                _linePosition = BORDER_SIZE + orientedImage.getHotSpotWithinImage().x;
                break;

            case RIGHT:
                _linePosition = BORDER_SIZE + breadth - orientedImage.getImage().getWidth( null ) +
                                orientedImage.getHotSpotWithinImage().x;

        }

        if ( "s3s7".contains( _name ) )
        Logger.logMsg( "" + _name + ":  breadth = " + breadth + ", length = " + length + ", knob breadth = " + knobBreadth + ", min size = ( " + _minimumSize.getWidth() + ", " + _minimumSize.getHeight() + " ), line position = " + _linePosition );
        if ( "s3s7".contains( _name ) )
            Logger.logMsg( _name + ":  " + _minimumSize );

//        Logger.logMsg( "slider \"" + _name + "\" has a computed minimum size of " + _minimumSize );

        return _minimumSize;

    }

    public void setMinimumSize( Dimension size ) {

        if ( _traceSizeChanges ) {

            Logger.logMsg( _name + ":  call to setMinimumSize( " + size + ")" );

        }

        super.setMinimumSize( size );

    }

    public void setMaximumSize( Dimension size ) {

        if ( _traceSizeChanges ) {

            Logger.logMsg( _name + ":  call to setMaximumSize( " + size + ")" );

        }

        super.setMaximumSize( size );

    }

    public void setPreferredSize( Dimension size ) {

        if ( _traceSizeChanges ) {

            Logger.logMsg( _name + ":  call to setPreferredSize( " + size + ")" );

        }

        super.setPreferredSize( size );

    }

    public void setBounds( int x, int y, int width, int height ) {

        if ( _traceSizeChanges ) {

            Logger.logMsg( _name + ":  call to setBounds( " + x + ", " + y + ", " + width + ", " + height + ")" );

        }

        super.setBounds( x, y, width, height );

    }

    public void setBounds( Rectangle bounds ) {

        if ( _traceSizeChanges ) {

            Logger.logMsg( _name + ":  call to setBounds( " + bounds + ")" );

        }

        super.setBounds( bounds );

    }

    public void paint( Graphics g ) {

        Graphics2D g2d = (Graphics2D) g;
        computeDrawingParameters();

        Logger.logMsg( "painting " + _name + " with size ( " + _width + ", " + _height + " )" );

        g.setColor( new Color( 255, 255, 255, 0 ) );
        g.fillRect( 0, 0, getWidth(), getHeight() );

        g.setColor( Color.WHITE );
        g.fillRect( 0, 0, getWidth(), getHeight() );

        g.setColor( Color.BLACK );

        int ticSpace = 0;

        if ( _drawTickMarks && ( _minorTickSpacing > 0 || _majorTickSpacing < 0 ) ) {

            if ( _minorTickSpacing > 0 ) {

                ticSpace = drawTickMarks( g, _minorTickSpacing, 2 );

            }

            if ( _majorTickSpacing > 0 ) {

                ticSpace = drawTickMarks( g, _majorTickSpacing, 6 );

            }

        }

        if ( _drawLabels ) {

            ticSpace += GAP_BETWEEN_TICK_MARKS_AND_LABELS;

            for ( int value = _brm.getMinimum(); value <= _brm.getMaximum(); value += _majorTickSpacing ) {

                Point valuePoint = mapValueToPoint( value );
                BufferedImage labelImage = getLabel( g2d, value );
                switch ( _positionOnLine ) {

                    case ABOVE:
                        g.drawImage( labelImage, valuePoint.x - labelImage.getWidth() / 2, valuePoint.y + ticSpace, this );
                        break;

                    case BELOW:
                        g.drawImage( labelImage, valuePoint.x - labelImage.getWidth() / 2, valuePoint.y - ( labelImage.getHeight() + ticSpace ), this );
                        break;

                    case LEFT:
                        g.drawImage( labelImage, valuePoint.x + ticSpace, valuePoint.y - labelImage.getHeight() / 2, this );
                        break;

                    case RIGHT:
                        g.drawImage( labelImage, valuePoint.x - ( labelImage.getWidth() + ticSpace ), valuePoint.y - labelImage.getHeight() / 2, this );
                        break;

                }

            }

        }

        g.setColor( Color.BLACK );
        Point lineStart = mapValueToPoint( _brm.getMinimum() );
        Point lineEnd = mapValueToPoint( _brm.getMaximum() );
        if ( _drawSliderLine ) {

            g.drawLine( lineStart.x, lineStart.y, lineEnd.x, lineEnd.y );

        }

        Point hotSpot = mapValueToPoint( _brm.getValue() );

        if ( REAL_MODE ) {

            _knob.drawKnob(
                    g2d,
                    hotSpot,
                    _knobSize,
                    _isSelected,
                    _positionOnLine,
                    this
            );

        } else {

            _knob.drawKnob(
                    g2d,
                    hotSpot,
                    _knobSize,
                    false, PositionOnLine.ABOVE,
                    this
            );
            _knob.drawKnob(
                    g2d,
                    hotSpot,
                    _knobSize,
                    false, PositionOnLine.BELOW,
                    this
            );
            _knob.drawKnob(
                    g2d,
                    hotSpot,
                    _knobSize,
                    false, PositionOnLine.LEFT,
                    this
            );
            _knob.drawKnob(
                    g2d,
                    hotSpot,
                    _knobSize,
                    false, PositionOnLine.RIGHT,
                    this
            );

        }

//        Logger.logMsg( "slider drawn" );

    }

//    private int drawLabel( Graphics2D g, String text, int x, int y ) {
//
//        MpsLabel label = new MpsLabel( text );
//        BufferedImage labelImage = label.getGeneratedImage( g );
//        g.drawImage( labelImage, x, y, this );
//        g.drawRect( x - 1, y - 1, labelImage.getWidth() + 2, labelImage.getHeight() + 2 );
//        Logger.logMsg( "bounding box of \"" + text + "\" is ( " + ( x - 1 ) + ", " + ( y - 1 ) + ", " + ( labelImage.getWidth() + 2 ) + ", " + ( labelImage.getHeight() + 2 ) + " )" );
//
//        return x + labelImage.getWidth() + 2;
//
//    }

    private int drawTickMarks( @Nullable Graphics g, int tickSpacing, int tickLength ) {

        if ( g != null ) {

            for ( int value = _brm.getMinimum(); value <= _brm.getMaximum(); value += tickSpacing ) {

                Point mark = mapValueToPoint( value );
                switch ( _positionOnLine ) {

                    case ABOVE:
                        g.drawLine( mark.x, mark.y + 1 + TIC_GAP, mark.x, mark.y + ( 1 + TIC_GAP + tickLength ) );
                        break;

                    case BELOW:
                        g.drawLine( mark.x, mark.y - ( 1 + TIC_GAP ), mark.x, mark.y - ( 1 + TIC_GAP + tickLength ) );
                        break;

                    case LEFT:
                        g.drawLine( mark.x + 1 + TIC_GAP, mark.y, mark.x + ( 1 + TIC_GAP + tickLength ), mark.y );
                        break;

                    case RIGHT:
                        g.drawLine( mark.x - ( 1 + TIC_GAP ), mark.y, mark.x - ( 1 + TIC_GAP + tickLength ), mark.y );
                        break;

                }

            }

        }

        return 1 + TIC_GAP + tickLength;

    }

    public void setDrawLabels( boolean drawLabels ) {

        _drawLabels = drawLabels;
        _minimumSize = null;

    }

    public boolean drawLabels() {

        return _drawLabels;

    }

    public void setLabelTable( Dictionary<Integer, MpsLabel> labelTable ) {

        _labelTable = labelTable;
        _minimumSize = null;

    }

    public Dictionary<Integer,MpsLabel> getLabelTable() {

        return _labelTable;

    }

    public String getName() {

        return _name;

    }

    public static void main( String[] args ) {

        Dictionary<Integer,MpsLabel> labels = new Hashtable<Integer, MpsLabel>();
        labels.put( 2, new MpsLabel( "two" ) );
        labels.put( 20, new MpsLabel( "twenty" ) );

        BasicProgramConfigInfo.init( "Obtuse", "Caliper", "test MultiPointSlider", null );
        JFrame frame = new JFrame( "Hello" );
        frame.setTitle( "Hi there" );
        JPanel bluePanel = new JPanel();
        bluePanel.setLayout( new BoxLayout( bluePanel, BoxLayout.Y_AXIS ) );
//        bluePanel.setBackground( new Color( 200, 200, 255 ) );
        MultiPointSlider slider = new MultiPointSlider( "s1", 0, 10 );
        slider.setLabelTable( labels );
        slider.setMinorTickSpacing( 1 );
        slider.setMajorTickSpacing( 2 );
        slider.setDrawTickMarks( true );
        slider.setDrawLabels( true );
        slider.getModel().setValue( slider.getModel().getMaximum() );
        slider.setMinimumSize( slider.computeMinimumSize() );
        bluePanel.add( slider );

        slider = new MultiPointSlider( "s2", 0, 1000 );
        slider.setLabelTable( labels );
        slider.setPositionOnLine( PositionOnLine.BELOW );
        slider.setMinorTickSpacing( 50 );
        slider.setMajorTickSpacing( 100 );
        slider.setDrawTickMarks( true );
        slider.setDrawLabels( true );
        slider.getModel().setValue( ( slider.getModel().getMinimum() ) );
        slider.setMinimumSize( slider.computeMinimumSize() );
        bluePanel.add( slider );

        slider = new MultiPointSlider( "s3", 0, 100 );
        slider.setLabelTable( labels );
        slider.setMinorTickSpacing( 5 );
        slider.setMajorTickSpacing( 10 );
        slider.setDrawTickMarks( true );
        slider.setDrawLabels( false );
        slider.getModel().setValue( slider.getModel().getMaximum() );
        slider.setMinimumSize( slider.computeMinimumSize() );
        bluePanel.add( slider );

        slider = new MultiPointSlider( "s4", 0, 1000 );
        slider.setLabelTable( labels );
        slider.setPositionOnLine( PositionOnLine.BELOW );
        slider.setMinorTickSpacing( 50 );
        slider.setMajorTickSpacing( 100 );
        slider.setDrawTickMarks( true );
        slider.setDrawLabels( false );
        slider.getModel().setValue( ( slider.getModel().getMinimum() ) );
        slider.setMinimumSize( slider.computeMinimumSize() );
        bluePanel.add( slider );

        JPanel redPanel = new JPanel();
        redPanel.setLayout( new BoxLayout( redPanel, BoxLayout.X_AXIS ) );
//        redPanel.setBackground( new Color( 255, 200, 200 ) );
        slider = new MultiPointSlider( "s5", 0, 10 );
        slider.setLabelTable( labels );
        slider.setMinorTickSpacing( 1 );
        slider.setMajorTickSpacing( 2 );
        slider.setDrawTickMarks( true );
        slider.setDrawLabels( true );
        slider.setPositionOnLine( PositionOnLine.LEFT );
        slider.getModel().setValue( slider.getModel().getMaximum() );
        final MultiPointSlider leftSlider = slider;
        slider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged( ChangeEvent changeEvent ) {

                        Logger.logMsg( "left slider changed:  value is " + leftSlider.getModel().getValue() );

                    }

                }
        );
        slider.setMinimumSize( slider.computeMinimumSize() );
        redPanel.add( slider );

        slider = new MultiPointSlider( "s6", 0, 1000 );
        slider.setLabelTable( labels );
        slider.setPositionOnLine( PositionOnLine.RIGHT );
        slider.setMinorTickSpacing( 50 );
        slider.setMajorTickSpacing( 100 );
        slider.setDrawTickMarks( true );
        slider.setDrawLabels( true );
        slider.getModel().setValue( ( slider.getModel().getMinimum() ) );
        slider.setMinimumSize( slider.computeMinimumSize() );
        redPanel.add( slider );

        slider = new MultiPointSlider( "s7", 0, 100 );
        slider.setMinorTickSpacing( 5 );
        slider.setMajorTickSpacing( 10 );
        slider.setDrawTickMarks( true );
        slider.setDrawLabels( false );
        slider.setPositionOnLine( PositionOnLine.LEFT );
        slider.getModel().setValue( slider.getModel().getMaximum() );
        slider.setMinimumSize( slider.computeMinimumSize() );
        redPanel.add( slider );

        slider = new MultiPointSlider( "s8", 0, 1000 );
        slider.setLabelTable( labels );
        slider.setPositionOnLine( PositionOnLine.RIGHT );
        slider.setMinorTickSpacing( 50 );
        slider.setMajorTickSpacing( 100 );
        slider.setDrawTickMarks( true );
        slider.setDrawLabels( false );
        slider.getModel().setValue( ( slider.getModel().getMinimum() ) );
        slider.setMinimumSize( slider.computeMinimumSize() );
        redPanel.add( slider );

        bluePanel.add( redPanel );
        JScrollPane jScrollPane = new JScrollPane( bluePanel );
        jScrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        jScrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );
        frame.setContentPane( jScrollPane );
        frame.pack();
        frame.setVisible( true );

    }

    public void setModel( BoundedRangeModel brm ) {

        if ( _brm != null ) {

            _brm.removeChangeListener( _myChangeListener );

        }

        _brm = brm;

        _brm.addChangeListener( _myChangeListener );
        _minimumSize = null;

    }

    public PositionOnLine getPositionOnLine() {

        return _positionOnLine;

    }

    public void setPositionOnLine( PositionOnLine positionOnLine ) {

        if ( _positionOnLine != null && _positionOnLine == positionOnLine ) {

            return;

        }

        _positionOnLine = positionOnLine;
        if ( _positionOnLine == PositionOnLine.ABOVE || _positionOnLine == PositionOnLine.BELOW ) {

            setMinimumSize( new Dimension( 400, 80 ) );
            setPreferredSize( new Dimension( 400, 80 ) );

        } else {

            setMinimumSize( new Dimension( 80, 300 ) );
            setPreferredSize( new Dimension( 80, 300 ) );

        }

        _minimumSize = null;

    }

    public boolean drawSliderLine() {

        return _drawSliderLine;

    }

    public void setDrawSliderLine( boolean drawSliderLine ) {

        _drawSliderLine = drawSliderLine;
        _minimumSize = null;

    }

    public int getMinorTickSpacing() {

        return _minorTickSpacing;

    }

    public void setMinorTickSpacing( int minorTickSpacing ) {

        _minorTickSpacing = minorTickSpacing;
        _minimumSize = null;

    }

    public int getMajorTickSpacing() {

        return _majorTickSpacing;

    }

    public void setMajorTickSpacing( int majorTickSpacing ) {

        _majorTickSpacing = majorTickSpacing;
        _minimumSize = null;

    }

    public boolean drawTickMarks() {

        return _drawTickMarks;

    }

    public void setDrawTickMarks( boolean drawTickMarks ) {

        _drawTickMarks = drawTickMarks;
        _minimumSize = null;

    }

    /**
     * Manage a particular orientation of an image.
     * <p/>
     * Intended to be used by the {@link com.obtuse.ui.MultiPointSlider} class.  Probably not all that useful in other contexts.
     */

    public static class OrientedImage {

        private final Point _hotSpotWithinImage;
        private final BufferedImage _image;

        public OrientedImage( Point hotSpotWithinImage, BufferedImage image ) {
            super();

            _hotSpotWithinImage = new Point( hotSpotWithinImage.x, hotSpotWithinImage.y );
            _image = image;

        }

        public boolean isPointInImage( Point actualHotSpot, Point p ) {

            Point imageLocation = new Point(
                    actualHotSpot.x - _hotSpotWithinImage.x,
                    actualHotSpot.y - _hotSpotWithinImage.y
            );
            Point computedHotSpot = new Point(
                    imageLocation.x + _hotSpotWithinImage.x,
                    imageLocation.y + _hotSpotWithinImage.y
            );
            Point pointWithinImage = new Point(
                    p.x - imageLocation.x,
                    p.y - imageLocation.y
            );
            Point imageSize = new Point(
                    _image.getWidth(),
                    _image.getHeight()
            );

            boolean isInside;
            if (
                    pointWithinImage.x >= 0 && pointWithinImage.x < imageSize.x
                    &&
                    pointWithinImage.y >= 0 && pointWithinImage.y < imageSize.y
            ) {

                int argb = _image.getRGB( pointWithinImage.x, pointWithinImage.y );
                isInside = ( argb & 0xff000000 ) != 0;

            } else {

                isInside = false;

            }

    //        Logger.logMsg(
    //                "image @ " + imageLocation +
    //                ", point @ " + p +
    //                ", point within image @ " + pointWithinImage +
    //                ", INSIDE is " + isInside +
    //                ", hotspot @ " + actualHotSpot +
    //                ", computed hotspot @ " + computedHotSpot
    //        );

            return isInside;

        }

        public void drawImage( Graphics2D g, Point actualHotSpot ) {

            g.translate( actualHotSpot.x, actualHotSpot.y );
    //            g.drawLine( -5, 0, 5, 0 );
    //            g.drawLine(  0, -5, 0, 5 );
            g.drawImage( _image, -_hotSpotWithinImage.x, -_hotSpotWithinImage.y, null );
            g.translate( -actualHotSpot.x, -actualHotSpot.y );

        }

        /**
         * Retrieve this image's hotspot.
         * @return a copy of this image's hotspot (to ensure that caller does not modify the hotspot).
         */

        public Point getHotSpotWithinImage() {

            return new Point( _hotSpotWithinImage.x, _hotSpotWithinImage.y );

        }

        public Image getImage() {

            return _image;

        }

    //    public int getScreenWidth( ImageObserver imageObserver ) {
    //
    //        int width = _image.getWidth( imageObserver );
    //        if ( _hotSpotWithinImage.x < 0 ) {
    //
    //            width += -_hotSpotWithinImage.x;
    //
    //        } else if ( _hotSpotWithinImage.x > width ) {
    //
    //            width = _hotSpotWithinImage.x;
    //
    //        }
    //
    //        return width;
    //
    //    }

        /**
         * Compute the amount of vertical screen space this image will consume.
         * <p/>
         * The computed value takes this instance' hotspot into account if the hotspot is either
         * above or below the image.
         *
         * @return the vertical screen space consumed by this image.
         */

        public int getScreenHeight() {

            int height = _image.getHeight( null );
            int before = height;
            if ( _hotSpotWithinImage.y < 0 ) {

                height += -_hotSpotWithinImage.y;
    //            Logger.logMsg( "- from " + before + " to " + height + ", adjustment " + -_hotSpotWithinImage.y );

            } else if ( _hotSpotWithinImage.y > height ) {

                height = _hotSpotWithinImage.y + 1;     // deal with zero-origin x and y values
    //            Logger.logMsg( "+ from " + before + " to " + height + ", adjustment =" + _hotSpotWithinImage.y + "+1");

    //        } else {
    //
    //            Logger.logMsg( "no adjustment" );

            }

            return height;

        }

        public int getScreenWidth() {

            int width = _image.getHeight( null );
            if ( _hotSpotWithinImage.x < 0 ) {

                width += -_hotSpotWithinImage.x;

            } else if ( _hotSpotWithinImage.x > width ) {

                width = _hotSpotWithinImage.x + 1;  // deal with zero-origin x and y values

            }

            return width;

        }

        public String toString() {

            return "OrientedImage( " + getScreenWidth() + 'x' + getScreenHeight() + ", hs = " + _hotSpotWithinImage + " )";

        }

    }

}
