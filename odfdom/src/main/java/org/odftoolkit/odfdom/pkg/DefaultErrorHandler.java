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
package org.odftoolkit.odfdom.pkg;

import java.util.logging.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/** Default implementation of the SAX <code>ErrorHandler</code> interface. */
class DefaultErrorHandler implements ErrorHandler {
	private static final Logger LOG = Logger.getLogger(DefaultErrorHandler.class.getName());

	/** Triggers an warning. In case an optional ODF conformance was not satisfied.
	 *  Default handling is to write into Java log using warning level */
	public void warning(SAXParseException exception) throws SAXException {		
		LOG.warning(exception.getLocalizedMessage());
	}

	/** Triggers an error. In case a mandatory ODF conformance was not satisfied.
	*  Default handling is to write into Java log using severe level */
	public void error(SAXParseException exception) throws SAXException {		
		LOG.severe(exception.getLocalizedMessage());
	}
	
	/** Triggers a fatalError. The ODF document can not be loaded due to an error.	 *
	 *   Default handling is to write into Java log using severe level and to throw a SAXException */
	public void fatalError(SAXParseException exception) throws SAXException {		
		LOG.severe(exception.getLocalizedMessage());
		throw exception;
	}	
}
