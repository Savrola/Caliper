package com.obtuse.util;

/*
 * Copyright © 2012 Daniel Boulet
 */

import java.awt.*;

/**
 * Something that owns a button created using {@link ButtonInfo}.
 * <p/>
 */

public interface ButtonManager {

    /**
     * Make sure that the buttons are enabled/disabled as appropriate.
     * Called by {@link ButtonInfo} after the 'run()' method invocation in response to a button being clicked.
     */

    void setButtonStates();

    /**
     * Set the cursor appropriately as it moves over a button.
     * @param predefinedCursor the cursor to be switched to.
     */

    void setCursor( Cursor predefinedCursor );

}
