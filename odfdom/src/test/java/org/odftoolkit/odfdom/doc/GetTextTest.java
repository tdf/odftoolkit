package org.odftoolkit.odfdom.doc;

import java.util.logging.Logger;
import org.junit.Test;
import org.odftoolkit.odfdom.incubator.doc.text.OdfEditableTextExtractor;
import org.junit.Assert;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

public class GetTextTest {

	public static final Logger LOG = Logger.getLogger(GetTextTest.class.getName());

	/**
	 * This method will invoke OdfEditableTextExtractor to test text extraction function.
	 */
	@Test
	public void testToString() {

		try {
			OdfDocument doc = OdfDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("text-extract.odt"));
			OdfEditableTextExtractor extractor = OdfEditableTextExtractor.newOdfEditableTextExtractor(doc);
			String output = extractor.getText();
			LOG.info(output);
			int count = 0;
			int index = output.indexOf("ODFDOM");
			while (index != -1) {
				count++;
				index = output.indexOf("ODFDOM", index + 1);
			}
			if (count != 30) {
				// there are
				// 23 ODFDOM in the /content.xml
				// 2  ODFDOM in the /styles.xml
				// 5 ODFDOM in the /Object 1/content.xml
				throw new RuntimeException("Something wrong! count=" + count);
			}
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}
