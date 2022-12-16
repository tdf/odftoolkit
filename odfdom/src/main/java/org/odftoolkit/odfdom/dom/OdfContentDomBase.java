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
package org.odftoolkit.odfdom.dom;

import java.util.IdentityHashMap;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.pkg.NamespaceName;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.pkg.OdfPackageDocument;

/** Base class for an XML stream that contains document content, i.e., styles.xml or content.xml. */
public class OdfContentDomBase extends OdfFileDom {

  private static final long serialVersionUID = 6823264460360047745L;

  IdentityHashMap<TableTableElement, OdfTable> mTableRepository =
      new IdentityHashMap<TableTableElement, OdfTable>();

  public OdfContentDomBase(OdfPackageDocument packageDocument, String packagePath) {
    super(packageDocument, packagePath);
  }

  public OdfContentDomBase(OdfPackage pkg, String packagePath) {
    super(pkg, packagePath);
  }

  /**
   * Retrieves the ODF Document
   *
   * @return The <code>OdfDocument</code>
   */
  @Override
  public OdfSchemaDocument getDocument() {
    return (OdfSchemaDocument) mPackageDocument;
  }

  /**
   * Creates an JDK <code>XPath</code> instance. Initialized with ODF namespaces from <code>
   * OdfDocumentNamespace</code>. Updated with all namespace of the XML file.
   *
   * @return an XPath instance with namespace context set to include the standard ODFDOM prefixes.
   */
  @Override
  public XPath getXPath() {
    if (mXPath == null) {
      mXPath = XPathFactory.newInstance().newXPath();
      mXPath.setNamespaceContext(this);
      for (NamespaceName name : OdfDocumentNamespace.values()) {
        mUriByPrefix.put(name.getPrefix(), name.getUri());
        mPrefixByUri.put(name.getUri(), name.getPrefix());
      }
    }
    return mXPath;
  }

  public IdentityHashMap<TableTableElement, OdfTable> getTableRepository() {
    return mTableRepository;
  }
}
