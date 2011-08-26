/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
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
 */
package org.odftoolkit.odfdom.taglet;

import java.util.Map;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import java.util.logging.Logger;

/**
 *	This class implements a custom taglet to the map the ODF datatype to the
 *  declaration of the ODF datatype in the OpenDocument specification.
 *
 *  The position of the OpenDocument specification in HTML can be provided using
 *  an environment variable or java system property, while the system property overrides
 *  the environment variable.
 *  In case nothing is been a default path within the JavaDoc doc-files directory is being used.
 *
 *  For example the taglet <code>{@odf.datatype countryCode}</code> would be resolved without variable settings to
 *  <code>JAVA_DOC_BASE/doc-files/OpenDocument-v1.2-draft.xhtml#datatype-countryCode</code>.
 */
public class OdfDatatypeTaglet implements Taglet {

	private static final Logger LOG = Logger.getLogger(OdfDatatypeTaglet.class.getName());
	private static final String NAME = "odf.datatype";
	private static final String ODF_SPEC_PATH = "../../../../../../doc-files/OpenDocument-v1.2-draft.xhtml";
	private static String mOdfSpecPath = null;

    /* FINDING THE ABSOLUTE PATH TO THE ODF SPEC IN HTML:
     * 1) Try to get the odfSpecPath from the Java System variable (ODF_SPEC_PATH)
     * 2) Try to get the odfSpecPath from the environemnt variable (ODF_SPEC_PATH)
     * 3) If both not worked, use the default path
     **/
    static {
        mOdfSpecPath = System.getProperty("ODF_SPEC_PATH");
        if (mOdfSpecPath == null) {
            mOdfSpecPath = System.getenv("ODF_SPEC_PATH");
            if (mOdfSpecPath == null) {
                mOdfSpecPath = ODF_SPEC_PATH;
                LOG.info("OdfSpecPath was set to " + mOdfSpecPath + " by class declaration.");
            } else {
                LOG.info("OdfSpecPath was set to " + mOdfSpecPath + " by environment property 'ODF_SPEC_PATH'.");
            }
        } else {
            LOG.info("OdfSpecPath was set to " + mOdfSpecPath + " by Java System property 'ODF_SPEC_PATH'.");
        }
    }

	/**
	 * @return the name of this custom tag.
	 */
	public String getName() {
		return NAME;
	}

	/**
	 * @return true since this tag can be used in a field
	 *         doc comment
	 */
	public boolean inField() {
		return true;
	}

	/**
	 * @return true since this tag can be used in a constructor
	 *         doc comment
	 */
	public boolean inConstructor() {
		return true;
	}

	/**
	 * @return true since this tag can be used in a method
	 *         doc comment
	 */
	public boolean inMethod() {
		return true;
	}

	/**
	 * @return true since this tag can be used in an overview
	 *         doc comment
	 */
	public boolean inOverview() {
		return true;
	}

	/**
	 * @return true since this tag can be used in a package
	 *         doc comment
	 */
	public boolean inPackage() {
		return true;
	}

	/**
	 * @return true since this
	 */
	public boolean inDatatype() {
		return true;
	}

	/**
	 * Will return true since this is an inline tag.
	 * @return true since this is an inline tag.
	 */
	public boolean isInlineTag() {
		return true;
	}

	/**
	 * Register this Taglet.
	 * @param tagletMap  the map to register this tag to.
	 */
	public static void register(Map<String, Taglet> tagletMap) {
		OdfDatatypeTaglet tag = new OdfDatatypeTaglet();
		Taglet t = tagletMap.get(tag.getName());
		if (t != null) {
			tagletMap.remove(tag.getName());
		}
		tagletMap.put(tag.getName(), tag);
	}

	/**
	 * Given the <code>Tag</code> representation of this custom
	 * tag, return its string representation.
	 * @param tag he <code>Tag</code> representation of this custom tag.
	 * @return the string representation of the custom tag
	 */
	public String toString(Tag tag) {
		int pos = tag.text().lastIndexOf(":");
		String fragmentIdentifier = "datatype-" + tag.text().substring(0, pos) + "_" + tag.text().substring(pos + 1);
		return "<a href=\"" + mOdfSpecPath + "#" + fragmentIdentifier + "\">" + tag.text() + "</a>";
	}

	/**
	 * This method should not be called since arrays of inline tags do not
	 * exist.  Method should be used to convert this
	 * inline tag to a string.
	 * @param tags the array of <code>Tag</code>s representing of this custom tag.
	 * @return Nothing is returned, instead an <code>UnsupportedOperationException</code> is being thrown.

	 */
	public String toString(Tag[] tags) {
		throw new java.lang.UnsupportedOperationException("Arrays of inline tags do not exist!");
	}

	public boolean inType() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
