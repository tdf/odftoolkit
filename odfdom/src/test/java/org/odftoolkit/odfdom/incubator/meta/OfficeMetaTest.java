/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2009 IBM. All rights reserved.
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
package org.odftoolkit.odfdom.incubator.meta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.odftoolkit.odfdom.OdfFileDom;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.attribute.meta.MetaValueTypeAttribute.Value;
import org.odftoolkit.odfdom.type.Duration;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

public class OfficeMetaTest {

	private String filename = "metaTest.odt";
	private OdfTextDocument doc;
	private OdfFileDom metadom;
	private OdfOfficeMeta fMetadata;
	private String generator = "ODFDOM/" + System.getProperty("odfdom.version");
	private String dctitle = "dctitle";
	private String dcdescription = "dcdescription";
	private String subject = "dcsubject";
	private List<String> keywords = new ArrayList<String>();
	private String initialCreator = "creator";
	private String dccreator = System.getProperty("user.name");
	private String printedBy = "persia p";
	private String language = "Chinese";
	private Integer editingCycles = new Integer(4);
	private Duration editingDuration = Duration.valueOf("P49DT11H8M9S");
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	@Before
	public void setUp() throws Exception {
		doc = (OdfTextDocument) OdfTextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(filename));
		metadom = doc.getMetaDom();
		fMetadata = new OdfOfficeMeta(metadom);
	}

	@After
	public void tearDown() throws Exception {
		doc.save(ResourceUtilities.getAbsolutePath(filename));
		doc = null;
		metadom = null;
	}

	@Test
	public void testSetGenerator() {
		fMetadata.setGenerator(generator);
	}

	@Test
	public void testGetGenerator() {
		Assert.assertEquals(generator, fMetadata.getGenerator());
	}

	@Test
	public void testSetDcTitle() {
		fMetadata.setTitle(dctitle);
	}

	@Test
	public void testGetDcTitle() {
		Assert.assertEquals(dctitle, fMetadata.getTitle());
	}

	@Test
	public void testSetDcDescription() {
		fMetadata.setDescription(dcdescription);
	}

	@Test
	public void testGetDcDescription() {
		Assert.assertEquals(dcdescription, fMetadata.getDescription());
	}

	@Test
	public void testSetSubject() {
		fMetadata.setSubject(subject);
	}

	@Test
	public void testGetSubject() {
		Assert.assertEquals(subject, fMetadata.getSubject());
	}

	@Test
	public void testSetAndGetKeywords() throws Exception {
		keywords.add("lenovo2");
		keywords.add("computer3");
		keywords.add("think center");
		fMetadata.setKeywords(keywords);
		tearDown();
		setUp();
		Assert.assertEquals(keywords, fMetadata.getKeywords());
	}

	@Test
	public void testSetInitialCreator() {
		fMetadata.setInitialCreator(initialCreator);
	}

	@Test
	public void testGetInitialCreator() {
		Assert.assertEquals(initialCreator, fMetadata.getInitialCreator());
	}

	@Test
	public void testSetDcCreator() {
		fMetadata.setCreator(dccreator);
	}

	@Test
	public void testGetDcCreator() {
		Assert.assertEquals(dccreator, fMetadata.getCreator());
	}

	@Test
	public void testSetPrintedBy() {
		fMetadata.setPrintedBy(printedBy);
	}

	@Test
	public void testGetPrintedBy() {
		Assert.assertEquals(printedBy, fMetadata.getPrintedBy());
	}

	@Test
	public void testSetAndGetCreationDate() throws Exception {
		Calendar creationDate = Calendar.getInstance();
		fMetadata.setCreationDate(creationDate);
		tearDown();
		setUp();
		// //the millisecond lost while changing calendar to string
		// creationDate.clear(Calendar.MILLISECOND);
		// fMetadata.getCreationDate().clear(Calendar.MILLISECOND);
		//Assert.assertEquals(0,creationDate.compareTo(fMetadata.getCreationDate
		// ()));
		String expected = sdf.format(creationDate.getTime());
		String actual = sdf.format(fMetadata.getCreationDate().getTime());
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testSetDcDate() throws Exception {
		Calendar dcDate = Calendar.getInstance();
		fMetadata.setDcdate(dcDate);
		tearDown();
		setUp();
		// dcDate.clear(Calendar.MILLISECOND);
		// fMetadata.getDcdate().clear(Calendar.MILLISECOND);
		// Assert.assertEquals(0,dcDate.compareTo(fMetadata.getDcdate()));
		String expected = sdf.format(dcDate.getTime());
		String actual = sdf.format(fMetadata.getDcdate().getTime());
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testSetPrintDate() throws Exception {
		Calendar printDate = Calendar.getInstance();
		fMetadata.setPrintDate(printDate);
		tearDown();
		setUp();
		// printDate.clear(Calendar.MILLISECOND);
		// fMetadata.getPrintDate().clear(Calendar.MILLISECOND);
		// Assert.assertEquals(0,printDate.compareTo(fMetadata.getPrintDate()));
		String expected = sdf.format(printDate.getTime());
		String actual = sdf.format(fMetadata.getPrintDate().getTime());
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testSetLanguage() {
		fMetadata.setLanguage(language);
	}

	@Test
	public void testGetLanguage() {
		Assert.assertEquals(language, fMetadata.getLanguage());
	}

	@Test
	public void testSetEditingCycles() {
		fMetadata.setEditingCycles(editingCycles);
	}

	@Test
	public void testGetEditingCycles() {
		Assert.assertNotNull(fMetadata.getEditingCycles());
	}

	@Test
	public void testSetEditingDuration() {
		fMetadata.setEditingDuration(editingDuration);
	}

	@Test
	public void testGetEditingDuration() {
		Assert.assertNotNull(fMetadata.getEditingDuration());
	}

	@Test
	public void testEmptyKeyword() throws Exception {
		List<String> emptyKeyword = new ArrayList<String>();
		fMetadata.setKeywords(emptyKeyword);
		tearDown();
		setUp();
		Assert.assertNull(fMetadata.getKeywords());
	}

	@Test
	public void testAddKeyword() throws Exception {
		String newKeyword = "hello";
		fMetadata.addKeyword(newKeyword);
		tearDown();
		setUp();
		Assert.assertEquals(true, fMetadata.getKeywords().contains(newKeyword));
	}

	@Test
	public void testSetAndGetUserdefinedData() throws Exception {
		// remove if there is userdefined data
		List<String> names = new ArrayList<String>();
		names = fMetadata.getUserDefinedDataNames();
		for (String name : names) {
			fMetadata.removeUserDefinedDataByName(name);
		}
		names.clear();
		names.add("weather");
		names.add("mood");
		names.add("late");
		// test set
		fMetadata.setUserDefinedData(names.get(0), Value.STRING.toString(),
				"windy");
		fMetadata.setUserDefinedData(names.get(1), Value.STRING.toString(),
				"happy");
		fMetadata.setUserDefinedData(names.get(2), Value.BOOLEAN.toString(),
				"false");
		tearDown();

		setUp();
		// test get
		Assert.assertEquals(names, fMetadata.getUserDefinedDataNames());
		Assert.assertEquals(Value.STRING.toString(), fMetadata.getUserDefinedDataType(names.get(0)));
		Assert.assertEquals("windy", fMetadata.getUserDefinedDataValue(names.get(0)));

		fMetadata.setUserDefinedDataValue(names.get(1), "false");
		fMetadata.setUserDefinedDataType(names.get(1), Value.BOOLEAN.toString());
		fMetadata.setUserDefinedData(names.get(2), Value.STRING.toString(),
				"no");
		tearDown();

		setUp();
		// update
		Assert.assertEquals("false", fMetadata.getUserDefinedDataValue(names.get(1)));
		Assert.assertEquals(Value.BOOLEAN.toString(), fMetadata.getUserDefinedDataType(names.get(1)));
		Assert.assertEquals("no", fMetadata.getUserDefinedDataValue(names.get(2)));
		Assert.assertEquals(Value.STRING.toString(), fMetadata.getUserDefinedDataType(names.get(2)));
		tearDown();

		setUp();
		// remove
		fMetadata.removeUserDefinedDataByName(names.get(0));
		tearDown();
		setUp();
		Assert.assertEquals(2, fMetadata.getUserDefinedDataNames().size());

	}

	@Test
	public void testReadEmptyDocumentMeta() throws Exception {

		// create a new empty document
		doc = OdfTextDocument.newTextDocument();
		doc.save(ResourceUtilities.newTestOutputFile("EmptyDocForMetaTest.odt"));

		// read empty document meta
		doc = (OdfTextDocument) OdfTextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("EmptyDocForMetaTest.odt"));
		metadom = doc.getMetaDom();
		fMetadata = new OdfOfficeMeta(metadom);
		//ToDO: automatic check of VERSION number ODFDOM/0.6.1$Build-TIMESTAMP
		//Assert.assertTrue(fMetadata.getGenerator().startsWith(generator));
		//ToDO: http://odftoolkit.org/bugzilla/show_bug.cgi?id=171
		// Assert.assertEquals(fMetadata.getGenerator(), generator);
		Assert.assertNull(fMetadata.getTitle());
		Assert.assertNull(fMetadata.getDescription());
		Assert.assertNull(fMetadata.getSubject());
		Assert.assertNull(fMetadata.getKeywords());
		Assert.assertNull(fMetadata.getPrintedBy());
		Assert.assertNull(fMetadata.getPrintDate());
		Assert.assertNotNull(fMetadata.getUserDefinedDataNames());
	}
	
	@Test
	public void testReadDocumentMeta() throws Exception {
		// create a new empty document
		OdfTextDocument textDoc = OdfTextDocument.newTextDocument();
		textDoc.save(ResourceUtilities.newTestOutputFile("DocForMetaTest.odt"));
		textDoc.close();
		// read empty document meta
		textDoc = (OdfTextDocument) OdfTextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("DocForMetaTest.odt"));
		OdfOfficeMeta meta = textDoc.getOfficeMetadata();
		Assert.assertNotNull(meta.getGenerator());
		Assert.assertNotNull(meta.getCreationDate());
		Assert.assertNotNull(meta.getCreator());
		Assert.assertNotNull(meta.getDcdate());
		Assert.assertTrue(meta.getEditingCycles()>0);
		Assert.assertNotNull(meta.getEditingDuration());
		Assert.assertNotNull(meta.getLanguage());
		textDoc.close();
	}
}
