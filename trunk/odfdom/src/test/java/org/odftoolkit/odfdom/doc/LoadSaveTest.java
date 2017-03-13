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
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.attribute.office.OfficeVersionAttribute;
import org.odftoolkit.odfdom.dom.element.office.OfficeDocumentContentElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LoadSaveTest {

	private static final String SOURCE = "not-only-odf.odt";
	private static final String TARGET = "loadsavetest.odt";
	private static final String FOREIGN_ATTRIBUTE_NAME = "foreignAttribute";
	private static final String FOREIGN_ATTRIBUTE_VALUE = "foreignAttributeValue";
	private static final String FOREIGN_ELEMENT_TEXT = "foreignText";

	public LoadSaveTest() {
	}

	@Test
	public void testLoadSave() {
		try {
			OdfDocument odfDocument = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(SOURCE));
			Assert.assertTrue(odfDocument.getPackage().contains("content.xml"));
			String baseURI1 = odfDocument.getBaseURI();
			String baseURI2 = ResourceUtilities.getURI(SOURCE).toString();
//			Assert.assertTrue(baseURI2.compareToIgnoreCase(baseURI1) == 0);
			System.out.println("SOURCE URI1:"+baseURI1);
			System.out.println("SOURCE URI2:"+baseURI2);
			
			OdfFileDom odfContent = odfDocument.getContentDom();
			String odf12 = OfficeVersionAttribute.Value._1_2.toString();
			OfficeDocumentContentElement content = (OfficeDocumentContentElement) odfContent.getDocumentElement();
			String version = content.getOfficeVersionAttribute();
			Assert.assertFalse(version.equals(odf12));

			NodeList lst = odfContent.getElementsByTagNameNS(OdfDocumentNamespace.TEXT.getUri(), "p");
			Node node = lst.item(0);
			String oldText = "Changed!!!";
			node.setTextContent(oldText);

			odfDocument.save(ResourceUtilities.newTestOutputFile(TARGET));
			odfDocument = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TARGET));

			odfContent = odfDocument.getContentDom();
			// ToDo: Will be used for issue 60: Load & Save of previous ODF versions (ie. ODF 1.0, ODF 1.1)
			//Assert.assertTrue(odfContent.getRootElement().getOfficeVersionAttribute().equals(odf12));
			lst = odfContent.getElementsByTagNameNS(OdfDocumentNamespace.TEXT.getUri(), "p");
			node = lst.item(0);
			String newText = node.getTextContent();
			Assert.assertTrue(newText.equals(oldText));

			node = lst.item(1);
			//check foreign attribute without namespace
			Element foreignElement = (Element) node.getChildNodes().item(0);
			String foreignText = foreignElement.getTextContent();
			Assert.assertTrue(foreignText.equals(FOREIGN_ELEMENT_TEXT));

			//check foreign element without namespace
			Attr foreignAttr = (Attr) node.getAttributes().getNamedItem(FOREIGN_ATTRIBUTE_NAME);
			String foreignAttrValue = foreignAttr.getValue();
			Assert.assertTrue(foreignAttrValue.equals(FOREIGN_ATTRIBUTE_VALUE));



		} catch (Exception e) {
			Logger.getLogger(LoadSaveTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}
}
