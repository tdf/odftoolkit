<%--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

	  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
--%>
<h1>The ODF Validator</h1>
<p><br /></p>
<h2>Options</h2>
<h3 id="mode">ODF Version</h3>
<ul>
	<li><p><span style="font-weight:bold;">autodetect  </span>: Detects the ODF version of the root document from the given ODF package. ODF documents with version 1.2/1.3 will be validated against conformance, version 1.0/1.1 against none strict. 
			In all other choices the document is validated with respect to the selected OpenDocument version regardless of the version information that is included in the file</p></li>
	<li><p><span style="font-weight:bold;">conformant  </span>: Checks the basic requirements conforming OpenDocument documents must meet.
			This modes considers the version of the checked documents. This means that for OpenDocument v1.2/1.3 documents (or if OpenDocument v1.2/1.3) has been selected) the
			conformance definitions of the OpenDocument v1.2/1.3 specification are taken as basis, while those of the OpenDocument v1.1/v1.0 specification are taken as basis
			for OpenDocument v1.1/v1.0 documents. Please note that not all provisions for conforming documents are checked.</p></li>
	<li><p><span style="font-weight:bold;">extended conformance test</span>: For OpenDocument v1.2/1.3 documents (or if OpenDocument v1.2/1.3 has been selected), the basic requirements of extended conforming ODF documents are checked.
			For OpenDocument v1.0/v1.1 documents (or if OpenDocument v1.0/v1.1 has been selected) this mode equals the <span style="font-weight:bold;">conformance test</span> mode.</p></li>
	<li><p><span style="font-weight:bold;">validation</span>: For OpenDocument v1.1/v1.0 documents (or if OpenDocument v1.1/v1.0 has been selected) the selected document is validated in regard of the OpenDocument v1.1/v1.0 schema.
			For OpenDocument v1.2/1.3 documents (or if OpenDocument v1.2/1.3 has been selected) this mode equals the <span style="font-weight:bold;">conformance test</span> mode.</p></li>
	<li><p><span style="font-weight:bold;">strict validation</span>: For OpenDocument v1.1/v1.0 documents (or if OpenDocument v1.1/v1.0 has been selected) the selected document is validated in regard of the strict OpenDocument v1.1/v1.0 schema.
			For OpenDocument v1.2/1.3 documents (or if OpenDocument v1.2/1.3 has been selected) this mode equals the <span style="font-weight:bold;">conformance test</span> mode.</p></li>
</ul>
<p>For OpenDocument v1.1/v1.0 documents, the <span style="font-weight:bold;">validation</span> and <span style="font-weight:bold;">strict validation</span> tests are more restrictive than a conformance test. Please note that this means that errors may be reported for documents that are actually conforming to the ODF specification.</p>
<p>The <span style="font-weight:bold;">strict validation test</span> is recommended for developers that want to make sure that an OpenDocument v1.0/v1.1 document does not only validate in regards to the ODF schema, but also does not use any extensions. The recommended mode
	for OpenDocument v1.2 documents is <span style="font-weight:bold;">conformance test</span>.</p>
<p>The following items are checked by the validation service:</p>
<ul>
	<li><p>OpenDocument v1.2/1.3 documents</p>
		<ul>
			<li><p>If the test type is <span style="font-weight:bold;">conformance test</span>, and if the file is not a formula file, then the sub files <i>content.xml</i>, <i>styles.xml</i>, <i>meta.xml</i> and <i>settings.xml</i> are  validated with respect to the OpenDocument v1.2/1.3 schema.</p></li>
			<li><p>If the test type is <span style="font-weight:bold;">extended conformance test</span>, and if the file is not a formula file, then the sub files <i>content.xml</i>, <i>styles.xml</i>, <i>meta.xml</i> and <i>settings.xml</i> are pre-processed as described in section 1.4.2.1 of the OpenDocument v1.2 specification (that is <i>foreign elements and attributes</i> are removed), and are then validated with respect to the OpenDocument v1.2/1.3 schema.</p></li>
		</ul>
	</li>
	<li><p>OpenDocument v1.1/1.0 documents</p>
		<ul>
			<li><p>If the test type is <span style="font-weight:bold;">conformance test</span>, and if the file is not a formula file, then the sub files <i>content.xml</i>, <i>styles.xml</i>, <i>meta.xml</i> and <i>settings.xml</i> are pre-processed as described in section 1.5 of the <a href="http://docs.oasis-open.org/office/v1.1/OS/OpenDocument-v1.1.odt">OpenDocument specification</a> (that is <i>foreign elements and attributes</i> are removed), and are then validated with respect to the schema of the selected OpenDocument version.</p></li>
			<li><p>If the test type is <span style="font-weight:bold;">validation</span>, and if the file is not a formula file, then the sub files <i>content.xml</i>, <i>styles.xml</i>, <i>meta.xml</i> and <i>settings.xml</i> are validated with respect to the schema of the selected OpenDocument version. Pre-processing of <i>foreign elements and attributes</i> is not applied.</p></li>
			<li><p>If the test type is <span style="font-weight:bold;">strict validation</span>, and if the file is not a formula file, then the sub files <i>content.xml</i>, <i>styles.xml</i>, <i>meta.xml</i> and <i>settings.xml</i> are validated with respect to the strict schema of the selected OpenDocument version. Pre-processing of <i>foreign elements and attributes</i> is not applied.</p></li>
		</ul>
	</li>
	<li><p>All versions</p>
		<ul>
			<li><p>If the file is a formula file, then the sub file <i>content.xml</i> is validated with respect to the MathML 3.0 W3C RelaxNG schema.  The sub files <i>styles.xml</i>, <i>meta.xml</i> and <i>settings.xml</i> are checked as described for other document types.</p></li>
			<li><p>The file <i>META-INF/manifest.xml</i> is validated with respect to the manifest schema of the selected ODF specification.</p></li>
			<li><p>If the file is an ODF 1.2/1.3 file, then the <i>META-INF/documentsignatures.xml</i> and <i>META-INF/macrosignatures.xml</i> sub files are validated with respect to the digital signatures schema of the ODF 1.2/1.3 specification.</p></li>
			<li><p>For all embedded objects in ODF format, the <i>content.xml</i>, <i>styles.xml</i>, <i>meta.xml</i> and <i>settings.xml</i> are validated as described for the main document above</p></li>
			<li><p>It is checked whether the file itself ans all embedded objects in ODF format contain at least a <i>content.xml</i> or <i>styles.xml</i> sub file.</p></li>
		</ul>
	</li>
