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
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.xerces.dom.DocumentImpl;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfMetaDom;
import org.odftoolkit.odfdom.dom.OdfSchemaDocument;
import org.odftoolkit.odfdom.dom.OdfSettingsDom;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.odftoolkit.odfdom.dom.rdfa.BookmarkRDFMetadataExtractor;
import org.odftoolkit.odfdom.pkg.manifest.OdfManifestDom;
import org.odftoolkit.odfdom.pkg.rdfa.DOMRDFaParser;
import org.odftoolkit.odfdom.pkg.rdfa.JenaSink;
import org.odftoolkit.odfdom.pkg.rdfa.MultiContentHandler;
import org.odftoolkit.odfdom.pkg.rdfa.SAXRDFaParser;
import org.odftoolkit.odfdom.pkg.rdfa.Util;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The DOM representation of an XML file within the ODF document.
 */
public class OdfFileDom extends DocumentImpl implements NamespaceContext {

    private static final long serialVersionUID = 766167617530147000L;
    protected String mPackagePath;
    protected OdfPackageDocument mPackageDocument;
    protected OdfPackage mPackage;
    protected XPath mXPath;
    protected Map<String, String> mUriByPrefix;
    protected Map<String, String> mPrefixByUri;
    /**
     * Contains only the duplicate prefix. The primary hold by mPrefixByUri
     * still have to be added
     */
    protected Map<String, Set<String>> mDuplicatePrefixesByUri;
    /**
     * The cache of in content metadata: key: a Node in the dom ; value: the
     * Jena RDF model of triples of the Node
     */
    protected Map<Node, Model> inCententMetadataCache;

    protected JenaSink sink;

    /**
     * Creates the DOM representation of an XML file of an ODF document.
     *
     * @param packageDocument the document the XML files belongs to
     * @param packagePath the internal package path to the XML file
     */
    protected OdfFileDom(OdfPackageDocument packageDocument, String packagePath) {
        super(false);
        if (packageDocument != null && packagePath != null) {
            mPackageDocument = packageDocument;
            mPackage = packageDocument.getPackage();
            mPackagePath = packagePath;
            mUriByPrefix = new HashMap<String, String>();
            mPrefixByUri = new HashMap<String, String>();
            mDuplicatePrefixesByUri = new HashMap<String, Set<String>>();
            inCententMetadataCache = new IdentityHashMap<Node, Model>();
            try {
                initialize();
            } catch (SAXException | IOException | ParserConfigurationException ex) {
                Logger.getLogger(OdfFileDom.class.getName()).log(Level.SEVERE, null, ex);
            }
            // Register every DOM to OdfPackage,
            // so a package close might save this DOM (similar as OdfDocumentPackage)
            this.addDomToCache(mPackage, packagePath);
        } else {
            throw new IllegalArgumentException("Arguments are not allowed to be NULL for OdfFileDom constructor!");
        }
    }

    /**
     * Creates the DOM representation of an XML file of an Odf document.
     *
     * @param pkg the package the XML files belongs to
     * @param packagePath the internal package path to the XML file
     */
    protected OdfFileDom(OdfPackage pkg, String packagePath) {
        super(false);
        if (pkg != null && packagePath != null) {
            mPackageDocument = null;
            mPackage = pkg;
            mPackagePath = packagePath;
            mUriByPrefix = new HashMap<String, String>();
            mPrefixByUri = new HashMap<String, String>();
            mDuplicatePrefixesByUri = new HashMap<String, Set<String>>();
            inCententMetadataCache = new HashMap<Node, Model>();
            try {
                initialize();
            } catch (SAXException | IOException | ParserConfigurationException ex) {
                Logger.getLogger(OdfFileDom.class.getName()).log(Level.SEVERE, null, ex);
            }
            // Register every DOM to OdfPackage,
            // so a package close might save this DOM (similar as
            // OdfDocumentPackage)
            addDomToCache(mPackage, packagePath);
        } else {
            throw new IllegalArgumentException("Arguments are not allowed to be NULL for OdfFileDom constructor!");
        }
    }

    /**
     * Adds the document to the pool of open documents of the package. A
     * document of a certain path is opened only once to avoid data duplication.
     */
    private void addDomToCache(OdfPackage pkg, String internalPath) {
        pkg.cacheDom(this, internalPath);
    }

