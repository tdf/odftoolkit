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

package org.odftoolkit.simple.common.field;

import org.odftoolkit.simple.Component;

/**
 * This is the abstract class of all kinds of fields.
 * 
 * @see org.odftoolkit.simple.common.navigation.FieldSelection
 * 
 * @since 0.5
 */
public abstract class Field extends Component {
	
	/**
	 * A <tt>FieldType</tt> specifies the type of a field.
	 * 
	 * @since 0.5
	 */
	public static enum FieldType {
		DATE_FIELD,
		FIXED_DATE_FIELD,
		TIME_FIELD,
		FIXED_TIME_FIELD,
		PREVIOUS_PAGE_NUMBER_FIELD,
		CURRENT_PAGE_NUMBER_FIELD,
		NEXT_PAGE_NUMBER_FIELD,
		PAGE_COUNT_FIELD,
		TITLE_FIELD,
		SUBJECT_FIELD,
		AUTHOR_NAME_FIELD,
		AUTHOR_INITIALS_FIELD,
		CHAPTER_FIELD,
		REFERENCE_FIELD,
		SIMPLE_VARIABLE_FIELD,
		USER_VARIABLE_FIELD,
		CONDITION_FIELD,
		HIDDEN_TEXT_FIELD;
	}
	
	/**
	 * Return the type of this field.
	 * 
	 * @return the type of this field.
	 */
	public abstract FieldType getFieldType();
}
