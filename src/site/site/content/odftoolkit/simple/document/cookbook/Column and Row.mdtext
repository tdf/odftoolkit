<strong><a href="../index.html">Documents</a></strong> > <strong><a href="index.html">Cookbook</a></strong> ><strong><a href="Column and Row.html">Column and Row</a></strong>
<div class="navigation">
 <ul>
  <li><a href="Table.html">previous</a></li>
  <li><a href="Cell.html">next</a></li>
 </ul>
</div>
<br/>
<br/><strong><a href="#Get Column/Row" >Get Column/Row</a></strong>
<div class="bodytext">
The class Column/Row represents the column/row of table.To get all the columns or rows you can use the getColumnList or getRowList of the table instance.
</div>
<br/><pre class='code' id="code0">
	<span class='javaclass'>List</span>&lt;<span class='javaclass'>Column</span>&gt; columns = table.getColumnList();<br/>
	<span class='javaclass'>List</span>&lt;<span class='javaclass'>Row</span>&gt; rows = table.getRowList();<br/>
</pre>
<br/><div class="bodytext">
You can also get single column/row by specifying the index of the column/row.
</div>
<div class="bodytext">
The column/row index start from 0.If not found, null will be returned.
</div>
<br/><pre class='code' id="code1">
	<br/>
	<span class='javaclass'>Column</span> column = table.getColumnByIndex(2);<br/>
	<span class='javaclass'>Row</span> row = table.getRowByIndex(0);<br/>
</pre>
<br/><div class="bodytext">
If you want to know the count of header column/row in the table,you can do like this:
</div>
<br/><pre class='code' id="code2">
	<span class='basic'>int</span> headerColumnCount = table.getHeaderColumnCount();<br/>
	<span class='basic'>int</span> headerRowCount = table.getHeaderRowCount();<br/>
</pre>
<br/><div class="bodytext">
If you want to know the index of the column/row,you can use the method below:
</div>
<br/><pre class='code' id="code3">
	<span class='basic'>int</span> columnIndex=column.getColumnIndex();<br/>
	<span class='basic'>int</span> rowIndex=row.getRowIndex();<br/>
</pre>
<br/><div class="bodytext">
Can I get the previous or next Column/Row by the current column/row instance?
</div>
<div class="bodytext">
Yes,you can ask the column/row instance itself,if it doesn't exist,null will be returned.
</div>
<br/><pre class='code' id="code4">
	<span class='javaclass'>Column</span> previousCol=column.getPreviousColumn();<br/>
	<span class='javaclass'>Column</span> nextCol=column.getNextColumn();<br/>
	<span class='javaclass'>Row</span> previousRow=row.getPreviousRow();<br/>
	<span class='javaclass'>Row</span> nextRow=row.getNextRow();<br/>
</pre>
<br/><br/><strong><a href="#Append or Insert Column/Row" >Append or Insert Column/Row</a></strong>
<div class="bodytext">
You can add a column to the end or insert many columns before the specified index
</div>
<div class="bodytext">
The appendColumn/Row method add an empty column/row at the end and return the new appended column/row
</div>
<br/><pre class='code' id="code5">
	<span class='javaclass'>Column</span> newColumn=table.appendColumn();<br/>
	<span class='javaclass'>Row</span> newRow=table.appendRow();<br/>
</pre>
<br/><div class="bodytext">
What can I do if I want to insert a column/row into the specified position?
</div>
<div class="bodytext">
	 You can use the insertColumn/RowBefore method,whose first parameter is the index of the column/row to be inserted before;	 The second parameter is the number of columns/rows to be inserted.
</div>
<br/><pre class='code' id="code6">
	<span class='javaclass'>List</span>&lt;<span class='javaclass'>Column</span>&gt; cols = table.insertColumnsBefore(1, 2);<br/>
	<span class='javaclass'>List</span>&lt;<span class='javaclass'>Row</span>&gt; newRows = table.insertRowsBefore(0, 2);<br/>
</pre>
<br/><br/><strong><a href="#Remove Columns/Rows" >Remove Columns/Rows</a></strong>
<div class="bodytext">
You can delete a number of columns/rows by index
</div>
<div class="bodytext">
The first parameter is the index of the first column/row to delete; The second parameter is the number of columns/rows to delete.
</div>
<div class="bodytext">
The code below remove 1 column whose index is 2;remove 2 rows whose index is 1,2. 
</div>
<br/><pre class='code' id="code7">
	table.removeColumnsByIndex(2, 1);<br/>
	table.removeRowsByIndex(1, 2);<br/>
</pre>
<br/><br/><strong><a href="#Set Column/Row" >Set Column/Row</a></strong>
<div class="bodytext">
If you want to change the width of the column or the height of the row,you can use it like this:
</div>
<div class="bodytext">
If the second parameter of row's setHeight is true, the row can fit the height to the text, vice versa.
</div>
<br/><pre class='code' id="code8">
	column.setWidth(column.getWidth()/2);<br/>
	row.setHeight(row.getHeight()/2, <span class='basic'>true</span>);<br/>
</pre>
<br/><br/> 
<div class="navigation">
 <ul>
  <li><a href="Table.html">previous</a></li>
  <li><a href="Cell.html">next</a></li>
  <li><a href="#">top</a></li>
 </ul>
</div>
<link type="text/css" rel="stylesheet" href="cookbook.css"/>
