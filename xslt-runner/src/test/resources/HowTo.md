# How to create RNG files as HTML files with Line Numbers

1. Rename RNG file with HTML suffix and open it in browser.
2. Choice from the context menu show source
* Original RNG file opened in Chrome browser has lines as

        <tr>
            <td class="line-number" value="61"></td>
            <td class="line-content">&lt;define name="ds-signature"&gt;</td>
        </tr>
    
* RNG with named with HTML suffix opened in Chrome browser is providing more styles:

        <tr>
            <td class="line-number" value="61"></td>
            <td class="line-content">
            <span class="html-tag">&lt;define 
                <span class="html-attribute-name">name</span>="
                <span class="html-attribute-value">ds-signature</span>"&gt;</span>
            </td>
        </tr>
    
3. From the 'source' window, choose again from the context menu 'inspect'
2. From the shown nodes, choose the body element and from the context menu choose 'copy element'
3. Copy it into the [template](template.html)
4. Make it XML by replacing &lt;br&gt; with &lt;br/&gt; (likely that's all)
4. Change correct indent from tab to 4 Space (only necessary for ODF 1.2 parts - easy with UltraEdit ^t)
5. Place the created HTML into the appropriate folder, e.g. '[xslt-runner/src/test/resources/odf13](../../../xslt-runner/src/test/resources/odf13)'
6. If necessary adopt the new input path in the [pom.xml](../../../pom.xml)
7. Do XSL transformation via Maven 'mvn clean install' to add IDs for RelaxNG Defines & HRefs for RelaxNG refs 
8. Copy the [sample_xslt/rng/view-source.css](../../../sample_xslt/rng/view-source.css) to the new created output target\generated-resources\xml\xslt) 

**NOTE:** 
Original CSS was downloaded from [Chromium sources](https://chromium.googlesource.com/chromium/blink/+/72fef91ac1ef679207f51def8133b336a6f6588f/Source/core/css/view-source.css?autodive=0%2F%2F%2F)
