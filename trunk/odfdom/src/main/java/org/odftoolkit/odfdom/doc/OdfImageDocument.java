/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
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
package org.odftoolkit.odfdom.doc;

import java.io.File;
import java.io.InputStream;

import org.odftoolkit.odfdom.dom.element.office.OfficeImageElement;
import org.odftoolkit.odfdom.pkg.MediaType;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.xml.sax.SAXException;

/**
 * This class represents an ODF image document.
 * 
 */
public class OdfImageDocument extends OdfDocument {

	private static final String EMPTY_IMAGE_DOCUMENT_PATH = "/OdfImageDocument.odi";
	static final Resource EMPTY_IMAGE_DOCUMENT_RESOURCE = new Resource(EMPTY_IMAGE_DOCUMENT_PATH);

	/**
	 * This enum contains all possible media types of OdfImageDocument documents.
	 */
	public enum OdfMediaType implements MediaType {

		IMAGE(OdfDocument.OdfMediaType.IMAGE),
		IMAGE_TEMPLATE(OdfDocument.OdfMediaType.IMAGE_TEMPLATE);
		private final OdfDocument.OdfMediaType mMediaType;

		OdfMediaType(OdfDocument.OdfMediaType mediaType) {
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
		public static OdfDocument.OdfMediaType getOdfMediaType(String mediaType) {
			return OdfDocument.OdfMediaType.getOdfMediaType(mediaType);
		}
	}

	/**
	 * Creates an empty image document.
	 * @return ODF image document based on a default template
	 * @throws java.lang.Exception - if the document could not be created
	 */
	public static OdfImageDocument newImageDocument() throws Exception {
		return (OdfImageDocument) OdfDocument.loadTemplate(EMPTY_IMAGE_DOCUMENT_RESOURCE, OdfDocument.OdfMediaType.IMAGE);
	}

	/**
	 * Creates an empty image template.
	 * @return ODF image template based on a default
	 * @throws java.lang.Exception - if the template could not be created
	 */
	public static OdfImageDocument newImageTemplateDocument() throws Exception {
		OdfImageDocument doc = (OdfImageDocument) OdfDocument.loadTemplate(EMPTY_IMAGE_DOCUMENT_RESOURCE, OdfDocument.OdfMediaType.IMAGE_TEMPLATE);
		doc.changeMode(OdfMediaType.IMAGE_TEMPLATE);
		return doc;
	}

	/** To avoid data duplication a new document is only created, if not already opened.
	 * A document is cached by this constructor using the internalpath as key. */
	protected OdfImageDocument(OdfPackage pkg, String internalPath, OdfImageDocument.OdfMediaType odfMediaType) throws SAXException {
		super(pkg, internalPath, odfMediaType.mMediaType);
	}

	/**
	 * Creates an OdfImageDocument from the OpenDocument provided by a resource Stream.
	 *
	 * <p>Since an InputStream does not provide the arbitrary (non sequentiell)
	 * read access needed by OdfImageDocument, the InputStream is cached. This usually
	 * takes more time compared to the other createInternalDocument methods.
	 * An advantage of caching is that there are no problems overwriting
	 * an input file.</p>
	 * 
	 * <p>If the resource stream is not a ODF image document, ClassCastException might be thrown.</p>
	 *
	 * @param inputStream - the InputStream of the ODF image document.
	 * @return the image document created from the given InputStream
	 * @throws java.lang.Exception - if the document could not be created.
	 */
	public static OdfImageDocument loadDocument(InputStream inputStream) throws Exception {
        return (OdfImageDocument) OdfDocument.loadDocument(inputStream);
    }
	
	/**
	 * Loads an OdfImageDocument from the provided path.
	 *
	 * <p>OdfImageDocument relies on the file being available for read access over
	 * the whole lifecycle of OdfImageDocument.</p>
	 *
	 * <p>If the resource stream is not a ODF image document, ClassCastException might be thrown.</p>
	 * 
	 * @param documentPath - the path from where the document can be loaded
	 * @return the image document from the given path
	 *		  or NULL if the media type is not supported by ODFDOM.
	 * @throws java.lang.Exception - if the document could not be created.
	 */
	public static OdfImageDocument loadDocument(String documentPath) throws Exception {
		return (OdfImageDocument)OdfDocument.loadDocument(documentPath);
	}
	
	/**
	 * Creates an OdfImageDocument from the OpenDocument provided by a File.
	 *
	 * <p>OdfImageDocument relies on the file being available for read access over
	 * the whole lifecycle of OdfImageDocument.</p>
	 *
	 * <p>If the resource stream is not a ODF image document, ClassCastException might be thrown.</p>
	 * 
	 * @param file - a file representing the ODF image document.
	 * @return the image document created from the given File
	 * @throws java.lang.Exception - if the document could not be created.
	 */
	public static OdfImageDocument loadDocument(File file) throws Exception {
		return (OdfImageDocument)OdfDocument.loadDocument(file);
	}
	/**
	 * Get the content root of a image document.
	 *
	 * @return content root, representing the office:drawing tag
	 * @throws Exception if the file DOM could not be created.
	 */
	@Override
	public OfficeImageElement getContentRoot() throws Exception {
		return super.getContentRoot(OfficeImageElement.class);
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
