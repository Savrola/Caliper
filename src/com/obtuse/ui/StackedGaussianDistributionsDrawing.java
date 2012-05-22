package com.obtuse.ui;

import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.WeightedGaussianDistribution;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;

/**
 * Draw one or more stacked proportionally weighted gaussian distributions.
 * <p/>
 * Copyright © 2012 Invidi Technologies Corporation
 * Copyright © 2012 Obtuse Systems Corporation
 */

public class StackedGaussianDistributionsDrawing extends JPanel {

    private WeightedGaussianDistribution[] _gds;
    private final double _from;
    private final double _to;

    public StackedGaussianDistributionsDrawing() {
        //noinspection MagicNumber
        this(
                new WeightedGaussianDistribution[] {
                        new WeightedGaussianDistribution( 1.0, 0.5, 0.5 / 3 )
                },
                0.0,
                1.0
        );

    }

//    public StackedGaussianDistributionsDrawing( GaussianDistribution gd ) {
//        this( gd, 0.0, 1.0 );
//    }

    public StackedGaussianDistributionsDrawing(
            WeightedGaussianDistribution[] gds,
            double from,
            double to
    ) {

        super();

        _gds = Arrays.copyOf( gds, gds.length );
        _from = from;
        _to = to;

        //noinspection MagicNumber
        setMinimumSize( new Dimension( 400, 100 ) );
        //noinspection MagicNumber
        setMaximumSize( new Dimension( 400, 100 ) );

    }

    @SuppressWarnings("ConstantConditions")
    public void paint( Graphics g ) {

//        Graphics2D g2d = (Graphics2D)g;

//        Logger.logMsg( "painting gaussian distribution " + _gd + " in (" + getWidth() + "," + getHeight() + ")" );

        ( (Graphics2D)g ).setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g.setColor( getBackground() );
//        g.setColor( Color.WHITE );
        g.fillRect( 0, 0, getWidth(), getHeight() );
//        ( (Graphics2D)g ).scale( 0.1, 0.1 );
//        ( (Graphics2D)g ).setStroke( new BasicStroke( 10f ) );

        double maxY = 0.0;
        for ( int pX = 0; pX < getWidth(); pX += 1 ) {

            double rX = mapXtoDrawing( pX, 0, getWidth() - 1, _from, _to );
            double rY = 0.0;
            for ( WeightedGaussianDistribution gd : _gds ) {

                rY += gd.getY( rX ) * gd.getWeight();

            }

            if ( rY > maxY ) {

                maxY = rY;

            }

        }

//        for ( WeightedGaussianDistribution gd : _gds ) {
//
//            maxY += gd.getY( gd.getCenter() ) * gd.getWeight();
//
//        }

        if ( maxY == 0 ) {

            maxY = 1.0;

        }

        int height = getHeight();

        int[] x = new int[getWidth()];
        int[] y = new int[getWidth()];

        for ( int pX = 0; pX < getWidth(); pX += 1 ) {

            double rX = mapXtoDrawing( pX, 0, getWidth() - 1, _from, _to );
            double rY = 0.0;
            for ( WeightedGaussianDistribution gd : _gds ) {

                rY += gd.getY( rX ) * gd.getWeight();

            }
//            Logger.logMsg( "pX = " + pX + ", rX = " + rX + ", rY = " + rY + ", scaled to " + ( rY / maxY ) );

            x[pX] = pX;
            y[pX] = (int)( ( height - 1 ) * ( 1.0 - rY / maxY ) );

//            g.drawLine( pX, 1 + (int)( height * ( rY / maxY ) ), pX, 1 + (int)( height * ( rY / maxY ) ) );

        }

        g.setColor( Color.BLACK );
        g.drawPolyline( x, y, getWidth() );

    }

    private double mapXtoDrawing( int pX, int minD, int maxD, double minR, double maxR ) {

        //noinspection UnnecessaryParentheses
        return minR + ( ( pX - minD ) * ( maxR - minR ) ) / ( maxD - minD );

    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Shared", "GDD", null );

        JFrame xx = new JFrame();
        StackedGaussianDistributionsDrawing gdd = new StackedGaussianDistributionsDrawing();
        xx.setContentPane( gdd );
        xx.pack();
        xx.setVisible( true );

    }

    @SuppressWarnings("UnusedDeclaration")
    public void setDistributions( Collection<WeightedGaussianDistribution> gds ) {

        _gds =
                gds.toArray( new WeightedGaussianDistribution[gds.size()] );

        repaint();

    }

    public void setDistributions( WeightedGaussianDistribution[] gds ) {

        _gds = Arrays.copyOf( gds, gds.length );
        repaint();

    }

}
