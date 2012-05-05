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

    private static String s_supportEmailAddress = null;

    @SuppressWarnings({ "ClassWithoutToString", "AssignmentToStaticFieldFromInstanceMethod" })
    private static class ExampleArgParser extends ArgParser {

        private static String s_deploymentName = null;

        @SuppressWarnings("UnusedDeclaration")
        private static int s_messageIdPrefix = 0;

        @SuppressWarnings("UnusedDeclaration")
        private static Integer s_podNumber = null;

        private static int s_rmiRegistryPort = 0;

        private static int s_tracePort = -1;

        private static String s_callbackHostname = null;

        private ExampleArgParser() {
            //noinspection ClassWithoutToString
            super(
                    new Arg[] {

                            new ArgInt( "-messageIdPrefix" ) {

                                public void process( String keyword, int arg ) {

                                    ExampleArgParser.s_messageIdPrefix = arg;

                                }

                            },

                            new ArgString( "-deployment" ) {

                                public void process( String keyword, String arg ) {

                                    ExampleArgParser.s_deploymentName = arg;

                                }

                            },

                            new ArgInt( "-pod" ) {

                                public void process( String keyword, int arg ) {

                                    ExampleArgParser.s_podNumber = arg;

                                }

                            },

                            new ArgInt( "-traceport" ) {

                                public void process( String keyword, int arg ) {

                                    ExampleArgParser.s_tracePort = arg;

                                }

                            },

                            new ArgInt( "-rmiregistryport" ) {

                                public void process( String keyword, int arg ) {

                                    ExampleArgParser.s_rmiRegistryPort = arg;

                                }

                            },

                            new ArgString( "-supportEmailAddress" ) {

                                public void process( String keyword, String arg ) {

                                    ArgParserExample.s_supportEmailAddress = arg;

                                }

                            },

                            new ArgString( "-callbackHostname" ) {

                                public void process( String keyword, String arg ) {

                                    ExampleArgParser.s_callbackHostname = arg;

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

        if ( ArgParserExample.s_supportEmailAddress == null ) {

            Logger.logErr( "ArgParserExample:  -supportEmailAddress must be specified" );

        }

        if ( ExampleArgParser.s_callbackHostname == null ) {

            Logger.logErr( "ArgParserExample:  callback hostname must be specified" );

        }

        boolean fatalError = false;
        if ( ExampleArgParser.s_rmiRegistryPort <= 1023 ) {

            Logger.logErr( "ArgParserExample:  -rmiregistryport must be at least 1024" );
            fatalError = true;

        }

        if ( ExampleArgParser.s_tracePort == 0 ) {

            Logger.logErr( "ArgParserExample:  don't know which trace port to use" );
            fatalError = true;

        }

        if ( ExampleArgParser.s_deploymentName == null ) {

            Logger.logErr( "ArgParserExample:  -deployment option must be specified" );
            fatalError = true;

        }

        if ( fatalError ) {

            System.exit( 1 );

        }

        // That's it for parsing and checking args - the 'real work' now begins . . .

    }

}
