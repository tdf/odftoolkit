<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		xmlns:svg="http://www.w3.org/2000/svg"
		xmlns:rng="http://relaxng.org/ns/structure/1.0"
		xmlns:xlink="http://www.w3.org/1999/xlink"
		version="1.1">

  <!--

  RELAX-NG Schema Tree To SVG Stylesheet
  ======================================

  by Kal Ahmed (kal@techquila.com)

  Last Modified: $Date: 2004/01/24 19:57:48 $

  TODO:

    - DONE: Support for the "interleave" connector
    - DONE: Box decoration for elements containing text
    - DONE: Namespace prefixes in element names (could be done in rng2tree.xsl)
    - DONE: Render to multiple SVG files
    - DONE: Allow specification of one or more target elements to render
    - Make "Reference" nodes clickable - should link to the definition node
    - DONE: Optional display of diagram key
    - Make attribute display optional / configurable:
        - Display as nodes
        - Display as "popup" text
        - Do not display at all
    - "Popup" attribute display (attribute information appears on hovering)
    - "Popup" documentation (requires change to rng2tree.xsl too)
    - "Collapsible" branches (probably need to wait for an SVG 1.2 implemenetation)

  -->

  <xsl:output method="xml" indent="yes"/>

  <!-- ======================================= -->
  <!-- BASIC OPTIONS - use these options to 
         control what is rendered and basic
         appearance options                    -->
  <!-- ======================================= -->

  <!-- The main diagram title string. -->
  <xsl:param name="title"/>

  <!-- Whitespace separated list of the names of elements
       to be rendered. Each element is rendered to a 
       separate SVG file named "element-{name}.svg"
       Use the special wildcard * to produce a diagram for all 
       of the elements in the schema.
       -->
  <xsl:param name="start-elements"/>

  <!-- Whitespace separated list of the names of defines
       to be rendered. Each define is rendered to a 
       separate SVG file named "pattern-{name}.svg"
       Use the special wildcard * to produce a diagram for all 
       of the defines in the schema.
       -->
  <xsl:param name="start-patterns"/>


  <xsl:param name="key" select="'static'"/>

  <!-- ========================================= -->
  <!-- DEBUGGING - you should not need to change
                   these options                 -->
  <!-- ========================================= -->

  <!-- Set to true() to enable debugging options -->
  <xsl:param name="debug" select="false()"/>

  <!-- Debugging option: Draw only from start node to the
       specified depth. Useful for testing small parts of
       a tree -->
  <xsl:param name="debug-depth" select="6"/>


  <!-- ========================================= -->
  <!-- RENDER CONTROLS - variables that control
         spacing and other layout aspects of the
	 diagram. You may need to change these
	 if you want a different display style   -->
  <!-- ========================================= -->
  
  <!-- Vertical space to allow for each tree branch -->
  <xsl:param name="max-node-height" select="50"/>

  <!-- Vertical height of element/attribute text boxes -->
  <xsl:param name="box-height" select="22"/>
  <!-- Top border of element/attribute text boxes -->
  <xsl:param name="box-t-border" select="5"/>
  <!-- Bottom border of element/attribute text boxes -->
  <xsl:param name="box-b-border" 
	     select="$max-node-height - $box-height - $box-t-border"/>

  <!-- Left/Right border of diagram -->
  <xsl:param name="lr-border" select="10"/>
  <!-- Top/Bottom border of diagram -->
  <xsl:param name="tb-border" select="10"/>

  <!-- Width of the icon used to mark elements containing text -->
  <xsl:param name="text-marker-width" select="15"/>

  <!-- Height to reserve for the diagram title -->
  <xsl:param name="title-height" select="50"/>

  <!-- Type of connector between nodes:
       'square': produce square tree connectors
       any other value: produce direct (diagonal) connectors
       -->
  <xsl:param name="connector-style" select="'square'"/>

  <!-- Horizontal space to reserve for each character of
       text in element/attribute text boxes. -->
  <xsl:param name="avg-char-width" select="11"/>

  <xsl:attribute-set name="pruned">
    <xsl:attribute name="type"><xsl:value-of select="@type"/></xsl:attribute>
    <xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
    <xsl:attribute name="min-count"><xsl:value-of select="@min-count"/></xsl:attribute>
    <xsl:attribute name="max-count"><xsl:value-of select="@max-count"/></xsl:attribute>
    <xsl:attribute name="reference"><xsl:value-of select="@reference"/></xsl:attribute>
    <xsl:attribute name="text-content"><xsl:value-of select="@text-content"/></xsl:attribute>
  </xsl:attribute-set>

  <xsl:template match="/">
    <xsl:choose>
      <xsl:when test="(string-length($start-elements) > 0) or (string-length($start-patterns) > 0)">
	<xsl:choose>
	  <xsl:when test="$start-elements='*'">
	    <xsl:call-template name="start-elements">
	      <xsl:with-param name="namelist">
		<xsl:apply-templates select="//node[@type='element']" mode="namelist"/>
	      </xsl:with-param>
	    </xsl:call-template>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:call-template name="start-elements">
	      <xsl:with-param name="namelist" select="normalize-space($start-elements)"/>
	    </xsl:call-template>
	  </xsl:otherwise>
	</xsl:choose>

	<xsl:choose>
	  <xsl:when test="$start-patterns='*'">
	    <xsl:call-template name="start-patterns">
	      <xsl:with-param name="namelist">
		<xsl:apply-templates select="//pattern-group" mode="namelist"/>
	      </xsl:with-param>
	    </xsl:call-template>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:call-template name="start-patterns">
	      <xsl:with-param name="namelist" select="normalize-space($start-patterns)"/>
	    </xsl:call-template>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="$debug">
	  <xsl:message>Pruning tree...</xsl:message>
	</xsl:if>
	<xsl:variable name="trimmed">
	  <xsl:apply-templates select="tree" mode="trim">
	    <xsl:with-param name="root" select="tree"/>
	  </xsl:apply-templates>
	</xsl:variable>
	<xsl:if test="$debug">
	  <xsl:message>Pruning complete!</xsl:message>
	</xsl:if>
	<xsl:apply-templates select="$trimmed/tree">
	  <xsl:with-param name="p-height" select="count($trimmed//*[not(./*)])"/>
	  <xsl:with-param name="offset" select="0"/>
	</xsl:apply-templates>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Templates for auto-generated namelists -->
  <xsl:template match="node" mode="namelist">
    <xsl:value-of select="@name"/><xsl:text> </xsl:text>
  </xsl:template>

  <xsl:template match="pattern-group" mode="namelist">
    <xsl:variable name="name" select="@name"/>
    <xsl:if test="not(preceding::pattern-group[@name = $name])">
      <xsl:value-of select="@name"/><xsl:text> </xsl:text>
    </xsl:if>
  </xsl:template>

  <xsl:template match="*" mode="namelist"><!-- suppress --></xsl:template>

  <!-- Recursive templates for processing namelists to a set of
       output SVG files -->
  <xsl:template name="start-elements">
    <xsl:param name="namelist"/>
    <xsl:if test="$debug">
      <xsl:message>In start-elements with namelist <xsl:value-of select="$namelist"/></xsl:message>
    </xsl:if>
    <xsl:if test="string-length($namelist) > 0">
      <xsl:variable name="first-name">
	<xsl:choose>
	  <xsl:when test="string-length(substring-before($namelist, ' ')) > 0">
	    <xsl:value-of select="substring-before($namelist, ' ')"/>
	  </xsl:when>
	  <xsl:otherwise><xsl:value-of select="$namelist"/></xsl:otherwise>
	</xsl:choose>
      </xsl:variable>
      <xsl:if test="//node[(@type='element') and (@name=$first-name)]">
	<xsl:variable name="root" select="//node[(@type='element') and (@name=$first-name)]"/>
	<xsl:variable name="trimmed">
	  <xsl:apply-templates select="$root" mode="trim">
	    <xsl:with-param name="root" select="$root"/>
	  </xsl:apply-templates>
	</xsl:variable>
	<xsl:document href="element-{$first-name}.svg"
		      method="xml" indent="yes">
	  <xsl:call-template name="draw">
	    <xsl:with-param name="root" select="$trimmed/*[1]"/>
	    <xsl:with-param name="start-nodes" select="$trimmed/*"/>
	  </xsl:call-template>
	</xsl:document>
      </xsl:if>
      <xsl:call-template name="start-elements">
	<xsl:with-param name="namelist" select="normalize-space(substring($namelist, string-length($first-name) + 1))"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>


  <xsl:template name="start-patterns">
    <xsl:param name="namelist"/>
    <xsl:if test="$debug">
      <xsl:message>In start-patterns with namelist <xsl:value-of select="$namelist"/></xsl:message>
    </xsl:if>
    <xsl:if test="string-length($namelist) > 0">
      <xsl:variable name="first-name">
	<xsl:choose>
	  <xsl:when test="string-length(substring-before($namelist, ' ')) > 0">
	    <xsl:value-of select="substring-before($namelist, ' ')"/>
	  </xsl:when>
	  <xsl:otherwise><xsl:value-of select="$namelist"/></xsl:otherwise>
	</xsl:choose>
      </xsl:variable>
      <xsl:if test="//pattern-group[@name=$first-name]">
	<xsl:variable name="root" select="//pattern-group[@name=$first-name]"/>
	<xsl:variable name="trimmed">
	  <xsl:apply-templates select="$root" mode="trim">
	    <xsl:with-param name="root" select="$root"/>
	  </xsl:apply-templates>
	</xsl:variable>
	<xsl:document href="pattern-{$first-name}.svg"
		      method="xml" indent="yes">
	  <xsl:call-template name="draw">
	    <xsl:with-param name="root" select="$trimmed/*[1]"/>
	    <xsl:with-param name="start-nodes" select="$trimmed/*"/>
	  </xsl:call-template>
	</xsl:document>
      </xsl:if>
      <xsl:call-template name="start-patterns">
	<xsl:with-param name="namelist" select="normalize-space(substring($namelist, string-length($first-name) + 1))"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template match="tree">
    <xsl:call-template name="draw">
      <xsl:with-param name="root" select="."/>
      <xsl:with-param name="start-nodes" select="*"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="draw">
    <xsl:param name="root"/>
    <xsl:param name="start-nodes"/>
    <xsl:param name="p-height" select="count($root//*[not(./*)])"/>
    <xsl:variable name="depth">
      <xsl:call-template name="max-depth">
	<xsl:with-param name="root" select="$root"/>
	<!--
	<xsl:with-param name="nodes" select=".//*[not(./*)]"/>
	-->
	<xsl:with-param name="nodes" select="$start-nodes"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:message>Tree Height: <xsl:value-of select="$p-height"/> nodes</xsl:message>
    <xsl:message>Tree Breadth: <xsl:value-of select="$depth"/> px</xsl:message>
    <xsl:processing-instruction name="xml-stylesheet">type="text/css" href="rng-diag.css"</xsl:processing-instruction>
    <xsl:variable name="key-height">
      <xsl:choose>
	<xsl:when test="$key = 'static'">
	  <xsl:value-of select="((10 div (floor($depth div 200))) + 1) * 30"/>
	</xsl:when>
	<xsl:when test="$key='dynamic'">0</xsl:when>
	<xsl:otherwise>0</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:message>Height reserved for key: <xsl:value-of select="$key-height"/></xsl:message>
    <svg:svg width="{$depth + ($lr-border * 2)}"
	     height="{$p-height * $max-node-height + ($tb-border *2) + $title-height + $key-height}">
      <svg:defs>
	<svg:g id="choice" stroke="black" stroke-width="1">
	  <svg:rect x="0" y="-10" width="20" height="20" fill="white"
		stroke="black" stroke-width="2"/>
	  <svg:line x1="0" y1="0" x2="5" y2="0"/>
	  <svg:line x1="15" y1="0" x2="20" y2="0"/>
	  <svg:line x1="5" y1="0" x2="15" y2="-7"/>
	</svg:g>
	<svg:g id="choice-optional" stroke="black" stroke-width="1">
	  <svg:rect x="0" y="-10" width="20" height="20" fill="white"
		stroke="black" stroke-width="1.5" stroke-dasharray="3,3"/>
	  <svg:line x1="0" y1="0" x2="5" y2="0"/>
	  <svg:line x1="15" y1="0" x2="20" y2="0"/>
	  <svg:line x1="5" y1="0" x2="15" y2="-7"/>
	</svg:g>
	<svg:g id="interleave" stroke="black" stroke-width="1">
	  <svg:rect  x="0" y="-10" width="20" height="20" fill="white"
		stroke="black" stroke-width="1.5"/>
	  <svg:line x1="0" y1="-5" x2="15" y2="-5"/>
	  <svg:line x1="5" y1="0"  x2="20" y2="0"/>
	  <svg:line x1="0" y1="5" x2="15" y2="5"/>
	</svg:g>
	<svg:g id="interleave-optional" stroke="black" stroke-width="1">
	  <svg:rect  x="0" y="-10" width="20" height="20" fill="white"
		stroke="black" stroke-width="1.5" stroke-dasharray="3,3"/>
	  <svg:line x1="0" y1="-5" x2="15" y2="-5"/>
	  <svg:line x1="5" y1="0"  x2="20" y2="0"/>
	  <svg:line x1="0" y1="5" x2="15" y2="5"/>
	</svg:g>
	<svg:g id="group" stroke="black" stroke-width="1">
	  <svg:rect  x="0" y="-10" width="20" height="20" fill="white"
		stroke="black" stroke-width="1.5"/>
	  <svg:line x1="8" y1="-5" x2="3" y2="-5"/>
	  <svg:line x1="3" y1="-5" x2="3" y2="5"/>
	  <svg:line x1="3" y1="5"  x2="8" y2="5"/>
	  <svg:line x1="12" y1="-5" x2="17" y2="-5"/>
	  <svg:line x1="17" y1="-5" x2="17" y2="5"/>
	  <svg:line x1="17" y1="5"  x2="12" y2="5"/>
	</svg:g>
	<svg:g id="group-optional" stroke="black" stroke-width="1">
	  <svg:rect  x="0" y="-10" width="20" height="20" fill="white"
		     stroke="black" stroke-width="1.5" stroke-dasharray="3,3"/>
	  <svg:line x1="8" y1="-5" x2="3" y2="-5"/>
	  <svg:line x1="3" y1="-5" x2="3" y2="5"/>
	  <svg:line x1="3" y1="5"  x2="8" y2="5"/>
	  <svg:line x1="12" y1="-5" x2="17" y2="-5"/>
	  <svg:line x1="17" y1="-5" x2="17" y2="5"/>
	  <svg:line x1="17" y1="5"  x2="12" y2="5"/>
	</svg:g>
	<svg:g id="text-marker">
	  <svg:line x1="0" y1="4" x2="{$text-marker-width}" y2="4" class="text-marker-line"/>
	  <svg:line x1="0" y1="8" x2="{$text-marker-width}" y2="8" class="text-marker-line"/>
	  <svg:line x1="0" y1="12" x2="{$text-marker-width}" y2="12" class="text-marker-line"/>
	</svg:g>
      </svg:defs>

      <svg:text class="diagram-title" x="{$lr-border}" y="{$tb-border + $title-height - ($title-height div 4)}">
	<xsl:value-of select="$title"/>
      </svg:text>

      <rect x="{$lr-border}" y="{$tb-border + $title-height}"
	    width="{$depth}"
	    height="{$p-height * $max-node-height}" class="background-rect"/>
      <svg:svg x="{$lr-border}" y="{$tb-border + $title-height}"
	    width="{$depth}"
	    height="{$p-height * $max-node-height}">
	<xsl:apply-templates select="$start-nodes">
	  <xsl:with-param name="p-height" select="$p-height"/>
	</xsl:apply-templates>
      </svg:svg>

      <xsl:message>Key: <xsl:value-of select="$key"/></xsl:message>
      <xsl:choose>
	<xsl:when test="$key='static'">
	  <xsl:message>Display static key</xsl:message>
	  <svg:svg x="${lr-border}" 
		   y="{($p-height * $max-node-height) + $title-height + (2 * $tb-border)}"
		   width="{$depth}" height="{$key-height}">
	    <xsl:call-template name="static-key">
	      <xsl:with-param name="max-width" select="$depth"/>
	    </xsl:call-template> 
	  </svg:svg>
	</xsl:when>
	<xsl:when test="$key='dynamic'">
	  <xsl:call-template name="dynamic-key"/>
	</xsl:when>
      </xsl:choose>
    </svg:svg>
  </xsl:template>


  <xsl:template match="node[@type='choice' or @type='interleave' or @type='group']">
    <xsl:variable name="child-depth">
      <xsl:call-template name="max-depth">
	<xsl:with-param name="root" select="."/>
	<xsl:with-param name="nodes" select="./*"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="depth" select="$child-depth + 50"/>
    <xsl:variable name="height">
      <xsl:choose>
	<xsl:when test=".//*"><xsl:value-of select="count(.//*[not(./*)])"/></xsl:when>
	<xsl:otherwise>1</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="y-center" select="((($height - 1) * $max-node-height) div 2) + ($box-t-border) + ($box-height div 2)"/>
    <xsl:if test="ancestor::node">
      <svg:line class="tree-line" x1="0" y1="{$y-center}"
		x2="5" y2="{$y-center}"/>
    </xsl:if>
    <xsl:if test="(@max-count = -1) or (@max-count > 1)">
      <svg:use xlink:href="#{@type}" x="2" y="{$y-center - 3}"/>
    </xsl:if>
    <xsl:choose>
      <xsl:when test="@min-count='0'">
	<svg:use xlink:href="#{@type}-optional" x="5" y="{$y-center}"/>
      </xsl:when>
      <xsl:otherwise>
	<svg:use xlink:href="#{@type}" x="5" y="{$y-center}"/>
      </xsl:otherwise>
    </xsl:choose>

    <!-- Draw the minimum and maximum values -->
    <xsl:if test="(@min-count and @max-count) and not(@min-count='' or @max-count='') and not(@min-count=1 and @max-count=1) and not(@min-count=0 and @max-count=1)">
      <svg:text class="annotation" x="2" y="{$y-center + 20}">
	<xsl:value-of select="@min-count"/>..<xsl:choose>
	<xsl:when test="@max-count > 0"><xsl:value-of select="@max-count"/></xsl:when>
	<xsl:otherwise>&#x221e;</xsl:otherwise>
	</xsl:choose>
      </svg:text>
    </xsl:if>

    <xsl:call-template name="render">
      <xsl:with-param name="nodes" select="*"/>
      <xsl:with-param name="depth" select="$child-depth"/>
      <xsl:with-param name="connect-from" select="$y-center"/>
      <xsl:with-param name="x-start" select="25"/>
      <xsl:with-param name="x-end" select="35"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="node | pattern-group | reference">
    <xsl:variable name="child-depth">
      <xsl:call-template name="max-depth">
	<xsl:with-param name="root" select="."/>
	<xsl:with-param name="nodes" select="./*"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="my-width"><xsl:apply-templates select="." mode="depth"/></xsl:variable>
    <xsl:variable name="depth" select="$child-depth + $my-width"/>
    <xsl:variable name="height">
      <xsl:choose>
	<xsl:when test=".//*"><xsl:value-of select="count(.//*[not(./*)])"/></xsl:when>
	<xsl:otherwise>1</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="y-offset" select="((($height - 1) * $max-node-height) div 2) + $box-t-border"/>
    <xsl:if test="ancestor::node">
      <svg:line class="tree-line" x1="0" y1="{$y-offset + ($box-height div 2)}"
		x2="5" y2="{$y-offset + ($box-height div 2)}"/>
    </xsl:if>

    <xsl:variable name="rect-class-base">
      <xsl:choose>
	<xsl:when test="local-name() = 'pattern-group'">pattern</xsl:when>
	<xsl:when test="local-name() = 'reference'">reference</xsl:when>
	<xsl:otherwise>
	  <xsl:value-of select="@type"/>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="rect-class">
      <xsl:choose>
	<xsl:when test="@min-count='0'"><xsl:value-of select="$rect-class-base"/>-optional</xsl:when>
	<xsl:otherwise><xsl:value-of select="$rect-class-base"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <xsl:variable name="node-width"><xsl:apply-templates select="." mode="depth"/></xsl:variable>
    <xsl:variable name="box-width" select="$node-width - 15"/>

    <!-- Draw shadow box for nodes with multiple or infinite max-count -->
    <xsl:if test="(@max-count &lt; 0) or (@max-count > 1)">
      <svg:rect  x="9" y="{$y-offset + 4}"
	      width="{$box-width}"
	      height="{$box-height}"
	      class="{$rect-class}">
	<xsl:if test="(local-name() = 'pattern-group') or (local-name() = 'reference')">
	  <xsl:attribute name="rx">3</xsl:attribute>
	  <xsl:attribute name="ry">3</xsl:attribute>
	</xsl:if>
      </svg:rect>
    </xsl:if>

    <!-- Draw the main labelled box -->
    <svg:rect x="5" y="{$y-offset}"
	      width="{$box-width}"
	      height="{$box-height}"
	      class="{$rect-class}">
      <xsl:if test="(local-name() = 'pattern-group') or (local-name() = 'reference')">
	<xsl:attribute name="rx">3</xsl:attribute>
	<xsl:attribute name="ry">3</xsl:attribute>
      </xsl:if>
    </svg:rect>

    <!-- Draw the box label -->
    <svg:text x="8" y="{$y-offset + $box-height - 4}" class="element-name" text-rendering="optimizeLegibility">
      <xsl:value-of select="@name"/>
    </svg:text>

    <!-- Mark elements which allow text content -->
    <xsl:if test="@text-content='true'">
      <svg:use x="{$box-width - $text-marker-width - 2}" y="{$y-offset + 4}"
	       xlink:href="#text-marker"/>
    </xsl:if>

    <!-- Draw the minimum and maximum values -->
    <xsl:if test="(@min-count and @max-count) and not(@min-count='' or @max-count='') and not(@min-count=1 and @max-count=1) and not(@min-count=0 and @max-count=1)">
      <svg:text class="annotation" x="10" y="{$y-offset + $box-height + $box-b-border - 3}" text-rendering="optimizeLegibility">
	<xsl:value-of select="@min-count"/>..<xsl:choose>
	<xsl:when test="@max-count > 0"><xsl:value-of select="@max-count"/></xsl:when>
	<xsl:otherwise>&#x221e;</xsl:otherwise>
	</xsl:choose>
      </svg:text>
    </xsl:if>

    <!-- Now render the children -->
    <xsl:call-template name="render">
      <xsl:with-param name="nodes" select="*"/>
      <xsl:with-param name="depth" select="$child-depth"/>
      <xsl:with-param name="connect-from" select="$y-offset + ($box-height div 2)"/>
      <xsl:with-param name="x-start" select="$box-width + 5"/>
      <xsl:with-param name="x-end" select="$box-width + 15"/>
    </xsl:call-template>
    <xsl:comment>After Render</xsl:comment>

  </xsl:template>

  <xsl:template name="render">
    <xsl:param name="nodes"/>
    <xsl:param name="connect-from" select="0"/>
    <xsl:param name="offset" select="0"/>
    <xsl:param name="x-start" /> <!-- select="$box-width + 5"/-->
    <xsl:param name="x-end" /> <!-- select="$max-node-width"/-->
    <xsl:if test="not($debug) or ($debug-depth > count(ancestor::*))">
    <xsl:if test="$nodes">
      <xsl:variable name="first-node" select="$nodes[1]"/>
      <xsl:variable name="height">
	<xsl:choose>
	  <xsl:when test="$first-node//*"><xsl:value-of select="count($first-node//*[not(./*)])"/></xsl:when>
	  <xsl:otherwise>1</xsl:otherwise>
	</xsl:choose>
      </xsl:variable>
      <xsl:comment>First-node height: <xsl:value-of select="$height"/></xsl:comment>
      <xsl:comment>Offset: <xsl:value-of select="$offset"/></xsl:comment>
      <xsl:variable name="bounds-height">
	<xsl:value-of select="$height - 1"/>
      </xsl:variable>
      <xsl:if test="$connect-from > 0">
	<xsl:variable name="connect-to-offset" select="($offset * $max-node-height) + ((($bounds-height) * $max-node-height) div 2) + $box-t-border"/>
	<xsl:variable name="connect-to" select="$connect-to-offset + ($box-height div 2)"/>
	<xsl:choose>
	  <xsl:when test="$connector-style = 'square'">
	    <line class="tree-line" x1="{$x-start}" y1="{$connect-from}"
		  x2="{$x-end - 5}" y2="{$connect-from}"/>
	    <line class="tree-line" x1="{$x-end - 5}" y1="{$connect-from}"
		  x2="{$x-end - 5}" y2="{$connect-to}"/>
	    <line class="tree-line" x1="{$x-end - 5}" y1="{$connect-to}"
		  x2="{$x-end}" y2="{$connect-to}"/>
	  </xsl:when>
	  <xsl:otherwise>
	    <line class="tree-line" x1="{$x-start}" y1="{$connect-from}"
		  x2="{$x-end}" y2="$connect-to"/>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:if>
      <xsl:variable name="depth">
	<xsl:call-template name="max-depth">
	  <xsl:with-param name="root" select="."/>
	  <xsl:with-param name="nodes" select=".//*"/>
	</xsl:call-template>
      </xsl:variable>
      <svg:svg x="{$x-end}"
		y="{$offset * $max-node-height}"
		width="{$depth}"
		height="{$height * $max-node-height}">
	<xsl:comment>Nested SVG for <xsl:value-of select="local-name($first-node)"/><xsl:text> </xsl:text> <xsl:value-of select="@name"/></xsl:comment>
	<xsl:apply-templates select="$first-node"/>
      </svg:svg>
      <xsl:call-template name="render">
	<xsl:with-param name="nodes" select="$nodes[position() > 1]"/>
	<xsl:with-param name="depth" select="$depth"/>
	<xsl:with-param name="offset" select="$offset + $height"/>
	<xsl:with-param name="connect-from" select="$connect-from"/>
	<xsl:with-param name="x-start" select="$x-start"/>
	<xsl:with-param name="x-end" select="$x-end"/>
      </xsl:call-template>
    </xsl:if>
    </xsl:if>
  </xsl:template>

  <xsl:template match="node[@type='choice' or @type='group' or @type='interleave']" mode="depth">50</xsl:template>
  <xsl:template match="node | pattern-group" mode="depth">
    <!-- Be sure to allow enough space for the lead-in line and the
         exit tree lines plus the text marker (if needed) -->
    <xsl:variable name="extra-space">
      <xsl:choose>
	<xsl:when test="@text-content">
	  <xsl:value-of select="15 + $text-marker-width"/>
	</xsl:when>
	<xsl:otherwise>15</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="string-length(@name) > 5">
	<xsl:value-of select="(string-length(@name) * $avg-char-width) + $extra-space"/>
      </xsl:when>
      <xsl:otherwise><xsl:value-of select="($avg-char-width * 5) + $extra-space"/></xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="reference" mode="depth"><xsl:value-of select="(string-length(@name) * $avg-char-width) + 15"/></xsl:template>

  <xsl:template name="max-depth">
    <xsl:param name="root"/>
    <xsl:param name="nodes"/>
    <xsl:param name="max" select="0"/>
    <xsl:choose>
      <xsl:when test="not($nodes)">
	<xsl:value-of select="$max"/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:variable name="first-node" select="$nodes[1]"/>
	<xsl:variable name="child-depth">
	  <xsl:call-template name="max-depth">
	    <xsl:with-param name="root" select="$first-node"/>
	    <xsl:with-param name="nodes" select="$first-node/*"/>
	  </xsl:call-template>
	</xsl:variable>
	<xsl:variable name="my-depth">
	  <xsl:apply-templates select="$first-node" mode="depth"/>
	</xsl:variable>
	<xsl:variable name="depth" select="$child-depth + $my-depth"/>
	<xsl:call-template name="max-depth">
	  <xsl:with-param name="root" select="$root"/>
	  <xsl:with-param name="nodes" select="$nodes[position() > 1]"/>
	  <xsl:with-param name="max">
	    <xsl:choose>
	      <xsl:when test="$depth > $max">
		<xsl:value-of select="$depth"/>
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


  <xsl:template match="pattern-group" mode="trim">
    <xsl:param name="root"/>
    <xsl:variable name="preceding-groups"><xsl:value-of select="count(preceding::pattern-group[@name=current()/@name and (count(ancestor-or-self::* | $root) = count(ancestor-or-self::*))])"/></xsl:variable>
    <xsl:choose>
      <xsl:when test="$preceding-groups = 0">
	<xsl:copy use-attribute-sets="pruned">
	  <xsl:apply-templates mode="trim">
	    <xsl:with-param name="root" select="$root"/>
	  </xsl:apply-templates>
	</xsl:copy>
      </xsl:when>
      <xsl:otherwise>
	<reference type="pattern" name="{@name}" min-count="{@min-count}" max-count="{@max-count}"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="*" mode="trim">
    <xsl:param name="root"/>
    <xsl:copy use-attribute-sets="pruned">
      <xsl:apply-templates mode="trim">
	<xsl:with-param name="root" select="$root"/>
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>

  <xsl:template name="dynamic-key">
    <svg:svg id="key-window" width="200" height="20">
      <svg:g>
	<svg:text x="5" y="15" class="element-name"
		  text-rendering="optimizeLegibility">
	  Click Here For Key.
	</svg:text>
	<svg:rect x="0" y="0" width="200" height="20" fill="none" stroke="white"/>
	<svg:animate begin="click"  xlink:href="#key-window"
		     attributeType="XML" attributeName="width" to="350" 
		     dur="0.5s" fill="freeze"/>
	<svg:animate begin="click"  xlink:href="#key-window"
		     attributeType="XML" attributeName="height" to="200" 
		     dur="0.5s" fill="freeze"/>
	<svg:animate begin="mouseout"  xlink:href="#key-window"
		     attributeType="XML" attributeName="width" to="200"
		     dur="0.5s" fill="freeze"/>
	<svg:animate begin="mouseout"  xlink:href="#key-window"
		     attributeType="XML" attributeName="height" to="20"
		     dur="0.5s" fill="freeze"/>
      </svg:g>
      <svg:svg x="0" y="20">
	<svg:rect x="0" y="0" width="350" height="178" class="background-rect"/>
	<svg:rect x="5" y="5" width="90" height="22" class="element"/>
	<svg:text x="10" y="23" class="element-name">element</svg:text>
	<svg:text x="100" y="20" text-rendering="optimizeLegibility"
		  font-family="Helvetica" font-size="8pt">
	  Element
	</svg:text>
	
	<svg:rect x="5" y="35" width="90" height="22" class="attribute"/>
	<svg:text x="10" y="53" class="element-name">attribute</svg:text>
	<svg:text x="100" y="50" text-rendering="optimizeLegibility"
		  font-family="Helvetica" font-size="8pt">
	  Attribute
	</svg:text>
	
	<svg:rect x="5" y="65" width="90" height="22" rx="3" ry="3" class="pattern"/>
	<svg:text x="10" y="83" class="element-name">pattern</svg:text>
	<svg:text x="100" y="80" text-rendering="optimizeLegibility"
		  font-family="Helvetica" font-size="8pt">
	  Pattern
	</svg:text>
	
	<svg:rect x="150" y="5" width="100" height="22" class="any-element"/>
	<svg:text x="155" y="23" class="element-name">anyElement</svg:text>
	<svg:text x="260" y="20" text-rendering="optimizeLegibility"
		  font-family="Helvetica" font-size="8pt">
	  Any Element
	</svg:text>
	
	<svg:rect x="150" y="35" width="100" height="22" class="any-attribute"/>
	<svg:text x="155" y="53" class="element-name">anyAttribute</svg:text>
	<svg:text x="260" y="50" text-rendering="optimizeLegibility"
		  font-family="Helvetica" font-size="8pt">
	  Any Attribute
	</svg:text>
	
	<svg:rect x="150" y="65" width="100" height="22" rx="3" ry="3" class="reference"/>
	<svg:text x="155" y="83" class="element-name">pattern</svg:text>
	<svg:text x="260" y="80" text-rendering="optimizeLegibility"
		  font-family="Helvetica" font-size="8pt">
	  Pattern Reference
	</svg:text>
	
	<svg:use xlink:href="#group" x="5" y="105"/>
	<svg:text x="30" y="110" text-rendering="optimizeLegibility"
		  font-family="Helvetica" font-size="8pt">
	  Group
	</svg:text>
	<svg:use xlink:href="#choice" x="150" y="105"/>
	<svg:text x="180" y="110" text-rendering="optimizeLegibility"
		  font-family="Helvetica" font-size="8pt">
	  Choice
	</svg:text>
	
	<svg:use xlink:href="#interleave" x="230" y="105"/>
	<svg:text x="260" y="110" text-rendering="optimizeLegibility"
		  font-family="Helvetica" font-size="8pt">
	  Interleave
	</svg:text>
	
	<svg:rect x="8" y="133" width="90" height="22" class="element"/>
	<svg:rect x="5" y="130" width="90" height="22" class="element"/>
	<svg:text x="10" y="146" text-rendering="optimizeLegibility" class="element">repeated</svg:text>
	
	<svg:rect x="105" y="130" width="90" height="22" class="element-optional"/>
	<svg:text x="110" y="146" text-rendering="optimizeLegibility" class="element">optional</svg:text>
	
	<svg:text x="200" y="138" text-rendering="optimizeLegibility" class="element">
	  <svg:tspan>Repeat range shown</svg:tspan>
	  <svg:tspan x="200" dy="12">below box if applicable.</svg:tspan>
	</svg:text>
	
      </svg:svg>
    </svg:svg>
  </xsl:template>

  <xsl:template name="static-key">
    <xsl:param name="max-width">200</xsl:param>
    <xsl:param name="item"/>
    <xsl:param name="x"/>
    <xsl:param name="y"/>
    <xsl:variable name="myY">
      <xsl:choose>
	<xsl:when test="$x &gt; ($max-width - 190)"><xsl:value-of select="$y + 30"/></xsl:when>
	<xsl:otherwise><xsl:value-of select="$y"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="myX">
      <xsl:choose>
	<xsl:when test="$x &gt; ($max-width - 190)">5</xsl:when>
	<xsl:otherwise><xsl:value-of select="$x"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="not($item)">
        <svg:svg id="key">
          <xsl:call-template name="static-key">
	    <xsl:with-param name="max-width" select="$max-width"/>
	    <xsl:with-param name="item">element</xsl:with-param>
	    <xsl:with-param name="x">5</xsl:with-param>
	    <xsl:with-param name="y">5</xsl:with-param>
          </xsl:call-template>
	</svg:svg>
      </xsl:when>
      <xsl:when test="$item='element'">
	<xsl:call-template name="do-key-item">
	  <xsl:with-param name="type" select="'element'"/>
	  <xsl:with-param name="desc" select="'Element'"/>
	  <xsl:with-param name="x" select="$myX"/>
	  <xsl:with-param name="y" select="$myY"/>
	  <xsl:with-param name="next" select="'attribute'"/>
	  <xsl:with-param name="max-width" select="$max-width"/>
	</xsl:call-template>
      </xsl:when>
      <xsl:when test="$item='attribute'">
	<xsl:call-template name="do-key-item">
	  <xsl:with-param name="type" select="'attribute'"/>
	  <xsl:with-param name="desc" select="'Attribute'"/>
	  <xsl:with-param name="x" select="$myX"/>
	  <xsl:with-param name="y" select="$myY"/>
	  <xsl:with-param name="next" select="'anyElement'"/>
	  <xsl:with-param name="max-width" select="$max-width"/>
	</xsl:call-template>
      </xsl:when>
      <xsl:when test="$item='anyElement'">
	<xsl:call-template name="do-key-item">
	  <xsl:with-param name="type" select="'any-element'"/>
	  <xsl:with-param name="label" select="'anyElement'"/>
	  <xsl:with-param name="desc" select="'Any Element'"/>
	  <xsl:with-param name="x" select="$myX"/>
	  <xsl:with-param name="y" select="$myY"/>
	  <xsl:with-param name="next" select="'anyAttribute'"/>	
  <xsl:with-param name="max-width" select="$max-width"/>
	</xsl:call-template>
      </xsl:when>
      <xsl:when test="$item='anyAttribute'">
	<xsl:call-template name="do-key-item">
	  <xsl:with-param name="type" select="'any-attribute'"/>
	  <xsl:with-param name="label" select="'anyAttribute'"/>
	  <xsl:with-param name="desc" select="'AnyAttribute'"/>
	  <xsl:with-param name="x" select="$myX"/>
	  <xsl:with-param name="y" select="$myY"/>
	  <xsl:with-param name="next" select="'pattern'"/>
	  <xsl:with-param name="max-width" select="$max-width"/>
	</xsl:call-template>
      </xsl:when>
      <xsl:when test="$item='pattern'">
	<xsl:call-template name="do-key-item">
	  <xsl:with-param name="type" select="'pattern'"/>
	  <xsl:with-param name="label" select="'pattern'"/>
	  <xsl:with-param name="desc" select="'Pattern'"/>
	  <xsl:with-param name="x" select="$x"/>
	  <xsl:with-param name="y" select="$myY"/>
	  <xsl:with-param name="next" select="'reference'"/>
	  <xsl:with-param name="max-width" select="$max-width"/>
	</xsl:call-template>
      </xsl:when>      
      <xsl:when test="$item='reference'">
	<xsl:call-template name="do-key-item">
	  <xsl:with-param name="type" select="'reference'"/>
	  <xsl:with-param name="label" select="'pattern'"/>
	  <xsl:with-param name="desc" select="'Pattern Reference'"/>
	  <xsl:with-param name="x" select="$myX"/>
	  <xsl:with-param name="y" select="$myY"/>
	  <xsl:with-param name="next" select="'optional'"/>
	  <xsl:with-param name="max-width" select="$max-width"/>
	</xsl:call-template>
      </xsl:when>
      <xsl:when test="$item='optional'">
	<xsl:call-template name="do-key-item">
	  <xsl:with-param name="type" select="'element-optional'"/>
	  <xsl:with-param name="label" select="'optional'"/>
	  <xsl:with-param name="desc" select="'Optional'"/>
	  <xsl:with-param name="x" select="$myX"/>
	  <xsl:with-param name="y" select="$myY"/>
	  <xsl:with-param name="next" select="'repeated'"/>
	  <xsl:with-param name="max-width" select="$max-width"/>
	</xsl:call-template>
      </xsl:when>
      <xsl:when test="$item='repeated'">
	<xsl:call-template name="key-element">
	  <xsl:with-param name="type" select="'element'"/>
	  <xsl:with-param name="label" select="''"/>
	  <xsl:with-param name="desc" select="''"/>
	  <xsl:with-param name="x" select="$x - 3"/>
	  <xsl:with-param name="y" select="$y - 3"/>
	</xsl:call-template>
	<xsl:call-template name="do-key-item">
	  <xsl:with-param name="type" select="'element'"/>
	  <xsl:with-param name="label" select="'Repeatable'"/>
	  <xsl:with-param name="desc" select="'Repeatable. Range is'"/>
	  <xsl:with-param name="subdesc" select="'shown below box'"/>
	  <xsl:with-param name="x" select="$myX"/>
	  <xsl:with-param name="y" select="$myY"/>
	  <xsl:with-param name="next" select="'choice'"/>
	  <xsl:with-param name="max-width" select="$max-width"/>
	</xsl:call-template>
      </xsl:when>
      <xsl:when test="$item='choice'">
	<svg:use xlink:href="#choice" x="{$myX}" y="{$myY + 10}"/>
	<svg:text x="{$myX + 30}" y="{$myY +15}" 
		  text-rendering="optimizeLegibility">
	  Choice
	</svg:text>
	<xsl:call-template name="static-key">
	  <xsl:with-param name="max-width" select="$max-width"/>
	  <xsl:with-param name="x" select="$myX + 100"/>
	  <xsl:with-param name="y" select="$myY"/>
	  <xsl:with-param name="item" select="'interleave'"/>
	</xsl:call-template>
      </xsl:when>
      <xsl:when test="$item='interleave'">
	<svg:use xlink:href="#interleave" x="{$myX}" y="{$myY + 10}"/>
	<svg:text x="{$myX + 30}" y="{$myY +15}" 
		  text-rendering="optimizeLegibility">
	  Interleave
	</svg:text>
	<xsl:call-template name="static-key">
	  <xsl:with-param name="max-width" select="$max-width"/>
	  <xsl:with-param name="x" select="$myX + 100"/>
	  <xsl:with-param name="y" select="$myY"/>
	  <xsl:with-param name="item" select="'group'"/>
	</xsl:call-template>
      </xsl:when>
      <xsl:when test="$item='group'">
	<svg:use xlink:href="#group" x="{$myX}" y="{$myY + 10}"/>
	<svg:text x="{$myX + 30}" y="{$myY +15}" 
		  text-rendering="optimizeLegibility">
	  Group
	</svg:text>
	<xsl:call-template name="static-key">
	  <xsl:with-param name="max-width" select="$max-width"/>
	  <xsl:with-param name="x" select="$myX + 100"/>
	  <xsl:with-param name="y" select="$myY"/>
	  <xsl:with-param name="item" select="'end'"/>
	</xsl:call-template>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="do-key-item">
    <xsl:param name="type"/>
    <xsl:param name="label" select="$type"/>
    <xsl:param name="desc"/>
    <xsl:param name="subdesc"/>
    <xsl:param name="x"/>
    <xsl:param name="y"/>
    <xsl:param name="next"/>
    <xsl:param name="max-width"/>
    <xsl:call-template name="key-element">
      <xsl:with-param name="type" select="$type"/>
      <xsl:with-param name="label" select="$label"/>
      <xsl:with-param name="desc" select="$desc"/>
      <xsl:with-param name="subdesc" select="$subdesc"/>
      <xsl:with-param name="x" select="$x"/>
      <xsl:with-param name="y" select="$y"/>
    </xsl:call-template>
    <xsl:call-template name="static-key">
      <xsl:with-param name="max-width" select="$max-width"/>
      <xsl:with-param name="item" select="$next"/>
      <xsl:with-param name="x" select="$x + 190"/>
      <xsl:with-param name="y" select="$y"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="key-element">
    <xsl:param name="type"/>
    <xsl:param name="label" select="$type"/>
    <xsl:param name="desc"/>
    <xsl:param name="subdesc"/>
    <xsl:param name="x"/>
    <xsl:param name="y"/>
    <svg:rect x="{$x}" y="{$y}" width="90" height="22" class="{$type}"/>
    <svg:text x="{$x + 5}" y="{$y + 16}" class="element-name" text-rendering="optimizeLegibility">
      <xsl:value-of select="$label"/>
    </svg:text>
    <xsl:choose>
      <xsl:when test="not($subdesc)">
	<svg:text x="{$x + 95}" y="{$y + 15}" text-rendering="optimizeLegibility"
		  font-family="Helvetica" font-size="8pt">
	  <xsl:value-of select="$desc"/>
	</svg:text>
      </xsl:when>
      <xsl:otherwise>
	<svg:text x="{$x + 95}" y="{$y + 8}" 
		  text-rendering="optimizeLegibility"
		  font-family="Helvetica" font-size="8pt">
	  <xsl:value-of select="$desc"/>
	</svg:text>
	<svg:text  x="{$x + 95}" y="{$y + 20}" 
		   text-rendering="optimizeLegibility"
		   font-family="Helvetica" font-size="8pt">
	  <xsl:value-of select="$subdesc"/>
	</svg:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
