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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xpath.XPathConstants;

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.attribute.text.TextAnchorTypeAttribute;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawPageElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawPageThumbnailElement;
import org.odftoolkit.odfdom.dom.element.office.OfficePresentationElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationNotesElement;
import org.odftoolkit.odfdom.pkg.MediaType;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.type.CellRangeAddressList;
import org.odftoolkit.simple.chart.AbstractChartContainer;
import org.odftoolkit.simple.chart.Chart;
import org.odftoolkit.simple.chart.ChartContainer;
import org.odftoolkit.simple.chart.DataSet;
import org.odftoolkit.simple.presentation.Slide;
import org.odftoolkit.simple.presentation.Notes.NotesBuilder;
import org.odftoolkit.simple.presentation.Slide.SlideBuilder;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class represents an empty ODF presentation.
 */
public class PresentationDocument extends Document implements ChartContainer{

	private static final String EMPTY_PRESENTATION_DOCUMENT_PATH = "/OdfPresentationDocument.odp";
	static final Resource EMPTY_PRESENTATION_DOCUMENT_RESOURCE = new Resource(EMPTY_PRESENTATION_DOCUMENT_PATH);
	private final SlideBuilder slideBuilder;
	private final NotesBuilder notesBuilder;
	private ChartContainerImpl chartContainerImpl;
	
	/**
	 * It represents the defined values of presentation:class. The
	 * presentation:class attribute classifies presentation shapes by their
	 * usage within a draw page.
	 * 
	 * @since 0.5
	 */
	public static enum PresentationClass {
		/**
		 * presentation charts are standard object shapes.
		 */
		CHAT("chart"),
		/**
		 * presentation graphics are standard graphic shapes.
		 */
		GRAPHIC("graphic"),
		/**
		 * presentation handouts are placeholder for the drawing page in a
		 * handout page.
		 */
		HANDOUT("handout"),
		/**
		 * presentation notes are used on notes pages.
		 */
		NOTES("notes"),
		/**
		 * presentation objects are standard object shapes.
		 */
		OBJECTS("object"),
		/**
		 * presentation organization charts are standard object shapes.
		 */
		ORGCHART("orgchart"),
		/**
		 * outlines are standard text shapes
		 */
		OUTLINE("outline"),
		/**
		 * presentation pages are used on notes pages
		 */
		PAGE("page"),
		/**
		 * subtitles are standard text shapes
		 */
		SUBTITLE("subtitle"),
		/**
		 * presentation tables are standard object shapes
		 */
		TABLE("table"),
		/**
		 * presentation texts are standard text shapes
		 */
		TEXT("text"),
		/**
		 * titles are standard text shapes
		 */
		TITLE("title"),
		/**
		 * drawing shape is used as a date and/or time shape. Date and Time
		 * shapes are standard text shapes.
		 */
		DATETIME("date-time"),
		/**
		 * drawing shape is used as a footer. Footer shapes are standard text
		 * shapes.
		 */
		FOOTER("footer"),
		/**
		 * drawing shape is used as a header. Header shapes are standard text
		 * shapes.
		 */
		HEADER("header"),
		/**
		 * drawing shape is used as a page number shape. Page Number shapes are
		 * standard text shapes.
		 */
		PAGENUMBER("page-number");

		private String value;

		PresentationClass(String aClass) {
			value = aClass;
		}

		@Override
		public String toString() {
			return value;
		}

		public static PresentationClass enumValueOf(String aValue) {
			if ((aValue == null) || (aValue.length() == 0))
				return null;

			for (PresentationClass aIter : values()) {
				if (aValue.equals(aIter.toString())) {
					return aIter;
				}
			}
			throw new RuntimeException("Unsupported Presentation Class!");
		}
	}
	
	/**
	 * This enum contains all possible media types of PresentationDocument
	 * documents.
	 */
	public enum OdfMediaType implements MediaType {

		PRESENTATION(Document.OdfMediaType.PRESENTATION), PRESENTATION_TEMPLATE(
				Document.OdfMediaType.PRESENTATION_TEMPLATE);
		private final Document.OdfMediaType mMediaType;

		OdfMediaType(Document.OdfMediaType mediaType) {
			this.mMediaType = mediaType;
		}

