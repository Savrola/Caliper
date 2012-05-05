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

    private long _cookie = 0L;

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

    @SuppressWarnings("UnusedDeclaration")
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

    @SuppressWarnings("UnusedDeclaration")
    public SavrolaLoginEnvironment( GarnettObjectInputStreamInterface gois )
            throws IOException {
        super();

        gois.checkVersion(
                SavrolaLoginEnvironment.class,
                SavrolaLoginEnvironment.VERSION,
                SavrolaLoginEnvironment.VERSION
        );

        _accountName = gois.readString();
        _savrolaStatus = SavrolaStatus.valueOf( gois.readString() );
        _cookie = gois.readLong();
        _capabilities = (SavrolaCapabilities)gois.readOptionalGarnettObject();

    }

    @SuppressWarnings("UnusedDeclaration")
    public SavrolaCapabilities getCapabilities() {

        return _capabilities;

    }

    @SuppressWarnings("UnusedDeclaration")
    public SavrolaStatus getSavrolaStatus() {

        return _savrolaStatus;

    }

    public String getAccountName() {

        return _accountName;

    }

    @SuppressWarnings("UnusedDeclaration")
    public void setCachedPassword( byte[] obfuscatedPassword ) {

        //noinspection AssignmentToCollectionOrArrayFieldFromParameter
        _obfuscatedPassword = obfuscatedPassword;

    }

    @SuppressWarnings("UnusedDeclaration")
    public byte[] getCachedPassword() {

        return _obfuscatedPassword;

    }

    @SuppressWarnings("UnusedDeclaration")
    public long getLoginCookie() {

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

        goos.writeVersion( SavrolaLoginEnvironment.VERSION );
        goos.writeString( _accountName );
        goos.writeString( _savrolaStatus.name() );
        goos.writeLong( _cookie );
        goos.writeOptionalGarnettObject( _capabilities );

    }

}