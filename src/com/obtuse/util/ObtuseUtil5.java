package com.obtuse.util;

import com.obtuse.db.PostgresConnection;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.zip.ZipFile;

/*
 * Copyright 2005-2012 Obtuse Systems Corporation
 */

/**
 * Utility methods and such.
 */

@SuppressWarnings({ "UnusedDeclaration" })
public class ObtuseUtil5 {

    @SuppressWarnings("UseOfObsoleteCollectionType")
    private static class UnmodifiableHashtable<K, V> extends Hashtable<K, V> {

        private boolean _readonly;
        private final Hashtable<? extends K, ? extends V> _ht;

        private UnmodifiableHashtable( Hashtable<? extends K, ? extends V> ht ) {

            super( ht );
            _ht = ht;
            _readonly = false;
        }

        private void makeReadonly() {

            _readonly = true;

        }

        public void clear() {

            if ( _readonly ) {

                throw new UnsupportedOperationException( "attempt to modify an unmodifiable Dictionary/Hashtable" );

            } else {

                super.clear();

            }

        }

        public Set<Map.Entry<K, V>> entrySet() {

            if ( _readonly ) {

                //noinspection unchecked,rawtypes

                return (Set)Collections.unmodifiableSet( _ht.entrySet() );

            } else {

                return super.entrySet();

            }

        }

        public Set<K> keySet() {

            if ( _readonly ) {

                return Collections.unmodifiableSet( _ht.keySet() );

            } else {

                return super.keySet();

            }

        }

        public V put( K key, V value ) {

            if ( _readonly ) {

                throw new UnsupportedOperationException( "attempt to modify an unmodifiable Dictionary/Hashtable" );

            } else {

                return super.put( key, value );

            }

        }

        public void putAll( Map<? extends K, ? extends V> t ) {

            if ( _readonly ) {

                throw new UnsupportedOperationException( "attempt to modify an unmodifiable Dictionary/Hashtable" );

            } else {

                super.putAll( t );

            }

        }

        public void rehash() {

            if ( _readonly ) {

                // Been there, done that.

            } else {

                super.rehash();

            }

        }

        public V remove( Object key ) {

            if ( _readonly ) {

                throw new UnsupportedOperationException( "attempt to modify an unmodifiable Dictionary/Hashtable" );

            } else {

                return super.remove( key );

            }

        }

        public Collection<V> values() {

            if ( _readonly ) {

                return Collections.unmodifiableCollection( _ht.values() );

            } else {

                return super.values();

            }

        }

    }

    private ObtuseUtil5() {

        super();
    }

    /**
     * Turn a serializable object into a byte array. No muss, no fuss, no exceptions!
     *
     * @param thing                  the object which is to be serialized.
     * @param printStackTraceOnError true if a stack trace should be printed if anything goes wrong.
     * @return the serialized form of <tt>thing</tt> or null if serialization fails.
     */

    @SuppressWarnings({ "SameParameterValue" })
    public static byte[] getSerializedVersion( Serializable thing, boolean printStackTraceOnError ) {

        try {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream( bos );
            oos.writeObject( thing );
            oos.flush();

            byte[] sv = bos.toByteArray();

            ObtuseUtil5.closeQuietly( oos );

            return sv;

        } catch ( IOException e ) {

            // %%% should probably be a fatal error.

            if ( printStackTraceOnError ) {

                //noinspection CallToPrintStackTrace
                e.printStackTrace();

            }

            return null;

        }

    }

    /**
     * Turn a byte array back into a serializable thing. Throws an exception if something goes wrong.
     *
     * @param sv the serialized form of the object.
     * @return the de-serialized object or null if de-serialization fails.
     * @throws java.io.IOException    if an I/O error occurs reading from the {@link InputStream} or a deserialization
     *                                error occurs (see {@link ObjectInputStream#readObject()} for details on what can
     *                                go wrong).
     * @throws ClassNotFoundException if the class of a serialized object in the {@link InputStream} cannot be found.
     */

    public static Serializable recoverSerializedVersion(
            byte[] sv
    )
            throws ClassNotFoundException, IOException {

        ByteArrayInputStream bis = new ByteArrayInputStream( sv );
        return ObtuseUtil5.recoverSerializedVersion( bis );

    }

    /**
     * Turn an {@link InputStream} back into a serializable thing. The next serialized object from the stream is read
     * and de-serialized. The stream is left ready to have the next object read from it.
     *
     * @param is the input stream that the serialized object is to be read from.
     * @return the de-serialized object or null if something goes wrong.
     * @throws java.io.IOException    if an I/O error occurs reading from the {@link InputStream} or a deserialization
     *                                error occurs (see {@link ObjectInputStream#readObject()} for details on what can
     *                                go wrong).
     * @throws ClassNotFoundException if the class of a serialized object in the {@link InputStream} cannot be found.
     */

