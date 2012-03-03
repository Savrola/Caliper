package com.obtuse.garnett;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * %%% something clever goes here.
 */

public enum SavrolaStatus {

    USER_ACTIVE {

        public String getDescription() {

            return "user account is active";

        }

    },

    USER_UNVERIFIED {

        public String getDescription() {

            return "user account is unverified";

        }

    };

    public abstract String getDescription();

}
