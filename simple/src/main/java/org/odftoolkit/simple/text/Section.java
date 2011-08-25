/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2011 IBM. All rights reserved.
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
package org.odftoolkit.simple.text;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.element.text.TextSectionElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Component;
import org.odftoolkit.simple.Document;

/**
 * This class represents section definition in text document. It provides
 * methods to manipulate section in text document, such as getting/setting
 * section name, moving section and so on.
 * 
 * @since 0.4
 */
public class Section extends Component implements ParagraphContainer {

	private ParagraphContainerImpl paragraphContainerImpl;
	private TextSectionElement mSectionElement;
	private Document mDocument;

	private Section(Document doc, TextSectionElement element) {
		mSectionElement = element;
		mDocument = doc;
	}

	/**
	 * Get a section instance by an object of <code>TextSectionElement</code>.
	 * 
	 * @param element
	 *            - an object of <code>TextSectionElement</code>
	 * @return an instance of <code>Section</code> that can represent
	 *         <code>TextSectionElement</code>
	 */
	public static Section getInstance(TextSectionElement element) {
		return new Section((Document) ((OdfFileDom) (element.getOwnerDocument())).getDocument(), element);
	}

	/**
	 * Return the ODF document which this section belongs to.
	 * 
	 * @return - the ODF document which this section belongs to.
	 */
	public Document getOwnerDocument() {
		return mDocument;
	}

	/**
	 * Return the name of this section
	 * 
	 * @return - the name of this section
	 */
	public String getName() {
		return mSectionElement.getTextNameAttribute();
	}

	/**
	 * Set the value of this section name
	 * 
	 * @param name
	 *            - the value of name to be set
	 */
	public void setName(String name) {
		mSectionElement.setTextNameAttribute(name);
	}

	/**
	 * Remove this section from the document.
	 * <p>
	 * All the linked resources which are only linked to this section will be
	 * removed too.
	 * 
	 */
	public void remove() {
		mDocument.removeElementLinkedResource(mSectionElement);
		mSectionElement.getParentNode().removeChild(mSectionElement);
		paragraphContainerImpl = null;
		mSectionElement = null;
		mDocument = null;
	}

	/**
	 * Return an instance of <code>TextSectionElement</code> which represents
	 * this section.
	 * 
	 * @return - an instance of <code>TextSectionElement</code> which represents
	 *         this section
	 */
	public TextSectionElement getOdfElement() {
		return mSectionElement;
	}

	/**
	 * Return whether this section is contained in footer or header.
	 * 
	 * @return - true if this section is contained in footer or header. false if
	 *         this section is not contained in footer or header.
	 */
	boolean isInHeaderFooter() {
		try {
			if (mSectionElement.getOwnerDocument() == mDocument.getStylesDom())
				return true;
		} catch (Exception e) {
			Logger.getLogger(Section.class.getName()).log(Level.SEVERE, "Failed in isInHeaderFooter", e);
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Section))
			return false;
		Section aSection = (Section) obj;
		if (aSection == this)
			return true;
		return aSection.getOdfElement().equals(mSectionElement);
	}

	//****************Paragraph support******************//
	
	public Paragraph addParagraph(String textContent) {
		return getParagraphContainerImpl().addParagraph(textContent);
	}

	public OdfElement getParagraphContainerElement() {
		return getParagraphContainerImpl().getParagraphContainerElement();
	}

	public boolean removeParagraph(Paragraph para) {
		return getParagraphContainerImpl().removeParagraph(para);
	}

	private class ParagraphContainerImpl extends AbstractParagraphContainer {
		public OdfElement getParagraphContainerElement()
		{
			return mSectionElement;
		}
	}
	private ParagraphContainerImpl getParagraphContainerImpl() {
		if (paragraphContainerImpl == null)
			paragraphContainerImpl = new ParagraphContainerImpl();
		return paragraphContainerImpl;
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
}
