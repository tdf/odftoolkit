<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  version="1.0"
  xmlns:svg="http://www.w3.org/2000/svg"
  xmlns:rng="http://relaxng.org/ns/structure/1.0"
  xmlns:xlink="http://www.w3.org/1999/xlink">

<xsl:param name="start">root</xsl:param>

<xsl:param name="boxwidth">125</xsl:param>
<xsl:param name="gridwidth">200</xsl:param>
<xsl:param name="gridheight">50</xsl:param>
<xsl:output method="xml" indent="yes"/>

<xsl:template match="/">
  <xsl:choose>
    <xsl:when test="$start">
      <xsl:apply-templates select="//rng:element[@name=$start]" mode="root"/>
    </xsl:when>
    <xsl:when test="rng:grammar/rng:start">
      <xsl:choose>
        <xsl:when test="rng:grammar/rng:start/rng:ref">
          <xsl:variable name="ref" select="rng:ref/@name"/>
          <xsl:apply-templates select="//rng:element[@name=$ref]" mode="root"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="rng:grammar/rng:start/rng:element" mode="root"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
      <xsl:message terminate="yes">No 'start' parameter defined and no rng:start element found in grammar. Please use the 'start' parameter to specify the root element of the diagram.</xsl:message>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="rng:element" mode="root">
  <xsl:variable name="width">
    <xsl:call-template name="depth">
      <xsl:with-param name="root" select="."/>
      <xsl:with-param name="nodes" select=".//rng:*[not(./rng:*)]"/>
      <xsl:with-param name="traversed" select="."/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:variable name="height">
    <xsl:apply-templates select="." mode="height">
      <xsl:with-param name="traversed" select="."/>
    </xsl:apply-templates>
  </xsl:variable>
  <xsl:message>Creating diagram with width <xsl:value-of select="$width"/> and height <xsl:value-of select="$height"/></xsl:message>
  <xsl:processing-instruction name="xml-stylesheet">type="text/css" href="rng-diag.css"</xsl:processing-instruction>
  <svg:svg width="{$gridwidth * $width + 10}"
	   height="{$gridheight * $height + 50}">
    <svg:defs>
      <g id="sequence-marker" xmlns="http://www.w3.org/2000/svg">
	<svg:rect width="20" height="10" fill="white" stroke="black" stroke-width="1"/>
	<svg:circle cx="5" cy="5" r="2"/>
	<svg:circle cx="10" cy="5" r="2"/>
	<svg:circle cx="15" cy="5" r="2"/>      
      </g>
      <g id="choice-marker" stroke="black" stroke-width="2">
	<svg:rect width="20" height="10" fill="white" stroke="black" stroke-width="1"/>
	<svg:line x1="0" y1="5" x2="5" y2="5"/>
	<svg:line x1="5" y1="5" x2="15" y2="2"/>
	<svg:line x1="15" y1="5" x2="20" y2="5"/>
      </g>
      <g id="ref-marker" stroke="black" stroke-width="2">
	<circle cx="5" cy="5" r="5"/>
      </g>
    </svg:defs>
    <svg:svg width="{$gridwidth * $width}"
	     height="{$gridheight * $height}">
      <xsl:call-template name="vertical-grid-lines">
	<xsl:with-param name="max" select="$width"/>
	<xsl:with-param name="height" select="$height"/>
      </xsl:call-template>
      <xsl:call-template name="horizontal-grid-lines">
	<xsl:with-param name="max" select="$height"/>
	<xsl:with-param name="width" select="$width"/>
      </xsl:call-template>
      <xsl:apply-templates select="." mode="draw">
	<xsl:with-param name="bound-w" select="$width"/>
	<xsl:with-param name="bound-h" select="$height"/>
	<xsl:with-param name="traversed" select="."/>
      </xsl:apply-templates>
    </svg:svg>
  </svg:svg>
</xsl:template>

