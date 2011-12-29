package com.obtuse.util;

/**
 * Demonstrate how to use the arg parsing classes.
 * <p/>
 * One possible invocation of this program would be:
 * <blockquote>
 *     java com.obtuse.util.ArgParserExample -messageIdPrefix 123 -deployment fred -pod 3 -traceport 4324 -rmiregistryport 4535 -supportEmailAddress help@example.com -callbackHostname critter.example.com
 * </blockquote>
 * <p/>
 * Copyright © 2011 Obtuse Systems Corporation.
 */

public class ArgParserExample {

    private static String _supportEmailAddress;

    @SuppressWarnings( { "ClassWithoutToString" })
    private static class ExampleArgParser extends ArgParser {

        private static String _deploymentName;

        private static int _messageIdPrefix = 0;

        private static Integer _podNumber;

        private static int _rmiRegistryPort = 0;

        private static int _tracePort = -1;

        private static String _callbackHostname = null;

        private ExampleArgParser() {
            //noinspection ClassWithoutToString
            super(
                    new Arg[] {
                            new ArgInt( "-messageIdPrefix" ) {
                                public void process( String keyword, int arg ) {
                                    _messageIdPrefix = arg;
                                }
                            },
                            new ArgString( "-deployment" ) {
                                public void process( String keyword, String arg ) {
                                    _deploymentName = arg;
                                }
                            },
                            new ArgInt( "-pod" ) {
                                public void process( String keyword, int arg ) {
                                    _podNumber = arg;
                                }
                            },
                            new ArgInt( "-traceport" ) {
                                public void process( String keyword, int arg ) {
                                    _tracePort = arg;
                                }
                            },
                            new ArgInt( "-rmiregistryport" ) {
                                public void process( String keyword, int arg ) {
                                    _rmiRegistryPort = arg;
                                }
                            },
                            new ArgString( "-supportEmailAddress" ) {
                                public void process( String keyword, String arg ) {
                                    _supportEmailAddress = arg;
                                }
                            },
                            new ArgString( "-callbackHostname" ) {
                                public void process( String keyword, String arg ) {
                                    _callbackHostname = arg;
                                }
                            }
                    }
            );

        }

    }

    private ArgParserExample() {
        super();

    }

    @SuppressWarnings( { "MagicNumber" })
    public static void main( String[] args ) {

        //noinspection ClassWithoutToString,ClassWithoutToString
        ExampleArgParser argParser = new ExampleArgParser();

        if ( !argParser.parse( args ) ) {

            System.exit( 1 );

        }

        if ( _supportEmailAddress == null ) {

            Logger.logErr( "ArgParserExample:  -supportEmailAddress must be specified" );

        }

        if ( ExampleArgParser._callbackHostname == null ) {

            Logger.logErr( "ArgParserExample:  callback hostname must be specified" );

        }

        boolean fatalError = false;
        if ( ExampleArgParser._rmiRegistryPort <= 1023 ) {

            Logger.logErr( "ArgParserExample:  -rmiregistryport must be at least 1024" );
            fatalError = true;

        }

        if ( ExampleArgParser._tracePort == 0 ) {

            Logger.logErr( "ArgParserExample:  don't know which trace port to use" );
            fatalError = true;

        }

        if ( ExampleArgParser._deploymentName == null ) {

            Logger.logErr( "ArgParserExample:  -deployment option must be specified" );
            fatalError = true;

        }

        if ( fatalError ) {

            System.exit( 1 );

        }

        // That's it for parsing and checking args - the 'real work' now begins . . .

    }

}
