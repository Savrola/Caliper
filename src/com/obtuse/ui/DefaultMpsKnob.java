package com.obtuse.ui;

import com.obtuse.util.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.obtuse.ui.MultiPointSlider.OrientedImage;
import static com.obtuse.ui.MultiPointSlider.PositionOnLine;

/**
 * The implementation of {@link MpsKnob} used by default by the {@link com.obtuse.ui.MultiPointSlider} class.
 */

public class DefaultMpsKnob extends MpsKnob {

    private SortedMap<PositionOnLine, Double> _rotations =
            new TreeMap<PositionOnLine, Double>();
    private ThreeDimensionalSortedMap<MpsKnobSize, PositionOnLine, Boolean, OrientedImage>
            _rotatedSelectedScaledImages =
            new ThreeDimensionalTreeMap<MpsKnobSize, PositionOnLine, Boolean, OrientedImage>();

    public DefaultMpsKnob( Image image ) {

        super( image );

        for ( PositionOnLine position : PositionOnLine.values() ) {

            switch ( position ) {

                case ABOVE:
                    _rotations.put( PositionOnLine.ABOVE, 0.0 );
                    break;

                case BELOW:
                    _rotations.put( PositionOnLine.BELOW, Math.PI );
                    break;

                case LEFT:
                    _rotations.put( PositionOnLine.LEFT, -Math.PI / 2 );
                    break;

                case RIGHT:
                    _rotations.put( PositionOnLine.RIGHT, Math.PI / 2 );
                    break;

            }

        }

    }

    @Override
    public boolean isPointOnKnob(
            Point hotSpot,
            MpsKnobSize knobSize,
            boolean isSelected,
            PositionOnLine positionOnLine,
            Point point
    ) {

        MultiPointSlider.OrientedImage orientedImage = getOrientedImage( knobSize, positionOnLine, isSelected );

        return orientedImage.isPointInImage( hotSpot, point );

    }

    @Override
    public void drawKnob(
            Graphics2D g,
            Point hotSpot,
            MpsKnobSize knobSize,
            boolean isSelected,
            PositionOnLine positionOnLine,
            ImageObserver imageObserver
    ) {

        MultiPointSlider.OrientedImage img = getOrientedImage( knobSize, positionOnLine, isSelected );

//        int x = hotSpot.x - knobSize.integerSize() / 2;
//        int y = hotSpot.y - knobSize.integerSize();

        img.drawImage( g, hotSpot );

//                Logger.logMsg(
//                        "hX = " + hotSpot.x + ", hy = " + hotSpot.y + ", x = " + x + ", y = " + y + ", knobSize = ( " +
//                        knobSize + ", " + knobSize.integerSize() + " ), isSelected = " + isSelected + ", pol = " + positionOnLine
//                );
//
//                double rotation = _rotations.get( positionOnLine );
//                if ( rotation != 0.0 ) {
//
//                    g.rotate( rotation, hotSpot.x, hotSpot.y );
//
//                }
//
//                int fudgeX = 0;
//                int fudgeY = 0;
//                switch ( positionOnLine ) {
//
//                    case ABOVE:
//                        fudgeX = 0;
//                        break;
//
//                    case BELOW:
//                        fudgeX = -1;
//                        fudgeY = -1;
//                        break;
//
//                    case LEFT:
//                        fudgeX = -1;
//                        break;
//
//                    case RIGHT:
//                        fudgeX = 0;
//                        fudgeY = -1;
//                        break;
//
//                }
//
//                g.drawImage(
//                        img,
//                        x + fudgeX,
//                        y - 1 + fudgeY,
//                        imageObserver
//                );
//
//                if ( rotation != 0.0 ) {
//
//                    g.rotate( -rotation, hotSpot.x, hotSpot.y );
//
//                }

    }

