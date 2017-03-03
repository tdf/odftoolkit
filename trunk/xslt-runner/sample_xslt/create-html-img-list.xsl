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

<xsl:stylesheet version="1.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:x="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="xsl">
  <xsl:output method="text" encoding="UTF-8"/>

  <xsl:template match="xhtml:html">
      <xsl:apply-templates select="//xhtml:img"/>
  </xsl:template>

<!--  <xsl:template match="img[starts-with(@name,'Object')]">-->
  <xsl:template match="xhtml:img">
      <xsl:choose>
          <xsl:when test="starts-with(@src,'./')">
              <xsl:value-of select="substring(@src,3)"/>
          </xsl:when>
          <xsl:otherwise>
              <xsl:value-of select="@src"/>
          </xsl:otherwise>
      </xsl:choose>
      <xsl:text>
</xsl:text>
  </xsl:template>

  <xsl:template match="node()">
      <xsl:apply-templates select="node()"/>
  </xsl:template>
</xsl:stylesheet>
