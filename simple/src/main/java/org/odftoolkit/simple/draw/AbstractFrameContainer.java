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
import java.util.List;

import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.PresentationDocument;

/**
 * AbstractFrameContainer is an abstract implementation of the FrameContainer
 * interface, with a default implementation for every method defined in
 * FrameContainer, except getFrameContainerElement(). A subclass must implement
 * the abstract method getFrameContainerElement().
 * 
 * @since 0.5
 */
public abstract class AbstractFrameContainer implements FrameContainer {

	public abstract OdfElement getFrameContainerElement();

	/**
	 * Add a frame to the container
	 * 
	 * @return the instance of DrawFrameElement
	 */
	public Frame addFrame() {
		Frame frame = Frame.newFrame(this);
		return frame;
	}

	/**
	 * Add a frame with the specific position to the container
	 * 
	 * @param rectangle
	 *            - the rectangle (position and size) of this frame
	 * @return an object of frame
	 */
	public Frame addFrame(FrameRectangle rectangle) {
		Frame frame = Frame.newFrame(this);
		frame.setRectangle(rectangle);
		return frame;
	}

	/**
	 * Return a frame whose name (specified with "draw:name") is a given value.
	 * 
	 * @param name
	 *            - the name of this frame
	 * @return a frame whose name is the given value
	 */
	public Frame getFrameByName(String name) {
		if (name == null)
			return null;

		OdfElement container = getFrameContainerElement();
		DrawFrameElement element = OdfElement.findFirstChildNode(DrawFrameElement.class, container);
		while (element != null) {
			if (name.equals(element.getDrawNameAttribute())) {
				Frame frame = Frame.getInstanceof(element);
				frame.mFrameContainer = this;
				return frame;
			}
			element = OdfElement.findNextChildNode(DrawFrameElement.class, element);
		}
		return null;
	}

	/**
	 * Return a list of frame whose usage (specified with "presentation:class")
	 * is a given value.
	 * <p>
	 * Null will be returned if the owner document is not a presentation
	 * document.
	 * 
	 * @param usage
	 *            - the usage value
	 * @return a list of frame whose usage is a given value. Null will be
	 *         returned if the owner document is not a presentation document.
	 * @see org.odftoolkit.simple.PresentationDocument.PresentationClass
	 */
	public List<Frame> getFrameByPresentationclass(PresentationDocument.PresentationClass usage) {
		OdfElement container = getFrameContainerElement();
		Document doc = (Document) ((OdfFileDom) container.getOwnerDocument()).getDocument();
		if (!(doc instanceof PresentationDocument)) {
			return null;
		}
		ArrayList<Frame> al = new ArrayList<Frame>();
		DrawFrameElement element = OdfElement.findFirstChildNode(DrawFrameElement.class, container);
		while (element != null) {
			if (usage.toString().equals(element.getPresentationClassAttribute())) {
				Frame frame = Frame.getInstanceof(element);
				frame.mFrameContainer = this;
				al.add(frame);
			}
			element = OdfElement.findNextChildNode(DrawFrameElement.class, element);
		}
		return al;
	}
}
