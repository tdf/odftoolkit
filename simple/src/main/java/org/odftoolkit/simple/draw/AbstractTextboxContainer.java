/* 
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

package org.odftoolkit.simple.draw;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawTextBoxElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.simple.PresentationDocument;
import org.odftoolkit.simple.text.AbstractParagraphContainer;
import org.w3c.dom.DOMException;

/**
 * AbstractTextboxContainer is an abstract implementation of the
 * TextboxContainer interface, with a default implementation for every method
 * defined in TextboxContainer, except getFrameContainerElement(). A subclass
 * must implement the abstract method getFrameContainerElement().
 * 
 * @since 0.5
 */
public abstract class AbstractTextboxContainer extends AbstractFrameContainer implements TextboxContainer {

	public Textbox addTextbox() {
		Textbox textbox = Textbox.newTextbox(this);
		return textbox;
	}

	/**
	 * Remove the text box.
	 * 
	 * @param box
	 *            - the text box to be removed
	 * @return true if the text box is removed successfully, false if errors
	 *         happen.
	 */
	public boolean removeTextbox(Textbox box) {
		OdfElement containerElement = getFrameContainerElement();
		DrawFrameElement drawFrame = box.getDrawFrameElement();
		try {
			drawFrame.removeChild(box.getOdfElement());
			if (drawFrame.hasChildNodes() == false)
				containerElement.removeChild(box.getDrawFrameElement());
		} catch (DOMException exception) {
			Logger.getLogger(AbstractParagraphContainer.class.getName()).log(Level.WARNING, exception.getMessage());
			return false;
		}
		return true;
	}

	public Iterator<Textbox> getTextboxIterator() {
		return new SimpleTextboxIterator(this);
	}

	public Textbox addTextbox(FrameRectangle position) {
		Textbox textbox = Textbox.newTextbox(this);
		textbox.setRectangle(position);
		return textbox;
	}

	/**
	 * Return a text box whose name is a given value.
	 * 
	 * @param name
	 *            - the name of the text box
	 * @return a text box whose name is a given value
	 * @see AbstractFrameContainer#getFrameByName(String)
	 */
	public Textbox getTextboxByName(String name) {
		Frame frame = getFrameByName(name);
		if (frame != null) {
			DrawTextBoxElement boxElement = OdfElement.findFirstChildNode(DrawTextBoxElement.class, frame
					.getDrawFrameElement());
			if (boxElement != null) {
				Textbox box = Textbox.getInstanceof(boxElement);
				box.mFrameContainer = frame.mFrameContainer;
				return box;
			}
		}
		return null;
	}

	/**
	 * This method is only useful for presentation slides.
	 * <p>
	 * This method will return a list of text boxs by the usage defined in
	 * presentation slides.
	 * 
	 * @param usage
	 *            - the usage description
	 * @return a list of text box Null will be returned if the owner document is
	 *         not a presentation
	 * @see AbstractFrameContainer#getFrameByPresentationclass(PresentationDocument.PresentationClass)
	 * @see org.odftoolkit.simple.PresentationDocument.PresentationClass
	 */
	public List<Textbox> getTextboxByUsage(PresentationDocument.PresentationClass usage) {
		List<Frame> frameList = getFrameByPresentationclass(usage);
		if (frameList == null)
			return null;

		ArrayList<Textbox> al = new ArrayList<Textbox>();
		for (int i = 0; i < frameList.size(); i++) {
			Frame frame = frameList.get(i);
			DrawTextBoxElement boxElement = OdfElement.findFirstChildNode(DrawTextBoxElement.class, frame
					.getDrawFrameElement());
			if (boxElement != null) {
				Textbox box = Textbox.getInstanceof(boxElement);
				box.mFrameContainer = frame.mFrameContainer;
				al.add(box);
			}
		}
		return al;
	}

	private class SimpleTextboxIterator implements Iterator<Textbox> {

		private OdfElement containerElement;
		private Textbox nextElement = null;
		private Textbox tempElement = null;

		private SimpleTextboxIterator(TextboxContainer container) {
			containerElement = container.getFrameContainerElement();
		}

		public boolean hasNext() {
			tempElement = findNext(nextElement);
			return (tempElement != null);
		}

		public Textbox next() {
			if (tempElement != null) {
				nextElement = tempElement;
				tempElement = null;
			} else {
				nextElement = findNext(nextElement);
			}
			if (nextElement == null) {
				return null;
			} else {
				return nextElement;
			}
		}

		public void remove() {
			if (nextElement == null) {
				throw new IllegalStateException("please call next() first.");
			}
			containerElement.removeChild(nextElement.getDrawFrameElement());
		}

		private Textbox findNext(Textbox thisBox) {
			DrawFrameElement nextFrame = null;
			if (thisBox == null) {
				nextFrame = OdfElement.findFirstChildNode(DrawFrameElement.class, containerElement);
			} else {
				nextFrame = OdfElement.findNextChildNode(DrawFrameElement.class, thisBox.getDrawFrameElement());
			}

			if (nextFrame != null) {
				DrawTextBoxElement nextbox = OdfElement.findFirstChildNode(DrawTextBoxElement.class, nextFrame);
				return Textbox.getInstanceof(nextbox);
			}
			return null;
		}
	}

}
