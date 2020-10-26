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

import java.util.HashSet;
import org.xml.sax.SAXParseException;

/** */
public class ValidationMessageCollectorErrorFilter implements SAXParseExceptionFilter {

  private String m_aGenerator;
  private HashSet<String> m_aMsgsReported;

  /** Creates a new instance of ValidationErrorFilter */
  public ValidationMessageCollectorErrorFilter() throws ODFValidatorException {
    m_aMsgsReported = new HashSet<String>();
  }

  public SAXParseException filterException(SAXParseException aExc) {
    String aMsg = aExc.getMessage();
    if (!m_aMsgsReported.contains(aMsg)) {
      m_aMsgsReported.add(aMsg);
    }

    return aExc;
  }

  public void startPackage(String aGenerator) {}

  public void setGenerator(String aGenerator) {
    m_aGenerator = aGenerator;
  }

  public void startSubFile() {}

  public String getGenerator() {
    return m_aGenerator;
  }

  public HashSet<String> getMessages() {
    return m_aMsgsReported;
  }
}
