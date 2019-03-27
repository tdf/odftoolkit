<strong><a href="../index.html">Documents</a></strong> > <strong><a href="index.html">Cookbook</a></strong> ><strong><a href="Fields.html">Fields</a></strong>
<div class="navigation">
 <ul>
  <li><a href="TextExtractor.html">previous</a></li>
  <li><a href="Forms.html">next</a></li>
 </ul>
</div>
<br/>
<br/><strong><a href="#Variable Field" >Variable Field</a></strong>
<div class="bodytext">
			You can use the following code to create a variable field, and set the value.
</div>
<br/><pre class='code' id="code0">
			<span class='javaclass'>TextDocument</span> doc = <span class='javaclass'>TextDocument</span>.newTextDocument();<br/>
			<span class='javaclass'>Paragraph</span> paragraph = doc.addParagraph("test_con_variable:");<br/>
			<span class='javaclass'>VariableField</span> simpleVariableField = <span class='javaclass'>Fields</span>.createSimpleVariableField(doc, "test_con_variable");<br/>
			simpleVariableField.updateField("true", paragraph.getOdfElement());<br/>
</pre>
<br/><div class="bodytext">
			Following code can be used to set value to variable field, and append it to an ODF element.
</div>
<br/><pre class='code' id="code1">
			simpleVariableField.updateField("user variable content", null);<br/>
			simpleVariableField.displayField(paragraph.getOdfElement());<br/>
</pre>
<br/><br/><strong><a href="#Condition Field" >Condition Field</a></strong>
<div class="bodytext">
			Following code can be used to create a condition field.
</div>
<br/><pre class='code' id="code2">
			<span class='javaclass'>Paragraph</span> newParagraph = doc.addParagraph("<span class='javaclass'>Condition</span> <span class='javaclass'>Field</span> <span class='javaclass'>Test</span>:");<br/>
			<span class='javaclass'>ConditionField</span> conditionField = <span class='javaclass'>Fields</span>.createConditionField(newParagraph.getOdfElement(), "test_con_variable == \"true\"",<br/>
					"trueText", "falseText");<br/>
</pre>
<br/><br/><strong><a href="#Hidden Field" >Hidden Field</a></strong>
<div class="bodytext">
			Following code can be used to create a hidden field.
</div>
<br/><pre class='code' id="code3">
			newParagraph = doc.addParagraph("<span class='javaclass'>Hide</span> <span class='javaclass'>Text</span> <span class='javaclass'>Field</span> <span class='javaclass'>Test</span>:");<br/>
			conditionField = <span class='javaclass'>Fields</span>.createHiddenTextField(newParagraph.getOdfElement(), "test_con_variable == \"true\"", "hiddenText");<br/>
</pre>
<br/><br/><strong><a href="#Cross Reference Field" >Cross Reference Field</a></strong>
<div class="bodytext">
			Following code can be used to create a reference field.
</div>
<br/><pre class='code' id="code4">
			<span class='javaclass'>OdfElement</span> newTextSpanElement = ((<span class='javaclass'>TextPElement</span>)doc.addParagraph("<span class='javaclass'>Reference</span> <span class='javaclass'>Content</span>:").getOdfElement()).newTextSpanElement();<br/>
			newTextSpanElement.setTextContent("<span class='javaclass'>This</span> is a test reference content.");<br/>
			<span class='javaclass'>ReferenceField</span> referenceField = <span class='javaclass'>Fields</span>.createReferenceField(newTextSpanElement, "test-ref");<br/>
</pre>
<br/><div class="bodytext">
			Following code can be used to append a reference field.
</div>
<br/><pre class='code' id="code5">
			referenceField.appendReferenceTo(doc.addParagraph("<span class='javaclass'>User</span> <span class='javaclass'>Reference</span> <span class='javaclass'>Field</span>:").getOdfElement(), <span class='javaclass'>ReferenceField</span>.<span class='javaclass'>DisplayType</span>.<span class='javaclass'>TEXT</span>);<br/>
</pre>
<br/><br/><strong><a href="#Chapter Field" >Chapter Field</a></strong>
<div class="bodytext">
			Following code can be used to create a chapter field.
</div>
<br/><pre class='code' id="code6">
			<span class='javaclass'>ChapterField</span> chapterField = <span class='javaclass'>Fields</span>.createChapterField(doc.addParagraph("<span class='javaclass'>Chapter</span>:").getOdfElement());<br/>
</pre>
<br/><br/><strong><a href="#Title and Subject Field" >Title and Subject Field</a></strong>
<div class="bodytext">
			Following code can be used to create a title field.
