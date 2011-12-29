package com.obtuse.util;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPOutputStream;

/**
 * A trace facility.
 * <p/>
 * Copyright © 2006, 2007 Loa Corporation.
 * Copyright © 2011 Daniel Boulet.
 */

public class Trace {

    private static final DateFormat _logFileNameFormatter =
            new SimpleDateFormat( "'trace_'yyyy-MM-dd_HH.mm.ss.SSS'.txt'" );

    public static final DateFormat OUR_DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS" );

    public static final DateFormat YYMMDD_HHMMSS_FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

    private static final Map<Integer, TraceHook> _traceHooks = new TreeMap<Integer, TraceHook>();

    private static final String TRACE_HOOKS_LOCK = new String( "trace hooks lock" );

    private static int _nextHookId = 0;

    private static boolean _liveTrace = false;

    private static String _programName;

    private static File _traceFileDirectory = new File( BasicProgramConfigInfo.getWorkingDirectory(), "traces" );

    public static Map<Long, Thread> _exceptionsInProgress = new TreeMap<Long, Thread>();

    public static final int MAX_FORMATTED_TRACE_DEPTH = 100;

    private static Tracer _tracer = null;

    public static void setTracer( Tracer tracer ) {

        _tracer = tracer;

    }

    static {

        //noinspection ClassWithoutToString
        Tracer tracer = new Tracer() {

            public void event( String description ) {
                Trace.event( description );
            }

            public void event( String description, Throwable e ) {
                Trace.event( description, e );
            }

            public String emitTrace( String description ) {
                return Trace.emitTrace( description );
            }

            public String emitTrace( String description, Throwable e ) {
                return Trace.emitTrace( description, e );
            }

            public String emitTrace( Throwable e ) {
                return Trace.emitTrace( e );
            }

        };

        setTracer( tracer );

    }

    private static class TraceEvent {

        private String _event;

        private long _timestamp;

        private long _tid;

        private Throwable _exception;

        private TraceEvent( String event ) {
            super();

            _event = event;
            _timestamp = System.currentTimeMillis();
            _tid = Thread.currentThread().getId();

        }

        private TraceEvent( String why, Throwable e ) {
            this( why + ( e == null ? "" : " (associated exception:  " + e + ")" ) );

            _exception = e;

        }

        @SuppressWarnings( { "UnusedDeclaration" } )
        private String getEvent() {

            return _event;

        }

        @SuppressWarnings( { "UnusedDeclaration" } )
        private long getTimeStamp() {

            return _timestamp;

        }

        private Throwable getException() {

            return _exception;

        }

        private void emit( List<String> results ) {

            String pfx = OUR_DATE_FORMAT.format( _timestamp ) + " {" + _tid + "}:  ";

            results.add( pfx + _event );

            if ( _exception != null ) {

                captureStackTrace( false, pfx, _exception, results );

            }

        }

        public String toString() {

            String msg = OUR_DATE_FORMAT.format( _timestamp ) + " {" + _tid + "}:  " + _event;

            if ( _exception != null ) {

                List<String> results = new LinkedList<String>();
                captureStackTrace( false, "", _exception, results );
                for ( String s : results ) {

                    msg += "\n" + s;

                }

            }

            return msg;

        }

    }

    private static Queue<TraceEvent> _traceEvents = new LinkedList<TraceEvent>();

    private static List<TraceFileManager> _traceFileManagers = new LinkedList<TraceFileManager>();

    private static final int MAX_TRACE_EVENTS = 25000;

    private Trace() {

        super();

    }

    public static void setLiveTrace( boolean value ) {

        _liveTrace = value;

    }

    public static void register( TraceFileManager traceFileManager ) {

        synchronized ( _traceFileManagers ) {

            _traceFileManagers.add( traceFileManager );

        }

    }

    public static void setProgramName( String programName ) {

        _programName = programName;

    }

    /**
     * Add a trace hook which is to be invoked whenever a trace report is requested (i.e. whenever emitResults is
     * called).
     *
     * @param hook the hook.
     *
     * @return the unique id number of the hook (if the same hook is added more than once, each 'addition' of the hook
     *         will get a different id number).
     */

