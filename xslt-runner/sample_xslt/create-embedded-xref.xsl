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
    <xsl:variable name="add-text-info" select="$add-xrefs='true'"/>
    <xsl:variable name="keep-attr-elem-xrefs" select="false()"/>
    
    <xsl:variable name="add-xref-anchors" select="false()"/>
    <xsl:variable name="keep-xref-anchors" select="$keep-anchors='true'"/>
    <xsl:variable name="check-xref-anchors" select="true()"/>

    <xsl:variable name="create-odf-references" select="true()"/>
    <xsl:variable name="create-cardinality-info" select="false()"/>

    <xsl:variable name="element-prefix" select="'element-'"/>
    <xsl:variable name="attribute-prefix" select="'attribute-'"/>
    <xsl:variable name="property-prefix" select="'property-'"/>
    <xsl:variable name="datatype-prefix" select="'datatype-'"/>

    <xsl:variable name="attributes-heading" select="'General Attributes'"/>
    <xsl:variable name="properties-heading" select="'Formatting Attributes'"/>
    <xsl:variable name="datatypes-heading" select="'Other Datatypes'"/>

        
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
    <xsl:template match="text:p[starts-with(.,$attribute-prefix) or starts-with(.,$property-prefix)]">        
        <!-- Remove anchor paragraph if $keep-xref-anchors is false -->
        <xsl:if test="$keep-xref-anchors">
            <xsl:copy>
                <xsl:apply-templates select="@*|node()"/>
            </xsl:copy>
        </xsl:if>
        <!-- Check attribute name -->
        <xsl:variable name="attr-name-raw" select="substring-after(normalize-space(.),'-')"/>
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
        <xsl:variable name="fp" select="starts-with(.,$property-prefix)"/>
        <xsl:variable name="attr-defs" select="document($xref-schema-file)/rng:grammar/rng:element[(starts-with(@name,'style:') and contains(@name,'-properties'))=$fp]/rng:attribute[@name=$attr-name]"/>
        <xsl:if test="$check-xref-anchors and not($attr-defs)">
            <xsl:message>XRef &quot;<xsl:value-of select="."/>&quot;: No attribute definition found in schema for &quot;<xsl:value-of select="$attr-name"/>&quot;.</xsl:message>
        </xsl:if>
        <!-- Add xrefs -->
        <xsl:if test="$add-attr-elem-xrefs">
            <xsl:apply-templates select="$attr-defs">
                <xsl:with-param name="element-list" select="$element-list"/>
                <xsl:with-param name="fp" select="$fp"/>
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
                <xsl:with-param name="element-list" select="concat('_',$remainder)"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <!-- ************************* -->
    <!-- ** existing references ** -->
    <!-- ************************* -->
    <xsl:template match="text:p[@text:style-name='Attribute_20_List' or @text:style-name='Child_20_Element_20_List' or @text:style-name='Parent_20_Element_20_List' or @text:style-name='Attribute_20_Value_20_List']">
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
                    <xsl:variable name="is-in-attributes" select="preceding::text:h[@text:outline-level='1']=$attributes-heading"/>
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
            <xsl:when test="starts-with($tag, 'odf:') or starts-with($tag, 'pkg:')">
                <!-- OWL -->
                <xsl:copy>
                    <xsl:apply-templates select="@*|node()"/>
                </xsl:copy>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="attr-name" select="$tag"/>
                <xsl:variable name="fp" select="preceding::text:h[@text:outline-level='1'][last()]=$properties-heading"/>
                <xsl:if test="$check-xref-anchors">
                    <xsl:choose>
                        <xsl:when test="not(document($xref-schema-file)/rng:grammar/rng:element[(starts-with(@name,'style:') and contains(@name,'-properties'))=$fp]/rng:attribute[@name=$attr-name])">
                            <xsl:message>Heading &quot;<xsl:value-of select="."/>&quot;: No attribute definition found in schema for &quot;<xsl:value-of select="$attr-name"/>&quot;<xsl:if test="$fp"> (property)</xsl:if>.</xsl:message>
                        </xsl:when>
                        <xsl:when test="$fp and not(//text:p[starts-with(.,$property-prefix) and (normalize-space(.)=concat($property-prefix,$attr-name) or starts-with(.,concat($property-prefix,$attr-name,'_')))])">
                            <xsl:message>Heading &quot;<xsl:value-of select="."/>&quot;: No attribute xref found for &quot;<xsl:value-of select="$attr-name"/>&quot; (property).</xsl:message>
                        </xsl:when>
                        <xsl:when test="not($fp) and not(//text:p[starts-with(.,$attribute-prefix) and (normalize-space(.)=concat($attribute-prefix,$attr-name) or starts-with(.,concat($attribute-prefix,$attr-name,'_')))])">
                            <xsl:message>Heading &quot;<xsl:value-of select="."/>&quot;: No attribute xref found for &quot;<xsl:value-of select="$attr-name"/>&quot;.</xsl:message>
                        </xsl:when>
                    </xsl:choose>
                </xsl:if>
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                    <xsl:variable name="ref-name">
                        <xsl:choose>
                            <xsl:when test="$fp"><xsl:value-of select="concat($property-prefix,$attr-name)"/></xsl:when>
                            <xsl:otherwise><xsl:value-of select="concat($attribute-prefix,$attr-name)"/></xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>
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
    
    <xsl:template match="text:h/text:reference-mark-start|text:h/text:reference-mark-end">
        <xsl:if test="not($create-odf-references) or not(starts-with(@text:name,$attribute-prefix) or starts-with(@text:name,$property-prefix) or starts-with(@text:name,$element-prefix) or starts-with(@text:name,$datatype-prefix))">
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

    <xsl:template match="text:h[@text:outline-level='3' and preceding::text:h[@text:outline-level='2'][last()]=$datatypes-heading]">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:variable name="ref-name" select="concat($datatype-prefix,normalize-space(.))"/>
            <xsl:if test="$create-odf-references">
                <text:reference-mark-start text:name="{$ref-name}"/>
            </xsl:if>
            <xsl:apply-templates select="node()"/>
            <xsl:if test="$create-odf-references">
                <text:reference-mark-end text:name="{$ref-name}"/>
            </xsl:if>
        </xsl:copy>
    </xsl:template>

    <!-- default: copy everything. -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- select all <element> nodes in the file or in included files -->
    <xsl:template match="rng:element">                
        <xsl:call-template name="create-elem-root-elem-list"/>
        <xsl:call-template name="create-elem-parent-elem-list"/>
        <xsl:choose>
            <xsl:when test="@name='office:script'">
                <xsl:call-template name="create-attr-list"/>
                <text:p text:style-name="Child_20_Element_20_List">
                    <xsl:text>The </xsl:text>
                    <text:span text:style-name="Element">
                        <xsl:text>&lt;</xsl:text>
                        <xsl:value-of select="@name"/>
                        <xsl:text>&gt;</xsl:text>
                    </text:span>
                    <xsl:text> element has mixed content where arbitrary child elements are permitted.</xsl:text>
                </text:p>
            </xsl:when>
            <xsl:when test="rng:element/rng:anyName">
                <!-- arbitrary content -->
                <xsl:message>Element <xsl:value-of select="@name"/>: No child element info added (element may have any content).</xsl:message>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="create-attr-list"/>
                <xsl:call-template name="create-child-elem-list"/>
                <xsl:call-template name="create-text-info"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>   

    <!-- select all <element> nodes in the file or in included files -->
    <xsl:template match="rng:attribute">
        <xsl:param name="element-list"/>
        <xsl:param name="fp"/>
        <xsl:variable name="name" select="@name"/>
        <xsl:if test="not(preceding::rng:attribute[@name=$name and (starts-with(ancestor::rng:element/@name,'style:') and contains(ancestor::rng:element/@name,'-properties'))=$fp])">
            <xsl:call-template name="create-attr-parent-elem-list">
                <xsl:with-param name="element-list" select="$element-list"/>
                <xsl:with-param name="fp" select="$fp"/>
            </xsl:call-template>
            <xsl:call-template name="create-attr-value-list">
                <xsl:with-param name="element-list" select="$element-list"/>
                <xsl:with-param name="fp" select="$fp"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>   
    
    <xsl:template name="create-attr-list">
        <xsl:variable name="count" select="count(rng:attribute)"/>
        <xsl:variable name="fp" select="starts-with(@name,'style:') and contains(@name,'-properties')"/>
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
                            <xsl:text> has the following attribute: </xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text> has the following attributes: </xsl:text>
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
                            <text:span text:style-name="Attribute"><xsl:value-of select="@name"/></text:span>
                            <xsl:if test="$create-cardinality-info">
                                <xsl:choose>
                                    <xsl:when test="not(@condition) or @condition='' or @condition='interleave' or @condition='interleave/interleave'">
                                        <!-- mandatory: no hint -->
                                    </xsl:when>
                                    <xsl:when test="contains(@condition,'optional') and not(contains(@condition,'group') or contains(@condition,'choice'))">
                                        <xsl:text>(?)</xsl:text>
                                    </xsl:when>
                                    <xsl:when test="@condition='zeroOrMore'">
                                        <xsl:text>(?)</xsl:text>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:message>Attribute <xsl:value-of select="@name"/>: Complex cardinality: <xsl:value-of select="@condition"/></xsl:message>
                                        <xsl:text>(!)</xsl:text>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:if>
                            <xsl:variable name="ref-name">
                                <xsl:choose>
                                    <xsl:when test="$fp"><xsl:value-of select="concat($property-prefix,@name)"/></xsl:when>
                                    <xsl:otherwise><xsl:value-of select="concat($attribute-prefix,@name)"/></xsl:otherwise>
                                </xsl:choose>
                            </xsl:variable>
                            <xsl:text> </xsl:text><text:reference-ref text:ref-name="{$ref-name}" text:reference-format="chapter">?</text:reference-ref>
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
        <xsl:variable name="count" select="count(rng:element[@name])"/>
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
                            <xsl:text> has the following child element: </xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text> has the following child elements: </xsl:text>
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

    <xsl:template name="create-text-info">
        <xsl:if test="$add-text-info">
            <xsl:choose>
                <xsl:when test="@name='text:script'">
                    <text:p text:style-name="Child_20_Element_20_List">
                        <xsl:text>The </xsl:text>
                        <text:span text:style-name="Element">
                            <xsl:text>&lt;</xsl:text>
                            <xsl:value-of select="@name"/>
                            <xsl:text>&gt;</xsl:text>
                        </text:span>
                        <xsl:text> element has text content. Text content is only permitted if a </xsl:text>
                        <text:span text:style-name="Attribute"><xsl:text>xlink:href</xsl:text></text:span>
                        <xsl:text> attribute is not present.</xsl:text>
                    </text:p>
                </xsl:when>
                <xsl:when test="@name='meta:user-defined'">
                    <text:p text:style-name="Child_20_Element_20_List">
                        <xsl:text>The </xsl:text>
                        <text:span text:style-name="Element">
                            <xsl:text>&lt;</xsl:text>
                            <xsl:value-of select="@name"/>
                            <xsl:text>&gt;</xsl:text>
                        </text:span>
                        <xsl:text> element has text content, or depending on the value of the </xsl:text>
                        <text:span text:style-name="Attribute"><xsl:text>meta:value-type</xsl:text></text:span>
                        <xsl:text> attribute content of type </xsl:text>
                        <xsl:for-each select="rng:ref|rng:data">
                            <xsl:choose>
                                <xsl:when test="position() = 1"/>
                                <xsl:when test="position() = last()">
                                    <xsl:text> or </xsl:text>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:text>, </xsl:text>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:call-template name="print-datatype">
                                <xsl:with-param name="name" select="@type|@name"/>
                            </xsl:call-template>
                        </xsl:for-each>
                        <xsl:text>.</xsl:text>
                    </text:p>
                </xsl:when>
                <xsl:when test="rng:text[not(@condition) or (@condition='interleave/interleave' and not(.//*))]">
                    <xsl:call-template name="new-line"/>
                    <text:p text:style-name="Child_20_Element_20_List">
                        <xsl:text>The </xsl:text>
                        <text:span text:style-name="Element">
                            <xsl:text>&lt;</xsl:text>
                            <xsl:value-of select="@name"/>
                            <xsl:text>&gt;</xsl:text>
                        </text:span>
                        <xsl:text> element has text content.</xsl:text>
                    </text:p>
                </xsl:when>
                <xsl:when test="rng:text[@condition='zeroOrMore/choice']">
                    <xsl:call-template name="new-line"/>
                    <text:p text:style-name="Child_20_Element_20_List">
                        <xsl:text>The </xsl:text>
                        <text:span text:style-name="Element">
                            <xsl:text>&lt;</xsl:text>
                            <xsl:value-of select="@name"/>
                            <xsl:text>&gt;</xsl:text>
                        </text:span>
                        <xsl:text> element has mixed content.</xsl:text>
                    </text:p>
                </xsl:when>
                <xsl:when test="rng:ref[not(@condition)]|rng:data[not(@condition)]">
                    <xsl:call-template name="new-line"/>
                    <text:p text:style-name="Child_20_Element_20_List">
                        <xsl:text>The </xsl:text>
                        <text:span text:style-name="Element">
                            <xsl:text>&lt;</xsl:text>
                            <xsl:value-of select="@name"/>
                            <xsl:text>&gt;</xsl:text>
                        </text:span>
                        <xsl:text> element has content of data type </xsl:text>
                        <xsl:call-template name="print-datatype">
                            <xsl:with-param name="name" select="rng:data/@type|rng:ref/@name"/>
                        </xsl:call-template>
                        <xsl:text>.</xsl:text>
                    </text:p>
                </xsl:when>
                <xsl:when test="rng:text|rng:ref|rng:data">
                     <xsl:message>Element <xsl:value-of select="@name"/>: complex condition for text: <xsl:value-of select="rng:text/@condition"/></xsl:message>
                </xsl:when>
            </xsl:choose>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="create-attr-parent-elem-list">
        <xsl:param name="attr-name" select="@name"/>
        <xsl:param name="element-list"/>
        <xsl:param name="fp"/>
        <xsl:variable name="parents" select="ancestor::rng:grammar/rng:element[((starts-with(@name,'style:') and contains(@name,'-properties'))=$fp) and (not($element-list) or contains($element-list,concat('_',$element-prefix,@name,'_'))) and rng:attribute[@name=$attr-name]]"/>
        <xsl:variable name="count" select="count($parents)"/>
        <xsl:if test="$check-xref-anchors and $count=0">
            <xsl:message>&quot;<xsl:value-of select="$attr-name"/>&quot; <xsl:if test="$fp"> (property)</xsl:if><xsl:if test="$element-list"> (<xsl:value-of select="$element-list"/>)</xsl:if>: No parent elements found in schema.</xsl:message>
        </xsl:if>
        <xsl:call-template name="new-line"/>
        <text:p text:style-name="Parent_20_Element_20_List">
            <xsl:text>The </xsl:text>
            <text:span text:style-name="Attribute">
                <xsl:value-of select="$attr-name"/>
            </text:span>
            <xsl:text> attribute </xsl:text>
            <xsl:choose>
                <xsl:when test="$count = 1">
                    <xsl:text> is usable with the following element: </xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text> is usable with the following elements: </xsl:text>
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

    <xsl:template name="create-attr-value-list">
        <xsl:param name="attr-name" select="@name"/>
        <xsl:param name="element-list"/>
        <xsl:param name="fp"/>
        <xsl:choose>
            <xsl:when test="$attr-name='text:id' or $attr-name='form:id' or $attr-name='office:value'">
                <xsl:choose>
                    <xsl:when test="not($element-list)">
                        <xsl:apply-templates select="*" mode="attr-value">
                            <xsl:with-param name="attr-name" select="$attr-name"/>
                        </xsl:apply-templates>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:apply-templates select="ancestor::rng:grammar/rng:element[((starts-with(@name,'style:') and contains(@name,'-properties'))=$fp) and (not($element-list) or contains($element-list,concat('_',$element-prefix,@name,'_')))]/rng:attribute[@name=$attr-name]/*" mode="attr-value">
                            <xsl:with-param name="attr-name" select="$attr-name"/>
                        </xsl:apply-templates>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="$attr-name='chart:legend-position' or 
                            $attr-name='draw:concave' or
                            $attr-name='form:image-position' or
                            $attr-name='meta:value-type' or
                            $attr-name='style:legend-expansion' or
                            $attr-name='style:family' or
                            $attr-name='style:type' or
                            $attr-name='table:member-type' or 
                            ($attr-name='table:orientation' and contains($element-list,'table:data-pilot-field'))or 
                            $attr-name='table:sort-mode' or
                            ($attr-name='text:display' and (contains($element-list,'text:section') or contains($element-list,'style:text-properties'))) or
                            $attr-name='chart:symbol-type'">
                <xsl:apply-templates select="ancestor::rng:grammar/rng:element[((starts-with(@name,'style:') and contains(@name,'-properties'))=$fp) and (not($element-list) or contains($element-list,concat('_',$element-prefix,@name,'_'))) and rng:attribute[@name=$attr-name]][1]" mode="merge-attr-value">
                    <xsl:with-param name="attr-name" select="$attr-name"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:when test="$attr-name='office:value-type'">
                <xsl:apply-templates select="ancestor::rng:grammar/rng:element[@name='text:variable-decl']/rng:attribute[@name=$attr-name]/*" mode="attr-value">
                    <xsl:with-param name="attr-name" select="$attr-name"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:when test="$attr-name='style:num-format'">
                <xsl:apply-templates select="ancestor::rng:grammar/rng:element[@name='text:page-number']" mode="merge-attr-value">
                    <xsl:with-param name="attr-name" select="$attr-name"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:when test="ancestor::rng:grammar/rng:element[((starts-with(@name,'style:') and contains(@name,'-properties'))=$fp) and (not($element-list) or contains($element-list,concat('_',$element-prefix,@name,'_'))) and count(rng:attribute[@name=$attr-name])>1]">
                <xsl:message>Attribute <xsl:value-of select="$attr-name"/>: Multiple attribute definitions do exist for at least one element.</xsl:message>
            </xsl:when>
            <xsl:when test="$attr-name='xlink:show' or
                            $attr-name='xlink:actuate' or
                            $attr-name='text:reference-format'">
                <xsl:message>Attribute <xsl:value-of select="$attr-name"/>: No type info added (type too complex).</xsl:message>
            </xsl:when>
            <xsl:when test="not($element-list)">
                <xsl:variable name="count" select="count(*)"/>
                <xsl:variable name="ename" select="name(*)"/>
                <xsl:if test="ancestor::rng:grammar/rng:element[(starts-with(@name,'style:') and contains(@name,'-properties'))=$fp]/rng:attribute[@name=$attr-name and (count(*)!=$count or name(*[1]) != $ename)]">
                    <xsl:message>Attribute <xsl:value-of select="$attr-name"/>: Multiple attribute definitions do exist but no element constrained anchors. this: <xsl:value-of select="$count"/>/<xsl:value-of select="$ename"/> </xsl:message>
                </xsl:if>
                <xsl:apply-templates select="*" mode="attr-value">
                    <xsl:with-param name="attr-name" select="$attr-name"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="ancestor::rng:grammar/rng:element[((starts-with(@name,'style:') and contains(@name,'-properties'))=$fp) and (not($element-list) or contains($element-list,concat('_',$element-prefix,@name,'_')))]/rng:attribute[@name=$attr-name]/*" mode="attr-value">
                    <xsl:with-param name="attr-name" select="$attr-name"/>
                </xsl:apply-templates>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="rng:ref" mode="attr-value">
        <xsl:param name="attr-name"/>
        <xsl:variable name="name" select="@name"/>
        <xsl:call-template name="new-line"/>
        <xsl:choose>
            <xsl:when test="//text:p[.=concat($datatype-prefix,$name)]">
                <text:p text:style-name="Attribute_20_Value_20_List">
                    <xsl:text>The </xsl:text>
                    <text:span text:style-name="Attribute">
                        <xsl:value-of select="$attr-name"/>
                    </text:span>
                    <xsl:text> attribute has the data type </xsl:text>
                    <xsl:call-template name="print-datatype">
                        <xsl:with-param name="name" select="$name"/>
                    </xsl:call-template>
                    <xsl:text>.</xsl:text>
                </text:p>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="document($xref-schema-file)/rng:grammar/rng:define[@name=$name]/*" mode="attr-value">
                    <xsl:with-param name="attr-name" select="$attr-name"/>
                </xsl:apply-templates>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="rng:choice" mode="attr-value">
        <xsl:param name="attr-name"/>
        <xsl:call-template name="new-line"/>
        <text:p text:style-name="Attribute_20_Value_20_List">
            <xsl:call-template name="print-the-values">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:call-template>
            <xsl:for-each select="*">
                <xsl:choose>
                    <xsl:when test="position() = 1"/>
                    <xsl:when test="position() = last()">
                        <xsl:text> or </xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>, </xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:apply-templates select="." mode="individual-value">
                    <xsl:with-param name="attr-name" select="$attr-name"/>
                </xsl:apply-templates>
            </xsl:for-each>
            <xsl:text>.</xsl:text>
        </text:p>
    </xsl:template>

    <xsl:template match="rng:value" mode="attr-value">
        <xsl:param name="attr-name"/>
        <xsl:if test="not(starts-with($attr-name,'xlink:'))">
            <xsl:call-template name="new-line"/>
            <text:p text:style-name="Attribute_20_Value_20_List">
                <xsl:text>The only value of the </xsl:text>
                <text:span text:style-name="Attribute">
                    <xsl:value-of select="$attr-name"/>
                </text:span>
                <xsl:text> attribute is </xsl:text>
                <xsl:apply-templates select="." mode="individual-value">
                    <xsl:with-param name="attr-name" select="$attr-name"/>
                </xsl:apply-templates>
                <xsl:text>.</xsl:text>
            </text:p>
        </xsl:if>
    </xsl:template>

    <xsl:template match="rng:list[rng:zeroOrMore]" mode="attr-value">
        <xsl:param name="attr-name"/>
        <xsl:call-template name="new-line"/>
        <text:p text:style-name="Attribute_20_Value_20_List">
            <xsl:call-template name="print-the-values">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:call-template>
            <xsl:text>space separated possibly empty lists of </xsl:text>
            <xsl:apply-templates select="*/*" mode="attr-list-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
            <xsl:text>.</xsl:text>
        </text:p>
    </xsl:template>

    <xsl:template match="rng:list[rng:oneOrMore]" mode="attr-value">
        <xsl:param name="attr-name"/>
        <xsl:call-template name="new-line"/>
        <text:p text:style-name="Attribute_20_Value_20_List">
            <xsl:call-template name="print-the-values">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:call-template>
            <xsl:text>space separated non-empty lists of </xsl:text>
            <xsl:apply-templates select="*/*" mode="attr-list-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
            <xsl:text>.</xsl:text>
        </text:p>
    </xsl:template>

    <xsl:template match="rng:list[count(*)=2 and count(rng:ref)=2]" mode="attr-value">
        <xsl:param name="attr-name"/>
        <xsl:call-template name="new-line"/>
        <text:p text:style-name="Attribute_20_Value_20_List">
            <xsl:call-template name="print-the-values">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:call-template>
            <xsl:apply-templates select="rng:ref[1]" mode="individual-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
            <xsl:text> followed by </xsl:text>
            <xsl:apply-templates select="rng:ref[2]" mode="individual-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
            <xsl:text>.</xsl:text>
        </text:p>
    </xsl:template>

    <xsl:template match="rng:list[count(*)=2 and count(rng:choice)=1 and count(rng:optional/rng:ref)=1]" mode="attr-value">
        <xsl:param name="attr-name"/>
        <xsl:call-template name="new-line"/>
        <text:p text:style-name="Attribute_20_Value_20_List">
            <xsl:call-template name="print-the-values">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:call-template>
            <xsl:apply-templates select="*[1]" mode="attr-list-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
            <xsl:text> optionally followed by </xsl:text>
            <xsl:apply-templates select="rng:optional/*" mode="individual-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
            <xsl:text>.</xsl:text>
        </text:p>
    </xsl:template>

    <xsl:template match="rng:list[count(*)=2 and count(rng:choice)=1 and count(rng:ref)=1]" mode="attr-value">
        <xsl:param name="attr-name"/>
        <xsl:call-template name="new-line"/>
        <text:p text:style-name="Attribute_20_Value_20_List">
            <xsl:call-template name="print-the-values">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:call-template>
            <xsl:apply-templates select="*[1]" mode="attr-list-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
            <xsl:text> followed by </xsl:text>
            <xsl:apply-templates select="*[2]" mode="individual-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
            <xsl:text>.</xsl:text>
        </text:p>
    </xsl:template>

    <xsl:template match="rng:list[count(*)=4 and count(rng:ref[@name='integer'])=4]" mode="attr-value">
        <xsl:param name="attr-name"/>
        <xsl:call-template name="new-line"/>
        <text:p text:style-name="Attribute_20_Value_20_List">
            <xsl:call-template name="print-the-values">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:call-template>
            <xsl:text>four space separated values of type </xsl:text>
            <xsl:call-template name="print-datatype">
                <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
            <xsl:text>.</xsl:text>
        </text:p>
    </xsl:template>

    <xsl:template match="rng:list[count(*)=3 and count(rng:ref[@name='positiveLength'])=3]" mode="attr-value">
        <xsl:param name="attr-name"/>
        <xsl:call-template name="new-line"/>
        <text:p text:style-name="Attribute_20_Value_20_List">
            <xsl:call-template name="print-the-values">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:call-template>
            <xsl:text>three space separated values of type </xsl:text>
            <xsl:call-template name="print-datatype">
                <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
            <xsl:text>.</xsl:text>
        </text:p>
    </xsl:template>

    <xsl:template match="rng:attribute[@name='style:mirror']/rng:choice" mode="attr-value">
        <xsl:param name="attr-name"/>
        <xsl:if test="not(count(*)=5 and count(rng:value)=2 and count(rng:ref[@name='horizontal-mirror'])=1 and count(rng:list)=2)">
            <xsl:message> Unexpected content <xsl:value-of select="$attr-name"/></xsl:message>
        </xsl:if>
        <xsl:call-template name="new-line"/>
        <text:p text:style-name="Attribute_20_Value_20_List">
            <xsl:call-template name="print-the-values">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:call-template>
            <xsl:for-each select="rng:value|ancestor::rng:grammar/rng:element/rng:define[@name='horizontal-mirror']/rng:choice/rng:value">
                <xsl:choose>
                    <xsl:when test="position() = 1"/>
                    <xsl:otherwise>
                        <xsl:text>, </xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:apply-templates select="." mode="individual-value">
                    <xsl:with-param name="attr-name" select="$attr-name"/>
                </xsl:apply-templates>
            </xsl:for-each>
            <xsl:text>, </xsl:text>
            <xsl:apply-templates select="rng:list[1]" mode="individual-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
            <xsl:text> or </xsl:text>
            <xsl:apply-templates select="rng:list[2]" mode="individual-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>

            <xsl:text>.</xsl:text>
        </text:p>
    </xsl:template>

    <xsl:template match="rng:attribute[@name='draw:line-skew']/rng:list" mode="attr-value">
        <xsl:param name="attr-name"/>
        <xsl:if test="not(count(*)=2 and count(rng:ref[@name='length'])=1 and count(rng:optional)=1 and count(rng:optional/*)=2)">
            <xsl:message> Unexpected content <xsl:value-of select="$attr-name"/></xsl:message>
        </xsl:if>
        <xsl:call-template name="new-line"/>
        <text:p text:style-name="Attribute_20_Value_20_List">
            <xsl:call-template name="print-the-values">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:call-template>
            <xsl:text>one, two or three space separated </xsl:text>
            <xsl:apply-templates select="rng:ref" mode="attr-list-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
            <xsl:text>.</xsl:text>
        </text:p>
    </xsl:template>

    <xsl:template match="rng:data[(@type='double' or @type='decimal') and count(rng:param)=2]" mode="attr-value">
        <xsl:param name="attr-name"/>
        <text:p text:style-name="Attribute_20_Value_20_List">
            <xsl:text>The </xsl:text>
            <text:span text:style-name="Attribute">
                 <xsl:value-of select="$attr-name"/>
            </text:span>
            <xsl:text> attribute has values of type </xsl:text>
            <xsl:call-template name="print-datatype">
                <xsl:with-param name="name" select="@type"/>
            </xsl:call-template>
            <xsl:text> in the range [</xsl:text>
            <text:span text:style-name="Attribute_20_Value">
                <xsl:value-of select="rng:param[@name='minInclusive']"/>
            </text:span>
            <xsl:text>,</xsl:text>
            <text:span text:style-name="Attribute_20_Value">
                <xsl:value-of select="rng:param[@name='maxInclusive']"/>
            </text:span>
            <xsl:text>].</xsl:text>
        </text:p>
    </xsl:template>
    
    <xsl:template match="*" mode="attr-value">
        <xsl:param name="attr-name"/>
        <xsl:message>*** Attribute <xsl:value-of select="$attr-name"/>: Unrecognized value element: <xsl:value-of select="name(.)"/></xsl:message>
    </xsl:template>

    <xsl:template match="rng:element" mode="merge-attr-value">
        <xsl:param name="attr-name"/>
        <xsl:call-template name="new-line"/>
        <text:p text:style-name="Attribute_20_Value_20_List">
            <xsl:call-template name="print-the-values">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:call-template>
            <xsl:for-each select="rng:attribute[@name=$attr-name]">
                <xsl:variable name="first" select="position() = 1"/>
                <xsl:variable name="last" select="position() = last()"/>
                <xsl:for-each select=".//rng:value|.//rng:ref|.//rng:empty">
                    <xsl:choose>
                        <xsl:when test="$first and position() = 1"/>
                        <xsl:when test="$last and position() = last()">
                            <xsl:text> or </xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>, </xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:apply-templates select="." mode="individual-value">
                        <xsl:with-param name="attr-name" select="$attr-name"/>
                    </xsl:apply-templates>
                </xsl:for-each>
            </xsl:for-each>
            <xsl:text>.</xsl:text>
        </text:p>
    </xsl:template>

    <xsl:template match="rng:ref" mode="attr-list-value">
        <xsl:param name="attr-name"/>
        <xsl:variable name="name" select="@name"/>
        <xsl:choose>
            <xsl:when test="//text:p[.=concat($datatype-prefix,$name)]">
                <xsl:text>values of type </xsl:text>
                <xsl:call-template name="print-datatype">
                    <xsl:with-param name="name" select="$name"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="document($xref-schema-file)/rng:grammar/rng:define[@name=$name]/*" mode="attr-list-value">
                    <xsl:with-param name="attr-name" select="$attr-name"/>
                </xsl:apply-templates>
            </xsl:otherwise>
         </xsl:choose>
    </xsl:template>

    <xsl:template match="rng:choice" mode="attr-list-value">
        <xsl:param name="attr-name"/>
        <xsl:text>one of these values: </xsl:text>
        <xsl:for-each select="*">
            <xsl:choose>
                <xsl:when test="position() = 1"/>
                <xsl:when test="position() = last()">
                    <xsl:text>, or </xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>, </xsl:text>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:apply-templates select="." mode="individual-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="*" mode="attr-list-value">
        <xsl:param name="attr-name"/>
        <xsl:message>*** Attribute <xsl:value-of select="$attr-name"/>: Unrecognized value element in list: <xsl:value-of select="name(.)"/></xsl:message>
    </xsl:template>


    <xsl:template match="rng:value" mode="individual-value">
        <xsl:param name="attr-name"/>
        <text:span text:style-name="Attribute_20_Value">
           <xsl:value-of select="."/>
        </text:span>
    </xsl:template>

    <xsl:template match="rng:empty" mode="individual-value">
        <xsl:param name="attr-name"/>
        <xsl:text>an empty string</xsl:text>
    </xsl:template>

    <xsl:template match="rng:ref" mode="individual-value">
        <xsl:param name="attr-name"/>
        <xsl:variable name="name" select="@name"/>
        <xsl:choose>
            <xsl:when test="//text:p[.=concat($datatype-prefix,$name)]">
                <xsl:text>a value of type </xsl:text>
                <xsl:call-template name="print-datatype">
                    <xsl:with-param name="name" select="@name"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="document($xref-schema-file)/rng:grammar/rng:define[@name=$name]/*" mode="individual-value">
                    <xsl:with-param name="attr-name" select="$attr-name"/>
                </xsl:apply-templates>
            </xsl:otherwise>
         </xsl:choose>
    </xsl:template>

    <xsl:template match="rng:choice" mode="individual-value">
        <xsl:param name="attr-name"/>
        <xsl:text>one of these values: </xsl:text>
        <xsl:for-each select="*">
            <xsl:choose>
                <xsl:when test="position() = 1"/>
                <xsl:when test="position() = last()">
                    <xsl:text> or </xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>, </xsl:text>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:apply-templates select="." mode="individual-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="rng:list[rng:oneOrMore and count(*)=1]" mode="individual-value">
        <xsl:param name="attr-name"/>
        <xsl:text>a non-empty space separated list of </xsl:text>
        <xsl:apply-templates select="*/*" mode="attr-list-value">
             <xsl:with-param name="attr-name" select="$attr-name"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="rng:list[count(*)=2 and (count(rng:ref) + count(rng:choice))=2]" mode="individual-value">
        <xsl:param name="attr-name"/>
        <xsl:apply-templates select="*[1]" mode="individual-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
        <xsl:text> followed by </xsl:text>
        <xsl:apply-templates select="*[2]" mode="individual-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="rng:list[count(*)=2 and (count(rng:ref) + count(rng:value))=2]" mode="individual-value">
        <xsl:param name="attr-name"/>
        <xsl:apply-templates select="*[1]" mode="individual-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
        <xsl:text> followed by </xsl:text>
        <xsl:apply-templates select="*[2]" mode="individual-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
    </xsl:template>


    <xsl:template match="rng:data[(@type='double' or @type='decimal') and count(rng:param)=2]" mode="individual-value">
        <xsl:param name="attr-name"/>
        <xsl:text>a value of type </xsl:text>
        <xsl:call-template name="print-datatype">
            <xsl:with-param name="name" select="@type"/>
        </xsl:call-template>
        <xsl:text> in the range [</xsl:text>
        <text:span text:style-name="Attribute_20_Value">
            <xsl:value-of select="rng:param[@name='minInclusive']"/>
        </text:span>
        <xsl:text>,</xsl:text>
        <text:span text:style-name="Attribute_20_Value">
            <xsl:value-of select="rng:param[@name='maxInclusive']"/>
        </text:span>
        <xsl:text>]</xsl:text>
    </xsl:template>

    <xsl:template match="rng:data[@type='decimal' and count(rng:param)=1]" mode="individual-value">
        <xsl:if test="not(rng:param[@name='minInclusive']='0.0')"></xsl:if>
        <xsl:param name="attr-name"/>
        <xsl:text>a non negative value of type </xsl:text>
        <xsl:call-template name="print-datatype">
            <xsl:with-param name="name" select="@type"/>
        </xsl:call-template>
    </xsl:template>


    <xsl:template match="*" mode="individual-value">
        <xsl:param name="attr-name"/>
        <xsl:message>*** Attribute <xsl:value-of select="$attr-name"/>: Unsupported individual value: <xsl:value-of select="name(.)"/></xsl:message>
    </xsl:template>

    <xsl:template name="print-the-values">
        <xsl:param name="attr-name"/>
        <xsl:text>The values of the </xsl:text>
        <text:span text:style-name="Attribute">
             <xsl:value-of select="$attr-name"/>
        </text:span>
        <xsl:text> attribute are </xsl:text>
    </xsl:template>

    <xsl:template name="print-datatype">
        <xsl:param name="name"/>
        <text:span text:style-name="Datatype">
            <xsl:value-of select="$name"/>
        </text:span>
        <xsl:text> </xsl:text><text:reference-ref text:ref-name="{concat($datatype-prefix,$name)}" text:reference-format="chapter">?</text:reference-ref>
    </xsl:template>


    <xsl:template name="create-elem-parent-elem-list">
        <xsl:param name="elem-name" select="@name"/>
        <xsl:variable name="parents" select="ancestor::rng:grammar/rng:element[rng:element[@name=$elem-name]]"/>
        <xsl:variable name="count" select="count($parents)"/>
        <xsl:if test="$count > 0">
            <xsl:call-template name="new-line"/>
            <text:p text:style-name="Parent_20_Element_20_List">
                <xsl:text>The </xsl:text>
                <text:span text:style-name="Element">
                    <xsl:text>&lt;</xsl:text><xsl:value-of select="$elem-name"/><xsl:text>&gt;</xsl:text>
                </text:span>
                <xsl:text> element </xsl:text>
                <xsl:choose>
                    <xsl:when test="$count = 1">
                        <xsl:text> is usable with the following element: </xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text> is usable with the following elements: </xsl:text>
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
        </xsl:if>
    </xsl:template>

    <xsl:template name="create-elem-root-elem-list">
        <xsl:param name="elem-name" select="@name"/>
        <xsl:if test="ancestor::rng:grammar/rng:start/rng:element[@name=$elem-name]">
            <xsl:call-template name="new-line"/>
            <text:p text:style-name="Parent_20_Element_20_List">
                <xsl:text>The </xsl:text>
                <text:span text:style-name="Element">
                    <xsl:text>&lt;</xsl:text><xsl:value-of select="$elem-name"/><xsl:text>&gt;</xsl:text>
                </text:span>
                <xsl:text> element is a root element.</xsl:text>
            </text:p>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="new-line">
        <xsl:text>
</xsl:text>
    </xsl:template>
    
</xsl:stylesheet>
