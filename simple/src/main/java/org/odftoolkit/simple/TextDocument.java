/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 * Copyright 2009, 2010 IBM. All rights reserved.
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
package org.odftoolkit.simple;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.odftoolkit.odfdom.dom.element.office.OfficeMasterStylesElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeTextElement;
import org.odftoolkit.odfdom.dom.element.style.StyleFooterElement;
import org.odftoolkit.odfdom.dom.element.style.StyleHeaderElement;
import org.odftoolkit.odfdom.dom.element.style.StyleMasterPageElement;
import org.odftoolkit.odfdom.dom.element.text.TextSectionElement;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.pkg.MediaType;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.simple.text.Footer;
import org.odftoolkit.simple.text.Header;
import org.odftoolkit.simple.text.Section;
import org.odftoolkit.simple.text.list.AbstractListContainer;
import org.odftoolkit.simple.text.list.List;
import org.odftoolkit.simple.text.list.ListContainer;
import org.odftoolkit.simple.text.list.ListDecorator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class represents an empty ODF text document.
 * 
 */
public class TextDocument extends Document implements ListContainer {

	private static final String EMPTY_TEXT_DOCUMENT_PATH = "/OdfTextDocument.odt";
	static final Resource EMPTY_TEXT_DOCUMENT_RESOURCE = new Resource(EMPTY_TEXT_DOCUMENT_PATH);

	private ListContainerImpl listContainerImpl;

	private Header header;
	private Footer footer;

	/**
	 * This enum contains all possible media types of SpreadsheetDocument
	 * documents.
	 */
	public enum OdfMediaType implements MediaType {

		TEXT(Document.OdfMediaType.TEXT), TEXT_TEMPLATE(Document.OdfMediaType.TEXT_TEMPLATE), TEXT_MASTER(
				Document.OdfMediaType.TEXT_MASTER), TEXT_WEB(Document.OdfMediaType.TEXT_WEB);
		private final Document.OdfMediaType mMediaType;

		OdfMediaType(Document.OdfMediaType mediaType) {
			this.mMediaType = mediaType;
		}

		/**
		 * @return the mediatype of this document
		 */
		public String getMediaTypeString() {
			return mMediaType.getMediaTypeString();
		}

		/**
		 * @return the ODF filesuffix of this document
		 */
		public String getSuffix() {
			return mMediaType.getSuffix();
		}

		/**
		 * 
		 * @param mediaType
		 *            string defining an ODF document
		 * @return the according OdfMediatype encapuslating the given string and
		 *         the suffix
		 */
		public static Document.OdfMediaType getOdfMediaType(String mediaType) {
			return Document.OdfMediaType.getOdfMediaType(mediaType);
		}
	}

	/**
	 * Creates an empty text document.
	 * 
	 * @return ODF text document based on a default template
	 * @throws java.lang.Exception
	 *             - if the document could not be created
	 */
	public static TextDocument newTextDocument() throws Exception {
		return (TextDocument) Document.loadTemplate(EMPTY_TEXT_DOCUMENT_RESOURCE, Document.OdfMediaType.TEXT);
	}

	/**
	 * Creates an empty text document.
	 * 
	 * @return ODF text document based on a default template
	 * @throws java.lang.Exception
	 *             - if the document could not be created
	 */
	public static TextDocument newTextDocument(TextDocument.OdfMediaType mimeType) throws Exception {
		return (TextDocument) Document.loadTemplate(EMPTY_TEXT_DOCUMENT_RESOURCE, Document.OdfMediaType.TEXT);
	}

	/**
	 * Creates an empty text template.
	 * 
	 * @return ODF text template based on a default
	 * @throws java.lang.Exception
	 *             - if the template could not be created
	 */
	public static TextDocument newTextTemplateDocument() throws Exception {
		return (TextDocument) Document.loadTemplate(EMPTY_TEXT_DOCUMENT_RESOURCE, Document.OdfMediaType.TEXT_TEMPLATE);
	}

	/**
	 * Creates an empty text master document.
	 * 
	 * @return ODF text master based on a default
	 * @throws java.lang.Exception
	 *             - if the document could not be created
	 */
	public static TextDocument newTextMasterDocument() throws Exception {
		TextDocument doc = (TextDocument) Document.loadTemplate(EMPTY_TEXT_DOCUMENT_RESOURCE,
				Document.OdfMediaType.TEXT_MASTER);
		doc.changeMode(OdfMediaType.TEXT_MASTER);
		return doc;
	}

