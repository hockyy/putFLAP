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


package jflap.gui.action;


import java.awt.event.ActionEvent;
import jflap.gui.environment.Universe;

public class OpenURLAction extends RestrictedAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public OpenURLAction() {
        super("Open URL", null);
    }

    public boolean isEnabled() {
        return Universe.CHOOSER == null;
    }

    public void actionPerformed(ActionEvent e) {

    }

}
