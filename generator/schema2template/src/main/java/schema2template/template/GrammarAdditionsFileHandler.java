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
package schema2template.template;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Often Process the custom configuration data XML Reads only the grammar-additions.xml Handler for
 * existing grammar-additions.xml
 */
public class GrammarAdditionsFileHandler extends DefaultHandler {

  // work-around for an element name (key) for attributes with default value not having specified a
  // parent element
  private static final String ALL_ELEMENTS = "*";
  private boolean inGrammarAdditions = false;
  private boolean inElements = false;
  private boolean inElement = false;
  private boolean inDatatypes = false;
  private boolean inData = false;
  private boolean inAttributes = false;
  private boolean inAttribute = false;
  private Locator mLocator;

  /**
   * Every XML element that represents the root of a semantic entity (even consisting of multiple
   * XML elements like &lt;table:table&gt; will be annotated by this. The string is the name known
   * by user (semantic).
   */
  private Map<String, String> mComponentRootElementNames;

  private Set<String> mRepetitionAttributeNames;

  private Map<String, String> mElementSuperClassNames;
  private Map<String, String> mElementBaseNames;
  private Map<String, List<String>> mElementStyleFamilies;
  private Set<String> mProcessedElements;
  private Map<String, String[]>
      mDatatypeValueConversion; // Datatype -> {value-type, conversion-classname}
  private Map<String, Map<String, String>>
      mAttributeDefaults; // Attributename -> {elementname or null, defaultValue}
  private Set<String> mProcessedDatatypes;

  public GrammarAdditionsFileHandler(
      Map<String, String> elementBaseNames,
      Map<String, String> elementSuperClassNames,
      Map<String, String> componentRootElementNames,
      Set<String> repetitionAttributeNames,
      Map<String, Map<String, String>> attributeDefaultMap,
      Map<String, List<String>> elementNameToFamilyMap,
      Map<String, String[]> datatypeValueConversion) {
    mElementBaseNames = elementBaseNames;
    mElementSuperClassNames = elementSuperClassNames;
    mComponentRootElementNames = componentRootElementNames;
    mRepetitionAttributeNames = repetitionAttributeNames;
    mAttributeDefaults = attributeDefaultMap;
    mDatatypeValueConversion = datatypeValueConversion;
    mElementStyleFamilies = elementNameToFamilyMap;
    mProcessedElements = new HashSet<String>();
    mProcessedDatatypes = new HashSet<String>();
  }

  private void readElementSettings(Attributes attrs) throws SAXException {
    String nodeName = attrs.getValue("name");
    if (nodeName == null) {
      throw new SAXException("Invalid element line " + mLocator.getLineNumber());
    }
    if (mProcessedElements.contains(nodeName)) {
      throw new SAXException("Multiple definition of element in line " + mLocator.getLineNumber());
    }
    mProcessedElements.add(nodeName);
    String base = attrs.getValue("base");
    if (base != null && base.length() > 0) {
      mElementBaseNames.put(nodeName, base);
    }
    String sc = attrs.getValue("extends");
    if (sc != null && sc.length() > 0) {
      mElementSuperClassNames.put(nodeName, sc);
    }
    String commaSeparatedStyleFamilies = attrs.getValue("family");
    if (commaSeparatedStyleFamilies != null) {
      StringTokenizer tok = new StringTokenizer(commaSeparatedStyleFamilies, ",");
      List<String> families = new ArrayList<String>();
      while (tok.hasMoreElements()) {
        String family = tok.nextToken();
        if (family.length() > 0) {
          families.add(family);
        }
      }
      if (families.size() > 0) {
        mElementStyleFamilies.put(nodeName, families);
      }
    }
    String componentRoot = attrs.getValue("root-of-component");
    if (componentRoot != null && componentRoot.length() > 0) {
      mComponentRootElementNames.put(nodeName, componentRoot);
    }
  }

