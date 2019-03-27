Title: Simple API Release Notes

**Release 0.6.6**  
*Auguest 12th 2011*

We are pleased to announce the release of the Simple Java API for ODF version 0.6.6 today. The improvements in this version include:  

  - Two critical bugs in Navigation API and several other issues are fixed.
  - Page columns, a page layout feature in text document, are now supported.
  - Unit test coverage rate increased.  This work improves the stability of our API.

You can download it [here][1].

ODF Toolkit has been accepted as [Apache incubator project][2] and the move process has been started. The security feature will be included in Apache version. Thanks all of the contributors and users, please continue to pay attention to us in Apache.

***Resolved Issues***

- [Bug 334][3] -  Paragraph.setStyleName() doesn't work.
- [Bug 346][4] -  Test don't work on Linux. 
- [Bug 347][5] -  VariableField doen't register in Component cache.
- [Bug 348][6] -  Paragraph deleted is not complete.
- [Bug 349][7] -  Definition of the number of columns in the page and column Break.
- [Bug 350][8] -  TextNavigation find matched Selection out of range.
- [Bug 353][9] -  Matching child nodes not honored by Navigation if next sibling matches.
- [Bug 354][10] -  parameter isMinHeight is not taken into account in the method setHeight (class Row).
- [Bug 355][11] -  StyleTypeDefinitions.HorizontalAlignmentType has a wrong value, 'justified' should be 'justify'. 
- [Bug 356][12] -  Document.OdfMediaType.TEXT.toString() leads to unreachable code block.
- [Bug 357][13] -  getFontSizeInPoint() in TextProperties doesn't work.
- [Bug 358][14] -  TableCellTest.testCellSizeOptimal Unit test failing on MacOS.

**Release 0.6.5**  
*July 1st 2011*

We are pleased to announce the release of the Simple Java API for ODF version 0.6.5 today. The improvements in this version focus on paragraph and text documents. They are:  
 
 - Hard page breaks, including append page break at the end of text document and append page break after a reference paragraph.
 - Headings, including append heading, apply plain text as heading.
 - Comments, including add comment to a text selection, apply comment to a paragraph.
 - Paragraph font, get/set paragraph font size, style, color and so on.
 - Paragraph alignment, get/set paragraph text alignment.
 - Hyperlinks, including navigation hyperlinks, apply hyperlink to a selection, append hyperlink to paragraph. 

An interesting [demo][15] is uploaded to the website to show how to use these new features to format a text document. 

***Resolved Issues***  

- [Bug 341][16] -  Component.getComponentByElement (OdfElement element) never return null.  
- [Bug 342][17] -  TextProperties.getTextProperties throws NullPointerException.   
- [Bug 343][18] -  OdfDocument.loadDocument(OdfPackage odfPackage, String internalPath) needs validate "internalPath".  
- [Bug 344][19] -  Enhance hyperlink feature.  
- [Bug 345][20] -  Supply Page Break, Heading, Comment, Paragraph Font and Alignment support.  

***API changes since 0.6 Release***    

Method Change List   
Note: The first column 'Java class' package is relative to 'org.odftoolkit'.   

<table>
<tr>
<td>Previous Class</td><td>Previous Method</td><td>New Class</td><td>New Method</td>
</tr>
<tr>
<td>simple.text.Paragraph</td><td>TextPElement  getOdfElement()</td><td>simple.text.Paragraph</td><td>TextParagraphElementBase  getOdfElement()</td>
</tr>
<tr>
<td>simple.text.Paragraph</td><td>DefaultStyleHandler  getStyleHandler()</td><td>simple.text.Paragraph</td><td>ParagraphStyleHandler  getStyleHandler()</td>
</tr>
<tr>
<td>simple.text.Paragraph</td><td>Paragraph getInstanceof(TextPElement)</td><td>simple.text.Paragraph</td><td>Paragraph getInstanceof(TextParagraphElementBase)</td>
</tr>
</table>

**Release 0.6**   
*June 1st 2011*

We are pleased to announce the release of the Simple Java API for ODF version 0.6 today. A major improvement of this version is the chart API. Now you can add charts to text, spreadsheet and presentation documents with easy-to-use methods. An interesting [demo][21] is uploaded to the website to show how to create a presentation/text/spreadsheet document with charts only using Simple ODF API. 

