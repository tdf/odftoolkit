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

package org.odftoolkit.simple.text;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.dc.DcCreatorElement;
import org.odftoolkit.odfdom.dom.element.dc.DcDateElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawControlElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeAnnotationElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.element.text.TextAElement;
import org.odftoolkit.odfdom.dom.element.text.TextHElement;
import org.odftoolkit.odfdom.dom.element.text.TextLineBreakElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.odftoolkit.odfdom.dom.element.text.TextSElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.dom.element.text.TextTabElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.pkg.OdfXMLFactory;
import org.odftoolkit.simple.Component;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.PresentationDocument;
import org.odftoolkit.simple.draw.AbstractTextboxContainer;
import org.odftoolkit.simple.draw.Control;
import org.odftoolkit.simple.draw.ControlContainer;
import org.odftoolkit.simple.draw.FrameRectangle;
import org.odftoolkit.simple.draw.Textbox;
import org.odftoolkit.simple.draw.TextboxContainer;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.StyleTypeDefinitions.HorizontalAlignmentType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * This class presents paragraph element in ODF document. It provides methods to
 * manipulate text content, and other child component under the paragraph.
 * Headings and body text paragraphs are collectively referred to as paragraph
 * elements.
 * 
 * @since 0.5
 */
public class Paragraph extends Component implements TextboxContainer,
		TextHyperlinkContainer, ControlContainer {

	private TextPElement mParagraphElement;
	private TextHElement mHeadingElement;
	private Document mOwnerDocument;
	private ParagraphStyleHandler mStyleHandler;
	private TextboxContainerImpl mTextboxContainerImpl;
	private TextHyperlinkContainerImpl mHyperlinkContainerImpl;

	private Paragraph(TextParagraphElementBase paragraphElement) {
		if (paragraphElement instanceof TextPElement) {
			mParagraphElement = (TextPElement) paragraphElement;
			mHeadingElement = null;
		}
		if (paragraphElement instanceof TextHElement) {
			mHeadingElement = (TextHElement) paragraphElement;
			mParagraphElement = null;
		}
		mOwnerDocument = (Document) ((OdfFileDom) paragraphElement.getOwnerDocument()).getDocument();
		mStyleHandler = new ParagraphStyleHandler(this);
	}

	/**
	 * Gets a paragraph instance by an instance of
	 * <code>TextParagraphElementBase</code>.
	 * 
	 * @param paragraphElement
	 *            the instance of TextParagraphElementBase.
	 * @return an instance of paragraph.
	 */
	public static Paragraph getInstanceof(TextParagraphElementBase paragraphElement) {
		if (paragraphElement == null)
			return null;

		Paragraph para = null;
		para = (Paragraph) Component.getComponentByElement(paragraphElement);
		if (para != null)
			return para;

		para = new Paragraph(paragraphElement);
		Component.registerComponent(para, paragraphElement);
		return para;
	}

	/**
	 * Create an instance of paragraph
	 * <p>
	 * The paragrah will be added at the end of this container.
	 * 
	 * @param container
	 *            the paragraph container that contains this paragraph.
	 */
	public static Paragraph newParagraph(ParagraphContainer container) {
		Paragraph para = null;
		OdfElement parent = container.getParagraphContainerElement();
		OdfFileDom ownerDom = (OdfFileDom) parent.getOwnerDocument();
		TextPElement pEle = ownerDom.newOdfElement(TextPElement.class);
		parent.appendChild(pEle);
		para = new Paragraph(pEle);
		Component.registerComponent(para, pEle);

		return para;
	}

	/**
	 * Set the text content of this paragraph.
	 * <p>
	 * All the existing text content of this paragraph would be removed, and
	 * then new text content would be set. The style of the last character will
	 * be inherited.
	 * <p>
	 * The white space characters in the content would be collapsed by default.
	 * For example, tab character would be replaced with <text:tab>, break line
	 * character would be replaced with <text:line-break>.
	 * 
	 * @param content
	 *            - the text content
	 * @see #setTextContentNotCollapsed(String content)
	 */
	public void setTextContent(String content) {
		removeTextContentImpl(getOdfElement());
		Node lastNode = getOdfElement().getLastChild();
		if (lastNode != null && lastNode.getNodeName() != null
				&& (lastNode.getNodeName().equals("text:a") || lastNode.getNodeName().equals("text:span"))) {
			if (content != null && !content.equals(""))
				appendTextElements((TextAElement) lastNode, content, true);
		} else {
			if (content != null && !content.equals(""))
				appendTextElements(getOdfElement(), content, true);
		}
		// remove empty hyperlink
		removeEmptyHyperlink(getOdfElement());
	}

	static void removeEmptyHyperlink(OdfElement element) {
		// remove empty hyperlink
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node;
			node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				String nodename = node.getNodeName();
				if (nodename.equals("text:a") && node.hasChildNodes() == false)
					element.removeChild(node);
			}
		}
	}

	/**
	 * Remove the text content of this paragraph. The empty hyperlink element
	 * will be removed.
	 * <p>
	 * The other child elements except text content will not be removed.
	 * 
	 */
	public void removeTextContent() {
		removeTextContentImpl(getOdfElement());
		removeEmptyHyperlink(getOdfElement());
	}

	static void removeTextContentImpl(OdfElement ownerElement) {
		NodeList nodeList = ownerElement.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node;
			node = nodeList.item(i);
			if (node.getNodeType() == Node.TEXT_NODE) {
				ownerElement.removeChild(node);
				// element removed need reset index.
				i--;
			} else if (node.getNodeType() == Node.ELEMENT_NODE) {
				String nodename = node.getNodeName();
				if (nodename.equals("text:s") || nodename.equals("text:tab") || nodename.equals("text:line-break")) {
					ownerElement.removeChild(node);
					// element removed need reset index.
					i--;
				} else if (nodename.equals("text:a"))
					removeTextContentImpl((OdfElement) node);
			}
		}
	}

	/**
	 * Return the text content of this paragraph.
	 * <p>
	 * The other child elements except text content will not be returned.
	 * 
	 * @return - the text content of this paragraph
	 */
	public String getTextContent() {
		return getTextContent(getOdfElement());
	}

	static String getTextContent(OdfElement ownerEle) {
		StringBuilder buffer = new StringBuilder();
		NodeList nodeList = ownerEle.getChildNodes();
		int i;
		for (i = 0; i < nodeList.getLength(); i++) {
			Node node;
			node = nodeList.item(i);
			if (node.getNodeType() == Node.TEXT_NODE)
				buffer.append(node.getNodeValue());
			else if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node instanceof TextSpanElement) {
					buffer.append(((TextSpanElement) node).getTextContent());
				}
				else if (node.getNodeName().equals("text:s")) {
					Integer count = ((TextSElement) node).getTextCAttribute();
					for (int j = 0; j < (count != null ? count : 0); j++)
						buffer.append(' ');
				} else if (node.getNodeName().equals("text:tab"))
					buffer.append('\t');
				else if (node.getNodeName().equals("text:line-break")) {
					String lineseperator = System.getProperty("line.separator");
					buffer.append(lineseperator);
				} else if (node.getNodeName().equals("text:a"))
					buffer.append(TextHyperlink.getInstanceof((TextAElement) node).getTextContent());
			}
		}
		return buffer.toString();
	}

	/**
	 * Set the text content of this paragraph.
	 * <p>
	 * All the existing text content of this paragraph would be removed, and
	 * then new text content would be set.
	 * <p>
	 * The white space characters in the content would not be collapsed.
	 * 
	 * @param content
	 *            - the text content
	 * @see #setTextContent(String content)
	 */
	public void setTextContentNotCollapsed(String content) {
		removeTextContent();
		if (content != null && !content.equals(""))
			appendTextElements(getOdfElement(), content, false);
	}

	/**
	 * Append the text content at the end of this paragraph. The appended text
	 * would follow the style of the last character.
	 * <p>
	 * The white space characters in the content would be collapsed by default.
	 * For example, tab character would be replaced with <text:tab>, break line
	 * character would be replaced with <text:line-break>.
	 * 
	 * @param content
	 *            - the text content
	 * @see #appendTextContentNotCollapsed(String content)
	 */
	public void appendTextContent(String content) {
		appendTextContent(content, true);
	}

	/**
	 * Append the text content at the end of this paragraph. The appended text
	 * would follow the style of the last character if the second parameter is
	 * set to true; Or else, the appended text would follow the default style of
	 * this paragraph.
	 * <p>
	 * The white space characters in the content would be collapsed by default.
	 * For example, tab character would be replaced with <text:tab>, break line
	 * character would be replaced with <text:line-break>.
	 * 
	 * @param content
	 *            - the text content
	 * @param isStyleInherited
	 *            - whether the style would be inherited by the appended text
	 * @see #appendTextContentNotCollapsed(String content)
	 */
	public void appendTextContent(String content, boolean isStyleInherited) {
		boolean canInherited = false;
		Node lastNode = getOdfElement().getLastChild();
		if (lastNode != null && lastNode.getNodeName() != null
				&& (lastNode.getNodeName().equals("text:a") || lastNode.getNodeName().equals("text:span")))
			canInherited = true;

		if (isStyleInherited && canInherited) {
			if (content != null && !content.equals(""))
				appendTextElements((OdfElement) lastNode, content, true);
		} else {
			if (content != null && !content.equals(""))
				appendTextElements(getOdfElement(), content, true);
		}
	}

	/**
	 * Append the text content at the end of this paragraph. The appended text
	 * would follow the style of the last character.
	 * <p>
	 * The white space characters in the content would not be collapsed.
	 * 
	 * @param content
	 *            - the text content
	 * @see #appendTextContent(String content)
	 */
	public void appendTextContentNotCollapsed(String content) {
		Node lastNode = getOdfElement().getLastChild();
		boolean canInherited = false;
		if (lastNode != null && lastNode.getNodeName() != null
				&& (lastNode.getNodeName().equals("text:a") || lastNode.getNodeName().equals("text:span")))
			canInherited = true;

		if (canInherited) {
			if (content != null && !content.equals(""))
				appendTextElements((OdfElement) lastNode, content, false);
		} else {
			if (content != null && !content.equals(""))
				appendTextElements(getOdfElement(), content, false);
		}
	}

	/**
	 * Set the style name of this paragraph
	 * 
	 * @param styleName
	 *            - the style name
	 */
	public void setStyleName(String styleName) {
		mStyleHandler.getStyleElementForWrite().setStyleNameAttribute(styleName);
	}

	/**
	 * Get the style name of this paragraph
	 * 
	 * @return - the style name
	 */
	public String getStyleName() {
		OdfStyleBase style = getStyleHandler().getStyleElementForRead();
		if (style == null) {
			return "";
		}
		if (style instanceof OdfStyle)
			return ((OdfStyle) style).getStyleNameAttribute();
		else
			return "";
	}

	/**
	 * Get the owner document of this paragraph.
	 * 
	 * @return the document who owns this paragraph.
	 */
	public Document getOwnerDocument() {
		return mOwnerDocument;
	}

	/**
	 * Remove this paragraph from its container.
	 * 
	 * @since 0.6.5
	 */
	public void remove(){
		Component.unregisterComponent(getOdfElement());
		getOdfElement().getParentNode().removeChild(getOdfElement());
		mParagraphElement=null;
		mHeadingElement=null;
		mOwnerDocument=null;
		mStyleHandler=null;
		mTextboxContainerImpl=null;
		mHyperlinkContainerImpl=null;
	}
	
	/**
	 * Get the style handler of this paragraph.
	 * <p>
	 * The style handler is an instance of ParagraphStyleHandler
	 * 
	 * @return an instance of ParagraphStyleHandler
	 * @see ParagraphStyleHandler
	 */
	public ParagraphStyleHandler getStyleHandler() {
		if (mStyleHandler != null)
			return mStyleHandler;
		else {
			mStyleHandler = new ParagraphStyleHandler(this);
			return mStyleHandler;
		}
	}

	/**
	 * Return the <code>TextParagraphElementBase</code> of this paragraph.
	 * Headings and body text paragraphs are collectively referred to as
	 * paragraph elements, so the <code>TextParagraphElementBase</code> can be
	 * <code>TextHElement</code> element or <code>TextPElement</code> element.
	 * 
	 * @return the <code>TextParagraphElementBase</code> of this paragraph.
	 */
	@Override
	public TextParagraphElementBase getOdfElement() {
		if (isHeading()) {
			return mHeadingElement;
		} else {
			return mParagraphElement;
		}
	}

	/**
	 * Creates a comment in the front of this paragraph.
	 * 
	 * @param content
	 *            the content of this comment.
	 * @param creator
	 *            the creator of this comment, if <code>creator</code> is null,
	 *            the value of <code>System.getProperty("user.name")</code> will
	 *            be used.
	 * @since 0.6.5
	 */
	public void addComment(String content, String creator) {
		// create annotation element.
		OdfFileDom dom = (OdfFileDom) getOdfElement().getOwnerDocument();
		OfficeAnnotationElement annotationElement = (OfficeAnnotationElement) OdfXMLFactory.newOdfElement(dom, OdfName
				.newName(OdfDocumentNamespace.OFFICE, "annotation"));
		getOdfElement().insertBefore(annotationElement, getOdfElement().getFirstChild());
		// set creator
		DcCreatorElement dcCreatorElement = annotationElement.newDcCreatorElement();
		if (creator == null) {
			creator = System.getProperty("user.name");
		}
		dcCreatorElement.setTextContent(creator);
		// set date
		String dcDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());
		DcDateElement dcDateElement = annotationElement.newDcDateElement();
		dcDateElement.setTextContent(dcDate);
		TextPElement notePElement = annotationElement.newTextPElement();
		TextSpanElement noteSpanElement = notePElement.newTextSpanElement();
		// set comment style
		OdfOfficeAutomaticStyles styles = null;
		if (dom instanceof OdfContentDom) {
			styles = ((OdfContentDom) dom).getAutomaticStyles();
		} else if (dom instanceof OdfStylesDom) {
			styles = ((OdfStylesDom) dom).getAutomaticStyles();
		}
		OdfStyle textStyle = styles.newStyle(OdfStyleFamily.Text);
		StyleTextPropertiesElement styleTextPropertiesElement = textStyle.newStyleTextPropertiesElement(null);
		styleTextPropertiesElement.setStyleFontNameAttribute("Tahoma");
		styleTextPropertiesElement.setFoFontSizeAttribute("10pt");
		styleTextPropertiesElement.setStyleFontNameAsianAttribute("Lucida Sans Unicode");
		styleTextPropertiesElement.setStyleFontSizeAsianAttribute("12pt");
		noteSpanElement.setStyleName(textStyle.getStyleNameAttribute());
		// set comment content
		noteSpanElement.setTextContent(content);
	}

	/**
	 * Returns the paragraph type, heading or body text paragraph.
	 * 
	 * @return the paragraph type, if this paragraph is heading, returns
	 *         <code>true</code>, otherwise return <code>false</code>.
	 * 
	 * @since 0.6.5
	 */
	public boolean isHeading() {
		if (mHeadingElement != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns outline level of this paragraph.
	 * 
	 * @return outline level, if this paragraph is a body text paragraph, 0 will
	 *         be returned.
	 * 
	 * @since 0.6.5
	 */
	public int getHeadingLevel() {
		if (isHeading()) {
			return mHeadingElement.getTextOutlineLevelAttribute();
		}
		return 0;
	}

	/**
	 * Sets the paragraph type, heading or body text paragraph.
	 * 
	 * @param isHeading
	 *            if <code>true</code>, this paragraph would be formatted as
	 *            heading, otherwise as a body text paragraph.
	 * @param level
	 *            the heading outline level of this paragraph, if
	 *            <code>isHeading</code> is <code>true</code>.
	 * 
	 * @since 0.6.5
	 */
	public void applyHeading(boolean isHeading, int level) {
		if (isHeading) {
			if (!isHeading()) {
				// create new heading element, clone children nodes.
				OdfFileDom ownerDocument = (OdfFileDom) getOdfElement().getOwnerDocument();
				mHeadingElement = ownerDocument.newOdfElement(TextHElement.class);
				Node firstChild = mParagraphElement.getFirstChild();
				while (firstChild != null) {
					// mHeadingElement.appendChild(firstChild.cloneNode(true));
					// firstChild = firstChild.getNextSibling();
					Node thisChild = firstChild;
					firstChild = firstChild.getNextSibling();
					mParagraphElement.removeChild(thisChild);
					mHeadingElement.appendChild(thisChild);
				}
				// update style
				mHeadingElement.setStyleName(mParagraphElement.getStyleName());
				// unregister component
				Component.unregisterComponent(mParagraphElement);
				// replace paragraph with heading
				OdfElement parentOdfElement = (OdfElement) mParagraphElement.getParentNode();
				parentOdfElement.replaceChild(mHeadingElement, mParagraphElement);
				mParagraphElement = null;
				// re-register component.
				Component.registerComponent(this, mHeadingElement);
			}
			// update outline level.
			mHeadingElement.setTextOutlineLevelAttribute(level);
		} else {
			if (isHeading()) {
				// need create new paragraph element and clone content.
				OdfFileDom ownerDocument = (OdfFileDom) getOdfElement().getOwnerDocument();
				mParagraphElement = ownerDocument.newOdfElement(TextPElement.class);
				Node firstChild = mHeadingElement.getFirstChild();
				while (firstChild != null) {
					Node thisChild = firstChild;
					firstChild = firstChild.getNextSibling();
					mHeadingElement.removeChild(thisChild);
					mParagraphElement.appendChild(thisChild);
				}
				// update style
				mParagraphElement.setStyleName(mHeadingElement.getStyleName());
				// unregister component
				Component.unregisterComponent(mHeadingElement);
				// replace heading with paragraph
				OdfElement parentOdfElement = (OdfElement) mHeadingElement.getParentNode();
				parentOdfElement.replaceChild(mParagraphElement, mHeadingElement);
				mHeadingElement = null;
				// re-register component.
				Component.registerComponent(this, mParagraphElement);
			}
		}
	}

	/**
	 * Formats the paragraph as heading. Its outline level is 1.
	 * 
	 * @since 0.6.5
	 */
	public void applyHeading() {
		applyHeading(true, 1);
	}

	/**
	 * Returns the font definition for this paragraph.
	 * 
	 * @return font if there is no style definition for this paragraph,
	 *         <code>null</code> will be returned.
	 * 
	 * @since 0.6.5
	 */
	public Font getFont() {
		return getStyleHandler().getFont(Document.ScriptType.WESTERN);
	}

	/**
	 * Sets font style for this paragraph.
	 * 
	 * @param font
	 *            the font definition of this paragraph
	 * 
	 * @since 0.6.5
	 */
	public void setFont(Font font) {
		getStyleHandler().setFont(font);
	}

	/**
	 * Return the horizontal alignment setting of this paragraph.
	 * <p>
	 * Null will returned if there is no explicit style definition for this
	 * paragraph.
	 * <p>
	 * Default value will be returned if explicit style definition is found but
	 * no horizontal alignment is set.
	 * 
	 * @return the horizontal alignment setting.
	 * @since 0.6.5
	 */
	public HorizontalAlignmentType getHorizontalAlignment() {
		return getStyleHandler().getHorizontalAlignment();
	}

	/**
	 * Set the horizontal alignment setting of this paragraph. If the alignment
	 * is set as Default, the explicit horizontal alignment setting is removed.
	 * 
	 * @param alignType
	 *            the horizontal alignment setting.
	 * @since 0.6.5
	 */
	public void setHorizontalAlignment(HorizontalAlignmentType alignType) {
		getStyleHandler().setHorizontalAlignment(alignType);
	}

	public Textbox addTextbox() {
		return getTextboxContainerImpl().addTextbox();
	}

	public Iterator<Textbox> getTextboxIterator() {
		return getTextboxContainerImpl().getTextboxIterator();
	}

	public boolean removeTextbox(Textbox box) {
		return getTextboxContainerImpl().removeTextbox(box);
	}

	public OdfElement getFrameContainerElement() {
		return getTextboxContainerImpl().getFrameContainerElement();
	}

	public Textbox addTextbox(FrameRectangle position) {
		return getTextboxContainerImpl().addTextbox(position);
	}

	public Textbox getTextboxByName(String name) {
		return getTextboxContainerImpl().getTextboxByName(name);
	}

	public List<Textbox> getTextboxByUsage(PresentationDocument.PresentationClass usage) {
		throw new UnsupportedOperationException("this method is not supported by paragraph.");
	}

	private class TextboxContainerImpl extends AbstractTextboxContainer {
		public OdfElement getFrameContainerElement() {
			return getOdfElement();
		}
	}

	private TextboxContainerImpl getTextboxContainerImpl() {
		if (mTextboxContainerImpl == null)
			mTextboxContainerImpl = new TextboxContainerImpl();
		return mTextboxContainerImpl;
	}

	static void appendTextElements(OdfElement ownerElement, String content, boolean isWhitespaceCollapsed) {
		OdfFileDom ownerDocument = (OdfFileDom) ownerElement.getOwnerDocument();
		if (isWhitespaceCollapsed) {
			int i = 0, length = content.length();
			String str = "";
			while (i < length) {
				char ch = content.charAt(i);
				if (ch == ' ') {
					int j = 1;
					i++;
					while ((i < length) && (content.charAt(i) == ' ')) {
						j++;
						i++;
					}
					if (j == 1) {
						str += ' ';
					} else {
						str += ' ';
						Text textnode = ownerDocument.createTextNode(str);
						ownerElement.appendChild(textnode);
						str = "";
						TextSElement spaceElement = ownerDocument.newOdfElement(TextSElement.class);
						ownerElement.appendChild(spaceElement);
						spaceElement.setTextCAttribute(j - 1);
					}
				} else if (ch == '\n') {
					if (str.length() > 0) {
						Text textnode = ownerDocument.createTextNode(str);
						ownerElement.appendChild(textnode);
						str = "";
					}
					TextLineBreakElement lineBreakElement = ownerDocument.newOdfElement(TextLineBreakElement.class);
					ownerElement.appendChild(lineBreakElement);
					i++;
				} else if (ch == '\t') {
					if (str.length() > 0) {
						Text textnode = ownerElement.getOwnerDocument().createTextNode(str);
						ownerElement.appendChild(textnode);
						str = "";
					}
					TextTabElement tabElement = ownerDocument.newOdfElement(TextTabElement.class);
					ownerElement.appendChild(tabElement);
					i++;
				} else if (ch == '\r') {
					i++;
				} else {
					str += ch;
					i++;
				}
			}
			if (str.length() > 0) {
				Text textnode = ownerDocument.createTextNode(str);
				ownerElement.appendChild(textnode);
			}
		} else {
			Text textnode = ownerDocument.createTextNode(content);
			ownerElement.appendChild(textnode);
		}
	}

	/************ Hyperlink support ************/
	public TextHyperlink applyHyperlink(URI linkto) {
		return getTextHyperlinkContainerImpl().applyHyperlink(linkto);
	}

	public Iterator<TextHyperlink> getHyperlinkIterator() {
		return getTextHyperlinkContainerImpl().getHyperlinkIterator();
	}

	public void removeHyperlinks() {
		getTextHyperlinkContainerImpl().removeHyperlinks();
	}

	public TextHyperlink appendHyperlink(String text, URI linkto) {
		return getTextHyperlinkContainerImpl().appendHyperlink(text, linkto);
	}

	private class TextHyperlinkContainerImpl extends AbstractTextHyperlinkContainer {
		public TextHyperlinkContainerImpl(OdfElement parent) {
			super(parent);
		}
	}

	private TextHyperlinkContainerImpl getTextHyperlinkContainerImpl() {
		if (mHyperlinkContainerImpl == null)
			mHyperlinkContainerImpl = new TextHyperlinkContainerImpl(getOdfElement());
		return mHyperlinkContainerImpl;
	}
	/************ End of Hyperlink support ************/

//	@Override
	public Control createDrawControl() {
		return Control.newDrawControl(this);
	}

//	@Override
	public OdfElement getDrawControlContainerElement() {
		return this.getOdfElement();
	}
}
