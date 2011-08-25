/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 * Copyright 2009 IBM. All rights reserved.
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
package org.odftoolkit.simple;

import java.io.File;
import java.io.InputStream;

import org.odftoolkit.odfdom.dom.element.office.OfficeSpreadsheetElement;
import org.odftoolkit.odfdom.pkg.MediaType;
import org.odftoolkit.odfdom.pkg.OdfPackage;

/**
 * This class represents an empty ODF spreadsheet document.
 * 
 */
public class SpreadsheetDocument extends Document {

	private static final String EMPTY_SPREADSHEET_DOCUMENT_PATH = "/OdfSpreadsheetDocument.ods";
	static final Resource EMPTY_SPREADSHEET_DOCUMENT_RESOURCE = new Resource(EMPTY_SPREADSHEET_DOCUMENT_PATH);

	/**
	 * This enum contains all possible media types of SpreadsheetDocument
	 * documents.
	 */
	public enum OdfMediaType implements MediaType {

		SPREADSHEET(Document.OdfMediaType.SPREADSHEET),
		SPREADSHEET_TEMPLATE(Document.OdfMediaType.SPREADSHEET_TEMPLATE);
		private final Document.OdfMediaType mMediaType;

		OdfMediaType(Document.OdfMediaType mediaType) {
			this.mMediaType = mediaType;
		}

		/**
		 * @return the mediatype of this document
		 */
		public String getMediaTypeString() {
			return mMediaType.getMediaTypeString();
		}

		/**
		 * @return the ODF filesuffix of this document
		 */
		public String getSuffix() {
			return mMediaType.getSuffix();
		}

		/**
		 *
		 * @param mediaType string defining an ODF document
		 * @return the according OdfMediatype encapuslating the given string and the suffix
		 */
		public static Document.OdfMediaType getOdfMediaType(String mediaType) {
			return Document.OdfMediaType.getOdfMediaType(mediaType);
		}
	}

	/**
	 * Creates an empty spreadsheet document.
	 * @return ODF spreadsheet document based on a default template*
	 * @throws java.lang.Exception - if the document could not be created
	 */
	public static SpreadsheetDocument newSpreadsheetDocument() throws Exception {
		return (SpreadsheetDocument) Document.loadTemplate(EMPTY_SPREADSHEET_DOCUMENT_RESOURCE, Document.OdfMediaType.SPREADSHEET);
	}

	/**
	 * Creates an empty spreadsheet template.
	 * @return ODF spreadsheet template based on a default
	 * @throws java.lang.Exception - if the template could not be created
	 */
	public static SpreadsheetDocument newSpreadsheetTemplateDocument() throws Exception {
		SpreadsheetDocument doc = (SpreadsheetDocument) Document.loadTemplate(EMPTY_SPREADSHEET_DOCUMENT_RESOURCE, Document.OdfMediaType.SPREADSHEET_TEMPLATE);
		doc.changeMode(OdfMediaType.SPREADSHEET_TEMPLATE);
		return doc;
	}

	/** To avoid data duplication a new document is only created, if not already opened.
	 * A document is cached by this constructor using the internalpath as key. */
	protected SpreadsheetDocument(OdfPackage pkg, String internalPath, SpreadsheetDocument.OdfMediaType odfMediaType) {
		super(pkg, internalPath, odfMediaType.mMediaType);
	}

	/**
	 * Creates an SpreadsheetDocument from the OpenDocument provided by a resource Stream.
	 *
	 * <p>Since an InputStream does not provide the arbitrary (non sequentiell)
	 * read access needed by SpreadsheetDocument, the InputStream is cached. This usually
	 * takes more time compared to the other createInternalDocument methods.
	 * An advantage of caching is that there are no problems overwriting
	 * an input file.</p>
	 * 
	 * <p>If the resource stream is not a ODF spreadsheet document, ClassCastException might be thrown.</p>
	 *
	 * @param inputStream - the InputStream of the ODF spreadsheet document.
	 * @return the spreadsheet document created from the given InputStream
	 * @throws java.lang.Exception - if the document could not be created.
	 */
	public static SpreadsheetDocument loadDocument(InputStream inputStream) throws Exception {
        return (SpreadsheetDocument) Document.loadDocument(inputStream);
    }
	
	/**
	 * Loads an SpreadsheetDocument from the provided path.
	 *
	 * <p>SpreadsheetDocument relies on the file being available for read access over
	 * the whole lifecycle of SpreadsheetDocument.</p>
	 *
	 * <p>If the resource stream is not a ODF spreadsheet document, ClassCastException might be thrown.</p>
	 * 
	 * @param documentPath - the path from where the document can be loaded
	 * @return the spreadsheet document from the given path
	 *		  or NULL if the media type is not supported by ODFDOM.
	 * @throws java.lang.Exception - if the document could not be created.
	 */
	public static SpreadsheetDocument loadDocument(String documentPath) throws Exception {
		return (SpreadsheetDocument)Document.loadDocument(documentPath);
	}
	
	/**
	 * Creates an SpreadsheetDocument from the OpenDocument provided by a File.
	 *
	 * <p>SpreadsheetDocument relies on the file being available for read access over
	 * the whole lifecycle of SpreadsheetDocument.</p>
	 *
	 * <p>If the resource stream is not a ODF spreadsheet document, ClassCastException might be thrown.</p>
	 * 
	 * @param file - a file representing the ODF spreadsheet document.
	 * @return the spreadsheet document created from the given File
	 * @throws java.lang.Exception - if the document could not be created.
	 */
	public static SpreadsheetDocument loadDocument(File file) throws Exception {
		return (SpreadsheetDocument)Document.loadDocument(file);
	}
	
	/**
	 * Get the content root of a spreadsheet document.
	 *
	 * @return content root, representing the office:spreadsheet tag
	 * @throws Exception if the file DOM could not be created.
	 */
	@Override
	public OfficeSpreadsheetElement getContentRoot() throws Exception {
		return super.getContentRoot(OfficeSpreadsheetElement.class);
	}

	/**
	 * Changes the document to the given mediatype.
	 * This method can only be used to convert a document to a related mediatype, e.g. template.
	 *
	 * @param mediaType the related ODF mimetype
	 */
	public void changeMode(OdfMediaType mediaType) {
		setOdfMediaType(mediaType.mMediaType);
	}
}