We also make some enhancements based on user requests:

 - Ability to set table cell widths and heights with high precision;
 - Ability to set validity rules for the values of table cells;
 - Provide helper methods for Spreadsheet document to get table;
 - Provide more layout definitions for slides

***Resolved Issues*** 
  
 - [Bug 332][22] -  Nice to have means to set table cell widths and heights with sub-millimeter precision.
 - [Bug 260][23] -  Table cell validate function.
 - [Bug 243][24] -  SpreadsheetDocument needs helper functions to get to Table.
 - [Bug 330][25] -  Supply Chart API Features.

***API changes since 0.5.5 Release***   

Method Change List  
Note: The first column 'Java class' package is relative to 'org.odftoolkit'.     

<table>
<tr>
<td>Previous Class</td><td>Previous Method</td><td>New Class</td><td>New Method</td>
</tr>
<tr>
<td>simple.table.Table</td><td>long getWidth()</td><td>simple.table.Table</td><td>double getWidth()</td>
</tr>
<tr>
<td>simple.table.Table</td><td>void setWidth(long)</td><td>simple.table.Table</td><td>void setWidth(double)</td>
</tr>
<tr>
<td>simple.table.Column</td><td>long getWidth()</td><td>simple.table.Column</td><td>double getWidth()</td>
</tr>
<tr>
<td>simple.table.Column</td><td>void  setWidth(long)</td><td>simple.table.Column</td><td>void  setWidth(double)</td>
</tr>
<tr>
<td>simple.table.Row</td><td>long getHeight()</td><td>simple.table.Table</td><td>double getHeight()</td>
</tr>
<tr>
<td>simple.table.Table</td><td>void setHeight(long)</td><td>simple.table.Table</td><td>void setHeight(double)</td>
</tr>
</table>

**Release 0.5.5**    
*April 30th 2011*   

We released the Simple Java API for ODF  version 0.5.5 today. In this version, we provide high level methods for image and text span. Now you can add images to text, spreadsheet and presentation documents. The position of the image can be specified by a rectangle, a paragraph or a cell. With text span, you can set a different style to a small unit of the text content. An interesting [demo][26] has been upload to website to demonstrate how to add a 2D barcode image to a presentation slide.

We also made some useful enhancements based on user requests:   

 - automatically adjust the table column width based on its text content
 - allow several paragraphs to be added to a single cell
 - set whether or not a header/footer is visible
 - optimize some table methods to improve performance

***Resolved Issues*** 
   
 - [Bug 256][27] -  JavaDoc should be deployed via Maven to the Simple website.  
 - [Bug 269][28] -  Desirable column width is calculated in characters on the basis of the size of the data that will go into the table.
 - [Bug 273][29] -  The performance of getRowList/getColumnList is too low, if there is a row element with 65535 repeat number.
 - [Bug 309][30] -  Supply method to set the header/footer visible or not.
 - [Bug 311][31] -  Insert several paragraphs in a cell or several different text in the same paragraph in a cell. 
 - [Bug 312][32] -  Allow to set alignment of image in a table cell. 
 - [Bug 326][33] -  Something is missing in order to move the object on the sheet.
 - [Bug 327][34] -  Problem with the adding of tables in Footer.
 - [Bug 328][35] -  Performance problem in CellRange constructor when Table.getRowCount() is large.
 - [Bug 329][36] -  Unable to set the column widths of a table with precision. 
 - [Bug 333][37] -  Provide convenient methods for image and text span.

***API changes since 0.5 Release***  
 
Method Change List      
Note: The first column 'Java class' package is relative to 'org.odftoolkit'.
<table>
<tr>
<td>Previous Class</td><td>Previous Method</td><td>New Class</td><td>New Method</td>
</tr>
<tr>
<td>simple.table.Cell</td><td>void  setImage(URI)</td><td>simple.table.Cell</td><td>Image  setImage(URI)</td>
</tr>
<tr>
<td>simple.table.Cell</td><td>Image  getImage()</td><td>simple.table.Cell</td><td>BufferedImage  getBufferedImage()</td>
</tr>
</table>

**Release 0.5**   
*April 2nd 2011*   

We released the Simple Java API for ODF  version 0.5 today.

In this version, we provides paragraph, field, and text box manipulation methods. 
 - Paragraph methods allow you to create paragraph, get paragraph by index, set the text content, and remove paragraph.
 - Fields methods allow you to easily add a field to a document, and change the value of a field. We now support data field, time field, chapter field, title field, subject field, author field, page number field, page count field, etc. 
 - Text box methods allow you to add a text box, change the content of text box, and delete a text box.  