		/**
		 * @return the ODF mediatype of this document
		 */
		public Document.OdfMediaType getOdfMediaType() {
			return mMediaType;
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
	 * Creates an empty presentation document.
	 * 
	 * @return ODF presentation document based on a default template
	 * @throws java.lang.Exception
	 *             - if the document could not be created
	 */
	public static PresentationDocument newPresentationDocument() throws Exception {
		return (PresentationDocument) Document.loadTemplate(EMPTY_PRESENTATION_DOCUMENT_RESOURCE,
				Document.OdfMediaType.PRESENTATION);
	}

	/**
	 * Creates an empty presentation template.
	 * 
	 * @return ODF presentation template based on a default
	 * @throws Exception
	 *             - if the template could not be created
	 */
	public static PresentationDocument newPresentationTemplateDocument() throws Exception {
		PresentationDocument doc = (PresentationDocument) Document.loadTemplate(EMPTY_PRESENTATION_DOCUMENT_RESOURCE,
				Document.OdfMediaType.PRESENTATION_TEMPLATE);
		doc.changeMode(OdfMediaType.PRESENTATION_TEMPLATE);
		return doc;
	}

	/**
	 * To avoid data duplication a new document is only created, if not already
	 * opened. A document is cached by this constructor using the internalpath
	 * as key.
	 */
	protected PresentationDocument(OdfPackage pkg, String internalPath, PresentationDocument.OdfMediaType odfMediaType) {
		super(pkg, internalPath, odfMediaType.mMediaType);
		slideBuilder = new SlideBuilder(this);
		notesBuilder = new NotesBuilder(this);
	}

	/**
	 * Creates an PresentationDocument from the OpenDocument provided by a
	 * resource Stream.
	 * 
	 * <p>
	 * Since an InputStream does not provide the arbitrary (non sequentiell)
	 * read access needed by PresentationDocument, the InputStream is cached.
	 * This usually takes more time compared to the other createInternalDocument
	 * methods. An advantage of caching is that there are no problems
	 * overwriting an input file.
	 * </p>
	 * 
	 * <p>
	 * If the resource stream is not a ODF presentation document,
	 * ClassCastException might be thrown.
	 * </p>
	 * 
	 * @param inputStream
	 *            - the InputStream of the ODF presentation document.
	 * @return the presentation document created from the given InputStream
	 * @throws java.lang.Exception
	 *             - if the document could not be created.
	 */
	public static PresentationDocument loadDocument(InputStream inputStream) throws Exception {
		return (PresentationDocument) Document.loadDocument(inputStream);
	}

	/**
	 * Loads an PresentationDocument from the provided path.
	 * 
	 * <p>
	 * PresentationDocument relies on the file being available for read access
	 * over the whole lifecycle of PresentationDocument.
	 * </p>
	 * 
	 * <p>
	 * If the resource stream is not a ODF presentation document,
	 * ClassCastException might be thrown.
	 * </p>
	 * 
	 * @param documentPath
	 *            - the path from where the document can be loaded
	 * @return the presentation document from the given path or NULL if the
	 *         media type is not supported by SIMPLE.
	 * @throws java.lang.Exception
	 *             - if the document could not be created.
	 */
	public static PresentationDocument loadDocument(String documentPath) throws Exception {
		return (PresentationDocument) Document.loadDocument(documentPath);
	}

	/**
	 * Creates an PresentationDocument from the OpenDocument provided by a File.
	 * 
	 * <p>
	 * PresentationDocument relies on the file being available for read access
	 * over the whole lifecycle of PresentationDocument.
	 * </p>
	 * 
	 * <p>
	 * If the resource stream is not a ODF presentation document,
	 * ClassCastException might be thrown.
	 * </p>
	 * 
	 * @param file
	 *            - a file representing the ODF presentation document.
	 * @return the presentation document created from the given File
	 * @throws java.lang.Exception
	 *             - if the document could not be created.
	 */
	public static PresentationDocument loadDocument(File file) throws Exception {
		return (PresentationDocument) Document.loadDocument(file);
	}

	/**
	 * Get the content root of a presentation document.
	 * 
	 * @return content root, representing the office:presentation tag
	 * @throws Exception
	 *             if the file DOM could not be created.
	 */
	@Override
	public OfficePresentationElement getContentRoot() throws Exception {
		return super.getContentRoot(OfficePresentationElement.class);
	}

	/**
	 * Switches this instance to the given type. This method can be used to e.g.
	 * convert a document instance to a template and vice versa. Changes take
	 * affect in the package when saving the document.
	 * 
	 * @param type
	 *            the compatible ODF mediatype.
	 */
	public void changeMode(OdfMediaType type) {
		setOdfMediaType(type.mMediaType);
	}

	private boolean hasCheckSlideName = false;

	/**
	 * Return the slide builder of this document. Every presentation document
	 * has a slide builder.
	 * 
	 * @return the slide builder of this document.
	 * @since 0.3.5
	 */
	public SlideBuilder getSlideBuilder() {
		return slideBuilder;
	}

	/**
	 * Return the notes builder of this document. Every presentation document
	 * has a notes builder.
	 * 
	 * @return the notes builder of this document.
	 * @since 0.3.5
	 */
	public NotesBuilder getNotesBuilder() {
		return notesBuilder;
	}

	/**
	 * Return the slide at a specified position in this presentation. Return
	 * null if the index is out of range.
	 * 
	 * @param index
	 *            the index of the slide to be returned
	 * @return a draw slide at the specified position
	 */
	public Slide getSlideByIndex(int index) {
		checkAllSlideName();
		OfficePresentationElement contentRoot = null;
		try {
			contentRoot = getContentRoot();
		} catch (Exception e) {
			Logger.getLogger(PresentationDocument.class.getName()).log(Level.SEVERE, null, e);
			return null;
		}
		NodeList slideNodes = contentRoot.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		if ((index >= slideNodes.getLength()) || (index < 0)) {
			return null;
		}
		DrawPageElement slideElement = (DrawPageElement) slideNodes.item(index);
		return Slide.getInstance(slideElement);
	}

	/**
	 * Get the number of the slides in this presentation.
	 * 
	 * @return the number of slides
	 */
	public int getSlideCount() {
		checkAllSlideName();
		OfficePresentationElement contentRoot = null;
		try {
			contentRoot = getContentRoot();
		} catch (Exception e) {
			Logger.getLogger(PresentationDocument.class.getName()).log(Level.SEVERE, null, e);
			return 0;
		}
		NodeList slideNodes = contentRoot.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		return slideNodes.getLength();
	}

	/**
	 * Return the slide which have a specified slide name in this presentation.
	 * <p>
	 * According to the odf specification "The draw:name attribute specifies a
	 * name by which this element can be referenced. It is optional but if
	 * present, must be unique within the document instance. If not present, an
	 * application may generate a unique name."
	 * <p>
	 * If the name is null, then return null because all the slide must has its
	 * own unique name.
	 * 
	 * @param name
	 *            the specified slide name
	 * @return the slide whose name equals to the specified name
	 */
	public Slide getSlideByName(String name) {
		checkAllSlideName();
		if (name == null) {
			return null;
		}
		OfficePresentationElement contentRoot = null;
		try {
			contentRoot = getContentRoot();
		} catch (Exception e) {
			Logger.getLogger(PresentationDocument.class.getName()).log(Level.SEVERE, null, e);
			return null;
		}
		NodeList slideNodes = contentRoot.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		for (int i = 0; i < slideNodes.getLength(); i++) {
			DrawPageElement slideElement = (DrawPageElement) slideNodes.item(i);
			Slide slide = Slide.getInstance(slideElement);
			String slideName = slide.getSlideName();
			if (slideName.equals(name)) {
				return slide;
			}
		}
		return null;
	}

	// when access slide related method, this function should be called
	private void checkAllSlideName() {
		// check if this function is called or not
		if (hasCheckSlideName) {
			return;
		}
		List<String> slideNameList = new ArrayList<String>();
		OfficePresentationElement contentRoot = null;
		try {
			contentRoot = getContentRoot();
		} catch (Exception e) {
			Logger.getLogger(PresentationDocument.class.getName()).log(Level.SEVERE, null, e);
			return;
		}
		NodeList slideNodes = contentRoot.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		for (int i = 0; i < slideNodes.getLength(); i++) {
			DrawPageElement slideElement = (DrawPageElement) slideNodes.item(i);
			String slideName = slideElement.getDrawNameAttribute();
			if ((slideName == null) || slideNameList.contains(slideName)) {
				slideName = "page" + (i + 1) + "-" + makeUniqueName();
				slideElement.setDrawNameAttribute(slideName);
			}
			slideNameList.add(slideName);
		}
		hasCheckSlideName = true;
	}

	/**
	 * Return a list iterator containing all slides in this presentation.
	 * 
	 * @return a list iterator containing all slides in this presentation
	 */
	public Iterator<Slide> getSlides() {
		checkAllSlideName();
		OfficePresentationElement contentRoot = null;
		try {
			contentRoot = getContentRoot();
		} catch (Exception e) {
			Logger.getLogger(PresentationDocument.class.getName()).log(Level.SEVERE, null, e);
			return null;
		}
		ArrayList<Slide> slideList = new ArrayList<Slide>();
		NodeList slideNodes = contentRoot.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		for (int i = 0; i < slideNodes.getLength(); i++) {
			DrawPageElement slideElement = (DrawPageElement) slideNodes.item(i);
			slideList.add(Slide.getInstance(slideElement));
		}
		return slideList.iterator();
	}

	/**
	 * Delete the slide at a specified position in this presentation.
	 * 
	 * @param index
	 *            the index of the slide that need to be delete
	 *            <p>
	 *            Throw IndexOutOfBoundsException if the slide index is out of
	 *            the presentation document slide count.
	 * @return false if the operation was not successful
	 */
	public boolean deleteSlideByIndex(int index) {
		boolean success = true;
		checkAllSlideName();
		OfficePresentationElement contentRoot = null;
		try {
			contentRoot = getContentRoot();
		} catch (Exception e) {
			Logger.getLogger(PresentationDocument.class.getName()).log(Level.SEVERE, null, e);
			success = false;
			return success;
		}
		NodeList slideNodes = contentRoot.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		if ((index >= slideNodes.getLength()) || (index < 0)) {
			throw new IndexOutOfBoundsException(
					"the specified Index is out of slide count when call deleteSlideByIndex method.");
		}
		DrawPageElement slideElement = (DrawPageElement) slideNodes.item(index);
		// remove all the content of the current page
		// 1. the reference of the path that contained in this slide is 1, then
		// remove it
		// success &= deleteLinkedRef(slideElement);
		// 2.the reference of the style is 1, then remove it
		// in order to save time, do not delete the style here
		// success &= deleteStyleRef(slideElement);
		// these two methods have been merged into 1 method
		success &= removeElementLinkedResource(slideElement);
		// remove the current page element
		contentRoot.removeChild(slideElement);
		adjustNotePageNumber(index);
		return success;
	}

	/**
	 * Delete all the slides with a specified name in this presentation.
	 * 
	 * @param name
	 *            the name of the slide that need to be delete
	 * @return false if the operation was not successful
	 */
	public boolean deleteSlideByName(String name) {
		boolean success = true;
		checkAllSlideName();
		OfficePresentationElement contentRoot = null;
		try {
			contentRoot = getContentRoot();
		} catch (Exception e) {
			Logger.getLogger(PresentationDocument.class.getName()).log(Level.SEVERE, null, e);
			success = false;
			return success;
		}
		Slide slide = getSlideByName(name);
		DrawPageElement slideElement = slide.getOdfElement();
		// remove all the content of the current page
		// 1. the reference of the path that contained in this slide is 1, then
		// remove its
		success &= deleteLinkedRef(slideElement);
		// 2.the reference of the style is 1, then remove it
		// in order to save time, do not delete style here
		success &= deleteStyleRef(slideElement);
		// remove the current page element
		contentRoot.removeChild(slideElement);
		adjustNotePageNumber(0);
		return success;
	}

	/**
	 * Make a copy of the slide at a specified position to another position in
	 * this presentation. The original slide which at the dest index and after
	 * the dest index will move after.
	 * <p>
	 * 
	 * @param source
	 *            the source position of the slide need to be copied
	 * @param dest
	 *            the destination position of the slide need to be copied
	 * @param newName
	 *            the new name of the copied slide
	 * @return the new slide at the destination position with the specified
	 *         name, and it has the same content with the slide at the source
	 *         position.
	 *         <p>
	 *         Throw IndexOutOfBoundsException if the slide index is out of the
	 *         presentation document slide count. If copy the slide at the end
	 *         of document, destIndex should set the same value with the slide
	 *         count.
	 */
	public Slide copySlide(int source, int dest, String newName) {
		checkAllSlideName();
		OfficePresentationElement contentRoot = null;
		try {
			contentRoot = getContentRoot();
		} catch (Exception e) {
			Logger.getLogger(PresentationDocument.class.getName()).log(Level.SEVERE, null, e);
			return null;
		}
		NodeList slideList = contentRoot.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		int slideCount = slideList.getLength();
		if ((source < 0) || (source >= slideCount) || (dest < 0) || (dest > slideCount)) {
			throw new IndexOutOfBoundsException("the specified Index is out of slide count when call copySlide method.");
		}
		DrawPageElement sourceSlideElement = (DrawPageElement) slideList.item(source);
		DrawPageElement cloneSlideElement = (DrawPageElement) sourceSlideElement.cloneNode(true);
		cloneSlideElement.setDrawNameAttribute(newName);
		if (dest == slideCount) {
			contentRoot.appendChild(cloneSlideElement);
		} else {
			DrawPageElement refSlide = (DrawPageElement) slideList.item(dest);
			contentRoot.insertBefore(cloneSlideElement, refSlide);
		}
		adjustNotePageNumber(Math.min(source, dest));
		// in case that the appended new slide have the same name with the
		// original slide
		hasCheckSlideName = false;
		checkAllSlideName();
		return Slide.getInstance(cloneSlideElement);
	}

	/**
	 * Move the slide at a specified position to the destination position.
	 * 
	 * @param source
	 *            the current index of the slide that need to be moved
	 * @param dest
	 *            The index of the destination position before the move action
	 *            <p>
	 *            Throw IndexOutOfBoundsException if the slide index is out of
	 *            the presentation document slide count.
	 */
	public void moveSlide(int source, int dest) {
		checkAllSlideName();
		OfficePresentationElement contentRoot = null;
		try {
			contentRoot = getContentRoot();
		} catch (Exception e) {
			Logger.getLogger(PresentationDocument.class.getName()).log(Level.SEVERE, null, e);
			return;
		}
		NodeList slideList = contentRoot.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		int slideCount = slideList.getLength();
		if ((source < 0) || (source >= slideCount) || (dest < 0) || (dest > slideCount)) {
			throw new IndexOutOfBoundsException("the specified Index is out of slide count when call moveSlide method.");
		}
		DrawPageElement sourceSlide = (DrawPageElement) slideList.item(source);
		if (dest == slideCount) {
			contentRoot.appendChild(sourceSlide);
		} else {
			DrawPageElement refSlide = (DrawPageElement) slideList.item(dest);
			contentRoot.insertBefore(sourceSlide, refSlide);
		}
		adjustNotePageNumber(Math.min(source, dest));
	}

	private Node cloneForeignElement_(Node element, OdfFileDom dom, boolean deep) {
		checkAllSlideName();
		return cloneForeignElement(element, dom, deep);
	}

	/**
	 * Append all the slides of the specified presentation document to the
	 * current document.
	 * 
	 * @param srcDoc
	 *            the specified <code>PresentationDocument</code> that need to
	 *            be appended
	 */
	public void appendPresentation(PresentationDocument srcDoc) {
		checkAllSlideName();
		OfficePresentationElement contentRoot = null;
		OdfFileDom contentDom = null;
		OfficePresentationElement srcContentRoot = null;
		try {
			contentRoot = getContentRoot();
			contentDom = getContentDom();
			srcContentRoot = srcDoc.getContentRoot();
		} catch (Exception e) {
			Logger.getLogger(PresentationDocument.class.getName()).log(Level.SEVERE, null, e);
		}
		NodeList slideList = contentRoot.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		int slideNum = slideList.getLength();
		// clone the srcContentRoot, and make a modification on this clone node.
		OfficePresentationElement srcCloneContentRoot = (OfficePresentationElement) srcContentRoot.cloneNode(true);
		// copy all the referred xlink:href here
		copyLinkedRefInBatch(srcCloneContentRoot, srcDoc);
		// copy all the referred style definition here
		copyForeignStyleRef(srcCloneContentRoot, srcDoc);
		Node child = srcCloneContentRoot.getFirstChild();
		while (child != null) {
			Node cloneElement = cloneForeignElement_(child, contentDom, true);
			contentRoot.appendChild(cloneElement);
			child = child.getNextSibling();
		}
		adjustNotePageNumber(slideNum - 1);

		// in case that the appended new slide have the same name with the
		// original slide
		hasCheckSlideName = false;
		checkAllSlideName();
	}

	/**
	 * Make a copy of slide which locates at the specified position of the
	 * source presentation document and insert it to the current presentation
	 * document at the new position. The original slide which at the dest index
	 * and after the dest index will move after.
	 * 
	 * @param destIndex
	 *            the new position of the copied slide in the current document
	 * @param srcDoc
	 *            the source document of the copied slide
	 * @param srcIndex
	 *            the slide index of the source document that need to be copied
	 * @return the new slide which has the same content with the source slide
	 *         <p>
	 *         Throw IndexOutOfBoundsException if the slide index is out of the
	 *         presentation document slide count If insert the foreign slide at
	 *         the end of document, destIndex should set the same value with the
	 *         slide count of the current presentation document.
	 */
	public Slide copyForeignSlide(int destIndex, PresentationDocument srcDoc, int srcIndex) {
		checkAllSlideName();
		OfficePresentationElement contentRoot = null;
		OdfFileDom contentDom = null;
		try {
			contentRoot = getContentRoot();
			contentDom = getContentDom();
		} catch (Exception e) {
			Logger.getLogger(PresentationDocument.class.getName()).log(Level.SEVERE, null, e);
			return null;
		}
		NodeList slideList = contentRoot.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		int slideCount = slideList.getLength();
		if ((destIndex < 0) || (destIndex > slideCount)) {
			throw new IndexOutOfBoundsException(
					"the specified Index is out of slide count when call copyForeignSlide method.");
		}
		Slide sourceSlide = srcDoc.getSlideByIndex(srcIndex);
		DrawPageElement sourceSlideElement = sourceSlide.getOdfElement();
		// clone the sourceSlideEle, and make a modification on this clone node.
		DrawPageElement sourceCloneSlideElement = (DrawPageElement) sourceSlideElement.cloneNode(true);

		// copy all the referred xlink:href here
		copyLinkedRefInBatch(sourceCloneSlideElement, srcDoc);
		// copy all the referred style definition here
		copyForeignStyleRef(sourceCloneSlideElement, srcDoc);
		// clone the sourceCloneSlideEle, and this cloned element should in the
		// current dom tree
		DrawPageElement cloneSlideElement = (DrawPageElement) cloneForeignElement_(sourceCloneSlideElement, contentDom,
				true);
		if (destIndex == slideCount) {
			contentRoot.appendChild(cloneSlideElement);
		} else {
			DrawPageElement refSlide = (DrawPageElement) slideList.item(destIndex);
			contentRoot.insertBefore(cloneSlideElement, refSlide);
		}
		adjustNotePageNumber(destIndex);
		// in case that the appended new slide have the same name with the
		// original slide
		hasCheckSlideName = false;
		checkAllSlideName();
		return Slide.getInstance(cloneSlideElement);
	}

	/**
	 * New a slide at the specified position with the specified name, and use
	 * the specified slide template. See <code>OdfDrawPage.SlideLayout</code>.
	 * <p>
	 * If index is invalid, such as larger than the current document slide
	 * number or is negative, then append the new slide at the end of the
	 * document.
	 * <p>
	 * The slide name can be null.
	 * 
	 * @param index
	 *            the new slide position
	 * @param name
	 *            the new slide name
	 * @param slideLayout
	 *            the new slide template
	 * @return the new slide which locate at the specified position with the
	 *         specified name and apply the specified slide template. If
	 *         slideLayout is null, then use the default slide template which is
	 *         a blank slide.
	 *         <p>
	 *         Throw IndexOutOfBoundsException if index is out of the
	 *         presentation document slide count.
	 */
	public Slide newSlide(int index, String name, Slide.SlideLayout slideLayout) {
		checkAllSlideName();
		OfficePresentationElement contentRoot = null;
		try {
			contentRoot = getContentRoot();
		} catch (Exception e) {
			Logger.getLogger(PresentationDocument.class.getName()).log(Level.SEVERE, null, e);
			return null;
		}
		NodeList slideList = contentRoot.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		int slideCount = slideList.getLength();
		if ((index < 0) || (index > slideCount)) {
			throw new IndexOutOfBoundsException("the specified Index is out of slide count when call newSlide method.");
		}
		// if insert page at the beginning of the document,
		// get the next page style as the new page style
		// else get the previous page style as the new page style
		DrawPageElement refStyleSlide = null;
		int refSlideIndex = 0;
		if (index > 0) {
			refSlideIndex = index - 1;
		}
		refStyleSlide = (DrawPageElement) slideList.item(refSlideIndex);
		String masterPageStyleName = "Default";
		String masterName = refStyleSlide.getDrawMasterPageNameAttribute();
		if (masterName != null) {
			masterPageStyleName = masterName;
		}
		DrawPageElement newSlideElement = contentRoot.newDrawPageElement(masterPageStyleName);
		newSlideElement.setDrawNameAttribute(name);
		String drawStyleName = refStyleSlide.getDrawStyleNameAttribute();
		if (drawStyleName != null) {
			newSlideElement.setDrawStyleNameAttribute(drawStyleName);
		}
		String pageLayoutName = refStyleSlide.getPresentationPresentationPageLayoutNameAttribute();
		if (pageLayoutName != null) {
			newSlideElement.setPresentationPresentationPageLayoutNameAttribute(pageLayoutName);
		}
		setSlideLayout(newSlideElement, slideLayout);
		// insert notes page
		NodeList noteNodes = refStyleSlide.getElementsByTagNameNS(OdfDocumentNamespace.PRESENTATION.getUri(), "notes");
		if (noteNodes.getLength() > 0) {
			PresentationNotesElement notePage = (PresentationNotesElement) noteNodes.item(0);
			PresentationNotesElement cloneNotePage = (PresentationNotesElement) notePage.cloneNode(true);
			newSlideElement.appendChild(cloneNotePage);
		}
		if (index < slideCount) {
			DrawPageElement refSlide = (DrawPageElement) slideList.item(index);
			contentRoot.insertBefore(newSlideElement, refSlide);
		}
		adjustNotePageNumber(index);
		// in case that the appended new slide have the same name with the
		// original slide
		hasCheckSlideName = false;
		checkAllSlideName();
		return Slide.getInstance(newSlideElement);
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

	public List<Chart> getChartByTitle(String title) {
		return getChartContainerImpl().getChartByTitle(title);
	}

	public int getChartCount() {
		return getChartContainerImpl().getChartCount();
	}
	
	// when insert a slide, the note page for this slide is also inserted.
	// note page refer the slide index in order to show the corresponding slide
	// notes view
	// this function is used to adjust note page referred slide index since
	// startIndex
	// when the slide at startIndex has been delete or insert
	private void adjustNotePageNumber(int startIndex) {
		try {
			OfficePresentationElement contentRoot = getContentRoot();
			NodeList slideList = contentRoot.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
			for (int i = startIndex; i < getSlideCount(); i++) {
				DrawPageElement page = (DrawPageElement) slideList.item(i);
				NodeList noteNodes = page.getElementsByTagNameNS(OdfDocumentNamespace.PRESENTATION.getUri(), "notes");
				if (noteNodes.getLength() > 0) {
					PresentationNotesElement notePage = (PresentationNotesElement) noteNodes.item(0);
					NodeList thumbnailList = notePage.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(),
							"page-thumbnail");
					if (thumbnailList.getLength() > 0) {
						DrawPageThumbnailElement thumbnail = (DrawPageThumbnailElement) thumbnailList.item(0);
						thumbnail.setDrawPageNumberAttribute(i + 1);
					}
				}
			}
		} catch (Exception e) {
			Logger.getLogger(PresentationDocument.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	// covered element
	// <presentation:notes>, <draw:page-thumbnail>, <draw:frame>
	// <style:presentation-page-layout>
	private void setSlideLayout(DrawPageElement page, Slide.SlideLayout slideLayout) {
		if (slideLayout == null) {
			slideLayout = Slide.SlideLayout.BLANK;
		}
		slideLayout.apply(page);
	}

	public OdfElement getTableContainerElement() {
		throw new UnsupportedOperationException("Presentation document is not supported to hold table directly.");
	}
	
	private ChartContainerImpl getChartContainerImpl() {
		if (chartContainerImpl == null) {
			chartContainerImpl = new ChartContainerImpl(this);
		}
		return chartContainerImpl;
	}
	
	private class ChartContainerImpl extends AbstractChartContainer {
		PresentationDocument sdoc;

		protected ChartContainerImpl(Document doc) {
			super(doc);
			sdoc = (PresentationDocument) doc;
		}

		protected DrawFrameElement getChartFrame() throws Exception {
			OdfContentDom contentDom2 = sdoc.getContentDom();
			DrawFrameElement drawFrame = contentDom2.newOdfElement(DrawFrameElement.class);
			DrawPageElement lastPage = (DrawPageElement) contentDom2.getXPath().evaluate("//draw:page[last()]",
					contentDom2, XPathConstants.NODE);
			lastPage.appendChild(drawFrame);
			drawFrame.setTextAnchorTypeAttribute(TextAnchorTypeAttribute.Value.PAGE.toString());
			return drawFrame;
		}
	}
}
