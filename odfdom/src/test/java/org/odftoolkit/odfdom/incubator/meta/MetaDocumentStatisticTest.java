/************************************************************************
* 
*  Licensed to the Apache Software Foundation (ASF) under one
*  or more contributor license agreements.  See the NOTICE file
*  distributed with this work for additional information
*  regarding copyright ownership.  The ASF licenses this file
*  to you under the Apache License, Version 2.0 (the
*  "License"); you may not use this file except in compliance
*  with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*
************************************************************************/

package org.odftoolkit.odfdom.incubator.meta;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.element.meta.MetaDocumentStatisticElement;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

public class MetaDocumentStatisticTest {

	private String filename = "metaTest.odt";
	private OdfTextDocument doc;
	private OdfFileDom metadom;
	private OdfMetaDocumentStatistic stat;
	private Integer cellCount = 1;
	private Integer characterCount = 2;
	private Integer drawCount = 3;
	private Integer frameCount = 4;
	private Integer imageCount = 5;
	private Integer nonWhitespaceCharacterCount = 6;
	private Integer objectCount = 7;
	private Integer oleObjectCount = 8;
	private Integer pageCount = 9;
	private Integer paragraphCount = 10;
	private Integer rowCount = 11;
	private Integer sentenceCount = 12;
	private Integer syllableCount = 13;
	private Integer tableCount = 14;
	private Integer wordCount = 15;

	@Before
	public void setUp() throws Exception {
		doc = (OdfTextDocument) OdfTextDocument.loadDocument(ResourceUtilities
				.getTestResourceAsStream(filename));
		metadom = doc.getMetaDom();
		OdfOfficeMeta odfOfficeMeta = new OdfOfficeMeta(metadom);
		stat = odfOfficeMeta.getDocumentStatistic();
		if (stat == null) {
			// the element does not exist in the metadata, add a new one
			MetaDocumentStatisticElement statEle = odfOfficeMeta
					.getOfficeMetaElement().newMetaDocumentStatisticElement();
			stat = new OdfMetaDocumentStatistic(statEle);
		}

	}

	@After
	public void tearDown() throws Exception {
		if (doc!=null)
			doc.save(ResourceUtilities.getAbsolutePath(filename));
		doc = null;
		metadom = null;
	}

	@Test
	public void testSetCellCount() {
		stat.setCellCount(cellCount);
	}

	@Test
	public void testGetCellCount() {
		Assert.assertEquals(cellCount, stat.getCellCount());
	}

	@Test
	public void testSetCharacterCount() {
		stat.setCharacterCount(characterCount);
	}

	@Test
	public void testGetCharacterCount() {
		Assert.assertEquals(characterCount, stat.getCharacterCount());
	}

	@Test
	public void testSetDrawCount() {
		stat.setDrawCount(drawCount);
	}

	@Test
	public void testGetDrawCount() {
		Assert.assertEquals(drawCount, stat.getDrawCount());
	}

	@Test
	public void testSetFrameCount() {
		stat.setFrameCount(frameCount);
	}

	@Test
	public void testGetFrameCount() {
		Assert.assertEquals(frameCount, stat.getFrameCount());
	}

	@Test
	public void testSetImageCount() {
		stat.setImageCount(imageCount);
	}

	@Test
	public void testGetImageCount() {
		Assert.assertEquals(imageCount, stat.getImageCount());
	}

	@Test
	public void testSetNonWhitespaceCharacterCount() {
		stat.setNonWhitespaceCharacterCount(nonWhitespaceCharacterCount);
	}

	@Test
	public void testGetNonWhitespaceCharacterCount() {
		Assert.assertEquals(nonWhitespaceCharacterCount, stat
				.getNonWhitespaceCharacterCount());
	}

	@Test
	public void testSetObjectCount() {
		stat.setObjectCount(objectCount);
	}

	@Test
	public void testGetObjectCount() {
		Assert.assertEquals(objectCount, stat.getObjectCount());
	}

	@Test
	public void testSetOleObjectCount() {
		stat.setOleObjectCount(oleObjectCount);
	}

	@Test
	public void testGetOleObjectCount() {
		Assert.assertEquals(oleObjectCount, stat.getOleObjectCount());
	}

	@Test
	public void testSetPageCount() {
		stat.setPageCount(pageCount);
	}

	@Test
	public void testGetPageCount() {
		Assert.assertEquals(pageCount, stat.getPageCount());
	}

	@Test
	public void testSetParagraphCount() {
		stat.setParagraphCount(paragraphCount);
	}

	@Test
	public void testGetParagraphCount() {
		Assert.assertEquals(paragraphCount, stat.getParagraphCount());
	}

	@Test
	public void testSetRowCount() {
		stat.setRowCount(rowCount);
	}

	@Test
	public void testGetRowCount() {
		Assert.assertEquals(rowCount, stat.getRowCount());
	}

	@Test
	public void testSetSentenceCount() {
		stat.setSentenceCount(sentenceCount);
	}

	@Test
	public void testGetSentenceCount() {
		Assert.assertEquals(sentenceCount, stat.getSentenceCount());
	}

	@Test
	public void testSetSyllableCount() {
		stat.setSyllableCount(syllableCount);
	}

	@Test
	public void testGetSyllableCount() {
		Assert.assertEquals(syllableCount, stat.getSyllableCount());
	}

	@Test
	public void testSetTableCount() {
		stat.setTableCount(tableCount);
	}

	@Test
	public void testGetTableCount() {
		Assert.assertEquals(tableCount, stat.getTableCount());
	}

	@Test
	public void testSetWordCount() {
		stat.setWordCount(wordCount);
	}

	@Test
	public void testGetWordCount() {
		Assert.assertEquals(wordCount, stat.getWordCount());
	}

	@Test
	public void testReadEmptyDocumentMeta() throws Exception {

		// create a new empty document
		doc = (OdfTextDocument) OdfTextDocument.newTextDocument();
		doc
				.save(ResourceUtilities
						.newTestOutputFile("EmptyDocForMetaTest.odt"));

		// read empty document meta
		doc = (OdfTextDocument) OdfTextDocument.loadDocument(ResourceUtilities
				.getTestResourceAsStream("EmptyDocForMetaTest.odt"));
		metadom = doc.getMetaDom();
		OdfOfficeMeta odfOfficeMeta = new OdfOfficeMeta(metadom);
		stat = odfOfficeMeta.getDocumentStatistic();
		Assert.assertNull(stat);

		// create a new stat
		MetaDocumentStatisticElement statEle = odfOfficeMeta
				.getOfficeMetaElement().newMetaDocumentStatisticElement();
		stat = new OdfMetaDocumentStatistic(statEle);

		Assert.assertNull(stat.getCellCount());
		Assert.assertNull(stat.getCharacterCount());
		Assert.assertNull(stat.getDrawCount());
		Assert.assertNull(stat.getFrameCount());
		Assert.assertNull(stat.getImageCount());
		Assert.assertNull(stat.getNonWhitespaceCharacterCount());
		Assert.assertNull(stat.getObjectCount());
		Assert.assertNull(stat.getOleObjectCount());
		Assert.assertNull(stat.getPageCount());
		Assert.assertNull(stat.getParagraphCount());
		Assert.assertNull(stat.getRowCount());
		Assert.assertNull(stat.getSentenceCount());
		Assert.assertNull(stat.getSyllableCount());
		Assert.assertNull(stat.getTableCount());
		Assert.assertNull(stat.getWordCount());

	}

}
