package com.obtuse.util;

/*
 * Copyright © 2011 Invidi Technologies Corporation
 * Copyright © 2011 Obtuse Systems Corporation
 */

import com.obtuse.util.exceptions.ObtuseXmlNodeException;
import org.w3c.dom.Node;

/**
 * A gaussian distribution with a weighting factor.
 */

public class WeightedGaussianDistribution extends GaussianDistribution implements InstanceFromXML {

    private final double _weight;

    public WeightedGaussianDistribution( double weight, double center, double standardDeviation ) {

        super( center, standardDeviation );

        if ( weight < 0.0 ) {

            throw new IllegalArgumentException(
                    "negative weight (" + weight + ") not allowed in WeightedGaussianDistribution"
            );

        }

        _weight = weight;

    }

    public WeightedGaussianDistribution( MessageProxy messageProxy, Node parentNode, Node targetNode )
            throws ObtuseXmlNodeException {

        super(
                ObtuseXMLUtils.getMandatoryDoubleAttributeValue( targetNode, "center" ),
                ObtuseXMLUtils.getMandatoryDoubleAttributeValue( targetNode, "stddev" )
        );

        _weight = ObtuseXMLUtils.getMandatoryDoubleAttributeValue( targetNode, "weight" );

    }

    public double getWeight() {

        return _weight;

    }

    public void emitAsXml( NestedXMLPrinter ps ) {

        ps.emitTag(
                "WeightedGaussianDistribution",
                new String[] {
                        "weight=\"" + _weight + "\"",
                        "center=\"" + getCenter() + "\"",
                        "stddev=\"" + getStandardDeviation() + "\""
                }
        );

    }

    public String toString() {

        return "WeightedGaussianDistribution( " + _weight + ", " + getCenter() + ", " + getStandardDeviation() +
               " )";

    }

}
