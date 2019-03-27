<strong><a href="../index.html">Documents</a></strong> > <strong><a href="index.html">Cookbook</a></strong> ><strong><a href="Manipulate TextSearch.html">Manipulate TextSearch</a></strong>
<div class="navigation">
 <ul>
  <li><a href="Style Handling.html">previous</a></li>
  <li><a href="TextExtractor.html">next</a></li>
 </ul>
</div>
<br/>
<br/><strong><a href="#TextNavigation" >TextNavigation</a></strong>
<div class="bodytext">
		First an ODF text document is needed to test the navigation operation. The following codes shows two main functions of TextNavigation		:hasNext() and getCurrentItem(). The first parameter of the TextNavigation constructor is the matched pattern String, and the second is		the navigation scope.
</div>
<div class="bodytext">
		The result of function getCurrentItem is a Selection object, so a TextSelection object is used here to check out 		the result.Finally the informations of all the String "What" in the text document will be printed out.
</div>
<br/><pre class='code' id="code0">
		<span class='javaclass'>TextDocument</span> textdoc=(<span class='javaclass'>TextDocument</span>)<span class='javaclass'>TextDocument</span>.loadDocument("textsearch.odt");<br/>
		<span class='javaclass'>TextNavigation</span> search1;<br/>
		search1=<span class='modifier'>new</span> <span class='javaclass'>TextNavigation</span>("<span class='javaclass'>What</span>",textdoc);<br/>
		<span class='control'>while</span> (search1.hasNext()) {<br/>
			<span class='javaclass'>TextSelection</span> item1 = (<span class='javaclass'>TextSelection</span>) search1.nextSelection();<br/>
			<span class='javaclass'>System</span>.out.println(item1);<br/>
		}		<br/>
</pre>
<br/><br/><strong><a href="#TextSelection" >TextSelection</a></strong>
<br/><br/>
<a href="#Get Index/Text of TextSelection" >Get Index/Text of TextSelection</a>
<div class="bodytext">
		Run the following codes will get the text content of the searched String "good" and the corresponding index in 		the text document.
</div>
<br/><pre class='code' id="code1">
		<span class='javaclass'>TextNavigation</span> search2=<span class='modifier'>new</span> <span class='javaclass'>TextNavigation</span>("good",textdoc);<br/>
		<span class='control'>while</span>(search2.hasNext()){<br/>
			<span class='javaclass'>TextSelection</span> item2=(<span class='javaclass'>TextSelection</span>) search2.nextSelection();<br/>
			<span class='javaclass'>String</span> searchedText=item2.getText();<br/>
			<span class='basic'>int</span> searchedIndex=item2.getIndex();	<br/>
			<span class='javaclass'>System</span>.out.println(searchedText);<br/>
			<span class='javaclass'>System</span>.out.println(searchedIndex);<br/>
		}		<br/>
</pre>
<br/><a href="#Cut String" >Cut String</a>
<div class="bodytext">
		To cut some specified string in a text document, you can do like the following codes which		cut off all the String "day" in the document.
</div>
<br/><pre class='code' id="code2">
	        search2=<span class='modifier'>new</span> <span class='javaclass'>TextNavigation</span>("day",textdoc);<br/>
		<span class='control'>while</span>(search2.hasNext()){<br/>
			<span class='javaclass'>TextSelection</span> item=(<span class='javaclass'>TextSelection</span>) search2.nextSelection();<br/>
			item.cut();	<br/>
		}		<br/>
</pre>
<br/><a href="#Paste String" >Paste String</a>
<div class="bodytext">
		The following codes paste the string "change" both at the front and at the end of the string "good",		by using the function pasteAtFrontOf() and pasteAtEndOf().
</div>
<br/><pre class='code' id="code3">
		search2 = <span class='basic'>null</span>;<br/>
		search2 = <span class='modifier'>new</span> <span class='javaclass'>TextNavigation</span>("good", textdoc);<br/>
		<span class='javaclass'>TextSelection</span> pastesource = <span class='basic'>null</span>;<br/>
		<span class='javaclass'>TextNavigation</span> search3 = <span class='modifier'>new</span> <span class='javaclass'>TextNavigation</span>("change", textdoc);<br/>
		<span class='control'>if</span> (search3.hasNext()) {<br/>
			pastesource = (<span class='javaclass'>TextSelection</span>) search3.nextSelection();<br/>
		}		<br/>
		<span class='control'>while</span> (search2.hasNext()) {<br/>
			<span class='javaclass'>TextSelection</span> item = (<span class='javaclass'>TextSelection</span>) search2.nextSelection();	<br/>
<span class='comments'>			//paste "change" at the front of "good"</span>
			pastesource.pasteAtFrontOf(item);<br/>
<span class='comments'>			//paste "change" at the end of "good"</span>
			pastesource.pasteAtEndOf(item);<br/>
		}<br/>