    public static int addTraceHook( TraceHook hook ) {

        synchronized ( TRACE_HOOKS_LOCK ) {

            _nextHookId += 1;
            _traceHooks.put( _nextHookId - 1, hook );
            return _nextHookId - 1;

        }

    }

    /**
     * Remove a trace hook. Attempts to remove hooks which don't exist are silently ignored (it seems foolish to add yet
     * new ways to crash an application in a facility which is intended to find bugs and within which deleting the wrong
     * hook id is hardly the end of the world).
     *
     * @param id the id number of the hook to be deleted.
     */

    public static void removeTraceHook( int id ) {

        synchronized ( TRACE_HOOKS_LOCK ) {

            _traceHooks.remove( id );

        }

    }

    public TraceHook getHook( int id ) {

        return _traceHooks.get( id );

    }

    public static void event( String event ) {

        event( event, null );

    }

    public static void event( Throwable e ) {

        event( "unexpected exception", e );

    }

    public static void event( String event, Throwable e ) {

        Long threadId = Thread.currentThread().getId();

        synchronized ( _exceptionsInProgress ) {

            // Avoid recursion

            if ( _exceptionsInProgress.containsKey( threadId ) ) {

                return;

            }

        }

        try {

            if ( _liveTrace ) {
                System.out.println( "<><> " + event );
                if ( e != null ) {
                    e.printStackTrace();
                }
            }

            synchronized ( TRACE_HOOKS_LOCK ) {

                _traceEvents.add( new TraceEvent( event, e ) );

                if ( _traceEvents.size() > MAX_TRACE_EVENTS ) {

                    _traceEvents.remove();

                }

            }

        } finally {

            synchronized ( _exceptionsInProgress ) {

                _exceptionsInProgress.remove( threadId );

            }

        }

    }

    public static List<String> getTrace( String why, String where ) {

        // Sitting on the trace hooks lock while collecting trace data risks a deadlock
        // (e.g. if some other thread tries to record a trace event) so we hold the
        // lock only long enough to collect copies of the raw trace data and hooks.

        List<TraceEvent> events;
        Map<Integer, TraceHook> hooks;
        synchronized ( TRACE_HOOKS_LOCK ) {

            events = new LinkedList<TraceEvent>();
            for ( TraceEvent event : _traceEvents ) {
                events.add( event );
            }

            hooks = new TreeMap<Integer, TraceHook>();
            for ( int hookId : _traceHooks.keySet() ) {
                hooks.put( hookId, _traceHooks.get( hookId ) );
            }

        }

        List<String> results = new LinkedList<String>();
        try {

            String what = "Trace requested at " + new Date() +
                          ( _programName == null ? "" : " for " + _programName ) +
                          ( why == null ? "" : " (" + why + ")" );

            results.add( what );
            results.add( "IMPORTANT:  email this to danny@savrola.com (you should find a copy in \"" + where + "\")" );

            ThreadMXBean bean = ManagementFactory.getThreadMXBean();
            long[] curThreads = bean.getAllThreadIds();

            //noinspection MagicNumber
            ThreadInfo[] thInfos = bean.getThreadInfo( curThreads, 50 );
            for ( ThreadInfo info : thInfos ) {

                if ( info != null ) {

                    results.add(
                            "{" + info.getThreadId() + "}:" +
                            info.getThreadName() +
                            " (state = " + info.getThreadState() +
// Java on Mac OS X sometimes crashes if the next line is uncommented!
//                                 ", cpu = " + formatCpuTime(bean.getThreadCpuTime(info.getThreadId()) ) +
                            ")"
                    );

                    String lock = info.getLockName();
                    if ( lock != null ) {

                        String lockOwnerName = info.getLockOwnerName();
                        long lockOwnerId = info.getLockOwnerId();

                        results.add(
                                "    thread is waiting for \"" + lock + "\" which is currently held by {" +
                                lockOwnerId + "}:" + lockOwnerName
                        );

                    }

                    StackTraceElement[] stack = info.getStackTrace();
                    for ( StackTraceElement element : stack ) {

                        results.add(
                                "    at " + element.getClassName() + "." + element.getMethodName() + "(" +
                                element.getFileName() + ":" + element.getLineNumber() + ")"
                        );

                    }

                    results.add( "" );
                }

            }

            results.add( "" );
            results.add( "trace events:" );

            for ( TraceEvent event : events ) {

                event.emit( results );
//                results.add( "    " + event );

            }

            // Process the hooks in the order that they were defined.

            if ( hooks.size() > 0 ) {

                results.add( "" );
                results.add( "trace hooks:" );

                // System.out.println("trace hooks");

                for ( int hookId : hooks.keySet() ) {

                    results.add( "" );
                    TraceHook hook = hooks.get( hookId );
                    System.out.println( "doing hook " + hook );
                    List<String> hookResults = hook.run();

                    for ( String line : hookResults ) {

                        results.add( line );

                    }

                }

            }

        } catch ( Throwable e ) {

            results.add( "*** unable to collect trace data due to exception:  " + e.getMessage() );

        }

        // System.out.println("trace data collected");

        return results;

    }

