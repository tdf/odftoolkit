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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents the in OpenDocument format used data type {@odf.datatype
 * cellRangeAddressList}
 */
public class CellRangeAddressList implements OdfDataType {

  private String mCellRangeAddressList;

  /**
   * Construct CellRangeAddressList by the parsing the given string
   *
   * @param cellRangeAddressList The String to be parsed into CellRangeAddressList
   * @throws IllegalArgumentException if the given argument is not a valid CellRangeAddressList
   */
  public CellRangeAddressList(List<CellRangeAddress> cellRangeAddressList)
      throws IllegalArgumentException {
    if (cellRangeAddressList == null) {
      throw new IllegalArgumentException("parameter can not be null for CellRangeAddressList");
    }

    mCellRangeAddressList = cellRangeAddressList.stream()
      .map(CellRangeAddress::toString)
      .collect(Collectors.joining(" "));
  }

  // TODO: Should a cell address stay a string?
  /**
   * Returns a space separated String Object representing this CellRangeAddressList's value
   *
   * @return return a string representation of the value of this CellRangeAddressList object
   */
  @Override
  public String toString() {
    return mCellRangeAddressList;
  }

  /**
   * Returns a CellRangeAddressList instance representing the specified String value
   *
   * @param stringValue a String value
   * @return return a CellRangeAddressList instance representing stringValue
   * @throws IllegalArgumentException if the given argument is not a valid CellRangeAddressList
   */
  public static CellRangeAddressList valueOf(String stringValue) throws IllegalArgumentException {
    if (stringValue == null) {
      throw new IllegalArgumentException("parameter is invalid for datatype CellRangeAddressList");
    }

    List<CellRangeAddress> aRet = new ArrayList<>();
    String[] names = stringValue.split(" ");
    for (String name : names) {
      aRet.add(new CellRangeAddress(name));
    }
    return new CellRangeAddressList(aRet);
  }

  /**
   * Returns a list of CellRangeAddress from the CellRangeAddressList Object
   *
   * @return a list of CellRangeAddress
   */
  public List<CellRangeAddress> getCellRangesAddressList() {
    List<CellRangeAddress> aRet = new ArrayList<>();
    String[] names = mCellRangeAddressList.split(" ");
    for (String name : names) {
      aRet.add(new CellRangeAddress(name));
    }
    return aRet;
  }

  /**
   * check if the specified String is a valid {@odf.datatype cellRangeAddressList} data type
   *
   * @param stringValue the value to be tested
   * @return true if the value of argument is valid for {@odf.datatype cellRangeAddressList} data
   *     type false otherwise
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
      if (!CellRangeAddress.isValid(name)) {
        return false;
      }
    }
    return true;
  }
}
