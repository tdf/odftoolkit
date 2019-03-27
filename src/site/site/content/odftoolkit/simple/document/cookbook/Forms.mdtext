<strong><a href="../index.html">Documents</a></strong> > <strong><a href="index.html">Cookbook</a></strong> ><strong><a href="Forms.html">Forms</a></strong>
<div class="navigation">
 <ul>
  <li><a href="Fields.html">previous</a></li>
  <li><a href="Manipulate Metadata.html">next</a></li>
 </ul>
</div>
<br/>
<br/><strong><a href="#Form" >Form</a></strong>
<div class="bodytext">
			Since version 0.8, new APIs are added to support forms. Because controls are implementation-dependent, the default form provider will follow the capability defined by OpenOffice.org, it may not be fully compatible with other ODF editors.	You can use the following code to create a form.
</div>
<br/><pre class='code' id="code0">
			<span class='javaclass'>TextDocument</span> textDoc = <span class='javaclass'>TextDocument</span>.newTextDocument();<br/>
			<span class='javaclass'>Form</span> form = textDoc.createForm("Form1");<br/>
</pre>
<br/><div class="bodytext">
			Following code shows how to get a form in a text document and remove it.
</div>
<br/><pre class='code' id="code1">
			<span class='javaclass'>Iterator</span>&lt;<span class='javaclass'>Form</span>&gt; iterator = textDoc.getFormIterator();<br/>
			<span class='modifier'>while</span> (iterator.hasNext()) {<br/>
				deleteForm = iterator.next();<br/>
				if (deleteForm.getFormName().equals("Form1"))<br/>
					<span class='javaclass'>break</span>;<br/>
			}<br/>
			textDoc.removeForm(deleteForm);<br/>
</pre>
<br/><br/><strong><a href="#Controls">Controls</a></strong>
<br/><br/><a href="#Button">Button</a>
<br/><div class="bodytext">
			Below codes will create a button and add it to the text document with a paragraph as the anchor position. The FrameRectangle specifies an area and the position of this button. The last two parameters are used to specify the control name and the initialized label value.
</div>
<br/><pre class='code' id="code2">
			<span class='javaclass'>Paragraph</span> para = doc.addParagraph("Add form button here:");<br/>
			<span class='javaclass'>FrameRectangle</span> btnRtg = new <span class='javaclass'>FrameRectangle</span>(0.5, 2, 2.9433, 0.5567, <span class='javaclass'>SupportedLinearMeasure.IN</span>);<br/>
			<span class='javaclass'>Button</span> btn = (<span class='javaclass'>Button</span>)form.createButton(para, btnRtg, "Button1", "Push Button 1");<br/>
</pre>
<br/><div class="bodytext">
			You can get an iterator of the button as follows.
</div>
<br/><pre class='code' id="code3">
			<span class='javaclass'>Iterator</span>&lt;<span class='javaclass'>FormControl</span>&gt; iterator = <span class='javaclass'>Button</span>.getSimpleIterator(form);<br/>
			<span class='modifier'>while</span>  (iterator.hasNext()) {<br/>
				btn = (<span class='javaclass'>Button</span>) iterator.next();<br/>
			}<br/>
</pre>
<br/><a href="#Label">Label</a>
<br/><div class="bodytext">
			Below codes will create a label and add it to the text document with a paragraph as the anchor position. 
</div>
<br/><pre class='code' id="code4">
			<span class='javaclass'>Paragraph</span> para = doc.addParagraph("Add form label here:");<br/>
			<span class='javaclass'>FrameRectangle</span> labelRtg = new <span class='javaclass'>FrameRectangle</span>(0.5, 1.2553, 1.2, 0.5, <span class='javaclass'>SupportedLinearMeasure.IN</span>);<br/>
			<span class='javaclass'>Label</span> label = (<span class='javaclass'>Label</span>) form.createLabel(doc, labelRtg, "Label2","This is a label.");<br/>
</pre>
<br/><div class="bodytext">
			You can get an iterator of the label as follows.
