package com.obtuse.util;

import org.w3c.dom.Document;
import org.xml.sax.*;

import javax.xml.parsers.*;
import java.io.*;

/*
 * Copyright © 2012 Daniel Boulet
 */

public class XMLParsingExample {

    private static final String INPUT_FILENAME = ".idea/compiler.xml";

    private XMLParsingExample() {
        super();

    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "XML", "ParsingExample", null );

        XMLParsingExample.parseOnTheFly( XMLParsingExample.INPUT_FILENAME );

        XMLParsingExample.loadEntireDocument( XMLParsingExample.INPUT_FILENAME );

    }

    @SuppressWarnings("SameParameterValue")
    private static void parseOnTheFly( String inputFilename ) {

        SAXParserFactory spf = SAXParserFactory.newInstance();
        @SuppressWarnings("TooBroadScope")
        SAXParser parser;
        XMLReader xmlReader;
        try {

            parser = spf.newSAXParser();
            xmlReader = parser.getXMLReader();

        } catch ( ParserConfigurationException e ) {

            Logger.logErr( "parser configuration problem", e );
            System.exit( 1 );
            return;

        } catch ( SAXException e ) {

            Logger.logErr( "parser initialization problem", e );
            System.exit( 1 );
            return;

        }

        try {

            SimpleXmlContentHandler contentHandler = new SimpleXmlContentHandler( true );
            xmlReader.setContentHandler( contentHandler );
            xmlReader.parse(
                    new InputSource( new BufferedInputStream( new FileInputStream( inputFilename ) ) )
            );

        } catch ( SAXParseException e ) {

            Logger.logErr(
                    inputFilename + " (" + e.getLineNumber() + "," + e.getColumnNumber() + "):  " + e.getMessage()
            );
            System.exit( 1 );

        } catch ( SAXException e ) {

            Logger.logErr( "SAX exception caught parsing file", e );
            System.exit( 1 );

        } catch ( FileNotFoundException e ) {

            Logger.logErr( "file not found", e );
            System.exit( 1 );

        } catch ( IOException e ) {

            Logger.logErr( "I/O error", e );
            System.exit( 1 );

        }

    }

    @SuppressWarnings("SameParameterValue")
    private static void loadEntireDocument( String inputFilename ) {

        Document doc;
        try {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            doc = docBuilder.parse( new File( inputFilename ) );

        } catch ( ParserConfigurationException e ) {

            Logger.logErr( "Parser configuration error", e );
            System.exit( 1 );
            return;

        } catch ( SAXException e ) {

            Logger.logErr( "Parsing error", e );
            System.exit( 1 );
            return;

        } catch ( IOException e ) {

            Logger.logErr( "I/O error", e );
            System.exit( 1 );
            return;

        }

        // Clean things up.

        doc.getDocumentElement().normalize();

        Logger.logMsg( "root element is " + doc.getDocumentElement().getNodeName() );

    }

}
