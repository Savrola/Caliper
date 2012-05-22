package com.obtuse.util.exceptions;

/*
 * Copyright © 2012 Daniel Boulet
 */

public class ObtuseXmlNodeException extends Exception {

    private Integer _elementIndex = null;

    public ObtuseXmlNodeException() {
        super();

    }

    public ObtuseXmlNodeException( String msg ) {
        super(msg);

    }

    public ObtuseXmlNodeException( String msg, Throwable cause ) {
        super( msg, cause );

    }

    public ObtuseXmlNodeException( String msg, int elementIndex ) {
        super( msg );

        _elementIndex = elementIndex;

    }

    public ObtuseXmlNodeException( String msg, int elementIndex, Throwable cause ) {
        super( msg, cause );

        _elementIndex = elementIndex;

    }

    public boolean isElementIndexSet() {

        return _elementIndex != null;

    }

    public int getElementIndex() {

        return _elementIndex.intValue();

    }

}
