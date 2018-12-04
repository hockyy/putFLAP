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





package jflap.gui.grammar.parse;

import jflap.automata.State;
import jflap.automata.StatePlacer;
import jflap.automata.Transition;
import jflap.automata.fsa.FSATransition;
import jflap.automata.fsa.FiniteStateAutomaton;
import jflap.automata.graph.AutomatonGraph;
import jflap.automata.graph.LayoutAlgorithm;
import jflap.automata.graph.layout.GEMLayoutAlgorithm;
import jflap.grammar.Grammar;
import jflap.grammar.Production;
import jflap.grammar.parse.LRParseTable;
import jflap.grammar.parse.LRParseTableGenerator;
import jflap.grammar.parse.Operations;
import jflap.gui.editor.EditorPane;
import jflap.gui.environment.GrammarEnvironment;
import jflap.gui.viewer.SelectionDrawer;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * This controller handles user actions for the building of an LR grammar parse
 * table.
 * 
 * @author Thomas Finley
 */

public class LRParseDerivationController extends LLParseDerivationController {
	/**
	 * Instantiates a new parse derivation controller.
	 * 
	 * @param grammar
	 *            the grammar
	 * @param augmented
	 *            the augmented grammar
	 * @param environment
	 *            the grammar environment
	 * @param firstFollow
	 *            the first-follow table
	 * @param directions
	 *            the label that displays what step the user is on
	 * @param dfa
	 *            the DFA built during the derivation of the parse table
	 * @param derivation
	 *            the view component with the user interface elements where the
	 *            derivation is performed
	 */
	public LRParseDerivationController(Grammar grammar, Grammar augmented,
			GrammarEnvironment environment, FirstFollowTable firstFollow,
			JLabel directions, FiniteStateAutomaton dfa,
			LRParseTableDerivationPane derivation) {
		super(grammar, environment, firstFollow, null, directions);
		this.augmented = augmented;
		this.dfa = dfa;
		this.derivation = derivation;
		itemChooser = new ItemSetChooser(augmented, firstFollow);
	}

	/**
	 * For the grammar, return the initial goto item set.
	 * 
	 * @return the initial goto item set
	 */
	private Set<Production> initialGotoSet() {
		Set<Production> initSet = new HashSet<>();
		// I am one lazy son of a bitch.
		Production[] ps = augmented.getProductions();
		Production p = ps[0]; // Oh yeah.
		p = new Production(p.getLHS(), GOTO_SYMBOL + p.getRHS());
		initSet.add(p);
		initSet = Operations.closure(augmented, initSet);
		return initSet;
	}

