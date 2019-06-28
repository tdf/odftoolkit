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
package org.odftoolkit.odfdom.dom.rdfa;

import org.apache.jena.rdf.model.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.odftoolkit.odfdom.dom.DefaultElementVisitor;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.text.TextBookmarkEndElement;
import org.odftoolkit.odfdom.dom.element.text.TextBookmarkStartElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.rdfa.DOMAttributes;
import org.odftoolkit.odfdom.pkg.rdfa.JenaSink;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;

/**
 * This is a sub class of <code>DefaultElementVisitor</code>, which is used to
 * extract metadata from {@odf.element text:bookmark-start} to
 * {@odf.element text:bookmark-end} pair.
 */
public class BookmarkRDFMetadataExtractor extends DefaultElementVisitor {

	protected static final char NewLineChar = '\n';
	protected static final char TabChar = '\t';
	private TextBookmarkStartElement bookmarkstart;
	private boolean found;
	protected final Map<TextBookmarkStartElement, ExtractorStringBuilder> builderMap;
	protected final Map<TextBookmarkStartElement, String> stringMap;

	private XMLEventFactory eventFactory = XMLEventFactory.newInstance();

	private JenaSink sink;

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
	 * Create a BookmarkRDFMetadataExtractor instance, which RDF metadata
	 * content of bookmarks can be extracted by
	 * <code>getBookmarkRDFMetadata()</code>.
	 *
	 * @param element
	 *            the ODF element whose text will be extracted.
	 * @return an instance of BookmarkRDFMetadataExtractor
	 */
	public static BookmarkRDFMetadataExtractor newBookmarkTextExtractor() {
		return new BookmarkRDFMetadataExtractor();
	}

	/**
	 * Return the RDF metadata of specified ODF element as a Jena Model.
	 *
	 * @return the text content as a string
	 */
	public Model getBookmarkRDFMetadata(OdfFileDom dom) {
		this.bookmarkstart = null;
		this.found = false;
		this.sink = dom.getSink();
		visit(dom.getRootElement());
		return getModel();
	}

	public Model getBookmarkRDFMetadata(TextBookmarkStartElement bookmarkstart) {
		this.bookmarkstart = bookmarkstart;
		this.found = false;
		this.sink = ((OdfFileDom) bookmarkstart.getOwnerDocument()).getSink();
		visit(((OdfFileDom) bookmarkstart.getOwnerDocument()).getRootElement());
		return getModel();
	}

	private Model getModel() {
		Model m = ModelFactory.createDefaultModel();
		for (Entry<TextBookmarkStartElement, String> entry : stringMap
				.entrySet()) {
			String xhtmlAbout = entry.getKey().getXhtmlAboutAttribute();
			String xhtmlProperty = entry.getKey().getXhtmlPropertyAttribute();
			String xhtmlContent = entry.getKey().getXhtmlContentAttribute();
			if (xhtmlAbout != null && xhtmlProperty != null) {
				String qname = entry.getKey().getNodeName();
				String namespaceURI = entry.getKey().getNamespaceURI();
				String localname = entry.getKey().getLocalName();
				String prefix = (qname.indexOf(':') == -1) ? "" : qname
						.substring(0, qname.indexOf(':'));

				StartElement e = eventFactory.createStartElement(prefix,
						namespaceURI, localname,
						fromAttributes(new DOMAttributes(entry.getKey()
								.getAttributes())), null, sink.getContext());

				xhtmlAbout = sink.getExtractor().expandSafeCURIE(e, xhtmlAbout,
						sink.getContext());
				xhtmlProperty = sink.getExtractor().expandCURIE(e,
						xhtmlProperty, sink.getContext());
				Resource s = m.createResource(xhtmlAbout);
				Property p = m.createProperty(xhtmlProperty);
				if (xhtmlContent != null) {
					s.addLiteral(p, xhtmlContent);
				} else {
					s.addLiteral(p, entry.getValue());
				}

			}
		}
		return m;
	}

