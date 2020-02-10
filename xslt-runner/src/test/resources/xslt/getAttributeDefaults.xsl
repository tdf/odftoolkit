<?xml version="1.0" encoding="UTF-8"?>
<!--

  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER

  Copyright 2000, 2010 Oracle and/or its affiliates.
  Copyright 2009 IBM. All rights reserved.

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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:chart="urn:oasis:names:tc:opendocument:xmlns:chart:1.0" xmlns:config="urn:oasis:names:tc:opendocument:xmlns:config:1.0" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dom="http://www.w3.org/2001/xml-events" xmlns:dr3d="urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0" xmlns:draw="urn:oasis:names:tc:opendocument:xmlns:drawing:1.0" xmlns:fo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0" xmlns:form="urn:oasis:names:tc:opendocument:xmlns:form:1.0" xmlns:math="http://www.w3.org/1998/Math/MathML" xmlns:meta="urn:oasis:names:tc:opendocument:xmlns:meta:1.0" xmlns:number="urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0" xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0" xmlns:ooo="http://openoffice.org/2004/office" xmlns:oooc="http://openoffice.org/2004/calc" xmlns:ooow="http://openoffice.org/2004/writer" xmlns:script="urn:oasis:names:tc:opendocument:xmlns:script:1.0" xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0" xmlns:svg="urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0" xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0" xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0" xmlns:xforms="http://www.w3.org/2002/xforms" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xt="http://www.jclark.com/xt" xmlns:common="http://exslt.org/common" xmlns:xalan="http://xml.apache.org/xalan" exclude-result-prefixes="chart config dc dom dr3d draw fo form math meta number office ooo oooc ooow script style svg table text xforms xlink xsd xsi xt common xalan" xmlns="http://www.w3.org/1999/xhtml">
<!-- Extracting default values from the ODF 1.2 part1 specification 
		Version 1.2.1 by Svante.Schubert@ gmail.com  -->
    <xsl:output method="xml" encoding="UTF-8" indent="yes" omit-xml-declaration="no" />
    
	<!-- ********************************************************** -->
	<!-- *** Get the default attribute values for ODF elements  *** -->
	<!-- ********************************************************** -->
    <xsl:template match="/">
        <config xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0" xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0" xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0" xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0" xmlns:draw="urn:oasis:names:tc:opendocument:xmlns:drawing:1.0" xmlns:fo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:meta="urn:oasis:names:tc:opendocument:xmlns:meta:1.0" xmlns:number="urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0" xmlns:svg="urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0" xmlns:chart="urn:oasis:names:tc:opendocument:xmlns:chart:1.0" xmlns:dr3d="urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0" xmlns:math="http://www.w3.org/1998/Math/MathML" xmlns:form="urn:oasis:names:tc:opendocument:xmlns:form:1.0" xmlns:script="urn:oasis:names:tc:opendocument:xmlns:script:1.0" xmlns:ooo="http://openoffice.org/2004/office" xmlns:ooow="http://openoffice.org/2004/writer" xmlns:oooc="http://openoffice.org/2004/calc" xmlns:dom="http://www.w3.org/2001/xml-events" xmlns:xforms="http://www.w3.org/2002/xforms" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:rpt="http://openoffice.org/2005/report" xmlns:of="urn:oasis:names:tc:opendocument:xmlns:of:1.2" xmlns:rdfa="http://docs.oasis-open.org/opendocument/meta/rdfa#" office:version="1.2">
            <attributes>
                <xsl:apply-templates />
            </attributes>
        </config>
    </xsl:template>

    <!-- for every stylable text element with the indicator of a default value (ie. the style 'Default_20_Value')...  -->
    <xsl:template match="*[@text:style-name='Default_20_Value']">
        <xsl:call-template name="get-default-value-declaration">
            <xsl:with-param name="attributeName">
                <!-- the attribute name is being gathered, by traversing backwards in the document to the previous attribute declaration (found in a heading) -->
                <xsl:apply-templates select="preceding::text:h[1]" mode="get-attribute-name"/>
            </xsl:with-param>
        </xsl:call-template>

    </xsl:template>


        <!-- starting from an attribute description where defaults exist..  -->
    <xsl:template match="text:h" mode="get-attribute-name">
        <!-- Within the header is a reference token, which gives clues about the default value's attribute for instance: 
            <text:reference-mark-start text:name="attribute-table:number-columns-repeated_element-table:table-cell"/>            
        -->
        <xsl:variable name="referenceToken" select="text:reference-mark-start/@text:name[contains(.,'attribute-')]"/>
        <xsl:choose>
            <xsl:when test="contains($referenceToken, '_element')">
                <!-- the name of the attribute -->
                <xsl:value-of select="substring-after(substring-before($referenceToken, '_element'), 'attribute-')"/>
            </xsl:when>
            <xsl:otherwise>
                <!-- the name of the attribute -->
                <xsl:value-of select="substring-after($referenceToken, 'attribute-')"/>
            </xsl:otherwise>
        </xsl:choose>        
    </xsl:template>

    <xsl:template name="get-default-value-declaration">
        <xsl:param name="attributeName" />

        <xsl:variable name="defaultValue">
                <!-- get the default Value (ie. the styleable text element with the style 'Attribute_20_Value')  -->
            <xsl:variable name="defaultValueElement" select="*[@text:style-name='Attribute_20_Value' or @text:style-name='Attribute_20_Value_20_Instance']"/>
            <xsl:choose>
                <xsl:when test="normalize-space($defaultValueElement) != ''">
                    <xsl:value-of select="normalize-space($defaultValueElement)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$defaultValueElement"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:if test="*[@text:style-name='Attribute_20_Value_20_Instance']">
            <xsl:comment>The following attribute default value is listed in the ODF schema</xsl:comment><xsl:text>            
      </xsl:text>
        </xsl:if>
        <xsl:choose>
            <xsl:when test="*[@text:style-name='Element']">
                <!-- sometimes a default values only occurs on a certain element or elements -->
                <xsl:for-each select="*[@text:style-name='Element']">
                    <!-- use the element name without the brackets -->
                     <xsl:variable name="elementName">
						<xsl:call-template name="get-element-name">
							<xsl:with-param name="nameString" select="normalize-space(.)"/>
						</xsl:call-template>
					</xsl:variable>
					<xsl:if test="$elementName != ''">
						<xsl:element name="attribute">
							<xsl:attribute name="name">
								<xsl:value-of select="$attributeName"/>
							</xsl:attribute>
							<xsl:attribute name="defaultValue">
								<xsl:value-of select="$defaultValue"/>
							</xsl:attribute>                        
							<xsl:attribute name="element">
								<xsl:value-of select="$elementName"/>
							</xsl:attribute>
						</xsl:element>
                    </xsl:if>
                </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
                <xsl:element name="attribute">
                    <!-- if the default value occurs for all elements -->
                    <xsl:attribute name="name">
                        <xsl:value-of select="$attributeName"/>
                    </xsl:attribute>
                    <xsl:attribute name="defaultValue">
                        <xsl:value-of select="$defaultValue"/>
                    </xsl:attribute>
                </xsl:element>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template name="get-element-name">
		<xsl:param name="nameString"/>
        <xsl:choose>
            <xsl:when test="contains($nameString, '&lt;') and contains($nameString, '&gt;')">
                <!-- the name of the element -->
                 <xsl:value-of select='substring-after(substring-before($nameString, "&gt;"), "&lt;")'/>   
            </xsl:when>
            <xsl:when test="not(contains($nameString, '&lt;'))">
                <!-- Ignore as it is just an ending part already addressed -->
            </xsl:when>            
            <xsl:otherwise>
                <!-- the name of the element was separated into several text:span -->
				<xsl:call-template name="get-element-name">
					<xsl:with-param name="nameString" select="concat($nameString, normalize-space(following-sibling::text:span))"/>
				</xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>  		
		
	</xsl:template>
                            

    <xsl:template match="@*|comment()|text()"/>

    <xsl:template match="*">
        <xsl:apply-templates />
    </xsl:template>

</xsl:stylesheet>
