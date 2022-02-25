/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * <p>Use is subject to license terms.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0. You can also obtain a copy of the License at
 * http://odftoolkit.org/docs/license.txt
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 *
 * <p>See the License for the specific language governing permissions and limitations under the
 * License.
 *
 * <p>**********************************************************************
 */
package org.odftoolkit.odfdom.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.odftoolkit.odfdom.pkg.rdfa.Util;

/** Test utility class providing resources for the test in- and output */
public final class ResourceUtilities {

  static final String INITIAL_OPS_SUFFIX = "-initial_ops.json";
  static final String RELOADED_OPS_SUFFIX = "-reloaded_ops.json";
  static final String HYPEN = "-";
  static final String ODT_SUFFIX = ".odt";
  static final List<String> NO_OPERATIONS = new ArrayList<String>(0);
  static final String NO_METHOD_NAME = "";
  // the smallest possible test document of this ODF type. Edited manually and proofed valid by
  // Apache ODF Validator.
  static final String EMPTY_AS_CAN_BE = "empty_as_can_be";
  private static final String PATH_FROM_TEST_CLASSES_TO_REFENCE = "test-reference" + File.separator;
  private static final String PATH_FROM_TEST_CLASSES_TO_OUTPUT = "test-output" + File.separator;
  private static final String PATH_FROM_TEST_CLASSES_TO_INPUT = "test-input" + File.separator;
  private static final String PATH_TO_SRC_TEST_INPUT =
      "src"
          + File.separator
          + "test"
          + File.separator
          + "resources"
          + File.separator
          + "test-input"
          + File.separator;
  private static final String PATH_TO_SRC_TEST_REFERENCES =
      "src"
          + File.separator
          + "test"
          + File.separator
          + "resources"
          + File.separator
          + "test-reference"
          + File.separator;

  private ResourceUtilities() {}

  static {
    new File(getTestInputFolder()).mkdirs();
    new File(getTestOutputFolder()).mkdirs();
  }

  /**
   * The relative path of the test file will be resolved and the absolute will be returned
   *
   * @param relativeFilePath Path of the test resource relative to <code>
   *     target/test-classes/test-input</code>.
   * @return the absolute path of the test file
   * @throws FileNotFoundException If the file could not be found
   */
  public static String getAbsoluteOutputPath(String relativeFilePath) throws FileNotFoundException {
    return getMavenTestFolder() + PATH_FROM_TEST_CLASSES_TO_OUTPUT + relativeFilePath;
  }

  /**
   * The relative path of the test file will be resolved and the absolute will be returned
   *
   * @param relativeFilePath Path of the test resource relative to <code>
   *     target/test-classes/test-output</code>.
   * @return the absolute path of the test file
   * @throws FileNotFoundException If the file could not be found
   */
  public static String getAbsoluteInputPath(String relativeFilePath) throws FileNotFoundException {
    return getAbsolutePath(PATH_FROM_TEST_CLASSES_TO_INPUT + relativeFilePath);
  }

  /**
   * The relative path of the test file will be resolved and the absolute will be returned
   *
   * @param relativeFilePath Path of the test resource relative to <code>target/test-classes</code>.
   * @return the absolute path of the test file
   * @throws FileNotFoundException If the file could not be found
   */
  public static String getAbsolutePath(String relativeFilePath) throws FileNotFoundException {
    String absPath = null;
    try {
      URI uri = getURI(relativeFilePath);
      if (uri == null) {
        throw new FileNotFoundException("Could not find the file '" + relativeFilePath + "'!");
      }
      absPath = uri.getPath();
    } catch (URISyntaxException ex) {
      Logger.getLogger(ResourceUtilities.class.getName()).log(Level.SEVERE, null, ex);
    }
    return absPath;
  }

  /**
   * The relative path of the test file will be resolved and the absolute will be returned
   *
   * @param relativeFilePath Path relative to <code>target/test-classes/test-input</code>.
   * @return the URI created based on the relativeFilePath
   * @throws URISyntaxException if no URI could be created from the given relative path
   */
  public static URI getTestInputURI(String relativeFilePath)
      throws URISyntaxException, FileNotFoundException {
    return getURI(PATH_FROM_TEST_CLASSES_TO_INPUT + relativeFilePath);
  }

  /**
   * The relative path of the test file will be resolved and the absolute will be returned
   *
   * @param relativeFilePath For a given path the usage of <code>File.separatoracter</code> is
   *     required. Path of the resource is relative to classpath: <code>target/test-classes</code>
   *     or <code>target/classes</code>
   * @return the URI created based on the relativeFilePath
   * @throws URISyntaxException if no URI could be created from the given relative path
   * @throws FileNotFoundException if the file could not be found by the ClassLoader
   */
  public static URI getURI(String relativeFilePath)
      throws FileNotFoundException, URISyntaxException {
    URL url = ResourceUtilities.class.getClassLoader().getResource(relativeFilePath);
    if (url == null) {
      throw new FileNotFoundException("Could not find the file '" + relativeFilePath + "'!");
    }
    String filePath = "file:" + url.getPath().replace("%5c", "/");
    filePath = Util.toExternalForm(new URI(filePath));
    return new URI(filePath);
  }

