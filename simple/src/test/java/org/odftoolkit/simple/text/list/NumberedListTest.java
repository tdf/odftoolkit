package org.odftoolkit.simple.text.list;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextListStyle;
import org.odftoolkit.simple.TextDocument;
import org.w3c.dom.Element;
import junit.framework.Assert;

public class NumberedListTest{

	private static final char lowerGreekAlfa = '\u03B1';

	@Test
	public void testSetExtendedNumberedGreekListDecorator() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			List greekList = doc.addList(new NumberedGreekLowerDecorator(doc));
			NumberedGreekLowerDecorator greekListdecorator = (NumberedGreekLowerDecorator)greekList.decorator;
			OdfTextListStyle listStyle = greekListdecorator.getListStyle();
			Element odfTextListLevelStyleNumberEle = listStyle.getFirstElementChild();
			String styleNumFormat = odfTextListLevelStyleNumberEle.getAttribute("style:num-format");
			// Test if the number style starts with lower greek alfa:
			Assert.assertTrue(styleNumFormat.indexOf(lowerGreekAlfa) == 0);
		} catch (Exception e) {
			Logger.getLogger(NumberedListTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

}
