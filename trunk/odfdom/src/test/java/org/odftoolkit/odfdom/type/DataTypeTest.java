/************************************************************************
* 
*  Licensed to the Apache Software Foundation (ASF) under one
*  or more contributor license agreements.  See the NOTICE file
*  distributed with this work for additional information
*  regarding copyright ownership.  The ASF licenses this file
*  to you under the Apache License, Version 2.0 (the
*  "License"); you may not use this file except in compliance
*  with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*
************************************************************************/
package org.odftoolkit.odfdom.type;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeFactory;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.odfdom.type.Length.Unit;

public class DataTypeTest {

	private static final Logger LOG = Logger.getLogger(DataTypeTest.class.getName());

	@Test
	public void testAnyURI() {
		// AnyURI
		AnyURI anyURI = AnyURI.valueOf("./Object 1");
		URI uri = anyURI.getURI();
		Assert.assertTrue(AnyURI.isValid(uri));
		try {
			uri = new URI(URITransformer.encodePath("http://www.sina.com"));
			Assert.assertTrue(AnyURI.isValid(uri));
		} catch (URISyntaxException e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	
	@Test
	public void testBase64() {
		// Base64Binary
		Base64Binary base64Binary = Base64Binary.valueOf("GVCC9H6p8LeqecY96ggY680uoZA=");
		byte[] bytes = base64Binary.getBytes();
		LOG.info("bytes:" + bytes.length);
		Assert.assertTrue(Base64Binary.isValid("KWy1spZbKcHOunnKMB6dVA=="));
	}
	
	@Test
	public void testCellAddress() {		
		// CellAddress
		CellAddress cellAddress = new CellAddress("Sheet1.A3");
		Assert.assertEquals(cellAddress.toString(), "Sheet1.A3");
		Assert.assertFalse(CellAddress.isValid("33"));
		Assert.assertTrue(CellAddress.isValid("$.$Z11"));
	}
	
	@Test
	public void testCellRangeAddress() {
		// CellRangeAddress
		CellRangeAddress cellRangeAddress1 = CellRangeAddress.valueOf("A.A1:A.F19");
		CellRangeAddress cellRangeAddress2 = new CellRangeAddress(
				"$(first).8:$(second).19");
		CellRangeAddress cellRangeAddress3 = new CellRangeAddress("$.$8:$.19");
		Assert.assertTrue(CellRangeAddress.isValid("$Sheet1.B12:$Sheet1.E35"));
		
		// CellRangeAddressList
		CellRangeAddressList addressList = CellRangeAddressList.valueOf(cellRangeAddress1.toString() + " " + cellRangeAddress2.toString());
		Assert.assertEquals(addressList.getCellRangesAddressList().get(0).toString(), cellRangeAddress1.toString());
		CellRangeAddressList addressList2 = null;
		try {
			addressList2 = CellRangeAddressList.valueOf("");
		} catch (IllegalArgumentException ex) {
			// CellRangeAddressList is not allowed to have a empty string
			Assert.assertNull(addressList2);
		}
	}
	
	@Test
	public void testColor() {
		// Color
		try {
			Color color = new Color("#ff00ff");
			String hexColor = Color.toSixDigitHexRGB("rgb(123,214,23)");
			Assert.assertTrue(Color.isValid(hexColor));
			java.awt.Color awtColor = Color.mapColorToAWTColor(color);
			Assert.assertEquals(new java.awt.Color(0xff00ff), awtColor);
			try {
				color = new Color(255, 0, 255);
				Assert.assertEquals("#ff00ff", color.toString());
				color = new Color(new java.awt.Color(255, 0, 255));
				Assert.assertEquals("#ff00ff", color.toString());
				color = new Color(1.0f, 0.0f, 1.0f);
				Assert.assertEquals("#ff00ff", color.toString());
				color = new Color("#f0f");
				Assert.assertEquals("#ff00ff", color.toString());
				Assert.assertEquals("#ff00ff", Color.FUCHSIA.toString());
			} catch (IllegalArgumentException ie) {
				Assert.fail(ie.getMessage());
			}
			try {
				Assert.assertEquals("#ff0000", Color.toSixDigitHexRGB("rgb(255,0,0)"));
				Assert.assertEquals("#ff0000", Color.toSixDigitHexRGB("rgb(300,0,0)"));
				Assert.assertEquals("#ff0000", Color.toSixDigitHexRGB("rgb(110%, 0%, 0%)"));
				Assert.assertEquals("#ff00ff", Color.toSixDigitHexRGB("fuchsia"));
				Assert.assertEquals("#ff0000", Color.toSixDigitHexRGB("#ff0000"));
				Assert.assertEquals("#ff0000", Color.toSixDigitHexRGB("#f00"));
			} catch (IllegalArgumentException ie) {
				Assert.fail(ie.getMessage());
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	@Test
	public void testDateTime() {
		// DateOrDateTime
		DateTime time1 = DateTime.valueOf("2007-09-28T22:01:13");
		Assert.assertNotNull(time1);
		Date time2 = null;
		try {
			time2 = Date.valueOf("2007-09-28T22:01:13");
		} catch (IllegalArgumentException ex) {
			Assert.assertNull(time2);
			time2 = Date.valueOf("2007-09-28");
			Assert.assertNotNull(time2);
		}

		DatatypeFactory aFactory = new org.apache.xerces.jaxp.datatype.DatatypeFactoryImpl();
		GregorianCalendar calendar = new GregorianCalendar();
		LOG.info(aFactory.newXMLGregorianCalendar(calendar).toString());
		DateOrDateTime time3 = new DateOrDateTime(aFactory.newXMLGregorianCalendar(calendar));
		Assert.assertNotNull(time3.getXMLGregorianCalendar());
	}
	
	@Test
	public void testStyle() {
		// StyleName,StyleNameRef,StyleNameList
		StyleName styleName1 = new StyleName("ta1");
		StyleName styleName2 = new StyleName("_22");
		StyleName styleName3 = StyleName.valueOf("cc_a");
		Assert.assertFalse(StyleName.isValid(""));
		Assert.assertFalse(StyleName.isValid("t:1"));
		StyleNameRef styleNameRef1 = StyleNameRef.valueOf("ce1");
		Assert.assertTrue(StyleNameRef.isValid(""));
		List<StyleName> styleList = new ArrayList<StyleName>();
		styleList.add(StyleName.valueOf(styleNameRef1.toString()));
		styleList.add(styleName1);
		styleList.add(styleName2);
		StyleNameRefs styleRefs = new StyleNameRefs(styleList);
		Assert.assertEquals(styleRefs.getStyleNameRefList().get(2).toString(),
				styleName2.toString());
		// StyleNameRefs is allowed to be empty string, it is defined to have
		// zero or more NCName
		Assert.assertTrue(StyleNameRefs.isValid(""));
		styleList = StyleNameRefs.valueOf("").getStyleNameRefList();
		Assert.assertTrue(styleList.size() == 0);
	}
	
	@Test
	public void testItegerPercent() {
		// Integer,Percent
		PositiveInteger positiveInt = new PositiveInteger(1);
		NonNegativeInteger nnInt = new NonNegativeInteger(positiveInt.intValue());
		Assert.assertFalse(NonNegativeInteger.isValid(-23));
		Percent percent = new Percent(0.3);
		Percent percent1 = Percent.valueOf("30.0%");
		Assert.assertTrue(percent1.doubleValue() == percent.doubleValue());
	}
	
	@Test
	public void testMeasurement() {
		// Measurement
		String inchMeasure = "-4.354in";
		Length length = new Length(inchMeasure);
		String cmMeasure = length.mapToUnit(Unit.CENTIMETER);
		NonNegativeLength nnLength = NonNegativeLength.valueOf(cmMeasure.substring(1));
		Assert.assertEquals(nnLength.mapToUnit(Unit.INCH), "4.354in");
		Assert.assertTrue(PositiveLength.isValid("0.01pt"));
		Assert.assertTrue(NonNegativeLength.isValid("0.00pt"));
		Assert.assertFalse(NonNegativeLength.isValid("-0.00pt"));
		int mmValue = Length.parseInt(length.toString(), Unit.MILLIMETER);

		NonNegativePixelLength pixelLength = NonNegativePixelLength.valueOf("1240px");
		NonNegativePixelLength pixelLength1 = null;
		try {
			pixelLength1 = new NonNegativePixelLength("234cm");
		} catch (NumberFormatException ex) {
			Assert.assertNull(pixelLength1);
		}
		
		// make sure Units are resolved correctly
		Unit unit = Length.parseUnit("cm");
		Assert.assertEquals(Unit.CENTIMETER, unit);
	}
}
