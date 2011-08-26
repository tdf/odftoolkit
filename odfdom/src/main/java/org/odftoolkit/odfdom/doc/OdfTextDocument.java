/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 * Copyright 2009 IBM. All rights reserved.
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
package org.odftoolkit.odfdom.doc;

import org.odftoolkit.odfdom.doc.office.OdfOfficeText;
import org.odftoolkit.odfdom.doc.text.OdfTextParagraph;
import org.w3c.dom.Node;

/**
 * This class represents an empty ODF text document file.
 * Note: The way of receiving a new empty OdfTextDocument will probably change. 
 * In the future the streams and DOM representation of an OpenDocument file will
 * be clonable and this stream buffering will be neglected.
 * 
 */
public class OdfTextDocument extends OdfDocument {

	private static String EMPTY_TEXT_DOCUMENT_PATH = "/OdfTextDocument.odt";
	private static Resource EMPTY_TEXT_DOCUMENT_RESOURCE = new Resource(EMPTY_TEXT_DOCUMENT_PATH);

	/**
	 * This enum contains all possible media types of OdfSpreadsheetDocument
	 * documents.
	 */
	public enum SupportedType {

		TEXT(OdfMediaType.TEXT),
		TEXT_TEMPLATE(OdfMediaType.TEXT_TEMPLATE),
		TEXT_MASTER(OdfMediaType.TEXT_MASTER),
		TEXT_WEB(OdfMediaType.TEXT_WEB);
		private final OdfMediaType mMediaType;

		SupportedType(OdfMediaType mediaType) {
			this.mMediaType = mediaType;
		}

		public OdfMediaType getOdfMediaType() {
			return mMediaType;
		}

		@Override
		public String toString() {
			return mMediaType.toString();
		}
	}

	/**
	 * Creates an empty text document.
	 * @return ODF text document based on a default template
	 * @throws java.lang.Exception - if the document could not be created
	 */
	public static OdfTextDocument newTextDocument() throws Exception {
		return (OdfTextDocument) OdfDocument.loadTemplate(EMPTY_TEXT_DOCUMENT_RESOURCE);
	}

	/**
	 * Creates an empty text template.
	 * @return ODF text template based on a default
	 * @throws java.lang.Exception - if the template could not be created
	 */
	public static OdfTextDocument newTextTemplateDocument() throws Exception {
		OdfTextDocument doc = (OdfTextDocument) OdfDocument.loadTemplate(EMPTY_TEXT_DOCUMENT_RESOURCE);
		doc.changeMode(SupportedType.TEXT_TEMPLATE);
		return doc;
	}

	/**
	 * Creates an empty text master document.
	 * @return ODF text master based on a default
	 * @throws java.lang.Exception - if the document could not be created
	 */
	public static OdfTextDocument newTextMasterDocument() throws Exception {
		OdfTextDocument doc = (OdfTextDocument) OdfDocument.loadTemplate(EMPTY_TEXT_DOCUMENT_RESOURCE);
		doc.changeMode(SupportedType.TEXT_MASTER);
		return doc;
	}

	/**
	 * Creates an empty text web.
	 * @return ODF text web based on a default
	 * @throws java.lang.Exception - if the document could not be created
	 */
	public static OdfTextDocument newTextWebDocument() throws Exception {
		OdfTextDocument doc = (OdfTextDocument) OdfDocument.loadTemplate(EMPTY_TEXT_DOCUMENT_RESOURCE);
		doc.changeMode(SupportedType.TEXT_WEB);
		return doc;
	}

	// Using static factory instead of constructor
	protected OdfTextDocument() {
	}

	/**
	 * Get the content root of a text document. Start here to get or create new
	 * elements of a text document like paragraphs, headings, tables or lists.
	 *
	 * @return content root, representing the office:text tag
	 * @throws Exception if the file DOM could not be created.
	 */
	public OdfOfficeText getContentRoot() throws Exception {
		return super.getContentRoot(OdfOfficeText.class);
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
		OdfOfficeText odfText = getContentRoot();
		return (OdfTextParagraph) odfText.newTextPElement();
	}

	/**
	 * Append text to the end of a text document.
	 * If there is no paragraph at the end of a document, a new one will be created.
	 *
	 * @param text initial text for the paragraph.
	 * @return The paragraph at the end of the text document, where the text has been added to.
	 * @throws Exception if the file DOM could not be created.
	 */
	public OdfTextParagraph addText(String text) throws Exception {
		OdfOfficeText odfText = getContentRoot();
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
	 * Switches this instance to the given type. This method can be used to e.g.
	 * convert a document instance to a template and vice versa. 	 * 
	 * @param type
	 */
	public void changeMode(SupportedType type) {
		setMediaType(type.getOdfMediaType());
		getPackage().setMediaType(type.toString());
	}
}
