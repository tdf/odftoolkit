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
 * <p>*********************************************************************
 */
package schema2template;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import schema2template.model.PuzzleComponent;
import schema2template.model.PuzzlePiece;
import schema2template.model.QNamed;
import schema2template.model.QNamedPuzzleComponent;
import schema2template.model.XMLModel;

/**
 * Model for Java specific enhancements like common base classes for elements and Java valuetypes
 * for valuetypes used in schema. Encapsulates information from the grammar-additions.xml file.
 */
public class SourceCodeModel {

  Map<String, SourceCodeBaseClass> mElementBaseMap;
  SortedSet<SourceCodeBaseClass> mBaseClasses;
  Map<String, SourceCodeBaseClass> mBaseNameToBaseClass;
  Map<String, String[]>
      mDataTypeValueAndConversionMap; // datatype -> {value-type, conversion-classname}

  /**
   * Construct SourceCodeModel. Not meant for template usage.
   *
   * @param schemaModel he XMLModel (grammar model)
   * @param elementNameBaseNameMap the mapping from element names to source code base class names
   * @param datatypeValueAndConversionMap the mapping from schema datatype to {source code types,
   *     name of conversion class}
   */
  public SourceCodeModel(
      XMLModel schemaModel,
      Map<String, String> elementNameBaseNameMap,
      Map<String, String[]> datatypeValueAndConversionMap) {
    mDataTypeValueAndConversionMap = datatypeValueAndConversionMap;

    // Intermediate Step -> get all baseNames
    SortedSet<String> baseNames = new TreeSet<String>(elementNameBaseNameMap.values());

    // Intermediate Step -> get all subElement Definitions for each baseName
    Map<String, SortedSet<PuzzlePiece>> baseNameElementsMap =
        new HashMap<String, SortedSet<PuzzlePiece>>(baseNames.size());
    for (String elementName : elementNameBaseNameMap.keySet()) {
      String baseName = elementNameBaseNameMap.get(elementName);
      SortedSet<PuzzlePiece> elements = baseNameElementsMap.get(baseName);
      if (elements == null) {
        elements = new TreeSet<PuzzlePiece>();
        baseNameElementsMap.put(baseName, elements);
      }
      QNamedPuzzleComponent subElement = schemaModel.getElement(elementName);
      if (subElement != null) {
        if (subElement instanceof Collection) {
          elements.addAll((Collection) subElement);
        } else {
          elements.add((PuzzlePiece) subElement);
        }
      } else {
        System.err.println("Warning: BaseClass definition for unknown element " + elementName);
      }
    }

    // Generate all baseclasses (additional intermediate step: register them)
    mBaseNameToBaseClass = new HashMap<String, SourceCodeBaseClass>(baseNames.size());
    mBaseClasses = new TreeSet<SourceCodeBaseClass>();
    for (String baseName : baseNames) {
      SourceCodeBaseClass javabaseclass =
          new SourceCodeBaseClass(baseName, baseNameElementsMap.get(baseName));
      mBaseClasses.add(javabaseclass);
      mBaseNameToBaseClass.put(baseName, javabaseclass);
    }

    // Generate a map from element tag name to base classes
    mElementBaseMap = new HashMap<String, SourceCodeBaseClass>(elementNameBaseNameMap.size());
    for (String elementName : elementNameBaseNameMap.keySet()) {
      String baseName = elementNameBaseNameMap.get(elementName);
      SourceCodeBaseClass baseclass = mBaseNameToBaseClass.get(baseName);
      mElementBaseMap.put(elementName, baseclass);
    }
  }

  /**
   * Use in templates: Get base class of one element
   *
   * @param subElement element
   * @return baseclass
   */
  public SourceCodeBaseClass getBaseClassOf(QNamed subElement) {
    SourceCodeBaseClass c = null;
    if (subElement != null) {
      if (mElementBaseMap.containsKey(subElement.getQName())) {
        c = mElementBaseMap.get(subElement.getQName());
      }
    }
    return c;
  }

  /**
   * Use in templates: Get all baseclasses
   *
   * @return all baseclasses
   */
  public SortedSet<SourceCodeBaseClass> getBaseClasses() {
    return mBaseClasses;
  }

  /**
   * Use in templates: Get baseclass by name
   *
   * @param baseName name of baseclass
   * @return baseclass object
   */
  public SourceCodeBaseClass getBaseClass(String baseName) {
    return mBaseNameToBaseClass.get(baseName);
  }

  /**
   * Use in templates: Get baseclass by name
   *
   * @param base name of baseclass
   * @return baseclass object
   */
  public SourceCodeBaseClass getBaseClass(QNamed base) {
    return getBaseClass(base.getQName());
  }

  /**
   * Use in templates: Get Source code value type for datatype used in schema
   *
   * @param datatype
   * @return source code value type
   */
  public String getValuetype(QNamed datatype) {
    String datatypename = datatype.getQName();
    String[] tuple = mDataTypeValueAndConversionMap.get(datatypename);
    if (tuple == null) {
      return "";
    }
    String retval = tuple[0];
    return (retval == null) ? "" : retval;
  }

  /**
   * Use in templates: Get source code value types for datatypes used in schema
   *
   * @param datatypes Schema datatypes
   * @return the corresponding source code datatypes
   */
  public SortedSet<String> getValuetypes(PuzzleComponent datatypes) {
    SortedSet<String> retval = null;
    if (datatypes != null) {
      retval = new TreeSet<String>();
      for (PuzzlePiece datatype : datatypes.getCollection()) {
        String datatypename = datatype.getQName();
        String[] tuple = mDataTypeValueAndConversionMap.get(datatypename);
        if (tuple != null) {
          String valuetype = tuple[0];
          if (valuetype != null) {
            retval.add(valuetype);
          }
        }
      }
      return retval;
    } else {
      return retval;
    }
  }

  /**
   * Use in templates: Translate Java object to simple Java datatype
   *
   * @param objectType like "Boolean"
   * @return simpleType like "boolean"
   */
  public String getPrimitiveType(String objectType) {
    if (objectType.equals("Boolean")) {
      return "boolean";
    }
    if (objectType.equals("Integer")) {
      return "int";
    }
    if (objectType.equals("Double")) {
      return "double";
    }
    return null;
  }

  /**
   * Use in templates: Get Java conversion class for datatype used in schema.
   *
   * @param datatype Source code datatype
   * @return name of source code conversion class for this datatype
   */
  public String getConversiontype(QNamed datatype) {
    String datatypename = datatype.getQName();
    String[] tuple = mDataTypeValueAndConversionMap.get(datatypename);
    if (tuple == null) {
      return "";
    }
    String retval = tuple[1];
    return (retval == null) ? "" : retval;
  }

  /**
   * Use in templates: Get Java conversion class for datatype used in schema.
   *
   * @param datatypename Source code datatypename
   * @return name of source code conversion class for this datatype
   */
  public String getConversiontype(String datatypename) {
    String[] tuple = mDataTypeValueAndConversionMap.get(datatypename);
    if (tuple == null) {
      return "";
    }
    String retval = tuple[1];
    return (retval == null) ? "" : retval;
  }
}
