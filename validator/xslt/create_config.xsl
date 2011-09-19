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

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" doctype-system="http://java.sun.com/dtd/properties.dtd"/>

  <xsl:param name="manifest-schema"/>
  <xsl:param name="strict-schema"/>
  <xsl:param name="schema"/>
  <xsl:param name="dsig-schema"/>
  <xsl:param name="mathml-schema"/>
  <xsl:param name="mathml2-schema"/>
  <xsl:param name="path"/>
  <xsl:param name="filter"/>

  
  <!-- default: copy everything. -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="entry[@key='manifest-schema']"><entry key="manifest-schema"><xsl:value-of select="$manifest-schema"/></entry></xsl:template>
  <xsl:template match="entry[@key='strict-schema']"><entry key="strict-schema"><xsl:value-of select="$strict-schema"/></entry></xsl:template>
  <xsl:template match="entry[@key='schema']"><entry key="schema"><xsl:value-of select="$schema"/></entry></xsl:template>
  <xsl:template match="entry[@key='mathml-schema']"><entry key="mathml-schema"><xsl:value-of select="$mathml-schema"/></entry></xsl:template>
  <xsl:template match="entry[@key='mathml2-schema']"><entry key="mathml2-schema"><xsl:value-of select="$mathml2-schema"/></entry></xsl:template>
  <xsl:template match="entry[@key='dsig-schema']"><entry key="dsig-schema"><xsl:value-of select="$dsig-schema"/></entry></xsl:template>
  <xsl:template match="entry[@key='filter']"><entry key="filter"><xsl:value-of select="$filter"/></entry></xsl:template>
  <xsl:template match="entry[@key='path1']"><entry key="path1"><xsl:value-of select="$path"/></entry></xsl:template>
</xsl:stylesheet>
