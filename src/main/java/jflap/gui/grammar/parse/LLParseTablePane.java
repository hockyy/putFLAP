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

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import jflap.grammar.parse.LLParseTable;
import jflap.gui.LeftTable;
import jflap.gui.environment.Universe;

/**
 * This holds a LL parse table.
 *
 * @author Thomas Finley
 */

public class LLParseTablePane extends LeftTable {
    /**
     * The built in highlight renderer generator.
     */
    private static final jflap.gui.HighlightTable.TableHighlighterRendererGenerator THRG =
        new TableHighlighterRendererGenerator() {
            private DefaultTableCellRenderer renderer = null;

            public TableCellRenderer getRenderer(int row, int column) {
                if (renderer == null) {
                    renderer = new LambdaCellRenderer();
                    renderer.setBackground(new Color(255, 150, 150));
                }
                return renderer;
            }
        };
    /**
     * The sets cell renderer.
     */
    private static final TableCellRenderer RENDERER = new LambdaCellRenderer();
    /**
     * The parse table for this pane.
     */
    private final LLParseTable table;

    /**
     * Instantiates a new parse table pane for a parse table.
     *
     * @param table the table pane's parse table
     */
    public LLParseTablePane(LLParseTable table) {
        super(table);
        this.table = table;
        setCellSelectionEnabled(true);

        for (int i = 1; i < getColumnCount(); i++) {
            getColumnModel().getColumn(i).setCellRenderer(RENDERER);
        }
    }

    /**
     * Retrieves the parse table in this pane.
     *
     * @return the parse table in this pane
     */
    public LLParseTable getParseTable() {
        return table;
    }

    /**
     * Modified to use the set renderer highlighter.
     */
    public void highlight(int row, int column) {
        highlight(row, column, THRG);
    }

    /**
     * The modified table cell renderer.
     */
    private static class LambdaCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value, boolean isSelected,
                                                       boolean hasFocus, int row,
                                                       int column) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);
            if (hasFocus && table.isCellEditable(row, column)) {
                return l;
            }
            l.setText(
                ((String) value).replace('!', Universe.curProfile.getEmptyString().charAt(0)));
            return l;
        }
    }
}
