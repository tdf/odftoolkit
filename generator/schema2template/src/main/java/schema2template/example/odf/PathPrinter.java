/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Copyright 2009, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * <p>Use is subject to license terms.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0. You can also obtain a copy of the License at
 * http://odftoolkit.org/docs/license.txt
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 *
 * <p>See the License for the specific language governing permissions and limitations under the
 * License.
 *
 * <p>**********************************************************************
 */
package schema2template.example.odf;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.NameClassAndExpression;
import com.sun.msv.grammar.ReferenceExp;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.logging.Logger;
import schema2template.model.MSVExpressionInformation;
import schema2template.model.MSVExpressionType;
import schema2template.model.MSVExpressionVisitorType;
import schema2template.model.MSVNameClassVisitorList;
import schema2template.model.PuzzlePiece;
import schema2template.model.XMLModel;

/**
 * ODF example class to print the MSV expressions in between a PuzzlePiece parent element and a
 * direct PuzzlePiece child element.
 *
 * <p>Example of a direct child: table:table -&gt; table:table-row<br>
 * Example of a non-direct child: table:table -&gt; table:table-cell
 *
 * <p>Directly change the string constants EXAMPLE_PARENT and EXAMPLE_CHILD in the source code to
 * set parent and child element.
 */
public class PathPrinter {

  private static final Logger LOG = Logger.getLogger(PathPrinter.class.getName());
  XMLModel xmlModel;

  PathPrinter(XMLModel xmlModel) {
    this.xmlModel = xmlModel;
  }

  public List<String> printChildPaths(String parentElementName, String childElementName) {
    SortedSet<PuzzlePiece> pieces = xmlModel.getElements(parentElementName);
    if (pieces == null) {
      LOG.severe("No parent element found by the given name: " + parentElementName);
      return null;
    } else {
      if (pieces.size() > 1) {
        LOG.severe(
            "There were more than one element by the parent name '"
                + parentElementName
                + "'. Dropped all instances but one.");
      }
    }
    PuzzlePiece parent = pieces.first();

    pieces = xmlModel.getElements(childElementName);
    if (pieces == null) {
      LOG.severe("No child element found by the given name: " + childElementName);
      return null;
    } else {
      if (pieces.size() > 1) {
        LOG.severe(
            "There were more than one element by the child name '"
                + childElementName
                + "'. Dropped all instances but one.");
      }
    }
    PuzzlePiece child = pieces.first();
    return printChildPaths(parent, child);
  }

  public List<String> printChildPaths(PuzzlePiece parent, PuzzlePiece child) {

    MSVExpressionInformation info = new MSVExpressionInformation(parent.getExpression(), null);

    List<List<Expression>> paths = null;
    if (child != null) {
      paths = info.getPathsContaining(child.getExpression());
    } else {
      paths = info.getPathsContaining(parent.getExpression());
    }
    if (paths == null) {
      return null;
    }
    return printChildPaths(paths);
  }

  public static List<String> printChildPaths(List<List<Expression>> paths) {
    final MSVExpressionVisitorType typeVisitor = new MSVExpressionVisitorType();
    final MSVNameClassVisitorList nameVisitor = new MSVNameClassVisitorList();
    List<String> retval = new ArrayList<>(paths.size());
    for (List<Expression> path : paths) {
      boolean first = true;
      String wayString = "";
      for (Expression step : path) {
        MSVExpressionType type = (MSVExpressionType) step.visit(typeVisitor);
        if (type == MSVExpressionType.REF) {
          wayString = wayString.concat(" -> REF " + ((ReferenceExp) step).name);
          continue;
        }
        String name = type.toString();
        String qname = "";
        // NameClassAndExpression is an MSV class for abstract named expressions (ie. attributes and
        // elements)
        if (step instanceof NameClassAndExpression) {
          List<String> names =
              (List<String>) ((NameClassAndExpression) step).getNameClass().visit(nameVisitor);
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
}
