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

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.*;

public class Echo extends HandlerBase {

  public Echo(Writer writer) {
    if (writer == null) {
      try {
        out = new OutputStreamWriter(System.out, "UTF-8");
      } catch (UnsupportedEncodingException ex) {
        Logger.getLogger(Echo.class.getName()).log(Level.SEVERE, null, ex);
      }
    } else {
      out = writer;
    }
  }

  private static Writer out;
  private static final StringBuilder sb = new StringBuilder();
  private static final String LINE_END = System.getProperty("line.separator");
  private String indentString = "    "; // Amount to indent
  private int indentLevel = 0;

  // ===========================================================
  // SAX DocumentHandler methods
  // ===========================================================
  public void startDocument() throws SAXException {
    emit("<?xml version='1.0' encoding='UTF-8'?>");
  }

  public void endDocument() throws SAXException {
    try {
      out.write(sb.toString());
      out.flush();
    } catch (IOException e) {
      throw new SAXException("I/O error", e);
    }
  }

  public void startElement(String name, AttributeList attrs) throws SAXException {
    indentLevel++;
    nl();
    emit("<" + name);
    if (attrs != null) {
      for (int i = 0; i < attrs.getLength(); i++) {
        emit(" ");
        emit(attrs.getName(i));
        emit("=\"");
        emit(attrs.getValue(i));
        emit("\"");
      }
    }
    emit(">");
  }

  public void endElement(String name) throws SAXException {
    emit("</" + name + ">");
    indentLevel--;
  }

  public void characters(char buf[], int offset, int len) throws SAXException {
    String s = new String(buf, offset, len);
    if (!s.trim().equals("")) {
      emit(s);
    }
  }

  // ===========================================================
  // Helpers ...
  // ===========================================================
  // Wrap I/O exceptions in SAX exceptions, to
  // suit handler signature requirements
  private void emit(String s) {
    sb.append(s);
  }

  // Start a new line
  // and indent the next line appropriately
  private void nl() {
    sb.append(LINE_END);
    for (int i = 0; i < indentLevel; i++) {
      sb.append(indentString);
    }
  }
}