    private static Serializable recoverSerializedVersion( InputStream is )
            throws ClassNotFoundException, IOException {

        ObjectInputStream ois = null;
        try {

            ois = new ObjectInputStream( is );
            @SuppressWarnings({ "UnnecessaryLocalVariable" })
            Serializable thing = (Serializable)ois.readObject();

            return thing;

        } finally {

            ObtuseUtil5.closeQuietly( ois );

        }

    }

    /**
     * Turn a byte array back into a serializable thing. Prints a stack trace and returns null if something goes wrong.
     *
     * @param sv                     the serialized form of the object.
     * @param printStackTraceOnError true if a stack trace should be printed if anything goes wrong.
     * @return the de-serialized object or null if de-serialization fails.
     */

    public static Serializable recoverSerializedVersion(
            byte[] sv,
            boolean printStackTraceOnError
    ) {

        ByteArrayInputStream bis = new ByteArrayInputStream( sv );
        try {

            return ObtuseUtil5.recoverSerializedVersion( bis, printStackTraceOnError );

        } finally {

            ObtuseUtil5.closeQuietly( bis );

        }

    }

    /**
     * Turn an {@link InputStream} back into a serializable thing. The next serialized object from the stream is read
     * and de-serialized. The stream is left ready to have the next object read from it.
     *
     * @param is                     the input stream that the serialized object is to be read from.
     * @param printStackTraceOnError true if a stack trace should be printed if anything goes wrong.
     * @return the de-serialized object or null if something goes wrong.
     */

    public static Serializable recoverSerializedVersion( InputStream is, boolean printStackTraceOnError ) {

        try {

            ObjectInputStream ois = new ObjectInputStream( is );
            Serializable thing = (Serializable)ois.readObject();
            ObtuseUtil5.closeQuietly( ois );
            return thing;

        } catch ( IOException e ) {

            // This must NOT be a fatal error - let the caller deal with it

            if ( printStackTraceOnError ) {

                //noinspection CallToPrintStackTrace
                e.printStackTrace();

            }

            return null;

        } catch ( ClassNotFoundException e ) {

            // This must NOT be a fatal error - let the caller deal with it

            if ( printStackTraceOnError ) {

                //noinspection CallToPrintStackTrace
                e.printStackTrace();

            }

            return null;

        } catch ( Exception e ) {

            if ( printStackTraceOnError ) {

                //noinspection CallToPrintStackTrace
                e.printStackTrace();

            }

            return null;

        }

    }

    /**
     * Read the contents of a file into a byte array without needing to worry about exceptions.
     *
     * @param fname                  the name of the file to be read.
     * @param maxLength              the maximum number of bytes to read (if the file is longer than this then the
     *                               excess data is silently not returned).
     * @param printStackTraceOnError specifies whether or not a stack trace is to be printed if an i/o error occurs.
     * @return a byte array containing the data read from the file or null if the file could not be read for any reason.
     *         A zero-length byte array is returned if the file exists but contains no data.
     */

    public static byte[] readEntireFile( String fname, int maxLength, boolean printStackTraceOnError ) {

        if ( fname == null ) {

            return null;

        }

        return ObtuseUtil5.readEntireFile( new File( fname ), maxLength, printStackTraceOnError );

    }

    /**
     * Read the contents of a file into a byte array without needing to worry about exceptions.
     *
     * @param file                   the file to be read.
     * @param maxLength              the maximum number of bytes to read (if the file is longer than this then the
     *                               excess data is silently not returned).
     * @param printStackTraceOnError specifies whether or not a stack trace is to be printed if an i/o error occurs.
     * @return a byte array containing the data read from the file or null if the file could not be read for any reason.
     *         A zero-length byte array is returned if the file exists but contains no data.
     */

    public static byte[] readEntireFile( File file, int maxLength, boolean printStackTraceOnError ) {

        if ( file == null ) {

            return null;

        }

        FileInputStream fs = null;
        try {

            fs = new FileInputStream( file );

            //noinspection UnnecessaryLocalVariable
            byte[] contents = ObtuseUtil5.readEntireStream( fs, maxLength, printStackTraceOnError );

            return contents;

        } catch ( IOException e ) {

            if ( printStackTraceOnError ) {

                //noinspection CallToPrintStackTrace
                e.printStackTrace();

            }

            return null;

/*
            e.printStackTrace();
            System.err.println( "ObtuseUtil:readEntireFile - unable to read contents of \""
                                + fname + "\" - bye!" );
            System.exit( 1 );
            return null;    // keep the compiler happy.
*/

        } finally {

            ObtuseUtil5.closeQuietly( fs );

        }

    }

