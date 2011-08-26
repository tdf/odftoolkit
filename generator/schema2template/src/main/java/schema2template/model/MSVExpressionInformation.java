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

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.NameClassAndExpression;
import com.sun.msv.grammar.OneOrMoreExp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Gather information from one MSV expression like:
 * <ul>
 * <li>which attributes are mandatory<li>
 * <li>which child elements are singletons</li>
 * <li>can it have text content</li>
 * </ul>
 */
public class MSVExpressionInformation {
/*
 * For each Named Expression (i.e. of Type Element or Attribute) we build a path
 *    thisNamedExpression -> Expression subEx -> Expression subsubEx -> ... -> childNamedExpression
 * All Expressions (thisNamedExpression, childNamedExpression and all in between) can be members of
 * multiple paths. Therefore we create a Map Expression->List<path>.
 *
 * If we query this Map for thisNamedExpression, we get all paths. If we query this Map for
 * childNamedExpression, we get all paths from this to the child. To display groups containing
 * one child, we have to query the group Expression to get all other elements of this group.
 */

    private static final MSVExpressionVisitorChildren childVisitor = new MSVExpressionVisitorChildren();
    private static final MSVExpressionVisitorType typeVisitor = new MSVExpressionVisitorType();
    private Map<Expression, List<List<Expression>>> mContainedInPaths;
    private Expression mExpression;
    private boolean mCanHaveText = false;
    // map child to its isSingleton property
    private Set<Expression> mSingletonChildren = new HashSet<Expression>();
    private Set<Expression> mMultipleChildren = new HashSet<Expression>();

    public MSVExpressionInformation(Expression exp) {
        mExpression = exp;
        mContainedInPaths = new HashMap<Expression, List<List<Expression>>>();

        // Builds paths to child elements and child attributes
        List<List<Expression>> paths = new ArrayList<List<Expression>>();
        List<Expression> start = new ArrayList<Expression>(1);
        start.add(exp);
        paths.add(start);
        buildPaths(childVisitor, paths);

        // Test whether an element can have text content
        for (List<Expression> path : paths) {
            if (((MSVExpressionType) path.get(path.size() - 1).visit(typeVisitor)) == MSVExpressionType.STRING) {
                mCanHaveText = true;
                break;
            }
        }

        List<List<Expression>> pathsToChildren = getPathsToClass(paths, NameClassAndExpression.class);

        for (List<Expression> path : pathsToChildren) {
            for (Expression step : path) {
                List<List<Expression>> pathsToStep = mContainedInPaths.get(step);
                if (pathsToStep == null) {
                    pathsToStep = new ArrayList<List<Expression>>(1);
                    pathsToStep.add(path);
                    mContainedInPaths.put(step, pathsToStep);
                } else {
                    if (!pathsToStep.contains(path)) {
                        pathsToStep.add(path);
                    }
                }
            }
        }

        registerChildrenMaxCardinalities(getPathsToClass(paths, ElementExp.class));
    }

