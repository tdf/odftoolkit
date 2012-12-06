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

package org.odftoolkit.simple.common;

import org.odftoolkit.odfdom.dom.DefaultElementVisitor;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.text.TextHElement;
import org.odftoolkit.odfdom.dom.element.text.TextLineBreakElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextSElement;
import org.odftoolkit.odfdom.dom.element.text.TextTabElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfNamespace;
import org.w3c.dom.Node;

/**
 * This is a sub class of <code>DefaultElementVisitor</code>, which is used to
 * extract display text from ODF element. For example, if you want to get all of
 * the text content in a slide notes, you can call <code>getOdfElement()</code>
 * to get the ODF element of this notes, then pass it to
 * <code>newOdfTextExtractor</code> to create a TextExtractor. The last step is
 * very easy, you only need to use <code>getText()</code>, all of the text
 * content will be return as string. Another easier way is pass the ODF element
 * to the static method <code>TextExtractor.getText(OdfElement)</code> directly.
 * <p>
 * If you pass the content root which you can get by
 * {@link org.odftoolkit.simple.Document#getContentRoot()
 * Document.getContentRoot()} as the parameter, the whole document content will
 * be returned, without any tag information.
 * <p>
 * This extractor implements parts of ODF elements' white space handling
 * functions. They are text:p, text:h, text:s, text:tab and text:linebreak,
 * which <code>visit()</code> are override to process white space, according to
 * ODF specification.
 * 
 * @see org.odftoolkit.odfdom.pkg.OdfElement
 */
public class TextExtractor extends DefaultElementVisitor {

	protected static final char NewLineChar = '\n';
	protected static final char TabChar = '\t';
	protected final ExtractorStringBuilder mTextBuilder;
	OdfElement mElement;
	
	/**
	 * This class is used to provide the string builder functions to extractor.
	 * It will automatically process the last NewLineChar.
	 * 
	 * @since 0.3.5
	 */
	protected static class ExtractorStringBuilder {
		private StringBuilder mBuilder;
		private boolean lastAppendNewLine;

		ExtractorStringBuilder() {
			mBuilder = new StringBuilder();
			lastAppendNewLine = false;
		}

		/**
		 * Append a string
		 * 
		 * @param str
		 *            - the string
		 */
		public void append(String str) {
			mBuilder.append(str);
		}

		/**
		 * Append a character
		 * 
		 * @param ch
		 *            - the character
		 */
		public void append(char ch) {
			mBuilder.append(ch);
		}

		/**
		 * Append a new line character at the end
		 */
		public void appendLine() {
			mBuilder.append(NewLineChar);
			lastAppendNewLine = true;
		}

		/**
		 * Return the string value.
		 * <p>
		 * If the last character is a new line character and is appended with
		 * appendLine(), the last new line character will be removed.
		 */
		public String toString() {
			if (lastAppendNewLine) {
				mBuilder.deleteCharAt(mBuilder.length() - 1);
			}
			return mBuilder.toString();
		}
	}

	/**
	 * Return the text content of a element as String
	 * 
	 * @param ele
	 *            the ODF element
	 * @return the text content of the element
	 */
	public static synchronized String getText(OdfElement ele) {
		TextExtractor extractor = newOdfTextExtractor(ele);
		return extractor.getText();
	}

	/**
	 * Create a TextExtractor instance using specified ODF element, which text
	 * content can be extracted by <code>getText()</code>.
	 * 
	 * @param element
	 *            the ODF element whose text will be extracted.
	 * @return an instance of TextExtractor
	 */
	public static TextExtractor newOdfTextExtractor(OdfElement element) {
		return new TextExtractor(element);
	}

	/**
	 * Return the text content of specified ODF element as a string.
	 * 
	 * @return the text content as a string
	 */
	public String getText() {
		visit(mElement);
		return mTextBuilder.toString();
	}

	/**
	 * Default constructor
	 */
	protected TextExtractor() {
		mTextBuilder = new ExtractorStringBuilder();
	}

	/**
	 * Constructor with an ODF element as parameter
	 * 
	 * @param element
	 *            the ODF element whose text would be extracted.
	 */
	protected TextExtractor(OdfElement element) {
		mTextBuilder = new ExtractorStringBuilder();
		mElement = element;
	}

	/**
	 * The end users needn't to care of this method, if you don't want to
	 * override the text content handling strategy of <code>OdfElement</code>.
	 * 
	 * @see org.odftoolkit.odfdom.dom.DefaultElementVisitor#visit(org.odftoolkit.odfdom.pkg.OdfElement)
	 */
	@Override
	public void visit(OdfElement element) {
		appendElementText(element);
		if (OdfDocumentNamespace.META.getUri().equals(element.getNamespaceURI())
				|| OdfDocumentNamespace.DC.getUri().equals(element.getNamespaceURI())) {
			mTextBuilder.appendLine();
		}
	}

	/**
	 * The end users needn't to care of this method, if you don't want to
	 * override the text content handling strategy of text:p.
	 * 
	 * @see org.odftoolkit.odfdom.dom.DefaultElementVisitor#visit(org.odftoolkit.odfdom.dom.element.text.TextPElement)
	 */
	@Override
	public void visit(TextPElement ele) {
		appendElementText(ele);
		mTextBuilder.appendLine();
	}

	/**
	 * The end users needn't to care of this method, if you don't want to
	 * override the text content handling strategy of text:h.
	 * 
	 * @see org.odftoolkit.odfdom.dom.DefaultElementVisitor#visit(org.odftoolkit.odfdom.dom.element.text.TextHElement)
	 */
	@Override
	public void visit(TextHElement ele) {
		appendElementText(ele);
		mTextBuilder.appendLine();
	}

	/**
	 * The end users needn't to care of this method, if you don't want to
	 * override the text content handling strategy of text:s.
	 * 
	 * @see org.odftoolkit.odfdom.dom.DefaultElementVisitor#visit(org.odftoolkit.odfdom.dom.element.text.TextSElement)
	 */
	@Override
	public void visit(TextSElement ele) {
		Integer count = ele.getTextCAttribute();
		if (count == null) {
			count = 1;
		}
		for (int i = 0; i < count; i++) {
			mTextBuilder.append(' ');
		}
	}

	/**
	 * The end users needn't to care of this method, if you don't want to
	 * override the text content handling strategy of text:tab.
	 * 
	 * @see org.odftoolkit.odfdom.dom.DefaultElementVisitor#visit(org.odftoolkit.odfdom.dom.element.text.TextTabElement)
	 */
	@Override
	public void visit(TextTabElement ele) {
		mTextBuilder.append(TabChar);
	}

	/**
	 * The end users needn't to care of this method, if you don't want to
	 * override the text content handling strategy of text:linebreak.
	 * 
	 * @see org.odftoolkit.odfdom.dom.DefaultElementVisitor#visit(org.odftoolkit.odfdom.dom.element.text.TextLineBreakElement)
	 */
	@Override
	public void visit(TextLineBreakElement ele) {
		mTextBuilder.append(NewLineChar);
	}

	/**
	 * Append the text content of this element to string buffer.
	 * 
	 * @param ele
	 *            the ODF element whose text will be appended.
	 */
	protected void appendElementText(OdfElement ele) {
		Node node = ele.getFirstChild();
		while (node != null) {
			if (node.getNodeType() == Node.TEXT_NODE) {
				mTextBuilder.append(node.getNodeValue());
			} else if (node.getNodeType() == Node.ELEMENT_NODE) {
				OdfElement element = (OdfElement) node;
				element.accept(this);
			}
			node = node.getNextSibling();
		}
	}
}