    /**
     * Read the contents of an {@link InputStream} into a byte array and return the byte array without needing to worry
     * about exceptions.
     *
     * @param is                     the {@link InputStream} to read the data from.
     * @param maxLength              the maximum number of bytes to read (if the stream's contents are longer than this
     *                               then the excess data is left in the stream).
     * @param printStackTraceOnError specifies whether or not a stack trace is to be printed if an i/o error occurs.
     * @return a byte array containing the data read from the file or null if the file could not be read for any reason.
     *         A zero-length byte array is returned if the stream contains no data.
     */

    public static byte[] readEntireStream( InputStream is, int maxLength, boolean printStackTraceOnError ) {

        if ( is == null ) {

            return null;

        }

        try {

            byte[] tmp = new byte[maxLength];
            int actualLen = is.read( tmp );
            if ( actualLen <= 0 ) {

                return new byte[0];

            }

            byte[] contents = new byte[actualLen];
            System.arraycopy( tmp, 0, contents, 0, actualLen );

            return contents;

        } catch ( IOException e ) {

            if ( printStackTraceOnError ) {

                //noinspection CallToPrintStackTrace
                e.printStackTrace();

            }

            return null;    // keep the compiler happy.

        }

    }

    /**
     * Write the contents of a byte array into a file without needing to worry about exceptions.
     *
     * @param bytes                  the byte array to be written.
     * @param fname                  the name of the file to be written.
     * @param printStackTraceOnError specifies whether or not a stack trace is to be printed if an i/o error occurs.
     * @return true if the operation succeeded and false otherwise.
     */

    @SuppressWarnings({ "BooleanMethodNameMustStartWithQuestion" })
    public static boolean writeBytesToFile( byte[] bytes, String fname, boolean printStackTraceOnError ) {

        return ObtuseUtil5.writeBytesToFile( bytes, new File( fname ), printStackTraceOnError );

    }

    /**
     * Write the contents of a byte array into a file without needing to worry about exceptions.
     *
     * @param bytes                  the byte array to be written.
     * @param file                   the file to be written.
     * @param printStackTraceOnError specifies whether or not a stack trace is to be printed if an i/o error occurs.
     * @return true if the operation succeeded and false otherwise.
     */

    @SuppressWarnings({ "BooleanMethodNameMustStartWithQuestion" })
    public static boolean writeBytesToFile( byte[] bytes, File file, boolean printStackTraceOnError ) {

        FileOutputStream fs = null;
        try {

            fs = new FileOutputStream( file );

            //noinspection UnnecessaryLocalVariable
            boolean rval = ObtuseUtil5.writeBytesToStream( bytes, fs, printStackTraceOnError );

            return rval;

        } catch ( IOException e ) {

            if ( printStackTraceOnError ) {

                //noinspection CallToPrintStackTrace
                e.printStackTrace();

            }

            return false;

        } finally {

            ObtuseUtil5.closeQuietly( fs );

        }

    }

    /**
     * Write the contents of a byte array to an {@link OutputStream} without needing to worry about exceptions.
     *
     * @param bytes                  the byte array to be written to the stream.
     * @param os                     the {@link OutputStream} to write the byte array to.
     * @param printStackTraceOnError specifies whether or not a stack trace is to be printed if an i/o error occurs.
     * @return true if it worked, false otherwise.
     */

    @SuppressWarnings({ "BooleanMethodNameMustStartWithQuestion" })
    public static boolean writeBytesToStream( byte[] bytes, OutputStream os, boolean printStackTraceOnError ) {

        try {

            os.write( bytes );

            return true;

        } catch ( IOException e ) {

            if ( printStackTraceOnError ) {

                //noinspection CallToPrintStackTrace
                e.printStackTrace();

            }

            return false;

        }

    }

    /**
     * Get the size of the serialized version of an object.
     *
     * @param thing the object which is to be serialized so that the length of its serialized form can be returned (the
     *              serialized form of the object is discarded - it is often much more sensible to just serialize the
     *              object using {@link #getSerializedVersion} and then just get the length of the returned byte
     *              array).
     * @return the length of the serialized form of the object.
     */

    public static int getSerializedSize( Serializable thing ) {

        byte[] sv = ObtuseUtil5.getSerializedVersion( thing, false );
        if ( sv == null ) {

            return 0;

        } else {

            return sv.length;

        }

    }

    /**
     * Validate the number and type of arguments passed to an XML-RPC routine.
     *
     * @param methodName the method which is to be called.
     * @param actual     the actual parameters which are to be passed to the method.
     * @param expected   a description of the parameter types which are expected.
     * @return null if arguments are valid and an appropriate error message otherwise.
     */

