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

import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import net.rootdev.javardfa.Constants;
import net.rootdev.javardfa.Setting;
import net.rootdev.javardfa.StatementSink;
import net.rootdev.javardfa.literal.LiteralCollector;
import net.rootdev.javardfa.uri.IRIResolver;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * A RDFa parser for SAX
 */
public class SAXRDFaParser extends RDFaParser {


	public static SAXRDFaParser createInstance(JenaSink sink) {
		URIExtractor extractor = new URIExtractorImpl(new IRIResolver(), true);
		sink.setExtractor(extractor);
		return new SAXRDFaParser(sink, XMLOutputFactory.newInstance(),
				XMLEventFactory.newInstance(), extractor);
	}

	private SAXRDFaParser(JenaSink sink, XMLOutputFactory outputFactory,
			XMLEventFactory eventFactory, URIExtractor extractor) {
		super(sink, outputFactory, eventFactory, extractor);
	}

	public void emitTriples(String subj, Collection<String> props, String obj) {
		for (String prop : props) {
			sink.addObject(subj, prop, obj);
		}
	}

	public void emitTriplesPlainLiteral(String subj, Collection<String> props,
			String lex, String language) {
		for (String prop : props) {
			sink.addLiteral(subj, prop, lex, language, null);
		}
	}

	public void emitTriplesDatatypeLiteral(String subj,
			Collection<String> props, String lex, String datatype) {
		for (String prop : props) {
			sink.addLiteral(subj, prop, lex, null, datatype);
		}
	}

	public void setDocumentLocator(Locator arg0) {
		this.locator = arg0;
		if (locator.getSystemId() != null)
			this.setBase(arg0.getSystemId());
	}

	public void startDocument() throws SAXException {
		sink.start();
	}

	public void endDocument() throws SAXException {
		sink.end();
		sink.setContext(context);
	}

	public void startPrefixMapping(String arg0, String arg1)
			throws SAXException {
		context.setNamespaceURI(arg0, arg1);
		extractor.setNamespaceURI(arg0, arg1);
		sink.addPrefix(arg0, arg1);
	}

	public void endPrefixMapping(String arg0) throws SAXException {
	}

	public void startElement(String arg0, String localname, String qname,
			Attributes arg3) throws SAXException {
		super.beginRDFaElement(arg0, localname, qname, arg3);
	}

	public void endElement(String arg0, String localname, String qname)
			throws SAXException {
		super.endRDFaElement(arg0, localname, qname);
	}

	public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
		super.writeCharacters(String.valueOf(arg0, arg1, arg2));
	}

	public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
			throws SAXException {
		// System.err.println("Whitespace...");
		if (literalCollector.isCollecting()) {
			XMLEvent e = eventFactory.createIgnorableSpace(String.valueOf(arg0,
					arg1, arg2));
			literalCollector.handleEvent(e);
		}
	}

	public void processingInstruction(String arg0, String arg1)
			throws SAXException {
	}

	public void skippedEntity(String arg0) throws SAXException {
	}

}

/*
 * (c) Copyright 2009 University of Bristol All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The name of the author may not
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
