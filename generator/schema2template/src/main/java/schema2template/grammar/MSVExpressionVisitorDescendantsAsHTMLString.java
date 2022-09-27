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
package schema2template.grammar;

import com.sun.msv.datatype.SerializationContext;
import com.sun.msv.datatype.xsd.*;
import com.sun.msv.grammar.*;
import com.sun.msv.grammar.util.ExpressionWalker;
import java.util.*;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.ValidationContext;

/**
 * This visitor visits an Expression and returns an HTML String with its children (or descendants)
 * relationship as brief pseudo reg-ex for documentation purpose.
 *
 * <p>Usage example: <em>(String) myExpression.visit(myMSVExpressionVisitorDescendantsAsHTMLString,
 * XMLModel.getHeadsOfIslands(grammar), 1)</em>
 *
 * <p>The HeadOfIslands are necessary to avoid endless loops, where islands are parts of the grammar
 * that are being reused/referenced multiple times. The final parameter express how many child
 * element level can be found before stopping. 1 will stop right after writing the name of the first
 * encountered child element.
 *
 * <p>Please note that you do not use any method of this class directly!
 */
public class MSVExpressionVisitorDescendantsAsHTMLString extends ExpressionWalker {

  // ExpressionWalker class traverses expressions in depth-first order.
  // So this invocation traverses the all reachable expressions from
  // the top level expression.
  public MSVExpressionVisitorDescendantsAsHTMLString(
      StringBuilder builder, Set<Expression> headsOfIslands) {
    this(builder, headsOfIslands, 1);
  }

  public MSVExpressionVisitorDescendantsAsHTMLString(
      StringBuilder builder, Set<Expression> headsOfIslands, int maxElementDepth) {
    this.builder = builder;
    this.maxElementDepth = maxElementDepth;
    // an island is a sub(tree) of the grammar being reused and should be written only once to avoid
    // endless-loops
    this.headsOfIslands = headsOfIslands;
  }

  StringBuilder builder = null;
  int maxElementDepth = 1;
  int currentElementDepth = 0;
  final Set<Expression> headsOfIslands;
  final Set<Expression> islandHeadPassed = new HashSet<Expression>();

  boolean isWithinAttributeValue = false;

  public void onElement(ElementExp exp) {
    String elementName = evaluateNameClass(exp.getNameClass());
    builder.append("&lt;" + elementName + " ");
    // prevent infinite loops by marking earlier multiple used/referenced expression (being
    // elements/references) as "head of islands"
    boolean isIslandHead = headsOfIslands.contains(exp);
    if ((!isIslandHead || (isIslandHead && !islandHeadPassed.contains(exp)))
        && (maxElementDepth > currentElementDepth)) {
      currentElementDepth++;
      if (isIslandHead) {
        // making sure that an island is only entered once
        islandHeadPassed.add(exp);
      }
      visitUnary(exp.contentModel);
    } else {
      builder.append(" ... ");
    }
    builder.append("&gt;");
  }

  public void onEpsilon() {
    builder.append("EMPTY");
  }

  public void onNullSet() {
    builder.append("\nEXPR_notAllowed");
  }

  public void onAnyString() {
    builder.append("TEXT");
  }

  public void onInterleave(InterleaveExp exp) {
    visitBinExp("interleave", exp, InterleaveExp.class);
  }

  public void onConcur(ConcurExp exp) {
    throw new IllegalArgumentException("the grammar includes concur, which is not supported");
  }

  public void onList(ListExp exp) {
    builder.append("\nSTART_list");
    visitUnary(exp.exp);
    builder.append("\nEND_list");
  }

  protected void onOptional(Expression exp) {
    if (exp instanceof OneOrMoreExp) {
      // (X+)? == X*
      onZeroOrMore((OneOrMoreExp) exp);
      return;
    }
    if (isWithinAttributeValue) {
      builder.append("(");
    } else {
      builder.append(" (");
    }
    visitUnary(exp);
    // context/state driven layout
    if (isWithinAttributeValue) {
      builder.append(")?");
    } else {
      builder.append(")?\n\t");
    }
  }

