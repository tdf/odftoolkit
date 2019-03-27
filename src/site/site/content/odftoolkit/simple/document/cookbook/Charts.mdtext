<strong><a href="../index.html">Documents</a></strong> > <strong><a href="index.html">Cookbook</a></strong> ><strong><a href="Charts.html">Charts</a></strong>
<div class="navigation">
 <ul>
  <li><a href="Cell Range.html">previous</a></li>
  <li><a href="Style Handling.html">next</a></li>
 </ul>
</div>
<br/>
<br/><strong><a href="#Overview" >Overview</a></strong>
<div class="bodytext">
	    Since 0.6, Simple ODF provides methods to manipulate charts in text document, spreadsheet document and presentation document.		You can create, update and delete charts with these methods.
</div>
<br/><strong><a href="#Create charts" >Create charts</a></strong>
<div class="bodytext">
		We all know, a chart is associated with a table. In order to create a chart, you must determine the data set of this chart.		The data set can be a cell range of a table, for example:
</div>
<br/><pre class='code' id="code0">
		<span class='javaclass'>CellRangeAddressList</span> cellRange = <span class='javaclass'>CellRangeAddressList</span>.valueOf("A.A1:A.B3");<br/>
		<span class='javaclass'>DataSet</span> dataSet = <span class='modifier'>new</span> <span class='javaclass'>DataSet</span>(cellRange, spreadsheetDoc, <span class='basic'>true</span>, <span class='basic'>true</span>, <span class='basic'>false</span>);<br/>
</pre>
<br/><div class="bodytext">
		Or a two dimensional array, for example:
</div>
<br/><pre class='code' id="code1">
		<span class='basic'>int</span> row = 2, column = 3;<br/>
		<span class='basic'>double</span>[][] data = <span class='modifier'>new</span> <span class='basic'>double</span>[column][row];<br/>
		<span class='javaclass'>String</span>[] labels = <span class='modifier'>new</span> <span class='javaclass'>String</span>[row];<br/>
		<span class='javaclass'>String</span>[] legends = <span class='modifier'>new</span> <span class='javaclass'>String</span>[column];<br/>
		<span class='javaclass'>DataSet</span> dataset = <span class='modifier'>new</span> <span class='javaclass'>DataSet</span>(labels, legends, data);<br/>
</pre>
<br/><div class="bodytext">
		You should also use rectangle to define the position and the size of this chart. For example: 
</div>
<br/><pre class='code' id="code2">
		<span class='javaclass'>Rectangle</span> rect = <span class='modifier'>new</span> <span class='javaclass'>Rectangle</span>();<br/>
		rect.x = 2000;<br/>
		rect.y = 2700;<br/>
		rect.width = 15000;<br/>
		rect.height = 8000;<br/>
		rect.y = 110000;<br/>
</pre>
<br/><div class="bodytext">
		Then you can create a chart:
</div>
<br/><pre class='code' id="code3">
		spreadsheetDoc.createChart("<span class='javaclass'>Page</span> <span class='javaclass'>Visit</span>", dataSet,rect);<br/>
</pre>
<br/><div class="bodytext">
		There are some shortcut methods to create charts, for example, below codes show how to create a chart in a text document:
</div>
<br/><pre class='code' id="code4">
		<span class='javaclass'>Chart</span> chart = textDoc.createChart(<br/>
				"<span class='javaclass'>Page</span> <span class='javaclass'>Visit</span>", spreadsheetDoc,<br/>
				cellRange, <span class='basic'>true</span>, <span class='basic'>true</span>, <span class='basic'>false</span>, rect);<br/>
</pre>
<br/><div class="bodytext">
		If you want to create a chart in a spreadsheet document, you need to specify a cell to be the anchor of this chart, for example: 
