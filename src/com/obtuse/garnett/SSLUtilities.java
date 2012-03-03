package com.obtuse.garnett;

import com.obtuse.garnett.exceptions.GarnettSSLChannelCreationFailedException;
import com.obtuse.util.ResourceUtils;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

/*
 * Copyright © 2012 Daniel Boulet
 */

public class SSLUtilities {

    private static final Map<SSLContextWrapper, SSLContextWrapper> _sslContexts =
            new HashMap<SSLContextWrapper, SSLContextWrapper>();

//    private static class MyTrustManager implements X509TrustManager {
//
//        private final X509TrustManager _realTrustManager;
//
//        private X509Certificate[] _certChain;
//
//        private String _authType;
//
//        private MyTrustManager( X509TrustManager realTrustManager ) {
//            super();
//
//            _realTrustManager = realTrustManager;
//
//        }
//
//        public X509Certificate[] getAcceptedIssuers() {
//
//            throw new UnsupportedOperationException( "we don't support getting the accepted issuers" );
//
//        }
//
//        public void checkClientTrusted( X509Certificate[] certChain, String authType ) {
//
//            throw new UnsupportedOperationException( "we don't support checking if the client is trustworthy" );
//
//        }
//
//        public void checkServerTrusted( X509Certificate[] certChain, String authType )
//                throws CertificateException {
//
//            _certChain = certChain;
//            _authType = authType;
//
//            X509Certificate[] certs = _certChain;
//            if ( certs == null ) {
//
//                Logger.logErr( "no certs captured" );
//
//            } else {
//
//                try {
//
//                    MessageDigest sha1 = MessageDigest.getInstance( "SHA1" );
//                    MessageDigest md5 = MessageDigest.getInstance( "MD5" );
//
//                    Logger.logMsg( "here are the certs:" );
//                    Logger.logMsg( "" );
//
//                    for ( X509Certificate cert : certs ) {
//
//                        Logger.logMsg( "Subject:        " + cert.getSubjectX500Principal() );
//                        Logger.logMsg( "Issuer:         " + cert.getIssuerX500Principal() );
//                        Logger.logMsg(
//                                "effective:      from " + cert.getNotBefore() + " through " + cert.getNotAfter()
//                        );
//                        Logger.logMsg( "serial number:  " + cert.getSerialNumber() );
//                        Logger.logMsg( "sig algorithm:  " + cert.getSigAlgName() );
//                        Logger.logMsg( "version:        " + cert.getVersion() );
//
//                        sha1.update( cert.getEncoded() );
//                        Logger.logMsg( "SHA1:     " + ObtuseUtil5x.hexvalue( sha1.digest() ) );
//
//                        md5.update( cert.getEncoded() );
//                        Logger.logMsg( "MD5:      " + ObtuseUtil5x.hexvalue( md5.digest() ) );
//
//                        Logger.logMsg( "serialized form is " + ObtuseUtil5x.getSerializedSize( cert ) + " bytes long" );
//                        Logger.logMsg( "encoded form is " + cert.getEncoded().length + " bytes long" );
//                        Logger.logMsg( "cert's class is " + cert.getClass() );
//
////                    _myIx += 1;
////                    ks.setCertificateEntry( "balzac-" + _myIx, cert );
//
////                    Logger.logMsg( "added to trusted certs" );
////                    Logger.logMsg( "" );
//
//                    }
//
//                } catch ( NoSuchAlgorithmException e ) {
//
//                    Logger.logErr( "got a NoSuchAlgorithmException looking for SHA1 or MD5 algorithm", e );
//
//                }
//
//            }
//
//            _realTrustManager.checkServerTrusted( certChain, authType );
//
//        }
//
//    }

    /**
     * Carry an {@link javax.net.ssl.SSLContext} around in a package that identifies it by the keystore that was used to create it.
     * This allows us to avoid having dozens of different {@link javax.net.ssl.SSLContext}s which all reference the same keystores.
     */

    private static class SSLContextWrapper {

        private SSLContext _sslContext;

        private final String _keystoreFname;

        private final char[] _keystorePassword;

        private SSLContextWrapper( String keystoreFname, char[] keystorePassword ) {
            super();

            _keystoreFname = keystoreFname;
            _keystorePassword = keystorePassword.clone();

        }

        private void setSSLContext( SSLContext sslContext ) {

            _sslContext = sslContext;

        }

        private SSLContext getSSLContext() {

            return _sslContext;

        }

        public int hashCode() {

            return _keystoreFname.hashCode() ^ new Integer( _keystorePassword.length ).hashCode();

        }

        @SuppressWarnings( { "EqualsWhichDoesntCheckParameterClass" } )
        public boolean equals( Object xrhs ) {

            try {

                SSLContextWrapper rhs = (SSLContextWrapper)xrhs;
                if ( rhs == null ) {

                    return false;

                }

                if ( _keystoreFname.equals( rhs._keystoreFname ) &&
                     _keystorePassword.length == rhs._keystorePassword.length ) {

                    for ( int i = 0; i < _keystorePassword.length; i += 1 ) {

                        if ( _keystorePassword[i] != rhs._keystorePassword[i] ) {

                            return false;

                        }

                    }

                    return true;

                }

                return false;

            } catch ( ClassCastException e ) {

                return false;

            }

        }