    static OdfFileDom newFileDom(OdfPackageDocument packageDocument, String packagePath) {
        OdfFileDom newFileDom = null;
        // before creating a new dom, make sure that there no DOM opened for this file already
        Document existingDom = packageDocument.getPackage().getCachedDom(packagePath);
        if (existingDom == null) {
            // ToDo: bug 264 - register OdfFileDom to this class
            if (packagePath.equals("content.xml") || packagePath.endsWith("/content.xml")) {
                newFileDom = new OdfContentDom((OdfSchemaDocument) packageDocument, packagePath);
            } else if (packagePath.equals("styles.xml") || packagePath.endsWith("/styles.xml")) {
                newFileDom = new OdfStylesDom((OdfSchemaDocument) packageDocument, packagePath);
            } else if (packagePath.equals("meta.xml") || packagePath.endsWith("/meta.xml")) {
                newFileDom = new OdfMetaDom((OdfSchemaDocument) packageDocument, packagePath);
            } else if (packagePath.equals("settings.xml") || packagePath.endsWith("/settings.xml")) {
                newFileDom = new OdfSettingsDom((OdfSchemaDocument) packageDocument, packagePath);
            } else if (packagePath.equals("META-INF/manifest.xml") || packagePath.endsWith("/META-INF/manifest.xml")) {
                newFileDom = new OdfManifestDom((OdfSchemaDocument) packageDocument, packagePath);
            } else {
                newFileDom = new OdfFileDom(packageDocument, packagePath);
            }
        } else {
            if (existingDom instanceof OdfFileDom) {
                newFileDom = (OdfFileDom) existingDom;
//ToDO: Issue 264 - Otherwise if NOT an OdfFileDom serialize old DOM AND CREATE A NEW ONE?!
// Or shall we always reference to the dom, than we can not inherit from Document? Pro/Con?s
//			}else{
//				// Create an OdfFileDOM from an existing DOM
//				newFileDom =
            }

        }
        return newFileDom;
    }

    public static OdfFileDom newFileDom(OdfPackage pkg, String packagePath) {
        OdfFileDom newFileDom = null;
        // before creating a new dom, make sure that there no DOM opened for this file already
        Document existingDom = pkg.getCachedDom(packagePath);
        if (existingDom == null) {
            if (packagePath.equals("META-INF/manifest.xml") || packagePath.endsWith("/META-INF/manifest.xml")) {
                newFileDom = new OdfManifestDom(pkg, packagePath);
            } else {
                newFileDom = new OdfFileDom(pkg, packagePath);
            }
        } else {
            if (existingDom instanceof OdfFileDom) {
                newFileDom = (OdfFileDom) existingDom;
//ToDO: Issue 264 - Otherwise if NOT an OdfFileDom serialize old DOM AND CREATE A NEW ONE?!
// Or shall we always reference to the dom, than we can not inherit from Document? Pro/Con?s
//			}else{
//				// Create an OdfFileDOM from an existing DOM
//				newFileDom =
            }

        }
        return newFileDom;
    }

    protected void initialize() throws SAXException, IOException, ParserConfigurationException {
        initialize(new OdfFileSaxHandler(this), this);
    }