</div>
<br/><pre class='code' id="code5">
			<span class='javaclass'>Iterator</span>&lt;<span class='javaclass'>FormControl</span>&gt; iterator = <span class='javaclass'>Label</span>.getSimpleIterator(form);<br/>
			<span class='modifier'>while</span>  (iterator.hasNext()) {<br/>
				label = (<span class='javaclass'>Label</span>) iterator.next();<br/>
			}
</pre>
<br/><a href="#TextBox">TextBox</a>
<br/><div class="bodytext">
			Below codes will create a text box and add it to the text document with a paragraph as the anchor position. The last parameter are used to specify whether this text box supports multiple-line input.
</div>
<br/><pre class='code' id="code6">
			<span class='javaclass'>Paragraph</span> para = doc.addParagraph("Add text box here:");<br/>
			<span class='javaclass'>FrameRectangle</span> textBoxRtg = new <span class='javaclass'>FrameRectangle</span>(0.5, 0.2846, 2.9432, 0.8567, <span class='javaclass'>SupportedLinearMeasure.IN</span>);<br/>
			<span class='javaclass'>TextBox</span> textbox = (<span class='javaclass'>TextBox</span>)form.createTextBox(para, textBoxRtg, "TextBox1", "Please input your value here", <span class='basic'>true</span>);<br/>
</pre>
<br/><div class="bodytext">
			You can get an iterator of the text box as follows.
</div>
<br/><pre class='code' id="code7">
			<span class='javaclass'>Iterator</span>&lt;<span class='javaclass'>FormControl</span>&gt; iterator = <span class='javaclass'>TextBox</span>.getSimpleIterator(form);<br/>
			<span class='modifier'>while</span> (iterator.hasNext()) {<br/>
				textBox = (<span class='javaclass'>TextBox</span>) iterator.next();<br/>
			}<br/>
</pre>
<br/><a href="#ListBox">ListBox</a>
<br/><div class="bodytext">
			Below codes will create a list box and add it to the text document with a paragraph as the anchor position. The fourth parameter is used to specify whether this list box supports multiple selection. And the last parameter is used to specify the visibility of a drop-down list.
</div>
<br/><pre class='code' id="code8">
			<span class='javaclass'>Paragraph</span> para = doc.addParagraph("Add list box here:");<br/>
			<span class='javaclass'>FrameRectangle</span> listBoxRtg = new <span class='javaclass'>FrameRectangle</span>(0.5752, 0.1429, 2.3307, 0.8398, <span class='javaclass'>SupportedLinearMeasure.IN</span>);<br/>
			<span class='javaclass'>ListBox</span> listBox = (<span class='javaclass'>ListBox</span>)form.createListBox(para, listBoxRtg, "ListBox", true, false);<br/>
</pre>
<br/><div class="bodytext">
			You can get an iterator of the list box as follows.
</div>
<br/><pre class='code' id="code9">
			<span class='javaclass'>Iterator</span>&lt;<span class='javaclass'>FormControl</span>&gt; iterator = <span class='javaclass'>ListBox</span>.getSimpleIterator(form);<br/>
			<span class='modifier'>while</span>  (iterator.hasNext()) {<br/>
				listBox = (<span class='javaclass'>ListBox</span>) iterator.next();<br/>
			}<br/>
</pre>
<br/><a href="#ComboBox">ComboBox</a>
<br/><div class="bodytext">
			Below codes will create a combo box and initialize its entry list with a string array.	 The last parameter is used to specify the visibility of a drop-down list.
</div>
<br/><pre class='code' id="code10">
			<span class='javaclass'>FormControl</span> comboBox = form.createComboBox(doc, <span class='modifier'>new</span> <span class='javaclass'>FrameRectangle</span>(0.7972, 1.2862, 2.4441, 0.2669, <span class='javaclass'>SupportedLinearMeasure.IN</span>), "combo1", "dd", <span class='basic'>true</span>);<br/>
			<span class='javaclass'>String</span>[] items = { "aa", "bb", "cc", "dd", "ee", "ff", "gg", "hh", "ii", "jj" };<br/>
			((<span class='javaclass'>ComboBox</span>) comboBox).addItems(items);<br/>
