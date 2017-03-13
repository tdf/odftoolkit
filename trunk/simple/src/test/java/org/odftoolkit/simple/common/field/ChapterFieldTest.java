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

import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.odfdom.dom.element.text.TextChapterElement;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.field.Field.FieldType;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class ChapterFieldTest {

	private static String TEST_DOCUMENT = "headerFooterHidden.odt";
	
	@Test
	public void testSetOutlineLevel() {
		try {
			TextDocument doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(TEST_DOCUMENT));
			ChapterField chapterField = Fields.createChapterField(doc.addParagraph("Chapter:").getOdfElement());
			chapterField.setOutlineLevel(1);
			
			TextChapterElement tchapterele = chapterField.getOdfElement();
			Integer level = tchapterele.getTextOutlineLevelAttribute();
			Assert.assertTrue(1 == level);
			
			//save
			//doc.save(ResourceUtilities.getAbsolutePath(TEST_DOCUMENT));
		} catch (Exception e) {
			Logger.getLogger(ChapterFieldTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetOdfElement() {
		try {
			TextDocument doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("headerFooterHidden.odt"));
			ChapterField chapterField = Fields.createChapterField(doc.addParagraph("Chapter:").getOdfElement());
			chapterField.setOutlineLevel(1);
			TextChapterElement tchapterEle = chapterField.getOdfElement();
			
			Assert.assertNotNull(tchapterEle);
			Assert.assertEquals("chapter", tchapterEle.getLocalName());
			Assert.assertEquals("text:chapter", tchapterEle.getNodeName());

			//save
			//doc.save(ResourceUtilities.getAbsolutePath("TextFieldSampleDocument.odt"));
			
		} catch (Exception e) {
			Logger.getLogger(ChapterFieldTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetFieldType() {
		try {
			TextDocument doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(TEST_DOCUMENT));
			ChapterField chapterField = Fields.createChapterField(doc.newParagraph("Chapter:"));
			FieldType fieldType = chapterField.getFieldType();
			Assert.assertNotNull(fieldType);
			Assert.assertEquals(FieldType.CHAPTER_FIELD, fieldType);
		} catch (Exception e) {
			Logger.getLogger(ChapterFieldTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}
}