You can implement powerful document generation scenarios with these API. You can get sample codes from the cookbook for paragraph, field and text box.

***Resolved Issues***

 - [Bug 307][38] -  Table.getColumnByIndex() throws NullPointerException.
 - [Bug 308][39] -  Remove padding from table in header/footer.
 - [Bug 314][40] -  Navigation API should support find/replace in a specific OdfElement range
 - [Bug 315][41] -  Add Field API to Simple ODF
 - [Bug 316][42] -  Access to header and footer to Standard and First Page.
 - [Bug 317][43] -  Table.getColumnCount() doesn't work when exists table:table-columns
 - [Bug 318][44] -  Provide paragraph functions and its style handling functions.
 - [Bug 319][45] -  Provide frame and text box API.
 - [Bug 320][46] -  Section should provide methods to access paragraph.
 - [Bug 323][47] -  VariableContainer should have a method to get its VariableFields.
 - [Bug 324][48] -  Table unnamed causes a NullPointerException.

***API changes since 0.4.5 Release***  
Method Change List   
Note: The first column 'Java class' package is relative to 'org.odftoolkit'.

<table>
<tr>
<td>Previous Class</td><td>Previous Method</td><td>New Class</td><td>New Method</td>
</tr>
<tr>
<td>simple.table.CellStyleHandler</td><td>getCellStyleElementForRead()</td><td>simple.style.DefaultStyleHandler</td><td>getStyleElementForRead()</td>
</tr>
<tr>
<td>simple.table.CellStyleHandler</td><td>getCellStyleElementForWrite()</td><td>simple.style.DefaultStyleHandler</td><td>getStyleElementForWrite()</td>
</tr>
</table>

**Release 0.4.5**   
*March 4th 2011*    

We are pleased to announce the release of the Simple Java API for ODF version 0.4.5.   Major changes in this release include:

 - We are now using ODFDOM 0.8.7 
 - We have added initial support  for footers and headers. Now you can simply get the footer and header of text document and add table/image/string content to footer and header. 
 - We have also improved the performance of copying slides from other presentations (foreign slides). The time to copy a single slide is decreased by 20%, and the time to copy slides with referenced resources is decreased much more. A sample to show the function of copying slides from foreign documents is available in the [website][49]. 

***Resolved Issues***

 - [Bug 224][50] -  Setting note for individual cell
 - [Bug 254][51] -  Update Simple API to support ODFDOM 0.8.7
 - [Bug 274][52] -  Create row/column/cell with/without repeat number
 - [Bug 275][53] -  Append row/column with/without previous row/column style
 - [Bug 276][54] -  Cell format type can be auto calculated according to the style of cell
 - [Bug 278][55] -  Adding border to created cell, OutOfMemoryError was throwed about 11,000 records
 - [Bug 284][56] -  Cell.getFont() throws NullPointerException
 - [Bug 290][57] -  Table API doesn't work for Presentation Document 
 - [Bug 295][58] -  The Simple API does not work with JDK5
 - [Bug 303][59] -  Performance tuning for foreign slide copy
 - [Bug 304][60] -  Support image and text in footer and header
 - [Bug 305][61] -  A bug that occurs when resizing the column width of a table to 0
 - [Bug 306][62] -  Errors when Locale is set to Germany

There is no API changes in this version.

**Release 0.4**  
*January 31st 2011*    

We are pleased to announce that we are releasing version 0.4 of the Simple Java API for ODF today. In this version, we have added powerful list functions. With these new methods, you can easily add lists to text document and presentation document, with numbering, bullets, or graphic bullets. An interesting code sample of this API is available in the [website][63].   
We have also introduced methods to get, copy and paste sections in text documents.  Copying and pasting sections are useful when you want to clone a section of a template multiple times for different data. The introduce of list and section functions has been added to [cookbook][64] for your reference.    
Also, along with various bug fixes we have greatly improved the performance of the table API. Now creating a large table by adding one row after another is 20 times faster than before. 

There is no API changes in this version.

***Resolved Issues***

 - [Bug 279][65] -  The performance of Table API need enhance, some methods cost too much time
 - [Bug 280][66] -  Supply List API Support in Simple API
 - [Bug 285][67] -  Supply text through line and under line setting support to Font
 - [Bug 291][68] -  Provide high level methods for section

