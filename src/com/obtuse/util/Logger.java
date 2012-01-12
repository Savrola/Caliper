package com.obtuse.util;

import java.io.*;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * Copyright © 2005, 2006, 2007 Loa Corporation.
 * Copyright © 2011 Daniel Boulet.
 */

/**
 * Manage a simple logging facility.
 * <p/>
 * Note that {@link BasicProgramConfigInfo#init} <b><u>MUST</u></b> be called before this class is used in any way which triggers
 * the invocation of this class's static initializer(s).  Experience seems to indicate that it is sufficient to call
 * {@link BasicProgramConfigInfo#init} before invoking any method defined by this class (your mileage may vary).
 *
 * @noinspection ClassWithoutToString, ForLoopReplaceableByForEach, RawUseOfParameterizedType, UseOfSystemOutOrSystemErr, UnusedDeclaration
 */

public class Logger {

    private List<LoggerListener> _listeners = new LinkedList<LoggerListener>();

    private StringBuffer _currentMessage = new StringBuffer();

    private File _outputFile;

    private String _outputFileName;

    private PrintStream _outputStream;

    private Date _messageStartTime;

    private PrintStream _mirror;     // If non-null, all messages sent to this logger are also sent here.

    private static Logger _stdout = null;

    private static Logger _stderr = null;

    private static Logger _friendly = null;

    private static final DateFormat OUR_DATE_FORMAT;

    private static final String COMPONENT_NAME;

    public static final File LOGS_DIRECTORY;

    private static final DateFormat LOG_FILE_NAME_FORMATTER;
    private static String _programName = null;

    static {

        if ( BasicProgramConfigInfo.getComponentName() == null ) {

            COMPONENT_NAME = BasicProgramConfigInfo.getApplicationName();

        } else {

            COMPONENT_NAME = BasicProgramConfigInfo.getComponentName();

        }

        if ( BasicProgramConfigInfo.getDateFormat() == null ) {

            OUR_DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS Z" );

        } else {

            OUR_DATE_FORMAT = BasicProgramConfigInfo.getDateFormat();

        }

        if ( BasicProgramConfigInfo.getLogFileNameFormat() == null ) {

            LOG_FILE_NAME_FORMATTER = new SimpleDateFormat( "yyyy-MM-dd_HH.mm.ss.SSS'.txt'" );

        } else {

            LOG_FILE_NAME_FORMATTER = new SimpleDateFormat( BasicProgramConfigInfo.getLogFileNameFormat() );

        }

        LOGS_DIRECTORY = new File( BasicProgramConfigInfo.getWorkingDirectory(), "logs" );

    }

    public Logger( File outputFile, boolean append )
            throws
            FileNotFoundException {
        super();

        _outputFile = outputFile;
        _outputStream = new PrintStream( new FileOutputStream( outputFile, append ), true );

    }

    public Logger( String outputFileName, PrintStream outputStream ) {
        super();

        _outputFileName = outputFileName;
        _outputStream = outputStream;

    }

    /**
     * Close this logger and set our mirror to null (this has the side-effect of closing our mirror if it is open and
     * neither {@link System#out} nor {@link System#err}).
     */

    public synchronized void close() {

        // Make sure we don't accidentally close stdout or stderr.
        // Note:  use of != instead of equals() is deliberate!

        //noinspection ObjectEquality
        if ( _outputStream != System.out && _outputStream != System.err && _outputStream != null ) {

            _outputStream.close();

        }

        //noinspection NullableProblems
        internalSetMirror( null, null );
    }

    /**
     * Set this logger's mirror to the specified {@link java.io.PrintStream}. Any existing mirror is closed if it is open and
     * neither {@link System#out} nor {@link System#err}).
     *
     * @param mirrorName the name of the mirror file/device/whatever.
     * @param mirror the PrintStream which is to be sent a copy of everything which is emitted by this Logger.
     */

    private void internalSetMirror( String mirrorName, PrintStream mirror ) {

        // Make sure we don't accidentally close stdout or stderr.
        // Note:  use of != instead of equals() is deliberate!

        //noinspection ObjectEquality
        if ( _mirror != null && _mirror != System.out && _mirror != System.err ) {

            println( "\n%%% mirror file closed" );
            _mirror.close();

        }

        _mirror = mirror;
        if ( _mirror != null ) {

            if ( mirrorName == null ) {

                println( "%%% mirror file (re)opened" );

            } else {

                println( "%%% mirror file \"" + mirrorName + "\" (re)opened" );

            }

        }

    }

    public void setMirror( String mirrorFilename, long key )
            throws
            FileNotFoundException {

        internalSetMirror( mirrorFilename, new PrintStream( new FileOutputStream( mirrorFilename, true ), true ) );

    }

    public static String formatTOD( Date when ) {

        return OUR_DATE_FORMAT.format( when );

    }

    private void printSegment( String s ) {

        if ( _messageStartTime == null ) {

            _messageStartTime = new Date();
        }

        _currentMessage.append( s );

    }

