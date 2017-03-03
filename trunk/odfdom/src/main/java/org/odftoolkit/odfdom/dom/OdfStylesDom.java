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

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.odftoolkit.odfdom.dom.element.office.OfficeBodyElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeDocumentStylesElement;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeMasterStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.pkg.NamespaceName;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.w3c.dom.Node;

/**
 * The DOM repesentation of the ODF styles.xml file of an ODF document.
 */
public class OdfStylesDom extends OdfFileDom {

	private static final long serialVersionUID = 766167617530147886L;

	/**
	 * Creates the DOM representation of an XML file of an Odf document.
	 *
	 * @param odfDocument   the document the XML files belongs to
	 * @param packagePath   the internal package path to the XML file
	 */
	public OdfStylesDom(OdfSchemaDocument odfDocument, String packagePath) {
		super(odfDocument, packagePath);
	}

	/** Might be used to initialize specific XML Namespace prefixes/URIs for this XML file*/
	@Override
	protected void initialize() {
		for (NamespaceName name : OdfDocumentNamespace.values()) {
			mUriByPrefix.put(name.getPrefix(), name.getUri());
			mPrefixByUri.put(name.getUri(), name.getPrefix());
		}
		super.initialize();
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
	 * * @return The root element <office:document-styles> of the styles.xml file as <code>OfficeDocumentStylesElement</code>.
	 */
	@Override
	public OfficeDocumentStylesElement getRootElement() {
		return (OfficeDocumentStylesElement) getDocumentElement();
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


	// ToDo bug 72 - STYLE REFACTORING - THE FOLLOWING METHODS WILL BE RE/MOVED
	// As Package layer should not refer to DOM/DOC layer and DOM files should not
	// handle automatic styles the upcoming DOM Document should capsulate this.
	/**
	 * @return the style:office-styles element of this dom. May return null
	 *         if there is not yet such element in this dom.
	 *
	 * @see #getOrCreateAutomaticStyles()
	 *
	 */
	public OdfOfficeStyles getOfficeStyles() {
		return OdfElement.findFirstChildNode(OdfOfficeStyles.class, getFirstChild());
	}

	/**
	 * Retrieve the ODF AutomaticStyles
	 *
	 * @return the {@odf.element style:automatic-styles} element of this dom. May return null
	 *         if there is not yet such element in this dom.
	 *
	 * @see #getOrCreateAutomaticStyles()
	 *
	 */
	public OdfOfficeAutomaticStyles getAutomaticStyles() {
		return OdfElement.findFirstChildNode(OdfOfficeAutomaticStyles.class, getFirstChild());
	}

	/**
	 * @return the {@odf.element style:automatic-styles} element of this dom. If it does not
	 *         yet exists, a new one is inserted into the dom and returned.
	 *
	 */
	public OdfOfficeAutomaticStyles getOrCreateAutomaticStyles() {

		OdfOfficeAutomaticStyles automaticStyles = getAutomaticStyles();
		if (automaticStyles == null) {
			automaticStyles = newOdfElement(OdfOfficeAutomaticStyles.class);

			Node parent = getFirstChild();

			// try to insert before body or before master-styles element
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
