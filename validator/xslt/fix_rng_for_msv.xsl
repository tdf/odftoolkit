<?xml version="1.0" encoding="UTF-8" ?>
<!--

  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER

  Copyright 2008 Sun Microsystems, Inc. All rights reserved.

  Use is subject to license terms.

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
  obtain a copy of the License at http://odftoolkit.org/docs/license.txt

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

  See the License for the specific language governing permissions and
  limitations under the License.

-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:rng="http://relaxng.org/ns/structure/1.0"
    exclude-result-prefixes="rng"
    >
    <xsl:output method="xml"/>

    <!-- default: copy everything. -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
   
    <xsl:template match="rng:element/rng:ref[@name='string']">
        <text/>
    </xsl:template>
    
    <xsl:template match="rng:element/rng:choice/rng:group/rng:ref[@name='string']">
        <text/>
    </xsl:template>
 
</xsl:stylesheet>
