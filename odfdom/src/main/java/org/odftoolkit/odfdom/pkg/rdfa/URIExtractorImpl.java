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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import net.rootdev.javardfa.Constants;
import net.rootdev.javardfa.Resolver;
import net.rootdev.javardfa.Setting;
import org.apache.commons.validator.routines.UrlValidator;

/**
 * URIExtractorImpl modified from net.rootdev.javardfa.uri.URIExtractor
 */
class URIExtractorImpl implements URIExtractor {
	private Set<Setting> settings;
	private final Resolver resolver;
	private Map<String, String> xmlnsMap = Collections.EMPTY_MAP;
	private boolean isForSAX;
	private UrlValidator urlValidator;

	public URIExtractorImpl(Resolver resolver, boolean isForSAX) {
		this.resolver = resolver;
		this.isForSAX = isForSAX;
		this.urlValidator = new UrlValidator();
	}

	public void setForSAX(boolean isForSAX) {
		this.isForSAX = isForSAX;
	}

	public void setSettings(Set<Setting> settings) {
		this.settings = settings;
	}

	public String getURI(StartElement element, Attribute attr,
			EvalContext context) {
		QName attrName = attr.getName();
		if (Util.qNameEquals(attrName, Constants.about)) // Safe CURIE or URI
		{
			return expandSafeCURIE(element, attr.getValue(), context);
		}
		if (Util.qNameEquals(attrName, Constants.datatype)) // A CURIE
		{
			return expandCURIE(element, attr.getValue(), context);
		}
		throw new RuntimeException("Unexpected attribute: " + attr);
	}

	private boolean isValidURI(String uri){
		return this.urlValidator.isValid(uri);
	}

	public List<String> getURIs(StartElement element, Attribute attr,
			EvalContext context) {

		List<String> uris = new LinkedList<String>();

		String[] curies = attr.getValue().split("\\s+");
		boolean permitReserved = Util
				.qNameEquals(Constants.rel, attr.getName())
				|| Util.qNameEquals(Constants.rev, attr.getName());
		for (String curie : curies) {
			if (Constants.SpecialRels.contains(curie.toLowerCase())) {
				if (permitReserved)
					uris.add("http://www.w3.org/1999/xhtml/vocab#"
							+ curie.toLowerCase());
			} else {
				String uri = expandCURIE(element, curie, context);
				if (uri != null) {
					uris.add(uri);
				}
			}
		}
		return uris;
	}

	public String expandCURIE(StartElement element, String value,
			EvalContext context) {

		if (value.startsWith("_:")) {
			if (!settings.contains(Setting.ManualNamespaces))
				return value;
			if (element.getNamespaceURI("_") == null)
				return value;
		}
		if (settings.contains(Setting.FormMode) && // variable
				value.startsWith("?")) {
			return value;
		}
		int offset = value.indexOf(":") + 1;
		if (offset == 0) {
			return null;
		}
		String prefix = value.substring(0, offset - 1);


		// Apparently these are not allowed to expand
		if ("xml".equals(prefix) || "xmlns".equals(prefix))
			return null;

		String namespaceURI = null;
		if (prefix.length() == 0) {
			namespaceURI = "http://www.w3.org/1999/xhtml/vocab#";
		} else {
			namespaceURI = element.getNamespaceURI(prefix);
			if (isForSAX) {
				if (namespaceURI != null) {
					if (xmlnsMap == Collections.EMPTY_MAP)
						xmlnsMap = new HashMap<String, String>();
					xmlnsMap.put(prefix, namespaceURI);
				}
			} else {
				if (namespaceURI == null) {
					namespaceURI = xmlnsMap.get(prefix);
				}
			}
		}
		if (namespaceURI == null) {
			return null;
			// throw new RuntimeException("Unknown prefix: " + prefix);
		}

		return namespaceURI + value.substring(offset);
	}

    @Override
	public String expandSafeCURIE(StartElement element, String value,
			EvalContext context) {
		if (value.startsWith("[") && value.endsWith("]")) {
			return expandCURIE(element, value.substring(1, value.length() - 1),
					context);
		} else {
			if (value.length() == 0) {
				return context.getBase();
			}

			if (settings.contains(Setting.FormMode) && value.startsWith("?")) {
				return value;
			}

			// earlier "return resolver.resolve(context.getBase(), value);"
            // now has JENA problem with '/' slash as base URL
            // </> Code: 57/REQUIRED_COMPONENT_MISSING in SCHEME: A component that is required by the scheme is missing.
            return value;
		}
	}

	public String resolveURI(String uri, EvalContext context) {
		return resolver.resolve(context.getBase(), uri);
	}

	public String getNamespaceURI(String prefix) {
		if (xmlnsMap.containsKey(prefix)) {
			return xmlnsMap.get(prefix);
		} else {
			return null;
		}
	}

	public void setNamespaceURI(String prefix, String namespaceURI){
		if (xmlnsMap == Collections.EMPTY_MAP)
			xmlnsMap = new HashMap<String, String>();
		xmlnsMap.put(prefix, namespaceURI);
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
