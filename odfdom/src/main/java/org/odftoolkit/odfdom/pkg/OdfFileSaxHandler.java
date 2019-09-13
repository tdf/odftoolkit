/** **********************************************************************
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
 *********************************************************************** */
package org.odftoolkit.odfdom.pkg;

import java.io.IOException;
import java.util.Stack;
import org.odftoolkit.odfdom.pkg.rdfa.JenaSink;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class OdfFileSaxHandler extends DefaultHandler {

    private static final String EMPTY_STRING = "";
    // the empty XML file to which nodes will be added
    private OdfFileDom mFileDom;
    // the context node
    protected Node mCurrentNode;        // a stack of sub handlers. handlers will be pushed on the stack whenever
    // they are required and must pop themselves from the stack when done
    private Stack<ContentHandler> mHandlerStack = new Stack<ContentHandler>();
    private StringBuilder mCharsForTextNode = new StringBuilder();
    private JenaSink sink;

    public OdfFileSaxHandler(Node rootNode) throws SAXException {
        if (rootNode instanceof OdfFileDom) {
            mFileDom = (OdfFileDom) rootNode;
        } else {
            mFileDom = (OdfFileDom) rootNode.getOwnerDocument();
        }
        mCurrentNode = rootNode;
    }

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        flushTextNode();
        // pop to the parent node
        mCurrentNode = mCurrentNode.getParentNode();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        flushTextNode();
        // if there is a specilized handler on the stack, dispatch the event
        Element element = null;
        if (uri.equals(EMPTY_STRING) || qName.equals(EMPTY_STRING)) {
            element = mFileDom.createElement(localName);
        } else {
            element = mFileDom.createElementNS(uri, qName);
        }
        String attrQname = null;
        String attrURL = null;
        OdfAttribute attr = null;
        for (int i = 0; i < attributes.getLength(); i++) {
            attrURL = attributes.getURI(i);
            attrQname = attributes.getQName(i);
            // if no namespace exists
            if (attrURL.equals(EMPTY_STRING) || attrQname.equals(EMPTY_STRING)) {
                // create attribute without prefix
                attr = mFileDom.createAttribute(attributes.getLocalName(i));
            } else {
                if (attrQname.startsWith("xmlns:")) {
                    // in case of xmlns prefix we have to create a new OdfNamespace
                    OdfNamespace namespace = mFileDom.setNamespace(attributes.getLocalName(i), attributes.getValue(i));
                    // if the file Dom is already associated to parsed XML add the new namespace to the root element
                    Element root = mFileDom.getRootElement();
                    if (root == null) {
                        root = element;
                    }
                    root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + namespace.getPrefix(), namespace.getUri());
                } else if (attrQname.equals("xmlns")) {
                    continue; // skip the name space default attribute
                }
                // create all attributes, even namespace attributes
                attr = mFileDom.createAttributeNS(attrURL, attrQname);
            }

            // namespace attributes will not be created and return null
            if (attr != null) {
                element.setAttributeNodeNS(attr);
                // don't exit because of invalid attribute values
                try {
                    // set Value in the attribute to allow validation in the attribute
                    attr.setValue(attributes.getValue(i));
                } // if we detect an attribute with invalid value: remove attribute node
                catch (IllegalArgumentException e) {
                    element.removeAttributeNode(attr);
                }
            }
        }
        // add the new element as a child of the current context node
        mCurrentNode.appendChild(element);
        // push the new element as the context node...
        mCurrentNode = element;
        if (!localName.equals("bookmark-start")) {
            setContextNode(mCurrentNode);
        }
    }

    /**
     * http://xerces.apache.org/xerces2-j/faq-sax.html#faq-2 : SAX may deliver
     * contiguous text as multiple calls to characters, for reasons having to do
     * with parser efficiency and input buffering. It is the programmer's
     * responsibility to deal with that appropriately, e.g. by accumulating text
     * until the next non-characters event.
     */
    protected void flushTextNode() {
        if (mCharsForTextNode.length() > 0) {
            Text text = mFileDom.createTextNode(mCharsForTextNode.toString());
            mCurrentNode.appendChild(text);
            mCharsForTextNode.setLength(0);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (!mHandlerStack.empty()) {
            mHandlerStack.peek().characters(ch, start, length);
        } else {
            mCharsForTextNode.append(ch, start, length);
        }
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
        return super.resolveEntity(publicId, systemId);
    }

    /**
     * Expose the current node to JenaSink to for caching the parsed RDF
     * triples.
     *
     * @return
     */
    protected void setContextNode(Node node) {
        if (this.sink != null) {
            sink.setContextNode(node);
        }
    }

    /**
     * Set the JenaSink object.
     *
     * @param sink
     */
    public void setSink(JenaSink sink) {
        this.sink = sink;
    }
}
