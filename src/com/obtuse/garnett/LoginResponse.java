package com.obtuse.garnett;

import java.io.IOException;

/*
 * Copyright © 2012 Daniel Boulet
 */

public class LoginResponse implements GarnettObject {

    public static final GarnettTypeName LOGIN_RESPONSE_NAME = new GarnettTypeName(
            LoginResponse.class.getCanonicalName()
    );

    public static int VERSION = 1;

    private SavrolaLoginEnvironment _loginEnv;

    private LoginResultCode _loginResultCode;

    private static final byte EDGARLOGINRESPONSE_FORMAT_VERSION = (byte)1;

    public LoginResponse() {
        super();

    }

    public LoginResponse( SavrolaLoginEnvironment loginEnv ) {
        super();

        if ( loginEnv == null ) {

            throw new IllegalArgumentException( "no result code provided on failed login" );

        }

        _loginEnv = loginEnv;
        _loginResultCode = LoginResultCode.SUCCESS;

    }

    public LoginResponse( LoginResultCode loginResultCode ) {
        super();

        if ( loginResultCode == LoginResultCode.SUCCESS ) {

            throw new IllegalArgumentException( "no login environment provided on successful login" );

        }

        _loginEnv = null;
        _loginResultCode = loginResultCode;

    }

    public LoginResponse( GarnettObjectInputStreamInterface gois )
            throws IOException {
        super();

        gois.checkVersion(
                LOGIN_RESPONSE_NAME,
                VERSION,
                VERSION
        );

        _loginEnv = (SavrolaLoginEnvironment)gois.readOptionalGarnettObject();
        _loginResultCode = LoginResultCode.valueOf( gois.readString() );

    }

    public SavrolaLoginEnvironment getLoaLoginEnvironment() {

        return _loginEnv;

    }

    public LoginResultCode getResultCode() {

        return _loginResultCode;

    }

    public String toString() {

        return "EdgarLoginResponse( " + _loginEnv + ", " + _loginResultCode + " )";

    }

    public GarnettTypeName getGarnettTypeName() {

        return LOGIN_RESPONSE_NAME;

    }

    public void serializeContents( GarnettObjectOutputStreamInterface goos )
            throws
            IOException {

        goos.writeVersion( VERSION );

        goos.writeOptionalGarnettObject( _loginEnv );
        goos.writeString( _loginResultCode.name() );

    }

}