    /*
     * Helper method: for one parent element, set property isSingleton for all parent->child relationships
     *
     * @param waysToChildren
     */
    private void registerChildrenMaxCardinalities(List<List<Expression>> waysToChildren) {
        Map<Expression, Boolean> multiples = new HashMap<Expression, Boolean>();        // Cardinality (the opposite of isSingleton): true=N, false=1
        Map<Expression, List<Expression>> paths = new HashMap<Expression, List<Expression>>();

        for (List<Expression> way : waysToChildren) {
            Expression childexp = way.get(way.size()-1);

            Boolean newCardinality = new Boolean(false);                        // Cardinality (the opposite of isSingleton): true=N, false=1
            for (Expression step : way) {
                if (step instanceof OneOrMoreExp) {
                    newCardinality = new Boolean(true);
                    break;
                }
            }

            // is this a multiple of an already existing parent -> ... -> child path? (i.e. parent1==parent2 && child1==child2)
            if (multiples.containsKey(childexp)) {

                // so we have a parent -> ... -> child multiple. Read the max cardinality of the existing path.
                Boolean existingCardinality = multiples.get(childexp);

                // so we have a parent -> ... -> child multiple. Is there a common CHOICE element on both paths?
                boolean commonChoice = false;

                // find common CHOICE element in both paths BEFORE a ONEOREMORE. Example for pattern match:
                //     <CHOICE>X,<ONEOREMORE>X</ONEOREMORE></CHOICE> -> one has 1, one has N
                //     <CHOICE>X,<SEQUENCE>A,X,B</SEQUENCE></CHOICE> -> both have 1
                // This restriction is needed because MSV does some optimization. (Example for no pattern match):
                //     <CHOICE>empty, X</CHOICE><ONEOREMORE><CHOICE>empty, X</CHOICE></ONEOREMORE>
                // MSV detects that this is two times the same choice and creates just one ChoiceExpression.
                // But this is not what we understand as a common CHOICE -> It's a common element definition
                // <OPTIONAL>X</OPTIONAL> == <CHOICE>empty, X</CHOICE>
                Set<ChoiceExp> choices = new HashSet<ChoiceExp>();
                for (Expression oldStep : paths.get(childexp)) {
                    if (oldStep instanceof ChoiceExp) {
                        choices.add((ChoiceExp) oldStep);
                    }
                    if (oldStep instanceof OneOrMoreExp) {
                        break;
                    }
                }
                for (Expression step : way) {
                    if (step instanceof ChoiceExp) {
                        if (choices.contains((ChoiceExp) step)) {
                            commonChoice = true;
                            break;
                        }
                    }
                    if (step instanceof OneOrMoreExp) {
                        break;
                    }
                }

                // Valid case: Both have N
                if (existingCardinality && newCardinality) {
                    // Do nothing
                }

                // One has 1, the other N
                if (!existingCardinality.equals(newCardinality)) {
                    // A case which we cannot handle. You have a choice between a definition which allows only 1 occurence and another which allows N occurences
                    if (commonChoice) {
                        System.err.println("We have a CHOICE between one definition with N and one with 1 -> What does that mean? WE CANNOT HANDLE THIS)");
                        System.exit(1);
                    }
                    // Valid case: One has 1, the other N, they don't share a common CHOICE -> Set N as the both defs are not exclusive (1 occurence + N occurences)
                    else {
                        multiples.put(childexp, new Boolean(true));
                    }
                }

                if (!existingCardinality && !newCardinality) {
                    // Valid case: Both have 1 and share a common CHOICE element
                    if (commonChoice) {
                        // Do nothing
                    }
                    // A case which we cannot handle. Both have 1:1 but do not share a common CHOICE element: 1:2 ??? ... 1:3 ???
                    else {
                        System.err.println("Already defined as 1, but two times without common choice. What does that mean? WE CANNOT HANDLE THIS!!!");
                        System.exit(1);
                    }
                }
            } else {
                multiples.put(childexp, newCardinality);
                paths.put(childexp, way);
            }
            setParentChildSingleton(childexp, !newCardinality);
        }
    }

    /*
     * Register whether is a child element is a singleton or not
     *
     * @param parent
     * @param child
     * @param singleton (true=singleton, false=can have multiple occurence)
     */
    private void setParentChildSingleton(Expression child, boolean singleton) {
        if (singleton) {
            mSingletonChildren.add(child);
        }
        else {
            mMultipleChildren.add(child);
        }
    }

    /**
     * Returns all singleton child elements
     *
     * @return All child elements which can only occur one time
     */
    public Set<Expression> getSingletons() {
        return mSingletonChildren;
    }

    /**
     * Returns all child elements which are no singletons
     *
     * @return All child elements which can only occur one time
     */
    public Set<Expression> getMultiples() {
        return mMultipleChildren;
    }

    /* Helper method. Build all paths from parent to children. One path is like
     *      ElementExp parent -> [all Expressions but ElementExp or AttributeExp]* -> ElementExp child
     *      ElementExp parent -> [all Expressions but ElementExp or AttributeExp]* -> AttributeExp child
     * Since we use recursion we cannot make sure a path ends with an ElementExp or AttributeExp.
     */
    private static void buildPaths(MSVExpressionVisitorChildren visitor, List<List<Expression>> paths) {
        List<Expression> waytoresearch = paths.get(paths.size() - 1);
        Expression endpoint = waytoresearch.get(waytoresearch.size() - 1);
        List<Expression> children = (List<Expression>) endpoint.visit(visitor);

        if (children.size() == 1) {
            Expression child = children.get(0);
            waytoresearch.add(child);
            if (!(child instanceof ElementExp) && !(child instanceof AttributeExp)) {
                buildPaths(visitor, paths);
            }
        } else if (children.size() > 1) {
            paths.remove(paths.size() - 1);
            for (Expression child : children) {
                List<Expression> newway = new ArrayList<Expression>();
                newway.addAll(waytoresearch);
                newway.add(child);
                paths.add(newway);
                if (!(child instanceof ElementExp) && !(child instanceof AttributeExp)) {
                    buildPaths(visitor, paths);
                }
            }
        }
    }

