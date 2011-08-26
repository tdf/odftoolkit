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

<!-- This stylesheet inserts cross references into the ODF v1.-2 -->
<!-- specification. The cross reference information is calculated -->
<!-- from a flat ODF schema file whose location must be provided -->
<!-- by the "xref-schema-file" parameter -->


<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                 
                xmlns:rng="http://relaxng.org/ns/structure/1.0"                
                xmlns:a="http://relaxng.org/ns/compatibility/annotations/1.0"                
                xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
                xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
                xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0"
                xmlns:xlink="http://www.w3.org/1999/xlink"
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

    <!-- The paths to the three parts (only required for part 0) -->
    <xsl:param name="part1-content-path" select="''"/>
    <xsl:param name="part2-content-path" select="''"/>
    <xsl:param name="part3-content-path" select="''"/>

    <!-- The paths to the three parts (only required for part 0) -->
    <xsl:param name="part1-toc-rel-path" select="''"/>
    <xsl:param name="part2-toc-rel-path" select="''"/>
    <xsl:param name="part3-toc-rel-path" select="''"/>

    <!-- The hypelink mode for copying TOC's to part 0 -->
    <!-- 'none': remove hyperlinks -->
    <!-- 'adapt': add relative URI to the documents -->
    <!-- '': do not adapt hyperlinks -->
    <xsl:param name="toc-hyperlink-mode" select="''"/>
    
    <xsl:variable name="add-attr-elem-xrefs" select="$add-xrefs='true'"/>
    <xsl:variable name="add-text-info" select="$add-xrefs='true'"/>
    <xsl:variable name="keep-attr-elem-xrefs" select="false()"/>
    
    <xsl:variable name="add-xref-anchors" select="false()"/>
    <xsl:variable name="keep-xref-anchors" select="$keep-anchors='true'"/>
    <xsl:variable name="check-xref-anchors" select="true()"/>

    <xsl:variable name="create-odf-references" select="$xref-schema-file!=''"/>
    <xsl:variable name="create-cardinality-info" select="false()"/>

    <xsl:variable name="keep-annotations" select="false()"/>

    <xsl:variable name="keep-todos" select="false()"/>

    <xsl:variable name="convert-bookmarks-and-hyperlinks" select="true()"/>

    <xsl:variable name="bib-messages" select="false()"/>

    <xsl:variable name="element-prefix" select="'element-'"/>
    <xsl:variable name="attribute-prefix" select="'attribute-'"/>
    <xsl:variable name="property-prefix" select="'property-'"/>
    <xsl:variable name="datatype-prefix" select="'datatype-'"/>
    <xsl:variable name="value-prefix" select="'value:'"/>
    <xsl:variable name="function-prefix" select="'anchor:'"/>
    <xsl:variable name="toc-prefix" select="'toc-'"/>

    <xsl:variable name="attributes-heading" select="'General Attributes'"/>
    <xsl:variable name="attributes-heading-level" select="'1'"/>
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
            <xsl:if test="not(preceding::text:h[1]/@text:outline-level='3')">
                <xsl:message>*** Attribute <xsl:value-of select="$attr-name"/>: Element list <xsl:value-of select="$element-list"/> for attribute on outline level 3</xsl:message>
            </xsl:if>
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
            <xsl:choose>
                <xsl:when test="starts-with($remainder,$element-prefix)">
                    <xsl:call-template name="check-element-list">
                        <xsl:with-param name="element-list" select="concat('_',$remainder)"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="starts-with($remainder,$value-prefix)"/>
                <xsl:otherwise>
                    <xsl:message>XRef &quot;<xsl:value-of select="."/>&quot;: Illegal token &quot;<xsl:value-of select="$remainder"/>&quot;.</xsl:message>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
    </xsl:template>

    <!-- *********************************** -->
    <!-- ** datatype anchors (datatype-*) ** -->
    <!-- *********************************** -->
    <xsl:template match="text:p[starts-with(.,$datatype-prefix)]">
        <!-- Remove anchor paragraph if $keep-xref-anchors is false -->
        <xsl:if test="$keep-xref-anchors">
            <xsl:copy>
                <xsl:apply-templates select="@*|node()"/>
            </xsl:copy>
        </xsl:if>
    </xsl:template>

    <!-- *********************************** -->
    <!-- ** formula anchors (anchor:*) ** -->
    <!-- *********************************** -->
    <xsl:template match="text:p[starts-with(.,$function-prefix)]">
        <!-- Remove anchor paragraph if $keep-xref-anchors is false -->
        <xsl:if test="$keep-xref-anchors">
            <xsl:copy>
                <xsl:apply-templates select="@*|node()"/>
            </xsl:copy>
        </xsl:if>
        <xsl:variable name="function-name" 
                      select="translate(substring-after(normalize-space(.),':'),'abcdefghijklmnopqrstuvwxyz“”','ABCDEFGHIJKLMNOPQRSTUVWXYZ&quot;&quot;')"/>
        <xsl:if test="not(/office:document-content/office:body/office:text/text:h[translate(normalize-space(.),'abcdefghijklmnopqrstuvwxyz “”','ABCDEFGHIJKLMNOPQRSTUVWXYZ-&quot;&quot;')=$function-name])">
            <xsl:message>*** XRef &quot;<xsl:value-of select="."/>&quot;: No heading found for anchor.</xsl:message>
        </xsl:if>
    </xsl:template>

    <!-- ************************ -->
    <!-- ** toc anchors (toc-) ** -->
    <!-- ************************ -->
    <xsl:template match="text:p[starts-with(.,$toc-prefix)]">
        <!-- Remove anchor paragraph if $keep-xref-anchors is false -->
        <xsl:if test="$keep-xref-anchors">
            <xsl:copy>
                <xsl:apply-templates select="@*|node()"/>
            </xsl:copy>
        </xsl:if>
        <xsl:variable name="part" select="substring-after(normalize-space(.), '-')"/>
        <xsl:variable name="content-path">
            <xsl:choose>
                <xsl:when test="$part='part1'">
                    <xsl:value-of select="$part1-content-path"/>
                </xsl:when>
                <xsl:when test="$part='part2'">
                    <xsl:value-of select="$part2-content-path"/>
                </xsl:when>
                <xsl:when test="$part='part3'">
                    <xsl:value-of select="$part3-content-path"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:message terminate="yes">*** Invalid part sepcified for toc: <xsl:value-of select="$part"/></xsl:message>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:if test="$content-path=''">
            <xsl:message terminate="yes" >*** No content path set for <xsl:value-of select="$part"/></xsl:message>
        </xsl:if>
        <text:section text:name="{normalize-space(.)}" text:protected="true">
            <xsl:apply-templates select="document($content-path)/office:document-content/office:body/office:text/text:table-of-content/text:index-body/*" mode="insert-toc">
                <xsl:with-param name="part" select="$part"/>
            </xsl:apply-templates>
        </text:section>
    </xsl:template>

    <xsl:template match="text:p" mode="insert-toc">
        <xsl:param name="part"/>
        <text:p>
            <xsl:apply-templates select="@*" mode="insert-toc">
                <xsl:with-param name="part" select="$part"/>
            </xsl:apply-templates>
            <xsl:attribute name="text:style-name"><xsl:value-of select="concat($part,'-',@text:style-name)"/></xsl:attribute>
            <xsl:choose>
                <xsl:when test="text:a or $toc-hyperlink-mode='none'">
                    <xsl:apply-templates select="node()" mode="insert-toc">
                        <xsl:with-param name="part" select="$part"/>
                    </xsl:apply-templates>
                </xsl:when>
                <xsl:otherwise>
                    <text:a xlink:type="simple">
                        <xsl:attribute name="xlink:href">
                            <xsl:variable name="appendix">
                                <xsl:choose>
                                    <xsl:when test="starts-with(.,'Appendix')">
                                        <xsl:value-of select="substring-before(substring(.,10),'. ')"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="substring-before(.,'. ')"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:variable>
                            <xsl:variable name="target" select="concat('#Appendix_',translate($appendix,'.','_'))"/>
                            <xsl:variable name="doc">
                                <xsl:choose>
                                    <xsl:when test="$toc-hyperlink-mode='adapt'">
                                        <xsl:call-template name="get-doc">
                                            <xsl:with-param name="part" select="$part"/>
                                        </xsl:call-template>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="''"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:variable>
                            <xsl:choose>
                                <xsl:when test="$doc!=''">
                                    <xsl:value-of select="concat('../',$doc,$target)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$target"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:attribute>
                        <xsl:apply-templates select="node()" mode="insert-toc">
                            <xsl:with-param name="part" select="$part"/>
                        </xsl:apply-templates>
                    </text:a>
                </xsl:otherwise>
            </xsl:choose>
        </text:p>
    </xsl:template>


    <xsl:template match="text:a" mode="insert-toc">
        <xsl:param name="part"/>
        <xsl:choose>
            <xsl:when test="$toc-hyperlink-mode='none'">
                <xsl:apply-templates select="node()" mode="insert-toc">
                    <xsl:with-param name="part" select="$part"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:when test="$toc-hyperlink-mode='adapt'">
                <xsl:variable name="doc">
                    <xsl:call-template name="get-doc">
                        <xsl:with-param name="part" select="$part"/>
                    </xsl:call-template>
                </xsl:variable>
                <text:a xlink:type="simple">
                    <xsl:attribute name="xlink:href">
                        <xsl:choose>
                            <xsl:when test="$doc!=''">
                                <xsl:value-of select="concat('../',$doc,@xlink:href)"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="@xlink:href"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                    <xsl:apply-templates select="node()" mode="insert-toc">
                        <xsl:with-param name="part" select="$part"/>
                    </xsl:apply-templates>
                </text:a>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="@*|node()" mode="insert-toc">
                        <xsl:with-param name="part" select="$part"/>
                    </xsl:apply-templates>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="get-doc">
        <xsl:param name="part"/>
        <xsl:choose>
            <xsl:when test="$part='part1'">
                <xsl:value-of select="$part1-toc-rel-path"/>
            </xsl:when>
            <xsl:when test="$part='part2'">
                <xsl:value-of select="$part2-toc-rel-path"/>
            </xsl:when>
            <xsl:when test="$part='part3'">
                <xsl:value-of select="$part3-toc-rel-path"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:message terminate="yes">*** Invalid part sepcified for toc: <xsl:value-of select="$part"/></xsl:message>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="@*|node()" mode="insert-toc">
        <xsl:param name="part"/>
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" mode="insert-toc">
                <xsl:with-param name="part" select="$part"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="office:automatic-styles">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
            <xsl:if test="$part1-content-path!=''">
                <xsl:call-template name="copy-toc-auto-styles">
                    <xsl:with-param name="part" select="'part1'"/>
                    <xsl:with-param name="content-path" select="$part1-content-path"/>
                </xsl:call-template>
            </xsl:if>
            <xsl:if test="$part2-content-path!=''">
                <xsl:call-template name="copy-toc-auto-styles">
                    <xsl:with-param name="part" select="'part2'"/>
                    <xsl:with-param name="content-path" select="$part2-content-path"/>
                </xsl:call-template>
            </xsl:if>
            <xsl:if test="$part3-content-path!=''">
                <xsl:call-template name="copy-toc-auto-styles">
                    <xsl:with-param name="part" select="'part3'"/>
                    <xsl:with-param name="content-path" select="$part3-content-path"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:copy>
    </xsl:template>

    <xsl:template name="copy-toc-auto-styles">
        <xsl:param name="part"/>
        <xsl:param name="content-path"/>
        <xsl:for-each select="document($content-path)/office:document-content/office:automatic-styles/style:style[@style:family='paragraph']">
            <xsl:if test="ancestor::office:document-content/office:body/office:text/text:table-of-content/text:index-body/text:p[@text:style-name=current()/@style:name]">
                <style:style>
                    <xsl:apply-templates select="@*"/>
                    <xsl:attribute name="style:name"><xsl:value-of select="concat($part,'-',@style:name)"/></xsl:attribute>
                    <xsl:apply-templates select="node()"/>
                </style:style>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <!-- ************************* -->
    <!-- ** existing references ** -->
    <!-- ************************* -->
    <xsl:template match="text:h/text:reference-mark-start|text:h/text:reference-mark-end">
        <xsl:if test="not($create-odf-references) or not(starts-with(@text:name,$attribute-prefix) or starts-with(@text:name,$property-prefix) or starts-with(@text:name,$element-prefix) or starts-with(@text:name,$datatype-prefix))">
            <xsl:copy>
                <xsl:apply-templates select="@*|node()"/>
            </xsl:copy>
        </xsl:if>
    </xsl:template>

    <!-- ******************************* -->
    <!-- ** existing cross references ** -->
    <!-- ******************************* -->
    <xsl:template match="text:p[@text:style-name='Attribute_20_List' or @text:style-name='Child_20_Element_20_List' or @text:style-name='Parent_20_Element_20_List' or @text:style-name='Attribute_20_Value_20_List']">
        <!-- Remove them if $keep-attr-elem-xrefs is false -->
        <xsl:if test="$keep-attr-elem-xrefs">
            <xsl:copy>
                <xsl:apply-templates select="@*|node()"/>
            </xsl:copy>
        </xsl:if>
    </xsl:template>

    <xsl:key name="style" match="style:style[@style:family='paragraph']" use="@style:name"/>

    <xsl:template match="text:p[@text:style-name='TODO' or key('style',@text:style-name)/@style:parent-style-name='TODO']">
        <!-- Remove them if $keep-attr-elem-xrefs is false -->
        <xsl:if test="$keep-todos">
            <xsl:copy>
                <xsl:apply-templates select="@*|node()"/>
            </xsl:copy>
        </xsl:if>
    </xsl:template>

    <!-- *************************** -->
    <!-- ** Bookmarks in headings ** -->
    <!-- *************************** -->
    <xsl:template match="text:h[text:bookmark[not(starts-with(@text:name,'__'))] or text:bookmark-start[not(starts-with(@text:name,'__'))]]" priority="1">
        <xsl:copy>
            <xsl:choose>
                <xsl:when test="$convert-bookmarks-and-hyperlinks">
                    <xsl:apply-templates select="@*"/>
                    <xsl:for-each select="text:bookmark[not(starts-with(@text:name,'__'))]|text:bookmark-start[not(starts-with(@text:name,'__'))]">
                        <xsl:sort select="@text:name"/>
                        <xsl:variable name="ref-name" select="@text:name"/>
                        <!-- xsl:message>Turning bookmark into reference mark: <xsl:value-of select="$ref-name"/>.</xsl:message -->
                        <text:reference-mark-start text:name="{$ref-name}"/>
                    </xsl:for-each>
                    <xsl:apply-templates select="node()" mode="convert_bookmarks"/>
                    <xsl:for-each select="text:bookmark[not(starts-with(@text:name,'__'))]|text:bookmark-start[not(starts-with(@text:name,'__'))]">
                        <xsl:sort select="@text:name" order="reverse"/>
                        <xsl:variable name="ref-name" select="@text:name"/>
                        <text:reference-mark-end text:name="{$ref-name}"/>
                    </xsl:for-each>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="@*|node()"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="text:bookmark[not(starts-with(@text:name,'__'))]" mode="convert_bookmarks">
        <xsl:apply-templates select="node()"/>
    </xsl:template>

    <xsl:template match="text:bookmark-start[not(starts-with(@text:name,'__'))]|text:bookmark-end[not(starts-with(@text:name,'__'))]" mode="convert_bookmarks"/>

    <xsl:template match="@*|node()" mode="convert_bookmarks">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="text:a[starts-with(@xlink:href,'#') and not(starts-with(@xlink:href,'#__') or contains(@xlink:href,'|outline'))]">
        <xsl:choose>
            <xsl:when test="$convert-bookmarks-and-hyperlinks">
                <xsl:variable name="ref-name" select="substring(@xlink:href,2)"/>
                <xsl:variable name="non-empty" select="string-length(normalize-space(.)) &gt; 0"/>
                <xsl:apply-templates select="node()"/>
                <xsl:choose>
                    <xsl:when test="$non-empty">
                        <!-- xsl:message>Turning hyperlink into reference: <xsl:value-of select="$ref-name"/>.</xsl:message -->
                        <xsl:text> </xsl:text><text:reference-ref text:ref-name="{$ref-name}" text:reference-format="chapter">?</text:reference-ref>
                    </xsl:when>
                    <xsl:otherwise>
                        <!-- xsl:message>Ignoring empty hyperlink: <xsl:value-of select="$ref-name"/>.</xsl:message -->
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="@*|node()"/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
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
        <!-- create ODF ref-mark elements if it is a valid element or attribute name -->
        <xsl:choose>
            <xsl:when test="not($create-odf-references) or starts-with($tag, 'odf:') or starts-with($tag, 'pkg:')">
                <!-- OWL -->
                <xsl:copy>
                    <xsl:apply-templates select="@*|node()"/>
                </xsl:copy>
            </xsl:when>
            <xsl:when test="starts-with($tag, '&lt;') and contains($tag,'&gt;')">
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                    <xsl:choose>
                        <xsl:when test="preceding::text:h[@text:outline-level=$attributes-heading-level]=$attributes-heading">
                            <xsl:variable name="fp" select="preceding::text:h[@text:outline-level='1'][last()]=$properties-heading"/>
                            <xsl:variable name="attr-name" select="normalize-space(preceding::text:h[@text:outline-level='2'][last()])"/>
                            <xsl:call-template name="create-element-ref-mark-start">
                                 <xsl:with-param name="tag" select="$tag"/>
                                 <xsl:with-param name="attr-name" select="$attr-name"/>
                                 <xsl:with-param name="fp" select="$fp"/>
                            </xsl:call-template>
                            <xsl:apply-templates select="node()"/>
                            <xsl:call-template name="create-element-ref-mark-end">
                                 <xsl:with-param name="tag" select="$tag"/>
                                 <xsl:with-param name="attr-name" select="$attr-name"/>
                                 <xsl:with-param name="fp" select="$fp"/>
                            </xsl:call-template>
                        </xsl:when>
                        <xsl:when test="preceding::text:h[@text:outline-level='3'][last()]='manifest:version'">
                            <xsl:variable name="attr-name" select="'manifest:version'"/>
                            <xsl:call-template name="create-element-ref-mark-start">
                                 <xsl:with-param name="tag" select="$tag"/>
                                 <xsl:with-param name="attr-name" select="$attr-name"/>
                                 <xsl:with-param name="fp" select="false()"/>
                            </xsl:call-template>
                            <xsl:apply-templates select="node()"/>
                            <xsl:call-template name="create-element-ref-mark-end">
                                 <xsl:with-param name="tag" select="$tag"/>
                                 <xsl:with-param name="attr-name" select="$attr-name"/>
                                 <xsl:with-param name="fp" select="false()"/>
                            </xsl:call-template>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:call-template name="create-element-ref-mark-start">
                                 <xsl:with-param name="tag" select="$tag"/>
                            </xsl:call-template>
                            <xsl:apply-templates select="node()"/>
                            <xsl:call-template name="create-element-ref-mark-end">
                                 <xsl:with-param name="tag" select="$tag"/>
                            </xsl:call-template>
                        </xsl:otherwise>
                    </xsl:choose>
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
                        <xsl:when test="$fp and not(/office:document-content/office:body/office:text/text:p[starts-with(.,$property-prefix) and (normalize-space(.)=concat($property-prefix,$attr-name) or starts-with(.,concat($property-prefix,$attr-name,'_')))])">
                            <xsl:message>Heading &quot;<xsl:value-of select="."/>&quot;: No attribute xref found for &quot;<xsl:value-of select="$attr-name"/>&quot; (property).</xsl:message>
                        </xsl:when>
                        <xsl:when test="not($fp) and not(/office:document-content/office:body/office:text/text:p[starts-with(.,$attribute-prefix) and (normalize-space(.)=concat($attribute-prefix,$attr-name) or starts-with(.,concat($attribute-prefix,$attr-name,'_')))])">
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

    <!-- **************************** -->
    <!-- ** Headings for datatypes ** -->
    <!-- **************************** -->
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

    <!-- ***************************************** -->
    <!-- ** Bibliograophy: Normative References ** -->
    <!-- ***************************************** -->
    <xsl:key name="bib-entry" match="text:bibliography-mark" use="@text:identifier"/>

    <xsl:template match="text:bibliography[@text:name='NormativeReferences']/text:index-body/text:p">
        <xsl:variable name="id" select="substring(substring-before(.,']'),2)"/>
        <xsl:if test="not(key('bib-entry',$id)/@text:custom5) or key('bib-entry',$id)/@text:custom5 != 'informative'">
