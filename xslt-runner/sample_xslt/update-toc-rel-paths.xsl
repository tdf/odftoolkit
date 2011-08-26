<?xml version="1.0" encoding="UTF-8" ?>
<!--

  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER

  Copyright 2011 Oracle and/or its affiliates. All rights reserved.

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
                xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                exclude-result-prefixes="xsl"
                version="1.0">
    <xsl:output method="xml" indent="no"/>

    <xsl:param name="state-old" select="'(none)'"/>
    <xsl:param name="state-new" select="'(none)'"/>

    <xsl:variable name="toc-prefix" select="'toc-'"/>



    <!-- ************************ -->
    <!-- ** toc anchors (toc-) ** -->
    <!-- ************************ -->
    <xsl:template match="text:section[starts-with(@text:name,$toc-prefix)]//text:a[starts-with(@xlink:href,'../')]">
            <xsl:variable name="new-href">
                <xsl:call-template name="remove-rel-path">
                    <xsl:with-param name="path" select="@xlink:href"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:message>href: <xsl:value-of select="@xlink:href"/> = <xsl:value-of select="$new-href"/></xsl:message>
        <text:a>
            <xsl:attribute name="xlink:href"><xsl:value-of select="$new-href"/></xsl:attribute>
            <xsl:apply-templates select="@*[not(name(.)='xlink:href')]"/>
            <xsl:apply-templates select="node()"/>
        </text:a>
    </xsl:template>

    <xsl:template name="remove-rel-path">
        <xsl:param name="path"/>
        <xsl:choose>
            <xsl:when test="contains($path,'/')">
                <xsl:call-template name="remove-rel-path">
                    <xsl:with-param name="path" select="substring-after($path,'/')"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="contains($path,$state-old)">
                        <xsl:value-of select="concat('../',substring-before($path,$state-old),$state-new,substring-after($path,$state-old))"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="concat('../',$path)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- default: copy everything. -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
