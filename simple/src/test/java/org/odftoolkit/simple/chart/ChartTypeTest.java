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
package org.odftoolkit.simple.chart;

import java.awt.Rectangle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class ChartTypeTest {

	@Test
	public void testEnumValueOf() throws Exception {
		try {
			TextDocument tdoc = TextDocument.loadDocument(ResourceUtilities.getAbsolutePath("headerFooterHidden.odt"));
			String title = "title_name";
			String[] labels = {"hello", "hi","odf"};
			String[] legends = {"hello1", "hi1","odf1"};
			double[][] data = {{1.11, 43.23}, {3.22, 4.00, 5.43}, {121.99, 123.1, 423.00}};
			DataSet dataset = new DataSet(labels, legends, data);
			Rectangle rect = new Rectangle();
			Chart chart = tdoc.createChart(title, dataset, rect);
			chart.setChartType(ChartType.AREA);
			
			String ctype = chart.getChartType().toString();
			ChartType chartType = ChartType.enumValueOf(ctype);
			Assert.assertEquals(chartType, ChartType.AREA);

			//save
			//tdoc.save(ResourceUtilities.getAbsolutePath("headerFooterHidden.odt"));
		} catch (Exception e) {
			Logger.getLogger(ChartTypeTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
		
	}

}
