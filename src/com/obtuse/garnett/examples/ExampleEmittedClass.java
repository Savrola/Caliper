package com.obtuse.garnett.examples;

/*
 * Copyright © 2011 Daniel Boulet.
 */

import com.obtuse.garnett.*;
import com.obtuse.garnett.exceptions.GarnettObjectVersionNotSupportedException;
import com.obtuse.garnett.exceptions.GarnettSerializationFailedException;
import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;

import java.io.*;

/**
 * This example explores what a Java variant of a user-designed class might look like.
 */

public class ExampleEmittedClass implements GarnettObject {

    public static final int BASE_CLASS_VERSION = 42;
    public static final GarnettTypeName BASE_CLASS_CANONICAL_NAME = new GarnettTypeName(
            ExampleEmittedClass.class.getCanonicalName()
    );

    public static class InheritedEmittedClass extends ExampleEmittedClass implements GarnettObject {

        public static final int INHERITED_CLASS_VERSION = 24;
        public static final GarnettTypeName INHERITED_CLASS_CANONICAL_NAME = new GarnettTypeName(
                InheritedEmittedClass.class.getCanonicalName()
        );

        private final String _stringValue;

        public InheritedEmittedClass( GarnettObjectInputStreamInterface gois )
                throws GarnettSerializationFailedException, IOException, GarnettObjectVersionNotSupportedException {
            super( gois );

            gois.checkVersion( INHERITED_CLASS_CANONICAL_NAME, INHERITED_CLASS_VERSION, INHERITED_CLASS_VERSION );

            _stringValue = gois.readOptionalString();

        }

        public InheritedEmittedClass( int intValue, String stringValue ) {
            super( intValue );

            _stringValue = stringValue;

        }

        public void serializeContents( GarnettObjectOutputStreamInterface boos )
                throws IOException {

            super.serializeContents( boos );

            boos.writeVersion( INHERITED_CLASS_VERSION );

            boos.writeOptionalString( _stringValue );

        }

        @SuppressWarnings({ "RefusedBequest" })
        @Override
        public GarnettTypeName getGarnettTypeName() {

            return INHERITED_CLASS_CANONICAL_NAME;

        }

        public String toString() {

            return super.toString() + " / \"" + _stringValue + "\"";

        }

    }

    private final int _finalInt;

    public ExampleEmittedClass( GarnettObjectInputStreamInterface gois )
            throws GarnettSerializationFailedException, IOException, GarnettObjectVersionNotSupportedException {
        super();

        gois.checkVersion( BASE_CLASS_CANONICAL_NAME, BASE_CLASS_VERSION, BASE_CLASS_VERSION );

        _finalInt = gois.readInt();

    }

    public ExampleEmittedClass( int intValue ) {
        super();

        _finalInt = intValue;

    }

    public GarnettTypeName getGarnettTypeName() {

        return BASE_CLASS_CANONICAL_NAME;

    }

    public void serializeContents( GarnettObjectOutputStreamInterface boos )
            throws IOException {

        boos.writeVersion( BASE_CLASS_VERSION );

        boos.writeInt( _finalInt );

    }

    public String toString() {

        return "" + _finalInt;

    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Garnett", "Test", null );
        String testFilename = "test.boos";

        testSerialization( testFilename );

        testDeserialization( testFilename );

    }

    private static void testDeserialization( String testFilename ) {

        GarnettObjectInputStream gois = null;
        try {

            gois = new GarnettObjectInputStream(
                    0,
                    new BufferedInputStream(
                            new FileInputStream( testFilename )
                    )
            );

            gois.getRestorerRegistry().addGarnettObjectFactory(
                    InheritedEmittedClass.BASE_CLASS_CANONICAL_NAME,
                    new GarnettObjectRestorerRegistry.GarnettObjectFactory() {

                        public GarnettObject instantiateInstance(
                                GarnettObjectInputStreamInterface
                                        garnettObjectInputStream
                        )
                                throws IOException {

                            return new ExampleEmittedClass( garnettObjectInputStream );

                        }

                    }
            );

            gois.getRestorerRegistry().addGarnettObjectFactory(
                    InheritedEmittedClass.INHERITED_CLASS_CANONICAL_NAME,
                    new GarnettObjectRestorerRegistry.GarnettObjectFactory() {

                        public GarnettObject instantiateInstance(
                                GarnettObjectInputStreamInterface
                                        garnettObjectInputStream
                        )
                                throws IOException {

                            return new InheritedEmittedClass( garnettObjectInputStream );

                        }

                    }
            );

            Integer opt42 = gois.readOptionalInteger();

            Logger.logMsg( "opt42 == " + opt42 );

            GarnettObject nullObject = gois.readOptionalGarnettObject();
            Logger.logMsg( "nullObject == " + nullObject );

            GarnettObject example1 = gois.readOptionalGarnettObject();
            Logger.logMsg( "example1 = " + example1 );

            GarnettObject example2 = gois.readOptionalGarnettObject();
            Logger.logMsg( "example2 = " + example2 );

            GarnettObject example3 = gois.readOptionalGarnettObject();
            Logger.logMsg( "example3 = " + example3 );

        } catch ( FileNotFoundException e ) {

            Logger.logErr( "unable to open \"" + testFilename + "\"", e );

        } catch ( IOException e ) {

            Logger.logErr( "unable to de-serialized object", e );

        } finally {

            try {

                if ( gois != null ) {

                    gois.close();

                }

            } catch ( IOException e ) {

                Logger.logErr( "close gois failed", e );

            }

        }

    }

    private static void testSerialization( String testFilename ) {

        GarnettObjectOutputStream boos = null;
        try {

            boos = new GarnettObjectOutputStream(
                    new FileOutputStream( testFilename )
            );

            boos.writeOptionalInteger( 42 );

            boos.writeOptionalGarnettObject( null );

            ExampleEmittedClass example1 = new InheritedEmittedClass( 1000, "hello world" );
            boos.writeOptionalGarnettObject( example1 );
            ExampleEmittedClass example2 = new ExampleEmittedClass( 12321 );
            boos.writeOptionalGarnettObject( example2 );
            boos.writeOptionalGarnettObject( example1 );

            Logger.logMsg( "example1 == " + example1 );
            Logger.logMsg( "example2 == " + example2 );

        } catch ( FileNotFoundException e ) {

            Logger.logErr( "unable to open \"" + testFilename + "\"", e );

        } catch ( IOException e ) {

            Logger.logErr( "unable to serialized object", e );

        } finally {

            try {

                if ( boos != null ) {

                    boos.close();
                    
                }

            } catch ( IOException e ) {

                Logger.logErr( "close boos failed", e );

            }

        }

    }

}
