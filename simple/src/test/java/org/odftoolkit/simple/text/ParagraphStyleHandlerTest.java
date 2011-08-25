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
package org.odftoolkit.simple.text;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FontStyle;
import org.odftoolkit.simple.style.StyleTypeDefinitions.TextLinePosition;

public class ParagraphStyleHandlerTest {

	private static final Logger LOGGER =  Logger.getLogger(ParagraphStyleHandlerTest.class.getName());

	@Test
	public void testGetParagraphByIndex() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Paragraph paragraph = doc.addParagraph("paragraphTest");
			ParagraphStyleHandler paragraphHandler = paragraph.getStyleHandler();
			
			paragraphHandler.setCountry("English", Document.ScriptType.WESTERN);
			
			//validate
			String country = paragraphHandler.getCountry(Document.ScriptType.WESTERN);
			Assert.assertEquals("English", country);
			
			paragraphHandler.setCountry(null, Document.ScriptType.WESTERN);
			
			//validate
			String country1 = paragraphHandler.getCountry(Document.ScriptType.WESTERN);
			
			Assert.assertNull(country1);
			
			//save
			//doc.save(ResourceUtilities.newTestOutputFile("testParagraphStyleHandler.odt"));
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testGetFont() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Paragraph paragraph = doc.addParagraph("paragraphTest");
			ParagraphStyleHandler paragraphHandler = paragraph.getStyleHandler();
			
			Font fontBase = new Font("Arial", FontStyle.ITALIC, 10, Color.BLACK, TextLinePosition.THROUGH);
			paragraphHandler.setFont(fontBase);
			//validate
			Font font = paragraphHandler.getFont(Document.ScriptType.WESTERN);
			Assert.assertEquals(fontBase, font);
			
			paragraphHandler.setFont(fontBase, Locale.CHINESE);
			//validate
			Font font1 = paragraphHandler.getFont(Document.ScriptType.WESTERN);
			Assert.assertEquals(fontBase, font1);
			
			//save
			//doc.save(ResourceUtilities.newTestOutputFile("testParagraphStyleHandler.odt"));
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testGetLanguage() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Paragraph paragraph = doc.addParagraph("paragraphTest");
			ParagraphStyleHandler paragraphHandler = paragraph.getStyleHandler();
			
			paragraphHandler.setLanguage("English", Document.ScriptType.WESTERN);
			
			//validate
			String language = paragraphHandler.getLanguage(Document.ScriptType.WESTERN);
			
			Assert.assertEquals("English", language);
			
			//save
			//doc.save(ResourceUtilities.newTestOutputFile("testParagraphStyleHandler.odt"));
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}
}
