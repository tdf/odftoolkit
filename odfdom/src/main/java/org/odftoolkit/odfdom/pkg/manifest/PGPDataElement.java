/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Use is subject to license terms.
 *
 * <p>Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
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

/*
 * This file is automatically generated.
 * Don't edit manually.
 */
package org.odftoolkit.odfdom.pkg.manifest;

import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.pkg.OdfPackageNamespace;

/** Manifest implementation of OpenDocument element {@odf.element manifest:PGPData}. */
public class PGPDataElement extends OdfElement {

  public static final OdfName ELEMENT_NAME =
      OdfName.newName(OdfPackageNamespace.MANIFEST, "PGPData");

  /**
   * Create the instance of <code>PGPDataElement</code>
   *
   * @param ownerDoc The type is <code>OdfFileDom</code>
   */
  public PGPDataElement(OdfFileDom ownerDoc) {
    super(ownerDoc, ELEMENT_NAME);
  }

  /**
   * Get the element name
   *
   * @return return <code>OdfName</code> the name of element {@odf.element manifest:PGPData}.
   */
  @Override
  public OdfName getOdfName() {
    return ELEMENT_NAME;
  }

  /**
   * Create child element {@odf.element manifest:PGPKeyID}.
   *
   * <p>Child element is mandatory.
   *
   * @return the element {@odf.element manifest:PGPKeyID}
   */
  public PGPKeyIDElement newPGPKeyIDElement() {
    PGPKeyIDElement pGPKeyID =
        ((OdfFileDom) this.ownerDocument).newOdfElement(PGPKeyIDElement.class);
    this.appendChild(pGPKeyID);
    return pGPKeyID;
  }

  /**
   * Create child element {@odf.element manifest:PGPKeyPacket}.
   *
   * @return the element {@odf.element manifest:PGPKeyPacket}
   */
  public PGPKeyPacketElement newPGPKeyPacketElement() {
    PGPKeyPacketElement pGPKeyPacket =
        ((OdfFileDom) this.ownerDocument).newOdfElement(PGPKeyPacketElement.class);
    this.appendChild(pGPKeyPacket);
    return pGPKeyPacket;
  }
}