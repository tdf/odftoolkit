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
package org.odftoolkit.odfdom.dom.test;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfFileDom;
import org.odftoolkit.odfdom.doc.element.office.OdfAutomaticStyles;
import org.odftoolkit.odfdom.doc.element.office.OdfStyles;
import org.odftoolkit.odfdom.doc.element.style.OdfPageLayout;
import org.odftoolkit.odfdom.doc.element.style.OdfPageLayoutProperties;
import org.odftoolkit.odfdom.doc.element.style.OdfStyle;
import org.odftoolkit.odfdom.doc.element.style.OdfTextProperties;
import org.odftoolkit.odfdom.doc.element.text.OdfListStyle;
import org.odftoolkit.odfdom.dom.OdfNamespace;
import org.odftoolkit.odfdom.dom.element.OdfElement;
import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.util.NodeAction;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DocumentTest {

    private static final String TEST_FILE = "test/resources/test2.odt";

    public DocumentTest() {
    }

    @Test
    public void testParser() {
        try {
            OdfDocument odfdoc = OdfDocument.loadDocument(TEST_FILE);
            OdfElement e = (OdfElement) odfdoc.getContentDom().getDocumentElement();

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDumpDom() {
        try {
            OdfDocument odfdoc = OdfDocument.loadDocument(TEST_FILE);

            Transformer trans = TransformerFactory.newInstance().newTransformer();
            trans.setOutputProperty("indent", "yes");
            trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            System.out.println("content.xml -------------------");
            trans.transform(new DOMSource(odfdoc.getContentDom()), new StreamResult(System.out));
            System.out.println("-------------------------------");
            System.out.println("styles.xml --------------------");
            trans.transform(new DOMSource(odfdoc.getStylesDom()), new StreamResult(System.out));
            System.out.println("-------------------------------");


        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testStylesDom() {
        try {
            OdfDocument odfdoc = OdfDocument.loadDocument(TEST_FILE);

            OdfFileDom stylesDom = odfdoc.getStylesDom();
            Assert.assertNotNull(stylesDom);
            
            // test styles.xml:styles
            OdfStyles styles = odfdoc.getDocumentStyles();
            Assert.assertNotNull(styles);
            
            Assert.assertNotNull(styles.getDefaultStyle(OdfStyleFamily.Graphic));
            Assert.assertNotNull(styles.getDefaultStyle(OdfStyleFamily.Paragraph));
            Assert.assertNotNull(styles.getDefaultStyle(OdfStyleFamily.Table));
            Assert.assertNotNull(styles.getDefaultStyle(OdfStyleFamily.TableRow));
            
            OdfStyle style = styles.getStyle( "Standard", OdfStyleFamily.Paragraph );
            Assert.assertNotNull( style );
            Assert.assertEquals( style.getStyleClass(), "text");
            
            style = styles.getStyle("List", OdfStyleFamily.Paragraph);
            Assert.assertNotNull(style);
            Assert.assertEquals( style.getProperty(OdfTextProperties.FontNameComplex), "Tahoma1" );
            Assert.assertTrue( style.hasProperty(OdfTextProperties.FontNameComplex));
            Assert.assertFalse( style.hasProperty(OdfTextProperties.FontNameAsian));            

            Assert.assertNull( styles.getStyle("foobar", OdfStyleFamily.Chart));
            
            // test styles.xml:automatic-styles
            OdfAutomaticStyles autostyles = stylesDom.getAutomaticStyles();
            Assert.assertNotNull(autostyles);
            
            OdfPageLayout pageLayout = autostyles.getPageLayout("pm1");
            Assert.assertNotNull(pageLayout);
            Assert.assertEquals(pageLayout.getProperty(OdfPageLayoutProperties.PageWidth), "8.5in");
            Assert.assertEquals(pageLayout.getProperty(OdfPageLayoutProperties.PageHeight), "11in");

            Assert.assertNull( autostyles.getStyle("foobar", OdfStyleFamily.Chart));
            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testContentNode()
    {
        try {
            OdfDocument odfdoc = OdfDocument.loadDocument(TEST_FILE);
            
            OdfFileDom contentDom = odfdoc.getContentDom();
            
            // test content.xml:automatic-styles
            OdfAutomaticStyles autoStyles = contentDom.getAutomaticStyles();
            Assert.assertNotNull( autoStyles );
            
            OdfStyle style = autoStyles.getStyle( "P1", OdfStyleFamily.Paragraph);
            Assert.assertNotNull( style );
            Assert.assertEquals( style.getName(), "P1" );
            Assert.assertEquals( style.getParentStyleName(), "Text_20_body" );
            Assert.assertEquals( style.getListStyleName(), "L1" );
            
            style = autoStyles.getStyle( "T1", OdfStyleFamily.Text );
            Assert.assertNotNull( style );
            Assert.assertEquals( style.getName(), "T1" );

            for( OdfStyle testStyle : autoStyles.getStylesForFamily( OdfStyleFamily.Paragraph ) )
                testStyle( testStyle );

            for( OdfStyle testStyle : autoStyles.getStylesForFamily( OdfStyleFamily.Text ) )
                testStyle( testStyle );
            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSaveDocument() {
        try {
            OdfDocument odfdoc = OdfDocument.loadDocument(TEST_FILE);
            OdfElement e = (OdfElement) odfdoc.getContentDom().getDocumentElement();
            NodeAction<String> replaceText = new NodeAction<String>() {

                protected void apply(Node cur, String replace, int depth) {
                    if (cur.getNodeType() == Node.TEXT_NODE) {
                        cur.setNodeValue(cur.getNodeValue().replaceAll("\\w", replace));
                    }
                }
            };
//            replaceText.performAction(e, "X");            
            odfdoc.save("build/test/list-out.odt");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    private void testStyle(OdfStyle testStyle) throws Exception
    {
        OdfFileDom dom = (OdfFileDom)testStyle.getOwnerDocument();
        if( testStyle.getParentStyleName().length() != 0 )
        {
            OdfStyle parentStyle = dom.getAutomaticStyles().getStyle( testStyle.getParentStyleName(), testStyle.getFamily());
            if( parentStyle == null )
                parentStyle = dom.getOdfDocument().getDocumentStyles().getStyle( testStyle.getParentStyleName(), testStyle.getFamily());

            Assert.assertNotNull(parentStyle);
        }
        
        if( testStyle.getListStyleName().length() != 0 )
        {
            OdfListStyle listStyle = dom.getAutomaticStyles().getListStyle( testStyle.getListStyleName());
            if( listStyle == null )
                listStyle = dom.getOdfDocument().getDocumentStyles().getListStyle( testStyle.getListStyleName());

            Assert.assertNotNull(listStyle);
        }
    }
}
