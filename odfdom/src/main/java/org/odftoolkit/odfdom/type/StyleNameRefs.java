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
package org.odftoolkit.odfdom.type;

/** This class represents the in OpenDocument format used data type {@odf.datatype styleNameRefs} */
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StyleNameRefs implements OdfDataType {

  private String mStyleNames;

  /**
   * Construct StyleNameRefs by the parsing the given StyleName list
   *
   * @param styleNames The String to be parsed into StyleNameRefs
   * @throws IllegalArgumentException if the given argument is not a valid StyleNameRefs
   */
  public StyleNameRefs(List<StyleName> styleNames) throws IllegalArgumentException {
    if (styleNames == null) {
      throw new IllegalArgumentException("parameter can not be null for StyleNameRefs");
    }
    mStyleNames = styleNames.stream().map(StyleName::toString).collect(Collectors.joining(" "));
  }

  /**
   * Returns a String Object representing this StyleNameRefs's value
   *
   * @return return a string representation of the value of this StyleNameRefs object
   */
  @Override
  public String toString() {
    return mStyleNames;
  }

  /**
   * Returns a StyleNameRefs instance representing the specified String value
   *
   * @param stringValue a String value
   * @return return a StyleNameRefs instance representing stringValue
   * @throws IllegalArgumentException if the given argument is not a valid StyleNameRefs
   */
  public static StyleNameRefs valueOf(String stringValue) throws IllegalArgumentException {
    if (stringValue == null) {
      throw new IllegalArgumentException("parameter is invalid for datatype StyleNameRefs");
    }

    List<StyleName> aRet = new ArrayList<>();
    if (stringValue.length() > 0) {
      String[] names = stringValue.split(" ");
      for (String name : names) {
        aRet.add(new StyleName(name));
      }
    }
    return new StyleNameRefs(aRet);
  }

  /**
   * Returns a list of StyleNameRef from the StyleNameRefs Object
   *
   * @return a list of StyleNameRef
   */
  public List<StyleName> getStyleNameRefList() {
    List<StyleName> aRet = new ArrayList<>();
    if (mStyleNames.length() > 0) {
      String[] names = mStyleNames.split(" ");
      for (String name : names) {
        aRet.add(new StyleName(name));
      }
    }
    return aRet;
  }

  /**
   * check if the specified String instance is a valid {@odf.datatype styleNameRefs} data type
   *
   * @param stringValue the value to be tested
   * @return true if the value of argument is valid for {@odf.datatype styleNameRefs} data type
   *     false otherwise
   */
  public static boolean isValid(String stringValue) {
    if (stringValue == null) {
      return false;
    }
    if (stringValue.length() == 0) {
      return true;
    }

    String[] names = stringValue.split(" ");
    for (String name : names) {
      if (!StyleNameRef.isValid(name)) {
        return false;
      }
    }
    return true;
  }
}
