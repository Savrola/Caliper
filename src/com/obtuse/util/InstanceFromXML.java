package com.obtuse.util;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * Mark something that can be serialized to XML format and deserialized from XML format.
 */

public interface InstanceFromXML {

    /**
     * Serialize this instance to XML.
     * @param ps where to send things to.
     */

    void emitAsXml( NestedXMLPrinter ps );

}