	/**
	 * Return the set of variables that have $ in their follow set.
	 * 
	 * @return the set of variables that have $ in their follow set
	 */
	private Set<String> variablesWithEndFollow() {
		Map<String, Set<String>> closure = Operations.follow(grammar);
		Set<String> variables = new HashSet<>();
		Iterator<Entry<String, Set<String>>> it = closure.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Set<String>> entry = (Map.Entry<String, Set<String>>) it.next();
			if (((Set<String>) entry.getValue()).contains("$"))
				variables.add(entry.getKey());
		}
		variables.add(grammar.getStartVariable() + "'");
		return variables;
	}

	private boolean isFinalSet(Set<Production> set) {
		Iterator<Production> it = set.iterator();
		while (it.hasNext()) {
			Production p = (Production) it.next();
			if (p.getRHS().endsWith(GOTO_SYMBOL))
				return true;
		}
		return false;
	}

	/**
	 * If the current step has not been completed, this method will report back
	 * to the user what remains to be done.
	 * 
	 * @return <CODE>true</CODE> if the current step is finished, <CODE>false</CODE>
	 *         plus some user output if the current step is unfinished
	 */
	boolean done() {
		switch (step) {
		case -1:
		case FIRST_SETS:
		case FOLLOW_SETS:
			return super.done();
		case BUILD_DFA:
			Iterator<Entry<Set<Production>, State>> it = itemsToState.entrySet().iterator();
			SelectionDrawer drawer = (SelectionDrawer) editor.getDrawer();
			int selected = 0;
			while (it.hasNext()) {
				Map.Entry<Set<Production>, State> entry = (Map.Entry<Set<Production>, State>) it.next();
				Set<Production> items = (Set<Production>) entry.getKey();
				State state = (State) entry.getValue();
				Transition[] t = dfa.getTransitionsFromState(state);
				String[] s = Operations.getCanGoto(items);
				if (s.length != t.length) {
					drawer.addSelected(state);
					selected++;
				}
			}
			if (selected != 0) {
				editor.repaint();
				JOptionPane.showMessageDialog(firstFollow,
						"The indicated states need more transitions.",
						"Set Not Fully Expanded", JOptionPane.ERROR_MESSAGE);
				drawer.clearSelected();
				editor.repaint();
				return false;
			}
			// Now check the final states.
			it = itemsToState.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Set<Production>, State> entry = (Map.Entry<Set<Production>, State>) it.next();
				Set<Production> items = (Set<Production>) entry.getKey();
				State state = (State) entry.getValue();
				boolean finalState = isFinalSet(items);
				if (finalState ^ dfa.isFinalState(state)) {
					drawer.addSelected(state);
					selected++;
				}
			}
			if (selected != 0) {
				editor.repaint();
				JOptionPane
						.showMessageDialog(
								firstFollow,
								"The indicated states are either final and\n"
										+ "shouldn't be, or are nonfinal and should be.",
								"States in Wrong Finality",
								JOptionPane.ERROR_MESSAGE);
				drawer.clearSelected();
				editor.repaint();
				return false;
			}
			return true;
		case PARSE_TABLE:
			int rows = targetParseTable.getRowCount();
			int columns = targetParseTable.getColumnCount();
			LRParseTablePane tableView = derivation.getParseTableView();
			try {
				tableView.getCellEditor().stopCellEditing();
			} catch (NullPointerException e) {
			}
			tableView.clearSelection();
			int highlighted = 0;
			for (int i = 0; i < rows; i++)
				for (int j = 0; j < columns; j++)
					if (!targetParseTable.getValueAt(i, j).equals(
							userParseTable.getValueAt(i, j))) {
						highlighted++;
						tableView.highlight(i, j);
					}
			if (highlighted == 0)
				return true;
			JOptionPane.showMessageDialog(firstFollow,
					"Highlighted cells are incorrect.", "Bad Parse Table",
					JOptionPane.ERROR_MESSAGE);
			tableView.dehighlight();
			return false;
		case FINISHED:
			JOptionPane.showMessageDialog(firstFollow,
					"The parse table is complete.", "Finished",
					JOptionPane.ERROR_MESSAGE);
		default:
			return false;
		}
	}

	/**
	 * This method will complete the current step. When done with whatever it
	 * must do it will call {@link #nextStep} to move to the next step unless it
	 * is ont he last step, in which case a small error message is displayed.
	 */
	public void completeStep() {
		switch (step) {
		case FIRST_SETS:
		case FOLLOW_SETS:
			super.completeStep();
			break;
		case BUILD_DFA:
			completeDFA();
			nextStep();
			break;
		case PARSE_TABLE:
			int rows = targetParseTable.getRowCount();
			int columns = targetParseTable.getColumnCount();
			LRParseTablePane tableView = derivation.getParseTableView();
			tableView.clearSelection();
			for (int i = 0; i < rows; i++)
				for (int j = 0; j < columns; j++)
					userParseTable.setValueAt(
							targetParseTable.getValueAt(i, j), i, j);
			nextStep();
			break;
		case FINISHED:
			JOptionPane.showMessageDialog(firstFollow,
					"The parse table is complete.", "Finished",
					JOptionPane.ERROR_MESSAGE);
			break;
		default:
			System.err.println("Complete step screwed up!  Step is " + step);
			break;
		}
	}

	/**
	 * This method will complete the step for whatever cells are highlighted, as
	 * is appropriate for the current step.
	 */
	public void completeSelected() {
		switch (step) {
		case FIRST_SETS:
		case FOLLOW_SETS:
			super.completeSelected();
			break;
		case BUILD_DFA:
			JOptionPane.showMessageDialog(firstFollow,
					"That request is invalid for this particular step.",
					"Nothing Selectable", JOptionPane.ERROR_MESSAGE);
			break;
		case PARSE_TABLE:
			int rows = targetParseTable.getRowCount();
			int columns = targetParseTable.getColumnCount();
			LRParseTablePane tableView = derivation.getParseTableView();
			for (int i = 0; i < rows; i++)
				for (int j = 0; j < columns; j++) {
					int cv = tableView.convertColumnIndexToView(j);
					if (!tableView.isCellSelected(i, cv))
						continue;
					userParseTable.setValueAt(
							targetParseTable.getValueAt(i, j), i, j);
				}
			tableView.repaint();
			break;
		}
	}

	/**
	 * Completes the DFA.
	 */
	private void completeDFA() {
		if (step != BUILD_DFA) {
			// That shouldn't be...
			System.err.println("COMPLETE DFA CALLED AT WRONG TIME");
			return;
		}
		// At this point, at least the initial state should exist.
		StatePlacer placer = new StatePlacer();
		Set<State> handledStates = new HashSet<>();
		State[] states = dfa.getStates();
		Set<Object> originalStates = new HashSet<>(Arrays.asList(states));
		while (states.length != handledStates.size()) {
			for (int i = 0; i < states.length; i++) {
				if (handledStates.contains(states[i]))
					continue;
				Set<Production> itemSet = (Set<Production>) stateToItems.get(states[i]);
				if (isFinalSet(itemSet)) {
					dfa.addFinalState(states[i]);
				} else {
					dfa.removeFinalState(states[i]);
				}
				// See what symbols have not been "gone to" yet.
				Transition[] t = dfa.getTransitionsFromState(states[i]);
				Set<String> mayAdd = new TreeSet<>(Arrays.asList(Operations
						.getCanGoto(itemSet)));
				for (int j = 0; j < t.length; j++)
					mayAdd.remove(((FSATransition) t[j]).getLabel());
				// Now mayAdd holds symbols those we haven't done yet.
				Iterator<String> it = mayAdd.iterator();
				while (it.hasNext()) {
					String symbol = (String) it.next();
					Set<Production> gotoSet = Operations.goTo(augmented, itemSet, symbol);
					State second = (State) itemsToState.get(gotoSet);
					if (second == null) {
						Point p = placer.getPointForState(dfa);
						second = dfa.createState(p);
						Production[] gotoArray = (Production[]) gotoSet
								.toArray(new Production[0]);
						assignItemsToState(gotoArray, second);
					}
					Transition nt = new FSATransition(states[i], second, symbol);
					dfa.addTransition(nt);
				}
				// That should just about do it...
				handledStates.add(states[i]);
			}
			states = dfa.getStates();
		}
		LayoutAlgorithm layout = new GEMLayoutAlgorithm();
		AutomatonGraph graph = new AutomatonGraph(dfa);
		layout.layout(graph, originalStates);
		graph.moveAutomatonStates();
	}

	/**
	 * This finishes absolutely everything.
	 */
	public void completeAll() {
		doAll = true;
		do {
			completeStep();
		} while (step != FINISHED);
		doAll = false;
	}

	/**
	 * This method is used by the embedded DFA editor to indicate that that user
	 * wants to evaluate the Goto(<I>I, S</I>). <I>I</I> is a set of items
	 * represented by the <CODE>first</CODE> parameter. <I>S</I> is a symbol
	 * that the user shall read.
	 * 
	 * @param first
	 *            the state that represents the group of items goto takes as an
	 *            argument
	 * @param point
	 *            if a new state need be created, this will be the point that
	 *            state is created at
	 * @param second
	 *            the state that represents the group of items goto evaluates
	 *            as; this may be <CODE>null</CODE> if the user has yet to
	 *            read this set
	 */
	public void gotoGroup(State first, Point point, State second) {
		String symbol = (String) JOptionPane.showInputDialog(firstFollow,
				"What is the grammar symbol for the transition?");
		if (symbol == null)
			return;
		Set<Production> from = (Set<Production>) stateToItems.get(first);
		Set<Production> to = Operations.goTo(augmented, from, symbol);
		// Does this group even progress on this symbol?
		if (to.size() == 0) {
			JOptionPane.showMessageDialog(firstFollow,
					"That symbol is invalid for this state.",
					"Bad Symbol for Group", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// Did the user drag to a state, or to empty space?
		if (second != null) {
			// We know what the second group is.
			Set<Production> toUser = (Set<Production>) stateToItems.get(second);
			if (!to.equals(toUser)) {
				JOptionPane.showMessageDialog(firstFollow, "The symbol "
						+ symbol + " can't join these two states.",
						"Bad Progression", JOptionPane.ERROR_MESSAGE);
				return;
			}
		} else {
			// We must ask the user what the second group is.
			Production[] items = itemChooser
					.getItemSet(to, "Goto on " + symbol);
			if (items == null)
				return;
			Set<Production> itemSet = new HashSet<>();
			for (int i = 0; i < items.length; i++)
				itemSet.add(items[i]);
			second = (State) itemsToState.get(itemSet);
			if (second == null) {
				second = dfa.createState(point);
				assignItemsToState(items, second);
			}
		}
		Transition t = new FSATransition(first, second, symbol);
		dfa.addTransition(t);
	}

	/**
	 * Moves the controller to the next step of the building of the parse table.
	 * 
	 * @return if the controller could be advanced to the next step
	 */
	public boolean nextStep() {
		if (!done())
			return false;

		step++;

		switch (step) {
		case FIRST_SETS:
			parseAction.setEnabled(false);
			firstFollow.getFFModel().setCanEditFirst(true);
			firstFollow.getFFModel().setCanEditFollow(false);
			directions
					.setText("Define FIRST sets.  ! is the lambda character.");
			break;
		case FOLLOW_SETS:
			firstFollow.getFFModel().setCanEditFirst(false);
			firstFollow.getFFModel().setCanEditFollow(true);
			directions
					.setText("Define FOLLOW sets.  $ is the end of string character.");
			break;
		case BUILD_DFA:
			doSelectedAction.setEnabled(false);
			firstFollow.getFFModel().setCanEditFollow(false);
			int choice = doAll ? JOptionPane.NO_OPTION
					: JOptionPane
							.showConfirmDialog(
									firstFollow,
									"Masterfully done hero!  Now you must\n"
											+ "define the set of items for the intial DFA state.\n"
											+ "Do you want to define the initial set yourself?",
									"Initial Set Construction",
									JOptionPane.YES_NO_OPTION);
			Set<Production> initialGotoSet = initialGotoSet();
			Production[] initials = choice == JOptionPane.YES_OPTION ? null
					: (Production[]) initialGotoSet.toArray(new Production[0]);
			while (initials == null) {
				initials = itemChooser.getItemSet(initialGotoSet,
						"Initial Goto Set");
				if (initials != null)
					break;
				JOptionPane.showMessageDialog(firstFollow,
						"The initial set MUST be created now.",
						"Initial Set Needed", JOptionPane.ERROR_MESSAGE);
			}
			State initialState = dfa.createState(new Point(60, 40));
			dfa.setInitialState(initialState);
			assignItemsToState(initials, initialState);
			directions.setText("Build the DFA.");
			break;
		case PARSE_TABLE:
			doSelectedAction.setEnabled(true);
			// Set up the data structures.
			targetParseTable = LRParseTableGenerator.generate(augmented, dfa,
					stateToItems, itemsToState, Operations.follow(grammar));
			userParseTable = new LRParseTable(augmented, dfa);
			// Set up the view.
			derivation.moveDFA();
			derivation.setParseTable(userParseTable);
			directions.setText("Fill entries in parse table.");
			break;
		case FINISHED:
			doSelectedAction.setEnabled(false);
			doStepAction.setEnabled(false);
			doAllAction.setEnabled(false);
			nextAction.setEnabled(false);
			parseAction.setEnabled(true);
			// derivation.setParseTable(targetParseTable);
			derivation.getParseTableView().shiftMode();
			directions
					.setText("Parse table complete.  Press \"parse\" to use it.");
			break;
		}
		return true;
	}

	/**
	 * This will handle parsing.
	 */
	public void parse() {
		LRParsePane panel = new LRParsePane(environment, augmented,
				userParseTable);
		environment.add(panel, "SLR(1) Parsing");
		environment.setActive(panel);
	}

	/**
	 * Assigns items to a particular state.
	 * 
	 * @param items
	 *            the items to assign to a state
	 * @param state
	 *            the state to assign the items to
	 */
	private void assignItemsToState(Production[] items, State state) {
		Set<Production> itemSet = new HashSet<>();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < items.length; i++) {
			itemSet.add(items[i]);
			if (i != 0)
				sb.append('\n');
			sb.append(items[i].toString());
		}
		state.setLabel(sb.toString());
		stateToItems.put(state, itemSet);
		itemsToState.put(itemSet, state);
	}

	/** The identifiers for steps. */
	static final int BUILD_DFA = 2, PARSE_TABLE = 3, FINISHED = 4;

	/**
	 * The finite state automaton that displays the groups of items.
	 */
	private FiniteStateAutomaton dfa;

	/** The item set chooser. */
	private ItemSetChooser itemChooser;

	/** The augmented grammar. */
	private Grammar augmented;

	/** The parse table derivation pane. */
	private LRParseTableDerivationPane derivation;

	/** The mapping of states to a set of items. */
	private Map<State, Set<Production>> stateToItems = new HashMap<>();

	/** The mapping of item sets to a state. */
	private Map<Set<Production>, State> itemsToState = new HashMap<>();

	/** The target parse table. */
	private LRParseTable targetParseTable;

	/** The user defined parse table. */
	private LRParseTable userParseTable;

	/**
	 * This indicates that a "do all" is in progress, and as such some things
	 * which would provide interaction should not.
	 */
	private boolean doAll = false;

	/* This is the goto position symbol. */
	private static final String GOTO_SYMBOL = "" + Operations.ITEM_POSITION;

	/** The editor. */
	EditorPane editor = null;

}
