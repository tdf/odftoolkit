/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
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

/**
 * Namespaces of OpenDocument 1.2 XML Package Schema
 */
public enum OdfPackageNamespace implements NamespaceName {


	MANIFEST("manifest", "urn:oasis:names:tc:opendocument:xmlns:manifest:1.0"),
	XML("xml", "http://www.w3.org/XML/1998/namespace"),
    DSIG("dsig", "urn:oasis:names:tc:opendocument:xmlns:digitalsignature:1.0"),
	DS("ds", "http://www.w3.org/2000/09/xmldsig#");

	private String mPrefix;
	private String mUri;

	OdfPackageNamespace(String prefix, String uri) {
		mPrefix = prefix;
		mUri = uri;
	}

	/**
	 * @return the prefix currently related to ODF Namespace.
	 */
	public String getPrefix() {
		return mPrefix;
	}

	/**
	 * @return the URI identifying the ODF Namespace.
	 */
	public String getUri() {
		return mUri;
	}
}
