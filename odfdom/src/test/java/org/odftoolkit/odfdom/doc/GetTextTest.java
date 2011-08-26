package org.odftoolkit.odfdom.doc;

import org.odftoolkit.odfdom.incubator.doc.text.OdfEditableTextExtractor;
import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextExtractor;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

public class GetTextTest {

	/**
	 * This method will invoke OdfEditableTextExtractor to test text extraction function.
	 */
	@Test
	public void testToString() {

		try {
			OdfDocument doc = OdfDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("text-extract.odt"));
			OdfEditableTextExtractor extractor = OdfEditableTextExtractor.newOdfEditableTextExtractor(doc);
			String output = extractor.getText();
			System.out.println(output);
			
			int count = 0;
			int index = output.indexOf("ODFDOM");
			while (index!=-1)
			{
				count++;
				index = output.indexOf("ODFDOM", index+1);
			}
			if (count!=30)
				throw new RuntimeException("Something wrong! count="+count);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

	}
	
}
