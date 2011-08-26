/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2009, 2010 Oracle and/or its affiliates. All rights reserved.
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

/**
 * <p>Domain specific models (Odf, Java, ...) often return 'ns:localname' Strings.
 * The purpose of this class is to encapsulate these Strings so in templates they
 * can be treated like other QNamed objects.</p>
 * <p>Contract: These objects are distincted only by the encapsulated String. So
 * they use this String for equals(o), hashCode() and compareTo(o).</p>
 */
public class QNameValue implements QNamed, Comparable<QNameValue> {

    private String mName;

    /**
     * Constructor to create a String wrapper
     *
     * @param name the String to wrap
     */
    public QNameValue(String name) {
        mName = name;
    }

    public boolean equals(Object o) {
        return (o instanceof QNameValue) ? ((QNameValue) o).mName.equals(mName) : false;
    }

    public int hashCode() {
        return mName.hashCode();
    }

    public String getLocalName() {
        return XMLModel.extractLocalname(mName);
    }

    public String getQName() {
        return mName;
    }

    public String getNamespace() {
        return XMLModel.extractNamespace(mName);
    }

    public int compareTo(QNameValue o) {
        return this.mName.compareTo(o.mName);
    }

}
