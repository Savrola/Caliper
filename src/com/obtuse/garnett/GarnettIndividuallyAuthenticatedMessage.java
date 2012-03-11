package com.obtuse.garnett;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * Marks messages which can be individually authenticated in the sense that they can contain a user name and
 * obfuscated password.  These messages may appear in semi-authenticated sessions.  If they appear in a session
 * which did not begin with a login message then they must be individually authenticated.
 */

public interface GarnettIndividuallyAuthenticatedMessage {

    /**
     * Get the authenticating account name in clear text.
     * <p>
     *     Note that this method <b><u>MUST</u></b> reconstruct the clear text account from the
     *     obfuscated account name since sending the clear text account name over the wire is
     *     forbidden.  Also note that obfuscation is intended to prevent developers and administrators
     *     from accidentally seeing an account name and/or a password in clear text.  It is not intended
     *     as any sort of actual security measure since anyone with access to an obfuscated account name
     *     or password can quite easily recover the clear text.
     * </p>
     * @return the account name in clear text.
     */

    String getClearAccountName();

    /**
     * Get the obfuscated authenticating account name.
     * @return the obfuscated account name.
     */

    byte[] getObfuscatedAccountName();

    /**
     * Get the obfuscated password.
     * @return the obfuscated password.
     */

    byte[] getObfuscatedPassword();

}
