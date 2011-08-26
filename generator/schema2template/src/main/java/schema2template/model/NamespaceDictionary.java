/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
package schema2template.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Translation NS URI &lt;-&gt; NS Localname
 */
public class NamespaceDictionary {

    private Map<String,String> uri2local;
    private Map<String,String> local2uri;

    /**
     * Construct a new empty dictionary
     */
    public NamespaceDictionary() {
        uri2local = new HashMap<String, String>();
        local2uri = new HashMap<String, String>();
    }

    /**
     * Register a new translation
     *
     * @param nsLocal short namespace
     * @param nsUri namespace URI
     */
    public void put(String nsLocal, String nsUri) {
        uri2local.put(nsUri, nsLocal);
        local2uri.put(nsLocal, nsUri);
    }

    /**
     * Translate local namespace to URI
     *
     * @param nsLocal short namespace
     * @return namespace URI
     */
    public String getNamespaceURI(String nsLocal) {
        return local2uri.get(nsLocal);
    }

    /**
     * Translate URI to local namespace
     *
     * @param nsUri namespace URI
     * @return short namespace
     */
    public String getLocalNamespace(String nsUri) {
        return uri2local.get(nsUri);
    }

    /**
     * Construct an example dictionary as needed by the OpenDocument schema file
     *
     * @return namespace Dictionary
     */
    public static NamespaceDictionary getStandardDictionary() {
        NamespaceDictionary dict = new NamespaceDictionary();
//todo Exchange the static list with a dynamic approach!!
	dict.put("office", "urn:oasis:names:tc:opendocument:xmlns:office:1.0");
	dict.put("style", "urn:oasis:names:tc:opendocument:xmlns:style:1.0");
	dict.put("text", "urn:oasis:names:tc:opendocument:xmlns:text:1.0");
	dict.put("table", "urn:oasis:names:tc:opendocument:xmlns:table:1.0");
	dict.put("draw", "urn:oasis:names:tc:opendocument:xmlns:drawing:1.0");
	dict.put("fo", "urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0");
	dict.put("xlink", "http://www.w3.org/1999/xlink");
	dict.put("dc", "http://purl.org/dc/elements/1.1/");
	dict.put("meta", "urn:oasis:names:tc:opendocument:xmlns:meta:1.0");
	dict.put("number", "urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0");
	dict.put("svg", "urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0");
	dict.put("chart", "urn:oasis:names:tc:opendocument:xmlns:chart:1.0");
	dict.put("dr3d", "urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0");
	dict.put("math", "http://www.w3.org/1998/Math/MathML");
	dict.put("form", "urn:oasis:names:tc:opendocument:xmlns:form:1.0");
	dict.put("script", "urn:oasis:names:tc:opendocument:xmlns:script:1.0");
	dict.put("dom", "http://www.w3.org/2001/xml-events");
	dict.put("xforms", "http://www.w3.org/2002/xforms");
	dict.put("xsd", "http://www.w3.org/2001/XMLSchema");
	dict.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
	dict.put("of", "urn:oasis:names:tc:opendocument:xmlns:of:1.2");
	dict.put("rdfa", "http://docs.oasis-open.org/opendocument/meta/rdfa#");
	dict.put("field", "urn:openoffice:names:experimental:ooo-ms-interop:xmlns:field:1.0");
	dict.put("config","urn:oasis:names:tc:opendocument:xmlns:config:1.0");
	dict.put("presentation","urn:oasis:names:tc:opendocument:xmlns:presentation:1.0");
	dict.put("db","urn:oasis:names:tc:opendocument:xmlns:database:1.0");
	dict.put("anim","urn:oasis:names:tc:opendocument:xmlns:animation:1.0");
	dict.put("grddl","http://www.w3.org/2003/g/data-view#");
	dict.put("xhtml","http://www.w3.org/1999/xhtml");
	dict.put("smil","urn:oasis:names:tc:opendocument:xmlns:smil-compatible:1.0");
        dict.put("xml", "http://www.w3.org/XML/1998/namespace");

        return dict;
    }

}
