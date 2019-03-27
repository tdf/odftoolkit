<strong><a href="../index.html">Documents</a></strong> > <strong><a href="index.html">Cookbook</a></strong> ><strong><a href="Cell Range.html">Cell Range</a></strong>
<div class="navigation">
 <ul>
  <li><a href="Cell.html">previous</a></li>
  <li><a href="Charts.html">next</a></li>
 </ul>
</div>
<br/>
<br/><strong><a href="#Get CellRange" >Get CellRange</a></strong>
<div class="bodytext">
You can get cell range by providing start and end index of the column and row,or just provide start and end address of the cell(if you are using the spreadsheet.)
</div>
<br/><pre class='code' id="code0">
	    <span class='javaclass'>CellRange</span> cellRange = table.getCellRangeByPosition(1, 0, 2, 0);<br/>
	    <span class='javaclass'>CellRange</span> cellRangeAdd = table.getCellRangeByPosition("$E1","$E6");<br/>
</pre>
<br/><br/><strong><a href="#Merge Text Table" >Merge Text Table</a></strong>
<div class="bodytext">
The code below merges all of the selected cells into one:
</div>
<br/><pre class='code' id="code1">
	    <span class='javaclass'>Table</span> table1 = document.getTableByName("Table1");<br/>
	    <span class='javaclass'>CellRange</span> cellrange = table1.getCellRangeByPosition(0, 0, table1.getColumnCount()-1, table1.getRowCount()-1);<br/>
	    cellRange.merge();<br/>
</pre>
<br/><br/><strong><a href="#Merge Text Column" >Merge Text Column</a></strong>
<div class="bodytext">
The code below shows how to merge the cells of the first column into one ：
</div>
<br/><pre class='code' id="code2">
	    	table1 = document.getTableByName("Table1");<br/>
	    	<span class='javaclass'>CellRange</span> firstColumn = table1.getCellRangeByPosition(0, 0, 0, table1.getRowCount()-1);<br/>
	    	firstColumn.merge();<br/>
</pre>
<br/><br/><strong><a href="#Merge Text Row" >Merge Text Row</a></strong>
<div class="bodytext">
The code below shows how to merge the cells of the first 2 rows into one ：
</div>
<br/><pre class='code' id="code3">
		table1 = document.getTableByName("Table1");<br/>
		<span class='basic'>int</span> rowCount = table1.getRowCount();<br/>
    	        <span class='javaclass'>CellRange</span> firstTwoRow = table1.getCellRangeByPosition(0, 0, table1.getColumnCount()-1, 1);<br/>
    	        firstTwoRow.merge();<br/>
</pre>
<br/><br/><strong><a href="#Merge SpreadSheet" >Merge SpreadSheet</a></strong>
<div class="bodytext">
Merge a spreadsheet's cell is the same as text document.Especially,when getting the cell range of spreadsheet,you can use special address instead of index.
</div>
<br/><pre class='code' id="code4">
		<span class='javaclass'>Table</span> sheet1 = document.getTableByName("Sheet1");<br/>
		<span class='javaclass'>CellRange</span> cellRange2 = sheet1.getCellRangeByPosition("$E1","$E6");<br/>
		cellRange2.setCellRangeName("<span class='javaclass'>TimeCellRange</span>");<br/>
		cellRange2.merge(); <br/>
</pre>
<br/><br/> 
<div class="navigation">
 <ul>
  <li><a href="Cell.html">previous</a></li>
  <li><a href="Charts.html">next</a></li>
  <li><a href="#">top</a></li>
 </ul>
</div>
<link type="text/css" rel="stylesheet" href="cookbook.css"/>
