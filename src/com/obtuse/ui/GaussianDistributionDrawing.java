package com.obtuse.ui;

import com.obtuse.util.GaussianDistribution;
import com.obtuse.util.WeightedGaussianDistribution;

/**
 * Draw a single gaussian distribution.
 */

public class GaussianDistributionDrawing extends StackedGaussianDistributionsDrawing {

    public GaussianDistributionDrawing() {

        super();

    }

    public GaussianDistributionDrawing( GaussianDistribution gd ) {

        this( gd, 0.0, 1.0 );

    }

    @SuppressWarnings("SameParameterValue")
    public GaussianDistributionDrawing(
            GaussianDistribution gd,
            double from,
            double to
    ) {

        super(
                new WeightedGaussianDistribution[] {
                        new WeightedGaussianDistribution(
                                1,
                                gd.getCenter(),
                                gd.getStandardDeviation()
                        )
                },
                from,
                to
        );

    }

    @SuppressWarnings("UnusedDeclaration")
    public void setDistribution( GaussianDistribution gd ) {

        setDistributions(
                new WeightedGaussianDistribution[] {
                        new WeightedGaussianDistribution(
                                1,
                                gd.getCenter(),
                                gd.getStandardDeviation()
                        )
                }
        );

    }

}
