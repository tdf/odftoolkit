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

<xsl:stylesheet version="1.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="xsl">
  <xsl:output method="xml" encoding="UTF-8" media-type="application/xhtml+xml" indent="no" omit-xml-declaration="no" doctype-public="-//W3C//DTD XHTML 1.1 plus MathML 2.0//EN" doctype-system="http://www.w3.org/TR/MathML2/dtd/xhtml-math11-f.dtd"/>

  <xsl:param name="baseURI" select="'.'"/>
  
  <xsl:variable name="baseURIDir" select="concat($baseURI,'/')"/>

  <xsl:template match="xhtml:base">
      <xsl:message>removing &lt;xhtml:base&gt;</xsl:message>
  </xsl:template>

  <xsl:template match="xhtml:a[starts-with(@href,$baseURIDir)]">
      <xsl:variable name="rel-href" select="substring-after(@href,$baseURIDir)"/>
      <xsl:variable name="fragment" select="substring-after($rel-href,'#')"/>
      <xsl:variable name="raw-path">
          <xsl:choose>
              <xsl:when test="$fragment=''">
                  <xsl:value-of select="$rel-href"/>
              </xsl:when>
              <xsl:otherwise>
                  <xsl:value-of select="substring-before($rel-href,'#')"/>
              </xsl:otherwise>
          </xsl:choose>
      </xsl:variable>
      <xsl:variable name="path">
          <xsl:choose>
              <xsl:when test="starts-with($raw-path,'../')">
                  <xsl:value-of select="substring($raw-path,4)"/>
              </xsl:when>
              <xsl:otherwise>
                  <xsl:value-of select="$raw-path"/>
              </xsl:otherwise>
          </xsl:choose>
      </xsl:variable>
      <xsl:variable name="new-href">
          <xsl:choose>
              <xsl:when test="contains($path,'.odt')">
                  <xsl:value-of select="concat(substring-before($path,'.odt'),'.html',substring-after($path,'.odt'))"/>
              </xsl:when>
              <xsl:otherwise>
                  <xsl:value-of select="$path"/>
              </xsl:otherwise>
          </xsl:choose>
          <xsl:choose>
              <xsl:when test="starts-with($fragment,'__RefHeading__')">
                  <xsl:variable name="t">
                      <xsl:call-template name="strip-trailing-space">
                          <xsl:with-param name="text" select="translate(text(),'0123456789&#160;','           ')"/>
                      </xsl:call-template>
                  </xsl:variable>
                  <xsl:variable name="text" select="substring(text(),1,string-length($t))"/>
                  <xsl:value-of select="concat('#a_',translate($text, '&#xA;&amp;&lt;&gt;.,;: %()[]/\+', '_______________________________'))"/>
              </xsl:when>
              <xsl:when test="$fragment!=''">
                  <xsl:value-of select="concat('#',$fragment)"/>
              </xsl:when>
          </xsl:choose>
      </xsl:variable>
      <xsl:copy>
          <xsl:apply-templates select="@*[local-name(.)!='href']"/>
          <xsl:attribute name="href">
              <xsl:value-of select="$new-href"/>
              <xsl:message>a &quot;<xsl:value-of select="text()"/>&quot;<xsl:if test="normalize-space(text())=''"> in &quot;<xsl:value-of select="parent::xhtml:p/text()"/>&quot;</xsl:if>: href=&quot;<xsl:value-of select="@href"/>&quot; -&gt; href=&quot;<xsl:value-of select="$new-href"/>&quot;</xsl:message>
          </xsl:attribute>
          <xsl:choose>
              <xsl:when test="starts-with($fragment,'__RefHeading__')">
                  <xsl:variable name="t">
                      <xsl:call-template name="strip-trailing-space">
                          <xsl:with-param name="text" select="translate(text(),'0123456789&#160;','           ')"/>
                      </xsl:call-template>
                  </xsl:variable>
                  <xsl:variable name="text" select="substring(text(),1,string-length($t))"/>
                  <xsl:value-of select="$text"/>
              <xsl:message>a: &quot;<xsl:value-of select="text()"/>&quot; -&gt; &quot;<xsl:value-of select="$text"/>&quot;</xsl:message>
              </xsl:when>
              <xsl:otherwise>
                  <xsl:apply-templates select="node()"/>
              </xsl:otherwise>
          </xsl:choose>
      </xsl:copy>
  </xsl:template>

  <xsl:template name="strip-trailing-space">
      <xsl:param name="text"/>
      <xsl:choose>
          <xsl:when test="substring($text,string-length($text))=' '">
              <xsl:call-template name="strip-trailing-space">
                  <xsl:with-param name="text" select="substring($text,1,string-length($text)-1)"/>
              </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
              <xsl:value-of select="$text"/>
          </xsl:otherwise>
      </xsl:choose>
  </xsl:template>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
