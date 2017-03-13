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
	String appName = getServletContext().getInitParameter( "APPLICATION_TITLE" );
	if ( null == appName ) {
		appName = "ODFToolkit";
	}
	String favIconURL = getServletContext().getInitParameter( "SUPPORTER_FAVICON_URL" );
	if ( null == favIconURL ) {
		favIconURL = "http://www.opendocsociety.org/favicon/;download";
	}
%>
<head>
	<title><% out.println( appName ); %></title>
	<meta http-equiv="content-type" content="text/html; charset=windows-1252" />
	<meta name="keywords" content="OpenDocument ODF Validator ODFToolkit ODFValidator"/>
	<meta name="description" content="ODFToolkit"/>
	<!-- Icon -->
	<link type="image/x-icon" href="<% out.println( favIconURL ); %>" rel="shortcut icon"/>
	<style type="text/css" media="screen, projection"><!--
		div#footer {
			font-family: Verdana, Arial, Helvetica, sans-serif;
			font-size: x-small;
		}
		#footer {
			margin: 1em 12px 1.5em 10px;
			padding-top: 1em;
		}
		#footer a:link, #footer a:visited {
			color: #0033CC;
			text-decoration: underline;
		}
		#footer a:hover, #footer a:active {
			color: #698ed1;
			text-decoration: none;
		}
		img {
			border: none;
		}

		#footer {
			float: left;
			display: block;
			margin: 0px;
			padding: 0px;
			width: 100%;
			margin-top: -1px;
			color: #777;
			border-top: solid 1px #ddd;
			background-image: none;
		}
		#footer .horizontalmenu {
			padding-top: 10px;
			font-size: 1.2em;
		}
		#footer li {
			display: inline;
			/* spacing between the links */
			padding: 0em 1em;
		}
		#sponsor {
			float: left;
			margin:.5em;
			margin-bottom: 20px;
		}
		#logo div img { float: left; padding: 0px 0 40px 15px; }

		p.selection {
			font-weight: bold;
		}

		div.validationResult {
			line-height: 150%; font-size: 1.2em; font-family: Arial, helvetica, sans-serif;
		}

		p.valid {
			background-color: lime;font-size: 2em; padding: 0.5cm;font-weight: bold;
		}

		p.invalid {
			background-color: red;font-size: 2em; padding: 0.5cm;font-weight: bold;
		}

		span.info {
			font-size: 1.1em;
		}

		span.info > span.messageType, span.info > span.filePath {

		}	

		span.warning > span.messageType, span.warning > span.filePath {
			background-color: #ffA500;
		}

		span.error > span.messageType, span.fatalError > span.messageType, span.error > span.filePath, span.fatalError > span.filePath {
			background-color: red;
		}		

		span.messageType {
			font-weight: bold;
		}	

		p.selection {
			font-weight: bold;
		}


		#footer p {
			margin-top: 1px;
			color: #555;
		}

		select {
			width: 8.3cm;
		}

		h1 {
			font-size: 2em;
		}

		h3 {
			font-size: 1.6em;
		}

		h1, h2 {
			border-bottom-width: 1px;
			border-bottom-style: solid;
			border-bottom-color: rgb(170,170,170);
			font-weight: normal;
		}

		h1, h2, h3, h4, h5, h6 {
			margin-bottom: 0.6em;
			padding-bottom: 0.2em;
			padding-top: 0.6em;
			font-family: Arial, helvetica, sans-serif;
		}
		.contentpart {
			font-family: Arial, helvetica, sans-serif;
			line-height: 1.2em;
			margin-top: 0.4em;
			margin-bottom: 0.6em;
			font-size: 0.8em;
		}

		-->
	</style>
</head>