<!--            <xsl:copy>
                <xsl:apply-templates select="@*|node()" mode="bib"/>
            </xsl:copy>
            -->
            <xsl:call-template name="bib-entry"/>
            <xsl:if test="$bib-messages">
                <xsl:message>Bibliographic entry [<xsl:value-of select="$id"/>] is normative.</xsl:message>
            </xsl:if>
        </xsl:if>
    </xsl:template>

    <!-- ********************************************* -->
    <!-- ** Bibliograophy: Non Normative References ** -->
    <!-- ********************************************* -->
    <xsl:template match="text:bibliography[@text:name='NonNormativeReferences']/text:index-body/text:p">
        <xsl:variable name="id" select="substring(substring-before(.,']'),2)"/>
        <xsl:if test="key('bib-entry',$id)/@text:custom5 = 'informative'">
<!--            <xsl:copy>
                <xsl:apply-templates select="@*|node()" mode="bib"/>
            </xsl:copy>-->
            <xsl:call-template name="bib-entry"/>
            <xsl:if test="$bib-messages">
                <xsl:message>Bibliographic entry [<xsl:value-of select="$id"/>] is non normative.</xsl:message>
            </xsl:if>
        </xsl:if>
    </xsl:template>

    <xsl:template name="bib-entry">
        <xsl:copy>
            <xsl:apply-templates select="@*" mode="bib"/>
            <xsl:for-each select="node()">
                <xsl:choose>
                    <xsl:when test="self::text() and contains(.,'http://')">
                        <xsl:variable name="before" select="substring-before(.,'http://')"/>
                        <xsl:variable name="tmp" select="substring(.,string-length($before)+1)"/>
                        <xsl:variable name="url">
                            <xsl:choose>
                                <xsl:when test="contains($tmp,',')">
                                    <xsl:value-of select="substring-before($tmp,',')"/>
                                </xsl:when>
                                <xsl:when test="substring($tmp,string-length($tmp))='.'">
                                    <xsl:value-of select="substring($tmp,1,string-length($tmp)-1)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$tmp"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="after" select ="substring($tmp,string-length($url)+1)"/>
                        <xsl:value-of select="$before"/>
                        <text:a xlink:type="simple" xlink:href="{$url}"><xsl:value-of select="$url"/></text:a>
                        <xsl:value-of select="$after"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:copy>
                            <xsl:apply-templates select="@*|node()"/>
                        </xsl:copy>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </xsl:copy>
    </xsl:template>


    <xsl:template match="@*|node()" mode="bib">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" mode="bib"/>
        </xsl:copy>
    </xsl:template>

    <!-- ********************************** -->
    <!-- ** Formula: Annotation sections ** -->
    <!-- ********************************** -->
    <xsl:template match="text:section[(@text:display='condition' and @text:condition='ooow:Note==0') or @text:display='none']">
        <!-- Ignore sections that are hidable by the "Note" condition -->
        <xsl:choose>
            <xsl:when test="$keep-annotations">
                <xsl:copy>
                    <xsl:apply-templates select="@*|node()"/>
                </xsl:copy>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="*" mode="check-annotations"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="text:p" mode="check-annotations">
        <xsl:variable name="style-name" select="@text:style-name"/>
        <xsl:choose>
            <xsl:when test="starts-with(.,'Test Case') or normalize-space(.)=''"/>
            <xsl:when test="$style-name='Rationale' or $style-name='Offset_20_Note' or $style-name='TODO'"/>
            <xsl:when test="//style:style[@style:name=$style-name and @style:family='paragraph' and @style:parent-style-name='Rationale']"/>
            <xsl:when test="//style:style[@style:name=$style-name and @style:family='paragraph' and @style:parent-style-name='Offset_20_Note']"/>
            <xsl:when test="//style:style[@style:name=$style-name and @style:family='paragraph' and @style:parent-style-name='TODO']"/>
            <xsl:when test="ancestor::text:section[@text:name='SectionTestCases']"/>
            <xsl:when test="ancestor::text:tracked-changes"/>
            <xsl:otherwise>
                <xsl:message>Normative Content found in section &quot;<xsl:value-of select="ancestor::text:section/@text:name"/>&quot;: <xsl:value-of select="substring(.,1,40)"/></xsl:message>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="*" mode="check-annotations"/>

    <xsl:template match="text:section">
        <xsl:message>Text sextions are not supported: <xsl_value-of select="@text:namd"/></xsl:message>
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>


    <xsl:template match="text:hidden-text[@text:condition='ooow:Note==0']">
        <!-- Ignore hidden text fields that are hidable by the "Note" condition -->
        <xsl:if test="$keep-annotations">
            <xsl:copy>
                <xsl:apply-templates select="@*|node()"/>
            </xsl:copy>
        </xsl:if>
    </xsl:template>



    <xsl:template name="create-element-ref-mark-start">
        <xsl:param name="tag"/>
        <xsl:param name="attr-name" select="''"/>
        <xsl:param name="fp" select="false()"/>
        <xsl:variable name="element-name" select="substring-after(substring-before($tag,'&gt;'),'&lt;')"/>
        <xsl:variable name="remainder" select="substring-after($tag,'&gt;')"/>
        
        <xsl:if test="$check-xref-anchors">
            <xsl:choose>
                <xsl:when test="$element-name='dsig:document-signatures' or $element-name='ds:Signature'"/>
                <xsl:when test="not(document($xref-schema-file)/rng:grammar/rng:element[@name=$element-name])">
                    <xsl:message>Heading: &quot;<xsl:value-of select="."/>&quot;: No element definition found in schema for element &quot;<xsl:value-of select="$element-name"/>&quot;.</xsl:message>
                </xsl:when>
                <xsl:when test="not(/office:document-content/office:body/office:text/text:p[starts-with(.,$element-prefix) and normalize-space(.)=concat($element-prefix,$element-name)])">
                    <xsl:message>Heading &quot;<xsl:value-of select="."/>&quot;: No element xref found for element &quot;<xsl:value-of select="$element-name"/>&quot;.</xsl:message>
                </xsl:when>
            </xsl:choose>
        </xsl:if>

        <xsl:if test="$create-odf-references">
            <xsl:variable name="ref-name">
                <xsl:choose>
                    <xsl:when test="string-length($attr-name)>0 and $fp">
                        <xsl:value-of select="concat($property-prefix,$attr-name,'_',$element-prefix,$element-name)"/>
                    </xsl:when>
                    <xsl:when test="string-length($attr-name)>0">
                        <xsl:value-of select="concat($attribute-prefix,$attr-name,'_',$element-prefix,$element-name)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="concat($element-prefix,$element-name)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <text:reference-mark-start text:name="{$ref-name}"/>
        </xsl:if>
        
        <xsl:if test="contains($remainder,'&lt;') and contains(substring-after($remainder,'&lt;'),'&gt;')">
            <xsl:call-template name="create-element-ref-mark-start">
                <xsl:with-param name="tag" select="$remainder"/>
                <xsl:with-param name="attr-name" select="$attr-name"/>
                <xsl:with-param name="fp" select="$fp"/>
            </xsl:call-template>
        </xsl:if>        
    </xsl:template>
    
    <xsl:template name="create-element-ref-mark-end">
        <xsl:param name="tag"/>
        <xsl:param name="attr-name" select="''"/>
        <xsl:param name="fp" select="false()"/>
        <xsl:variable name="element-name" select="substring-after(substring-before($tag,'&gt;'),'&lt;')"/>
        <xsl:variable name="remainder" select="substring-after($tag,'&gt;')"/>

        <xsl:if test="contains($remainder,'&lt;') and contains(substring-after($remainder,'&lt;'),'&gt;')">
            <xsl:call-template name="create-element-ref-mark-end">
                <xsl:with-param name="tag" select="$remainder"/>
                <xsl:with-param name="attr-name" select="$attr-name"/>
                <xsl:with-param name="fp" select="$fp"/>
            </xsl:call-template>
        </xsl:if>

        <xsl:if test="$create-odf-references">
            <xsl:variable name="ref-name">
                <xsl:choose>
                    <xsl:when test="string-length($attr-name)>0 and $fp">
                        <xsl:value-of select="concat($property-prefix,$attr-name,'_',$element-prefix,$element-name)"/>
                    </xsl:when>
                    <xsl:when test="string-length($attr-name)>0">
                        <xsl:value-of select="concat($attribute-prefix,$attr-name,'_',$element-prefix,$element-name)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="concat($element-prefix,$element-name)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <text:reference-mark-end text:name="{$ref-name}"/>
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
            <xsl:when test="@name='manifest:algorithm'">
                <xsl:call-template name="create-attr-list"/>
                <xsl:message>Element <xsl:value-of select="@name"/>: No child element info added (element may have any content).</xsl:message>
            </xsl:when>
            <xsl:when test="rng:element/rng:anyName">
                <!-- arbitrary content -->
                <xsl:message>Element <xsl:value-of select="@name"/>: No attribute and child element info added (element may have any content).</xsl:message>
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
        <xsl:variable name="elem-name" select="@name"/>
        <text:p text:style-name="Attribute_20_List">
            <xsl:text>The </xsl:text>
            <text:span text:style-name="Element">
                <xsl:text>&lt;</xsl:text>
                <xsl:value-of select="$elem-name"/>
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
                            <xsl:variable name="aname" select="@name"/>
                            <xsl:variable name="ref-name">
                                <xsl:choose>
                                    <xsl:when test="$fp and /office:document-content/office:body/office:text/text:p[starts-with(.,concat($property-prefix,$aname,'_')) and contains(concat(normalize-space(.),'_'),concat($element-prefix,$elem-name,'_'))]"><xsl:value-of select="concat($property-prefix,$aname,'_',$element-prefix,$elem-name)"/></xsl:when>
                                    <xsl:when test="not($fp) and /office:document-content/office:body/office:text/text:p[starts-with(.,concat($attribute-prefix,$aname,'_')) and contains(concat(normalize-space(.),'_'),concat($element-prefix,$elem-name,'_'))]"><xsl:value-of select="concat($attribute-prefix,$aname,'_',$element-prefix,$elem-name)"/></xsl:when>
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
                        <xsl:text> element has character data content. Character data content is only permitted if a </xsl:text>
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
                        <xsl:text> element has character data content, or depending on the value of the </xsl:text>
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
                        <xsl:text> element has character data content.</xsl:text>
                    </text:p>
                </xsl:when>
                <xsl:when test="rng:text[@condition='zeroOrMore/choice'or @condition='zeroOrMore/choice/choice']">
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
                            <xsl:with-param name="elem-name" select="@name"/>
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
                            ($attr-name='text:display' and ($fp or contains($element-list,'text:section'))) or
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
            <xsl:when test="$attr-name='text:reference-format'">
                <xsl:apply-templates select="ancestor::rng:grammar/rng:element/rng:attribute[@name=$attr-name]/*" mode="attr-value">
                    <xsl:with-param name="attr-name" select="$attr-name"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:when test="ancestor::rng:grammar/rng:element[((starts-with(@name,'style:') and contains(@name,'-properties'))=$fp) and (not($element-list) or contains($element-list,concat('_',$element-prefix,@name,'_'))) and count(rng:attribute[@name=$attr-name])>1]">
                <xsl:message>Attribute <xsl:value-of select="$attr-name"/>: Multiple attribute definitions do exist for at least one element.</xsl:message>
            </xsl:when>
            <xsl:when test="$attr-name='xlink:show' or
                            $attr-name='xlink:actuate'">
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
                <xsl:apply-templates select="ancestor::rng:grammar/rng:element[((starts-with(@name,'style:') and contains(@name,'-properties'))=$fp) and starts-with($element-list,concat('_',$element-prefix,@name,'_'))]/rng:attribute[@name=$attr-name]/*" mode="attr-value">
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
            <xsl:when test="/office:document-content/office:body/office:text/text:p[.=concat($datatype-prefix,$name)]">
                <text:p text:style-name="Attribute_20_Value_20_List">
                    <xsl:text>The </xsl:text>
                    <text:span text:style-name="Attribute">
                        <xsl:value-of select="$attr-name"/>
                    </text:span>
                    <xsl:text> attribute has the data type </xsl:text>
                    <xsl:call-template name="print-datatype">
                        <xsl:with-param name="name" select="$name"/>
                        <xsl:with-param name="attr-name" select="$attr-name"/>
                    </xsl:call-template>
                    <xsl:text>.</xsl:text>
                </text:p>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="document($xref-schema-file)/rng:grammar/rng:define[@name=$name]" mode="attr-value">
                    <xsl:with-param name="attr-name" select="$attr-name"/>
                </xsl:apply-templates>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="rng:define" mode="attr-value">
        <xsl:param name="attr-name"/>
        <xsl:if test="count(*)>1">
            <xsl:message>*** Attribute <xsl:value-of select="$attr-name"/>: Referenced define <xsl:value-of select="@name"/> has multiple child elements.</xsl:message>
        </xsl:if>
        <xsl:apply-templates select="*" mode="attr-value">
            <xsl:with-param name="attr-name" select="$attr-name"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="rng:data" mode="attr-value">
        <xsl:param name="attr-name"/>
        <xsl:variable name="type" select="@type"/>
        <xsl:call-template name="new-line"/>
        <xsl:choose>
            <!-- A plain data type is only valid if there is a datatype-* in the
                 specification text and if there isn't a same named define -->
            <xsl:when test="/office:document-content/office:body/office:text/text:p[.=concat($datatype-prefix,$type)] and not(document($xref-schema-file)/rng:grammar/rng:define[@name=$type])">
                <text:p text:style-name="Attribute_20_Value_20_List">
                    <xsl:text>The </xsl:text>
                    <text:span text:style-name="Attribute">
                        <xsl:value-of select="$attr-name"/>
                    </text:span>
                    <xsl:text> attribute has the data type </xsl:text>
                    <xsl:call-template name="print-datatype">
                        <xsl:with-param name="name" select="$type"/>
                        <xsl:with-param name="attr-name" select="$attr-name"/>
                    </xsl:call-template>
                    <xsl:text>.</xsl:text>
                </text:p>
            </xsl:when>
            <xsl:otherwise>
                <xsl:message>*** Attribute <xsl:value-of select="$attr-name"/>: Unrecognized value element: <xsl:value-of select="name(.)"/></xsl:message>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="rng:choice[count(*)=1]" mode="attr-value">
        <xsl:param name="attr-name"/>
        <xsl:message>*** Attribute <xsl:value-of select="$attr-name"/>: &lt;choice&gt; with only one child element.</xsl:message>
        <xsl:apply-templates select="*" mode="attr-value">
            <xsl:with-param name="attr-name" select="$attr-name"/>
        </xsl:apply-templates>
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

    <xsl:template match="rng:attribute[@name='text:reference-format']/rng:choice" mode="attr-value">
        <xsl:param name="attr-name"/>
        <xsl:call-template name="new-line"/>
        <text:p text:style-name="Attribute_20_Value_20_List">
            <xsl:variable name="elem-name" select="ancestor::rng:element/@name"/>
        <xsl:text>For </xsl:text>
        <text:span text:style-name="Element"><xsl:text>&lt;</xsl:text><xsl:value-of select="$elem-name"/><xsl:text>&gt;</xsl:text></text:span><xsl:text> </xsl:text><text:reference-ref text:ref-name="{concat($element-prefix,$elem-name)}" text:reference-format="chapter">?</text:reference-ref>
        <xsl:text> elements, the values of the </xsl:text>
        <text:span text:style-name="Attribute">
             <xsl:value-of select="$attr-name"/>
        </text:span>
        <xsl:if test="*[not(self::rng:value or self::rng:ref)]"><xsl:message>*** Attribute <xsl:value-of select="$attr-name"/>: unexpected child elements</xsl:message></xsl:if>
        <xsl:text> attribute are </xsl:text>
            <xsl:variable name="has-values" select="count(rng:value)>0"/>
            <xsl:for-each select="rng:ref">
                <xsl:variable name="name" select="@name"/>
                <xsl:if test="document($xref-schema-file)/rng:grammar/rng:define[@name=$name]/*[not(self::rng:choice)]"><xsl:message>*** Attribute <xsl:value-of select="$attr-name"/>: unexpected child elements in referenced define</xsl:message></xsl:if>
                <xsl:for-each select="document($xref-schema-file)/rng:grammar/rng:define[@name=$name]/rng:choice/*">
                    <xsl:choose>
                        <xsl:when test="position() = 1"/>
                        <xsl:when test="not($has-values) and position() = last()">
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
            <xsl:for-each select="rng:value">
                <xsl:choose>
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

    <xsl:template match="rng:choice[rng:list[rng:oneOrMore and count(*)=1] and count(*)=count(rng:value)+1]" mode="attr-value">
        <xsl:param name="attr-name"/>
        <xsl:call-template name="new-line"/>
        <text:p text:style-name="Attribute_20_Value_20_List">
            <xsl:call-template name="print-the-values">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:call-template>
            <xsl:for-each select="rng:value">
                <xsl:apply-templates select="." mode="individual-value">
                    <xsl:with-param name="attr-name" select="$attr-name"/>
                </xsl:apply-templates>
                <xsl:text>, </xsl:text>
            </xsl:for-each>
            <xsl:text> or white space separated non-empty lists of </xsl:text>
            <xsl:apply-templates select="rng:list/rng:oneOrMore/*" mode="attr-list-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
            <xsl:text>.</xsl:text>
            <xsl:text>.</xsl:text>
        </text:p>
    </xsl:template>

    <xsl:template match="rng:choice[rng:list[count(*)=2 and count(rng:choice)=2] and count(*)=count(rng:value)+1]" mode="attr-value">
        <xsl:param name="attr-name"/>
        <xsl:call-template name="new-line"/>
        <text:p text:style-name="Attribute_20_Value_20_List">
            <xsl:call-template name="print-the-values">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:call-template>
            <xsl:for-each select="rng:value">
                <xsl:apply-templates select="." mode="individual-value">
                    <xsl:with-param name="attr-name" select="$attr-name"/>
                </xsl:apply-templates>
                <xsl:text>, </xsl:text>
            </xsl:for-each>
            <xsl:text>or two white space separated values. The first of these values </xsl:text>
            <xsl:apply-templates select="rng:list/*[1]" mode="single-attr-list-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
            <xsl:text>. The second of these values </xsl:text>
            <xsl:apply-templates select="rng:list/*[2]" mode="single-attr-list-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
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

    <xsl:template match="rng:list[rng:zeroOrMore and count(*)=1]" mode="attr-value">
        <xsl:param name="attr-name"/>
        <xsl:call-template name="new-line"/>
        <text:p text:style-name="Attribute_20_Value_20_List">
            <xsl:call-template name="print-the-value-is">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:call-template>
            <xsl:text>a white space separated lists of </xsl:text>
            <xsl:apply-templates select="*/*" mode="attr-list-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
            <xsl:text>, including the empty list.</xsl:text>
        </text:p>
    </xsl:template>

    <xsl:template match="rng:list[rng:oneOrMore and count(*)=1]" mode="attr-value">
        <xsl:param name="attr-name"/>
        <xsl:call-template name="new-line"/>
        <text:p text:style-name="Attribute_20_Value_20_List">
            <xsl:call-template name="print-the-value-is">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:call-template>
            <xsl:text>a white space separated non-empty lists of </xsl:text>
            <xsl:apply-templates select="*/*" mode="attr-list-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
            <xsl:text>.</xsl:text>
        </text:p>
    </xsl:template>

    <xsl:template match="rng:list[count(*)=2 and count(rng:ref|rng:choice)=2]" mode="attr-value">
        <xsl:param name="attr-name"/>
        <xsl:call-template name="new-line"/>
        <text:p text:style-name="Attribute_20_Value_20_List">
            <xsl:call-template name="print-the-attribute-has">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:call-template>
            <xsl:text>two white space separated values. The first value </xsl:text>
            <xsl:apply-templates select="*[1]" mode="single-attr-list-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
            <xsl:text>. The second value </xsl:text>
            <xsl:apply-templates select="*[2]" mode="single-attr-list-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
            <xsl:text>.</xsl:text>
        </text:p>
    </xsl:template>

    <xsl:template match="rng:list[count(*)=2 and count(rng:choice)=1 and count(rng:optional/rng:ref)=1]" mode="attr-value">
        <xsl:param name="attr-name"/>
        <xsl:call-template name="new-line"/>
        <text:p text:style-name="Attribute_20_Value_20_List">
            <xsl:call-template name="print-the-attribute-has">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:call-template>
            <xsl:text>one or two white space separated values. The first values </xsl:text>
            <xsl:apply-templates select="*[1]" mode="single-attr-list-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
            <xsl:text>. The second value </xsl:text>
            <xsl:apply-templates select="rng:optional/*" mode="single-attr-list-value">
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
            <xsl:text>four white space separated values of type </xsl:text>
            <xsl:call-template name="print-datatype">
                <xsl:with-param name="name" select="'integer'"/>
                <xsl:with-param name="attr-name" select="$attr-name"/>
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
            <xsl:text>three white space separated values of type </xsl:text>
            <xsl:call-template name="print-datatype">
                <xsl:with-param name="name" select="'positiveLength'"/>
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:call-template>
            <xsl:text>.</xsl:text>
        </text:p>
    </xsl:template>

    <xsl:template match="rng:element[@name='style:background-image']/rng:attribute[@name='style:position']/rng:choice" mode="attr-value">
        <xsl:param name="attr-name"/>
        <xsl:if test="not(count(rng:list)=2)">
            <xsl:message>Unexpected content <xsl:value-of select="$attr-name"/></xsl:message>
        </xsl:if>
        <xsl:call-template name="new-line"/>
        <text:p text:style-name="Attribute_20_Value_20_List">
            <xsl:call-template name="print-the-values">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:call-template>
            <xsl:for-each select="rng:value">
                <xsl:apply-templates select="." mode="individual-value">
                    <xsl:with-param name="attr-name" select="$attr-name"/>
                </xsl:apply-templates>
                <xsl:text>, </xsl:text>
            </xsl:for-each>
            <xsl:text>or two white space separated values, that may appear in any order. One of these values </xsl:text>
            <xsl:apply-templates select="rng:list[1]/rng:ref[1]" mode="single-attr-list-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
            <xsl:text>. The other value </xsl:text>
            <xsl:apply-templates select="rng:list[1]/rng:ref[2]" mode="single-attr-list-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
            <xsl:text>.</xsl:text>
        </text:p>
    </xsl:template>


    <xsl:template match="rng:attribute[@name='style:mirror']/rng:choice" mode="attr-value">
        <xsl:param name="attr-name"/>
        <xsl:if test="not(count(*)=5 and count(rng:value)=2 and count(rng:ref[@name='horizontal-mirror'])=1 and count(rng:list)=2)">
            <xsl:message>Unexpected content <xsl:value-of select="$attr-name"/></xsl:message>
        </xsl:if>
        <xsl:call-template name="new-line"/>
        <text:p text:style-name="Attribute_20_Value_20_List">
            <xsl:call-template name="print-the-values">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:call-template>
            <xsl:for-each select="rng:value|ancestor::rng:grammar/rng:element/rng:define[@name='horizontal-mirror']/rng:choice/rng:value">
                <xsl:apply-templates select="." mode="individual-value">
                    <xsl:with-param name="attr-name" select="$attr-name"/>
                </xsl:apply-templates>
                <xsl:text>, </xsl:text>
            </xsl:for-each>
            <xsl:text>or two white space separated values, that may appear in any order. One of these values is always </xsl:text>
            <xsl:apply-templates select="rng:list[1]/rng:value" mode="individual-value">
                <xsl:with-param name="attr-name" select="$attr-name"/>
            </xsl:apply-templates>
            <xsl:text>. The other value </xsl:text>
            <xsl:apply-templates select="rng:list[1]/rng:ref" mode="single-attr-list-value">
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
            <xsl:text>one, two or three white space separated </xsl:text>
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
                <xsl:with-param name="attr-name" select="$attr-name"/>
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
            <xsl:when test="/office:document-content/office:body/office:text/text:p[.=concat($datatype-prefix,$name)]">
                <xsl:text>values of type </xsl:text>
                <xsl:call-template name="print-datatype">
                    <xsl:with-param name="name" select="$name"/>
                    <xsl:with-param name="attr-name" select="$attr-name"/>
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

    <xsl:template match="rng:ref" mode="single-attr-list-value">
        <xsl:param name="attr-name"/>
        <xsl:variable name="name" select="@name"/>
        <xsl:choose>
            <xsl:when test="/office:document-content/office:body/office:text/text:p[.=concat($datatype-prefix,$name)]">
                <xsl:text>is of type </xsl:text>
                <xsl:call-template name="print-datatype">
                    <xsl:with-param name="name" select="@name"/>
                    <xsl:with-param name="attr-name" select="$attr-name"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="document($xref-schema-file)/rng:grammar/rng:define[@name=$name]/*" mode="single-attr-list-value">
                    <xsl:with-param name="attr-name" select="$attr-name"/>
                </xsl:apply-templates>
            </xsl:otherwise>
         </xsl:choose>
    </xsl:template>

    <xsl:template match="rng:data[(@type='double' or @type='decimal') and count(rng:param)=2 and rng:param[@name='minInclusive'] and rng:param[@name='maxInclusive']]" mode="single-attr-list-value">
        <xsl:param name="attr-name"/>
        <xsl:text>is </xsl:text>
        <xsl:apply-templates select="self::*" mode="individual-value">
            <xsl:with-param name="attr-name" select="$attr-name"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="rng:choice[count(*)=count(rng:value)]" mode="single-attr-list-value">
        <xsl:param name="attr-name"/>
        <xsl:text>is one of: </xsl:text>
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

    <xsl:template match="rng:choice[rng:ref[@name='percent'] and count(*)=(count(rng:value)+1)]" mode="single-attr-list-value">
        <xsl:param name="attr-name"/>
        <xsl:text>is of type </xsl:text>
        <xsl:call-template name="print-datatype">
            <xsl:with-param name="name" select="'percent'"/>
            <xsl:with-param name="attr-name" select="$attr-name"/>
        </xsl:call-template>
        <xsl:text>, or is one of: </xsl:text>
        <xsl:for-each select="rng:value">
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

    <xsl:template match="*" mode="single-attr-list-value">
        <xsl:param name="attr-name"/>
        <xsl:message>*** Attribute <xsl:value-of select="$attr-name"/>: Unrecognized value element in fixed-length list: <xsl:value-of select="name(.)"/></xsl:message>
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
            <xsl:when test="/office:document-content/office:body/office:text/text:p[.=concat($datatype-prefix,$name)]">
                <xsl:text>a value of type </xsl:text>
                <xsl:call-template name="print-datatype">
                    <xsl:with-param name="name" select="@name"/>
                    <xsl:with-param name="attr-name" select="$attr-name"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="document($xref-schema-file)/rng:grammar/rng:define[@name=$name]/*" mode="individual-value">
                    <xsl:with-param name="attr-name" select="$attr-name"/>
                </xsl:apply-templates>
            </xsl:otherwise>
         </xsl:choose>
    </xsl:template>

    <xsl:template match="rng:data[(@type='double' or @type='decimal') and count(rng:param)=2 and rng:param[@name='minInclusive'] and rng:param[@name='maxInclusive']]" mode="individual-value">
        <xsl:param name="attr-name"/>
        <xsl:text>a value of type </xsl:text>
        <xsl:call-template name="print-datatype">
            <xsl:with-param name="name" select="@type"/>
            <xsl:with-param name="attr-name" select="$attr-name"/>
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

    <xsl:template match="rng:data[@type='decimal' and count(rng:param)=1 and rng:param[@name='minInclusive']='0.0']" mode="individual-value">
        <xsl:param name="attr-name"/>
        <xsl:text>a non negative value of type </xsl:text>
        <xsl:call-template name="print-datatype">
            <xsl:with-param name="name" select="@type"/>
            <xsl:with-param name="attr-name" select="$attr-name"/>
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

    <xsl:template name="print-the-value-is">
        <xsl:param name="attr-name"/>
        <xsl:text>The value of the </xsl:text>
        <text:span text:style-name="Attribute">
             <xsl:value-of select="$attr-name"/>
        </text:span>
        <xsl:text> attribute is </xsl:text>
    </xsl:template>

    <xsl:template name="print-the-attribute-has">
        <xsl:param name="attr-name"/>
        <xsl:text>The </xsl:text>
        <text:span text:style-name="Attribute">
             <xsl:value-of select="$attr-name"/>
        </text:span>
        <xsl:text> attribute has </xsl:text>
    </xsl:template>

    <xsl:template name="print-datatype">
        <xsl:param name="name"/>
        <xsl:param name="attr-name" select="'[unknown]'"/>
        <xsl:if test="string-length($name)=0">
            <xsl:message>*** Attribute <xsl:value-of select="$attr-name"/>: Empty datatype name.</xsl:message>
        </xsl:if>
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
                        <xsl:text> is usable within the following element: </xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text> is usable within the following elements: </xsl:text>
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
