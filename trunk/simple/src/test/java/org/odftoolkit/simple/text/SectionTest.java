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

package org.odftoolkit.simple.text;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import junit.framework.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.draw.DrawImageElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.text.list.List;
import org.odftoolkit.simple.text.list.ListDecorator;
import org.odftoolkit.simple.text.list.ListTest;
import org.odftoolkit.simple.text.list.NumberDecorator;
import org.odftoolkit.simple.text.list.OutLineDecorator;
import org.odftoolkit.simple.utils.ResourceUtilities;
import org.w3c.dom.Attr;
import org.w3c.dom.NodeList;
import sun.misc.BASE64Encoder;

public class SectionTest {

	@Test
	public void testCopyPasteResource() {
		try {
			TextDocument doc =
				TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("Sections.odt"));
			Section theSec = doc.getSectionByName("ImageSection");
			String newName = doc.appendSection(theSec, false).getName();
			doc.save(ResourceUtilities.newTestOutputFile("NewSection.odt"));

			TextDocument newDoc =
				TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("NewSection.odt"));
			theSec = newDoc.getSectionByName("ImageSection");
			Section newSec = newDoc.getSectionByName(newName);

			XPath xpath = newDoc.getContentDom().getXPath();
			DrawImageElement oldImage =
				(DrawImageElement) xpath.evaluate(".//draw:image", theSec.getOdfElement(),
					XPathConstants.NODE);
			DrawImageElement newImage =
				(DrawImageElement) xpath.evaluate(".//draw:image", newSec.getOdfElement(),
					XPathConstants.NODE);
			Assert.assertEquals(oldImage.getXlinkHrefAttribute(), newImage.getXlinkHrefAttribute());

			OdfPackage packageDocument = newDoc.getPackage();
			String imagePathPrefix = "Pictures/";
			int count = 0;
			Iterator<String> filePaths = packageDocument.getFilePaths().iterator();
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

