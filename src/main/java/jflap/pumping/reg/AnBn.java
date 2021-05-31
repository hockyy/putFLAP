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


package jflap.pumping.reg;

import jflap.pumping.LemmaMath;
import jflap.pumping.RegularPumpingLemma;

/**
 * The jflap.regular jflap.pumping lemma for <i>L</i> =
 * {<i>a<sup>n</sup>b<sup>n</sup></i> : <i>n</i> &#8805; 0}.
 *
 * @author Jinghui Lim & Chris Morgan
 */
public class AnBn extends RegularPumpingLemma {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public String getTitle() {
        return "a^n b^n : n >= 0";
    }

    public String getHTMLTitle() {
        return "<i>a<sup>n</sup>b<sup>n</sup></i> : <i>n</i> " + GREATER_OR_EQ + " 0";
    }

    public void setDescription() {
        partitionIsValid = false;
        explanation =
            "For any <i>m</i> value, a possible value for <i>w</i> is \"a<sup><i>m</i></sup>" +
                "b<sup><i>m</i></sup>\".  The <i>y</i> value thus would be a multiple of \"a\".  " +
                "For any <i>i</i> " + NOT_EQUAL + " 1, n<sub>a</sub> " + NOT_EQUAL +
                " n<sub>b</sub>, giving a string " +
                "which is not in the language.  Thus, the language is not jflap.regular.";
    }

    protected void chooseW() {
        w = pumpString("a", m) + pumpString("b", m);
    }

    public void chooseI() {
        i = LemmaMath.flipCoin();
    }

    protected void setRange() {
        myRange = new int[] {4, 18};
    }

    public boolean isInLang(String s) {
        int a, b;
        char[] list = new char[] {'a', 'b'};
        if (LemmaMath.isMixture(s, list)) {
            return false;
        }

        a = LemmaMath.countInstances(s, 'a');
        b = LemmaMath.countInstances(s, 'b');
        return a == b;
    }
}
