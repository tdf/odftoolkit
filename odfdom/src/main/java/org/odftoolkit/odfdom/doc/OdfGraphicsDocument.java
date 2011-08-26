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
package org.odftoolkit.odfdom.doc;

import org.odftoolkit.odfdom.doc.office.OdfOfficeDrawing;
import org.odftoolkit.odfdom.pkg.MediaType;
import org.odftoolkit.odfdom.pkg.OdfPackage;

/**
 * This class represents an empty ODF graphics document.
 * 
 */
public class OdfGraphicsDocument extends OdfDocument {

	private static final String EMPTY_GRAPHICS_DOCUMENT_PATH = "/OdfGraphicsDocument.odg";
	static final Resource EMPTY_GRAPHICS_DOCUMENT_RESOURCE = new Resource(EMPTY_GRAPHICS_DOCUMENT_PATH);

	/**
	 * This enum contains all possible media types of OdfGraphicsDocument documents.
	 */
	public enum OdfMediaType implements MediaType {

		GRAPHICS(OdfDocument.OdfMediaType.GRAPHICS),
		GRAPHICS_TEMPLATE(OdfDocument.OdfMediaType.GRAPHICS_TEMPLATE);
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
	 * Creates an empty graphics document.
	 * @return ODF graphics document based on a default template
	 * @throws java.lang.Exception - if the document could not be created
	 */
	public static OdfGraphicsDocument newGraphicsDocument() throws Exception {
		return (OdfGraphicsDocument) OdfDocument.loadTemplate(EMPTY_GRAPHICS_DOCUMENT_RESOURCE, OdfDocument.OdfMediaType.GRAPHICS);
	}

	/**
	 * Creates an empty graphics template.
	 * @return ODF graphics template based on a default
	 * @throws java.lang.Exception - if the template could not be created
	 */
	public static OdfGraphicsDocument newGraphicsTemplateDocument() throws Exception {
		OdfGraphicsDocument doc = (OdfGraphicsDocument) OdfDocument.loadTemplate(EMPTY_GRAPHICS_DOCUMENT_RESOURCE, OdfDocument.OdfMediaType.GRAPHICS_TEMPLATE);
		doc.changeMode(OdfMediaType.GRAPHICS_TEMPLATE);
		return doc;
	}

	// Using static factory instead of constructor
	protected OdfGraphicsDocument(OdfPackage pkg, String internalPath, OdfGraphicsDocument.OdfMediaType odfMediaType) {
		super(pkg, internalPath, odfMediaType.mMediaType);
	}

	/**
	 * Get the content root of a graphics document.
	 *
	 * @return content root, representing the office:drawing tag
	 * @throws Exception if the file DOM could not be created.
	 */
	@Override
	public OdfOfficeDrawing getContentRoot() throws Exception {
		return super.getContentRoot(OdfOfficeDrawing.class);
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
