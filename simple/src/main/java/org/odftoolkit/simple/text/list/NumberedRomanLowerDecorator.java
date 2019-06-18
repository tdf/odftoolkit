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

package org.odftoolkit.simple.text.list;

import org.odftoolkit.simple.Document;

/**
 * NumberDecorator is an implementation of the ListDecorator interface,
 * decorates a given List as number list. User can extend this class and realize
 * their own list and list item style. For example, set a specifies list item
 * with red color.
 * <p>
 * A NumberDecorator can be reused in the same Document.
 *
 * @since 0.4
 */
public class NumberedRomanLowerDecorator extends NumberDecoratorBase {

	/**
	 * Constructor with Document.
	 * @param doc
	 *            the Document which this NumberDecorator will be used on.
	 */
	public NumberedRomanLowerDecorator(Document doc) {
	    	super(doc, "Simple_Default_roman_lower_List", "Numbering_20_Symbols", "i", ")", null);
	}
}
