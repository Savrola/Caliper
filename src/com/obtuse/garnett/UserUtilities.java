package com.obtuse.garnett;

import com.obtuse.garnett.exceptions.*;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil5;
import com.obtuse.util.exceptions.HowDidWeGetHereError;
import com.obtuse.util.exceptions.ObtuseProtocolErrorException;

import java.nio.CharBuffer;
import java.util.Random;
import java.util.regex.Pattern;

/*
 * Copyright © 2012 Daniel Boulet
 */

public class UserUtilities {

    // Every printable ASCII character except spaces, front slashes and back slashes are allowed in usernames.
    // The username must also be a valid email address which implicitly places additional constraints although
    // these are for the most part not checked by us.

    private static final Pattern VALID_USER_NAME_PATTERN = Pattern.compile( "[!-~]*" );

    private static final String FORBIDDEN_USER_NAME_CHARACTERS = "\\/ ";

    // A very rudimentary check for a valid email address.

    private static final Pattern VALID_EMAIL_ADDRESS_PATTERN = Pattern.compile( "[^@]+@[^@]+\\.[^@]+" );

    // Every printable ASCII character including spaces are allowed in passwords.

    private static final Pattern VALID_PASSWORD_PATTERN = Pattern.compile( "[ -~]*" );

    /**
     * Specifies whether or not debug-mode is enabled. If true then debug output is written to stdout.
     */

    private static boolean _debugMode = false;

    /**
     * The current versions of the obfuscation algorithm implemented by this class.
     */

    public static final byte OBFUSCATION_VERSION_PORTABLE = (byte) 3;    // Obfuscation based on a more portable random number generator

    /**
     * The size of the header on an obfuscated array. ElephantRMIImpl relies on this value not changing!!!
     */

    public static final int OBFUSCATED_HEADER_SIZE = 3;

    /**
     * The characters which may appear in a string which is to be obfuscated.
     */

    public static final String VALID_CHARACTERS =
            " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

    private UserUtilities() {
        super();
    }

    /**
     * Determine if a user name is valid. A valid user name must start with a letter and only contain letters, digits
     * and underscores.
     *
     * @param name the user name to be validated.
     * @throws com.obtuse.garnett.exceptions.GarnettInvalidAccountNameException
     *          if the user name is not valid.
     */

    public static void validateAccountName( String name )
            throws
            GarnettInvalidAccountNameException {

        if ( name.length() < 1 ) {
            throw new GarnettInvalidAccountNameException( "\"" + name + "\" is too short" );
        }

        if ( "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf( (int) name.charAt( 0 ) ) < 0 ) {
            throw new GarnettInvalidAccountNameException( "\"" + name + "\" does not start with a letter" );
        }

        if ( !VALID_USER_NAME_PATTERN.matcher( name ).matches() ) {
            throw new GarnettInvalidAccountNameException( "\"" + name + "\" contains invalid characters" );
        }

        for ( char ch : FORBIDDEN_USER_NAME_CHARACTERS.toCharArray() ) {
            if ( name.indexOf( (int) ch ) >= 0 ) {
                throw new GarnettInvalidAccountNameException( "\"" + name + "\" contains invalid characters" );
            }
        }

    }

    /**
     * Apply stronger rules to new user names (insist that they truly resemble an email address).
     * <p/>
     * A username is considered to be valid if it satisfies {@link #validateAccountName} and matches the regular
     * expression <blockquote> [^@]+@[^@]+\.[^@]+ </blockquote> %%% This regular expression is absurdly permissive and
     * needs to get tightened up eventually.
     *
     * @param name     the username to be checked.
     * @param internal true if the name is a component name.
     * @throws com.obtuse.garnett.exceptions.GarnettInvalidAccountNameException
     *          if the username is not valid.
     */

