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
package org.odftoolkit.odfdom.pkg.rdfa;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

public class Util {

	private static final String SLASH = "/";
    private static final String EMPTY_BASE_URI = "";
	/**
	 * Test whether two QNames are equal to each other. This is a bug fix for
	 * java-rdfa library. "xhtml:about" and "about" should be considered as
	 * RDFa, while java-rdfa recognizes only the later one.
	 *
	 * @param at
	 *            , the QName of an Attribute
	 * @param name
	 *            , the QName to be compared
	 * @return
	 */
	public static boolean qNameEquals(QName at, QName name) {
		if (!name.getNamespaceURI().equals(XMLConstants.DEFAULT_NS_PREFIX)) {
			if (at.equals(name)) {
				return true;
			}
		} else if (at.getLocalPart().equals(name.getLocalPart())) {
			return true;
		}
		return false;
	}

	/**
	 * Get the RDF base uri of the given internalPath. Note that there would be
	 * a SLASH at the end of the RDF base uri
	 *
	 * @param pkgBaseUri
	 *            , the base uri of the package
	 * @param internalPath
	 *            , the internalPath relative to the root document
	 * @return
	 */
	public static String getRDFBaseUri(String pkgBaseUri, String internalPath) {
		String baseUri = null;
        String subDirectory = internalPath.lastIndexOf(SLASH) == -1 ? "" : "" + SLASH + internalPath.substring(0, internalPath.lastIndexOf(SLASH));
		if(pkgBaseUri != null) {
            baseUri = pkgBaseUri + subDirectory + SLASH;
        } else {
            baseUri = subDirectory + SLASH;
        }
		return baseUri;
	}

	/**
	 * To test whether the subPath is a sut path of superPath
	 *
	 * @param subPath
	 *            , an internal path in the ODF package
	 * @param superPath
	 *            , an internal path in the ODF package
	 * @return
	 */
	public static boolean isSubPathOf(String subPath, String superPath) {
		if (superPath == null || subPath == null) {
			return false;
		}
		return SLASH.equals(superPath) || (subPath.length() > superPath.length() && subPath.startsWith(superPath));
	}

	/**
	 * To fix the 3 slashes bug for File URI: For example:
	 * file:/C:/work/test.txt -> file:///C:/work/test.txt
	 *
	 * @param u - the File URI
	 * @return the String of the URI
	 */
	public static String toExternalForm(URI u)  {
		StringBuilder sb = new StringBuilder();
		if (u.getScheme() != null) {
			sb.append(u.getScheme());
			sb.append(':');
		}
		if (u.isOpaque()) {
			sb.append(u.getSchemeSpecificPart());
		} else {
			if (u.getHost() != null) {
				sb.append("//");
				if (u.getUserInfo() != null) {
					sb.append(u.getUserInfo());
					sb.append('@');
				}
				boolean needBrackets = ((u.getHost().indexOf(':') >= 0) && !u.getHost().startsWith("[") && !u.getHost().endsWith("]"));
				if (needBrackets)
					sb.append('[');
				sb.append(u.getHost());
				if (needBrackets)
					sb.append(']');
				if (u.getPort() != -1) {
					sb.append(':');
					sb.append(u.getPort());
				}
			} else if (u.getRawAuthority() != null) {
				sb.append("//");
				sb.append(u.getRawAuthority());
			} else {
				sb.append("//");
			}
			if (u.getRawPath() != null)
				sb.append(u.getRawPath());
			if (u.getRawQuery() != null) {
				sb.append('?');
				sb.append(u.getRawQuery());
			}
		}
		if (u.getFragment() != null) {
			sb.append('#');
			sb.append(u.getFragment());
		}
		String ret = null;
        try {
            ret = new URI(sb.toString()).toASCIIString();
        } catch (URISyntaxException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
		return ret;
	}
}
