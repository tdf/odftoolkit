/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
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

package org.odftoolkit.odfdom.dom.example;

import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.style.props.OdfStyleProperty;
import org.w3c.dom.Node;


/**
 *
 * @author j
 */
class StyleUtils {

  
    /**
     * Returns a property for a (text) node, as it would appear for a user in OpenOffice:
     * First examining the style (and the style's parent styles) and if the property is not
     * found the search continues in the enclosing node, and so on, until an enclosing node
     * is found, whitc has a style (or an inherited style) where the property is defined.
     * Example: findActualStylePropertyValueForNode(textNode, OdfTextProperties.FontName) will give the font name
     * @param node (text) node to be examined
     * @param propertyName for example OdfTextProperties.FontName
     * @return proterty the value of the property, for example "Thorndale"
     */
    static String findActualStylePropertyValueForNode(Node node, OdfStyleProperty propertyName) {
      Node nodeWithStyle = node;

      while (nodeWithStyle!=null && !(nodeWithStyle instanceof OdfStylableElement)) {
            nodeWithStyle = nodeWithStyle.getParentNode();
        }

      if (nodeWithStyle==null) {
        // Property value not found in any nodes' styles!
        return null;
      }


      String propertyValue = ((OdfStylableElement) nodeWithStyle).getProperty(propertyName);

      if (propertyValue != null) {
            return propertyValue;
        }

      // Continue the search in enclosing nodes
      return findActualStylePropertyValueForNode(nodeWithStyle.getParentNode(), propertyName);

    }

    private StyleUtils() {
    }



}
