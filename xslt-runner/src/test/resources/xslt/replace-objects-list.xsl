<?xml version="1.0" encoding="UTF-8" ?>
<!--

  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER

  Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.

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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
                xmlns:draw="urn:oasis:names:tc:opendocument:xmlns:drawing:1.0"
                exclude-result-prefixes="xsl"
                version="1.0">
    <xsl:output method="text"/>

    <xsl:param name="ref-html"/>

    <xsl:template match="office:document-content">
        <xsl:apply-templates select="//draw:frame[draw:object]"/>
    </xsl:template>

    <xsl:template match="draw:frame[draw:object]">
        <xsl:value-of select="document($ref-html)//img[@name=current()/@draw:name]/@src"/>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <!-- default: ignore everything. -->
    <xsl:template match="@*|node()"/>
</xsl:stylesheet>
