/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * <p>Use is subject to license terms.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0. You can also obtain a copy of the License at
 * http://odftoolkit.org/docs/license.txt
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 *
 * <p>See the License for the specific language governing permissions and limitations under the
 * License.
 *
 * <p>*********************************************************************
 */
package org.odftoolkit.odfdom.doc;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
  private static final String SOURCE2 = "svgTitleTest.odt";
  private static final String TARGET = "loadsavetest.odt";
  private static final String TARGET2 = "loadsavetest2.odt";
  private static final String FOREIGN_ATTRIBUTE_NAME = "foreignAttribute";
  private static final String FOREIGN_ATTRIBUTE_VALUE = "foreignAttributeValue";
  private static final String FOREIGN_ELEMENT_TEXT = "foreignText";

  public LoadSaveTest() {}

  @Test
  public void testLoadSave() {
    try {
      OdfDocument odfDocument =
          OdfDocument.loadDocument(ResourceUtilities.getAbsoluteInputPath(SOURCE));
      Assert.assertTrue(odfDocument.getPackage().contains("content.xml"));
      String baseURI1 = odfDocument.getBaseURI();
      String baseURI2 = ResourceUtilities.getTestInputURI(SOURCE).toString();
      //			Assert.assertTrue(baseURI2.compareToIgnoreCase(baseURI1) == 0);
      System.out.println("SOURCE URI1:" + baseURI1);
      System.out.println("SOURCE URI2:" + baseURI2);

      OdfFileDom odfContent = odfDocument.getContentDom();
      String odf12 = OfficeVersionAttribute.Value._1_2.toString();
      OfficeDocumentContentElement content =
          (OfficeDocumentContentElement) odfContent.getDocumentElement();
      String version = content.getOfficeVersionAttribute();
      Assert.assertFalse(version.equals(odf12));

      NodeList lst = odfContent.getElementsByTagNameNS(OdfDocumentNamespace.TEXT.getUri(), "p");
      Node node = lst.item(0);
      String oldText = "Changed!!!";
      node.setTextContent(oldText);

      odfDocument.save(ResourceUtilities.getTestOutputFile(TARGET));
      odfDocument = OdfDocument.loadDocument(ResourceUtilities.getAbsoluteOutputPath(TARGET));

      odfContent = odfDocument.getContentDom();
      // ToDo: Will be used for issue 60: Load & Save of previous ODF versions (ie. ODF 1.0, ODF
      // 1.1)
      // Assert.assertTrue(odfContent.getRootElement().getOfficeVersionAttribute().equals(odf12));
      lst = odfContent.getElementsByTagNameNS(OdfDocumentNamespace.TEXT.getUri(), "p");
      node = lst.item(0);
      String newText = node.getTextContent();
      Assert.assertTrue(newText.equals(oldText));

      node = lst.item(1);
      // check foreign attribute without namespace
      Element foreignElement = (Element) node.getChildNodes().item(0);
      String foreignText = foreignElement.getTextContent();
      Assert.assertTrue(foreignText.equals(FOREIGN_ELEMENT_TEXT));

      // check foreign element without namespace
      Attr foreignAttr = (Attr) node.getAttributes().getNamedItem(FOREIGN_ATTRIBUTE_NAME);
      String foreignAttrValue = foreignAttr.getValue();
      Assert.assertTrue(foreignAttrValue.equals(FOREIGN_ATTRIBUTE_VALUE));

    } catch (Exception e) {
      Logger.getLogger(LoadSaveTest.class.getName())
          .log(Level.SEVERE, e.getMessage() + ExceptionUtils.getStackTrace(e), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testLoadSave2() {
    try {
      System.out.println("\n\nStarting SVG Title Test (testLoadSave2)");
      OdfDocument odfDocument =
          OdfDocument.loadDocument(ResourceUtilities.getAbsoluteInputPath(SOURCE2));
      Assert.assertTrue(odfDocument.getPackage().contains("content.xml"));
      String baseURI1 = odfDocument.getBaseURI();
      System.out.println("SOURCE URI: " + baseURI1);

      OdfFileDom odfContent = odfDocument.getContentDom();
      OfficeDocumentContentElement content =
          (OfficeDocumentContentElement) odfContent.getDocumentElement();
      NodeList svgTitleList =
          odfContent.getElementsByTagNameNS(OdfDocumentNamespace.SVG.getUri(), "title");
      Assert.assertTrue(
          "There should be a single <svg:title> within the document",
          svgTitleList.getLength() == 1);
      NodeList svgDescList =
          odfContent.getElementsByTagNameNS(OdfDocumentNamespace.SVG.getUri(), "desc");
      Assert.assertTrue(
          "There should be no <svg:desc> within the document", svgDescList.getLength() == 0);

      odfDocument.save(ResourceUtilities.getTestOutputFile(TARGET2));
      odfDocument = OdfDocument.loadDocument(ResourceUtilities.getAbsoluteOutputPath(TARGET2));
      System.out.println("TARGET: " + ResourceUtilities.getAbsoluteOutputPath(TARGET2));

      odfContent = odfDocument.getContentDom();
      svgTitleList = odfContent.getElementsByTagNameNS(OdfDocumentNamespace.SVG.getUri(), "title");
      Assert.assertTrue(
          "There should be a single <svg:title> within the document",
          svgTitleList.getLength() == 1);
      svgDescList = odfContent.getElementsByTagNameNS(OdfDocumentNamespace.SVG.getUri(), "desc");
      Assert.assertTrue(
          "There should be no <svg:desc> within the document", svgDescList.getLength() == 0);
    } catch (Exception e) {
      Logger.getLogger(LoadSaveTest.class.getName())
          .log(Level.SEVERE, e.getMessage() + ExceptionUtils.getStackTrace(e), e);
      Assert.fail(e.getMessage());
    }
  }
}
