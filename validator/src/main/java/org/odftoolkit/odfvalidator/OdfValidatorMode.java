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

package org.odftoolkit.odfvalidator;

public enum OdfValidatorMode {
    /***
     * all versions
     */
    CONFORMANCE ("conformant"), //

    /***
     * ODF 1.0 and 1.1 only, equals CONFORMANCE for ODF 1.2
     */
    VALIDATE ("valid"),

    /***
     * ODF 1.0 and 1.1 only, equals CONFORMANCE for ODF 1.2
     */
    VALIDATE_STRICT ("strict valid"),

    /***
     * ODF 1.2 and 1.3 only, equals CONFORMANCE for ODF 1.0/1.1
     */
    EXTENDED_CONFORMANCE ("extended conformant");

    private String mValue;

    OdfValidatorMode( String value )
    {
        mValue = value;
    }

    @Override
    public String toString()
    {
        return mValue;
    }
}
