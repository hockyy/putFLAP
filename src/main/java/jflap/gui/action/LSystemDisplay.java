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
import java.awt.event.KeyEvent;
import javax.swing.*;
import jflap.grammar.lsystem.LSystem;
import jflap.gui.environment.LSystemEnvironment;
import jflap.gui.environment.tag.CriticalTag;
import jflap.gui.lsystem.DisplayPane;

/**
 * This action creates a new L-system renderer.
 *
 * @author Thomas Finley
 */

public class LSystemDisplay extends LSystemAction {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new <CODE>BruteParseAction</CODE>.
     *
     * @param environment the grammar environment
     */
    public LSystemDisplay(LSystemEnvironment environment) {
        super(environment, "Render System", null);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D,
            MAIN_MENU_MASK));
    }

    /**
     * Performs the action.
     */
    public void actionPerformed(ActionEvent e) {
        LSystem lsystem = getEnvironment().getLSystem();

        if (lsystem.getAxiom().size() == 0) {
            JOptionPane.showMessageDialog(getEnvironment(),
                "The axiom must have one or more symbols.",
                "Nonempty Axiom Required", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            DisplayPane pane = new DisplayPane(lsystem);
            getEnvironment().add(pane, "L-S Render", new CriticalTag() {
            });
            getEnvironment().setActive(pane);
        } catch (NoClassDefFoundError ex) {
            JOptionPane
                .showMessageDialog(
                    getEnvironment(),
                    "Sorry, but this uses features requiring Java 1.4 or later!",
                    "JVM too primitive", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

}
