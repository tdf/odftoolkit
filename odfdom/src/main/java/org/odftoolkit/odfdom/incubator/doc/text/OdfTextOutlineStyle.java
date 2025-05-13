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
package org.odftoolkit.odfdom.incubator.doc.text;

import org.odftoolkit.odfdom.dom.element.text.TextOutlineStyleElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.w3c.dom.Node;

/** Convenient functionalty for the parent ODF OpenDocument element */
public class OdfTextOutlineStyle extends TextOutlineStyleElement {
  private static final long serialVersionUID = -337172468409606629L;

  public OdfTextOutlineStyle(OdfFileDom ownerDoc) {
    super(ownerDoc);
  }

  /**
   * Retrieves the ODF TextOutlineLevelStyle with level count
   *
   * @param level The level count
   * @return The <code>OdfTextOutlineLevelStyle</code>
   */
  public OdfTextOutlineLevelStyle getLevel(int level) {
    Node levelElement = this.getFirstChild();

    while (levelElement != null) {
      if (levelElement instanceof OdfTextOutlineLevelStyle) {
        OdfTextOutlineLevelStyle levelStyle = (OdfTextOutlineLevelStyle) levelElement;
        if (levelStyle.getTextLevelAttribute() == level) {
          return levelStyle;
        }
      }
      levelElement = levelElement.getNextSibling();
    }
    return null;
  }

  /**
   * Retrieves or create the ODF TextOutlineLevelStyle with level count
   *
   * @param level The level count
   * @return The <code>OdfTextOutlineLevelStyle</code>
   */
  public OdfTextOutlineLevelStyle getOrCreateLevel(int level) {
    OdfTextOutlineLevelStyle style = getLevel(level);
    if (style == null) {
      style = ((OdfFileDom) this.ownerDocument).newOdfElement(OdfTextOutlineLevelStyle.class);
      style.setTextLevelAttribute(level);
      this.appendChild(style);
    }
    return style;
  }
}
