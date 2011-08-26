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
package org.odftoolkit.odfdom.dom.example;

import static java.util.logging.Level.INFO;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.style.StyleGraphicPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTablePropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTableRowPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfStyleProperty;
import org.odftoolkit.odfdom.incubator.doc.style.OdfDefaultStyle;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.utils.NodeAction;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.w3c.dom.Node;

/**
 *
 * @author j
 */
public class StyleExamplesTest {

	private static String TEST_FILE = "test2.odt";
	private static final Logger LOG = Logger.getLogger(StyleExamplesTest.class.getName());

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

			LOG.finest(textNode.getParentNode().toString());

			String teksto = textNode.getTextContent().trim();
			if (teksto.length() == 0) {
				return;
			}

			String font = StyleUtils.findActualStylePropertyValueForNode(textNode, desiredProperty);

			LOG.log(Level.FINEST, "{0}: {1}", new Object[]{font, teksto});
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
		if (LOG.isLoggable(INFO)) {
			OdfDocument odfdoc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE));
			LOG.info("Parsed document.");

			OdfElement e = (OdfElement) odfdoc.getContentDom().getDocumentElement();
			NodeAction dumpStyles = new NodeAction() {

				@Override
				protected void apply(Node node, Object arg, int depth) {
					String indent = new String();
					for (int i = 0; i < depth; i++) {
						indent += "  ";
					}
					if (node.getNodeType() == Node.TEXT_NODE) {
						LOG.log(INFO, "{0}{1}", new Object[]{indent, node.getNodeName()});
						LOG.log(INFO, ": {0}\n", node.getNodeValue());
					}
					if (node instanceof OdfStylableElement) {
						try {
							//LOG.info(indent + "-style info...");
							OdfStylableElement se = (OdfStylableElement) node;
							OdfStyleBase as = se.getAutomaticStyle();
							OdfStyle ds = se.getDocumentStyle();
							if (as != null) {
								LOG.log(INFO, "{0}-AutomaticStyle: {1}", new Object[]{indent, as});
							}
							if (ds != null) {
								LOG.log(INFO, "{0}-OdfDocumentStyle: {1}", new Object[]{indent, ds});
							}
						} catch (Exception ex) {
							LOG.log(Level.SEVERE, ex.getMessage(), ex);
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
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

    @Test
	@Ignore
	/** Should there be a validation in the future? */
    public void testSetValue() throws Exception {
        OdfTextDocument odt = OdfTextDocument.newTextDocument();
        OdfContentDom dom = odt.getContentDom();
        OdfStyle style1 = new OdfStyle(dom);

        // No exception should be thrown here
        style1.setStyleFamilyAttribute(OdfStyleFamily.Paragraph.toString());
        assertEquals(style1.getStyleFamilyAttribute(), OdfStyleFamily.Paragraph.toString());

        // Catch only IllegalArgumentException
        try {
            style1.setStyleFamilyAttribute("ImSoInvalid");
        } catch (IllegalArgumentException e) {
            return;   // test passed
        }
        // We need an exception from the setValue method! Otherwise we don't know that an empty attribute node has to be removed
        fail("An IllegalArgumentException has to be thrown for invalid attributes so the attribute node can be removed afterwards.");
    }
}