    public static void validateNewAccountName( String name, boolean internal )
            throws
            GarnettInvalidAccountNameException {

        validateAccountName( name );

        if ( internal ) {

            if ( !name.endsWith( "@internal" ) ) {

                throw new GarnettInvalidAccountNameException(
                        "\"" + name + "\" has an invalid suffix for an internal name"
                );

            }

        } else {

            if ( !VALID_EMAIL_ADDRESS_PATTERN.matcher( name ).matches() ) {

                throw new GarnettInvalidAccountNameException( "\"" + name + "\" is not a valid email address" );

            }

        }

    }

    /**
     * Determine if a user name is valid.
     *
     * @param name the user name to be validated.
     * @return true if the user name is valid according to the rules described at {@link #validateAccountName}; false
     *         otherwise.
     */

    public static boolean isValidUserName( String name ) {

        try {

            validateAccountName( name );
            return true;

        } catch ( GarnettInvalidAccountNameException e ) {

            return false;

        }

    }

    /**
     * Determine if a new user name is valid.
     *
     * @param name     the user name to be validated.
     * @param internal true if the name is a component name.
     * @return true if the user name is valid according to the rules described at {@link #validateNewAccountName}; false
     *         otherwise.
     */

    public static boolean isValidNewUserName( String name, boolean internal ) {

        try {

            validateNewAccountName( name, internal );
            return true;

        } catch ( GarnettInvalidAccountNameException e ) {

            return false;

        }

    }

    /**
     * Clears a character array to 0's. This method is intended to be used to destroy in-memory copies of passwords once
     * they are no longer needed.
     *
     * @param chars the array to be cleared.
     */

    public static void zap( char[] chars ) {

        if ( chars == null ) {

            return;

        }

        for ( int i = 0; i < chars.length; i += 1 ) {

            chars[i] = (char) 0;

        }

    }

    /**
     * Clears a byte array to 0's. This method is intended to be used to destroy in-memory obfuscated copies of
     * passwords once they are no longer needed.
     *
     * @param bytes the array to be cleared.
     */

    public static void zap( byte[] bytes ) {

        if ( bytes == null ) {

            return;

        }

        for ( int i = 0; i < bytes.length; i += 1 ) {

            bytes[i] = (byte) 0;

        }

    }

    /**
     * Determine if a password is valid. Note that this is a very minimalist test for validity. In particular, this
     * method does not make any judgement regarding the strength of the password.
     *
     * @param password      the password to be validated.
     * @param isNewPassword is this a new or an existing password?
     * @throws com.obtuse.garnett.exceptions.GarnettInvalidPasswordException
     *          if the password fails validation (the message in the exception will explain what was wrong without
     *          actually revealing the password). Note that a GarnettPasswordTooShortException (which is a sub-class
     *          of {@link com.obtuse.garnett.exceptions.GarnettInvalidPasswordException}) is thrown if the password is too short but is otherwise
     *          valid.
     */

    @SuppressWarnings({ "UnusedDeclaration" })
    public static void validatePassword( char[] password, boolean isNewPassword )
            throws
            GarnettInvalidPasswordException {

        CharBuffer cb = CharBuffer.wrap( password );
        if ( !VALID_PASSWORD_PATTERN.matcher( cb ).matches() ) {

            throw new GarnettInvalidPasswordException( "password contains invalid characters" );

        }

        if ( password.length < 6 ) {

            throw new GarnettPasswordTooShortException( "password is too short" );

        }

    }

    public static boolean isValidPassword( char[] password, boolean newPassword ) {

        try {

            validatePassword( password, newPassword );
            return true;

        } catch ( GarnettInvalidPasswordException e ) {

            return false;

        }

    }

    private static void checkUser( String user, boolean internal ) {

        try {

            validateAccountName( user );
            validateNewAccountName( user, internal );
            Logger.logMsg( "username \"" + user + "\" is valid" );

        } catch ( GarnettInvalidAccountNameException e ) {

            Logger.logMsg( "username \"" + user + "\" is not valid:  " + e.getMessage() );

        }

    }

    private static void checkPassword( String password ) {

        try {

            validatePassword( password.toCharArray(), true );
            Logger.logMsg( "password \"" + password + "\" is valid" );

        } catch ( GarnettInvalidPasswordException e ) {

            Logger.logMsg( "password \"" + password + "\" is not valid:  " + e.getMessage() );

        }

    }

