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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import schema2template.SourceCodeBaseClass;
import schema2template.model.*;

/**
 * Model for ODF specific enhancements. Capsulates information from the from the config file. For
 * example, these might be: - style families used for ODF elements - default value attribute
 */
public class OdfModel {

  /** This inner class is not meant for use in templates. */
  public static class AttributeDefaults {
    private Map<String, String> elementDefault = new HashMap<String, String>();

    public void addDefault(String defaultvalue) {
      addDefault(null, defaultvalue);
    }

    public void addDefault(String elementname, String defaultvalue) {
      elementDefault.put(elementname, defaultvalue);
    }

    public String getDefault() {
      return getDefault(null);
    }

    public String getDefault(String elementname) {
      String retval = elementDefault.get(elementname);
      // Fallback: Look for global default
      if (retval == null && elementname != null) {
        retval = elementDefault.get(null);
      }
      return retval;
    }

    public Set<String> getDefaults() {
      Set<String> defaults = new HashSet<String>();
      for (String elementname : elementDefault.keySet()) {
        String retval = elementDefault.get(elementname);
        if (retval != null) {
          defaults.add(retval);
        }
      }
      return defaults;
    }
  }

  final Map<String, List<String>> mNameToFamiliesMap;
  final Map<String, AttributeDefaults> mNameToDefaultsMap;
  final Map<String, List<String>> mStyleFamilyPropertiesMap;

  public OdfModel(
      Map<String, List<String>> nameToFamiliesMap,
      Map<String, AttributeDefaults> attributeDefaults,
      XMLModel xmlModel) {
    mNameToFamiliesMap = nameToFamiliesMap;
    mNameToDefaultsMap = attributeDefaults;
    mStyleFamilyPropertiesMap =
        new OdfFamilyPropertiesPatternMatcher(xmlModel.getGrammar()).getFamilyProperties();
  }

  /**
   * Determine whether an ELEMENT is stylable (a.k.a. has at least one defined style family). Note:
   * All Definitions sharing the same name share the same style families.
   *
   * @param element stylable element name
   * @return whether there are style families defined for this Definition
   */
  public boolean isStylable(QNamed element) {
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
    for (PuzzlePiece def : base.getSubElements()) {
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
  public List<QNamed> getStyleFamilies(QNamed element) {
    List<QNamed> retval = new ArrayList<QNamed>();
    for (String family : mNameToFamiliesMap.get(element.getQName())) {
      retval.add(new QNameValue(family));
    }
    return retval;
  }

  /**
   * Get all defined style family names
   *
   * @return SortedSet of Style Family Names
   */
  public SortedSet<QNamed> getStyleFamilies() {
    Iterator<List<String>> iter = mNameToFamiliesMap.values().iterator();
    List<QNamed> families = new ArrayList<QNamed>();
    while (iter.hasNext()) {
      for (String family : iter.next()) {
        families.add(new QNameValue(family));
      }
    }
    return new TreeSet<QNamed>(families);
  }

  /**
   * Get default value of ODF attribute, depending on the ODF element which contains this attribute.
   *
   * @param attribute Attribute
   * @param parentelement Parent element
   * @return Default value for attribute of parent
   */
  public String getDefaultAttributeValue(QNamed attribute, QNamed parentelement) {
    AttributeDefaults defaults = mNameToDefaultsMap.get(attribute.getQName());
    if (defaults == null) {
      return null;
    }
    return defaults.getDefault(parentelement.getQName());
  }

  /**
   * Get default values of ODF attribute.
   *
   * @param attribute Attribute
   * @return Default values for attribute
   */
  public Set<String> getDefaultAttributeValues(QNamed attribute) {
    if (mNameToDefaultsMap == null || attribute == null) {
      return null;
    } else {
      AttributeDefaults defaults = mNameToDefaultsMap.get(attribute.getQName());
      if (defaults == null) {
        return null;
      } else {
        return defaults.getDefaults();
      }
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
    return mStyleFamilyPropertiesMap;
  }
}