**Release 0.3.5**  
*December 24th 2010*    

Today we released a new version of the Simple Java API for ODF:  Release 0.3.5. In this version, we have improved the navigation, text extraction, table and style handling features by fixing bugs and improving the JavaDoc. We also fixed a memory leak issue related to tables.    
You may notice that there are no new features in this month's release. Our intent is to alternate "bug fixing" releases with "new feature" release. So next month will be a new feature release (0.4) followed by a bug fixing release (0.4.5), etc.

***Resolved Issues***

 - [Bug 250][69] -  Table.newTable(Document document, int numRows, int numCols) does not work well  
 - [Bug 251][70] -  Table.appendRows throws ClassCastException  
 - [Bug 253][71] -  Column.setWidth() don't work for new created table  
 - [Bug 255][72] -  cell.setFormatString() and column.setWidth() throw exceptions  
 - [Bug 258][73] -  Where documentation says "deprecated" new preferred way should be given  
 - [Bug 259][74] -  Setting font style to a spreadsheet cell is incorrectly inherited  
 - [Bug 261][75] -  JavaDoc for the TextExtractor needs a lot more prose  
 - [Bug 262][76] -  JavaDoc for TextNavigation match() method is confusing  
 - [Bug 266][77] -  hasNext() method of TextNavigation apparently is REQUIRED and should not be  
 - [Bug 267][78] -  String returned by TextExtractor.getText() has a carriage return (ASCII 13) at the start  
 - [Bug 271][79] -  Comments of Table Style API
 - [Bug 272][80] -  Static Map variables in Table lead memory leak

***API changes since 0.3 Release***  
Enum name change list   
The enum's defined in org.odftoolkit.simple.style.StyleTypeDefinitions have changed. The prefix "Simple" has been removed.

Note: The first column 'Java class' package is relative to 'org.odftoolkit.simple'.

<table>
<tr>
<td>Class</td><td>Enum Previous name</td><td>Enum New name</td>
</tr>
<tr>
<td>style.StyleTypeDefinitions</td><td>SimpleFontStyle</td><td>FontStyle</td>
</tr>
<tr>
<td></td><td>SimpleHorizontalAlignmentType</td><td>HorizontalAlignmentType</td>
</tr>
<tr>
<td></td><td>SimpleVerticalAlignmentType</td><td>VerticalAlignmentType</td>
</tr>
<tr>
<td></td><td>SimpleCellBordersType</td><td>CellBordersType</td>
</tr>
</table>

Method name change list     
Note: The first column 'Java class' package is relative to 'org.odftoolkit.simple'.

<table>
<tr>
<td>Class</td><td>Previous method</td><td>New method</td>
</tr>
<tr>
<td>common.navigation.Navigation</td><td>getCurrentItem()</td><td>nextSelection()</td>
</tr>
<tr>
<td>common.navigation.TextNavigation</td><td>getCurrentItem()</td><td>nextSelection()</td>
</tr>
<tr>
<td>common.navigation.TextStyleNavigation</td><td>getCurrentItem()</td><td>nextSelection()</td>
</tr>
<tr>
<td>style.Font</td><td>setSimpleFontStyle(StyleTypeDefinitions$SimpleFontStyle)</td><td>setFontStyle(StyleTypeDefinitions$FontStyle)</td>
</tr>
<tr>
<td></td><td>StyleTypeDefinitions.SimpleFontStyle  getSimpleFontStyle()</td><td>StyleTypeDefinitions$FontStyle  getFontStyle()</td>
</tr>
</table>

Method deleted    
The locale handling methods in org.odftoolkit.simple.style.Font have been removed in order to simplify the API. Users who want to use advanced functions can use the suggested substitute.

Note: The first column 'Java class' package is relative to 'org.odftoolkit.simple'.

<table>
<tr>
<td>Class</td><td>deleted method</td><td>suggested substitute</td>
</tr>
<tr>
<td>style.Font</td><td>Locale setLocale()</td><td>CellStyleHandler.setFont(Font font, Locale language)</td>
</tr>
<tr>
<td>style.Font</td><td>Locale getLocale()</td><td>CellStyleHandler.getCountry(ScriptType type),CellStyleHandler.getLanguage(ScriptType type)</td>
</tr>
</table>