  public void onChoice(ChoiceExp exp) {
    // use optional instead of <choice> p <empty/> </choice>
    if (exp.exp1 == Expression.epsilon) {
      onOptional(exp.exp2);
      return;
    }
    if (exp.exp2 == Expression.epsilon) {
      onOptional(exp.exp1);
      return;
    }

    Expression[] children = exp.getChildren();
    for (int i = 0; i < children.length; i++) {
      children[i].visit(this);
      if ((i + 1) < children.length) {
        builder.append(" | ");
      }
    }
  }

  public void onSequence(SequenceExp exp) {
    builder.append("\n\t(");
    visitBinExp("group", exp, SequenceExp.class);
    builder.append(")");
  }

  public void visitBinExp(String elementName, BinaryExp exp, Class<?> type) {
    // since AGM is binarized,
    // <choice> a b c </choice> is represented as
    // <choice> a <choice> b c </choice></choice>
    // this method print them as <choice> a b c </choice>
    // builder.append("\nSTART: " + elementName);
    Expression[] children = exp.getChildren();
    for (int i = 0; i < children.length; i++) children[i].visit(this);
    // builder.append("\nEND: " + elementName);
  }

  public void onMixed(MixedExp exp) {
    builder.append("<MIXED>");
    visitUnary(exp.exp);
    builder.append("</MIXED>");
  }

  public void onOneOrMore(OneOrMoreExp exp) {
    builder.append("(");
    visitUnary(exp.exp);
    builder.append(")+ ");
  }

  protected void onZeroOrMore(OneOrMoreExp exp) {
    // note that this method is not a member of TREXPatternVisitor.
    builder.append("(");
    visitUnary(exp.exp);
    builder.append(")* ");
  }

  public void onAttribute(AttributeExp exp) {
    builder.append(evaluateNameClass(exp.nameClass) + "=\"");
    isWithinAttributeValue = true;
    visitUnary(exp.exp);
    builder.append("\" ");
    isWithinAttributeValue = false;
  }

  /** print expression but suppress unnecessary sequence. */
  public void visitUnary(Expression exp) {
    // TREX treats <zeroOrMore> p q </zeroOrMore>
    // as <zeroOrMore><group> p q </group></zeroOrMore>
    // This method tries to exploit this property to
    // simplify the result.
    if (exp instanceof SequenceExp) {
      SequenceExp seq = (SequenceExp) exp;
      visitUnary(seq.exp1);
      seq.exp2.visit(this);
    } else {
      exp.visit(this);
    }
  }

  public void onValue(ValueExp exp) {
    if (exp.dt instanceof XSDatatypeImpl) {
      XSDatatypeImpl base = (XSDatatypeImpl) exp.dt;

      final List<String> ns = new ArrayList<String>();

      String lex =
          base.convertToLexicalValue(
              exp.value,
              new SerializationContext() {
                public String getNamespacePrefix(String namespaceURI) {
                  int cnt = ns.size() / 2;
                  ns.add("xmlns:ns" + cnt);
                  ns.add(namespaceURI);
                  return "ns" + cnt;
                }
              });

      if (base != TokenType.theInstance) {
        // if the type is token, we don't need @type.
        // builder.append("\ntype: " + base.getName());
        builder.append(base.getName());
      }
      builder.append(lex);
      // builder.append("\nEND_value");
      return;
    }

    throw new UnsupportedOperationException(exp.dt.getClass().getName());
  }

  public void onData(DataExp exp) {
    Datatype dt = exp.dt;

    if (dt instanceof XSDatatypeImpl) {
      XSDatatypeImpl dti = (XSDatatypeImpl) dt;
      if (dti.getName() != null
          && (isPrimitiveDatatypeType(dti.getName()) || isDerivedDataType(dti.getName()))) {
        builder.append(
            "<a href=\"https://www.w3.org/TR/xmlschema-2/#"
                + dti.getName()
                + "\">&lt;"
                + dti.getName()
                + "&gt;</a>");
      } else if (exp.name.equals("anyIRI")) {
        builder.append("<a href=\"https://www.rfc-editor.org/rfc/rfc3987\">&lt;anyIRI\"&gt;</a>");
      } else if (isPredefinedType(dt)) {
        // it's a MSV pre-defined type.
        builder.append("<!" + dti.getName() + "!>");
      } else {
        serializeDataType(dti);
      }
      return;
    }

    // unknown datatype
    builder.append("\nEXPR_data-unknown: " + "class " + dt.getClass().getName());
  }

