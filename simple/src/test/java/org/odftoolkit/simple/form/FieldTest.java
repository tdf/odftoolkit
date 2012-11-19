package org.odftoolkit.simple.form;

import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.draw.ControlStyleHandler;
import org.odftoolkit.simple.draw.FrameRectangle;
import org.odftoolkit.simple.style.GraphicProperties;
import org.odftoolkit.simple.style.StyleTypeDefinitions.AnchorType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FrameVerticalPosition;
import org.odftoolkit.simple.style.StyleTypeDefinitions.SupportedLinearMeasure;
import org.odftoolkit.simple.style.StyleTypeDefinitions.VerticalRelative;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class FieldTest {
	private static final FrameRectangle fieldRtg = new FrameRectangle(0.5, 2.0,
			2.9433, 0.5567, SupportedLinearMeasure.IN);

	@BeforeClass
	public static void createForm() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Form form = doc.createForm("Test Form");

			Paragraph.newParagraph(doc);
			Paragraph.newParagraph(doc);
			Paragraph.newParagraph(doc);
			Paragraph para = Paragraph.newParagraph(doc);
			FormControl dateField = form.createDateField(para, fieldRtg,
					"DateField", "20120715");
			((DateField) dateField).setSpinButonVisible(true);
			((DateField) dateField).setDropDownVisible(true);
			((DateField) dateField).formatDate("yy/MM/dd", Locale.US);

			Paragraph.newParagraph(doc);
			Paragraph.newParagraph(doc);
			Paragraph.newParagraph(doc);
			para = Paragraph.newParagraph(doc);
			FormControl timeField = form.createTimeField(para, fieldRtg,
					"TimeField", "15234000");
			((TimeField) timeField).setSpinButonVisible(true);
			((TimeField) timeField).formatTime("HH:mm a", Locale.US);

			Paragraph.newParagraph(doc);
			Paragraph.newParagraph(doc);
			Paragraph.newParagraph(doc);
			para = Paragraph.newParagraph(doc);
			FormControl numericField = form.createNumericField(para, fieldRtg,
					"NumericField", "-154.3567");
			((NumericField) numericField).setDecimalAccuracy(3);
			((NumericField) numericField).setSpinButonVisible(true);

			Paragraph.newParagraph(doc);
			Paragraph.newParagraph(doc);
			Paragraph.newParagraph(doc);
			para = Paragraph.newParagraph(doc);
			FormControl patternField = form.createPatternField(para, fieldRtg,
					"PatternField", "12345");
			((PatternField) patternField).setLiteralMask("###");
			((PatternField) patternField).setEditMask("abc");
			((PatternField) patternField).setSpinButonVisible(true);

			Paragraph.newParagraph(doc);
			Paragraph.newParagraph(doc);
			Paragraph.newParagraph(doc);
			para = Paragraph.newParagraph(doc);
			para.appendTextContent("insert currency field here:");
			FormControl currencyField = form.createCurrencyField(para,
					fieldRtg, "CurrencyField", "135.467");
			((CurrencyField) currencyField).setCurrencySymbol("CNY");
			((CurrencyField) currencyField).setDecimalAccuracy(4);
			((CurrencyField) currencyField).setSpinButonVisible(true);

			doc
					.save(ResourceUtilities
							.newTestOutputFile("TestCreateField.odt"));

		} catch (Exception e) {
			Logger.getLogger(FieldTest.class.getName()).log(Level.SEVERE, null,
					e);
		}
	}

	@Test
	public void testCreateField() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateField.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = Field.getSimpleIterator(form);
			if (iterator.hasNext()) {
				Field field = (Field) iterator.next();
				Assert.assertNotNull(field);
				Assert.assertEquals("DateField", field.getName());
				Assert.assertEquals("20120715", field.getCurrentValue());
			}
			if (iterator.hasNext()) {
				Field field = (Field) iterator.next();
				Assert.assertNotNull(field);
				Assert.assertEquals("TimeField", field.getName());
				Assert.assertEquals("15234000", field.getCurrentValue());
			}
			if (iterator.hasNext()) {
				Field field = (Field) iterator.next();
				Assert.assertNotNull(field);
				Assert.assertEquals("NumericField", field.getName());
				Assert.assertEquals("-154.3567", field.getCurrentValue());
			}
			if (iterator.hasNext()) {
				Field field = (Field) iterator.next();
				Assert.assertNotNull(field);
				Assert.assertEquals("PatternField", field.getName());
				Assert.assertEquals("12345", field.getCurrentValue());
			}
			if (iterator.hasNext()) {
				Field field = (Field) iterator.next();
				Assert.assertNotNull(field);
				Assert.assertEquals("CurrencyField", field.getName());
				Assert.assertEquals("135.467", field.getCurrentValue());
			}
		} catch (Exception e) {
			Logger.getLogger(FieldTest.class.getName()).log(Level.SEVERE, null,
					e);
		}

	}

	@Test
	public void testRemoveField() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateField.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = Field.getSimpleIterator(form);
			while (iterator.hasNext()) {
				Field field = (Field) iterator.next();
				if (field.getName().equals("PatternField")) {
					iterator.remove();
					break;
				}
			}
			Field find = null;
			while (iterator.hasNext()) {
				Field field = (Field) iterator.next();
				if (field.getName().equals("PatternField")) {
					find = field;
					break;
				}
			}
			Assert.assertNull(find);
			textDoc.save(ResourceUtilities
					.newTestOutputFile("TestRemoveField.odt"));
		} catch (Exception e) {
			Logger.getLogger(FieldTest.class.getName()).log(Level.SEVERE, null,
					e);
		}
	}

	@Test
	public void testSetFieldRectangle() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateField.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = Field.getSimpleIterator(form);
			Field find = null;
			while (iterator.hasNext()) {
				Field field = (Field) iterator.next();
				if (field.getName().equals("TimeField")) {
					find = field;
					break;
				}
			}
			Assert.assertNotNull(find);
			// change the bounding box
			find.setRectangle(new FrameRectangle(2.25455, 0.2, 6, 2.5,
					SupportedLinearMeasure.IN));
			Assert.assertEquals(6.0, find.getRectangle().getWidth());
			Assert.assertEquals(2.5, find.getRectangle().getHeight());
			Assert.assertEquals(0.2, find.getRectangle().getY());
			Assert.assertEquals(2.2546, find.getRectangle().getX());
			textDoc.save(ResourceUtilities
					.newTestOutputFile("TestSetFieldRectangle.odt"));
		} catch (Exception e) {
			Logger.getLogger(FieldTest.class.getName()).log(Level.SEVERE, null,
					e);
		}
	}

	@Test
	public void testSetAnchorType() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateField.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = Field.getSimpleIterator(form);
			Field find = null;
			while (iterator.hasNext()) {
				Field field = (Field) iterator.next();
				if (field.getName().equals("CurrencyField")) {
					find = field;
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
					.newTestOutputFile("TestSetFieldAnchorType.odt"));
		} catch (Exception e) {
			Logger.getLogger(FieldTest.class.getName()).log(Level.SEVERE, null,
					e);
		}
	}

	@Test
	public void testSetFormat() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateField.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = Field.getSimpleIterator(form);
			Field find = null;
			while (iterator.hasNext()) {
				Field field = (Field) iterator.next();
				if (field.getName().equals("DateField")) {
					find = field;
					break;
				}
			}
			Assert.assertNotNull(find);

			// set date format
			String date = "19840605";
			String format = "yy-MM-dd";
			find.setCurrentValue(date);
			((DateField) find).formatDate(format, Locale.GERMANY);
			Assert.assertEquals(date, find.getCurrentValue());
			Assert.assertEquals(format, ((DateField) find).getDateFormat());
			find.setCurrentValue(null);
			Assert.assertEquals("", find.getCurrentValue());
			try {
				((DateField) find).formatDate(null, Locale.US);
			} catch (IllegalArgumentException e) {
				Assert.assertTrue(true);
			}
			while (iterator.hasNext()) {
				Field field = (Field) iterator.next();
				if (field.getName().equals("TimeField")) {
					find = field;
					break;
				}
			}
			
			textDoc.save(ResourceUtilities
					.newTestOutputFile("TestSetFormatValue.odt"));
		} catch (Exception e) {
			Logger.getLogger(FieldTest.class.getName()).log(Level.SEVERE, null,
					e);
		}
	}
}
