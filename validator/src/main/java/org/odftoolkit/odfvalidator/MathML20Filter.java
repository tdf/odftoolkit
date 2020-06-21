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
package org.odftoolkit.odfvalidator;

import java.io.IOException;
import java.io.StringReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/** */
class MathML20Filter extends XMLFilterImpl {

  private Logger m_aLogger;

  /** Creates a new instance of MathMLFilter */
  MathML20Filter(Logger aLogger) {
    m_aLogger = aLogger;
  }

  @Override
  public InputSource resolveEntity(String aPublicId, String aSystemId)
      throws SAXException, IOException {
    // Ignore the external OOo MathML DTD

    String aMsg = "Ignoring doctype definition '" + aPublicId + "'";
    m_aLogger.logInfo(aMsg, false);
    return new InputSource(new StringReader(""));
  }
}
