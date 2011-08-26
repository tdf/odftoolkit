/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 */
package org.odftoolkit.odfdom.doc.presentation;

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.OdfElement;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.draw.OdfDrawPage;
import org.odftoolkit.odfdom.doc.office.OdfOfficePresentation;
import org.odftoolkit.odfdom.doc.office.OdfOfficePresentation.TemplatePageType;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

/**
 *
 * @author cl93746
 */
public class PresentationAPITest {

	OdfDocument odfdoc;
	private static final String PRSENTATION_FILE1 = "TestPresentationAPI.odp";
	private static final String PRSENTATION_FILE2 = "blank.odp";
	private static final String PRSENTATION_FILE3 = "presentationWithEmbedDoc.odp";
	private static final String PRSENTATION_FILE4 = "TestPresentationWithEmbedDoc.odp";

	public PresentationAPITest() {
		try {
			odfdoc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath("presentation.odp"));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testPresentation() {
		try {
			OdfOfficePresentation presentation = OdfElement.findFirstChildNode(OdfOfficePresentation.class, odfdoc.getOfficeBody());
			Assert.assertNotNull(presentation);

			int count = presentation.getPageCount();

			OdfDrawPage page = (OdfDrawPage) presentation.newDrawPageElement("default");
			page.setDrawNameAttribute("slide-insert");
			presentation.insertPageBefore(0, page);
			Assert.assertEquals(count + 1, presentation.getPageCount());
			Assert.assertEquals("slide-insert", presentation.getPageAt(0).getDrawNameAttribute());
			Assert.assertEquals("slide-name-1", presentation.getPageAt(1).getDrawNameAttribute());
			Assert.assertEquals("slide-name-2", presentation.getPageAt(2).getDrawNameAttribute());
			Assert.assertEquals("slide-name-3", presentation.getPageAt(3).getDrawNameAttribute());

			presentation.deletePage("slide-insert");
			Assert.assertEquals(count, presentation.getPageCount());
			Assert.assertEquals("slide-name-1", presentation.getPageAt(0).getDrawNameAttribute());

			presentation.insertPageAfter(2, page);
			Assert.assertEquals(count + 1, presentation.getPageCount());
			Assert.assertEquals("slide-name-1", presentation.getPageAt(0).getDrawNameAttribute());
			Assert.assertEquals("slide-name-2", presentation.getPageAt(1).getDrawNameAttribute());
			Assert.assertEquals("slide-name-3", presentation.getPageAt(2).getDrawNameAttribute());
			Assert.assertEquals("slide-insert", presentation.getPageAt(3).getDrawNameAttribute());


			presentation.deletePage(3);
			Assert.assertEquals(count, presentation.getPageCount());
			Assert.assertEquals("slide-name-1", presentation.getPageAt(0).getDrawNameAttribute());
			Assert.assertEquals("slide-name-2", presentation.getPageAt(1).getDrawNameAttribute());
			Assert.assertEquals("slide-name-3", presentation.getPageAt(2).getDrawNameAttribute());

			presentation.movePage(2, 0);
			Assert.assertEquals("slide-name-3", presentation.getPageAt(0).getDrawNameAttribute());
			Assert.assertEquals("slide-name-1", presentation.getPageAt(1).getDrawNameAttribute());
			Assert.assertEquals("slide-name-2", presentation.getPageAt(2).getDrawNameAttribute());

			odfdoc.save(ResourceUtilities.newTestOutputFile(PRSENTATION_FILE1));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCreatePresentationPage() {
		try {
			OdfDocument odpdoc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(PRSENTATION_FILE2));
			OdfOfficePresentation presentation = OdfElement.findFirstChildNode(OdfOfficePresentation.class, odpdoc.getOfficeBody());
			presentation.createTemplatePage(TemplatePageType.DEFAULT, "slide_deault");
			presentation.createTemplatePage(TemplatePageType.ONLYTITLE, "slide_title");
			presentation.createTemplatePage(TemplatePageType.OUTLINE, "slide_outline");
			presentation.createTemplatePage(TemplatePageType.TEXT, "slide_text");
			presentation.createTemplatePage(TemplatePageType.TWOBLOCK, "slide_twoblock");
			odpdoc.save(ResourceUtilities.newTestOutputFile("TestPresentationCreatePage.odp"));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testPresentationEmbedDocumentPage() {
		try {
			OdfDocument odpdoc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(PRSENTATION_FILE3));
			OdfOfficePresentation presentation = OdfElement.findFirstChildNode(OdfOfficePresentation.class, odpdoc.getOfficeBody());
			presentation.deletePage(1);
			presentation.deletePage("chartInside");
			odpdoc.save(ResourceUtilities.newTestOutputFile(PRSENTATION_FILE4));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
}
