package com.obtuse.util;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/*
 * Copyright © 2012 Daniel Boulet
 */

@SuppressWarnings( { "ClassWithoutToString" } )
public class PostParameters implements Serializable {

    private final Map<String, String> _parameters = new TreeMap<String, String>();

    private static final byte POSTPARAMETERS_FORMAT_VERSION = (byte)1;

    public static final String SAVROLA_PAYPAL_BUSINESS_EMAIL = "subscriptions@savrola.com";

    /**
     * The only acceptable value for the receiver_email field. Completed payment notifications are rejected by VanHorne
     * if any other value is found so BE CAREFUL!
     */

    public static final String SAVROLA_PAYPAL_RECEIVER_EMAIL = "subscriptions@savrola.com";

    /**
     * Possible parameter names.
     */

    @SuppressWarnings( { "ClassMayBeInterface" } )
    public static class Name {

//        public static final String CUSTOM_POD_TAG = "pt";
//
//        public static final String CUSTOM_DEPLOYMENT_NAME = "dn";
//
//        public static final String CUSTOM_POD_NUMBER = "pn";
//
//        public static final String CUSTOM_ACCOUNT_NAME = "an";
//
//        public static final String CUSTOM_SAVROLA_APP_NAME = "lapn";
//
//        public static final String CUSTOM_CLIENT_IP_ADDRESS = "cipa";
//
//        public static final String CUSTOM_TAX_RATE = "ctr";
//
//        public static final String BUSINESS = "business";
//
//        public static final String RECEIVER_ID = "receiver_id";
//
//        public static final String PAYER_ID = "payer_id";
//
//        public static final String PAYER_EMAIL = "payer_email";
//
//        public static final String PAYER_STATUS = "payer_status";
//
//        public static final String TXN_ID = "txn_id";
//
//        public static final String TXN_TYPE = "txn_type";
//
//        public static final String PARENT_TXN_ID = "parent_txn_id";
//
//        public static final String CUSTOM = "custom";
//
//        public static final String PAYMENT_DATE = "payment_date";
//
//        public static final String PAYMENT_FEE = "payment_fee";
//
//        public static final String PAYMENT_GROSS = "payment_gross";
//
//        public static final String PAYMENT_STATUS = "payment_status";
//
//        public static final String PAYMENT_TYPE = "payment_type";
//
//        public static final String MC_CURRENCY = "mc_currency";
//
//        public static final String SUBSCR_ID = "subscr_id";
//
//        public static final String PERIOD3 = "period3";
//
//        public static final String RECEIVER_EMAIL = "receiver_email";
//
//        public static final String RESIDENCE_COUNTRY = "residence_country";
//
//        public static final String MC_AMOUNT3 = "mc_amount3";
//
//        public static final String MC_GROSS = "mc_gross";
//
//        public static final String MC_FEE = "mc_fee";
//
//        public static final String ITEM_NAME = "item_name";
//
//        public static final String ITEM_NUMBER = "item_number";
//
//        @SuppressWarnings( { "ConstantNamingConvention" } )
//        public static final String TAX = "tax";

        private Name() {
            super();
        }
    }

    @SuppressWarnings( { "ClassMayBeInterface" } )
    public static class Value {

//        public static final String TXN_TYPE_SUBSCR_PAYMENT = "subscr_payment";
//
//        public static final String TXN_TYPE_SUBSCR_SIGNUP = "subscr_signup";
//
//        public static final String TXN_TYPE_SUBSCR_CANCEL = "subscr_cancel";
//
//        public static final String TXN_TYPE_SUBSCR_MODIFY = "subscr_modify";
//
//        public static final String PAYMENT_STATUS_COMPLETED = "Completed";
//
//        public static final String PAYMENT_STATUS_PENDING = "Pending";
//
//        public static final String PAYMENT_STATUS_REFUNDED = "Refunded";

        private Value() {
            super();
        }
    }

    public PostParameters() {
        super();
    }

    public PostParameters( String encodedParms )
            throws
            InterruptedException {
        super();

        PostParameters.decodeParms( encodedParms, this );
    }

    public PostParameters( PostParameters source ) {
        super();

        for ( String key : source.getKeys() ) {

            _parameters.put( key, source.getParameter( key ) );

        }

    }

    public Set<String> getKeys() {

        return _parameters.keySet();

    }

    public Collection<String> values() {

        return _parameters.values();

    }

    public void setParameter( String key, String value ) {

        _parameters.put( key, value );

    }

    public String getParameter( String key ) {

        return _parameters.get( key );

    }

    public int size() {

        return _parameters.size();

    }

//    public void readCompactForm( CompactBuffer cb )
//            throws
//            SavrolaSerializationFailedException {
//
//        byte version = cb.getByte();
//        if ( version != POSTPARAMETERS_FORMAT_VERSION ) {
//            throw new IllegalArgumentException( "unsupported PostParameters format " + version );
//        }
//
//        int size = cb.getInt();
//        for ( int i = 0; i < size; i += 1 ) {
//
//            String key = cb.getString();
//            String value = cb.getString();
//
//            _parameters.put( key, value );
//
//        }
//
//    }
//
//    public void writeCompactForm( CompactBuffer cb, boolean pureTypeEmitted )
//            throws
//            BufferOverflowException {
//
//        if ( !pureTypeEmitted ) {
//            cb.putPureType( PureType.POSTPARAMETERS );
//        }
//
//        cb.putByte( POSTPARAMETERS_FORMAT_VERSION );
//
//        cb.putInt( size() );
//        for ( String key : getKeys() ) {
//            cb.putString( key );
//            cb.putString( _parameters.get( key ) );
//        }
//
//    }

