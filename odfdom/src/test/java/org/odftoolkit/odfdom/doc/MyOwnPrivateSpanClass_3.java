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
package org.odftoolkit.odfdom.doc;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;

public class MyOwnPrivateSpanClass_3 extends OdfElement {

  /** */
  private static final long serialVersionUID = -4819629767539792181L;

  public static final OdfName ELEMENT_NAME = OdfName.newName(OdfDocumentNamespace.TEXT, "span");

  /** Creates a new instance of OdfParagraphElementImpl */
  public MyOwnPrivateSpanClass_3(OdfFileDom ownerDoc) {
    super(ownerDoc, ELEMENT_NAME);
  }

  @Override
  public OdfName getOdfName() {
    return ELEMENT_NAME;
  }
}
