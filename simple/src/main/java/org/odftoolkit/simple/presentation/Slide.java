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
package org.odftoolkit.simple.presentation;

import java.util.IdentityHashMap;
import java.util.Iterator;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawPageElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawTextBoxElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationNotesElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.PresentationDocument;
import org.odftoolkit.simple.text.list.AbstractListContainer;
import org.odftoolkit.simple.text.list.List;
import org.odftoolkit.simple.text.list.ListContainer;
import org.odftoolkit.simple.text.list.ListDecorator;
import org.w3c.dom.NodeList;

/**
 * <code>Slide</code> represents the presentation slide feature of the ODF
 * document. <code>Slide</code> provides methods to get the slide index,get the
 * content of the current slide, etc.
 */
public class Slide implements ListContainer {

	DrawPageElement maSlideElement;
	private ListContainerImpl listContainerImpl = new ListContainerImpl();

	/**
	 * This is a tool class which supplies all of the slide creation detail.
	 * <p>
	 * The end user isn't allowed to create it directly, otherwise an
	 * <code>IllegalStateException</code> will be thrown.
	 * 
	 *@since 0.3.5
	 */
	public static class SlideBuilder {

		private final IdentityHashMap<DrawPageElement, Slide> maSlideRepository = new IdentityHashMap<DrawPageElement, Slide>();

		/**
		 * SlideBuilder constructor. This constructor should only be use in
		 * owner {@link org.odftoolkit.simple.PresentationDocument
		 * PresentationDocument} constructor. The end user isn't allowed to call
		 * it directly, otherwise an <code>IllegalStateException</code> will be
		 * thrown.
		 * 
		 * @param doc
		 *            the owner <code>PresentationDocument</code>.
		 * @throws IllegalStateException
		 *             if new SlideBuilder out of owner PresentationDocument
		 *             constructor, this exception will be thrown.
		 */
		public SlideBuilder(PresentationDocument doc) {
			if (doc.getSlideBuilder() != null) {
				throw new IllegalStateException(
						"SlideBuilder only can be created in owner PresentationDocument constructor.");
			}
		}

		/**
		 * Get a presentation slide instance by an instance of
		 * <code>DrawPageElement</code>.
		 * 
		 * @param pageElement
		 *            an instance of <code>DrawPageElement</code>
		 * @return an instance of <code>Slide</code> that can represent
		 *         <code>pageElement</code>
		 */
		public synchronized Slide getSlideInstance(DrawPageElement pageElement) {
			if (maSlideRepository.containsKey(pageElement)) {
				return maSlideRepository.get(pageElement);
			} else {
				Slide newSlide = new Slide(pageElement);
				maSlideRepository.put(pageElement, newSlide);
				return newSlide;
			}
		}
	}

	private Slide(DrawPageElement pageElement) {
		maSlideElement = pageElement;
	}

	/**
	 * Get a presentation slide instance by an instance of
	 * <code>DrawPageElement</code>.
	 * 
	 * @param pageElement
	 *            an instance of <code>DrawPageElement</code>
	 * @return an instance of <code>Slide</code> that can represent
	 *         <code>pageElement</code>
	 */
	public static Slide getInstance(DrawPageElement pageElement) {
		PresentationDocument ownerDocument = (PresentationDocument) ((OdfFileDom) (pageElement.getOwnerDocument()))
				.getDocument();
		return ownerDocument.getSlideBuilder().getSlideInstance(pageElement);
	}

	/**
	 * Return an instance of <code>DrawPageElement</code> which represents
	 * presentation slide feature.
	 * 
	 * @return an instance of <code>DrawPageElement</code>
	 */
	public DrawPageElement getOdfElement() {
		return maSlideElement;
	}