</pre>
<br/><a href="#Replace String with String" >Replace String with String</a>
<div class="bodytext">
The following codes replace all the string "replacesource" with the string "replacedest" in the text document.
</div>
<br/><pre class='code' id="code4">
		search2 = <span class='basic'>null</span>;<br/>
		search2 = <span class='modifier'>new</span> <span class='javaclass'>TextNavigation</span>("replacesource", textdoc);<br/>
		<span class='control'>while</span> (search2.hasNext()) {<br/>
		<span class='javaclass'>TextSelection</span> item= (<span class='javaclass'>TextSelection</span>) search2.nextSelection();<br/>
			item.replaceWith("replacedest");<br/>
		}		<br/>
</pre>
<br/><a href="#Replace String with Paragraph" >Replace String with Paragraph</a>
<div class="bodytext">
The following codes replace all the string "replacesource" with a paragraph which can be in the same document or in a different document.
</div>
<br/><pre class='code' id="code5">
		search2 = <span class='basic'>null</span>;<br/>
		search2 = <span class='modifier'>new</span> <span class='javaclass'>TextNavigation</span>("replacesource", textdoc);<br/>
		<span class='control'>while</span> (search2.hasNext()) {<br/>
			<span class='javaclass'>TextSelection</span> item= (<span class='javaclass'>TextSelection</span>) search2.nextSelection();<br/>
			<span class='javaclass'>Paragraph</span> paragraph = textdoc.getParagraphByIndex(0, <span class='basic'>true</span>);<br/>
			item.replaceWith(paragraph);<br/>
		}		<br/>
</pre>
<br/><a href="#Replace String with Image" >Replace String with Image</a>
<div class="bodytext">
The following codes replace all the string "replacesource" with an image. There are two ways to call the replaceWith method. You can use an image URI as parameter.
</div>
<br/><pre class='code' id="code6">
		search2 = <span class='basic'>null</span>;<br/>
		search2 = <span class='modifier'>new</span> <span class='javaclass'>TextNavigation</span>("replacesource", textdoc);<br/>
		<span class='control'>while</span> (search2.hasNext()) {<br/>
			<span class='javaclass'>TextSelection</span> item= (<span class='javaclass'>TextSelection</span>) search2.nextSelection();<br/>
	<span class='comments'>		//Use URI as parameter.</span>
			item.replaceWith(<span class='modifier'>new</span> <span class='javaclass'>URI</span>("image.png"));<br/>
		}		<br/>		
</pre>
<div class="bodytext">
Or you can use an Image object as below:
</div>
<br/><pre class='code' id="code7">
	<span class='comments'>		//Use Image as parameter.</span>
			item.replaceWith(<span class='javaclass'>Image</span>.newImage(para, <span class='modifier'>new</span> <span class='javaclass'>URI</span>("image.png")));<br/>
</pre>
<br/><a href="#Replace String with Table" >Replace String with Table</a>
<div class="bodytext">
The following codes replace all the string "replacesource" with a table named "myTable" in the text document.
</div>
<br/><pre class='code' id="code8">
		search2 = <span class='basic'>null</span>;<br/>
		<span class='javaclass'>Table</span> table = textdoc.getTableByName("myTable");<br/>
		search2 = <span class='modifier'>new</span> <span class='javaclass'>TextNavigation</span>("replacesource", textdoc);<br/>
		<span class='control'>while</span> (search2.hasNext()) {<br/>
			<span class='javaclass'>TextSelection</span> item= (<span class='javaclass'>TextSelection</span>) search2.nextSelection();<br/>
			item.replaceWith(table);<br/>
		}		<br/>
