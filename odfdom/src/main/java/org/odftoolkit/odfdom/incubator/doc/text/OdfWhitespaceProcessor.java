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

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.text.TextLineBreakElement;
import org.odftoolkit.odfdom.dom.element.text.TextSElement;
import org.odftoolkit.odfdom.dom.element.text.TextTabElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * It's a tool class to help process white space.
 *
 * @author J David Eisenberg
 */
public class OdfWhitespaceProcessor {

  public OdfWhitespaceProcessor() {}

  /**
   * Add given text content to an element, handling multiple blanks, tabs, and newlines properly.
   *
   * @param element the element to which content is being added
   * @param content text content including whitespace
   */
  public void append(Element element, String content) {
    char ch;
    StringBuilder partial = new StringBuilder();
    int spaces = 0;
    OdfFileDom owner = (OdfFileDom) element.getOwnerDocument();
    for (int i = 0; i < content.length(); i++) {
      ch = content.charAt(i);
      if (ch == ' ') {
        if (spaces == 0) {
          partial.append(' ');
        }
        spaces++;
      } else if (ch == '\n') {
        emitPartial(element, partial, spaces, owner);
        spaces = 0;
        element.appendChild(new TextLineBreakElement(owner));
      } else if (ch == '\t') {
        emitPartial(element, partial, spaces, owner);
        spaces = 0;
        element.appendChild(new TextTabElement(owner));
      } else if (ch != '\r') // ignore DOS half of CR-LF
      {
        if (spaces > 1) {
          emitPartial(element, partial, spaces, owner);
        }
        partial.append(ch);
        spaces = 0;
      }
    }
    emitPartial(element, partial, spaces, owner);
  }

  /*
   * Send out any information that has been buffered
   */
  private void emitPartial(Element element, StringBuilder partial, int spaces, OdfFileDom owner) {
    /*
     * send out any partial text
     */
    if (partial.length() != 0) {
      element.appendChild(owner.createTextNode(partial.toString()));
    }
    /*
     * and any spaces if necessary
     */
    if (spaces > 1) {
      TextSElement spaceElement = new TextSElement(owner);
      spaceElement.setTextCAttribute(spaces - 1);
      element.appendChild(spaceElement);
    }
    /*
     * and reset all the counters
     */
    partial.delete(0, partial.length());
  }

  /**
   * Retrieve the text content of an element. Recursively retrieves all the text nodes, expanding
   * whitespace where necessary. Ignores any elements except <code>&lt;text:s&gt;</code>, <code>
   * &lt;text:line-break&gt;</code> and <code>&lt;text:tab&gt</code>.
   *
   * @param element an element whose text you want to retrieve
   * @return the element's text content, with whitespace expanded
   */
  public String getText(Node element) {
    String result = "";
    int spaceCount;
    Node node = element.getFirstChild();
    while (node != null) {
      if (node.getNodeType() == Node.TEXT_NODE) {
        result += node.getNodeValue();
      } else if (node.getNodeType() == Node.ELEMENT_NODE) {
        if (node.getLocalName().equals("s")) // text:s
        {
          try {
            spaceCount =
                Integer.parseInt(
                    ((Element) node).getAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "c"));
          } catch (Exception e) {
            spaceCount = 1;
          }
          for (int i = 0; i < spaceCount; i++) {
            result += " ";
          }
        } else if (node.getLocalName().equals("line-break")) {
          result += "\n";
        } else if (node.getLocalName().equals("tab")) {
          result += "\t";
        } else {
          result = result + getText(node);
        }
      }
      node = node.getNextSibling();
    }
    return result;
  }

  /**
   * Append text content to a given element, handling whitespace properly. This is a static method
   * that creates its own OdfWhitespaceProcessor, so that you don't have to.
   *
   * @param element the element to which content is being added
   * @param content text content including whitespace
   */
  public static void appendText(Element element, String content) {
    OdfWhitespaceProcessor processor = new OdfWhitespaceProcessor();
    processor.append(element, content);
  }
}
