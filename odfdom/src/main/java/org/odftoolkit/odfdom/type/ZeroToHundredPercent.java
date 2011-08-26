package org.odftoolkit.odfdom.type;

import java.util.regex.Pattern;

/**
 * This class represents the in OpenDocument format used data type {@odf.datatype zeroToHundredPercent}
 */
public class ZeroToHundredPercent implements OdfDataType{
	private double mN;
	private static final Pattern zeroToHundredPercentPattern = Pattern.compile("^([0-9]?[0-9](\\.[0-9]*)?|100(\\.0*)?|\\.[0-9]+)%$");
	
	/**
	 * Allocates a ZeroToHundredPercent object representing the n argument
	 *
	 * @param n
	 *            the value of the ZeroToHundredPercent
	 * @throws IllegalArgumentException if the given argument is not a valid ZeroToHundredPercent
	 */
	public ZeroToHundredPercent(double n) throws IllegalArgumentException {
		if( n > 1 || n < 0)
			throw new IllegalArgumentException("parameter is invalid for datatype ZeroToHundredPercent");
		mN = n;
	}

	/**
	 * Returns a String Object representing this ZeroToHundredPercent's value
	 *
	 * @return return a string representation of the value of this ZeroToHundredPercent
	 *         object
	 */
	@Override
	public String toString() {
		return Double.toString(mN * 100) + "%";
	}

	/**
	 * Returns a ZeroToHundredPercent instance representing the specified String value
	 *
	 * @param stringValue
	 *            a String value
	 * @return return a ZeroToHundredPercent instance representing stringValue
	 * @throws IllegalArgumentException if the given argument is not a valid ZeroToHundredPercent
	 */
	public static ZeroToHundredPercent valueOf(String stringValue)
			throws IllegalArgumentException {
		if ((stringValue == null) || (stringValue.length() == 0)) {
			return new ZeroToHundredPercent(0.0);
		}

		int n = stringValue.indexOf("%");
		if (n != -1) {
			return new ZeroToHundredPercent(Double.valueOf(stringValue.substring(0, n)).doubleValue() / 100);
		} else {
			throw new IllegalArgumentException("parameter is invalid for datatype ZeroToHundredPercent");
		}
	}

	/**
	 * Returns the value of this ZeroToHundredPercent object as a double primitive
	 *
	 * @return the primitive double value of this ZeroToHundredPercent object.
	 */
	public double doubleValue() {
		return mN;
	}

	/**
	 * check if the specified Double instance is a valid {@odf.datatype zeroToHundredPercent} data type
	 *
	 * @param doubleValue
	 *            the value to be tested
	 * @return true if the value of argument is valid for {@odf.datatype zeroToHundredPercent} data type
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
	
	/**
	 * check if the specified String instance is a valid {@odf.datatype zeroToHundredPercent} data type
	 *
	 * @param stringValue
	 *            the value to be tested
	 * @return true if the value of argument is valid for {@odf.datatype zeroToHundredPercent} data type
	 *         false otherwise
	 */
	public static boolean isValid(String stringValue) {
		if ((stringValue == null) || (!zeroToHundredPercentPattern.matcher(stringValue).matches())) {
			return false;
		} else {
			return true;
		}
	}
}
