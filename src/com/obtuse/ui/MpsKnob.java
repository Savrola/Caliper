package com.obtuse.ui;

import com.obtuse.util.ImageIconUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * Manage a slider knob for an {@link MultiPointSlider}.
 */

public abstract class MpsKnob {

    private BufferedImage _image;

    public MpsKnob( Image image ) {

        super();

        _image = ImageIconUtils.toBufferedImage( image );

    }

    @SuppressWarnings("UnusedDeclaration")
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

    public abstract MultiPointSlider.OrientedImage getOrientedImage(
            MpsKnobSize knobSize,
            MultiPointSlider.PositionOnLine positionOnLine,
            boolean isSelected
    );

}
