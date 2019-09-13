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
package org.odftoolkit.odfdom.dom;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.odftoolkit.odfdom.dom.element.office.OfficeDocumentMetaElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeMetaElement;
import org.odftoolkit.odfdom.pkg.NamespaceName;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.xml.sax.SAXException;

/**
 * The DOM representation of the ODF meta.xml file of an ODF document.
 */
public class OdfMetaDom extends OdfFileDom {

	private static final long serialVersionUID = 766167617530147884L;

	/**
	 * Creates the DOM representation of an XML file of an Odf document.
	 *
	 * @param odfDocument   the document the XML files belongs to
	 * @param packagePath   the internal package path to the XML file
	 */
	public OdfMetaDom(OdfSchemaDocument odfDocument, String packagePath) {
		super(odfDocument, packagePath);
		OfficeDocumentMetaElement rootElement = this.getRootElement();
		if(rootElement == null){
			rootElement = new OfficeDocumentMetaElement(this);
			this.appendChild(rootElement);
			OfficeMetaElement officeMetaElement = new OfficeMetaElement(this);
			rootElement.appendChild(officeMetaElement);
		}
	}

	/** Might be used to initialize specific XML Namespace prefixes/URIs for this XML file*/
	@Override
	protected void initialize() {
		for (NamespaceName name : OdfDocumentNamespace.values()) {
			mUriByPrefix.put(name.getPrefix(), name.getUri());
			mPrefixByUri.put(name.getUri(), name.getPrefix());
		}
        try {
            super.initialize();
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            Logger.getLogger(OdfMetaDom.class.getName()).log(Level.SEVERE, null, ex);
        }
	}

	/**
	 * Retrieves the Odf Document
	 *
	 * @return The <code>OdfDocument</code>
	 */
	@Override
	public OdfSchemaDocument getDocument() {
		return (OdfSchemaDocument) mPackageDocument;
	}

	/**
	 * @return The root element <office:document-meta> of the meta.xml file as <code>OfficeDocumentMetaElement</code>.
	 */
	@Override
	public final OfficeDocumentMetaElement getRootElement() {
		return (OfficeDocumentMetaElement) getDocumentElement();
	}

	/**
	 * Creates an JDK <code>XPath</code> instance.
	 * Initialized with ODF namespaces from <code>OdfDocumentNamespace</code>. Updated with all namespace of the XML file.
	 * @return an XPath instance with namespace context set to include the standard
	 * ODFDOM prefixes.
	 */
	@Override
	public XPath getXPath() {
		if (mXPath == null) {
			mXPath = XPathFactory.newInstance().newXPath();
			mXPath.setNamespaceContext(this);
			for (NamespaceName name : OdfDocumentNamespace.values()) {
				mUriByPrefix.put(name.getPrefix(), name.getUri());
				mPrefixByUri.put(name.getUri(), name.getPrefix());
			}
		}
		return mXPath;
	}
}
