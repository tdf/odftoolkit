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
 ************************************************************************//*
package org.odftoolkit.odfdom.pkg;

import java.util.Map;
import org.odftoolkit.odfdom.pkg.manifest.Algorithm;
import org.odftoolkit.odfdom.pkg.manifest.EncryptionData;
import org.odftoolkit.odfdom.pkg.manifest.KeyDerivation;
import org.odftoolkit.odfdom.pkg.manifest.OdfFileEntry;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class OdfManifestSaxHandler implements ContentHandler {

	private OdfFileEntry _currentFileEntry;
	private EncryptionData _currentEncryptionData;
	private OdfPackage mPackage;
	private static final String EMPTY_STRING = "";

	public OdfManifestSaxHandler(OdfPackage pkg) {
		mPackage = pkg;
	}

	*//**
	 * Receive an object for locating the origin of SAX document events.
	 *//*
	public void setDocumentLocator(Locator locator) {
	}

	*//**
	 * Receive notification of the beginning of a document.
	 *//*
	public void startDocument() throws SAXException {
	}

	*//**
	 * Receive notification of the end of a document.
	 *//*
	public void endDocument() throws SAXException {
	}

	*//**
	 * Begin the scope of a prefix-URI Namespace mapping.
	 *//*
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
	}

	*//**
	 * End the scope of a prefix-URI mapping.
	 *//*
	public void endPrefixMapping(String prefix) throws SAXException {
	}

	*//**
	 * Receive notification of the beginning of an element.
	 *//*
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		Map<String, OdfFileEntry> entries = mPackage.getManifestEntries();

		if (localName.equals("file-entry")) {
			String path = atts.getValue("manifest:full-path");
			if (path.equals(EMPTY_STRING)) {
				if(mPackage.getErrorHandler() != null){
					mPackage.logValidationError(OdfPackageConstraint.MANIFEST_WITH_EMPTY_PATH, mPackage.getBaseURI());
				}
			} 
			path = OdfPackage.normalizePath(path);
			_currentFileEntry = entries.get(path);
			if (_currentFileEntry == null) {
				_currentFileEntry = new OdfFileEntry();
			}
			if (path != null) {
				entries.put(path, _currentFileEntry);
			}
			_currentFileEntry.setPath(atts.getValue("manifest:full-path"));
			_currentFileEntry.setMediaTypeString(atts.getValue("manifest:media-type"));
			if (atts.getValue("manifest:size") != null) {
				try {
					_currentFileEntry.setSize(Integer.parseInt(atts.getValue("manifest:size")));
				} catch (NumberFormatException nfe) {
					throw new SAXException("not a number: "
							+ atts.getValue("manifest:size") + nfe.getMessage());
				}
			}
		} else if (localName.equals("encryption-data")) {
			_currentEncryptionData = new EncryptionData();
			if (_currentFileEntry != null) {
				_currentEncryptionData.setChecksumType(atts.getValue("manifest:checksum-type"));
				_currentEncryptionData.setChecksum(atts.getValue("manifest:checksum"));
				_currentFileEntry.setEncryptionData(_currentEncryptionData);
			}
		} else if (localName.equals("algorithm")) {
			Algorithm algorithm = new Algorithm();
			algorithm.setName(atts.getValue("manifest:algorithm-name"));
			algorithm.setInitializationVector(atts.getValue("manifest:initialization-vector"));
			if (_currentEncryptionData != null) {
				_currentEncryptionData.setAlgorithm(algorithm);
			}
		} else if (localName.equals("key-derivation")) {
			KeyDerivation keyDerivation = new KeyDerivation();
			keyDerivation.setName(atts.getValue("manifest:key-derivation-name"));
			keyDerivation.setSalt(atts.getValue("manifest:salt"));
			if (atts.getValue("manifest:iteration-count") != null) {
				try {
					keyDerivation.setIterationCount(Integer.parseInt(atts.getValue("manifest:iteration-count")));
				} catch (NumberFormatException nfe) {
					throw new SAXException("not a number: "
							+ atts.getValue("manifest:iteration-count"));
				}
			}
			if (_currentEncryptionData != null) {
				_currentEncryptionData.setKeyDerivation(keyDerivation);
			}
		}else if (localName.equals("manifest")) {
                     mPackage.setManifestVersion(atts.getValue("manifest:version"));
                }

	}

	*//**
	 * Receive notification of the end of an element.
	 *//*
	public void endElement(String namespaceURI, String localName,
			String qName) throws SAXException {
		//ToDo Issue 263: Parsing of Manifest.xml specific part to be added to parser
		if (localName.equals("encryption-data")) {
			_currentEncryptionData = null;
		}
	}

	*//**
	 * Receive notification of character data.
	 *//*
	public void characters(char[] ch, int start, int length)
			throws SAXException {
	}

	*//**
	 * Receive notification of ignorable whitespace in element content.
	 *//*
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
	}

	*//**
	 * Receive notification of a processing instruction.
	 *//*
	public void processingInstruction(String target, String data)
			throws SAXException {
	}

	*//**
	 * Receive notification of a skipped entity.
	 *//*
	public void skippedEntity(String name) throws SAXException {
	}
}
*/