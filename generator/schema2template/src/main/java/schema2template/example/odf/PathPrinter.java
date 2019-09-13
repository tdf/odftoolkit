/************************************************************************
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
 ************************************************************************/
package schema2template.example.odf;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.NameClassAndExpression;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.reader.trex.ng.RELAXNGReader;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.parsers.SAXParserFactory;
import schema2template.model.MSVExpressionInformation;
import schema2template.model.MSVExpressionType;
import schema2template.model.MSVExpressionVisitorType;
import schema2template.model.MSVNameClassVisitorList;
import schema2template.model.PuzzlePiece;
import schema2template.model.PuzzlePieceSet;

/**
 * ODF example class to print the MSV expressions in between a PuzzlePiece parent element
 * and a direct PuzzlePiece child element.
 *
 * <p>Example of a direct child: table:table -&gt; table:table-row<br />
 * Example of a non-direct child: table:table -&gt; table:table-cell</p>
 *
 * <p>Directly change the string constants EXAMPLE_PARENT and EXAMPLE_CHILD in
 * the source code to set parent and child element.</p>
 */
public class PathPrinter {

    // CHANGE THIS...
    public final static String EXAMPLE_PARENT = "table:table";
    public final static String EXAMPLE_CHILD = "table:table-row";
    PuzzlePiece mParent;
    MSVExpressionInformation mInfo;


    PathPrinter(PuzzlePiece parent) {
        mParent = parent;
        mInfo = new MSVExpressionInformation(parent.getExpression(), null);
    }

    /**
     * Map Name to PuzzlePiece(s).
     */
    static Map<String, SortedSet<PuzzlePiece>> createDefinitionMap(Set<PuzzlePiece> definitions) {
        Map<String, SortedSet<PuzzlePiece>> retval = new HashMap<String, SortedSet<PuzzlePiece>>();
        Iterator<PuzzlePiece> iter = definitions.iterator();
        while (iter.hasNext()) {
            PuzzlePiece def = iter.next();
            SortedSet<PuzzlePiece> multiples = retval.get(def.getQName());
            if (multiples == null) {
                multiples = new TreeSet<PuzzlePiece>();
                retval.put(def.getQName(), multiples);
            }
            multiples.add(def);
        }
        return retval;
    }

    List<String> printChildPaths(PuzzlePiece child) {
        List<List<Expression>> paths = null;
        if (child != null) {
            paths = mInfo.getPathsContaining(child.getExpression());
        } else {
            paths = mInfo.getPathsContaining(mParent.getExpression());
        }
        if (paths == null) {
            return null;
        }
        return printChildPaths(paths);
    }

    public static List<String> printChildPaths(List<List<Expression>> paths){
        final MSVExpressionVisitorType typeVisitor = new MSVExpressionVisitorType();
        final MSVNameClassVisitorList nameVisitor = new MSVNameClassVisitorList();
        List<String> retval = new ArrayList<>(paths.size());
        for (List<Expression> path : paths) {
            boolean first = true;
            String wayString = "";
            for (Expression step : path) {
                MSVExpressionType type = (MSVExpressionType) step.visit(typeVisitor);
                if (type == MSVExpressionType.REF) {
                    wayString = wayString.concat(" -> REF " + ((ReferenceExp) step).name );
                    continue;
                }
                String name = type.toString();
                String qname = "";
				// NameClassAndExpression is an MSV class for abstract named expressions (ie. attributes and elements)
                if (step instanceof NameClassAndExpression) {
                    List<String> names = (List<String>) ((NameClassAndExpression) step).getNameClass().visit(nameVisitor);
                    if (names != null) {
                        boolean firstQ = true;
                        for (String singleQ : names) {
                            if (firstQ) {
                                firstQ = false;
                                qname = singleQ;
                            } else {
                                qname += "," + singleQ;
                            }
                        }
                    }
                }
                if (first) {
                    first = false;
                    wayString = name + " " + qname;
                } else {
                    if (step instanceof NameClassAndExpression) {
                        name = name + " " + qname;
                    }
                    wayString = wayString.concat(" -> " + name);
                }
            }
            retval.add(wayString);
        }
        return retval;
    }

    private static Expression parseOdfSchema(File rngFile) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);

        Expression root = RELAXNGReader.parse(
                rngFile.getAbsolutePath(),
                factory,
                new com.sun.msv.reader.util.IgnoreController()).getTopLevel();

        if (root == null) {
            throw new Exception("Schema could not be parsed.");
        }
        return root;
    }

    public static void main(String[] args) throws Exception {
        Expression root = parseOdfSchema(new File(OdfHelper.odf12RngFile));
        PuzzlePieceSet elements = new PuzzlePieceSet();
        PuzzlePieceSet attributes = new PuzzlePieceSet();
        PuzzlePiece.extractPuzzlePieces(root, elements, attributes, null);
        Map<String, SortedSet<PuzzlePiece>> nameToDefinition = createDefinitionMap(new TreeSet<PuzzlePiece>(elements));

        System.out.println("Print all paths from parent element (e.g. \"text:p\") to direct child element (e.g. \"text:span\")");

        SortedSet<PuzzlePiece> pieces = nameToDefinition.get(EXAMPLE_PARENT);

        if (pieces == null) {
            System.out.println("No parent element found by the given name: " + EXAMPLE_PARENT);
        }

        PuzzlePiece parent = pieces.first();

        pieces = nameToDefinition.get(EXAMPLE_CHILD);

        if (pieces == null) {
            System.out.println("No child element found by the given name: " + EXAMPLE_CHILD);
        }

        PuzzlePiece child = pieces.first();

        if (pieces.size() > 1) {
            System.out.println("There were more than one element by this name. Dropped all instances but one.");
        }

        System.out.println();
        System.out.println("PATHS from " + parent.getQName() + " to " + child.getQName() + ": ");
        System.out.println("---------------------------------------------------------");

        List<String> paths = new PathPrinter(parent).printChildPaths(child);

        if (paths == null) {
            System.out.println("No Path found.");
        } else {
            for (String s : paths) {
                System.out.println(s);
            }
        }
    }
}
