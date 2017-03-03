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

package org.odftoolkit.simple.style;

/**
 * A <tt>NumberFormat</tt> specifies a numbering sequence.
 * 
 * @since 0.5
 */
public enum NumberFormat {

	HINDU_ARABIC_NUMBER("1"), 
	LOWERCASE_LATIN_ALPHABET("a"), 
	UPPERCASE_LATIN_ALPHABET("A"),
	LOWERCASE_ROMAN_NUMBER("i"),
	UPPERCASE_ROMAN_NUMBER("I");
	
	private final String numberFormat;

	NumberFormat(String format) {
		numberFormat = format;
	}

	@Override
	public String toString() {
		return numberFormat;
	}
}
