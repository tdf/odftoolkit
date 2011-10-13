<?xml version='1.0' encoding="UTF-8"?>
 <!-- 
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" encoding="UTF-8"/>
	
    <!-- 
        To access contents of a office file content (e.g. meta.xml, styles.xml) 
        this URL has to be added before the inner path.
        
        For instance sourceBaseURL might be:
            file:/E:/cws/multiPkgFileTest-ODFDOM/target/test-classes/test2.odt/
    -->
	<xsl:param name="sourceBaseURL" select="'./'" />    

    <xsl:template match="/">
        <xsl:message>The sourceBaseURL is '<xsl:value-of select="$sourceBaseURL"/>'</xsl:message>
        <concatedFiles>            
            <xsl:copy-of select="document(concat($sourceBaseURL, 'styles.xml'))" />
            <xsl:copy-of select="document(concat($sourceBaseURL, 'meta.xml'))" />        
        </concatedFiles>
    </xsl:template>
</xsl:stylesheet>

