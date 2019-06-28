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
package org.odftoolkit.odfdom.pkg.rdfa;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLOutputFactory;
import org.odftoolkit.odfdom.dom.element.text.TextBookmarkStartElement;
import org.w3c.dom.Node;

/**
 * A RDFa parser for DOM
 *
 */
public class DOMRDFaParser extends RDFaParser {

	private static final XMLOutputFactory DEFAULT_XML_OUTPUT_FACTORY = XMLOutputFactory.newFactory();
	private static final XMLEventFactory DEFAULT_XML_EVENT_FACTORY = XMLEventFactory.newFactory();

	public static DOMRDFaParser createInstance(JenaSink sink) {
		sink.getExtractor().setForSAX(false);
		return new DOMRDFaParser(sink, sink.getExtractor());
	}

	public DOMRDFaParser(JenaSink sink, XMLOutputFactory outputFactory,
			XMLEventFactory eventFactory, URIExtractor extractor) {
		super(sink, outputFactory, eventFactory, extractor);

	}

    public DOMRDFaParser(JenaSink sink, URIExtractor extractor) {
 		this(sink, DEFAULT_XML_OUTPUT_FACTORY, DEFAULT_XML_EVENT_FACTORY, extractor);
 	}

	/**
	 * Parse the RDFa in-content metadata of the node.
	 *
	 * @param node
	 */
	public void parse(Node node) {
		process(node);
	}

	private void process(Node node) {

		switch (node.getNodeType()) {
		case Node.ELEMENT_NODE:
			if (!(node instanceof TextBookmarkStartElement)) {
				sink.setContextNode(node);
			}
			// Start element
			beginRDFaElement(node.getNamespaceURI(), node.getLocalName(),
					node.getNodeName(), new DOMAttributes(node.getAttributes()));
			// Recurse to child
//			if (node.hasChildNodes() == true) {
//				process(node.getFirstChild());
//			}
			if (node.hasChildNodes() == true) {
				Node n = node.getFirstChild();
				process(n);
				while (n.getNextSibling()!=null){
					process(n.getNextSibling());
					n= n.getNextSibling();
				}
			}

			// End element
			endRDFaElement(node.getNamespaceURI(), node.getLocalName(),
					node.getNodeName());
			break;
		case Node.CDATA_SECTION_NODE:
		case Node.TEXT_NODE:
			// Text or CDATA
			writeCharacters(node.getNodeValue());
			break;
		}

	}
}
