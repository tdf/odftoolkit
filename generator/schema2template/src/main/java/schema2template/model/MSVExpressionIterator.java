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
package schema2template.model;

import static schema2template.SchemaToTemplate.DEBUG;

import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.NameClassAndExpression;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.ValueExp;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

/**
 * Iterates through the MSV expression tree.
 *
 * <p>Traversing the MSV Tree structure by 1) First trying to get the child (going as deep as
 * possible) 2) Second if no child is available, trying to get the next sibling 3) If no sibling
 * available, get a sibling of the parent (going back to step 1)
 *
 * <p>Also has the ability to limit iteration to given subclasses and to limit subtree to the next
 * element expressions below.
 */
public final class MSVExpressionIterator implements Iterator<Expression> {

  private static final Logger LOG = Logger.getLogger(MSVExpressionIterator.class.getName());
  private Expression mCurrentExpression; // the expression that will be received by next()
  // public int mCurrentExpressionDepth; // Level of current expression starting with 0
  private MSVExpressionVisitorChildren mVisitor;
  // list of already visited expressions to avoid endless recursion
  // The stack assists the iteration to go back up (usually done in by recursion)
  // The stack contains the next expression, parent, grandparent, ...
  private Stack<UniqueAncestor> mAncestorsAndCurrent;
  // to prevent enless loops, known Element expression will be remembered and not again reentered
  // Situation: Element a contains Element b contains Element a, will stop after the second and not
  // continuing with b
  private HashSet<Expression> mKnownElementExpressions;

  // The vertical tree position is remembered by the stack, the horizontal position will be
  // remembered by a sibling index
  private class UniqueAncestor {

    public UniqueAncestor(Expression exp, int siblingIndex) {
      mExp = exp;
      mSiblingIndex = siblingIndex;
    }

    private Expression mExp;
    private int mSiblingIndex;
  }
  // limit browsing to subclasses of Expression
  private Class mDesiredExpression;
  // if false, only return direct children of root. Don't return root as first element or grand
  // children
  private boolean mOnlyChildren;
  private int mCurrentDepth = 0;
  public static final boolean ALL_SUBTREE = true;
  public static final boolean DIRECT_CHILDREN_ONLY = true;

  public int getDepth() {
    return mCurrentDepth;
  }

  /**
   * Iterate through the expression tree
   *
   * @param root Expression root
   */
  public MSVExpressionIterator(Expression root) {
    this(root, Expression.class, false);
  }

  /**
   * Iterate through the expression tree, but only return objects of desiredExpression
   *
   * @param root Expression root
   * @param desiredExpression Limit returned expressions to subclasses of desiredExpression
   */
  public MSVExpressionIterator(Expression root, Class desiredExpression) {
    this(root, desiredExpression, false);
  }

  /**
   * Iterate..., but only return objects of desiredExpression and (if not onlyChildren) don't go to
   * children of ElementExp elements (this does not concern root node!).
   *
   * <p>Example: Root is table:table. If you choose onlyChildren=false and to limit
   * desiredExpression=ElementExp.class, then you will get all direct element children of
   * table:table, like table:table-row. But you won't get the children of table:table-row.
   *
   * @param root Expression root
   * @param desiredExpression Limit returned expressions to subclasses of desiredExpression
   * @param onlyChildren if only children should be returned
   */
  public MSVExpressionIterator(Expression root, Class desiredExpression, boolean onlyChildren) {
    // initialize members
    mCurrentExpression = root;
    mDesiredExpression = desiredExpression;
    mOnlyChildren = onlyChildren;

    // create helpers
    mVisitor = new MSVExpressionVisitorChildren();
    mKnownElementExpressions = new HashSet<Expression>();

    // Initialize status
    mAncestorsAndCurrent = new Stack<UniqueAncestor>();
    mAncestorsAndCurrent.push(new UniqueAncestor(root, 0));

    // make sure that there is at least one desired expression - for hasNext()
    while (!mDesiredExpression.isInstance(mCurrentExpression) && mCurrentExpression != null) {
      mCurrentExpression = getNextExpression();
    }

    // Ignore root, if only children are desired
    if (mOnlyChildren && root == mCurrentExpression) {
      mCurrentExpression = getNextExpression();
    }
  }

  /**
   * Iterates the MSVExpressionTree and dumps it into a string
   *
   * @return the MSVExpressionTree serialized into a String
   */
  public static String dumpMSVExpressionTree(Expression rootExpression) throws Exception {
    MSVExpressionIterator iterator = new MSVExpressionIterator(rootExpression);
    StringBuilder builder = new StringBuilder();
    while (iterator.hasNext()) {
      Expression expr = iterator.next();
      builder.append(dumpMSVExpression(expr, iterator.getDepth())).append("\n");
    }
    return builder.toString();
  }

