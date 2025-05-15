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
package org.odftoolkit.odfdom.pkg.rdfa;

import java.util.ArrayList;
import java.util.Arrays;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/** A proxy for delegating the parsing events to its sub ContentHandler(s). */
public class MultiContentHandler implements ContentHandler {
  ArrayList<ContentHandler> subContentHandlers;

  public MultiContentHandler(ContentHandler... subs) {
    subContentHandlers = new ArrayList<>(Arrays.asList(subs));
  }

  public void setDocumentLocator(Locator locator) {
    for (ContentHandler sub : subContentHandlers) {
      sub.setDocumentLocator(locator);
    }
  }

  public void startDocument() throws SAXException {
    for (ContentHandler sub : subContentHandlers) {
      sub.startDocument();
    }
  }

  public void endDocument() throws SAXException {
    for (ContentHandler sub : subContentHandlers) {
      sub.endDocument();
    }
  }

  public void startPrefixMapping(String prefix, String uri) throws SAXException {
    for (ContentHandler sub : subContentHandlers) {
      sub.startPrefixMapping(prefix, uri);
    }
  }

  public void endPrefixMapping(String prefix) throws SAXException {
    for (ContentHandler sub : subContentHandlers) {
      sub.endPrefixMapping(prefix);
    }
  }

  public void startElement(String uri, String localName, String qName, Attributes atts)
      throws SAXException {
    for (ContentHandler sub : subContentHandlers) {
      sub.startElement(uri, localName, qName, atts);
    }
  }

  public void endElement(String uri, String localName, String qName) throws SAXException {
    for (ContentHandler sub : subContentHandlers) {
      sub.endElement(uri, localName, qName);
    }
  }

  public void characters(char[] ch, int start, int length) throws SAXException {
    for (ContentHandler sub : subContentHandlers) {
      sub.characters(ch, start, length);
    }
  }

  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    for (ContentHandler sub : subContentHandlers) {
      sub.ignorableWhitespace(ch, start, length);
    }
  }

  public void processingInstruction(String target, String data) throws SAXException {
    for (ContentHandler sub : subContentHandlers) {
      sub.processingInstruction(target, data);
    }
  }

  public void skippedEntity(String name) throws SAXException {
    for (ContentHandler sub : subContentHandlers) {
      sub.skippedEntity(name);
    }
  }
}
