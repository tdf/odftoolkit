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

import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import net.rootdev.javardfa.Constants;
import net.rootdev.javardfa.ProfileCollector;
import net.rootdev.javardfa.Setting;
import net.rootdev.javardfa.literal.LiteralCollector;
import net.rootdev.javardfa.uri.IRIResolver;
import net.rootdev.javardfa.uri.URIExtractor10;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;

/**
 * A RDFa Parser modified from net.rootdev.javardfa.Parser
 *
 */
class RDFaParser extends net.rootdev.javardfa.Parser {

	boolean ignore = false;

	protected XMLEventFactory eventFactory;
	protected JenaSink sink;
	protected Set<Setting> settings;
	protected LiteralCollector literalCollector;
	protected URIExtractor extractor;
	protected Locator locator;
	protected EvalContext context;

	protected RDFaParser(JenaSink sink, XMLOutputFactory outputFactory,
			XMLEventFactory eventFactory, URIExtractor extractor) {
		super(sink, outputFactory, eventFactory, new URIExtractor10(new IRIResolver()), ProfileCollector.EMPTY_COLLECTOR);
		this.sink = sink;
		this.eventFactory = eventFactory;
		this.settings = EnumSet.noneOf(Setting.class);
		this.extractor = extractor;

		this.literalCollector = new LiteralCollector(this, eventFactory,
				outputFactory);

		extractor.setSettings(settings);

		// Important, although I guess the caller doesn't get total control
		outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES,
				true);
	}

	protected void beginRDFaElement(String arg0, String localname,
			String qname, Attributes arg3) {
		if (localname.equals("bookmark-start")) {
			ignore = true;
			return;
		}
		try {
			// System.err.println("Start element: " + arg0 + " " + arg1 + " " +
			// arg2);

			// This is set very late in some html5 cases (not even ready by
			// document start)
			if (context == null) {
				this.setBase(locator.getSystemId());
			}

			// Dammit, not quite the same as XMLEventFactory
			String prefix = /* (localname.equals(qname)) */
			(qname.indexOf(':') == -1) ? "" : qname.substring(0,
					qname.indexOf(':'));
			if (settings.contains(Setting.ManualNamespaces)) {
				getNamespaces(arg3);
				if (prefix.length() != 0) {
					arg0 = context.getNamespaceURI(prefix);
					localname = localname.substring(prefix.length() + 1);
				}
			}
			StartElement e = eventFactory.createStartElement(prefix, arg0,
					localname, fromAttributes(arg3), null, context);

			if (literalCollector.isCollecting())
				literalCollector.handleEvent(e);

			// If we are gathering XML we stop parsing
			if (!literalCollector.isCollectingXML())
				context = parse(context, e);
		} catch (XMLStreamException ex) {
			throw new RuntimeException("Streaming issue", ex);
		}
	}

	protected void endRDFaElement(String arg0, String localname, String qname) {
		if (localname.equals("bookmark-start")) {
			ignore = false;
			return;
		}
		if (literalCollector.isCollecting()) {
			String prefix = (localname.equals(qname)) ? "" : qname.substring(0,
					qname.indexOf(':'));
			XMLEvent e = eventFactory.createEndElement(prefix, arg0, localname);
			literalCollector.handleEvent(e);
		}
		// If we aren't collecting an XML literal keep parsing
		if (!literalCollector.isCollectingXML())
			context = context.parent;
	}

	protected void writeCharacters(String value) {
		if (!ignore) {
			if (literalCollector.isCollecting()) {
				XMLEvent e = eventFactory.createCharacters(value);
				literalCollector.handleEvent(e);
			}
		}
	}

	/**
	 * Set the base uri of the DOM.
	 */
	public void setBase(String base) {
		this.context = new EvalContext(base);
		sink.setBase(context.getBase());
	}

	protected EvalContext parse(EvalContext context, StartElement element)
			throws XMLStreamException {
		boolean skipElement = false;
		String newSubject = null;
		String currentObject = null;
		List<String> forwardProperties = new LinkedList();
		List<String> backwardProperties = new LinkedList();
		String currentLanguage = context.language;

		if (settings.contains(Setting.OnePointOne)) {

			if (getAttributeByName(element, Constants.vocab) != null) {
				context.vocab = getAttributeByName(element, Constants.vocab)
						.getValue().trim();
			}

			if (getAttributeByName(element, Constants.prefix) != null) {
				parsePrefixes(getAttributeByName(element, Constants.prefix)
						.getValue(), context);
			}
		}

		// The xml / html namespace matching is a bit ropey. I wonder if the
		// html 5
		// parser has a setting for this?
		if (settings.contains(Setting.ManualNamespaces)) {
			if (getAttributeByName(element, Constants.xmllang) != null) {
				currentLanguage = getAttributeByName(element, Constants.xmllang)
						.getValue();
				if (currentLanguage.length() == 0)
					currentLanguage = null;
			} else if (getAttributeByName(element, Constants.lang) != null) {
				currentLanguage = getAttributeByName(element, Constants.lang)
						.getValue();
				if (currentLanguage.length() == 0)
					currentLanguage = null;
			}
		} else if (getAttributeByName(element, Constants.xmllangNS) != null) {
			currentLanguage = getAttributeByName(element, Constants.xmllangNS)
					.getValue();
			if (currentLanguage.length() == 0)
				currentLanguage = null;
		}

		if (Constants.base.equals(element.getName())
				&& getAttributeByName(element, Constants.href) != null) {
			context.setBase(getAttributeByName(element, Constants.href)
					.getValue());
			sink.setBase(context.getBase());
		}
		if (getAttributeByName(element, Constants.rev) == null
				&& getAttributeByName(element, Constants.rel) == null) {
			Attribute nSubj = findAttribute(element, Constants.about);
			if (nSubj != null) {
				newSubject = extractor.getURI(element, nSubj, context);
			}
			if (newSubject == null) {
				if (Constants.body.equals(element.getName())
						|| Constants.head.equals(element.getName())) {
					newSubject = context.base;
				} else if (getAttributeByName(element, Constants.typeof) != null) {
					newSubject = createBNode();
				} else {
					if (context.parentObject != null) {
						newSubject = context.parentObject;
					}
					if (getAttributeByName(element, Constants.property) == null) {
						skipElement = true;
					}
				}
			}
		} else {
			Attribute nSubj = findAttribute(element, Constants.about,
					Constants.src);
			if (nSubj != null) {
				newSubject = extractor.getURI(element, nSubj, context);
			}
			if (newSubject == null) {
				// if element is head or body assume about=""
				if (Constants.head.equals(element.getName())
						|| Constants.body.equals(element.getName())) {
					newSubject = context.base;
				} else if (getAttributeByName(element, Constants.typeof) != null) {
					newSubject = createBNode();
				} else if (context.parentObject != null) {
					newSubject = context.parentObject;
				}
			}
			Attribute cObj = findAttribute(element, Constants.resource,
					Constants.href);
			if (cObj != null) {
				currentObject = extractor.getURI(element, cObj, context);
			}
		}

		if (newSubject != null
				&& getAttributeByName(element, Constants.typeof) != null) {
			List<String> types = extractor.getURIs(element,
					getAttributeByName(element, Constants.typeof), context);
			for (String type : types) {
				emitTriples(newSubject, Constants.rdfType, type);
			}
		}


		if (currentObject != null) {
			if (getAttributeByName(element, Constants.rel) != null) {
				emitTriples(newSubject, extractor.getURIs(element,
						getAttributeByName(element, Constants.rel), context),
						currentObject);
			}
			if (getAttributeByName(element, Constants.rev) != null) {
				emitTriples(currentObject, extractor.getURIs(element,
						getAttributeByName(element, Constants.rev), context),
						newSubject);
			}
		} else {
			if (getAttributeByName(element, Constants.rel) != null) {
				forwardProperties.addAll(extractor.getURIs(element,
						getAttributeByName(element, Constants.rel), context));
			}
			if (getAttributeByName(element, Constants.rev) != null) {
				backwardProperties.addAll(extractor.getURIs(element,
						getAttributeByName(element, Constants.rev), context));
			}
			if (!forwardProperties.isEmpty() || !backwardProperties.isEmpty()) {
				// if predicate present
				currentObject = createBNode();
			}
		}

		// Getting literal values. Complicated!
		if (getAttributeByName(element, Constants.property) != null) {
			List<String> props = extractor.getURIs(element,
					getAttributeByName(element, Constants.property), context);
			String dt = getDatatype(element);
			if (getAttributeByName(element, Constants.content) != null) { // The
																			// easy
																			// bit
				String lex = getAttributeByName(element, Constants.content)
						.getValue();
				if (dt == null || dt.length() == 0) {
					emitTriplesPlainLiteral(newSubject, props, lex,
							currentLanguage);
				} else {
					emitTriplesDatatypeLiteral(newSubject, props, lex, dt);
				}
			} else {
				literalCollector
						.collect(newSubject, props, dt, currentLanguage);
			}
		}

		if (!skipElement && newSubject != null) {
			emitTriples(context.parentSubject, context.forwardProperties,
					newSubject);

			emitTriples(newSubject, context.backwardProperties,
					context.parentSubject);
		}

		EvalContext ec = new EvalContext(context);
		if (skipElement) {
			ec.language = currentLanguage;
		} else {
			if (newSubject != null) {
				ec.parentSubject = newSubject;
			} else {
				ec.parentSubject = context.parentSubject;
			}

			if (currentObject != null) {
				ec.parentObject = currentObject;
			} else if (newSubject != null) {
				ec.parentObject = newSubject;
			} else {
				ec.parentObject = context.parentSubject;
			}

			ec.language = currentLanguage;
			ec.forwardProperties = forwardProperties;
			ec.backwardProperties = backwardProperties;
		}
		return ec;
	}

	private void getNamespaces(Attributes attrs) {
		for (int i = 0; i < attrs.getLength(); i++) {
			String qname = attrs.getQName(i);
			String prefix = getPrefix(qname);
			if ("xmlns".equals(prefix)) {
				String pre = getLocal(prefix, qname);
				String uri = attrs.getValue(i);
				if (!settings.contains(Setting.ManualNamespaces)
						&& pre.contains("_"))
					continue; // not permitted
				context.setNamespaceURI(pre, uri);
				extractor.setNamespaceURI(pre, uri);
				sink.addPrefix(pre, uri);
			}
		}
	}

	private String getPrefix(String qname) {
		if (!qname.contains(":")) {
			return "";
		}
		return qname.substring(0, qname.indexOf(":"));
	}

	private String getLocal(String prefix, String qname) {
		if (prefix.length() == 0) {
			return qname;
		}
		return qname.substring(prefix.length() + 1);
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

	private Attribute findAttribute(StartElement element, QName... names) {
		for (QName aName : names) {
			Attribute a = getAttributeByName(element, aName);
			if (a != null) {
				return a;
			}
		}
		return null;
	}

	private void parsePrefixes(String value, EvalContext context) {
		String[] parts = value.split("\\s+");
		for (int i = 0; i < parts.length; i += 2) {
			String prefix = parts[i];
			if (i + 1 < parts.length && prefix.endsWith(":")) {
				String prefixFix = prefix.substring(0, prefix.length() - 1);
				context.setPrefix(prefixFix, parts[i + 1]);
				sink.addPrefix(prefixFix, parts[i + 1]);
			}
		}
	}

	private Attribute getAttributeByName(StartElement element, QName name) {
		if (name == null || element == null) {
			return null;
		}
		Iterator it = element.getAttributes();
		while (it.hasNext()) {
			Attribute at = (Attribute) it.next();
			if (Util.qNameEquals(at.getName(), name)) {
				return at;
			}
		}
		return null;
	}

	int bnodeId = 0;

	private String createBNode() // TODO probably broken? Can you write bnodes
									// in rdfa directly?
	{
		return "_:node" + (bnodeId++);
	}

	private String getDatatype(StartElement element) {
		Attribute de = getAttributeByName(element, Constants.datatype);
		if (de == null) {
			return null;
		}
		String dt = de.getValue();
		if (dt.length() == 0) {
			return dt;
		}
		return extractor.expandCURIE(element, dt, context);
	}
}
