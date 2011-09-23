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

package org.odftoolkit.simple.presentation;

import java.util.IdentityHashMap;
import java.util.Iterator;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawTextBoxElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationNotesElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Component;
import org.odftoolkit.simple.PresentationDocument;
import org.odftoolkit.simple.text.list.AbstractListContainer;
import org.odftoolkit.simple.text.list.List;
import org.odftoolkit.simple.text.list.ListContainer;
import org.odftoolkit.simple.text.list.ListDecorator;
import org.w3c.dom.NodeList;

/**
 * <code>Notes</code> represents the presentation notes feature of the ODF
 * document. <code>Notes</code> provides methods to creates notes, add content,
 * add list, etc.
 * 
 */
public class Notes extends Component implements ListContainer {

	PresentationNotesElement maNoteElement;
	private ListContainerImpl listContainerImpl;

	/**
	 * This is a tool class which supplies all of the notes creation detail.
	 * <p>
	 * The end user isn't allowed to create it directly, otherwise an
	 * <code>IllegalStateException</code> will be thrown.
	 * 
	 *@since 0.3.5
	 */
	public static class NotesBuilder {

		private final IdentityHashMap<PresentationNotesElement, Notes> maNotesRepository = new IdentityHashMap<PresentationNotesElement, Notes>();

		/**
		 * NotesBuilder constructor. This constructor should only be use in
		 * owner {@link org.odftoolkit.simple.PresentationDocument
		 * PresentationDocument} constructor. The end user isn't allowed to call
		 * it directly, otherwise an <code>IllegalStateException</code> will be
		 * thrown.
		 * 
		 * @param doc
		 *            the owner <code>PresentationDocument</code>.
		 * @throws IllegalStateException
		 *             if new NotesBuilder out of owner PresentationDocument
		 *             constructor, this exception will be thrown.
		 */
		public NotesBuilder(PresentationDocument doc) {
			if (doc.getNotesBuilder() != null) {
				throw new IllegalStateException(
						"NotesBuilder only can be created in owner PresentationDocument constructor.");
			}
		}

		/**
		 * Get a presentation notes page instance by an instance of
		 * <code>PresentationNotesElement</code>.
		 * 
		 * @param noteElement
		 *            an instance of <code>PresentationNotesElement</code>
		 * @return an instance of <code>Notes</code> that can represent
		 *         <code>PresentationNotesElement</code>
		 */
		public synchronized Notes getNotesInstance(PresentationNotesElement noteElement) {
			if (maNotesRepository.containsKey(noteElement))
				return maNotesRepository.get(noteElement);
			else {
				Notes newNotes = new Notes(noteElement);
				maNotesRepository.put(noteElement, newNotes);
				return newNotes;
			}
		}
	}

	private Notes(PresentationNotesElement noteElement) {
		maNoteElement = noteElement;
	}

	/**
	 * Get a presentation notes page instance by an instance of
	 * <code>PresentationNotesElement</code>.
	 * 
	 * @param noteElement
	 *            an instance of <code>PresentationNotesElement</code>
	 * @return an instance of <code>Notes</code> that can represent
	 *         <code>PresentationNotesElement</code>
	 */
	public static Notes getInstance(PresentationNotesElement noteElement) {
		PresentationDocument ownerDocument = (PresentationDocument) ((OdfFileDom) (noteElement.getOwnerDocument()))
				.getDocument();
		return ownerDocument.getNotesBuilder().getNotesInstance(noteElement);

	}

	/**
	 * Return an instance of <code>PresentationNotesElement</code> which
	 * represents presentation notes page feature.
	 * 
	 * @return an instance of <code>PresentationNotesElement</code>
	 */
	public PresentationNotesElement getOdfElement() {
		return maNoteElement;
	}

	/**
	 * insert some text to the notes page
	 * 
	 * @param text
	 *            the text that need to insert in the notes page
	 */
	public void addText(String text) {
		NodeList frameList = maNoteElement.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "frame");
		if (frameList.getLength() > 0) {
			DrawFrameElement frame = (DrawFrameElement) frameList.item(0);
			NodeList textBoxList = frame.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "text-box");
			if (textBoxList.getLength() > 0) {
				DrawTextBoxElement textBox = (DrawTextBoxElement) textBoxList.item(0);
				TextPElement newPara = textBox.newTextPElement();
				newPara.setTextContent(text);
			}
		}
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
			DrawFrameElement frame = null;
			DrawTextBoxElement textBox = null;
			NodeList frameList = maNoteElement.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "frame");
			if (frameList.getLength() <= 0) {
				frame = maNoteElement.newDrawFrameElement();
			} else {
				frame = (DrawFrameElement) frameList.item(frameList.getLength() - 1);
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
