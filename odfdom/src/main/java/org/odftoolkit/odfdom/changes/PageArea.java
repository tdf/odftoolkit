/** ***********************************************************
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 ************************************************************ */
package org.odftoolkit.odfdom.changes;

public enum PageArea {
    /**
     * The parser is always in one of the three areas header, footer and body
     */
    HEADER_DEFAULT("header_default", "header"),
    HEADER_FIRST("header_first", "header-first"),
    HEADER_EVEN("header_even", "header-left"),
    BODY("body", null),
    FOOTER_DEFAULT("footer_default", "footer"),
    FOOTER_FIRST("footer_first", "footer-first"),
    FOOTER_EVEN("footer_even", "footer-left");

    private String areaName;
    private String localName;

    private PageArea(String areaName, String localName) {
        this.areaName = areaName;
        this.localName = localName;
    }

    public String getPageAreaName() {
        return areaName;
    }

    /**
     * @return the local name of the XML element
     */
    public String getLocalName() {
        return localName;
    }
}
