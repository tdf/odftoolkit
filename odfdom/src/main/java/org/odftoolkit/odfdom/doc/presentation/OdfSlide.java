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
package org.odftoolkit.odfdom.doc.presentation;

import java.util.Hashtable;

import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.draw.DrawPageElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationNotesElement;
import org.w3c.dom.NodeList;

/**
 * <code>OdfSlide</code> represents the presentation slide feature of the ODF document.
 * <code>OdfSlide</code> provides methods to get the slide index,get the content of the current slide, etc.
 *
 */
public class OdfSlide {

	DrawPageElement maSlideElement;
	private static Hashtable<DrawPageElement, OdfSlide> maSlideRepository =
			new Hashtable<DrawPageElement, OdfSlide>();

	private OdfSlide(DrawPageElement pageElement) {
		maSlideElement = pageElement;
	}

	/**
	 * Get a presentation slide instance by an instance of <code>DrawPageElement</code>.
	 *
	 * @param pageElement	an instance of <code>DrawPageElement</code>
	 * @return an instance of <code>OdfSlide</code> that can represent <code>pageElement</code>
	 */
	public static OdfSlide getInstance(DrawPageElement pageElement) {
		if (maSlideRepository.containsKey(pageElement)) {
			return maSlideRepository.get(pageElement);
		} else {
			OdfSlide newSlide = new OdfSlide(pageElement);
			maSlideRepository.put(pageElement, newSlide);
			return newSlide;
		}
	}

	/**
	 * Return an instance of <code>DrawPageElement</code> which represents presentation slide feature.
	 *
	 * @return an instance of <code>DrawPageElement</code>
	 */
	public DrawPageElement getOdfElement() {
		return maSlideElement;
	}

	/**
	 * Get the current slide index in the owner document.
	 * @return the slide index in the owner document
	 * <p>
	 * -1, if the odf element which can represent this slide is not in the document DOM tree
	 */
	public int getSlideIndex() {
		OdfFileDom contentDom = (OdfFileDom) maSlideElement.getOwnerDocument();
		NodeList slideNodes = contentDom.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		for (int i = 0; i < slideNodes.getLength(); i++) {
			DrawPageElement slideEle = (DrawPageElement) slideNodes.item(i);
			if (slideEle == maSlideElement)//should not equals here, see OdfElement.equals(Object obj)
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * Get the current slide name.
	 * <p>
	 * If the "draw:name" attribute is not present there,
	 * create an unique name for this slide
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
	 * It must be unique slide name in the current presentation.
	 * If not, an IllegalArgumentException will be thrown.
	 * If the given name is null,  an IllegalArgumentException will also be thrown.
	 * @param name	the new name of the current slide
	 * @throws IllegalArgumentException if the given name is null or it is not unique in the current presentation.
	 */
	public void setSlideName(String name) {
		if (name == null) {
			throw new IllegalArgumentException("slide name is null is not accepted in the presentation document");
		}
		//check if name is unique in this presentation
		OdfFileDom contentDom = (OdfFileDom) maSlideElement.getOwnerDocument();
		NodeList slideNodes = contentDom.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		for (int i = 0; i < slideNodes.getLength(); i++) {
			DrawPageElement slideEle = (DrawPageElement) slideNodes.item(i);
			OdfSlide slide = OdfSlide.getInstance(slideEle);
			String slideName = slide.getSlideName();
			if (slideName.equals(name)) {
				throw new IllegalArgumentException("the given slide name is already exist in the current presentation document");
			}
		}
		maSlideElement.setDrawNameAttribute(name);
	}

	/**
	 * Get the Notes page of this slide
	 * @return the instance of <code>OdfPresentationNotes</code> which represent the notes page of the current slide
	 */
	public OdfPresentationNotes getNotesPage() {
		NodeList notesList = maSlideElement.getElementsByTagNameNS(OdfDocumentNamespace.PRESENTATION.getUri(), "notes");
		if (notesList.getLength() > 0) {
			PresentationNotesElement noteEle = (PresentationNotesElement) notesList.item(0);
			return OdfPresentationNotes.getInstance(noteEle);

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
		 * Blank,  a blank presentation
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
		 * @return   the template type value
		 */
		@Override
		public String toString() {
			return mValue;
		}

		/**
		 * Return the name of the template slide type.
		 * @param aEnum    a <code>SlideLayout</code>
		 * @return         the name of slide template type
		 */
		public static String toString(SlideLayout aEnum) {
			return aEnum.toString();
		}

		/**
		 * Return a template slide type.
		 * @param aString   the name of the slide template type
		 * @return       a <code>SlideLayout</code>
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
}
