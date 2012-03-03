package com.obtuse.garnett;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * Our Garnett-related error codes.
 */

public enum GarnettErrorCode {

    SUCCESS {

        protected String getMessage() {

            return "success";

        }

    },

    FAILURE {
        protected String getMessage() {

            return "failure";

        }

    };

    protected abstract String getMessage();

    /**
     * Get a message describing an error code without risking a NPE.
     * @param code the error code of interest.
     * @return a message describing the error code or <tt>"null error code"</tt> if <tt>code</tt> is null.
     */

    public String getMessage( GarnettErrorCode code ) {

        if ( code == null ) {

            return "null error code";

        }

        return code.getMessage();

    }

}
