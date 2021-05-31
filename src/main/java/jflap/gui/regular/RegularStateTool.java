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
import jflap.gui.editor.StateTool;
import jflap.gui.viewer.AutomatonDrawer;
import jflap.gui.viewer.AutomatonPane;

/**
 * A tool that handles the creation of the final state for the FSA to regular
 * expression conversion.
 *
 * @author Thomas Finley
 * @see jflap.gui.regular.FSAToREController
 */

public class RegularStateTool extends StateTool {
    /**
     * The state that was created.
     */
    private State state = null;
    /**
     * The controller object.
     */
    private final FSAToREController controller;

    /**
     * Instantiates a new regular state tool.
     *
     * @param view       the view that the automaton is drawn in
     * @param drawer     the automaton drawer for the view
     * @param controller the controller object we report to
     */
    public RegularStateTool(AutomatonPane view, AutomatonDrawer drawer,
                            FSAToREController controller) {
        super(view, drawer);
        this.controller = controller;
    }

    /**
     * When the user clicks, one creates a state.
     *
     * @param event the mouse event
     */
    public void mousePressed(MouseEvent event) {
        if ((state = controller.stateCreate(event.getPoint())) == null) {
            return;
        }
        getView().repaint();
    }

    /**
     * When the user drags, one moves the created state.
     *
     * @param event the mouse event
     */
    public void mouseDragged(MouseEvent event) {
        if (state == null) {
            return;
        }
        state.setPoint(event.getPoint());
        getView().repaint();
    }
}