    protected void initialize(DefaultHandler handler, OdfFileDom dom) throws SAXException, IOException, ParserConfigurationException {
        InputStream fileStream = null;
        try {
            fileStream = mPackage.getInputStream(mPackagePath);
            if (fileStream != null) {
                XMLReader xmlReader = mPackage.getXMLReader();
                String baseUri = Util.getRDFBaseUri(mPackage.getBaseURI(), mPackagePath);
                if(handler instanceof OdfFileSaxHandler){
                    OdfFileSaxHandler odfSaxHandler = ((OdfFileSaxHandler) handler);
                    sink = new JenaSink(this);
                    odfSaxHandler.setSink(sink);
                    SAXRDFaParser rdfa = SAXRDFaParser.createInstance(sink);
                    rdfa.setBase(baseUri);
                    // the file is parsed by ODF ContentHandler, and then RDFa ContentHandler
                    MultiContentHandler multi = new MultiContentHandler(odfSaxHandler, rdfa);
                    xmlReader.setContentHandler(multi);
                }else{
                    xmlReader.setContentHandler(handler);
                }
                InputSource xmlSource = new InputSource(fileStream);
                xmlReader.parse(xmlSource);
            }
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            Logger.getLogger(OdfFileDom.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            try {
                if (fileStream != null) {
                    fileStream.close();
                }
            } catch (IOException iex) {
                Logger.getLogger(OdfFileDom.class.getName()).log(Level.SEVERE, null, iex);
            }
        }
    }

    /**
     * Retrieves the <code>OdfPackageDocument</code> of the XML file. A package
     * document is usually represented as a directory with a media type.
     *
     * @return The document holding the XML file.
     *
     */
    public OdfPackageDocument getDocument() {
        return mPackageDocument;
    }

    /**
     * Retrieves the <code>String</code> of Package Path
     *
     * @return The path of the XML file relative to the package root
     */
    public String getPackagePath() {
        return mPackagePath;
    }

    /**
     * Retrieves the ODF root element.
     *
     * @return The <code>OdfElement</code> being the root of the document.
     */
    public OdfElement getRootElement() {
        return (OdfElement) getDocumentElement();
    }

    /**
     * Create ODF element with namespace uri and qname
     *
     * @param name The element name
     *
     */
    @Override
    public OdfElement createElement(String name) throws DOMException {
        return createElementNS(OdfName.newName(name));
    }

    /**
     * Create ODF element with namespace uri and qname
     *
     * @param nsuri The namespace uri
     * @param qname The element qname
     *
     */
    @Override
    public OdfElement createElementNS(String nsuri, String qname) throws DOMException {
        return createElementNS(OdfName.newName(nsuri, qname));
    }

    /**
     * Create ODF element with ODF name
     *
     * @param name The <code>OdfName</code>
     * @return The <code>OdfElement</code>
     * @throws DOMException
     */
    public OdfElement createElementNS(OdfName name) throws DOMException {
        return OdfXMLFactory.newOdfElement(this, name);
    }

    /**
     * Create the ODF attribute with its name
     *
     * @param name the attribute qname
     * @return The <code>OdfAttribute</code>
     * @throws DOMException
     */
    @Override
    public OdfAttribute createAttribute(String name) throws DOMException {
        return createAttributeNS(OdfName.newName(name));
    }

    /**
     * Create the ODF attribute with namespace uri and qname
     *
     * @param nsuri The namespace uri
     * @param qname the attribute qname
     * @return The <code>OdfAttribute</code>
     * @throws DOMException
     */
    @Override
    public OdfAttribute createAttributeNS(String nsuri, String qname) throws DOMException {
        return createAttributeNS(OdfName.newName(nsuri, qname));
    }

    /**
     * Create the ODF attribute with ODF name
     *
     * @param name The <code>OdfName</code>
     * @return The <code>OdfAttribute</code>
     * @throws DOMException
     */
    public OdfAttribute createAttributeNS(OdfName name) throws DOMException {
        return OdfXMLFactory.newOdfAttribute(this, name);
    }

    @SuppressWarnings("unchecked")
    /**
     * @return an ODF element of the given subclass, defined by its ELEMENT_NAME
     * field. Returns null for none ODF elements, as they are of type
     * OdfAlienElement without such field, as their XML name varies.
     */
    public <T extends OdfElement> T newOdfElement(Class<T> clazz) {
//        return (T) OdfXMLFactory.getNodeFromClass(this, clazz);
        try {
            Field fname = clazz.getField("ELEMENT_NAME");
            OdfName name = (OdfName) fname.get(null);
            return (T) createElementNS(name);
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            }
            return null;
        }
    }

    @Override
    public String toString() {
        return ((OdfElement) this.getDocumentElement()).toString();
    }

    // JDK Namespace handling
    /**
     * Create an XPath instance to select one or more nodes from an ODF
     * document. Therefore the namespace context is set to the OdfNamespace
     *
     * @return an XPath instance with namespace context set to include the
     * standard ODFDOM prefixes.
     */
    public XPath getXPath() {
        if (mXPath == null) {
            mXPath = XPathFactory.newInstance().newXPath();
        }
        return mXPath;
    }

