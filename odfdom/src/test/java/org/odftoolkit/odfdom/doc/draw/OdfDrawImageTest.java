/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.odftoolkit.odfdom.doc.draw;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.net.URI;
import java.util.logging.Logger;

import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawFrame;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

/**
 *
 * @author hs234750
 */
public class OdfDrawImageTest {

	private static final Logger LOG = Logger.getLogger(OdfDrawImageTest.class.getName());

	/**
	 * Test of newImage method, of class OdfDrawImage.
	 */
	@Test
	public void testInsertImage_URI() throws Exception {
		LOG.info("insertImage from URI");
		OdfTextDocument odt = OdfTextDocument.newTextDocument();
		OdfTextParagraph para = (OdfTextParagraph) odt.getContentRoot().newTextPElement();
		OdfDrawFrame frame = (OdfDrawFrame) para.newDrawFrameElement();
		OdfDrawImage image = (OdfDrawImage) frame.newDrawImageElement();
		String packagePath = image.newImage(new URI("file:" + ResourceUtilities.getAbsolutePath("testA.jpg")));
		assertEquals(image.getXlinkTypeAttribute(), "simple");
		LOG.info(frame.getSvgWidthAttribute());
		LOG.info(frame.getSvgHeightAttribute());
		assert (frame.getSvgWidthAttribute().startsWith("19.") && frame.getSvgWidthAttribute().endsWith("cm"));
		assert (frame.getSvgHeightAttribute().startsWith("6.") && frame.getSvgHeightAttribute().endsWith("cm"));
		assertEquals(odt.getPackage().getFileEntry(packagePath).getMediaTypeString(), "image/jpeg");
	}

	/**
	 * Test of newImage method, of class OdfDrawImage.
	 */
	@Test
	public void testInsertImage_InputStream() throws Exception {
		LOG.info("insertImage from InputStream");
		OdfTextDocument odt = OdfTextDocument.newTextDocument();
		OdfTextParagraph para = (OdfTextParagraph) odt.getContentRoot().newTextPElement();
		OdfDrawFrame frame = (OdfDrawFrame) para.newDrawFrameElement();
		OdfDrawImage image = (OdfDrawImage) frame.newDrawImageElement();
		String packagePath = "Pictures/myChosenImageName.jpg";
		String mediaType = "image/jpeg";
		image.newImage(new FileInputStream(ResourceUtilities.getAbsolutePath("testA.jpg")), packagePath, mediaType);
		assertEquals(image.getXlinkTypeAttribute(), "simple");
		assert (frame.getSvgWidthAttribute().startsWith("19.") && frame.getSvgWidthAttribute().endsWith("cm"));
		assert (frame.getSvgHeightAttribute().startsWith("6.") && frame.getSvgHeightAttribute().endsWith("cm"));
	}
}
