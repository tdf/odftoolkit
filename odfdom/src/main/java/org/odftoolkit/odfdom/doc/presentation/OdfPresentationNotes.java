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

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawTextBoxElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationNotesElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.w3c.dom.NodeList;

/**
 * Convenient functionality for the parent ODF OpenDocument element
 *
 */
public class OdfPresentationNotes
{
	PresentationNotesElement maNoteElement;
	private static Hashtable<PresentationNotesElement, OdfPresentationNotes> maNotesRepository =
		new Hashtable<PresentationNotesElement, OdfPresentationNotes>();

	private OdfPresentationNotes( PresentationNotesElement noteElement )
	{
		maNoteElement = noteElement;
	}

	/**
	 * Return an instance of <code>PresentationNotesElement</code> which represents presentation notes page feature.
	 *
	 * @return an instance of <code>PresentationNotesElement</code>
	 */
	public PresentationNotesElement getOdfElement()
	{
		return maNoteElement;
	}
	/**
	 * Get a presentation notes page instance by an instance of <code>PresentationNotesElement</code>.
	 *
	 * @param noteElement	an instance of <code>PresentationNotesElement</code>
	 * @return an instance of <code>OdfPresentationNotes</code> that can represent <code>PresentationNotesElement</code>
	 */
	public static OdfPresentationNotes getInstance(PresentationNotesElement noteElement)
	{
		if (maNotesRepository.containsKey(noteElement))
			return maNotesRepository.get(noteElement);
		else {
			OdfPresentationNotes newNotes = new OdfPresentationNotes(noteElement);
			maNotesRepository.put(noteElement, newNotes);
			return newNotes;
		}
	}

	/**
	 * insert some text to the notes page
	 * @param text	the text that need to insert in the notes page
	 */
	public void addText(String text){
		NodeList frameList = maNoteElement.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "frame");
		if(frameList.getLength() > 0){
			DrawFrameElement frame = (DrawFrameElement)frameList.item(0);
			NodeList textBoxList = frame.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "text-box");
			if(textBoxList.getLength() > 0){
				DrawTextBoxElement textBox = (DrawTextBoxElement)textBoxList.item(0);
				TextPElement newPara = textBox.newTextPElement();
				newPara.setTextContent(text);
			}
		}
	}
}