    /**
     * <p>
     * Get Namespace URI bound to a prefix in the current scope (the XML
     * file).</p>
     *
     * <p>
     * When requesting a Namespace URI by prefix, the following table describes
     * the returned Namespace URI value for all possible prefix values:</p>
     *
     * <table border="2" rules="all" cellpadding="4">
     * <thead>
     * <tr>
     * <td align="center" colspan="2">
     * <code>getNamespaceURI(prefix)</code> return value for specified prefixes
     * </td>
     * </tr>
     * <tr>
     * <td>prefix parameter</td>
     * <td>Namespace URI return value</td>
     * </tr>
     * </thead>
     * <tbody>
     * <tr>
     * <td><code>DEFAULT_NS_PREFIX</code> ("")</td>
     * <td>default Namespace URI in the current scope or      <code>{@link
     *         javax.xml.XMLConstants#NULL_NS_URI XMLConstants.NULL_NS_URI("")}
     * </code> when there is no default Namespace URI in the current scope</td>
     * </tr>
     * <tr>
     * <td>bound prefix</td>
     * <td>Namespace URI bound to prefix in current scope</td>
     * </tr>
     * <tr>
     * <td>unbound prefix</td>
     * <td>
     * <code>{@link
     *         javax.xml.XMLConstants#NULL_NS_URI XMLConstants.NULL_NS_URI("")}
     * </code>
     * </td>
     * </tr>
     * <tr>
     * <td><code>XMLConstants.XML_NS_PREFIX</code> ("xml")</td>
     * <td><code>XMLConstants.XML_NS_URI</code>
     * ("http://www.w3.org/XML/1998/namespace")</td>
     * </tr>
     * <tr>
     * <td><code>XMLConstants.XMLNS_ATTRIBUTE</code> ("xmlns")</td>
     * <td><code>XMLConstants.XMLNS_ATTRIBUTE_NS_URI</code>
     * ("http://www.w3.org/2000/xmlns/")</td>
     * </tr>
     * <tr>
     * <td><code>null</code></td>
     * <td><code>IllegalArgumentException</code> is thrown</td>
     * </tr>
     * </tbody>
     * </table>
     *
     * @param prefix prefix to look up
     *
     * @return Namespace URI bound to prefix in the current scope
     *
     * @throws IllegalArgumentException When <code>prefix</code> is
     * <code>null</code>
     */
    public String getNamespaceURI(String prefix) {
        String nsURI = null;
        nsURI = mUriByPrefix.get(prefix);
        if (nsURI == null) {
            // look in Duplicate URI prefixes
            Set<String> urisWithDuplicatePrefixes = this.mDuplicatePrefixesByUri.keySet();
            for (String aURI : urisWithDuplicatePrefixes) {
                Set<String> prefixes = this.mDuplicatePrefixesByUri.get(aURI);
                // check if requested prefix exists in hashset
                if (prefixes.contains(prefix)) {
                    nsURI = aURI;
                    break;
                }
            }
        }
        // there is a possibility it still may be null - so we check
        if (nsURI == null) {
            nsURI = XMLConstants.NULL_NS_URI;
        }
        return nsURI;
    }

    /**
     * <p>
     * Get prefix bound to Namespace URI in the current scope (the XML
     * file).</p>
     * <p>
     * Multiple prefixes bound to Namespace URI will be normalized to the first
     * prefix defined.</p>
     *
     * <p>
     * When requesting a prefix by Namespace URI, the following table describes
     * the returned prefix value for all Namespace URI values:</p>
     *
     * <table border="2" rules="all" cellpadding="4">
     * <thead>
     * <tr>
     * <th align="center" colspan="2">
     * <code>getPrefix(namespaceURI)</code> return value for specified Namespace
     * URIs
     * </th>
     * </tr>
     * <tr>
     * <th>Namespace URI parameter</th>
     * <th>prefix value returned</th>
     * </tr>
     * </thead>
     * <tbody>
     * <tr>
     * <td>&lt;default Namespace URI&gt;</td>
     * <td><code>XMLConstants.DEFAULT_NS_PREFIX</code> ("")
     * </td>
     * </tr>
     * <tr>
     * <td>bound Namespace URI</td>
     * <td>prefix bound to Namespace URI in the current scope, if multiple
     * prefixes are bound to the Namespace URI in the current scope, a single
     * arbitrary prefix, whose choice is implementation dependent, is
     * returned</td>
     * </tr>
     * <tr>
     * <td>unbound Namespace URI</td>
     * <td><code>null</code></td>
     * </tr>
     * <tr>
     * <td><code>XMLConstants.XML_NS_URI</code>
     * ("http://www.w3.org/XML/1998/namespace")</td>
     * <td><code>XMLConstants.XML_NS_PREFIX</code> ("xml")</td>
     * </tr>
     * <tr>
     * <td><code>XMLConstants.XMLNS_ATTRIBUTE_NS_URI</code>
     * ("http://www.w3.org/2000/xmlns/")</td>
     * <td><code>XMLConstants.XMLNS_ATTRIBUTE</code> ("xmlns")</td>
     * </tr>
     * <tr>
     * <td><code>null</code></td>
     * <td><code>IllegalArgumentException</code> is thrown</td>
     * </tr>
     * </tbody>
     * </table>
     *
     * @param namespaceURI URI of Namespace to lookup
     *
     * @return prefix bound to Namespace URI in current context
     *
     * @throws IllegalArgumentException When <code>namespaceURI</code> is
     * <code>null</code>
     */
    public String getPrefix(String namespaceURI) {
        return mPrefixByUri.get(namespaceURI);
    }

