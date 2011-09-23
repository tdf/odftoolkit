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

package org.odftoolkit.simple.common;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class GetTextTest {

	public static final Logger LOG = Logger.getLogger(GetTextTest.class.getName());

	/**
	 * This method will invoke EditableTextExtractor to test text extraction
	 * function.
	 */
	@Test
	public void testToString() {
		try {
			Document doc = Document.loadDocument(ResourceUtilities.getTestResourceAsStream("text-extract.odt"));
			EditableTextExtractor extractor = EditableTextExtractor.newOdfEditableTextExtractor(doc);
			String output = extractor.getText();
			LOG.info(output);
			int count = 0;
			int index = output.indexOf("SIMPLE");
			while (index != -1) {
				count++;
				index = output.indexOf("SIMPLE", index + 1);
			}
			if (count != 30) {
				// there are
				// 23 SIMPLE in the /content.xml
				// 2 SIMPLE in the /styles.xml
				// 5 SIMPLE in the /Object 1/content.xml
				throw new RuntimeException("Something wrong! count=" + count);
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testReturnChar() {
		try {
			TextDocument textDoc = TextDocument.newTextDocument();
			textDoc.newParagraph();
			OdfTextParagraph graph = textDoc.newParagraph("abc");

			TextExtractor extractor = TextExtractor.newOdfTextExtractor(textDoc.getContentRoot());
			String text = extractor.getText();
			System.out.println(text);

			int count = 0;
			for (int i = 0; i < text.length(); i++)
				if (text.charAt(i) == '\r')
					count++;
			Assert.assertEquals(2, count);

			extractor = TextExtractor.newOdfTextExtractor(graph);
			text = extractor.getText();
			count = 0;
			for (int i = 0; i < text.length(); i++)
				if (text.charAt(i) == '\r')
					count++;
			Assert.assertEquals(0, count);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}
}