    @SuppressWarnings({ "RawUseOfParameterizedType", "CollectionDeclaredAsConcreteClass" })
    public static String validateArgs(
            String methodName,
            @SuppressWarnings("UseOfObsoleteCollectionType") Vector actual,
            Class[] expected
    ) {

        if ( actual.size() != expected.length ) {

            return methodName + " requires " + expected.length + " arguments but caller provided " +
                   actual.size();

        }

        for ( int i = 0; i < expected.length; i += 1 ) {

            Object arg = actual.elementAt( i );
            if ( arg == null ) {

                return "XMLRPC is not supposed to support null values but argument "
                       + ( i + 1 ) + " (one-origin) to " + methodName +
                       " is null (impressive! WRONG but still impressive!)";

            }

            if ( !arg.getClass().equals( expected[i] ) ) {

                return "argument " + ( i + 1 ) + " (one-origin) parameter to " + methodName +
                       " is of the wrong type (expected "
                       + expected[i] + ", received " + arg.getClass() + ")";

            }

        }

        return null;

    }

    public static String lpad( float value, int width, int digits ) {

        return ObtuseUtil5.lpad( (double)value, width, digits );

    }

    private static DecimalFormat[] s_cachedFormats = new DecimalFormat[1];

    public static String lpad( double di, int w, int v ) {

        if ( v >= ObtuseUtil5.s_cachedFormats.length ) {

            DecimalFormat[] tmp = new DecimalFormat[v + 1];
            System.arraycopy( ObtuseUtil5.s_cachedFormats, 0, tmp, 0, ObtuseUtil5.s_cachedFormats.length );
            ObtuseUtil5.s_cachedFormats = tmp;

        }

        if ( ObtuseUtil5.s_cachedFormats[v] == null ) {

            String format = "0.";
            for ( int i = 0; i < v; i += 1 ) {

                format += "#";

            }

            ObtuseUtil5.s_cachedFormats[v] = new DecimalFormat( format );

        }

        return ObtuseUtil5.lpad( ObtuseUtil5.s_cachedFormats[v].format( di ), w );

    }

    private static DecimalFormat[] s_cachedZeroFormats = new DecimalFormat[1];

    public static String lpad0( double di, int w, int v ) {

        if ( v >= ObtuseUtil5.s_cachedZeroFormats.length ) {

            DecimalFormat[] tmp = new DecimalFormat[v + 1];
            System.arraycopy( ObtuseUtil5.s_cachedZeroFormats, 0, tmp, 0, ObtuseUtil5.s_cachedZeroFormats.length );
            ObtuseUtil5.s_cachedZeroFormats = tmp;

        }

        if ( ObtuseUtil5.s_cachedZeroFormats[v] == null ) {

            String format = "0.";
            for ( int i = 0; i < v; i += 1 ) {

                format += "0";

            }

            ObtuseUtil5.s_cachedZeroFormats[v] = new DecimalFormat( format );

        }

        return ObtuseUtil5.lpad( ObtuseUtil5.s_cachedZeroFormats[v].format( di ), w );

    }

//    public static String lpad( double value, int width, int digits ) {
//
//        if ( value < 0.0 ) {
//
//            return lpad( "-" + lpad( -value, 0, digits ), width );
//
//        }
//
//        long mult = 1L;
//        for ( int i = 0; i < digits; i += 1 ) {
//
//            //noinspection MagicNumber
//            mult *= 10L;
//
//        }
//
//        long lv = Math.round( value * (double)mult );
////        if ( lv == 0 ) {
////
////            return lpad( rpad( "0.", d + 2, '0' ), w );
////
////        }
//        String rv = "" + lv;
//        while ( rv.length() < digits + 1 ) {
//
//            rv = "0" + rv;
//
//        }
//
//        rv = rv.substring( 0, rv.length() - digits ) + '.' + rv.substring( rv.length() - digits );
//
//        return lpad( rv, width );
//
//    }

    /**
     * Pad a string on the left to a specified width using a specified padding character.
     *
     * @param s the string to be padded.
     * @param w the width (i.e. length) of the padded string (if the string is already longer than this then the string
     *          itself is returned as-is).
     * @param p the character to be used to pad the string on the left if it is shorter than <tt>w</tt>.
     * @return the padded string or the original string if it is already at least as wide as <tt>w</tt>.
     */

    public static String lpad( String s, int w, char p ) {

        String str = s == null ? "null" : s;
        return ObtuseUtil5.generatePaddingString( w, p, str ) + str;

    }

    public static String generatePaddingString( int w, char p, String str ) {

        int padding = w - str.length();
        String padString;
        if ( padding > 0 ) {

            char[] padArray = new char[padding];
            for ( int i = 0; i < padding; i += 1 ) {

                padArray[i] = p;

            }

            padString = new String( padArray );

        } else {

            padString = "";

        }

        return padString;

    }

    /**
     * Pad a string on the left to a specified width using blanks as the padding character. Note that a call to this
     * method using <tt>lpad( s, w )</tt> is equivalent to a call to {@link #lpad(String, int, char)} using <tt>lpad( s,
     * w, '&nbsp;' )</tt>.
     *
     * @param s the string to be padded.
     * @param w the width (i.e. length) of the padded string (if the string is already longer than this then the string
     *          itself is returned as-is).
     * @return the padded string or the original string if it is already at least as wide as <tt>w</tt>.
     */