	/**
	 * Creates an empty text web.
	 * 
	 * @return ODF text web based on a default
	 * @throws java.lang.Exception
	 *             - if the document could not be created
	 */
	public static TextDocument newTextWebDocument() throws Exception {
		TextDocument doc = (TextDocument) Document.loadTemplate(EMPTY_TEXT_DOCUMENT_RESOURCE,
				Document.OdfMediaType.TEXT_WEB);
		doc.changeMode(OdfMediaType.TEXT_WEB);
		return doc;
	}

	/**
	 * Creates an TextDocument from the OpenDocument provided by a resource
	 * Stream.
	 * 
	 * <p>
	 * Since an InputStream does not provide the arbitrary (non sequentiell)
	 * read access needed by TextDocument, the InputStream is cached. This
	 * usually takes more time compared to the other createInternalDocument
	 * methods. An advantage of caching is that there are no problems
	 * overwriting an input file.
	 * </p>
	 * 
	 * <p>
	 * If the resource stream is not a ODF text document, ClassCastException
	 * might be thrown.
	 * </p>
	 * 
	 * @param inputStream
	 *            - the InputStream of the ODF text document.
	 * @return the text document created from the given InputStream
	 * @throws java.lang.Exception
	 *             - if the document could not be created.
	 */
	public static TextDocument loadDocument(InputStream inputStream) throws Exception {
		return (TextDocument) Document.loadDocument(inputStream);
	}

	/**
	 * Loads an TextDocument from the provided path.
	 * 
	 * <p>
	 * TextDocument relies on the file being available for read access over the
	 * whole lifecycle of TextDocument.
	 * </p>
	 * 
	 * <p>
	 * If the resource stream is not a ODF text document, ClassCastException
	 * might be thrown.
	 * </p>
	 * 
	 * @param documentPath
	 *            - the path from where the document can be loaded
	 * @return the text document from the given path or NULL if the media type
	 *         is not supported by SIMPLE.
	 * @throws java.lang.Exception
	 *             - if the document could not be created.
	 */
	public static TextDocument loadDocument(String documentPath) throws Exception {
		return (TextDocument) Document.loadDocument(documentPath);
	}

	/**
	 * Creates an TextDocument from the OpenDocument provided by a File.
	 * 
	 * <p>
	 * TextDocument relies on the file being available for read access over the
	 * whole lifecycle of TextDocument.
	 * </p>
	 * 
	 * <p>
	 * If the resource stream is not a ODF text document, ClassCastException
	 * might be thrown.
	 * </p>
	 * 
	 * @param file
	 *            - a file representing the ODF text document.
	 * @return the text document created from the given File
	 * @throws java.lang.Exception
	 *             - if the document could not be created.
	 */
	public static TextDocument loadDocument(File file) throws Exception {
		return (TextDocument) Document.loadDocument(file);
	}

	/**
	 * To avoid data duplication a new document is only created, if not already
	 * opened. A document is cached by this constructor using the internalpath
	 * as key.
	 */
	protected TextDocument(OdfPackage pkg, String internalPath, TextDocument.OdfMediaType odfMediaType) {
		super(pkg, internalPath, odfMediaType.mMediaType);
	}

	/**
	 * Get the content root of a text document. Start here to get or create new
	 * elements of a text document like paragraphs, headings, tables or lists.
	 * 
	 * @return content root, representing the office:text tag
	 * @throws Exception
	 *             if the file DOM could not be created.
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
	 * @throws Exception
	 *             if the file DOM could not be created.
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
	 * @throws Exception
	 *             if the file DOM could not be created.
	 */
	public OdfTextParagraph newParagraph() throws Exception {
		OfficeTextElement odfText = getContentRoot();
		return (OdfTextParagraph) odfText.newTextPElement();
	}

	/**
	 * Append text to the end of a text document. If there is no paragraph at
	 * the end of a document, a new one will be created.
	 * 
	 * @param text
	 *            initial text for the paragraph.
	 * @return The paragraph at the end of the text document, where the text has
	 *         been added to.
	 * @throws Exception
	 *             if the file DOM could not be created.
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
	 * @param mediaType
	 *            the related ODF mimetype
	 */
	public void changeMode(OdfMediaType mediaType) {
		setOdfMediaType(mediaType.mMediaType);
	}

