/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
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
package org.odftoolkit.odfdom.doc;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.dom.element.style.StyleTableColumnPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTablePropertiesElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.w3c.dom.NodeList;

public class CreateTableTest {

	public CreateTableTest() {
	}

	@Test
	public void testCreateTable1() {
		try {
			OdfFileDom doc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath("empty.odt")).getContentDom();

			// find the last paragraph
			NodeList lst = doc.getElementsByTagNameNS(
					TextPElement.ELEMENT_NAME.getUri(),
					TextPElement.ELEMENT_NAME.getLocalName());
			Assert.assertTrue(lst.getLength() > 0);
			OdfTextParagraph p0 = (OdfTextParagraph) lst.item(lst.getLength() - 1);

			TableTableElement table = doc.newOdfElement(TableTableElement.class);

			TableTableRowElement tr = (TableTableRowElement) table.appendChild(
					doc.newOdfElement(TableTableRowElement.class));
			TableTableCellElement td1 = (TableTableCellElement) tr.appendChild(
					doc.newOdfElement(TableTableCellElement.class));
			OdfTextParagraph p1 = doc.newOdfElement(OdfTextParagraph.class);
			p1.appendChild(doc.createTextNode("content 1"));
			td1.appendChild(p1);

			TableTableCellElement td2 = (TableTableCellElement) tr.appendChild(
					doc.newOdfElement(TableTableCellElement.class));
			OdfTextParagraph p2 = doc.newOdfElement(OdfTextParagraph.class);
			p2.appendChild(doc.createTextNode("cell 2"));
			td2.appendChild(p2);

			TableTableCellElement td3 = (TableTableCellElement) tr.appendChild(
					doc.newOdfElement(TableTableCellElement.class));
			OdfTextParagraph p3 = doc.newOdfElement(OdfTextParagraph.class);
			p3.appendChild(doc.createTextNode("table cell content 3"));
			td3.appendChild(p3);

			p0.getParentNode().insertBefore(table, p0);

			table.setProperty(StyleTablePropertiesElement.Width, "12cm");
			table.setProperty(StyleTablePropertiesElement.Align, "left");

			td1.setProperty(StyleTableColumnPropertiesElement.ColumnWidth, "2cm");

			td2.setProperty(StyleTableColumnPropertiesElement.ColumnWidth, "4cm");

			td3.setProperty(StyleTableColumnPropertiesElement.ColumnWidth, "6cm");

			doc.getDocument().save(ResourceUtilities.newTestOutputFile("tabletest.odt"));

		} catch (Exception e) {
			Logger.getLogger(CreateTableTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
}
