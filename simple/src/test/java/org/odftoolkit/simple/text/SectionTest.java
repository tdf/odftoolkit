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

import java.util.Iterator;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.odfdom.dom.element.draw.DrawImageElement;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class SectionTest {

	@Test
	public void testCopyPasteResource() {
		try {
			TextDocument doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("Sections.odt"));
			Section theSec = doc.getSectionByName("ImageSection");
			String newName = doc.appendSection(theSec, false).getName();
			doc.save(ResourceUtilities.newTestOutputFile("NewSection.odt"));

			TextDocument newDoc = TextDocument
					.loadDocument(ResourceUtilities.getTestResourceAsStream("NewSection.odt"));
			theSec = newDoc.getSectionByName("ImageSection");
			Section newSec = newDoc.getSectionByName(newName);

			XPath xpath = newDoc.getContentDom().getXPath();
			DrawImageElement oldImage = (DrawImageElement) xpath.evaluate(".//draw:image", theSec.getOdfElement(),
					XPathConstants.NODE);
			DrawImageElement newImage = (DrawImageElement) xpath.evaluate(".//draw:image", newSec.getOdfElement(),
					XPathConstants.NODE);
			Assert.assertEquals(oldImage.getXlinkHrefAttribute(), newImage.getXlinkHrefAttribute());

			OdfPackage packageDocument = newDoc.getPackage();
			String imagePathPrefix = "Pictures/";
			int count = 0;
			Iterator<String> filePaths = packageDocument.getFileEntries().iterator();
			while (filePaths.hasNext()) {
				String path = filePaths.next();
				if (path.startsWith(imagePathPrefix) && path.length() > imagePathPrefix.length())
					count++;
				if (count > 2)
					break;
			}
			Assert.assertEquals(1, count);
			// ---------resource copied------
			doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("Sections.odt"));
			theSec = doc.getSectionByName("ImageSection");
			newName = doc.appendSection(theSec, true).getName();
			doc.save(ResourceUtilities.newTestOutputFile("NewSection1.odt"));

			newDoc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("NewSection1.odt"));
			theSec = newDoc.getSectionByName("ImageSection");
			newSec = newDoc.getSectionByName(newName);

			xpath = newDoc.getContentDom().getXPath();
			oldImage = (DrawImageElement) xpath.evaluate(".//draw:image", theSec.getOdfElement(), XPathConstants.NODE);
			newImage = (DrawImageElement) xpath.evaluate(".//draw:image", newSec.getOdfElement(), XPathConstants.NODE);
			if (oldImage.getXlinkHrefAttribute().equals(newImage.getXlinkHrefAttribute()))
				Assert.fail();

			packageDocument = newDoc.getPackage();
			count = 0;
			filePaths = packageDocument.getFileEntries().iterator();
			while (filePaths.hasNext()) {
				String path = filePaths.next();
				if (path.startsWith(imagePathPrefix) && path.length() > imagePathPrefix.length())
					count++;
				if (count > 2)
					break;
			}
			Assert.assertEquals(2, count);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testForeignCopyPaste() {
		try {
			TextDocument newDoc = TextDocument.newTextDocument();

			TextDocument doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("Sections.odt"));
			Iterator<Section> sections = doc.getSectionIterator();

			int count = 0;
			while (sections.hasNext()) {
				Section aSection = sections.next();
				count++;
				newDoc.newParagraph("----Start of " + aSection.getName() + "---------");
				newDoc.appendSection(aSection, false);
				newDoc.newParagraph("----End of " + aSection.getName() + "---------");
				newDoc.newParagraph();
				newDoc.newParagraph();
				newDoc.newParagraph();
			}
			newDoc.save(ResourceUtilities.newTestOutputFile("NewNewSections.odt"));

			newDoc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("NewNewSections.odt"));
			sections = newDoc.getSectionIterator();
			int i = 0;
			while (sections.hasNext()) {
				sections.next();
				i++;
			}
			// an embed section is counted two times
			Assert.assertEquals(count + 1, i);

			OdfPackage packageDocument = newDoc.getPackage();
			String imagePathPrefix = "Pictures/";
			count = 0;
			Iterator<String> filePaths = packageDocument.getFileEntries().iterator();
			while (filePaths.hasNext()) {
				String path = filePaths.next();
				if (path.startsWith(imagePathPrefix) && path.length() > imagePathPrefix.length())
					count++;
				if (count > 2)
					break;
			}
			Assert.assertEquals(1, count);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testCopyPasteAll() {
		try {
			TextDocument doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("Sections.odt"));
			Iterator<Section> sections = doc.getSectionIterator();

			int count = 0;
			while (sections.hasNext()) {
				Section aSection = sections.next();
				count++;
				doc.newParagraph("----Start of " + aSection.getName() + "---------");
				doc.appendSection(aSection, false);
				doc.newParagraph("----End of " + aSection.getName() + "---------");
				doc.newParagraph();
				doc.newParagraph();
				doc.newParagraph();
			}
			doc.save(ResourceUtilities.newTestOutputFile("NewSections.odt"));

			doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("NewSections.odt"));
			sections = doc.getSectionIterator();
			int i = 0;
			while (sections.hasNext()) {
				sections.next();
				i++;
			}
			// an embed section is counted two times
			Assert.assertEquals(count * 2 + 1, i);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testRemoveSection() {
		try {
			TextDocument doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("Sections.odt"));
			Section theSec = doc.getSectionByName("Section11");
			Assert.assertNull(theSec);
			Iterator<Section> sections = doc.getSectionIterator();
			int count = 0;
			while (sections.hasNext()) {
				sections.next();
				count++;
			}

			theSec = doc.getSectionByName("Section1");
			theSec.remove();
			sections = doc.getSectionIterator();
			int i = 0;
			while (sections.hasNext()) {
				sections.next();
				i++;
			}
			Assert.assertEquals(count - 1, i);

			theSec = doc.getSectionByName("ImageSection");
			theSec.remove();
			OdfPackage packageDocument = doc.getPackage();
			String imagePathPrefix = "Pictures/";
			count = 0;
			Iterator<String> filePaths = packageDocument.getFileEntries().iterator();
			while (filePaths.hasNext()) {
				String path = filePaths.next();
				if (path.startsWith(imagePathPrefix) && path.length() > imagePathPrefix.length())
					count++;
				if (count > 1)
					break;
			}
			Assert.assertEquals(0, count);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testSetGetName() {
		try {
			TextDocument doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("Sections.odt"));
			Section theSec = doc.getSectionByName("ImageSection");
			theSec.setName("ImageSection_NewName");
			doc.save(ResourceUtilities.newTestOutputFile("NewSection.odt"));

			TextDocument newDoc = TextDocument
					.loadDocument(ResourceUtilities.getTestResourceAsStream("NewSection.odt"));
			theSec = newDoc.getSectionByName("ImageSection");
			Assert.assertNull(theSec);
			theSec = newDoc.getSectionByName("ImageSection_NewName");
			Assert.assertNotNull(theSec);
			Assert.assertEquals("ImageSection_NewName", theSec.getName());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
