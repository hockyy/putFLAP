/*
 *  JFLAP - Formal Languages and Automata Package
 *
 *
 *  Susan H. Rodger
 *  Computer Science Department
 *  Duke University
 *  August 27, 2009

 *  Copyright (c) 2002-2009
 *  All rights reserved.

 *  JFLAP is open source software. Please see the LICENSE for terms.
 *
 */


package jflap.gui.environment;

import java.util.EventListener;

/**
 * An interface that those interested in changes in the file of an environment
 * should implement.
 *
 * @see jflap.gui.environment.FileChangeEvent
 * @see jflap.gui.environment.Environment
 * @see jflap.gui.environment.Environment#setFile
 * @see jflap.gui.environment.Environment#getFile
 */

public interface FileChangeListener extends EventListener {
    /**
     * Registers with the listener that an event has occurred.
     *
     * @param event the event
     */
    void fileChanged(FileChangeEvent event);
}
