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

import org.odftoolkit.odfdom.doc.office.OdfOfficeChart;

/**
 * This class represents an empty ODF document file, which will be embedded
 * in an existing ODF document.
 * Note: The way of receiving a new empty OdfEmbeddedDocument will probably change. 
 * In the future the streams and DOM representation of an OpenDocument file will
 * be clonable and this stream buffering will be neglected.
 */
public class OdfChartDocument extends OdfDocument {

	private static String EMPTY_CHART_DOCUMENT_PATH = "/OdfChartDocument.odc";
	private static Resource EMPTY_CHART_DOCUMENT_RESOURCE = new Resource(EMPTY_CHART_DOCUMENT_PATH);

	/**
	 * This enum contains all possible media types of OdfChartDocument documents.
	 */
	public enum SupportedType {

		CHART(OdfMediaType.CHART),
		CHART_TEMPLATE(OdfMediaType.CHART_TEMPLATE);
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
	 * Creates an empty charts document.
	 * *  <br/><em>Note: ODF Chart documents are (with OOo 3.0) only used as embedded document and not used stand-alone.</em>
	 * @return ODF charts document based on a default template
	 * @throws java.lang.Exception - if the document could not be created
	 */
	public static OdfChartDocument newChartDocument() throws Exception {
		return (OdfChartDocument) OdfDocument.loadTemplate(EMPTY_CHART_DOCUMENT_RESOURCE);
	}

	/**
	 * Creates an empty charts template.
	 * *  <br/><em>Note: ODF Chart documents are (with OOo 3.0) only used as embedded document and not used stand-alone.</em>
	 * @return ODF charts template based on a default template
	 * @throws java.lang.Exception - if the template could not be created
	 */
	public static OdfChartDocument newChartTemplateDocument() throws Exception {
		OdfChartDocument doc = (OdfChartDocument) OdfDocument.loadTemplate(EMPTY_CHART_DOCUMENT_RESOURCE);
		doc.changeMode(SupportedType.CHART_TEMPLATE);
		return doc;
	}

	// Using static factory instead of constructor
	protected OdfChartDocument() {
	}

	/**
	 * Get the content root of a chart document.
	 *
	 * @return content root, representing the office:chart tag
	 * @throws Exception if the file DOM could not be created.
	 */
	public OdfOfficeChart getContentRoot() throws Exception {
		return super.getContentRoot(OdfOfficeChart.class);
	}

	/**
	 * Switches this instance to the given type. This method can be used to e.g. convert
	 * a document instance to a template and vice versa. 
	 * @param type
	 */
	public void changeMode(SupportedType type) {
		setMediaType(type.getOdfMediaType());
		getPackage().setMediaType(type.toString());
	}
}
