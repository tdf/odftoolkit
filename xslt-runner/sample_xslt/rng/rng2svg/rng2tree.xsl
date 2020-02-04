<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		version="1.1"
		xmlns:rng="http://relaxng.org/ns/structure/1.0">

  <xsl:param name="start"/>
  <xsl:param name="debug"/>

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
    <tree>
      <xsl:choose>
	<xsl:when test="not($start='')">
	  <xsl:message>Starting tree from element <xsl:value-of select="start"/></xsl:message>
	  <xsl:apply-templates select=".//rng:element[rng:name=$start or @name=$start]"/>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:message>Starting tree from the grammar's start element</xsl:message>
	  <xsl:apply-templates select=".//rng:start"/>
	</xsl:otherwise>
      </xsl:choose>
    </tree>
  </xsl:template>

  <xsl:template match="rng:start">
    <xsl:message>In rng:start</xsl:message>
    <xsl:choose>
      <xsl:when test="rng:ref">
	<xsl:if test="$debug">
	  <xsl:message>Processing rng:ref[<xsl:value-of select="current()/rng:ref/@name"/>] in start</xsl:message>
	</xsl:if>
	<xsl:apply-templates select="//rng:define[@name = current()/rng:ref/@name]">
	  <xsl:with-param name="traversed" select="."/>
	</xsl:apply-templates>
      </xsl:when>
      <xsl:otherwise>
	<xsl:apply-templates>
	  <xsl:with-param name="traversed" select="."/>
	</xsl:apply-templates>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="rng:define">
    <xsl:param name="traversed"/>
    <xsl:param name="min-count" select="1"/>
    <xsl:param name="max-count" select="1"/>
    <xsl:if test="$debug">
      <xsl:message>Processing define <xsl:value-of select="@name"/></xsl:message>
    </xsl:if>
    <xsl:choose>
      <xsl:when test="count($traversed | .) = count($traversed)">
	<xsl:if test="$debug">
	  <xsl:message>Generating reference to previously traversed pattern.</xsl:message>
	</xsl:if>
	<reference type="pattern" name="{@name}" to="{generate-id()}" min-count="{$min-count}" max-count="{$max-count}"/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="$debug">
	  <xsl:message>Generating new pattern group.</xsl:message>
	</xsl:if>
	<pattern-group name="{@name}" min-count="{$min-count}" max-count="{$max-count}">
	  <xsl:apply-templates>
	    <xsl:with-param name="traversed" select="$traversed | ."/>
	  </xsl:apply-templates>
	</pattern-group>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="count-distinct">
    <xsl:param name="nodes"/>
    <xsl:param name="count" select="0"/>
    <xsl:choose>
      <xsl:when test="not($nodes)">
	<xsl:value-of select="$count"/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:variable name="first-node" select="$nodes[1]"/> 
	<xsl:call-template name="count-distinct">
	  <xsl:with-param name="nodes" select="$nodes[not(. = $first-node)]"/>
	  <xsl:with-param name="count" select="$count + 1"/>
	</xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="rng:name" mode="qname">
    <xsl:choose>
      <xsl:when test="@ns=''">
	<xsl:value-of select="."/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:variable name="nsuri" select="@ns"/>
	<xsl:choose>
	  <xsl:when test="count(namespace::*[.=$nsuri]) > 0">
	    <xsl:variable name="nsprefix" select="name(namespace::*[. = $nsuri])"/>
	    <xsl:value-of select="$nsprefix"/>:<xsl:value-of select="."/>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:variable name="nscount">
	      <xsl:call-template name="count-distinct">
		<xsl:with-param name="nodes"
				select="//@ns[. = $nsuri]/preceding::*/@ns"/>
	      </xsl:call-template>
	    </xsl:variable>
	    <xsl:text>ns</xsl:text><xsl:value-of select="$nscount"/>:<xsl:value-of select="."/>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="rng:element[rng:anyName]">
    <xsl:param name="traversed"/>
    <xsl:param name="min-count" select="1"/>
    <xsl:param name="max-count" select="1"/>
    <node type="any-element" name="anyElement" min-count="{$min-count}" max-count="{$max-count}">
      <xsl:attribute name="text-content">
        <xsl:call-template name="has-text-content">
          <xsl:with-param name="nodes" select="rng:*"/>
        </xsl:call-template>
      </xsl:attribute>
      <xsl:attribute name="except-ns">
	<xsl:for-each select="rng:except/rng:nsName">
	  '<xsl:value-of select="@ns"/>'<xsl:text> </xsl:text>
	</xsl:for-each>
      </xsl:attribute>
      <xsl:apply-templates>
	<xsl:with-param name="traversed" select="$traversed"/>
      </xsl:apply-templates>
    </node>
  </xsl:template>

  <xsl:template match="rng:element">
    <xsl:param name="traversed"/>
    <xsl:param name="min-count" select="1"/>
    <xsl:param name="max-count" select="1"/>
    <xsl:variable name="qname"><xsl:apply-templates select="rng:name" mode="qname"/></xsl:variable>
    <xsl:if test="$debug">
      <xsl:message>Processing element <xsl:value-of select="$qname"/></xsl:message>
    </xsl:if>
    <node type="element" name="{rng:name}{@name}" qname="{$qname}" min-count="{$min-count}" max-count="{$max-count}">
      <xsl:attribute name="text-content">
	<xsl:call-template name="has-text-content">
	  <xsl:with-param name="nodes" select="rng:*"/>
	</xsl:call-template>
      </xsl:attribute>
      <xsl:apply-templates>
	<xsl:with-param name="traversed" select="$traversed"/>
      </xsl:apply-templates>
    </node>
  </xsl:template>

  <xsl:template match="rng:attribute[rng:anyName]">
    <xsl:param name="min-count" select="1"/>
    <xsl:param name="max-count" select="1"/>
    <node type="any-attribute" name="anyAttribute" min-count="{$min-count}" max-count="{$max-count}">
      <xsl:attribute name="except-ns">
	<xsl:for-each select="rng:except/rng:nsName">
	  '<xsl:value-of select="@ns"/>'<xsl:text> </xsl:text>
	</xsl:for-each>
      </xsl:attribute>
    </node>
  </xsl:template>

  <xsl:template match="rng:attribute">
    <xsl:param name="min-count" select="1"/>
    <xsl:param name="max-count" select="1"/>
    <node type="attribute" name="{@name}{rng:name}" min-count="{$min-count}" max-count="{$max-count}"/>
  </xsl:template>

  <xsl:template match="rng:group">
    <xsl:param name="traversed"/>
    <xsl:param name="min-count" select="1"/>
    <xsl:param name="max-count" select="1"/>
    <node type="group" min-count="{$min-count}" max-count="{$max-count}">
      <xsl:apply-templates>
	<xsl:with-param name="traversed" select="$traversed"/>
	<xsl:with-param name="min-count" select="$min-count"/>
	<xsl:with-param name="max-count" select="$max-count"/>
      </xsl:apply-templates>
    </node>
  </xsl:template>

  <xsl:template match="rng:optional">
    <xsl:param name="traversed"/>
    <xsl:apply-templates>
      <xsl:with-param name="traversed" select="$traversed"/>
      <xsl:with-param name="min-count">0</xsl:with-param>
      <xsl:with-param name="max-count">1</xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="rng:zeroOrMore">
    <xsl:param name="traversed"/>
    <xsl:apply-templates>
      <xsl:with-param name="traversed" select="$traversed"/>
      <xsl:with-param name="min-count">0</xsl:with-param>
      <xsl:with-param name="max-count">-1</xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="rng:oneOrMore">
    <xsl:param name="traversed"/>
    <xsl:apply-templates>
      <xsl:with-param name="traversed" select="$traversed"/>
      <xsl:with-param name="min-count">1</xsl:with-param>
      <xsl:with-param name="max-count">-1</xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="rng:choice">
    <xsl:param name="traversed"/>
    <xsl:param name="min-count" select="1"/>
    <xsl:param name="max-count" select="1"/>
    <node type="choice" min-count="{$min-count}" max-count="{$max-count}">
      <xsl:apply-templates>
	<xsl:with-param name="traversed" select="$traversed"/>
      </xsl:apply-templates>
    </node>
  </xsl:template>

  <xsl:template match="rng:interleave">
    <xsl:param name="traversed"/>
    <xsl:param name="min-count" select="1"/>
    <xsl:param name="max-count" select="1"/>
    <node type="interleave" min-count="{$min-count}" max-count="{$max-count}">
      <xsl:apply-templates>
	<xsl:with-param name="traversed" select="$traversed"/>
      </xsl:apply-templates>
    </node>
  </xsl:template>

  <xsl:template match="rng:ref">
    <xsl:param name="traversed"/>
    <xsl:param name="min-count"/>
    <xsl:param name="max-count"/>
    <xsl:apply-templates select="//rng:define[@name=current()/@name]">
      <xsl:with-param name="traversed" select="$traversed"/>
      <xsl:with-param name="min-count" select="$min-count"/>
      <xsl:with-param name="max-count" select="$max-count"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template name="has-text-content">
    <xsl:param name="nodes"/>
    <xsl:param name="flag" select="false()"/>
    <xsl:choose>
      <xsl:when test="$flag='true' or not($nodes)">
	<xsl:value-of select="$flag"/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:variable name="first-node" select="$nodes[1]"/>
	<xsl:choose>
	  <xsl:when test="(local-name($first-node) = 'text') or (local-name($first-node) = 'value') or (local-name($first-node) = 'data')">
	    <xsl:call-template name="has-text-content">
	      <xsl:with-param name="nodes" select="$nodes[position() > 1]"/>
	      <xsl:with-param name="flag" select="true()"/>
	    </xsl:call-template>
	  </xsl:when>
	  <xsl:when test="local-name($first-node) = 'ref'">
	    <xsl:call-template name="has-text-content">
	      <xsl:with-param name="nodes" select="$nodes[position() > 1]"/>
	      <xsl:with-param name="flag">
		<xsl:call-template name="has-text-content">
		  <xsl:with-param name="nodes" select="//rng:define[@name = $first-node/@name]/rng:*"/>
		</xsl:call-template>
	      </xsl:with-param>
	    </xsl:call-template>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:call-template name="has-text-content">
	      <xsl:with-param name="nodes" select="$nodes[position() > 1]"/>
	      <xsl:with-param name="flag" select="$flag"/>
	    </xsl:call-template>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

<xsl:template match="*"><!-- Suppress --></xsl:template>
</xsl:stylesheet>
