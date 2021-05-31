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


package jflap.gui;

import java.awt.*;
import javax.swing.*;
import jflap.automata.Automaton;
import jflap.automata.AutomatonSimulator;
import jflap.automata.Configuration;
import jflap.gui.action.SimulateAction;

/**
 * Input GUI BOX
 * Not a great looking GUI, but it gets the job done. NOTE: It is no longer used in JFLAP
 *
 * @author Kyung Min (Jason) Lee
 */
public class InputBox extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final Component myComponent;
    private String myInputString;
    private final String myTitle;

    private AutomatonSimulator mySimulator;

    private final SimulateAction myAction;

    private boolean myIsTuringMachine;

    private Automaton myAutomaton;

    public InputBox(SimulateAction simulateAction, Component component, String string) {
        myAction = simulateAction;
        myComponent = component;
        myTitle = string;

        JPanel panel = new JPanel(new GridLayout(3, 2));
        JTextField[] fields = new JTextField[1];
        for (int i = 0; i < 1; i++) {
            panel.add(new JLabel(myTitle + " "));
            panel.add(fields[i] = new JTextField());
        }
        JButton jb = new JButton("Open Input File");
        panel.add(jb);
        int result = JOptionPane.showOptionDialog(component, panel, myTitle,
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, null, null);
        if (result != JOptionPane.YES_OPTION && result != JOptionPane.OK_OPTION) {
            return;
        }
        String[] input = new String[1];
        for (int i = 0; i < 1; i++) {
            input[i] = fields[i].getText();
        }

        System.out.println(input[0]);
/*		
		JButton j=new JButton("Click to Open Input File");
		j.setSize(150,50);
		j.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser ourChooser=new JFileChooser (System.getProperties().getProperty("user.dir"));
				int retval=ourChooser.showOpenDialog(null);
				File f=null;
				if (retval==JFileChooser.APPROVE_OPTION)
				{
					f=ourChooser.getSelectedFile();
					try {
						Scanner sc=new Scanner(f);
						myInputString=sc.nextLine();
						exit();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generate catch block
						JOptionPane.showConfirmDialog(myComponent, "Error", "Error in the file!", JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					}
					
				}
				
			}
			
		});
		JButton jb=new JButton();
		jb.setText("or Type Input Manually");
		jb.setSize(150,50);
		jb.setEnabled(false);
		jb.setBorderPainted(false);
		JPanel top=new JPanel();
		top.setSize(300,50);
		top.add(j, BorderLayout.WEST);
		top.add(jb, BorderLayout.EAST);
	
		
		JPanel bottom=new JPanel();
		final JTextField jt=new JTextField(20);
		jt.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				myInputString=jt.getText();
				exit();
			}
			
		});
		bottom.add(jt);
		JButton jbb=new JButton("OK");
		jbb.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				myInputString=jt.getText();
				exit();
			}
			
		});
		bottom.add(jbb);
		this.setTitle(myTitle);
		this.setSize(400,100);
		this.setVisible(true);
		this.setLocation(300, 300);
		this.add(top, BorderLayout.NORTH);
		this.add(bottom, BorderLayout.CENTER);
		
		*/
    }

    public static void main(String[] args) {
        InputBox ib = new InputBox(null, null, "read");

    }

    public String getInputString() {

        return myInputString;
    }

    public void exit() {
        System.out.println(myInputString);
        if (myIsTuringMachine) {
            //	configs = ((TMSimulator) simulator).getInitialConfigurations(s);
        } else {
            Configuration[] configs = mySimulator.getInitialConfigurations(myInputString);
            myAction.handleInteraction(myAutomaton, mySimulator, configs, myInputString);
        }
        this.dispose();
    }

    public void addSimulator(Automaton atm, AutomatonSimulator simulator, boolean isTuring) {
        // TODO Auto-generated method stub
        myAutomaton = atm;
        mySimulator = simulator;
        myIsTuringMachine = isTuring;
    }
}
