<strong><a href="../index.html">Documents</a></strong> > <strong><a href="index.html">Cookbook</a></strong> ><strong><a href="Style Handling.html">Style Handling</a></strong>
<div class="navigation">
 <ul>
  <li><a href="Charts.html">previous</a></li>
  <li><a href="Manipulate TextSearch.html">next</a></li>
 </ul>
</div>
<br/>
<br/><strong><a href="#Overview" >Overview</a></strong>
<div class="bodytext">
Style handling methods provide convenient methods to set font and borders.
</div>
<br/><strong><a href="#Font handling" >Font handling</a></strong>
<div class="bodytext">
		The most simple method to define font settings is to create a font		object, and set it to a cell object.		The below code snippet defines a font object to describe "Arial"		italic font with size "12pt" and black color,		and then set it to a cell. The font will work for western characters		by default.
</div>
<br/><pre class='code' id="code0">
		<span class='javaclass'>SpreadsheetDocument</span> document = <span class='javaclass'>SpreadsheetDocument</span>.newSpreadsheetDocument();<br/>
		<span class='javaclass'>Table</span> table = document.getTableByName("Sheet1");<br/>
		<span class='javaclass'>Font</span> font = <span class='modifier'>new</span> <span class='javaclass'>Font</span>("<span class='javaclass'>Arial</span>", <span class='javaclass'>StyleTypeDefinitions</span>.<span class='javaclass'>FontStyle</span>.<span class='javaclass'>ITALIC</span>, 12, <span class='javaclass'>Color</span>.<span class='javaclass'>BLACK</span>);<br/>
		<span class='javaclass'>Cell</span> cell = table.getCellByPosition("A1");<br/>
		cell.setFont(font);<br/>
</pre>
<br/><div class="bodytext">
		The most simple method to get font settings of western characters is:
</div>
<br/><pre class='code' id="code1">
		<span class='javaclass'>Font</span> theFont = cell.getFont();<br/>
		<span class='basic'>double</span> size = theFont.getSize();<br/>
		<span class='javaclass'>String</span> fontName = theFont.getFamilyName();<br/>
		<span class='javaclass'>StyleTypeDefinitions</span>.<span class='javaclass'>FontStyle</span> fontStyle = theFont.getFontStyle();<br/>
		<span class='javaclass'>Color</span> fontColor = theFont.getColor();<br/>
</pre>
<br/><br/><strong><a href="#Advanced font handling" >Advanced font handling</a></strong>
<div class="bodytext">
		<i>CellStyleHandler</i> can help you to achieve advanced functions.		In Open Document Format, there can be different font settings for		different script types. 		For example, a font setting for English characters and another font		setting for Chinese characters.		If you want to define the font setting for other script types, you		can reference to below codes.		The below code snippet defines a font for Chinese characters.
</div>
<br/><pre class='code' id="code2">
		cell.getStyleHandler().setFont(font, <span class='modifier'>new</span> <span class='javaclass'>Locale</span>(<span class='javaclass'>Locale</span>.<span class='javaclass'>CHINESE</span>.getLanguage(), <span class='javaclass'>Locale</span>.<span class='javaclass'>CHINA</span>.getCountry()));<br/>
</pre>
<br/><div class="bodytext">
		The below code snippet shows how to get the font setting for other kinds of scripts.
</div>
<br/><pre class='code' id="code3">
		<span class='javaclass'>CellStyleHandler</span> styleHandler = cell.getStyleHandler();<br/>
		<span class='javaclass'>Font</span> westernFont = styleHandler.getFont(<span class='javaclass'>Document</span>.<span class='javaclass'>ScriptType</span>.<span class='javaclass'>WESTERN</span>);<br/>
		<span class='javaclass'>Font</span> chineseFont = styleHandler.getFont(<span class='javaclass'>Document</span>.<span class='javaclass'>ScriptType</span>.<span class='javaclass'>CJK</span>);<br/>
		<span class='javaclass'>Font</span> complexFont = styleHandler.getFont(<span class='javaclass'>Document</span>.<span class='javaclass'>ScriptType</span>.<span class='javaclass'>CTL</span>);<br/>
</pre>
<br/><br/><strong><a href="#Border handling" >Border handling</a></strong>
<div class="bodytext">
		The most simple way to set border is to create a border object and		then set it to a cell object.		Below code snippet illustrates how to set a cell object with four		borders.
</div>
<br/><pre class='code' id="code4">
		cell = table.getCellByPosition("A1");<br/>
		cell.setStringValue("four border");<br/>
		<span class='javaclass'>Border</span> border = <span class='modifier'>new</span> <span class='javaclass'>Border</span>(<span class='javaclass'>Color</span>.<span class='javaclass'>RED</span>, 1, <span class='javaclass'>StyleTypeDefinitions</span>.<span class='javaclass'>SupportedLinearMeasure</span>.<span class='javaclass'>PT</span>);<br/>
		cell.setBorders(<span class='javaclass'>CellBordersType</span>.ALL_FOUR, border);<br/>
</pre>
<br/><div class="bodytext">
		Below code snippet illustrates how to set a cell object with left and		right borders, top and bottom borders and diagonal lines.
</div>
<br/><pre class='code' id="code5">
		cell.setBorders(<span class='javaclass'>CellBordersType</span>.LEFT_RIGHT, border);<br/>
		cell.setBorders(<span class='javaclass'>CellBordersType</span>.TOP_BOTTOM, border);<br/>
		cell.setBorders(<span class='javaclass'>CellBordersType</span>.DIAGONAL_LINES, border);<br/>
</pre>
<br/><div class="bodytext">
		Below code snippet illustrates how to set a cell object with left		border, top border and diagonal from bottom left to top right.
</div>
<br/><pre class='code' id="code6">
		cell.setBorders(<span class='javaclass'>CellBordersType</span>.<span class='javaclass'>LEFT</span>, border);<br/>
		cell.setBorders(<span class='javaclass'>CellBordersType</span>.<span class='javaclass'>TOP</span>, border);<br/>
		cell.setBorders(<span class='javaclass'>CellBordersType</span>.<span class='javaclass'>DIAGONALBLTR</span>, border);<br/>
</pre>
<br/><div class="bodytext">
		Below code snippet illustrates how to get a border definition.
</div>
<br/><pre class='code' id="code7">
		<span class='javaclass'>Border</span> thisBorder = cell.getBorder(<span class='javaclass'>CellBordersType</span>.<span class='javaclass'>LEFT</span>);<br/>
		thisBorder = cell.getBorder(<span class='javaclass'>CellBordersType</span>.<span class='javaclass'>TOP</span>);<br/>
		thisBorder = cell.getBorder(<span class='javaclass'>CellBordersType</span>.<span class='javaclass'>DIAGONALBLTR</span>);<br/>
</pre>
<br/><br/> 
<div class="navigation">
 <ul>
  <li><a href="Charts.html">previous</a></li>
  <li><a href="Manipulate TextSearch.html">next</a></li>
  <li><a href="#">top</a></li>
 </ul>
</div>
<link type="text/css" rel="stylesheet" href="cookbook.css"/>