<xsl:template name="vertical-grid-lines">
  <xsl:param name="max"/>
  <xsl:param name="height"/>
  <xsl:param name="count" select="0"/>
  <xsl:if test="not($count > $max)">
    <svg:line x1="{$count * $gridwidth}" y1="{$gridheight * $height}"
	      x2="{$count * $gridwidth}" y2="0"/>
    <xsl:call-template name="vertical-grid-lines">
      <xsl:with-param name="max" select="$max"/>
      <xsl:with-param name="height" select="$height"/>
      <xsl:with-param name="count" select="$count + 1"/>
    </xsl:call-template>
  </xsl:if>
</xsl:template>

<xsl:template name="horizontal-grid-lines">
  <xsl:param name="max"/>
  <xsl:param name="width"/>
  <xsl:param name="count" select="0"/>
  <xsl:if test="not($count > $max)">
    <svg:line x1="0" y1="{$gridheight * $count}"
	      x2="{$width * $gridwidth}" y2="{$gridheight * $count}"/>
    <xsl:call-template name="horizontal-grid-lines">
      <xsl:with-param name="max" select="$max"/>
      <xsl:with-param name="width" select="$width"/>
      <xsl:with-param name="count" select="$count + 1"/>
    </xsl:call-template>
  </xsl:if>
</xsl:template>

<xsl:template match="rng:optional | rng:zeroOrMore" mode="draw">
  <xsl:param name="parent-x" select="-1"/>
  <xsl:param name="parent-y" select="-1"/>
  <xsl:param name="parent-h"/>
  <xsl:param name="bound-x" select="0"/>
  <xsl:param name="bound-y" select="0"/>
  <xsl:param name="bound-w" select="0"/>
  <xsl:param name="bound-h" select="0"/>
  <xsl:param name="traversed"/>
  <xsl:apply-templates select="rng:*" mode="draw">
    <xsl:with-param name="parent-x" select="$parent-x"/>
    <xsl:with-param name="parent-y" select="$parent-y"/>
    <xsl:with-param name="parent-h" select="$parent-h"/>
    <xsl:with-param name="bound-x" select="$bound-x"/>
    <xsl:with-param name="bound-y" select="$bound-y"/>
    <xsl:with-param name="bound-w" select="$bound-w"/>
    <xsl:with-param name="bound-h" select="$bound-h"/>
    <xsl:with-param name="traversed" select="$traversed"/>
  </xsl:apply-templates>
</xsl:template>

