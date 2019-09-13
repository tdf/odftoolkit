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
package org.odftoolkit.odfdom.doc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import org.odftoolkit.odfdom.doc.presentation.OdfSlide;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.odftoolkit.odfdom.dom.attribute.presentation.PresentationClassAttribute;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawPageElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawPageThumbnailElement;
import org.odftoolkit.odfdom.dom.element.office.OfficePresentationElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationNotesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleGraphicPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StylePresentationPageLayoutElement;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.pkg.MediaType;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.pkg.OdfNamespace;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.pkg.OdfPackageDocument;
import org.odftoolkit.odfdom.pkg.manifest.OdfFileEntry;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class represents an empty ODF presentation.
 *
 */
public class OdfPresentationDocument extends OdfDocument {

	private static final String EMPTY_PRESENTATION_DOCUMENT_PATH = "/OdfPresentationDocument.odp";
	static final Resource EMPTY_PRESENTATION_DOCUMENT_RESOURCE = new Resource(EMPTY_PRESENTATION_DOCUMENT_PATH);

	/**
	 * This enum contains all possible media types of OdfPresentationDocument
	 * documents.
	 */
	public enum OdfMediaType implements MediaType {

		PRESENTATION(OdfDocument.OdfMediaType.PRESENTATION),
		PRESENTATION_TEMPLATE(OdfDocument.OdfMediaType.PRESENTATION_TEMPLATE);
		private final OdfDocument.OdfMediaType mMediaType;

		OdfMediaType(OdfDocument.OdfMediaType mediaType) {
			this.mMediaType = mediaType;
		}

		/**
		 * @return the ODF mediatype of this document
		 */
		public OdfDocument.OdfMediaType getOdfMediaType() {
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
		 * @param mediaType string defining an ODF document
		 * @return the according OdfMediatype encapuslating the given string and the suffix
		 */
		public static OdfDocument.OdfMediaType getOdfMediaType(String mediaType) {
			return OdfDocument.OdfMediaType.getOdfMediaType(mediaType);
		}
	}

	/**
	 * Creates an empty presentation document.
	 * @return ODF presentation document based on a default template
	 * @throws java.lang.Exception - if the document could not be created
	 */
	public static OdfPresentationDocument newPresentationDocument() throws Exception {
		return (OdfPresentationDocument) OdfDocument.loadTemplate(EMPTY_PRESENTATION_DOCUMENT_RESOURCE, OdfDocument.OdfMediaType.PRESENTATION);
	}

	/**
	 * Creates an empty presentation template.
	 * @return ODF presentation template based on a default
	 * @throws Exception - if the template could not be created
	 */
	public static OdfPresentationDocument newPresentationTemplateDocument() throws Exception {
		OdfPresentationDocument doc = (OdfPresentationDocument) OdfDocument.loadTemplate(EMPTY_PRESENTATION_DOCUMENT_RESOURCE, OdfDocument.OdfMediaType.PRESENTATION_TEMPLATE);
		doc.changeMode(OdfMediaType.PRESENTATION_TEMPLATE);
		return doc;
	}

	/** To avoid data duplication a new document is only created, if not already opened.
	 * A document is cached by this constructor using the internalpath as key. */
	protected OdfPresentationDocument(OdfPackage pkg, String internalPath, OdfPresentationDocument.OdfMediaType odfMediaType) throws SAXException {
		super(pkg, internalPath, odfMediaType.mMediaType);
	}

	/**
	 * Creates an OdfPresentationDocument from the OpenDocument provided by a resource Stream.
	 *
	 * <p>Since an InputStream does not provide the arbitrary (non sequentiell)
	 * read access needed by OdfPresentationDocument, the InputStream is cached. This usually
	 * takes more time compared to the other createInternalDocument methods.
	 * An advantage of caching is that there are no problems overwriting
	 * an input file.</p>
	 *
	 * <p>If the resource stream is not a ODF presentation document, ClassCastException might be thrown.</p>
	 *
	 * @param inputStream - the InputStream of the ODF presentation document.
	 * @return the presentation document created from the given InputStream
	 * @throws java.lang.Exception - if the document could not be created.
	 */
	public static OdfPresentationDocument loadDocument(InputStream inputStream) throws Exception {
		return (OdfPresentationDocument) OdfDocument.loadDocument(inputStream);
	}

	/**
	 * Loads an OdfPresentationDocument from the provided path.
	 *
	 * <p>OdfPresentationDocument relies on the file being available for read access over
	 * the whole lifecycle of OdfPresentationDocument.</p>
	 *
	 * <p>If the resource stream is not a ODF presentation document, ClassCastException might be thrown.</p>
	 *
	 * @param documentPath - the path from where the document can be loaded
	 * @return the presentation document from the given path
	 *		  or NULL if the media type is not supported by ODFDOM.
	 * @throws java.lang.Exception - if the document could not be created.
	 */
	public static OdfPresentationDocument loadDocument(String documentPath) throws Exception {
		return (OdfPresentationDocument) OdfDocument.loadDocument(documentPath);
	}

	/**
	 * Creates an OdfPresentationDocument from the OpenDocument provided by a File.
	 *
	 * <p>OdfPresentationDocument relies on the file being available for read access over
	 * the whole lifecycle of OdfPresentationDocument.</p>
	 *
	 * <p>If the resource stream is not a ODF presentation document, ClassCastException might be thrown.</p>
	 *
	 * @param file - a file representing the ODF presentation document.
	 * @return the presentation document created from the given File
	 * @throws java.lang.Exception - if the document could not be created.
	 */
	public static OdfPresentationDocument loadDocument(File file) throws Exception {
		return (OdfPresentationDocument) OdfDocument.loadDocument(file);
	}

	/**
	 * Get the content root of a presentation document.
	 *
	 * @return content root, representing the office:presentation tag
	 * @throws Exception if the file DOM could not be created.
	 */
	@Override
	public OfficePresentationElement getContentRoot() throws Exception {
		return super.getContentRoot(OfficePresentationElement.class);
	}

	/**
	 * Switches this instance to the given type. This method can be used to e.g.
	 * convert a document instance to a template and vice versa.
	 * Changes take affect in the package when saving the document.
	 *
	 * @param type the compatible ODF mediatype.
	 */
	public void changeMode(OdfMediaType type) {
		setOdfMediaType(type.mMediaType);
	}
	private boolean hasCheckSlideName = false;
	//if the copy foreign slide for several times,
	//the same style might be copied for several times with the different name
	//so use styleRenameMap to keep track the renamed style so we can reuse the style,
	//rather than new several styles which only have the different style names.
	//while if the style elements really have the same style name but with different content
	//such as that these style elements are from different document
	//so the value for each key should be a list
	private HashMap<String, List<String>> styleRenameMap = new HashMap<String, List<String>>();
	//the map is used to record if the renamed style name is appended to the current dom
	private HashMap<String, Boolean> styleAppendMap = new HashMap<String, Boolean>();
	//the object rename map for image.
	//can not easily recognize if the embedded document are the same.
//	private HashMap<String, String> objectRenameMap = new HashMap<String, String>();

