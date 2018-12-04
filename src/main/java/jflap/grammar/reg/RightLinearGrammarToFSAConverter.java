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





package jflap.grammar.reg;

import jflap.automata.Automaton;
import jflap.automata.State;
import jflap.automata.StatePlacer;
import jflap.automata.Transition;
import jflap.automata.fsa.FSATransition;
import jflap.grammar.Grammar;
import jflap.grammar.GrammarToAutomatonConverter;
import jflap.grammar.Production;
import jflap.grammar.ProductionChecker;

import java.awt.*;

/**
 * The right linear jflap.grammar converter can be used to convert jflap.regular grammars,
 * specifically right-linear grammars, to their equivalent finite state
 * jflap.automata. You can do the conversion all at once by calling convertToAutomaton
 * (a method inherited from super class GrammarToAutomatonConverter), or you can
 * do the conversion step by step by first calling createStatesForConversion to
 * create a state for each variable in the jflap.grammar. Then, one by one, for each
 * Production rule in the jflap.grammar, you can call getTransitionForProduction and
 * add the returned transition to the fsa that you are building. Once you do
 * this for each production in the jflap.grammar, you will have the equivalent fsa.
 * 
 * @author Ryan Cavalcante
 */

public class RightLinearGrammarToFSAConverter extends
		GrammarToAutomatonConverter {
	/**
	 * Creates an instance of <CODE>RightLinearGrammarToFSAConverter</CODE>.
	 */
	public RightLinearGrammarToFSAConverter() {

	}

	/**
	 * Returns the transition created by converting <CODE>production</CODE> to
	 * its equivalent transition.
	 * 
	 * @param production
	 *            the production
	 * @return the equivalent transition.
	 */
	public Transition getTransitionForProduction(Production production) {
		ProductionChecker pc = new ProductionChecker();
		String lhs = production.getLHS();
		State from = getStateForVariable(lhs);

		/** if of the form A->xB */
		if (ProductionChecker.isRightLinearProductionWithVariable(production)) {
			String[] variables = production.getVariablesOnRHS();
			String variable = variables[0];
			State to = getStateForVariable(variable);
			String rhs = production.getRHS();
			String label = rhs.substring(0, rhs.length() - 1);
			FSATransition trans = new FSATransition(from, to, label);
			return trans;
		}
		/** if of the form A->x */
		else if (ProductionChecker.isLinearProductionWithNoVariable(production)) {
			String transLabel = production.getRHS();
			State finalState = getStateForVariable(FINAL_STATE);
			FSATransition ftrans = new FSATransition(from, finalState,
					transLabel);
			return ftrans;
		}
		return null;
	}

	/**
	 * Adds all states to <CODE>automaton</CODE> necessary for the conversion
	 * of <CODE>jflap.grammar</CODE> to its equivalent automaton. This creates a
	 * state for each variable in <CODE>jflap.grammar</CODE> and maps each created
	 * state to the variable it was created for by calling mapStateToVariable.
	 * 
	 * @param grammar
	 *            the jflap.grammar being converted.
	 * @param automaton
	 *            the automaton being created.
	 */
	public void createStatesForConversion(Grammar grammar, Automaton automaton) {
		initialize();
		StatePlacer sp = new StatePlacer();
		String[] variables = grammar.getVariables();
		for (int k = 0; k < variables.length; k++) {
			String variable = variables[k];
			Point point = sp.getPointForState(automaton);
			State state = automaton.createState(point);
			if (variable.equals(grammar.getStartVariable()))
				automaton.setInitialState(state);
			state.setLabel(variable);
			mapStateToVariable(state, variable);
		}

		Point pt = sp.getPointForState(automaton);
		State finalState = automaton.createState(pt);
		automaton.addFinalState(finalState);
		mapStateToVariable(finalState, FINAL_STATE);
	}

	protected String FINAL_STATE = "FINAL";

}
