package com.obtuse.ui;

import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.GaussianDistribution;

import javax.swing.*;
import java.awt.*;

/*
 * Copyright © 2012 Daniel Boulet
 */

public class GaussianDistributionDrawing extends JPanel {

    private GaussianDistribution _gaussianDistribution;
    private final double _from;
    private final double _to;

    public GaussianDistributionDrawing() {
        this( new GaussianDistribution( 0.5, 0.5 / 3 ) );

    }

    public GaussianDistributionDrawing( GaussianDistribution gaussianDistribution ) {
        this( gaussianDistribution, 0.0, 1.0 );

    }

    public GaussianDistributionDrawing( GaussianDistribution gaussianDistribution, double from, double to ) {

        _gaussianDistribution = gaussianDistribution;
        _from = from;
        _to = to;

        setMinimumSize( new Dimension( 100, 100 ) );

    }

    public void paint( Graphics g ) {

        Graphics2D g2d = (Graphics2D) g;

//        Logger.logMsg( "painting gaussian distribution " + _gaussianDistribution + " in (" + getWidth() + "," + getHeight() + ")" );

        g.setColor( getBackground() );
        g.fillRect( 0, 0, getWidth(), getHeight() );

        double maxY = _gaussianDistribution.getY( _gaussianDistribution.getCenter() );

        int height = getHeight();

        int x[] = new int[getWidth()], y[] = new int[getWidth()];

        for ( int pX = 0; pX < getWidth(); pX += 1 ) {

            double rX = mapXtoDrawing( pX, 0, getWidth() - 1, _from, _to );
            double rY = _gaussianDistribution.getY( rX );

//            Logger.logMsg( "pX = " + pX + ", rX = " + rX + ", rY = " + rY + ", scaled to " + ( rY / maxY ) );

            x[pX] = pX;
            y[pX] = (int)( height * ( 1.0 - rY / maxY ) );

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
        GaussianDistributionDrawing gdd = new GaussianDistributionDrawing();
        xx.setContentPane( gdd );
        xx.pack();
        xx.setVisible( true );

    }

    public void setDistribution( GaussianDistribution gaussianDistribution ) {

        _gaussianDistribution = gaussianDistribution;
        repaint();

    }

}
