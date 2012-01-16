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
<%
	String supporterTitle = getServletContext().getInitParameter( "SUPPORTER_TITLE" );
	if ( null == supporterTitle ) {
		supporterTitle = "OpenDoc Society";
	}
	String supporterURL = getServletContext().getInitParameter( "SUPPORTER_URL" );
	if ( null == supporterURL) {
		supporterURL = "http://opendocsociety.org/";
	}
%>
<p style="float:left; font-weight:bold;">This service is provided to you by <a href="<% out.println( supporterURL ); %>"><% out.println( supporterTitle ); %></a></p>


