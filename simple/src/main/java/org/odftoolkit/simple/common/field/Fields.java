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

import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.simple.common.field.PageNumberField.DisplayType;
import org.odftoolkit.simple.common.field.VariableField.VariableType;

/**
 * This is a tool class to help the user creating all kinds of fields as needed.
 * 
 * @see org.odftoolkit.simple.common.navigation.FieldSelection
 * @since 0.5
 */
public class Fields {

	/**
	 * Create an automatically update date field for the specific OdfElement,
	 * which displays current date.
	 * 
	 * @param odfElement
	 *            the OdfElement which owns this field.
	 * @return the created date field.
	 */
	public static DateField createDateField(OdfElement odfElement) {
		DateField dateField = new DateField(odfElement);
		dateField.setFixed(false);
		return dateField;
	}

	/**
	 * Create a fixed date field for the specific OdfElement, which displays the
	 * field created date.
	 * 
	 * @param odfElement
	 *            the OdfElement which owns this field.
	 * @return the created date field.
	 */
	public static DateField createFixedDateField(OdfElement odfElement) {
		return new DateField(odfElement);
	}

	/**
	 * Create an automatically update time field for the specific OdfElement,
	 * which displays current time.
	 * 
	 * @param odfElement
	 *            the OdfElement which owns this field.
	 * @return the created time field.
	 */
	public static TimeField createTimeField(OdfElement odfElement) {
		TimeField timeField = new TimeField(odfElement);
		timeField.setFixed(false);
		return timeField;
	}

	/**
	 * Create a fixed time field for the specific OdfElement, which displays the
	 * field created time.
	 * 
	 * @param odfElement
	 *            the OdfElement which owns this field.
	 * @return the created time field.
	 */
	public static TimeField createFixedTimeField(OdfElement odfElement) {
		return new TimeField(odfElement);
	}

	/**
	 * Create a page number field for the specific OdfElement, which displays
	 * previous page number.
	 * 
	 * @param odfElement
	 *            the OdfElement which owns this field.
	 * @return the created page number field.
	 */
	public static PageNumberField createPreviousPageNumberField(OdfElement odfElement) {
		PageNumberField pageNumberField = new PageNumberField(odfElement);
		pageNumberField.setDisplayPage(DisplayType.PREVIOUS_PAGE);
		return pageNumberField;
	}

	/**
	 * Create a page number field for the specific OdfElement, which displays
	 * current page number.
	 * 
	 * @param odfElement
	 *            the OdfElement which owns this field.
	 * @return the created page number field.
	 */
	public static PageNumberField createCurrentPageNumberField(OdfElement odfElement) {
		return new PageNumberField(odfElement);
	}

	/**
	 * Create a page number field for the specific OdfElement, which displays
	 * next page number.
	 * 
	 * @param odfElement
	 *            the OdfElement which owns this field.
	 * @return the created page number field.
	 */
	public static PageNumberField createNextPageNumberField(OdfElement odfElement) {
		PageNumberField pageNumberField = new PageNumberField(odfElement);
		pageNumberField.setDisplayPage(DisplayType.NEXT_PAGE);
		return pageNumberField;
	}

	/**
	 * Create a page count field for the specific OdfElement, which displays
	 * page total count of this document.
	 * 
	 * @param odfElement
	 *            the OdfElement which owns this field.
	 * @return the created page count field.
	 */
	public static PageCountField createPageCountField(OdfElement odfElement) {
		return new PageCountField(odfElement);
	}

	/**
	 * Create a title field for the specific OdfElement, which displays title
	 * data of this document.
	 * 
	 * @param odfElement
	 *            the OdfElement which owns this field.
	 * @return the created title field.
	 */
	public static TitleField createTitleField(OdfElement odfElement) {
		return new TitleField(odfElement);
	}

	/**
	 * Create a subject field for the specific OdfElement, which displays the
	 * subject data of this document.
	 * 
	 * @param odfElement
	 *            the OdfElement which owns this field.
	 * @return the created author field.
	 */
	public static SubjectField createSubjectField(OdfElement odfElement) {
		return new SubjectField(odfElement);
	}

