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
import org.odftoolkit.odfdom.pkg.MediaType;
import org.odftoolkit.odfdom.pkg.OdfPackage;

/**
 * This class represents an empty ODF document , which will be in general embedded
 * in an existing ODF (Spreadsheet) document.
 */
public class OdfChartDocument extends OdfDocument {

	private static final String EMPTY_CHART_DOCUMENT_PATH = "/OdfChartDocument.odc";
	static final Resource EMPTY_CHART_DOCUMENT_RESOURCE = new Resource(EMPTY_CHART_DOCUMENT_PATH);

	/**
	 * This enum contains all possible media types of OdfChartDocument documents.
	 */
	public enum OdfMediaType implements MediaType {

		CHART(OdfDocument.OdfMediaType.CHART),
		CHART_TEMPLATE(OdfDocument.OdfMediaType.CHART_TEMPLATE);
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
	 * Creates an empty charts document.
	 * *  <br/><em>Note: ODF Chart documents are (with OOo 3.0) only used as embedded document and not used stand-alone.</em>
	 * @return ODF charts document based on a default template
	 * @throws java.lang.Exception - if the document could not be created
	 */
	public static OdfChartDocument newChartDocument() throws Exception {
		return (OdfChartDocument) OdfDocument.loadTemplate(EMPTY_CHART_DOCUMENT_RESOURCE, OdfDocument.OdfMediaType.CHART);
	}

	/**
	 * Creates an empty charts template.
	 * *  <br/><em>Note: ODF Chart documents are (with OOo 3.0) only used as embedded document and not used stand-alone.</em>
	 * @return ODF charts template based on a default template
	 * @throws java.lang.Exception - if the template could not be created
	 */
	public static OdfChartDocument newChartTemplateDocument() throws Exception {
		OdfChartDocument doc = (OdfChartDocument) OdfDocument.loadTemplate(EMPTY_CHART_DOCUMENT_RESOURCE, OdfDocument.OdfMediaType.CHART_TEMPLATE);
		doc.changeMode(OdfMediaType.CHART_TEMPLATE);
		return doc;
	}

	// Using static factory instead of constructor
	protected OdfChartDocument(OdfPackage pkg, String internalPath, OdfChartDocument.OdfMediaType odfMediaType) {
		super(pkg, internalPath, odfMediaType.mMediaType);
	}

	/**
	 * Get the content root of a chart document.
	 *
	 * @return content root, representing the office:chart tag
	 * @throws Exception if the file DOM could not be created.
	 */
	@Override
	public OdfOfficeChart getContentRoot() throws Exception {
		return super.getContentRoot(OdfOfficeChart.class);
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