  public void onOther(OtherExp exp) {
    exp.exp.visit(this); // ignore otherexp
  }

  public void onRef(ReferenceExp exp) {
    // prevent infinite loops by marking earlier multiple used/referenced expression (being
    // elements/references) as "head of islands"
    boolean isIslandHead = headsOfIslands.contains(exp);
    if (!isIslandHead || (isIslandHead && !islandHeadPassed.contains(exp))) {
      if (isIslandHead) {
        // making sure that an island is only entered once
        islandHeadPassed.add(exp);
      }
      // this expression will not be written as a named pattern.
      if (isPrimitiveDatatypeType(exp.name) || isDerivedDataType(exp.name)) {
        builder.append(
            "<a href=\"https://www.w3.org/TR/xmlschema-2/#"
                + exp.name
                + "\">&lt;"
                + exp.name
                + "&gt;</a>");
      } else if (exp.name.equals("anyIRI")) {
        builder.append("<a href=\"https://www.rfc-editor.org/rfc/rfc3987\">&lt;anyIRI&gt;</a>");
      } else {
        exp.exp.visit(this);
      }
      if (isIslandHead) {
        // if the ref was left, we can re-enter it
        islandHeadPassed.remove(exp);
      }
    } else {
      if (isPrimitiveDatatypeType(exp.name) || isDerivedDataType(exp.name)) {
        builder.append(
            "<a href=\"https://www.w3.org/TR/xmlschema-2/#"
                + exp.name
                + "\">&lt;"
                + exp.name
                + "&gt;</a>");
      } else if (exp.name.equals("anyIRI")) {
        builder.append("<a href=\"https://www.rfc-editor.org/rfc/rfc3987\">&lt;anyIRI&gt;</a>");
      } else {
        builder.append("&lt;xsd:ref name=\"" + exp.name + "\"/&gt;");
      }
    }
  }

  /** if XML Schema primitive datatypes https://www.w3.org/TR/xmlschema-2/#built-in-datatypes */
  private static boolean isPrimitiveDatatypeType(String s) {
    return (s.equals("string")
        || s.equals("boolean")
        || s.equals("decimal")
        || s.equals("float")
        || s.equals("double")
        || s.equals("duration")
        || s.equals("dateTime")
        || s.equals("time")
        || s.equals("date")
        || s.equals("gYearMonth")
        || s.equals("gYear")
        || s.equals("gMonthDay")
        || s.equals("gDay")
        || s.equals("gMonth")
        || s.equals("hexBinary")
        || s.equals("base64Binary")
        || s.equals("anyURI")
        || s.equals("QName")
        || s.equals("NOTATION"));
  }

  /** if XML Schema primitive datatypes https://www.w3.org/TR/xmlschema-2/#built-in-datatypes */
  private static boolean isDerivedDataType(String s) {
    return s.equals("normalizedString")
        || s.equals("token")
        || s.equals("language")
        || s.equals("NMTOKEN")
        || s.equals("NMTOKENS")
        || s.equals("Name")
        || s.equals("NCName")
        || s.equals("ID")
        || s.equals("IDREF")
        || s.equals("IDREFS")
        || s.equals("ENTITY")
        || s.equals("ENTITIES")
        || s.equals("integer")
        || s.equals("nonPositiveInteger")
        || s.equals("negativeInteger")
        || s.equals("long")
        || s.equals("int")
        || s.equals("short")
        || s.equals("byte")
        || s.equals("nonNegativeInteger")
        || s.equals("unsignedLong")
        || s.equals("unsignedInt")
        || s.equals("unsignedShort")
        || s.equals("unsignedByte")
        || s.equals("positiveInteger");
  }