    /**
     * <p>
     * Get all prefixes bound to a Namespace URI in the current scope. (the XML
     * file)</p>
     * <p>
     * NOTE: Multiple prefixes bound to a similar Namespace URI will be
     * normalized to the first prefix defined. Still the namespace attributes
     * exist in the XML as inner value prefixes might be used.</p>
     *
     * <p>
     * <strong>The <code>Iterator</code> is
     * <em>not</em> modifiable. e.g. the <code>remove()</code> method will throw
     * <code>UnsupportedOperationException</code>.</strong></p>
     *
     * <p>
     * When requesting prefixes by Namespace URI, the following table describes
     * the returned prefixes value for all Namespace URI values:</p>
     *
     * <table border="2" rules="all" cellpadding="4">
     * <thead>
     * <tr>
     * <th align="center" colspan="2"><code>
     *         getPrefixes(namespaceURI)</code> return value for specified Namespace
     * URIs</th>
     * </tr>
     * <tr>
     * <th>Namespace URI parameter</th>
     * <th>prefixes value returned</th>
     * </tr>
     * </thead>
     * <tbody>
     * <tr>
     * <td>bound Namespace URI, including the &lt;default Namespace URI&gt;</td>
     * <td>
     * <code>Iterator</code> over prefixes bound to Namespace URI in the current
     * scope in an arbitrary,
     * <strong>implementation dependent</strong>, order
     * </td>
     * </tr>
     * <tr>
     * <td>unbound Namespace URI</td>
     * <td>empty <code>Iterator</code></td>
     * </tr>
     * <tr>
     * <td><code>XMLConstants.XML_NS_URI</code>
     * ("http://www.w3.org/XML/1998/namespace")</td>
     * <td><code>Iterator</code> with one element set to
     * <code>XMLConstants.XML_NS_PREFIX</code> ("xml")</td>
     * </tr>
     * <tr>
     * <td><code>XMLConstants.XMLNS_ATTRIBUTE_NS_URI</code>
     * ("http://www.w3.org/2000/xmlns/")</td>
     * <td><code>Iterator</code> with one element set to
     * <code>XMLConstants.XMLNS_ATTRIBUTE</code> ("xmlns")</td>
     * </tr>
     * <tr>
     * <td><code>null</code></td>
     * <td><code>IllegalArgumentException</code> is thrown</td>
     * </tr>
     * </tbody>
     * </table>
     *
     * @param namespaceURI URI of Namespace to lookup
     *
     * @return <code>Iterator</code> for all prefixes bound to the Namespace URI
     * in the current scope
     *
     * @throws IllegalArgumentException When <code>namespaceURI</code> is
     * <code>null</code>
     */
    public Iterator<String> getPrefixes(String namespaceURI) {
        Set<String> prefixes = mDuplicatePrefixesByUri.get(namespaceURI);
        if (prefixes == null) {
            prefixes = new HashSet<String>();
        }
        String givenPrefix = mPrefixByUri.get(namespaceURI);
        if (givenPrefix != null) {
            prefixes.add(givenPrefix);
        }
        return prefixes.iterator();
    }

    /**
     * @return a map of namespaces, where the URI is the key and the prefix is
     * the value
     */
    Map<String, String> getMapNamespacePrefixByUri() {
        return mPrefixByUri;
    }