	private Iterator fromAttributes(Attributes attributes) {
		List toReturn = new LinkedList();

		for (int i = 0; i < attributes.getLength(); i++) {
			String qname = attributes.getQName(i);
			String prefix = qname.contains(":") ? qname.substring(0,
					qname.indexOf(":")) : "";
			Attribute attr = eventFactory.createAttribute(prefix,
					attributes.getURI(i), attributes.getLocalName(i),
					attributes.getValue(i));

			if (!qname.equals("xmlns") && !qname.startsWith("xmlns:"))
				toReturn.add(attr);
		}

		return toReturn.iterator();
	}

	/**
	 * Constructor with an ODF element as parameter
	 *
	 * @param element
	 *            the ODF element whose text would be extracted.
	 */
	private BookmarkRDFMetadataExtractor() {
		builderMap = new HashMap<TextBookmarkStartElement, ExtractorStringBuilder>();
		stringMap = new HashMap<TextBookmarkStartElement, String>();
	}

	/**
	 * The end users needn't to care of this method, if you don't want to
	 * override the text content handling strategy of <code>OdfElement</code>.
	 *
	 * @see org.odftoolkit.odfdom.dom.DefaultElementVisitor#visit(org.odftoolkit.odfdom.pkg.OdfElement)
	 */
	@Override
	public void visit(OdfElement element) {
		if (bookmarkstart != null && found) {
			return;
		}
		if (this.bookmarkstart == null) {
			if (element instanceof TextBookmarkStartElement) {
				builderMap.put((TextBookmarkStartElement) element,
						new ExtractorStringBuilder());
			}

		} else {
			if (element == bookmarkstart) {
				builderMap.put((TextBookmarkStartElement) element,
						new ExtractorStringBuilder());
			}
		}
		appendElementText(element);
		if (element.getNamespaceURI()
				.equals(OdfDocumentNamespace.META.getUri())
				|| element.getNamespaceURI().equals(
						OdfDocumentNamespace.DC.getUri())) {
			// textBuilderAppendLine();
		}
	}

	/**
	 * Append the text content of this element to string buffer.
	 *
	 * @param ele
	 *            the ODF element whose text will be appended.
	 */
	private void appendElementText(OdfElement ele) {
		Node node = ele.getFirstChild();
		while (node != null) {
			if (node.getNodeType() == Node.TEXT_NODE) {
				textBuilderAppend(node.getNodeValue());
			} else if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node instanceof TextBookmarkEndElement) {
					TextBookmarkEndElement end = (TextBookmarkEndElement) node;
					endBookmark(end);
				}

				OdfElement element = (OdfElement) node;
				element.accept(this);
			}
			node = node.getNextSibling();
		}
	}

	private void textBuilderAppendLine() {
		for (Entry<TextBookmarkStartElement, ExtractorStringBuilder> entry : builderMap
				.entrySet()) {
			entry.getValue().appendLine();
		}
	}

	private void textBuilderAppend(char ch) {
		for (Entry<TextBookmarkStartElement, ExtractorStringBuilder> entry : builderMap
				.entrySet()) {
			entry.getValue().append(ch);
		}
	}

	private void textBuilderAppend(String str) {
		for (Entry<TextBookmarkStartElement, ExtractorStringBuilder> entry : builderMap
				.entrySet()) {
			entry.getValue().append(str);
		}
	}

	private void endBookmark(TextBookmarkEndElement end) {
		TextBookmarkStartElement start = null;
		for (Entry<TextBookmarkStartElement, ExtractorStringBuilder> entry : builderMap
				.entrySet()) {
			if (entry.getKey().getTextNameAttribute()
					.equals(end.getTextNameAttribute())) {
				start = entry.getKey();
				break;
			}
		}
		if (start != null) {
			stringMap.put(start, builderMap.get(start).toString());
			builderMap.remove(start);
			if (bookmarkstart != null) {
				found = true;
			}
		}
	}
}
