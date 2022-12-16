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
package org.odftoolkit.odfdom.dom;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.odftoolkit.odfdom.changes.ChangesFileSaxHandler;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.element.office.OfficeAutomaticStylesElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeBodyElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeDocumentContentElement;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeMasterStyles;
import org.odftoolkit.odfdom.pkg.NamespaceName;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileSaxHandler;
import org.odftoolkit.odfdom.pkg.OdfPackageDocument;
import org.odftoolkit.odfdom.pkg.OdfValidationException;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/** The DOM representation of the ODF content.xml file of an ODF document. */
public class OdfContentDom extends OdfContentDomBase {

  private static final long serialVersionUID = 766167617530147883L;

  /**
   * Creates the DOM representation of an XML file of an Odf document.
   *
   * @param odfDocument the document the XML files belongs to
   * @param packagePath the internal package path to the XML file
   */
  public OdfContentDom(OdfSchemaDocument odfDocument, String packagePath) {
    super(odfDocument, packagePath);
  }

  /** Might be used to initialize specific XML Namespace prefixes/URIs for this XML file */
  @Override
  protected void initialize() throws SAXException, IOException, ParserConfigurationException {
    for (NamespaceName name : OdfDocumentNamespace.values()) {
      mUriByPrefix.put(name.getPrefix(), name.getUri());
      mPrefixByUri.put(name.getUri(), name.getPrefix());
    }
    try {
      if (mPackageDocument instanceof OdfTextDocument
          && ((OdfDocument) mPackageDocument).hasCollaboration()) {
        super.initialize(new ChangesFileSaxHandler(this), this);
      } else {
        super.initialize(new OdfFileSaxHandler(this), this);
      }
    } catch (IOException | ParserConfigurationException | SAXException ex) {
      Logger.getLogger(OdfPackageDocument.class.getName()).log(Level.SEVERE, null, ex);
      OdfValidationException ve =
          new OdfValidationException(
              OdfSchemaConstraint.DOCUMENT_WITH_EXISTENT_BUT_UNREADABLE_CONTENT_OR_STYLES_XML,
              mPackage.getBaseURI(),
              ex,
              OdfSchemaDocument.OdfXMLFile.STYLES.getFileName());
      ErrorHandler eh = mPackage.getErrorHandler();
      if (eh != null) {
        try {
          eh.error(ve);
        } catch (SAXException ex1) {
          Logger.getLogger(OdfStylesDom.class.getName()).log(Level.SEVERE, null, ex1);
          throw ex1;
        }
      }
    }
  }

  /**
   * @return The root element <office:document-content> of the content.xml file as <code>
   *     OfficeDocumentContentElement</code>.
   */
  @Override
  public OfficeDocumentContentElement getRootElement() {
    return (OfficeDocumentContentElement) getDocumentElement();
  }

  /**
   * Retrieve the ODF AutomaticStyles
   *
   * @return the {@odf.element style:automatic-styles} element of this dom. May return null if there
   *     is not yet such element in this dom.
   * @see #getOrCreateAutomaticStyles()
   */
  public OdfOfficeAutomaticStyles getAutomaticStyles() {
    return OdfElement.findFirstChildNode(OdfOfficeAutomaticStyles.class, getFirstChild());
  }

  /**
   * @return the {
   * @odf.element style:automatic-styles} element of this dom. If it does not yet exists, a new one
   *     is inserted into the dom and returned.
   */
  public OdfOfficeAutomaticStyles getOrCreateAutomaticStyles() {

    OdfOfficeAutomaticStyles automaticStyles = getAutomaticStyles();
    if (automaticStyles == null) {
      automaticStyles = newOdfElement(OfficeAutomaticStylesElement.class);

      Node parent = getFirstChild();

      /* from the ODF 1.2 schema
          <define name="office-document-content">
              <element name="office:document-content">
                  <ref name="office-document-common-attrs"/>
                  <ref name="office-scripts"/>
                  <ref name="office-font-face-decls"/>
                  <ref name="office-automatic-styles"/>
                  <ref name="office-body"/>
              </element>
          </define>
      */
      // try to insert before body element
      OdfElement sibling = OdfElement.findFirstChildNode(OfficeBodyElement.class, parent);
      if (sibling == null) {
        sibling = OdfElement.findFirstChildNode(OdfOfficeMasterStyles.class, parent);
      }
      if (sibling == null) {
        parent.appendChild(automaticStyles);
      } else {
        parent.insertBefore(automaticStyles, sibling);
      }
    }
    return automaticStyles;
  }
}