    /**
     * Adds a new Namespace to the DOM. Making the prefix usable with JDK
     * <code>XPath</code>. All namespace attributes will be written to the root
     * element during later serialization of the DOM by the
     * <code>OdfPackage</code>.
     *
     * @param prefix of the namespace to be set to this DOM
     * @param uri of the namespace to be set to this DOM
     * @return the namespace that was set. If an URI was registered before to
     * the DOM, the previous prefix will be taken. In case of a given prefix
     * that was already registered, but related to a new URI, the prefix will be
     * adapted. The new prefix receives the suffix '__' plus integer, e.g. "__1"
     * for the first duplicate and "__2" for the second.
     */
    public OdfNamespace setNamespace(String prefix, String uri) {
        //collision detection, when a new prefix/URI pair exists
        OdfNamespace newNamespace = null;
        //Scenario a) the URI already registered, use existing prefix
        // but save all others for the getPrefixes function. There might be still some
        // in attribute values using prefixes, that were not exchanged.
        String existingPrefix = mPrefixByUri.get(uri);
        if (existingPrefix != null) {
            //Use the existing prefix of the used URL, neglect the given
            newNamespace = OdfNamespace.newNamespace(existingPrefix, uri);

            //Add the new prefix to the duplicate prefix map for getPrefixes(String uri)
            Set<String> prefixes = mDuplicatePrefixesByUri.get(uri);
            if (prefixes == null) {
                prefixes = new HashSet<String>();
                mDuplicatePrefixesByUri.put(uri, prefixes);
            }
            prefixes.add(prefix);
        } else {
            //Scenario b) the prefix already exists and the URI does not exist
            String existingURI = mUriByPrefix.get(prefix);
            if (existingURI != null && !existingURI.equals(uri)) {
                //Change the prefix appending "__" plus counter.
                int i = 1;
                do {
                    int suffixStart = prefix.lastIndexOf("__");
                    if (suffixStart != -1) {
                        prefix = prefix.substring(0, suffixStart);
                    }
                    //users have to take care for their attribute values using namespace prefixes.
                    prefix = prefix + "__" + i;
                    i++;
                    existingURI = mUriByPrefix.get(prefix);
                } while (existingURI != null && !existingURI.equals(uri));
            }
            newNamespace = OdfNamespace.newNamespace(prefix, uri);
            mPrefixByUri.put(uri, prefix);
            mUriByPrefix.put(prefix, uri);
        }
        // if the file Dom is already associated to parsed XML add the new namespace to the root element
        Element root = getRootElement();
        if (root != null) {
            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, uri);
        }
        return newNamespace;
    }

    /**
     * Adds a new Namespace to the DOM. Making the prefix usable with JDK
     * <code>XPath</code>. All namespace attributes will be written to the root
     * element during later serialization of the DOM by the
     * <code>OdfPackage</code>.
     *
     * @param name the namespace to be set
     * @return the namespace that was set. If an URI was registered before to
     * the DOM, the previous prefix will be taken. In case of a given prefix
     * that was already registered, but related to a new URI, the prefix will be
     * adapted. The new prefix receives the suffix '__' plus integer, e.g. "__1"
     * for the first duplicate and "__2" for the second.
     */
    public OdfNamespace setNamespace(NamespaceName name) {
        return setNamespace(name.getPrefix(), name.getUri());
    }

    /**
     * Get in-content metadata cache model
     *
     * @return in-content metadata cache model
     */
    public Map<Node, Model> getInContentMetadataCache() {
        return this.inCententMetadataCache;
    }

    /**
     * Update the in content metadata of the node. It should be called whenever
     * the xhtml:xxx attributes values of the node are changed.
     *
     * @param the node, whose in content metadata will be updated
     */
    public void updateInContentMetadataCache(Node node) {
        this.getInContentMetadataCache().remove(node);
        DOMRDFaParser parser = DOMRDFaParser.createInstance(this.sink);
        String baseUri = Util.getRDFBaseUri(mPackage.getBaseURI(), mPackagePath);
        parser.setBase(baseUri);
        parser.parse(node);
    }

    /**
     * @return the RDF metadata of all the bookmarks within the dom
     */
    public Model getBookmarkRDFMetadata() {
        return BookmarkRDFMetadataExtractor.newBookmarkTextExtractor().getBookmarkRDFMetadata(this);
    }

    /**
     * The end users needn't to care of this method, which is used by
     * BookmarkRDFMetadataExtractor
     *
     * @return the JenaSink
     */
    public JenaSink getSink() {
        return sink;
    }

    /**
     * @return counter for ids that are not allowed to be saved (otherwise it is
     * not guaranteed that this id is unique)
     */
    public String getNextMarkupId() {
        return getDocument().getPackage().getNextMarkupId();
    }
}
