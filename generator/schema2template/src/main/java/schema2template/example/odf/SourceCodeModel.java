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
package schema2template.example.odf;

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
 * Model for Java specific enhancements like common base classes for elements and Java valuetypes for
 * valuetypes used in schema.
 */
public class SourceCodeModel {
    
    Map<String, SourceCodeBaseClass> mElementBaseMap;
    SortedSet<SourceCodeBaseClass> mBaseclasses;
    Map<String, SourceCodeBaseClass> mBasenameToBaseclass;
    Map<String, String[]> mDatatypeValueAndConversionMap;  // datatype -> {value-type, conversion-classname}

    /**
     * Construct SourceCodeModel. Not meant for template usage.
     *
     * @param model the XMLModel
     * @param odfmodel the OdfModel
     * @param elementnameBasenameMap the mapping from element names to source code base class names
     * @param datatypeValueAndConversionMap the mapping from schema datatype to {source code types, name of conversion class}
     */
    public SourceCodeModel(XMLModel model, OdfModel odfmodel, Map<String, String> elementnameBasenameMap, Map<String, String[]> datatypeValueAndConversionMap) {
        mDatatypeValueAndConversionMap = datatypeValueAndConversionMap;

        // Intermediate Step -> get all basenames
        SortedSet<String> basenames = new TreeSet<String>(elementnameBasenameMap.values());

        // Intermediate Step -> get all subelement Definitions for each basename
        Map<String, SortedSet<PuzzlePiece>> basenameElementsMap = new HashMap<String, SortedSet<PuzzlePiece>>(basenames.size());
        for (String elementname : elementnameBasenameMap.keySet()) {
            String basename = elementnameBasenameMap.get(elementname);
            SortedSet<PuzzlePiece> elements = basenameElementsMap.get(basename);
            if (elements == null) {
                elements = new TreeSet<PuzzlePiece>();
                basenameElementsMap.put(basename, elements);
            }
            QNamedPuzzleComponent subelement = model.getElement(elementname);
            if (subelement != null) {
                if (subelement instanceof Collection) {
                    elements.addAll((Collection) subelement);
                }
                else {
                    elements.add((PuzzlePiece) subelement);
                }
            }
            else {
                System.err.println("Warning: Baseclass definition for unknown element " + elementname);
            }
        }

        // Generate all baseclasses (additional intermediate step: register them)
        mBasenameToBaseclass = new HashMap<String, SourceCodeBaseClass>(basenames.size());
        mBaseclasses = new TreeSet<SourceCodeBaseClass>();
        for (String basename : basenames) {
            SourceCodeBaseClass javabaseclass = new SourceCodeBaseClass(odfmodel, basename, basenameElementsMap.get(basename));
            mBaseclasses.add(javabaseclass);
            mBasenameToBaseclass.put(basename, javabaseclass);
        }

        // Generate a map from element tag name to baseclasses
        mElementBaseMap = new HashMap<String, SourceCodeBaseClass>(elementnameBasenameMap.size());
        for (String elementname : elementnameBasenameMap.keySet()) {
            String basename = elementnameBasenameMap.get(elementname);
            SourceCodeBaseClass baseclass = mBasenameToBaseclass.get(basename);
            mElementBaseMap.put(elementname, baseclass);
        }
    }

    /**
     * Use in templates: Get baseclass of one element
     *
     * @param subelement element
     * @return baseclass
     */
    public SourceCodeBaseClass getBaseclassOf(QNamed subelement) {
        return mElementBaseMap.get(subelement.getQName());
    }

    /**
     * Use in templates: Get all baseclasses
     * @return all baseclasses
     */
    public SortedSet<SourceCodeBaseClass> getBaseclasses() {
        return mBaseclasses;
    }

    /**
     * Use in templates: Get baseclass by name
     * @param basename name of baseclass
     * @return baseclass object
     */
    public SourceCodeBaseClass getBaseclass(String basename) {
        return mBasenameToBaseclass.get(basename);
    }

    /**
     * Use in templates: Get baseclass by name
     *
     * @param base name of baseclass
     * @return baseclass object
     */
    public SourceCodeBaseClass getBaseclass(QNamed base) {
        return getBaseclass(base.getQName());
    }

    /**
     * Use in templates: Get Source code value type for datatype used in schema
     *
     * @param datatype
     * @return source code value type
     */
    public String getValuetype(QNamed datatype) {
        String datatypename = datatype.getQName();
        String[] tuple = mDatatypeValueAndConversionMap.get(datatypename);
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
        SortedSet<String> retval = new TreeSet<String>();
        for (PuzzlePiece datatype : datatypes.getCollection()) {
            String datatypename = datatype.getQName();
            String[] tuple = mDatatypeValueAndConversionMap.get(datatypename);
            if (tuple != null) {
                String valuetype = tuple[0];
                if (valuetype != null) {
                    retval.add(valuetype);
                }
            }
        }
        return retval;
    }

    /**
     * Use in templates: Translate Java object to simple Java datatype
     *
     * @param objectType like "Boolean"
     * @return simpleType like "boolean"
     */
    public String getSimpleType(String objectType) {
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
        String[] tuple = mDatatypeValueAndConversionMap.get(datatypename);
        if (tuple == null) {
            return "";
        }
        String retval = tuple[1];
        return (retval == null) ? "" : retval;
    }

}
