<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:html="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:a="http://relaxng.org/ns/compatibility/annotations/1.0" xmlns:rng="http://relaxng.org/ns/structure/1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:map="http://www.w3.org/2005/xpath-functions/map" version="3.0">
  <xsl:output method="xml" indent="yes" omit-xml-declaration="no"/>

  <xsl:template match="/rng:grammar">
      <xsl:copy>
        <xsl:apply-templates select="rng:start"/>
        <xsl:apply-templates select="rng:define">
          <xsl:sort select="@name"/>
        </xsl:apply-templates>
      </xsl:copy>
  </xsl:template>

  <xsl:template match="@* | node()">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>