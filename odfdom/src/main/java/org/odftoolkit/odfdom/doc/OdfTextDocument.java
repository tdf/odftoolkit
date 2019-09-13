/**
 * **********************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ***********************************************************************
 */
package org.odftoolkit.odfdom.doc;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.odftoolkit.odfdom.dom.OdfSettingsDom;
import org.odftoolkit.odfdom.dom.element.office.OfficeTextElement;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.pkg.MediaType;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * This class represents an empty ODF text document.
 *
 */
public class OdfTextDocument extends OdfDocument {

	private static final String EMPTY_TEXT_DOCUMENT_PATH = "/OdfTextDocument.odt";
	static final Resource EMPTY_TEXT_DOCUMENT_RESOURCE = new Resource(EMPTY_TEXT_DOCUMENT_PATH);
	private Boolean hasTabsRelativeToIndent = null;

	/**
	 * This enum contains all possible media types of OdfSpreadsheetDocument
	 * documents.
	 */
	public enum OdfMediaType implements MediaType {

		TEXT(OdfDocument.OdfMediaType.TEXT),
		TEXT_TEMPLATE(OdfDocument.OdfMediaType.TEXT_TEMPLATE),
		TEXT_MASTER(OdfDocument.OdfMediaType.TEXT_MASTER),
		TEXT_WEB(OdfDocument.OdfMediaType.TEXT_WEB);
		private final OdfDocument.OdfMediaType mMediaType;

		OdfMediaType(OdfDocument.OdfMediaType mediaType) {
			this.mMediaType = mediaType;
		}

		/**
		 * @return the media type of this document
		 */
		public String getMediaTypeString() {
			return mMediaType.getMediaTypeString();
		}

		/**
		 * @return the ODF file suffix of this document
		 */
		public String getSuffix() {
			return mMediaType.getSuffix();
		}

		/**
		 *
		 * @param mediaType string defining an ODF document
		 * @return the according OdfMediatype encapsulating the given string and
		 * the suffix
		 */
		public static OdfDocument.OdfMediaType getOdfMediaType(String mediaType) {
			return OdfDocument.OdfMediaType.getOdfMediaType(mediaType);
		}
	}

	/**
	 * Creates an empty text document.
	 *
	 * @return ODF text document based on a default template
	 * @throws java.lang.Exception - if the document could not be created
	 */
	public static OdfTextDocument newTextDocument() throws Exception {
		return (OdfTextDocument) OdfDocument.loadTemplate(EMPTY_TEXT_DOCUMENT_RESOURCE, OdfDocument.OdfMediaType.TEXT);
	}

	/**
	 * Creates an empty text document.
	 *
	 * @return ODF text document based on a default template
	 * @throws java.lang.Exception - if the document could not be created
	 */
	public static OdfTextDocument newTextDocument(OdfTextDocument.OdfMediaType mimeType) throws Exception {
		return (OdfTextDocument) OdfDocument.loadTemplate(EMPTY_TEXT_DOCUMENT_RESOURCE, OdfDocument.OdfMediaType.TEXT);
	}

	/**
	 * Creates an empty text template.
	 *
	 * @return ODF text template based on a default
	 * @throws java.lang.Exception - if the template could not be created
	 */
	public static OdfTextDocument newTextTemplateDocument() throws Exception {
		return (OdfTextDocument) OdfDocument.loadTemplate(EMPTY_TEXT_DOCUMENT_RESOURCE, OdfDocument.OdfMediaType.TEXT_TEMPLATE);
	}

	/**
	 * Creates an empty text master document.
	 *
	 * @return ODF text master based on a default
	 * @throws java.lang.Exception - if the document could not be created
	 */
	public static OdfTextDocument newTextMasterDocument() throws Exception {
		OdfTextDocument doc = (OdfTextDocument) OdfDocument.loadTemplate(EMPTY_TEXT_DOCUMENT_RESOURCE, OdfDocument.OdfMediaType.TEXT_MASTER);
		doc.changeMode(OdfMediaType.TEXT_MASTER);
		return doc;
	}

