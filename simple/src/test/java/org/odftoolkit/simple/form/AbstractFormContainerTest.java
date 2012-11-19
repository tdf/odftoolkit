package org.odftoolkit.simple.form;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.draw.FrameRectangle;
import org.odftoolkit.simple.style.StyleTypeDefinitions.SupportedLinearMeasure;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class AbstractFormContainerTest {
	final static FrameRectangle btnRtg = new FrameRectangle(0.5, 2, 2.9433,
			0.5567, SupportedLinearMeasure.IN);

	@BeforeClass
	public static void createForm() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Form form1 = doc.createForm("Form1");
			form1.createButton(doc, btnRtg, "Button", "Push Button");

			Paragraph para = doc.addParagraph("Insert a Label here.");
			Form form2 = doc.createForm("Form2");
			form2.createLabel(para, btnRtg, "Label", "Label");

			Form form3 = doc.createForm("Form3");
			Table table1 = Table.newTable(doc, 2, 2);
			Cell cell = table1.getCellByPosition("A1");
			para = cell.addParagraph("Insert a text box here.");
			form3.createTextBox(para, btnRtg, "Text Box", "TextBox", true);

			Assert.assertNotNull(doc.getFormByName("Form1"));

			doc.save(ResourceUtilities.newTestOutputFile("TestCreateForm.odt"));
		} catch (Exception e) {
			Logger.getLogger(AbstractFormContainerTest.class.getName()).log(
					Level.SEVERE, null, e);
		}
	}

	@Test
	public void testCreateForm() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateForm.odt"));
			Assert.assertNotNull(textDoc.getFormByName("Form1"));
			Assert.assertNotNull(textDoc.getFormByName("Form2"));
			Assert.assertNotNull(textDoc.getFormByName("Form3"));
		} catch (Exception e) {
			Logger.getLogger(AbstractFormContainerTest.class.getName()).log(
					Level.SEVERE, null, e);
		}
	}

	@Test
	public void testRemoveForm() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateForm.odt"));
			Form deleteForm = null;
			Iterator<Form> iterator = textDoc.getFormIterator();
			while (iterator.hasNext()) {
				deleteForm = iterator.next();
				if (deleteForm.getFormName().equals("Form3"))
					break;
			}
			textDoc.removeForm(deleteForm);
			Assert.assertNotNull(textDoc.getFormByName("Form1"));
			Assert.assertNotNull(textDoc.getFormByName("Form2"));
			Assert.assertNull(textDoc.getFormByName("Form3"));
			textDoc.save(ResourceUtilities
					.newTestOutputFile("TestRemoveForm.odt"));

		} catch (Exception e) {
			Logger.getLogger(AbstractFormContainerTest.class.getName()).log(
					Level.SEVERE, null, e);
		}
	}
}