	/**
	 * Copy a section and append it at the end of the text document, whether the
	 * section is in this document or in a different document.
	 * <p>
	 * The IDs and names in this section would be changed to ensure unique.
	 * <p>
	 * If the section contains a linked resource, <code>isResourceCopied</code>
	 * would specify whether the linked resource would be copied or not, when
	 * the copy and append happens within a same document.
	 * 
	 * @param section
	 *            - the section object
	 * @param isResourceCopied
	 *            - whether the linked resource is copied or not.
	 */
	public Section appendSection(Section section, boolean isResourceCopied) {
		boolean isForeignNode = false;
		try {
			if (section.getOdfElement().getOwnerDocument() != getContentDom())
				isForeignNode = true;
			TextSectionElement oldSectionEle = section.getOdfElement();
			TextSectionElement newSectionEle = (TextSectionElement) oldSectionEle.cloneNode(true);

			if (isResourceCopied || isForeignNode)
				copyLinkedRef(newSectionEle);
			if (isForeignNode)
				copyForeignStyleRef(newSectionEle, section.getOwnerDocument());
			if (isForeignNode) // not in a same document
				newSectionEle = (TextSectionElement) cloneForeignElement(newSectionEle, getContentDom(), true);

			updateNames(newSectionEle);
			updateXMLIds(newSectionEle);
			OfficeTextElement contentRoot = getContentRoot();
			contentRoot.appendChild(newSectionEle);
			return Section.getInstance(newSectionEle);
		} catch (Exception e) {
			Logger.getLogger(TextDocument.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}
	
	/**
	 * Get the header of this text document.
	 * 
	 * @return the header of this text document.
	 * @since 0.4.5
	 */
	public Header getHeader() {
		if (header == null) {
			try {
				StyleMasterPageElement masterPageElement = getMasterPages().get("Standard");
				StyleHeaderElement headerElement = OdfElement.findFirstChildNode(StyleHeaderElement.class,
						masterPageElement);
				if (headerElement == null) {
					headerElement = masterPageElement.newStyleHeaderElement();
				}
				header = new Header(headerElement);
			} catch (Exception e) {
				Logger.getLogger(TextDocument.class.getName()).log(Level.SEVERE, null, e);
			}
		}
		return header;
	}

	/**
	 * Get the footer of this text document.
	 * 
	 * @return the footer of this text document.
	 * @since 0.4.5
	 */
	public Footer getFooter() {
		if (footer == null) {
			try {
				StyleMasterPageElement masterPageElement = getMasterPages().get("Standard");
				StyleFooterElement footerElement = OdfElement.findFirstChildNode(StyleFooterElement.class,
						masterPageElement);
				if (footerElement == null) {
					footerElement = masterPageElement.newStyleFooterElement();
				}
				footer = new Footer(footerElement);
			} catch (Exception e) {
				Logger.getLogger(TextDocument.class.getName()).log(Level.SEVERE, null, e);
			}
		}
		return footer;
	}
	
	public OdfElement getTableContainerElement() {
		return getTableContainerImpl().getTableContainerElement();
	}

	public OdfElement getListContainerElement() {
		return getListContainerImpl().getListContainerElement();
	}

	public List addList() {
		return getListContainerImpl().addList();
	}

	public List addList(ListDecorator decorator) {
		return getListContainerImpl().addList(decorator);
	}

	public void clearList() {
		getListContainerImpl().clearList();
	}

	public Iterator<List> getListIterator() {
		return getListContainerImpl().getListIterator();
	}

	public boolean removeList(List list) {
		return getListContainerImpl().removeList(list);
	}

	private ListContainerImpl getListContainerImpl() {
		if (listContainerImpl == null) {
			listContainerImpl = new ListContainerImpl();
		}
		return listContainerImpl;
	}

	private class ListContainerImpl extends AbstractListContainer {

		public OdfElement getListContainerElement() {
			OdfElement containerElement = null;
			try {
				containerElement = getContentRoot();
			} catch (Exception e) {
				Logger.getLogger(TextDocument.class.getName()).log(Level.SEVERE, null, e);
			}
			return containerElement;
		}
	}
	
	private Map<String, StyleMasterPageElement> getMasterPages() throws Exception {
		OdfStylesDom stylesDoc = getStylesDom();
		OfficeMasterStylesElement masterStyles = OdfElement.findFirstChildNode(OfficeMasterStylesElement.class,
				stylesDoc.getRootElement());
		Map<String, StyleMasterPageElement> masterPages = null;
		if (masterStyles != null) {
			NodeList lstMasterPages = stylesDoc.getElementsByTagNameNS(OdfDocumentNamespace.STYLE.getUri(),
					"master-page");
			if (lstMasterPages != null && lstMasterPages.getLength() > 0) {
				masterPages = new HashMap<String, StyleMasterPageElement>();
				for (int i = 0; i < lstMasterPages.getLength(); i++) {
					StyleMasterPageElement masterPage = (StyleMasterPageElement) lstMasterPages.item(i);
					String styleName = masterPage.getStyleNameAttribute();
					masterPages.put(styleName, masterPage);
				}
			}
		}
		return masterPages;
	}

}
