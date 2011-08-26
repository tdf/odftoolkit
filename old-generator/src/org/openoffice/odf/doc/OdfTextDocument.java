/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
package org.openoffice.odf.doc;


/**
 * This class represents an empty ODF text document file.
 * Note: The way of receiving a new empty OdfTextDocument will probably change. 
 * In the future the streams and DOM representation of an OpenDocument file will
 * be clonable and this stream buffering will be neglected.
 * 
 */
public class OdfTextDocument extends OdfDocument {

    private static String EMPTY_TEXT_DOCUMENT_PATH = "/resources/OdfTextDocument.odt";
    private static Resource EMPTY_TEXT_DOCUMENT_RESOURCE = new Resource(EMPTY_TEXT_DOCUMENT_PATH);
    
    /**
     * Creates an empty text document.
     * @return ODF text document based on a default template
     * @throws java.lang.Exception - if the document could not be created
     */
    public static OdfTextDocument createTextDocument() throws Exception {
        return (OdfTextDocument) OdfDocument.loadTemplate(EMPTY_TEXT_DOCUMENT_RESOURCE);
    }

    // Using static factory instead of constructor
    protected OdfTextDocument() {};
    
    /**
     * Get the media type
     * 
     * @return the mediaTYPE string of this package
     */
    @Override
    public String getMediaType() {
        return OdfDocument.OdfMediaType.TEXT.toString();
    }
    private static final String TO_STRING_METHOD_TOKEN = "\n" + OdfDocument.OdfMediaType.TEXT + " - ID: ";

    @Override
    public String toString() {
        return TO_STRING_METHOD_TOKEN + this.hashCode() + " " + getPackage().getBaseURI();
    }
}