  private void readDatatypeSettings(Attributes attrs) throws SAXException {
    String attrName = attrs.getValue("name");
    if (attrName == null) {
      throw new SAXException("Invalid datatype line " + mLocator.getLineNumber());
    }
    if (mProcessedDatatypes.contains(attrName)) {
      throw new SAXException("Multiple definition of datatype in line " + mLocator.getLineNumber());
    }
    mProcessedDatatypes.add(attrName);
    String[] tuple = new String[2];
    tuple[0] = attrs.getValue("value-type");
    tuple[1] = attrs.getValue("conversion-type");
    mDatatypeValueConversion.put(attrName, tuple);
  }

  private void readAttributeSettings(Attributes attrs) throws SAXException {
    String attrName = attrs.getValue("name");
    if (attrName == null) {
      throw new SAXException("Invalid attribute line " + mLocator.getLineNumber());
    }

    String elementName = attrs.getValue("element");
    String defaultValue = attrs.getValue("defaultValue");
    Map<String, String> defaultValueByParentElement = mAttributeDefaults.get(attrName);
    if (defaultValueByParentElement == null) {
      defaultValueByParentElement = new HashMap<String, String>();
      mAttributeDefaults.put(attrName, defaultValueByParentElement);
    }
    if (elementName == null) {
      elementName = ALL_ELEMENTS;
    }
    defaultValueByParentElement.put(elementName, defaultValue);

    String repetition = attrs.getValue("repetition");
    if (repetition != null && repetition.length() > 0 && Boolean.parseBoolean(repetition)) {
      mRepetitionAttributeNames.add(attrName);
    }
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes)
      throws SAXException {
    if (qName.equals("grammar-additions") && !inGrammarAdditions) {
      inGrammarAdditions = true;
      return;
    }
    if (qName.equals("elements") && inGrammarAdditions && !inElements) {
      inElements = true;
      return;
    }
    if (qName.equals("element") && inElements && !inElement) {
      inElement = true;
      readElementSettings(attributes);
      return;
    }
    if (qName.equals("attributes") && inGrammarAdditions && !inAttributes) {
      inAttributes = true;
      return;
    }
    if (qName.equals("attribute") && inAttributes && !inAttribute) {
      inAttribute = true;
      readAttributeSettings(attributes);
      return;
    }
    if (qName.equals("data-types") && inGrammarAdditions && !inDatatypes) {
      inDatatypes = true;
      return;
    }
    if (qName.equals("data") && inDatatypes && !inData) {
      inData = true;
      readDatatypeSettings(attributes);
      return;
    }

    throw new SAXException("Malformed grammar-additions.xml in line " + mLocator.getLineNumber());
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (qName.equals("grammar-additions") && inGrammarAdditions) {
      inGrammarAdditions = false;
      return;
    }
    if (qName.equals("elements") && inElements) {
      inElements = false;
      return;
    }
    if (qName.equals("element") && inElement) {
      inElement = false;
      return;
    }
    if (qName.equals("attributes") && inAttributes) {
      inAttributes = false;
      return;
    }
    if (qName.equals("attribute") && inAttribute) {
      inAttribute = false;
      return;
    }
    if (qName.equals("data-types") && inDatatypes) {
      inDatatypes = false;
      return;
    }
    if (qName.equals("data") && inData) {
      inData = false;
      return;
    }

    throw new SAXException("Malformed grammar-additions.xml in line " + mLocator.getLineNumber());
  }

  @Override
  public void setDocumentLocator(Locator locator) {
    mLocator = locator;
  }

  /**
   * Read grammar-additions.xml. Input Convention: Input empty Maps, Maps will be filled.
   *
   * @param cf Config file
   */
  public static void readGrammarAdditionsFile(
      File cf,
      Map<String, String> elementBaseNames,
      Map<String, String> elementSuperClassNamesNames,
      Map<String, String> componentRootElementNames,
      Set<String> repetitionAttributeNames,
      Map<String, Map<String, String>> attributeDefaults,
      Map<String, List<String>> elementNameToFamilyMap,
      Map<String, String[]> datatypeValueConversion)
      throws ParserConfigurationException, SAXException, IOException {

    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
    parser.parse(
        cf,
        new GrammarAdditionsFileHandler(
            elementBaseNames,
            elementSuperClassNamesNames,
            componentRootElementNames,
            repetitionAttributeNames,
            attributeDefaults,
            elementNameToFamilyMap,
            datatypeValueConversion));
  }
}
