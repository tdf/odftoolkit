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
<%@ page import="
		 java.io.UnsupportedEncodingException,
		 java.io.PrintStream,
		 java.io.InputStream,
		 java.io.ByteArrayOutputStream,
		 java.io.File,
		 org.apache.commons.fileupload.servlet.ServletFileUpload,
		 org.apache.commons.fileupload.FileItemIterator,
		 org.apache.commons.fileupload.FileItemStream,
		 org.apache.commons.fileupload.util.Streams,
		 org.odftoolkit.odfvalidator.ODFValidator,
		 org.odftoolkit.odfvalidator.Logger,
		 org.odftoolkit.odfvalidator.Configuration,
		 org.odftoolkit.odfvalidator.OdfValidatorMode,
		 org.odftoolkit.odfvalidator.ValidationMessageCollectorErrorFilter,
		 org.odftoolkit.odfvalidator.OdfVersion" %>

<%
int loggingSelection = 0;
int modeSelection = 0;
if(ServletFileUpload.isMultipartContent(request)) {
%><h1>ODF Validator Result Page</h1><%
	ServletFileUpload upload = new ServletFileUpload();
	FileItemIterator iter = upload.getItemIterator(request);
	ODFValidator validator = null;
	boolean result = false;
	while (iter.hasNext()) {
		FileItemStream item = iter.next();
		InputStream stream = item.openStream();
		/* The file item contains a simple name-value pair of a form field */
		if(item.isFormField()) {
			if("loggingSelection".equals(item.getFieldName())) {
				loggingSelection = Integer.parseInt(Streams.asString(stream));
			} else if("modeSelection".equals(item.getFieldName())) {
				modeSelection = Integer.parseInt(Streams.asString(stream));
			}
		} else {
			/* The file item contains an uploaded file */
			if((stream != null) && (item.getName() != null) && (!item.getName().equals(""))) {
				Logger.LogLevel aLogLevel = null;
				switch (loggingSelection) {
					case 2:
						aLogLevel = Logger.LogLevel.ERROR;
						break;
					case 1:
						aLogLevel = Logger.LogLevel.WARNING;
						break;
					default:
						aLogLevel = Logger.LogLevel.INFO;
						break;
				}
				OdfVersion aOdfVersion = null;
				OdfValidatorMode odfValidatorMode = null;
				switch (modeSelection) {
					case 6:
						aOdfVersion = OdfVersion.V1_0;
						odfValidatorMode = OdfValidatorMode.VALIDATE;
						break;
					case 4:
						aOdfVersion = OdfVersion.V1_1;
						odfValidatorMode = OdfValidatorMode.VALIDATE;
						break;
					case 5:
						aOdfVersion = OdfVersion.V1_0;
						odfValidatorMode = OdfValidatorMode.VALIDATE_STRICT;
						break;
					case 3:
						aOdfVersion = OdfVersion.V1_1;
						odfValidatorMode = OdfValidatorMode.VALIDATE_STRICT;
						break;
					case 2:
						odfValidatorMode = OdfValidatorMode.EXTENDED_CONFORMANCE;
						aOdfVersion = OdfVersion.V1_2;
						break;
					case 1:
						aOdfVersion = OdfVersion.V1_2;
						odfValidatorMode = OdfValidatorMode.CONFORMANCE;
						break;
					case 7:
						aOdfVersion = OdfVersion.V1_3;
						odfValidatorMode = OdfValidatorMode.CONFORMANCE;
						break;
					case 8:
						odfValidatorMode = OdfValidatorMode.EXTENDED_CONFORMANCE;
						aOdfVersion = OdfVersion.V1_3;
						break;
					default:
						odfValidatorMode = OdfValidatorMode.CONFORMANCE;
						break;
				}				
				validator = new ODFValidator(null, aLogLevel, true, aOdfVersion);
				ByteArrayOutputStream bout = null;
				if(validator != null) {
					out.println("<h2>Result for " + item.getName() + "</h2>");
					bout = new ByteArrayOutputStream();
					PrintStream pout = new PrintStream(bout, true, "UTF-8");
					ValidationMessageCollectorErrorFilter filter = new ValidationMessageCollectorErrorFilter();
					try {
						result = !validator.validateStream(pout, stream, item.getName() , odfValidatorMode, filter);
					} catch (Exception e) {
						e.printStackTrace(pout);
						result = false;
					}
				}				
				if(result) {
					out.println("<p class='valid'>The document is " + odfValidatorMode.toString()+ " ODF" + validator.getOdfVersion() + "!");
				} else {
					out.println("<p class='invalid'>The document is NOT " + odfValidatorMode.toString()+ " ODF" + validator.getOdfVersion() + "!");
				}
				String s = null;
				try {
					s = new String(bout.toByteArray(), "UTF-8");
					s = s.replaceAll("&", "&amp;");
					s = s.replaceAll("<", "&lt;");
					s = s.replaceAll(">", "&gt;");
					s = s.replaceAll("&lt;span class='info'&gt;", "<span class='info'>");
					s = s.replaceAll("&lt;span class='warning'&gt;", "<span class='warning'>");
					s = s.replaceAll("&lt;span class='error'&gt;", "<span class='error'>");
					s = s.replaceAll("&lt;span class='fatalError'&gt;", "<span class='fatalError'>");
					s = s.replaceAll("&lt;span class='filePath'&gt;", "<span class='filePath'>");
					s = s.replaceAll("&lt;span class='messageType'&gt;", "<span class='messageType'>");					
					s = s.replaceAll("&lt;/span&gt;&lt;/br&gt;", "</span></br>");				
					s = s.replaceAll("&lt;/span&gt;", "</span>");
				} catch (UnsupportedEncodingException use) {
					out.println(use);
				}
				if(s != null && (s.length() > 0)) {					
					out.println("</p><h3>Details:<br/></h3><div class='validationResult'>" + s + "</div>");
				} else {
					out.println("</p>");
				}
			}
		}
	}
%><p><br/><input type="button" value="Back" onClick="history.go(-1)" /></p><%
} else {
	%><h1>ODF Validator</h1><p>This service checks conformance of ODF documents based on their OpenDocument Format specification. It does not cover all conformance criteria, yet (see <a href="info.html#details">implementation details</a>).</p>
<form id="validationForm" action="<% out.println(request.getRequestURI()); %>" enctype="multipart/form-data" method="post">
	<p class="selection"><a href="info.html">ODF Version</a>:<br/>
		<select name="modeSelection" size="1" onChange="javascript: return setconfig();">
			<option value="0" selected="">auto-detect</option>
			<option value="7">OASIS ODF 1.3 (conforming)</option>
			<option value="8">OASIS ODF 1.3 (extended conforming)</option>
			<option value="1">OASIS ODF 1.2 (conforming)</option>
			<option value="2">OASIS ODF 1.2 (extended conforming)</option>
			<option value="3">OASIS ODF 1.1 (strict)</option>
			<option value="4">OASIS ODF 1.1</option>
			<option value="5">OASIS ODF 1.0 - ISO/IEC 26300 (strict)</option>
			<option value="6">OASIS ODF 1.0 - ISO/IEC 26300</option>
		</select>
		<br/>
	</p>
	<p class="selection"><a href="info.html#logging">Logging</a>:
		<br/><select name="loggingSelection" size="1">
			<option value="0" selected="">verbose</option>
			<option value="1">only warnings and errors</option>
			<option value="2">only errors</option>
		</select>
		<br/>
		<br/>
		Choose ODF documents for validation:		
		<br/>
		<!-- HTML 5 Draft 20110914 does not allow maxlength attribute at input element:
			maxlength="100000" -->
		<input name="File" multiple="multiple" type="file" />
		<br/>
		<br/>
		<br/>
		<!-- HTML 5 Draft 20110914 does not allow alt attribute at input element:
			alt="Press button to upload your documents and get them validated." -->
		<input type="submit" value="Validate"  />
		<!-- HTML 5 Draft 20110914 does not allow alt attribute at input element:
			alt="To reset this formular press this button." -->		
		<input type="reset"  value="Reset"  />
	</p>
</form>
<%
}
%>