<xsl:template match="rng:element|rng:attribute|rng:text|rng:ref" mode="draw">
  <xsl:param name="parent-x" select="-1"/>
  <xsl:param name="parent-y" select="-1"/>
  <xsl:param name="parent-h"/>
  <xsl:param name="bound-x" select="0"/>
  <xsl:param name="bound-y" select="0"/>
  <xsl:param name="bound-w" select="0"/>
  <xsl:param name="bound-h" select="0"/>
  <xsl:param name="traversed"/>
  <xsl:message>Draw element <xsl:value-of select="@name"/></xsl:message>
  <xsl:message><xsl:text>  </xsl:text>Parent x: <xsl:value-of select="$parent-x"/></xsl:message>
  <xsl:message><xsl:text>  </xsl:text>Parent y: <xsl:value-of select="$parent-y"/></xsl:message>
  <xsl:message><xsl:text>  </xsl:text>Parent h: <xsl:value-of select="$parent-h"/></xsl:message>
  <svg:svg x="{$bound-x * $gridwidth}" y="{$bound-y * $gridheight}"
	   width="{$bound-w * $gridwidth}" height="{$bound-h * $gridheight}">
    <g transform="translate(0, {($bound-h * $gridheight div 2) - ($gridheight div 2)})">
      <svg:rect x="12"
		y="10"
		width="{$boxwidth - 4}"
		height="{$gridheight - 25}">
	<xsl:if test="local-name(.)='ref'">
	  <xsl:attribute name="rx">5</xsl:attribute>
	  <xsl:attribute name="ry">5</xsl:attribute>
	</xsl:if>
	<xsl:attribute name="class">
	  <xsl:value-of select="local-name(.)"/>
	  <xsl:if test="parent::rng:optional">-optional</xsl:if>
	</xsl:attribute>
      </svg:rect>
      <svg:text x="14"
		y="{$gridheight - 21}"
		class="element-name">
	<xsl:value-of select="@name"/>
      </svg:text>
      <xsl:if test="local-name(.)='text'">
	<svg:line x1="14" y1="16" x2="{$boxwidth + 6}" y2="16"/>
	<svg:line x1="14" y1="19" x2="{$boxwidth + 6}" y2="19"/>
	<svg:line x1="14" y1="22" x2="{$boxwidth + 6}" y2="22"/>
	<svg:line x1="14" y1="25" x2="{$boxwidth + 6}" y2="25"/>
	<svg:line x1="14" y1="28" x2="{$boxwidth + 6}" y2="28"/>
	<svg:line x1="14" y1="31" x2="{$boxwidth + 6}" y2="31"/>
      </xsl:if>
      <xsl:if test="$parent-x > -1">
	<svg:line x1="0" y1="{$gridheight div 2}"
		  x2="11" y2="{$gridheight div 2}"
		  class="tree-line"/>
      </xsl:if>
      <xsl:if test="parent::rng:zeroOrMore and (count(../rng:*) = 1)">
	<svg:text x="{$boxwidth - 10}" y="{$gridheight}">0..</svg:text>
      </xsl:if>
      <xsl:apply-templates select="." mode="following-content">
	<xsl:with-param name="traversed" select="$traversed"/>
      </xsl:apply-templates>
    </g>
    <xsl:choose>
      <xsl:when test="count(rng:*) = 1 and rng:choice">
	<xsl:call-template name="layout">
	  <xsl:with-param name="nodes" select="rng:choice/rng:*"/>
	  <xsl:with-param name="width" select="$bound-w - 1"/>
	  <xsl:with-param name="parent-x" select="0"/>
	  <xsl:with-param name="parent-y" select="$bound-y + ($bound-h div 2)"/>	  
	  <xsl:with-param name="parent-h" select="$bound-h"/>
	  <xsl:with-param name="traversed" select="$traversed"/>
	</xsl:call-template>
      </xsl:when>
      <xsl:when test="local-name(.)='ref'">
	<xsl:variable name="ref" select="@name"/>
	<xsl:variable name="defines" select="//rng:define[@name=$ref]"/>
	<xsl:choose>
	  <xsl:when test="count($traversed | $defines) = count($traversed)">
	    <!-- TODO: Just draw a reference element -->
	  </xsl:when>
	  <xsl:when test="(count($defines) = 1) and (count($defines[1]/rng:*) = 1) and ($defines[1]/rng:choice)">
	    <xsl:call-template name="layout">
	      <xsl:with-param name="nodes" select="$defines[1]/rng:choice/rng:*"/>
	      <xsl:with-param name="width" select="$bound-w - 1"/>
	      <xsl:with-param name="parent-x" select="0"/>
	      <xsl:with-param name="parent-y" select="$bound-y + ($bound-h div 2)"/>	  
	      <xsl:with-param name="parent-h" select="$bound-h"/>
	      <xsl:with-param name="traversed" select="$traversed | $defines"/>
	    </xsl:call-template>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:call-template name="layout">
	      <xsl:with-param name="nodes" select="//rng:define[@name=$ref]/rng:*"/>
	      <xsl:with-param name="width" select="$bound-w - 1"/>
	      <xsl:with-param name="parent-x" select="0"/>
	      <xsl:with-param name="parent-y" select="$bound-y + ($bound-h div 2)"/>	  
	      <xsl:with-param name="parent-h" select="$bound-h"/>
	      <xsl:with-param name="traversed" select="$traversed | $defines"/>
	    </xsl:call-template>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:when>
      <xsl:otherwise>
	<xsl:call-template name="layout">
	  <xsl:with-param name="nodes" select="rng:*"/>
	  <xsl:with-param name="width" select="$bound-w - 1"/>
	  <xsl:with-param name="parent-x" select="0"/>
	  <xsl:with-param name="parent-y" select="$bound-y + ($bound-h div 2)"/>
	  <xsl:with-param name="parent-h" select="$bound-h"/>
	  <xsl:with-param name="traversed" select="$traversed"/>
	</xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </svg:svg>
  <xsl:if test="$parent-x > -1">
    <xsl:comment>element: <xsl:value-of select="@name"/></xsl:comment>
    <xsl:comment>bound y: <xsl:value-of select="$bound-y"/></xsl:comment>
    <xsl:comment>bound h: <xsl:value-of select="$bound-h"/></xsl:comment>
    <xsl:comment>parent y: <xsl:value-of select="$parent-y"/></xsl:comment>
    <xsl:comment>parent h: <xsl:value-of select="$parent-h"/></xsl:comment>
    <svg:line x1="{($parent-x + 1) * $gridwidth}" 
	      y1="{($bound-y * $gridheight) + ($bound-h * $gridheight div 2)}"
	      x2="{($parent-x + 1) * $gridwidth}" 
	      y2="{$parent-h * $gridheight div 2}" class="tree-line"/>
  </xsl:if>
