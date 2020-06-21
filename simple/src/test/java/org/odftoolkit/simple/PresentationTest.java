/*
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
 */
package org.odftoolkit.simple;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.odftoolkit.odfdom.dom.attribute.presentation.PresentationClassAttribute;
import org.odftoolkit.odfdom.dom.element.draw.DrawFillImageElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawGradientElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawHatchElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawMarkerElement;
import org.odftoolkit.odfdom.dom.element.style.StyleMasterPageElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawFrame;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeMasterStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.presentation.Slide;
import org.odftoolkit.simple.utils.ResourceUtilities;
import org.w3c.dom.NodeList;

/** @author cl93746 */
public class PresentationTest {

  Document odfdoc;

  public PresentationTest() {
    try {
      odfdoc = Document.loadDocument(ResourceUtilities.getAbsolutePath("presentation.odp"));
    } catch (Exception e) {
      Logger.getLogger(PresentationTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testPresentation() {
    try {
      PresentationDocument odpdoc = (PresentationDocument) odfdoc;

      Slide page = odpdoc.getSlideByName("slide-name-1");
      Assert.assertTrue((page != null) && page.getSlideName().equals("slide-name-1"));
      Assert.assertEquals(page, odpdoc.getSlideByIndex(0));

      page = odpdoc.getSlideByName("slide-name-2");
      Assert.assertTrue((page != null) && page.getSlideName().equals("slide-name-2"));
      Assert.assertEquals(page, odpdoc.getSlideByIndex(1));

      page = odpdoc.getSlideByName("slide-name-3");
      Assert.assertTrue((page != null) && page.getSlideName().equals("slide-name-3"));
      Assert.assertEquals(page, odpdoc.getSlideByIndex(2));

    } catch (Exception e) {
      Logger.getLogger(PresentationTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testMasterStyles() {
    try {
      OdfOfficeMasterStyles officeMasterStyles = odfdoc.getOfficeMasterStyles();
      Assert.assertNotNull(officeMasterStyles);

      // check if iterator has all two master pages
      testIterator(StyleMasterPageElement.class, officeMasterStyles.getMasterPages(), 2);

      // test "master-name-1"
      StyleMasterPageElement master = officeMasterStyles.getMasterPage("master-name-1");
      Assert.assertNotNull(master);
      Assert.assertEquals(master.getStyleNameAttribute(), "master-name-1");

      // test "master-name-2"
      master = officeMasterStyles.getMasterPage("master-name-2");
      Assert.assertNotNull(master);
      Assert.assertEquals(master.getStyleNameAttribute(), "master-name-2");

      // test handout master
      Assert.assertNotNull(officeMasterStyles.getHandoutMaster());

      // test layerset
      Assert.assertNotNull(officeMasterStyles.getLayerSet());
    } catch (Exception e) {
      Logger.getLogger(PresentationTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testStyles() {
    try {
      OdfOfficeStyles officeStyles = odfdoc.getDocumentStyles();
      Assert.assertNotNull(officeStyles);

      Assert.assertNotNull(officeStyles.getGradient("Linear_20_blue_2f_white"));
      testIterator(DrawGradientElement.class, officeStyles.getGradients().iterator(), 1);

      Assert.assertNotNull(officeStyles.getMarker("Arrow"));
      testIterator(DrawMarkerElement.class, officeStyles.getMarker().iterator(), 1);

      Assert.assertNotNull(officeStyles.getHatch("Black_20_0_20_Degrees"));
      testIterator(DrawHatchElement.class, officeStyles.getHatches().iterator(), 1);

      Assert.assertNotNull(officeStyles.getFillImage("Aqua"));
      testIterator(DrawFillImageElement.class, officeStyles.getFillImages().iterator(), 1);

      // check for some styles
      Assert.assertNotNull(officeStyles.getDefaultStyle(OdfStyleFamily.Graphic));
      Assert.assertNotNull(officeStyles.getStyle("standard", OdfStyleFamily.Graphic));
      Assert.assertNotNull(
          officeStyles.getStyle("master-name-1-outline1", OdfStyleFamily.Presentation));

      Iterator<OdfStyle> style_iter =
          officeStyles.getStylesForFamily(OdfStyleFamily.Presentation).iterator();
      Assert.assertNotNull(style_iter);
      Assert.assertTrue(style_iter.hasNext());
    } catch (Exception e) {
      Logger.getLogger(PresentationTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  private <T extends OdfElement> void testIterator(Class<T> clazz, Iterator<T> iter, int elements) {
    Assert.assertNotNull(iter);
    while (elements > 0) {
      Assert.assertTrue(iter.hasNext());
      Assert.assertTrue(clazz.isInstance(iter.next()));
      elements--;
    }

    Assert.assertFalse(iter.hasNext());
  }

  @Test
  public void testPresentationClassAttribute() {
    try {
      GraphicsDocument doc = GraphicsDocument.newGraphicsDocument();
      OdfFileDom dom = doc.getContentDom();
      OdfDrawFrame f = new OdfDrawFrame(dom);

      f.setPresentationClassAttribute(PresentationClassAttribute.Value.GRAPHIC.toString());
      Logger.getLogger(DocumentCreationTest.class.getName())
          .info(f.getPresentationClassAttribute());
    } catch (Exception e) {
      Logger.getLogger(PresentationTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testStyleUsageCount() {
    try {
      OdfOfficeAutomaticStyles s = odfdoc.getStylesDom().getAutomaticStyles();
      OdfStyle pr1 = s.getStyle("pr1", OdfStyleFamily.Presentation);
      int styleUserCount = pr1.getStyleUserCount();
      OdfStylesDom stylesDom = odfdoc.getStylesDom();
      XPath xpath = stylesDom.getXPath();
      NodeList elementsWithStyle =
          (NodeList)
              xpath.evaluate(
                  "//draw:frame[@presentation:style-name='pr1']",
                  stylesDom,
                  XPathConstants.NODESET);
      int elementsWithStyleCount = elementsWithStyle.getLength();
      Assert.assertTrue(styleUserCount == elementsWithStyleCount);
      //			//#bug51,the bug will be induced by using set attribute method
      //			OdfDrawFrame frame = (OdfDrawFrame) elementsWithStyle.item(0);
      //			frame.setPresentationStyleNameAttribute("pr2");
      //			styleUserCount = pr1.getStyleUserCount();
      //			elementsWithStyle = (NodeList)
      // xpath.evaluate("//draw:frame[@presentation:style-name='pr1']",
      //					odfdoc.getStylesDom(), XPathConstants.NODESET);
      //			elementsWithStyleCount = elementsWithStyle.getLength();
      //			Assert.assertTrue("Last part of bug51 still to be fixed..!!", styleUserCount ==
      // elementsWithStyleCount);
    } catch (Exception e) {
      Logger.getLogger(PresentationTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }
}
