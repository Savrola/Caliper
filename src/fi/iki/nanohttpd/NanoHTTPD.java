package fi.iki.nanohttpd;

import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.PostParameters;
import org.jetbrains.annotations.Nullable;

import javax.net.ServerSocketFactory;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * A simple, tiny, nicely embeddable HTTP 1.0 server in Java
 * <p> NanoHTTPD version 1.1, Copyright &copy; 2001,2005-2007 Jarno Elonen (elonen@iki.fi, http://iki.fi/elonen/)
 * <p><b>Features + limitations: </b>
 * <ul>
 * <li> Only one Java file </li>
 * <li> Java 1.1 compatible </li>
 * <li> Released as open source, Modified BSD licence </li>
 * <li> No fixed config files, logging, authorization etc. (Implement yourself if you need them.) </li>
 * <li> Supports parameter parsing of GET and POST methods </li>
 * <li> Supports both dynamic content and file serving </li>
 * <li> Never caches anything </li>
 * <li> Doesn't limit bandwidth, request time or simultaneous connections </li>
 * <li> Example code serves files and shows all HTTP parameters and headers </li>
 * <li> File server supports directory listing, index.html and index.htm </li>
 * <li> File server does the 301 redirection trick for directories without '/' </li>
 * <li> File server supports simple skipping for files (continue download) </li>
 * <li> File server uses current directory as a web root </li>
 * <li> File server serves also very long files without memory overhead </li>
 * <li> Contains a built-in list of most common mime types </li>
 * <li> All header names are converted lowercase so they don't vary between browsers/clients </li>
 * </ul>
 * <p><b>Ways to use: </b>
 * <ul>
 * <li> Run as a standalone app, serves files from current directory and shows requests</li>
 * <li> Subclass serve() and embed to your own program </li>
 * <li> Call serveFile() from serve() with your own base directory </li>
 * </ul>
 * <p/>
 * See the end of the source file for distribution license (Modified BSD licence)
 */

@SuppressWarnings( { "ClassWithoutToString" } )
public abstract class NanoHTTPD {

    public static final int HTTP_PORT = 80;

    private static final long ONE_K = 1024L;

    @SuppressWarnings({ "UnusedDeclaration", "FieldCanBeLocal" })
    private String _myBindName;

//    private static final String PAYMENT_LOG_FILE = "payments.log";

    /**
     * HTTP response. Return one of these from serve().
     */

    @SuppressWarnings({ "ClassWithoutToString", "SameParameterValue" })
    public class Response {

        /**
         * HTTP status code after processing, e.g. "200 OK", HTTP_OK
         */

        private final String _status;

        /**
         * MIME type of content, e.g. "text/html"
         */

        private String _mimeType = null;

        /**
         * Data of the response, may be null.
         */

        private InputStream _data = null;

        /**
         * Headers for the HTTP response. Use addHeader() to add lines.
         */

        private final Properties _header = new Properties();

        /**
         * Default constructor: response = HTTP_OK, data = mime = 'null'
         */

        @SuppressWarnings("UnusedDeclaration")
        public Response() {
            super();

            _status = NanoHTTPD.HTTP_OK;

        }

        /**
         * Basic constructor.
         *
         * @param status   status of response.
         * @param mimeType type of response.
         * @param data     the actual response.
         */

        public Response( String status, String mimeType, InputStream data ) {
            super();

            _status = status;
            _mimeType = mimeType;
            _data = data;

        }

        /**
         * Convenience method that makes an InputStream out of given text.
         *
         * @param status   status of response.
         * @param mimeType type of response.
         * @param txt      the response.
         */

        public Response( String status, String mimeType, String txt ) {
            super();

            _status = status;
            _mimeType = mimeType;
            _data = new ByteArrayInputStream( txt.getBytes() );

        }

        /**
         * Adds given line to the header.
         *
         * @param name  the name.
         * @param value the value.
         */

        public void addHeader( String name, String value ) {

            _header.put( name, value );

        }

        public String getStatus() {

            return _status;

        }

        public String getMimeType() {

            return _mimeType;

        }

        public InputStream getData() {

            return _data;

        }

        public Properties getHeader() {

            return _header;

        }

    }

    /**
     * Starts a HTTP server to given port.<p> Throws an IOException if the socket is already in use
     *
     * @param bindName the host name to bind the server to.
     * @param port     the port that the server is listening on.
     * @param ssf      the server socket factory to use to get our listen socket.
     * @throws java.io.IOException if something bad happens in network-land.
     */

    protected NanoHTTPD( String bindName, int port, ServerSocketFactory ssf )
            throws
            IOException {

        super();

        _myTcpPort = port;
        _myBindName = bindName;

        final ServerSocket ss;
        if ( ssf == null ) {

            ss = new ServerSocket( _myTcpPort );

        } else {

            ss = ssf.createServerSocket();
            ss.bind( bindName == null ? new InetSocketAddress( port ) : new InetSocketAddress( bindName, port ) );

        }

        //noinspection ClassWithoutToString
        Thread t = new Thread(
                new Runnable() {

                    public void run() {

                        try {

                            //noinspection InfiniteLoopStatement
                            while ( true ) {

                                new HTTPSession( ss.accept() );

                            }

                        } catch ( IOException ioe ) {

                            // just ignore this

                        }

                    }

                }

        );

        t.start();

    }

    /**
     * Override this to customize the server.
     * <p/>
     * (By default, this delegates to serveFile() and allows directory listing.)
     *
     * @param sock           our socket.
     * @param uri            Percent-decoded URI without parameters, for example "/index.cgi"
     * @param method         "GET", "POST" etc.
     * @param header         Header entries, percent decoded
     * @param parms          Parsed, percent decoded parameters from URI and, in case of POST, data.
     * @param uploadContents the data uploaded via a POST operation or null if no uploaded data is available.
     *
     * @return HTTP response, see class Response for details
     */

    public abstract Response serve(
            Socket sock,
            String uri,
            String method,
            Properties header,
            PostParameters parms,
            byte[] uploadContents
    );

    /**
     * Some HTTP response status codes
     */

    public static final String
            HTTP_OK = "200 OK",
            HTTP_REDIRECT = "301 Moved Permanently",
            HTTP_FORBIDDEN = "403 Forbidden",
            HTTP_NOTFOUND = "404 Not Found",
            HTTP_BADREQUEST = "400 Bad Request",
            HTTP_INTERNALERROR = "500 Internal Server Error",
            HTTP_NOTIMPLEMENTED = "501 Not Implemented";

    /**
     * Common mime types for dynamic content
     */

    public static final String
            MIME_PLAINTEXT = "text/plain",
            MIME_HTML = "text/html",
            MIME_DEFAULT_BINARY = "application/octet-stream",
            MIME_TAR = "application/x-tar",
            MIME_TGZ = "application/x-compressed";

    protected abstract int getMaxUploadSize();

    @SuppressWarnings("UnusedDeclaration")
    public String getMyBindName() {

        return _myBindName;

    }

    @SuppressWarnings("UnusedDeclaration")
    public int getMyTcpPort() {

        return _myTcpPort;

    }

    /**
     * Starts as a standalone file server and waits for Enter.
     *
     * @param args see usage line below.
     */

    public static void main( String[] args ) {
        Logger.logMsg(
                "NanoHTTPD 1.1 (C) 2001,2005-2007 Jarno Elonen\n" +
                "(Command line options: [port] [--licence])\n"
        );

        // Show licence if requested
        int lopt = -1;
        for ( int i = 0; i < args.length; ++i ) {

            if ( args[i].toLowerCase().endsWith( "licence" ) ) {

                lopt = i;
                Logger.logMsg( NanoHTTPD.LICENCE + "\n" );

            }

        }

        // Change port if requested
        int port = NanoHTTPD.HTTP_PORT;
        if ( args.length > 0 && lopt != 0 ) {

            port = Integer.parseInt( args[0] );

        }

        if ( args.length > 1 && args[1].toLowerCase().endsWith( "licence" ) ) {

            Logger.logMsg( NanoHTTPD.LICENCE + "\n" );

        }

        NanoHTTPD nh = null;
        try {

            nh = new NanoHTTPD( "localhost", port, null ) {

                public static final int MAX_UPLOAD_SIZE = 10000;

                public int getMaxUploadSize() {

                    //noinspection UnqualifiedStaticUsage
                    return MAX_UPLOAD_SIZE;

                }

                public Response serve(
                        @SuppressWarnings("UnusedParameters") Socket sock,
                        String uri,
                        String method,
                        Properties header,
                        PostParameters parms,
                        @SuppressWarnings("UnusedParameters") byte[] uploadContents
                ) {

                    Logger.logMsg( method + " '" + uri + "' " );

                    Enumeration<?> e = header.propertyNames();
                    while ( e.hasMoreElements() ) {

                        String value = (String)e.nextElement();
                        Logger.logMsg( "  HDR: '" + value + "' = '" + header.getProperty( value ) + "'" );

                    }

                    for ( String key : parms.getKeys() ) {

                        Logger.logMsg( "  PRM: '" + key + "' = '" + parms.getParameter( key ) + "'" );

                    }

                    return serveFile( uri, header, new File( "." ), false );

                }

            };

        } catch ( IOException ioe ) {

            Logger.logErr( "Couldn't start server:\n" + ioe );
            System.exit( 1 );

        }

        nh.setMyFileDir( new File( "" ) );

        Logger.logMsg(
                "Now serving files on port " + port + " from \"" +
                nh.getMyFileDir().getAbsolutePath() + "\""
        );

    }

    private String readLine( BufferedInputStream is )
            throws
            IOException {

        String rval = "";

        while ( true ) {

            int ch = is.read();
            if ( ch != (int)'\r' ) {

                if ( ch == (int)'\n' ) {

                    return rval;

                } else if ( ch < 0 ) {

                    return null;

                } else {

                    rval += (char)ch;

                }

            }

        }

    }

    /**
     * Handles one session, i.e. parses the HTTP request and returns the response.
     */

    @SuppressWarnings( { "ClassWithoutToString" } )
    private class HTTPSession implements Runnable {

        private HTTPSession( Socket s ) {

            super();
            _mySocket = s;
            @SuppressWarnings("ThisEscapedInObjectConstruction") Thread t = new Thread( this );
            t.start();

        }

        public void run() {

            InputStream is = null;
            try {

                is = _mySocket.getInputStream();
                if ( is == null ) {

                    return;

                }

                BufferedInputStream in = new BufferedInputStream( is );

                // Read the request line
                String s = readLine( in );
                if ( s == null ) {

                    sendError( NanoHTTPD.HTTP_BADREQUEST, "BAD REQUEST: No request. Usage: GET /example/file.html" );

                }

                StringTokenizer st = new StringTokenizer( s );
                if ( !st.hasMoreTokens() ) {

                    sendError( NanoHTTPD.HTTP_BADREQUEST, "BAD REQUEST: Syntax error. Usage: GET /example/file.html" );

                }

                String method = st.nextToken();

                if ( !st.hasMoreTokens() ) {

                    sendError( NanoHTTPD.HTTP_BADREQUEST, "BAD REQUEST: Missing URI. Usage: GET /example/file.html" );

                }

                String uri = PostParameters.decodePercent( st.nextToken() );

                // Decode parameters from the URI
                PostParameters parms = new PostParameters();
                int qmi = uri.indexOf( (int)'?' );
                if ( qmi >= 0 ) {

                    PostParameters.decodeParms( uri.substring( qmi + 1 ), parms );
                    uri = PostParameters.decodePercent( uri.substring( 0, qmi ) );

                }

                // If there's another token, it's protocol version,
                // followed by HTTP headers. Ignore version but parse headers.
                // NOTE: this now forces header names uppercase since they are
                // case insensitive and vary by client.
                Properties header = new Properties();
                if ( st.hasMoreTokens() ) {

                    String line = readLine( in );
                    while ( !line.trim().isEmpty() ) {

                        int p = line.indexOf( (int)':' );
                        header.put( line.substring( 0, p ).trim().toLowerCase(), line.substring( p + 1 ).trim() );
                        line = readLine( in );

                    }

                }

                byte[] uploadContents = null;

                // If the method is POST, there may be parameters
                // in data section, too, read it:
                if ( "POST".equalsIgnoreCase( method ) ) {

                    int size = 0;
                    String contentLength = header.getProperty( "content-length" );
                    if ( contentLength != null ) {

                        try {

                            size = Integer.parseInt( contentLength );

                        } catch ( NumberFormatException ex ) {

                            size = 0;

                        }

                    }

                    if ( size > getMaxUploadSize() ) {

                        sendError(
                                NanoHTTPD.HTTP_NOTIMPLEMENTED,
                                "Error 501:  too much data in upload op (size was " + contentLength + ")"
                        );

                        in.close();
                        return;

                    }

                    ByteBuffer buffer = ByteBuffer.allocate( size );
                    while ( buffer.remaining() > 0 ) {

                        byte[] tmp = new byte[buffer.remaining()];
                        int rlen = in.read( tmp );
                        if ( rlen <= 0 ) {

                            break;

                        }
                        buffer.put( tmp, 0, rlen );

                    }

                    buffer.flip();
                    if ( buffer.remaining() == size ) {

                        uploadContents = new byte[size];
                        buffer.get( uploadContents );

                    } else {

                        Logger.logMsg(
                                "got " + buffer.remaining() + " bytes of what was supposed to be " + size +
                                " bytes of uploaded data - dropping session"
                        );
                        return;     // something went wrong in network land

                    }

                }

                // Ok, now do the serve()
                Response r = serve( _mySocket, uri, method, header, parms, uploadContents );
                if ( r == null ) {

                    sendError( NanoHTTPD.HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: Serve() returned a null response." );

                } else if ( HTTP_OK.equals( r.getStatus() ) ) {

                    sendResponse( r.getStatus(), r.getMimeType(), r.getHeader(), r.getData() );

                } else {

                    if ( r.getData() == null ) {

                        sendError( r.getStatus(), "unable to satisfy request" );

                    } else {

                        sendError( r.getStatus(), r.getData() );

                    }

                }

                in.close();

            } catch ( IOException ioe ) {

                try {

                    sendError( NanoHTTPD.HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage() );

                } catch ( Throwable t ) {

                    // just ignore this.

                }

            } catch ( IllegalArgumentException e ) {

                try {

                    sendError( NanoHTTPD.HTTP_INTERNALERROR, "SERVER INTERNAL ERROR:  " + e.getMessage() );

                } catch ( Throwable t ) {

                    // just ignore this

                }

            } catch ( InterruptedException ie ) {

                // Thrown by sendError, ignore and exit the thread.

            } finally {

                ObtuseUtil.closeQuietly( is );
                ObtuseUtil.closeQuietly( _mySocket );

            }

        }

        /**
         * Returns an error message as a HTTP response and throws InterruptedException to stop further request
         * processing.
         *
         * @param status the status code to send.
         * @param msg    the message to send.
         *
         * @throws InterruptedException (always thrown) to abort the operation.
         */

        private void sendError( String status, String msg )
                throws
                InterruptedException {

            sendResponse( status, NanoHTTPD.MIME_PLAINTEXT, null, new ByteArrayInputStream( msg.getBytes() ) );
            throw new InterruptedException();

        }

        /**
         * Returns an error message as a HTTP response and throws InterruptedException to stop further request
         * processing.
         *
         * @param status the status code to send.
         * @param data   an input stream containing the message to send.
         *
         * @throws InterruptedException (always thrown) to abort the operation.
         */

        private void sendError( String status, InputStream data )
                throws
                InterruptedException {

            sendResponse( status, NanoHTTPD.MIME_PLAINTEXT, null, data );
            throw new InterruptedException();

        }

        /**
         * Sends given response to the socket.
         *
         * @param status the status code to send.
         * @param mime   the MIME type of the response.
         * @param header the header 'keywords' to send.
         * @param data   the actual result to send.
         */

        private void sendResponse( String status, String mime, @Nullable Properties header, InputStream data ) {

            try {

                if ( status == null ) {

                    throw new Error( "sendResponse(): Status can't be null." );

                }

                OutputStream out = _mySocket.getOutputStream();
                PrintWriter pw = new PrintWriter( out );
                pw.print( "HTTP/1.0 " + status + " \r\n" );

                if ( mime != null ) {

                    pw.print( "Content-Type: " + mime + "\r\n" );

                }

                if ( header == null || header.getProperty( "Date" ) == null ) {

                    pw.print( "Date: " + NanoHTTPD.s_gmtFrmt.format( new Date() ) + "\r\n" );

                }

                if ( header != null ) {

                    Enumeration<?> e = header.keys();

                    while ( e.hasMoreElements() ) {

                        String key = (String)e.nextElement();
                        String value = header.getProperty( key );
                        pw.print( key + ": " + value + "\r\n" );

                    }

                }

                pw.print( "\r\n" );
                pw.flush();

                if ( data != null ) {

                    //noinspection MagicNumber
                    byte[] buff = new byte[2048];
                    while ( true ) {

                        //noinspection MagicNumber
                        int read = data.read( buff, 0, 2048 );
                        if ( read <= 0 ) {

                            break;

                        }
                        out.write( buff, 0, read );

                    }

                }
                out.flush();
                out.close();
                if ( data != null ) {

                    data.close();

                }

            } catch ( IOException ioe ) {

                // Couldn't write? No can do.

                ObtuseUtil.closeQuietly( _mySocket );

            } finally {

                if ( data != null ) {

                    ObtuseUtil.closeQuietly( data );

                }

            }

        }

        private final Socket _mySocket;
    }

    /**
     * URL-encodes everything between "/"-characters. Encodes spaces as '%20' instead of '+'.
     *
     * @param uri the URI which is to be encoded.
     *
     * @return the encoded URI.
     */

    private String encodeUri( String uri ) {

        String newUri = "";
        StringTokenizer st = new StringTokenizer( uri, "/ ", true );
        while ( st.hasMoreTokens() ) {

            String tok = st.nextToken();
            if ( "/".equals( tok ) ) {

                newUri += "/";

            } else if ( " ".equals( tok ) ) {

                newUri += "%20";

            } else {

                // For Java 1.4 you'll want to use this instead:
                try {

                    newUri += URLEncoder.encode( tok, "UTF-8" );

                } catch ( UnsupportedEncodingException uee ) {

                    // just ignore this

                }

            }

        }

        return newUri;

    }

    @SuppressWarnings("FieldCanBeLocal")
    private final int _myTcpPort;

    private File _myFileDir = null;

    // ==================================================
    // File server code
    // ==================================================

    /**
     * Serves file from homeDir and its' subdirectories (only). Uses only URI, ignores all headers and HTTP parameters.
     *
     * @param uri                   the URI of the file which is to be served.
     * @param header                the header 'keywords' to be returned with the response.
     * @param homeDir               the home directory of the web site.
     * @param allowDirectoryListing should directory listings be allowed.
     *
     * @return the response.
     */

    public Response serveFile(
            String uri,
            Properties header,
            File homeDir,
            boolean allowDirectoryListing
    ) {

        // Make sure we won't die of an exception later
        if ( !homeDir.isDirectory() ) {

            return new Response(
                    NanoHTTPD.HTTP_INTERNALERROR, NanoHTTPD.MIME_PLAINTEXT,
                    "INTERNAL ERRROR: serveFile(): given homeDir is not a directory."
            );

        }

        // Remove URL arguments
        String uri1 = uri.trim().replace( File.separatorChar, '/' );
        if ( uri1.indexOf( (int)'?' ) >= 0 ) {

            uri1 = uri1.substring( 0, uri1.indexOf( (int)'?' ) );

        }

        // Prohibit getting out of current directory
        if ( uri1.startsWith( ".." ) || uri1.endsWith( ".." ) || uri1.contains( "../" ) ) {

            return new Response(
                    NanoHTTPD.HTTP_FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT,
                    "FORBIDDEN: Won't serve ../ for security reasons."
            );

        }

        File f = new File( homeDir, uri1 );
        if ( !f.exists() ) {

            return new Response(
                    NanoHTTPD.HTTP_NOTFOUND, NanoHTTPD.MIME_PLAINTEXT,
                    "Error 404, file not found."
            );

        }

        // List the directory, if necessary
        if ( f.isDirectory() ) {

            // Browsers get confused without '/' after the
            // directory, send a redirect.
            if ( !uri1.endsWith( "/" ) ) {

                uri1 += "/";
                Response r = new Response(
                        NanoHTTPD.HTTP_REDIRECT, NanoHTTPD.MIME_HTML,
                        "<html><body>Redirected: <a href=\"" + uri1 + "\">" +
                        uri1 + "</a></body></html>"
                );
                r.addHeader( "Location", uri1 );
                return r;

            }

            // First try index.html and index.htm
            if ( new File( f, "index.html" ).exists() ) {

                f = new File( homeDir, uri1 + "/index.html" );

            } else if ( new File( f, "index.htm" ).exists() ) {

                f = new File( homeDir, uri1 + "/index.htm" );

            }

            // No index file, list the directory
            else if ( allowDirectoryListing ) {

                String[] files = f.list();
                String msg = "<html><body><h1>Directory " + uri1 + "</h1><br/>";

                if ( uri1.length() > 1 ) {

                    String u = uri1.substring( 0, uri1.length() - 1 );
                    int slash = u.lastIndexOf( (int)'/' );
                    if ( slash >= 0 && slash < u.length() ) {

                        msg += "<b><a href=\"" + uri1.substring( 0, slash + 1 ) + "\">..</a></b><br/>";

                    }

                }

                for ( int i = 0; i < files.length; ++i ) {

                    File curFile = new File( f, files[i] );
                    boolean dir = curFile.isDirectory();
                    if ( dir ) {

                        msg += "<b>";
                        files[i] += "/";

                    }

                    msg += "<a href=\"" + encodeUri( uri1 + files[i] ) + "\">" +
                           files[i] + "</a>";

                    // Show file size
                    if ( curFile.isFile() ) {

                        long len = curFile.length();
                        msg += " &nbsp;<font size=2>(";
                        if ( len < NanoHTTPD.ONE_K ) {

                            msg += curFile.length() + " bytes";

                        } else if ( len < NanoHTTPD.ONE_K * NanoHTTPD.ONE_K ) {

                            //noinspection UnnecessaryParentheses,ImplicitNumericConversion,MagicNumber
                            msg += curFile.length() / NanoHTTPD.ONE_K + "." + ( curFile.length() % NanoHTTPD.ONE_K / 10 % 100 ) + " KB";

                        } else {

                            //noinspection ImplicitNumericConversion,MagicNumber
                            msg += curFile.length() / ( NanoHTTPD.ONE_K * NanoHTTPD.ONE_K ) + "." +
                                   curFile.length() % ( NanoHTTPD.ONE_K * NanoHTTPD.ONE_K ) / 10 % 100 + " MB";

                        }

                        msg += ")</font>";

                    }
                    msg += "<br/>";
                    if ( dir ) {

                        msg += "</b>";

                    }

                }

                return new Response( NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_HTML, msg );

            } else {

                return new Response(
                        NanoHTTPD.HTTP_FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT,
                        "FORBIDDEN: No directory listing."
                );

            }

        }

        try {

            // Get MIME type from file name extension, if possible
            String mime = null;
            int dot = f.getCanonicalPath().lastIndexOf( (int)'.' );
            if ( dot >= 0 ) {

                mime = NanoHTTPD.s_theMimeTypes.get( f.getCanonicalPath().substring( dot + 1 ).toLowerCase() );

            }
            if ( mime == null ) {

                mime = NanoHTTPD.MIME_DEFAULT_BINARY;

            }

            // Support (simple) skipping:
            long startFrom = 0L;
            String range = header.getProperty( "Range" );
            if ( range != null ) {

                if ( range.startsWith( "bytes=" ) ) {

                    range = range.substring( "bytes=".length() );
                    int minus = range.indexOf( (int)'-' );
                    if ( minus > 0 ) {

                        range = range.substring( 0, minus );

                    }

                    try {

                        startFrom = Long.parseLong( range );

                    } catch ( NumberFormatException nfe ) {

                        // just ignore this

                    }

                }

            }

            FileInputStream fis = new FileInputStream( f );
            //noinspection ResultOfMethodCallIgnored
            fis.skip( startFrom );
            Response r = new Response( NanoHTTPD.HTTP_OK, mime, fis );
            r.addHeader( "Content-length", "" + ( f.length() - startFrom ) );
            r.addHeader(
                    "Content-range", "" + startFrom + "-" +
                                     ( f.length() - 1L ) + "/" + f.length()
            );

            return r;

        } catch ( IOException ioe ) {

            return new Response( NanoHTTPD.HTTP_FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: Reading file failed." );

        }

    }

    /**
     * Hashtable mapping (String)FILENAME_EXTENSION -> (String)MIME_TYPE
     */

    private static final Map<String, String> s_theMimeTypes = new HashMap<String, String>();

    static {

        StringTokenizer st = new StringTokenizer(
                "htm		text/html " +
                "html		text/html " +
                "txt		text/plain " +
                "asc		text/plain " +
                "gif		image/gif " +
                "jpg		image/jpeg " +
                "jpeg		image/jpeg " +
                "png		image/png " +
                "mp3		audio/mpeg " +
                "m3u		audio/mpeg-url " +
                "pdf		application/pdf " +
                "doc		application/msword " +
                "ogg		application/x-ogg " +
                "zip		application/octet-stream " +
                "exe		application/octet-stream " +
                "class		application/octet-stream "
        );

        while ( st.hasMoreTokens() ) {

            NanoHTTPD.s_theMimeTypes.put( st.nextToken(), st.nextToken() );

        }

    }

    /**
     * GMT date formatter
     */
    private static final java.text.SimpleDateFormat s_gmtFrmt;

    static {

        s_gmtFrmt = new java.text.SimpleDateFormat( "E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US );
        NanoHTTPD.s_gmtFrmt.setTimeZone( TimeZone.getTimeZone( "GMT" ) );

    }

    /**
     * The distribution licence
     */

    public static final String LICENCE =
            "\tCopyright (C) 2001,2005 by Jarno Elonen <elonen@iki.fi>\n" +
            "\n" +
            "\tRedistribution and use in source and binary forms, with or without\n" +
            "\tmodification, are permitted provided that the following conditions\n" +
            "\tare met:\n" +
            "\n" +
            "\tRedistributions of source code must retain the above copyright notice,\n" +
            "\tthis list of conditions and the following disclaimer. Redistributions in\n" +
            "\tbinary form must reproduce the above copyright notice, this list of\n" +
            "\tconditions and the following disclaimer in the documentation and/or other\n" +
            "\tmaterials provided with the distribution. The name of the author may not\n" +
            "\tbe used to endorse or promote products derived from this software without\n" +
            "\tspecific prior written permission. \n" +
            " \n" +
            "\tTHIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR\n" +
            "\tIMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES\n" +
            "\tOF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.\n" +
            "\tIN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,\n" +
            "\tINCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT\n" +
            "\tNOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,\n" +
            "\tDATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY\n" +
            "\tTHEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT\n" +
            "\t(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE\n" +
            "\tOF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.";

    public File getMyFileDir() {

        return _myFileDir;

    }

    public void setMyFileDir( File myFileDir ) {

        _myFileDir = myFileDir;

    }

    @SuppressWarnings("UnusedDeclaration")
    public static void showLicense() {

        Logger.logMsg( NanoHTTPD.LICENCE + "\n" );

    }

    @SuppressWarnings("UnusedDeclaration")
    protected byte[] cleanUploadedBytes( byte[] uploadContents ) {

        ByteBuffer tmpBuffer = ByteBuffer.allocate( uploadContents.length );
        for ( byte b : uploadContents ) {

            if ( b >= (byte)' ' && b <= (byte)'~' ) {

                tmpBuffer.put( b );

            } else {

                Logger.logErr(
                        "ERROR:  unexpected by in POST parameters 0x" + ObtuseUtil.hexvalue( b ) + " - ignored"
                );

            }

        }

        tmpBuffer.flip();
        byte[] cleanBytes = new byte[ tmpBuffer.remaining() ];
        tmpBuffer.get( cleanBytes );

        return cleanBytes;

    }

//    public static void logPaymentNotification(
//            Socket sock,
//            String encodedParmsString,
//            String customParmsString,
//            String paymentStatus
//    ) {
//
//        try {
//
//            Logger paymentLogFile = new Logger( new File( PAYMENT_LOG_FILE ), true );
//            //noinspection ObjectToString
//            String msg = "payment notification from " + sock.getRemoteSocketAddress() +
//                         "(" + paymentStatus + "):  parms = " + encodedParmsString +
//                         "; custom parms = " + customParmsString;
//            paymentLogFile.println( msg );
//            paymentLogFile.close();
//            Logger.logMsg( msg );
//
//        } catch ( FileNotFoundException e ) {
//
//            Trace.emitTrace( "FATAL:  unable to open payment log file - bye!", e );
//            System.exit( 1 );
//
//        }
//
//    }

    @SuppressWarnings("UnusedDeclaration")
    protected void logParms( PostParameters params, String title ) {

        if ( params != null ) {

            Logger.logMsg( title + ":" );
            for ( String key : params.getKeys() ) {

                String value = params.getParameter( key );
                Logger.logMsg( "\t" + key + " = \"" + value + "\"" );

            }

        } else {

            Logger.logMsg( title + " are empty" );

        }

    }

}
