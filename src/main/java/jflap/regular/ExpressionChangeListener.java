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


package jflap.regular;

import java.util.EventListener;

/**
 * The expression change listener should be implemented by objects that wish to
 * be notified when a jflap.regular expression changes.
 *
 * @author Thomas Finley
 */

public interface ExpressionChangeListener extends EventListener {
    /**
     * This method is called when a jflap.regular expression changes.
     *
     * @param event the event object that was changed
     */
	void expressionChanged(ExpressionChangeEvent event);
}