  private static String dumpMSVExpression(Expression expr, int depth) {
    String returnValue = null;
    MSVExpressionVisitorType typeVisitor = new MSVExpressionVisitorType();
    MSVNameClassVisitorList nameVisitor = new MSVNameClassVisitorList();
    MSVExpressionType type = (MSVExpressionType) expr.visit(typeVisitor);
    returnValue = (depth + ": " + type.toString());

    // AttributeExp, ElementExp
    if (expr instanceof NameClassAndExpression) {
      List<String> names =
          (List<String>) ((NameClassAndExpression) expr).getNameClass().visit(nameVisitor);
      for (String name : names) {
        returnValue += (" \"" + name + "\",");
        if (DEBUG) System.out.println(returnValue);
      }
    } else if (expr instanceof ReferenceExp) {
      returnValue += (" '" + ((ReferenceExp) expr).name + "',");
      if (DEBUG) System.out.println(returnValue);
    } else if (type == MSVExpressionType.VALUE) {
      returnValue += (" '" + ((ValueExp) expr).value.toString() + "',");
      if (DEBUG) System.out.println(returnValue);
    } else if (type == MSVExpressionType.DATA) {
      returnValue += (" '" + ((DataExp) expr).getName().localName + "',");
      if (DEBUG) System.out.println(returnValue);
    } else {
      if (DEBUG) System.out.println(returnValue);
    }
    return returnValue;
  }

  public boolean hasNext() {
    return (mCurrentExpression != null) ? true : false;
  }

  /**
   * Iterating the Tree like the following If there are (unvisited) children -> go down If there are
   * no (unvisited) children, but unvisted siblings -> go up and right
   */
  private Expression getNextExpression() {
    Expression nextExpression = null;
    // the current expression might be null if the desired type of expression was never found in the
    // tree
    if (mCurrentExpression != null) {
      // if all tree is desired, or root, or if it is not element expression
      if (!mOnlyChildren
          || mAncestorsAndCurrent.size() == 1
          || !(mAncestorsAndCurrent.peek().mExp instanceof ElementExp)) {
        List<Expression> children = (List<Expression>) mCurrentExpression.visit(mVisitor);
        // see if we can go DOWN the tree
        if (children.size() > 0) {
          Expression nextExpCandidate = children.get(0);
          // DO NOT expand elements which occur more than one time in the ancestors hierarchy (i.e.
          // since we compute the last element: Do not expand it, if it also occurs before)
          mAncestorsAndCurrent.push(new UniqueAncestor(nextExpCandidate, 0));
          if (isNoKnownElement(nextExpCandidate)) {
            // GO DOWN - Proceed with first child
            nextExpression = nextExpCandidate;
          }
        }
      }

      // if you could not get depper, but you can go up
      // if there was no first child for the next expression and still some parent not being the
      // root
      while (nextExpression == null && mAncestorsAndCurrent.size() > 1) {
        // go one up the stack
        UniqueAncestor uniqueAncestor = mAncestorsAndCurrent.pop();
        // get the new parent
        Expression parent = mAncestorsAndCurrent.peek().mExp;
        // to get the siblings
        List<Expression> siblings = (List<Expression>) parent.visit(mVisitor);
        // get the unvisted sibling index
        final int nextSiblingIndex = uniqueAncestor.mSiblingIndex + 1;
        if (nextSiblingIndex < siblings.size()) {
          Expression nextExpCandidate = siblings.get(nextSiblingIndex);
          // DO NOT expand elements which occur more than one time in the ancestors hierarchy (i.e.
          // since we compute the last element: Do not expand it, if it also occurs before)
          mAncestorsAndCurrent.push(new UniqueAncestor(nextExpCandidate, nextSiblingIndex));
          if (isNoKnownElement(nextExpCandidate)) {
            // GO RIGHT - Add next sibling to the stack
            nextExpression = nextExpCandidate;
          }
        }
      }
    }
    return nextExpression;
  }

  private boolean isNoKnownElement(Expression exp) {
    boolean isNew = false;
    if (!(exp instanceof ElementExp) || !mKnownElementExpressions.contains(exp)) {
      mKnownElementExpressions.add(exp);
      isNew = true;
    } else {
      //			LOG.info("Found known element expression:" + dumpMSVExpression(exp, this.getDepth()));
    }
    return isNew;
  }

  public Expression next() {
    if (mCurrentExpression == null) {
      return null;
    }
    Expression retVal = mCurrentExpression;
    mCurrentDepth = mAncestorsAndCurrent.size() - 1;
    mCurrentExpression = getNextExpression();

    // as there is always a desired expression, make sure the next one is adequate
    while (!mDesiredExpression.isInstance(mCurrentExpression) && mCurrentExpression != null) {
      mCurrentExpression = getNextExpression();
    }
    return retVal;
  }

  // Unsupported
  public void remove() {
    throw new UnsupportedOperationException("Not supported.");
  }
}