    public static String lpad( String s, int w ) {

        return ObtuseUtil5.lpad( s, w, ' ' );

    }

    /**
     * Pad the string representation of a long on the left to a specified width using a specified padding character.
     *
     * @param l the long whose string representation is to be padded.
     * @param w the width (i.e. length) of the padded string representation (if the string representation is already
     *          longer than this then the string representation itself is returned as-is).
     * @param p the character to be used to pad the string representation on the left if it is shorter than <tt>w</tt>.
     * @return the padded string representation or the actual string representation if it is already at least as wide as
     *         <tt>w</tt>.
     */

    public static String lpad( long l, int w, char p ) {

        return ObtuseUtil5.lpad( "" + l, w, p );

    }

    /**
     * Pad the string representation of a long on the left to a specified width using blanks as the padding character.
     * Note that a call to this method using <tt>lpad( l, w )</tt> is equivalent to a call to {@link
     * #lpad(long, int, char)} using <tt>lpad( w, w, '&nbsp;' )</tt>.
     *
     * @param l the long whose string representation is to be padded.
     * @param w the width (i.e. length) of the padded string representation (if the string representation is already
     *          longer than this then the string representation itself is returned as-is).
     * @return the padded string representation or the actual string representation if it is already at least as wide as
     *         <tt>w</tt>.
     */

    public static String lpad( long l, int w ) {

        return ObtuseUtil5.lpad( "" + l, w );

    }

    /**
     * Pad a string on the right to a specified width using a specified padding character.
     *
     * @param s the string to be padded.
     * @param w the width (i.e. length) of the padded string (if the string is already longer than this then the string
     *          itself is returned as-is).
     * @param p the character to be used to pad the string on the right if it is shorter than <tt>w</tt>.
     * @return the padded string or the original string if it is already at least as wide as <tt>w</tt>.
     */

    public static String rpad( String s, int w, char p ) {

        String str = s == null ? "null" : s;
        return str + ObtuseUtil5.generatePaddingString( w, p, str );

//        String rval = s == null ? "null" : s;
//        while ( rval.length() < w ) {
//
//            rval += p;
//
//        }
//
//        return rval;

    }

    /**
     * Pad a string on the right to a specified width using blanks as the padding character. Note that a call to this
     * method using <tt>lpad( s, w )</tt> is equivalent to a call to {@link #lpad(String, int, char)} using <tt>lpad( s,
     * w, '&nbsp;' )</tt>.
     *
     * @param s the string to be padded.
     * @param w the width (i.e. length) of the padded string (if the string is already longer than this then the string
     *          itself is returned as-is).
     * @return the padded string or the original string if it is already at least as wide as <tt>w</tt>.
     */

    public static String rpad( String s, int w ) {

        return ObtuseUtil5.rpad( s, w, ' ' );

    }

    /**
     * Pad the string representation of a long on the right to a specified width using a specified padding character.
     *
     * @param l the long whose string representation is to be padded.
     * @param w the width (i.e. length) of the padded string representation (if the string representation is already
     *          longer than this then the string representation itself is returned as-is).
     * @param p the character to be used to pad the string representation on the right if it is shorter than
     *          <tt>w</tt>.
     * @return the padded string representation or the actual string representation if it is already at least as wide as
     *         <tt>w</tt>.
     */

    public static String rpad( long l, int w, char p ) {

        return ObtuseUtil5.rpad( "" + l, w, p );

    }

    /**
     * Pad the string representation of a long on the right to a specified width using blanks as the padding character.
     * Note that a call to this method using <tt>lpad( l, w )</tt> is equivalent to a call to {@link
     * #lpad(long, int, char)} using <tt>lpad( w, w, '&nbsp;' )</tt>.
     *
     * @param l the long whose string representation is to be padded.
     * @param w the width (i.e. length) of the padded string representation (if the string representation is already
     *          longer than this then the string representation itself is returned as-is).
     * @return the padded string representation or the actual string representation if it is already at least as wide as
     *         <tt>w</tt>.
     */

    public static String rpad( long l, int w ) {

        return ObtuseUtil5.rpad( "" + l, w );

    }

    /**
     * Replicate a string a specified number of times.
     * For example, <tt>replicate( "hello", 3 )</tt> yields <tt>"hellohellohello"</tt>.
     *
     * @param str   the string to replicate.
     * @param count the number of copies to be made.
     * @return the replicated string.
     */

    public static String replicate( String str, int count ) {

        StringBuilder rval = new StringBuilder();
        for ( int i = 0; i < count; i += 1 ) {

            rval.append( str );

        }

        return rval.toString();

    }