    public static String emitTrace( String why ) {

        String where = _logFileNameFormatter.format( System.currentTimeMillis() );
        System.out.println( "where string built" );
        List<String> results = getTrace( why, where );
        System.out.println( "results collected" );

        String rval = emitResults( why, results, where, true );
        System.out.println( "file emitted" );

        ObtuseUtil5.safeSleepMillis( javax.management.timer.Timer.ONE_SECOND * 5L );
        return rval;

    }

    public static String emitTrace( Throwable e ) {

        return emitTrace( "unexpected exception", e );

    }

    public static String emitTrace( String why, Throwable e ) {

        if ( e == null ) {

            return emitTrace( why );

        } else {

            String where = _logFileNameFormatter.format( System.currentTimeMillis() );
            String exceptionDescription = e.getMessage();
            if ( exceptionDescription == null ) {

                exceptionDescription = e.toString();

            }
            String longWhy = ( why == null ? "" : why + " / " ) + exceptionDescription;
            List<String> results = getTrace(
                    longWhy,
                    where
            );

            captureStackTrace( true, "", e, results );

            return emitResults( longWhy, results, where, true );

        }

    }

    private static void captureStackTrace( boolean bothStyles, String pfx, Throwable e, List<String> results ) {

        if ( bothStyles ) {

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter( sw );
            e.printStackTrace( pw );
            pw.flush();
            results.add( pfx );
            results.add( pfx + "Exception stack traceback:" );
            results.add( pfx + sw.toString() );  // FINALLY we can put the stack trace into our results list
            pw.close();
            ObtuseUtil5.closeQuietly( sw );
            results.add( pfx );
            results.add( pfx + "same stack traceback using new formatter:" );

        } else {

            results.add( pfx + "Exception stack traceback (using new formatter):" );

        }

        String[] trace = formatDeeperStackTrace( e );
        for ( String line : trace ) {

            results.add( pfx + line );

        }

    }

    private static String emitResults( String why, List<String> results, String where, boolean compressOutput ) {

        final long timeStamp = System.currentTimeMillis();

        String traceFname = where;
        PrintWriter writer = null;

        try {

            _traceFileDirectory.mkdirs();

            File traceFile;
            if ( compressOutput ) {

                traceFname += ".gz";
                traceFile = new File( _traceFileDirectory, traceFname );
                writer = new PrintWriter( new GZIPOutputStream( new FileOutputStream( traceFile ) ) );

            } else {

                traceFile = new File( _traceFileDirectory, traceFname );
                writer = new PrintWriter( new FileOutputStream( new File( _traceFileDirectory, traceFname ) ) );

            }

            traceFname = traceFile.getPath();

            for ( String s : results ) {
                writer.println( s );
            }

        } catch ( FileNotFoundException e ) {

            Logger.logErr( "unable to open trace file \"" + traceFname + "\"", e );

        } catch ( IOException e ) {

            Logger.logErr( "I/O error writing trace file \"" + traceFname + "\"", e );

        } finally {

            writer.close();

        }

        // Report the trace via the friendly logger.

        Logger.logMsg( "A trace file has been captured (" + why + ")", null );

        tellTraceFileManagers( traceFname, timeStamp );

        return traceFname;

    }

