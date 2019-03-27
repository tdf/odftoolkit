<strong><a href="../index.html">Documents</a></strong> > <strong><a href="index.html">Cookbook</a></strong> ><strong><a href="Cell.html">Cell</a></strong>
<div class="navigation">
 <ul>
  <li><a href="Column and Row.html">previous</a></li>
  <li><a href="Cell Range.html">next</a></li>
 </ul>
</div>
<br/>
<br/><strong><a href="#Get Cell" >Get Cell</a></strong>
<div class="bodytext">
If you want to get the specified cell in a table,you can use the getCellByPosition method of Table.
</div>
<div class="bodytext">
The first parameter is the column index,the second parameter is the row index.
</div>
<br/><pre class='code' id="code0">
         <span class='javaclass'>TextDocument</span> document = (<span class='javaclass'>TextDocument</span>) <span class='javaclass'>TextDocument</span>.loadDocument(filePath);<br/>
	 <span class='javaclass'>Table</span> table=document.getTableByName("stringTable");<br/>
	 <span class='javaclass'>Cell</span> cell=table.getCellByPosition(1, 1);<br/>
</pre>
<br/><div class="bodytext">
If you are manipulating a spreadsheet,you can get the cell by its address:
</div>
<br/><pre class='code' id="code1">
	 <span class='javaclass'>Table</span> sheet1 = document.getTableByName("Sheet1");<br/>
	 <span class='javaclass'>Cell</span> odsCell=sheet1.getCellByPosition("A1");<br/>
</pre>
<br/><div class="bodytext">
If you want to get a cell from a row,you can specify the index of the cell in the row.
</div>
<br/><pre class='code' id="code2">
	 <span class='javaclass'>Row</span> row=table.getRowByIndex(1);<br/>
	 <span class='javaclass'>Cell</span> cell2=row.getCellByIndex(1);<br/>
	 <span class='javaclass'>System</span>.out.println(cell2.getStringValue());<br/>
</pre>
<br/><div class="bodytext">
What can I do if I have a Cell instance and want to know which column and row it belongs to ?
</div>
<div class="bodytext">
The code below shows how you can do that:
</div>
<br/><pre class='code' id="code3">
	 <span class='javaclass'>Row</span> row1=cell.getTableRow();<br/>
	 <span class='javaclass'>Column</span> column1=cell.getTableColumn();<br/>
</pre>
<br/><br/><strong><a href="#Control Cell Attributes" >Control Cell Attributes</a></strong>
<div class="bodytext">
Use the getStyleName() method you can get the style name of the cell.
</div>
<div class="bodytext">
If you want to change the style,it must be set when you set the display text.
</div>
<br/><pre class='code' id="code4">
	 <span class='javaclass'>String</span> cellSytle=cell.getStyleName();<br/>
	 cell.setDisplayText("content", cellSytle);<br/>
</pre>
<br/><div class="bodytext">
What can I do if I want to control the display alignment of the cell?
</div>
<div class="bodytext">
You can set the horizontal and vertical alignment to do so.
</div>
<div class="bodytext">
The code below shows how to get and set the alignment.You can refer to the javadoc about the alignment type.
</div>
<br/><pre class='code' id="code5">
	  <span class='javaclass'>StyleTypeDefinitions</span>.<span class='javaclass'>HorizontalAlignmentType</span> horizontalAlign=cell.getHorizontalAlignmentType();<br/>
	  <span class='javaclass'>StyleTypeDefinitions</span>.<span class='javaclass'>VerticalAlignmentType</span> verticalAlign=cell.getVerticalAlignmentType();<br/>
	  cell.setHorizontalAlignment(<span class='javaclass'>StyleTypeDefinitions</span>.<span class='javaclass'>HorizontalAlignmentType</span>.<span class='javaclass'>CENTER</span>);<br/>
	  cell.setVerticalAlignment(<span class='javaclass'>StyleTypeDefinitions</span>.<span class='javaclass'>VerticalAlignmentType</span>.<span class='javaclass'>BOTTOM</span>);<br/>
</pre>
<br/><div class="bodytext">
If the content of the cell is too long,you can set the wrap option of the cell.
</div>
<br/><pre class='code' id="code6">
	   cell.setTextWrapped(<span class='basic'>true</span>);<br/>
</pre>
<br/><div class="bodytext">
If don't know the cell is wrapped or not,you can use the method:
</div>
<br/><pre class='code' id="code7">
	   <span class='basic'>boolean</span> isTextWrapped=cell.isTextWrapped();<br/>
