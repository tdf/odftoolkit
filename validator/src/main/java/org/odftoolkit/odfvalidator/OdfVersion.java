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

public enum OdfVersion {
  V1_0("1.0"),
  V1_1("1.1"),
  V1_2("1.2"),
  V1_3("1.3"),
  V1_4("1.4");

  private String m_aValue;

  OdfVersion(String _aValue) {
    m_aValue = _aValue;
  }

  @Override
  public String toString() {
    return m_aValue;
  }

  public static OdfVersion valueOf(String _aString, boolean _bAttrValue) {
    if (_aString == null) return _bAttrValue ? V1_1 : null;

    for (OdfVersion aIter : values()) {
      if (_aString.equals(aIter.toString())) {
        return aIter;
      }
    }
    return _bAttrValue ? V1_1 : null;
  }
}
