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
package schema2template.template;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import schema2template.grammar.PuzzleComponent;
import schema2template.grammar.PuzzlePiece;
import schema2template.grammar.XMLModel;

/**
 * Model for Java specific enhancements like common base classes for elements and Java value types
 * for value types used in schema. Encapsulates information from the grammar-additions.xml file.
 */
public class SourceCodeModel {

  Map<String, String> mElementSuperClassNameMap;
  Map<String, SourceCodeBaseClass> mElementBaseMap;
  SortedSet<SourceCodeBaseClass> mBaseClasses;
  Map<String, SourceCodeBaseClass> mBaseNameToBaseClass;
  Map<String, String[]>
      mDataTypeValueAndConversionMap; // datatype -> {value-type, conversion-classname}

  /**
   * Construct SourceCodeModel. Not meant for template usage.
   *
   * @param schemaModel the XMLModel (grammar model)
   * @param elementNameBaseNameMap the mapping from element names to source code base class names
   * @param datatypeValueAndConversionMap the mapping from schema datatype to {source code types,
   *     name of conversion class}
   */
  public SourceCodeModel(
      XMLModel schemaModel,
      Map<String, String> elementNameBaseNameMap,
      Map<String, String> elementSuperClassNameMap,
      Map<String, String[]> datatypeValueAndConversionMap) {
    mDataTypeValueAndConversionMap = datatypeValueAndConversionMap;
    mElementSuperClassNameMap = elementSuperClassNameMap;

    // Intermediate Step -> get all baseNames
    SortedSet<String> baseNames = new TreeSet<String>(elementNameBaseNameMap.values());

    // Intermediate Step -> get all childOfBaseElement Definitions for each baseName
    Map<String, SortedSet<PuzzlePiece>> baseNameElementsMap =
        new HashMap<String, SortedSet<PuzzlePiece>>(baseNames.size());
    for (String elementName : elementNameBaseNameMap.keySet()) {
      String baseName = elementNameBaseNameMap.get(elementName);
      SortedSet<PuzzlePiece> elements = baseNameElementsMap.get(baseName);
      if (elements == null) {
        elements = new TreeSet<PuzzlePiece>();
        baseNameElementsMap.put(baseName, elements);
      }
      PuzzleComponent childElement = schemaModel.getElement(elementName);
      if (childElement != null) {
        if (childElement instanceof Collection) {
          elements.addAll((Collection) childElement);
        } else {
          elements.add((PuzzlePiece) childElement);
        }
      } else {
        System.err.println("Warning: BaseClass definition for unknown element " + elementName);
      }
    }

    // Generate all base classes (additional intermediate step: register them)
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
   * @param childElement element
   * @return baseclass
   */
  public SourceCodeBaseClass getBaseClassOf(PuzzleComponent childElement) {
    SourceCodeBaseClass c = null;
    if (childElement != null) {
      if (mElementBaseMap.containsKey(childElement.getQName())) {
        c = mElementBaseMap.get(childElement.getQName());
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
   * @return sourceCodeBaseClass object
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
  public SourceCodeBaseClass getBaseClass(PuzzleComponent base) {
    return getBaseClass(base.getQName());
  }

  /**
   * Use in templates: Get Source code value type for datatype used in schema
   *
   * @param datatype
   * @return source code value type
   */
  public String getValuetype(PuzzleComponent datatype) {
    String datatypename = datatype.getQName();
    String[] tuple = mDataTypeValueAndConversionMap.get(datatypename);
    if (tuple == null) {
      return "";
    }
    String retval = tuple[0];
    return (retval == null) ? "" : retval;
  }

  /**
   * Use in templates: Check for super class
   *
   * @param nodeName the name of the defined XML element or attribute
   * @return if there has been a super class being specified via 'extends' attribute in the
   *     grammar-additions.xml
   */
  public boolean hasSuperClass(String nodeName) {
    boolean hasSuperClassName = false;
    if (mElementSuperClassNameMap != null && nodeName != null) {
      hasSuperClassName = mElementSuperClassNameMap.containsKey(nodeName);
    }
    return hasSuperClassName;
  }

  /**
   * Use in templates: Get fully qualified super class name
   *
   * @param nodeName the name of the defined XML element or attribute
   * @return the super class name fully qualified with Java Package as the one being set via
   *     'extends' attribute in the grammar-additions.xml
   */
  public String getSuperClass(String nodeName) {
    String superClassName = null;
    if (mElementSuperClassNameMap != null && nodeName != null) {
      superClassName = mElementSuperClassNameMap.get(nodeName);
    }
    return superClassName;
  }

  /**
   * Use in templates: Get super class name (without Java package)
   *
   * @param nodeName the name of the defined XML element or attribute
   * @return the super class name if one was set via 'extends' in the grammar-additions.xml
   */
  public String getSuperClassName(String nodeName) {
    String superClassName = null;
    if (mElementSuperClassNameMap != null && nodeName != null) {
      superClassName = mElementSuperClassNameMap.get(nodeName);
      if (superClassName != null & superClassName.contains(".")) {
        superClassName =
            superClassName.substring(superClassName.lastIndexOf(".") + 1, superClassName.length());
      }
    }
    return superClassName;
  }
  /**
   * Use in templates: Get Source code value type for datatype used in schema
   *
   * @param nodeName the name of the defined XML element or attribute
   * @return the package name of the super class
   */
  public String getSuperClassPackageName(String nodeName) {
    String superClassName = null;
    if (mElementSuperClassNameMap != null && nodeName != null) {
      superClassName = mElementSuperClassNameMap.get(nodeName);
      if (superClassName != null & superClassName.contains(".")) {
        superClassName = superClassName.substring(0, superClassName.lastIndexOf("."));
      }
    }
    return superClassName;
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
  public String getConversiontype(PuzzleComponent datatype) {
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