    /**
     * Return a String containing the hex representation of a long value. For example, <tt>hexvalue( 27L )</tt> yields
     * <tt>"000000000000001b"</tt>. Note that the string is always 16 characters long.
     *
     * @param v the long value whose hex representation is to be returned.
     * @return the hex representation of <tt>v</tt>.
     */

    @SuppressWarnings({ "MagicNumber" })
    public static String hexvalue( long v ) {

        //noinspection UnnecessaryParentheses

        return ""
               + ObtuseUtil5.hexvalue( (int)( ( v >> 32 ) & 0x00000000ffffffffL ) )
               + ObtuseUtil5.hexvalue( (int)( v & 0x00000000ffffffffL ) );

    }

    /**
     * Return a String containing the hex representation of an int value. For example, <tt>hexvalue( 27 )</tt> yields
     * <tt>"0000001b"</tt>. Note that the string is always 8 characters long.
     *
     * @param v the int value whose hex representation is to be returned.
     * @return the hex representation of <tt>v</tt>.
     */

    @SuppressWarnings({ "UnnecessaryParentheses", "MagicNumber" })
    public static String hexvalue( int v ) {

        return ""
               + ObtuseUtil5.hexvalue( (byte)( ( v >> 24 ) & 0xff ) )
               + ObtuseUtil5.hexvalue( (byte)( ( v >> 16 ) & 0xff ) )
               + ObtuseUtil5.hexvalue( (byte)( ( v >> 8 ) & 0xff ) )
               + ObtuseUtil5.hexvalue( (byte)( v & 0xff ) );

    }

    /**
     * Return a String containing the hex representation of a single byte value. For example, <tt>hexvalue( (byte)27
     * )</tt> yields <tt>"1b"</tt>. Note that the returned string is always two characters long.
     *
     * @param v the byte value whose hex representation is to be returned.
     * @return the hex representation of <tt>v</tt>.
     */

    @SuppressWarnings({ "UnnecessaryParentheses", "MagicNumber" })
    public static String hexvalue( byte v ) {

        int high = ( v >> 4 ) & 0xf;
        int low = (int)v & 0xf;

        return ""
               + "0123456789abcdef".charAt( high )
               + "0123456789abcdef".charAt( low );

    }

    /**
     * Convert a byte array to its hexadecimal representation. For example, <tt>hexvalue( new byte[] { 1, 10, 100 }
     * )</tt> yields <tt>"010a64"</tt>. Note that the returned string always contains twice as many characters as the
     * input array contains bytes.
     * <p/>
     * While this method uses a {@link StringBuffer} to avoid creating lots and lots of dead strings, it will still
     * consume quite a bit of memory if the byte array is sufficiently large.
     *
     * @param bv the byte array to be converted.
     * @return the hex representation of <tt>v</tt>.
     */

    public static String hexvalue( byte[] bv ) {

        if ( bv == null ) {

            return "null";

        }

        StringBuilder rval = new StringBuilder();
        for ( byte b : bv ) {

            rval.append( ObtuseUtil5.hexvalue( b ) );

        }

        return rval.toString();

    }

    /**
     * Sleep for specified number of milliseconds without having to bother catching the InterruptedException potentially
     * thrown by the regular sleep method.
     *
     * @param milliseconds the minimum number of milliseconds that the calling thread should be suspended for (the
     *                     thread could conceivably be suspended for an arbitrary number of additional milliseconds
     *                     depending upon far too many factors to enumerate here).
     */

    public static void safeSleepMillis( long milliseconds ) {

        try {

            Thread.sleep( milliseconds );

        } catch ( InterruptedException e ) {

            //noinspection CallToPrintStackTrace
            e.printStackTrace();

        }

    }

    /**
     * Dump a byte array in hex - prints the contents of a byte array onto {@link System#out} in geek-readable form.
     *
     * @param data the byte array to be formatted and printed onto {@link System#out}.
     */

    @SuppressWarnings({ "MagicNumber" })
    public static void dump( byte[] data ) {

        for ( int offset = 0; offset < data.length; offset += 16 ) {

            StringBuilder rval = new StringBuilder( ObtuseUtil5.hexvalue( offset ) ).append( " " );
            for ( int j = 0; j < 16; j += 1 ) {

                if ( j % 4 == 0 ) {

                    rval.append( ' ' );

                }

                if ( offset + j < data.length ) {

                    rval.append( ObtuseUtil5.hexvalue( data[offset + j] ) );

                } else {

                    rval.append( "  " );

                }

            }

            rval.append( " *" );

            for ( int j = 0; j < 16 && offset + j < data.length; j += 1 ) {

                byte b = data[offset + j];
                //noinspection ImplicitNumericConversion
                if ( b < ' ' || b > '~' ) {

                    rval.append( '.' );

                } else {

                    rval.append( (char)b );

                }

            }

            Logger.logMsg( rval.append( "*" ).toString() );

        }

    }

