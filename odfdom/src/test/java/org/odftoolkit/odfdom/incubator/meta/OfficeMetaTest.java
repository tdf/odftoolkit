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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.attribute.meta.MetaValueTypeAttribute.Value;
import org.odftoolkit.odfdom.dom.example.LoadMultipleTimes;
import org.odftoolkit.odfdom.type.Duration;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

public class OfficeMetaTest {

	private static final Logger LOG = Logger.getLogger(OfficeMetaTest.class.getName());
	private String filename = "metaTest.odt";
	private OdfTextDocument doc;
	private OdfFileDom metadom;
	private OdfOfficeMeta fMetadata;
	private static final String generator = "ODFDOM/SNAPSHOT-TEST";
	private String dctitle = "dctitle";
	private String dcdescription = "dcdescription";
	private String subject = "dcsubject";
	private List<String> keywords = new ArrayList<String>();
	private String initialCreator = "creator";
	private String dccreator = "Mr. fictionalTestUser";
	private String printedBy = "persia p";
	private String language = "EN_us";
	private Integer editingCycles = new Integer(4);
	private Duration editingDuration = Duration.valueOf("P49DT11H8M9S");
	private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	@Before
	public void setUp() throws Exception {
		doc = (OdfTextDocument) OdfTextDocument.loadDocument(ResourceUtilities.getAbsolutePath(filename));
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
	@Ignore // Add Test when the version number changes and the templates should be adapted
	public void updateTemplates() {
		// Adapt all files with latest XML changes automatically
		loadSaveDirFiles("target/classes/"); // all template files to be bundled within the JAR
		// The following is only necessary, when the templates in the source repository should be adapted, e.g. before a release
		loadSaveDirFiles("src/main/resources/");
	}

	/** The reference templates of the JAR will be loaded and saved. */
	private void loadSaveDirFiles(String targetDirectory) {
		try {
			LOG.log(Level.INFO, "Loading/saving resources from the directory: ''{0}''!", targetDirectory);
			File resDir = new File(targetDirectory);
			File[] resFiles = resDir.listFiles();
			boolean validTest = true;
			for (File odfFile : resFiles) {
				if (!odfFile.isDirectory() ) { //&& odfFile.getName().startsWith("Odf")
					OdfDocument odfDoc = OdfDocument.loadDocument(odfFile);
					String version = System.getProperty("odfdom.version");
					OdfOfficeMeta meta = odfDoc.getOfficeMetadata();
					meta.setAutomaticUpdate(false);
					meta.setCreator(null);
					meta.setCreationDate(null);
					meta.setDate(null);
					meta.setDescription(null);
					meta.setEditingCycles(null);
					meta.setEditingDuration(null);
					meta.setLanguage(null);
					meta.setPrintDate(null);
					meta.setPrintedBy(null);
					meta.setSubject(null);
					meta.setTitle(null);
					meta.removeUserDefinedDataByName("Info 1");
					meta.removeUserDefinedDataByName("Info 2");
					meta.removeUserDefinedDataByName("Info 3");
					meta.removeUserDefinedDataByName("Info 4");
					String timeStamp = mSimpleDateFormat.format(Calendar.getInstance().getTime());
					if (version != null) {
						if (version.endsWith("SNAPSHOT")) {
							version = version + "(" + timeStamp + ")";
						}
						meta.setGenerator("ODFDOM/" + version);
					} else {
						meta.setGenerator("ODFDOM/SNAPSHOT(" + timeStamp + ")");
						validTest = false;
					}
					LOG.log(Level.INFO, "Updating the resource {0}", odfFile.getPath());
					odfDoc.save(odfFile.getPath());
				}
			}
			Assert.assertTrue("No meta:generator could be set as the System property 'odfdom.version' set by the Maven pom.xml was not found!", validTest);

			//ToDO: Add validation test afterwards..
		} catch (Exception ex) {
			Logger.getLogger(LoadMultipleTimes.class.getName()).log(Level.SEVERE, null, ex);
		}
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
		String expected = mSimpleDateFormat.format(creationDate.getTime());
		String actual = mSimpleDateFormat.format(fMetadata.getCreationDate().getTime());
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testSetDcDate() throws Exception {
		Calendar dcDate = Calendar.getInstance();
		fMetadata.setDate(dcDate);
		tearDown();
		setUp();
		// dcDate.clear(Calendar.MILLISECOND);
		// fMetadata.getDate().clear(Calendar.MILLISECOND);
		// Assert.assertEquals(0,dcDate.compareTo(fMetadata.getDate()));
		String expected = mSimpleDateFormat.format(dcDate.getTime());
		String actual = mSimpleDateFormat.format(fMetadata.getDate().getTime());
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
		String expected = mSimpleDateFormat.format(printDate.getTime());
		String actual = mSimpleDateFormat.format(fMetadata.getPrintDate().getTime());
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
		List<String> names;
		names = fMetadata.getUserDefinedDataNames();
		if (names == null) {
			names = new ArrayList<String>();
		} else {
			for (String name : names) {
				fMetadata.removeUserDefinedDataByName(name);
			}
			names.clear();
		}
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
		doc = (OdfTextDocument) OdfTextDocument.loadDocument(ResourceUtilities.getAbsolutePath("EmptyDocForMetaTest.odt"));
		metadom = doc.getMetaDom();
		fMetadata = new OdfOfficeMeta(metadom);
		//Assert.assertTrue(fMetadata.getGenerator().startsWith(generator));
		//ToDO: http://odftoolkit.org/bugzilla/show_bug.cgi?id=171
		// Assert.assertEquals(fMetadata.getGenerator(), generator);
		Assert.assertNull(fMetadata.getTitle());
		Assert.assertNull(fMetadata.getDescription());
		Assert.assertNull(fMetadata.getSubject());
		Assert.assertNull(fMetadata.getKeywords());
		Assert.assertNull(fMetadata.getPrintedBy());
		Assert.assertNull(fMetadata.getPrintDate());
		Assert.assertNull(fMetadata.getUserDefinedDataNames());
	}

	@Test
	public void testReadDocumentMeta() throws Exception {
		// create a new empty document
		OdfTextDocument textDoc = OdfTextDocument.newTextDocument(); // activiating metadata updates
		textDoc.save(ResourceUtilities.newTestOutputFile("DocForMetaTest.odt"));
		textDoc.close();
		// read empty document meta
		textDoc = (OdfTextDocument) OdfTextDocument.loadDocument(ResourceUtilities.getAbsolutePath("DocForMetaTest.odt"));
		OdfOfficeMeta meta = textDoc.getOfficeMetadata();
		Assert.assertNotNull(meta.getGenerator());
		Assert.assertNotNull(meta.getCreationDate());
		Assert.assertNull(meta.getCreator());
		Assert.assertNotNull(meta.getDate());
		Assert.assertTrue(meta.getEditingCycles() > 0);
		Assert.assertNotNull(meta.getEditingDuration());
		Assert.assertNull(meta.getLanguage());
		textDoc.close();
	}
}
