package org.odftoolkit.odfdom.maven_performancetest_plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfFileDom;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.element.office.OdfSpreadsheet;
import org.odftoolkit.odfdom.doc.element.table.OdfTable;
import org.odftoolkit.odfdom.doc.element.table.OdfTableCell;
import org.odftoolkit.odfdom.doc.element.table.OdfTableRow;
import org.odftoolkit.odfdom.doc.element.text.OdfParagraph;
import org.odftoolkit.odfdom.dom.OdfNamespace;
import org.odftoolkit.odfdom.dom.type.office.OdfValueType;
import org.w3c.dom.NodeList;

public class PerformanceEvaluation {

	private static String TEST_FILE_FOLDER;

	private static String[] TEST_FILE_NAME;

	//private static final String timesheetTemplate = "timesheetTemplate.ods";
	//private static final String memorysheetTemplate = "memorysheetTemplate.ods";

	private double[] totalTime = new double[3];
	private double[] totalLoadTimeForEach = null;
	private double[] totalSaveTimeForEach = null;
	private double[] totalParseTimeForEach = null;

	private double[] memoryfootprint = null;

	private String time_spreadsheet = null;
	private String memory_spreadsheet = null;

	private int count = 1;
	private String testTag = "new test";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 1. init variables
		//if (args.length < 4) {
		//	System.out
		//			.println("Usage: PerformanceEvaluation TestFileFolder myNewTest timelog.ods memorylog.ods");
		//}
		//PerformanceEvaluation test = new PerformanceEvaluation(args[0], args[1],10, args[2],args[3]);
		PerformanceEvaluation test = new PerformanceEvaluation("C:\\workplace\\resources",
				"test",1, "C:/workplace/ODFToolkit/odfdom~developer/timelog.ods","C:/workplace/ODFToolkit/odfdom~developer/memorylog.ods");

