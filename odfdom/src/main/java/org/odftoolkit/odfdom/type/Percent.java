/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ************************************************************************/
package org.odftoolkit.odfdom.type;

import java.util.regex.Pattern;

/**
 * This class represents the in OpenDocument format used data type {@odf.datatype percent}
 */
public class Percent implements OdfFieldDataType, OdfDataType {

	private double mN;
	private static final Pattern percentPattern = Pattern.compile("^-?([0-9]+(\\.[0-9]*)?|\\.[0-9]+)%$");

	/**
	 * Allocates a Percent object representing the n argument
	 *
	 * @param n
	 *            the value of the Percent
	 * @throws IllegalArgumentException if the given argument is not a valid Percent
	 */
	public Percent(double n) throws IllegalArgumentException {
		mN = n;
	}

	/**
	 * Returns a String Object representing this Percent's value
	 *
	 * @return return a string representation of the value of this Percent
	 *         object
	 */
	@Override
	public String toString() {
		return Double.toString(mN * 100) + "%";
	}

	/**
	 * Returns a Percent instance representing the specified String value
	 *
	 * @param stringValue
	 *            a String value
	 * @return return a Percent instance representing stringValue
	 * @throws IllegalArgumentException if the given argument is not a valid Percent
	 */
	public static Percent valueOf(String stringValue)
			throws IllegalArgumentException {
		if ((stringValue == null) || (stringValue.length() == 0)) {
			return new Percent(0.0);
		}

		int n = stringValue.indexOf("%");
		if (n != -1) {
			return new Percent(Double.valueOf(stringValue.substring(0, n)).doubleValue() / 100);
		} else {
			throw new IllegalArgumentException("parameter is invalid for datatype Percent");
		}
	}

	/**
	 * Returns the value of this Percent object as a double primitive
	 *
	 * @return the primitive double value of this Percent object.
	 */
	public double doubleValue() {
		return mN;
	}

	/**
	 * check if the specified Double instance is a valid {@odf.datatype percent} data type
	 *
	 * @param doubleValue
	 *            the value to be tested
	 * @return true if the value of argument is valid for {@odf.datatype percent} data type
	 *         false otherwise
	 */
	public static boolean isValid(Double doubleValue) {
		if (doubleValue == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * check if the specified String instance is a valid {@odf.datatype percent} data type
	 *
	 * @param stringValue
	 *            the value to be tested
	 * @return true if the value of argument is valid for {@odf.datatype percent} data type
	 *         false otherwise
	 */
	public static boolean isValid(String stringValue) {
		if ((stringValue == null) || (!percentPattern.matcher(stringValue).matches())) {
			return false;
		} else {
			return true;
		}
	}
}
