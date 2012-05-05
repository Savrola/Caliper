package com.obtuse.garnett;

/*
 * Copyright © 2012 Daniel Boulet
 */

/**
 * %%% something clever goes here.
 */

public class GenericAutomagicUpgrader {

    private GenericAutomagicUpgrader() {
        super();

    }

    public static String getDeploymentName() {

        return "devel";

    }

    public static int getPodNumber() {

        return 0;

    }

    public static boolean isDefaultDeployment() {

        return "default".equals( GenericAutomagicUpgrader.getDeploymentName() );

    }

}