    /**
     * Obfuscate a user name while verifying that it is acceptable. This is intended to be used to 'protect' the user
     * name from a developer looking at a hexdump of a message. It is not intended to hide anything from a villian (we
     * rely on SSL for that).
     *
     * @param original the characters to be obfuscated.
     * @return the obfuscated characters.
     * @throws com.obtuse.garnett.exceptions.GarnettInvalidAccountNameException
     *          if the user name contains verbotten characters.
     */

    public static byte[] obfuscateAccountName( char[] original )
            throws
            GarnettInvalidAccountNameException {

        validateAccountName( new String( original ) );

        try {

            return obfuscate( original );

        } catch ( GarnettInvalidCharacterException e ) {

            throw new GarnettInvalidAccountNameException( "contains illegal characters", e );

        }

    }

    /**
     * Obfuscate a user name while verifying that it is acceptable. This is intended to be used to 'protect' the user
     * name from a developer looking at a hexdump of a message. It is not intended to hide anything from a villian (we
     * rely on SSL for that).  Uses the obfuscation algorithm of your choice (presumably the algorithm preferred
     * by the eventual elucidator of the obfuscation).
     *
     * @param original the characters to be obfuscated.
     * @param version  the algorithm you want to use to obfuscate.
     * @return the obfuscated characters.
     * @throws com.obtuse.garnett.exceptions.GarnettInvalidAccountNameException
     *          if the user name contains verbotten characters.
     */

    public static byte[] obfuscateAccountName( char[] original, int version )
            throws
            GarnettInvalidAccountNameException {

        validateAccountName( new String( original ) );

        try {

            return obfuscate( original, version );

        } catch ( GarnettInvalidCharacterException e ) {

            throw new GarnettInvalidAccountNameException( "contains illegal characters", e );

        }

    }

    /**
     * Obfuscate a password while verifying that it is acceptable. This is intended to be used to 'protect' the password
     * from a developer looking at a hexdump of a message. It is not intended to hide anything from a villian (we rely
     * on SSL for that).
     *
     * @param original      the characters to be obfuscated.
     * @param isNewPassword is this a new password or an existing password.
     * @return the obfuscated characters.
     * @throws com.obtuse.garnett.exceptions.GarnettInvalidPasswordException
     *          if the password contains verbotten characters.
     */

    public static byte[] obfuscatePassword( char[] original, boolean isNewPassword )
            throws
            GarnettInvalidPasswordException {

        try {

            // Check if the password is valid.
            // This could throw a GarnettInvalidPasswordException which will terminate this method.
            // It could also throw a GarnettPasswordTooShortException which we'll ignore.

            validatePassword( original, isNewPassword );

        } catch ( GarnettPasswordTooShortException e ) {

            // Don't worry about passwords which are too short here.
            // Logger.logMsg("password too short but otherwise ok");

        }

        // Although the password is otherwise valid, it could be too short (which is okay for us).
        // Time to obfuscate it.

        try {

            return obfuscate( original );

        } catch ( GarnettInvalidCharacterException e ) {

            throw new HowDidWeGetHereError( "password with invalid characters not detected by validatePassword", e );

        }

    }

    /**
     * Obfuscate a string while verifying that it is acceptable. This is intended to be used to 'protect' a username or
     * a password from a developer looking at a hexdump of a message. It is not intended to hide anything from a villian
     * (we rely on SSL for that).  Uses the original Java-specific algorithm.
     *
     * @param original the characters to be obfuscated.
     * @return the obfuscated characters.
     * @throws com.obtuse.garnett.exceptions.GarnettInvalidCharacterException
     *          if <tt>original</tt> contains characters which are not ASCII printable characters.
     */

    public static byte[] obfuscate( char[] original )
            throws
            GarnettInvalidCharacterException {

        //noinspection MagicNumber
        return obfuscate( original, (int) OBFUSCATION_VERSION_PORTABLE );

    }

