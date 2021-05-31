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

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import jflap.automata.Automaton;
import jflap.automata.NondeterminismDetector;
import jflap.automata.NondeterminismDetectorFactory;
import jflap.automata.State;
import jflap.gui.editor.ArrowDisplayOnlyTool;
import jflap.gui.environment.Environment;
import jflap.gui.environment.tag.CriticalTag;
import jflap.gui.viewer.AutomatonPane;
import jflap.gui.viewer.SelectionDrawer;

/**
 * This is the action used to highlight nondeterministic states.
 *
 * @author Thomas Finley
 */

public class NondeterminismAction extends AutomatonAction {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * The automaton this simulate action runs simulations on!
     */
    private final Automaton automaton;
    /**
     * The environment that the simulation pane will be put in.
     */
    private final Environment environment;

    /**
     * Instantiates a new <CODE>NondeterminismAction</CODE>.
     *
     * @param automaton   the automaton that read will be simulated on
     * @param environment the environment object that we shall add our simulator pane to
     */
    public NondeterminismAction(Automaton automaton, Environment environment) {
        super("Highlight Nondeterminism", null);
        this.automaton = automaton;
        this.environment = environment;
    }

    /**
     * This action is only applicable to automaton objects.
     *
     * @param object the object to test for being an automaton
     * @return <CODE>true</CODE> if this object is an instance of a subclass
     * of <CODE>Automaton</CODE>, <CODE>false</CODE> otherwise
     */
    public static boolean isApplicable(Object object) {
        return object instanceof Automaton;
    }

    /**
     * Performs the action.
     */
    public void actionPerformed(ActionEvent e) {
        SelectionDrawer drawer = new SelectionDrawer(automaton);
        NondeterminismDetector d = NondeterminismDetectorFactory
            .getDetector(automaton);
        State[] nd = d.getNondeterministicStates(automaton);
        for (int i = 0; i < nd.length; i++) {
            drawer.addSelected(nd[i]);
        }
        AutomatonPane ap = new AutomatonPane(drawer);
        NondeterminismPane pane = new NondeterminismPane(ap);
        environment.add(pane, "Nondeterminism", new CriticalTag() {
        });
        environment.setActive(pane);
    }

    /**
     * A class that exists to make integration with the help system feasible.
     */
    private class NondeterminismPane extends JPanel {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public NondeterminismPane(AutomatonPane ap) {
            super(new BorderLayout());
            ap.addMouseListener(new ArrowDisplayOnlyTool(ap, ap.getDrawer()));
            add(ap, BorderLayout.CENTER);
            add(new JLabel("Nondeterministic states are highlighted."),
                BorderLayout.NORTH);
        }
    }
}
