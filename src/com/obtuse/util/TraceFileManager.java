package com.obtuse.util;

/*
 * Copyright © 2006 Loa Corporation.
 * Copyright © 2011 Daniel Boulet.
 */

/**
 * Describe something that wants to be informed when a trace file is emitted.
 */

public interface TraceFileManager {

    void newTraceFile( String traceFileName, long timeStamp );

}
