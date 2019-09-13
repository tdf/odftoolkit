 /*
  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER

  Copyright 2018-2019 The Document Foundation. All rights reserved.
  Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.

  Use is subject to license terms.

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
  obtain a copy of the License at http://odftoolkit.org/docs/license.txt

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

  See the License for the specific language governing permissions and
  limitations under the License.

 ************************************************************************/

/**  Provide convenient methods to manipulate table in ODF text and spreadsheet document.

	<p>
	Table is a common used feature in ODF.
	This package provides convenient methods to manipulate table feature and its child features, such as row, column, cell, cell range.
	<p>
	OdfTable represents table in ODF. It provides methods to new a table, delete a table, get rows,
	columns, and cells of a table, new a cell range, insert and delete rows and columns, and so on.
	<p>
	OdfTableRow and OdfTableColumn represents row and column in ODF. It provides methods to get cells,
	get next and previous row and column, set properties of rows and columns, and so on.
	<p>
	OdfTableCell represents cell in ODF. It provides many methods to set the properties and values of a cell,
	such as horizontal alignment, vertical alignment, value, formula, value format, and so on.
	<p>
	OdfTableRange represents a range of cells in ODF. It provides a method to merge a range of cells to a single cell, and some other methods too. */
package org.odftoolkit.odfdom.doc.table;
