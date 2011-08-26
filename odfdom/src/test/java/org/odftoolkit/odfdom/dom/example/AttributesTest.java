/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.odftoolkit.odfdom.dom.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Ignore;

import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.pkg.OdfFileDom;

/** The test was deactivated. As an invalid value will no longer throw an exception and cause the removal of the attribute. */
public class AttributesTest {

    @Test
	@Ignore
    public void testSetValue() throws Exception {
        OdfTextDocument odt = OdfTextDocument.newTextDocument();
        OdfFileDom dom = odt.getContentDom();
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