    /**
     * Obfuscate a string using the algorithm version of your choice. (Actually,
     * you're not allowed to use NONE ... see the obfuscate() method that
     * does the work.
     *
     * @param original the characters to be obfuscated.
     * @param version  the algorithm you want to use to obfuscate.
     * @return the obfuscated characters.
     * @throws com.obtuse.garnett.exceptions.GarnettInvalidCharacterException
     *          if <tt>original</tt> contains characters which are not ASCII printable characters.
     */

    public static byte[] obfuscate( char[] original, int version )
            throws
            GarnettInvalidCharacterException {

        if ( version == (int) OBFUSCATION_VERSION_PORTABLE ) {

            //noinspection MagicNumber
            return obfuscate( original, GarnettObfuscationRandom.portableSeed & 0x0000ffff, version );

        }

        throw new HowDidWeGetHereError( "request to use an unsupported obfuscation algorithm" );
    }

    /**
     * Obfuscate a password using the same seed as was used to obfuscate another array.
     *
     * @param original   the characters to be obfuscated.
     * @param seedSource the already obfuscated other array.
     * @return the obfuscated original.
     * @throws com.obtuse.garnett.exceptions.GarnettInvalidPasswordException
     *          if <tt>original</tt> contains characters which are not ASCII printable characters.
     */

    public static byte[] obfuscatePassword( char[] original, byte[] seedSource )
            throws
            GarnettInvalidPasswordException {

        //noinspection UnnecessaryParentheses,MagicNumber
        int seed = ( ( seedSource[1] & 0xff ) << 8 ) | ( seedSource[2] & 0xff );

        try {

            //noinspection MagicNumber
            return obfuscate( original, seed, seedSource[0] & 0xff );

        } catch ( GarnettInvalidCharacterException e ) {

            throw new GarnettInvalidPasswordException( "password contains illegal characters", e );

        }

    }

    /**
     * The real obfuscator.
     *
     * @param original the character array to be obfuscated.
     * @param seed     the seed to use to do the obfuscation.
     * @param version  the version of obfuscation algorithm to use.
     * @return the obfuscated string.
     * @throws com.obtuse.garnett.exceptions.GarnettInvalidCharacterException
     *          if <tt>original</tt> contains characters which are not ASCII printable characters.
     */

    public static byte[] obfuscate( char[] original, int seed, int version )
            throws
            GarnettInvalidCharacterException {

        byte[] rval;
        if ( (int) version == (int) OBFUSCATION_VERSION_PORTABLE ) {

            rval = new byte[OBFUSCATED_HEADER_SIZE + original.length];

            Random obfuscator = new GarnettObfuscationRandom( (long)seed );
            obfuscator.nextBytes( rval );
            if ( _debugMode ) {

                Logger.logMsg( "hash:" );
                ObtuseUtil5.dump( rval );

            }

            rval[0] = (byte) version;
            rval[1] = (byte) ( seed >> 8 );
            //noinspection MagicNumber
            rval[2] = (byte) ( seed & 0xff );

            if ( _debugMode ) {

                Logger.logMsg( "original seed is " + seed );

            }

            for ( int i = 0; i < original.length; i += 1 ) {

                char ch = original[i];
                int ix = VALID_CHARACTERS.indexOf( (int) ch );
                if ( ix < 0 ) {

                    throw new GarnettInvalidCharacterException(
                            "user names and passwords must contain printable ASCII characters"
                    );

                }

                rval[3 + i] ^= (byte) ix;

            }

//        } else if ( (int) version == (int) OBFUSCATION_VERSION_NONE ) {
//
//            if ( GenericAutomagicUpgrader.isDefaultDeployment() ) {
//
//                throw new IllegalArgumentException( "clear text obfuscation not supported in default deployment" );
//
//            }
//
//            rval = new byte[OBFUSCATED_HEADER_SIZE + original.length];  // header is used for version only
//
//            rval[0] = OBFUSCATION_VERSION_NONE;
//            rval[1] = (byte) 0;
//            rval[2] = (byte) 0;
//
//            for ( int i = 0; i < original.length; i += 1 ) {
//
//                char ch = original[i];
//                int ix = VALID_CHARACTERS.indexOf( (int) ch );
//                if ( ix < 0 ) {
//
//                    throw new GarnettInvalidCharacterException(
//                            "user names and passwords must contain printable ASCII characters"
//                    );
//
//                }
//
//                rval[OBFUSCATED_HEADER_SIZE + i] = (byte) VALID_CHARACTERS.charAt( ix );
//
//            }

        } else {

            throw new HowDidWeGetHereError( "request to use an unsupported obfuscation algorithm" );

        }

        return rval;
    }

