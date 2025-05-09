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

import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * This class represents the in OpenDocument format used data type {
 *
 * @odf.datatype length}
 */
public class Length implements OdfDataType {

  private static final Logger LOG = Logger.getLogger(Length.class.getName());

  /** Measurement units for ODF datatype length */
  public enum Unit {
    //		TWIP(0.0176389241667372, "twip"), // TWentieth of an Inch Point

    POINT(0.352777778, "pt"), // Pica Point
    PIXEL(0.28, "px"), // Pixel (see http://www.w3.org/TR/2001/REC-xsl-20011015/slice5.html#pixels)
    //		DIDOT_POINT(0.375972222, "dpt"), // Didot point (or Point typographique) after the French
    // typographer Firmin Didot (1764-1836).
    MILLIMETER(1.0, "mm"), // see http://www.w3.org/TR/2001/REC-xsl-20011015/sliceD.html#ISO31
    MICROMETER(0.001, "\u00b5m"), // see http://en.wikipedia.org/wiki/Micrometre
    PICA(4.2176, "pc"), // 1 Inch = 6 Pica = 72 Pica Point
    CENTIMETER(10.0, "cm"), // see http://www.w3.org/TR/2001/REC-xsl-20011015/sliceD.html#ISO31
    INCH(25.4, "in");
    //		FEET(304.8, "ft"),
    //		METER(1000.0, "m"),
    //		KILOMETER(1000000.0, "km"),
    //		MILES(1609344.0, "mi");
    private final double mUnitInMillimiter;
    private final String mUnitAbbreviation;

    Unit(double unitInMillimiter, String unitAbbreviation) {
      this.mUnitInMillimiter = unitInMillimiter;
      this.mUnitAbbreviation = unitAbbreviation;
    }

    /** @return the length of the Unit in Millimeter */
    public double unitInMillimiter() {
      return mUnitInMillimiter;
    }

    /** @return the abbreviation of the Unit (e.g. cm for Centimeter) */
    public String abbr() {
      return mUnitAbbreviation;
    }
  }

  private String mLengthString = null;
  private static final Pattern lengthPattern =
      Pattern.compile("^-?([0-9]+(\\.[0-9]*)?|\\.[0-9]+)((cm)|(mm)|(in)|(pt)|(pc)|(px))$");

  /**
   * Construct Length by the parsing the given string
   *
   * @param length The String to be parsed into Length
   * @throws NumberFormatException if the given argument is not a valid Length (an Integer only will
   *     be presumed to be 'pt')
   */
  public Length(String length) throws NumberFormatException {
    if (!isValid(length)) {
      try {
        Integer.parseInt(length);
        length = length + Unit.POINT.abbr();
      } catch (NumberFormatException e) {
        throw new NumberFormatException(
            "parameter '" + length + "' is invalid for datatype Length");
      }
    }
    mLengthString = length;
  }