	/**
	 * Return the slide at a specified position in this presentation.
	 * Return null if the index is out of range.
	 *
	 * @param index	the index of the slide to be returned
	 * @return	a draw slide at the specified position
	 */
	public OdfSlide getSlideByIndex(int index) {
		checkAllSlideName();
		OfficePresentationElement contentRoot = null;
		try {
			contentRoot = getContentRoot();
		} catch (Exception e) {
			Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, e);
			return null;
		}
		NodeList slideNodes = contentRoot.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		if ((index >= slideNodes.getLength()) || (index < 0)) {
			return null;
		}
		DrawPageElement slideElement = (DrawPageElement) slideNodes.item(index);
		return OdfSlide.getInstance(slideElement);
	}

	/**
	 * Get the number of the slides in this presentation.
	 *
	 * @return	the number of slides
	 */
	public int getSlideCount() {
		checkAllSlideName();
		OfficePresentationElement contentRoot = null;
		try {
			contentRoot = getContentRoot();
		} catch (Exception e) {
			Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, e);
			return 0;
		}
		NodeList slideNodes = contentRoot.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		return slideNodes.getLength();
	}

	/**
	 * Return the slide which have a specified slide name in this presentation.
	 * <p>
	 * According to the odf specification
	 * "The draw:name attribute specifies a name by which this element can be referenced.
	 * It is optional but if present, must be unique within the document instance.
	 * If not present, an application may generate a unique name."
	 * <p>
	 * If the name is null, then return null because all the slide must has its own unique name.
	 *
	 * @param name	the specified slide name
	 * @return	the slide whose name equals to the specified name
	 */
	public OdfSlide getSlideByName(String name) {
		checkAllSlideName();
		if (name == null) {
			return null;
		}
		OfficePresentationElement contentRoot = null;
		try {
			contentRoot = getContentRoot();
		} catch (Exception e) {
			Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, e);
			return null;
		}
		NodeList slideNodes = contentRoot.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		for (int i = 0; i < slideNodes.getLength(); i++) {
			DrawPageElement slideElement = (DrawPageElement) slideNodes.item(i);
			OdfSlide slide = OdfSlide.getInstance(slideElement);
			String slideName = slide.getSlideName();
			if (slideName.equals(name)) {
				return slide;
			}
		}
		return null;
	}

	//when access slide related method, this function should be called
	private void checkAllSlideName() {
		//check if this function is called or not
		if (hasCheckSlideName) {
			return;
		}
		List<String> slideNameList = new ArrayList<String>();
		OfficePresentationElement contentRoot = null;
		try {
			contentRoot = getContentRoot();
		} catch (Exception e) {
			Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, e);
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
	 * @return	a list iterator containing all slides in this presentation
	 */
	public Iterator<OdfSlide> getSlides() {
		checkAllSlideName();
		OfficePresentationElement contentRoot = null;
		try {
			contentRoot = getContentRoot();
		} catch (Exception e) {
			Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, e);
			return null;
		}
		ArrayList<OdfSlide> slideList = new ArrayList<OdfSlide>();
		NodeList slideNodes = contentRoot.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		for (int i = 0; i < slideNodes.getLength(); i++) {
			DrawPageElement slideElement = (DrawPageElement) slideNodes.item(i);
			slideList.add(OdfSlide.getInstance(slideElement));
		}
		return slideList.iterator();
	}

	/**
	 * Delete the slide at a specified position in this presentation.
	 *
	 * @param index	the index of the slide that need to be delete
	 * <p>
	 * Throw IndexOutOfBoundsException if the slide index is out of the presentation document slide count.
	 * @return false if the operation was not successful
	 */
	public boolean deleteSlideByIndex(int index) {
		boolean success = true;
		checkAllSlideName();
		OfficePresentationElement contentRoot = null;
		try {
			contentRoot = getContentRoot();
		} catch (Exception e) {
			Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, e);
			success = false;
			return success;
		}
		NodeList slideNodes = contentRoot.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		if ((index >= slideNodes.getLength()) || (index < 0)) {
			throw new IndexOutOfBoundsException("the specified Index is out of slide count when call deleteSlideByIndex method.");
		}
		DrawPageElement slideElement = (DrawPageElement) slideNodes.item(index);
		//remove all the content of the current page
		//1. the reference of the path that contained in this slide is 1, then remove it
		success &= deleteLinkRef(slideElement);
		//2.the reference of the style is 1, then remove it
		//in order to save time, do not delete the style here
		success &= deleteStyleRef(slideElement);
		//remove the current page element
		contentRoot.removeChild(slideElement);
		adjustNotePageNumber(index);
		return success;
	}

	private boolean deleteStyleRef(DrawPageElement slideEle) {
		boolean success = true;
		try {
			//method 1:
			//1.1. iterate child element of the content element
			//1.2. if the child element is an OdfStylableElement, get the style-name ref count
			////////////////
			//method 2:
			//2.1. get the list of the style definition
			ArrayList<OdfElement> removeStyles = new ArrayList<OdfElement>();
			OdfOfficeAutomaticStyles autoStyles = getContentDom().getAutomaticStyles();

			NodeList stylesList = autoStyles.getChildNodes();
			OdfContentDom contentDom = getContentDom();
			XPath xpath = contentDom.getXPath();

			//2.2. get the reference of each style which occurred in the current page
			for (int i = 0; i < stylesList.getLength(); i++) {
				Node item = stylesList.item(i);
				if (item instanceof OdfElement) {
					OdfElement node = (OdfElement) item;
					String styleName = node.getAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "name");
					if (styleName != null) {
						//search the styleName contained at the current page element
						NodeList styleNodes = (NodeList) xpath.evaluate("//*[@*='" + styleName + "']", contentDom, XPathConstants.NODESET);
						int styleCnt = styleNodes.getLength();
						if (styleCnt > 1) {
							//the first styleName is occurred in the style definition
							//so check if the second styleName and last styleName is occurred in the current page element
							//if yes, then remove it
							OdfElement elementFirst = (OdfElement) styleNodes.item(1);
							OdfElement elementLast = (OdfElement) styleNodes.item(styleCnt - 1);
							boolean isSamePage = false;
							if (elementFirst instanceof DrawPageElement) {
								DrawPageElement tempPage = (DrawPageElement) elementFirst;
								if (tempPage.equals(slideEle)) {
									isSamePage = true;
								}
							}
							int relationFirst = slideEle.compareDocumentPosition(elementFirst);
							int relationLast = slideEle.compareDocumentPosition(elementLast);
							//if slide element contains the child element which has the styleName reference
							if (((relationFirst & Node.DOCUMENT_POSITION_CONTAINED_BY) > 0
									&& (relationLast & Node.DOCUMENT_POSITION_CONTAINED_BY) > 0)
									|| (isSamePage && (styleCnt == 1))) {
								if (node instanceof OdfStyleBase) {
									removeStyles.add(node);
								}
							}
						} else {
							continue;
						}
					}
				}
			}
			for (int i = 0; i < removeStyles.size(); i++) {
				autoStyles.removeChild(removeStyles.get(i));
			}
		} catch (Exception e) {
			Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, e);
			success = false;
		}
		return success;
	}

	//delete all the xlink:href object which is contained in slideElement and does not referred by other slides
	private boolean deleteLinkRef(DrawPageElement slideEle) {
		boolean success = true;
		try {
			OdfContentDom contentDom = getContentDom();
			XPath xpath = contentDom.getXPath();
			NodeList linkNodes = (NodeList) xpath.evaluate("//*[@xlink:href]", contentDom, XPathConstants.NODESET);
			for (int i = 0; i < linkNodes.getLength(); i++) {
				OdfElement object = (OdfElement) linkNodes.item(i);
				String refObjPath = object.getAttributeNS(OdfDocumentNamespace.XLINK.getUri(), "href");
				int relation = slideEle.compareDocumentPosition(object);
				//if slide element contains the returned element which has the xlink:href reference
				if ((relation & Node.DOCUMENT_POSITION_CONTAINED_BY) > 0 && refObjPath != null && refObjPath.length() > 0) {
					//the path of the object is start with "./"
					NodeList pathNodes = (NodeList) xpath.evaluate("//*[@xlink:href='" + refObjPath + "']", getContentDom(), XPathConstants.NODESET);
					int refCount = pathNodes.getLength();
					if (refCount == 1) {
						//delete "./"
						if (refObjPath.startsWith("./")) {
							refObjPath = refObjPath.substring(2);
						}
						//check if the current document contains the same path
						OdfFileEntry fileEntry = getPackage().getFileEntry(refObjPath);
						if (fileEntry != null) {
							//it is a stream, such as image, binary file
							getPackage().remove(refObjPath);
						} else {
							//note: if refObjPath is a directory, it must end with '/'
							fileEntry = getPackage().getFileEntry(refObjPath + "/");
							removeDocument(refObjPath);
						}
					}
				}
			}
		} catch (XPathExpressionException e) {
			Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, e);
			success = false;
		} catch (Exception e) {
			Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, e);
			success = false;
		}
		return success;
	}

	/**
	 * Delete all the slides with a specified name in this presentation.
	 *
	 * @param name	the name of the slide that need to be delete
	 * @return false if the operation was not successful
	 */
	public boolean deleteSlideByName(String name) {
		boolean success = true;
		checkAllSlideName();
		OfficePresentationElement contentRoot = null;
		try {
			contentRoot = getContentRoot();
		} catch (Exception e) {
			Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, e);
			success = false;
			return success;
		}
		OdfSlide slide = getSlideByName(name);
		DrawPageElement slideElement = slide.getOdfElement();
		//remove all the content of the current page
		//1. the reference of the path that contained in this slide is 1, then remove its
		success &= deleteLinkRef(slideElement);
		//2.the reference of the style is 1, then remove it
		//in order to save time, do not delete style here
		success &= deleteStyleRef(slideElement);
		//remove the current page element
		contentRoot.removeChild(slideElement);
		adjustNotePageNumber(0);
		return success;
	}

	/**
	 * Make a copy of the slide at a specified position to another position in this presentation.
	 * The original slide which at the dest index and after the dest index will move after.
	 * <p>
	 * @param source	the source position of the slide need to be copied
	 * @param dest		the destination position of the slide need to be copied
	 * @param newName	the new name of the copied slide
	 * @return the new slide at the destination position with the specified name, and it has the same content
	 * with the slide at the source position.
	 * <p>
	 * Throw IndexOutOfBoundsException if the slide index is out of the presentation document slide count.
	 * If copy the slide at the end of document, destIndex should set the same value with the slide count.
	 */
	public OdfSlide copySlide(int source, int dest, String newName) {
		checkAllSlideName();
		OfficePresentationElement contentRoot = null;
		try {
			contentRoot = getContentRoot();
		} catch (Exception e) {
			Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, e);
			return null;
		}
		NodeList slideList = contentRoot.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		int slideCount = slideList.getLength();
		if ((source < 0) || (source >= slideCount)
				|| (dest < 0) || (dest > slideCount)) {
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
		//in case that the appended new slide have the same name with the original slide
		hasCheckSlideName = false;
		checkAllSlideName();
		return OdfSlide.getInstance(cloneSlideElement);
	}

	/**
	 * Move the slide at a specified position to the destination position.
	 *
	 * @param source	the current index of the slide that need to be moved
	 * @param dest		The index of the destination position before the move action
	 * <p>
	 * Throw IndexOutOfBoundsException if the slide index is out of the presentation document slide count.
	 */
	public void moveSlide(int source, int dest) {
		checkAllSlideName();
		OfficePresentationElement contentRoot = null;
		try {
			contentRoot = getContentRoot();
		} catch (Exception e) {
			Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, e);
			return;
		}
		NodeList slideList = contentRoot.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		int slideCount = slideList.getLength();
		if ((source < 0) || (source >= slideCount)
				|| (dest < 0) || (dest > slideCount)) {
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

	/**
	 * Append all the slides of the specified presentation document to the current document.
	 * @param srcDoc	the specified <code>OdfPresentationDocument</code> that need to be appended
	 */
	public void appendPresentation(OdfPresentationDocument srcDoc) {
		checkAllSlideName();
		OfficePresentationElement contentRoot = null;
		OdfFileDom contentDom = null;
		OfficePresentationElement srcContentRoot = null;
		try {
			contentRoot = getContentRoot();
			contentDom = getContentDom();
			srcContentRoot = srcDoc.getContentRoot();
		} catch (Exception e) {
			Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, e);
		}
		NodeList slideList = contentRoot.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		int slideNum = slideList.getLength();
		//clone the srcContentRoot, and make a modification on this clone node.
		OfficePresentationElement srcCloneContentRoot = (OfficePresentationElement) srcContentRoot.cloneNode(true);
		//copy all the referred xlink:href here
		copyForeignLinkRef(srcCloneContentRoot);
		//copy all the referred style definition here
		copyForeignStyleRef(srcCloneContentRoot, srcDoc);
		Node child = srcCloneContentRoot.getFirstChild();
		while (child != null) {
			Node cloneElement = cloneForeignElement(child, contentDom, true);
			contentRoot.appendChild(cloneElement);
			child = child.getNextSibling();
		}
		adjustNotePageNumber(slideNum - 1);

		//in case that the appended new slide have the same name with the original slide
		hasCheckSlideName = false;
		checkAllSlideName();
	}

	/**
	 * Make a copy of slide which locates at the specified position of the source presentation document
	 * and insert it to the current presentation document at the new position.
	 * The original slide which at the dest index and after the dest index will move after.
	 * @param destIndex	the new position of the copied slide in the current document
	 * @param srcDoc	the source document of the copied slide
	 * @param srcIndex	the slide index of the source document that need to be copied
	 * @return the new slide which has the same content with the source slide
	 * <p>
	 * Throw IndexOutOfBoundsException if the slide index is out of the presentation document slide count
	 * If insert the foreign slide at the end of document, destIndex should set the same value
	 * with the slide count of the current presentation document.
	 */
	public OdfSlide copyForeignSlide(int destIndex,
			OdfPresentationDocument srcDoc, int srcIndex) {
		checkAllSlideName();
		OfficePresentationElement contentRoot = null;
		OdfFileDom contentDom = null;
		try {
			contentRoot = getContentRoot();
			contentDom = getContentDom();
		} catch (Exception e) {
			Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, e);
			return null;
		}
		NodeList slideList = contentRoot.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		int slideCount = slideList.getLength();
		if ((destIndex < 0) || (destIndex > slideCount)) {
			throw new IndexOutOfBoundsException("the specified Index is out of slide count when call copyForeignSlide method.");
		}
		OdfSlide sourceSlide = srcDoc.getSlideByIndex(srcIndex);
		DrawPageElement sourceSlideElement = sourceSlide.getOdfElement();
		//clone the sourceSlideEle, and make a modification on this clone node.
		DrawPageElement sourceCloneSlideElement = (DrawPageElement) sourceSlideElement.cloneNode(true);

		//copy all the referred xlink:href here
		copyForeignLinkRef(sourceCloneSlideElement);
		//copy all the referred style definition here
		copyForeignStyleRef(sourceCloneSlideElement, srcDoc);
		//clone the sourceCloneSlideEle, and this cloned element should in the current dom tree
		DrawPageElement cloneSlideElement = (DrawPageElement) cloneForeignElement(sourceCloneSlideElement, contentDom, true);
		if (destIndex == slideCount) {
			contentRoot.appendChild(cloneSlideElement);
		} else {
			DrawPageElement refSlide = (DrawPageElement) slideList.item(destIndex);
			contentRoot.insertBefore(cloneSlideElement, refSlide);
		}
		adjustNotePageNumber(destIndex);
		//in case that the appended new slide have the same name with the original slide
		hasCheckSlideName = false;
		checkAllSlideName();
		return OdfSlide.getInstance(cloneSlideElement);
	}

	//clone the source clone element's referred object path to the current package
	//if the current package contains the same name with the referred object path,
	//rename the object path and path reference of this slide element
	//notes: the source clone element is the copied one to avoid changing the content of the source document.
	private void copyForeignLinkRef(OdfElement sourceCloneEle) {
		try {
			OdfFileDom fileDom = (OdfFileDom) sourceCloneEle.getOwnerDocument();
			XPath xpath;
			if (fileDom instanceof OdfContentDom) {
				xpath = ((OdfContentDom) fileDom).getXPath();
			} else {
				xpath = ((OdfStylesDom) fileDom).getXPath();
			}
			OdfPackageDocument srcDoc = fileDom.getDocument();
			//new a map to put the original name and the rename string, in case that the same name might be referred by the slide several times.
			HashMap<String, String> objectRenameMap = new HashMap<String, String>();
			NodeList linkNodes = (NodeList) xpath.evaluate(".//*[@xlink:href]", sourceCloneEle, XPathConstants.NODESET);
			for (int i = 0; i <= linkNodes.getLength(); i++) {
				OdfElement object = null;
				if (linkNodes.getLength() == i) {
					if (sourceCloneEle.hasAttributeNS(OdfDocumentNamespace.XLINK.getUri(), "href")) {
						object = sourceCloneEle;
					} else {
						break;
					}
				} else {
					object = (OdfElement) linkNodes.item(i);
				}
				String refObjPath = object.getAttributeNS(OdfDocumentNamespace.XLINK.getUri(), "href");
				if (refObjPath != null && refObjPath.length() > 0) {
					//the path of the object is start with "./"
					boolean hasPrefix = false;
					String prefix = "./";
					if (refObjPath.startsWith(prefix)) {
						refObjPath = refObjPath.substring(2);
						hasPrefix = true;
					}
					//check if the current document contains the same path
					OdfFileEntry fileEntry = getPackage().getFileEntry(refObjPath);
					//note: if refObjPath is a directory, it must end with '/'
					if (fileEntry == null) {
						fileEntry = getPackage().getFileEntry(refObjPath + "/");
					}
					String newObjPath = refObjPath;
					if (fileEntry != null) {
						//rename the object path
						newObjPath = objectRenameMap.get(refObjPath);
						if (newObjPath == null) {
							//if refObjPath still contains ".", it means that it has the suffix
							//then change the name before the suffix string
							int dotIndex = refObjPath.indexOf(".");
							if (dotIndex != -1) {
								newObjPath = refObjPath.substring(0, dotIndex) + "-" + makeUniqueName() + refObjPath.substring(dotIndex);
							} else {
								newObjPath = refObjPath + "-" + makeUniqueName();
							}
							objectRenameMap.put(refObjPath, newObjPath);
						}
						object.setAttributeNS(OdfDocumentNamespace.XLINK.getUri(), "xlink:href", hasPrefix ? (prefix + newObjPath) : newObjPath);
					}
					InputStream is = srcDoc.getPackage().getInputStream(refObjPath);
					if (is != null) {
						String mediaType = srcDoc.getPackage().getFileEntry(refObjPath).getMediaTypeString();
						getPackage().insert(is, newObjPath, mediaType);
					} else {
						OdfDocument embedDoc = (OdfDocument) srcDoc.loadSubDocument(refObjPath);
						if (embedDoc != null) {
							insertDocument(embedDoc, newObjPath);
						}
					}
				}
			}
		} catch (Exception e) {
			Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	private void copyForeignStyleRef(OdfElement sourceCloneEle,
			OdfPresentationDocument doc) {
		try {
			OdfContentDom contentDom = getContentDom();
			XPath xpath = contentDom.getXPath();
			//1. collect all the referred style element which has "style:name" attribute
			//1.1. style:name of content.xml
			String styleQName = "style:name";
			NodeList srcStyleDefNodeList = (NodeList) xpath.evaluate("//*[@" + styleQName + "]", contentDom, XPathConstants.NODESET);
			HashMap<OdfElement, List<OdfElement>> srcContentStyleCloneEleList = new HashMap<OdfElement, List<OdfElement>>();
			HashMap<OdfElement, OdfElement> appendContentStyleList = new HashMap<OdfElement, OdfElement>();
			getCopyStyleList(null, sourceCloneEle, styleQName, srcStyleDefNodeList, srcContentStyleCloneEleList, appendContentStyleList, true);
			//1.2. style:name of styles.xml
			srcStyleDefNodeList = (NodeList) xpath.evaluate("//*[@" + styleQName + "]", doc.getStylesDom(), XPathConstants.NODESET);
			HashMap<OdfElement, List<OdfElement>> srcStylesStyleCloneEleList = new HashMap<OdfElement, List<OdfElement>>();
			HashMap<OdfElement, OdfElement> appendStylesStyleList = new HashMap<OdfElement, OdfElement>();
			getCopyStyleList(null, sourceCloneEle, styleQName, srcStyleDefNodeList, srcStylesStyleCloneEleList, appendStylesStyleList, true);
			//1.3 rename, copy the referred style element to the corresponding position in the dom tree
			insertCollectedStyle(styleQName, srcContentStyleCloneEleList, getContentDom(), appendContentStyleList);
			insertCollectedStyle(styleQName, srcStylesStyleCloneEleList, getStylesDom(), appendStylesStyleList);


			//2. collect all the referred style element which has "draw:name" attribute
			//2.1 draw:name of styles.xml
			//the value of draw:name is string or StyleName,
			//only when the value is StyleName type, the style definition should be cloned to the destination document
			//in ODF spec, such attribute type is only exist in <office:styles> element, so only search it in styles.xml dom
			styleQName = "draw:name";
			srcStyleDefNodeList = (NodeList) xpath.evaluate("//*[@" + styleQName + "]", doc.getStylesDom(), XPathConstants.NODESET);
			HashMap<OdfElement, List<OdfElement>> srcDrawStyleCloneEleList = new HashMap<OdfElement, List<OdfElement>>();
			HashMap<OdfElement, OdfElement> appendDrawStyleList = new HashMap<OdfElement, OdfElement>();
			Iterator<OdfElement> iter = appendContentStyleList.keySet().iterator();
			while (iter.hasNext()) {
				OdfElement styleElement = iter.next();
				OdfElement cloneStyleElement = appendContentStyleList.get(styleElement);
				getCopyStyleList(styleElement, cloneStyleElement, styleQName, srcStyleDefNodeList, srcDrawStyleCloneEleList, appendDrawStyleList, false);
			}
			iter = appendStylesStyleList.keySet().iterator();
			while (iter.hasNext()) {
				OdfElement styleElement = iter.next();
				OdfElement cloneStyleElement = appendStylesStyleList.get(styleElement);
				getCopyStyleList(styleElement, cloneStyleElement, styleQName, srcStyleDefNodeList, srcDrawStyleCloneEleList, appendDrawStyleList, false);
			}
			//2.2 rename, copy the referred style element to the corresponding position in the dom tree
			//note: "draw:name" style element only exist in styles.dom
			insertCollectedStyle(styleQName, srcDrawStyleCloneEleList, getStylesDom(), appendDrawStyleList);

		} catch (Exception e) {
			Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, e);
		}

	}

	//1. modified the style name of the style definition element which has the same name with the source document
	//2. As to the style definition which match 1) condition, modified the referred style name of the element which reference this style
	//3. All the style which also contains other style reference, should be copied to the source document.
	private void insertCollectedStyle(String styleQName,
			HashMap<OdfElement, List<OdfElement>> srcStyleCloneEleList, OdfFileDom dom, HashMap<OdfElement, OdfElement> appendStyleList) {
		try {
			String stylePrefix = OdfNamespace.getPrefixPart(styleQName);
			String styleLocalName = OdfNamespace.getLocalPart(styleQName);
			String styleURI = OdfDocumentNamespace.STYLE.getUri();
			// is the DOM always the styles.xml
			XPath xpath = dom.getXPath();
			NodeList destStyleNodeList = (NodeList) xpath.evaluate("//*[@" + styleQName + "]", dom, XPathConstants.NODESET);

//			HashMap<String, String> styleRenameMap = new HashMap<String, String>();
			Iterator<OdfElement> iter = srcStyleCloneEleList.keySet().iterator();
			while (iter.hasNext()) {
				OdfElement styleElement = iter.next();
				OdfElement cloneStyleElement = appendStyleList.get(styleElement);
				if (cloneStyleElement == null) {
					cloneStyleElement = (OdfElement) styleElement.cloneNode(true);
					appendStyleList.put(styleElement, cloneStyleElement);
				}
				String styleName = styleElement.getAttributeNS(styleURI, styleLocalName);
				List<String> newStyleNameList = styleRenameMap.get(styleName);
				// if the newStyleNameList != null, means that styleName exists in dest document
				// and it has already been renamed
				if ((newStyleNameList != null)
						|| (isStyleNameExist(destStyleNodeList, styleName) != null)) {
					String newStyleName = null;
					if (newStyleNameList == null) {
						newStyleNameList = new ArrayList<String>();
						newStyleName = styleName + "-" + makeUniqueName();
						newStyleNameList.add(newStyleName);
						styleRenameMap.put(styleName, newStyleNameList);
					} else {
						for (int i = 0; i < newStyleNameList.size(); i++) {
							String styleNameIter = newStyleNameList.get(i);
							OdfElement destStyleElementWithNewName = isStyleNameExist(destStyleNodeList, styleNameIter);
							//check if the two style elements have the same content
							//if not, the cloneStyleElement should rename, rather than reuse the new style name
							cloneStyleElement.setAttributeNS(styleURI, styleQName, styleNameIter);
							if ((destStyleElementWithNewName != null) && destStyleElementWithNewName.equals(cloneStyleElement)) {
								newStyleName = styleNameIter;
								break;
							}
						}
						if (newStyleName == null) {
							newStyleName = styleName + "-" + makeUniqueName();
							newStyleNameList.add(newStyleName);
						}
					}
					// if newStyleName has been set in the element as the new name
					// which means that the newStyleName is conform to the odf spec
					// then change element style reference name
					if (changeStyleRefName(srcStyleCloneEleList.get(styleElement), styleName, newStyleName)) {
						cloneStyleElement.setAttributeNS(styleURI, styleQName, newStyleName);
						//if display name should also be renamed
						String displayName = cloneStyleElement.getAttributeNS(styleURI, "display-name");
						if ((displayName != null) && (displayName.length() > 0)) {
							cloneStyleElement.setAttributeNS(styleURI, stylePrefix + ":display-name",
									displayName + newStyleName.substring(newStyleName.length() - 8));
						}
					}

				}
			}

			iter = appendStyleList.keySet().iterator();
			while (iter.hasNext()) {
				OdfElement styleElement = iter.next();
				OdfElement cloneStyleElement = appendStyleList.get(styleElement);
				String newStyleName = cloneStyleElement.getAttributeNS(styleURI, styleLocalName);
				Boolean isAppended = styleAppendMap.get(newStyleName);
				//if styleAppendMap contain the newStyleName,
				//means that cloneStyleElement has already been appended
				if ((isAppended != null) && isAppended.booleanValue() == true) {
					continue;
				} else {
					styleAppendMap.put(newStyleName, true);
				}
				OdfElement cloneForeignStyleElement = (OdfElement) cloneForeignElement(cloneStyleElement, dom, true);
				String styleElePath = getElementPath(styleElement);
				appendForeignStyleElement(cloneForeignStyleElement, dom, styleElePath);
				copyForeignLinkRef(cloneStyleElement);
			}
		} catch (Exception e) {
			Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, e);
		}

	}

	//get all the copy of referred style element which is directly referred or indirectly referred by cloneEle
	//all the style are defined in srcStyleNodeList
	//and these style are all have the styleName defined in styleQName attribute
	//the key of copyStyleEleList is the style definition element
	//the value of the corresponding key is the clone of the element which refer to the key,
	//the cloned element can be the content of slide or the style element.
	//the key of appendStyleList is the style definition element which has the other style reference
	//the value of the corresponding key is the the style definition clone element
	//loop means if recursive call this function
	//if loop == true, get the style definition element reference other style definition element
	private void getCopyStyleList(OdfElement ele, OdfElement cloneEle, String styleQName, NodeList srcStyleNodeList,
			HashMap<OdfElement, List<OdfElement>> copyStyleEleList, HashMap<OdfElement, OdfElement> appendStyleList, boolean loop) {
		try {
			String styleLocalName = OdfNamespace.getLocalPart(styleQName);
			String styleURI = OdfDocumentNamespace.STYLE.getUri();
			//OdfElement override the "toString" method
			String cloneEleStr = cloneEle.toString();
			for (int i = 0; i < srcStyleNodeList.getLength(); i++) {
				OdfElement styleElement = (OdfElement) srcStyleNodeList.item(i);
				String styleName = styleElement.getAttributeNS(styleURI, styleLocalName);
				if (styleName != null) {
					int index = 0;
					index = cloneEleStr.indexOf("=\"" + styleName + "\"", index);
					while (index >= 0) {
						String subStr = cloneEleStr.substring(0, index);
						int lastSpaceIndex = subStr.lastIndexOf(' ');
						String attrStr = subStr.substring(lastSpaceIndex + 1, index);
						XPath xpath = ((OdfFileDom) cloneEle.getOwnerDocument()).getXPath();
						NodeList styleRefNodes = (NodeList) xpath.evaluate(".//*[@" + attrStr + "='" + styleName + "']", cloneEle, XPathConstants.NODESET);
						boolean isExist = false;
						for (int j = 0; j <= styleRefNodes.getLength(); j++) {
							OdfElement styleRefElement = null;
							if (j == styleRefNodes.getLength()) {
								isExist = isStyleNameRefExist(cloneEle, styleName, false);
								if (isExist) {
									styleRefElement = cloneEle;
								} else {
									continue;
								}
							} else {
								OdfElement tmpElement = (OdfElement) styleRefNodes.item(j);
								if (isStyleNameRefExist(tmpElement, styleName, false)) {
									styleRefElement = tmpElement;
								} else {
									continue;
								}
							}
							boolean hasLoopStyleDef = true;
							if (copyStyleEleList.get(styleElement) == null) {
								List<OdfElement> styleRefEleList = new ArrayList<OdfElement>();
								copyStyleEleList.put(styleElement, styleRefEleList);
								hasLoopStyleDef = false;
							}
							copyStyleEleList.get(styleElement).add(styleRefElement);

							OdfElement cloneStyleElement = appendStyleList.get(styleElement);
							if (cloneStyleElement == null) {
								cloneStyleElement = (OdfElement) styleElement.cloneNode(true);
								appendStyleList.put(styleElement, cloneStyleElement);
							}
							if (loop && !hasLoopStyleDef) {
								getCopyStyleList(styleElement, cloneStyleElement, styleQName, srcStyleNodeList, copyStyleEleList, appendStyleList, loop);
							}
						}
						index = cloneEleStr.indexOf("=\"" + styleName + "\"", index + styleName.length());
					}
				}
			}
		} catch (Exception e) {
			Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, e);
		}
	}
	//append the cloneStyleElement to the contentDom which position is defined by styleElePath

	private void appendForeignStyleElement(OdfElement cloneStyleEle,
			OdfFileDom dom, String styleElePath) {
		StringTokenizer token = new StringTokenizer(styleElePath, "/");
		boolean isExist = true;
		Node iterNode = dom.getFirstChild();
		Node parentNode = dom;
		while (token.hasMoreTokens()) {
			String onePath = token.nextToken();

			while ((iterNode != null) && isExist) {
				String path = iterNode.getNamespaceURI();
				String prefix = iterNode.getPrefix();
				if (prefix == null) {
					path += "@" + iterNode.getLocalName();
				} else {
					path += "@" + prefix + ":" + iterNode.getLocalName();
				}
				if (!path.equals(onePath)) {
					//not found, then get the next sibling to find such path node
					iterNode = iterNode.getNextSibling();
				} else {
					//found, then get the child nodes to find the next path node
					parentNode = iterNode;
					iterNode = iterNode.getFirstChild();
					break;
				}
			}

			if (iterNode == null) {
				//should new the element since the current path node
				if (isExist) {
					isExist = false;
				}
				StringTokenizer token2 = new StringTokenizer(onePath, "@");
				OdfElement newElement = dom.createElementNS(OdfName.newName(token2.nextToken(), token2.nextToken()));
				parentNode.appendChild(newElement);
				parentNode = newElement;
			}
		}
		parentNode.appendChild(cloneStyleEle);
	}

	//The returned string is a path from the top of the dom tree to the specified element
	//and the path is split by "/" between each node
	private String getElementPath(OdfElement styleEle) {
		String path = "";
		Node parentNode = styleEle.getParentNode();
		while (!(parentNode instanceof OdfFileDom)) {
			String qname = null;
			String prefix = parentNode.getPrefix();
			if (prefix == null) {
				qname = parentNode.getLocalName();
			} else {
				qname = prefix + ":" + parentNode.getLocalName();
			}
			path = parentNode.getNamespaceURI() + "@" + qname + "/" + path;
			parentNode = parentNode.getParentNode();
		}
		return path;
	}

	//change the element referred oldStyleName to the new name
	//if true then set newStyleName attribute value successfully
	//if false means that the newStyleName value is not conform to the ODF spec, so do not modify the oldStyleName
	private boolean changeStyleRefName(List<OdfElement> list, String oldStyleName, String newStyleName) {
		boolean rtn = false;
		for (int index = 0; index < list.size(); index++) {
			OdfElement element = list.get(index);
			NamedNodeMap attributes = element.getAttributes();

			if (attributes != null) {
				for (int i = 0; i < attributes.getLength(); i++) {
					Node item = attributes.item(i);
					String value = item.getNodeValue();
					if (oldStyleName.equals(value)) {
						try {
							item.setNodeValue(newStyleName);
							rtn = true;
							break;
						} catch (IllegalArgumentException e) {
							return false;
						}
					}
				}
			}
		}
		return rtn;
	}

	//check if the element contains the referred styleName
	private boolean isStyleNameRefExist(Node element, String styleName, boolean deep) {
		NamedNodeMap attributes = element.getAttributes();
		if (attributes != null) {
			for (int i = 0; i < attributes.getLength(); i++) {
				Node item = attributes.item(i);
				if (item.getNodeValue().equals(styleName)
						&& !item.getNodeName().equals("style:name")) //this is style definition, not reference
				{
					return true;
				}
			}
		}
		if (deep) {
			Node childNode = element.getFirstChild();
			while (childNode != null) {
				if (!isStyleNameRefExist(childNode, styleName, true)) {
					childNode = childNode.getNextSibling();
				} else {
					return true;
				}
			}
		}
		return false;
	}

	//check if nodeList contains the node that "style:name" attribute has the same value with styleName
	//Note: nodeList here is all the style definition list
	private OdfElement isStyleNameExist(NodeList nodeList,
			String styleName) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			OdfElement element = (OdfElement) nodeList.item(i);
			String name = element.getAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "name");
			if (name.equals(styleName)) //return true;
			{
				return element;
			}
		}
		//return false;
		return null;
	}

	private String makeUniqueName() {
		return String.format("a%06x", (int) (Math.random() * 0xffffff));
	}

	/**
	 * Make a content copy of the specified element,
	 * and the returned element should have the specified ownerDocument.
	 * @param element	The element that need to be copied
	 * @param dom		The specified DOM tree that the returned element belong to
	 * @param deep		If true, recursively clone the subtree under the element,
	 * 					false, only clone the element itself
	 * @return	Returns a duplicated element which is not in the DOM tree with the specified element
	 */
	public Node cloneForeignElement(Node element, OdfFileDom dom, boolean deep) {
		checkAllSlideName();
		if (element instanceof OdfElement) {
			OdfElement cloneElement = dom.createElementNS(((OdfElement) element).getOdfName());

			NamedNodeMap attributes = element.getAttributes();
			if (attributes != null) {
				for (int i = 0; i < attributes.getLength(); i++) {
					Node item = attributes.item(i);
					String qname = null;
					String prefix = item.getPrefix();
					if (prefix == null) {
						qname = item.getLocalName();
					} else {
						qname = prefix + ":" + item.getLocalName();
					}

					cloneElement.setAttributeNS(item.getNamespaceURI(), qname, item.getNodeValue());
				}
			}

			if (deep) {
				Node childNode = element.getFirstChild();
				while (childNode != null) {
					cloneElement.appendChild(cloneForeignElement(childNode, dom, true));
					childNode = childNode.getNextSibling();
				}
			}

			return cloneElement;
		} else {
			return dom.createTextNode(element.getNodeValue());
		}

	}

	/**
	 * New a slide at the specified position with the specified name,
	 * and use the specified slide template.
	 * See <code>OdfDrawPage.SlideLayout</code>.
	 * <p>
	 * If index is invalid, such as larger than the current document
	 * slide number or is negative,
	 * then append the new slide at the end of the document.
	 * <p>
	 * The slide name can be null.
	 * @param index		the new slide position
	 * @param name		the new slide name
	 * @param slideLayout	the new slide template
	 * @return	the new slide which locate at the specified position
	 * with the specified name and apply the specified slide template.
	 * If slideLayout is null, then use the default slide template which is a blank slide.
	 * <p>
	 * Throw IndexOutOfBoundsException if index is out of the presentation document slide count.
	 */
	public OdfSlide newSlide(int index, String name, OdfSlide.SlideLayout slideLayout) {
		checkAllSlideName();
		OfficePresentationElement contentRoot = null;
		try {
			contentRoot = getContentRoot();
		} catch (Exception e) {
			Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, e);
			return null;
		}
		NodeList slideList = contentRoot.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		int slideCount = slideList.getLength();
		if ((index < 0) || (index > slideCount)) {
			throw new IndexOutOfBoundsException("the specified Index is out of slide count when call newSlide method.");
		}
		//if insert page at the beginning of the document,
		//get the next page style as the new page style
		//else get the previous page style as the new page style
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
		//insert notes page
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
		//in case that the appended new slide have the same name with the original slide
		hasCheckSlideName = false;
		checkAllSlideName();
		return OdfSlide.getInstance(newSlideElement);
	}

	//when insert a slide, the note page for this slide is also inserted.
	//note page refer the slide index in order to show the corresponding slide notes view
	//this function is used to adjust note page referred slide index since startIndex
	//when the slide at startIndex has been delete or insert
	private void adjustNotePageNumber(int startIndex) {
		try {
			OfficePresentationElement contentRoot = getContentRoot();
			NodeList slideList = contentRoot.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
			for (int i = startIndex; i < getSlideCount(); i++) {
				DrawPageElement page = (DrawPageElement) slideList.item(i);
				NodeList noteNodes = page.getElementsByTagNameNS(OdfDocumentNamespace.PRESENTATION.getUri(), "notes");
				if (noteNodes.getLength() > 0) {
					PresentationNotesElement notePage = (PresentationNotesElement) noteNodes.item(0);
					NodeList thumbnailList = notePage.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page-thumbnail");
					if (thumbnailList.getLength() > 0) {
						DrawPageThumbnailElement thumbnail = (DrawPageThumbnailElement) thumbnailList.item(0);
						thumbnail.setDrawPageNumberAttribute(i + 1);
					}
				}
			}
		} catch (Exception e) {
			Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	//covered element
	//<presentation:notes>, <draw:page-thumbnail>, <draw:frame>
	//<style:presentation-page-layout>
	private void setSlideLayout(DrawPageElement page,
			OdfSlide.SlideLayout slideLayout) {
		if (slideLayout == null) {
			slideLayout = OdfSlide.SlideLayout.BLANK;
		}
		OdfOfficeStyles styles = null;
        try {
            styles = this.getStylesDom().getOrCreateOfficeStyles();
        } catch (SAXException ex) {
            Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
		String layoutName;

		if (slideLayout.toString().equals(OdfSlide.SlideLayout.TITLE_ONLY.toString())) {
			layoutName = "AL1T" + makeUniqueName();
			try {
				StylePresentationPageLayoutElement layout = styles.newStylePresentationPageLayoutElement(layoutName);
				layout.newPresentationPlaceholderElement("title", "2.058cm", "1.743cm", "23.91cm", "3.507cm");
			} catch (Exception e1) {
				Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, e1);
			}
			page.setPresentationPresentationPageLayoutNameAttribute(layoutName);

			DrawFrameElement frame1 = page.newDrawFrameElement();
			frame1.setProperty(StyleGraphicPropertiesElement.StyleShadow, "true");
			frame1.setProperty(StyleGraphicPropertiesElement.AutoGrowHeight, "true");
			frame1.setProperty(StyleGraphicPropertiesElement.MinHeight, "3.507");
			frame1.setPresentationStyleNameAttribute(frame1.getStyleName());

			frame1.setDrawLayerAttribute("layout");
			frame1.setSvgHeightAttribute("3.006cm");
			frame1.setSvgWidthAttribute("24.299cm");
			frame1.setSvgXAttribute("1.35cm");
			frame1.setSvgYAttribute("0.717cm");
			frame1.setPresentationClassAttribute(PresentationClassAttribute.Value.TITLE.toString());
			frame1.setPresentationPlaceholderAttribute(true);
			frame1.newDrawTextBoxElement();
		} else if (slideLayout.toString().equals(OdfSlide.SlideLayout.TITLE_OUTLINE.toString())) {
			layoutName = makeUniqueName();
			try {
				styles = super.getStylesDom().getOfficeStyles();
				if (styles == null) {
					styles = super.getStylesDom().newOdfElement(OdfOfficeStyles.class);
				}
				StylePresentationPageLayoutElement layout = styles.newStylePresentationPageLayoutElement(layoutName);
				layout.newPresentationPlaceholderElement("title", "2.058cm", "1.743cm", "23.91cm", "3.507cm");
				layout.newPresentationPlaceholderElement("outline", "2.058cm", "1.743cm", "23.91cm", "3.507cm");

			} catch (Exception e1) {
				Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, e1);
			}
			page.setPresentationPresentationPageLayoutNameAttribute(layoutName);


			DrawFrameElement frame1 = page.newDrawFrameElement();
			frame1.setProperty(StyleGraphicPropertiesElement.StyleShadow, "true");
			frame1.setProperty(StyleGraphicPropertiesElement.AutoGrowHeight, "true");
			frame1.setProperty(StyleGraphicPropertiesElement.MinHeight, "3.507");
			frame1.setPresentationStyleNameAttribute(frame1.getStyleName());

			frame1.setDrawLayerAttribute("layout");
			frame1.setSvgHeightAttribute("3.006cm");
			frame1.setSvgWidthAttribute("24.299cm");
			frame1.setSvgXAttribute("1.35cm");
			frame1.setSvgYAttribute("0.717cm");
			frame1.setPresentationClassAttribute(PresentationClassAttribute.Value.TITLE.toString());
			frame1.setPresentationPlaceholderAttribute(true);
			frame1.newDrawTextBoxElement();
			DrawFrameElement frame2 = page.newDrawFrameElement();

			frame2.setProperty(StyleGraphicPropertiesElement.FillColor, "#ffffff");
			frame2.setProperty(StyleGraphicPropertiesElement.MinHeight, "13.114");
			frame2.setPresentationStyleNameAttribute(frame2.getStyleName());

			frame2.setDrawLayerAttribute("layout");
			frame2.setSvgHeightAttribute("11.629cm");
			frame2.setSvgWidthAttribute("24.199cm");
			frame2.setSvgXAttribute("1.35cm");
			frame2.setSvgYAttribute("4.337cm");
			frame2.setPresentationClassAttribute(PresentationClassAttribute.Value.OUTLINE.toString());
			frame2.setPresentationPlaceholderAttribute(true);
			frame2.newDrawTextBoxElement();
		} else if (slideLayout.toString().equals(OdfSlide.SlideLayout.TITLE_PLUS_TEXT.toString())) {
			layoutName = makeUniqueName();
			try {
				styles = super.getStylesDom().getOfficeStyles();
				if (styles == null) {
					styles = super.getStylesDom().newOdfElement(OdfOfficeStyles.class);
				}
				StylePresentationPageLayoutElement layout = styles.newStylePresentationPageLayoutElement(layoutName);
				layout.newPresentationPlaceholderElement("title", "2.058cm", "1.743cm", "23.91cm", "1.743cm");
				layout.newPresentationPlaceholderElement("subtitle", "2.058cm", "5.838cm", "23.91cm", "13.23cm");

			} catch (Exception e1) {
				Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, e1);
			}
			page.setPresentationPresentationPageLayoutNameAttribute(layoutName);

			DrawFrameElement frame1 = page.newDrawFrameElement();
			frame1.setProperty(StyleGraphicPropertiesElement.AutoGrowHeight, "true");
			frame1.setProperty(StyleGraphicPropertiesElement.MinHeight, "3.507");
			frame1.setPresentationStyleNameAttribute(frame1.getStyleName());

			frame1.setDrawLayerAttribute("layout");
			frame1.setSvgHeightAttribute("3.006cm");
			frame1.setSvgWidthAttribute("24.299cm");
			frame1.setSvgXAttribute("1.35cm");
			frame1.setSvgYAttribute("0.717cm");
			frame1.setPresentationClassAttribute(PresentationClassAttribute.Value.TITLE.toString());
			frame1.setPresentationPlaceholderAttribute(true);
			frame1.newDrawTextBoxElement();
			DrawFrameElement frame2 = page.newDrawFrameElement();
			frame2.setProperty(StyleGraphicPropertiesElement.AutoGrowHeight, "true");
			frame2.setProperty(StyleGraphicPropertiesElement.MinHeight, "3.507");
			frame2.setPresentationStyleNameAttribute(frame2.getStyleName());

			frame2.setDrawLayerAttribute("layout");
			frame2.setSvgHeightAttribute("11.88cm");
			frame2.setSvgWidthAttribute("24.299cm");
			frame2.setSvgXAttribute("1.35cm");
			frame2.setSvgYAttribute("4.712cm");
			frame2.setPresentationClassAttribute(PresentationClassAttribute.Value.SUBTITLE.toString());
			frame2.setPresentationPlaceholderAttribute(true);
			frame2.newDrawTextBoxElement();

		} else if (slideLayout.toString().equals(OdfSlide.SlideLayout.TITLE_PLUS_2_TEXT_BLOCK.toString())) {

			layoutName = makeUniqueName();
			try {
				styles = super.getStylesDom().getOfficeStyles();
				if (styles == null) {
					styles = super.getStylesDom().newOdfElement(OdfOfficeStyles.class);
				}
				StylePresentationPageLayoutElement layout = styles.newStylePresentationPageLayoutElement(layoutName);
				layout.newPresentationPlaceholderElement("outline", "2.058cm", "1.743cm", "23.91cm", "1.743cm");
				layout.newPresentationPlaceholderElement("outline", "1.35cm", "4.212cm", "11.857cm", "11.629cm");
				layout.newPresentationPlaceholderElement("outline", "4.212cm", "13.8cm", "11.857cm", "11.629cm");

			} catch (Exception e1) {
				Logger.getLogger(OdfPresentationDocument.class.getName()).log(Level.SEVERE, null, e1);
			}

			DrawFrameElement frame1 = page.newDrawFrameElement();
			frame1.setProperty(StyleGraphicPropertiesElement.AutoGrowHeight, "true");
			frame1.setProperty(StyleGraphicPropertiesElement.MinHeight, "3.507");
			frame1.setPresentationStyleNameAttribute(frame1.getStyleName());

			frame1.setDrawLayerAttribute("layout");
			frame1.setSvgHeightAttribute("3.006cm");
			frame1.setSvgWidthAttribute("24.299cm");
			frame1.setSvgXAttribute("1.35cm");
			frame1.setSvgYAttribute("0.717cm");
			frame1.setPresentationClassAttribute(PresentationClassAttribute.Value.TITLE.toString());
			frame1.setPresentationPlaceholderAttribute(true);
			frame1.newDrawTextBoxElement();
			DrawFrameElement frame2 = page.newDrawFrameElement();
			frame2.setProperty(StyleGraphicPropertiesElement.AutoGrowHeight, "true");
			frame2.setProperty(StyleGraphicPropertiesElement.MinHeight, "3.507");
			frame2.setPresentationStyleNameAttribute(frame2.getStyleName());

			frame2.setDrawLayerAttribute("layout");
			frame2.setSvgHeightAttribute("11.629cm");
			frame2.setSvgWidthAttribute("11.857cm");
			frame2.setSvgXAttribute("1.35cm");
			frame2.setSvgYAttribute("4.212cm");
			frame2.setPresentationClassAttribute(PresentationClassAttribute.Value.OUTLINE.toString());
			frame2.setPresentationPlaceholderAttribute(true);
			frame2.newDrawTextBoxElement();
			DrawFrameElement frame3 = page.newDrawFrameElement();
			frame3.setProperty(StyleGraphicPropertiesElement.AutoGrowHeight, "true");
			frame3.setProperty(StyleGraphicPropertiesElement.MinHeight, "3.507");
			frame3.setPresentationStyleNameAttribute(frame3.getStyleName());

			frame3.setDrawLayerAttribute("layout");
			frame3.setSvgHeightAttribute("11.62cm");
			frame3.setSvgWidthAttribute("11.857cm");
			frame3.setSvgXAttribute("13.8cm");
			frame3.setSvgYAttribute("4.212cm");
			frame3.setPresentationClassAttribute(PresentationClassAttribute.Value.OUTLINE.toString());
			frame3.setPresentationPlaceholderAttribute(true);
			frame3.newDrawTextBoxElement();

			page.setPresentationPresentationPageLayoutNameAttribute(layoutName);

		}
	}
}
