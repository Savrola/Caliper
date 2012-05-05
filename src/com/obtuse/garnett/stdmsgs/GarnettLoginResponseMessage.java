package com.obtuse.garnett.stdmsgs;

/*
 * Copyright © 2012 Daniel Boulet
 */

import com.obtuse.garnett.*;

import java.io.IOException;
import java.util.Arrays;

/**
 * Respond to a login attempt.
 */

public class GarnettLoginResponseMessage extends GarnettResponseMessage {

    public static final int VERSION = 1;

    private final boolean _worked;
    private final LoginResponse _loginResponse;
    private final GarnettKeywordValue[] _accountAttributes;
    private final SavrolaCapabilities _capabilities;

    @SuppressWarnings("UnusedDeclaration")
    public GarnettLoginResponseMessage(
            long requestId,
            LoginResponse loginResponse,
            GarnettKeywordValue[] accountAttributes,
            SavrolaCapabilities capabilities
    ) {
        super( requestId );

        _loginResponse = loginResponse;
        _accountAttributes = Arrays.copyOf( accountAttributes, accountAttributes.length );
        _capabilities = capabilities;
        _worked = capabilities != null;

    }

    @SuppressWarnings("UnusedDeclaration")
    public GarnettLoginResponseMessage( GarnettObjectInputStreamInterface gois )
            throws IOException {
        super( gois );

        gois.checkVersion(
                GarnettLoginResponseMessage.class,
                GarnettLoginResponseMessage.VERSION,
                GarnettLoginResponseMessage.VERSION
        );

        _worked = gois.readBoolean();
        _loginResponse = (LoginResponse)gois.readOptionalGarnettObject();
        _capabilities = (SavrolaCapabilities)gois.readOptionalGarnettObject();
        _accountAttributes = (GarnettKeywordValue[])gois.readOptionalGarnettObjectArray();

    }

    @Override
    public boolean worked() {

        return _worked;

    }

    public GarnettTypeName getGarnettTypeName() {

        return new GarnettTypeName( GarnettLoginResponseMessage.class.getCanonicalName() );

    }

    public void serializeContents( GarnettObjectOutputStreamInterface goos )
            throws IOException {

        goos.writeVersion( GarnettLoginResponseMessage.VERSION );
        goos.writeBoolean( _worked );
        goos.writeOptionalGarnettObject( _loginResponse );
        goos.writeOptionalGarnettObject( _capabilities );
        goos.writeGarnettObjectArray( _accountAttributes );
        if ( _accountAttributes == null ) {

            goos.writeInt( -1 );

        } else {

            goos.writeInt( _accountAttributes.length );
            for ( GarnettKeywordValue keywordValue : _accountAttributes ) {

                goos.writeGarnettObject( keywordValue );

            }

        }

    }

    public String toString() {

        return "GarnettLoginResponseMessage( worked = " + worked() + " )";

    }

    public LoginResponse getLoginResponse() {

        return _loginResponse;

    }

}
