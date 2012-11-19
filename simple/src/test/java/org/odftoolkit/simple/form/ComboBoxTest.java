package org.odftoolkit.simple.form;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.draw.ControlStyleHandler;
import org.odftoolkit.simple.draw.FrameRectangle;
import org.odftoolkit.simple.form.FormTypeDefinition.FormCommandType;
import org.odftoolkit.simple.form.FormTypeDefinition.FormListSourceType;
import org.odftoolkit.simple.style.GraphicProperties;
import org.odftoolkit.simple.style.StyleTypeDefinitions.AnchorType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FrameVerticalPosition;
import org.odftoolkit.simple.style.StyleTypeDefinitions.SupportedLinearMeasure;
import org.odftoolkit.simple.style.StyleTypeDefinitions.VerticalRelative;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class ComboBoxTest {
	private static final FrameRectangle comboRtg = new FrameRectangle(0.7972,
			1.2862, 2.4441, 0.2669, SupportedLinearMeasure.IN);

	@BeforeClass
	public static void createForm() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Form form = doc.createForm("Test Form");

			FormControl comboBox = form.createComboBox(doc, new FrameRectangle(
					0.7972, 1.2862, 2.4441, 0.2669, SupportedLinearMeasure.IN),
					"combo1", "dd", true);
			String[] items = { "aa", "bb", "cc", "dd", "ee", "ff", "gg", "hh",
					"ii", "jj" };
			((ComboBox) comboBox).addItems(items);

			Paragraph para = doc.addParagraph("Insert a combo box here.");
			comboBox = form.createComboBox(para, comboRtg, "combo2",
					"default text", true);
			form.setDataSource("Bibliography");
			form.setCommandType(FormCommandType.TABLE);
			form.setCommand("biblio");
			((ComboBox) comboBox).setListSourceType(FormListSourceType.SQL);
			((ComboBox) comboBox)
					.setListSource("SELECT \"Publisher\", \"Identifier\" FROM \"biblio\"");
			((ComboBox) comboBox).setDataField("Author");
			comboBox.setAnchorType(AnchorType.AS_CHARACTER);

			Table table = Table.newTable(doc, 2, 2);
			table.setTableName("Table");
			Cell cell = table.getCellByPosition("B1");
			para = cell.addParagraph("Insert a combo box here.");
			form.createComboBox(para, comboRtg, "combo3", "default text", true);

			doc.save(ResourceUtilities
					.newTestOutputFile("TestCreateComboBox.odt"));

		} catch (Exception e) {
			Logger.getLogger(ComboBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

	@Test
	public void testCreateComboBox() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateComboBox.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = ComboBox.getSimpleIterator(form);
			int count = 0;
			
			// combo1
			ComboBox comboBox = (ComboBox) iterator.next();
			Assert.assertNotNull(comboBox);
			Assert.assertEquals("combo" + (++count), comboBox.getName());
			Assert.assertEquals("dd", comboBox.getValue());
			ArrayList<String> entries = comboBox.getEntries();
			Assert.assertEquals("aa", entries.get(0));
			Assert.assertEquals("jj", entries.get(entries.size() - 1));
			
			// combo2
			comboBox = (ComboBox) iterator.next();
			Assert.assertNotNull(comboBox);
			Assert.assertEquals("combo" + (++count), comboBox.getName());
			Assert.assertEquals(
					"SELECT \"Publisher\", \"Identifier\" FROM \"biblio\"",
					comboBox.getListSource());
			Assert.assertEquals(FormListSourceType.SQL, comboBox
					.getListSourceType());
			Assert.assertEquals("Author", comboBox.getDataField());
		
			// combo3
			comboBox = (ComboBox) iterator.next();
			Assert.assertNotNull(comboBox);
			Assert.assertEquals("combo" + (++count), comboBox.getName());
			Assert.assertEquals("default text", comboBox.getValue());
			Assert.assertEquals(textDoc.getTableByName("Table")
					.getCellByPosition("B1").getParagraphByIndex(0, true)
					.getOdfElement(), comboBox.getDrawControl()
					.getContainerElement());

		} catch (Exception e) {
			Logger.getLogger(ComboBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
		}

	}

	@Test
	public void testRemoveComboBox() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateComboBox.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = ComboBox.getSimpleIterator(form);
			while (iterator.hasNext()) {
				ComboBox combo = (ComboBox) iterator.next();
				if (combo.getName().equals("ComboBox2")) {
					iterator.remove();
					break;
				}
			}
			ComboBox find = null;
			while (iterator.hasNext()) {
				ComboBox btn = (ComboBox) iterator.next();
				if (btn.getName().equals("ComboBox2")) {
					find = btn;
					break;
				}
			}
			Assert.assertNull(find);
			textDoc.save(ResourceUtilities
					.newTestOutputFile("TestRemoveComboBox.odt"));
		} catch (Exception e) {
			Logger.getLogger(ComboBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

	@Test
	public void testSetComboBoxRectangle() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateComboBox.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = ComboBox.getSimpleIterator(form);
			ComboBox find = null;
			while (iterator.hasNext()) {
				ComboBox btn = (ComboBox) iterator.next();
				if (btn.getName().equals("combo2")) {
					find = btn;
					break;
				}
			}
			Assert.assertNotNull(find);
			// change the bounding box
			find.setRectangle(new FrameRectangle(2.25455, 5, 3, 0.5,
					SupportedLinearMeasure.IN));
			Assert.assertEquals(3.0, find.getRectangle().getWidth());
			Assert.assertEquals(0.5, find.getRectangle().getHeight());
			Assert.assertEquals(5.0, find.getRectangle().getY());
			Assert.assertEquals(2.2546, find.getRectangle().getX());
			textDoc.save(ResourceUtilities
					.newTestOutputFile("TestSetComboBoxRectangle.odt"));
		} catch (Exception e) {
			Logger.getLogger(ComboBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

	@Test
	public void testSetAnchorType() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateComboBox.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = ComboBox.getSimpleIterator(form);
			ComboBox find = null;
			while (iterator.hasNext()) {
				ComboBox btn = (ComboBox) iterator.next();
				if (btn.getName().equals("combo3")) {
					find = btn;
					break;
				}
			}
			Assert.assertNotNull(find);
			// change the bounding box
			find.setAnchorType(AnchorType.AS_CHARACTER);
			// validate
			ControlStyleHandler frameStyleHandler = find.getDrawControl()
					.getStyleHandler();
			GraphicProperties graphicPropertiesForWrite = frameStyleHandler
					.getGraphicPropertiesForWrite();
			Assert.assertEquals(VerticalRelative.BASELINE,
					graphicPropertiesForWrite.getVerticalRelative());
			Assert.assertEquals(FrameVerticalPosition.TOP,
					graphicPropertiesForWrite.getVerticalPosition());

			textDoc.save(ResourceUtilities
					.newTestOutputFile("TestSetComboBoxAnchorType.odt"));
		} catch (Exception e) {
			Logger.getLogger(ComboBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

	@Test
	public void testSetProperties() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateComboBox.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = ComboBox.getSimpleIterator(form);
			ComboBox find = null;
			while (iterator.hasNext()) {
				ComboBox btn = (ComboBox) iterator.next();
				if (btn.getName().equals("combo3")) {
					find = btn;
					break;
				}
			}
			Assert.assertNotNull(find);
			// set new name
			String newName = "combo4";
			find.setName(newName);
			Assert.assertEquals(newName, find.getName());
			// set new text value
			find.setCurrentValue(newName);
			Assert.assertEquals(newName, find.getCurrentValue());
			// set drop-down visibility
			find.setFormDropdown(false);
			Assert.assertEquals(newName, find.getCurrentValue());
			// set list source
			String sql = "SELECT DISTINCT \"ISBN\" FROM \"biblio\" ";
			find.setListSource(sql);
			Assert.assertEquals(sql, find.getListSource());

			textDoc.save(ResourceUtilities
					.newTestOutputFile("TestSetComboBoxProperties.odt"));
		} catch (Exception e) {
			Logger.getLogger(ComboBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

}
