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
package org.odftoolkit.odfdom.incubator.doc.number;

import org.odftoolkit.odfdom.dom.element.number.*;
import org.odftoolkit.odfdom.dom.element.style.StyleMapElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.w3c.dom.Node;

/** Convenient functionality for the parent ODF OpenDocument element */
public class OdfNumberTextStyle extends NumberTextStyleElement {
  private static final String BOOLEAN = "BOOLEAN";

  public OdfNumberTextStyle(OdfFileDom ownerDoc) {
    super(ownerDoc);
  }

  public OdfNumberTextStyle(OdfFileDom ownerDoc, String format, String styleName) {
    super(ownerDoc);
    this.setStyleNameAttribute(styleName);
    setFormat(format);
  }

  @Override
  public void setFormat(String format) {
    // TODO: create attribute from format string
    while (!format.isEmpty()) {
      int atPos = format.indexOf("@");
      if (atPos == 0) {
        ((NumberTextStyleElement) this).newNumberTextContentElement();
      } else {
        NumberTextElement newSubElement = ((NumberTextStyleElement) this).newNumberTextElement();
        newSubElement.setTextContent(atPos < 0 ? format : format.substring(0, atPos));
        if (atPos < 0) {
          break; // finished
        } else {
          ((NumberTextStyleElement) this).newNumberTextContentElement();
        }
      }
      format = format.substring(atPos + 1);
    }
  }

  /**
   * Get the format string that represents this style.
   *
   * @return the format string
   */
  @Override
  public String getFormat(boolean caps) {
    String mappedResult = "";
    String result = "";
    Node m = getFirstChild();
    while (m != null) {
      if (m instanceof NumberTextElement) {
        String textcontent = m.getTextContent();
        if (textcontent == null || textcontent.length() == 0) {
          textcontent = " ";
        }
        result += textcontent;
      } else if (m instanceof NumberTextContentElement) {
        result += "@";
      } else if (m instanceof StyleMapElement) {
        mappedResult += getMapping((StyleMapElement) m);
        mappedResult += ";";
      }

      m = m.getNextSibling();
    }
    if (!mappedResult.isEmpty()) {
      result = mappedResult + result;
    }
    return result;
  }
}
