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

package org.odftoolkit.simple.chart;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.type.CellRangeAddressList;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class DataSetTest {

  private static final String TEST_FILE = "ChartTest.ods";

  @Test
  public void testSetValuesFromSpreadsheet() {
    try {
      SpreadsheetDocument sheet =
          (SpreadsheetDocument)
              Document.loadDocument(ResourceUtilities.getTestResourceAsStream(TEST_FILE));
      DataSet data = new DataSet();
      data.setValues(CellRangeAddressList.valueOf("A.A1:A.C4"), sheet, true, true, true);
      Assert.assertEquals("A.A1:A.C4", data.getCellRangeAddress().toString());
      printDataset(data);

      data.setValues(CellRangeAddressList.valueOf("A.A1:A.C4"), sheet, true, true, false);
      printDataset(data);
      System.out.println("Max Item count:" + data.getMaxLengthOfDataSeries());
      data.setValues(CellRangeAddressList.valueOf("A.A1:A.C4"), sheet, false, true, false);
      printDataset(data);
      System.out.println("Max Item count:" + data.getMaxLengthOfDataSeries());
      data.setValues(CellRangeAddressList.valueOf("A.A1:A.C4"), sheet, true, false, true);
      printDataset(data);
      System.out.println("Max Item count:" + data.getMaxLengthOfDataSeries());
    } catch (Exception e) {
      Logger.getLogger(DataSetTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
    }
  }

  @Test
  public void testSetValuesFromArray() {
    int row = 2, column = 3;
    double[][] data = new double[column][row];
    String[] labels = new String[row];
    String[] legends = new String[column];
    for (int i = 0; i < column; i++) {
      legends[i] = "legend" + (i + 1);
      for (int j = 0; j < row; j++) {
        if (i == 0) labels[j] = "label" + (j + 1);
        data[i][j] = i * j + i + j;
      }
    }
    DataSet dataset = new DataSet(labels, legends, data);
    printDataset(dataset);
  }

  private void printDataset(DataSet data) {
    String[] firstrow = data.getLocalTableFirstRow();
    String[] firstcolumn = data.getLocalTableFirstColumn();
    Double[][] tabledata = data.getLocalTableData();
    System.out.println();
    System.out.print("\t\t");
    for (int j = 0; j < firstrow.length; j++) {
      System.out.print(firstrow[j] + "\t");
    }
    System.out.println();

    for (int i = 0; i < firstcolumn.length; i++) {
      System.out.print(firstcolumn[i] + "\t\t");
      for (int j = 0; j < firstrow.length; j++)
        if (tabledata[i][j] == null) System.out.printf("nul\t");
        else System.out.printf("%.2f\t", tabledata[i][j].doubleValue());
      System.out.println();
    }
  }

  @Test
  public void testGetCellRanges() {
    Vector<String> seriesCellRange = new Vector<String>();
    Vector<String> legendCellAddr = new Vector<String>();
    DataSet ds = new DataSet();
    String labelCellRange =
        ds.getCellRanges("A.A2:A.E21", false, true, false, seriesCellRange, legendCellAddr);
    Assert.assertEquals("A.A2:A.A21", labelCellRange);
    Assert.assertEquals(4, seriesCellRange.size());
    Assert.assertEquals("A.B2:A.B21", seriesCellRange.firstElement());
    Assert.assertEquals("A.E2:A.E21", seriesCellRange.lastElement());
    Assert.assertEquals(4, legendCellAddr.size());
    Assert.assertNull(legendCellAddr.firstElement());
    Assert.assertNull(legendCellAddr.lastElement());

    labelCellRange =
        ds.getCellRanges("A.A1:A.E21", true, true, true, seriesCellRange, legendCellAddr);
    Assert.assertEquals("A.B1:A.E1", labelCellRange);
    Assert.assertEquals(20, seriesCellRange.size());
    Assert.assertEquals("A.B2:A.E2", seriesCellRange.firstElement());
    Assert.assertEquals("A.B21:A.E21", seriesCellRange.lastElement());
    Assert.assertEquals(20, legendCellAddr.size());
    Assert.assertEquals("A.A2", legendCellAddr.firstElement());
    Assert.assertEquals("A.A21", legendCellAddr.lastElement());

    labelCellRange =
        ds.getCellRanges("A.B1:A.E21", true, false, false, seriesCellRange, legendCellAddr);
    Assert.assertNull(labelCellRange);
    Assert.assertEquals(4, seriesCellRange.size());
    Assert.assertEquals("A.B2:A.B21", seriesCellRange.firstElement());
    Assert.assertEquals("A.E2:A.E21", seriesCellRange.lastElement());
    Assert.assertEquals(4, legendCellAddr.size());
    Assert.assertEquals("A.B1", legendCellAddr.firstElement());
    Assert.assertEquals("A.E1", legendCellAddr.lastElement());

    labelCellRange = ds.getLocalTableCellRanges(4, 20, seriesCellRange, legendCellAddr);
    Assert.assertEquals("local-table.A2:local-table.A21", labelCellRange);
    Assert.assertEquals(4, seriesCellRange.size());
    Assert.assertEquals("local-table.B2:local-table.B21", seriesCellRange.firstElement());
    Assert.assertEquals("local-table.E2:local-table.E21", seriesCellRange.lastElement());
    Assert.assertEquals(4, legendCellAddr.size());
    Assert.assertEquals("local-table.B1", legendCellAddr.firstElement());
    Assert.assertEquals("local-table.E1", legendCellAddr.lastElement());
  }
}
