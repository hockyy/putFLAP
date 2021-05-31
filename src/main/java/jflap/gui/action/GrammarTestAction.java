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
import jflap.grammar.Grammar;
import jflap.grammar.UnrestrictedGrammar;
import jflap.gui.environment.EnvironmentFrame;
import jflap.gui.environment.GrammarEnvironment;
import jflap.gui.environment.Universe;
import jflap.gui.environment.tag.CriticalTag;
import jflap.gui.grammar.transform.ChomskyPane;

/**
 * This is a simple test action for grammars.
 *
 * @author Thomas Finley
 */

public class GrammarTestAction extends GrammarAction {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * The grammar environment.
     */
    private final GrammarEnvironment environment;
    /**
     * The frame for the grammar environment.
     */
    private final EnvironmentFrame frame;

    /**
     * Instantiates a new <CODE>GrammarOutputAction</CODE>.
     *
     * @param environment the grammar environment
     */
    public GrammarTestAction(GrammarEnvironment environment) {
        super("Grammar Test", null);
        this.environment = environment;
        this.frame = Universe.frameForEnvironment(environment);
    }

    /**
     * Performs the action.
     */
    public void actionPerformed(ActionEvent e) {
        Grammar g = environment.getGrammar(UnrestrictedGrammar.class);
        if (g == null) {
            return;
        }
        ChomskyPane cp = new ChomskyPane(environment, g);
        environment.add(cp, "Test", new CriticalTag() {
        });
        environment.setActive(cp);
    }
}