</xsl:template>

<xsl:template match="rng:element|rng:attribute" mode="following-content">
  <xsl:if test="(count(rng:*) - count(rng:empty)) > 0">
    <svg:line x1="{10 + $boxwidth - 1}" x2="{$gridwidth}" 
	      y1="{$gridheight div 2}" y2="{$gridheight div 2}"
	      stroke="black" stroke-width="1"
	      class="tree-line"/>
  </xsl:if>
  <xsl:if test="count(rng:*) > 1">
    <svg:use x="{$boxwidth + 15}" y="{($gridheight div 2) - 5}"
	     xlink:href="#sequence-marker"/>
  </xsl:if>
  <xsl:if test="count(rng:*) = 1 and rng:choice">
    <svg:use x="{$boxwidth + 15}" y="{($gridheight div 2) - 5}"
	     xlink:href="#choice-marker"/>
  </xsl:if>
</xsl:template>

<xsl:template match="rng:ref" mode="following-content">
  <xsl:param name="traversed"/>
  <xsl:variable name="ref" select="@name"/>
  <xsl:variable name="defines" select="//rng:define[@name=$ref]"/>
  <xsl:choose>
    <xsl:when test="not(count($traversed | $defines) = count($traversed))">
      <xsl:choose>
	<xsl:when test="count($defines) = 1">
	  <xsl:if test="count(//rng:define[@name=$ref]/rng:*) - count(//rng:define[@name=$ref]/rng:empty) > 0">
	    <svg:line x1="{10 + $boxwidth - 1}" x2="{$gridwidth}" 
		      y1="{$gridheight div 2}" y2="{$gridheight div 2}"
		      stroke="black" stroke-width="1"
		      class="tree-line"/>
	  </xsl:if>
	  <xsl:choose>
	    <xsl:when test="count(//rng:define[@name=$ref]/rng:*) > 1">
	      <svg:use x="{$boxwidth + 15}" y="{($gridheight div 2) - 5}"
		       xlink:href="#sequence-marker"/>
	    </xsl:when>
	    <xsl:when test="count(//rng:define[@name=$ref]/rng:*) = 1 and //rng:define[@name=$ref]/rng:choice">
	      <svg:use x="{$boxwidth + 15}" y="{($gridheight div 2) - 5}"
		       xlink:href="#choice-marker"/>
	    </xsl:when>
	  </xsl:choose>
	</xsl:when>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
      <svg:use x="{$boxwidth - 5}" y="{($gridheight div 2) - 5}"
	       xlink:href="#ref-marker"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="layout">
  <xsl:param name="nodes"/>
  <xsl:param name="width"/>
  <xsl:param name="y" select="0"/>
  <xsl:param name="parent-x"/>
  <xsl:param name="parent-y"/>
  <xsl:param name="parent-h"/>
  <xsl:param name="traversed"/>
  <xsl:if test="$nodes">
    <xsl:variable name="height">
      <xsl:apply-templates select="$nodes[1]" mode="height">
	<xsl:with-param name="traversed" select="$traversed"/>
      </xsl:apply-templates>
    </xsl:variable>
    <xsl:if test="$height > 0">
      <xsl:apply-templates select="$nodes[1]" mode="draw">
	<xsl:with-param name="bound-x" select="1"/>
	<xsl:with-param name="bound-y" select="$y"/>
	<xsl:with-param name="bound-w" select="$width"/>
	<xsl:with-param name="bound-h" select="$height"/>
	<xsl:with-param name="parent-x" select="$parent-x"/>
	<xsl:with-param name="parent-y" select="$parent-y"/>
	<xsl:with-param name="parent-h" select="$parent-h"/>
	<xsl:with-param name="traversed" select="$traversed | $nodes[1]"/>
      </xsl:apply-templates>
    </xsl:if>
    <xsl:call-template name="layout">
      <xsl:with-param name="nodes" select="$nodes[position() > 1]"/>
      <xsl:with-param name="width" select="$width"/>
      <xsl:with-param name="y" select="$y + $height"/>
      <xsl:with-param name="parent-x" select="$parent-x"/>
      <xsl:with-param name="parent-y" select="$parent-y"/>
      <xsl:with-param name="parent-h" select="$parent-h"/>
      <xsl:with-param name="traversed" select="$traversed | $nodes[1]"/>
    </xsl:call-template>
  </xsl:if>
  