    /**
     * Decodes parameters in percent-encoded URI-format ( e.g. "name=Jack%20Daniels&pass=Single%20Malt" ) and adds them
     * to given Properties.
     *
     * @param parms the parameters of the URL.
     * @param p     where to put the parsed parameters.
     *
     * @throws InterruptedException if the operation needs to be aborted due to an error.
     */

    public static void decodeParms( String parms, PostParameters p )
            throws
            InterruptedException {

        if ( parms == null ) {
            return;
        }

        StringTokenizer st = new StringTokenizer( parms, "&" );
        while ( st.hasMoreTokens() ) {

            String e = st.nextToken();
            int sep = e.indexOf( (int)'=' );

            if ( sep >= 0 ) {

                p.setParameter(
                        PostParameters.decodePercent( e.substring( 0, sep ) ).trim(),
                        PostParameters.decodePercent( e.substring( sep + 1 ) )
                );

            }

        }

    }

    /**
     * Decodes the percent encoding scheme. <br/> For example: "an+example%20string" -> "an example string"
     *
     * @param str the string to be decoded.
     *
     * @return the decoded string.
     */

    public static String decodePercent( String str ) {
        try {
            StringBuffer sb = new StringBuffer();
            @SuppressWarnings("TooBroadScope") int i = 0;
            //noinspection ForLoopWithMissingComponent
            for ( ; i < str.length(); i++ ) {
                char c = str.charAt( i );
                switch ( c ) {
                    case '+':
                        sb.append( ' ' );
                        break;
                    case '%':
                        //noinspection MagicNumber
                        sb.append( (char)Integer.parseInt( str.substring( i + 1, i + 3 ), 16 ) );
                        i += 2;
                        break;
                    default:
                        sb.append( c );
                        break;
                }
            }
            return new String( sb.toString().getBytes() );
        }
        catch ( Exception e ) {
            throw new IllegalArgumentException( "ERROR: Bad percent-encoding.", e );
        }
    }

    public String encode() {

        StringBuffer buf = new StringBuffer();
        String ampersand = "";
        for ( String key : getKeys() ) {

            String value = getParameter( key );
            String encoding = "UTF-8";
            try {

                buf.append( ampersand );
                buf.append( URLEncoder.encode( key, encoding ) );
                buf.append( "=" );
                buf.append( URLEncoder.encode( value, encoding ) );

                ampersand = "&";

            } catch ( UnsupportedEncodingException e ) {

                Logger.logErr( "ERROR:  payment notification request lost!" );
                Trace.emitTrace(
                        "Unsupported encoding reported when trying to encode post parameters" +
                        " (requested encoding was \"" + encoding + "\") - bye!",
                        e
                );
                System.exit( 1 );

            }

        }

        return buf.toString();

    }

    @SuppressWarnings( { "UnnecessaryLocalVariable" } )
    public static PostParameters makeProperties( String encodedProperties ) {

        try {

            PostParameters rval = new PostParameters( encodedProperties );

            return rval;

        } catch ( InterruptedException e ) {

            Logger.logErr(
                    "ERROR:  unable to decode \"" + encodedProperties + "\"", e
            );
            return null;

        }

    }

    public static boolean crossCompare( String encodedV1String, String encodedV2String ) {

        boolean worked = true;

        try {

            PostParameters v1 = new PostParameters( encodedV1String );

            PostParameters v2 = new PostParameters( encodedV2String );

            for ( String key : v1.getKeys() ) {

                String value1 = v1.getParameter( key );

                if ( value1 == null ) {

                    Logger.logMsg( "v1's key \"" + key + "\"'s value is missing from v1" );
                    worked = false;

                } else {

                    String value2 = v2.getParameter( key );

                    if ( value2 == null ) {

                        Logger.logMsg( "v1's key \"" + key + "\" is missing from v2" );
                        worked = false;

                    } else if ( !value1.equals( value2 ) ) {

                        Logger.logMsg(
                                "v1's key \"" + key + "\" yielded \"" + value1 + "\" but v2 yielded \"" + value2 + "\""
                        );
                        worked = false;

                    }

                }

            }

            for ( String key : v2.getKeys() ) {

                String value2 = v2.getParameter( key );

                if ( value2 == null ) {

                    Logger.logMsg( "v2's key \"" + key + "\"'s value is missing from v2" );
                    worked = false;

                } else {

                    String value1 = v1.getParameter( key );

                    if ( value1 == null ) {

                        Logger.logMsg( "v2's key \"" + key + "\" is missing from v1" );
                        worked = false;

                    } else if ( !value2.equals( value1 ) ) {

                        Logger.logMsg(
                                "v2's key \"" + key + "\" yielded \"" + value2 + "\" but v1 yielded \"" + value1 + "\""
                        );
                        worked = false;

                    }

                }

            }

        } catch ( InterruptedException e ) {

            Logger.logErr(
                    "cross compare of \"" + encodedV1String + "\" vs \"" + encodedV2String +
                    "\" failed with an exception", e
            );
            worked = false;

        }

        return worked;

    }
}