  private static String evaluateNameClass(NameClass nc) {
    String elementName = "";
    if ((nc instanceof SimpleNameClass)
        || (nc instanceof AnyNameClass)
        || (nc instanceof ChoiceNameClass)) {
      String elementPrefix = null;
      if ((nc instanceof SimpleNameClass)) {
        elementName = ((SimpleNameClass) nc).localName;
        // the feature below works only with the namespace-prefix2 branch on MSV
        elementPrefix = ((SimpleNameClass) nc).prefix;
        if (elementPrefix != null && !elementPrefix.isEmpty()) {
          elementName = elementPrefix.concat(":").concat(elementName);
        }
      } else if (nc instanceof AnyNameClass) {
        elementName = "*:*";
      } else if (nc instanceof ChoiceNameClass) {
        elementName = "CHOICE_NAME_CLASS";
      }
      if (elementName.equals("ExtensionContent")) {
        elementName = "ExtensionContent";
      }
    }
    return elementName;
  }

  /**
   * serializes the given datatype.
   *
   * <p>The caller should generate events for &lt;simpleType&gt; element if necessary.
   */
  protected void serializeDataType(XSDatatype dt) {

    if (dt instanceof UnionType) {
      serializeUnionType((UnionType) dt);
      return;
    }

    // store names of the applied facets into this set
    Set<String> appliedFacets = new HashSet<String>();

    // store effective facets (those which are not shadowed by another facet).
    Vector<XSDatatype> effectiveFacets = new Vector<XSDatatype>();

    XSDatatype x = dt;
    while (x instanceof DataTypeWithFacet || x instanceof FinalComponent) {

      if (x instanceof FinalComponent) {
        // skip FinalComponent
        x = x.getBaseType();
        continue;
      }

      String facetName = ((DataTypeWithFacet) x).facetName;

      if (facetName.equals(XSDatatypeImpl.FACET_ENUMERATION)) {
        // if it contains enumeration, then we will serialize this
        // by using <value>s.
        serializeEnumeration((XSDatatypeImpl) dt, (EnumerationFacet) x);
        return;
      }

      if (facetName.equals(XSDatatypeImpl.FACET_WHITESPACE)) {
        System.err.println("warning: unsupported whiteSpace facet is ignored");
        x = x.getBaseType();
        continue;
      }

      // find the same facet twice.
      // pattern is allowed more than once.
      if (!appliedFacets.contains(facetName)
          || appliedFacets.equals(XSDatatypeImpl.FACET_PATTERN)) {

        appliedFacets.add(facetName);
        effectiveFacets.add(x);
      }

      x = ((DataTypeWithFacet) x).baseType;
    }

    if (x instanceof ListType) {
      // the base type is list.
      serializeListType((XSDatatypeImpl) dt);
      return;
    }

    // it cannot be the union type. Union type cannot be derived by
    // restriction.

    // so this must be one of the pre-defined types.
    if (!(x instanceof ConcreteType)) throw new Error(x.getClass().getName());

    if (x instanceof com.sun.msv.grammar.relax.EmptyStringType) {
      // empty string
      builder.append("\"\"");
      return;
    }
    if (x instanceof com.sun.msv.grammar.relax.NoneType) {
      // "none" is equal to <notAllowed/>
      builder.append("notAllowed");
      return;
    }

    builder.append(x.getName());

    // serialize effective facets
    for (int i = effectiveFacets.size() - 1; i >= 0; i--) {
      DataTypeWithFacet dtf = (DataTypeWithFacet) effectiveFacets.get(i);

      if (dtf instanceof LengthFacet) {
        param("length", Long.toString(((LengthFacet) dtf).length));
      } else if (dtf instanceof MinLengthFacet) {
        param("minLength", Long.toString(((MinLengthFacet) dtf).minLength));
      } else if (dtf instanceof MaxLengthFacet) {
        param("maxLength", Long.toString(((MaxLengthFacet) dtf).maxLength));
      } else if (dtf instanceof PatternFacet) {
        String pattern = "";
        PatternFacet pf = (PatternFacet) dtf;
        for (int j = 0; j < pf.getRegExps().length; j++) {
          if (pattern.length() != 0) pattern += "|";
          pattern += pf.patterns[j];
        }
        param("pattern", pattern);
      } else if (dtf instanceof TotalDigitsFacet) {
        param("totalDigits", Long.toString(((TotalDigitsFacet) dtf).precision));
      } else if (dtf instanceof FractionDigitsFacet) {
        param("fractionDigits", Long.toString(((FractionDigitsFacet) dtf).scale));
      } else if (dtf instanceof RangeFacet) {
        param(dtf.facetName, dtf.convertToLexicalValue(((RangeFacet) dtf).limitValue, null));
        // we don't need to pass SerializationContext because it is only
        // for QName.
      } else if (dtf instanceof WhiteSpaceFacet) {; // do nothing.
      } else
        // undefined facet type
        throw new Error();
    }

    builder.append("]");
  }

