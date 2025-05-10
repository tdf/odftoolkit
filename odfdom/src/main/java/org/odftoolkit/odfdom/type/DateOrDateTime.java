/**
 * **********************************************************************
 *
 * <p>Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * <p>**********************************************************************
 */
package org.odftoolkit.odfdom.type;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * This class represents the in OpenDocument format used data type {@odf.datatype dateOrDateTime}
 */
public class DateOrDateTime implements OdfDataType {

  private static final Logger LOG = Logger.getLogger(DateOrDateTime.class.getName());
  private XMLGregorianCalendar mDateOrDateTime;

  /**
   * Construct an newly DateOrDateTime object that represents the specified XMLGregorianCalendar
   * value
   *
   * @param dateOrDateTime the value to be represented by the DateOrDateTime Object
   * @throws IllegalArgumentException if the given argument is not a valid DateOrDateTime
   */
  public DateOrDateTime(XMLGregorianCalendar dateOrDateTime) throws IllegalArgumentException {
    if (DateOrDateTime.isValid(dateOrDateTime)) {
      mDateOrDateTime = dateOrDateTime;
    } else {
      throw new IllegalArgumentException("parameter is invalid for datatype DateOrDateTime");
    }
  }

  /**
   * Returns a String Object representing this DateOrDateTime value
   *
   * @return return a string representation of the value of this DateOrDateTime object
   */
  @Override
  public String toString() {
    return mDateOrDateTime.toXMLFormat();
  }

  /**
   * Returns a DateOrDateTime instance representing the specified String value
   *
   * @param stringValue a String value
   * @return return a DateOrDateTime instance representing stringValue
   * @throws IllegalArgumentException if the given argument is not a valid DateOrDateTime
   */
  public static DateOrDateTime valueOf(String stringValue) throws IllegalArgumentException {
    try {
      DatatypeFactory aFactory = new org.apache.xerces.jaxp.datatype.DatatypeFactoryImpl();
      return new DateOrDateTime(aFactory.newXMLGregorianCalendar(stringValue));
    } catch (IllegalArgumentException ex) {
      LOG.log(Level.SEVERE, "parameter is invalid for datatype DateOrDateTime", ex);
      throw new IllegalArgumentException("parameter is invalid for datatype DateOrDateTime");
    }
  }

  /**
   * Returns the value of this DateOrDateTime object as an XMLGregorianCalendar
   *
   * @return the XMLGregorianCalendar value of this DateOrDateTime object.
   */
  public XMLGregorianCalendar getXMLGregorianCalendar() {
    return mDateOrDateTime;
  }

  /**
   * check if the specified XMLGregorianCalendar instance is a valid {@odf.datatype dateOrDateTime}
   * data type
   *
   * @param dateOrDateTime the value to be tested
   * @return true if the value of argument is valid for {@odf.datatype dateOrDateTime} data type
   *     false otherwise
   */
  public static boolean isValid(XMLGregorianCalendar dateOrDateTime) {
    return (Date.isValid(dateOrDateTime) || DateTime.isValid(dateOrDateTime));
  }
}
