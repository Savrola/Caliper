package com.obtuse.util;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Stack;

/*
 * Copyright © 2012 Daniel Boulet
 */

@SuppressWarnings("SameParameterValue")
public class SimpleXmlContentHandler extends DefaultHandler {

    private Locator _locator;

    private final Stack<String> _nesting = new Stack<String>();
    private final boolean _logActivity;

    public SimpleXmlContentHandler( boolean logActivity ) {
        super();

        _logActivity = logActivity;

    }

    public void setDocumentLocator( Locator locator ) {

        _locator = locator;

    }

    public void startDocument()
            throws SAXException {

        if ( _logActivity ) {

            Logger.logMsg( "start document" );

        }

    }

    public void endDocument()
            throws SAXException {

        if ( _logActivity ) {

            Logger.logMsg( "end document" );

        }

    }

    public void startElement( String uri, String localName, String qName, Attributes attributes )
            throws SAXException {

        _nesting.push( qName );

        if ( _logActivity  ) {

            Logger.logMsg(
                    "start element:  uri = \"" + uri +
                    "\", localName = \"" + localName +
                    "\", qName = \"" + qName +
                    "\", attributes = " + SimpleXmlContentHandler.formatAttributes( attributes )
            );

        }

    }

    public static String formatAttributes( Attributes attributes ) {

        if ( attributes.getLength() == 0 ) {

            return "{}";

        }

        StringBuilder sb = new StringBuilder();
        sb.append( "{ " );
        String comma = "";
        for ( int ix = 0; ix < attributes.getLength(); ix += 1 ) {

            sb
                    .append( comma )
                    .append( attributes.getQName( ix ) )
                    .append( "=\"" )
                    .append( attributes.getValue( ix ) )
                    .append( "\"" );

            comma = ", ";

        }
        sb.append( " }" );

        return sb.toString();

    }

    public void endElement( String uri, String localName, String qName )
            throws SAXException {

        if ( _nesting.peek().equals( qName ) ) {

            if ( _logActivity ) {

                Logger.logMsg(
                        "end element:  uri = \"" + uri +
                        "\", localName = \"" + localName +
                        "\", qName = \"" + qName + "\""
                );

            }

            _nesting.pop();

        } else {

            throw new SAXParseException(
                    "structural problem:  expected to close \"" + _nesting.peek() + "\" but got \"" + qName + "\"",
                    _locator
            );

        }

    }

    public void characters( char[] chars, int start, int length )
            throws SAXException {

        if ( _logActivity ) {

            Logger.logMsg( "characters:  \"" + new String( chars, start, length ) + "\"" );

        }

    }

    public void ignorableWhitespace( char[] chars, int start, int length )
            throws SAXException {

        if ( _logActivity ) {

            Logger.logMsg( "ignorable whitespace:  \"" + new String( chars, start, length ) + "\"" );

        }

    }

    public void processingInstruction( String s, String s1 )
            throws SAXException {

    }

    public void skippedEntity( String s )
            throws SAXException {

    }

}
