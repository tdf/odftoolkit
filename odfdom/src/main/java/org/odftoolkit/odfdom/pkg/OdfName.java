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

/**
 * The class provides a simplified interface for XML names. The class defines a name for an XML
 * node. It embraces XML NamespaceURI, XML prefix and XML localname.
 */
public class OdfName implements Comparable<OdfName> {

  private OdfNamespace mNS;
  private String mLocalName;
  private String mExpandedName; // i.e. {nsURI}localName
  private static HashMap<String, OdfName> mOdfNames = new HashMap<String, OdfName>();
  private static StringBuilder mSB;

  private OdfName(OdfNamespace ns, String localname, String expandedName) {
    mNS = ns;
    mLocalName = localname;
    mExpandedName = expandedName;
  }

  /**
   * Returns the OdfName for the given namespace and name. Creates a new one, if the OdfName was not
   * asked before.
   *
   * @param name of the XML node
   * @return the OdfName for the given OdfNamesapce and name.
   */
  public static OdfName newName(String name) {
    return createName(null, name);
  }

  /**
   * Returns the OdfName for the given namespace and name. Creates a new one, if the OdfName was not
   * asked before.
   *
   * @param odfNamespace the namespace of the name to be created
   * @param name of the XML node. Can be both local or qualified name.
   * @return the OdfName for the given OdfNamesapce and name.
   */
  public static OdfName newName(OdfNamespace odfNamespace, String name) {
    return createName(odfNamespace, name);
  }

  /**
   * Returns the OdfName for the given namespace and name. Creates a new one, if the OdfName was not
   * asked before.
   *
   * @param namespaceNamed represents a W3C Namespace Name. The interface <code>NamespaceName</code>
   *     is often implemented by an enum.
   * @param name of the XML node. Can be both local or qualified name.
   * @return the OdfName for the given OdfNamesapce and name.
   */
  public static OdfName newName(NamespaceName namespaceNamed, String name) {
    return createName(OdfNamespace.newNamespace(namespaceNamed), name);
  }

  public static OdfName newName(String uri, String qname) {
    String prefix = OdfNamespace.getPrefixPart(qname);
    String localName = OdfNamespace.getLocalPart(qname);
    OdfNamespace ns = OdfNamespace.newNamespace(prefix, uri);
    return createName(ns, localName);
  }

  private static OdfName createName(OdfNamespace odfNamespace, String name) {
    int i = 0;
    // make sure the name is the local name
    if ((i = name.indexOf(':')) >= 0) {
      name = name.substring(i + 1);
    }
    return getOdfName(odfNamespace, name);
  }

  /** Used for receiving an OdfName, in case of a default nameaspace, when there is no prefix. */
  public static OdfName getOdfName(OdfNamespace odfNamespace, String localName) {
    String expandedName = null;
    if (odfNamespace != null) {
      expandedName = createExpandedName(odfNamespace.toString(), localName);
    } else {
      expandedName = localName;
    }
    // return a similar OdfName if one was already created before..
    OdfName odfName = mOdfNames.get(expandedName);
    if (odfName != null) {
      return odfName;
    } else {
      // otherwise create a new OdfName, store it in the map and return it..
      odfName = new OdfName(odfNamespace, localName, expandedName);
      mOdfNames.put(expandedName, odfName);
      return odfName;
    }
  }

  private static String createExpandedName(String nsUri, String localName) {
    if (mSB == null) {
      mSB = new StringBuilder();
    } else {
      mSB.delete(0, mSB.length());
    }
    mSB.append('{');
    mSB.append(nsUri);
    mSB.append('}');
    mSB.append(localName);
    return mSB.toString();
  }

  /**
   * @return the XML Namespace URI, for <text:p> it would be
   *     urn:oasis:names:tc:opendocument:xmlns:text:1.0
   */
  public String getUri() {
    if (mNS == null) {
      return null;
    } else {
      return mNS.getUri();
    }
  }

  /** @return the XML localname, for <text:p> it would be p. */
  public String getLocalName() {
    return mLocalName;
  }

  /** @return the XML prefix, for <text:p> it would be text. */
  public String getPrefix() {
    String prefix = null;
    if (mNS != null) {
      prefix = mNS.getPrefix();
    }
    return prefix;
  }

  /** @return the XML QName, the qualified name e.g. for <text:p> it is text:p. */
  public String getQName() {
    if (mNS != null) {
      return ((mNS.getPrefix() + ":" + mLocalName).intern());
    } else {
      return mLocalName;
    }
  }

  /**
   * @return the OdfName as String, represented by a concatenation of XML Namespace URI (within
   *     brackets) and local name, as for <text:p> it would be
   *     {urn:oasis:names:tc:opendocument:xmlns:text:1.0}p
   */
  @Override
  public String toString() {
    return mExpandedName;
  }

  @Override
  @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
  public boolean equals(Object obj) {
    if (obj != null) {
      return toString().equals(obj.toString());
    } else {
      return false;
    }
  }

  /**
   * @param namespaceUri of the XML node to be compared.
   * @param name of the XML node to be compared. Can be qualifed name or localname.
   * @return true if the given OdfName has the same namespaceURI and localname.
   */
  public boolean equals(String namespaceUri, String name) {
    if (!mNS.getUri().equals(namespaceUri)) {
      return false;
    }

    int beginIndex = name.indexOf(':');
    if (beginIndex >= 0) {
      return mLocalName.equals(name.substring(beginIndex + 1));
    } else {
      return mLocalName.equals(name);
    }
  }

  @Override
  /** Returns the hashcode of the OdfName */
  public int hashCode() {
    return toString().hashCode();
  }

  /** Compares the by parameter given OdfName with this OdfName */
  public int compareTo(OdfName o) {
    return toString().compareTo(o.toString());
  }
}
