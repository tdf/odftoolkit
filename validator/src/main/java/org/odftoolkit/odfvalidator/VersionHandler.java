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
package org.odftoolkit.odfvalidator;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class VersionHandler extends DefaultHandler {

  private static final String OFFICE_NAMESPACE_URI =
      "urn:oasis:names:tc:opendocument:xmlns:office:1.0";
  private static final String DOCUMENT_META = "document-meta";
  private static final String DOCUMENT_CONTENT = "document-content";
  private static final String DOCUMENT_SETTINGS = "document-settings";
  private static final String DOCUMENT_STYLES = "document-styles";
  private static final String VERSION = "version";

  @Override
  public void startElement(String aUri, String aLocalName, String aQName, Attributes aAttributes)
      throws SAXException {
    super.startElement(aUri, aLocalName, aQName, aAttributes);

    if (aUri.equals(OFFICE_NAMESPACE_URI)
        && (aLocalName.equalsIgnoreCase(DOCUMENT_META)
            || aLocalName.equalsIgnoreCase(DOCUMENT_CONTENT)
            || aLocalName.equalsIgnoreCase(DOCUMENT_SETTINGS)
            || aLocalName.equalsIgnoreCase(DOCUMENT_STYLES))) {
      String aVersion = aAttributes.getValue(OFFICE_NAMESPACE_URI, VERSION);
      throw new SAXVersionException(aVersion);
    }
  }
}