		try {
			// 1. Collect Test result
			test.test();
			// 2. Save to spreadsheet
			test.writeToLog();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public PerformanceEvaluation(String folder, String testtag, int count, String timelog,	String memorylog) {
		
		TEST_FILE_FOLDER = folder;
		if (folder.endsWith("\\") )
				TEST_FILE_FOLDER = folder;
		else TEST_FILE_FOLDER = folder+"\\";
		readFileList(folder);
		if (TEST_FILE_NAME==null) return;
		
		time_spreadsheet = timelog;
		memory_spreadsheet = memorylog;
		this.count = count;
		testTag = testtag;

		totalTime[0] = 0;
		totalTime[1] = 0;
		totalTime[2] = 0;

		totalLoadTimeForEach = new double[TEST_FILE_NAME.length];
		totalSaveTimeForEach = new double[TEST_FILE_NAME.length];
		totalParseTimeForEach = new double[TEST_FILE_NAME.length];
		memoryfootprint = new double[TEST_FILE_NAME.length * 3];

		for (int i = 0; i < TEST_FILE_NAME.length; i++) {
			totalLoadTimeForEach[i] = 0;
			totalSaveTimeForEach[i] = 0;
			totalParseTimeForEach[i] = 0;
			memoryfootprint[3 * i] = 0;
			memoryfootprint[3 * i + 1] = 0;
			memoryfootprint[3 * i + 2] = 0;
		}

	}
	
	private void readFileList(String folder)
	{		
		String filename;
		
		System.out.println("[PerformaceTestPlugin] Reading test documents from "+folder);
		File myFolder = new File(folder);
		if (!myFolder.isDirectory())
			return;
		File[] files = myFolder.listFiles();
		ArrayList myList = new ArrayList();
		
		for(int i=0;i<files.length;i++)
		{
			filename = files[i].getName();
			if (filename.endsWith("ods") || filename.endsWith("odp") || filename.endsWith("odt"))
				myList.add(filename);
			//TEST_FILE_NAME[i]=files[i].getName();
			//System.out.println("name="+TEST_FILE_NAME[i]);
		}
		
		System.out.println("[PerformaceTestPlugin] "+myList.size()+" test files are loaded");
		if (myList.size()>0)
			TEST_FILE_NAME = (String[]) myList.toArray(new String[1]);
	}
	
	public void writeToLog() throws Exception {
		FileInputStream timefile, memoryfile;
		OdfDocument timedoc, memorydoc;

		if (TEST_FILE_NAME==null) return;
		
		try {
			timefile = new FileInputStream(time_spreadsheet);
			timedoc = OdfDocument.loadDocument(timefile);
		} catch (FileNotFoundException e) {
			//Create an empty spreadsheet
			timedoc = OdfSpreadsheetDocument.createSpreadsheetDocument();
			OdfSpreadsheet spreadsheet = (OdfSpreadsheet) timedoc.getContentDom().getElementsByTagNameNS(
					OdfNamespace.OFFICE.getUri(), "spreadsheet").item(0);
			spreadsheet.removeChild(spreadsheet.getFirstChild());
		}

		try {
			memoryfile = new FileInputStream(memory_spreadsheet);
			memorydoc = OdfDocument.loadDocument(memoryfile);
		} catch (FileNotFoundException e) {
			//Create an empty spreadsheet
			memorydoc = OdfSpreadsheetDocument.createSpreadsheetDocument();
			OdfSpreadsheet spreadsheet = (OdfSpreadsheet) memorydoc.getContentDom().getElementsByTagNameNS(
					OdfNamespace.OFFICE.getUri(), "spreadsheet").item(0);
			spreadsheet.removeChild(spreadsheet.getFirstChild());
		}
		
		String[] summaryName = new String[] {"Load All Documents","Parse All Documents","Save All Documents"};
		updateTableCells(timedoc,"Summary",totalTime, summaryName);
		updateTableCells(timedoc, "Load ODF", totalLoadTimeForEach, TEST_FILE_NAME);
		updateTableCells(timedoc, "Parse ODF", totalParseTimeForEach, TEST_FILE_NAME);
		updateTableCells(timedoc, "Save ODF", totalSaveTimeForEach, TEST_FILE_NAME);
		
		String[] memorylabel = new String[TEST_FILE_NAME.length*3];
		for(int i=0;i<TEST_FILE_NAME.length;i++)
		{
			memorylabel[3*i]="load "+TEST_FILE_NAME[i];
			memorylabel[3*i+1]="parse "+TEST_FILE_NAME[i];
			memorylabel[3*i+2]="save "+TEST_FILE_NAME[i];
		}
		updateTableCells(memorydoc, "Memory footprint", memoryfootprint, memorylabel);
		
		timedoc.save(time_spreadsheet);
		System.out.println("[PerformaceTestPlugin] Test results are written to "+time_spreadsheet);
		memorydoc.save(memory_spreadsheet);
		System.out.println("[PerformaceTestPlugin] Test results are written to "+memory_spreadsheet);
	}
	
	private void updateTableCells(OdfDocument odfdoc, String tablename,
			double[] values, String[] labels) {
		int i = 0, j = 0;
		OdfTableRow td;
		OdfTableCell cell;
		OdfFileDom dom;
		NodeList tableList;
		OdfTable myTable;
		NodeList lst;
		OdfParagraph p;
		OdfSpreadsheet spreadsheet=null;
		
		try {
			dom = odfdoc.getContentDom();
			tableList = dom.getElementsByTagNameNS(
					OdfNamespace.TABLE.getUri(), "table");
			spreadsheet = (OdfSpreadsheet) dom.getElementsByTagNameNS(
					OdfNamespace.OFFICE.getUri(), "spreadsheet").item(0);
			
			i = 0;
			if (tableList.getLength()>0)
			{
				for(;i<tableList.getLength();i++)
				{
					String currentname = ((OdfTable) tableList.item(i)).getName();
					if (currentname==null) {
						currentname="";
					}
					if (currentname.equalsIgnoreCase(tablename))
						break;
				}
			}
			if (i<tableList.getLength()) //table with the specific table name is found
				myTable = (OdfTable) tableList.item(i);
			else { //table with the specific table name is not found. Create table
				myTable = (OdfTable)dom.createOdfElement(OdfTable.class);
				myTable.setName(tablename);
				spreadsheet.appendChild(myTable);
			}

			lst = myTable.getElementsByTagNameNS(OdfNamespace.TABLE
					.getUri(), "table-row");
			if (lst.getLength()==0) { //the first table row is not existed. Create table row
				td = (OdfTableRow)dom.createOdfElement(OdfTableRow.class);
				cell = (OdfTableCell)dom.createOdfElement(OdfTableCell.class);
				p = (OdfParagraph) dom.createOdfElement(OdfParagraph.class);
				if (tablename.startsWith("Memory"))
						p.setTextContent("memory(b)");
				else p.setTextContent("time(ms)");
				td.appendCell(cell);
				cell.appendChild(p);
				myTable.appendChild(td);
			}
			else td = (OdfTableRow) lst.item(0); //the first table row is existed.
			cell = (OdfTableCell)dom.createOdfElement(OdfTableCell.class);
			td.appendCell(cell);
			p = (OdfParagraph) dom.createOdfElement(OdfParagraph.class);
			p.setTextContent(testTag);
			cell.appendChild(p);
			
			
			for (i = 1; i < values.length+1; i++) {
				if (i < lst.getLength()) { //table row is existed
					td = (OdfTableRow) lst.item(i);
				}
				else { //table row is not existed.
					td = (OdfTableRow)dom.createOdfElement(OdfTableRow.class);
					myTable.appendChild(td);
					//append first cell with labels
					cell = (OdfTableCell)dom.createOdfElement(OdfTableCell.class);
					td.appendCell(cell);
					p = (OdfParagraph)dom.createOdfElement(OdfParagraph.class);
					p.setTextContent(labels[j]);
					cell.appendChild(p);
				}
				cell = (OdfTableCell)dom.createOdfElement(OdfTableCell.class);
				cell.setValueType(OdfValueType.FLOAT);
				cell.setValue(new Double(values[j]));
				p = (OdfParagraph)dom.createOdfElement(OdfParagraph.class);
				p.setTextContent(values[j] + "");
				cell.appendChild(p);
				td.appendCell(cell);
				j++;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void firsttry() throws Exception {
		OdfDocument doc = null;
		OdfFileDom dom = null;
		String filename = null;
		
		for (int j = 0; j < TEST_FILE_NAME.length; j++) {
			filename = TEST_FILE_FOLDER + TEST_FILE_NAME[j];
			doc = OdfDocument.loadDocument(filename);
			dom = doc.getContentDom();
			doc.save(filename);
		}
	}

	public void test() throws Exception {
		long start, end;
		OdfDocument doc = null;
		OdfFileDom dom = null;
		String filename = null;

		if (TEST_FILE_NAME==null) return;
		
		firsttry();

		for (int i = 0; i < count; i++) {
			for (int j = 0; j < TEST_FILE_NAME.length; j++) {
				filename = TEST_FILE_FOLDER + TEST_FILE_NAME[j];
				start = System.currentTimeMillis();
				doc = OdfDocument.loadDocument(filename);
				end = System.currentTimeMillis();
				totalLoadTimeForEach[j] += end - start;
				totalTime[0] += end - start;
				if (i == 0) {
					System.gc();
					memoryfootprint[3 * j] = Runtime.getRuntime().totalMemory()
							- Runtime.getRuntime().freeMemory();
				}

				start = System.currentTimeMillis();
				dom = doc.getContentDom();
				end = System.currentTimeMillis();
				totalParseTimeForEach[j] += end - start;
				totalTime[1] += end - start;
				if (i == 0) {
					System.gc();
					memoryfootprint[3 * j + 1] = Runtime.getRuntime()
							.totalMemory()
							- Runtime.getRuntime().freeMemory();
				}

				start = System.currentTimeMillis();
				doc.save(filename);
				end = System.currentTimeMillis();
				totalSaveTimeForEach[j] += end - start;
				totalTime[2] += end - start;

				doc = null;
				dom = null;
				if (i == 0) {
					System.gc();
					memoryfootprint[3 * j + 2] = Runtime.getRuntime()
							.totalMemory()
							- Runtime.getRuntime().freeMemory();
				}
			}
		}
		
		for(int i=0;i<3;i++)
			totalTime[i] = totalTime[i] / count;
		
		for(int i=0;i<TEST_FILE_NAME.length;i++)
		{
			totalLoadTimeForEach[i] = totalLoadTimeForEach[i] / count;
			totalParseTimeForEach[i] = totalParseTimeForEach[i] / count;
			totalSaveTimeForEach[i] = totalSaveTimeForEach[i] / count;
		}
	}
}