</pre>
<br/><div class="bodytext">
			You can get an iterator of the combo box as follows.
</div>
<br/><pre class='code' id="code11">
			<span class='javaclass'>Iterator</span>&lt;<span class='javaclass'>FormControl</span>&gt; iterator = <span class='javaclass'>ComboBox</span>.getSimpleIterator(form);<br/>
			<span class='modifier'>while</span>  (iterator.hasNext()) {<br/>
				comboBox = (<span class='javaclass'>ComboBox</span>) iterator.next();<br/>
			}<br/>
</pre>
<br/><a href="#RadioButton">RadioButton</a>
<br/><div class="bodytext">
			Below codes will create three radio buttons in a group, named "Group1", but each of them has different item value, from 1 to 3.
</div>
<br/><pre class='code' id="code12">
			<span class='javaclass'>FrameRectangle</span> radioRtg = <span class='modifier'>new</span> <span class='javaclass'>FrameRectangle</span>(0.7972, 1.2862, 2.4441, 0.2669, <span class='javaclass'>SupportedLinearMeasure.IN</span>);<br/>
			<span class='javaclass'>RadioButton</span> radiobutton = (<span class='javaclass'>RadioButton</span>) form.createRadioButton(doc, radioRtg, "Group1", "RadioButton 1", "1");<br/>
			<span class='javaclass'>RadioButton</span> radiobutton = (<span class='javaclass'>RadioButton</span>) form.createRadioButton(doc, radioRtg, "Group1", "RadioButton 2", "2");<br/>
			<span class='javaclass'>RadioButton</span> radiobutton = (<span class='javaclass'>RadioButton</span>) form.createRadioButton(doc, radioRtg, "Group1", "RadioButton 3", "3");<br/>
</pre>
<br/><div class="bodytext">
			You can get an iterator of the radio button as follows.
</div>
<br/><pre class='code' id="code13">
			<span class='javaclass'>Iterator</span>&lt;<span class='javaclass'>FormControl</span>&gt; iterator = <span class='javaclass'>RadioButton</span>.getSimpleIterator(form);<br/>
			<span class='modifier'>while</span>  (iterator.hasNext()) {<br/>
				radioBtn = (<span class='javaclass'>RadioButton</span>) iterator.next();<br/>
			}<br/>
</pre>
<br/><a href="#CheckBox">CheckBox</a>
<br/><div class="bodytext">
			Below code will create a check box and initialize its label and value in the last two parameters.
</div>
<br/><pre class='code' id="code14">
			<span class='javaclass'>FrameRectangle</span> checkBoxRtg = new <span class='javaclass'>FrameRectangle</span>(0.7972, 1.2862, 2.4441, 0.2669, <span class='javaclass'>SupportedLinearMeasure.IN</span>);<br/>
			<span class='javaclass'>CheckBox</span> checkBox = (<span class='javaclass'>CheckBox</span>) form.createCheckBox(doc, checkBoxRtg, "CheckBox 1", "This is choice 1", "1");<br/>
</pre>
<br/><div class="bodytext">
			You can get an iterator of the check box as follows.
</div>
<br/><pre class='code' id="code15">
			<span class='javaclass'>Iterator</span>&lt;<span class='javaclass'>FormControl</span>&gt; iterator = <span class='javaclass'>CheckBox</span>.getSimpleIterator(form);<br/>
			<span class='modifier'>while</span>  (iterator.hasNext()) {<br/>
				checkBox = (<span class='javaclass'>CheckBox</span>) iterator.next();<br/>
			}<br/>
</pre>
<br/><a href="#Date Field">Date Field</a>
<br/><div class="bodytext">
			Below code will create a date field, set the spin button and drop-down button visible and also set the date format as "12/07/15".
</div>
<br/><pre class='code' id="code16">
			<span class='javaclass'>FrameRectangle</span> fieldRtg = <span class='basic'>new</span> <span class='javaclass'>FrameRectangle</span>(0.5, 2.0, 2.9433, 0.5567, <span class='javaclass'>SupportedLinearMeasure.IN</span>);<br/>
			<span class='javaclass'>DateField</span> dateField = (<span class='javaclass'>DateField</span>)form.createDateField(para, fieldRtg, "DateField", "20120715");<br/>
			dateField.setSpinButonVisible(<span class='basic'>true</span>);<br/>
			dateField.setDropDownVisible(<span class='basic'>true</span>);<br/>
			dateField.formatDate("yy/MM/dd", <span class='javaclass'>Locale.US</span>);<br/>