	/**
	 * Creates an empty text web.
	 *
	 * @return ODF text web based on a default
	 * @throws java.lang.Exception - if the document could not be created
	 */
	public static OdfTextDocument newTextWebDocument() throws Exception {
		OdfTextDocument doc = (OdfTextDocument) OdfDocument.loadTemplate(EMPTY_TEXT_DOCUMENT_RESOURCE, OdfDocument.OdfMediaType.TEXT_WEB);
		doc.changeMode(OdfMediaType.TEXT_WEB);
		return doc;
	}

	/**
	 * Creates an OdfTextDocument from the OpenDocument provided by a resource
	 * Stream.
	 *
	 * <p>Since an InputStream does not provide the arbitrary (non sequential)
	 * read access needed by OdfTextDocument, the InputStream is cached. This
	 * usually takes more time compared to the other createInternalDocument
	 * methods. An advantage of caching is that there are no problems
	 * overwriting an input file.</p>
	 *
	 * <p>If the resource stream is not a ODF text document, ClassCastException
	 * might be thrown.</p>
	 *
	 * @param inputStream - the InputStream of the ODF text document.
	 * @return the text document created from the given InputStream
	 * @throws java.lang.Exception - if the document could not be created.
	 */
	public static OdfTextDocument loadDocument(InputStream inputStream) throws Exception {
		return (OdfTextDocument) OdfDocument.loadDocument(inputStream);
	}

	/**
	 * Creates an OdfTextDocument from the OpenDocument provided by a resource
	 * Stream.
	 *
	 * <p>Since an InputStream does not provide the arbitrary (non sequential)
	 * read access needed by OdfTextDocument, the InputStream is cached. This
	 * usually takes more time compared to the other createInternalDocument
	 * methods. An advantage of caching is that there are no problems
	 * overwriting an input file.</p>
	 *
	 * <p>If the resource stream is not a ODF text document, ClassCastException
	 * might be thrown.</p>
	 *
	 * @param inputStream - the InputStream of the ODF text document.
	 * @param configuration - key/value pairs of user given run-time settings
	 * (configuration)
	 * @return the text document created from the given InputStream
	 * @throws java.lang.Exception - if the document could not be created.
	 */
	public static OdfTextDocument loadDocument(InputStream inputStream, Map<String, Object> configuration) throws Exception {
 		return (OdfTextDocument) OdfDocument.loadDocument(inputStream, configuration);
	}

	/**
	 * Loads an OdfTextDocument from the provided path.
	 *
	 * <p>OdfTextDocument relies on the file being available for read access
	 * over the whole life-cycle of OdfTextDocument.</p>
	 *
	 * <p>If the resource stream is not a ODF text document, ClassCastException
	 * might be thrown.</p>
	 *
	 * @param documentPath - the path from where the document can be loaded
	 * @return the text document from the given path or NULL if the media type
	 * is not supported by ODFDOM.
	 * @throws java.lang.Exception - if the document could not be created.
	 */
	public static OdfTextDocument loadDocument(String documentPath) throws Exception {
		return (OdfTextDocument) OdfDocument.loadDocument(documentPath);
	}

	/**
	 * Creates an OdfTextDocument from the OpenDocument provided by a File.
	 *
	 * <p>OdfTextDocument relies on the file being available for read access
	 * over the whole lifecycle of OdfTextDocument.</p>
	 *
	 * <p>If the resource stream is not a ODF text document, ClassCastException
	 * might be thrown.</p>
	 *
	 * @param file - a file representing the ODF text document.
	 * @return the text document created from the given File
	 * @throws java.lang.Exception - if the document could not be created.
	 */
	public static OdfTextDocument loadDocument(File file) throws Exception {
		return (OdfTextDocument) OdfDocument.loadDocument(file);
	}

	/**
	 * To avoid data duplication a new document is only created, if not already
	 * opened. A document is cached by this constructor using the internal path
	 * as key.
	 */
	protected OdfTextDocument(OdfPackage pkg, String internalPath, OdfTextDocument.OdfMediaType odfMediaType) throws SAXException {
		super(pkg, internalPath, odfMediaType.mMediaType);
	}

	/**
	 * Get the content root of a text document. Start here to get or create new
	 * elements of a text document like paragraphs, headings, tables or lists.
	 *
	 * @return content root, representing the office:text tag
	 * @throws Exception if the file DOM could not be created.
	 */
	@Override
	public OfficeTextElement getContentRoot() throws Exception {
		return super.getContentRoot(OfficeTextElement.class);
	}

