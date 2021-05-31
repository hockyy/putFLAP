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


package jflap.file.xml;

import java.util.Map;
import jflap.grammar.Grammar;
import jflap.grammar.Production;
import jflap.grammar.UnboundGrammar;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This transducer is the codec for {@link jflap.grammar.Grammar} objects.
 *
 * @author Thomas Finley
 */

public class GrammarTransducer extends AbstractTransducer {
    /**
     * The tag name for productions.
     */
    public static final String PRODUCTION_NAME = "production";
    /**
     * The tag name for the left of the production.
     */
    public static final String PRODUCTION_LEFT_NAME = "left";
    /**
     * The tag name for the right of the production.
     */
    public static final String PRODUCTION_RIGHT_NAME = "right";
    /**
     * The comment for the list of productions.
     */
    private static final String COMMENT_PRODUCTIONS = "The list of productions.";

    /**
     * Returns a production for a given node.
     *
     * @param node the node the encapsulates a production
     */
    public static Production createProduction(Node node) {
        Map<String, String> e2t = elementsToText(node);
        String left = e2t.get(PRODUCTION_LEFT_NAME);
        String right = e2t.get(PRODUCTION_RIGHT_NAME);
        if (left == null) {
            left = "";
        }
        if (right == null) {
            right = "";
        }
        return new Production(left, right);
    }

    /**
     * Returns an element that encodes a given production.
     *
     * @param document   the document to create the element in
     * @param production the production to encode
     * @return an element that encodes a production
     */
    public static Element createProductionElement(Document document,
                                                  Production production) {
        Element pe = createElement(document, PRODUCTION_NAME, null, null);
        pe.appendChild(createElement(document, PRODUCTION_LEFT_NAME, null,
            production.getLHS()));
        pe.appendChild(createElement(document, PRODUCTION_RIGHT_NAME, null,
            production.getRHS()));
        return pe;
    }

    /**
     * Returns the type this transducer recognizes, "jflap.grammar".
     *
     * @return the string "jflap.grammar"
     */
    public String getType() {
        return "grammar";
    }

    /**
     * Given a document, this will return the corresponding jflap.grammar encoded in
     * the DOM document.
     *
     * @param document the DOM document to convert
     * @return the {@link jflap.grammar.Grammar} instance
     */
    public java.io.Serializable fromDOM(Document document) {
        Grammar g = new UnboundGrammar();
        NodeList list = document.getDocumentElement().getElementsByTagName(
            PRODUCTION_NAME);
        for (int i = 0; i < list.getLength(); i++) {
            Production p = createProduction(list.item(i));
            g.addProduction(p);
        }
        return g;
    }

    /**
     * Given a JFLAP jflap.grammar, this will return the corresponding DOM encoding of
     * the structure.
     *
     * @param structure the JFLAP jflap.grammar to encode
     * @return a DOM document instance
     */
    public Document toDOM(java.io.Serializable structure) {
        Grammar grammar = (Grammar) structure;
        Document doc = newEmptyDocument();
        Element se = doc.getDocumentElement();
        // Add the productions as subelements of the structure element.
        Production[] productions = grammar.getProductions();
        if (productions.length > 0) {
            se.appendChild(createComment(doc, COMMENT_PRODUCTIONS));
        }
        for (int i = 0; i < productions.length; i++) {
            se.appendChild(createProductionElement(doc, productions[i]));
        }
        // Return the completed document.
        return doc;
    }
}
