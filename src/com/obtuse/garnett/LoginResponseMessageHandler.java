package com.obtuse.garnett;

/*
 * Copyright © 2012 Daniel Boulet
 */

import com.obtuse.garnett.stdmsgs.GarnettLoginResponseMessage;
import com.obtuse.util.Logger;

/**
 * Manage a login attempt response.
 */

public class LoginResponseMessageHandler implements GarnettMessageHandler {

    private LoginResponse _loginResponse = null;

    private boolean _done = false;

    private String _userName;

    public LoginResponseMessageHandler( String userName ) {
        super();

        _userName = userName;

    }

    public void processMessage( GarnettSession garnettSession, GarnettMessage garnettMessage ) {

        GarnettLoginResponseMessage loginResponseMessage = (GarnettLoginResponseMessage)garnettMessage;
        _loginResponse = loginResponseMessage.getLoginResponse();

        if ( !markDone() ) {
            Logger.logMsg( "response to login for \"" + _userName + "\" came too late - ignored" );
        }

    }

    public LoginResponse getLoginResponse() {

        return _loginResponse;

    }

    public boolean isDone() {

        return _done;

    }

    @SuppressWarnings( { "BooleanMethodIsAlwaysInverted" } )
    public synchronized boolean markDone() {

        if ( !_done ) {

            _done = true;
            notifyAll();
            return true;

        }

        return false;

    }

    public boolean worked() {

        return _loginResponse != null;

    }

    public String toString() {

        return "LoginResponseMessageHandler( user name = \"" + _userName + "\", done = " + _done + " )";

    }

}