    /**
     * Recover a previously obfuscated character array. See {@link #obfuscate} for more info.
     *
     * @param obfuscated the obfuscated data which is to be recovered.
     * @return the 'clear text' version of the obfuscated data.
     * @throws com.obtuse.util.exceptions.ObtuseProtocolErrorException
     *          if the obfuscated data has been encoded using an obfuscation scheme that this method does not support or
     *          if one of the obfuscated bytes cannot be mapped back into a valid character.
     */

    public static char[] elucidate( byte[] obfuscated )
            throws
            ObtuseProtocolErrorException {

        char[] rval;
        byte obfuscationAlgorithmVersion = obfuscated[0];

        if ( obfuscationAlgorithmVersion == OBFUSCATION_VERSION_PORTABLE ) {

            rval = new char[obfuscated.length - 3];

            //noinspection UnnecessaryParentheses,MagicNumber
            int seed = ( ( obfuscated[1] & 0xff ) << 8 ) | ( obfuscated[2] & 0xff );

            if ( _debugMode ) {

                Logger.logMsg( "recovered seed is " + seed );

            }

            Random obfuscator = new GarnettObfuscationRandom( (long)seed );
            byte[] hash = new byte[obfuscated.length];
            obfuscator.nextBytes( hash );
            if ( _debugMode ) {

                Logger.logMsg( "hash:" );
                ObtuseUtil5.dump( hash );

            }

            for ( int i = 0; i < rval.length; i += 1 ) {

                try {

                    int ix = hash[3 + i] ^ obfuscated[3 + i];
                    rval[i] = VALID_CHARACTERS.charAt( ix );

                } catch ( IndexOutOfBoundsException e ) {

                    //noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException
                    throw new ObtuseProtocolErrorException( "invalid byte at offset " + ( 3 + i ) );

                }

            }

//        } else if ( obfuscationAlgorithmVersion == OBFUSCATION_VERSION_NONE ) {
//
//            if ( GenericAutomagicUpgrader.isDefaultDeployment() ) {
//
//                throw new IllegalArgumentException( "clear text de-obfuscation not supported in default deployment" );
//
//            }
//
//            rval = new char[obfuscated.length - OBFUSCATED_HEADER_SIZE];
//
//            for ( int i = 0; i < rval.length; i += 1 ) {
//
//                int ix = obfuscated[OBFUSCATED_HEADER_SIZE + i];
//                rval[i] = (char) ix;         // assumes US-ASCII, clients must concur!
//
//            }

        } else {

            throw new ObtuseProtocolErrorException( "invalid obfuscation version (" + obfuscationAlgorithmVersion + ")" );

        }

        return rval;

    }


    private static void checkObfuscation( String testData )
            throws GarnettInvalidCharacterException, ObtuseProtocolErrorException {

        byte[] ob;
        char[] an;

//        ob = obfuscate( testData.toCharArray(), 42, OBFUSCATION_VERSION_NONE );
//        assert ( ob[0] == OBFUSCATION_VERSION_NONE );
//        assert ( ob.length == testData.length() + OBFUSCATED_HEADER_SIZE );
//        an = elucidate( ob );
//        assert ( an.length == testData.length() );
//        assert ( ( new String( an ) ).equals( testData ) );
//        Logger.logMsg( testData + " == " + new String( an ) );

        ob = obfuscate( testData.toCharArray(), 42, OBFUSCATION_VERSION_PORTABLE );
        assert ( ob[0] == OBFUSCATION_VERSION_PORTABLE );
        assert ( ob.length == testData.length() + OBFUSCATED_HEADER_SIZE );
        an = elucidate( ob );
        assert ( an.length == testData.length() );
        assert ( ( new String( an ) ).equals( testData ) );
        Logger.logMsg( testData + " == " + new String( an ) );

    }


