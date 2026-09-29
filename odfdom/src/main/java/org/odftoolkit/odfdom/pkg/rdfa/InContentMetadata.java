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
package org.odftoolkit.odfdom.pkg.rdfa;

import java.util.Map;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.odftoolkit.odfdom.dom.element.text.TextBookmarkStartElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Reads the RDF statements that ODF in-content metadata attributes state about an element.
 *
 * <p>ODF allows RDF metadata on the elements <code>&lt;text:p&gt;</code>, <code>&lt;text:h&gt;
 * </code>, <code>&lt;text:meta&gt;</code>, <code>&lt;text:bookmark-start&gt;</code>, <code>
 * &lt;table:table-cell&gt;</code> and <code>&lt;table:covered-table-cell&gt;</code> using four
 * attributes of the XHTML namespace (ODF 1.2 Part 1, §4.2.1 "In Content Metadata (RDFa)" and
 * §19.905 to §19.908). The ODF schema only permits <code>xhtml:about</code> and <code>
 * xhtml:property</code> together, so every such element is self-contained and states one RDF
 * statement per predicate:
 *
 * <table border="1">
 *   <caption>Mapping of ODF attributes to an RDF statement</caption>
 *   <tr><th>RDF</th><th>ODF source</th><th>Interpretation</th></tr>
 *   <tr>
 *     <td>subject</td>
 *     <td><code>xhtml:about</code> (URIorSafeCURIE)</td>
 *     <td><code>[prefix:reference]</code> is a safe CURIE, expanded as described below. <code>
 *       [_:label]</code> or <code>_:label</code> is a blank node, identical for equal labels in the
 *       same ODF document. An empty value refers to the ODF document itself, the {@link
 *       OdfFileDom#getRDFBaseUri() RDF base IRI}. Any other value is taken as an IRI, unchanged,
 *       even when relative.</td>
 *   </tr>
 *   <tr>
 *     <td>predicates</td>
 *     <td><code>xhtml:property</code> (CURIEs)</td>
 *     <td>A whitespace separated list of CURIEs, each one resulting in a separate statement.</td>
 *   </tr>
 *   <tr>
 *     <td>object</td>
 *     <td><code>xhtml:content</code> (string), or the text of the element</td>
 *     <td>Always a literal, without surrounding whitespace. Without <code>xhtml:content</code> it
 *       is the text of all descendant text nodes, as for {@link Node#getTextContent()}. Markup
 *       within the element does not create an XML literal, as ODF defines the object as the
 *       "literal content" of the element. Empty literals create no statement.</td>
 *   </tr>
 *   <tr>
 *     <td>datatype</td>
 *     <td><code>xhtml:datatype</code> (CURIE)</td>
 *     <td>The expanded CURIE is the datatype IRI of the literal, e.g. <code>xsd:date</code>. If
 *       it is missing, empty or cannot be expanded, the literal has no explicit datatype.</td>
 *   </tr>
 * </table>
 *
 * <p>A CURIE (compact URI, RDFa 1.0 §7) <code>prefix:reference</code> is expanded by
 * concatenating the namespace IRI bound to <code>prefix</code> by an XML namespace declaration
 * with <code>reference</code>. For example, <code>dc:title</code> becomes <code>
 * http://purl.org/dc/elements/1.1/title</code> when the file declares <code>
 * xmlns:dc="http://purl.org/dc/elements/1.1/"</code>. The prefixes are looked up in the {@link
 * OdfFileDom} as {@link javax.xml.namespace.NamespaceContext}, as ODFDOM moves all namespace
 * declarations of a file to its root element while loading, and because elements are processed
 * before they are inserted into the DOM tree. An empty prefix, as in <code>:reference</code>,
 * refers to the XHTML vocabulary <code>http://www.w3.org/1999/xhtml/vocab#</code>. A CURIE
 * without a colon or with an undeclared prefix is ignored.
 *
 * <p>The object of a <code>&lt;text:bookmark-start&gt;</code> is the text up to the matching
 * <code>&lt;text:bookmark-end&gt;</code>, which is not part of the bookmark element itself. This
 * text is collected by {@link org.odftoolkit.odfdom.dom.rdfa.BookmarkRDFMetadataExtractor}, which
 * calls {@link #addStatements(Model, Element, String)} with it. Therefore {@link #collect(Node,
 * Map)} skips bookmarks.
 */
public final class InContentMetadata {

  /** Namespace of the ODF in-content metadata attributes, e.g. <code>xhtml:about</code>. */
  private static final String XHTML = "http://www.w3.org/1999/xhtml";

  /** IRI prefix for CURIEs with an empty prefix, e.g. <code>:next</code> (RDFa 1.0 §7). */
  private static final String XHTML_VOCAB = "http://www.w3.org/1999/xhtml/vocab#";

  private InContentMetadata() {}

  /**
   * Refreshes the cache entries of the given node and of all its descendant elements.
   *
   * <p>Every element whose attributes state RDF statements gets a new model containing exactly
   * these statements. Every other element loses its entry, so that removed attributes or emptied
   * text do not leave stale statements behind. Bookmarks are skipped, see {@link
   * InContentMetadata}.
   *
   * <p>Note that the object of an element may be the text of its descendants. A text change within
   * a descendant, e.g. of a <code>&lt;text:span&gt;</code> within a <code>&lt;text:p&gt;</code>,
   * only reaches the cache when <code>collect</code> is called for the ancestor carrying the
   * metadata.
   *
   * @param node the root of the subtree to be refreshed, ignored unless it is an element
   * @param cache the cache to be refreshed, keyed by element identity
   */
  public static void collect(Node node, Map<Node, Model> cache) {
    if (node instanceof Element element) {
      Model model = null;
      if (element.hasAttributeNS(XHTML, "about")
          && !(element instanceof TextBookmarkStartElement)) {
        model = ModelFactory.createDefaultModel();
        addStatements(model, element, element.getTextContent());
      }
      if (model == null || model.isEmpty()) {
        cache.remove(element);
      } else {
        cache.put(element, model);
      }
      for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
        collect(child, cache);
      }
    }
  }

  /**
   * Adds the RDF statements stated by the in-content metadata attributes of an element to a model.
   *
   * <p>Nothing is added, if the element lacks <code>xhtml:about</code> or <code>xhtml:property
   * </code>, if the subject cannot be determined or if the literal is empty.
   *
   * @param model the model receiving the statements
   * @param element an element of an {@link OdfFileDom}, carrying in-content metadata attributes
   * @param text the literal content of the element, used unless <code>xhtml:content</code> exists
   */
  public static void addStatements(Model model, Element element, String text) {
    String about = attribute(element, "about");
    String properties = attribute(element, "property");
    String content = attribute(element, "content");
    String lexicalForm = (content != null ? content : text == null ? "" : text).trim();
    Resource subject = about == null ? null : subject(model, element, about.trim());
    if (subject == null || properties == null || lexicalForm.isEmpty()) {
      return;
    }
    String datatype = expand(element, attribute(element, "datatype"));
    Literal object =
        datatype == null
            ? model.createLiteral(lexicalForm)
            : model.createTypedLiteral(
                lexicalForm, TypeMapper.getInstance().getSafeTypeByName(datatype));
    for (String curie : properties.trim().split("\\s+")) {
      String predicate = expand(element, curie);
      if (predicate != null) {
        model.add(subject, model.createProperty(predicate), object);
      }
    }
  }

  /** Returns the subject of an <code>xhtml:about</code> value, or null if it has none. */
  private static Resource subject(Model model, Element element, String about) {
    boolean safeCurie = about.startsWith("[") && about.endsWith("]");
    String value = safeCurie ? about.substring(1, about.length() - 1) : about;
    String baseUri = ((OdfFileDom) element.getOwnerDocument()).getRDFBaseUri();
    if (value.startsWith("_:")) {
      return model.createResource(AnonId.create(baseUri + value));
    }
    String iri = safeCurie ? expand(element, value) : value.isEmpty() ? baseUri : value;
    return iri == null ? null : model.createResource(iri);
  }

  /** Returns the IRI of a CURIE, or null if it is null, has no colon or an unknown prefix. */
  private static String expand(Element element, String curie) {
    int colon = curie == null ? -1 : curie.indexOf(':');
    if (colon < 0) {
      return null;
    }
    String prefix = curie.substring(0, colon);
    String namespace =
        prefix.isEmpty()
            ? XHTML_VOCAB
            : ((OdfFileDom) element.getOwnerDocument()).getNamespaceURI(prefix);
    return namespace.isEmpty() ? null : namespace + curie.substring(colon + 1);
  }

  /** Returns the value of an attribute of the XHTML namespace, or null if it is missing. */
  private static String attribute(Element element, String localName) {
    return element.hasAttributeNS(XHTML, localName)
        ? element.getAttributeNS(XHTML, localName)
        : null;
  }
}
