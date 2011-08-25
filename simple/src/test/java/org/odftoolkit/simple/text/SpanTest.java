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

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.navigation.TextNavigation;
import org.odftoolkit.simple.common.navigation.TextSelection;
import org.odftoolkit.simple.style.DefaultStyleHandler;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FontStyle;
import org.odftoolkit.simple.style.StyleTypeDefinitions.TextLinePosition;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class SpanTest {

	@Test
	public void testSpan() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			doc.addParagraph("This is a test paragraph!");
			TextNavigation navigation = new TextNavigation("test", doc);
			TextSelection sel = (TextSelection) navigation.nextSelection();
			Span span = Span.newSpan(sel);
			TextHyperlink link = span.applyHyperlink(new URI("http://www.ibm.com"));
			DefaultStyleHandler handler = span.getStyleHandler();
			Font font1Base = new Font("Arial", FontStyle.ITALIC, 10, Color.BLACK, TextLinePosition.THROUGH);
			handler.getTextPropertiesForWrite().setFont(font1Base);
			doc.save(ResourceUtilities.newTestOutputFile("spantest.odt"));

			String content = span.getTextContent();
			Assert.assertEquals("test", content);
			span.setTextContent("new test");
			Assert.assertEquals("new test", span.getTextContent());
			doc.save(ResourceUtilities.newTestOutputFile("spantest.odt"));

		} catch (Exception e) {
			Logger.getLogger(SpanTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}

	}
}
