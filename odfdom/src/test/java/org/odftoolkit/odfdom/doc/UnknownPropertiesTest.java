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
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.OdfStylePropertiesBase;
import org.odftoolkit.odfdom.dom.element.style.StyleTabStopElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTabStopsElement;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UnknownPropertiesTest {

    private static final String SOURCE = "unknown_properties.odt";
    private static final String TARGET = "unknown_properties_saved.odt";

    public UnknownPropertiesTest() {
    }

    @Test
    public void unknownPropertiesTest() {
        try {
            for (int i = 0; i < 2; ++i) {
                OdfDocument odfDocument = i == 0 ? OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(SOURCE)) : OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TARGET));

                Document odfContent = odfDocument.getContentDom();
                NodeList lst = odfContent.getElementsByTagNameNS(OdfDocumentNamespace.TEXT.getUri(), "p");
                Node node = lst.item(0);
                OdfTextParagraph para = (OdfTextParagraph) node;
                OdfStyleBase paraLocalStyle = para.getAutomaticStyle();

                OdfStylePropertiesBase paraProperties = paraLocalStyle.getPropertiesElement(OdfStylePropertiesSet.ParagraphProperties);

                StyleTabStopsElement tabstops = OdfElement.findFirstChildNode(StyleTabStopsElement.class, paraProperties );
                Assert.assertNotNull(tabstops);

                StyleTabStopElement tabstop = OdfElement.findFirstChildNode(StyleTabStopElement.class, tabstops);
                Assert.assertNotNull(tabstop);

                Assert.assertEquals(tabstop.getStylePositionAttribute().toString(), "7.643cm");

                if (i == 0) {
                    odfDocument.save(ResourceUtilities.newTestOutputFile(TARGET));
                }
            }
        } catch (Exception e) {
            Logger.getLogger(UnknownPropertiesTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            Assert.fail(e.getMessage());
        }
    }
}
