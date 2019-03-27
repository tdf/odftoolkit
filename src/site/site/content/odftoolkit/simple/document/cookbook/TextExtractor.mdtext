<strong><a href="../index.html">Documents</a></strong> > <strong><a href="index.html">Cookbook</a></strong> ><strong><a href="TextExtractor.html">TextExtractor</a></strong>
<div class="navigation">
 <ul>
  <li><a href="Manipulate TextSearch.html">previous</a></li>
  <li><a href="Fields.html">next</a></li>
 </ul>
</div>
<br/>
<br/><strong><a href="#Get Text" >Get Text</a></strong>
<div class="bodytext">
		TextExtractor provides a method to get the display text of a single element. EditableTextExtractor is a sub class of TextExtractor. 		It provides a method to return all the text that the user can typically edit in a document, including text in cotent.xml, 		header and footer in styles.xml, meta data in meta.xml. 
</div>
<div class="bodytext">
		The following codes use EditableTextExtractor as an example, the text of the document "textExtractor.odt" is extracted for user.		For TextExtractor, it can't extract the text from a TextDocument.
</div>
<br/><pre class='code' id="code0">
		<span class='javaclass'>TextDocument</span> textdoc=(<span class='javaclass'>TextDocument</span>)<span class='javaclass'>TextDocument</span>.loadDocument("textExtractor.odt");<br/>
		<span class='javaclass'>EditableTextExtractor</span> extractorD = <span class='javaclass'>EditableTextExtractor</span>.newOdfEditableTextExtractor(textdoc);<br/>
		<span class='javaclass'>String</span> output = extractorD.getText();<br/>
		<span class='javaclass'>System</span>.out.println(output);<br/>
</pre>
<br/><div class="bodytext">
		In the following codes, the whole document content will be returned.		This operation is the same in TextExtractor.
</div>
<br/><pre class='code' id="code1">
		<span class='javaclass'>OdfElement</span> elem=textdoc.getContentRoot();<br/>
		<span class='javaclass'>EditableTextExtractor</span> extractorE = <span class='javaclass'>EditableTextExtractor</span>.newOdfEditableTextExtractor(elem);<br/>
		<span class='javaclass'>System</span>.out.println(extractorE.getText());<br/>
</pre>
<br/><br/> 
<div class="navigation">
 <ul>
  <li><a href="Manipulate TextSearch.html">previous</a></li>
  <li><a href="Fields.html">next</a></li>
  <li><a href="#">top</a></li>
 </ul>
</div>
<link type="text/css" rel="stylesheet" href="cookbook.css"/>