</ul>
<h3 id="logging">Logging</h3>
<ul>
	<li><span style="font-weight:bold;">verbose  </span>: Gives out all kind of messages.</li>
	<li><span style="font-weight:bold;">only warnings and errors</span>: Logs all error and warning without info messages.</li>
	<li><span style="font-weight:bold;">only errors</span>: Logs only error messages.</li>
</ul>
<h2 id="details">Implementation Details</h2>
<p>This services is based on the <a href="http://incubator.apache.org/odftoolkit/conformance/ODFValidator.html">Apache ODF Validator</a> and Sun's <a href="https://msv.dev.java.net/">Multi-Schema XML Validator (MSV)</a> is used for all validation tasks.
<p>The following actions take place before or during the validation:</p>
<ul>
	<li><p>A DTD document declaration within a <i>manifest.xml</i> file is ignored. For the logging level <span style="font-weight:bold;">verbose</span>, an information is displayed if this happens<br/><br/>
			<span style="font-weight:bold;">Note:</span> Very early ODF implementations in OpenOffice.org wrongly included a document type declaration. Ignoring the document type enables the validation of the manifest despite of this error.
		</p></li>
	<li><p>A namespace &quot;http://openoffice.org/2001/manifest&quot; within a <i>manifest.xml</i> file is changed to &quot;urn:oasis:names:tc:opendocument:xmlns:manifest:1.0&quot;. For the logging level <span style="font-weight:bold;">verbose</span>, an information is displayed if this happens.<br/><br/>
			<span style="font-weight:bold;">Note:</span> Early ODF implementations in OpenOffice.org used the wrong namespace. Changing it to the correct one enables the validation of the manifest despite the wrong namespace.</p></li>
	<li><p>Namespaces defined in the <a href="http://www.oasis-open.org/committees/download.php/10765/office-spec-1.0-cd-2.pdf">ODF v1.0 Committee Draft 2</a> are replaced with those of the <a href="http://www.oasis-open.org/specs/index.php#opendocumentv1.0">ODF v1.0 OASIS Standard</a>. For the logging level <span style="font-weight:bold;">verbose</span>, an information is displayed if this happens.<br/><br/>
			<span style="font-weight:bold;">Note:</span> Changing the namespaces enables the validation of documents that conform to the ODF v1.0 CD2. Such documents have been saved by OpenOffice.org 1.0 beta versions.</p></li>
	<li><p>The value of <i>draw:points</i> attributes is truncated to 2048 characters. For the logging level <span style="font-weight:bold;">verbose</span>, an information is displayed if this happens.<br/><br/>
			<span style="font-weight:bold;">Note:</span> The truncation of this attribute value avoids a stack overflow in MSV while validating the attribute value against a regular expression.</p></li>
	<li><p>A namespace &quot;http://openoffice.org/2004/database&quot; within a <i>content.xml</i> file is changed to &quot;urn:oasis:names:tc:opendocument:xmlns:database:1.0&quot;, and a namespace &quot;http://openoffice.org/2004/office&quot; within a <i>content.xml</i> file is changed to &quot;urn:oasis:names:tc:opendocument:xmlns:office:1.0&quot;. For the logging level <span style="font-weight:bold;">verbose</span>, an information is displayed if this happens.<br><br>
			<span style="font-weight:bold;">Note:</span> These namespaces were used in OpenOffice.org 2.x database documents, because database documents are included in OpenDocument since version 1.2 only. Changing them enables the validation of  OpenOffice.org 2.x database documents.</p></li>
</ul>

<p>The following schemas are being used:</p>
<ul>
	<li><p>MathML: The MathML 3.0 (2nd edition) schema from <a href="https://www.w3.org/TR/MathML/appendixa.html#parsing.usingrnc">http://www.w3.org/Math/RelaxNG/mathml3/mathml3.rng</a> is used in general for MathML validation.<br/></li>
	<li><p>OpenDocument <a href="http://relaxng.org/">RELAX NG</a> Schemas: The schemas used are those available on the <a href="http://www.oasis-open.org/committees/tc_home.php?wg_abbrev=office#technical">OASIS OpenDocument Technical Committee web page</a><!--, except that <q>&lt;ref name=&quot;string&quot;&gt;</q> is replaced with <q>&lt;text/&gt;</q> when it defines the content model of an element. Reason is that the former leads to (wrong) validation errors within <a href="https://msv.dev.java.net/">MSV</a>-->.</p></li>
</ul>

<p><br /><input type="button" value="Back" onClick="history.go(-1)" /><br /><br /></p>

