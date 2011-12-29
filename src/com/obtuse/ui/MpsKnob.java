package com.obtuse.ui;

import com.obtuse.util.ImageIconUtils;
import com.obtuse.util.OrientedImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/**
 * Created by IntelliJ IDEA.
 * User: danny
 * Date: 2011/12/25
 * Time: 10:34
 * To change this template use File | Settings | File Templates.
 */

public abstract class MpsKnob {

    private BufferedImage _image;

    public MpsKnob( Image image ) {

        super();

        _image = ImageIconUtils.toBufferedImage( image );

    }

    protected void setImage( Image image ) {

        _image = ImageIconUtils.toBufferedImage( image );

    }

    public BufferedImage getImage() {

        return _image;

    }

    public abstract boolean isPointOnKnob(
            Point hotSpot,
            MpsKnobSize knobSize,
            boolean isSelected,
            MultiPointSlider.PositionOnLine positionOnLine,
            Point point
    );

    public abstract void drawKnob(
            Graphics2D g,
            Point hotSpot,
            MpsKnobSize knobSize,
            boolean isSelected,
            MultiPointSlider.PositionOnLine positionOnLine,
            ImageObserver imageObserver
    );

    public abstract OrientedImage getOrientedImage(
            MpsKnobSize knobSize,
            MultiPointSlider.PositionOnLine positionOnLine,
            boolean isSelected
    );

}
