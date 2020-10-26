/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * <p>Use is subject to license terms.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0. You can also obtain a copy of the License at
 * http://odftoolkit.org/docs/license.txt
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 *
 * <p>See the License for the specific language governing permissions and limitations under the
 * License.
 *
 * <p>**********************************************************************
 */
package org.odftoolkit.odfdom.pkg;

import java.util.HashMap;
import java.util.Map;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;

/**
 * Class wrapping the XML Namespace URI and XML Namespace prefix (used by default) as a single
 * entity For instance, the ODF paragraph element <text:p> uses by default in the ODF specification
 * the prefix "text" and is bound to this prefix heres
 */
public class OdfNamespace implements Comparable<OdfNamespace>, NamespaceName {

  private static Map<String, OdfNamespace> mNamespacesByURI = new HashMap<String, OdfNamespace>();
  private String mUri;
  private String mPrefix;
  /**
   * In case of a default namespace no prefix is given, but we require the prefix for our class
   * loader
   */
  private static Map<String, String> mUrlToPrefix;

  private OdfNamespace() {}

  private OdfNamespace(String prefix, String uri) {
    mUri = uri;
    mPrefix = prefix;
  }

  /**
   * Returns the OdfNamespace for the given name. Creates a new one, if the name was not asked
   * before.
   *
   * @param name represents a W3C Namespace Name. The interface <code>NamespaceName</code> is often
   *     implemented by an enum.
   * @return the OdfNamespace for the given name.
   */
  public static OdfNamespace newNamespace(NamespaceName name) {
    OdfNamespace ns = null;
    if (name != null) {
      ns = newNamespace(name.getPrefix(), name.getUri());
    }
    return ns;
  }

  /**
   * Returns the OdfNamespace for the given name. Creates a new one, if the name was not asked
   * before.
   *
   * @param prefix abbreviation for the URL, might be null when a default namespace was set
   * @param uri identifying the namespace.
   * @return the namespace.
   */
  public static OdfNamespace newNamespace(String prefix, String uri) {
    OdfNamespace odfNamespace = null;
    if (prefix == null || prefix.isEmpty()) {
      if (mUrlToPrefix == null) {
        initializeUrl2DefaultPrefixMap();
      }
      prefix = mUrlToPrefix.get(uri);
    }
    if (uri != null && uri.length() > 0) {
      odfNamespace = mNamespacesByURI.get(uri);
      if (odfNamespace == null) {
        odfNamespace = new OdfNamespace(prefix, uri);
        mNamespacesByURI.put(uri, odfNamespace);
      } else {
        if (prefix != null) {
          // prefix will be adapted for all OdfNamespaces (last wins)
          odfNamespace.mPrefix = prefix;
        }
      }
    }
    return odfNamespace;
  }

  /**
   * In case of a default namespace no prefix is given, but we require the prefix for our class
   * loader
   */
  public static void initializeUrl2DefaultPrefixMap() {
    mUrlToPrefix = new HashMap(32);
    // add all namespaces from the ODF package specification
    for (OdfPackageNamespace packageNamespace : OdfPackageNamespace.values()) {
      mUrlToPrefix.put(packageNamespace.getUri(), packageNamespace.getPrefix());
    }
    // add all namespaces from the ODF schema specification
    for (OdfDocumentNamespace schemaNamespace : OdfDocumentNamespace.values()) {
      mUrlToPrefix.put(schemaNamespace.getUri(), schemaNamespace.getPrefix());
    }
  }

  /**
   * Returns the namespace for the given uri.
   *
   * @param uri identifying the namespace.
   * @return the namespace identified by the given uri.
   */
  public static OdfNamespace getNamespace(String uri) {
    OdfNamespace ns = null;
    if (uri != null) {
      ns = mNamespacesByURI.get(uri);
    }
    return ns;
  }

  public String getPrefix() {
    return mPrefix;
  }

  public String getUri() {
    return mUri;
  }

  @Override
  @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
  public boolean equals(Object obj) {
    if (mUri != null) {
      return mUri.equals(obj.toString());
    } else {
      return mUri == obj;
    }
  }

  @Override
  public int hashCode() {
    if (mUri != null) {
      return mUri.hashCode();
    } else {
      return 0;
    }
  }

  @Override
  public String toString() {
    return mUri;
  }

  /**
   * Splits the XML Qname into the local name and the prefix.
   *
   * @param qname is the qualified name to be split.
   * @return an array of two strings containing first the prefix and the second the local part.
   * @throws IllegalArgumentException if no qualified name was given.
   */
  public static String[] splitQName(String qname) throws IllegalArgumentException {
    String localpart = qname;
    String prefix = null;
    int colon = qname.indexOf(':');
    if (colon > 0) {
      localpart = qname.substring(colon + 1);
      prefix = qname.substring(0, colon);
    } else {
      throw new IllegalArgumentException(
          "A qualified name was required, but '" + qname + "' was given!");
    }
    return new String[] {prefix, localpart};
  }

  /**
   * @param qname is the qualified name to be splitted.
   * @return the local name of the XML Qname.
   * @throws IllegalArgumentException if no qualified name was given.
   */
  public static String getPrefixPart(String qname) {
    return splitQName(qname)[0];
  }

  /**
   * @param qname is the qualified name to be splitted.
   * @return the prefix of the XML Qname.
   * @throws IllegalArgumentException if no qualified name was given.
   */
  public static String getLocalPart(String qname) {
    return splitQName(qname)[1];
  }

  public int compareTo(OdfNamespace namespace) {
    return toString().compareTo(namespace.toString());
  }
}