	/**
	 * Get the current slide index in the owner document.
	 * 
	 * @return the slide index in the owner document
	 *         <p>
	 *         -1, if the odf element which can represent this slide is not in
	 *         the document DOM tree
	 */
	public int getSlideIndex() {
		OdfFileDom contentDom = (OdfFileDom) maSlideElement.getOwnerDocument();
		NodeList slideNodes = contentDom.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		for (int i = 0; i < slideNodes.getLength(); i++) {
			DrawPageElement slideEle = (DrawPageElement) slideNodes.item(i);
			if (slideEle == maSlideElement)// should not equals here, see
			// OdfElement.equals(Object obj)
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * Get the current slide name.
	 * <p>
	 * If the "draw:name" attribute is not present there, create an unique name
	 * for this slide
	 * 
	 * @return the name of the current slide
	 */
	public String getSlideName() {
		String slideName = maSlideElement.getDrawNameAttribute();
		if (slideName == null) {
			slideName = makeUniqueSlideName();
			maSlideElement.setDrawNameAttribute(slideName);
		}
		return slideName;
	}

	/**
	 * Set the current slide name.
	 * <p>
	 * It must be unique slide name in the current presentation. If not, an
	 * IllegalArgumentException will be thrown. If the given name is null, an
	 * IllegalArgumentException will also be thrown.
	 * 
	 * @param name
	 *            the new name of the current slide
	 * @throws IllegalArgumentException
	 *             if the given name is null or it is not unique in the current
	 *             presentation.
	 */
	public void setSlideName(String name) {
		if (name == null) {
			throw new IllegalArgumentException("slide name is null is not accepted in the presentation document");
		}
		// check if name is unique in this presentation
		OdfFileDom contentDom = (OdfFileDom) maSlideElement.getOwnerDocument();
		NodeList slideNodes = contentDom.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		for (int i = 0; i < slideNodes.getLength(); i++) {
			DrawPageElement slideEle = (DrawPageElement) slideNodes.item(i);
			Slide slide = Slide.getInstance(slideEle);
			String slideName = slide.getSlideName();
			if (slideName.equals(name)) {
				throw new IllegalArgumentException(
						"the given slide name is already exist in the current presentation document");
			}
		}
		maSlideElement.setDrawNameAttribute(name);
	}

	/**
	 * Get the Notes page of this slide
	 * 
	 * @return the instance of <code>Notes</code> which represent the notes page
	 *         of the current slide
	 */
	public Notes getNotesPage() {
		NodeList notesList = maSlideElement.getElementsByTagNameNS(OdfDocumentNamespace.PRESENTATION.getUri(), "notes");
		if (notesList.getLength() > 0) {
			PresentationNotesElement noteEle = (PresentationNotesElement) notesList.item(0);
			return Notes.getInstance(noteEle);

		}
		return null;
	}

	private String makeUniqueSlideName() {
		int index = getSlideIndex();
		String slideName = "page" + (index + 1) + "-" + String.format("a%06x", (int) (Math.random() * 0xffffff));
		return slideName;
	}

	/**
	 * A slide layout is a slide with some predefine placeholder.
	 * 
	 * we define some template layout as below:
	 * 
	 * "blank" template is a slide without any filled element,
	 * 
	 * "title_only" template is a slide with a title,
	 * 
	 * "title_outline" template is a slide with a title and an outline block,
	 * 
	 * "title_text" template is a slide with a title and a text block,
	 * 
	 * "title_two_text_block" template is a slide with a title two text blocks.
	 */
	public enum SlideLayout {

		/**
		 * Blank, a blank presentation
		 */
		BLANK("blank"),
		/**
		 * Title_only, the presentation with title only
		 */
		TITLE_ONLY("title_only"),
		/**
		 * Title_outline, the presentation with outline
		 */
		TITLE_OUTLINE("title_outline"),
		/**
		 * Title_text, the presentation with title and one text block
		 */
		TITLE_PLUS_TEXT("title_text"),
		/**
		 * title_two_text_block, the presentation with title and two text blocks
		 */
		TITLE_PLUS_2_TEXT_BLOCK("title_two_text_block");
		private String mValue;

		SlideLayout(String aValue) {
			mValue = aValue;
		}

		/**
		 * Return the slide template type value.
		 * 
		 * @return the template type value
		 */
		@Override
		public String toString() {
			return mValue;
		}

		/**
		 * Return the name of the template slide type.
		 * 
		 * @param aEnum
		 *            a <code>SlideLayout</code>
		 * @return the name of slide template type
		 */
		public static String toString(SlideLayout aEnum) {
			return aEnum.toString();
		}

		/**
		 * Return a template slide type.
		 * 
		 * @param aString
		 *            the name of the slide template type
		 * @return a <code>SlideLayout</code>
		 */
		public static SlideLayout enumValueOf(String aString) {
			for (SlideLayout aIter : values()) {
				if (aString.equals(aIter.toString())) {
					return aIter;
				}
			}
			return null;
		}
	}

	@Override
	public OdfElement getListContainerElement() {
		return listContainerImpl.getListContainerElement();
	}

	@Override
	public List addList() {
		return listContainerImpl.addList();
	}

	@Override
	public List addList(ListDecorator decorator) {
		return listContainerImpl.addList(decorator);
	}

	@Override
	public void clearList() {
		listContainerImpl.clearList();
	}

	@Override
	public Iterator<List> getListIterator() {
		return listContainerImpl.getListIterator();
	}

	@Override
	public boolean removeList(List list) {
		return listContainerImpl.removeList(list);
	}

	private class ListContainerImpl extends AbstractListContainer {

		@Override
		public OdfElement getListContainerElement() {
			DrawFrameElement frame = null;
			DrawTextBoxElement textBox = null;
			NodeList frameList = maSlideElement.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "frame");
			if (frameList.getLength() > 0) {
				int index = frameList.getLength() - 1;
				while (index >= 0) {
					frame = (DrawFrameElement) frameList.item(index);
					String presentationClass = frame.getPresentationClassAttribute();
					if (presentationClass == null || "outline".equals(presentationClass)
							|| "text".equals(presentationClass) || "subtitle".equals(presentationClass)) {
						break;
					} else {
						index--;
					}
					frame = null;
				}
			}
			if (frame == null) {
				throw new UnsupportedOperationException(
						"There is no list container in this slide, please chose a proper slide layout.");
			}
			NodeList textBoxList = frame.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "text-box");
			if (textBoxList.getLength() <= 0) {
				textBox = frame.newDrawTextBoxElement();
			} else {
				textBox = (DrawTextBoxElement) textBoxList.item(textBoxList.getLength() - 1);
			}
			return textBox;
		}
	}
}
