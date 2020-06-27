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

import java.io.InputStream;
import java.io.Reader;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

class SchemaResourceResolver implements LSResourceResolver {
  private String m_aBaseURI = null;
  private Logger m_aLogger = null;
  private DOMImplementationLS m_aDOMImplLS = null;
  private boolean m_bGetDOMImplLSFailed = false;

  class LSInputImpl implements LSInput {
    private InputStream m_aInputStream = null;
    private String m_aSystemId = null;

    public Reader getCharacterStream() {
      return null;
    }

    public void setCharacterStream(Reader aCharacterStream) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    public InputStream getByteStream() {
      return m_aInputStream;
    }

    public void setByteStream(InputStream aByteStream) {
      m_aInputStream = aByteStream;
    }

    public String getStringData() {
      return null;
    }

    public void setStringData(String stringData) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getSystemId() {
      return m_aSystemId;
    }

    public void setSystemId(String aSystemId) {
      m_aSystemId = aSystemId;
    }

    public String getPublicId() {
      return null;
    }

    public void setPublicId(String publicId) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getBaseURI() {
      return null;
    }

    public void setBaseURI(String baseURI) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getEncoding() {
      return null;
    }

    public void setEncoding(String encoding) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean getCertifiedText() {
      return false;
    }

    public void setCertifiedText(boolean certifiedText) {
      throw new UnsupportedOperationException("Not supported yet.");
    }
  }

  public SchemaResourceResolver(Logger aLogger, String aBaseURI) {
    m_aBaseURI = aBaseURI;
    m_aLogger = aLogger;
  }

  public LSInput resolveResource(
      String aType, String aNamespaceURI, String aPublicId, String aSystemId, String aBaseURI) {
    LSInput aInput = null;

    if (aBaseURI == null) aBaseURI = m_aBaseURI;

    if (aSystemId != null
        && aBaseURI != null
        && InternalResources.isInternalResourceIdentifer(aBaseURI)) {
      String aURI = aBaseURI.substring(0, aBaseURI.lastIndexOf('/') + 1).concat(aSystemId);
      String aPath = InternalResources.getResourcePath(aURI);
      InputStream aInStream = getClass().getResourceAsStream(aPath);
      if (aInStream != null) {
        aInput = createLSInput();
        aInput.setSystemId(aURI);
        aInput.setByteStream(aInStream);

        String aMsg = "resolving '" + aURI + "'";
        m_aLogger.logInfo(aMsg, false);
      } else {
        m_aLogger.logFatalError("Missing internal schema file: ".concat(aPath));
      }
    }

    return aInput;
  }

  private LSInput createLSInput() {
    LSInput aLSInput = null;

    if (m_aDOMImplLS == null && !m_bGetDOMImplLSFailed) {
      DOMImplementationRegistry aDOMReg = null;

      try {
        aDOMReg = DOMImplementationRegistry.newInstance();
      } catch (ClassNotFoundException e) {
        m_aLogger.logFatalError(e.getMessage());
      } catch (InstantiationException e) {
        m_aLogger.logFatalError(e.getMessage());
      } catch (IllegalAccessException e) {
        m_aLogger.logFatalError(e.getMessage());
      }
      DOMImplementation aDOMImpl = aDOMReg.getDOMImplementation("LS 3.0");
      if (aDOMImpl != null) {
        m_aDOMImplLS = (DOMImplementationLS) aDOMImpl;
      } else {
        m_bGetDOMImplLSFailed = true;
        m_aLogger.logInfo(
            "Could not find DOM LS 3.0 Implementation, using own implementation", false);
      }
    }
    if (m_aDOMImplLS != null) {
      aLSInput = m_aDOMImplLS.createLSInput();
    } else {
      aLSInput = new LSInputImpl();
    }

    return aLSInput;
  }
}
