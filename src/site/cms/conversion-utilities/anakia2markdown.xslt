<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="xml" omit-xml-declaration="yes"/>
  <xsl:strip-space elements="*"/>

  <xsl:template match="properties">
    <xsl:apply-templates select="node()"/>
    <xsl:text>&#10;</xsl:text>
  </xsl:template>

  <xsl:template match="document|body">
    <xsl:apply-templates select="node()"/>
  </xsl:template>

  <xsl:template match="ul|ol|dl">
    <xsl:text>&#10;</xsl:text>
    <xsl:apply-templates select="node()"/>
  </xsl:template>

  <xsl:template match="document/properties/author">
  </xsl:template>

  <xsl:template match="document/properties/title">
    <xsl:text>Title: </xsl:text>
    <xsl:value-of select="normalize-space(text())"/>
    <xsl:text>&#10;</xsl:text>
  </xsl:template>

  <xsl:template match="atom[@url]">
    <xsl:text>Atom: </xsl:text>
    <xsl:value-of select="@url"/>
    <xsl:text>&#10;      "</xsl:text>
    <xsl:value-of select="."/>
    <xsl:text>"</xsl:text>
    <xsl:text>&#10;</xsl:text>
  </xsl:template>

  <xsl:template match="document/body/section">
    <xsl:apply-templates select="@*|node()"/>
  </xsl:template>

  <xsl:template match="section/title">
  </xsl:template>

  <xsl:template match="toc">
    <xsl:text>&#10;[TOC]&#10;</xsl:text>
  </xsl:template>

  <xsl:template name="section">
    <xsl:param name="hashes"/>
    <xsl:param name="title"/>

    <xsl:text>&#10;</xsl:text>
    <xsl:value-of select="$hashes"/>
    <xsl:text> </xsl:text>
    <xsl:value-of select="normalize-space($title)"/>
    <xsl:text> </xsl:text>
    <xsl:value-of select="$hashes"/>
    <xsl:if test="@id">
      <xsl:text> {#</xsl:text>
      <xsl:value-of select="@id"/>
      <xsl:text>}</xsl:text>
    </xsl:if>
    <xsl:text>&#10;</xsl:text>
    <xsl:apply-templates select="*"/>
  </xsl:template>

  <xsl:template match="document/body/section/section">
    <xsl:call-template name="section">
      <xsl:with-param name="hashes">#</xsl:with-param>
      <xsl:with-param name="title">
        <xsl:value-of select="title"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="document/body/section/section/section">
    <xsl:call-template name="section">
      <xsl:with-param name="hashes">##</xsl:with-param>
      <xsl:with-param name="title">
        <xsl:value-of select="title"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="document/body/section/section/section/section">
    <xsl:call-template name="section">
      <xsl:with-param name="hashes">###</xsl:with-param>
      <xsl:with-param name="title">
        <xsl:value-of select="title"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="h3[not(*)]">
    <xsl:call-template name="section">
      <xsl:with-param name="hashes">###</xsl:with-param>
      <xsl:with-param name="title">
        <xsl:value-of select="."/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="h4[not(*)]">
    <xsl:call-template name="section">
      <xsl:with-param name="hashes">####</xsl:with-param>
      <xsl:with-param name="title">
        <xsl:value-of select="."/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="p">
    <xsl:text>&#10;</xsl:text>
    <xsl:apply-templates select="@*|node()"/>
    <xsl:text>&#10;</xsl:text>
  </xsl:template>

  <xsl:template match="hr">
    <xsl:text>&#10;----------&#10;</xsl:text>
  </xsl:template>

  <xsl:template match="blockquote">
    <xsl:text disable-output-escaping="yes">&#10;&gt;</xsl:text>
    <xsl:apply-templates select="@*|node()"/>
    <xsl:text>&#10;</xsl:text>
  </xsl:template>

  <xsl:template match="ul/li">
    <xsl:text>&#10;- </xsl:text>
    <xsl:apply-templates select="@*|node()"/>
    <xsl:text>&#10;</xsl:text>
  </xsl:template>

  <xsl:template match="ul/li/ul/li">
    <xsl:text>&#10;    - </xsl:text>
    <xsl:apply-templates select="@*|node()"/>
    <xsl:text>&#10;</xsl:text>
  </xsl:template>

  <xsl:template match="ol/li">
    <xsl:text>&#10;1. </xsl:text>
    <xsl:apply-templates select="@*|node()"/>
    <xsl:text>&#10;</xsl:text>
  </xsl:template>

  <xsl:template match="ol/li/ol/li">
    <xsl:text>&#10;    1. </xsl:text>
    <xsl:apply-templates select="@*|node()"/>
    <xsl:text>&#10;</xsl:text>
  </xsl:template>

  <xsl:template match="table">
    <xsl:text>&#10;</xsl:text>
    <xsl:apply-templates select="node()"/>
  </xsl:template>

  <xsl:template match="tr">
    <xsl:apply-templates select="node()"/>
    <xsl:text>|&#10;</xsl:text>

    <xsl:if test="th">
      <xsl:text>|</xsl:text>
      <xsl:for-each select="th" xmlns:str="http://exslt.org/strings">
        <xsl:value-of select="str:padding(string-length(.)+2,'-')"/>
        <xsl:text>|</xsl:text>
      </xsl:for-each>
      <xsl:text>&#10;</xsl:text>
    </xsl:if>
  </xsl:template>

  <xsl:template match="tr/td|th">
    <xsl:text>| </xsl:text>
    <xsl:apply-templates select="@*|node()"/>
    <xsl:text> </xsl:text>
  </xsl:template>

  <xsl:template match="tr/td//tr/td">
    <xsl:text> | </xsl:text>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:template>

  <xsl:template match="a[@href]">
    <xsl:text> [</xsl:text>
    <xsl:value-of select="normalize-space(text())"/>
    <xsl:text>](</xsl:text>
    <xsl:value-of select="@href"/>
    <xsl:text>) </xsl:text>
  </xsl:template>

  <xsl:template match="img[@src]">
    <xsl:text> ![</xsl:text>
    <xsl:value-of select="@alt"/>
    <xsl:text>](</xsl:text>
    <xsl:value-of select="@src"/>
    <xsl:text> "</xsl:text>
    <xsl:value-of select="@title"/>
    <xsl:text>") </xsl:text>
  </xsl:template>

  <xsl:template match="dl/dt">
    <xsl:text>&#10;&#10;</xsl:text>
    <xsl:apply-templates select="@*|node()"/>
    <xsl:if test="@id">
      <xsl:text> {#</xsl:text>
      <xsl:value-of select="@id" />
      <xsl:text>}</xsl:text>
    </xsl:if>
  </xsl:template>

  <xsl:template match="dl/dd">
    <xsl:text>&#10;:    </xsl:text>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:template>

  <xsl:template match="strong|b">
    <xsl:text> **</xsl:text>
    <xsl:apply-templates select="@*|node()"/>
    <xsl:text>** </xsl:text>
  </xsl:template>

  <xsl:template match="em|i">
    <xsl:text> *</xsl:text>
    <xsl:apply-templates select="@*|node()"/>
    <xsl:text>* </xsl:text>
  </xsl:template>

  <xsl:template match="code[not(*)]">
    <xsl:text> `</xsl:text>
    <xsl:value-of select="."/>
    <xsl:text>` </xsl:text>
  </xsl:template>

  <xsl:template match="tt[not(*)]">
    <xsl:text> `</xsl:text>
    <xsl:value-of select="."/>
    <xsl:text>` </xsl:text>
  </xsl:template>

  <xsl:template match="pre[not(*)]">
    <xsl:text> `</xsl:text>
    <xsl:value-of select="."/>
    <xsl:text>` </xsl:text>
  </xsl:template>

  <xsl:template match="code/pre">
    <xsl:copy xmlns:str="http://exslt.org/strings">
      <xsl:value-of select="str:replace(.,'*','\*')"/>
    </xsl:copy>  
  </xsl:template>

  <xsl:template match="source|pre">
    <pre><xsl:value-of select="."/></pre>
  </xsl:template>

  <xsl:template match="note">
    <xsl:apply-templates select="node()"/>
  </xsl:template>

  <xsl:template match="text()">
    <xsl:value-of select="normalize-space(.)"/>
  </xsl:template>

  <xsl:template match="*">
    <xsl:text disable-output-escaping="yes">&lt;</xsl:text>
    <xsl:value-of select="name()"/>
    <xsl:for-each select="@*">
      <xsl:text> </xsl:text>
      <xsl:value-of select="name()"/>
      <xsl:text>="</xsl:text>
      <xsl:value-of select="."/>
      <xsl:text>"</xsl:text>
    </xsl:for-each>
    <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
    <xsl:apply-templates select="node()"/>
    <xsl:text disable-output-escaping="yes">&lt;/</xsl:text>
    <xsl:value-of select="name()"/>
    <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
  </xsl:template>

  <xsl:template match="@*">
  </xsl:template>
</xsl:stylesheet>

