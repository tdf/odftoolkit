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

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

class ContentFilter extends NamespaceFilter {

  private static final String CD2_SVG_NAMESPACE_URI = "http://www.w3.org/2000/svg";
  private static final String CD2_XSL_NAMESPACE_URI = "http://www.w3.org/1999/XSL/Format";
  private static final String CD2_SMIL_NAMESPACE_URI = "http://www.w3.org/2001/SMIL20/";
  private static final String BASE_OFFICE_NAMESPACE_URI = "http://openoffice.org/2004/office";
  private static final String BASE_DB_NAMESPACE_URI = "http://openoffice.org/2004/database";
  private static final String SVG_NAMESPACE_URI = OdfDocumentNamespace.SVG.getUri();
  private static final String XSL_NAMESPACE_URI = OdfDocumentNamespace.FO.getUri();
  private static final String SMIL_NAMESPACE_URI = OdfDocumentNamespace.SMIL.getUri();
  private static final String DRAW_NAMESPACE_URI = OdfDocumentNamespace.DRAW.getUri();
  private static final String OFFICE_NAMESPACE_URI = OdfDocumentNamespace.OFFICE.getUri();
  private static final String DB_NAMESPACE_URI = OdfDocumentNamespace.DB.getUri();
  private static final String BASE_OFFICE_NAMESPACE_PREFIX = "office";
  private static final String POLYGON = "polygon";
  private static final String CONTOUR_POLYGON = "contour-polygon";
  private static final String POLYLINE = "polyline";
  private static final String POINTS = "points";
  private static final int MAX_POINTS_LEN = 2048;

  private Logger m_aLogger;
  private String m_aLocalElementName;
  private boolean m_bRoot = true;

  /** Creates a new instance of KnownIssueFilter */
  ContentFilter(Logger aLogger, String aLocalElementName) {
    m_aLogger = aLogger;
    m_aLocalElementName = aLocalElementName;
  }

  String adaptNamespaceUri(String aUri, String aPrefix) {
    String aNewUri = null;
    if (aUri.equals(CD2_SVG_NAMESPACE_URI)) aNewUri = SVG_NAMESPACE_URI;
    else if (aUri.equals(CD2_XSL_NAMESPACE_URI)) aNewUri = XSL_NAMESPACE_URI;
    else if (aUri.equals(CD2_SMIL_NAMESPACE_URI)) aNewUri = SMIL_NAMESPACE_URI;
    else if (aUri.equals(BASE_OFFICE_NAMESPACE_URI)
        && (aPrefix == null || aPrefix.startsWith(BASE_OFFICE_NAMESPACE_PREFIX)))
      aNewUri = OFFICE_NAMESPACE_URI;
    else if (aUri.equals(BASE_DB_NAMESPACE_URI)) aNewUri = DB_NAMESPACE_URI;

    return aNewUri;
  }

  void namespaceUriAdapted(String aUri, String aNewUri) {
    String aMsg =
        (aUri.equals(BASE_OFFICE_NAMESPACE_URI) || aUri.equals(BASE_DB_NAMESPACE_URI))
            ? "Adapting Base namspace'" + aUri + "'"
            : "Adapting OpenDocument CD2 namspace'"
                + aUri
                + "' (has been stored by old OOo versions)";
    m_aLogger.logInfo(aMsg, false);
  }

  @Override
  public void startElement(String aUri, String aLocalName, String aQName, Attributes aAtts)
      throws SAXException {
    if (aUri.equals(DRAW_NAMESPACE_URI)
        && (aLocalName.equals(POLYGON)
            || aLocalName.equals(POLYLINE)
            || aLocalName.equals(CONTOUR_POLYGON))) {
      String aPointsValue = aAtts.getValue(DRAW_NAMESPACE_URI, POINTS);
      if (aPointsValue != null && aPointsValue.length() > MAX_POINTS_LEN) {
        m_aLogger.logInfo(
            String.format(
                "'draw:points' of <%s> has been stripped (value starts with '%s')",
                aQName, aPointsValue.substring(0, 40)),
            false);
        AttributesImpl aNewAtts = new AttributesImpl(aAtts);
        int nAttr = aNewAtts.getIndex(DRAW_NAMESPACE_URI, POINTS);
        int nPos = aPointsValue.lastIndexOf(' ', MAX_POINTS_LEN);
        String aNewValue = nPos != -1 ? aPointsValue.substring(0, nPos) : "";
        aNewAtts.setValue(nAttr, aNewValue);
        aAtts = aNewAtts;
      }
    }
    super.startElement(aUri, aLocalName, aQName, aAtts);
    if (m_bRoot) {
      if (!(aUri.equals(OFFICE_NAMESPACE_URI) && aLocalName.equals(m_aLocalElementName)))
        m_aLogger.logError("Invalid root element: " + aQName);
      m_bRoot = false;
    }
  }
}
