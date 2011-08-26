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
package org.odftoolkit.odfdom.dom.example;

import java.util.ArrayList;
import static java.util.logging.Level.INFO;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.style.OdfDefaultStyle;
import org.odftoolkit.odfdom.doc.style.OdfStyle;
import org.odftoolkit.odfdom.OdfElement;
import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.style.StyleGraphicPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTablePropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTableRowPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfStyleProperty;
import org.odftoolkit.odfdom.utils.NodeAction;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.w3c.dom.Node;

/**
 *
 * @author j
 */
public class StyleExamples {

    private static String TEST_FILE = "test2.odt";
    private Logger mLog = Logger.getLogger(StyleExamples.class.getName());

    static class DumpPropertyAndText extends NodeAction<ArrayList<String>> {

        OdfStyleProperty desiredProperty; // = OdfTextProperties.FontName;

        public DumpPropertyAndText(OdfStyleProperty desiredProperty) {
            this.desiredProperty = desiredProperty;
        }

        @Override
		protected void apply(Node textNode, ArrayList<String> fontAndText, int depth) {

            if (textNode.getNodeType() != Node.TEXT_NODE) {
                return;
            }
            if (textNode.hasChildNodes()) {
                return;
            }

            Logger logger = Logger.getLogger(StyleExamples.class.getName());
            logger.finest(textNode.getParentNode().toString());

            String teksto = textNode.getTextContent().trim();
            if (teksto.length() == 0) {
                return;
            }

            String font = StyleUtils.findActualStylePropertyValueForNode(textNode, desiredProperty);

            logger.finest(font + ": " + teksto);
            fontAndText.add(font + ": " + teksto);
        }
    }

    @Test
    public void displayActualFontForEachTextNode() throws Exception {
        OdfDocument odfDocument = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE));

        OdfElement documentRoot = (OdfElement) odfDocument.getContentDom().getDocumentElement();

        ArrayList<String> fontAndText = new ArrayList<String>();

        DumpPropertyAndText dumpFontAndText = new DumpPropertyAndText(StyleTextPropertiesElement.FontName);
        dumpFontAndText.performAction(documentRoot, fontAndText);

        Assert.assertEquals("Thorndale: Hello", fontAndText.get(0));
        Assert.assertEquals("Thorndale: world", fontAndText.get(1));
        Assert.assertEquals("Thorndale: absatz", fontAndText.get(2));
        Assert.assertEquals("Cumberland: z", fontAndText.get(3));
        Assert.assertEquals("Cumberland: we", fontAndText.get(4));
        Assert.assertEquals("Cumberland: i", fontAndText.get(5));
        Assert.assertEquals("Thorndale: Absatz", fontAndText.get(6));
        Assert.assertEquals("Thorndale: drei", fontAndText.get(7));
        Assert.assertEquals("Thorndale: num 1", fontAndText.get(8));
        Assert.assertEquals("Thorndale: num 2", fontAndText.get(9));
        Assert.assertEquals("Thorndale: bullet1", fontAndText.get(10));
        Assert.assertEquals("Thorndale: bullet2", fontAndText.get(11));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void dumpAllStyles() throws Exception {
        if (mLog.isLoggable(INFO)) {
            OdfDocument odfdoc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE));
            mLog.info("Parsed document.");

            OdfElement e = (OdfElement) odfdoc.getContentDom().getDocumentElement();
            NodeAction dumpStyles = new NodeAction() {

                @Override
				protected void apply(Node node, Object arg, int depth) {
                    String indent = new String();
                    for (int i = 0; i < depth; i++) {
                        indent += "  ";
                    }
                    if (node.getNodeType() == Node.TEXT_NODE) {
                        mLog.info(indent + node.getNodeName());
                        mLog.info(": " + node.getNodeValue() + "\n");
                    }
                    if (node instanceof OdfStylableElement) {
                        try {
                            //mLog.info(indent + "-style info...");
                            OdfStylableElement se = (OdfStylableElement) node;
                            OdfStyleBase as = se.getAutomaticStyle();
                            OdfStyle ds = se.getDocumentStyle();
                            if (as != null) {
                                mLog.info(indent + "-AutomaticStyle: " + as);
                            }
                            if (ds != null) {
                                mLog.info(indent + "-OdfDocumentStyle: " + ds);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            };
            dumpStyles.performAction(e, null);
        }
    }

    @Test
    public void testDefaultStyles() {
        try {
            OdfDocument doc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE));

            doc.getDocumentStyles();
            OdfDefaultStyle oDSG = doc.getDocumentStyles().getDefaultStyle(OdfStyleFamily.Graphic);
            Assert.assertEquals(oDSG.getFamilyName(), OdfStyleFamily.Graphic.getName());
            String prop1 = oDSG.getProperty(StyleGraphicPropertiesElement.ShadowOffsetX);
            Assert.assertEquals(prop1, "0.1181in");

            OdfDefaultStyle oDSP = doc.getDocumentStyles().getDefaultStyle(OdfStyleFamily.Paragraph);
            Assert.assertEquals(oDSP.getFamilyName(), OdfStyleFamily.Paragraph.getName());
            String prop2 = oDSP.getProperty(StyleTextPropertiesElement.FontName);
            Assert.assertEquals(prop2, "Thorndale");
            String prop3 = oDSP.getProperty(StyleTextPropertiesElement.LetterKerning);
            Assert.assertEquals(prop3, "true");

            OdfDefaultStyle oDST = doc.getDocumentStyles().getDefaultStyle(OdfStyleFamily.Table);
            Assert.assertEquals(oDST.getFamilyName(), OdfStyleFamily.Table.getName());
            String prop4 = oDST.getProperty(StyleTablePropertiesElement.BorderModel);
            Assert.assertEquals(prop4, "collapsing");


            OdfDefaultStyle oDSTR = doc.getDocumentStyles().getDefaultStyle(OdfStyleFamily.TableRow);
            Assert.assertEquals(oDSTR.getFamilyName(), OdfStyleFamily.TableRow.getName());
            String prop5 = oDSTR.getProperty(StyleTableRowPropertiesElement.KeepTogether);
            Assert.assertEquals(prop5, "auto");

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
