/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2010 IBM. All rights reserved.
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
package org.odftoolkit.simple.style;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.style.StyleTypeDefinitions.HorizontalAlignmentType;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class ParagraphPropertiesTest {
	
	private static final Logger LOGGER =  Logger.getLogger(ParagraphPropertiesTest.class.getName());
	static final String filename = "testGetCellAt.ods";

	@Test
	public void testGetSetBorder() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Paragraph paragraph1 = doc.addParagraph("paragraph1");
			
			paragraph1.setHorizontalAlignment(HorizontalAlignmentType.JUSTIFY);
			HorizontalAlignmentType align = paragraph1.getHorizontalAlignment();
			Assert.assertEquals(HorizontalAlignmentType.JUSTIFY, align);

			paragraph1.setHorizontalAlignment(HorizontalAlignmentType.LEFT);
			align = paragraph1.getHorizontalAlignment();
			Assert.assertEquals(HorizontalAlignmentType.LEFT, align);

			paragraph1.setHorizontalAlignment(HorizontalAlignmentType.RIGHT);
			align = paragraph1.getHorizontalAlignment();
			Assert.assertEquals(HorizontalAlignmentType.RIGHT, align);

			doc.save(ResourceUtilities.newTestOutputFile("TestParagraphPropertiesSetGetHoriAlignment.odt"));
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail();
		}

	}
	
}