</xsl:template>

<xsl:template name="depth">
  <xsl:param name="traversed"/>
  <xsl:param name="root"/>
  <xsl:param name="nodes"/>
  <xsl:param name="max" select="0"/>
  <xsl:choose>
    <xsl:when test="not($nodes)">
      <xsl:message>Depth of <xsl:value-of select="local-name($root)"/><xsl:text> </xsl:text><xsl:value-of select="$root/@name"/> is <xsl:value-of select="$max"/></xsl:message>
      <xsl:value-of select="$max"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:variable name="value-of-first">
	<xsl:message>Depth of <xsl:value-of select="local-name($nodes[1])"/></xsl:message>
	<xsl:choose>
	  <xsl:when test="local-name($nodes[1]) = 'ref'">
	    <xsl:message>Calculating depth of reference <xsl:value-of select="$nodes[1]/@name"/> from <xsl:value-of select="$root/@name"/>. Base Depth is <xsl:value-of select="count($nodes[1]/ancestor::*) - count($root/ancestor::*)"/></xsl:message>
	    <xsl:variable name="ref-depth">
	      <xsl:call-template name="ref-depth">
		<xsl:with-param name="ref-name" select="$nodes[1]/@name"/>
		<xsl:with-param name="traversed" select="$traversed"/>
	      </xsl:call-template>
	    </xsl:variable>
	    <xsl:value-of select="$ref-depth + count($nodes[1]/ancestor::*[not(rng:zeroOrMore|rng:optional)]) - count($root/ancestor::*[not(rng:zeroOrMore|rng:optional)])"/>
	    <xsl:message>Depth of reference <xsl:value-of select="$nodes[1]/@name"/> from <xsl:value-of select="$root/@name"/> is <xsl:value-of select="$ref-depth + count($nodes[1]/ancestor::*) - count($root/ancestor::*)"/></xsl:message>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:value-of select="count($nodes[1]/ancestor::*) - count($root/ancestor::*)"/>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:variable>
      <xsl:call-template name="depth">
	<xsl:with-param name="root" select="$root"/>
	<xsl:with-param name="nodes" select="$nodes[position() > 1]"/>
	<xsl:with-param name="traversed" select="$traversed"/>
	<xsl:with-param name="max">
	  <xsl:choose>
	    <xsl:when test="$value-of-first > $max">
	      <xsl:value-of select="$value-of-first"/>
	    </xsl:when>
	    <xsl:otherwise>
	      <xsl:value-of select="$max"/>
	    </xsl:otherwise>
	  </xsl:choose>
	</xsl:with-param>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<xsl:template name="ref-depth">
    <xsl:param name="ref-name"/>
    <xsl:param name="traversed"/>
    <xsl:choose>
      <xsl:when test="not(count($traversed | //rng:define[@name=$ref-name]) = count($traversed))">
	<xsl:call-template name="max-ref-depth">
	  <xsl:with-param name="nodes" select="//rng:define[@name=$ref-name]"/>
	  <xsl:with-param name="traversed" select="$traversed | //rng:define[@name=$ref-name]"/>
	</xsl:call-template>
      </xsl:when>
      <xsl:otherwise>1</xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template name="max-ref-depth">
    <xsl:param name="nodes"/>
    <xsl:param name="traversed"/>
    <xsl:param name="max" select="0"/>
    <xsl:choose>
        <xsl:when test="not($nodes)">
            <xsl:value-of select="$max"/>
        </xsl:when>
        <xsl:otherwise>
            <xsl:variable name="value-of-first">
                <xsl:call-template name="depth">
                    <xsl:with-param name="root" select="$nodes[1]"/>
                    <xsl:with-param name="nodes" select="$nodes[1]//rng:*[not(./rng:*)]"/>
		    <xsl:with-param name="traversed" select="$traversed"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:call-template name="max-ref-depth">
                <xsl:with-param name="nodes" select="$nodes[position() > 1]"/>
                <xsl:with-param name="max">
                    <xsl:choose>
                        <xsl:when test="$value-of-first > $max">
                            <xsl:value-of select="$value-of-first"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$max"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template match="rng:choice" mode="height">
  <xsl:param name="traversed"/>
    <xsl:call-template name="sum-heights">
        <xsl:with-param name="nodes" select="rng:*"/>
	<xsl:with-param name="traversed" select="$traversed"/>
    </xsl:call-template>