    @Override
    public MultiPointSlider.OrientedImage getOrientedImage(
            MpsKnobSize knobSize,
            PositionOnLine positionOnLine,
            boolean isSelected
    ) {

        int ks = knobSize.integerSize();

        MultiPointSlider.OrientedImage orientedImage =
                _rotatedSelectedScaledImages.get( knobSize, positionOnLine, isSelected );
        if ( orientedImage == null ) {

            BufferedImage scaledImage = ImageIconUtils.toBufferedImage(
                    getImage().getScaledInstance(
                            knobSize.integerSize(),
                            -1,
                            Image.SCALE_SMOOTH
                    )
            );
            BufferedImage selectedScaledImage = ImageIconUtils.changeImageBrightness( scaledImage, 0.9f );
            BufferedImage rotatedImage;
            BufferedImage sourceImage = isSelected ? selectedScaledImage : scaledImage;
            Point hotSpot;
            if ( positionOnLine == PositionOnLine.ABOVE ) {

                rotatedImage = sourceImage;
                hotSpot = new Point( ks / 2, ks + 1 );

            } else {

                if ( positionOnLine == PositionOnLine.LEFT || positionOnLine == PositionOnLine.RIGHT ) {

                    rotatedImage = new BufferedImage(
                            sourceImage.getHeight( null ),
                            sourceImage.getWidth( null ),
                            BufferedImage.TYPE_INT_ARGB
                    );

                    Graphics2D g2d = (Graphics2D)rotatedImage.getGraphics();
                    Double rotation = _rotations.get( positionOnLine );
                    g2d.rotate( rotation.doubleValue() );
//                            g2d.setColor( Color.RED );
//                            g2d.drawLine( 0, 0, 50, 50 );
//                            g2d.setColor( Color.BLUE );
//                            g2d.drawLine( 0, 0, -50, 50 );
//                            g2d.setColor( Color.GREEN );
//                            g2d.drawLine( 0, 0, 50, -50 );
//                            g2d.setColor( Color.WHITE );
//                            g2d.drawLine( 0, 0, -50, -50 );

                    @SuppressWarnings("UnusedDeclaration")
                    boolean drawImageRval;
                    if ( positionOnLine == PositionOnLine.LEFT ) {

                        //noinspection UnusedAssignment
                        drawImageRval = g2d.drawImage( sourceImage, -rotatedImage.getWidth( null ), 0, null );
                        hotSpot = new Point( ks + 1, ks / 2 );

                    } else {

                        //noinspection UnusedAssignment
                        drawImageRval = g2d.drawImage( sourceImage, 0, -rotatedImage.getWidth( null ), null );
                        hotSpot = new Point( -2, ks / 2 );

                    }

                    g2d.dispose();

                } else {

                    rotatedImage = new BufferedImage(
                            sourceImage.getWidth( null ),
                            sourceImage.getHeight( null ),
                            BufferedImage.TYPE_INT_ARGB
                    );

                    Graphics2D g2d = (Graphics2D)rotatedImage.getGraphics();
                    g2d.rotate( _rotations.get( PositionOnLine.BELOW ).doubleValue() );
//                            g2d.setColor( Color.RED );
//                            g2d.drawLine( 0, 0, 50, 50 );
//                            g2d.setColor( Color.BLUE );
//                            g2d.drawLine( 0, 0, -50, 50 );
//                            g2d.setColor( Color.GREEN );
//                            g2d.drawLine( 0, 0, 50, -50 );
//                            g2d.setColor( Color.WHITE );
//                            g2d.drawLine( 0, 0, -50, -50 );
                    g2d.drawImage( sourceImage, -rotatedImage.getWidth( null ), -rotatedImage.getHeight( null ), null );
                    hotSpot = new Point( ks / 2, -2 );

                    g2d.dispose();

                }

            }

            orientedImage = new MultiPointSlider.OrientedImage( hotSpot, rotatedImage );
            _rotatedSelectedScaledImages.put( knobSize, positionOnLine, isSelected, orientedImage );

        }

        return orientedImage;

    }

}
