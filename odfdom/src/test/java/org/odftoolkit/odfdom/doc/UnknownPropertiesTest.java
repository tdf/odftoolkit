/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.style.OdfStyleTabStop;
import org.odftoolkit.odfdom.doc.style.OdfStyleTabStops;
import org.odftoolkit.odfdom.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.OdfNamespace;
import org.odftoolkit.odfdom.OdfElement;
import org.odftoolkit.odfdom.dom.OdfNamespaceNames;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.OdfStylePropertiesBase;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
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
                NodeList lst = odfContent.getElementsByTagNameNS(OdfNamespaceNames.TEXT.getNamespaceUri(), "p");
                Node node = lst.item(0);
                OdfTextParagraph para = (OdfTextParagraph) node;
                OdfStyleBase paraLocalStyle = para.getAutomaticStyle();

                OdfStylePropertiesBase paraProperties = paraLocalStyle.getPropertiesElement(OdfStylePropertiesSet.ParagraphProperties);

                OdfStyleTabStops tabstops = OdfElement.findFirstChildNode(OdfStyleTabStops.class, paraProperties );
                Assert.assertNotNull(tabstops);

                OdfStyleTabStop tabstop = OdfElement.findFirstChildNode(OdfStyleTabStop.class, tabstops);
                Assert.assertNotNull(tabstop);

                Assert.assertEquals(tabstop.getStylePositionAttribute().toString(), "7.643cm");

                if (i == 0) {
                    odfDocument.save(ResourceUtilities.newTestOutputFile(TARGET));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
