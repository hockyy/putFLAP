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
import java.util.HashMap;
import javax.swing.*;
import jflap.automata.Transition;
import jflap.automata.graph.AutomatonGraph;
import jflap.automata.graph.LayoutAlgorithm;
import jflap.automata.graph.layout.GEMLayoutAlgorithm;
import jflap.automata.pda.PushdownAutomaton;
import jflap.grammar.Grammar;
import jflap.grammar.Production;
import jflap.grammar.cfg.CFGToPDALLConverter;
import jflap.gui.environment.GrammarEnvironment;
import jflap.gui.environment.Universe;
import jflap.gui.environment.tag.CriticalTag;
import jflap.gui.grammar.convert.ConvertPane;

/**
 * This is the action that initiates the conversion of a context free grammar to
 * a PDA using LL conversion.
 *
 * @author Thomas Finley
 */

public class ConvertCFGLL extends GrammarAction {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * The grammar environment.
     */
    private final GrammarEnvironment environment;

    /**
     * Instantiates a new <CODE>ConvertCFGLL</CODE> action.
     *
     * @param environment the grammar environment
     */
    public ConvertCFGLL(GrammarEnvironment environment) {
        super("Convert CFG to PDA (LL)", null);
        this.environment = environment;
    }

    /**
     * Performs the action.
     */
    public void actionPerformed(ActionEvent e) {
        Grammar grammar = environment.getGrammar();
        if (grammar == null) {
            return;
        }
        if (grammar.getProductions().length == 0) {
            JOptionPane.showMessageDialog(Universe
                    .frameForEnvironment(environment),
                "The grammar should exist.");
            return;
        }
        // Create the initial automaton.
        PushdownAutomaton pda = new PushdownAutomaton();
        CFGToPDALLConverter convert = new CFGToPDALLConverter();
        convert.createStatesForConversion(grammar, pda);
        // Create the map of productions to transitions.
        HashMap<Production, Transition> ptot = new HashMap<>();
        Production[] prods = grammar.getProductions();
        for (int i = 0; i < prods.length; i++) {
            ptot.put(prods[i], convert.getTransitionForProduction(prods[i]));
        }
        // Add the view to the environment.
        final ConvertPane cp = new ConvertPane(grammar, pda, ptot, environment);
        environment.add(cp, "Convert to PDA (LL)", new CriticalTag() {
        });

        // Do the layout of the states.
        AutomatonGraph graph = new AutomatonGraph(pda);
        LayoutAlgorithm layout = new GEMLayoutAlgorithm();
        layout.layout(graph, null);
        graph.moveAutomatonStates();
        environment.setActive(cp);
        environment.validate();
        cp.getEditorPane().getAutomatonPane().fitToBounds(20);
    }
}
