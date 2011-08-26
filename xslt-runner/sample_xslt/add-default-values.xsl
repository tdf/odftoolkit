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

<!-- This stylesheet inserts default values into the ODF v1.2 -->
<!-- specification. The default values are taken from a flat -->
<!-- ODF schema file whose location must be provided by the -->
<!-- "xref-schema-file" parameter -->


<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                 
                xmlns:rng="http://relaxng.org/ns/structure/1.0"                
                xmlns:a="http://relaxng.org/ns/compatibility/annotations/1.0"                
                xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
                exclude-result-prefixes="rng xsl a"
                version="1.0">    
    <xsl:output method="xml" indent="no"/>    
    
        <!-- The flat schema file -->
    <xsl:param name="xref-schema-file"/>
    
    <xsl:variable name="add-default-values" select="true()"/>
    <xsl:variable name="keep-xref-anchors" select="true()"/>
    
    <xsl:variable name="attribute-prefix" select="'attribute-'"/>
    <xsl:variable name="element-prefix" select="'element-'"/>
 
    <!-- ********** -->
    <!-- ** root ** -->
    <!-- ********** -->
    <xsl:template match="text:p[starts-with(.,$attribute-prefix)]">        
        <xsl:variable name="attr-name" select="normalize-space(substring(., string-length($attribute-prefix)+1))"/>
        <xsl:if test="not(document($xref-schema-file)/rng:grammar/rng:element/rng:attribute[@name=$attr-name])">
            <xsl:message>No attribute definition found in schema for xref &quot;<xsl:value-of select="."/>&quot;</xsl:message>
        </xsl:if>
        <xsl:if test="$add-default-values">
            <xsl:apply-templates select="document($xref-schema-file)/rng:grammar//rng:attribute[@name=$attr-name and @a:defaultValue]"/>
        </xsl:if>
        <xsl:if test="$keep-xref-anchors">
            <xsl:copy>
                <xsl:apply-templates select="@*|node()"/>
            </xsl:copy>
        </xsl:if>
    </xsl:template>    
        
    <!-- default: copy everything. -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="rng:attribute">
        <xsl:variable name="name" select="@name"/>
        <xsl:variable name="defaultValue" select="@a:defaultValue"/>
        <xsl:if test="not(preceding::rng:attribute[@name=$name and @a:defaultValue=$defaultValue])">
            <xsl:variable name="hasAlsoNoDefault" select="count(ancestor::rng:grammar/rng:element[rng:attribute[@name=$name] and not(rng:attribute[@name=$name and @a:defaultValue])]) > 0"/>
            <xsl:variable name="hasAlsoOtherDefault" select="count(ancestor::rng:grammar//rng:attribute[@name=$name and @a:defaultValue!=$defaultValue]) > 0"/>
            <xsl:if test="$hasAlsoNoDefault">
                <xsl:message>Some <xsl:value-of select="$name"/> attributes do not have a default value.</xsl:message>
            </xsl:if>
            <xsl:if test="$hasAlsoOtherDefault">
                <xsl:message>Some <xsl:value-of select="$name"/> attributes do have a different default value than &quot;<xsl:value-of select="$defaultValue"/>&quot;.</xsl:message>
            </xsl:if>
            <xsl:call-template name="add-default-values">
                <xsl:with-param name="listElements" select="$hasAlsoNoDefault or $hasAlsoOtherDefault"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>   

    
    <!-- select all <element> nodes in the file or in included files -->
    <xsl:template name="add-default-values">
        <xsl:param name="listElements" select="false()"/>
        <text:p text:style-name="Default_20_Value">
            <xsl:choose>
                <xsl:when test="$listElements">
                    <xsl:text>For </xsl:text>
                    <xsl:call-template name="create-element-list"/>
                    <xsl:text> the</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>The</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:text> default value for this attribute is </xsl:text>
            <text:span text:style-name="Attribute_20_Value"><xsl:value-of select="@a:defaultValue"/></text:span>
            <xsl:text>.</xsl:text>
        </text:p>
        <xsl:text>
   </xsl:text>
    </xsl:template>   

    <xsl:template name="create-element-list">
        <xsl:param name="attr-name" select="@name"/>
        <xsl:param name="defaultValue" select="@a:defaultValue"/>
        <!-- collect elements -->
        <xsl:variable name="elements" select="ancestor::rng:grammar/rng:element[rng:attribute[@name=$attr-name and @a:defaultValue=$defaultValue]]"/>
        <xsl:variable name="count" select="count($elements)"/>
        <xsl:if test="$count = 1">
            <xsl:text>a </xsl:text>
        </xsl:if>
        <xsl:for-each select="$elements">
            <xsl:sort select="@name"/>
            <xsl:variable name="name" select="@name"/>
            <xsl:choose>
                <xsl:when test="position() = 1"/>
                <xsl:when test="position() = last()">
                     <xsl:text> and </xsl:text>
                </xsl:when>
                <xsl:otherwise>
                     <xsl:text>, </xsl:text>
                </xsl:otherwise>
            </xsl:choose>
            <text:span text:style-name="Element"><xsl:text>&lt;</xsl:text><xsl:value-of select="@name"/><xsl:text>&gt;</xsl:text></text:span><xsl:text> </xsl:text><text:reference-ref text:ref-name="{concat($element-prefix,$name)}" text:reference-format="chapter">?</text:reference-ref>
        </xsl:for-each>
        <xsl:choose>
            <xsl:when test="$count = 1">
                <xsl:text> element</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text> elements</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
