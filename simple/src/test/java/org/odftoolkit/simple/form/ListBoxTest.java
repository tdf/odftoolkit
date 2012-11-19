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

public class ListBoxTest {
	private static final FrameRectangle listBoxRtg = new FrameRectangle(0.5752,
			0.1429, 2.3307, 0.8398, SupportedLinearMeasure.IN);

	@BeforeClass
	public static void createForm() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Form form = doc.createForm("Test Form");

			// list box1
			FormControl listBox = form.createListBox(doc, listBoxRtg, "list1",
					true, false);
			String[] items = { "aa", "bb", "cc", "dd", "ee", "ff", "gg", "hh",
					"ii", "jj" };
			((ListBox) listBox).addItems(items);

			// list box2
			Paragraph para = doc.addParagraph("Insert a list box here.");
			listBox = form
					.createListBox(para, listBoxRtg, "list2", false, true);
			form.setDataSource("Bibliography");
			form.setCommandType(FormCommandType.TABLE);
			form.setCommand("biblio");
			((ListBox) listBox).setListSourceType(FormListSourceType.SQL);
			((ListBox) listBox)
					.setListSource("SELECT \"Publisher\", \"Identifier\" FROM \"biblio\"");
			((ListBox) listBox).setDataField("Author");
			listBox.setAnchorType(AnchorType.AS_CHARACTER);

			// list box3
			Table table = Table.newTable(doc, 2, 2);
			table.setTableName("Table");
			Cell cell = table.getCellByPosition("B1");
			para = cell.addParagraph("Insert a list box here.");
			form.createListBox(para, listBoxRtg, "list3", false, true);

			doc.save(ResourceUtilities
					.newTestOutputFile("TestCreateListBox.odt"));

		} catch (Exception e) {
			Logger.getLogger(ListBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

	@Test
	public void testCreateListBox() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateListBox.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = ListBox.getSimpleIterator(form);
			int count = 0;

			// list1
			ListBox listBox = (ListBox) iterator.next();
			Assert.assertNotNull(listBox);
			Assert.assertEquals("list" + (++count), listBox.getName());
			ArrayList<String> entries = listBox.getEntries();
			Assert.assertEquals("aa", entries.get(0));
			Assert.assertEquals("jj", entries.get(entries.size() - 1));

			// list2
			listBox = (ListBox) iterator.next();
			Assert.assertNotNull(listBox);
			Assert.assertEquals("list" + (++count), listBox.getName());
			Assert.assertEquals(
					"SELECT \"Publisher\", \"Identifier\" FROM \"biblio\"",
					listBox.getListSource());
			Assert.assertEquals(FormListSourceType.SQL, listBox
					.getListSourceType());
			Assert.assertEquals("Author", listBox.getDataField());

			// list3
			listBox = (ListBox) iterator.next();
			Assert.assertNotNull(listBox);
			Assert.assertEquals("list" + (++count), listBox.getName());
			Assert.assertEquals(textDoc.getTableByName("Table")
					.getCellByPosition("B1").getParagraphByIndex(0, true)
					.getOdfElement(), listBox.getDrawControl()
					.getContainerElement());

		} catch (Exception e) {
			Logger.getLogger(ListBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
		}

	}

	@Test
	public void testRemoveListBox() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateListBox.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = ListBox.getSimpleIterator(form);
			while (iterator.hasNext()) {
				ListBox list = (ListBox) iterator.next();
				if (list.getName().equals("list2")) {
					iterator.remove();
					break;
				}
			}
			ListBox find = null;
			while (iterator.hasNext()) {
				ListBox listbox = (ListBox) iterator.next();
				if (listbox.getName().equals("list2")) {
					find = listbox;
					break;
				}
			}
			Assert.assertNull(find);
			textDoc.save(ResourceUtilities
					.newTestOutputFile("TestRemoveListBox.odt"));
		} catch (Exception e) {
			Logger.getLogger(ListBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

	@Test
	public void testSetListBoxRectangle() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateListBox.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = ListBox.getSimpleIterator(form);
			ListBox find = null;
			while (iterator.hasNext()) {
				ListBox listbox = (ListBox) iterator.next();
				if (listbox.getName().equals("list2")) {
					find = listbox;
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
					.newTestOutputFile("TestSetListBoxRectangle.odt"));
		} catch (Exception e) {
			Logger.getLogger(ListBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

	@Test
	public void testSetAnchorType() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateListBox.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = ListBox.getSimpleIterator(form);
			ListBox find = null;
			while (iterator.hasNext()) {
				ListBox listbox = (ListBox) iterator.next();
				if (listbox.getName().equals("list3")) {
					find = listbox;
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
					.newTestOutputFile("TestSetListBoxAnchorType.odt"));
		} catch (Exception e) {
			Logger.getLogger(ListBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

	@Test
	public void testSetProperties() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateListBox.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = ListBox.getSimpleIterator(form);
			ListBox find = null;
			while (iterator.hasNext()) {
				ListBox listbox = (ListBox) iterator.next();
				if (listbox.getName().equals("list2")) {
					find = listbox;
					break;
				}
			}
			Assert.assertNotNull(find);
			// set new name
			String newName = "list4";
			find.setName(newName);
			Assert.assertEquals(newName, find.getName());
			// set drop-down visibility
			find.setFormDropdown(false);
			Assert.assertEquals(false, find.getFormDropdown());
			// set multi-selection
			find.setFormMultiSelection(true);
			Assert.assertEquals(true, find.getFormMultiSelection());
			// set list source
			String sql = "SELECT DISTINCT \"ISBN\" FROM \"biblio\" ";
			find.setListSource(sql);
			Assert.assertEquals(sql, find.getListSource());

			textDoc.save(ResourceUtilities
					.newTestOutputFile("TestSetListBoxProperties.odt"));
		} catch (Exception e) {
			Logger.getLogger(ListBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

}
