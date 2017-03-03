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
package org.odftoolkit.simple;

import java.awt.Rectangle;
import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.attribute.text.TextAnchorTypeAttribute;
import org.odftoolkit.odfdom.dom.element.draw.DrawControlElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeDocumentStylesElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeFormsElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeMasterStylesElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeTextElement;
import org.odftoolkit.odfdom.dom.element.style.StyleColumnsElement;
import org.odftoolkit.odfdom.dom.element.style.StyleFooterElement;
import org.odftoolkit.odfdom.dom.element.style.StyleHeaderElement;
import org.odftoolkit.odfdom.dom.element.style.StyleMasterPageElement;
import org.odftoolkit.odfdom.dom.element.style.StylePageLayoutPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleSectionPropertiesElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.text.TextIndexBodyElement;
import org.odftoolkit.odfdom.dom.element.text.TextIndexEntryLinkStartElement;
import org.odftoolkit.odfdom.dom.element.text.TextIndexEntryTabStopElement;
import org.odftoolkit.odfdom.dom.element.text.TextIndexSourceStylesElement;
import org.odftoolkit.odfdom.dom.element.text.TextIndexTitleElement;
import org.odftoolkit.odfdom.dom.element.text.TextIndexTitleTemplateElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.odftoolkit.odfdom.dom.element.text.TextSectionElement;
import org.odftoolkit.odfdom.dom.element.text.TextSequenceDeclsElement;
import org.odftoolkit.odfdom.dom.element.text.TextTableOfContentElement;
import org.odftoolkit.odfdom.dom.element.text.TextTableOfContentEntryTemplateElement;
import org.odftoolkit.odfdom.dom.element.text.TextTableOfContentSourceElement;
import org.odftoolkit.odfdom.dom.element.text.TextTocMarkElement;
import org.odftoolkit.odfdom.dom.element.text.TextTocMarkStartElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeMasterStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStylePageLayout;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.pkg.MediaType;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.type.CellRangeAddressList;
import org.odftoolkit.odfdom.type.Length.Unit;
import org.odftoolkit.simple.chart.AbstractChartContainer;
import org.odftoolkit.simple.chart.Chart;
import org.odftoolkit.simple.chart.ChartContainer;
import org.odftoolkit.simple.chart.DataSet;
import org.odftoolkit.simple.common.field.AbstractVariableContainer;
import org.odftoolkit.simple.common.field.VariableContainer;
import org.odftoolkit.simple.common.field.VariableField;
import org.odftoolkit.simple.common.field.VariableField.VariableType;
import org.odftoolkit.simple.draw.Control;
import org.odftoolkit.simple.draw.ControlContainer;
import org.odftoolkit.simple.form.AbstractFormContainer;
import org.odftoolkit.simple.form.Form;
import org.odftoolkit.simple.form.FormContainer;
import org.odftoolkit.simple.style.MasterPage;
import org.odftoolkit.simple.style.TOCStyle;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.text.AbstractParagraphContainer;
import org.odftoolkit.simple.text.Footer;
import org.odftoolkit.simple.text.Header;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.text.ParagraphContainer;
import org.odftoolkit.simple.text.Section;
import org.odftoolkit.simple.text.list.AbstractListContainer;
import org.odftoolkit.simple.text.list.List;
import org.odftoolkit.simple.text.list.ListContainer;
import org.odftoolkit.simple.text.list.ListDecorator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * This class represents an empty ODF text document.
 * 
 */
