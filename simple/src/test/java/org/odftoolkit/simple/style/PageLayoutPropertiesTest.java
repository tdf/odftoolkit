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
package org.odftoolkit.simple.style;

import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;

import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.odfdom.type.Percent;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.style.StyleTypeDefinitions.AdjustmentStyle;
import org.odftoolkit.simple.style.StyleTypeDefinitions.LineStyle;
import org.odftoolkit.simple.style.StyleTypeDefinitions.PrintOrientation;
import org.odftoolkit.simple.style.StyleTypeDefinitions.WritingMode;

public class PageLayoutPropertiesTest {

	@Test
	public void testGetSetMargins() {

		try {
			TextDocument newDoc = TextDocument.newTextDocument();
			MasterPage master = MasterPage.getOrCreateMasterPage(newDoc,
					"TestMaster");
			master.setMargins(10, 20, 0, -5);
			Assert.assertEquals(9.9996, master.getMarginTop());
			Assert.assertEquals(19.9992, master.getMarginBottom());
			Assert.assertEquals(0.0, master.getMarginLeft());
			Assert.assertEquals(-5.0011, master.getMarginRight());

		} catch (Exception e) {
			Logger.getLogger(PageLayoutPropertiesTest.class.getName()).log(
					Level.SEVERE, null, e);
			Assert.fail();
		}

	}

	@Test
	public void testGetSetPageSize() {
		try {
			TextDocument newDoc = TextDocument.newTextDocument();
			MasterPage master = MasterPage.getOrCreateMasterPage(newDoc,
					"TestMaster");
			master.setPageWidth(100);
			master.setPageHeight(300);
			Assert.assertEquals(100.0009, master.getPageWidth());
			Assert.assertEquals(300.0003, master.getPageHeight());

			master.setPageWidth(0);
			master.setPageHeight(-100);
			Assert.assertEquals(0.0, master.getPageWidth());
			Assert.assertEquals(-100.0009, master.getPageHeight());

		} catch (Exception e) {
			Logger.getLogger(PageLayoutPropertiesTest.class.getName()).log(
					Level.SEVERE, null, e);
			Assert.fail();
		}
	}

	@Test
	public void testGetSetNumFormat() {
		try {
			TextDocument newDoc = TextDocument.newTextDocument();
			MasterPage master = MasterPage.getOrCreateMasterPage(newDoc,
					"TestMaster");
			master.setNumberFormat(NumberFormat.HINDU_ARABIC_NUMBER.toString());
			Assert.assertEquals(NumberFormat.HINDU_ARABIC_NUMBER.toString(),
					master.getNumberFormat());
			master.setNumberFormat("");
			Assert.assertEquals("", master.getNumberFormat());
			master.setNumberFormat(null);
			Assert.assertNull(master.getNumberFormat());

		} catch (Exception e) {
			Logger.getLogger(PageLayoutPropertiesTest.class.getName()).log(
					Level.SEVERE, null, e);
			Assert.fail();
		}
	}

	@Test
	public void testGetSetWritingMode() {
		try {
			TextDocument newDoc = TextDocument.newTextDocument();
			MasterPage master = MasterPage.getOrCreateMasterPage(newDoc,
					"TestMaster");
			master.setWritingMode(WritingMode.LRTB);
			Assert.assertEquals(WritingMode.LRTB.toString(), master
					.getWritingMode());
			master.setNumberFormat(null);
			Assert.assertNull(master.getNumberFormat());

		} catch (Exception e) {
			Logger.getLogger(PageLayoutPropertiesTest.class.getName()).log(
					Level.SEVERE, null, e);
			Assert.fail();
		}
	}

	@Test
	public void testGetSetPrintOrientation() {
		try {
			TextDocument newDoc = TextDocument.newTextDocument();
			MasterPage master = MasterPage.getOrCreateMasterPage(newDoc,
					"TestMaster");
			master.setPrintOrientation(PrintOrientation.LANDSCAPE);
			Assert.assertEquals(PrintOrientation.LANDSCAPE.toString(), master
					.getPrintOrientation());
			master.setPrintOrientation(null);
			Assert.assertNull(master.getPrintOrientation());

		} catch (Exception e) {
			Logger.getLogger(PageLayoutPropertiesTest.class.getName()).log(
					Level.SEVERE, null, e);
			Assert.fail();
		}
	}

	@Test
	public void testGetSetMax() {
		try {
			TextDocument newDoc = TextDocument.newTextDocument();
			MasterPage master = MasterPage.getOrCreateMasterPage(newDoc,
					"TestMaster");
			master.setFootnoteMaxHeight(10);
			Assert.assertEquals(9.9996, master
					.getFootnoteMaxHeight());
			master.setFootnoteMaxHeight(0);
			Assert.assertEquals(0.0,master.getFootnoteMaxHeight());

		} catch (Exception e) {
			Logger.getLogger(PageLayoutPropertiesTest.class.getName()).log(
					Level.SEVERE, null, e);
			Assert.fail();
		}
	}

	@Test
	public void testGetSetFootnoteSep() {
		try {
			TextDocument newDoc = TextDocument.newTextDocument();
			MasterPage master = MasterPage.getOrCreateMasterPage(newDoc,
					"TestMaster");
			master.setFootnoteSepProperties(AdjustmentStyle.CENTER, Color.AQUA,
					10, 10, LineStyle.DASH_BOLD, Percent.valueOf("15%"), 10);
			Assert.assertEquals(AdjustmentStyle.CENTER.toString(),master.getFootnoteSepAdjustment());
			Assert.assertEquals(Color.AQUA.toString(), master.getFootnoteSepColor());
			Assert.assertEquals(9.9996, master.getFootnoteSepDistanceAfterSep());
			Assert.assertEquals(9.9996, master.getFootnoteSepDistanceBeforeSep());
			Assert.assertEquals(LineStyle.DASH_BOLD.toString(), master.getFootnoteSepLineStyle());
			Assert.assertEquals(0.15, master.getFootnoteSepWidth());
			Assert.assertEquals(9.9996, master.getFootnoteSepThickness());

			master.setFootnoteSepProperties(null, null, 0, 0, null, null, 0);
			Assert.assertEquals(AdjustmentStyle.LEFT.toString(),master.getFootnoteSepAdjustment());
			Assert.assertNull(master.getFootnoteSepColor());
			Assert.assertEquals(0.0,master.getFootnoteSepDistanceAfterSep());
			Assert.assertEquals(0.0,master.getFootnoteSepDistanceBeforeSep());
			Assert.assertEquals(LineStyle.NONE.toString(),master.getFootnoteSepLineStyle());
			Assert.assertEquals(0.0, master.getFootnoteSepWidth());
			Assert.assertEquals(0.0,master.getFootnoteSepThickness());


		} catch (Exception e) {
			Logger.getLogger(PageLayoutPropertiesTest.class.getName()).log(
					Level.SEVERE, null, e);
			Assert.fail();
		}
	}
}
