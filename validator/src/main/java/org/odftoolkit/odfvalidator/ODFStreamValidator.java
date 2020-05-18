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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.xml.sax.ErrorHandler;

/** Validator for Streams */
public class ODFStreamValidator extends ODFRootPackageValidator {

  private InputStream m_aInputStream = null;
  private String m_aBaseURI = null;
  private byte[] m_Buffer = null;

  ODFStreamValidator(
      InputStream aInputStream,
      String aBaseURI,
      Logger.LogLevel nLogLevel,
      OdfValidatorMode eMode,
      OdfVersion aVersion,
      SAXParseExceptionFilter aFilter,
      ODFValidatorProvider aValidatorProvider)
      throws ODFValidatorException {
    super(nLogLevel, eMode, aVersion, aFilter, aValidatorProvider);

    m_aInputStream = aInputStream;
    m_aBaseURI = aBaseURI;
  }

  protected OdfPackage getPackage(ErrorHandler handler) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buf = new byte[4096];
    while (true) {
      int n = m_aInputStream.read(buf);
      if (n < 0) {
        break;
      }
      baos.write(buf, 0, n);
    }
    m_Buffer = baos.toByteArray();

    ByteArrayInputStream bais = new ByteArrayInputStream(m_Buffer);

    OdfPackage ret = OdfPackage.loadPackage(bais, m_aBaseURI, handler);
    m_Buffer = null; // only needed in fallbackValidateManifest
    return ret;
  }

  protected String getLoggerName() {
    return m_aBaseURI;
  }

  protected @Override String getDocumentPath() {
    return ""; // this is the root document
  }
};