  /**
   * Relative to the test input directory (ie. "target/test-classes/test-input") a test file will be
   * returned dependent on the relativeFilePath provided.
   *
   * @param relativeFilePath Path of the test output resource relative to <code>
   *     target/test-classes/test-inpput/</code>.
   * @return the empty <code>File</code> of the test output (to be filled)
   */
  public static File getTestInputFile(String relativeFilePath) {
    return new File(getTestInputFolder() + relativeFilePath);
  }

  /**
   * @return the absolute path of the test output folder, which is usually <code>
   *     target/test-classes/test-input</code>.
   */
  public static String getTestInputFolder() {
    return getMavenTestFolder() + PATH_FROM_TEST_CLASSES_TO_INPUT;
  }

  /**
   * Relative to the test output directory (ie. "target/test-classes/test-output") a test file will
   * be returned dependent on the relativeFilePath provided.
   *
   * @param relativeFilePath Path of the test output resource relative to <code>
   *     target/test-classes/test-output/</code>.
   * @return the empty <code>File</code> of the test output (to be filled)
   */
  public static File getTestOutputFile(String relativeFilePath) {
    return new File(getTestOutputFolder() + relativeFilePath);
  }

  /**
   * @return the absolute path of the test output folder, which is usually <code>
   *     target/test-classes/test-output</code>.
   */
  public static String getTestOutputFolder() {
    return getMavenTestFolder() + PATH_FROM_TEST_CLASSES_TO_OUTPUT;
  }

  /**
   * @return the absolute path of the test output folder, which is usually <code>
   *     target/test-classes/</code>.
   */
  public static String getMavenTestFolder() {
    //    String testFolder = null;
    //    try {
    //      testFolder = ResourceUtilities.class.getClassLoader().getResource("").toURI().getPath();
    //    } catch (URISyntaxException ex) {
    //      Logger.getLogger(ResourceUtilities.class.getName()).log(Level.SEVERE, null, ex);
    //    }
    //    return testFolder;

    // does not work from JAR only with test classes..
    return "target"
        + File.separator
        + "test-classes"
        + File
            .separator; // ResourceUtilities.class.getClassLoader().getResource("").toURI().getPath();
  }

  /**
   * The Input of the test file will be resolved and the absolute will be returned
   *
   * @param relativeFilePath Path of the test resource relative to <code>
   *     target/test-classes/test-input</code>.
   * @return the absolute path of the test file
   */
  public static InputStream getTestInputAsStream(String relativeFilePath) {
    return getTestResourceAsStream(PATH_FROM_TEST_CLASSES_TO_INPUT + relativeFilePath);
  }

  /**
   * The Input of the test file will be resolved and the absolute will be returned
   *
   * @param relativeFilePath Path of the test resource relative to <code>
   *     target/test-classes/test-output</code>.
   * @return the absolute path of the test file
   */
  public static InputStream getTestOutputAsStream(String relativeFilePath) {
    return getTestResourceAsStream(PATH_FROM_TEST_CLASSES_TO_OUTPUT + relativeFilePath);
  }

  /**
   * The Input of the test file will be resolved and the absolute will be returned
   *
   * @param relativeFilePath Path of the test resource relative to <code>target/test-classes/</code>
   *     .
   * @return the absolute path of the test file
   */
  public static InputStream getTestResourceAsStream(String relativeFilePath) {
    return ResourceUtilities.class.getClassLoader().getResourceAsStream(relativeFilePath);
  }

  /**
   * Relative to the test reference directory (ie. "test-classes/test/resources") a test file will
   * be returned dependent on the relativeFilePath provided.
   *
   * @param relativeFilePath Path of the test output resource relative to <code>
   *     target/test-classes/test-reference</code>.
   * @return the empty <code>File</code> of the test output (to be filled)
   */
  public static File getTestReferenceFile(String relativeFilePath) {
    return new File(getTestReferenceFolder() + relativeFilePath);
  }

  /**
   * @return the absolute path of the test reference folder, which is for ODFDOM <code>
   *     target/test-classes/test-reference</code>.
   */
  public static String getTestReferenceFolder() {
    String refFolder = null;
    try {
      refFolder =
          ResourceUtilities.class.getClassLoader().getResource("").toURI().getPath()
              + PATH_FROM_TEST_CLASSES_TO_REFENCE;
    } catch (URISyntaxException ex) {
      Logger.getLogger(ResourceUtilities.class.getName()).log(Level.SEVERE, null, ex);
    }
    return refFolder;
  }

