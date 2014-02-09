/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.ed.
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
 * The NamespaceName interface is being used to provide an easy way to define Namespaces
 * in a higher layer (e.g. via enum as OdfDocumentNamespace) and access them from a lower layer e.g. constructors of org.odftoolkit.odfdom.pkg.OdfNamespace.
 */
public interface NamespaceName {

	/**
	 * @return the prefix currently related to XML Namespace.
	 * Note: Even in a single XML file, a user might assign different prefixes to a XML Namespace, different NamespaceNames might exist.
	 */
	public String getPrefix();

	/**
	 * @return the URI identifiying the XML Namespace.
	 */
	public String getUri();

}