</pre>
<br/><div class="bodytext">
If you want to set the background color of the cell,be care that the color type is org.odftoolkit.odfdom.type.Color.
</div>
<br/><pre class='code' id="code8">
	    <span class='javaclass'>Color</span> cellBackgroundColor=cell.getCellBackgroundColor();<br/>
	    cell.setCellBackgroundColor(<span class='javaclass'>Color</span>.valueOf("#000000"));<br/>
</pre>
<br/><div class="bodytext">
How can I control the spanned number of the column/row:
</div>
<br/><pre class='code' id="code9">
	    <span class='basic'>int</span> spannedNum=cell.getcgetColumnSpannedNumber();<br/>
	    cell.setColumnSpannedNumber(spannedNum);<br/>
	    <span class='basic'>int</span> rowSpannedNum=cell.getRowSpannedNumber();<br/>
	    cell.setRowSpannedNumber(rowSpannedNum);<br/>
</pre>
<br/><div class="bodytext">
For column,maybe you want to know the column repeated number:
</div>
<br/><pre class='code' id="code10">
	    <span class='basic'>int</span> repeatedNum=cell.getColumnsRepeatedNumber();<br/>
	    cell.setColumnsRepeatedNumber(repeatedNum);<br/>
</pre>
<br/><div class="bodytext">
How about formatting a cell's content? You can set the format string of the cell to do so.
</div>
<div class="bodytext">
For example you want to format the date to yyyy-MM-dd ,you can:
</div>
<br/><pre class='code' id="code11">
	      <span class='javaclass'>String</span> cellFormatStr=cell.getFormatString();<br/>
	      cell.setDateValue(<span class='modifier'>new</span> <span class='javaclass'>GregorianCalendar</span>(2010,5,1));<br/>
	      cell.setFormatString("yyyy-<span class='javaclass'>MM</span>-dd");<br/>
</pre>
<br/><div class="bodytext">
Be care that the setFormatString only works for float, date and percentage.
</div>
<div class="bodytext">
You may be confused by the difference between getFormatString and getFormula,the difference is that:
</div>
<div class="bodytext">
For the setFormula method,it just sets as a formula attribute,the cell value will not be calculated.
</div>
<br/><pre class='code' id="code12">
	      <span class='javaclass'>String</span> formula=cell.getFormula();<br/>
	      cell.setFormula(formula);    <br/>
</pre>
<br/><div class="bodytext">
How can I clear the content of the cell?
</div>
<div class="bodytext">
RemoveContent remove all of the cell while the removeTextContent only remove the text content of the cell.
</div>
<br/><pre class='code' id="code13">
	   cell.removeContent();<br/>
	   cell.removeTextContent();<br/>
</pre>
<br/><br/><strong><a href="#Get&Set Cell Value Type" >Get&Set Cell Value Type</a></strong>
<div class="bodytext">
	 The cell value can have different types,for the setValueType method:	 the parameter can be 	 <ul><li>"boolean"</li>	 <li>"currency"</li>	 <li>"date"</li>	 <li>"float"</li>         <li>"percentage"</li>	 <li>"string"</li>	 <li>"time"</li>	 <li>"void"</li>	 </ul>	 If the parameter type is not a valid cell type, an IllegalArgumentException will be thrown.
</div>
<br/><pre class='code' id="code14">
	   <span class='javaclass'>String</span> valueType=cell.getValueType();<br/>
	   cell.setValueType(valueType);<br/>
</pre>
<br/><div class="bodytext">
For the following getXXXValue() method:it gets the cell value as xxx type.An IllegalArgumentException will be thrown if the cell type is not xxx.
</div>
<a href="#Get&Set boolean type Cell" >Get&Set boolean type Cell</a>
<div class="bodytext">
	For setBooleanValue method:it sets the cell value as a boolean and sets the value type to be boolean.
</div>
<br/><pre class='code' id="code15">
	   <span class='basic'>boolean</span> booleanValue=cell.getBooleanValue();<br/>
	   cell.setBooleanValue(booleanValue);<br/>
</pre>
<br/><a href="#Get&Set currency type Cell" >Get&Set currency type Cell</a>
<div class="bodytext">
For the following getting methods,if the value type is not "currency", an IllegalArgumentException will be thrown.
</div>
<div class="bodytext">
The currency code of the cell is like "USD", "EUR", "CNY", and the currency symbol is like "$"
</div>
<br/><pre class='code' id="code16">
	   <span class='javaclass'>String</span> currencyCode=cell.getCurrencyCode();  <br/>
	   cell.setCurrencyCode("<span class='javaclass'>USD</span>");<br/>
