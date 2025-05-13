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

import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;

public class ODFValidationResult
    implements ManifestListener, MetaInformationListener, ForeignContentListener {
  public enum Status {
    /** The document is non conforming to a particular conformance level. */
    NON_CONFORMING,
    /** The document is conforming to a particular conformance level. */
    CONFORMING,
    /** It is unknown whether the document conforms to a particular level. */
    UNKNOWN,
    /** The conformance level is not applicable to the specified file. */
    NOT_APPLICABLE
  }

  private String m_aGenerator = null;
  private String m_aMediaType = "";

  private HashMap<String, Long> m_aForeignElementMap = null;
  private HashMap<String, Long> m_aForeignAttributeMap = null;

  private Status m_aStrictValid = Status.UNKNOWN;
  private Status m_aValid = Status.UNKNOWN;
  private Status m_aConforming = Status.UNKNOWN;
  private Status m_aExtendedConforming = Status.UNKNOWN;

  private OdfVersion m_aVersion = null;
  private OdfValidatorMode m_eMode = null;

  ODFValidationResult(OdfVersion aVersion, OdfValidatorMode eMode) {
    m_aVersion = aVersion;
    m_eMode = eMode;
  }

  public void setGenerator(String aGenerator) {
    m_aGenerator = aGenerator;
  }

  public void setMediaType(String aMediaType) {
    m_aMediaType = aMediaType;
  }

  public void foreignElementDetected(
      String aUri, String aLocalName, String aQName, Attributes aAtts) {
    if (m_aForeignElementMap == null) m_aForeignElementMap = new HashMap<String, Long>();

    Long aCount = m_aForeignElementMap.get(aUri);
    if (aCount == null) aCount = 0L;
    m_aForeignElementMap.put(aUri, aCount + 1);
  }

  public void foreignAttributeDetected(
      String aUri, String aLocalName, String aQName, String aValue) {
    if (m_aForeignAttributeMap == null) m_aForeignAttributeMap = new HashMap<String, Long>();

    Long aCount = m_aForeignAttributeMap.get(aUri);
    if (aCount == null) aCount = 0L;
    m_aForeignAttributeMap.put(aUri, aCount + 1);
  }

  public String getGenerator() {
    return m_aGenerator;
  }

  public String getMediaType() {
    return m_aMediaType;
  }

  public boolean hasForeignElements() {
    return m_aForeignElementMap != null;
  }

  public Map<String, Long> getForeignElements() {
    return m_aForeignElementMap;
  }

  public boolean hasForeignAttributes() {
    return m_aForeignAttributeMap != null;
  }

  public Map<String, Long> getForeignAttributes() {
    return m_aForeignAttributeMap;
  }
}
