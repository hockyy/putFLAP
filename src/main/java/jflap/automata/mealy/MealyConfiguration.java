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


package jflap.automata.mealy;

import jflap.automata.Configuration;
import jflap.automata.State;

/**
 * A <code>MealyConfiguration</code> object is a <code>Configuration</code>
 * object with an additional field for the read string and the output string.
 * The output is saved as a separate field that is passed in through the
 * constructor. All additional output appended to the output passed in.
 * A <code>MealyConfiguration</code> serves both the
 * {@link jflap.automata.mealy.MealyMachine} and the {@link jflap.automata.mealy.MooreMachine}
 *
 * @author Jinghui Lim
 */
public class MealyConfiguration extends Configuration {
    /**
     * The total read.
     */
    private String myInput = "";
    /**
     * The unprocesed read.
     */
    private String myUnprocessedInput = "";
    /**
     * The processed output.
     */
    private String myOutput = "";

    /**
     * Constructs a new <code>MealyConfiguration</code>.
     *
     * @param state       the state that the MealyMachine is currently in
     * @param parent      the configuration that is the parent of this configuration
     * @param input       the entire test read
     * @param unprocessed the unprocessed read
     * @param output      the ouput produced before this configuration
     */
    public MealyConfiguration(State state, MealyConfiguration parent,
                              String input, String unprocessed, String output) {
        super(state, parent);
        myInput = input;
        myUnprocessedInput = unprocessed;
        myOutput = output;
    }

    /**
     * Returns the total read.
     *
     * @return total read
     */
    public String getInput() {
        return myInput;
    }

    /**
     * Returns the unprocessed read.
     *
     * @return unprocessed read
     */
    public String getUnprocessedInput() {
        return myUnprocessedInput;
    }

    /**
     * Changes the unprocessed read.
     *
     * @param input the string that will replace the unprocessed read
     */
    public void setUnprocessedInput(String input) {
        myUnprocessedInput = input;
    }

    /**
     * Returns the output produced by previous ancestor configurations.
     * This is the only way to obtain the output produced by a Mealy
     * machine, as <code>Configuration.isAccept()</code> returns a
     * <code>boolean</code>.
     *
     * @return output by ancestor configurations.
     */
    public String getOutput() {
        return myOutput;
    }

    /**
     * Returns a string representation of this configuration.
     *
     * @return string representation of this configuration.
     */
    public String toString() {
        return super.toString() + ": " + getUnprocessedInput() + "--" + getOutput();
    }

    /**
     * Accepts if there is no more unprocessed read (all of the read has been
     * process and output generated).
     *
     * @return <code>true</code> if all read has been processed,
     * <code>false</code> otherwise.
     */
    public boolean isAccept() {
        return getUnprocessedInput().length() == 0;
    }

    /**
     * This function checks if two configurations are equal. Two configurations
     * are equal if they have the same output and unprocessed read and satisfy
     * the <code>.equals()</code> test of the base <code>Configuration</code>
     * class.
     *
     * @param configuration the configuration to check for equality
     * @return <code>true</code> if the two configurations are equal,
     * <code>false</code> otherwise
     * @see jflap.automata.Configuration#equals(Object)
     */
    public boolean equals(Object configuration) {
        if (configuration == this) {
            return true;
        }
        try {
            MealyConfiguration config = (MealyConfiguration) configuration;
            return super.equals(config) &&
                myUnprocessedInput.equals(config.myUnprocessedInput) &&
                myOutput.equals(config.myOutput);
        } catch (ClassCastException e) {
            return false;
        }
    }

    /**
     * Returns a hashcode for this object.
     *
     * @return hashcode for this object
     */
    public int hashCode() {
        return super.hashCode() ^ myUnprocessedInput.hashCode() ^ myOutput.hashCode();
    }
}
