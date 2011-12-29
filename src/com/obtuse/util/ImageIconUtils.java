package com.obtuse.util;

/*
 * Copyright © 2007, 2008 Loa Corporation
 * Copyright © 2011 Daniel Boulet
 */

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.awt.image.RescaleOp;
import java.net.URL;

/**
 * Utility methods for creating icons from images stored in our resources package.
 */

@SuppressWarnings( { "UnusedDeclaration" } )
public class ImageIconUtils {

    private static String _resourcesBaseDirectory = ".";

//    static {
//
//        String resourceBaseURLString = "file:.";
//        try {
//
//            _resourcesBaseURL = new URL( resourceBaseURLString );
//
//        } catch ( MalformedURLException e ) {
//
//            if ( BasicProgramConfigInfo.isInitialized() ) {
//
//                Logger.logErr( "unable to set default resources Base URL in ImageIconUtils", e );
//
//            } else {
//
//                System.err.println( "unable to set default resources Base URL in ImageIconUtils" );
//                e.printStackTrace();
//
//            }
//
//        }
//
//    }

    private ImageIconUtils() {
        super();

    }

    public static ImageIcon fetchIconImage( String fileName ) {

        return fetchIconImage( fileName, 0 );

    }

    public static void setDefaultResourcesDirectory( String resourcesBaseDirectory ) {

        _resourcesBaseDirectory = resourcesBaseDirectory;

    }

    public static String getDefaultResourceBaseDirectory() {

        return _resourcesBaseDirectory;

    }

    public static ImageIcon fetchIconImage( String fileName, int size ) {

        return fetchIconImage( fileName, size, _resourcesBaseDirectory );

    }

    public static ImageIcon fetchIconImage( String fileName, int size, String resourceBaseDirectory ) {

        URL url = null;
        try {

            url = ImageIconUtils.class.getClassLoader().getResource( resourceBaseDirectory + '/' + fileName );

        } catch ( Throwable e ) {

            // just ignore whatever went wrong

        }

        ImageIcon rval;
        if ( url != null ) {

            rval = new ImageIcon( url );

        } else {

            rval = null;

        }

        if ( size == 0 ) {

            return rval;

        } else {

            return new ImageIcon(
                    rval.getImage().getScaledInstance(
                            size,
                            -1,
                            Image.SCALE_SMOOTH
                    )
            );

        }

    }

    /**
     * Get a {@link java.awt.image.BufferedImage} version of an {@link java.awt.Image}.
     * Identical to {@link #copyToBufferedImage(java.awt.Image)} except that the original image is returned if it is a {@link java.awt.image.BufferedImage}.
     * <p/>
     * This method came from
     * <blockquote>
     * http://www.exampledepot.com/egs/java.awt.image/Image2Buf.html
     * </blockquote>
     * I (danny) don't know the terms of use as their "Terms of Use" link didn't do anything in either Safari or Firefox on my Mac OS X Snow Leopard system.
     *
     * @param xImage the image to be converted.
     *
     * @return the original image if it is a {@link java.awt.image.BufferedImage}; otherwise, the original image converted to a {@link java.awt.image.BufferedImage}.
     */

    public static BufferedImage toBufferedImage(Image xImage) {

        if (xImage instanceof BufferedImage) {

            return (BufferedImage)xImage;

        }
        return copyToBufferedImage( xImage );


    }

    /**
     * Make a {@link java.awt.image.BufferedImage} copy of an {@link java.awt.Image}.
     * Identical to {@link #toBufferedImage(java.awt.Image)} except that a new image is returned even if the original image is a {@link java.awt.image.BufferedImage}.
     * <p/>
     * This method came from
     * <blockquote>
     * http://www.exampledepot.com/egs/java.awt.image/Image2Buf.html
     * </blockquote>
     * I (danny) don't know the terms of use as their "Terms of Use" link didn't do anything in either Safari or Firefox on my Mac OS X Snow Leopard system.
     *
     * @param xImage the image to be converted.
     *
     * @return a copy of the original image.
     */

    public static BufferedImage copyToBufferedImage( Image xImage ) {

        // This code ensures that all the pixels in the image are loaded

        Image image = new ImageIcon(xImage).getImage();

        // Determine if the image has transparent pixels; for this method's
        // implementation, see Determining If an Image Has Transparent Pixels

        boolean hasAlpha = hasAlpha(image);

        // Create a buffered image with a format that's compatible with the screen

        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {

            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {

                transparency = Transparency.BITMASK;

            }

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                    image.getWidth(null), image.getHeight(null), transparency);

        } catch (HeadlessException e) {

            // The system does not have a screen

        }

        if (bimage == null) {

            // Create a buffered image using the default color model

            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {

                type = BufferedImage.TYPE_INT_ARGB;

            }

            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);

        }

        // Copy image to buffered image

        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image

        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;

    }

    /**
     * This method returns true if the specified image has transparent pixels.
     *<p/>
     * This method came from
     * <blockquote>
     * http://www.exampledepot.com/egs/java.awt.image/HasAlpha.html
     * </blockquote>
     * I (danny) don't know the terms of use as their "Terms of Use" link didn't do anything in either Safari or Firefox on my Mac OS X Snow Leopard system.
     *
     * @param image the image to be inspected.
     *
     * @return true if the image has transparent pixels; false otherwise.
     */

    public static boolean hasAlpha(Image image) {

        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {

            BufferedImage bimage = (BufferedImage)image;
            return bimage.getColorModel().hasAlpha();

        }

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {

            pg.grabPixels();

        } catch (InterruptedException e) {

        }

        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();

    }

    /**
     * Create a new {@link java.awt.image.BufferedImage} which is brighter or darker than the specified {@link java.awt.Image}.
     *
     * @param image the image to be brightened or darkened.
     * @param scaleFactor how much the image is to be brightened (if greater than 1) or darkened (if less than 1).
     * For example, 1.2 makes the image 20% brighter whereas 0.8 makes the image 20% darker.
     *
     * @return the brighter or darker image (always a new image even if the scaling factor is 1.0).
     */

    public static BufferedImage changeImageBrightness( Image image, float scaleFactor ) {

        BufferedImage bufferedVersion = copyToBufferedImage( image );

        RescaleOp op = new RescaleOp(scaleFactor, 0, null);
        op.filter( bufferedVersion, bufferedVersion );

        return bufferedVersion;

    }

    /**
     * Create a new {@link javax.swing.ImageIcon} which is brighter or darker than the specified {@link javax.swing.ImageIcon}.
     *
     * @param imageIcon the {@link javax.swing.ImageIcon} to be brightened or darkened.
     * @param scaleFactor how much the {@link javax.swing.ImageIcon} is to be brightened (if greater than 1) or darkened (if less than 1).
     * For example, 1.2 makes the image 20% brighter whereas 0.8 makes the image 20% darker.
     *
     * @return the brighter or darker image (always a new {@link javax.swing.ImageIcon} even if the scaling factor is 1.0).
     */

    public static ImageIcon changeImageIconBrightness( ImageIcon imageIcon, float scaleFactor ) {

        return new ImageIcon( changeImageBrightness( imageIcon.getImage(), scaleFactor ) );

    }

}
