/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.odftoolkit.odfdom.doc.draw;

import java.io.FileInputStream;
import java.net.URI;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import static org.junit.Assert.*;

/**
 *
 * @author hs234750
 */
public class OdfDrawImageTest {

    /**
     * Test of insertImage method, of class OdfDrawImage.
     */
    @Test
    public void testInsertImage_URI() throws Exception {
        System.out.println("insertImage from URI");
        OdfTextDocument odt = OdfTextDocument.newTextDocument();
        OdfTextParagraph para = (OdfTextParagraph) odt.getContentRoot().newTextPElement();
        OdfDrawFrame frame = (OdfDrawFrame) para.newDrawFrameElement();
        OdfDrawImage image = (OdfDrawImage) frame.newDrawImageElement();
        String packagePath = image.insertImage(new URI("file:" + ResourceUtilities.getTestResource("test.jpg")));
        assertEquals(image.getXlinkTypeAttribute(), "simple");
        System.out.println(frame.getSvgWidthAttribute());
        System.out.println(frame.getSvgHeightAttribute());
        assert(frame.getSvgWidthAttribute().startsWith("19.") && frame.getSvgWidthAttribute().endsWith("cm"));
        assert(frame.getSvgHeightAttribute().startsWith("6.") && frame.getSvgHeightAttribute().endsWith("cm"));
        assertEquals(odt.getPackage().getFileEntry(packagePath).getMediaType(), "image/jpeg");
    }

    /**
     * Test of insertImage method, of class OdfDrawImage.
     */
    @Test
    public void testInsertImage_InputStream() throws Exception {
        System.out.println("insertImage from InputStream");
        OdfTextDocument odt = OdfTextDocument.newTextDocument();
        OdfTextParagraph para = (OdfTextParagraph) odt.getContentRoot().newTextPElement();
        OdfDrawFrame frame = (OdfDrawFrame) para.newDrawFrameElement();
        OdfDrawImage image = (OdfDrawImage) frame.newDrawImageElement();
        String packagePath = "Pictures/myChosenImageName.jpg";
        String mediaType = "image/jpeg";
        image.insertImage(new FileInputStream(ResourceUtilities.getTestResource("test.jpg")), packagePath, mediaType);
        assertEquals(image.getXlinkTypeAttribute(), "simple");
        assert(frame.getSvgWidthAttribute().startsWith("19.") && frame.getSvgWidthAttribute().endsWith("cm"));
        assert(frame.getSvgHeightAttribute().startsWith("6.") && frame.getSvgHeightAttribute().endsWith("cm"));
    }

}