public class TextDocument extends Document implements ListContainer,
		ParagraphContainer, VariableContainer, ChartContainer, FormContainer,
		ControlContainer {

	private static final String EMPTY_TEXT_DOCUMENT_PATH = "/OdfTextDocument.odt";
	static final Resource EMPTY_TEXT_DOCUMENT_RESOURCE = new Resource(EMPTY_TEXT_DOCUMENT_PATH);

	private ListContainerImpl listContainerImpl;
	private ParagraphContainerImpl paragraphContainerImpl;
	private VariableContainerImpl variableContainerImpl;
	private ChartContainerImpl chartContainerImpl;
	private FormContainerImpl formContainerImpl = null;
	
	private Header firstPageHeader;
	private Header standardHeader;

	private Footer firstPageFooter;
	private Footer standardFooter;

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
	 * @deprecated As of Simple version 0.5, replaced by
	 *             <code>addParagraph(String text)</code>
	 * @see #addParagraph(String)
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
	 * @deprecated As of Simple version 0.5, replaced by
	 *             <code>Paragraph.newParagraph(ParagraphContainer)</code>
	 * @see Paragraph#newParagraph(ParagraphContainer)
	 * @see #addParagraph(String)
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
	 * @deprecated As of Simple version 0.5, replaced by
	 *             <code>Paragraph.appendTextContent(String content)</code>
	 * @see Paragraph#appendTextContent(String)
	 * @see Paragraph#appendTextContentNotCollapsed(String)
	 * @see #getParagraphByIndex(int, boolean)
	 * @see #getParagraphByReverseIndex(int, boolean)
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
				copyLinkedRefInBatch(newSectionEle, section.getOwnerDocument());
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
	 * Create an empty section and append it at the end of the text document.
	 * 
	 * @param name
	 *            - specify the section name
	 * @return an instance of the section
	 * @throws RuntimeException
	 *             if content DOM could not be initialized
	 */
	public Section appendSection(String name) {
		TextSectionElement newSectionEle = null;
		try {
			OdfContentDom contentDocument = getContentDom();
			OdfOfficeAutomaticStyles styles = contentDocument
					.getAutomaticStyles();
			OdfStyle style = styles.newStyle(OdfStyleFamily.Section);
			StyleSectionPropertiesElement sProperties = style
					.newStyleSectionPropertiesElement();
			sProperties.setTextDontBalanceTextColumnsAttribute(false);
			sProperties.setStyleEditableAttribute(false);
			StyleColumnsElement columnEle = sProperties
					.newStyleColumnsElement(1);
			columnEle.setFoColumnGapAttribute("0in");

			newSectionEle = getContentRoot()
					.newTextSectionElement("true", name);
			newSectionEle.setStyleName(style.getStyleNameAttribute());
			return Section.getInstance(newSectionEle);

		} catch (Exception e) {
			Logger.getLogger(TextDocument.class.getName()).log(Level.SEVERE,
					null, e);
			throw new RuntimeException(name + "Section appends failed.", e);
		}
	}

	/**
	 * Get the Standard Page header of this text document.
	 * 
	 * @return the Standard Page header of this text document.
	 * @since 0.4.5
	 */
	public Header getHeader() {
		return getHeader(false);
	}

	/**
	 * Get the header of this text document.
	 * 
	 * @param isFirstPage
	 *            if <code>isFirstPage</code> is true, return the First Page
	 *            header, otherwise return Standard Page header.
	 * 
	 * @return the header of this text document.
	 * @since 0.5
	 */
	public Header getHeader(boolean isFirstPage) {
		Header tmpHeader = isFirstPage ? firstPageHeader : standardHeader;
		if (tmpHeader == null) {
			try {
				StyleMasterPageElement masterPageElement = getMasterPage(isFirstPage);
				StyleHeaderElement headerElement = OdfElement.findFirstChildNode(StyleHeaderElement.class,
						masterPageElement);
				if (headerElement == null) {
					headerElement = masterPageElement.newStyleHeaderElement();
				}
				tmpHeader = new Header(headerElement);
			} catch (Exception e) {
				Logger.getLogger(TextDocument.class.getName()).log(Level.SEVERE, null, e);
			}
			if (isFirstPage) {
				firstPageHeader = tmpHeader;
			} else {
				standardHeader = tmpHeader;
			}
		}
		return tmpHeader;
	}

	/**
	 * Get the Standard Page footer of this text document.
	 * 
	 * @return the Standard Page footer of this text document.
	 * @since 0.4.5
	 */
	public Footer getFooter() {
		return getFooter(false);
	}

	/**
	 * Get the footer of this text document.
	 * 
	 * @param isFirstPage
	 *            if <code>isFirstPage</code> is true, return the First Page
	 *            footer, otherwise return Standard Page footer.
	 * 
	 * @return the footer of this text document.
	 * @since 0.5
	 */
	public Footer getFooter(boolean isFirstPage) {
		Footer tmpFooter = isFirstPage ? firstPageFooter : standardFooter;
		if (tmpFooter == null) {
			try {
				StyleMasterPageElement masterPageElement = getMasterPage(isFirstPage);
				StyleFooterElement footerElement = OdfElement.findFirstChildNode(StyleFooterElement.class,
						masterPageElement);
				if (footerElement == null) {
					footerElement = masterPageElement.newStyleFooterElement();
				}
				tmpFooter = new Footer(footerElement);
			} catch (Exception e) {
				Logger.getLogger(TextDocument.class.getName()).log(Level.SEVERE, null, e);
			}
			if (isFirstPage) {
				firstPageFooter = tmpFooter;
			} else {
				standardFooter = tmpFooter;
			}
		}
		return tmpFooter;
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
	
	/**
	 * Appends a new page break to this document.
	 * 
	 * @since 0.6.5
	 */
	public void addPageBreak() {
		addPageOrColumnBreak(null, "page");
	}
	
	/**
	 * Appends a new page break to this document after the reference paragraph.
	 * 
	 * @param refParagraph
	 *            the reference paragraph after where the page break inserted.
	 * @since 0.6.5
	 */
	public void addPageBreak(Paragraph refParagraph) {
		addPageOrColumnBreak(refParagraph, "page");
	}
	
	/** 
	 * Defines several columns to the page whose style is specified.
	 * 
	 * @param columnsNumber
	 * 			the number of columns (are of width identical)
	 * @param spacing
	 * 			column spacing in cm (ex. 2.40 for 2,4 cm)
	 * 
	 * @since 0.6.6
	 */
	public void setPageColumns(int columnsNumber, double spacing) {
		String vSpacingColumn = (new DecimalFormat("#0.###").format(spacing) + Unit.CENTIMETER.abbr()).replace(",", ".");
		// Get back the name of the style Page wanted Layout
		// (Example of the got back name : pm1 or Mpm1 for the standard style)
		try {
			String stylePageLayoutName = null;
			int pageLayoutNameCount = 0;
			NodeList list = getStylesDom().getElementsByTagName("office:master-styles");
			if (list.getLength() > 0) {
				OdfOfficeMasterStyles officeMasterStyles = (OdfOfficeMasterStyles) list.item(0);
				// Get back the StylePageLayoutName
				for (int i = 0; i < officeMasterStyles.getLength(); i++) {
					StyleMasterPageElement syleMasterPage = (StyleMasterPageElement) officeMasterStyles.item(i);
					if(syleMasterPage.getStyleNameAttribute().equals("Standard")){					
						stylePageLayoutName = syleMasterPage.getStylePageLayoutNameAttribute();
						break;
					}
				}
				// Allows to know if StylePageLayoutName is unique
				for (int i = 0; i < officeMasterStyles.getLength(); i++) {
					StyleMasterPageElement syleMasterPage = (StyleMasterPageElement) officeMasterStyles.item(i);
					if(syleMasterPage.getStylePageLayoutNameAttribute().equals(stylePageLayoutName)){					
						pageLayoutNameCount++;
					}
				}
			}
			
			OdfOfficeAutomaticStyles autoStyles = getStylesDom().getAutomaticStyles();
			int autoStylesCount = autoStyles.getLength();			
			OdfStylePageLayout pageLayout = autoStyles.getPageLayout(stylePageLayoutName);
			if(pageLayout != null) {
				// Clone the OdfStylePageLayout if another master style possesses the same name before modifying its properties
				if(pageLayoutNameCount > 1){
					Node pageLayoutNew = pageLayout.cloneNode(true);					
					// Rename the style of the clone before modifying its properties
					String oldPageLayoutName = pageLayout.getStyleNameAttribute();
					pageLayout.setStyleNameAttribute("Mpm" + (autoStylesCount+1));
					// Allocate the new name of the style to the master style (the cloned style)
					if (list.getLength() > 0) {
						OdfOfficeMasterStyles masterpage = (OdfOfficeMasterStyles) list.item(0);
						for (int i = 0; i < masterpage.getLength(); i++) {
							StyleMasterPageElement vSyleMasterPage = (StyleMasterPageElement) masterpage.item(i);
							if(vSyleMasterPage.getStyleNameAttribute().equals("Standard")){
								if(vSyleMasterPage.getStylePageLayoutNameAttribute().equals(oldPageLayoutName)){					
									vSyleMasterPage.setStylePageLayoutNameAttribute(pageLayout.getStyleNameAttribute());
								}
							}
						}
					}
					autoStyles.appendChild(pageLayoutNew);
				}
				NodeList vListStlePageLprop = pageLayout.getElementsByTagName("style:page-layout-properties");
				StylePageLayoutPropertiesElement vStlePageLprop = (StylePageLayoutPropertiesElement) vListStlePageLprop.item(0);
				StyleColumnsElement vStyleColumnsElement = vStlePageLprop.newStyleColumnsElement(columnsNumber);
				vStyleColumnsElement.setFoColumnGapAttribute(vSpacingColumn);
			}
		} catch (Exception e) {
			Logger.getLogger(TextDocument.class.getName()).log(Level.SEVERE, null, e);
			throw new RuntimeException("Page column sets failed.", e);
		}		
	}
	
	/**
	 * Appends a new column break to this document.
	 * 
	 * @since 0.6.6
	 */
	public void addColumnBreak() {
		addPageOrColumnBreak(null, "column");
	}
	
	/**
	 * Appends a new column break to this document after the reference paragraph.
	 * 
	 * @param refParagraph
	 *            the reference paragraph after where the column break inserted.
	 * @since 0.6.6
	 */
	public void addColumnBreak(Paragraph refParagraph) {
		addPageOrColumnBreak(refParagraph, "column");
	}
	
	/** 
	 * Appends a new column or page break to this document.
	 * 
	 * @param refParagraph
	 * 			the reference paragraph after where the column break inserted.
	 * @param breakAttribute
	 * 			the attribute name (page or column)
	 */
	private void addPageOrColumnBreak(Paragraph refParagraph, String breakAttribute) {
		TextPElement pEle = null;
		try {
			OdfContentDom contentDocument = getContentDom();
			OdfOfficeAutomaticStyles styles = contentDocument.getAutomaticStyles();
			OdfStyle style = styles.newStyle(OdfStyleFamily.Paragraph);
			style.newStyleParagraphPropertiesElement().setFoBreakBeforeAttribute(breakAttribute);
			if(refParagraph == null){
				pEle = getContentRoot().newTextPElement();
			} else {
				OfficeTextElement contentRoot = getContentRoot();
				pEle = contentRoot.newTextPElement();
				OdfElement refEle = refParagraph.getOdfElement();
				contentRoot.insertBefore(pEle, refEle.getNextSibling());
			}
			pEle.setStyleName(style.getStyleNameAttribute());
		} catch (Exception e) {
			Logger.getLogger(TextDocument.class.getName()).log(Level.SEVERE, null, e);
			throw new RuntimeException(breakAttribute + "Break appends failed.", e);
		}
	}

	/**
	 * Appends a new page break to this document after the reference paragraph,
	 * and the master page style will be applied to the new page.
	 * 
	 * @param refParagraph
	 *            the reference paragraph after where the page break inserted.
	 * @param master
	 *            the master page style applied to the new page.
	 * @since 0.8
	 */

	public void addPageBreak(Paragraph refParagraph, MasterPage master) {
		TextPElement pEle = null;
		try {
			OdfContentDom contentDocument = getContentDom();
			OdfOfficeAutomaticStyles styles = contentDocument
					.getAutomaticStyles();
			OdfStyle style = styles.newStyle(OdfStyleFamily.Paragraph);
			style.setStyleMasterPageNameAttribute(master.getName());

			if (refParagraph == null) {
				pEle = getContentRoot().newTextPElement();
			} else {
				OfficeTextElement contentRoot = getContentRoot();
				pEle = contentRoot.newTextPElement();
				OdfElement refEle = refParagraph.getOdfElement();
				contentRoot.insertBefore(pEle, refEle.getNextSibling());
			}
			pEle.setStyleName(style.getStyleNameAttribute());
		} catch (Exception e) {
			Logger.getLogger(TextDocument.class.getName()).log(Level.SEVERE,
					null, e);
			throw new RuntimeException("PageBreak with mater page - "
					+ master.getName() + " - appends failed.", e);
		}
	}

	/**
	 * Creates a new paragraph and append text.
	 * 
	 * @param text
	 *            the text content of this paragraph
	 * @return the new paragraph
	 */
	public Paragraph addParagraph(String text) {
		Paragraph para = getParagraphContainerImpl().addParagraph(text);
		return para;
	}

	/**
	 * Remove paragraph from this document
	 * 
	 * @param para
	 *            the instance of paragraph
	 * @return true if the paragraph is removed successfully, false if errors
	 *         happen.
	 */
	public boolean removeParagraph(Paragraph para) {
		return getParagraphContainerImpl().removeParagraph(para);
	}

	public OdfElement getParagraphContainerElement() {
		return getParagraphContainerImpl().getParagraphContainerElement();
	}

	public Paragraph getParagraphByIndex(int index, boolean isEmptyParagraphSkipped) {
		return getParagraphContainerImpl().getParagraphByIndex(index, isEmptyParagraphSkipped);
	}

	public Paragraph getParagraphByReverseIndex(int reverseIndex, boolean isEmptyParagraphSkipped) {
		return getParagraphContainerImpl().getParagraphByReverseIndex(reverseIndex, isEmptyParagraphSkipped);
	}

	public Iterator<Paragraph> getParagraphIterator() {
		return getParagraphContainerImpl().getParagraphIterator();
	}

	public VariableField declareVariable(String name, VariableType type) {
		return getVariableContainerImpl().declareVariable(name, type);
	}

	public VariableField getVariableFieldByName(String name) {
		return getVariableContainerImpl().getVariableFieldByName(name);
	}

	public OdfElement getVariableContainerElement() {
		return getVariableContainerImpl().getVariableContainerElement();
	}
	
	public Chart createChart(String title, DataSet dataset, Rectangle rect) {
		return getChartContainerImpl().createChart(title, dataset, rect);
	}

	public Chart createChart(String title, SpreadsheetDocument document, CellRangeAddressList cellRangeAddr, boolean firstRowAsLabel,
			boolean firstColumnAsLabel, boolean rowAsDataSeries, Rectangle rect) {
		return getChartContainerImpl().createChart(title, document, cellRangeAddr, firstRowAsLabel, firstColumnAsLabel,
				rowAsDataSeries, rect);
	}

	public Chart createChart(String title, String[] labels, String[] legends, double[][] data, Rectangle rect) {
		return getChartContainerImpl().createChart(title, labels, legends, data, rect);
	}

	public void deleteChartById(String chartId) {
		getChartContainerImpl().deleteChartById(chartId);
	}

	public void deleteChartByTitle(String title) {
		getChartContainerImpl().deleteChartByTitle(title);
	}

	public Chart getChartById(String chartId) {
		return getChartContainerImpl().getChartById(chartId);
	}

	public java.util.List<Chart> getChartByTitle(String title) {
		return getChartContainerImpl().getChartByTitle(title);
	}

	public int getChartCount() {
		return getChartContainerImpl().getChartCount();
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

	private StyleMasterPageElement getMasterPage(boolean pFirstPage) throws Exception {
		String pageStyleName = pFirstPage ? "First_20_Page" : "Standard";
		OfficeDocumentStylesElement rootElement = getStylesDom().getRootElement();
		OfficeMasterStylesElement masterStyles = OdfElement.findFirstChildNode(OfficeMasterStylesElement.class,
				rootElement);
		if (masterStyles == null) {
			masterStyles = rootElement.newOfficeMasterStylesElement();
		}
		StyleMasterPageElement masterPageEle = null;
		NodeList lastMasterPages = masterStyles.getElementsByTagNameNS(OdfDocumentNamespace.STYLE.getUri(),
				"master-page");
		if (lastMasterPages != null && lastMasterPages.getLength() > 0) {
			for (int i = 0; i < lastMasterPages.getLength(); i++) {
				StyleMasterPageElement masterPage = (StyleMasterPageElement) lastMasterPages.item(i);
				String styleName = masterPage.getStyleNameAttribute();
				if (pageStyleName.equals(styleName)) {
					masterPageEle = masterPage;
					break;
				}
			}
		}
		if (masterPageEle == null) {
			OdfStylePageLayout layout = OdfElement.findFirstChildNode(OdfStylePageLayout.class, getStylesDom()
					.getAutomaticStyles());
			masterPageEle = masterStyles.newStyleMasterPageElement(pageStyleName, layout.getStyleNameAttribute());
		}
		return masterPageEle;
	}

	private class ParagraphContainerImpl extends AbstractParagraphContainer {
		public OdfElement getParagraphContainerElement() {
			OdfElement containerElement = null;
			try {
				containerElement = getContentRoot();
			} catch (Exception e) {
				Logger.getLogger(TextDocument.class.getName()).log(Level.SEVERE, null, e);
			}
			return containerElement;
		}
	}

	private ParagraphContainerImpl getParagraphContainerImpl() {
		if (paragraphContainerImpl == null)
			paragraphContainerImpl = new ParagraphContainerImpl();
		return paragraphContainerImpl;
	}

	private class VariableContainerImpl extends AbstractVariableContainer {

		public OdfElement getVariableContainerElement() {
			try {
				return getContentRoot();
			} catch (Exception e) {
				Logger.getLogger(TextDocument.class.getName()).log(Level.SEVERE, null, e);
				return null;
			}
		}
	}

	private VariableContainer getVariableContainerImpl() {
		if (variableContainerImpl == null) {
			variableContainerImpl = new VariableContainerImpl();
		}
		return variableContainerImpl;
	}
	
	private ChartContainerImpl getChartContainerImpl() {
		if (chartContainerImpl == null) {
			chartContainerImpl = new ChartContainerImpl(this);
		}
		return chartContainerImpl;
	}
	
	private class ChartContainerImpl extends AbstractChartContainer {
		TextDocument sdoc;

		protected ChartContainerImpl(Document doc) {
			super(doc);
			sdoc = (TextDocument) doc;
		}

		protected DrawFrameElement getChartFrame() throws Exception {
			OdfContentDom contentDom2 = sdoc.getContentDom();
			DrawFrameElement drawFrame = contentDom2.newOdfElement(DrawFrameElement.class);
			TextPElement lastPara = sdoc.getContentRoot().newTextPElement();
			lastPara.appendChild(drawFrame);
			drawFrame.setTextAnchorTypeAttribute(TextAnchorTypeAttribute.Value.PARAGRAPH.toString());
			return drawFrame;
		}
	}

	/**
	 * Create a new Table Of Content to this document before the reference
	 * paragraph. Because until you do a full layout of the document, taking
	 * into account font metrics,line breaking algorithms, hyphenation, image
	 * positioning, "orphan and widow" rules, etc., you don't know what content
	 * is on which page. So all the page numbers in TOC are be set value "1".
	 * Please update the page numbers from AOO Menus: Tools->Update->All Indexes
	 * and Tables
	 * 
	 * @param refParagraph
	 *            the reference paragraph where the TOC be inserted.
	 * @param before
	 *            true:insert TOC before the reference paragraph. false:insert
	 *            TOC after the reference paragraph.
	 * @since 0.8.6
	 */
	public TextTableOfContentElement createDefaultTOC(Paragraph refParagraph, boolean before) {
		
		TextTableOfContentElement textTableOfContent =null;
		if(refParagraph==null){
			Logger.getLogger(TextDocument.class.getName()).log(Level.SEVERE,
					"Failed to create Default TOC, The refParagraph where the TOC be inserted is null");
			throw new RuntimeException("Failed to create Default TOC, The refParagraph where the TOC be inserted is null");
		}
		Node refparagraphNode = refParagraph.getOdfElement();
		Node rootNode = refparagraphNode.getParentNode();		
		try {
			OdfContentDom content = getContentDom();
			textTableOfContent = content
					.newOdfElement(TextTableOfContentElement.class);
			textTableOfContent.setTextNameAttribute("Table of Contents");
			textTableOfContent.setTextProtectedAttribute(true);
			TextTableOfContentSourceElement textTableOfContentSource = textTableOfContent
					.newTextTableOfContentSourceElement();
			textTableOfContentSource.setTextOutlineLevelAttribute(10);
			textTableOfContentSource.setTextUseIndexMarksAttribute(true);
			TextIndexTitleTemplateElement textIndexTitleTemplate = textTableOfContentSource
					.newTextIndexTitleTemplateElement();
			textIndexTitleTemplate
					.setTextStyleNameAttribute("Contents_20_Heading");
			textIndexTitleTemplate.setTextContent("Table of Contents");
			for (int i = 1; i <= 10; i++) {
				TextTableOfContentEntryTemplateElement textTableOfContentEntryTemplate = textTableOfContentSource
						.newTextTableOfContentEntryTemplateElement(i,
								"Contents_20_" + i);
				TextIndexEntryLinkStartElement TextIndexEntryLinkStart = textTableOfContentEntryTemplate
						.newTextIndexEntryLinkStartElement();
				TextIndexEntryLinkStart
						.setTextStyleNameAttribute("Index_20_Link");
				textTableOfContentEntryTemplate
						.newTextIndexEntryChapterElement();
				textTableOfContentEntryTemplate.newTextIndexEntryTextElement();
				TextIndexEntryTabStopElement TextIndexEntryTabStop = textTableOfContentEntryTemplate
						.newTextIndexEntryTabStopElement("right");
				TextIndexEntryTabStop.setStyleLeaderCharAttribute(".");
				textTableOfContentEntryTemplate
						.newTextIndexEntryPageNumberElement();
				textTableOfContentEntryTemplate
						.newTextIndexEntryLinkEndElement();
			}
			TextIndexBodyElement textIndexBody = textTableOfContent
					.newTextIndexBodyElement();
			TextIndexTitleElement TextIndexTitle = textIndexBody
					.newTextIndexTitleElement("Table of Contents_Head");
			TextPElement texp = TextIndexTitle.newTextPElement();
			texp.setTextStyleNameAttribute("Contents_20_Heading");
			texp.setTextContent("Table of Contents");
			Iterator<Paragraph> paragraphIterator = getParagraphIterator();
			while (paragraphIterator.hasNext()) {
				Paragraph paragraph = paragraphIterator.next();
				String text = paragraph.getTextContent();
				String stylename = paragraph.getStyleName();
				// Outline support
				if (paragraph.isHeading()) {
					int headingLevel = paragraph.getHeadingLevel();
					if (stylename.length() <= 0) {
						stylename = "Contents_20_" + headingLevel;
					}
					ceateIndexBodyEntry(textIndexBody, stylename, text);
				}
				// end of Outline support
				// Index Makes support
				TextParagraphElementBase podf = paragraph.getOdfElement();
				NodeList cns = podf.getChildNodes();
				for (int i = 0; i < cns.getLength(); i++) {
					Node node = cns.item(i);
					if (node instanceof TextTocMarkElement) {
						TextTocMarkElement textTocMarkElement = (TextTocMarkElement) node;
						text = textTocMarkElement.getTextStringValueAttribute();
						int headingLevel = textTocMarkElement
								.getTextOutlineLevelAttribute();
						stylename = "Contents_20_" + headingLevel;
						ceateIndexBodyEntry(textIndexBody, stylename, text);
					}
					if (node instanceof TextTocMarkStartElement) {
						TextTocMarkStartElement textTocMarkStartElement = (TextTocMarkStartElement) node;
						Node tmp = node.getNextSibling();
						while (!(tmp instanceof Text)) {
							tmp = node.getNextSibling();
						}
						text = tmp.getTextContent();
						int headingLevel = textTocMarkStartElement
								.getTextOutlineLevelAttribute();
						stylename = "Contents_20_" + headingLevel;
						ceateIndexBodyEntry(textIndexBody, stylename, text);
					}
				}
				// end of Index Makes support
			}
			if (before) {
				rootNode.insertBefore(textTableOfContent, refparagraphNode);
			} else {
				// Insert TOC after the refParagraph
				Node refNextNode = refparagraphNode.getNextSibling();
				if (refNextNode == null) {
					rootNode.appendChild(textTableOfContent);
				} else {
					rootNode.insertBefore(textTableOfContent, refNextNode);
				}
			}
			return textTableOfContent;
		} catch (Exception e) {
			Logger.getLogger(TextDocument.class.getName()).log(Level.SEVERE,
					"Failed to create Default TOC", e);
			throw new RuntimeException("Failed to create Default TOC", e);
		}
	}

	private void ceateIndexBodyEntry(TextIndexBodyElement textIndexBody,
			String stylename, String text) {
		TextPElement textp = textIndexBody.newTextPElement();
		textp.setTextStyleNameAttribute(stylename);
		textp.newTextNode(text);
		textp.newTextTabElement();
		textp.newTextNode("1");
	}

	/**
	 * Create a new Table Of Content to this document before the reference
	 * paragraph. The additional paragraph styles list will be included in the
	 * TOC. Because until you do a full layout of the document, taking into
	 * account font metrics,line breaking algorithms, hyphenation, image
	 * positioning, "orphan and widow" rules, etc., you don't know what content
	 * is on which page. So all the page numbers in TOC are be set value "1".
	 * Please update the page numbers from AOO Menus: Tools->Update->All Indexes
	 * and Tables.  If additionalStyle is null then call createDefaultTOC().
	 * 
	 * @param refParagraph
	 *            the reference paragraph before where the TOC inserted.
	 * @param additionalStyle
	 *            the additional paragraph styles that you want to include in
	 *            the TOC
	 * @param before
	 *            true:insert TOC before the reference paragraph. 
	 *            false:insert TOC after the reference paragraph.
	 * @since 0.8.6
	 */
	public TextTableOfContentElement createTOCwithStyle(Paragraph refParagraph,
			TOCStyle additionalStyle, boolean before) {
		if(refParagraph==null){
			Logger.getLogger(TextDocument.class.getName()).log(Level.SEVERE,
					"Failed to create Default TOC, The refParagraph where the TOC be inserted is null");
			throw new RuntimeException("Failed to create Default TOC, The refParagraph where the TOC be inserted is null");
		}
		if (additionalStyle == null) {
			TextTableOfContentElement textTableOfContentElement = createDefaultTOC(refParagraph, before);
			return textTableOfContentElement;
		}
		HashMap<Integer, String> tocstyleList = additionalStyle.getStyle();
		if (tocstyleList.isEmpty()) {
			TextTableOfContentElement textTableOfContentElement =createDefaultTOC(refParagraph, before);
			return textTableOfContentElement;
		}

		Collection<String> tocvalues = tocstyleList.values();
		OdfOfficeStyles docstyles = getOrCreateDocumentStyles();
		Iterable<OdfStyle> paragraphStyles = docstyles
				.getStylesForFamily(OdfStyleFamily.Paragraph);
		ArrayList<String> pstyle = new ArrayList<String>();
		Iterator<OdfStyle> iterator = paragraphStyles.iterator();
		while (iterator.hasNext()) {
			OdfStyle style = iterator.next();
			String name = style.getStyleNameAttribute();
			pstyle.add(name);
		}
		if (!pstyle.containsAll(tocvalues)) {
			Logger.getLogger(TextDocument.class.getName())
					.log(Level.SEVERE,
							"Failed to create TOC with Styles. Some of addtional Styles that you want to include in the TOC can't be found in the document.\n"
									+ tocvalues.toString());
			throw new RuntimeException(
					"Failed to create TOC Styles.Some of addtional Styles that you want to include in the TOC can't be found in the document.");
		}
		TextTableOfContentElement textTableOfContent =null;
		// Additional Styles support
		HashMap<String, Integer> tmptocstyleList = new HashMap<String, Integer>();
		if (!tocstyleList.isEmpty()) {
			Set<Integer> key = tocstyleList.keySet();
			Iterator<Integer> it = key.iterator();
			while (it.hasNext()) {
				Integer textOutlineLevelValue = it.next();
				String textStyleNameValue = tocstyleList
						.get(textOutlineLevelValue);
				tmptocstyleList.put(textStyleNameValue, textOutlineLevelValue);
			}
		}
		// end of Additional Styles support

		Node refparagraphNode = refParagraph.getOdfElement();
		Node rootNode = refparagraphNode.getParentNode();
		try {
			OdfContentDom content = getContentDom();
			textTableOfContent = content
					.newOdfElement(TextTableOfContentElement.class);
			textTableOfContent.setTextNameAttribute("Table of Contents");
			textTableOfContent.setTextProtectedAttribute(true);

			TextTableOfContentSourceElement textTableOfContentSource = textTableOfContent
					.newTextTableOfContentSourceElement();
			textTableOfContentSource.setTextOutlineLevelAttribute(10);
			textTableOfContentSource.setTextUseIndexMarksAttribute(true);
			textTableOfContentSource.setTextUseIndexSourceStylesAttribute(true);
			TextIndexTitleTemplateElement textIndexTitleTemplate = textTableOfContentSource
					.newTextIndexTitleTemplateElement();
			textIndexTitleTemplate
					.setTextStyleNameAttribute("Contents_20_Heading");
			textIndexTitleTemplate.setTextContent("Table of Contents");
			for (int i = 1; i <= 10; i++) {
				TextTableOfContentEntryTemplateElement textTableOfContentEntryTemplate = textTableOfContentSource
						.newTextTableOfContentEntryTemplateElement(i,
								"Contents_20_" + i);
				TextIndexEntryLinkStartElement TextIndexEntryLinkStart = textTableOfContentEntryTemplate
						.newTextIndexEntryLinkStartElement();
				TextIndexEntryLinkStart
						.setTextStyleNameAttribute("Index_20_Link");
				textTableOfContentEntryTemplate
						.newTextIndexEntryChapterElement();
				textTableOfContentEntryTemplate.newTextIndexEntryTextElement();
				TextIndexEntryTabStopElement TextIndexEntryTabStop = textTableOfContentEntryTemplate
						.newTextIndexEntryTabStopElement("right");
				TextIndexEntryTabStop.setStyleLeaderCharAttribute(".");
				textTableOfContentEntryTemplate
						.newTextIndexEntryPageNumberElement();
				textTableOfContentEntryTemplate
						.newTextIndexEntryLinkEndElement();
			}

			if (!tocstyleList.isEmpty()) {
				Set<Integer> key = tocstyleList.keySet();
				Iterator<Integer> it = key.iterator();
				while (it.hasNext()) {
					Integer textOutlineLevelValue = it.next();
					String textStyleNameValue = tocstyleList
							.get(textOutlineLevelValue);
					TextIndexSourceStylesElement textIndexSourceStyles = textTableOfContentSource
							.newTextIndexSourceStylesElement(textOutlineLevelValue);
					textIndexSourceStyles
							.newTextIndexSourceStyleElement(textStyleNameValue);
				}
			}

			TextIndexBodyElement textIndexBody = textTableOfContent
					.newTextIndexBodyElement();
			TextIndexTitleElement TextIndexTitle = textIndexBody
					.newTextIndexTitleElement("Table of Contents_Head");
			TextPElement texp = TextIndexTitle.newTextPElement();
			texp.setTextStyleNameAttribute("Contents_20_Heading");
			texp.setTextContent("Table of Contents");
			Iterator<Paragraph> paragraphIterator = getParagraphIterator();
			while (paragraphIterator.hasNext()) {
				Paragraph paragraph = paragraphIterator.next();
				String text = paragraph.getTextContent();
				String stylename = paragraph.getStyleName();
				// Outline support
				if (paragraph.isHeading()) {
					int headingLevel = paragraph.getHeadingLevel();
					if (stylename.length() <= 0) {
						stylename = "Contents_20_" + headingLevel;
					}
					ceateIndexBodyEntry(textIndexBody, stylename, text);
				}
				// end of Outline support
				// Additional Styles support
				if (tmptocstyleList.containsKey(stylename)) {
					int headingLevel = tmptocstyleList.get(stylename);
					stylename = "Contents_20_" + headingLevel;
					ceateIndexBodyEntry(textIndexBody, stylename, text);
				}
				// end of Additional Styles support
				// Index Makes support
				TextParagraphElementBase podf = paragraph.getOdfElement();
				NodeList cns = podf.getChildNodes();
				for (int i = 0; i < cns.getLength(); i++) {
					Node node = cns.item(i);
					if (node instanceof TextTocMarkElement) {
						TextTocMarkElement textTocMarkElement = (TextTocMarkElement) node;
						text = textTocMarkElement.getTextStringValueAttribute();
						int headingLevel = textTocMarkElement
								.getTextOutlineLevelAttribute();
						stylename = "Contents_20_" + headingLevel;
						ceateIndexBodyEntry(textIndexBody, stylename, text);
					}
					if (node instanceof TextTocMarkStartElement) {
						TextTocMarkStartElement textTocMarkStartElement = (TextTocMarkStartElement) node;
						Node tmp = node.getNextSibling();
						while (!(tmp instanceof Text)) {
							tmp = node.getNextSibling();
						}
						text = tmp.getTextContent();
						int headingLevel = textTocMarkStartElement
								.getTextOutlineLevelAttribute();
						stylename = "Contents_20_" + headingLevel;
						ceateIndexBodyEntry(textIndexBody, stylename, text);
					}
				}
				// end of Index Makes support
			}
			if (before) {
				rootNode.insertBefore(textTableOfContent, refparagraphNode);
			} else {
				// Insert TOC after the refParagraph
				Node refNextNode = refparagraphNode.getNextSibling();
				if (refNextNode == null) {
					rootNode.appendChild(textTableOfContent);
				} else {
					rootNode.insertBefore(textTableOfContent, refNextNode);
				}
			}
			return textTableOfContent;
			
		} catch (Exception e) {
			Logger.getLogger(TextDocument.class.getName()).log(Level.SEVERE,
					"Failed to create Default TOC", e);
			throw new RuntimeException("Failed to create Default TOC", e);
		}
	}
	
	/**
	 * Copy a Paragraph and insert it before or after the Reference Paragraph in the text document, whether the
	 * Paragraph is in this document or in a different document.
	 * 
	 * @param referenceParagraph
	 *            - where the Paragraph be inserted 
	 * @param sourceParagraph
	 *            - the Paragraph which will be copied
	 * @param before
	 *            true:insert Paragraph before the reference paragraph. 
	 *            false:insert Paragraph after the reference paragraph.       
	 */
	public Paragraph insertParagraph(Paragraph referenceParagraph, Paragraph sourceParagraph,boolean before) {
		boolean isForeignNode = false;
		try {
			Node refparagraphNode = referenceParagraph.getOdfElement();
			if (sourceParagraph.getOdfElement().getOwnerDocument() != getContentDom())
				isForeignNode = true;
			 TextParagraphElementBase oldParagraphEle = sourceParagraph.getOdfElement();
			 TextParagraphElementBase newParagraphEle = (TextParagraphElementBase) oldParagraphEle.cloneNode(true);
			
			if (isForeignNode)
				copyForeignStyleRef(sourceParagraph.getOdfElement(), sourceParagraph.getOwnerDocument());
			if (isForeignNode) // not in a same document
				newParagraphEle = (TextParagraphElementBase) cloneForeignElement(newParagraphEle, getContentDom(), true);
			
			if (before) {
				refparagraphNode.getParentNode().insertBefore(newParagraphEle, refparagraphNode);
			} else {
				// Insert Paragraph after the refParagraph
				Node refNextNode = refparagraphNode.getNextSibling();
				if (refNextNode == null) {
					refparagraphNode.getParentNode().appendChild(newParagraphEle);
				} else {
					refparagraphNode.getParentNode().insertBefore(newParagraphEle, refNextNode);
				}
			}
			
			return Paragraph.getInstanceof(newParagraphEle);
		} catch (Exception e) {
			Logger.getLogger(TextDocument.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}
	/**
	 * Copy a Table and insert it before or after the Reference Paragraph in the text document, whether the
	 * Table is in this TextDocument or in a different Document.
	 * 
	 * @param referenceParagraph
	 *            - where the Paragraph be inserted 
	 * @param sourceParagraph
	 *            - the Paragraph which will be copied
	 * @param before
	 *            true:insert Paragraph before the reference paragraph. 
	 *            false:insert Paragraph after the reference paragraph.       
	 */
	public Table insertTable(Paragraph referenceParagraph, Table sourceTable,boolean before) {
		
		Document ownDocument = sourceTable.getOwnerDocument();
		TableTableElement newTEle = (TableTableElement)insertOdfElement(referenceParagraph.getOdfElement(),ownDocument,sourceTable.getOdfElement(),before);
		Table table = Table.getInstance(newTEle);
		return table;
	}
	/**
	 * Copy a OdfElement and insert it before or after the Reference OdfElement in the TextDocument, whether the
	 * OdfElement is in this TextDocument or in a different Document.
	 * 
	 * @param referenceOdfElement
	 *            - where the OdfElement be inserted 
	 * @param sourceDocument
	 *            - the source Document which contain the sourceOdfElement
	 * @param sourceOdfElement
	 *            - the OdfElement which will be copied
	 * @param before
	 *            true:insert OdfElement before the reference OdfElement. 
	 *            false:insert OdfElement after the reference OdfElement.       
	 */
	public OdfElement insertOdfElement(OdfElement referenceOdfElement,Document sourceDocument ,OdfElement sourceOdfElement,boolean before) {
		boolean isForeignNode = false;
		try {
			
			if (sourceOdfElement.getOwnerDocument() != getContentDom())
				isForeignNode = true;
			 
			OdfElement newOdfElement = (OdfElement) sourceOdfElement.cloneNode(true);
			
			if (isForeignNode) {
				copyForeignStyleRef(newOdfElement, sourceDocument);
				copyLinkedRef(newOdfElement);
				newOdfElement = (OdfElement) cloneForeignElement(newOdfElement,
						getContentDom(), true);
			}
			
			if (before) {
				referenceOdfElement.getParentNode().insertBefore(newOdfElement, referenceOdfElement);
			} else {
				// Insert newOdfElement after the referenceOdfElement
				Node refNextNode = referenceOdfElement.getNextSibling();
				if (refNextNode == null) {
					referenceOdfElement.getParentNode().appendChild(newOdfElement);
				} else {
					referenceOdfElement.getParentNode().insertBefore(newOdfElement, refNextNode);
				}
			}
			return newOdfElement;
		} catch (Exception e) {
			Logger.getLogger(TextDocument.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}
	
	private OdfElement insertOdfElementwithoutstyle(OdfElement referenceOdfElement,Document sourceDocument ,OdfElement sourceOdfElement,boolean before) {
		try {			 
			OdfElement newOdfElement = (OdfElement) sourceOdfElement.cloneNode(true);
			newOdfElement = (OdfElement)  cloneForeignElement(newOdfElement, getContentDom(), true);
			if (before) {
				referenceOdfElement.getParentNode().insertBefore(newOdfElement, referenceOdfElement);
			} else {
				// Insert newOdfElement after the referenceOdfElement
				Node refNextNode = referenceOdfElement.getNextSibling();
				if (refNextNode == null) {
					referenceOdfElement.getParentNode().appendChild(newOdfElement);
				} else {
					referenceOdfElement.getParentNode().insertBefore(newOdfElement, refNextNode);
				}
			}
			return newOdfElement;
		} catch (Exception e) {
			Logger.getLogger(TextDocument.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}
	
	/**
	 * Copy text content of the source TextDocument and insert it to the current TextDocument
	 * after the reference Paragraph, with Styles or without Styles.
	 * 
	 * @param sourceDocument
	 *            the source TextDocument
	 * @param referenceParagraph
	 *  		  where the text content of the source TextDocument be inserted 
	 * @param isCopyStyle
	 *            true:copy the styles in source document to current TextDocment. 
	 *            false:don't copy the styles in source document to current TextDocment. 
	 */
	public void insertContentFromDocumentAfter(TextDocument sourceDocument, Paragraph referenceParagraph, boolean isCopyStyle){
		try {
			OfficeTextElement sroot = sourceDocument.getContentRoot();
			NodeList clist = sroot.getChildNodes();
			for (int i=(clist.getLength()-1); i>=0; i--) {
				OdfElement node = (OdfElement) clist.item(i);
				if(isCopyStyle){
					insertOdfElement(referenceParagraph.getOdfElement(), sourceDocument, node, false);
				}
				else {
					insertOdfElementwithoutstyle(referenceParagraph.getOdfElement(), sourceDocument, node, false);
				}
			}
			
		} catch (Exception e) {
			Logger.getLogger(TextDocument.class.getName()).log(Level.SEVERE, null, e);
		}
	}
	/**
	 * Copy text content of the source TextDocument and insert it to the current TextDocument
	 * before the reference Paragraph, with Styles or without Styles.
	 * 
	 * @param srcDoc
	 *            the source TextDocument
	 * @param referenceParagraph
	 *  		  where the text content of the source TextDocument be inserted 
	 * @param isCopyStyle
	 *            true:copy the styles in source document to current TextDocment. 
	 *            false:don't copy the styles in source document to current TextDocment. 
	 */
	public void insertContentFromDocumentBefore(TextDocument sourceDocument, Paragraph referenceParagraph, boolean isCopyStyle){
		try {
			OfficeTextElement sroot = sourceDocument.getContentRoot();
			NodeList clist = sroot.getChildNodes();
			for (int i = 0; i < clist.getLength(); i++) {
				OdfElement node = (OdfElement) clist.item(i);
				if(isCopyStyle){
					insertOdfElement(referenceParagraph.getOdfElement(), sourceDocument, node, true);
				}
				else {
					insertOdfElementwithoutstyle(referenceParagraph.getOdfElement(), sourceDocument, node, true);
				}
			}
		} catch (Exception e) {
			Logger.getLogger(TextDocument.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	private FormContainerImpl getFormContainerImpl() {
		if (formContainerImpl == null) {
			formContainerImpl = new FormContainerImpl();
		}
		return formContainerImpl;
	}

	private class FormContainerImpl extends AbstractFormContainer {

		public OfficeFormsElement getFormContainerElement() {
			OfficeFormsElement forms = null;
			try {
				OfficeTextElement root = getContentRoot();
				forms = OdfElement.findFirstChildNode(OfficeFormsElement.class,
						root);
				if (forms == null) {
					Node firstChild = root.getFirstChild();
					OfficeFormsElement officeForms = ((OdfFileDom) getContentDom())
							.newOdfElement(OfficeFormsElement.class);
					forms = (OfficeFormsElement) root.insertBefore(officeForms,
							firstChild);
				}
				return forms;
			} catch (Exception e) {
				Logger.getLogger(TextDocument.class.getName()).log(
						Level.SEVERE, null, e);
			}
			return forms;
		}

	}

	/**
	 * Create a form with specified name in this text document.
	 * 
	 * @see FormContainer#createForm(String)
	 */
	public Form createForm(String name) {
		return getFormContainerImpl().createForm(name);
	}

	/**
	 * Get a form iterator to traverse all the forms in this document.
	 * 
	 * @see FormContainer#getFormIterator()
	 */
	public Iterator<Form> getFormIterator() {
		return getFormContainerImpl().getFormIterator();
	}

	/**
	 * Remove a form with the specified name in this document.
	 * 
	 * @see FormContainer#removeForm(Form)
	 */
	public boolean removeForm(Form form) {
		return getFormContainerImpl().removeForm(form);
	}

//	@Override
	public Form getFormByName(String name) {
		return getFormContainerImpl().getFormByName(name);
	}

//	@Override
	public OfficeFormsElement getFormContainerElement() {
		return getFormContainerImpl().getFormContainerElement();
	}

//	@Override
	public boolean getApplyDesignMode() {
		return getFormContainerImpl().getApplyDesignMode();
	}

//	@Override
	public boolean getAutomaticFocus() {
		return getFormContainerImpl().getAutomaticFocus();
	}

//	@Override
	public void setApplyDesignMode(boolean isDesignMode) {
		getFormContainerImpl().setApplyDesignMode(isDesignMode);

	}

//	@Override
	public void setAutomaticFocus(boolean isAutoFocus) {
		getFormContainerImpl().setAutomaticFocus(isAutoFocus);

	}

//	@Override
	public Control createDrawControl() {
		OdfElement parent = this.getDrawControlContainerElement();
		OdfFileDom ownerDom = (OdfFileDom) parent.getOwnerDocument();
		DrawControlElement element = ownerDom
				.newOdfElement(DrawControlElement.class);
		Node refChild = OdfElement.findFirstChildNode(
				TextSequenceDeclsElement.class, parent);
		parent.insertBefore(element, refChild.getNextSibling());
		Control control = new Control(element);
		Component.registerComponent(control, element);
		return control;
	}

//	@Override
	public OdfElement getDrawControlContainerElement() {
		OdfElement element = null;
		try {
			element = this.getContentRoot();
		} catch (Exception e) {
			Logger.getLogger(TextDocument.class.getName()).log(Level.SEVERE,
					null, e);
		}
		return element;
	}

}
