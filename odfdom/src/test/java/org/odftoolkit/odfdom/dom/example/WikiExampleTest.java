/* 
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

package org.odftoolkit.odfdom.dom.example;

import java.net.URI;
import org.junit.Ignore;

import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeTextElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfPackage;

/** The test was deactivated. As an invalid value will no longer throw an exception and cause the removal of the attribute. */
public class WikiExampleTest {

	@Test
	@Ignore
	public void testWikiExamples1() throws Exception {

		// WIKI EXAMPLE I from http://odftoolkit.org/projects/odfdom/pages/Home

		// Create a text document from a standard template (empty documents within the JAR)
		OdfTextDocument odt = OdfTextDocument.newTextDocument();

		// Append text to the end of the document.
		odt.addText("This is my very first ODF test");

		// Save document
		odt.save("MyFilename.odt");
	}


	@Test
	@Ignore
	public void testWikiExamples2() throws Exception {

		//********************************************************************
		// WIKI EXAMPLE I from http://odftoolkit.org/projects/odfdom/pages/Layers

		// loads the ODF document package from the path
		OdfPackage pkg = OdfPackage.loadPackage("/home/myDocuments/myVacation.odt");

		// loads the image from the URL and inserts the image in the package,
		// adapting the manifest
		pkg.insert(new URI("./myHoliday.png"), "Pictures/myHoliday.png", "image/png");
		pkg.save("/home/myDocuments/myVacation.odt");



		//********************************************************************
		// WIKI EXAMPLE II from http://odftoolkit.org/projects/odfdom/pages/Layers

		// Load file
		OdfTextDocument odt = (OdfTextDocument) OdfDocument.loadDocument("ImageIn.odt");

		// get root of all content of a text document
		OfficeTextElement officeText = odt.getContentRoot();

		// get first paragraph
		TextPElement firstParagraph =
		  OdfElement.findFirstChildNode(TextPElement.class, officeText);

		// XPath alternative to get the first paragraph
		/*
		  XPath xpath = XPathFactory.newInstance().newXPath();
		  xpath.setNamespaceContext(new OdfNamespace());
		  OdfFileDom dom = odt.getContentDom();
		  firstParagraph = (TextPElement) xpath.evaluate("//text:p[1]", dom, XPathConstants.NODE);
		*/

		// insert a frame
		DrawFrameElement frame = firstParagraph.newDrawFrameElement();

		// insert an image: This is a class from the Document API
		OdfDrawImage image = (OdfDrawImage) frame.newDrawImageElement();
		image.newImage(new URI("./MySampleImage.png"));

		// Save file
		odt.save("ImageOut.odt");

		//********************************************************************
		// WIKI EXAMPLE III from http://odftoolkit.org/projects/odfdom/pages/Layers

		// Load Image
		odt = (OdfTextDocument) OdfDocument.loadDocument("ImageIn.odt");

		// Play around with text
		odt.addText("When there is no paragraph, the text will be embedded in a new paragraph");
		odt.newParagraph("Create new paragraph");
		odt.addText("\nThis is a new line");

		// Insert Image and make last paragraph its anchor
		odt.newImage(new URI("./MySampleImage.png"));

		// Insert new spreadsheet as sub document into the package within directory  "myOdsDirectoryPath/"
		odt.insertDocument(OdfSpreadsheetDocument.newSpreadsheetDocument(), "myOdsDirectoryPath");

		// Save file
		odt.save("ImageOut.odt");
	}
}