			newDoc =
				TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("NewSection1.odt"));
			theSec = newDoc.getSectionByName("ImageSection");
			newSec = newDoc.getSectionByName(newName);

			xpath = newDoc.getContentDom().getXPath();
			oldImage =
				(DrawImageElement) xpath.evaluate(".//draw:image", theSec.getOdfElement(),
					XPathConstants.NODE);
			newImage =
				(DrawImageElement) xpath.evaluate(".//draw:image", newSec.getOdfElement(),
					XPathConstants.NODE);
			if (oldImage.getXlinkHrefAttribute().equals(newImage.getXlinkHrefAttribute()))
				Assert.fail();

			packageDocument = newDoc.getPackage();
			count = 0;
			filePaths = packageDocument.getFilePaths().iterator();
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

			TextDocument doc =
				TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("Sections.odt"));
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

			newDoc =
				TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("NewNewSections.odt"));
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
			Iterator<String> filePaths = packageDocument.getFilePaths().iterator();
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
			TextDocument doc =
				TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("Sections.odt"));
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
	public void testAppendNewSection() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			doc.addParagraph("Paragraph1");
			Section section = doc.appendSection("Section1");
			section.addParagraph("Here's a section.");
			Assert.assertNotNull(section);
			Assert.assertEquals(section.getName(), "Section1");
			Assert.assertEquals(section.getParagraphByIndex(0, true).getTextContent(),
				"Here's a section.");

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	private class MyDigestGenerator implements ProtectionKeyDigestProvider {

		// @Override
		public String generateHashKey(String passwd) {

			String hashKey = null;
			if (passwd != null && passwd.length() > 0) {
				MessageDigest md;
				try {
					byte[] pwd = passwd.getBytes();
					md = MessageDigest.getInstance("MD5");
					byte[] byteCode = md.digest(pwd);
					BASE64Encoder encoder = new BASE64Encoder();
					hashKey = encoder.encode(byteCode);
				} catch (NoSuchAlgorithmException e) {
					Logger.getLogger(Section.class.getName(), "Fail to initiate the digest method.");
				}
			}
			return hashKey;

		}

		// @Override
		public String getProtectionKeyDigestAlgorithm() {
			return "http://www.w3.org/2000/09/#md5";
		}

	}

	@Test
	public void testSetProtectionKeyDigestProvider() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Section section = doc.appendSection("Section1");
			section.addParagraph("Section 1");
			Assert.assertNotNull(section);

			section.setProtectionKeyDigestProvider(new MyDigestGenerator());
			section.setProtectedWithPassword("12345");
			Assert.assertEquals(true, section.isProtected());
			Assert.assertEquals("gnzLDuqKcGxMNKFokfhOew==", section.getProtectedPassword());
			Assert.assertEquals("http://www.w3.org/2000/09/#md5",
				section.getProtectionKeyDigestAlgorithm());

			section.setProtectionKeyDigestProvider(null);
			section.setProtectedWithPassword("12345");
			Assert.assertEquals(true, section.isProtected());
			Assert.assertEquals("LyQWujvPXbGDYsrSDKkAiVFavg8=", section.getProtectedPassword());
			Assert.assertEquals("http://www.w3.org/2000/09/xmldsig#sha1",
				section.getProtectionKeyDigestAlgorithm());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testSetProtectSection() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			doc.addParagraph("Paragraph1");
			String secName = "Section1";
			Section section = doc.appendSection(secName);
			Assert.assertNotNull(section);

			section.setProtectedWithPassword("12345");
			Assert.assertEquals(true, section.isProtected());
			Assert.assertEquals("LyQWujvPXbGDYsrSDKkAiVFavg8=", section.getProtectedPassword());
			Assert.assertEquals("http://www.w3.org/2000/09/xmldsig#sha1",
				section.getProtectionKeyDigestAlgorithm());

			section.setProtectedWithPassword("");
			Assert.assertEquals(true, section.isProtected());
			Assert.assertNull(section.getProtectedPassword());
			Assert.assertEquals("http://www.w3.org/2000/09/xmldsig#sha1",
				section.getProtectionKeyDigestAlgorithm());

			section.setProtected(false);
			Assert.assertEquals(false, section.isProtected());
			Assert.assertNull(section.getProtectedPassword());
			Assert.assertEquals("http://www.w3.org/2000/09/xmldsig#sha1",
				section.getProtectionKeyDigestAlgorithm());

			section.setProtectedWithPassword(null);
			Assert.assertEquals(false, section.isProtected());
			Assert.assertEquals("http://www.w3.org/2000/09/xmldsig#sha1",
				section.getProtectionKeyDigestAlgorithm());
			Assert.assertNull(section.getProtectedPassword());

			section.setProtected(true);
			Assert.assertEquals(true, section.isProtected());
			Assert.assertEquals("http://www.w3.org/2000/09/xmldsig#sha1",
				section.getProtectionKeyDigestAlgorithm());
			Assert.assertNull(section.getProtectedPassword());

			section.setProtectedWithPassword("12345");
			Assert.assertEquals(true, section.isProtected());
			Assert.assertEquals("LyQWujvPXbGDYsrSDKkAiVFavg8=", section.getProtectedPassword());
			Assert.assertEquals("http://www.w3.org/2000/09/xmldsig#sha1",
				section.getProtectionKeyDigestAlgorithm());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testRemoveSection() {
		try {
			TextDocument doc =
				TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("Sections.odt"));
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
			Iterator<String> filePaths = packageDocument.getFilePaths().iterator();
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
			TextDocument doc =
				TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("Sections.odt"));
			Section theSec = doc.getSectionByName("ImageSection");
			theSec.setName("ImageSection_NewName");
			doc.save(ResourceUtilities.newTestOutputFile("NewSection.odt"));

			TextDocument newDoc =
				TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("NewSection.odt"));
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

	@Test
	public void testAddParagraph() {
		try {
			TextDocument doc =
				TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("Sections.odt"));
			Section theSec = doc.getSectionByName("ImageSection");

			Paragraph para = theSec.addParagraph("paragraph");
			String paracontent = para.getTextContent();
			Assert.assertEquals("paragraph", paracontent);

			OdfElement odfEle = theSec.getParagraphContainerElement();
			Assert.assertEquals("paragraph", odfEle.getLastChild().getTextContent());

			boolean flag = theSec.removeParagraph(para);
			if (flag) {
				OdfElement odfEle1 = theSec.getParagraphContainerElement();
				Assert.assertTrue(odfEle1.getLastChild().getTextContent() != "paragraph");
			}

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testGetParagraphIterator() {
		try {
			TextDocument doc =
				TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("Sections.odt"));
			Section theSec = doc.getSectionByName("ImageSection");

			Paragraph para = theSec.addParagraph("paragraph");
			String paracontent = para.getTextContent();
			Assert.assertEquals("paragraph", paracontent);

			boolean flag = false;
			Iterator<Paragraph> iter = theSec.getParagraphIterator();
			while (iter.hasNext()) {
				Paragraph parai = iter.next();
				if ("paragraph".equals(parai.getTextContent()))
					flag = true;
			}

			Assert.assertTrue(flag);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testGetParagraphByIndex() {
		try {
			TextDocument doc =
				TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("Sections.odt"));
			Section theSec = doc.getSectionByName("ImageSection");

			Paragraph para = theSec.addParagraph("paragraph");
			String paracontent = para.getTextContent();
			Assert.assertEquals("paragraph", paracontent);

			Paragraph para1 = theSec.getParagraphByIndex(2, true);
			Assert.assertEquals("paragraph", para1.getTextContent());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testGetParagraphByReverseIndex() {
		try {
			TextDocument doc =
				TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("Sections.odt"));
			Section theSec = doc.getSectionByName("ImageSection");
			Section theSec2 = doc.getSectionByName("ImageSection");

			Paragraph para = theSec.addParagraph("paragraph");
			String paracontent = para.getTextContent();
			Assert.assertEquals("paragraph", paracontent);

			Paragraph para1 = theSec.getParagraphByReverseIndex(0, true);
			Assert.assertEquals("paragraph", para1.getTextContent());

			Paragraph para2 = theSec.getParagraphByIndex(2, true);
			Assert.assertEquals("paragraph", para2.getTextContent());

			boolean flag = theSec.equals(para);
			Assert.assertTrue(!flag);
			flag = theSec.equals(theSec2);
			Assert.assertTrue(flag);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testAddTable() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Section theSec = doc.appendSection("TableSection");

			Table table1 = theSec.addTable();
			table1.getCellByPosition("A1").addParagraph("A1");
			OdfElement odfEle = theSec.getTableContainerElement();
			Assert.assertEquals("A1", Table.getInstance((TableTableElement) odfEle.getLastChild())
				.getCellByPosition("A1").getDisplayText());

			Table table2 = theSec.addTable(3, 3);
			table2.getCellByPosition("C3").addParagraph("C3");
			odfEle = theSec.getTableContainerElement();
			Assert.assertEquals("C3", Table.getInstance((TableTableElement) odfEle.getLastChild())
				.getCellByPosition("C3").getDisplayText());

			table1.remove();
			table2.remove();
			OdfElement odfEle1 = theSec.getTableContainerElement();
			Assert.assertNull(odfEle1.getLastChild());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testGetTableByName() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Section sect = doc.appendSection("TableSeciton");
			sect.addTable(2, 2);
			sect.addTable(2, 2);
			sect.addTable(5, 5).getCellByPosition("E5").setBooleanValue(true);
			Cell cell = sect.getTableByName("Table3").getCellByPosition(4, 4);
			Assert.assertNotNull(cell);
			Assert.assertTrue(cell.getBooleanValue());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testAddRemoveList() {

		try {
			TextDocument doc = TextDocument.newTextDocument();
			Section sect = doc.appendSection("List Section");

			ListDecorator numberDecorator = new NumberDecorator(doc);
			ListDecorator outLineDecorator = new OutLineDecorator(doc);
			String[] subItemContents = { "sub list item 1", "sub list item 2", "sub list item 3" };

			List list1 = new List(sect);
			boolean removeResult = sect.removeList(list1);
			Assert.assertTrue(removeResult);

			List list2 = new List(sect, numberDecorator);
			list2.addItems(subItemContents);
			Assert.assertEquals(ListDecorator.ListType.NUMBER, list2.getType());
			removeResult = sect.removeList(list2);
			Assert.assertTrue(removeResult);

			List list3 = new List(sect, "Bullet List", null);
			list3.addItems(subItemContents);
			Assert.assertEquals(ListDecorator.ListType.BULLET, list3.getType());
			removeResult = sect.removeList(list3);
			Assert.assertTrue(removeResult);

			List list4 = sect.addList();
			removeResult = sect.removeList(list4);
			Assert.assertTrue(removeResult);

			List list5 = sect.addList(outLineDecorator);
			list5.addItems(subItemContents);
			Assert.assertEquals(ListDecorator.ListType.NUMBER, list5.getType());
			removeResult = sect.removeList(list5);
			Assert.assertTrue(removeResult);
		} catch (Exception e) {
			Logger.getLogger(ListTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testIterateList() {
		try {

			TextDocument doc = TextDocument.newTextDocument();
			Section sect = doc.appendSection("List Section");

			ListDecorator numberDecorator = new NumberDecorator(doc);
			ListDecorator outLineDecorator = new OutLineDecorator(doc);
			String[] subItemContents = { "sub list item 1", "sub list item 2", "sub list item 3" };

			// create 2 lists
			new List(sect, numberDecorator);
			sect.addList().addItems(subItemContents);

			Iterator<List> listIterator = sect.getListIterator();
			int i = 0;
			while (listIterator.hasNext()) {
				listIterator.next();
				i++;
			}
			Assert.assertEquals(2, i);

			// add 2 new lists.
			sect.addList(outLineDecorator);
			List list = sect.addList();
			listIterator = sect.getListIterator();
			i = 0;
			while (listIterator.hasNext()) {
				listIterator.next();
				i++;
			}
			Assert.assertEquals(4, i);

			// remove 1 list.
			sect.removeList(list);
			listIterator = sect.getListIterator();
			i = 0;
			while (listIterator.hasNext()) {
				listIterator.next();
				i++;
			}
			Assert.assertEquals(3, i);

			// remove all of the lists.
			sect.clearList();
			listIterator = sect.getListIterator();
			i = 0;
			while (listIterator.hasNext()) {
				listIterator.next();
				i++;
			}
			Assert.assertEquals(0, i);

		} catch (Exception e) {
			Logger.getLogger(ListTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetEmbeddedSectionByName() {
		try {
			TextDocument doc =
				TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("Sections.odt"));
			Section sectOut = doc.getSectionByName("InnerSection");
			Section sectEmbedded = sectOut.getEmbeddedSectionByName("EmbedSection");
			Assert.assertEquals(true, sectEmbedded != null);
		} catch (Exception e) {
			Logger.getLogger(ListTest.class.getName()).log(Level.SEVERE, "Problem with section test:", e);
			Assert.fail();
		}
	}

	/**
	 * tests the Function 'updateXMLIds' indirectly by calling
	 * TextDocument.appendSection() The function updateXMLIds has to change the
	 * xml:id of a list element and its reference text:continue-list in the same
	 * way.
	 *
	 * The SectionContinuedList.odt includes one section. This section includes
	 * three lists, the third list continues the first one.
	 *
	 *
	 * In this test we copy the section and remove the source section. Then we
	 * investigate the xml:id and text:continue-list attribute of the copied
	 * section. The attribute values must be changed in the same way.
	 *
	 */
	@Test
	public void testUpdateXMLIds() {
		try {
			TextDocument doc =
				TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("SectionContinuedList.odt"));
			Iterator<Section> sections = doc.getSectionIterator();

			Section secCopy = null;
			while (sections.hasNext()) {
				Section aSection = sections.next();
				secCopy = doc.appendSection(aSection, false);
				aSection.remove();
				break;
			}

			// Check if the id of the continued List is changed in the same way
			// like the id of the source.

			OdfElement odfEle = secCopy.getOdfElement();
			String xpathValue = "//*[@xml:id]";
			XPath xpath = doc.getContentDom().getXPath();
			NodeList childList = (NodeList) xpath.evaluate(xpathValue, odfEle, XPathConstants.NODESET);
			OdfElement ele = (OdfElement) childList.item(0);
			Attr attriXmlId = ele.getAttributeNodeNS(OdfDocumentNamespace.XML.getUri(), "id");

			String xpathValueRef = "//*[@text:continue-list]";
			NodeList refIdList = (NodeList) xpath.evaluate(xpathValueRef, odfEle, XPathConstants.NODESET);
			ele = (OdfElement) refIdList.item(0);
			Attr attriContinueId =
				ele.getAttributeNodeNS(OdfDocumentNamespace.TEXT.getUri(), "continue-list");

			// Both attibute values must be the same
			Assert.assertEquals(attriContinueId.getValue(), attriXmlId.getValue());
            doc.save(ResourceUtilities.newTestOutputFile("SectionContinuedList_updatedXmlId.odt"));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}
