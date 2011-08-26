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

<!-- This stylesheet creates an content.xml for a spreadsheet document that -->
<!-- lists all ODF elements and attributes                                  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                 
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
            xmlns:smil="urn:oasis:names:tc:opendocument:xmlns:smil-compatible:1.0"
                xmlns="http://relaxng.org/ns/structure/1.0" version="1.0">    
    <xsl:output method="xml" indent="yes"/>    
        
        
    <!-- ********** -->
    <!-- ** root ** -->
    <!-- ********** -->
    <xsl:template match="/rng:grammar">        
        <xsl:variable name="root" select="."/>
        <office:document-content xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0" xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0" xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0" xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0" xmlns:draw="urn:oasis:names:tc:opendocument:xmlns:drawing:1.0" xmlns:fo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:meta="urn:oasis:names:tc:opendocument:xmlns:meta:1.0" xmlns:number="urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0" xmlns:presentation="urn:oasis:names:tc:opendocument:xmlns:presentation:1.0" xmlns:svg="urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0" xmlns:chart="urn:oasis:names:tc:opendocument:xmlns:chart:1.0" xmlns:dr3d="urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0" xmlns:math="http://www.w3.org/1998/Math/MathML" xmlns:form="urn:oasis:names:tc:opendocument:xmlns:form:1.0" xmlns:script="urn:oasis:names:tc:opendocument:xmlns:script:1.0" xmlns:ooo="http://openoffice.org/2004/office" xmlns:ooow="http://openoffice.org/2004/writer" xmlns:oooc="http://openoffice.org/2004/calc" xmlns:dom="http://www.w3.org/2001/xml-events" xmlns:xforms="http://www.w3.org/2002/xforms" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" office:version="1.0">
            <office:scripts/>
            <office:font-face-decls>
                <style:font-face style:name="Albany" svg:font-family="Albany" style:font-family-generic="swiss" style:font-pitch="variable"/>
                <style:font-face style:name="Andale Sans UI" svg:font-family="&apos;Andale Sans UI&apos;" style:font-family-generic="system" style:font-pitch="variable"/>
                <style:font-face style:name="Lucidasans" svg:font-family="Lucidasans" style:font-family-generic="system" style:font-pitch="variable"/>
            </office:font-face-decls>
            <office:automatic-styles>
                <style:style style:name="co1" style:family="table-column">
                    <style:table-column-properties fo:break-before="auto" style:column-width="6.4cm"/>
                </style:style>
                <style:style style:name="co2" style:family="table-column">
                    <style:table-column-properties fo:break-before="auto" style:column-width="7.4cm"/>
                </style:style>
                <style:style style:name="co3" style:family="table-column">
                    <style:table-column-properties fo:break-before="auto" style:column-width="7.6cm"/>
                </style:style>
                <style:style style:name="co4" style:family="table-column">
                    <style:table-column-properties fo:break-before="auto" style:column-width="7.1cm"/>
                </style:style>
                <style:style style:name="co5" style:family="table-column">
                    <style:table-column-properties fo:break-before="auto" style:column-width="2.267cm"/>
                </style:style>
                <style:style style:name="ro1" style:family="table-row">
                    <style:table-row-properties style:row-height="0.681cm" fo:break-before="auto" style:use-optimal-row-height="true"/>
                </style:style>
                <style:style style:name="ro2" style:family="table-row">
                    <style:table-row-properties style:row-height="0.427cm" fo:break-before="auto" style:use-optimal-row-height="true"/>
                </style:style>
                <style:style style:name="ta1" style:family="table" style:master-page-name="Default">
                    <style:table-properties table:display="true" style:writing-mode="lr-tb"/>
                </style:style>
            </office:automatic-styles>
            <office:body>
                <office:spreadsheet>
                    <table:calculation-settings>
                        <table:null-date table:date-value="1900-01-01"/>
                    </table:calculation-settings>
                    <table:table table:name="Sheet1" table:style-name="ta1" table:print="false">
                        <office:forms form:automatic-focus="false" form:apply-design-mode="false"/>
                        <table:table-column table:style-name="co1" table:default-cell-style-name="Heading"/>
                        <table:table-column table:style-name="co2" table:default-cell-style-name="Heading"/>
                        <table:table-column table:style-name="co3" table:default-cell-style-name="Heading"/>
                        <table:table-column table:style-name="co4" table:default-cell-style-name="Heading"/>
                        <table:table-row table:style-name="ro1">
                            <table:table-cell office:value-type="string">
                                <text:p>Element</text:p>
                            </table:table-cell>
                            <table:table-cell office:value-type="string">
                                <text:p>Attribute</text:p>
                            </table:table-cell>
                            <table:table-cell office:value-type="string">
                                <text:p>Datatype</text:p>
                            </table:table-cell>
                            <table:table-cell office:value-type="string">
                                <text:p>Default Value</text:p>
                            </table:table-cell>
                        </table:table-row>
                        
                        <!-- select all <element> nodes in the file or in included files -->
                        <xsl:for-each select="//rng:element">                
                            <xsl:sort select="@name"/>
                            <xsl:call-template name="create-attr-list"/>
                        </xsl:for-each> 
                        
                    </table:table>
                </office:spreadsheet>        
            </office:body>
        </office:document-content>
   </xsl:template>    
        
    <xsl:template name="create-attr-list">
        <xsl:variable name="element-name" select="@name"/>
        <xsl:for-each select="rng:attribute">
            <xsl:sort select="@name"/>
            <table:table-row table:style-name="ro2">
                <table:table-cell table:style-name="Default" office:value-type="string">
                    <text:p>
                        <xsl:value-of select="$element-name"/>
                    </text:p>
                </table:table-cell>
                <table:table-cell table:style-name="Default" office:value-type="string">
                    <text:p>
                        <xsl:value-of select="@name"/>
                    </text:p>
                </table:table-cell>
                <table:table-cell table:style-name="Default" office:value-type="string">
                    <text:p>
                        <xsl:apply-templates select="*" mode="print-elem"/>
                    </text:p>
                </table:table-cell>
                <table:table-cell table:style-name="Default" office:value-type="string">
                    <text:p>
                        <xsl:if test="@a:defaultValue">
                            <xsl:text>&quot;</xsl:text>
                            <xsl:value-of select="@a:defaultValue"/>
                            <xsl:text>&quot;</xsl:text>
                        </xsl:if>
                    </text:p>
                </table:table-cell>
            </table:table-row>
        </xsl:for-each>   
    </xsl:template>

    <xsl:template match="*" mode="print-elem">
        <xsl:text>&lt;</xsl:text><xsl:value-of select="name(.)"/>
        <xsl:for-each select="@*">
            <xsl:text> </xsl:text><xsl:value-of select="name(.)"/><xsl:text>=&quot;</xsl:text><xsl:value-of select="."/><xsl:text>&quot;</xsl:text>
        </xsl:for-each>
        <xsl:variable name="has-nodes" select="count(node()) > 0"/>
        <xsl:if test="not($has-nodes)">
            <xsl:text>/</xsl:text>
        </xsl:if>
        <xsl:text>&gt;</xsl:text>
        <xsl:if test="$has-nodes">
            <xsl:apply-templates mode="print-elem"/>
            <xsl:text>&lt;/</xsl:text><xsl:value-of select="name(.)"/><xsl:text>&gt;</xsl:text>
        </xsl:if>
    </xsl:template>

    <xsl:template match="text()" mode="print-elem">
        <xsl:value-of select="."/>
    </xsl:template>
    
    
</xsl:stylesheet>