    private void printNewline() {

        if ( _messageStartTime == null ) {

            _messageStartTime = new Date();

        }

        String formattedMessageStartTime = OUR_DATE_FORMAT.format( _messageStartTime );
        if ( _outputStream != null ) {

            //noinspection UnnecessaryParentheses
            _outputStream.println(
                    MessageFormat.format(
                            "{0}:  {1}",
                            formattedMessageStartTime,
                            _currentMessage.toString()
                    )
            );

        }

        if ( _mirror != null ) {

            _mirror.println( formattedMessageStartTime + ":  " + _currentMessage.toString() );

        }

        flush();

        // Make a copy of our listeners list from within a synchronized block.
        // Use this list outside the block to call all the listeners.
        // While somewhat awkward, this ensures that the list of listeners does not change
        // while we're sending the message to them without having to call the listeners
        // from within a synchronized block (which just seems ugly and risks deadlocks).

        List<LoggerListener> tmpListeners;

        synchronized ( this ) {

            tmpListeners = new LinkedList<LoggerListener>( _listeners );

        }

        Trace.event( "processing listeners" );

        for ( Iterator iter = tmpListeners.iterator(); iter.hasNext(); ) {

            LoggerListener listener = (LoggerListener)iter.next();
            listener.logMessage( _messageStartTime, _currentMessage.toString() );

        }

        Trace.event( "done processing listeners" );

        _currentMessage = new StringBuffer();
        _messageStartTime = null;

    }

    public synchronized void print( String Xs ) {

        //noinspection UnnecessaryLocalVariable
        String s = Xs;
        int last = 0;
        int ix;

        while ( ( ix = s.indexOf( (int)'\n', last ) ) >= last ) {

            String nextSection = s.substring( last, ix );
            printSegment( nextSection );
            printNewline();
            last = ix + 1;

        }

        if ( last < s.length() ) {

            String lastSection = s.substring( last );
            printSegment( lastSection );

        } else {

            printSegment( "" );   // needed to properly handle \n at the end of the string and harmless otherwise.

        }

    }

    public synchronized void println( String s ) {

        print( s );
        printNewline();

        // The Java 1.4.2 docs are not clear as to whether System.out or System.err are
        // opened with autoflushing enabled so we force a flush here just to be sure.

        flush();

    }

    /**
     * Flush the underlying {@link java.io.PrintStream}. Calling this method is generally not necessary as it is called
     * implicitly after each newline is written.
     */

    public synchronized void flush() {

        if ( _outputStream != null ) {

            _outputStream.flush();

        }

        if ( _mirror != null ) {

            _mirror.flush();

        }

    }

    /**
     * Log an exception/error out via this Logger instance.
     * <p/>
     * The totally unadorned stack trace contained within the {@link Throwable} is written out via this Logger instance
     * using {@link #print}.
     *
     * @param e the exception/error to be logged.
     */

    public void log( Throwable e ) {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter( sw );
        e.printStackTrace( pw );
        pw.flush();
        print( sw.toString() );  // FINALLY we can emit the stack trace via our print method
        // (this ensures that each line is time stamped - not necessary but cleaner)

        pw.close();

        try {

            sw.close();

        } catch ( IOException ee ) {

            // just ignore it.

        }

    }

    /**
     * Get the {@link java.io.File} object associated with the underlying {@link java.io.PrintStream}.
     *
     * @return the appropriate {@link java.io.File} object or null if our {@link #Logger(String, java.io.PrintStream)}
     *         constructor was used to create this Logger.
     */

    public File getOutputFile() {

        return _outputFile;

    }

    /**
     * Get the name of the output file.
     *
     * @return the name of this Logger's output file.
     */

    public String getOutputFileName() {

        if ( _outputFile == null ) {

            return _outputFileName;

        } else {

            return _outputFile.getPath();

        }

    }

    /**
     * Get the Logger associated with stdout. The logger is allocated if it does not already exist.
     *
     * @return the Logger that is associated with {@link System#out}.
     */

    public static Logger getStdout() {

        if ( _stdout == null ) {

            _stdout = new Logger( "<stdout>", System.out );

            try {

                //noinspection ResultOfMethodCallIgnored
                LOGS_DIRECTORY.mkdirs();
                _stdout.setMirror( LOGS_DIRECTORY.getPath() + "/" + ( _programName == null ? COMPONENT_NAME : _programName ) + "_stdout_" + LOG_FILE_NAME_FORMATTER
                        .format( new Date() ), -1L );

            } catch ( FileNotFoundException e ) {

                // it was worth a shot.
                Trace.event("caught an exception trying to set stdout's mirror", e );

            }

        }

        return _stdout;

    }

    /**
     * Get the Logger associated with stderr. The logger is allocated if it does not already exist.
     *
     * @return the Logger that is associated with {@link System#err}.
     */

