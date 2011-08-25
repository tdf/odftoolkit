/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2011 IBM. All rights reserved.
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
package org.odftoolkit.simple.chart;

/**
 * The value set of chart type.
 * 
 * @since 0.6
 */
public enum ChartType {

	/**
	 * BAR, The bar type chart
	 */
	BAR("chart:bar"),
	/**
	 * PIE, The pie type chart
	 */
	PIE("chart:circle"),
	/**
	 * RING, The ring type chart
	 */
	RING("chart:ring"),
	/**
	 * AREA, The area type chart
	 */
	AREA("chart:area"),
	/**
	 * LINE, The line type chart
	 */
	LINE("chart:line"),
	/**
	 * SCATTER, The scatter type chart
	 */
	SCATTER("chart:scatter"),
	/**
	 * RADAR, The radar type chart
	 */
	RADAR("chart:radar"),
	/**
	 * STOCK, The stock type chart
	 */
	STOCK("chart:stock");

	private String m_aValue;

	private ChartType(String _aValue) {
		m_aValue = _aValue;
	}
	

	@Override
	public String toString() {
		return m_aValue;
	}

	/**
	 * Returns the enum of ChartType string.
	 * 
	 * @param mString
	 *            the string value
	 * @return the enum of ChartType.
	 */
	public static ChartType enumValueOf(String mString) {
		for (ChartType aIter : values()) {
			if (mString.equals(aIter.toString())) {
				return aIter;
			}
		}
		return null;
	}
}