  protected void param(String name, String value) {
    /*2DO
    writer.start("param", new String[] { "name", name });
    writer.characters(value);
    writer.end("param");

     */
  }

  /** returns true if the specified type is a pre-defined XSD type without any facet. */
  protected boolean isPredefinedType(Datatype x) {
    return !(x instanceof DataTypeWithFacet
        || x instanceof UnionType
        || x instanceof ListType
        || x instanceof FinalComponent
        || x instanceof com.sun.msv.grammar.relax.EmptyStringType
        || x instanceof com.sun.msv.grammar.relax.NoneType);
  }

  /** serializes a union type. this method is called by serializeDataType method. */
  protected void serializeUnionType(UnionType dt) {
    // serialize member types.
    for (int i = 0; i < dt.memberTypes.length; i++) serializeDataType(dt.memberTypes[i]);
  }

  /** serializes a list type. this method is called by serializeDataType method. */
  protected void serializeListType(XSDatatypeImpl dt) {

    ListType base = (ListType) dt.getConcreteType();

    if (dt.getFacetObject(XSDatatype.FACET_LENGTH) != null) {
      // with the length facet.
      int len = ((LengthFacet) dt.getFacetObject(XSDatatype.FACET_LENGTH)).length;
      builder.append("<rng:list>");
      for (int i = 0; i < len; i++) serializeDataType(base.itemType);
      builder.append("</rng:list>");

      return;
    }

    if (dt.getFacetObject(XSDatatype.FACET_MAXLENGTH) != null)
      throw new UnsupportedOperationException(
          "warning: maxLength facet to list type is not properly converted.");

    MinLengthFacet minLength = (MinLengthFacet) dt.getFacetObject(XSDatatype.FACET_MINLENGTH);

    builder.append("<list>");
    if (minLength != null) {
      // list n times
      for (int i = 0; i < minLength.minLength; i++) serializeDataType(base.itemType);
    }
    builder.append("<zeroOrMore>");
    serializeDataType(base.itemType);
    builder.append("</zeroOrMore>");
    builder.append("</list>");
  }

  /** serializes a type with enumeration. this method is called by serializeDataType method. */
  protected void serializeEnumeration(XSDatatypeImpl dt, EnumerationFacet enums) {

    Object[] values = enums.values.toArray();

    if (values.length > 1) builder.append("rng:choice");

    for (int i = 0; i < values.length; i++) {
      final Vector<String> ns = new Vector<String>();

      String lex =
          dt.convertToLexicalValue(
              values[i],
              new SerializationContext() {
                public String getNamespacePrefix(String namespaceURI) {
                  int cnt = ns.size() / 2;
                  ns.add("xmlns:ns" + cnt);
                  ns.add(namespaceURI);
                  return "ns" + cnt;
                }
              });

      // make sure that the converted lexical value is allowed by this type.
      // sometimes, facets that are added later rejects some of
      // enumeration values.

      boolean allowed =
          dt.isValid(
              lex,
              new ValidationContext() {

                public String resolveNamespacePrefix(String prefix) {
                  if (!prefix.startsWith("ns")) return null;
                  int i = Integer.parseInt(prefix.substring(2));
                  return (String) ns.get(i * 2 + 1);
                }

                public boolean isUnparsedEntity(String name) {
                  return true;
                }

                public boolean isNotation(String name) {
                  return true;
                }

                public String getBaseUri() {
                  return null;
                }
              });

      ns.add("type");
      ns.add(dt.getConcreteType().getName());

      if (allowed) {
        builder.append("<rng:value type=" + dt.getConcreteType().getName() + "\">");
        builder.append(lex);
        builder.append("</rng:value>");
      }
    }

    if (values.length > 1) builder.append("</choice>");
  }
}