    /**
     * Escape ampersands and less-than characters in a string using HTML-style &amp;amp; and &amp;lt; constructs.
     *
     * @param str the string to be escaped.
     * @return the escaped string.
     */

    public static String htmlEscape( String str ) {

        String rval = "";
        String s = str;
        while ( true ) {

            int ix1 = s.indexOf( (int)'&' );
            int ix2 = s.indexOf( (int)'<' );
            if ( ix1 < 0 && ix2 < 0 ) {

                break;

            }

            int ix;
            if ( ix1 < 0 ) {

                ix = ix2;

            } else if ( ix2 < 0 ) {

                ix = ix1;

            } else {

                ix = ix1 < ix2 ? ix1 : ix2;

            }

            rval += s.substring( 0, ix );
            s = s.substring( ix );
            if ( s.startsWith( "&" ) ) {

                rval += "&amp;";

            } else {

                rval += "&lt;";

            }

            s = s.substring( 1 );

        }

        rval += s;

        return rval;

    }

    /**
     * Close something while ignoring any {@link IOException}s.
     *
     * @param thing the thing to be closed.
     */

    public static void closeQuietly( Closeable thing ) {

        try {

            if ( thing != null ) {

                thing.close();

            }

        } catch ( IOException e ) {

            // Ignore close failures.

        }

    }

    public static void closeQuietly( ServerSocket sock ) {

        try {

            if ( sock != null ) {

                sock.close();

            }

        } catch ( IOException e ) {

            // Ignore close failures.

        }

    }

    public static void closeQuietly( Socket sock ) {

        try {

            if ( sock != null ) {

                sock.close();

            }

        } catch ( IOException e ) {

            // Ignore close failures.

        }

    }

    public static void closeQuietly( ZipFile zipFile ) {

        try {

            if ( zipFile != null ) {

                zipFile.close();

            }

        } catch ( IOException e ) {

            // Ignore close failures.

        }

    }

    public static void closeQuietly( ResultSet rs ) {

        try {

            if ( rs != null ) {

                rs.close();

            }

        } catch ( SQLException e ) {

            Logger.logErr( "close of result set failed", e );

        }

    }

    public static void closeQuietly( PreparedStatement rs ) {

        try {

            if ( rs != null ) {

                rs.close();

            }

        } catch ( SQLException e ) {

            Logger.logErr( "close of prepared statement failed", e );

        }

    }

    public static void closeQuietly( PostgresConnection postgresConnection ) {

        try {

            postgresConnection.close();

        } catch ( SQLException e ) {

            Logger.logErr( "close of PostgresConnection failed", e );

        }
    }

    /**
     * A method which deliberately does nothing.
     * Useful as a statement upon which to set a breakpoint.
     */

    public static void doNothing() {

    }

    /**
     * Use double quotes to escape commas and quotes in the way that MS Excel seems to do it.
     * <p/>
     * Some examples are probably in order.
     * <ul>
     * <li>if the string is
     * <blockquote><tt>hello world</tt></blockquote>
     * then the output is the string itself (unchanged).
     * <li>if the string is
     * <blockquote><tt>hello,world</tt></blockquote>
     * then the output is
     * <blockquote><tt>"hello,world"</tt></blockquote>
     * <li>if the string is
     * <blockquote><tt>"hello" world</tt></blockquote>
     * then the output is
     * <blockquote><tt>"""hello"" world"</tt></blockquote>
     * <li>if the string is
     * <blockquote><tt>hello ",world"</tt></blockquote>
     * the the output is
     * <blockquote><tt>"hello "",world"""</tt></blockquote>
     * </ul>
     * Note that all the double quotes in the above examples actually appear in the strings.
     * In other words, the above examples do <b>NOT</b> use double quotes to enclose strings.
     *
     * @param string the string to be enquoted.
     * @return the original string if it does not contain quotes or commas; the enquoted string otherwise.
     * @throws NullPointerException if <tt>string</tt> is null.
     */

    public static String enquoteForCSV( String string ) {

        if ( !string.contains( "," ) && !string.contains( "\"" ) ) {

            return string;

        }

        StringBuilder rval = new StringBuilder( "\"" );
        int quoteOffset = -1;
        while ( true ) {

            int newQuoteOffset = string.indexOf( '"', quoteOffset + 1 );

            if ( newQuoteOffset < 0 ) {

                break;

            }

            rval.append( string.substring( quoteOffset + 1, newQuoteOffset + 1 ) ).append( '"' );

            quoteOffset = newQuoteOffset;

        }

        return rval.append( string.substring( quoteOffset + 1 ) ).append( '"' ).toString();

    }

