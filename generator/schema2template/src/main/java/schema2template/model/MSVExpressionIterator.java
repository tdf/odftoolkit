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
package schema2template.model;

import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * Iterates through the MSV expression tree.
 * <p>Also has the ability to limit iteration to given subclasses and to limit subtree
 * to the next element expressions below. </p>
 */
public class MSVExpressionIterator implements Iterator<Expression> {

    private Expression mNextElement;        // Next Element
    private MSVExpressionVisitorChildren mVisitor;
    // list of already visited expressions to avoid endless recursion
    private HashSet<Expression> mVisited;
    // current expression, parent, grandparent, ...
    private Stack<Expression> mAncestors;
    // limit browsing to subclasses of Expression
    private Class mSuperclass;
    // if false, only return direct children of root. Don't return root as first element or grand children
    private boolean mAllElements;

    public static final boolean ALL_ELEMENTS = true;
    public static final boolean DIRECT_CHILDREN_ONLY = false;

    /**
     * Iterate through the expression tree
     *
     * @param root Expression root
     */
    public MSVExpressionIterator(Expression root) {
        this(root, Expression.class, true);
    }

    /**
     *  Iterate through the expression tree, but only return objects of superClass
     *
     * @param root Expression root
     * @param superClass Limit returned expressions to subclasses of superClass
     */
    public MSVExpressionIterator(Expression root, Class superClass) {
        this(root, superClass, true);
    }


	/**
     * Iterate..., but only return objects of superClass and (if not allElements)
     * don't go to children of ElementExp elements (this does not concern root node!).
     *
     * <p>Example: Root is table:table. If you choose allElements=false and to limit
     * superClass=ElementExp.class, then you will get all direct element children of table:table,
     * like table:table-row. But you won't get the children of table:table-row.
     * </p>
     *
     * @param root Expression root
     * @param superClass Limit returned expressions to subclasses of superClass
     * @param allElements Wether element subchildren should be expanded
     */
    public MSVExpressionIterator(Expression root, Class superClass, boolean allElements) {
        mSuperclass = superClass;
        mAllElements = allElements;
        mAncestors = new Stack<Expression>();
        mAncestors.push(root);
        mVisited = new HashSet<Expression>();
        mVisitor = new MSVExpressionVisitorChildren();
        mNextElement = root;

        // Ignore root if we only want direct children
        if (hasNext() && !mAllElements) {
            next();
            mVisited.remove(root); // give root a chance in case it's a child of itself
        }

        // Got to first node of valid type
        if (hasNext() && !mSuperclass.isInstance(mNextElement)) {
            next();
        }

    }

    @Override
    public boolean hasNext() {
        return (mNextElement != null) ? true : false;
    }

    // Configure mNextElement. Please note that the current mNextElement has 
    // to be last stack element and has to be marked visited!
    private void setNext() {
        while (true) {
            List<Expression> children = (List<Expression>) mNextElement.visit(mVisitor);

            mNextElement = null;

            // Only proceed with children if it is allowed to expand ElementExp nodes (not counting root)
            if (mAllElements || mAncestors.size() <= 1 || !(mAncestors.peek() instanceof ElementExp)) {
                // Proceed with children. But ignore all already visited elements to avoid recursion.
                for (Expression candidate : children) {
                    if (!mVisited.contains(candidate)) {
                        mNextElement = candidate;
                        mAncestors.push(mNextElement);
                        break;
                    }
                }
            }

            // if there are no (unvisited) children -> go up, up, ..., and right
            while (mNextElement == null && mAncestors.size() > 1) {
                mAncestors.pop();
                Expression parent = mAncestors.peek();
                List<Expression> siblings = (List<Expression>) parent.visit(mVisitor);

                // Proceed with siblings
                for (Expression candidate : siblings) {
                    if (!mVisited.contains(candidate)) {
                        mNextElement = candidate;
                        mAncestors.push(mNextElement);
                        break;
                    }
                }
            }

            if (mNextElement == null || mSuperclass.isInstance(mNextElement)) {
                return;
            }

            // Ignore mNextElement since it has the wrong type
            mVisited.add(mNextElement);
        }
    }

    // Get next element of type ElementExp. Internally set mNextElement to next expression.
    @Override
    public Expression next() {
        if (mNextElement == null) {
            return null;
        }
        Expression retval = mNextElement;
        mVisited.add(retval);

        // configure mNextElement
        setNext();

        return retval;
    }

    // Unsupported
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported.");
    }
}
