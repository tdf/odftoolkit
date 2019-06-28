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

import org.w3c.dom.NamedNodeMap;
import org.xml.sax.Attributes;

/**
 * Simple wrapper class for NamedNodeMap as Attributes
 */
public class DOMAttributes implements Attributes{

	private NamedNodeMap attributes;

	/**
	 * Class constructor
	 *
	 * @param attributes
	 */
	public DOMAttributes(NamedNodeMap attributes) {
		this.attributes = attributes;
	}


	public int getLength() {
		return attributes.getLength();
	}

	public String getURI(int index) {
		return attributes.item(index).getNamespaceURI();
	}

	public String getLocalName(int index) {
		return attributes.item(index).getLocalName();
	}

	public String getQName(int index) {
		return attributes.item(index).getNodeName();
	}

	public String getType(int index) {
		throw new RuntimeException("DOMAttributes.getType() is not supported");
	}

	public String getValue(int index) {
		return attributes.item(index).getNodeValue();
	}

	public int getIndex(String uri, String localName) {
		throw new RuntimeException("DOMAttributes.getIndex(String uri, String localName) is not supported");
	}

	public int getIndex(String qName) {
		throw new RuntimeException("DOMAttributes.getIndex(String qName) is not supported");
	}

	public String getType(String uri, String localName) {
		throw new RuntimeException("DOMAttributes.getType(String uri, String localName) is not supported");
	}

	public String getType(String qName) {
		throw new RuntimeException("DOMAttributes.getType(String qName) is not supported");
	}

	public String getValue(String uri, String localName) {
		throw new RuntimeException("DOMAttributes.getValue(String uri, String localName) is not supported");
	}

	public String getValue(String qName) {
		throw new RuntimeException("DOMAttributes.getValue(String qName) is not supported");
	}

}
