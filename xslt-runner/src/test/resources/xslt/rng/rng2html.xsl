<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:a="http://relaxng.org/ns/compatibility/annotations/1.0" xmlns:rng="http://relaxng.org/ns/structure/1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:map="http://www.w3.org/2005/xpath-functions/map" version="3.0" extension-element-prefixes="rng a">
    <xsl:output encoding="UTF-8" method="html" indent="no"/>

    <!--Identity template -->
    <xsl:template match="@*|*|processing-instruction()|comment()">
        <xsl:copy>
            <xsl:apply-templates select="*|@*|text()|processing-instruction()|comment()"/>
        </xsl:copy>
    </xsl:template>
                      
    <!--
     Adding an ID for every RelaxNG define:
     Input:
    <span class="html-tag">&lt;define <span class="html-attribute-name">name</span>="<span class="html-attribute-value">style-handout-master</span>"&gt;</span>
     -->
    <xsl:template match="span[@class ='html-tag' and (starts-with(xs:string(.), '&lt;define') or starts-with(xs:string(.), '&lt;rng:define'))]/span[@class='html-attribute-value']">
        <span class="html-attribute-value">
            <xsl:element name="a">
                <xsl:attribute name="id">
                    <xsl:value-of select="."/>
                </xsl:attribute>
                <xsl:value-of select="."/>
            </xsl:element>
        </span>
    </xsl:template>

    <!--
    Adding an Anchor for every RelaxNG ref:
    Input:
    <span class="html-tag">&lt;ref <span class="html-attribute-name">name</span>="<span class="html-attribute-value">style-handout-master</span>"/&gt;</span>
    -->
    <xsl:template match="span[@class ='html-tag' and (starts-with(xs:string(.), '&lt;ref') or starts-with(xs:string(.), '&lt;rng:ref'))]/span[@class='html-attribute-value']">
        <span class="html-attribute-value">
            <xsl:element name="a">
                <xsl:attribute name="href">
                    <xsl:call-template name="isBuildInTypeTest">
                        <xsl:with-param name="type" select="."></xsl:with-param>
                    </xsl:call-template>
                    <xsl:value-of select="."/>
                </xsl:attribute>
                <xsl:value-of select="."/>
            </xsl:element>
        </span>
    </xsl:template>

      <!-- Test if the given type is part of the W3C XML schema datatype specification -->
    <xsl:template name="isBuildInTypeTest">
        <xsl:param name="type"/>
        <xsl:choose>
            <xsl:when test="
          $type = 'normalizedString' or
          $type = 'tokenlanguage' or
          $type = 'NMTOKEN' or
          $type = 'NMTOKENS' or
          $type = 'Name' or
          $type = 'NCName' or
          $type = 'ID' or
          $type = 'IDREF' or
          $type = 'IDREFS' or
          $type = 'ENTITY' or
          $type = 'ENTITIES' or
          $type = 'integer' or
          $type = 'nonPositiveInteger' or
          $type = 'negativeInteger' or
          $type = 'long' or
          $type = 'int' or
          $type = 'short' or
          $type = 'byte' or
          $type = 'nonNegativeInteger' or
          $type = 'unsignedLong' or
          $type = 'unsignedInt' or
          $type = 'unsignedShort' or
          $type = 'unsignedByte' or
          $type = 'positiveInteger' or
          $type = 'string' or
          $type = 'boolean' or
          $type = 'decimal' or
          $type = 'float' or
          $type = 'double' or
          $type = 'duration' or
          $type = 'dateTime' or
          $type = 'time' or
          $type = 'date' or
          $type = 'gYearMonth' or
          $type = 'gYear' or
          $type = 'gMonthDay' or
          $type = 'gDay' or
          $type = 'gMonth' or
          $type = 'hexBinary' or
          $type = 'base64Binary' or
          $type = 'anyURI' or
          $type = 'QName' or
          $type = 'NOTATION'">https://www.w3.org/TR/xmlschema-2/#</xsl:when>
            <xsl:otherwise>#</xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>