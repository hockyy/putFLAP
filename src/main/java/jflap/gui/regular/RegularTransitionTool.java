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


package jflap.gui.regular;

import java.awt.event.MouseEvent;
import jflap.automata.State;
import jflap.gui.editor.TransitionTool;
import jflap.gui.viewer.AutomatonDrawer;
import jflap.gui.viewer.AutomatonPane;

/**
 * A tool that handles the creation of transitions for the FSA to regular
 * expression conversion. This simply calls the appropriate <CODE>FSAToREController</CODE>
 * method.
 *
 * @author Thomas Finley
 * @see jflap.gui.regular.FSAToREController#transitionCreate
 */

public class RegularTransitionTool extends TransitionTool {
    /**
     * The regular conversion controller.
     */
    private final FSAToREController controller;

    /**
     * Instantiates a new transition tool.
     *
     * @param view       the view where the automaton is drawn
     * @param drawer     the object that draws the automaton
     * @param controller the controller object for the transition from an FSA to an RE
     */
    public RegularTransitionTool(AutomatonPane view, AutomatonDrawer drawer,
                                 FSAToREController controller) {
        super(view, drawer);
        this.controller = controller;
    }

    /**
     * When we release the mouse, a transition from the start state to this
     * released state must be formed.
     *
     * @param event the mouse event
     */
    public void mouseReleased(MouseEvent event) {
        // Did we even start at a state?
        if (first == null) {
            return;
        }
        State state = getDrawer().stateAtPoint(event.getPoint());
        if (state != null) {
            controller.transitionCreate(first, state);
        }
        first = null;
        getView().repaint();
    }
}
