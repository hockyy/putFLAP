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
import java.awt.event.*;
import java.io.Serializable;
import javax.swing.*;
import jflap.automata.fsa.FiniteStateAutomaton;
import jflap.automata.mealy.MealyMachine;
import jflap.automata.mealy.MooreMachine;
import jflap.automata.pda.PushdownAutomaton;
import jflap.automata.turing.TuringMachine;
import jflap.automata.turing.TuringMachineBuildingBlocks;
import jflap.grammar.cfg.ContextFreeGrammar;
import jflap.grammar.lsystem.LSystem;
import jflap.gui.environment.FrameFactory;
import jflap.gui.environment.Universe;
import jflap.gui.menu.MenuBarCreator;
import jflap.gui.pumping.CFPumpingLemmaChooser;
import jflap.gui.pumping.RegPumpingLemmaChooser;
import jflap.regular.RegularExpression;

/**
 * The <CODE>NewAction</CODE> handles when the user decides to create some new
 * environment, that is, some sort of new automaton, or grammar, or regular
 * expression, or some other such editable object.
 *
 * @author Thomas Finley
 */

public class NewAction extends RestrictedAction {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * The universal dialog.
     */
    private static NewDialog DIALOG = null;

    /**
     * Instantiates a new <CODE>NewAction</CODE>.
     */
    public NewAction() {
        super("New...", null);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N,
            MAIN_MENU_MASK));
    }

    /**
     * Dispose of environment dialog
     * by Moti Ben-Ari
     */
    public static void closeNew() {
        DIALOG.dispose();
        DIALOG = null;
    }

    /**
     * Shows the new environment dialog.
     */
    public static void showNew() {
		if (DIALOG == null) {
			DIALOG = new NewDialog();
		}
        DIALOG.setVisible(true);
        DIALOG.toFront();
    }

    /**
     * Hides the new environment dialog.
     */
    public static void hideNew() {
        DIALOG.setVisible(false);
    }

    /**
     * Called once a type of editable object is choosen. The editable object is
     * passed in, the dialog is hidden, and the window is created.
     *
     * @param object the object that we are to edit
     */
    private static void createWindow(Serializable object) {
        DIALOG.setVisible(false);
        FrameFactory.createFrame(object);
    }

    /**
     * Shows the new machine dialog box.
     *
     * @param event the action event
     */
    public void actionPerformed(ActionEvent event) {
        showNew();
    }

    /**
     * The dialog box that allows one to create new environments.
     */
    private static class NewDialog extends JFrame {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        /**
         * Instantiates a <CODE>NewDialog</CODE> instance.
         */
        public NewDialog() {
            // super((java.awt.Frame)null, "New Document");
            super("JFLAP 7.1 (Modded by Hocky Yudhiono)");
            getContentPane().setLayout(new GridLayout(0, 1));
            initMenu();
            initComponents();
            setResizable(false);
            this.pack();
            this.setLocation(50, 50);

            this.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent event) {
                    if (Universe.numberOfFrames() > 0) {
                        NewDialog.this.setVisible(false);
                    } else {
                        QuitAction.beginQuit();
                    }
                }
            });
        }

        private void initMenu() {
            // Mini menu!
            JMenuBar menuBar = new JMenuBar();
            JMenu menu = new JMenu("File");
            if (Universe.CHOOSER != null) {
                MenuBarCreator.addItem(menu, new OpenAction());
            }
            try {
                SecurityManager sm = System.getSecurityManager();
				if (sm != null) {
					sm.checkExit(0);
				}
                MenuBarCreator.addItem(menu, new QuitAction());
            } catch (SecurityException e) {
                // Well, can't exit anyway.
            }
            menuBar.add(menu);
            menu = new JMenu("Help");
            MenuBarCreator.addItem(menu, new NewHelpAction());
            MenuBarCreator.addItem(menu, new AboutAction());
            menuBar.add(menu);
            menu = new JMenu("Batch");
            MenuBarCreator.addItem(menu, new TestAction());
            menuBar.add(menu);
            menu = new JMenu("Preferences");

            JMenu tmPrefMenu = new JMenu("Turing Machine Preferences");
            tmPrefMenu.add(Universe.curProfile.getTuringFinalCheckBox());
            tmPrefMenu.add(Universe.curProfile.getAcceptByFinalStateCheckBox());
            tmPrefMenu.add(Universe.curProfile.getAcceptByHaltingCheckBox());
            tmPrefMenu.add(Universe.curProfile.getAllowStayCheckBox());

            //MenuBarCreator.addItem(menu, new ColorChooserAction());
            MenuBarCreator.addItem(menu, new EmptyStringCharacterAction());
//            menu.add(Universe.curProfile.getTuringFinalCheckBox());
            menu.add(new SetUndoAmountAction());
            menu.add(new ColorChooserAction());

            menu.add(tmPrefMenu);


            menuBar.add(menu);
            setJMenuBar(menuBar);
        }

        private void initComponents() {
            JButton button = null;
            // Let's hear it for sloth!

            button = new JButton("Finite Automaton");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    createWindow(new FiniteStateAutomaton());
                }
            });
            getContentPane().add(button);

            button = new JButton("Mealy Machine");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    createWindow(new MealyMachine());
                }
            });
            getContentPane().add(button);
            button = new JButton("Moore Machine");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    createWindow(new MooreMachine());
                }
            });
            getContentPane().add(button);

            button = new JButton("Pushdown Automaton");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Object[] possibleValues =
                        {"Multiple Character Input", "Single Character Input"};
                    Object selectedValue = JOptionPane.showInputDialog(null,
                        "Type of PDA Input", "Input",
                        JOptionPane.INFORMATION_MESSAGE, null,
                        possibleValues, possibleValues[0]);
                    if (selectedValue == possibleValues[0]) {
                        createWindow(new PushdownAutomaton());
                    } else if (selectedValue == possibleValues[1]) {
                        createWindow(new PushdownAutomaton(true));
                    }
                }
            });
            getContentPane().add(button);

            button = new JButton("Turing Machine");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    createWindow(new TuringMachine(1));
                }
            });
            getContentPane().add(button);

            button = new JButton("Multi-Tape Turing Machine");
            button.addActionListener(new ActionListener() {
                private Integer[] INTS = null;

                public void actionPerformed(ActionEvent e) {
                    if (INTS == null) {
                        INTS = new Integer[4];
						for (int i = 0; i < INTS.length; i++) {
							INTS[i] = new Integer(i + 2);
						}
                    }
                    Number n = (Number) JOptionPane.showInputDialog(
                        NewDialog.this.getContentPane(), "How many tapes?",
                        "Multi-tape Machine", JOptionPane.QUESTION_MESSAGE,
                        null, INTS, INTS[0]);
					if (n == null) {
						return;
					}
                    createWindow(new TuringMachine(n.intValue()));
                }
            });
            getContentPane().add(button);

            button = new JButton("Turing Machine With Building Blocks");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    createWindow(new TuringMachineBuildingBlocks(1));
                }
            });
            getContentPane().add(button);

            button = new JButton("Grammar");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    createWindow(new ContextFreeGrammar());
                }
            });
            getContentPane().add(button);

            button = new JButton("L-System");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    createWindow(new LSystem());
                }
            });
            getContentPane().add(button);

            button = new JButton("Regular Expression");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    createWindow(new RegularExpression());
                }
            });
            getContentPane().add(button);

            button = new JButton("Regular Pumping Lemma");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    createWindow(new RegPumpingLemmaChooser());
                }
            });
            getContentPane().add(button);

            button = new JButton("Context-Free Pumping Lemma");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    createWindow(new CFPumpingLemmaChooser());
                }
            });
            getContentPane().add(button);
        }
    }
}
