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

/**
 * Unambiguously named (ns:localname) object.
 *
 * <p>Contract: Every object implementing hasQName should overwrite the toString() method and
 * return the QName.</p>
 * <p>Warning: Using this interface does not imply any information about
 * the equals() or hashCode() methods. So for using objects with qualified names in a
 * Collection, you need information from the implementing class.</p>
 */
public interface QNamed {

    /**
     * Get the QName (i.e. namespace:localname )
     *
     * @return full name
     */
    public String getQName();

    /**
     * Get only namespace
     *
     * @return namespace
     */
    public String getNamespace();

    /**
     * Get only localname
     *
     * @return localname
     */
    public String getLocalName();

}
