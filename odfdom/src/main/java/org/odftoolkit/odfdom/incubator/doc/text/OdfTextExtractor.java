/************************************************************************
*
*  Licensed to the Apache Software Foundation (ASF) under one
*  or more contributor license agreements.  See the NOTICE file
*  distributed with this work for additional information
*  regarding copyright ownership.  The ASF licenses this file
*  to you under the Apache License, Version 2.0 (the
*  "License"); you may not use this file except in compliance
*  with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*
************************************************************************/
package org.odftoolkit.odfdom.incubator.doc.text;

import org.odftoolkit.odfdom.dom.DefaultElementVisitor;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.text.TextHElement;
import org.odftoolkit.odfdom.dom.element.text.TextLineBreakElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextSElement;
import org.odftoolkit.odfdom.dom.element.text.TextTabElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.w3c.dom.Node;

/**
 * It's a sub class of DefaultElementVisitor. It provides a method to get the display text
 * of a single element.
 * <p> If you pass the content root as the parameter, the whole document content will be
 * returned, without any tag information.</p>
 * <p> It implements part of white space handling fuctions: text:p, text:h, text:s, text:tab, text:linebreak are processed
 * according to ODF specification.</p>
 *
 */
public class OdfTextExtractor extends DefaultElementVisitor {

	protected StringBuilder mTextBuilder;
	OdfElement mElement;
	protected static final char NewLineChar = '\r';
	protected static final char TabChar = '\t';

	/**
	 * Default constructor
	 */
	protected OdfTextExtractor() {
	}

	/**
	 * Constructor with an ODF element as paramter
	 * @param element the ODF element whose text would be extracted.
	 */
	protected OdfTextExtractor(OdfElement element) {
		mTextBuilder = new StringBuilder();
		mElement = element;
	}

	/**
	 * Append the text content of this element to string buffer.
	 * @param ele the ODF element whose text will be appended.
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

	/**
	 * An instance of OdfTextExtractor will be created to
	 * extract the text content of an ODF element.
	 * @param element the ODF element whose text will be extracted.
	 * @return An instance of OdfTextExtractor
	 */
	public static OdfTextExtractor newOdfTextExtractor(OdfElement element) {
		return new OdfTextExtractor(element);
	}

	/* (non-Javadoc)
	 * @see org.odftoolkit.odfdom.dom.DefaultElementVisitor#visit(org.odftoolkit.odfdom.pkg.OdfElement)
	 */
	@Override
	public void visit(OdfElement element) {
		if (element.getNamespaceURI().equals(OdfDocumentNamespace.META.getUri())
				|| element.getNamespaceURI().equals(OdfDocumentNamespace.DC.getUri())) {
			mTextBuilder.append(NewLineChar);
		}
		appendElementText(element);
	}

	/* (non-Javadoc)
	 * @see org.odftoolkit.odfdom.dom.DefaultElementVisitor#visit(org.odftoolkit.odfdom.dom.element.text.TextPElement)
	 */
	@Override
	public void visit(TextPElement ele) {
		mTextBuilder.append(NewLineChar);
		appendElementText(ele);
	}

	/* (non-Javadoc)
	 * @see org.odftoolkit.odfdom.dom.DefaultElementVisitor#visit(org.odftoolkit.odfdom.dom.element.text.TextHElement)
	 */
	@Override
	public void visit(TextHElement ele) {
		mTextBuilder.append(NewLineChar);
		appendElementText(ele);
	}

	/* (non-Javadoc)
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

	/* (non-Javadoc)
	 * @see org.odftoolkit.odfdom.dom.DefaultElementVisitor#visit(org.odftoolkit.odfdom.dom.element.text.TextTabElement)
	 */
	@Override
	public void visit(TextTabElement ele) {
		mTextBuilder.append(TabChar);
	}

	/* (non-Javadoc)
	 * @see org.odftoolkit.odfdom.dom.DefaultElementVisitor#visit(org.odftoolkit.odfdom.dom.element.text.TextLineBreakElement)
	 */
	@Override
	public void visit(TextLineBreakElement ele) {
		mTextBuilder.append(NewLineChar);
		appendElementText(ele);
	}

	/**
	 * Return the text content as a string
	 * @return the text content as a string
	 */
	public String getText() {
		visit(mElement);
		return mTextBuilder.toString();
	}
}