    private static void tellTraceFileManagers( final String traceFname, final long timeStamp ) {

        //noinspection ClassWithoutToString,RefusedBequest
        new Thread() {

            public void run() {

                synchronized ( _traceFileManagers ) {

                    boolean handled = false;
                    for ( TraceFileManager tfm : _traceFileManagers ) {

                        tfm.newTraceFile( traceFname, timeStamp );
                        handled = true;

                    }

                    if ( !handled ) {

                        Logger.logMsg(
                                "please email \"" + traceFname + "\" to danny@loapowertools.com", null
                        );

                    }
                }

            }

        }.start();

    }

    public static void startTracePortListener( int port ) {

        try {

            final ServerSocket listenSocket = new ServerSocket();

            listenSocket.setReuseAddress( true );
            listenSocket.bind(
                    new InetSocketAddress(
                            "127.0.0.1",
                            port
                    )
            );
            Logger.logMsg( "listening for a trace request on port " + listenSocket.getLocalPort() );

            //noinspection RefusedBequest,ClassWithoutToString
            new Thread( "trace port listener" ) {

                public void run() {

                    while ( true ) {
                        try {

                            Socket sock = listenSocket.accept();
                            Logger.logErr( "trace requested" );
                            emitTrace( "externally requested" );
                            Logger.logErr( "trace done" );
                            ObtuseUtil5.closeQuietly( sock );

                        } catch ( Throwable e ) {

                            Logger.logErr(
                                    "trace port listener caught an exception/error - thread terminating after one more trace",
                                    e
                            );
                            emitTrace( "trace port listener failed", e );

                            ObtuseUtil5.closeQuietly( listenSocket );

                            return;

                        }

                    }

                }

            }.start();

        } catch ( SocketException e ) {

            Trace.emitTrace( "trace port listener unable to allocate listen socket on port " + port, e );
            System.exit( 1 );

        } catch ( IOException e ) {

            Trace.emitTrace( "trace port listener unable to allocate listen socket on port " + port, e );
            System.exit( 1 );

        }

    }

    public String toString() {

        return "Trace for " + _programName;

    }

    public static void setDirectory( File tmpLoaPostDir ) {

        if ( tmpLoaPostDir != null ) {

            _traceFileDirectory = new File( tmpLoaPostDir, "traces" );

        }

    }

    public static void appendStackTrace( Collection<String> trace, Throwable e ) {

        StackTraceElement[] stack = e.getStackTrace();
        for ( int i = 0; i < stack.length && i < MAX_FORMATTED_TRACE_DEPTH; i += 1 ) {

            StackTraceElement element = stack[i];
            String source;
            if ( element.isNativeMethod() ) {

                source = "Native Method";

            } else if ( element.getFileName() == null ) {

                source = "Unknown Source";

            } else {

                source = element.getFileName() + ":" + element.getLineNumber();

            }

            trace.add( "\tat " + element.getClassName() + "." + element.getMethodName() + "(" + source + ")" );

        }

        if ( stack.length > MAX_FORMATTED_TRACE_DEPTH ) {

            trace.add( "\t... " + ( stack.length - MAX_FORMATTED_TRACE_DEPTH ) + " more" );

        }

    }

    public static String[] formatDeeperStackTrace( Throwable e ) {

        Vector<String> trace = new Vector<String>();

        if ( e.getCause() == null ) {

            trace.add( "" + e.getClass().getName() + ": " + e.getLocalizedMessage() );
            appendStackTrace( trace, e );

        } else {

            trace.add( "" + e.getClass().getName() + ": " + e.getLocalizedMessage() );
//            trace.add( "\t" + e.getClass().getName() + ":" + e.getLocalizedMessage() );
            appendStackTrace( trace, e );
            trace.add( "Caused by: " + e.getCause().getClass().getName() + ": " + e.getCause().getLocalizedMessage() );
            appendStackTrace( trace, e.getCause() );

        }

        return trace.toArray( new String[ trace.size() ] );

    }

}