</div>
<br/><pre class='code' id="code7">
			<span class='javaclass'>TitleField</span> titleField = <span class='javaclass'>Fields</span>.createTitleField(doc.addParagraph("<span class='javaclass'>The</span> <span class='javaclass'>Title</span>:").getOdfElement());<br/>
</pre>
<br/><div class="bodytext">
			Following code can be used to create a subject field.
</div>
<br/><pre class='code' id="code8">
			<span class='javaclass'>SubjectField</span> subjectField = <span class='javaclass'>Fields</span>.createSubjectField(doc.addParagraph("<span class='javaclass'>The</span> <span class='javaclass'>Subject</span>:").getOdfElement());<br/>
</pre>
<br/><br/><strong><a href="#Author Field" >Author Field</a></strong>
<div class="bodytext">
			Following code can be used to create a author initial field and a author name field.
</div>
<br/><pre class='code' id="code9">
			<span class='javaclass'>AuthorField</span> authorField = <span class='javaclass'>Fields</span>.createAuthorInitialsField(doc.addParagraph("<span class='javaclass'>The</span> initials of the author :").getOdfElement());<br/>
			authorField = <span class='javaclass'>Fields</span>.createAuthorNameField(doc.addParagraph("<span class='javaclass'>Author</span>:").getOdfElement());<br/>
</pre>
<br/><br/><strong><a href="#Page Number Field" >Page Number Field</a></strong>
<div class="bodytext">
			Following code can be used to create a current page number field.
</div>
<br/><pre class='code' id="code10">
			<span class='javaclass'>PageNumberField</span> numberField = <span class='javaclass'>Fields</span>.createCurrentPageNumberField(doc.addParagraph("<span class='javaclass'>Current</span> <span class='javaclass'>Page</span> <span class='javaclass'>Number</span>:").getOdfElement());<br/>
			numberField.setNumberFormat(<span class='javaclass'>NumberFormat</span>.UPPERCASE_LATIN_ALPHABET);<br/>
			numberField.setDisplayPage(<span class='javaclass'>DisplayType</span>.NEXT_PAGE);<br/>
</pre>
<br/><div class="bodytext">
			Following code can be used to create a previous page number and a next page number field.
</div>
<br/><pre class='code' id="code11">
			numberField = <span class='javaclass'>Fields</span>.createPreviousPageNumberField(doc.addParagraph("<span class='javaclass'>Previous</span> <span class='javaclass'>Page</span> <span class='javaclass'>Number</span>:").getOdfElement());<br/>
			numberField = <span class='javaclass'>Fields</span>.createNextPageNumberField(doc.addParagraph("<span class='javaclass'>Next</span> <span class='javaclass'>Page</span> <span class='javaclass'>Number</span>:").getOdfElement());<br/>
</pre>
<br/><br/><strong><a href="#Page Number Field" >Page Number Field</a></strong>
<div class="bodytext">
			Following code can be used to create a page count field, and set the number format.
</div>
<br/><pre class='code' id="code12">
			<span class='javaclass'>PageCountField</span> countField = <span class='javaclass'>Fields</span>.createPageCountField(doc.addParagraph("<span class='javaclass'>Page</span> <span class='javaclass'>Count</span>:").getOdfElement());<br/>
			countField.setNumberFormat(<span class='javaclass'>NumberFormat</span>.UPPERCASE_LATIN_ALPHABET);<br/>
</pre>
<br/><br/><strong><a href="#Date Field" >Date Field</a></strong>
<div class="bodytext">
			Following code can be used to create a date field, and set the format.
</div>
<br/><pre class='code' id="code13">
			<span class='javaclass'>DateField</span> dateField = <span class='javaclass'>Fields</span>.createDateField(doc.addParagraph("<span class='javaclass'>Date</span>:").getOdfElement());<br/>
			dateField.formatDate("yy-<span class='javaclass'>MM</span>-dd");<br/>
</pre>
<br/><br/><strong><a href="#Time Field" >Time Field</a></strong>
<div class="bodytext">
			Following code can be used to create a time field, and set the format.
</div>
<br/><pre class='code' id="code14">
			<span class='javaclass'>TimeField</span> timeField = <span class='javaclass'>Fields</span>.createTimeField(doc.addParagraph("<span class='javaclass'>Time</span>:").getOdfElement());<br/>
			timeField.formatTime("<span class='javaclass'>HH</span>:mm:ss a");<br/>
</pre>
<br/><br/> 
<div class="navigation">
 <ul>
  <li><a href="TextExtractor.html">previous</a></li>
 <li><a href="Forms.html">next</a></li>
  <li><a href="#">top</a></li>
 </ul>
</div>
<link type="text/css" rel="stylesheet" href="cookbook.css"/>
