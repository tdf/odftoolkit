package org.odftoolkit.simple.draw;

import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.PresentationDocument;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.PresentationDocument.PresentationClass;
import org.odftoolkit.simple.presentation.Slide;
import org.odftoolkit.simple.presentation.Slide.SlideLayout;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class AbstractTextboxContainerTest {

	@Test
	public void testRemoveTextbox() {
		try {
			// new image in presentation
			PresentationDocument pDoc = PresentationDocument.newPresentationDocument();
			Slide slide = pDoc.newSlide(0, "slide name ", SlideLayout.TITLE_OUTLINE);
			java.util.List<Textbox> listTbox = slide.getTextboxByUsage(PresentationClass.TITLE);
			Textbox titleBox = listTbox.get(0);
			titleBox.setBackgroundColor(Color.BLUE);
			//titleBox.setImage(ResourceUtilities.getURI("image_list_item.png"));
			titleBox.setName("title");
			titleBox.setTextContent("this is title");
			
			//validate
			Textbox tbox = slide.getTextboxByName("title");
			Assert.assertEquals(titleBox, tbox);
			
			slide.removeTextbox(titleBox);
			
			Textbox tbox1 = slide.getTextboxByName("title");
			Assert.assertNull(tbox1);

			//save
			pDoc.save(ResourceUtilities.newTestOutputFile("imagep.odp"));
		} catch (Exception e) {
			Logger.getLogger(AbstractTextboxContainerTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}

	}
	
	@Test
	public void testAddTextbox() {
		try {
			TextDocument textDoc = TextDocument.newTextDocument();
			Paragraph p = textDoc.addParagraph("abc");
			Textbox box1 = p.addTextbox();
			box1.setTextContent("content XXXX");
			textDoc.save(ResourceUtilities.newTestOutputFile("textsample.odt"));
			
		} catch (Exception e) {
			Logger.getLogger(AbstractTextboxContainerTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}

	}
	
}