  /**
   * Check if the specified String instance is a valid {
   *
   * @odf.datatype length} data type
   * @param stringValue the value to be tested
   * @return true if the value of argument is valid for {
   * @odf.datatype length} data type false otherwise
   */
  public static boolean isValid(String stringValue) {
    if ((stringValue == null) || (!lengthPattern.matcher(stringValue).matches())) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * Returns the Unit of the given length.
   *
   * @param length the <code>Unit</code> should be obtained from
   * @return Returns a <code>Unit</code> object representing the specified Length unit.
   */
  public static Unit parseUnit(String length) {
    Unit lengthUnit = null;
    if (length == null) {
      throw new NumberFormatException("The input length should not be null!");
    } else {
      boolean identifiedInput = false;
      for (Unit unit : Unit.values()) {
        if (length.contains(unit.abbr())) {
          lengthUnit = unit;
          identifiedInput = true;
          break;
        }
      }
      if (!identifiedInput) {
        throw new NumberFormatException("The input length " + length + " has no valid Unit!");
      }
    }
    return lengthUnit;
  }

  /**
   * Returns the value of the given length as int.
   *
   * @param length the <code>int</code> value should be obtained from
   * @return Returns a <code>int</code> value representing the specified Length value.
   */
  public static int parseInt(String length) {
    return (int) parseDouble(length, null);
  }

  /**
   * Returns the value of the given length as int.
   *
   * @param length the <code>int</code> value should be obtained from
   * @param destinationUnit The unit to be converted to
   * @return Returns a <code>int</code> value representing the specified Length value.
   */
  public static int parseInt(String length, Unit destinationUnit) {
    return (int) parseDouble(length, destinationUnit);
  }

  /**
   * Returns the value of the given length as long.
   *
   * @param length the <code>long</code> value should be obtained from
   * @return Returns a <code>long</code> value representing the specified Length value.
   */
  public static long parseLong(String length) {
    return (long) parseDouble(length, null);
  }

  /**
   * Maps the a length string to a different unit
   *
   * @param length The value to be mapped
   * @param destinationUnit The unit to be converted to
   * @return The converted value without unit suffix as Double
   */
  public static long parseLong(String length, Unit destinationUnit) {
    return (long) parseDouble(length, destinationUnit);
  }

  /**
   * Returns the value of the given length as double.
   *
   * @param length the <code>double</code> value should be obtained from
   * @return Returns a <code>double</code> value representing the specified Length value.
   */
  public static double parseDouble(String length) {
    return parseDouble(length, null);
  }

  /**
   * Maps the a length string to a different unit
   *
   * @param length The value to be mapped
   * @param destinationUnit The unit to be converted to
   * @return The converted value without unit suffix as double
   */
  public static double parseDouble(String length, Unit destinationUnit) {
    double newValue = 0;
    if (length != null) {
      double roundingFactor = 10000.0;
      boolean identifiedInput = false;

      for (Unit unit : Unit.values()) {
        if (length.contains(unit.abbr())) {
          Double value = Double.valueOf(length.substring(0, length.indexOf(unit.abbr())));
          // if no destination unit was given the unit remains the same
          if (destinationUnit != null) {
            // using roundfactor proved to be more precise when used with Java XSLT processor
            newValue =
                Math.round(
                        roundingFactor
                            * value
                            / destinationUnit.unitInMillimiter()
                            * unit.mUnitInMillimiter)
                    / roundingFactor;
          } else {
            destinationUnit = unit;
          }
          identifiedInput = true;
          break;
        }
      }
      if (!identifiedInput) {
        LOG.warning(
            "The unit "
                + length
                + " could not be transformed to "
                + destinationUnit
                + "!");
      }
    } else {
      LOG.warning("The input length should not be null!");
    }
    return newValue;
  }

  /**
   * @param destinationUnit The unit to be converted to
   * @return The converted value as result
   */
  public String mapToUnit(Unit destinationUnit) {
    return mapToUnit(mLengthString, destinationUnit);
  }

  /** @return The length in Millimeter */
  public Double getMillimeters() {
    return getLength(this.mLengthString, Unit.MILLIMETER);
  }

  /**
   * WARNING: Not an allowed ODF value, just for interim calculation used!
   *
   * @return The length in Micrometer
   */
  public Double getMicrometer() {
    return getLength(this.mLengthString, Unit.MICROMETER);
  }

  /** @return The length in point */
  public Double getPoint() {
    return getLength(this.mLengthString, Unit.POINT);
  }

  /** @return The length in pixel */
  public Double getPixel() {
    return getLength(this.mLengthString, Unit.PIXEL);
  }

  /** @return The length in pica */
  public Double getPica() {
    return getLength(this.mLengthString, Unit.PICA);
  }

  /** @return The length in inch */
  public Double getInch() {
    return getLength(this.mLengthString, Unit.INCH);
  }

  /** @return The length in centimeter */
  public Double getCentimeter() {
    return getLength(this.mLengthString, Unit.CENTIMETER);
  }

  /**
   * Maps the a length string to a different unit
   *
   * @param length The value to be mapped
   * @param destinationUnit The unit to be converted to
   * @return The converted value without unit suffix as Integer
   */
  public static Double getLength(String length, Unit destinationUnit) {
    double newValue = 0.0;
    if (length != null) {
      double roundingFactor = 10000.0;
      boolean identifiedInput = false;

      for (Unit unit : Unit.values()) {
        if (length.contains(unit.abbr())) {
          Double value = Double.valueOf(length.substring(0, length.indexOf(unit.abbr())));
          // if no destination unit was given the unit remains the same
          if (destinationUnit != null) {
            // using roundfactor proved to be more precise when used with Java XSLT processor
            newValue =
                Math.round(
                        roundingFactor
                            * value
                            / destinationUnit.unitInMillimiter()
                            * unit.mUnitInMillimiter)
                    / roundingFactor;
          } else {
            destinationUnit = unit;
          }
          identifiedInput = true;
          break;
        }
      }
      if (!identifiedInput) {
        LOG.warning(
            "The unit "
                + length
                + " could not be transformed to "
                + destinationUnit
                + "!");
      }
    } else {
      LOG.warning("The input length should not be null!");
    }
    return newValue;
  }

  /**
   * Maps the a length string to a different unit
   *
   * @param length The value to be mapped
   * @param destinationUnit The unit to be converted to
   * @return The converted value with unit suffix as String
   */
  public static String mapToUnit(String length, Unit destinationUnit) {
    Double newValue = getLength(length, destinationUnit);
    return String.valueOf(newValue) + destinationUnit.abbr();
  }

  /**
   * Returns a Length instance representing the specified String value
   *
   * @param stringValue a String value
   * @return return a Length instance representing stringValue
   * @throws NumberFormatException if the given argument is not a valid Length
   */
  public static Length valueOf(String stringValue) throws NumberFormatException {
    return new Length(stringValue);
  }

  /**
   * Returns a String Object representing this Length's value
   *
   * @return return a string representation of the value of this Length object
   */
  @Override
  public String toString() {
    return mLengthString;
  }
}
