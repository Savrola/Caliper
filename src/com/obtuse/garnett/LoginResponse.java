package com.obtuse.garnett;

import java.io.IOException;

/*
 * Copyright © 2012 Daniel Boulet
 */

public class LoginResponse implements GarnettObject {

    public static final int VERSION = 1;

    private SavrolaLoginEnvironment _loginEnv = null;

    private LoginResultCode _loginResultCode = null;

    @SuppressWarnings("UnusedDeclaration")
    public LoginResponse( SavrolaLoginEnvironment loginEnv ) {
        super();

        if ( loginEnv == null ) {

            throw new IllegalArgumentException( "no result code provided on failed login" );

        }

        _loginEnv = loginEnv;
        _loginResultCode = LoginResultCode.SUCCESS;

    }

    @SuppressWarnings("UnusedDeclaration")
    public LoginResponse( LoginResultCode loginResultCode ) {
        super();

        if ( loginResultCode == LoginResultCode.SUCCESS ) {

            throw new IllegalArgumentException( "no login environment provided on successful login" );

        }

        _loginEnv = null;
        _loginResultCode = loginResultCode;

    }

    @SuppressWarnings("UnusedDeclaration")
    public LoginResponse( GarnettObjectInputStreamInterface gois )
            throws IOException {
        super();

        gois.checkVersion(
                LoginResponse.class,
                LoginResponse.VERSION,
                LoginResponse.VERSION
        );

        _loginEnv = (SavrolaLoginEnvironment)gois.readOptionalGarnettObject();
        _loginResultCode = LoginResultCode.valueOf( gois.readString() );

    }

    @SuppressWarnings("UnusedDeclaration")
    public SavrolaLoginEnvironment getSavrolaLoginEnvironment() {

        return _loginEnv;

    }

    @SuppressWarnings("UnusedDeclaration")
    public LoginResultCode getResultCode() {

        return _loginResultCode;

    }

    public String toString() {

        return "EdgarLoginResponse( " + _loginEnv + ", " + _loginResultCode + " )";

    }

    public GarnettTypeName getGarnettTypeName() {

        return new GarnettTypeName( LoginResponse.class.getCanonicalName() );

    }

    public void serializeContents( GarnettObjectOutputStreamInterface goos )
            throws
            IOException {

        goos.writeVersion( LoginResponse.VERSION );

        goos.writeOptionalGarnettObject( _loginEnv );
        goos.writeString( _loginResultCode.name() );

    }

}
