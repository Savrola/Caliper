package com.obtuse.garnett;

import java.io.IOException;

/*
 * Copyright © 2012 Daniel Boulet
 */

public class SavrolaLoginEnvironment implements GarnettObject {

    public static final byte VERSION = 1;

    private String _accountName;

    private transient byte[] _obfuscatedPassword = null;

    private SavrolaStatus _savrolaStatus;

    private long _cookie;

    private SavrolaCapabilities _capabilities;

    /**
     * Create a login environment description of the form that is used in server code.
     *
     * @param accountName  the name of the logged in user.
     * @param savrolaStatus the Savrola-level status of the user:
     *                      <ul>
     *                          <li>USER_ACTIVE is the 'normal' status.
     *                          <li>USER_UNVERIFIED indicates that the user must enter the activation code to enable the
     *                     account.
     *                          <li>other status values render the account unusable for various reasons.
     *                      </ul>
     * @param capabilities the user's universal capabilities.
     */

    public SavrolaLoginEnvironment(
            String accountName,
            SavrolaStatus savrolaStatus,
            SavrolaCapabilities capabilities
    ) {
        super();

        if ( accountName == null ) {

            throw new IllegalArgumentException( "savrolaStatus is null" );

        }

        _accountName = accountName;
        _savrolaStatus = savrolaStatus;
        _capabilities = capabilities;

    }

    public SavrolaLoginEnvironment( GarnettObjectInputStreamInterface gois )
            throws IOException {
        super();

        gois.checkVersion(
                getGarnettTypeName(),
                VERSION,
                VERSION
        );

        _accountName = gois.readString();
        _savrolaStatus = SavrolaStatus.valueOf( gois.readString() );
        _cookie = gois.readLong();
        _capabilities = (SavrolaCapabilities)gois.readOptionalGarnettObject();

    }

    public SavrolaCapabilities getCapabilities() {

        return _capabilities;

    }

    public SavrolaStatus getSavrolaStatus() {

        return _savrolaStatus;

    }

    public String getAccountName() {

        return _accountName;

    }

    public void setCachedPassword( byte[] obfuscatedPassword ) {

        _obfuscatedPassword = obfuscatedPassword;

    }

    public byte[] getCachedPassword() {

        return _obfuscatedPassword;

    }

    public long getLongCookie() {

        return _cookie;

    }

    public String toString() {

        return "SavrolaLoginEnvironment( user = " + getAccountName() + ", status = " + _savrolaStatus + " )";

    }

    public GarnettTypeName getGarnettTypeName() {

        return new GarnettTypeName( SavrolaLoginEnvironment.class.getCanonicalName() );

    }

    public void serializeContents( GarnettObjectOutputStreamInterface goos )
            throws IOException {

        goos.writeVersion( VERSION );
        goos.writeString( _accountName );
        goos.writeString( _savrolaStatus.name() );
        goos.writeLong( _cookie );
        goos.writeOptionalGarnettObject( _capabilities );

    }

}