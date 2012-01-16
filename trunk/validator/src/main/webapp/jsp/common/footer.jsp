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
<jsp:directive.page contentType="text/html" />
<%
	String _supporterTitle = getServletContext().getInitParameter( "SUPPORTER_TITLE" );
	if ( null == _supporterTitle ) {
		_supporterTitle = "OpenDoc Society";
	}
	String _supporterURL = getServletContext().getInitParameter( "SUPPORTER_URL" );
	if ( null == _supporterURL) {
		_supporterURL = "http://opendocsociety.org/";
	}
	String _supporterIconURL = getServletContext().getInitParameter( "SUPPORTER_ICON_URL" );
	if ( null == _supporterIconURL ) {
		_supporterIconURL = "http://opendocsociety.org/organisation/logo/png/opendocsociety_logo_normal_color-on-trans-200x42/;download";
	}
%>
<div id="footer">
	<div id="supporter-logo" style="float:left">
		<p><a href="<% out.println( _supporterURL ); %>">
				<img title="Sponsored by <% out.println( _supporterTitle ); %>" alt="<% out.println( _supporterTitle ); %>" src="<% out.println( _supporterIconURL ); %> " />
			</a></p>
	</div>
	<div id="smallprint">
		<p style="text-align:left">This service does not cover all conformance criteria of the OpenDocument Format specification. It is not applicable for formal validation proof. Problems reported by this service only indicate that a document may not conform to the specification. It must not be concluded from errors that are reported that the document does not conform to the specification without further investigation of the error report, and it must not be concluded from the absence of error reports that the OpenDocument Format document conforms to the OpenDocument Format specification.
			By any use of this Website, you agree to be bound by these <a href="http://www.apache.org/licenses/LICENSE-2.0.html">Policies and Terms of Use</a>.</p>
	</div>
</div>
