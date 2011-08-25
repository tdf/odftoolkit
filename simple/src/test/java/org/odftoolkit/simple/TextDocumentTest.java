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
package org.odftoolkit.simple;

import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.office.OfficeAnnotationElement;
import org.odftoolkit.odfdom.dom.element.style.StyleParagraphPropertiesElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.utils.ResourceUtilities;
import org.w3c.dom.Node;

public class TextDocumentTest {
	
	@Test
	public void testAddPageBreak() {
		try {
			// test new creation document.
			TextDocument newDoc = TextDocument.newTextDocument();
			Paragraph paragraph = newDoc.addParagraph("before page break");
			newDoc.addPageBreak();
			validPageBreakExist(newDoc, paragraph);
			Paragraph refParagraph = newDoc.addParagraph("after page break");
			newDoc.addParagraph("end page");
			newDoc.addPageBreak(refParagraph);
			validPageBreakExist(newDoc, refParagraph);
			newDoc.save(ResourceUtilities.newTestOutputFile("AddPageBreakOutput.odt"));

			// test exist document.
			TextDocument existDoc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("test2.odt"));
			paragraph = existDoc.addParagraph("before page break");
			existDoc.addPageBreak();
			validPageBreakExist(existDoc, paragraph);
			refParagraph = existDoc.getParagraphByIndex(0, true);
			existDoc.addPageBreak(refParagraph);
			validPageBreakExist(existDoc, refParagraph);
			existDoc.save(ResourceUtilities.newTestOutputFile("test2Out.odt"));
			
		} catch (Exception e) {
			Logger.getLogger(TextDocumentTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}
	}
	
	@Test
	public void testAddComment() {
		try {
			// test new creation document.
			TextDocument newDoc = TextDocument.newTextDocument();
			
			Paragraph paragraph = newDoc.addParagraph("Paragraph1");
			paragraph.addComment("This is a comment for Paragraph1", "Simple ODF");
			Node firstChildNode = paragraph.getOdfElement().getFirstChild();
			Assert.assertTrue(firstChildNode instanceof OfficeAnnotationElement);
			OfficeAnnotationElement comment = (OfficeAnnotationElement) firstChildNode;
			Assert.assertEquals("Simple ODF", comment.getFirstChild().getTextContent());
			Assert.assertEquals("This is a comment for Paragraph1", comment.getLastChild().getTextContent());
			Assert.assertTrue(firstChildNode instanceof OfficeAnnotationElement);
			
			paragraph = newDoc.addParagraph("Paragraph2");
			paragraph.addComment("This is a comment for Paragraph2", null);
			firstChildNode = paragraph.getOdfElement().getFirstChild();
			Assert.assertTrue(firstChildNode instanceof OfficeAnnotationElement);
			comment = (OfficeAnnotationElement) firstChildNode;
			Assert.assertEquals(System.getProperty("user.name"), comment.getFirstChild().getTextContent());
			Assert.assertEquals("This is a comment for Paragraph2", comment.getLastChild().getTextContent());
			
			newDoc.save(ResourceUtilities.newTestOutputFile("AddCommentOutput.odt"));
		} catch (Exception e) {
			Logger.getLogger(TextDocumentTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}
	}
	
	private void validPageBreakExist(TextDocument newDoc, Paragraph paragraph) throws Exception {
		Node paragraphNode = paragraph.getOdfElement().getNextSibling();
		Assert.assertTrue(paragraphNode instanceof TextPElement);
		OdfContentDom contentDocument = newDoc.getContentDom();
		OdfOfficeAutomaticStyles styles = contentDocument.getAutomaticStyles();
		OdfStyle style = styles.getStyle(((TextPElement) paragraphNode).getStyleName(), OdfStyleFamily.Paragraph);
		Assert.assertNotNull(style);
		Node paragraphPropertiesNode = style.getFirstChild();
		Assert.assertNotNull(paragraphPropertiesNode instanceof StyleParagraphPropertiesElement);
		Assert.assertEquals(((StyleParagraphPropertiesElement) paragraphPropertiesNode).getFoBreakBeforeAttribute(),
				"page");
	}
}
