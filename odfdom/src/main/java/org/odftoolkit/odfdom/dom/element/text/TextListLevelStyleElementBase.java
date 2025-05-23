/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
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

/*
 * This file is automatically generated.
 * Don't edit manually.
 */
package org.odftoolkit.odfdom.dom.element.text;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.attribute.text.TextLevelAttribute;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.style.StyleListLevelPropertiesElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;

/** DOM implementation of OpenDocument base element */
public abstract class TextListLevelStyleElementBase extends OdfStyleBase {

  /**
   * Create the instance of <code>TextListLevelStyleElementBase</code>
   *
   * @param ownerDoc The type is <code>OdfFileDom</code>
   * @param elementName The type is <code>OdfName</code>
   */
  public TextListLevelStyleElementBase(OdfFileDom ownerDoc, OdfName elementName) {
    super(ownerDoc, elementName);
  }

  /**
   * Receives the value of the ODFDOM attribute representation <code>TextLevelAttribute</code> , See
   * {@odf.attribute text:level}
   *
   * @return - the <code>Integer</code> , the value or <code>null</code>, if the attribute is not
   *     set and no default value defined.
   */
  public Integer getTextLevelAttribute() {
    TextLevelAttribute attr =
        (TextLevelAttribute) getOdfAttribute(OdfDocumentNamespace.TEXT, "level");
    if (attr != null && !attr.getValue().isEmpty()) {
      return attr.intValue();
    }
    return null;
  }

  /**
   * Sets the value of ODFDOM attribute representation <code>TextLevelAttribute</code> , See
   * {@odf.attribute text:level}
   *
   * @param textLevelValue The type is <code>Integer</code>
   */
  public void setTextLevelAttribute(Integer textLevelValue) {
    TextLevelAttribute attr = new TextLevelAttribute((OdfFileDom) this.ownerDocument);
    setOdfAttribute(attr);
    attr.setIntValue(textLevelValue);
  }

  /**
   * Create child element {@odf.element style:list-level-properties}.
   *
   * @return the element {@odf.element style:list-level-properties}
   */
  public StyleListLevelPropertiesElement newStyleListLevelPropertiesElement() {
    StyleListLevelPropertiesElement styleListLevelProperties =
        ((OdfFileDom) this.ownerDocument).newOdfElement(StyleListLevelPropertiesElement.class);
    this.appendChild(styleListLevelProperties);
    return styleListLevelProperties;
  }
}
