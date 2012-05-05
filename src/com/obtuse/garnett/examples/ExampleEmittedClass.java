package com.obtuse.garnett.examples;

/*
 * Copyright © 2011 Daniel Boulet.
 */

import com.obtuse.garnett.*;
import com.obtuse.garnett.exceptions.*;
import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;

import java.io.*;

/**
 * This example explores what a Java variant of a user-designed class might look like.
 */

@SuppressWarnings("MagicNumber")
public class ExampleEmittedClass implements GarnettObject {

    public static final int BASE_CLASS_VERSION = 42;

    public static class InheritedEmittedClass extends ExampleEmittedClass {

        public static final int INHERITED_CLASS_VERSION = 24;

        private final String _stringValue;

        @SuppressWarnings("DuplicateThrows")
        public InheritedEmittedClass( GarnettObjectInputStreamInterface gois )
                throws GarnettSerializationFailedException, IOException, GarnettObjectVersionNotSupportedException {
            super( gois );

            gois.checkVersion(
                    InheritedEmittedClass.class,
                    InheritedEmittedClass.INHERITED_CLASS_VERSION,
                    InheritedEmittedClass.INHERITED_CLASS_VERSION
            );

            _stringValue = gois.readOptionalString();

        }

        public InheritedEmittedClass( int intValue, String stringValue ) {
            super( intValue );

            _stringValue = stringValue;

        }

        public void serializeContents( GarnettObjectOutputStreamInterface goos )
                throws IOException {

            super.serializeContents( goos );

            goos.writeVersion( InheritedEmittedClass.INHERITED_CLASS_VERSION );

            goos.writeOptionalString( _stringValue );

        }

        @SuppressWarnings({ "RefusedBequest" })
        @Override
        public GarnettTypeName getGarnettTypeName() {

            return new GarnettTypeName( InheritedEmittedClass.class.getCanonicalName() );

        }

        public String toString() {

            return super.toString() + " / \"" + _stringValue + "\"";

        }

    }

    private final int _finalInt;

    @SuppressWarnings("DuplicateThrows")
    public ExampleEmittedClass( GarnettObjectInputStreamInterface gois )
            throws GarnettSerializationFailedException, IOException, GarnettObjectVersionNotSupportedException {
        super();

        gois.checkVersion(
                ExampleEmittedClass.class,
                ExampleEmittedClass.BASE_CLASS_VERSION,
                ExampleEmittedClass.BASE_CLASS_VERSION
        );

        _finalInt = gois.readInt();

    }

    public ExampleEmittedClass( int intValue ) {
        super();

        _finalInt = intValue;

    }

    public GarnettTypeName getGarnettTypeName() {

        return new GarnettTypeName( ExampleEmittedClass.class.getCanonicalName() );

    }

    public void serializeContents( GarnettObjectOutputStreamInterface goos )
            throws IOException {

        goos.writeVersion( ExampleEmittedClass.BASE_CLASS_VERSION );

        goos.writeInt( _finalInt );

    }

    public String toString() {

        return "" + _finalInt;

    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Garnett", "Test", null );
        String testFilename = "test.goos";

        ExampleEmittedClass.testSerialization( testFilename );

        ExampleEmittedClass.testDeserialization( testFilename );

    }

    private static void testDeserialization( String testFilename ) {

        BufferedInputStream inStream;
        try {

            inStream = new BufferedInputStream(
                    new FileInputStream( testFilename )
            );

        } catch ( FileNotFoundException e ) {

            Logger.logErr( testFilename + ":  unable to open test input file", e );

            return;

        }

        final GarnettObjectInputStream gois;
        try {

            gois = new GarnettObjectInputStream(
                    0,
                    inStream
            );

        } catch ( IOException e ) {

            Logger.logErr( testFilename + ":  unable to create GOIS", e );

            return;

        } catch ( GarnettUnsupportedProtocolVersionException e ) {

            Logger.logErr( testFilename + ":  unsupported or bogus Garnett protocol version", e );

            return;

        } catch ( GarnettIllegalArgumentException e ) {

            Logger.logErr( testFilename + ":  illegal argument to Garnett method", e );

            return;

        }

        try {

            gois.getRestorerRegistry().addGarnettObjectFactory(
                    new GarnettTypeName( ExampleEmittedClass.class.getCanonicalName() ),
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
                    new GarnettTypeName( InheritedEmittedClass.class.getCanonicalName() ),
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

        } catch ( IOException e ) {

            Logger.logErr( "unable to de-serialized object", e );

        } finally {

            try {

                gois.close();

            } catch ( IOException e ) {

                Logger.logErr( "close gois failed", e );

            }

        }

    }

    private static void testSerialization( String testFilename ) {

        GarnettObjectOutputStream goos = null;
        try {

            goos = new GarnettObjectOutputStream(
                    new FileOutputStream( testFilename ),
                    new GarnettSessionPrefix(
                            new GarnettComponentInstanceName( "testing" ),
                            0
                    )
            );

            goos.writeOptionalInteger( 42 );

            goos.writeOptionalGarnettObject( null );

            ExampleEmittedClass example1 = new InheritedEmittedClass( 1000, "hello world" );
            goos.writeOptionalGarnettObject( example1 );
            ExampleEmittedClass example2 = new ExampleEmittedClass( 12321 );
            goos.writeOptionalGarnettObject( example2 );
            goos.writeOptionalGarnettObject( example1 );

            Logger.logMsg( "example1 == " + example1 );
            Logger.logMsg( "example2 == " + example2 );

        } catch ( GarnettIllegalArgumentException e ) {

            Logger.logErr( "unable to build a GSP", e );

        } catch ( FileNotFoundException e ) {

            Logger.logErr( "unable to open \"" + testFilename + "\"", e );

        } catch ( IOException e ) {

            Logger.logErr( "unable to serialized object", e );

        } finally {

            try {

                if ( goos != null ) {

                    goos.close();
                    
                }

            } catch ( IOException e ) {

                Logger.logErr( "close goos failed", e );

            }

        }

    }

}
