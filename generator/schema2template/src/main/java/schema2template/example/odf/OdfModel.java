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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import schema2template.model.QNamed;
import schema2template.model.QNameValue;

/**
 * Model for ODF specific enhancements.
 * For example, these might be style families used for ODF elements or attribute default values.
 */
public class OdfModel {

    /**
     * This inner class is not meant for use in templates.
     */
    static class AttributeDefaults {
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
        	Set<String> defaults=new HashSet<String>();
        	for(String elementname : elementDefault.keySet()){
        		String retval = elementDefault.get(elementname);
        		if(retval!=null){
        			defaults.add(retval);
        		}
        	}
            return defaults;
        }
    }

    Map<String, List<String>> mNameToFamiliesMap;
    Map<String, AttributeDefaults> mNameToDefaultsMap;

    public OdfModel(Map<String, List<String>> nameToFamiliesMap, Map<String, AttributeDefaults> attributeDefaults) {
        mNameToFamiliesMap = nameToFamiliesMap;
        mNameToDefaultsMap = attributeDefaults;
    }

    /**
     * Determine whether an ELEMENT is stylable (a.k.a. has at least one
     * defined style family). Note: All Definitions sharing the same name share
     * the same style families.
     *
     * @param element Element
     * @return whether there are style families defined for this Definition
     */
    public boolean isStylable(QNamed element) {
        return mNameToFamiliesMap.containsKey(element.getQName());
    }

    /**
     * Get defined style families for this ELEMENT Definition. Note: All Definitions sharing
     * the same name share the same style families.
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
        AttributeDefaults defaults = mNameToDefaultsMap.get(attribute.getQName());
        if (defaults == null) {
            return null;
        }
        return defaults.getDefaults();
    }
}
