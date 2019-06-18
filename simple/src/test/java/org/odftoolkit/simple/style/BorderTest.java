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

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.style.StyleTypeDefinitions.CellBordersType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.SupportedLinearMeasure;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class BorderTest {

	static final String filename = "testGetCellAt.ods";

	@Test
	public void testGetSetBorder() {
		Border borderbase1 = new Border(new Color("#ff3333"), 5, SupportedLinearMeasure.PT);
		Border borderbase2 = new Border(new Color("#0000ff"), 0.0154, 0.0008, 0.0008, SupportedLinearMeasure.IN);
		Border borderbase3 = new Border(new Color("#ff3333"), 0.0362, 0.0008, 0.0008, SupportedLinearMeasure.IN);
		Border borderbase4 = new Border(new Color("#00ccff"), 0.0701, 0.0008, 0.0346, SupportedLinearMeasure.IN);
		try {
			SpreadsheetDocument doc = SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(filename));
			Table table = doc.getTableByName("A");
			Cell cell1 = table.getCellByPosition("A8");
			Border border1 = cell1.getBorder(CellBordersType.BOTTOM);
			Border border11 = cell1.getBorder(CellBordersType.LEFT);
			Assert.assertEquals(borderbase1, border1);
			Assert.assertEquals(borderbase1, border11);

			Cell cell2 = table.getCellByPosition("A10");
			Border border2 = cell2.getBorder(CellBordersType.TOP);
			Assert.assertEquals(borderbase2, border2);
			Border border3 = cell2.getBorder(CellBordersType.DIAGONALBLTR);
			Assert.assertEquals(borderbase3, border3);

			Cell cell3 = table.getCellByPosition("A12");
			Border border4 = cell3.getBorder(CellBordersType.LEFT);
			Border border41 = cell3.getBorder(CellBordersType.RIGHT);
			Assert.assertEquals(borderbase4, border4);
			Assert.assertEquals(borderbase4, border41);

			Cell cell4 = table.getCellByPosition("B8");
			cell4.setBorders(CellBordersType.ALL_FOUR, borderbase1);
			Border border5 = cell4.getBorder(CellBordersType.BOTTOM);
			Border border51 = cell4.getBorder(CellBordersType.LEFT);
			Assert.assertEquals(borderbase1, border5);
			Assert.assertEquals(borderbase1, border51);

			Cell cell5 = table.getCellByPosition("B10");
			cell5.setBorders(CellBordersType.TOP, borderbase2);
			cell5.setBorders(CellBordersType.DIAGONALBLTR, borderbase3);
			Assert.assertEquals(borderbase2, cell5.getBorder(CellBordersType.TOP));
			Assert.assertEquals(borderbase3, cell5.getBorder(CellBordersType.DIAGONALBLTR));

			Cell cell6 = table.getCellByPosition("B12");
			cell6.setBorders(CellBordersType.LEFT_RIGHT, borderbase4);
			Assert.assertEquals(borderbase4, cell6.getBorder(CellBordersType.LEFT));
			Assert.assertEquals(borderbase4, cell6.getBorder(CellBordersType.RIGHT));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}

	}


	@Test
	public void testSetWidth() {
		Border borderbase = new Border(new Color("#00ccff"), 0.0701, 0.0008, 0.0346, SupportedLinearMeasure.IN);
		try {
			SpreadsheetDocument doc = SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(filename));
			Table table = doc.getTableByName("A");
			//setWidth
			borderbase.setWidth(0.056);

			Cell cell = table.getCellByPosition("A14");
			cell.setBorders(CellBordersType.LEFT, borderbase);
			cell.setBorders(CellBordersType.TOP, borderbase);

			//verification
			Border thisBorder = cell.getBorder(CellBordersType.LEFT);
			thisBorder = cell.getBorder(CellBordersType.TOP);
			Assert.assertEquals(0.056, thisBorder.getWidth());
			//save
			//doc.save(ResourceUtilities.newTestOutputFile("testSetWidth.ods"));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}

	}


	@Test
	public void testSGetInnerLineWidth() {
		Border borderbase = new Border(new Color("#00ccff"), 0.0701, 0.0008, 0.0346, SupportedLinearMeasure.IN);
		try {
			SpreadsheetDocument doc = SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(filename));
			Table table = doc.getTableByName("A");
			//setWidth
			borderbase.setInnerLineWidth(0.0156);

			Cell cell = table.getCellByPosition("A14");
			cell.setBorders(CellBordersType.LEFT, borderbase);
			cell.setBorders(CellBordersType.TOP, borderbase);

			//verification
			Border thisBorder = cell.getBorder(CellBordersType.LEFT);
			thisBorder = cell.getBorder(CellBordersType.TOP);
			Assert.assertEquals(0.0156, thisBorder.getInnerLineWidth());
			//save
			//doc.save(ResourceUtilities.newTestOutputFile("testSetWidth.ods"));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}

	}


	@Test
	public void testSGetDistance() {
		Border borderbase = new Border(new Color("#00ccff"), 0.0701, 0.0008, 0.0346, SupportedLinearMeasure.IN);
		try {
			SpreadsheetDocument doc = SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(filename));
			Table table = doc.getTableByName("A");
			//setWidth
		//	borderbase.setInnerLineWidth(0.0156);
			borderbase.setDistance(0.123);

			Cell cell = table.getCellByPosition("A14");
			cell.setBorders(CellBordersType.LEFT, borderbase);
			cell.setBorders(CellBordersType.TOP, borderbase);

			//verification
			Border thisBorder = cell.getBorder(CellBordersType.LEFT);
			thisBorder = cell.getBorder(CellBordersType.TOP);
			Assert.assertEquals(0.123, thisBorder.getDistance());
			//save
			//doc.save(ResourceUtilities.newTestOutputFile("testSetWidth.ods"));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}

	}


	@Test
	public void testSGetOuterLineWidth() {
		Border borderbase = new Border(new Color("#00ccff"), 0.0701, 0.0008, 0.0346, SupportedLinearMeasure.IN);
		try {
			SpreadsheetDocument doc = SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(filename));
			Table table = doc.getTableByName("A");

			System.out.println(borderbase.getOuterLineWidth());
			borderbase.setOuterLineWidth(0.125);
			System.out.println(borderbase.getOuterLineWidth());

			Cell cell = table.getCellByPosition("A14");
			cell.setBorders(CellBordersType.LEFT, borderbase);
			cell.setBorders(CellBordersType.TOP, borderbase);

			//verification
			Border thisBorder = cell.getBorder(CellBordersType.LEFT);
			thisBorder = cell.getBorder(CellBordersType.TOP);
			Assert.assertEquals(0.125, thisBorder.getOuterLineWidth());
			//save
			//doc.save(ResourceUtilities.newTestOutputFile("testSetWidth.ods"));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}


	@Test
	public void testSGetLinearMeasure() {
		Border borderbase = new Border(new Color("#00ccff"), 0.0701, 0.0008, 0.0346, SupportedLinearMeasure.IN);
		try {
			SpreadsheetDocument doc = SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(filename));
			Table table = doc.getTableByName("A");

			borderbase.setLinearMeasure(StyleTypeDefinitions.SupportedLinearMeasure.CM);

			Cell cell = table.getCellByPosition("A14");
			cell.setBorders(CellBordersType.LEFT, borderbase);
			cell.setBorders(CellBordersType.TOP, borderbase);

			//verification
			Border thisBorder = cell.getBorder(CellBordersType.LEFT);
			thisBorder = cell.getBorder(CellBordersType.TOP);
			Assert.assertEquals(StyleTypeDefinitions.SupportedLinearMeasure.CM, thisBorder.getLinearMeasure());
			//save
			//doc.save(ResourceUtilities.newTestOutputFile("testSetWidth.ods"));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}
