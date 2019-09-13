/** **********************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2009, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *********************************************************************** */
package schema2template.model;

import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.NameClassAndExpression;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SequenceExp;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import static schema2template.model.PuzzlePiece.CHILD_VISITOR;
import static schema2template.model.PuzzlePiece.getName;
import static schema2template.model.PuzzlePiece.getType;

/**
 * Create a Tinkerpop Graph MSV expression like:
 * <ul>
 * <li>which attributes are mandatory<li>
 * <li>which child elements are singletons</li>
 * <li>can it have text content</li>
 * </ul>
 */
class GraphSupport {

    public GraphSupport(Expression exp, String schemaFileName) {
        Graph g = buildGraph(exp);
        if (exp instanceof ElementExp && schemaFileName != null) {
            String elementName = getName((NameClassAndExpression) exp);
            saveGraph(g, elementName, schemaFileName);
        }
        // Optimize the graph
    }


    private static void saveGraph(Graph g, String elementName, String schemaFileName) {
        String fileName;
        if (elementName.equals("*")) {
            fileName = "ALL_ELEMENTS";
        } else {
            fileName = elementName.replace(" | ", "_").replace(":", "_");
        }
        try {
            String directoryName = schemaFileName.replace(".", "_");
            File f = new File("target" + File.separator + "graphML" + File.separator + directoryName);
            f.mkdirs();
            // g.io(IoCore.gryo()).writeGraph("target" + File.separator + "graphML" + File.separator + directoryName + File.separator + fileName + ".kryo");
            g.io(IoCore.graphml()).writeGraph("target" + File.separator + "graphML" + File.separator + directoryName + File.separator + fileName + ".graphml");

        } catch (IOException ex) {
            Logger.getLogger(GraphSupport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Creates a sub graph for elements & attributes with their element children
     * as last descendant nodes
     */
    static public Graph buildGraph(Expression exp) {
        return buildGraph(null, null, null, exp, null);
    }

    /* Starting from every XML attribute and element (ie. ElementExp or AttributeExp) a subgraph of the XML RelaxNG schema is being created.
     */
    private static Graph buildGraph(Graph g, Vertex v, Vertex parentV, Expression exp, Expression parentExp) {
        if (g == null) {
            g = TinkerGraph.open();
            v = createVertex(g, exp);
        }

        // stop building the graph at element and attribtue children
        addGraphProperties(g, v, parentV, exp, parentExp);
        if (!(exp instanceof NameClassAndExpression) || parentExp == null) {
            List<Expression> children = (List<Expression>) exp.visit(CHILD_VISITOR);
            Integer newChildNo = 0;
            for (Expression newChildExp : children) {
                Vertex newChildV = createVertex(g, newChildExp);
                // only for sequences the order of children is important
                if (exp instanceof SequenceExp && parentV != null) {
                    newChildNo++;
                    v.addEdge("has", newChildV, "order", newChildNo.toString(), "color", "#00ee00"); // sequence edges using color green2 see http://www.farb-tabelle.de/de/rgb2hex.htm?q=green2
                }
                g = buildGraph(g, newChildV, v, newChildExp, exp);
            }
        }
        return g;
    }

    private static Vertex createVertex(Graph g, Expression exp){
        Vertex v = null;
        String type = getType(exp).toString();
        if (type != null && !type.isEmpty()) {
            v = g.addVertex(type); // for the root element
        }
        return v;
    }

    private static Vertex addGraphProperties(Graph g, Vertex v, Vertex parentVertex, Expression exp, Expression parentExp) {
        // property: type
        if(v == null){
            v = createVertex(g, exp);
        }
        String type = getType(exp).toString();

        // property: name  -- only for all ElementExp || AttributeExp
        // property: label -- for all
        if (exp instanceof NameClassAndExpression) {
            String name = getName((NameClassAndExpression) exp);
            v.property("label", name);
            if (exp instanceof ElementExp) {
                v.property("color", "#6495ed"); // elements using CornflowerBlue
            } else { // attribute}
                v.property("color", "#ee0000"); // attributes using red2 see http://www.farb-tabelle.de/de/rgb2hex.htm?q=red2
            }
        } else if (exp instanceof ReferenceExp) {
            // COLORS WILL DIFFERENTATITE MAKING VERTEX COMPARISON EASIER
            // v.property("label", "Ref: " + ((ReferenceExp) exp).name);
            String refName = ((ReferenceExp) exp).name;
            if(refName == null){
                refName = "NONAME";
            }
            v.property("label", refName);
            v.property("color", "#ffd700"); // gold1
        } else {
            v.property("label", type);
            v.property("type", type);
        }

        // if not already added an edge for the sequence
        if (parentVertex != null && !(parentExp instanceof SequenceExp)) {
            parentVertex.addEdge("has", v);
        }
        return v;
    }
}
