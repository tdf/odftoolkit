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

import java.util.Iterator;

import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawTextBoxElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Component;
import org.odftoolkit.simple.common.TextExtractor;
import org.odftoolkit.simple.style.StyleTypeDefinitions.CellBordersType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.OdfDrawStroke;
import org.odftoolkit.simple.text.AbstractParagraphContainer;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.text.ParagraphContainer;
import org.odftoolkit.simple.text.list.AbstractListContainer;
import org.odftoolkit.simple.text.list.List;
import org.odftoolkit.simple.text.list.ListContainer;
import org.odftoolkit.simple.text.list.ListDecorator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class provides method to set the content, properties and style of text
 * box.
 * 
 * @since 0.5
 */
public class Textbox extends Frame implements ListContainer, ParagraphContainer {

	/*
	 * The <draw:frame> element is usable within the following elements:
	 * Supported: <draw:page> 10.2.4, <draw:text-box> 10.4.3, <office:text> 3.4,
	 * <presentation:notes> 16.17, <style:handout-master> 10.2.1,
	 * <style:master-page> 16.9, <table:covered-table-cell> 9.1.5,
	 * <table:table-cell> 9.1.4, <text:p> 5.1.3, <text:section> 5.4 Not
	 * supported: <draw:a> 10.4.12, <draw:g> 10.3.15, <office:image> 3.9,
	 * <table:shapes> 9.2.8, <text:a> 6.1.8, <text:deletion> 5.5.4, <text:h>
	 * 5.1.2, <text:index-body> 8.2.2, <text:index-title> 8.2.3, <text:meta>
	 * 6.1.9, <text:meta-field> 7.5.19, <text:note-body> 6.3.4, <text:ruby-base>
	 * 6.4.2,and <text:span> 6.1.7.
	 * 
	 * The <draw:text-box> element is usable within the following element:
	 * <draw:frame> 10.4.2.
	 * 
	 * <draw:text-box> element has following children: <dr3d:scene> 10.5.2,
	 * <draw:a> 10.4.12, <draw:caption> 10.3.11, <draw:circle> 10.3.8,
	 * <draw:connector> 10.3.10, <draw:control> 10.3.13, <draw:custom-shape>
	 * 10.6.1, <draw:ellipse> 10.3.9, <draw:frame> 10.4.2, <draw:g> 10.3.15,
	 * <draw:line> 10.3.3, <draw:measure> 10.3.12, <draw:page-thumbnail>
	 * 10.3.14, <draw:path> 10.3.7, <draw:polygon> 10.3.5, <draw:polyline>
	 * 10.3.4, <draw:rect> 10.3.2, <draw:regular-polygon> 10.3.6, <table:table>
	 * 9.1.2, <text:alphabetical-index> 8.8, <text:bibliography> 8.9,
	 * <text:change> 5.5.7.4, <text:change-end> 5.5.7.3, <text:change-start>
	 * 5.5.7.2, <text:h> 5.1.2, <text:illustration-index> 8.4, <text:list>
	 * 5.3.1, <text:numbered-paragraph> 5.3.6, <text:object-index> 8.6, <text:p>
	 * 5.1.3, <text:section> 5.4, <text:soft-page-break> 5.6, <text:table-index>
	 * 8.5, <text:table-of-content> 8.3 and <text:user-index> 8.7.
	 */
	DrawTextBoxElement mTextboxElement;
	private ParagraphContainerImpl paragraphContainerImpl;
	private ListContainerImpl listContainerImpl;

	private Textbox(DrawTextBoxElement textbox) {
		super((DrawFrameElement) textbox.getParentNode());
		mTextboxElement = textbox;

	}

	/**
	 * Get a text box instance by an instance of <code>DrawTextBoxElement</code>
	 * .
	 * 
	 * @param element
	 *            - the instance of DrawTextBoxElement
	 * @return an instance of text box
	 */
	public static Textbox getInstanceof(DrawTextBoxElement element) {
		if (element == null)
			return null;

		Textbox textbox = null;
		textbox = (Textbox) Component.getComponentByElement(element);
		if (textbox != null)
			return textbox;

		textbox = new Textbox(element);
		Component.registerComponent(textbox, element);
		return textbox;
	}

	/**
	 * Create an instance of frame
	 * <p>
	 * The frame will be added at the end of this container.
	 * 
	 * @param container
	 *            - the frame container that contains this frame.
	 */
	public static Textbox newTextbox(TextboxContainer container) {
		Textbox textbox = null;
		OdfElement parent = container.getFrameContainerElement();
		OdfFileDom ownerDom = (OdfFileDom) parent.getOwnerDocument();
		DrawFrameElement fElement = ownerDom.newOdfElement(DrawFrameElement.class);
		parent.appendChild(fElement);
		DrawTextBoxElement boxElement = fElement.newDrawTextBoxElement();
		textbox = new Textbox(boxElement);
		textbox.mFrameContainer = container;
		Component.registerComponent(textbox, boxElement);

		// set text box default style
		textbox.getStyleHandler().setBorders(null, CellBordersType.NONE);
		textbox.getStyleHandler().setStroke(OdfDrawStroke.NONE, null, null, null);
		textbox.getStyleHandler().setBackgroundColor(null);
		// set style:run-through="foreground"
		textbox.getStyleHandler().setBackgroundFrame(false);
		return textbox;
	}

	/**
	 * Set the text content of this text box.
	 * <p>
	 * This method will clear the content at first, and then create a paragraph
	 * with the given content.
	 * 
	 * @param content
	 *            - the text content
	 * @see #clearContent()
	 * @see #addParagraph(String textContent)
	 */
	public void setTextContent(String content) {
		clearContent();
		addParagraph(content);
	}

	/**
	 * Remove all the content of this text box.
	 */
	public void clearContent() {
		NodeList nodeList = mTextboxElement.getChildNodes();
		int i;
		for (i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE || node.getNodeType() == node.TEXT_NODE)
				mTextboxElement.removeChild(nodeList.item(i));
		}
	}

	/**
	 * Get the text content of this text box.
	 * 
	 * @return - the text content
	 * @see TextExtractor#getText(OdfElement)
	 */
	public String getTextContent() {
		return TextExtractor.getText(mTextboxElement);
	}

	/**
	 * Get the instance of <code>DrawTextBoxElement</code> which represents this
	 * frame.
	 * 
	 * @return the instance of <code>DrawTextBoxElement</code>
	 */
	@Override
	public DrawTextBoxElement getOdfElement() {
		return mTextboxElement;
	}

	// ***************List support*************************//

	public List addList() {
		return getListContainerImpl().addList();
	}

	public List addList(ListDecorator decorator) {
		return getListContainerImpl().addList(decorator);
	}

	public void clearList() {
		getListContainerImpl().clearList();
	}

	public OdfElement getListContainerElement() {
		return getListContainerImpl().getListContainerElement();
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
			return mTextboxElement;
		}
	}

	// ****************Paragraph support******************//

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
		public OdfElement getParagraphContainerElement() {
			return mTextboxElement;
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
