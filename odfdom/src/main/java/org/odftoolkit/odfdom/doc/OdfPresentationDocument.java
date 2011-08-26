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

import java.util.Collection;
import java.util.EnumSet;

import org.odftoolkit.odfdom.doc.office.OdfOfficePresentation;

/**
 * This class represents an empty ODF presentation file.
 * Note: The way of receiving a new empty OdfPresentationDocument will probably change. 
 * In the future the streams and DOM representation of an OpenDocument file will
 * be clonable and this stream buffering will be neglected.
 * 
 */
public class OdfPresentationDocument extends OdfDocument {

	private static String EMPTY_PRESENTATION_DOCUMENT_PATH = "/OdfPresentationDocument.odp";
	private static Resource EMPTY_PRESENTATION_DOCUMENT_RESOURCE = new Resource(EMPTY_PRESENTATION_DOCUMENT_PATH);

	/**
	 * This enum contains all possible media types of OdfPresentationDocument
	 * documents.
	 */
	public enum SupportedType {

		PRESENTATION(OdfMediaType.PRESENTATION),
		PRESENTATION_TEMPLATE(OdfMediaType.PRESENTATION_TEMPLATE);
		private final OdfMediaType mMediaType;

		SupportedType(OdfMediaType mediaType) {
			this.mMediaType = mediaType;
		}

		public OdfMediaType getOdfMediaType() {
			return mMediaType;
		}

		@Override
		public String toString() {
			return mMediaType.toString();
		}
	}

	/**
	 * Creates an empty presentation document.
	 * @return ODF presentation document based on a default template
	 * @throws java.lang.Exception - if the document could not be created
	 */
	public static OdfPresentationDocument newPresentationDocument() throws Exception {
		return (OdfPresentationDocument) OdfDocument.loadTemplate(EMPTY_PRESENTATION_DOCUMENT_RESOURCE);
	}

	/**
	 * Creates an empty presentation template.
	 * @return ODF presentation template based on a default
	 * @throws Exception - if the template could not be created
	 */
	public static OdfPresentationDocument newPresentationTemplateDocument() throws Exception {
		OdfPresentationDocument doc = (OdfPresentationDocument) OdfDocument.loadTemplate(EMPTY_PRESENTATION_DOCUMENT_RESOURCE);
		doc.changeMode(SupportedType.PRESENTATION_TEMPLATE);
		return doc;
	}

	// Using static factory instead of constructor
	protected OdfPresentationDocument() {
	}

	/**
	 * Get the content root of a presentation document.
	 *
	 * @return content root, representing the office:presentation tag
	 * @throws Exception if the file DOM could not be created.
	 */
	public OdfOfficePresentation getContentRoot() throws Exception {
		return super.getContentRoot(OdfOfficePresentation.class);
	}

	/**
	 * Switches this instance to the given type. This method can be used to e.g. convert
	 * a document instance to a template and vice versa. If the type is not supported by
	 * the concrete document instance an IllegalArgumentException is raised.
	 * @param type
	 */
	public void changeMode(SupportedType type) {
		setMediaType(type.getOdfMediaType());
		getPackage().setMediaType(type.toString());
	}
}