  /**
   * @return the absolute path of the test reference folder, which is for ODFDOM <code>
   *     src/test/resources/test-reference</code>.
   */
  public static String getSrcTestReferenceFolder() {
    String refFolder = null;
    try {
      Path testClassesPath =
          Path.of(ResourceUtilities.class.getClassLoader().getResource("").toURI());

      String projectRootPath = testClassesPath.getParent().getParent().toString();
      refFolder = projectRootPath + File.separator + PATH_TO_SRC_TEST_REFERENCES;
    } catch (Throwable ex) {
      Logger.getLogger(ResourceUtilities.class.getName()).log(Level.SEVERE, null, ex);
    }
    return refFolder;
  }

  /**
   * @return the absolute path of the test reference folder, which is for ODFDOM <code>
   *     src/test/resources/test-input</code>.
   */
  public static String getSrcTestInputFolder() {
    String refFolder = null;
    try {
      Path testClassesPath =
          Path.of(ResourceUtilities.class.getClassLoader().getResource("").toURI());

      String projectRootPath = testClassesPath.getParent().getParent().toString();
      refFolder = projectRootPath + File.separator + PATH_TO_SRC_TEST_INPUT;
    } catch (Throwable ex) {
      Logger.getLogger(ResourceUtilities.class.getName()).log(Level.SEVERE, null, ex);
    }
    return refFolder;
  }

  /** @return the <code>File</code> of the test directory, within the temporary directory. */
  public static File getTempTestDirectory() {
    File tempDir = new File(ResourceUtilities.getTestOutputFolder() + File.separator + "temp");
    tempDir.mkdir(); // if it already exist no problem
    return tempDir;
  }

  /*
   * @param file the file to be saved, when creating a test file, you might use <code>newTestOutputFile(String relativeFilePath)</code>.
   * @param inputData the data to be written into the file
   */
  public static void saveStringToFile(File file, String data) {
    saveStringToFile(file, Charset.forName("UTF-8"), data);
  }

  /**
   * @param file the file to be saved, when creating a test file, you might use <code>
   *     newTestOutputFile(String relativeFilePath)</code>.
   * @param charset the character encoding
   * @param inputData the data to be written into the file
   */
  public static void saveStringToFile(File file, Charset charset, String inputData) {
    BufferedWriter out = null;
    try {
      out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset));
      // out = new BufferedWriter(new FileWriter(file));
      out.write(inputData);
    } catch (IOException ex) {
      Logger.getLogger(ResourceUtilities.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      try {
        if (out != null) {
          out.close();
        }
      } catch (IOException ex) {
        Logger.getLogger(ResourceUtilities.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  /**
   * @param file the file to be loaded, when accessing a test file, you might use <code>
   *     newTestOutputFile(String relativeFilePath)</code>.
   * @return the data from the given file as a String
   */
  public static String loadFileAsString(File file) {
    FileInputStream input = null;
    String result = null;
    try {
      input = new FileInputStream(file);
      byte[] fileData = new byte[input.available()];
      input.read(fileData);
      input.close();
      result = new String(fileData, "UTF-8");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(ResourceUtilities.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(ResourceUtilities.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      try {
        input.close();
      } catch (IOException ex) {
        Logger.getLogger(ResourceUtilities.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return result;
  }

  /**
   * @param file the file to be loaded, when accessing a test file, you might use <code>
   *     newTestOutputFile(String relativeFilePath)</code>.
   * @return the data from the given file as a byte array
   */
  public static byte[] loadFileAsBytes(File file) throws IOException {
    // Open the file
    RandomAccessFile f = new RandomAccessFile(file, "r");
    try {
      // check length
      long longlength = f.length();
      int length = (int) longlength;
      if (length != longlength) {
        throw new IOException("File size >= 2 GB");
      }
      // Read file and return data
      byte[] data = new byte[length];
      f.readFully(data);
      return data;
    } finally {
      f.close();
    }
  }

  /**
   * Returns the JSONObject as String with all characters over 127 encoded as escaped unicode, e.g.
   * as "\u00AB"
   */
  public static String encodeInAscii(String encodedString) {
    StringBuilder output = new StringBuilder(encodedString.length());
    char[] charArray = encodedString.toCharArray();

    for (int i = 0; i < charArray.length; i++) {
      char c = charArray[i];
      if ((int) c > 127) {
        encodedString = "000" + Integer.toHexString((int) c).toUpperCase();
        output.append("\\u").append(encodedString.substring(encodedString.length() - 4));
      } else {
        output.append(c);
      }
    }
    return output.toString();
  }
}