    public static String enquoteForJavaString( String string ) {

        if ( string == null ) {

            return "null";

        }

        StringBuilder rval = new StringBuilder( "\"" );
        for ( char c : string.toCharArray() ) {

            switch ( c ) {

                case '\n':
                    rval.append( "\\n" );
                    break;

                case '\r':
                    rval.append( "\\r" );
                    break;

                case '\t':
                    rval.append( "\\t" );
                    break;

                case '\\':
                    rval.append( "\\\\" );
                    break;

                case '"':
                    rval.append( "\\\"" );
                    break;

                default:
                    rval.append( c );

            }

        }

        rval.append( '"' );
        return rval.toString();

    }

    private static MessageDigest s_md5Algorithm = null;
    @SuppressWarnings("ConstantNamingConvention")
    private static final Long _md5Lock = 0L;

    public static String computeMD5( InputStream is )
            throws IOException {

        synchronized ( ObtuseUtil5._md5Lock ) {

            if ( ObtuseUtil5.s_md5Algorithm == null ) {

                MessageDigest alg;
                try {

                    alg = MessageDigest.getInstance( "MD5" );

                } catch ( NoSuchAlgorithmException e ) {

                    // There's no real risk that this can happen is there?

                    throw new IllegalArgumentException( "This version of Java does not support MD5 checksums" );

                }

                ObtuseUtil5.s_md5Algorithm = alg;

            }

            BufferedInputStream fis = null;

            try {

                ObtuseUtil5.s_md5Algorithm.reset();
                fis = new BufferedInputStream( is );

                //noinspection MagicNumber
                byte[] buffer = new byte[1024];
                while ( true ) {

                    int rLen = fis.read( buffer );
                    if ( rLen < 0 ) {

                        break;

                    }

                    ObtuseUtil5.s_md5Algorithm.update( buffer, 0, rLen );

                }

                byte[] digest = ObtuseUtil5.s_md5Algorithm.digest();

                return ObtuseUtil5.hexvalue( digest );

            } finally {

                ObtuseUtil5.closeQuietly( fis );

            }
        }

    }

    public static String computeMD5( File file )
            throws IOException {

        FileInputStream fis = new FileInputStream( file );
        try {

            return ObtuseUtil5.computeMD5( fis );

        } finally {

            ObtuseUtil5.closeQuietly( fis );

        }

    }

    public static int safeDivide( int numerator, int denominator ) {

        return denominator == 0 ? 0 : numerator / denominator;

    }

    public static int safeDivide( int numerator, int denominator, int safeReturnValue ) {

        return denominator == 0 ? safeReturnValue : numerator / denominator;

    }

    public static double safeDivide( double numerator, double denominator ) {

        return denominator == 0 ? 0 : numerator / denominator;

    }

    public static double safeDivide( double numerator, double denominator, double safeReturnValue ) {

        return denominator == 0.0 ? safeReturnValue : numerator / denominator;

    }

    /**
     * Add the contents of an array to a collection and return the collection.
     * <p/>Returning the collection facilitates certain constructs including:
     * <blockquote>
     * <tt>doit( ObtuseUtil5.addAll( new LinkedList&lt;String>(), new String[] { "hello", "there", "world" } );</tt>
     * </blockquote>
     *
     * @param collection  the collection to which things are to be added.
     * @param newElements the new elements to add to the collection.
     * @return the collection after the elements have been added.
     */

    public static <T> Collection<T> addAll(
            Collection<T> collection,
            T... newElements
    ) {

        Collections.addAll( collection, newElements );
        return collection;

    }

//    private static void doit( String s ) {
//
//        System.out.println( "<" + s + "> yielded <" + enquoteForCSV( s ) + ">" );
//
//    }
//
//    public static void main( String[] args ) {
//
//        doit( "hello world" );
//        doit( "" );
//        doit( "\"hello world\"" );
//        doit( "hello,world" );
//        doit( "\"\"\"\"" );
//        doit( "\"hello,world\"" );
//        doit( "\"hello world" );
//        doit( "hello world\"" );
//        System.exit( 0 );
//
//    }

    /**
     * Returns an unmodifiable view of the specified hash table.
     * This method allows modules to provide users with "read-only" access to internal hash tables (including {@link
     * Dictionary}s).
     * Query operations on the returned hash table "read through" to the specified hash table,
     * and attempts to modify the returned hash table, whether direct or via its collection views,
     * result in an UnsupportedOperationException.
     *
     * @param ht the hash table for which an unmodifiable view is to be returned.
     * @return an unmodifiable view of the specified hash table.
     */

    @SuppressWarnings({ "CollectionDeclaredAsConcreteClass", "UseOfObsoleteCollectionType" })
    public static <K, V> Hashtable<K, V> unmodifiableHashtable( final Hashtable<? extends K, ? extends V> ht ) {

        UnmodifiableHashtable<K, V> unmodifiableHashtable = new UnmodifiableHashtable<K, V>( ht );

        unmodifiableHashtable.makeReadonly();

        return unmodifiableHashtable;

    }

}