	/**
	 * Create an author field for the specific OdfElement, which displays author
	 * full name of this document.
	 * 
	 * @param odfElement
	 *            the OdfElement which owns this field.
	 * @return the created author field.
	 */
	public static AuthorField createAuthorNameField(OdfElement odfElement) {
		return new AuthorField(odfElement, false);
	}

	/**
	 * Create an author field for the specific OdfElement, which displays the
	 * initials of the author of this document.
	 * 
	 * @param odfElement
	 *            the OdfElement which owns this field.
	 * @return the created author field.
	 */
	public static AuthorField createAuthorInitialsField(OdfElement odfElement) {
		return new AuthorField(odfElement, true);
	}

	/**
	 * Create a chapter field for the specific OdfElement.
	 * 
	 * @param odfElement
	 *            the OdfElement which owns this field.
	 * @return the created chapter field.
	 */
	public static ChapterField createChapterField(OdfElement odfElement) {
		return new ChapterField(odfElement);
	}

	/**
	 * Create a ReferenceField for the specific OdfElement.
	 * 
	 * @param odfElement
	 *            the OdfElement which owns this field.
	 * @param referenceName
	 *            the reference field name.
	 * @return the created reference field.
	 */
	public static ReferenceField createReferenceField(OdfElement odfElement, String referenceName) {
		return new ReferenceField(odfElement, referenceName);
	}

	/**
	 * Declare a simple variable field. Simple variables, can take different
	 * values at different positions throughout a document.
	 * <p>
	 * Simple variables can be used to display different text in recurring
	 * elements, such as headers or footers.
	 * 
	 * @param container
	 *            the container which this variable field is contained.
	 * @param name
	 *            the name of this variable field.
	 * @return the created variable field.
	 */
	public static VariableField createSimpleVariableField(VariableContainer container, String name) {
		return new VariableField(container, name, VariableType.SIMPLE);
	}

	/**
	 * Declare a user variable field. User variables have the same value
	 * throughout a document. If a user variable is set anywhere within the
	 * document, all fields in the document that display the user variable have
	 * the same value.
	 * 
	 * @param container
	 *            the container which this variable field is contained.
	 * @param name
	 *            the name of this variable field.
	 * @param value
	 *            the initial value of this variable field.
	 * @return the created variable field.
	 */
	public static VariableField createUserVariableField(VariableContainer container, String name, String value) {
		VariableField field = new VariableField(container, name, VariableType.USER);
		field.updateField(value, (OdfElement) null);
		return field;
	}

	/**
	 * Declare a condition field, which specifies a condition for display of one
	 * text string or another. If the condition is true, one of the text strings
	 * is displayed. If the condition is false, the other text string is
	 * displayed.
	 * 
	 * @param odfElement
	 *            the OdfElement which owns this field.
	 * @param condition
	 *            the condition that determines which of the two text strings is
	 *            displayed.
	 * @param trueText
	 *            the text string to display if a <code>condition</code> is
	 *            true.
	 * @param falseText
	 *            the text string to display if a <code>condition</code> is
	 *            false.
	 * @return the created condition field.
	 */
	public static ConditionField createConditionField(OdfElement odfElement, String condition, String trueText,
			String falseText) {
		return new ConditionField(odfElement, condition, trueText, falseText, false);
	}

	/**
	 * Declare a hidden text field, which hides the text it contains when a
	 * specified condition is true.
	 * 
	 * @param odfElement
	 *            the OdfElement which owns this field.
	 * @param condition
	 *            the condition that determines whether the text string is
	 *            displayed or not.
	 * @param text
	 *            the text string to display.
	 * @return the created condition field.
	 */
	public static ConditionField createHiddenTextField(OdfElement odfElement, String condition, String text) {
		return new ConditionField(odfElement, condition, null, text, true);
	}

	// private constructor, cannot instantiate.
	private Fields() {
	};
}