    private static List<List<Expression>> getPathsToClass(List<List<Expression>> paths, Class clazz) {
        List<List<Expression>> remainingPaths = new ArrayList<List<Expression>>();
        for (List<Expression> path : paths) {
            if (clazz.isInstance(path.get(path.size() - 1))) {
                remainingPaths.add(path);
            }
        }
        return remainingPaths;
    }

    /**
     * Gets all paths leading from this.getExpression() to exp (but not necessarily ending in exp).
     * A path always starts with this.getExpression() and ends in
     * someChildDefinition.getExpression().
     *
     * @param exp The MSV Expression. If you use this.getExpression() you get
     * all paths starting from this.getExpression().
     * If you use someChildDefinition.getExpression() you get all paths from
     * this.getExpression() to the Expression of the Child Definition.
     *
     * @return A List of paths containing exp or null if there are no such paths
     */
    public List<List<Expression>> getPathsContaining(Expression exp) {
        return mContainedInPaths.get(exp);
    }

    /**
     * Can the MSV expression have text content?
     *
     * @return true if the node defined by this can have text content
     */
    public boolean canHaveText() {
        return mCanHaveText;
    }

    /**
     * Determines whether an Element or Attribute child is mandatory.
     *
     * <p>If there are multiples of child (other equally named expressions)
     * providing only one of those Expressions will determine whether exactly
     * this expression is mandatory. In most cases this will return false,
     * and in most cases this is not what you want to know.
     * Therefore you can provide a Collection of (equally named) child expressions.
     * </p>
     *
     * @return whether child is mandatory
     */
    public boolean isMandatory(Collection<Expression> equallyNamedChildren) {

        if (equallyNamedChildren == null || equallyNamedChildren.size() == 0) {
            throw new RuntimeException("ExpressionInformation: Cannot determine isMandatory for a null or empty children list.");
        }

        Set<List<Expression>> twins = new HashSet<List<Expression>>();

        for (Expression exp : equallyNamedChildren) {
            if (!(exp instanceof NameClassAndExpression)) {
                throw new RuntimeException("ExpressionInformation: Cannot determine isMandatory for Expression other than ELEMENT and ATTRIBUTE");
            }
            // Twins == Same parent (first element in path), same child (last element in path)
            // Contract: For all paths containing an Element or Attribute child this child is always the last Expression of the path
            twins.addAll(getPathsContaining(exp));
        }

        /*
         * We assume the subnode is mandatory until proven otherwise. The prove is done by examining CHOICE elements.
         *
         * A CHOICE splits a path in exactly _two_ parts:
         * If the CHOICE is only contained in the current path, the other part does not lead to the subnode. Therefore the subnode is optional.
         * A CHOICE which is shared by two twins (see above) means that CHILD can still be assumed as mandatory.
         *
         * However there's a MSV implementation detail:
         * All twins share the parent element and are identically before some CHOICE splits two of them. Once two paths are split, by logic
         * they cannot share a CHOICE any more. At least you would think so... However MSV does some optimization to merge two
         * identical CHOICES, even if they are in different places. In the following example the two inner CHOICE elements have the same MSV instance:
         *      <CHOICE><CHOICE>A,B</CHOICE><CHOICE>A,B</CHOICE></CHOICE>
         * So both twins do not really share such a CHOICE. You have to look for another path which _really_ shares this choice or - if you find none -
         * set CHILD to optional.
         */

        HashSet<Expression> visitedChoices = new HashSet<Expression>();

        for (List<Expression> path : twins) {
            for (int s=0; s<path.size(); s++) {
                Expression step = path.get(s);
                if (step instanceof ChoiceExp && !visitedChoices.contains(step)) {
                    visitedChoices.add(step);

                    // If other twin paths share the same choice...
                    List<List<Expression>> choiceInPaths = new ArrayList<List<Expression>>(mContainedInPaths.get(step));
                    choiceInPaths.retainAll(twins);

                    // small Performance gain: A CHOICE contained only in one path makes CHILD always optional
                    if (choiceInPaths.size() <= 1) {
                        return false;
                    }

                    // we need to find two paths to CHILD ('path' and another one from the twins) _really_ (s.a.) sharing this choice. Otherwise child is optional.
                    int sharingPaths = 0;

                    for (List<Expression> otherPath : choiceInPaths) {
                        if (otherPath.size() > s && path.subList(0, s+1).equals(otherPath.subList(0, s+1))) {
                            sharingPaths++;
                            if (sharingPaths==2) {
                                break;
                            }
                        }
                    }
                    if (sharingPaths < 2) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
