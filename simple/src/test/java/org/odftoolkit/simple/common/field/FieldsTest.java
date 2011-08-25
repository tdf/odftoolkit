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
package org.odftoolkit.simple.common.field;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.odfdom.dom.element.text.TextAuthorInitialsElement;
import org.odftoolkit.odfdom.dom.element.text.TextAuthorNameElement;
import org.odftoolkit.odfdom.dom.element.text.TextDateElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.dom.element.text.TextTimeElement;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.field.PageNumberField.DisplayType;
import org.odftoolkit.simple.style.NumberFormat;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class FieldsTest {

	private static String TEST_DOCUMENT = "TextFieldSampleDocument.odt";

	@Test
	public void testCreateConditionField() {
		try {
			TextDocument doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(TEST_DOCUMENT));

			// declare simple variable
			VariableField simpleVariableField = Fields.createSimpleVariableField(doc, "test_con_variable");
			OdfTextParagraph varParagraph = doc.newParagraph("test_con_variable:");
			simpleVariableField.updateField("true", varParagraph);

			// test condition field
			OdfTextParagraph newParagraph = doc.newParagraph("Condition Field Test:");
			ConditionField conditionField = Fields.createConditionField(newParagraph, "test_con_variable == \"true\"",
					"trueText", "falseText");
			Assert.assertNotNull(conditionField);

			// test hide field
			newParagraph = doc.newParagraph("Hide Text Field Test:");
			conditionField = Fields.createHiddenTextField(newParagraph, "test_con_variable == \"true\"", "hiddenText");
			Assert.assertNotNull(conditionField);
		} catch (Exception e) {
			Logger.getLogger(FieldsTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCreateVariableField() {
		try {
			TextDocument doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(TEST_DOCUMENT));
			// declare simple variable
			VariableField simpleVariableField = Fields.createSimpleVariableField(doc, "test_simple_variable");
			Assert.assertNotNull(simpleVariableField);
			TextSpanElement newTextSpanElement = doc.newParagraph("Update Simple Variable Field:").newTextSpanElement();
			simpleVariableField.updateField("simple variable content", newTextSpanElement);
			newTextSpanElement = doc.newParagraph("Show Simple Variable Field:").newTextSpanElement();
			simpleVariableField.displayField(newTextSpanElement);

			// declare user variable
			VariableField userVariableField = Fields.createUserVariableField(doc, "test_user_variable", "test");
			Assert.assertNotNull(userVariableField);
			OdfTextParagraph newParagraph = doc.newParagraph("Update User Variable Field:");
			userVariableField.updateField("user variable content", null);
			newParagraph = doc.newParagraph("Show User Variable Field:");
			userVariableField.displayField(newParagraph);
		} catch (Exception e) {
			Logger.getLogger(FieldsTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCreateReferenceField() {
		try {
			TextDocument doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(TEST_DOCUMENT));
			TextSpanElement newTextSpanElement = doc.newParagraph("Reference Content:").newTextSpanElement();
			newTextSpanElement.setTextContent("This is a test reference content.");
			ReferenceField referenceField = Fields.createReferenceField(newTextSpanElement, "test-ref");
			Assert.assertNotNull(referenceField);
			referenceField
					.appendReferenceTo(doc.newParagraph("User Reference Field:"), ReferenceField.DisplayType.TEXT);
		} catch (Exception e) {
			Logger.getLogger(FieldsTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testAddChapterField() {
		try {
			TextDocument doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(TEST_DOCUMENT));
			ChapterField chapterField = Fields.createChapterField(doc.newParagraph("Chapter:"));
			Assert.assertNotNull(chapterField);
		} catch (Exception e) {
			Logger.getLogger(FieldsTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testAddTitleOrSubjectField() {
		try {
			TextDocument doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(TEST_DOCUMENT));
			TitleField titleField = Fields.createTitleField(doc.newParagraph("The Title:"));
			Assert.assertNotNull(titleField);
			Assert.assertEquals("document title", titleField.getOdfElement().getTextContent());

			SubjectField subjectField = Fields.createSubjectField(doc.newParagraph("The Subject:"));
			Assert.assertNotNull(subjectField);
			Assert.assertEquals("document subject", subjectField.getOdfElement().getTextContent());
		} catch (Exception e) {
			Logger.getLogger(FieldsTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testAddAuthorField() {
		try {
			TextDocument doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(TEST_DOCUMENT));
			AuthorField authorField = Fields
					.createAuthorInitialsField(doc.newParagraph("The initials of the author :"));
			Assert.assertNotNull(authorField);
			Assert.assertTrue(authorField.getOdfElement() instanceof TextAuthorInitialsElement);

			authorField = Fields.createAuthorNameField(doc.newParagraph("Author:"));
			Assert.assertNotNull(authorField);
			Assert.assertTrue(authorField.getOdfElement() instanceof TextAuthorNameElement);
		} catch (Exception e) {
			Logger.getLogger(FieldsTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testAddPageNumberField() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			PageNumberField numberField = Fields.createCurrentPageNumberField(doc.newParagraph("Current Page Number:"));
			Assert.assertNotNull(numberField);
			numberField.setNumberFormat(NumberFormat.UPPERCASE_LATIN_ALPHABET);
			String format = numberField.getOdfElement().getStyleNumFormatAttribute();
			Assert.assertEquals("A", format);
			numberField.setDisplayPage(DisplayType.NEXT_PAGE);
			String type = numberField.getOdfElement().getTextSelectPageAttribute();
			Assert.assertEquals("next", type);

			numberField = Fields.createPreviousPageNumberField(doc.newParagraph("Previous Page Number:"));
			type = numberField.getOdfElement().getTextSelectPageAttribute();
			Assert.assertEquals("previous", type);
			numberField = Fields.createNextPageNumberField(doc.newParagraph("Next Page Number:"));
			type = numberField.getOdfElement().getTextSelectPageAttribute();
			Assert.assertEquals("next", type);
		} catch (Exception e) {
			Logger.getLogger(FieldsTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testAddPageCountField() {
		try {
			TextDocument doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(TEST_DOCUMENT));
			PageCountField countField = Fields.createPageCountField(doc.newParagraph("Page Count:"));
			Assert.assertNotNull(countField);
			countField.setNumberFormat(NumberFormat.UPPERCASE_LATIN_ALPHABET);
			String format = countField.getOdfElement().getStyleNumFormatAttribute();
			Assert.assertEquals("A", format);
		} catch (Exception e) {
			Logger.getLogger(FieldsTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testAddDateField() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			DateField dateField = Fields.createDateField(doc.newParagraph("Date:"));
			Assert.assertNotNull(dateField);

			TextDateElement dateEle = dateField.getOdfElement();
			String oldContent = dateEle.getTextContent();
			SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date oldDate = oldFormat.parse(oldContent);

			String format = "yy-MM-dd";
			dateField.formatDate(format);
			String newContent = dateEle.getTextContent();
			SimpleDateFormat newFormat = new SimpleDateFormat(format);
			Date newDate = newFormat.parse(newContent);
			Assert.assertEquals(oldDate.getTime(), newDate.getTime());
		} catch (Exception e) {
			Logger.getLogger(FieldsTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testAddTimeField() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			TimeField timeField = Fields.createTimeField(doc.newParagraph("Time:"));
			Assert.assertNotNull(timeField);

			TextTimeElement timeEle = timeField.getOdfElement();
			String oldContent = timeEle.getTextContent();
			SimpleDateFormat oldFormat = new SimpleDateFormat("HH:mm:ss");
			Date oldDate = oldFormat.parse(oldContent);

			String format = "HH:mm:ss a";
			timeField.formatTime(format);
			String newContent = timeEle.getTextContent();
			SimpleDateFormat newFormat = new SimpleDateFormat(format);
			Date newDate = newFormat.parse(newContent);
			Assert.assertEquals(oldDate.getTime(), newDate.getTime());
		} catch (Exception e) {
			Logger.getLogger(FieldsTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}
}
