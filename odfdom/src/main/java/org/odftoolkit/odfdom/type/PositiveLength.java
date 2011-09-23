/************************************************************************
* 
*  Licensed to the Apache Software Foundation (ASF) under one
*  or more contributor license agreements.  See the NOTICE file
*  distributed with this work for additional information
*  regarding copyright ownership.  The ASF licenses this file
*  to you under the Apache License, Version 2.0 (the
*  "License"); you may not use this file except in compliance
*  with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*
************************************************************************/
package org.odftoolkit.odfdom.type;

import java.util.regex.Pattern;

/**
 * This class represents the in OpenDocument format used data type {@odf.datatype positiveLength}
 */
public class PositiveLength extends Length {
	private static final Pattern positiveLengthPattern = Pattern.compile("^([0-9]*[1-9][0-9]*(\\.[0-9]*)?|0+\\.[0-9]*[1-9][0-9]*|\\.[0-9]*[1-9][0-9]*)((cm)|(mm)|(in)|(pt)|(pc)|(px))$");
	
	/**
	 * Construct PositiveLength by the parsing the given string
	 *
	 * @param length
	 *            The String to be parsed into PositiveLength
	 * @throws NumberFormatException if the given argument is not a valid PostitiveLength
	 */
	public PositiveLength(String length) throws NumberFormatException {
		super(length);
		if (!isValid(length)) {
			throw new NumberFormatException(
					"parameter is invalid for datatype PositiveLength");
		}
	}


	/**
	 * Returns a PositiveLength instance representing the specified String value
	 *
	 * @param stringValue
	 *            a String value
	 * @return return a PositiveLength instance representing stringValue
	 * @throws NumberFormatException if the given argument is not a valid PostitiveLength
	 */
	public static PositiveLength valueOf(String stringValue)
			throws NumberFormatException {
		return new PositiveLength(stringValue);
	}

	/**
	 * check if the specified String instance is a valid {@odf.datatype positiveLength} data
	 * type
	 *
	 * @param stringValue
	 *            the value to be tested
	 * @return true if the value of argument is valid for {@odf.datatype positiveLength} data
	 *         type false otherwise
	 */
	public static boolean isValid(String stringValue) {
		if ((stringValue == null) || (!positiveLengthPattern.matcher(stringValue).matches())) {
			return false;
		} else {
			return true;
		}
	}
}