</xsl:template>

<xsl:template match="rng:ref" mode="height">
  <xsl:param name="traversed"/>
  <xsl:variable name="ref" select="@name"/>
  <xsl:choose>
    <xsl:when test="not(count($traversed | //rng:define[@name=$ref]) = count($traversed))">
      <xsl:call-template name="sum-heights">
	<xsl:with-param name="nodes" select="//rng:define[@name=$ref]"/>
	<xsl:with-param name="traversed" select="$traversed | //rng:define[@name=$ref]"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>1</xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="rng:attribute" mode="height">1</xsl:template>
<xsl:template match="rng:text" mode="height">1</xsl:template>
<xsl:template match="rng:element" mode="height">
  <xsl:param name="traversed"/>
  <xsl:variable name="ret">
    <xsl:choose>
      <xsl:when test="not(rng:define|rng:optional|rng:zeroOrMore|rng:ref|rng:element|rng:choice)">1</xsl:when>
      <xsl:otherwise>
	<xsl:variable name="child-heights">
	  <xsl:call-template name="sum-heights">
	    <xsl:with-param name="nodes" select="rng:*"/>
	    <xsl:with-param name="traversed" select="$traversed"/>
	  </xsl:call-template>
	</xsl:variable>
	<xsl:value-of select="$child-heights"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:message>Height of element <xsl:value-of select="@name"/> is <xsl:value-of select="$ret"/></xsl:message>
  <xsl:value-of select="$ret"/>
</xsl:template>

<xsl:template match="rng:*" mode="height">
  <xsl:param name="traversed" select="."/>
  <xsl:call-template name="sum-heights">
    <xsl:with-param name="nodes" select="rng:*"/>
    <xsl:with-param name="traversed" select="$traversed"/>
  </xsl:call-template>
</xsl:template>

<xsl:template name="sum-heights">
    <xsl:param name="nodes"/>
    <xsl:param name="traversed"/>
    <xsl:param name="total" select="0"/>
    <xsl:choose>
        <xsl:when test="not($nodes)">
            <xsl:value-of select="$total"/>
        </xsl:when>
        <xsl:otherwise>
            <xsl:variable name="add">
	      <xsl:apply-templates select="$nodes[1]" mode="height">
		<xsl:with-param name="traversed" select="$traversed"/>
	      </xsl:apply-templates>
            </xsl:variable>
            <xsl:call-template name="sum-heights">
                <xsl:with-param name="nodes" select="$nodes[position() > 1]"/>
		<xsl:with-param name="traversed" select="$traversed"/>
                <xsl:with-param name="total">
                    <xsl:value-of select="$total + $add"/>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

</xsl:stylesheet>
