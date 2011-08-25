package org.odftoolkit.simple.common;

import java.util.logging.Logger;
import org.junit.Test;
import org.junit.Assert;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.common.EditableTextExtractor;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class GetTextTest {

	public static final Logger LOG = Logger.getLogger(GetTextTest.class.getName());

	/**
	 * This method will invoke EditableTextExtractor to test text extraction function.
	 */
	@Test
	public void testToString() {

		try {
			Document doc = Document.loadDocument(ResourceUtilities.getTestResourceAsStream("text-extract.odt"));
			EditableTextExtractor extractor = EditableTextExtractor.newOdfEditableTextExtractor(doc);
			String output = extractor.getText();
			LOG.info(output);
			int count = 0;
			int index = output.indexOf("SIMPLE");
			while (index != -1) {
				count++;
				index = output.indexOf("SIMPLE", index + 1);
			}
			if (count != 30) {
				// there are
				// 23 SIMPLE in the /content.xml
				// 2  SIMPLE in the /styles.xml
				// 5 SIMPLE in the /Object 1/content.xml
				throw new RuntimeException("Something wrong! count=" + count);
			}
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}
