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

<!-- This stylesheet inserts cross references into the ODF v1.-2 -->
<!-- specification. The cross reference information is calculated -->
<!-- from a flat ODF schema file whose location must be provided -->
<!-- by the "xref-schema-file" parameter -->


<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                 
                xmlns:rng="http://relaxng.org/ns/structure/1.0"                
                xmlns:a="http://relaxng.org/ns/compatibility/annotations/1.0"                
                xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
                xmlns="http://relaxng.org/ns/structure/1.0" 
                exclude-result-prefixes="rng xsl a"
                version="1.0">    
    <xsl:output method="xml" indent="no"/>    
    
    <!-- The flat schema file -->
    <xsl:param name="xref-schema-file"/>
    
    <!-- Whether or not to keep "element-" and "attribute-" paragraphs. -->
    <xsl:param name="keep-anchors" select="'false'"/>
    
    <!-- Whether or not to add element and attribute lists. -->
    <xsl:param name="add-xrefs" select="'true'"/>
    
    <xsl:variable name="add-attr-elem-xrefs" select="$add-xrefs='true'"/>
    <xsl:variable name="keep-attr-elem-xrefs" select="false()"/>
    
    <xsl:variable name="add-xref-anchors" select="false()"/>
    <xsl:variable name="keep-xref-anchors" select="$keep-anchors='true'"/>
    <xsl:variable name="check-xref-anchors" select="true()"/>

    <xsl:variable name="create-odf-references" select="true()"/>

    <xsl:variable name="element-prefix" select="'element-'"/>
    <xsl:variable name="attribute-prefix" select="'attribute-'"/>
        
    <!-- ********************************* -->
    <!-- ** element anchors (element-*) ** -->
    <!-- ********************************* -->
    <xsl:template match="text:p[starts-with(.,$element-prefix)]">        
        <!-- Remove anchor paragraph if $keep-xref-anchors is false -->
        <xsl:if test="$keep-xref-anchors">
            <xsl:copy>
                <xsl:apply-templates select="@*|node()"/>
            </xsl:copy>
        </xsl:if>
        <!-- Check element name -->
        <xsl:variable name="element-name" select="normalize-space(substring(., string-length($element-prefix)+1))"/>
        <xsl:variable name="element-defs" select="document($xref-schema-file)/rng:grammar/rng:element[@name=$element-name]"/>
        <xsl:if test="$check-xref-anchors and not($element-defs)">
            <xsl:message>XRef &quot;<xsl:value-of select="."/>&quot;: No element definition found in schema for &quot;<xsl:value-of select="$element-name"/>&quot;.</xsl:message>
        </xsl:if>
        <!-- Add xrefs -->
        <xsl:if test="$add-attr-elem-xrefs">
            <xsl:apply-templates select="$element-defs"/>
        </xsl:if>
    </xsl:template>    

    <!-- ************************************* -->
    <!-- ** attribute anchors (attribute-*) ** -->
    <!-- ************************************* -->
    <xsl:template match="text:p[starts-with(.,$attribute-prefix)]">        
        <!-- Remove anchor paragraph if $keep-xref-anchors is false -->
        <xsl:if test="$keep-xref-anchors">
            <xsl:copy>
                <xsl:apply-templates select="@*|node()"/>
            </xsl:copy>
        </xsl:if>
        <!-- Check attribute name -->
        <xsl:variable name="attr-name-raw" select="substring(normalize-space(.), string-length($attribute-prefix)+1)"/>
        <xsl:variable name="has-elements" select="contains($attr-name-raw,'_')"/>
        <xsl:variable name="attr-name">
            <xsl:choose>
                <xsl:when test="$has-elements">
                    <xsl:value-of select="substring-before($attr-name-raw,'_')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$attr-name-raw"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="element-list">
            <xsl:if test="$has-elements">
                <xsl:value-of select="concat(substring($attr-name-raw,string-length($attr-name)+1),'_')"/>
            </xsl:if>
        </xsl:variable>
        <xsl:if test="$check-xref-anchors and $has-elements">
            <xsl:call-template name="check-element-list">
                <xsl:with-param name="element-list" select="$element-list"/>
            </xsl:call-template>
        </xsl:if>
        <xsl:variable name="attr-defs" select="document($xref-schema-file)/rng:grammar/rng:element/rng:attribute[@name=$attr-name]"/>
        <xsl:if test="$check-xref-anchors and not($attr-defs)">
            <xsl:message>XRef &quot;<xsl:value-of select="."/>&quot;: No attribute definition found in schema for &quot;<xsl:value-of select="$attr-name"/>&quot;.</xsl:message>
        </xsl:if>
        <!-- Add xrefs -->
        <xsl:if test="$add-attr-elem-xrefs">
            <xsl:apply-templates select="$attr-defs">
                <xsl:with-param name="element-list" select="$element-list"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>    
    
    <xsl:template name="check-element-list">
        <xsl:param name="element-list"/>
        <xsl:param name="list" select="substring($element-list,string-length($element-prefix)+2)"/>
        <xsl:variable name="element-name" select="substring-before($list,'_')"/>
        <xsl:if test="not(document($xref-schema-file)/rng:grammar/rng:element[@name=$element-name])">
            <xsl:message>XRef &quot;<xsl:value-of select="."/>&quot;: No element definition found in schema for &quot;<xsl:value-of select="$element-name"/>&quot;.</xsl:message>
        </xsl:if>
        <xsl:variable name="remainder" select="substring-after($list,'_')"/>
        <xsl:if test="$remainder">
            <xsl:call-template name="check-element-list">
                <xsl:with-param name="element-list" select="$remainder"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <!-- ************************* -->
    <!-- ** existing references ** -->
    <!-- ************************* -->
    <xsl:template match="text:p[@text:style-name='Attribute_20_List' or @text:style-name='Child_20_Element_20_List' or @text:style-name='Parent_20_Element_20_List']">
        <!-- Remove them if $keep-attr-elem-xrefs is false -->
        <xsl:if test="$keep-attr-elem-xrefs">
            <xsl:copy>
                <xsl:apply-templates select="@*|node()"/>
            </xsl:copy>
        </xsl:if>
    </xsl:template>

    <!-- ****************************************** -->
    <!-- ** Headings for elements and attributes ** -->
    <!-- ****************************************** -->
    <xsl:template match="text:h[contains(.,':')]">
        <!-- get element or attribute name -->
        <xsl:variable name="tag">
            <xsl:choose>
                <xsl:when test="contains(.,'(')">
                    <xsl:value-of select="normalize-space(substring-before(.,'('))"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <!-- create ODF ref-mark elements if it is a avlid element or attribute name -->
        <xsl:choose>
            <xsl:when test="starts-with($tag, '&lt;') and contains($tag,'&gt;')">
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                    <xsl:variable name="is-in-attributes" select="preceding::text:h[@text:outline-level='1']='Attributes'"/>
                    <xsl:call-template name="create-element-ref-mark-start">
                         <xsl:with-param name="tag" select="$tag"/>
                         <xsl:with-param name="is-in-attributes" select="$is-in-attributes"/>
                    </xsl:call-template>
                    <xsl:apply-templates select="node()"/>
                    <xsl:call-template name="create-element-ref-mark-end">
                         <xsl:with-param name="tag" select="$tag"/>
                         <xsl:with-param name="is-in-attributes" select="$is-in-attributes"/>
                    </xsl:call-template>
                </xsl:copy>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="attr-name" select="$tag"/>
                <xsl:if test="$check-xref-anchors">
                    <xsl:choose>
                        <xsl:when test="not(document($xref-schema-file)/rng:grammar//rng:attribute[@name=$attr-name])">
                            <xsl:message>Heading &quot;<xsl:value-of select="."/>&quot;: No attribute definition found in schema for &quot;<xsl:value-of select="$attr-name"/>&quot;.</xsl:message>
                        </xsl:when>
                        <xsl:when test="not(//text:p[starts-with(.,$attribute-prefix) and (normalize-space(.)=concat($attribute-prefix,$attr-name) or starts-with(.,concat($attribute-prefix,$attr-name,'_')))])">
                            <xsl:message>Heading &quot;<xsl:value-of select="."/>&quot;: No attribute xref found for &quot;<xsl:value-of select="$attr-name"/>&quot;.</xsl:message>
                        </xsl:when>
                    </xsl:choose>
                </xsl:if>
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                    <xsl:variable name="ref-name" select="concat($attribute-prefix,$attr-name)"/>
                    <xsl:if test="$create-odf-references">
                        <text:reference-mark-start text:name="{$ref-name}"/>
                    </xsl:if>
                    <xsl:apply-templates select="node()"/>
                    <xsl:if test="$create-odf-references">
                        <text:reference-mark-end text:name="{$ref-name}"/>
                    </xsl:if>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="text:reference-mark-start|text:reference-mark-end">
        <xsl:if test="not($create-odf-references) or not(starts-with(@text:name,$attribute-prefix) or starts-with(@text:name,$element-prefix))">
            <xsl:copy>
                <xsl:apply-templates select="@*|node()"/>
            </xsl:copy>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="create-element-ref-mark-start">
        <xsl:param name="tag"/>
        <xsl:param name="is-in-attributes"/>
        <xsl:variable name="element-name" select="substring-after(substring-before($tag,'&gt;'),'&lt;')"/>
        <xsl:variable name="remainder" select="substring-after($tag,'&gt;')"/>
        
        <xsl:if test="$check-xref-anchors">
            <xsl:choose>
                <xsl:when test="not(document($xref-schema-file)/rng:grammar/rng:element[@name=$element-name])">
                    <xsl:message>Heading: &quot;<xsl:value-of select="."/>&quot;: No element definition found in schema for element &quot;<xsl:value-of select="$element-name"/>&quot;.</xsl:message>
                </xsl:when>
                <xsl:when test="not(//text:p[starts-with(.,$element-prefix) and normalize-space(.)=concat($element-prefix,$element-name)])">
                    <xsl:message>Heading &quot;<xsl:value-of select="."/>&quot;: No element xref found for element &quot;<xsl:value-of select="$element-name"/>&quot;.</xsl:message>
                </xsl:when>
            </xsl:choose>
        </xsl:if>

        <xsl:if test="$create-odf-references and not($is-in-attributes)">
            <xsl:variable name="ref-name" select="concat($element-prefix,$element-name)"/>
            <text:reference-mark-start text:name="{$ref-name}"/>
        </xsl:if>
        
        <xsl:if test="contains($remainder,'&lt;') and contains(substring-after($remainder,'&lt;'),'&gt;')">
            <xsl:call-template name="create-element-ref-mark-start">
                <xsl:with-param name="tag" select="$remainder"/>
                <xsl:with-param name="is-in-attributes" select="$is-in-attributes"/>
            </xsl:call-template>
        </xsl:if>        
    </xsl:template>
    
    <xsl:template name="create-element-ref-mark-end">
        <xsl:param name="tag"/>
        <xsl:param name="is-in-attributes"/>
        <xsl:if test="not($is-in-attributes)">
            <xsl:variable name="element-name" select="substring-after(substring-before($tag,'&gt;'),'&lt;')"/>
            <xsl:variable name="remainder" select="substring-after($tag,'&gt;')"/>

            <xsl:if test="contains($remainder,'&lt;') and contains(substring-after($remainder,'&lt;'),'&gt;')">
                <xsl:call-template name="create-element-ref-mark-end">
                    <xsl:with-param name="tag" select="$remainder"/>
                    <xsl:with-param name="is-in-attributes" select="$is-in-attributes"/>
                </xsl:call-template>
            </xsl:if>

            <xsl:if test="$create-odf-references">
                <xsl:variable name="ref-name" select="concat($element-prefix,$element-name)"/>
                <text:reference-mark-end text:name="{$ref-name}"/>
            </xsl:if>        
        </xsl:if>
    </xsl:template>
    
    <!-- default: copy everything. -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- select all <element> nodes in the file or in included files -->
    <xsl:template match="rng:element">                
        <xsl:call-template name="create-elem-parent-elem-list"/>
        <xsl:call-template name="create-attr-list"/>
        <xsl:call-template name="create-child-elem-list"/>
    </xsl:template>   

    <!-- select all <element> nodes in the file or in included files -->
    <xsl:template match="rng:attribute">
        <xsl:param name="element-list"/>
        <xsl:variable name="name" select="@name"/>
        <xsl:if test="not(preceding::rng:attribute[@name=$name])">
            <xsl:call-template name="create-attr-parent-elem-list">
                <xsl:with-param name="element-list" select="$element-list"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>   
    
    <xsl:template name="create-attr-list">
        <xsl:variable name="count" select="count(rng:attribute)"/>
        <xsl:call-template name="new-line"/>
        <text:p text:style-name="Attribute_20_List">
            <xsl:text>The </xsl:text>
            <text:span text:style-name="Element">
                <xsl:text>&lt;</xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text>&gt;</xsl:text>
            </text:span>
            <xsl:text> element </xsl:text>
            <xsl:choose>
                <xsl:when test="$count > 0">
                    <xsl:choose>
                        <xsl:when test="$count = 1">
                            <xsl:text> may have the following attribute: </xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text> may have the following attributes: </xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                    <!-- collect attributes -->
                    <xsl:for-each select="rng:attribute">
                        <xsl:sort select="@name"/>
                        <xsl:variable name="name" select="@name"/>
                        <xsl:if test="not(preceding-sibling::rng:attribute[@name=$name])">
                            <xsl:choose>
                                <xsl:when test="position() = 1"/>
                                <xsl:when test="position() = last()">
                                    <xsl:text> and </xsl:text>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:text>, </xsl:text>
                                </xsl:otherwise>
                            </xsl:choose>
                            <text:span text:style-name="Attribute"><xsl:value-of select="@name"/></text:span><xsl:text> </xsl:text><text:reference-ref text:ref-name="{concat($attribute-prefix,$name)}" text:reference-format="chapter">?</text:reference-ref>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text> has no attributes</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:text>.</xsl:text>
        </text:p>
    </xsl:template>

    <xsl:template name="create-child-elem-list">
        <xsl:variable name="count" select="count(rng:element)"/>
        <xsl:call-template name="new-line"/>
        <text:p text:style-name="Child_20_Element_20_List">
            <xsl:text>The </xsl:text>
            <text:span text:style-name="Element">
                <xsl:text>&lt;</xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text>&gt;</xsl:text>
            </text:span>
            <xsl:text> element </xsl:text>
            <xsl:choose>
                <xsl:when test="$count > 0">
                    <xsl:choose>
                        <xsl:when test="$count = 1">
                            <xsl:text> may have the following child element: </xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text> may have the following child elements: </xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                    <!-- collect elements -->
                    <xsl:for-each select="rng:element">
                        <xsl:sort select="@name"/>
                        <xsl:variable name="name" select="@name"/>
                        <xsl:if test="not(preceding-sibling::rng:element[@name=$name])">
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
                        </xsl:if>
                    </xsl:for-each>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text> has no child elements</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:text>.</xsl:text>
        </text:p>
    </xsl:template>
    
    <xsl:template name="create-attr-parent-elem-list">
        <xsl:param name="attr-name" select="@name"/>
        <xsl:param name="element-list"/>
        <xsl:variable name="parents" select="ancestor::rng:grammar/rng:element[(not($element-list) or contains($element-list,concat('_',$element-prefix,@name,'_'))) and rng:attribute[@name=$attr-name]]"/>
        <xsl:variable name="count" select="count($parents)"/>
        <xsl:call-template name="new-line"/>
        <text:p text:style-name="Parent_20_Element_20_List">
            <xsl:text>The </xsl:text>
            <text:span text:style-name="Attribute">
                <xsl:value-of select="$attr-name"/>
            </text:span>
            <xsl:text> attribute </xsl:text>
            <xsl:choose>
                <xsl:when test="$count = 1">
                    <xsl:text> may be used with the following element: </xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text> may be used with the following elements: </xsl:text>
                </xsl:otherwise>
            </xsl:choose>
            <!-- collect elements -->
            <xsl:for-each select="$parents">
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
            <xsl:text>.</xsl:text>
        </text:p>
    </xsl:template>

    <xsl:template name="create-elem-parent-elem-list">
        <xsl:param name="elem-name" select="@name"/>
        <xsl:variable name="parents" select="ancestor::rng:grammar/rng:element[rng:element[@name=$elem-name]]"/>
        <xsl:variable name="count" select="count($parents)"/>
        <xsl:call-template name="new-line"/>
        <text:p text:style-name="Parent_20_Element_20_List">
            <xsl:text>The </xsl:text>
            <text:span text:style-name="Element">
                <xsl:text>&lt;</xsl:text><xsl:value-of select="$elem-name"/><xsl:text>&gt;</xsl:text>
            </text:span>
            <xsl:text> element </xsl:text>
            <xsl:choose>
                <xsl:when test="$count = 1">
                    <xsl:text> may be used with the following element: </xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text> may be used with the following elements: </xsl:text>
                </xsl:otherwise>
            </xsl:choose>
            <!-- collect elements -->
            <xsl:for-each select="$parents">
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
            <xsl:text>.</xsl:text>
        </text:p>
    </xsl:template>
    
    <xsl:template name="new-line">
        <xsl:text>
</xsl:text>
    </xsl:template>
    
</xsl:stylesheet>
