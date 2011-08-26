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
package org.openoffice.odf.dom.test;

import org.junit.Assert;
import org.junit.Test;
import org.openoffice.odf.doc.OdfDocument;
import org.openoffice.odf.doc.element.style.OdfTabStop;
import org.openoffice.odf.doc.element.style.OdfTabStops;
import org.openoffice.odf.doc.element.text.OdfParagraph;
import org.openoffice.odf.dom.OdfNamespace;
import org.openoffice.odf.dom.element.OdfElement;
import org.openoffice.odf.dom.element.OdfStyleBase;
import org.openoffice.odf.dom.element.OdfStylePropertiesBase;
import org.openoffice.odf.dom.style.props.OdfStylePropertiesSet;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UnknownPropertiesTest {

    private static final String SOURCE = "test/resources/unknown_properties.odt";
    private static final String TARGET = "build/test/unknown_properties_saved.odt";

    public UnknownPropertiesTest() {
    }

    @Test
    public void unknownPropertiesTest() {
        try {
            for (int i = 0; i < 2; ++i) {
                OdfDocument odfDocument = i == 0 ? OdfDocument.loadDocument(SOURCE) : OdfDocument.loadDocument(TARGET);

                Document odfContent = odfDocument.getContentDom();
                NodeList lst = odfContent.getElementsByTagNameNS(OdfNamespace.TEXT.getUri(), "p");
                Node node = lst.item(0);
                OdfParagraph para = (OdfParagraph) node;
                OdfStyleBase paraLocalStyle = para.getAutomaticStyle();

                OdfStylePropertiesBase paraProperties = paraLocalStyle.getPropertiesElement(OdfStylePropertiesSet.ParagraphProperties);

                OdfTabStops tabstops = OdfElement.findFirstChildNode(OdfTabStops.class, paraProperties );
                Assert.assertNotNull(tabstops);

                OdfTabStop tabstop = OdfElement.findFirstChildNode(OdfTabStop.class, tabstops);
                Assert.assertNotNull(tabstop);

                Assert.assertEquals(tabstop.getPosition(), "7.643cm");

                if (i == 0) {
                    odfDocument.save(TARGET);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
