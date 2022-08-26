/*
 * Copyright 2021 The Document Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package schema2template.example.odf;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.ValueExp;
import com.sun.msv.grammar.util.ExpressionWalker;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import schema2template.model.PuzzlePiece;

/**
 * This class searches for a certain Pattern in the ODF grammar and returns the results.
 *
 * <p>The pattern: A @style:family attribute has one ore more style:properties elements. For
 * instance, style:family="table-cell" has three elements style:text-properties*
 * style:paragraph-properties style:table-cell-properties
 *
 * <p>A result is Map<String, List<String>> to be accessed via getFamilyProperties(). In the
 * regression test OdfFamilyPropertiesPatternMatcherTest the map is serialized to proof the
 * functionality! "@style:family = 'table-cell' = style:table-cell-properties
 * style:paragraph-properties style:text-properties"
 *
 * <p>Interesting side-fact: All three can have a different @fo:background-color overwriting each
 * other!
 */
public class OdfFamilyPropertiesPatternMatcher {

  private final Grammar mGrammar;

  // collect all reachable ElementExps and ReferenceExps.
  final Set<Expression> elementNodes = new HashSet<>();
  final Set<Expression> refNodes = new HashSet<>();
  // ElementExps and ReferenceExps who are referenced more than once.
  final Set<Expression> elementHeads = new HashSet<>();
  final Set<Expression> refHeads = new HashSet<>();

  // MATCH PATTERN PROPERTIES
  private Map<String, List<String>> propertiesByFamily = null;
  Boolean collectingState = Boolean.FALSE;
  Integer depthCollecting = null;

  public OdfFamilyPropertiesPatternMatcher(Grammar g) {
    mGrammar = g;
  }

  /**
   * @return Map key is @style:family attribute the Map value is a list of all style:X-properties
   *     elements related to this family
   * @see OdfFamilyPropertiesPatternMatcherTest for expected ODF 1.3 output!
   */
  public Map<String, List<String>> getFamilyProperties() {
    if (propertiesByFamily == null) {
      initialize();
    }
    return propertiesByFamily;
  }

  private void initialize() {
    propertiesByFamily = new TreeMap<>();

    mGrammar
        .getTopLevel()
        .visit(
            new ExpressionWalker() {
              // ExpressionWalker class traverses expressions in depth-first order.
              // So this invokation traverses the all reachable expressions from
              // the top level expression.

              // Whenever visiting elements and RefExps, they are memorized
              // to identify head of islands.
              int depth = 1;
              List result = null;

              @Override
              public void onElement(ElementExp exp) {
                if (elementNodes.contains(exp)) {
                  elementHeads.add(exp);
                  evaluatePattern(exp);
                  return; // prevent infinite recursion.
                } else {
                  evaluatePattern(exp);
                  elementNodes.add(exp);
                  depth++;
                  super.onElement(exp);
                  depth--;
                }
              }

              @Override
              public void onAttribute(AttributeExp exp) {
                String attrName = PuzzlePiece.getName(exp);
                // System.out.println("AttributeName: " + attrName);
                if (attrName.equals("style:family")) {
                  // System.out.println("NEW FAMILY" + asString(propertiesByFamily));
                  collectingState = Boolean.TRUE;
                  depthCollecting = depth;
                  result = new ArrayList();
                  if (exp.exp instanceof ValueExp) {
                    // System.out.println("style:family-1" + ((ValueExp) exp.exp).value.toString());
                    propertiesByFamily.put(((ValueExp) exp.exp).value.toString(), result);
                  } else if (exp.exp instanceof ChoiceExp) {
                    // System.out.println("style:family-A" + ((ValueExp) ((ChoiceExp)
                    // exp.exp).exp1).value.toString());
                    // System.out.println("style:family-B" + ((ValueExp) ((ChoiceExp)
                    // exp.exp).exp2).value.toString());
                    propertiesByFamily.put(
                        ((ValueExp) ((ChoiceExp) exp.exp).exp1).value.toString(), result);
                    propertiesByFamily.put(
                        ((ValueExp) ((ChoiceExp) exp.exp).exp2).value.toString(), result);

                  } else {
                    // System.out.println("NOT FAMILY '" + attrName + "'");
                    /*
                    <rng:attribute name="style:family">
                      <rng:choice>
                        <rng:value>graphic</rng:value>
                        <rng:value>presentation</rng:value>
                      </rng:choice>
                    </rng:attribute>
                     */
                  }
                }
                super.onAttribute(exp);
              }

              @Override
              public void onRef(ReferenceExp exp) {
                // String refName = ((ReferenceExp) exp).name;
                // System.out.println("REF NAME" + refName);
                // we will allow two times nested refs, but than we break
                if (refNodes.contains(exp)) {
                  if (refHeads.contains(exp)) {
                    return; // prevent infinite recursion.
                  } else {
                    // allow the reference to be parsed once again to find our pattern
                    refHeads.add(exp);
                  }
                }
                elementNodes.add(exp);
                super.onRef(exp);
                // first only remove the head
                if (refHeads.contains(exp)) {
                  refHeads.remove(exp);
                } else if (refNodes.contains(exp)) {
                  // second time remove the head
                  refNodes.remove(exp);
                }
              }

              public void endCollectingState() {
                collectingState = Boolean.FALSE;
              }

              private void evaluatePattern(ElementExp exp) {
                // System.out.println("depth:" + depth);
                if (exp.getNameClass() instanceof SimpleNameClass) {
                  String elementName = ((SimpleNameClass) exp.getNameClass()).localName;
                  // System.out.println("ELEMENT SimpleNameClass.localName: " + elementName);
                  // //SvanteDebug
                  if (elementName != null
                      && collectingState
                      && elementName.endsWith("properties")) {
                    // 2DO: as long MSV does not preserve (default) namespace prefix, we skip for
                    // now prefix
                    result.add(elementName);
                  } else {
                    if (collectingState) {
                      if (depthCollecting >= depth) {
                        endCollectingState();
                      } else {
                        // System.out.println("+++ found deeper element" + elementName);
                      }
                    }
                  }
                } else {
                  // System.out.println("ELEMENT NameClass: " + exp.getNameClass().toString());
                  // //SvanteDebug
                  if (collectingState) {
                    endCollectingState();
                  }
                }
              }
            });
  }

  /**
   * A result is the serialized map of a style:family and its property elements, received by
   * getFamilyProperties() The string looks like the following, there is a regression test poofing
   * this functionality! "@style:family = 'table-cell' = style:table-cell-properties
   * style:paragraph-properties style:text-properties"
   */
  @Override
  public String toString() {
    Map<String, List<String>> results = getFamilyProperties();
    Set<String> families = results.keySet();
    StringBuilder sb = new StringBuilder();
    for (String family : families) {
      sb.append("@style:family = '").append(family).append("' =");
      List<String> propNames = results.get(family);
      for (String propName : propNames) {
        sb.append(" style:").append(propName);
      }
      sb.append("\n");
    }
    return sb.toString();
  }
}
