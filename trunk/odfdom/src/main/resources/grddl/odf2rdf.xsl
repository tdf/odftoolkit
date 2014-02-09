<?xml version="1.0" encoding="UTF-8"?>
<!--

  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER

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
<stylesheet version="1.0" xmlns="http://www.w3.org/1999/XSL/Transform" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:chart="urn:oasis:names:tc:opendocument:xmlns:chart:1.0" xmlns:dr3d="urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0" xmlns:draw="urn:oasis:names:tc:opendocument:xmlns:drawing:1.0" xmlns:fo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0" xmlns:form="urn:oasis:names:tc:opendocument:xmlns:form:1.0" xmlns:math="http://www.w3.org/1998/Math/MathML" xmlns:meta="urn:oasis:names:tc:opendocument:xmlns:meta:1.0" xmlns:number="urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0" xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0" xmlns:script="urn:oasis:names:tc:opendocument:xmlns:script:1.0" xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0" xmlns:svg="urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0" xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0" xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0" xmlns:xforms="http://www.w3.org/2002/xforms" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:pkg="http://docs.oasis-open.org/ns/office/1.2/meta/pkg#" xmlns:odf="http://docs.oasis-open.org/ns/office/1.2/meta/odf#">
	<output method="xml" encoding="UTF-8" media-type="application/rdf+xml" indent="yes" omit-xml-declaration="yes" />

  <!-- The base URL of the OpenDocument's three XML files (directory within package) -->
	<param name="sourceBaseURL" select="'.'" />
	<param name="FILE_URL">
		<choose>
			<when test="substring($sourceBaseURL, string-length($sourceBaseURL), 1) = '/'">
				<value-of select="concat($sourceBaseURL , substring-after(name(/*), 'office:document-'), '.xml')"/>
			</when>
			<otherwise>
				<!-- add '/' to the end of the document URL -->
				<value-of select="concat($sourceBaseURL ,'/', substring-after(name(/*), 'office:document-'), '.xml')"/>
			</otherwise>
		</choose>
	</param>


	<!-- Version 1.2.0 by Svante.Schubert AT gmail.com
		 First two digits mark the supported ODF version  -->
	<variable name="stylesheetVersion" select="'1.2.0'"/>

	<template match="/">
		<element name="rdf:RDF">
			<attribute name="xml:base">
				<value-of select="$sourceBaseURL"/>
			</attribute>
			<element name="pkg:Document">
				<attribute name="rdf:about">
					<value-of select="$sourceBaseURL"/>
				</attribute>
				<call-template name="setMimeType"/>
				<apply-templates select="*/@office:version" mode="root"/>
				<apply-templates select="/office:document-meta/office:meta/*" mode="meta-file"/>
			</element>
			<apply-templates mode="content-styles-files"/>
		</element>
	</template>

	<template match="@office:version" mode="root">
		<attribute name="office:version">
			<value-of select="."/>
		</attribute>
	</template>

	<template name="setMimeType">
		<!-- Use new ODF 1.2 RDF mimeType property -->
		<if test="@office:mimetype or name(/*/office:body/*)">
			<attribute name="pkg:mimeType">
				<choose>
					<when test="@office:mimetype">
						<value-of select="@office:mimetype"/>
					</when>
					<otherwise>
						<value-of select="concat('vnd.oasis.opendocument.', substring-after(name(/*/office:body/*), 'office:'))"/>
					</otherwise>
				</choose>
			</attribute>
		</if>
	</template>

	 <!-- OpenDocument meta.xml handling -->
	<template match="*" mode="meta-file">
		<element name="{name()}">
			<value-of select="text()"/>
		</element>
	</template>

	<template match="*[* or @*]" mode="meta-file">
		<element name="{name()}">
			<apply-templates select="@* | text()" mode="meta-file-child"/>
		</element>
	</template>

	<template match="@*" mode="meta-file-child">
		<attribute name="{name()}">
			<value-of select="."/>
		</attribute>
	</template>

	<template match="text()" mode="meta-file-child">
		<attribute name="meta:value">
			<value-of select="."/>
		</attribute>
	</template>

	<!-- OpenDocument content.xml handling -->
	<template match="/office:document-content/office:body/*" mode="content-styles-files">
		<apply-templates mode="content-styles-files" />
	</template>

	<!-- OpenDocument styles.xml handling (header&footer)-->
	<template match="/office:document-styles/office:master-styles/style:master-page/*" mode="content-styles-files">
		<apply-templates mode="content-styles-files" />
	</template>

	<!-- ignore RDFa of deleted content -->
	<template match="text:tracked-changes" mode="content-styles-files" />

	<!-- RDFa -->
	<template match="*" mode="content-styles-files">
		<apply-templates mode="content-styles-files" />
	</template>

	<template match="text()" mode="content-styles-files"/>

	<template match="*[@xhtml:about]" mode="content-styles-files">
		<choose>
			<when test="name() = 'text:bookmark-start'">
				<call-template name="resolve-RDF-subject">
					<with-param name="text">
						<apply-templates select="following::node()[1]" mode="rdfa-bookmark-literal">
							<with-param name="name" select="@text:name"/>
						</apply-templates>
					</with-param>
				</call-template>
			</when>
			<otherwise>
				<call-template name="resolve-RDF-subject">
					<with-param name="text">
						<apply-templates mode="rdfa-literal"/>
					</with-param>
				</call-template>
			</otherwise>
		</choose>
	</template>

	<template match="*" mode="rdfa-literal">
		<apply-templates mode="rdfa-literal"/>
	</template>

	<template match="text()" mode="rdfa-literal">
		<value-of select="."/>
	</template>

	<template match="*" mode="rdfa-bookmark-literal">
		<param name="name"/>

		<choose>
			<when test="descendant::node()[1]">
				<apply-templates select="descendant::node()[1]" mode="rdfa-bookmark-literal">
					<with-param name="name" select="$name"/>
				</apply-templates>
			</when>
			<otherwise>
				<apply-templates select="following::node()[1]" mode="rdfa-bookmark-literal">
					<with-param name="name" select="$name"/>
				</apply-templates>
			</otherwise>
		</choose>
	</template>

	<template match="text()" mode="rdfa-bookmark-literal">
		<param name="name"/>

		<!-- heuristic instead of a check if parent may contain text -->
		<if test="normalize-space(.) != ''">
			<value-of select="."/>
		</if>
		<apply-templates select="following::node()[1]" mode="rdfa-bookmark-literal">
			<with-param name="name" select="$name"/>
		</apply-templates>
	</template>

	<template match="text:bookmark-end" mode="rdfa-bookmark-literal">
		<param name="name"/>

		<!-- end condition of the recursion, if the bookmark-end  -->
		<if test="$name != @text:name">
			<apply-templates select="following::node()[1]" mode="rdfa-bookmark-literal">
				<with-param name="name" select="$name"/>
			</apply-templates>
		</if>
	</template>

	<template name="resolve-RDF-subject">
		<param name="text" />

		<!-- create RDF subject -->
		<element name="rdf:Description">
			<attribute name="rdf:about">
				<call-template name="resolve-URI-or-Safe-CURIE">
					<with-param name="URIorSafeCURIE" select="@xhtml:about"/>
				</call-template>
			</attribute>
			<if test="@xhtml:content">
				<attribute name="rdfs:label" namespace="http://www.w3.org/2000/01/rdf-schema#">
					<value-of select="$text"/>
				</attribute>
			</if>
			<call-template name="resolve-RDF-property">
				<with-param name="CURIEs" select="@xhtml:property"/>
				<with-param name="text" select="$text"/>
			</call-template>
		</element>
	</template>

	<template name="resolve-RDF-property">
		<param name="CURIEs"/>
		<param name="text"/>

		<variable name="multiplePredicates" select="contains($CURIEs, ' ')"/>
		<variable name="CURIE">
			<choose>
				<when test="$multiplePredicates">
					<value-of select="normalize-space(substring-before($CURIEs, ' '))"/>
				</when>
				<otherwise>
					<value-of select="$CURIEs"/>
				</otherwise>
			</choose>
		</variable>
		<variable name="ns">
			<call-template name="get-namespace">
				<with-param name="CURIE" select="normalize-space(substring-before($CURIEs, ' '))"/>
			</call-template>
		</variable>
		<!-- RDF property -->
		<element name="{$CURIE}" namespace="{$ns}">
			<apply-templates select="@xhtml:datatype"/>
			<choose>
				<when test="@xhtml:content">
					<value-of select="@xhtml:content"/>
				</when>
				<otherwise>
					<value-of select="$text"/>
				</otherwise>
			</choose>
		</element>

		<if test="$multiplePredicates">
			<call-template name="resolve-RDF-property">
				<with-param name="CURIEs" select="normalize-space(substring-after($CURIEs, $CURIE))"/>
				<with-param name="text" select="$text"/>
			</call-template>
		</if>
	</template>

	<template match="@xhtml:datatype">
		<variable name="ns">
			<call-template name="get-namespace">
				<with-param name="CURIE" select="."/>
			</call-template>
		</variable>

		<attribute name="rdf:datatype">
			<value-of select="concat($ns, substring-after(., substring-before(.,':')))"/>
		</attribute>
	</template>

	<template name="get-namespace">
		<param name="CURIE"/>

		<variable name="prefix" select="substring-before($CURIE,':')"/>
		<choose>
			<when test="string-length($prefix)&gt;0">
				<value-of select="ancestor-or-self::*/namespace::*[name()=$prefix][1]"/>
			</when>
			<otherwise> <!-- default namespace -->
				<value-of select="ancestor-or-self::*/namespace::*[name()=''][1]"/>
			</otherwise>
		</choose>
	</template>

	<template name="resolve-URI-or-Safe-CURIE">
		<param name="URIorSafeCURIE"/>
		<choose>
			<when test="starts-with($URIorSafeCURIE,'[')"> <!-- a SafeCURIE -->
				<value-of select="substring-after(substring-before($URIorSafeCURIE,']'),'[')"/>
			</when>
			<when test="starts-with($URIorSafeCURIE,'#')"> <!-- an ODF element -->
				<value-of select="concat($FILE_URL,$URIorSafeCURIE)"/>
			</when>
			<when test="string-length($URIorSafeCURIE)=0"> <!-- an ODF document -->
				<value-of select="$sourceBaseURL"/>
			</when>
			<otherwise> <!-- an IRI -->
				<value-of select="$URIorSafeCURIE"/>
			</otherwise>
		</choose>
	</template>

	<template match="node()"/>
</stylesheet>