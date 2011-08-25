/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2010 IBM. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ************************************************************************/
package org.odftoolkit.simple.style;

import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.style.StyleTypeDefinitions.SimpleFontStyle;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.CellStyleHandler;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class FontTest {

	static final String filename = "testGetCellAt.ods";
	
	@Test
	public void testGetSetFont()
	{
		Font font1Base = new Font("Arial",SimpleFontStyle.ITALIC,10,Color.BLACK);
		Font font2Base = new Font("'Times New Roman'",SimpleFontStyle.REGULAR,(float)13.95,new Color("#ff3333"));
		Font font3Base = new Font("SimSun",SimpleFontStyle.BOLD,8,Color.BLACK);
		Font font4Base = new Font("Arial",SimpleFontStyle.REGULAR,10,Color.BLACK);
		try {
			SpreadsheetDocument doc = SpreadsheetDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(filename));
			Table table = doc.getTableByName("A");
			Cell cell1 = table.getCellByPosition("A2");
			CellStyleHandler handler1 = cell1.getStyleHandler();			
			Font font1 = handler1.getFont(Document.ScriptType.WESTERN);
			Assert.assertEquals(font1Base,font1);
			
			Cell cell2 = table.getCellByPosition("A3");
			CellStyleHandler handler2 = cell2.getStyleHandler();			
			Font font2 = handler2.getFont(Document.ScriptType.WESTERN);
			Assert.assertEquals(font2Base,font2);

			Cell cell3 = table.getCellByPosition("A4");
			CellStyleHandler handler3 = cell3.getStyleHandler();			
			Font font3 = handler3.getFont(Document.ScriptType.CJK);
			Assert.assertEquals(font3Base,font3);
			Font font4 = handler3.getFont(Document.ScriptType.WESTERN);
			Assert.assertEquals(font4Base,font4);
			
			Cell cell5 = table.getCellByPosition("B2");
			cell5.getStyleHandler().setFont(font1Base);
			cell5.setStringValue("Arial Italic black 10");
			Assert.assertEquals(font1Base, cell5.getStyleHandler().getFont(Document.ScriptType.WESTERN));

			Cell cell6 = table.getCellByPosition("B3");
			font2Base.setLocale(new Locale(Locale.ENGLISH.getLanguage(), Locale.US.getCountry()));
			cell6.getStyleHandler().setFont(font2Base);
			cell6.setStringValue("Times New Roman, Regular, 13.9, Red");
			Assert.assertEquals(font2Base, cell6.getStyleHandler().getFont(Document.ScriptType.WESTERN));
			
			Cell cell7 = table.getCellByPosition("B4");
			font3Base.setLocale(new Locale(Locale.CHINESE.getLanguage(), Locale.CHINA.getCountry()));
			font4Base.setLocale(new Locale(Locale.ENGLISH.getLanguage(), Locale.US.getCountry()));
			cell7.getStyleHandler().setFont(font3Base);
			cell7.getStyleHandler().setFont(font4Base);
			cell7.setStringValue("SimSun BOLD 8 BLACK");
			Assert.assertEquals(font3Base, cell7.getStyleHandler().getFont(Document.ScriptType.CJK));
			Assert.assertEquals(font4Base, cell7.getStyleHandler().getFont(Document.ScriptType.WESTERN));
			
			doc.save(ResourceUtilities.newTestOutputFile("testFontOutput.ods"));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

//	public StyleTypeDefinitions.SimpleFontStyle getFontStyle(
//			Document.ScriptType type);
//
//	public void setFontStyle(StyleTypeDefinitions.SimpleFontStyle style);
//
//	public void setFontStyle(StyleTypeDefinitions.SimpleFontStyle style,
//			Document.ScriptType type);
//
//	public int getFontSizeInPoint();
//
//	public int getFontSizeInPoint(Document.ScriptType type);
//
//	public void setFontSizeInPoint(int size);
//
//	public void setFontSizeInPoint(int size, Document.ScriptType type);
//
//	public String getLanguage();
//
//	public String getLanguage(Document.ScriptType type);
//
//	public void setLanguage(String language);
//
//	public void setLanguage(String language, Document.ScriptType type);
//
//	public String getCountry();
//
//	public String getCountry(Document.ScriptType type);
//
//	public void setCountry(String country);
//
//	public void setCountry(String country, Document.ScriptType type);
//
//	public Color getFontColor();
//
//	public void setFontColor(Color fontColor);
//
//	public String getFontName();
//
//	public String getFontName(Document.ScriptType type);
//
//	public void setFontName(String fontName);
//
//	public void setFontName(String fontName, Document.ScriptType type);
//
//	public Font getFont();
//
//	public Font getFont(Document.ScriptType type);
//
//	public void setFont(Font font);
//
//	public String getFontFamilyName(Document.ScriptType type);
}
