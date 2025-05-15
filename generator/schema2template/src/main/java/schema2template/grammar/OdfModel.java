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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import schema2template.template.SourceCodeBaseClass;

/**
 * Model for ODF specific enhancements. Capsulates information from the from the config file. For
 * example, these might be: - style families used for ODF elements - default value attribute
 */
public class OdfModel {

  private final Map<String, List<String>> mNameToFamiliesMap;

  private final Map<String, String> mComponentRootElementNames;

  /** Contains all attributes names that cause a repetition of it's element! */
  private final Set<String> mRepetitionAttributeNames;

  /**
   * The attribute name is the key to another map having the default as value with element parent as
   * key
   */
  private final Map<String, Map<String, String>> mAttributeDefaults;

  private final Map<String, List<String>> mStyleFamilyToPropertiesMap;
  // work-around for an element name (key) for attributes with default value not having specified a
  // parent element
  private final String ALL_ELEMENTS = "*";

  /** the easy user access to the parsed grammar */
  private final XMLModel mXmlModel;

  public OdfModel(
      Map<String, List<String>> nameToFamiliesMap,
      Map<String, String> componentRootElementNames,
      Set<String> repetitionAttributeNames,
      Map<String, Map<String, String>> attributeDefaults,
      XMLModel xmlModel) {
    mNameToFamiliesMap = nameToFamiliesMap;
    mComponentRootElementNames = componentRootElementNames;
    mRepetitionAttributeNames = repetitionAttributeNames;
    mAttributeDefaults = attributeDefaults;
    mXmlModel = xmlModel;
    mStyleFamilyToPropertiesMap =
        new OdfFamilyPropertiesPatternMatcher(xmlModel.getGrammar()).getFamilyProperties();
  }

  /**
   * Determine whether an ELEMENT is stylable (a.k.a. has at least one defined style family). Note:
   * All Definitions sharing the same name share the same style families.
   *
   * @param element stylable element name
   * @return whether there are style families defined for this Definition
   */
  public boolean isStylable(PuzzleComponent element) {
    return mNameToFamiliesMap.containsKey(element.getQName());
  }

  /**
   * Determines whether all subclasses of this JavaBaseClass are stylable or not stylable.
   *
   * @return whether all subclasses are stylable (true) or none (false).
   * @throws RuntimeException if some subclasses are stylable and some are not
   */
  public boolean isStylable(SourceCodeBaseClass base) {
    boolean notStylable = false;
    boolean stylable = false;
    for (PuzzlePiece def : base.getChildElementsOfBaseClass()) {
      if (isStylable(def)) {
        stylable = true;
      } else {
        notStylable = true;
      }
    }
    if (stylable && !notStylable) {
      return true;
    }
    if (notStylable && !stylable) {
      return false;
    }
    throw new RuntimeException(
        "Base Class "
            + base.getQName()
            + " used for stylable AND not stylable elements. This is not possible.");
  }

  /**
   * Get defined style families for this ELEMENT Definition. Note: All Definitions sharing the same
   * name share the same style families.
   *
   * @param element Element
   * @return list of style family names
   */
  public List<String> getStyleFamilies(PuzzleComponent element) {
    List<String> retval = new ArrayList<String>();
    if (mNameToFamiliesMap.containsKey(element.getQName())) {
      retval.addAll(mNameToFamiliesMap.get(element.getQName()));
    }
    return retval;
  }

  /**
   * Get all defined style family names
   *
   * @return SortedSet of Style Family Names
   */
  public SortedSet<String> getStyleFamilies() {
    Iterator<List<String>> iter = mNameToFamiliesMap.values().iterator();
    List<String> families = new ArrayList<String>();
    while (iter.hasNext()) {
      families.addAll(iter.next());
    }
    return new TreeSet<String>(families);
  }

  /**
   * Get default value of ODF attribute, depending on the ODF element which contains this attribute.
   *
   * @param attributeName Attribute's qualified name
   * @param parentElementName Parent element's qualified name
   * @return Default value for attribute of parent
   */
  public String getDefaultAttributeValue(String attributeName, String parentElementName) {
    String defaultValue = null;
    if (parentElementName.equals("table:table-cell") && attributeName.equals("table:protect")) {
      System.err.println("YEAH!");
    }
    if (mAttributeDefaults == null || attributeName == null || attributeName.isBlank()) {
      return null;
    } else {
      Map<String, String> defaultValueByElementParents = mAttributeDefaults.get(attributeName);
      if (defaultValueByElementParents == null) {
        return null;
      }
      defaultValue = defaultValueByElementParents.get(parentElementName);
      if (defaultValue == null) {
        defaultValue = defaultValueByElementParents.get(ALL_ELEMENTS);
      }
    }
    return defaultValue;
  }

  /**
   * Get default values of ODF attribute.
   *
   * @param attributeName Attribute qualified name
   * @return Default values for attribute
   */
  public Set<String> getDefaultAttributeValues(String attributeName) {
    if (mAttributeDefaults == null || attributeName == null || attributeName.isBlank()) {
      return null;
    } else {
      Map<String, String> defaultValueByElementParents = mAttributeDefaults.get(attributeName);
      if (defaultValueByElementParents == null) {
        return null;
      }
      return new TreeSet<>(defaultValueByElementParents.values());
    }
  }

  /**
   * Get the relation between @style:family value and child property elements, which is being
   * extracted with MSV from the grammar: For instance: <style:style style:family="paragraph">
   * <style:paragraph-properties /> <style:text-properties /> </style:style>
   *
   * @return the map showing the realtion between style:family attribute value as key and the
   *     style:*-properties element names as values.
   */
  public Map<String, List<String>> getStyleFamilyPropertiesMap() {
    return mStyleFamilyToPropertiesMap;
  }

  /**
   * The name of all elements that are the beginning of a semantic user component, which is usually
   * added/deleted by user aligned with the common user given name!
   *
   * @param qualifiedElementName the QName of the element
   */
  public boolean isRootOfComponent(String qualifiedElementName) {
    return mComponentRootElementNames.containsKey(qualifiedElementName);
  }

  /**
   * Some attribute represents the number of repetition of its parent element. This method
   * identifies those.
   *
   * @param qualifiedAttributeName the QName of the attribute
   * @return true if the attribute represents the semantic of the repetition of its parent element!
   */
  public boolean isRepetionAttribute(String qualifiedAttributeName) {
    return mRepetitionAttributeNames.contains(qualifiedAttributeName);
  }

  /**
   * Some attribute represents the number of repetition of its parent element. This method returns
   * such an attribute for a given element.
   *
   * @param qualifiedElementName the QName of the element
   * @return the attribute with the repetition function as PuzzlePiece
   */
  public PuzzlePiece getRepetionAttribute(String qualifiedElementName) {
    PuzzlePiece repetitionAttributeName = null;
    for (PuzzlePiece attribute : mXmlModel.getElement(qualifiedElementName).getAttributes()) {
      if (mRepetitionAttributeNames.contains(attribute.getQName())) {
        if (repetitionAttributeName != null) {
          throw new RuntimeException(
              "In element '"
                  + qualifiedElementName
                  + "' should be only one repetition attribute, but there are '"
                  + repetitionAttributeName
                  + "' and '"
                  + attribute.getQName()
                  + "'!");
        }
        repetitionAttributeName = attribute;
      }
    }
    return repetitionAttributeName;
  }
}