</pre>
<br/><a href="#Replace String with Field" >Replace String with Field</a>
<div class="bodytext">
The following codes replace all the string "replacesource" with a field named "myField" in the text document.
</div>	
<br/><pre class='code' id="code9">
		search2 = <span class='basic'>null</span>;<br/>
		<span class='javaclass'>Field</span> field = textdoc.getVariableFieldByName("myField");<br/>
		search2 = <span class='modifier'>new</span> <span class='javaclass'>TextNavigation</span>("replacesource", textdoc);<br/>
		<span class='control'>while</span> (search2.hasNext()) {<br/>
			<span class='javaclass'>TextSelection</span> item= (<span class='javaclass'>TextSelection</span>) search2.nextSelection();<br/>
			item.replaceWith(field);<br/>
		}		<br/>
</pre>
<br/><a href="#Replace String with TextDocument" >Replace String with TextDocument</a>
<div class="bodytext">
The following codes replace all the string "replacesource" with the contents of the text document named replacedest.odt.
</div> 
<br/><pre class='code' id="code10">
		search2 = <span class='basic'>null</span>;<br/>
		<span class='javaclass'>TextDocument</span> destDoc=<span class='javaclass'>TextDocument</span>.loadDocument("replacedest.odt");	 <br/>
		search2 = <span class='modifier'>new</span> <span class='javaclass'>TextNavigation</span>("replacesource", textdoc);<br/>
		<span class='control'>while</span> (search2.hasNext()) {<br/>
			<span class='javaclass'>TextSelection</span> item= (<span class='javaclass'>TextSelection</span>) search2.nextSelection();<br/>
			item.replaceWith(destDoc);<br/>
		}		<br/>
</pre>
<br/><a href="#Add Reference to String" >Add Reference to String</a>
<div class="bodytext">
		To add reference for a string, you can do like the following codes. Here 		function addHref is used, the parameter of it is an URL object. The codes add network address		"http:www.ibm.com" to the string "network".
</div>
<br/><pre class='code' id="code11">
		search2 = <span class='basic'>null</span>;<br/>
		search2 = <span class='modifier'>new</span> <span class='javaclass'>TextNavigation</span>("network", textdoc);<br/>
		<span class='control'>while</span> (search2.hasNext()) {<br/>
			<span class='javaclass'>TextSelection</span> item = (<span class='javaclass'>TextSelection</span>) search2.nextSelection();<br/>
			item.addHref(<span class='modifier'>new</span> <span class='javaclass'>URL</span>("http://www.ibm.com"));<br/>
		}<br/>
</pre>
<br/><a href="#Add Comment" >Add Comment</a>
<div class="bodytext">
		Adding comment is a useful function when review document, such as spell check and security check. 		You can do it like the following codes. Here, function addComment is used, the first parameter is 		the comment content, the second parameter is the comment author. The codes add a spell suggestion 		before the string "natwork".
</div>
<br/><pre class='code' id="code12">
		<span class='javaclass'>TextNavigation</span> search4 = <span class='modifier'>new</span> <span class='javaclass'>TextNavigation</span>("natwork", textdoc);<br/>
		<span class='control'>while</span> (search4.hasNext()) {<br/>
			<span class='javaclass'>TextSelection</span> selection = (<span class='javaclass'>TextSelection</span>) search4.nextSelection();<br/>
			selection.addComment("<span class='javaclass'>Please</span> change 'natwork' with 'network'.",	"<span class='javaclass'>SpellChecker</span>");<br/>
		}<br/>
</pre>
<br/><br/><strong><a href="#FieldSelection" >FieldSelection</a></strong>
<div class="bodytext">
		Field Selection is a decorator class of TextSelection, which help user replace a text content with field.		Following code can be used to search the document content, and replace with a simple field.
</div>
<br/><pre class='code' id="code13">
		<span class='javaclass'>TextDocument</span> doc = <span class='javaclass'>TextDocument</span>.loadDocument("fieldSample.odt");<br/>
		<span class='javaclass'>TextNavigation</span> search = <span class='modifier'>new</span> <span class='javaclass'>TextNavigation</span>("<span class='javaclass'>ReplaceDateTarget</span>", doc);<br/>
		<span class='control'>while</span> (search.hasNext()) {<br/>
			<span class='javaclass'>TextSelection</span> item = (<span class='javaclass'>TextSelection</span>) search.nextSelection();<br/>
			<span class='javaclass'>FieldSelection</span> fieldSelection = <span class='modifier'>new</span> <span class='javaclass'>FieldSelection</span>(item);<br/>
			fieldSelection.replaceWithSimpleField(<span class='javaclass'>Field</span>.<span class='javaclass'>FieldType</span>.FIXED_DATE_FIELD);<br/>
		}<br/>
