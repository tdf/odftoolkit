<?xml version="1.0" encoding="utf-8"?>
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

<!-- This stylesheet extracts the ODF schemas from the ODF v1.0 and v1.1 -->
<!-- specification.                                                      -->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0" 
                xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
                xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0">
    
    <xsl:output method="text"/>
    
    <!-- Supported mode parameter values are: -->
    <!-- 'schema': Extracts the schema -->
    <!-- 'strict-schema': Extracts the strict schema defined in appendix A -->
    <!-- 'manifest-schema': Extracts the manifest schema -->
    <xsl:param name="mode" select="'schema'"/>
       
    <!-- map mode parameter to style names -->
    <!-- which paragraph styles do we wish to extract? -->
    <xsl:variable name="extract-style-name">
        <xsl:choose>
            <xsl:when test="$mode='strict-schema'">
                <xsl:value-of select="'RelaxNG_20_Strict'"/>
            </xsl:when>
            <xsl:when test="$mode='manifest-schema'">
                <xsl:value-of select="'RelaxNG_20_Manifest'"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="'RelaxNG'"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <!-- define 'styles' key. It selects all style:style element with a certain name -->
    <xsl:key name="styles"
             match="style:style"
             use="@style:name" />

    <!-- Look only at paragraphs -->
    <xsl:template match="office:document-content">
        <xsl:apply-templates select="office:body/office:text//text:p"/>
    </xsl:template>

    <!-- Analyze paragraphs -->
    <xsl:template match="text:p">
        <!-- determine all parent styles of this paragraph; use key 'parents' -->
        <xsl:variable name="content-style-names" select="@text:style-name|key('styles',@text:style-name)/@style:parent-style-name"/>
        <xsl:variable name="styles-style-names">
            <xsl:for-each select="document('styles.xml',.)">
                <xsl:value-of select="key('styles',$content-style-names)/@style:parent-style-name"/>
            </xsl:for-each>
        </xsl:variable>

        <!-- if schema parent style is found, generate output -->
        <xsl:if test="$content-style-names = $extract-style-name or $styles-style-names = $extract-style-name">
            <xsl:apply-templates mode="output"/>
            <xsl:text>
</xsl:text>
        </xsl:if>
    </xsl:template>

    <!-- default: don't output anything -->
    <xsl:template match="node()|@*"/>

    <!-- generate output: just copy all text -->
    <xsl:template mode="output" match="*">
        <xsl:apply-templates mode="output"/>
    </xsl:template>
        
    <xsl:template mode="output" match="text()">
        <xsl:copy/>
    </xsl:template>
    
    <!-- white space handling: <text:s>, <text:tab> elements  -->
    <xsl:template mode="output" match="text:tab">
        <xsl:text>    </xsl:text>
    </xsl:template>
    
    <xsl:template mode="output" match="text:s">
        <xsl:call-template name="repeat">
            <xsl:with-param name="count" select="@text:c"/>
            <xsl:with-param name="string" select="' '"/>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template name="repeat">
        <xsl:param name="count"/>
        <xsl:param name="string"/>
        <xsl:if test="$count > 0">
            <xsl:value-of select="$string"/>
            <xsl:call-template name="repeat">
                <xsl:with-param name="count" select="$count - 1"/>
                <xsl:with-param name="string" select="$string"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>