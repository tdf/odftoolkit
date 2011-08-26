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

<!-- This stylesheet creates a "flat" schema from an ODF schema. -->
<!-- In the resulting schema, all defines have been resolved and -->
<!-- other simplifications have been made.                       -->
<!-- The rsulting schema IS NOT equivalent to the original ODF   -->
<!-- schema, and it IS NOT a valid RNG schema. It can be used    -->
<!-- only for documentation purposes.                            -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:rng="http://relaxng.org/ns/structure/1.0"
                xmlns:a="http://relaxng.org/ns/compatibility/annotations/1.0"
                xmlns="http://relaxng.org/ns/structure/1.0" version="1.0">
    <xsl:output method="xml" indent="yes"/>

    <!-- The paramter 'incl-default-values' specifies whether default -->
    <!-- value definition should be included into the schema -->
    <xsl:param name="incl-default-values" select="'true'"/>
    
    <!-- The parameter 'incl-types' specifies whether attribute type -->
    <!-- information shall be included -->
    <xsl:param name="incl-types" select="'true'"/>
    
    <!-- The parameter 'incl-conditions' specifies whether elements -->
    <!-- like <optional> shall be included -->
    <xsl:param name="incl-conditions" select="'true'"/>
    
    <!-- The parameter 'incl-elements' specifies whether child elements -->
    <!-- information shall be included -->
    <xsl:param name="incl-elements" select="'false'"/>
    
    
    <xsl:variable name="include-default-values" select="$incl-default-values = 'true'"/>
    <xsl:variable name="include-types" select="$incl-types = 'true'"/>
    <xsl:variable name="include-conditions" select="$incl-conditions = 'true'"/>
    <xsl:variable name="include-elements" select="$incl-elements = 'true'"/>
    
    <!-- ********** -->
    <!-- ** root ** -->
    <!-- ********** -->
    <xsl:template match="/rng:grammar">
        <rng:grammar
            xmlns:rng="http://relaxng.org/ns/structure/1.0"
            xmlns:a="http://relaxng.org/ns/compatibility/annotations/1.0"
            xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
            xmlns:meta="urn:oasis:names:tc:opendocument:xmlns:meta:1.0"
            xmlns:config="urn:oasis:names:tc:opendocument:xmlns:config:1.0"
            xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
            xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
            xmlns:draw="urn:oasis:names:tc:opendocument:xmlns:drawing:1.0"
            xmlns:presentation="urn:oasis:names:tc:opendocument:xmlns:presentation:1.0"
            xmlns:dr3d="urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0"
            xmlns:chart="urn:oasis:names:tc:opendocument:xmlns:chart:1.0"
            xmlns:form="urn:oasis:names:tc:opendocument:xmlns:form:1.0"
            xmlns:script="urn:oasis:names:tc:opendocument:xmlns:script:1.0"
            xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0"
            xmlns:number="urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0"
            xmlns:anim="urn:oasis:names:tc:opendocument:xmlns:animation:1.0"
            xmlns:dc="http://purl.org/dc/elements/1.1/"
            xmlns:xlink="http://www.w3.org/1999/xlink"
            xmlns:math="http://www.w3.org/1998/Math/MathML"
            xmlns:xforms="http://www.w3.org/2002/xforms"
            xmlns:fo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0"
            xmlns:svg="urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0"
            xmlns:smil="urn:oasis:names:tc:opendocument:xmlns:smil-compatible:1.0">
            <xsl:for-each select="//rng:start|document(//rng:include/@href)//rng:start">
                <start>
                    <xsl:apply-templates mode="collect-attrs"/>
                </start>
            </xsl:for-each>
            <!-- select all <element> nodes in the file or in included files -->
            <xsl:for-each select="//rng:element|document(//rng:include/@href)//rng:element">
                <xsl:choose>
                    <xsl:when test="@name">
                        <xsl:variable name="name" select="@name"/>
                        <xsl:if test="not(preceding::rng:element[@name=$name])">
                            <xsl:comment>*** &lt;<xsl:value-of select="@name"/>&gt; ***</xsl:comment>
                            <element name="{@name}">
                                <!-- collect attributes -->
                                <xsl:for-each select="//rng:element[@name=$name]|document(//rng:include/@href)//rng:element[@name=$name]">
                                    <xsl:apply-templates mode="collect-attrs"/>
                                </xsl:for-each>
                            </element>
                        </xsl:if>
                    </xsl:when>
                    <xsl:when test="rng:choice/rng:name">
                        <xsl:variable name="element" select="."/>
                        <xsl:for-each select="rng:choice/rng:name">
                            <xsl:comment>*** &lt;<xsl:value-of select="."/>&gt; ***</xsl:comment>
                            <element name="{.}">
                                <!-- collect attributes -->
                                <xsl:apply-templates select="$element/*" 
                                                     mode="collect-attrs"/>
                            </element>
                        </xsl:for-each>    
                    </xsl:when>
                </xsl:choose>
            </xsl:for-each>
        </rng:grammar>
    </xsl:template>
    
    
    <!-- **************** -->
    <!-- ** attributes ** -->
    <!-- **************** -->

    <!-- match <attribute> elements that have a name attribute -->
    <xsl:template match="rng:attribute[@name]" mode="collect-attrs">
        <xsl:param name="condition" select=""/>
        <attribute name="{@name}">
            <xsl:if test="$include-default-values">
                <xsl:apply-templates select="@a:defaultValue"/>
            </xsl:if>
            <xsl:if test="$condition">
                <xsl:attribute name="condition">
                    <xsl:value-of select="$condition"/>
                </xsl:attribute>
            </xsl:if>
            <!-- collect attribute type -->
            <xsl:if test="$include-types">
                <xsl:apply-templates mode="collect-type"/>
            </xsl:if>
        </attribute>
    </xsl:template>
    
    <!-- match <attribute> elements that have a <name> descendent -->
    <xsl:template match="rng:attribute[rng:choice/rng:name]" mode="collect-attrs">
        <xsl:param name="condition" select=""/>
        <xsl:variable name="attribute" select="."/>
        <xsl:for-each select="rng:choice/rng:name">
            <attribute name="{.}">
                <!-- collect attribute type -->
                <xsl:if test="$include-default-values">
                    <xsl:apply-templates select="$attribute/@a:defaultValue"/>
                </xsl:if>
                <xsl:if test="$condition">
                    <xsl:attribute name="condition">
                        <xsl:value-of select="$condition"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="$include-types">
                    <xsl:apply-templates select="$attribute/*" 
                                         mode="collect-type"/>
                </xsl:if>
            </attribute>
        </xsl:for-each>    
    </xsl:template>

    <!-- ignore attribute definitions with any name -->
    <xsl:template match="rng:attribute[rng:anyName]" mode="collect-attrs"/>

    <!-- match @a:default-value" -->
    <xsl:template match="@a:defaultValue">
        <xsl:attribute name="a:defaultValue">
            <xsl:value-of select="."/>
        </xsl:attribute>
    </xsl:template>

    <!-- ************** -->
    <!-- ** elements ** -->
    <!-- ************** -->

    <!-- match <element> elements that have a name attribute -->
    <xsl:template match="rng:element[@name]" mode="collect-attrs">
        <xsl:param name="condition" select=""/>
        <xsl:if test="$include-elements">
            <element name="{@name}">
                <xsl:if test="$condition">
                    <xsl:attribute name="condition">
                        <xsl:value-of select="$condition"/>
                    </xsl:attribute>
                </xsl:if>
            </element>
        </xsl:if>
    </xsl:template>
    
    <!-- match <element> elements that have a <name> descendent -->
    <xsl:template match="rng:element[rng:choice/rng:name]" mode="collect-attrs">
        <xsl:param name="condition" select=""/>
        <xsl:if test="$include-elements">
            <xsl:for-each select="rng:choice/rng:name">
                <element name="{.}">
                    <xsl:if test="$condition">
                        <xsl:attribute name="condition">
                            <xsl:value-of select="$condition"/>
                        </xsl:attribute>
                    </xsl:if>
                </element>
            </xsl:for-each>    
        </xsl:if>
    </xsl:template>

    <!-- ignore attribute definitions with any name -->
    <xsl:template match="rng:element[rng:anyName]" mode="collect-attrs"/>

    <!-- ignore name elements (they are covered by the fore-each loop already) -->
    <xsl:template match="rng:name" mode="collect-attrs"/>

    <!-- ignore data elements (they may only occure in elements here) -->
    <xsl:template match="rng:data" mode="collect-attrs"/>
    
    
    <!-- ************* -->
    <!-- ** control ** -->
    <!-- ************* -->
        
    <!--match <ref> elements -->
    <xsl:template match="rng:ref" mode="collect-attrs">
        <xsl:param name="condition" select=""/>
        <xsl:comment><xsl:value-of select="@name"/></xsl:comment>
        <!-- match <define> with same name in the current file and within
             included files. -->
        <xsl:variable name="new-condition">
            <xsl:choose>
                <xsl:when test="$include-conditions and @combine and string-length($condition) > 0">
                    <xsl:value-of select="concat($condition, '/', @combine)"/>
                </xsl:when>
                <xsl:when test="$include-conditions and @combine">
                    <xsl:value-of select="concat(@combine)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$condition"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="name" select="@name"/>        
        <xsl:apply-templates 
            select="/rng:grammar/rng:define[@name=$name]/*|/rng:grammar/rng:include/rng:define[@name=$name]/*|document(/rng:grammar/rng:include/@href)/rng:grammar/rng:define[@name=$name]/*"
            mode="collect-attrs">
                <xsl:with-param name="condition" select="$new-condition"/>
        </xsl:apply-templates>
        <xsl:comment>/<xsl:value-of select="@name"/></xsl:comment>
    </xsl:template>
    
    <xsl:template match="rng:define" mode="collect-attrs">
        <xsl:param name="condition" select=""/>
        <xsl:variable name="name" select="@name"/>
        <xsl:apply-templates  select="*" mode="collect-attrs">
            <xsl:with-param name="condition" select="$condition"/>
        </xsl:apply-templates>
        
    </xsl:template>
    
    <!-- match conditions and lists -->
    <xsl:template match="rng:interleave|rng:mixed|rng:optional|rng:choice|rng:group|rng:zeroOrMore|rng:oneOrMore" mode="collect-attrs">
        <xsl:param name="condition" select=""/>
        <xsl:variable name="new-condition">
            <xsl:choose>
                <xsl:when test="$include-conditions and string-length($condition) > 0">
                    <xsl:value-of select="concat($condition, '/', name(.))"/>
                </xsl:when>
                <xsl:when test="$include-conditions">
                    <xsl:value-of select="concat(name(.))"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$condition"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:apply-templates mode="collect-attrs">
            <xsl:with-param name="condition" select="$new-condition"/>
        </xsl:apply-templates>
    </xsl:template>
        
    <xsl:template match="rng:text|rng:empty" mode="collect-attrs"/>
    
    <!-- match all other elements and ignore them -->
    <xsl:template match="*" mode="collect-attrs">
        <xsl:message>Ignored element &lt;<xsl:value-of select="name(.)"/>&gt;, content: <xsl:value-of select="."/>, parent: <xsl:value-of select="name(..)"/>, grandparent: <xsl:value-of select="name(../..)"/>, define name: <xsl:value-of select="ancestor::rng:define[1]/@name"/></xsl:message>
    </xsl:template>

    <xsl:template match="text()" mode="collect-attrs">
        <!-- xsl:message>Ignored <xsl:value-of select="."/></xsl:message -->
    </xsl:template>
        
        <!-- ********************* -->
        <!-- ** attribute types ** -->
        <!-- ********************* -->
        
        <!-- match <ref> elements -->
        <xsl:template match="rng:ref" mode="collect-type">
            <!-- references are assumed to be type names -->
            <ref name="{@name}"/>
        </xsl:template>
        
        <!-- match <data> elements -->
        <xsl:template match="rng:data" mode="collect-type">
            <!-- data types are data types -->
            <data type="{@type}"/>
        </xsl:template>
        
        <!-- match elements that get copied -->
        <xsl:template match="rng:choice|rng:list|rng:group|rng:oneOrMore|rng:zeroOrMore|rng:empty|rng:text" mode="collect-type">
            <xsl:copy><xsl:apply-templates mode="collect-type"/></xsl:copy>
        </xsl:template>
        
        <!-- match <value> elements -->
        <xsl:template match="rng:value" mode="collect-type">
        <value><xsl:value-of select="."/></value>
    </xsl:template>
    
    <!-- match everything else and ignore it -->
    <xsl:template match="*|text()" mode="collect-type"/>
    
</xsl:stylesheet>
