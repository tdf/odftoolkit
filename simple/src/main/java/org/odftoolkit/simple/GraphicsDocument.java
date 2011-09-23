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
package org.odftoolkit.simple;

import java.io.File;
import java.io.InputStream;

import org.odftoolkit.odfdom.dom.element.office.OfficeDrawingElement;
import org.odftoolkit.odfdom.pkg.MediaType;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfPackage;

/**
 * This class represents an empty ODF graphics document.
 * 
 */
public class GraphicsDocument extends Document {

	private static final String EMPTY_GRAPHICS_DOCUMENT_PATH = "/OdfGraphicsDocument.odg";
	static final Resource EMPTY_GRAPHICS_DOCUMENT_RESOURCE = new Resource(EMPTY_GRAPHICS_DOCUMENT_PATH);

	/**
	 * This enum contains all possible media types of GraphicsDocument
	 * documents.
	 */
	public enum OdfMediaType implements MediaType {

		GRAPHICS(Document.OdfMediaType.GRAPHICS), GRAPHICS_TEMPLATE(Document.OdfMediaType.GRAPHICS_TEMPLATE);
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
		 * @param mediaType
		 *            string defining an ODF document
		 * @return the according OdfMediatype encapuslating the given string and
		 *         the suffix
		 */
		public static Document.OdfMediaType getOdfMediaType(String mediaType) {
			return Document.OdfMediaType.getOdfMediaType(mediaType);
		}
	}

	/**
	 * Creates an empty graphics document.
	 * 
	 * @return ODF graphics document based on a default template
	 * @throws java.lang.Exception
	 *             - if the document could not be created
	 */
	public static GraphicsDocument newGraphicsDocument() throws Exception {
		return (GraphicsDocument) Document.loadTemplate(EMPTY_GRAPHICS_DOCUMENT_RESOURCE,
				Document.OdfMediaType.GRAPHICS);
	}

	/**
	 * Creates an empty graphics template.
	 * 
	 * @return ODF graphics template based on a default
	 * @throws java.lang.Exception
	 *             - if the template could not be created
	 */
	public static GraphicsDocument newGraphicsTemplateDocument() throws Exception {
		GraphicsDocument doc = (GraphicsDocument) Document.loadTemplate(EMPTY_GRAPHICS_DOCUMENT_RESOURCE,
				Document.OdfMediaType.GRAPHICS_TEMPLATE);
		doc.changeMode(OdfMediaType.GRAPHICS_TEMPLATE);
		return doc;
	}

	/**
	 * To avoid data duplication a new document is only created, if not already
	 * opened. A document is cached by this constructor using the internalpath
	 * as key.
	 */
	protected GraphicsDocument(OdfPackage pkg, String internalPath, GraphicsDocument.OdfMediaType odfMediaType) {
		super(pkg, internalPath, odfMediaType.mMediaType);
	}

	/**
	 * Creates an GraphicsDocument from the OpenDocument provided by a resource
	 * Stream.
	 * 
	 * <p>
	 * Since an InputStream does not provide the arbitrary (non sequentiell)
	 * read access needed by GraphicsDocument, the InputStream is cached. This
	 * usually takes more time compared to the other createInternalDocument
	 * methods. An advantage of caching is that there are no problems
	 * overwriting an input file.
	 * </p>
	 * 
	 * <p>
	 * If the resource stream is not a ODF graphics document, ClassCastException
	 * might be thrown.
	 * </p>
	 * 
	 * @param inputStream
	 *            - the InputStream of the ODF graphics document.
	 * @return the graphics document created from the given InputStream
	 * @throws java.lang.Exception
	 *             - if the document could not be created.
	 */
	public static GraphicsDocument loadDocument(InputStream inputStream) throws Exception {
		return (GraphicsDocument) Document.loadDocument(inputStream);
	}

	/**
	 * Loads an GraphicsDocument from the provided path.
	 * 
	 * <p>
	 * GraphicsDocument relies on the file being available for read access over
	 * the whole lifecycle of GraphicsDocument.
	 * </p>
	 * 
	 * <p>
	 * If the resource stream is not a ODF graphics document, ClassCastException
	 * might be thrown.
	 * </p>
	 * 
	 * @param documentPath
	 *            - the path from where the document can be loaded
	 * @return the graphics document from the given path or NULL if the media
	 *         type is not supported by SIMPLE.
	 * @throws java.lang.Exception
	 *             - if the document could not be created.
	 */
	public static GraphicsDocument loadDocument(String documentPath) throws Exception {
		return (GraphicsDocument) Document.loadDocument(documentPath);
	}

	/**
	 * Creates an GraphicsDocument from the OpenDocument provided by a File.
	 * 
	 * <p>
	 * GraphicsDocument relies on the file being available for read access over
	 * the whole lifecycle of GraphicsDocument.
	 * </p>
	 * 
	 * <p>
	 * If the resource stream is not a ODF graphics document, ClassCastException
	 * might be thrown.
	 * </p>
	 * 
	 * @param file
	 *            - a file representing the ODF graphics document.
	 * @return the graphics document created from the given File
	 * @throws java.lang.Exception
	 *             - if the document could not be created.
	 */
	public static GraphicsDocument loadDocument(File file) throws Exception {
		return (GraphicsDocument) Document.loadDocument(file);
	}

	/**
	 * Get the content root of a graphics document.
	 * 
	 * @return content root, representing the office:drawing tag
	 * @throws Exception
	 *             if the file DOM could not be created.
	 */
	@Override
	public OfficeDrawingElement getContentRoot() throws Exception {
		return super.getContentRoot(OfficeDrawingElement.class);
	}

	/**
	 * Changes the document to the given mediatype. This method can only be used
	 * to convert a document to a related mediatype, e.g. template.
	 * 
	 * @param mediaType
	 *            the related ODF mimetype
	 */
	public void changeMode(OdfMediaType mediaType) {
		setOdfMediaType(mediaType.mMediaType);
	}

	public OdfElement getTableContainerElement() {
		throw new UnsupportedOperationException("Graphics document is not supported to hold table now.");
	}
}