**Release 0.3**  
*December 1st 2010*    

We are pleased to announce the 2nd release of the Simple Java API for ODF. In this release. we provided easy-to-use functions for setting the text and border styles in tables, as a first taste of a high-level style API.    
We also enhanced the navigation functions. Now the navigation functions work for text, spreadsheet, presentation and chart document. [An instructive sample][81] has been added to the website to demonstrate these powerful functions.    
There are also a few updates in table cell functions: some functions are enhanced and some functions are marked as "Deprecated". More details are described below.

***Resolved Issues***   

 - [Bug 246][82]-  Extend the navigation API under simple.text.search to spreadsheet and presentation.
 - [Bug 247][83]-  Provide table methods for presentation document.
 - [Bug 248][84]-  setHorizontalAlignment and setVerticalAlignment do not work for new created table.
 - [Bug 249][85]-  getRowCount() doesn't work if table:table-rows or table:table-group exits.
 - [Bug 252][86]-  Provide high level style methods for table.	

***API changes since 0.2 Release***   
Package/Class Change List    
Note: The first column 'Java class' package is relative to 'org.odftoolkit'.

<table>
<tr>
<td>Previous Package</td><td>Previous Class</td><td>New Package</td><td>New Class</td>
</tr>
<tr>
<td>simple.text.search</td><td>Navigation</td><td>simple.common.navigation</td><td>Navigation</td>
</tr>
<tr>
<td></td><td>Selection</td><td></td><td>Selection</td>
</tr>
<tr>
<td></td><td>TextNavigation</td><td></td><td>TextNavigation</td>
</tr>
<tr>
<td></td><td>TextSelection</td><td></td><td>TextSelection</td>
</tr>
<tr>
<td></td><td>TextStyleNavigation</td><td></td><td>TextStyleNavigation</td>
</tr>
<tr>
<td></td><td>InvalidNavigationException</td><td></td><td>InvalidNavigationException</td>
</tr>
</table>

**Release 0.2**   

Simple Java API for ODF 0.2 has been released on Nov 1th, with table, presentation, and metadata convenient methods moved from ODFDOM into a new package structure. Better documents are provided in this release. The cookbook, JavaDoc and demo codes can be found. You can download the binary jar file from downloads page.   
Go to PackageLayer to get an overall introduction of package structure and the API changes from ODFDOM.


  [1]: ../downloads.html
  [2]: http://incubator.apache.org/projects/odftoolkit.html
  [3]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=334
  [4]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=346
  [5]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=347
  [6]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=348
  [7]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=349
  [8]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=350
  [9]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=353
  [10]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=354
  [11]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=355
  [12]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=356
  [13]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=357
  [14]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=358
  [15]: ../demo/demo10.html
  [16]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=341
  [17]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=342
  [18]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=343
  [19]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=344
  [20]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=345
  [21]: ../demo/demo9.html
  [22]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=332
  [23]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=260
  [24]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=243
  [25]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=332
  [26]: ../demo/demo8.html
  [27]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=256
  [28]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=269
  [29]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=273
  [30]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=309
  [31]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=311
  [32]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=312
  [33]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=326
  [34]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=327
  [35]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=328
  [36]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=329
  [37]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=333
  [38]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=307
  [39]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=308
  [40]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=314
  [41]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=315
  [42]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=316
  [43]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=317
  [44]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=318
  [45]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=319
  [46]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=320
  [47]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=323
  [48]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=324
  [49]: ../demo/demo5.html
  [50]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=224
  [51]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=254
  [52]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=274
  [53]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=275
  [54]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=276
  [55]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=278
  [56]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=284
  [57]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=290
  [58]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=295
  [59]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=303
  [60]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=304
  [61]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=305
  [62]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=306
  [63]: ../demo/demo4.html
  [64]: cookbook/Manipulate%20Text%20Document.html#Manipulate%20Section
  [65]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=279
  [66]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=280
  [67]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=285
  [68]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=291
  [69]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=250
  [70]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=251
  [71]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=253
  [72]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=255
  [73]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=258
  [74]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=259
  [75]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=261
  [76]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=262
  [77]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=266
  [78]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=267
  [79]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=271
  [80]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=272
  [81]: ../demo/demo3.html
  [82]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=246
  [83]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=247
  [84]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=248
  [85]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=249
  [86]: https://odftoolkit.org/bugzilla/show_bug.cgi?id=252 