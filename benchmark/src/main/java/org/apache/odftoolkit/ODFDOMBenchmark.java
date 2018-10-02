/*
  Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */
package org.apache.odftoolkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.office.OfficeSpreadsheetElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.w3c.dom.NodeList;

/**
 * See JIRA issue https://issues.apache.org/jira/browse/ODFTOOLKIT-479
 * for details!
 */
public class ODFDOMBenchmark {

    private static final Logger LOG = Logger.getLogger(ODFDOMBenchmark.class.getName());
    private static final String TEST_FILE_DIR_NAME = "performance";
    public ODFDOMBenchmark() {
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Fork(3)
    @Measurement(iterations = 3)
    @Warmup(iterations = 1)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public OdfFileDom testPerformance(ExecutionPlan plan) {
        OdfFileDom dom = null;
        try {
            long start, end;
            OdfDocument doc = null;

            if (plan.testFileStreams == null) {
                return dom;
            }
            LOG.info("Starting the test!");
            for (int i = 0; i < plan.count; i++) {
                int j = -1;
                for (String filename : plan.testFileStreams.keySet()) {
                	LOG.info("Starting reading " + filename);
                    j++;
                    start = System.currentTimeMillis();
                    doc = OdfDocument.loadDocument(plan.testFileStreams.get(filename));
                    end = System.currentTimeMillis();
                    plan.totalLoadTimeForEach[j] += end - start;
                    plan.totalTime[0] += end - start;
                    if (i == 0) {
                        System.gc();
                        plan.memoryfootprint[3 * j] = Runtime.getRuntime().totalMemory()
                                - Runtime.getRuntime().freeMemory();
                    }
                    LOG.info("Starting parsing!");
                    start = System.currentTimeMillis();
                    dom = doc.getContentDom();
                    end = System.currentTimeMillis();
                    LOG.info("Stop parsing!");
                    plan.totalParseTimeForEach[j] += end - start;
                    plan.totalTime[1] += end - start;
                    if (i == 0) {
                        System.gc();
                        plan.memoryfootprint[3 * j + 1] = Runtime.getRuntime().totalMemory()
                                - Runtime.getRuntime().freeMemory();
                    }

                    start = System.currentTimeMillis();
                    doc.save(plan.rootPath + File.separator + filename);
                    end = System.currentTimeMillis();
                    plan.totalSaveTimeForEach[j] += end - start;
                    plan.totalTime[2] += end - start;

                    doc = null;
                    dom = null;
                    if (i == 0) {
                        System.gc();
                        plan.memoryfootprint[3 * j + 2] = Runtime.getRuntime().totalMemory()
                                - Runtime.getRuntime().freeMemory();
                    }
                }
            }

            for (int i = 0; i < 3; i++) {
                plan.totalTime[i] = plan.totalTime[i] / plan.count;
            }

            for (int i = 0; i < plan.testFileStreams.size(); i++) {
                plan.totalLoadTimeForEach[i] = plan.totalLoadTimeForEach[i] / plan.count;
                plan.totalParseTimeForEach[i] = plan.totalParseTimeForEach[i] / plan.count;
                plan.totalSaveTimeForEach[i] = plan.totalSaveTimeForEach[i] / plan.count;
            }

        } catch (Exception e) {
            Logger.getLogger(ODFDOMBenchmark.class.getName()).log(java.util.logging.Level.SEVERE, null, e);

        }
        return dom;
    }

    @State(Scope.Benchmark)
    public static class ExecutionPlan {

        public double[] totalTime = new double[3];
        public double[] totalLoadTimeForEach = null;
        public double[] totalSaveTimeForEach = null;
        public double[] totalParseTimeForEach = null;
        public double[] memoryfootprint = null;
        public String time_spreadsheet = null;
        public String memory_spreadsheet = null;
        public String reportFileFolder = null;
        public int count;
        public String rootPath;
        public URL jarLocation;
        public Map<String, InputStream> testFileStreams;
        public String outputPath;

        @Setup(Level.Iteration)
        public void setUp() {

            CodeSource src = ODFDOMBenchmark.class.getProtectionDomain().getCodeSource();
            if (src != null) {
                jarLocation = src.getLocation();
            }else{
                LOG.severe("Could not locate JAR file!");
            }

            // jar file URL
            rootPath = jarLocation.toExternalForm();
            // directory containing the JAR
            rootPath = rootPath.substring(6, rootPath.lastIndexOf("/"));

            outputPath = rootPath + File.separatorChar + TEST_FILE_DIR_NAME;
            new File(outputPath).mkdir();
            String executeTimesTest = System.getProperty("executetimes");
            if (executeTimesTest != null) {
                count = Integer.parseInt(executeTimesTest);
            } else {
                count = 1;
            }
            reportFileFolder = getOutputPath() + File.separatorChar;
            memory_spreadsheet = reportFileFolder + "memorylog.ods";
            time_spreadsheet = reportFileFolder + "timelog.ods";

            testFileStreams = new HashMap<String, InputStream>();
            readFileList(jarLocation);
            if (testFileStreams == null) {
                LOG.severe("No test files found!");
                return;
            }

            totalTime[0] = 0;
            totalTime[1] = 0;
            totalTime[2] = 0;

            totalLoadTimeForEach = new double[testFileStreams.size()];
            totalSaveTimeForEach = new double[testFileStreams.size()];
            totalParseTimeForEach = new double[testFileStreams.size()];
            memoryfootprint = new double[testFileStreams.size() * 3];

            for (int i = 0; i < testFileStreams.size(); i++) {
                totalLoadTimeForEach[i] = 0;
                totalSaveTimeForEach[i] = 0;
                totalParseTimeForEach[i] = 0;
                memoryfootprint[3 * i] = 0;
                memoryfootprint[3 * i + 1] = 0;
                memoryfootprint[3 * i + 2] = 0;
            }
        }

        @TearDown(Level.Iteration)
        public void writeToLog() throws Exception {
            FileInputStream timefile, memoryfile;
            OdfDocument timedoc, memorydoc;

            if (testFileStreams == null) {
                return;
            }

            try {
                timefile = new FileInputStream(time_spreadsheet);
                timedoc = OdfDocument.loadDocument(timefile);
            } catch (FileNotFoundException e) {
                //Create an empty spreadsheet
                timedoc = OdfSpreadsheetDocument.newSpreadsheetDocument();
                OfficeSpreadsheetElement spreadsheet = (OfficeSpreadsheetElement) timedoc.getContentDom().getElementsByTagNameNS(
                        OdfDocumentNamespace.OFFICE.getUri(), "spreadsheet").item(0);
                spreadsheet.removeChild(spreadsheet.getFirstChild());
            }

            try {
                memoryfile = new FileInputStream(memory_spreadsheet);
                memorydoc = OdfDocument.loadDocument(memoryfile);
            } catch (FileNotFoundException e) {
                //Create an empty spreadsheet
                memorydoc = OdfSpreadsheetDocument.newSpreadsheetDocument();
                OfficeSpreadsheetElement spreadsheet = (OfficeSpreadsheetElement) memorydoc.getContentDom().getElementsByTagNameNS(
                        OdfDocumentNamespace.OFFICE.getUri(), "spreadsheet").item(0);
                spreadsheet.removeChild(spreadsheet.getFirstChild());
            }

            String[] summaryName = new String[]{"Load All Documents", "Parse All Documents", "Save All Documents"};
            updateTableCells(timedoc, "Summary", totalTime, summaryName);
            updateTableCells(timedoc, "Load ODF", totalLoadTimeForEach, testFileStreams.keySet().toArray(new String[0]));
            updateTableCells(timedoc, "Parse ODF", totalParseTimeForEach, testFileStreams.keySet().toArray(new String[0]));
            updateTableCells(timedoc, "Save ODF", totalSaveTimeForEach, testFileStreams.keySet().toArray(new String[0]));

            String[] memorylabel = new String[testFileStreams.size() * 3];
            int i = -1;
            for (String testFileName : testFileStreams.keySet()) {
                i++;

                memorylabel[3 * i] = "load " + testFileName;
                memorylabel[3 * i + 1] = "parse " + testFileName;
                memorylabel[3 * i + 2] = "save " + testFileName;
            }
            updateTableCells(memorydoc, "Memory footprint", memoryfootprint, memorylabel);

            timedoc.save(time_spreadsheet);
            LOG.log(java.util.logging.Level.INFO, "[PerformaceTest] Test results are written to {0}", time_spreadsheet);
            memorydoc.save(memory_spreadsheet);
            LOG.log(java.util.logging.Level.INFO, "[PerformaceTest] Test results are written to {0}", memory_spreadsheet);
        }

        private String getOutputPath() {
            String path = null;
            File outputpath = new File(rootPath, "performance-reports");
            if (!outputpath.exists()) {
                outputpath.mkdir();
            }
            path = outputpath.getPath();
            return path;
        }

        private void readFileList(URL jar) {
            try {
                LOG.log(java.util.logging.Level.INFO, "[PerformaceTest] Reading test documents from JAR {0}", jar);
                ZipInputStream zip = null;
                ZipFile zf;
                File jarFile = new File(jar.toExternalForm().substring(6).replace('/', File.separatorChar));
                zf = new ZipFile(jarFile);
                try {
                    zip = new ZipInputStream(jar.openStream());
                    while (true) {
                        ZipEntry e = zip.getNextEntry();
                        if (e == null) {
                            break;
                        }
                        String name = e.getName();
                        if (name.startsWith(TEST_FILE_DIR_NAME) && !e.isDirectory() && (name.endsWith("ods") || name.endsWith("odp") || name.endsWith("odt"))) {
                            LOG.log(java.util.logging.Level.INFO, "[PerformaceTest] Reading test document {0}", name);
                            testFileStreams.put(name, zf.getInputStream(zf.getEntry(name)));
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ODFDOMBenchmark.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                } finally {
                    try {
                        zip.close();
                    } catch (IOException ex) {
                        Logger.getLogger(ODFDOMBenchmark.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ODFDOMBenchmark.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }

        private void updateTableCells(OdfDocument odfdoc, String tablename,
                double[] values, String[] labels) {
            int i = 0, j = 0;
            TableTableRowElement td;
            TableTableCellElement cell;
            OdfFileDom dom;
            NodeList tableList;
            TableTableElement myTable;
            NodeList lst;
            OdfTextParagraph p;
            OfficeSpreadsheetElement spreadsheet = null;

            try {
                dom = odfdoc.getContentDom();
                tableList = dom.getElementsByTagNameNS(
                        OdfDocumentNamespace.TABLE.getUri(), "table");
                spreadsheet = (OfficeSpreadsheetElement) dom.getElementsByTagNameNS(
                        OdfDocumentNamespace.OFFICE.getUri(), "spreadsheet").item(0);

                i = 0;
                if (tableList.getLength() > 0) {
                    for (; i < tableList.getLength(); i++) {
                        String currentname = ((TableTableElement) tableList.item(i)).getTableNameAttribute();
                        if (currentname == null) {
                            currentname = "";
                        }
                        if (currentname.equalsIgnoreCase(tablename)) {
                            break;
                        }
                    }
                }
                if (i < tableList.getLength()) //table with the specific table name is found
                {
                    myTable = (TableTableElement) tableList.item(i);
                } else { //table with the specific table name is not found. Create table
                    myTable = dom.newOdfElement(TableTableElement.class);
                    myTable.setTableNameAttribute(tablename);
                    spreadsheet.appendChild(myTable);
                }

                lst = myTable.getElementsByTagNameNS(OdfDocumentNamespace.TABLE.getUri(), "table-row");
                if (lst.getLength() == 0) { //the first table row is not existed. Create table row
                    td = dom.newOdfElement(TableTableRowElement.class);
                    cell = dom.newOdfElement(TableTableCellElement.class);
                    p = dom.newOdfElement(OdfTextParagraph.class);
                    if (tablename.startsWith("Memory")) {
                        p.setTextContent("memory(b)");
                    } else {
                        p.setTextContent("time(ms)");
                    }
                    td.appendChild(cell);
                    cell.appendChild(p);
                    myTable.appendChild(td);
                } else {
                    td = (TableTableRowElement) lst.item(0); //the first table row is existed.
                }
                cell = dom.newOdfElement(TableTableCellElement.class);
                td.appendChild(cell);
                p = dom.newOdfElement(OdfTextParagraph.class);
                cell.appendChild(p);

                for (i = 1; i < values.length + 1; i++) {
                    if (i < lst.getLength()) { //table row is existed
                        td = (TableTableRowElement) lst.item(i);
                    } else { //table row is not existed.
                        td = dom.newOdfElement(TableTableRowElement.class);
                        myTable.appendChild(td);
                        //append first cell with labels
                        cell = dom.newOdfElement(TableTableCellElement.class);
                        td.appendChild(cell);
                        p = dom.newOdfElement(OdfTextParagraph.class);
                        p.setTextContent(labels[i]);
                        cell.appendChild(p);
                    }
                    cell = dom.newOdfElement(TableTableCellElement.class);
                    cell.setOfficeValueTypeAttribute("float");
                    cell.setOfficeValueAttribute(new Double(values[j]));
                    p = dom.newOdfElement(OdfTextParagraph.class);
                    p.setTextContent(values[j] + "");
                    cell.appendChild(p);
                    td.appendChild(cell);
                    j++;
                }
            } catch (Exception e) {
                LOG.log(java.util.logging.Level.SEVERE, null, e);
            }
        }
    }
}
