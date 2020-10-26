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

import org.w3c.dom.DOMException;

/** Any element within the ODF package that is listed in the ODF schema. */
public class OdfAlienElement extends OdfElement {

  private static final long serialVersionUID = 6693153432396354134L;

  public OdfAlienElement(OdfFileDom ownerDocument, OdfName name) throws DOMException {
    super(ownerDocument, name.getUri(), name.getQName());
    ELEMENT_NAME = name;
  }

  public final OdfName ELEMENT_NAME;

  @Override
  public OdfName getOdfName() {
    return ELEMENT_NAME;
  }

  /** Special handling for this class, which represents elements of various names */
  protected OdfElement cloneOdfElement() {
    return new OdfAlienElement((OdfFileDom) this.ownerDocument, this.ELEMENT_NAME);
  }
}
