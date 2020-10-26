<?xml version="1.0" encoding="utf-8"?>
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

<!-- Downloaded from http://techquila.com/tech/relax-ng-tools/ --> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:a="http://relaxng.org/ns/compatibility/annotations/1.0"
		xmlns:rng="http://relaxng.org/ns/structure/1.0"
		version="1.0"
		extension-element-prefixes="rng a">

  <xsl:param name="title">RELAX-NG Schema Documentation</xsl:param>
  <xsl:param name="default.documentation.string" select="'No documentation available.'"/>
  <xsl:param name="intro"/>
  <xsl:param name="target"/>

  <xsl:output indent="yes" doctype-system="../../../dtds/docbkx412/docbookx.dtd"/>

  <xsl:template match="rng:grammar">
    <xsl:message>Processing with intro=<xsl:value-of select="$intro"/></xsl:message>
    <article>
      <title><xsl:value-of select="$title"/></title>

      <xsl:if test="$intro">
	<xsl:copy-of select="document($intro)"/>
      </xsl:if>
	
      <section>
	<title>Grammar Documentation</title>
	<para>Namespace: <xsl:value-of select="@ns"/></para>
	<xsl:choose>
	  <xsl:when test="$target">
	    <xsl:apply-templates select="//rng:element[@name=$target or rng:name=$target]"/>
	    <xsl:apply-templates select="//rng:define[@name=$target]"/>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:apply-templates select="//rng:element">
	      <xsl:sort select="@name|rng:name" order="ascending"/>
	    </xsl:apply-templates>
	    <xsl:apply-templates select="//rng:define">
	      <xsl:sort select="@name" order="ascending"/>
	    </xsl:apply-templates>
	  </xsl:otherwise>
	</xsl:choose>
      </section>
    </article>
  </xsl:template>

  <xsl:template match="rng:element">
    <xsl:variable name="name" select="@name|rng:name"/>
    <xsl:variable name="nsuri">
      <xsl:choose>
	<xsl:when test="ancestor::rng:div[@ns]">
	  <xsl:value-of select="ancestor::rng:div[@ns][1]/@ns"/>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:value-of select="ancestor::rng:grammar[@ns][1]/@ns"/>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="nsprefix">
      <xsl:if test="$nsuri">
	<xsl:value-of select="name(namespace::*[.=$nsuri])"/>
      </xsl:if>
    </xsl:variable>
    <xsl:variable name="qname">
      <xsl:choose>
	<xsl:when test="not($nsprefix='')">
	  <xsl:value-of select="$nsprefix"/>:<xsl:value-of select="$name"/>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:value-of select="$name"/>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <section>
      <title>
	Element: <xsl:value-of select="$qname"/>
      </title>

      <table role="element">

	<xsl:attribute name="id">
	  <xsl:call-template name="makeid">
	    <xsl:with-param name="node" select="."/>
	  </xsl:call-template>
	</xsl:attribute>

	<title>Element: <xsl:value-of select="$qname"/></title>
	<tgroup cols="2">
	  <colspec colnum="1"/>
	  <colspec colnum="2" colwidth="*"/>
	  <tbody>
	    <xsl:if test="not($nsuri='')">
	      <row>
		<entry role="header" valign="top"><para>Namespace</para></entry>
		<entry><para><xsl:value-of select="$nsuri"/></para></entry>
	      </row>
	    </xsl:if>
	    <xsl:if test="a:documentation or $default.documentation.string">
	      <row>
		<entry role="header" valign="top"><para>Documentation</para></entry>
		<entry>
		  <xsl:choose>
		    <xsl:when test="a:documentation">
		      <para><xsl:apply-templates select="a:documentation"/></para>
		    </xsl:when>
		    <xsl:otherwise>
		      <para><xsl:value-of select="$default.documentation.string"/></para>
		    </xsl:otherwise>
		  </xsl:choose>
		</entry>
	      </row>
	    </xsl:if>
	    <row>
	      <entry role="header" valign="top"><para>Content Model</para></entry>
	      <entry><para><xsl:apply-templates mode="content-model"/></para></entry>
	    </row>

	    <xsl:variable name="hasatts">
	      <xsl:apply-templates select="." mode="has-attributes"/>
	    </xsl:variable>
	    <xsl:if test="starts-with($hasatts, 'true')">
	      <row>
		<entry role="header" valign="top"><para>Attributes</para></entry>
		<entrytbl cols="3">
		  <tbody>
		    <row>
		      <entry role="header"><para>Attribute</para></entry>
		      <entry role="header"><para>Type</para></entry>
		      <entry role="header"><para>Use</para></entry>
		      <entry role="header"><para>Documentation</para></entry>
		    </row>
		    <xsl:variable name="nesting" select="count(ancestor::rng:element)"/>
		    <xsl:apply-templates select=".//rng:attribute[count(ancestor::rng:element)=$nesting+1] | .//rng:ref[count(ancestor::rng:element)=$nesting+1]" mode="attributes">
		      <xsl:with-param name="matched" select="."/>
		      <xsl:with-param name="optional"><xsl:value-of select="false()"/></xsl:with-param>
		    </xsl:apply-templates>
		  </tbody>
		</entrytbl>
	      </row>
	    </xsl:if>
	    <row>
	      <entry role="header" valign="top"><para>Source</para></entry>
	      <entry>
		<programlisting> 
		  <xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text><xsl:copy-of select="."/><xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>
		</programlisting>
	      </entry>
	    </row>
	  </tbody>
	</tgroup>
      </table>
    </section>
  </xsl:template>

  <xsl:template match="rng:attribute" mode="attributes">
    <xsl:param name="matched"/>
    <xsl:param name="optional"/>
    <row>
      <entry><xsl:value-of select="@name"/></entry>
      <entry>
	<xsl:choose>
	  <xsl:when test="rng:data">
	    xsd:<xsl:value-of select="rng:data/@type"/>
	  </xsl:when>
	  <xsl:when test="rng:text">
	    TEXT
	  </xsl:when>
	  <xsl:when test="rng:choice">
	    Enumeration:<xsl:text> </xsl:text>
	    <xsl:for-each select="rng:choice/rng:value">
	      "<xsl:value-of select="."/>"
	      <xsl:if test="following-sibling::*"> | </xsl:if>
	    </xsl:for-each> 
	  </xsl:when>
	  <xsl:otherwise>
	    TEXT
	  </xsl:otherwise>
	</xsl:choose>
      </entry>
      <entry>
	<xsl:choose>
	  <xsl:when test="ancestor::rng:optional">Optional</xsl:when>
	  <xsl:when test="boolean($optional)">Optional</xsl:when>
	  <xsl:otherwise>Required</xsl:otherwise>
	</xsl:choose>
      </entry>
      <entry>
	<para>
	    <xsl:apply-templates select="a:documentation[1]"/>
	</para>
      </entry>
    </row>
  </xsl:template>

  <xsl:template match="rng:ref" mode="attributes">
    <xsl:param name="matched"/>
    <xsl:param name="optional"/>
    <xsl:variable name="name" select="@name"/>
    <xsl:variable name="opt" select="count(ancestor::rng:optional) > 0"/>
    <xsl:apply-templates select="//rng:define[@name=$name]" mode="attributes">
      <xsl:with-param name="matched" select="$matched"/>
      <xsl:with-param name="optional" select="boolean($optional) or boolean($opt)"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="rng:define" mode="attributes">
    <xsl:param name="matched"/>
    <xsl:param name="optional"/>
    <xsl:if test="not(count(matched)=count(matched|.))">
      <xsl:apply-templates select=".//rng:attribute[not(ancestor::rng:element)] | .//rng:ref[not(ancestor::rng:element)]" mode="attributes">
	<xsl:with-param name="matched" select="$matched|."/>
	<xsl:with-param name="optional" select="$optional or ancestor::rng:optional"/>
      </xsl:apply-templates>
    </xsl:if>
  </xsl:template>

  <xsl:template match="rng:element" mode="has-attributes">
    <xsl:choose>
      <xsl:when test=".//rng:attribute[count(ancestor::rng:element)=count(current()/ancestor::rng:element) + 1]">true</xsl:when>
      <xsl:otherwise>
	<xsl:apply-templates select=".//rng:ref[count(ancestor::rng:element)=count(current()/ancestor::rng:element) + 1]" mode="has-attributes"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="rng:ref" mode="has-attributes">
     <xsl:variable name="name" select="@name"/>
     <xsl:apply-templates select="//rng:define[@name=$name]" mode="has-attributes"/>
  </xsl:template>

  <xsl:template match="rng:define" mode="has-attributes">
    <xsl:choose>
      <xsl:when test=".//rng:attribute[not(ancestor::rng:element)]">true</xsl:when>
      <xsl:otherwise>
	<xsl:apply-templates select=".//rng:ref[not(ancestor::rng:element)]" mode="has-attributes"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="rng:*" mode="has-attributes">
    <xsl:apply-templates mode="has-attributes"/>
  </xsl:template>

  <xsl:template match="*|node()" mode="has-attributes">
    <!-- suppress -->
  </xsl:template>

  <xsl:template match="rng:define">
    <xsl:variable name="name" select="@name"/>
    <xsl:choose>
      <xsl:when test="following::rng:define[@name=$name and not(@combine)]">
	<xsl:apply-templates select="//rng:define[@name=$name and not(@combine)]" mode="define-base"/>
      </xsl:when>
      <xsl:when test="not(preceding::rng:define[@name=$name])">
	<xsl:apply-templates select="." mode="define-base"/>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="rng:define" mode="define-base">
    <xsl:variable name="name" select="@name"/>
    <xsl:variable name="haselements">
      <xsl:apply-templates select="." mode="find-element">
	<xsl:with-param name="matched" select=".."/>
      </xsl:apply-templates>
    </xsl:variable>
    <xsl:variable name="combined">
      <xsl:if test="@combine">
	<xsl:value-of select="following::rng:define[@name=$name]"/>
      </xsl:if>
      <xsl:if test="not(@combine)">
	<xsl:value-of select="//rng:define[@name=$name and @combine]"/>
      </xsl:if>
    </xsl:variable>
    <xsl:variable name="nsuri">
      <xsl:choose>
	<xsl:when test="ancestor::rng:div[@ns][1]">
	  <xsl:value-of select="ancestor::rng:div[@ns][1]/@ns"/>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:value-of select="ancestor::rng:grammar[@ns][1]/@ns"/>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <section>
      <xsl:attribute name="id"><xsl:value-of select="@name"/></xsl:attribute>
      <title>Pattern: <xsl:value-of select="@name"/></title>
      <table>
	<title>Pattern: <xsl:value-of select="@name"/></title>
	<tgroup cols="2">
	  <colspec colnum="1"/>
	  <colspec colnum="2" colwidth="*"/>
	  <tbody>
	    <row>
	      <entry class="header" valign="top"><para>Namespace</para></entry>
	      <entry><para><xsl:value-of select="$nsuri"/></para></entry>
	    </row>
	    <xsl:if test="a:documentation or not($default.documentation.string='')">
	      <row>
		<entry class="header" valign="top"><para>Documentation</para></entry>
		<entry>
		  <para>
		    <xsl:choose>
		      <xsl:when test="a:documentation">
			<xsl:apply-templates select="a:documentation"/>
		      </xsl:when>
		      <xsl:otherwise>
			<xsl:value-of select="$default.documentation.string"/>
		      </xsl:otherwise>
		    </xsl:choose>
		  </para>
		</entry>
	      </row>
	    </xsl:if>
	    <xsl:if test="starts-with($haselements, 'true')">
	      <row valign="top">
		<entry class="header"><para>Content Model</para></entry>
		<entry>
		  <para>
		    <xsl:apply-templates select="*" mode="content-model"/>
		    <xsl:if test="@combine">
		      <xsl:apply-templates select="following::rng:define[@name=$name]" mode="define-combine"/>
		    </xsl:if>
		    <xsl:if test="not(@combine)">
		      <xsl:apply-templates select="//rng:define[@name=$name and @combine]" mode="define-combine"/>
		    </xsl:if>
		  </para>
		</entry>
	      </row>
	    </xsl:if>
	    <xsl:variable name="hasatts">
	      <xsl:apply-templates select="." mode="has-attributes"/>
	    </xsl:variable>
	    <xsl:if test="starts-with($hasatts, 'true')">
	      <row valign="top">
		<entry class="header">Attributes</entry>
		<entrytbl cols="3">
		  <tbody>
		    <row>
		      <entry role="header"><para>Attribute</para></entry>
		      <entry role="header"><para>Type</para></entry>
		      <entry role="header"><para>Use</para></entry>
		      <entry role="header"><para>Documentation</para></entry>
		    </row>
		    <xsl:variable name="nesting" select="count(ancestor::rng:element)"/>
		    <xsl:apply-templates select=".//rng:attribute[count(ancestor::rng:element)=$nesting] | .//rng:ref[count(ancestor::rng:element)=$nesting]" mode="attributes">
		      <xsl:with-param name="matched" select="."/>
		    </xsl:apply-templates>
		  </tbody>
		</entrytbl>
	      </row>
	    </xsl:if>
	  </tbody>
	</tgroup>
      </table>
    </section>
  </xsl:template>
  
  <xsl:template match="rng:define" mode="define-combine">
    <xsl:choose>
      <xsl:when test="@combine='choice'">
	| (<xsl:apply-templates mode="content-model"/>)
      </xsl:when>
      <xsl:when test="@combine='interleave'">
	&amp; (<xsl:apply-templates mode="content-model"/>)
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="rng:element" mode="makeid">
    <xsl:apply-templates select="ancestor::rng:element[1]" mode="makeid"/>.<xsl:value-of select="@name"/>
  </xsl:template>

  <xsl:template match="rng:define" mode="makeid">
    <xsl:value-of select="@name"/>
  </xsl:template>

  <xsl:template match="*" mode="makeid">
    <xsl:apply-templates select="ancestor::rng:element[1] | ancestor::rng:define[1]"/>
  </xsl:template>

  <xsl:template name="makeid">
    <xsl:param name="node"/>
    <xsl:variable name="id"><xsl:apply-templates select="$node" mode="makeid"/></xsl:variable>
    <xsl:choose>
      <xsl:when test="ancestor-or-self::rng:define"><xsl:value-of select="ancestor-or-self::rng:define[1]/@name"/><xsl:value-of select="$id"/></xsl:when>
      <xsl:otherwise><xsl:value-of select="substring-after($id, '.')"/></xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- ================================================= -->
  <!-- CONTENT MODEL PATTERNS                            -->
  <!-- The following patterns construct a text           -->
  <!-- description of an element content model           -->
  <!-- ================================================= -->
  <xsl:template match="rng:element" mode="content-model">
    <link>
      <xsl:attribute name="linkend"><xsl:call-template name="makeid"><xsl:with-param name="node" select="."/></xsl:call-template></xsl:attribute>
      <xsl:value-of select="@name"/>
    </link>
    <xsl:if test="not(parent::rng:choice) and (following-sibling::rng:element | following-sibling::rng:optional | following-sibling::rng:oneOrMore | following-sibling::rng:zeroOrMore)">,</xsl:if>
  </xsl:template>

  <xsl:template match="rng:group" mode="content-model">
    (<xsl:apply-templates mode="content-model"/>)
  </xsl:template>

  <xsl:template match="rng:optional" mode="content-model">
    <xsl:if test=".//rng:element | .//rng:ref[not(ancestor::rng:attribute)]">
      <xsl:apply-templates mode="content-model"/>?
      <xsl:if test="not(parent::rng:choice) and (following-sibling::rng:element | following-sibling::rng:optional | following-sibling::rng:oneOrMore | following-sibling::rng:zeroOrMore)">,</xsl:if>
    </xsl:if>
  </xsl:template>

  <xsl:template match="rng:oneOrMore" mode="content-model">
    (<xsl:apply-templates mode="content-model"/>)+
    <xsl:if test="not(parent::rng:choice) and (following-sibling::rng:element | following-sibling::rng:optional | following-sibling::rng:oneOrMore | following-sibling::rng:zeroOrMore)">,</xsl:if>
  </xsl:template>

  <xsl:template match="rng:zeroOrMore" mode="content-model">
    (<xsl:apply-templates mode="content-model"/>)*
    <xsl:if test="not(parent::rng:choice) and (following-sibling::rng:element | following-sibling::rng:optional | following-sibling::rng:oneOrMore | following-sibling::rng:zeroOrMore)">,</xsl:if>
  </xsl:template>

  <xsl:template match="rng:choice" mode="content-model">
    ( 
    <xsl:for-each select="*">
      <xsl:apply-templates select="." mode="content-model"/> 
      <xsl:if test="following-sibling::rng:*"> | </xsl:if>
    </xsl:for-each>
    )
  </xsl:template>

  <xsl:template match="rng:value" mode="content-model">
    "<xsl:value-of select="."/>"
  </xsl:template>

  <xsl:template match="rng:empty" mode="content-model">
    EMPTY
  </xsl:template>

  <xsl:template match="rng:ref" mode="content-model">
    <xsl:variable name="haselement"><xsl:apply-templates select="." mode="find-element"/></xsl:variable>
    <xsl:if test="starts-with($haselement, 'true')">
      <link linkend="{@name}">%<xsl:value-of select="@name"/>;</link>
      <xsl:if test="not(parent::rng:choice) and (following-sibling::rng:element | following-sibling::rng:optional | following-sibling::rng:oneOrMore | following-sibling::rng:zeroOrMore)">, </xsl:if>
    </xsl:if>
  </xsl:template>

  <xsl:template match="rng:text" mode="content-model">
    TEXT
    <xsl:if test="not(parent::rng:choice) and (following-sibling::rng:element | following-sibling::rng:optional | following-sibling::rng:oneOrMore | following-sibling::rng:zeroOrMore)">, </xsl:if>
  </xsl:template>

  <xsl:template match="rng:data" mode="content-model">
    xsd:<xsl:value-of select="@type"/>
  </xsl:template>

  <xsl:template match="*" mode="content-model">
    <!-- suppress -->
  </xsl:template>

  <xsl:template match="rng:define" mode="find-element">
    <xsl:param name="matched"/>
    <xsl:if test="not(count($matched | .)=count($matched))">
      <xsl:choose>
	<xsl:when test=".//rng:element|.//rng:text">
	  <xsl:value-of select="true()"/>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:apply-templates select=".//rng:ref[not(ancestor::rng:attribute)]" mode="find-element">
	    <xsl:with-param name="matched" select="$matched | ."/>
	  </xsl:apply-templates>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:if>
  </xsl:template>

  <xsl:template match="rng:ref" mode="find-element">
    <xsl:param name="matched" select="."/>
    <xsl:variable name="ref" select="@name"/>
    <xsl:apply-templates select="//rng:define[@name=$ref]" mode="find-element">
      <xsl:with-param name="matched" select="$matched"/>
    </xsl:apply-templates>
  </xsl:template>


</xsl:stylesheet>