</pre>
<br/><div class="bodytext">
		Following code can be used to search the document content, and replace with a condition field.
</div>
<br/><pre class='code' id="code14">
		<span class='javaclass'>TextSelection</span> item = (<span class='javaclass'>TextSelection</span>) search.nextSelection();<br/>
		<span class='javaclass'>FieldSelection</span> fieldSelection = <span class='modifier'>new</span> <span class='javaclass'>FieldSelection</span>(item);<br/>
		fieldSelection.replaceWithConditionField("test_con_variable == \"true\"", "trueText", "falseText");<br/>
</pre>
<br/><div class="bodytext">
		Following code can be used to replace with a hidden field.
</div>
<br/><pre class='code' id="code15">
		fieldSelection.replaceWithHiddenTextField("test_con_variable == \"true\"", "hiddenText");<br/>
</pre>
<br/><div class="bodytext">
		Following code can be used to replace with a reference field.
</div>
<br/><pre class='code' id="code16">
		<span class='javaclass'>ReferenceField</span> referenceField = <span class='javaclass'>Fields</span>.createReferenceField(doc.addParagraph("span").getOdfElement(), "selection-test-ref");<br/>
		fieldSelection.replaceWithReferenceField(referenceField, <span class='javaclass'>ReferenceField</span>.<span class='javaclass'>DisplayType</span>.<span class='javaclass'>TEXT</span>);<br/>
</pre>
<br/><div class="bodytext">
		Following code can be used to replace with a variable field.
</div>
<br/><pre class='code' id="code17">
		<span class='javaclass'>VariableField</span> userVariableField = <span class='javaclass'>Fields</span>.createUserVariableField(doc, "selection_user_variable", "test");<br/>
		fieldSelection.replaceWithVariableField(userVariableField);<br/>
</pre>
<br/><br/><strong><a href="#TextStyleNavigation" >TextStyleNavigation</a></strong>
<div class="bodytext">
		Similar with TextNavigation, TextStyleNavigation has two main functions: getCurrentItem() and		hasNext() which is shown in the following codes. The input parameter of TextStyleNavigation constructor		is a map of OdfStyleProperty, so here a TreeMap "searchProps" which contains the Style properties is used		to construct the TextStyleNavigation object.
</div>
<br/><pre class='code' id="code18">
		<span class='javaclass'>TextStyleNavigation</span> searchStyle1;<br/>
		<span class='javaclass'>TreeMap</span>&lt;<span class='javaclass'>OdfStyleProperty</span>, <span class='javaclass'>String</span>&gt; searchProps = <span class='modifier'>new</span> <span class='javaclass'>TreeMap</span>&lt;<span class='javaclass'>OdfStyleProperty</span>, <span class='javaclass'>String</span>&gt;();<br/>
		searchProps.put(<span class='javaclass'>StyleTextPropertiesElement</span>.<span class='javaclass'>FontName</span>, "<span class='javaclass'>Times</span> <span class='javaclass'>New</span> Roman1");<br/>
		searchProps.put(<span class='javaclass'>StyleTextPropertiesElement</span>.<span class='javaclass'>FontSize</span>, "16pt");<br/>
		searchStyle1 = <span class='modifier'>new</span> <span class='javaclass'>TextStyleNavigation</span>(searchProps, textdoc);<br/>
		<span class='control'>if</span> (searchStyle1.hasNext()) {<br/>
			<span class='javaclass'>TextSelection</span> itemstyle = (<span class='javaclass'>TextSelection</span>) searchStyle1.nextSelection();<br/>
			<span class='javaclass'>System</span>.out.print((itemstyle.toString()));<br/>
		}<br/>
</pre>
<br/><br/> 
<div class="navigation">
 <ul>
  <li><a href="Style Handling.html">previous</a></li>
  <li><a href="TextExtractor.html">next</a></li>
  <li><a href="#">top</a></li>
 </ul>
</div>
<link type="text/css" rel="stylesheet" href="cookbook.css"/>
