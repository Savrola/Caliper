package com.obtuse.util;

import java.util.Date;

/*
 * Copyright © 2012 Daniel Boulet.
 * Copyright © 2012 Daniel Boulet.
 */

/**
 * Describe something that's interested in seeing all messages sent via {@link Logger}.
 */

public interface LoggerListener {

    void logMessage( Date messageTime, String msg );

}
