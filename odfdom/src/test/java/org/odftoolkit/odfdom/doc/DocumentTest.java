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

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.OdfFileDom;
import org.odftoolkit.odfdom.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.doc.style.OdfStylePageLayout;
import org.odftoolkit.odfdom.doc.style.OdfStyle;
import org.odftoolkit.odfdom.doc.text.OdfTextListStyle;
import org.odftoolkit.odfdom.OdfName;
import org.odftoolkit.odfdom.OdfNamespace;
import org.odftoolkit.odfdom.doc.office.OdfOfficeText;
import org.odftoolkit.odfdom.dom.OdfNamespaceNames;
import org.odftoolkit.odfdom.dom.element.style.StylePageLayoutPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.utils.NodeAction;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.w3c.dom.Node;

public class DocumentTest {

    private static final String TEST_FILE = "test2.odt";
    private static final String TEST_FILE_WITHOUT_OPT = "no_size_opt.odt";

    public DocumentTest() {
    }

    @Test
    public void testParser() {
        try {
            OdfDocument.loadDocument(ResourceUtilities.getTestResource(TEST_FILE));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetContentRoot() {
        try {
            OdfTextDocument odt = (OdfTextDocument) OdfDocument.loadDocument(ResourceUtilities.getTestResource(TEST_FILE_WITHOUT_OPT));
            Assert.assertNotNull(odt.getContentRoot());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDumpDom() {
        try {
            OdfDocument odfdoc = OdfDocument.loadDocument(ResourceUtilities.getTestResource(TEST_FILE));

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
            OdfDocument odfdoc = OdfDocument.loadDocument(ResourceUtilities.getTestResource(TEST_FILE));

            OdfFileDom stylesDom = odfdoc.getStylesDom();
            Assert.assertNotNull(stylesDom);
            
            // test styles.xml:styles
            OdfOfficeStyles styles = odfdoc.getDocumentStyles();
            Assert.assertNotNull(styles);
            
            Assert.assertNotNull(styles.getDefaultStyle(OdfStyleFamily.Graphic));
            Assert.assertNotNull(styles.getDefaultStyle(OdfStyleFamily.Paragraph));
            Assert.assertNotNull(styles.getDefaultStyle(OdfStyleFamily.Table));
            Assert.assertNotNull(styles.getDefaultStyle(OdfStyleFamily.TableRow));
            
            OdfStyle style = styles.getStyle( "Standard", OdfStyleFamily.Paragraph );
            Assert.assertNotNull( style );
            Assert.assertEquals( style.getStyleClassAttribute(), "text");
            
            style = styles.getStyle("List", OdfStyleFamily.Paragraph);
            Assert.assertNotNull(style);
            Assert.assertEquals( style.getProperty(StyleTextPropertiesElement.FontNameComplex), "Tahoma1" );
            Assert.assertTrue( style.hasProperty(StyleTextPropertiesElement.FontNameComplex));
            Assert.assertFalse( style.hasProperty(StyleTextPropertiesElement.FontNameAsian));            

            Assert.assertNull( styles.getStyle("foobar", OdfStyleFamily.Chart));
            
            // test styles.xml:automatic-styles
            OdfOfficeAutomaticStyles autostyles = stylesDom.getAutomaticStyles();
            Assert.assertNotNull(autostyles);
            
            OdfStylePageLayout pageLayout = autostyles.getPageLayout("pm1");
            Assert.assertNotNull(pageLayout);
            Assert.assertEquals(pageLayout.getProperty(StylePageLayoutPropertiesElement.PageWidth), "8.5in");
            Assert.assertEquals(pageLayout.getProperty(StylePageLayoutPropertiesElement.PageHeight), "11in");

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
            OdfDocument odfdoc = OdfDocument.loadDocument(ResourceUtilities.getTestResource(TEST_FILE));
            
            OdfFileDom contentDom = odfdoc.getContentDom();
            
            // test content.xml:automatic-styles
            OdfOfficeAutomaticStyles autoStyles = contentDom.getAutomaticStyles();
            Assert.assertNotNull( autoStyles );
            
            OdfStyle style = autoStyles.getStyle( "P1", OdfStyleFamily.Paragraph);
            Assert.assertNotNull( style );
            Assert.assertEquals( style.getStyleNameAttribute(), "P1" );
            Assert.assertEquals( style.getStyleParentStyleNameAttribute(), "Text_20_body" );
            Assert.assertEquals( style.getStyleListStyleNameAttribute(), "L1" );
            
            style = autoStyles.getStyle( "T1", OdfStyleFamily.Text );
            Assert.assertNotNull( style );
            Assert.assertEquals( style.getStyleNameAttribute(), "T1" );

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
            OdfDocument odfdoc = OdfDocument.loadDocument(ResourceUtilities.getTestResource(TEST_FILE));
            new NodeAction<String>() {

                @Override
				protected void apply(Node cur, String replace, int depth) {
                    if (cur.getNodeType() == Node.TEXT_NODE) {
                        cur.setNodeValue(cur.getNodeValue().replaceAll("\\w", replace));
                    }
                }
            };
//            replaceText.performAction(e, "X");            
            odfdoc.save(ResourceUtilities.createTestResource("list-out.odt"));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    private void testStyle(OdfStyle testStyle) throws Exception
    {
        OdfFileDom dom = (OdfFileDom)testStyle.getOwnerDocument();
        if( testStyle.getStyleParentStyleNameAttribute()!=null )
        {
            OdfStyle parentStyle = dom.getAutomaticStyles().getStyle( testStyle.getStyleParentStyleNameAttribute(), testStyle.getFamily());
            if( parentStyle == null )
                parentStyle = dom.getOdfDocument().getDocumentStyles().getStyle( testStyle.getStyleParentStyleNameAttribute(), testStyle.getFamily());

            Assert.assertNotNull(parentStyle);
        }
        if(testStyle.hasOdfAttribute(OdfName.get( OdfNamespace.get(OdfNamespaceNames.STYLE), "list-style-name" )))
        {
	        if( testStyle.getStyleListStyleNameAttribute()!=null )
	        {
	            OdfTextListStyle listStyle = dom.getAutomaticStyles().getListStyle( testStyle.getStyleListStyleNameAttribute());
	            if( listStyle == null )
	                listStyle = dom.getOdfDocument().getDocumentStyles().getListStyle( testStyle.getStyleListStyleNameAttribute());
	
	            Assert.assertNotNull(listStyle);
	        }
        }
    }
}