    public static Logger getStderr() {

        if ( _stderr == null ) {

            _stderr = new Logger( "<stderr>", System.err );

            try {

                //noinspection ResultOfMethodCallIgnored
                LOGS_DIRECTORY.mkdirs();
                _stderr.setMirror( LOGS_DIRECTORY.getPath() + "/" + ( _programName == null ? COMPONENT_NAME : _programName ) + "_stderr_" + LOG_FILE_NAME_FORMATTER
                        .format( new Date() ), -1L );

            } catch ( FileNotFoundException e ) {

                // it was worth a shot.
                Logger.logErr( "caught an exception trying to set stderr's mirror", e );

            }

        }

        return _stderr;

    }

//    public static void setProgramName( String programName ) {
//
//        _programName = programName;
//
//        _stderr = null;
//        getStderr();
//        _stdout = null;
//        getStdout();
//
//    }

    /**
     * Get the Logger intended to be used for 'user friendly' messages.
     * The logger is allocated if it does not already exist.
     * <p/>
     * Note that the 'user friendly' logger only sends messages to its listeners (i.e. if there are
     * no listeners then there are no messages sent anywhere).
     *
     * @return the Logger that is intended to be used for 'user friendly' messages.
     */

    public static Logger getFriendly() {

        if ( _friendly == null ) {

            _friendly = new Logger( "<friendly>", null );

        }

        return _friendly;

    }

    /**
     * Send a log message to the 'user friendly' logger.
     *
     * @param msg the message to be printed.
     */

    public static void logFriendly( String msg ) {

        getFriendly().println( getPrefix() + msg );

    }

    /**
     * Send a log message to stdout.
     *
     * @param msg the message to be printed.
     */

    public static void logMsg( String msg ) {

        Trace.event(msg);
        getStdout().println( getPrefix() + msg );

    }

    public static void logMsgs( String[] lines ) {

        for ( String line : lines ) {

            logMsg( line );

        }

    }

    public static void logMsgs( Collection<String> lines ) {

        for ( String line : lines ) {

            logMsg( line );

        }

    }

    /**
     * Send something to a Logger.
     * @param msg what to send.
     */

    public void msg( String msg ) {

        Trace.event(msg);
        println( getPrefix() + msg );

    }

    /**
     * Log something with an optional throwable to a Logger.
     * @param msg the message.
     * @param e the throwable (ignored if null).
     */

    public void msg( String msg, Throwable e ) {

        Trace.event( msg, e );
        println( getPrefix() + msg );
        if ( e != null ) {

            log( e );

        }

    }

    private static String getPrefix() {

        return "{" + Thread.currentThread().getId() + "} ";

    }

    /**
     * Send a log message to the 'user friendly' logger and a probably different message to stdout.
     *
     * @param friendly the 'user friendly' message.
     * @param geek the 'geek-readable' message (if null then the friendly message takes its place).
     */

    public static void logMsg( String friendly, String geek ) {

        getFriendly().println( friendly );

//        if ( geek == null ) {

        String prefixedMessage = getPrefix() + ( geek == null ? friendly : geek );
        Trace.event( prefixedMessage );
        getStdout().println( prefixedMessage );

//        } else {
//
//            String prefixedMessage = getPrefix() + geek;
//            Trace.event( prefixedMessage );
//            getStdout().println( prefixedMessage );
//
//        }

    }

    /**
     * Send a log message to stderr.
     *
     * @param msg the message to be printed.
     */

    public static void logErr( String msg ) {

        String prefixedMessage = getPrefix() + msg;
        Trace.event( prefixedMessage );
        getStderr().println( prefixedMessage );

    }

    /**
     * Send a log message to the 'user friendly' logger and a probably different message to stderr.
     *
     * @param friendly the 'user friendly' message.
     * @param geek the 'geek-readable' message (if null then the friendly message takes its place).
     */

    public static void logErr( String friendly, String geek ) {

        getFriendly().println( friendly );

        if ( geek == null ) {

            logErr( friendly );

        } else {

            logErr( geek );

        }

    }

    /**
     * Send a log message and a stack trace to stderr.
     *
     * @param msg the message to be printed.
     * @param e the throwable containing the stack trace.
     */

    public static void logErr( String msg, Throwable e ) {

        logErr( msg );

        if ( e != null ) {

            Trace.event( msg, e );
            getStderr().log( e );

        }

    }

    /**
     * Send a log message to the 'user friendly' logger and a probably different message along with a
     * stack trace to stderr.
     *
     * @param friendly the 'user friendly' message.
     * @param geek the 'geek-readable' message (if null then the friendly message takes its place).
     * @param e the throwable containing the stack trace.
     */

    public static void logErr( String friendly, String geek, Throwable e ) {

        getFriendly().println( friendly );
        if ( geek == null ) {

            logErr( friendly, e );

        } else {

            logErr( geek, e );

        }

    }

    /**
     * Add a listener.
     * @param listener the listener to be added to this instance's list of listeners.
     */

    public synchronized void addListener( LoggerListener listener ) {

        _listeners.add( listener );

    }

}