</pre>
<br/><div class="bodytext">
			You can get an iterator of the date field as follows.
</div>
<br/><pre class='code' id="code17">
			<span class='javaclass'>Iterator</span>&lt;<span class='javaclass'>FormControl</span>&gt; iterator = <span class='javaclass'>DateField</span>.getSimpleIterator(form);<br/>
			<span class='modifier'>while</span>  (iterator.hasNext()) {<br/>
				dateField = (<span class='javaclass'>DateField</span>) iterator.next();<br/>
			}<br/>
</pre>
<br/><a href="#Time Field">Time Field</a>
<br/><div class="bodytext">
			Below code will create a time field, set the spin button visible and set the date format as "15:23:40".
</div>
<br/><pre class='code' id="code18">
			<span class='javaclass'>FrameRectangle</span> fieldRtg = <span class='basic'>new</span> <span class='javaclass'>FrameRectangle</span>(0.5, 2.0, 2.9433, 0.5567, <span class='javaclass'>SupportedLinearMeasure.IN</span>);<br/>
			<span class='javaclass'>TimeField</span> timeField = (<span class='javaclass'>TimeField</span>) form.createTimeField(para, fieldRtg, "TimeField", "15234000");<br/>
			timeField.setSpinButonVisible(<span class='basic'>true</span>);<br/>
			timeField.formatTime("HH:mm a", <span class='javaclass'>Locale.US</span>);<br/>
</pre>
<br/><div class="bodytext">
			You can get an iterator of the time field as follows.
</div>
<br/><pre class='code' id="code19">
			<span class='javaclass'>Iterator</span>&lt;<span class='javaclass'>FormControl</span>&gt; iterator = <span class='javaclass'>TimeField</span>.getSimpleIterator(form);<br/>
			<span class='modifier'>while</span>  (iterator.hasNext()) {<br/>
				timeField = (<span class='javaclass'>TimeField</span>) iterator.next();<br/>
			}<br/>
</pre>
<br/><a href="#Numeric Field">Numeric Field</a>
<br/><div class="bodytext">
			Below code will create a numeric field, set the spin button visible and set the decimal accurcy to 3.
</div>
<br/><pre class='code' id="code20">
			<span class='javaclass'>FrameRectangle</span> fieldRtg = <span class='basic'>new</span> <span class='javaclass'>FrameRectangle</span>(0.5, 2.0, 2.9433, 0.5567, <span class='javaclass'>SupportedLinearMeasure.IN</span>);<br/>
			<span class='javaclass'>NumericField</span> numericField = (<span class='javaclass'>NumericField</span>) form.createNumericField(para, fieldRtg, "NumericField", "-154.3567");<br/>
			numericField.setDecimalAccuracy(3);<br/>
			numericField.setSpinButonVisible(<span class='basic'>true</span>);<br/>
</pre>
<br/><div class="bodytext">
			You can get an iterator of the numeric field as follows.
</div>
<br/><pre class='code' id="code21">
			<span class='javaclass'>Iterator</span>&lt;<span class='javaclass'>FormControl</span>&gt; iterator = <span class='javaclass'>NumericField</span>.getSimpleIterator(form);<br/>
			<span class='modifier'>while</span>  (iterator.hasNext()) {<br/>
				numericField = (<span class='javaclass'>NumericField</span>) iterator.next();<br/>
			}<br/>
</pre>
<br/><a href="#Pattern Field">Pattern Field</a>
<br/><div class="bodytext">
			Below code will create a pattern field, set the spin button visible and set the literal mask and edit mask, which only allows 5 digits of numbers.
