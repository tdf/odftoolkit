/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Use is subject to license terms.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ************************************************************************/
package org.odftoolkit.odfdom.pkg.manifest;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.odftoolkit.odfdom.dom.OdfSchemaDocument;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.xml.sax.SAXException;

/**
 * The DOM representation of the ODF manifest.xml file of an ODF document.
 *
 * @since 0.8.9
 */
public class OdfManifestDom extends OdfFileDom {

	private static final long serialVersionUID = 8149848234988627233L;

	/**
	 * Creates the DOM representation of an XML file of an Odf document.
	 *
	 * @param odfDocument
	 *            the document the XML files belongs to
	 * @param packagePath
	 *            the internal package path to the XML file
	 */
	public OdfManifestDom(OdfSchemaDocument odfDocument, String packagePath) {
		super(odfDocument, packagePath);
	}

	/**
	 * Creates the DOM representation of an XML file of an Odf document.
	 *
	 * @param pkg
	 *            the package the XML files belongs to
	 * @param packagePath
	 *            the internal package path to the XML file
	 */
	public OdfManifestDom(OdfPackage pkg, String packagePath) {
		super(pkg, packagePath);
	}

	/**
	 * Might be used to initialize specific XML Namespace prefixes/URIs for this
	 * XML file
	 */
	@Override
	protected void initialize() {
		mUriByPrefix.put("manifest", "urn:oasis:names:tc:opendocument:xmlns:manifest:1.0");
		mPrefixByUri.put("urn:oasis:names:tc:opendocument:xmlns:manifest:1.0", "manifest");
        try {
            super.initialize();
        } catch (SAXException |IOException | ParserConfigurationException ex) {
            Logger.getLogger(OdfManifestDom.class.getName()).log(Level.SEVERE, null, ex);
        }
	}

	/**
	 * @return The root element <manifest:manifest > of the manifest.xml file as
	 *         <code>ManifestElement</code>.
	 */
	@Override
	public ManifestElement getRootElement() {
		return (ManifestElement) getDocumentElement();
	}

	/**
	 * Creates an JDK <code>XPath</code> instance. Initialized with ODF
	 * namespaces from <code>OdfDocumentNamespace</code>. Updated with all
	 * namespace of the XML file.
	 *
	 * @return an XPath instance with namespace context set to include the
	 *         standard ODFDOM prefixes.
	 */
	@Override
	public XPath getXPath() {
		if (mXPath == null) {
			mXPath = XPathFactory.newInstance().newXPath();
			mXPath.setNamespaceContext(this);
			mUriByPrefix.put("manifest", "urn:oasis:names:tc:opendocument:xmlns:manifest:1.0");
			mPrefixByUri.put("urn:oasis:names:tc:opendocument:xmlns:manifest:1.0", "manifest");
		}
		return mXPath;
	}
}
