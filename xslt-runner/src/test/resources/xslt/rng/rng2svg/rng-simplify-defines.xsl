<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		version="1.1" xmlns="http://relaxng.org/ns/structure/1.0" xmlns:rng="http://relaxng.org/ns/structure/1.0" xmlns:exsl="http://exslt.org/common" extension-element-prefixes="exsl" 
exclude-result-prefixes = "exsl rng">

<!-- Simplification step 7.18. Taken from Eric van der Vlist's RNG simplification
     stylesheet and modified to run independantly of the preceeding steps. -->
<xsl:template match="/">
  <xsl:apply-templates mode="step7.18"/>
</xsl:template>

<xsl:template match="*|text()|@*" mode="step7.18">
	<xsl:copy>
		<xsl:apply-templates select="@*" mode="step7.18"/>
		<xsl:apply-templates mode="step7.18"/>
	</xsl:copy>
</xsl:template>

<xsl:template match="@combine" mode="step7.18"/>
<xsl:template match="rng:start[preceding-sibling::rng:start]|rng:define[@name=preceding-sibling::rng:define/@name]" mode="step7.18"/>

<xsl:template match="rng:start[not(preceding-sibling::rng:start) and following-sibling::rng:start]" mode="step7.18">
	<xsl:copy>
		<xsl:apply-templates select="@*" mode="step7.18"/>
		<xsl:element name="{parent::*/rng:start/@combine}">
			<xsl:call-template name="start7.18"/>
		</xsl:element>
	</xsl:copy>
</xsl:template>

<xsl:template name="start7.18">
	<xsl:param name="left" select="following-sibling::rng:start[2]"/>
	<xsl:param name="node-name" select="parent::*/rng:start/@combine"/>
	<xsl:param name="out">
		<xsl:element name="{$node-name}">
			<xsl:apply-templates select="*" mode="step7.18"/>
			<xsl:apply-templates select="following-sibling::rng:start[1]/*" mode="step7.18"/>
		</xsl:element>
	</xsl:param>
	<xsl:choose>
		<xsl:when test="$left/*">
			<xsl:variable name="newOut">
				<xsl:element name="{$node-name}">
					<xsl:copy-of select="$out"/>
					<xsl:apply-templates select="$left/*" mode="step7.18"/>
				</xsl:element>
			</xsl:variable>
			<xsl:call-template name="start7.18">
				<xsl:with-param name="left" select="$left/following-sibling::rng:start[1]"/>
				<xsl:with-param name="node-name" select="$node-name"/>
				<xsl:with-param name="out" select="$newOut"/>
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:copy-of select="$out"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="rng:define[not(@name=preceding-sibling::rng:define/@name) and @name=following-sibling::rng:define/@name]" mode="step7.18">
	<xsl:copy>
		<xsl:apply-templates select="@*" mode="step7.18"/>
		<xsl:call-template name="define7.18"/>
	</xsl:copy>
</xsl:template>

<xsl:template name="define7.18">
	<xsl:param name="left" select="following-sibling::rng:define[@name=current()/@name][2]"/>
	<xsl:param name="node-name" select="parent::*/rng:define[@name=current()/@name]/@combine"/>
	<xsl:param name="out">
		<xsl:element name="{$node-name}">
			<xsl:apply-templates select="*" mode="step7.18"/>
			<xsl:apply-templates select="following-sibling::rng:define[@name=current()/@name][1]/*" mode="step7.18"/>
		</xsl:element>
	</xsl:param>
	<xsl:choose>
		<xsl:when test="$left/*">
			<xsl:variable name="newOut">
				<xsl:element name="{$node-name}">
					<xsl:copy-of select="$out"/>
					<xsl:apply-templates select="$left/*" mode="step7.18"/>
				</xsl:element>
			</xsl:variable>
			<xsl:call-template name="define7.18">
				<xsl:with-param name="left" select="$left/following-sibling::rng:define[@name=current()/@name][1]"/>
				<xsl:with-param name="node-name" select="$node-name"/>
				<xsl:with-param name="out" select="$newOut"/>
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:copy-of select="$out"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>
