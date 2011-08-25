package org.odftoolkit.simple.draw;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.simple.PresentationDocument;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.presentation.Slide;
import org.odftoolkit.simple.presentation.Slide.SlideLayout;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FrameHorizontalPosition;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class ImageTest {

	@Test
	public void testNewImage() {
		try {
			// new image in text document
			TextDocument doc = TextDocument.newTextDocument();
			Paragraph para = doc.addParagraph("");
			Image image = Image.newImage(para, ResourceUtilities.getURI("image_list_item.png"));
			image.setName("this image");
			doc.save(ResourceUtilities.newTestOutputFile("imagetest.odt"));
			Iterator<Image> iter = Image.imageIterator(para);
			if (iter.hasNext()) {
				Image aImage = iter.next();
				Assert.assertEquals(image, aImage);
			}

			// new image in presentation
			PresentationDocument pDoc = PresentationDocument.newPresentationDocument();
			Slide slide = pDoc.newSlide(0, "test", SlideLayout.TITLE_OUTLINE);
			Textbox box = slide.getTextboxByUsage(PresentationDocument.PresentationClass.TITLE).get(0);
			box.setImage(ResourceUtilities.getURI("image_list_item.png"));
			pDoc.save(ResourceUtilities.newTestOutputFile("imagep.odp"));

			// new image in a table
			TextDocument sDoc = TextDocument.newTextDocument();
			Table table1 = sDoc.addTable(2, 2);
			Cell cell1 = table1.getCellByPosition(0, 0);
			Image image3 = cell1.setImage(ResourceUtilities.getURI("image_list_item.png"));
			image3.setHorizontalPosition(FrameHorizontalPosition.LEFT);
			sDoc.save(ResourceUtilities.newTestOutputFile("imges.odt"));

			SpreadsheetDocument sheet = SpreadsheetDocument.newSpreadsheetDocument();
			Table table2 = sheet.getTableList().get(0);
			Cell cell2 = table2.getCellByPosition(1, 1);
			Image image4 = cell2.setImage(ResourceUtilities.getURI("image_list_item.png"));
			sheet.save(ResourceUtilities.newTestOutputFile("imgesheet.ods"));
			Image aImage4 = cell2.getImage();
			Assert.assertEquals(image4, aImage4);

		} catch (Exception e) {
			Logger.getLogger(ImageTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}

	}

}