	/**
	 * Creates a new paragraph and append text
	 *
	 * @param text
	 * @return the new paragraph
	 * @throws Exception if the file DOM could not be created.
	 */
	public OdfTextParagraph newParagraph(String text) throws Exception {
		OdfTextParagraph para = newParagraph();
		para.addContent(text);
		return para;
	}

	/**
	 * Creates a new paragraph
	 *
	 * @return The new paragraph
	 * @throws Exception if the file DOM could not be created.
	 */
	public OdfTextParagraph newParagraph() throws Exception {
		OfficeTextElement odfText = getContentRoot();
		return (OdfTextParagraph) odfText.newTextPElement();
	}

	/**
	 * Append text to the end of a text document. If there is no paragraph at
	 * the end of a document, a new one will be created.
	 *
	 * @param text initial text for the paragraph.
	 * @return The paragraph at the end of the text document, where the text has
	 * been added to.
	 * @throws Exception if the file DOM could not be created.
	 */
	public OdfTextParagraph addText(String text) throws Exception {
		OfficeTextElement odfText = getContentRoot();
		Node n = odfText.getLastChild();
		OdfTextParagraph para;
		if (OdfTextParagraph.class.isInstance(n)) {
			para = (OdfTextParagraph) n;
		} else {
			para = newParagraph();
		}
		para.addContent(text);
		return para;
	}

	/**
	 * Changes the document to the given mediatype. This method can only be used
	 * to convert a document to a related mediatype, e.g. template.
	 *
	 * @param mediaType the related ODF mimetype
	 */
	public void changeMode(OdfMediaType mediaType) {
		setOdfMediaType(mediaType.mMediaType);
	}

	/**
	 * In LO/AO Tabs are counted from the start of text and the fo:left-margin
	 * have to be added. Microsoft Office does not behave this way. For the
	 * filter a compatibility flag was added to the settings.xml
	 * <config:config-item config:name="TabsRelativeToIndent"
	 * config:type="boolean">false</config:config-item>
	 */
	public Boolean hasTabsRelativeToIndent() {
		if (hasTabsRelativeToIndent == null) {
			try {
				OdfSettingsDom settingsDom = this.getSettingsDom();
				// if there is a settings.xml in the document..
				if (settingsDom != null) {
					// 1) Find the correct collection "ooo:configuration-settings"
					NodeList configItemSets = settingsDom.getRootElement().getElementsByTagNameNS("urn:oasis:names:tc:opendocument:xmlns:config:1.0", "config-item-set");
					Element configElement = null;
					for (int i = 0; i < configItemSets.getLength(); i++) {
						Node config = configItemSets.item(i);
						if (config instanceof Element) {
							configElement = (Element) config;
							if (configElement.getLocalName().equals("config-item-set") && configElement.getAttributeNS("urn:oasis:names:tc:opendocument:xmlns:config:1.0", "name").equals("ooo:configuration-settings")) {
								break;
							} else {
								configElement = null;
							}
						}
					}
					// 1) Within the collection find the correct configuration "ooo:configuration-settings"
					if (configElement != null) {
						NodeList configItems = configElement.getElementsByTagNameNS("urn:oasis:names:tc:opendocument:xmlns:config:1.0", "config-item");
						for (int i = 0; i < configItems.getLength(); i++) {
							Node config = configItems.item(i);
							if (config instanceof Element) {
								configElement = (Element) config;
								if (configElement.getLocalName().equals("config-item") && configElement.getAttributeNS("urn:oasis:names:tc:opendocument:xmlns:config:1.0", "name").equals("TabsRelativeToIndent")) {
									Node child = configElement.getFirstChild();
									if (child instanceof Text) {
										hasTabsRelativeToIndent = ((Text) child).getTextContent().equals("true");
									}
									break;
								}
							}
						}
					}
				}
			} catch (Exception ex) {
				Logger.getLogger(OdfTextDocument.class.getName()).log(Level.SEVERE, null, ex);
			}
			// there is nothing set, use the LO/AO default behavior
			if(hasTabsRelativeToIndent == null){
				hasTabsRelativeToIndent = true;
			}
		}
		return hasTabsRelativeToIndent;
	}
}