</div>
<br/><pre class='code' id="code22">
			<span class='javaclass'>FrameRectangle</span> fieldRtg = <span class='basic'>new</span> <span class='javaclass'>FrameRectangle</span>(0.5, 2.0, 2.9433, 0.5567, <span class='javaclass'>SupportedLinearMeasure.IN</span>);<br/>
			<span class='javaclass'>PatternField</span> patternField = (<span class='javaclass'>PatternField</span>) form.createPatternField(para, fieldRtg, "PatternField", "12345");<br/>
			patternField.setEditMask("NNLNNN");<br/>
			patternField.setLiteralMask("##.###");<br/>
			patternField.setSpinButonVisible(<span class='basic'>true</span>);<br/>
</pre>
<br/><div class="bodytext">
			You can get an iterator of the pattern field as follows.
</div>
<br/><pre class='code' id="code23">
			<span class='javaclass'>Iterator</span>&lt;<span class='javaclass'>FormControl</span>&gt; iterator = <span class='javaclass'>PatternField</span>.getSimpleIterator(form);<br/>
			<span class='modifier'>while</span>  (iterator.hasNext()) {<br/>
				patternField = (<span class='javaclass'>PatternField</span>) iterator.next();<br/>
			}<br/>
</pre>
<br/><a href="#Currency Field">Currency Field</a>
<br/><div class="bodytext">
			Below code will create a currency field, set the spin button visible, set the decimal accurcy to 4 and use the 'CNY' as the currency symbol.
</div>
<br/><pre class='code' id="code24">
			<span class='javaclass'>FrameRectangle</span> fieldRtg = <span class='basic'>new</span> <span class='javaclass'>FrameRectangle</span>(0.5, 2.0, 2.9433, 0.5567, <span class='javaclass'>SupportedLinearMeasure.IN</span>);<br/>
			<span class='javaclass'>CurrencyField</span> currencyField = (<span class='javaclass'>CurrencyField</span>) form.createCurrencyField(para, fieldRtg, "CurrencyField", "135.467");<br/>
			currencyField.setCurrencySymbol("CNY");<br/>
			currencyField.setDecimalAccuracy(4);<br/>
			currencyField.setSpinButonVisible(true);<br/>
</pre>
<br/><div class="bodytext">
			You can get an iterator of the currency field as follows.
</div>
<br/><pre class='code' id="code25">
			<span class='javaclass'>Iterator</span>&lt;<span class='javaclass'>FormControl</span>&gt; iterator = <span class='javaclass'>CurrencyField</span>.getSimpleIterator(form);<br/>
			<span class='modifier'>while</span>  (iterator.hasNext()) {<br/>
				currencyfield = (<span class='javaclass'>CurrencyField</span>) iterator.next();<br/>
			}<br/>
</pre>
<br/><br/><strong><a href="#Size and Style">Size and Style</a></strong>
<div class="bodytext">
			If you want to handle more style settings of a form control like horizontal alignment, you can try ControlStyleHandler and GraphicProperties.
</div>
<br/><pre class='code' id="code26">
			<span class='javaclass'>ControlStyleHandler</span> handler = button.getDrawControl().getStyleHandler();<br/>
			<span class='javaclass'>GraphicProperties</span> properties = handler.getGraphicPropertiesForWrite();<br/>
			properties.setHorizontalPosition(<span class='javaclass'>StyleTypeDefinitions.FrameHorizontalPosition.FROMLEFT</span>);<br/>
</pre>
<div class="bodytext">
			You can use FrameRectangle to change the position and size of a form control.
</div>
<br/><pre class='code' id="code27">
			<span class='javaclass'>FrameRectangle</span> rtg = <span class='basic'>new</span> <span class='javaclass'>FrameRectangle</span>(0.01, 2.0, 5, 2,<span class='javaclass'>SupportedLinearMeasure.IN</span>);<br/>
			button.setRectangle(rtg);<br/>
</pre>
<br/><br/> 
<div class="navigation">
 <ul>
  <li><a href="Fields.html">previous</a></li>
  <li><a href="Manipulate Metadata.html">next</a></li>
  <li><a href="#">top</a></li>
 </ul>
</div>
<link type="text/css" rel="stylesheet" href="cookbook.css"/>