        public String toString() {

            return "SSLContextWrapper( keystore = " + _keystoreFname + " )";

        }

    }

    public SSLUtilities() {
        super();

    }

    /**
     * Get or create an {@link javax.net.ssl.SSLContext} associated with a specified keystore file and password.
     *
     * @param keystoreFname       the keystore file.
     * @param keystorePassword    its password.
     * @param keystoreInputStream an input stream referring to the keystore file.
     *
     * @return the SSL context associated with the keystore file and password.
     *
     * @throws com.obtuse.garnett.exceptions.GarnettSSLChannelCreationFailedException
     *          if the attempt fails.
     */

    private static SSLContext getSSLContext(
            String keystoreFname, InputStream keystoreInputStream, char[] keystorePassword
    )
            throws
            GarnettSSLChannelCreationFailedException {

        synchronized ( _sslContexts ) {

            SSLContextWrapper tmp = new SSLContextWrapper( keystoreFname, keystorePassword );
            if ( _sslContexts.containsKey( tmp ) ) {

//                Logger.logMsg( "reusing SSL cert(s) from " + keystoreFname );
                return _sslContexts.get( tmp ).getSSLContext();

            } else {

//                Logger.logMsg( "loading SSL cert(s) from " + keystoreFname );

                tmp.setSSLContext(
                        wrappedCreateSSLContext(
                                true,
                                keystoreInputStream,
                                keystorePassword,
                                null
                        )
                );

                _sslContexts.put( tmp, tmp );
                return tmp.getSSLContext();

            }

        }

    }

    public static SSLContext getOurClientSSLContext()
            throws GarnettSSLChannelCreationFailedException, IOException {

        return getSSLContext(
                "GarnettClient.keystore",
                ResourceUtils.openResource( "GarnettClient.keystore", "com.obtuse.garnett.resources" ),
                // new char[] { 'p', 'i', 'c', 'k', 'l', 'e', 's' }
                "pickles".toCharArray()
        );

    }

    private static SSLContext createSSLContext(
            boolean clientMode,
            InputStream keyStoreInputStream,
            char[] keystorePassword,
            char[] keyPassword
    )
            throws
            KeyStoreException,
            IOException,
            NoSuchAlgorithmException,
            CertificateException,
            KeyManagementException,
            UnrecoverableKeyException {

        KeyStore keyStore = KeyStore.getInstance( "JKS" );
        keyStore.load( keyStoreInputStream, keystorePassword );

        SSLContext sslContext = SSLContext.getInstance( "TLS" );

        if ( clientMode ) {

//            TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
//            tmf.init( keyStore );
//            TrustManager[] tms = tmf.getTrustManagers();
//            for ( TrustManager tm : tms ) {
//                Logger.logMsg( "got trust manager " + tm );
//            }
//
//            MyTrustManager myTrustManager = new MyTrustManager( (X509TrustManager)tms[0] );
//
//            sslContext.init( null, new TrustManager[] { myTrustManager }, null );

            TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
            tmf.init( keyStore );
            sslContext.init( null, tmf.getTrustManagers(), null );

        } else {

            KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
            kmf.init( keyStore, keyPassword );
            sslContext.init( kmf.getKeyManagers(), null, null );

        }

        return sslContext;

    }

    public static SSLContext wrappedCreateSSLContext(
            boolean clientMode, InputStream keyStoreInputStream, char[] keystorePassword, char[] keyPassword
    )
            throws
            GarnettSSLChannelCreationFailedException {

        try {

            return createSSLContext( clientMode, keyStoreInputStream, keystorePassword, keyPassword );

        } catch ( NoSuchAlgorithmException e ) {

            throw new GarnettSSLChannelCreationFailedException( "caught a NoSuchAlgorithmException", e );

        } catch ( KeyManagementException e ) {

            throw new GarnettSSLChannelCreationFailedException( "caught a KeyManagementException", e );

        } catch ( FileNotFoundException e ) {

            throw new GarnettSSLChannelCreationFailedException( "caught a FileNotFoundException", e );

        } catch ( IOException e ) {

            throw new GarnettSSLChannelCreationFailedException( "caught an IOException", e );

        } catch ( CertificateException e ) {

            throw new GarnettSSLChannelCreationFailedException( "caught a CertificateException", e );

        } catch ( KeyStoreException e ) {

            throw new GarnettSSLChannelCreationFailedException( "caught a KeyStoreException", e );

        } catch ( UnrecoverableKeyException e ) {

            throw new GarnettSSLChannelCreationFailedException( "caught an UnrecoverableKeyException", e );

        }

    }

}
