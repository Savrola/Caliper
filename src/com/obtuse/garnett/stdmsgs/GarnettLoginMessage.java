package com.obtuse.garnett.stdmsgs;

/*
 * Copyright © 2012 Daniel Boulet
 */

import com.obtuse.garnett.*;
import com.obtuse.garnett.exceptions.GarnettInvalidAccountNameException;
import com.obtuse.util.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Login to a Garnett-aware server.
 */

public class GarnettLoginMessage extends GarnettRequestMessage {

    public static final GarnettTypeName GARNETT_LOGIN_MESSAGE_NAME = new GarnettTypeName(
            GarnettLoginMessage.class.getCanonicalName()
    );

    public static final int VERSION = 1;

    /**
     * The clear text account name is cached but never transmitted (useful when debugging).
     */

    private final transient String _clearAccountName;

    private final byte[] _obfuscatedAccountName;

    private final byte[] _obfuscatedPassword;

    public GarnettLoginMessage( String accountName, @Nullable byte[] obfuscatedPassword )
            throws GarnettInvalidAccountNameException {
        super();

        Logger.logMsg( "new login message" );
        _obfuscatedAccountName = UserUtilities.obfuscateAccountName( accountName.toCharArray() );
        _obfuscatedPassword = obfuscatedPassword;

        _clearAccountName = accountName;

    }

    public GarnettLoginMessage( GarnettObjectInputStreamInterface gois )
            throws IOException {
        super();

        gois.checkVersion(
                GARNETT_LOGIN_MESSAGE_NAME,
                VERSION,
                VERSION
        );

        _obfuscatedAccountName = gois.readByteArray();
        _obfuscatedPassword = gois.readOptionalByteArray();

        // Try to reconstruct the cached account name.  Don't get fussed if it fails.

        String accountName;
        try {

            accountName = new String( UserUtilities.elucidate( _obfuscatedAccountName ) );

        } catch ( Throwable e ) {

            accountName = null;

        }

        _clearAccountName = accountName;

    }

    public String getClearAccountName() {

        return _clearAccountName;

    }

    public byte[] getObfuscatedAccountName() {

        return _obfuscatedAccountName;

    }

    public byte[] getObfuscatedPassword() {

        return _obfuscatedPassword;

    }

    public GarnettTypeName getGarnettTypeName() {

        return GARNETT_LOGIN_MESSAGE_NAME;

    }

    public void serializeContents( GarnettObjectOutputStreamInterface boos )
            throws IOException {

        boos.writeVersion( VERSION );

        boos.writeByteArray( _obfuscatedAccountName );
        boos.writeOptionalByteArray( _obfuscatedPassword );

    }

    public String toString() {

        return "GarnettLoginMessage( \"" + _clearAccountName + "\" )";

    }

}
