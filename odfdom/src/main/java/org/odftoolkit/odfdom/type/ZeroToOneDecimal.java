/* 
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package org.odftoolkit.odfdom.type;

/**
 * This class represents the in OpenDocument format used data type {@odf.datatype zeroToOneDecimal}
 */
public class ZeroToOneDecimal implements OdfDataType{
	private double mN;
	
	/**
	 * Allocates a ZeroToOneDecimal object representing the n argument
	 *
	 * @param n
	 *            the value of the ZeroToOneDecimal
	 * @throws IllegalArgumentException if the given argument is not a valid ZeroToOneDecimal
	 */
	public ZeroToOneDecimal(double n) throws IllegalArgumentException {
		if( n > 1 || n < 0)
			throw new IllegalArgumentException("parameter is invalid for datatype ZeroToOneDecimal");
		mN = n;
	}

	/**
	 * Returns a String Object representing this ZeroToOneDecimal's value
	 *
	 * @return return a string representation of the value of this ZeroToOneDecimal
	 *         object
	 */
	@Override
	public String toString() {
		return Double.toString(mN);
	}

	/**
	 * Returns a ZeroToOneDecimal instance representing the specified String value
	 *
	 * @param stringValue
	 *            a String value
	 * @return return a ZeroToOneDecimal instance representing stringValue
	 * @throws IllegalArgumentException if the given argument is not a valid ZeroToOneDecimal
	 */
	public static ZeroToOneDecimal valueOf(String stringValue)
			throws IllegalArgumentException {
		String aTmp = stringValue.trim();
		double n = Double.valueOf(aTmp);
		return new ZeroToOneDecimal(n);
	}

	/**
	 * Returns the value of this ZeroToOneDecimal object as a double primitive
	 *
	 * @return the primitive double value of this ZeroToOneDecimal object.
	 */
	public double doubleValue() {
		return mN;
	}

	/**
	 * check if the specified Double instance is a valid {@odf.datatype zeroToOneDecimal} data type
	 *
	 * @param doubleValue
	 *            the value to be tested
	 * @return true if the value of argument is valid for {@odf.datatype zeroToOneDecimal} data type
	 *         false otherwise
	 */
	public static boolean isValid(Double doubleValue) {
		if ( (doubleValue != null) && (doubleValue.doubleValue() <= 1) && 
				(doubleValue.doubleValue() >= 0)) {
			return true;
		} else {
			return false;
		}
	}
}