    public static void main( String[] args ) throws ObtuseProtocolErrorException {

        checkUser( "danny", false );
        checkUser( "dan ny", false );
        checkUser( "dan/ny", false );
        checkUser( "dan\\ny", false );
        checkUser( "{danny}", false );
        checkUser( "danny@matilda.com", false );
        checkUser( "danny@", false );
        checkUser( "@matilda.com", false );
        checkUser( "danny@matilda", false );
        checkPassword( "hello" );
        checkPassword( "abcdef" );
        checkPassword( "hello there world" );
        checkPassword( "{testing}" );
        checkPassword( "copyright ©" );
        Logger.logMsg( "© == " + ObtuseUtil5.hexvalue( "©".getBytes() ) );

        try {
            // A couple of quick tests that use the various algorithms...
            byte[] ob = obfuscateAccountName( "AnAccountName".toCharArray() );
            char[] an = elucidate( ob );
            assert ( ( new String( an ) ).equals( "AnAccountName" ) );

//            ob = obfuscateAccountName( "AnAccountName".toCharArray(), OBFUSCATION_VERSION_NUMBER );
//            an = elucidate( ob );
//            assert ( ( new String( an ) ).equals( "AnAccountName" ) );
//
//            ob = obfuscateAccountName( "AnAccountName".toCharArray(), OBFUSCATION_VERSION_NONE );
//            an = elucidate( ob );
//            assert ( ( new String( an ) ).equals( "AnAccountName" ) );

            ob = obfuscateAccountName( "AnAccountName".toCharArray(), OBFUSCATION_VERSION_PORTABLE );
            an = elucidate( ob );
            assert ( ( new String( an ) ).equals( "AnAccountName" ) );

            ob = obfuscatePassword( "A password".toCharArray(), false );
            an = elucidate( ob );
            assert ( ( new String( an ) ).equals( "A password" ) );

            // If client obfuscates using clear text, make sure server can elucidate
            // it correctly. Server might internally re-obfuscate using original algorithm
            // but this is OK as long as it isn't sent back to client that way (see above).
            // There are a couple cases where this algorithm is used by the server
            // side in responses that are sent back to clients (e.g. obfuscated account
            // name is sometimes included in error responses).
            // TODO: figure out a way to respect the client's obfuscation algorithm preference in these cases
            //       step one has been done by adding an obfuscateAccountName() method that takes a version argument
            //       make sure that account name is the only instance of this
            //       need to get client's preference still...maybe a separate SLC message or augmented data to login that tells us?
            //       where to store client's preference once we have it?
            //       finally ... do clients really need this now anyway?  The current server obfuscation works
            //       with the Java clients and the Mac client doesn't use the obfuscated account ids that are sent
            //       from server to client.
            checkObfuscation( "A String of Characters" );
            checkObfuscation( "" );
            checkObfuscation( "a" );
            Boolean gotIt = false;
            try {
                checkObfuscation( "a with ©©©© weird characters" );
            } catch ( GarnettInvalidCharacterException e ) {
                gotIt = true;
            }
            assert ( gotIt );
        } catch ( GarnettInvalidCharacterException e ) {
            e.printStackTrace();
        } catch ( GarnettInvalidAccountNameException e ) {
            e.printStackTrace();
        } catch ( GarnettInvalidPasswordException e ) {
            e.printStackTrace();
        } catch ( ObtuseProtocolErrorException e ) {
            e.printStackTrace();
        }

        System.exit( 1 );

    }

}
