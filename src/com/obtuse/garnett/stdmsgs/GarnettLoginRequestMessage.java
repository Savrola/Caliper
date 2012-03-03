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

public class GarnettLoginRequestMessage extends GarnettRequestMessage {

    public static final GarnettTypeName GARNETT_LOGIN_MESSAGE_NAME = new GarnettTypeName(
            GarnettLoginRequestMessage.class.getCanonicalName()
    );

    public static final int VERSION = 1;

    /**
     * The clear text account name is cached but never transmitted (useful when debugging).
     */

    private final transient String _clearAccountName;

    private final byte[] _obfuscatedAccountName;

    private final byte[] _obfuscatedPassword;

    private GarnettObject _augmentedData;

    /**
     * The code number sent to the user via email which activates their account.
     */

    private final long _activationCode;

    public GarnettLoginRequestMessage( String accountName, @Nullable byte[] obfuscatedPassword, long activationCode )
            throws GarnettInvalidAccountNameException {
        super();

        Logger.logMsg( "new login message" );
        _obfuscatedAccountName = UserUtilities.obfuscateAccountName( accountName.toCharArray() );
        _obfuscatedPassword = obfuscatedPassword;
        _activationCode = activationCode;

        _clearAccountName = accountName;

    }

    public GarnettLoginRequestMessage( String accountName, @Nullable byte[] obfuscatedPassword )
            throws GarnettInvalidAccountNameException {
        this( accountName, obfuscatedPassword, 0L );

    }

    public GarnettLoginRequestMessage( GarnettObjectInputStreamInterface gois )
            throws IOException {
        super();

        gois.checkVersion(
                GARNETT_LOGIN_MESSAGE_NAME,
                VERSION,
                VERSION
        );

        _obfuscatedAccountName = gois.readByteArray();
        _obfuscatedPassword = gois.readOptionalByteArray();
        _activationCode = gois.readLong();
        _augmentedData = gois.readOptionalGarnettObject();

        // Try to reconstruct the cached account name.  Don't get fussed if it fails.

        _clearAccountName = reconstructClearAccountName();

    }

    private String reconstructClearAccountName() {

        String accountName;
        try {

            accountName = new String( UserUtilities.elucidate( _obfuscatedAccountName ) );

        } catch ( Throwable e ) {

            accountName = null;

        }
        return accountName;
    }

    @Override
    public Class<? extends GarnettResponseMessage> getResponseClass() {

        return GarnettLoginResponseMessage.class;

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

    public void serializeContents( GarnettObjectOutputStreamInterface goos )
            throws IOException {

        super.serializeContents( goos );
        goos.writeVersion( VERSION );

        goos.writeByteArray( _obfuscatedAccountName );
        goos.writeOptionalByteArray( _obfuscatedPassword );
        goos.writeLong( _activationCode );
        goos.writeOptionalGarnettObject( _augmentedData );

    }

    public String toString() {

        return "GarnettLoginRequestMessage( \"" + _clearAccountName + "\" )";

    }

    public long getActivationCode() {

        return _activationCode;

    }

    public GarnettObject getAugmentedData() {

        return _augmentedData;
    }

    public void setAugmentedData( GarnettObject augmentedData ) {

        _augmentedData = augmentedData;
    }
}
