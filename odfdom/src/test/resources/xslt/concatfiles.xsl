<?xml version='1.0' encoding="UTF-8"?>
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

