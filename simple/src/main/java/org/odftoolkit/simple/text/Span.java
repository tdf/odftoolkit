package org.odftoolkit.simple.text;

import org.odftoolkit.odfdom.dom.element.text.TextSElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Component;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.common.navigation.InvalidNavigationException;
import org.odftoolkit.simple.common.navigation.TextSelection;
import org.odftoolkit.simple.style.DefaultStyleHandler;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * This class represents the application of a style to the character data of a
 * portion of text.
 * <p>
 * It provides convenient methods to create a span and manipulate attributes of
 * a span.
 * 
 * @since 0.5.5
 */
public class Span extends Component {

	private TextSpanElement mSpanElement;
	private Document mOwnerDocument;
	private DefaultStyleHandler mStyleHandler;

	private Span(TextSpanElement element) {
		mSpanElement = element;
		mOwnerDocument = (Document) ((OdfFileDom) element.getOwnerDocument()).getDocument();
		mStyleHandler = new DefaultStyleHandler(element);
	}

	/**
	 * Get a span instance by an instance of <code>TextSpanElement</code>.
	 * 
	 * @param sElement
	 *            - the instance of TextSpanElement
	 * @return an instance of span
	 */
	public static Span getInstanceof(TextSpanElement sElement) {
		if (sElement == null)
			return null;

		Span span = null;
		span = (Span) Component.getComponentByElement(sElement);
		if (span != null)
			return span;

		span = new Span(sElement);
		Component.registerComponent(span, sElement);
		return span;
	}

	/**
	 * Create a span instance with a text selection
	 * 
	 * @param text
	 * @return an instance of span
	 * @see org.odftoolkit.simple.common.navigation.TextSelection
	 */
	public static Span newSpan(TextSelection textSelection) {
		try {
			TextSpanElement element = textSelection.createSpanElement();
			return Span.getInstanceof(element);
		} catch (InvalidNavigationException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Get the style handler of this span.
	 * <p>
	 * The style handler is an instance of DefaultStyleHandler
	 * 
	 * @return an instance of DefaultStyleHandler
	 * @see org.odftoolkit.simple.style.DefaultStyleHandler
	 */
	public DefaultStyleHandler getStyleHandler() {
		if (mStyleHandler != null)
			return mStyleHandler;
		else {
			mStyleHandler = new DefaultStyleHandler(mSpanElement);
			return mStyleHandler;
		}
	}

	/**
	 * Get the owner document of this span
	 * 
	 * @return the document who owns this span
	 */
	public Document getOwnerDocument() {
		return mOwnerDocument;
	}

	/**
	 * Return the instance of "text:span" element
	 * 
	 * @return the instance of "text:span" element
	 */
	@Override
	public TextSpanElement getOdfElement() {
		return mSpanElement;
	}

	/**
	 * Remove the text content of this span.
	 * 
	 */
	public void removeTextContent() {
		NodeList nodeList = mSpanElement.getChildNodes();
		int i;
		for (i = 0; i < nodeList.getLength(); i++) {
			Node node;
			node = nodeList.item(i);
			if (node.getNodeType() == Node.TEXT_NODE)
				mSpanElement.removeChild(node);
			else if (node.getNodeType() == Node.ELEMENT_NODE) {
				String nodename = node.getNodeName();
				if (nodename.equals("text:s") || nodename.equals("text:tab") || nodename.equals("text:line-break"))
					mSpanElement.removeChild(node);
			}
		}
	}

	/**
	 * Set the text content of this span.
	 * <p>
	 * All the existing text content of this paragraph would be removed, and
	 * then new text content would be set.
	 * 
	 * @param content
	 *            - the text content
	 */
	public void setTextContent(String content) {
		removeTextContent();
		if (content != null && !content.equals(""))
			appendTextElements(content, true);
	}

	/**
	 * Return the text content of this span.
	 * <p>
	 * The other child elements except text content will not be returned.
	 * 
	 * @return - the text content of this span
	 */
	public String getTextContent() {
		StringBuffer buffer = new StringBuffer();
		NodeList nodeList = mSpanElement.getChildNodes();
		int i;
		for (i = 0; i < nodeList.getLength(); i++) {
			Node node;
			node = nodeList.item(i);
			if (node.getNodeType() == Node.TEXT_NODE)
				buffer.append(node.getNodeValue());
			else if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getNodeName().equals("text:s")) {
					int count = ((TextSElement) node).getTextCAttribute();
					for (int j = 0; j < count; j++)
						buffer.append(' ');
				} else if (node.getNodeName().equals("text:tab"))
					buffer.append('\t');
				else if (node.getNodeName().equals("text:line-break")) {
					String lineseperator = System.getProperty("line.separator");
					buffer.append(lineseperator);
				}
			}
		}
		return buffer.toString();
	}

	/**
	 * Append the text content at the end of this paragraph.
	 * 
	 * @param content
	 *            - the text content
	 */
	public void appendTextContent(String content) {
		if (content != null && !content.equals(""))
			appendTextElements(content, true);
	}

	private void appendTextElements(String content, boolean isWhitespaceCollapsed) {
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
						Text textnode = mSpanElement.getOwnerDocument().createTextNode(str);
						mSpanElement.appendChild(textnode);
						str = "";
						TextSElement spaceElement = mSpanElement.newTextSElement();
						spaceElement.setTextCAttribute(j - 1);
					}
				} else if (ch == '\n') {
					if (str.length() > 0) {
						Text textnode = mSpanElement.getOwnerDocument().createTextNode(str);
						mSpanElement.appendChild(textnode);
						str = "";
					}
					mSpanElement.newTextLineBreakElement();
					i++;
				} else if (ch == '\t') {
					if (str.length() > 0) {
						Text textnode = mSpanElement.getOwnerDocument().createTextNode(str);
						mSpanElement.appendChild(textnode);
						str = "";
					}
					mSpanElement.newTextTabElement();
					i++;
				} else if (ch == '\r') {
					i++;
				} else {
					str += ch;
					i++;
				}
			}
			if (str.length() > 0) {
				Text textnode = mSpanElement.getOwnerDocument().createTextNode(str);
				mSpanElement.appendChild(textnode);
			}
		} else {
			Text textnode = mSpanElement.getOwnerDocument().createTextNode(content);
			mSpanElement.appendChild(textnode);
		}
	}
}
