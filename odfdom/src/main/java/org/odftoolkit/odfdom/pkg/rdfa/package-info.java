/*
 * Copyright 2026 The Document Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Support for ODF in-content metadata, i.e. RDF statements attached to elements of the <code>
 * content.xml</code> and <code>styles.xml</code> files.
 *
 * <h2>In-content metadata in ODF</h2>
 *
 * <p>Besides RDF/XML files registered in the package's <code>manifest.rdf</code> (ODF 1.2 Part 3,
 * "Metadata Manifest"), ODF allows RDF metadata within the document content (ODF 1.2 Part 1,
 * §4.2.1 "In Content Metadata (RDFa)"). It borrows four attributes and their data types from
 * RDFa 1.0: <code>xhtml:about</code>, <code>xhtml:property</code>, <code>xhtml:datatype</code>
 * and <code>xhtml:content</code>. Unlike RDFa in XHTML, the ODF schema only allows them on a few
 * elements and always demands <code>xhtml:about</code> and <code>xhtml:property</code> together.
 * Other RDFa attributes like <code>rel</code>, <code>rev</code>, <code>typeof</code> or <code>
 * resource</code> and the RDFa inheritance of subjects between nested elements do not exist in
 * ODF. For example, the paragraph
 *
 * <pre>
 * &lt;text:p xhtml:about="[dbpedia:J._K._Rowling]" xhtml:property="dbpprop:birthDate"
 *     xhtml:datatype="xsd:date" xhtml:content="1965-07-31"&gt;July 31st, 1965&lt;/text:p&gt;
 * </pre>
 *
 * states the RDF statement <code>&lt;http://dbpedia.org/page/J._K._Rowling&gt;
 * &lt;http://dbpedia.org/property/birthDate&gt; "1965-07-31"^^xsd:date</code>, given the
 * corresponding namespace declarations. The detailed rules are documented at {@link
 * org.odftoolkit.odfdom.pkg.rdfa.InContentMetadata}.
 *
 * <h2>Two ways of retrieving in-content metadata</h2>
 *
 * <ol>
 *   <li><b>From the XML files by GRDDL</b>: {@link
 *       org.odftoolkit.odfdom.dom.OdfSchemaDocument#getInContentMetadata()} and {@link
 *       org.odftoolkit.odfdom.dom.OdfSchemaDocument#getRDFMetadata()} transform the XML files
 *       with the XSLT stylesheet <code>grddl/odf2rdf.xsl</code> into RDF/XML, which Jena parses.
 *       This way does not use this package, except for {@link
 *       org.odftoolkit.odfdom.pkg.rdfa.Util}, and reflects the content as stored in the package.
 *   <li><b>From the DOM</b>: {@link
 *       org.odftoolkit.odfdom.dom.OdfSchemaDocument#getInContentMetadataFromCache()} merges the
 *       per element cache of each XML file, see {@link
 *       org.odftoolkit.odfdom.pkg.OdfFileDom#getInContentMetadataCache()}. This way reflects the
 *       current state of the DOM, including changes not yet saved. The metadata of bookmarks is
 *       available by {@link org.odftoolkit.odfdom.pkg.OdfFileDom#getBookmarkRDFMetadata()} and
 *       {@link org.odftoolkit.odfdom.dom.rdfa.BookmarkRDFMetadataExtractor}.
 * </ol>
 *
 * <h2>Life cycle of the DOM cache</h2>
 *
 * <p>The cache is created, when it is first requested, by a single traversal of the DOM with
 * {@link org.odftoolkit.odfdom.pkg.rdfa.InContentMetadata#collect(org.w3c.dom.Node,
 * java.util.Map)}. Loading a document therefore costs nothing for users not interested in RDF.
 * Afterwards, the generated classes of the six elements allowing in-content metadata keep the
 * cache up to date: they call {@link
 * org.odftoolkit.odfdom.pkg.OdfFileDom#updateInContentMetadataCache(org.w3c.dom.Node)} when
 * their text content is replaced or when they are inserted into the DOM, and remove their entry
 * when they are removed from the DOM. Other changes, like setting an <code>xhtml:*</code>
 * attribute or changing the text of a descendant element, require an explicit call of {@link
 * org.odftoolkit.odfdom.pkg.OdfFileDom#updateInContentMetadataCache(org.w3c.dom.Node)}.
 *
 * <h2>History</h2>
 *
 * <p>Up to ODFDOM 0.13.0, the DOM cache was filled by a modified copy of the general purpose RDFa
 * 1.0 parser <code>net.rootdev:java-rdfa:1.0.0-BETA1</code>, which ran as a second SAX handler
 * while loading every XML file. That library is no longer maintained and depends on the <code>
 * jena-iri</code> library, which Apache Jena 6 no longer provides. As ODF only uses the small,
 * self-contained subset of RDFa described above, it is now implemented by {@link
 * org.odftoolkit.odfdom.pkg.rdfa.InContentMetadata} directly. The following public classes of
 * this package, which were only used internally for the RDFa parsing, have been removed: <code>
 * DOMAttributes</code>, <code>DOMRDFaParser</code>, <code>JenaSink</code>, <code>
 * MultiContentHandler</code>, <code>SAXRDFaParser</code> and <code>URIExtractor</code>, together
 * with <code>OdfFileDom.getSink()</code> and <code>OdfFileSaxHandler.setSink(JenaSink)</code>.
 * The API returning Jena models is unchanged.
 */
package org.odftoolkit.odfdom.pkg.rdfa;
