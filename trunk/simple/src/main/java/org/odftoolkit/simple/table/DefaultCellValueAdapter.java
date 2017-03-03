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

package org.odftoolkit.simple.table;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This is a default realization of {@link CellValueAdapter CellValueAdapter}.
 * It will adapt cell string content as common rule.
 * 
 * @see CellValueAdapter
 * 
 * @since 0.3
 */
public class DefaultCellValueAdapter implements CellValueAdapter{
	
	/**
	 * The default date format of table cell.
	 */
	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	
	/**
	 * The default time format of table cell.
	 */
	private static final String DEFAULT_TIME_FORMAT = "'PT'HH'H'mm'M'ss'S'";
	
	/**
	 * The default percent format of table cell.
	 */
	private static final String DEFAULT_PERCENT_FORMAT = "0%";
	
	/**
	 * The default currency format of table cell.
	 */
	private static final String DEFAULT_CURRENCY_FORMAT = "$#,##0.00;-$#,##0.00";
	
	/* (non-Javadoc)
	 * @see org.odftoolkit.simple.table.CellValueAdapter#adaptValue()
	 */
	public void adaptValue(Cell cell, String value){
		String type = cell.getValueType();
		String formatStr = cell.getFormatString();
		if ("boolean".equals(type)) {
			if ("true".equalsIgnoreCase(value)) {
				cell.setBooleanValue(Boolean.TRUE);
			} else if ("false".equalsIgnoreCase(value)) {
				cell.setBooleanValue(Boolean.FALSE);
			} else {
				cell.setValueType("string");
				adaptValue(cell, value);
			}
		} else if ("currency".equals(type)) {
			if (formatStr == null) {
				formatStr = DEFAULT_CURRENCY_FORMAT;
			}
			try {
				DecimalFormat decimalFormat = new DecimalFormat(formatStr);
				Number number = decimalFormat.parse(value);
				cell.setCurrencyValue(number.doubleValue(), cell.getCurrencyCode());
				cell.setCurrencyFormat(cell.getCurrencySymbol(), formatStr);
			} catch (ParseException e) {
				cell.setValueType("string");
				adaptValue(cell, value);
			}
		} else if ("date".equals(type)) {
			if (formatStr == null) {
				formatStr = DEFAULT_DATE_FORMAT;
			}
			try {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr);
				Date date = simpleDateFormat.parse(value);
				Calendar calender = Calendar.getInstance();
				calender.setTime(date);
				cell.setDateValue(calender);
			} catch (ParseException e) {
				cell.setValueType("string");
				adaptValue(cell, value);
			}
		} else if ("float".equals(type)) {
			try {
				if (formatStr != null) {
					DecimalFormat decimalFormat = new DecimalFormat(formatStr);
					Number number = decimalFormat.parse(value);
					cell.setDoubleValue(number.doubleValue());
				} else {
					Double doubleValue = Double.parseDouble(value);
					cell.setDoubleValue(doubleValue);
				}
			} catch (ParseException e) {
				cell.setValueType("string");
				adaptValue(cell, value);
			} catch (NumberFormatException e) {
				cell.setValueType("string");
				adaptValue(cell, value);
			}
		} else if ("percentage".equals(type)) {
			if (formatStr == null) {
				formatStr = DEFAULT_PERCENT_FORMAT;
			}
			try {
				DecimalFormat decimalFormat = new DecimalFormat(formatStr);
				Number number = decimalFormat.parse(value);
				cell.setPercentageValue(number.doubleValue());
			} catch (ParseException e) {
				cell.setValueType("string");
				adaptValue(cell, value);
			}
		} else if ("time".equals(type)) {
			if (formatStr == null) {
				formatStr = DEFAULT_TIME_FORMAT;
			}
			try {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr);
				Date time = simpleDateFormat.parse(value);
				Calendar calender = Calendar.getInstance();
				calender.setTime(time);
				cell.setTimeValue(calender);
			} catch (ParseException e) {
				cell.setValueType("string");
				adaptValue(cell, value);
			}
		} else {
			// for string and void
			// adapt boolean
			if ("true".equalsIgnoreCase(value)) {
				cell.setBooleanValue(Boolean.TRUE);
			} else if ("false".equalsIgnoreCase(value)) {
				cell.setBooleanValue(Boolean.FALSE);
			} else {
				// adapt date
				String[] dateFormats = { "MM/dd/yyyy", "MMM d, yyyy", "yyyy-MM-dd", "MM/dd/yy" };
				for (String dateFormat : dateFormats) {
					try {
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
						Date date = simpleDateFormat.parse(value);
						Calendar calender = Calendar.getInstance();
						calender.setTime(date);
						cell.setDateValue(calender);
						cell.setFormatString(dateFormat);
						return;
					} catch (ParseException e) {
						continue;
					}
				}
				// adapt time
				String[] timeFormats = { "MM/dd/yyyy HH:mm:ss", "yyyy-MM-dd HH:mm:ss", "HH:mm:ss", "HH:mm a", "HH:mm"};
				for (String timeFormat : timeFormats) {
					try {
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat);
						Date time = simpleDateFormat.parse(value);
						Calendar calender = Calendar.getInstance();
						calender.setTime(time);
						cell.setTimeValue(calender);
						cell.setFormatString(timeFormat);
						return;
					} catch (ParseException e) {
						continue;
					}
				}
				// adapt percent
				String[] percentFormats = { "0.00%"/*, "0%"*/ };
				for (String percentFormat : percentFormats) {
					try {
						DecimalFormat decimalFormat = new DecimalFormat(percentFormat);
						Number number = decimalFormat.parse(value);
						cell.setPercentageValue(number.doubleValue());
						cell.setFormatString(percentFormat);
						return;
					} catch (ParseException e) {
						continue;
					}
				}
				// adapt float
				String[] floatFormats = { /*"#,###.00", "#,##0.00", */"#,##0"/*, "0.00", "0" */};
				for (String floatStr : floatFormats) {
					try {
						DecimalFormat decimalFormat = new DecimalFormat(floatStr);
						Number number = decimalFormat.parse(value);
						cell.setDoubleValue(number.doubleValue());
						cell.setFormatString(floatStr);
						return;
					} catch (ParseException e) {
						continue;
					}
				}
				//TODO: adapt currency
				// adapt string
				cell.setStringValue(value);
			}
		}
	}
}
