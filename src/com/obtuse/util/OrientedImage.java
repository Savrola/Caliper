package com.obtuse.util;

import com.obtuse.ui.MultiPointSlider;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/**
* Created by IntelliJ IDEA.
* User: danny
* Date: 2011/12/25
* Time: 10:35
* To change this template use File | Settings | File Templates.
*/

public class OrientedImage {

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