</pre>
<br/><div class="bodytext">
You can also set currency value and currency format.Please note the overall format includes the symbol character, for example: $#,##0.00.
</div>
<br/><pre class='code' id="code17">
	   cell.setCurrencyValue(100.00, "<span class='javaclass'>USD</span>");<br/>
	   cell.setCurrencyFormat("$", "$#,##0.00");<br/>
</pre>
<br/><a href="#Get&Set date type Cell" >Get&Set date type Cell</a>
<br/><pre class='code' id="code18">
	   <span class='javaclass'>Calendar</span> dateValue=cell.getDateValue(); <br/>
	   cell.setDateValue(<span class='modifier'>new</span> <span class='javaclass'>GregorianCalendar</span>(2010,5,1));<br/>
</pre>
<br/><a href="#Get&Set float type Cell" >Get&Set float type Cell</a>
<br/><pre class='code' id="code19">
	  <span class='basic'>double</span> floatValue=cell.getDoubleValue();  <br/>
	  cell.setDoubleValue(<span class='modifier'>new</span> <span class='javaclass'>Double</span>(22.99f));<br/>
</pre>
<br/><a href="#Get&Set percentage type Cell" >Get&Set percentage type Cell</a>
<br/><pre class='code' id="code20">
	  <span class='basic'>double</span> percentageValue=cell.getPercentageValue();<br/>
	  cell.setPercentageValue(0.89);<br/>
</pre>
<br/><a href="#Get&Set string type Cell" >Get&Set string type Cell</a>
<div class="bodytext">
If the cell type is not string, the display text will be returned.
</div>
<br/><pre class='code' id="code21">
	  <span class='javaclass'>String</span> stringValue=cell.getStringValue();   <br/>
	  cell.setStringValue("simple");<br/>
</pre>
<br/><a href="#Deal with the Time Value" >Deal with the Time Value</a>
<div class="bodytext">
If you want to get the string type of time value,you can format it:
</div>
<br/><pre class='code' id="code22">
	  cell.setTimeValue(<span class='javaclass'>Calendar</span>.getInstance());<br/>
	  <span class='javaclass'>SimpleDateFormat</span> simpleFormat = <span class='modifier'>new</span> <span class='javaclass'>SimpleDateFormat</span>("'<span class='javaclass'>PT</span>'<span class='javaclass'>HH</span>'H'mm'M'ss'S'");<br/>
          <span class='javaclass'>String</span>  timeString= simpleFormat.format(cell.getTimeValue().getTime());<br/>
</pre>
<br/><a href="#Something about Display Text" >Something about Display Text</a>
<div class="bodytext">
          Please note the display text in ODF viewer might be different from the value set by this method,	  because the displayed text in ODF viewer is calculated and set by editor. 
</div>
<br/><pre class='code' id="code23">
	  <span class='javaclass'>String</span> displayText=cell.getDisplayText();<br/>
	  cell.setDisplayText(displayText);<br/>
</pre>
<br/><br/><strong><a href="#Set image" >Set image</a></strong>
<div class="bodytext">
		From version 0.5.5, we support high level APIs for images.	    You can use following codes to set an image to a cell.
</div>
<br/><pre class='code' id="code24">
	    <span class='javaclass'>Image</span> myImage = cell.setImage(<span class='modifier'>new</span> <span class='javaclass'>URI</span>("http://www.xxx.com/a.jpg"));<br/>
</pre>
<br/><div class="bodytext">
	    You can use following codes to access an image in a cell.
</div>
<br/><pre class='code' id="code25">
	    <span class='javaclass'>Image</span> image = cell.getImage();<br/>
	    <span class='javaclass'>String</span> imagename = image.getName();<br/>
	    <span class='javaclass'>FrameRectangle</span> rect = image.getRectangle();<br/>
	    rect.setX(1);<br/>
	    rect.setY(1);<br/>
	    image.setRectangle(rect);<br/>
</pre>
<br/><br/> 
<div class="navigation">
 <ul>
  <li><a href="Column and Row.html">previous</a></li>
  <li><a href="Cell Range.html">next</a></li>
  <li><a href="#">top</a></li>
 </ul>
</div>
<link type="text/css" rel="stylesheet" href="cookbook.css"/>
