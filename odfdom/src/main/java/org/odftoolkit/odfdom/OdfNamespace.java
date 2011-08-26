/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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

package org.odftoolkit.odfdom;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * Class wrapping the XML Namespace URI and XML Namespace prefix as a single entity.
 */
public class OdfNamespace implements Comparable, NamespaceContext {

	private static Map<String, String> mURIsByPrefix = new HashMap<String, String>();
	private static Map<String, String> mPrefixesByURI = new HashMap<String, String>();
	private static Map<String, OdfNamespace> mNamespacesByURI = new HashMap<String, OdfNamespace>();
	private String mUri;
	private String mPrefix;

	private OdfNamespace() {}
	private OdfNamespace(String prefix, String uri) {
			mUri = uri;
			mPrefix = prefix;
	}

	/** Returns the OdfNamespace for the given name.
	 *  Creates a new one, if the name was not asked before.
	 * @param name of the namespace.
	 * @return the OdfNamespace for the given name.
	 */
	public static OdfNamespace newNamespace(NamespaceName name) {
		OdfNamespace ns = null;
		if (name != null) {
			ns = newNamespace(name.getPrefix(), name.getUri());
		}
		return ns;
	}

	/** Returns the OdfNamespace for the given name.
	 *  Creates a new one, if the name was not asked before.
	 * @param uri identifying the namespace.
	 * @return the namespace.
	 */
	public static OdfNamespace newNamespace(String prefix, String uri) {
		OdfNamespace odfNamespace = null;
		if (uri != null && uri.length() > 0
				&& prefix != null && prefix.length() > 0) {
			odfNamespace = mNamespacesByURI.get(uri);
			if (odfNamespace == null) {
				odfNamespace = new OdfNamespace(prefix, uri);
				mPrefixesByURI.put(uri, prefix);
				mURIsByPrefix.put(prefix, uri);
				mNamespacesByURI.put(uri, odfNamespace);
			}
		}
		return odfNamespace;
	}

	/** Returns the namespace for the given uri.
	 * @param uri identifying the namespace.
	 * @return the namespace identified by the given uri.
	 */
	public static OdfNamespace getNamespace(String uri) {
		OdfNamespace ns = null;
		if (uri != null) {
			ns = mNamespacesByURI.get(uri);
		}
		return ns;
	}

	// 2do: after removing NamespaceContext rename to getNamespacePrefix()
	public String getPrefix() {
		return mPrefix;
	}

	// 2do: after removing NamespaceContext rename to getNamespaceURI()
	public String getUri() {
		return mUri;
	}

	@Override
	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	public boolean equals(Object obj) {
		if (mUri != null) {
			return mUri.equals(obj.toString());
		} else {
			return mUri == obj;
		}
	}

	@Override
	public int hashCode() {
		if (mUri != null) {
			return mUri.hashCode();
		} else {
			return 0;
		}
	}

	@Override
	public String toString() {
		return mUri;
	}

	/** Splits the XML Qname into the local name and the prefix.
	 *
	 * @param qname is the qualified name to be splitted.
	 * @return an array of two strings containing first the prefix and the second the local part.
	 * @throws IllegalArgumentException if no qualified name was given.
	 */
	public static String[] splitQName(String qname) throws IllegalArgumentException{
		String localpart = qname;
		String prefix = null;
		int colon = qname.indexOf(':');
		if (colon > 0) {
			localpart = qname.substring(colon + 1);
			prefix = qname.substring(0, colon);
		} else {
			throw new IllegalArgumentException("A qualified name was required, but '" + qname +"' was given!");
		}
		return new String[]{prefix, localpart};
	}


	/** 
	 * @param qname is the qualified name to be splitted.
	 * @return the local name of the XML Qname.
	 * @throws IllegalArgumentException if no qualified name was given.
	 */
	public static String getPrefixPart(String qname) {
		return splitQName(qname)[0];
	}

	/**
	 * @param qname is the qualified name to be splitted.
	 * @return the prefix of the XML Qname.
	 * @throws IllegalArgumentException if no qualified name was given.
	 */
	public static String getLocalPart(String qname) {
		return splitQName(qname)[1];
	}

	public int compareTo(Object o) {
		return toString().compareTo(o.toString());
	}


	// 2DO: The NamespaceContext interface have to be moved to the 
	// OdfElement/OdfAttribute as in case there are multiple namespaces declared
	// The DOM Tree have to be reviewed from the beginning of the node up to the parents..
	/** See JavaDoc of <code>NamespaceContext</code> interface */
	public String getNamespaceURI(String prefix) {
		String nsURI = null;
		nsURI = mURIsByPrefix.get(prefix);
		if (nsURI == null) {
			nsURI = XMLConstants.NULL_NS_URI;
		}
		return nsURI;
	}

	/** See JavaDoc of <code>NamespaceContext</code> interface */
	public String getPrefix(String namespaceUri) {
		return mPrefixesByURI.get(namespaceUri);
	}

	// 2DO: Will be adapted when the NamespaceContext is being removed
	/** See JavaDoc of <code>NamespaceContext</code> interface */
	public Iterator getPrefixes(String namespaceuri) {
		return mURIsByPrefix.keySet().iterator();
	}

	/**
	 * @param prefix of the namespace questioned.
	 * @return the namespace URI of the namespace prefix given.
	 */
	public static String getNamespaceURIByPrefix(String prefix) {
		return mURIsByPrefix.get(prefix);
	}
}