</div>
<br/><pre class='code' id="code5">
		spreadsheetDoc.createChart("<span class='javaclass'>Page</span> <span class='javaclass'>Visit</span>", spreadsheetDoc, cellRange,<br/>
				<span class='basic'>true</span>, <span class='basic'>true</span>, <span class='basic'>false</span>, rect, spreadsheetDoc.getTableByName("C")<br/>
						.getCellByPosition("D10"));<br/>
</pre>
<br/><div class="bodytext">
		If you want to create a chart in a presentation document, you can use the existing layout of a slide, which means, you don't need to specify		a rectangle. The layouts that could contain a chart include: TITLE_PLUS_CHART, TITLE_PLUS_2_CHART, TITLE_LEFT_CHART_RIGHT_OUTLINE,		TITLE_PLUS_3_OBJECT, and TITLE_PLUS_4_OBJECT. For example:
</div>
<br/><pre class='code' id="code6">
		<span class='javaclass'>Slide</span> slide = presentationDoc.newSlide(2, "Slide3",<br/>
				<span class='javaclass'>SlideLayout</span>.TITLE_PLUS_2_CHART);<br/>
		chart = slide.createChart("<span class='javaclass'>Count</span> of <span class='javaclass'>Visits</span>", spreadsheetDoc,<br/>
				cellRange, <span class='basic'>true</span>, <span class='basic'>true</span>, <span class='basic'>false</span>, <span class='basic'>null</span>);<br/>
</pre>
<br/><br/><strong><a href="#Update charts" >Update charts</a></strong>
<div class="bodytext">
		You can update charts properties, for example, the title, axis title, chart type, whether to apply 3D effect, whether to use legend with API.		For example:
</div>
<br/><pre class='code' id="code7">
		chart.setChartTitle("<span class='javaclass'>New</span> title");<br/>
		chart.setAxisTitle("<span class='javaclass'>Hour</span>", "<span class='javaclass'>Number</span>");<br/>
		chart.setChartType(<span class='javaclass'>ChartType</span>.<span class='javaclass'>AREA</span>);<br/>
		chart.setApply3DEffect(<span class='basic'>true</span>);<br/>
		chart.setUseLegend(<span class='basic'>true</span>);<br/>
</pre>
<br/><div class="bodytext">
		You can update the data set too. For example:
</div>
<br/><pre class='code' id="code8">
		chart.setChartData(<span class='modifier'>new</span> <span class='javaclass'>DataSet</span>(<span class='javaclass'>CellRangeAddressList</span>.valueOf("A.A1:A.C4"), spreadsheetDoc, true, true, true));<br/>
</pre>
<br/><br/><strong><a href="#Get and delete charts" >Get and delete charts</a></strong>
<div class="bodytext">
		You can get charts by title e.g.
</div>
<br/><pre class='code' id="code9">
		chart = textDoc.getChartByTitle("<span class='javaclass'>New</span> title").get(0);<br/>
</pre>
<br/><div class="bodytext">
		You can also get a chart by its unique ID. The unique ID of a chart in Simple ODF API is the path of the chart document (relative to the ODF document package).		The unique ID can be gotten with method:
</div>
<br/><pre class='code' id="code10">
		<span class='javaclass'>String</span> chartid = chart.getChartID();<br/>
		chart = textDoc.getChartById(chartid);<br/>
</pre>
<br/><div class="bodytext">
		You can also get the count of charts in this document.
</div>
<br/><pre class='code' id="code11">
		<span class='basic'>int</span> count = textDoc.getChartCount();<br/>
</pre>
<br/><div class="bodytext">
		You can delete a chart by ID or by title, e.g.
</div>
<br/><pre class='code' id="code12">
		textDoc.deleteChartById(chartid);<br/>
		textDoc.deleteChartByTitle("<span class='javaclass'>New</span> title");<br/>
</pre>
<br/><br/> 
<div class="navigation">
 <ul>
  <li><a href="Cell Range.html">previous</a></li>
  <li><a href="Style Handling.html">next</a></li>
  <li><a href="#">top</a></li>
 </ul>
</div>
<link type="text/css" rel="stylesheet" href="cookbook.css"/>
