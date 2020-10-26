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
package org.odftoolkit.odfvalidator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;
import java.util.Vector;

public class Configuration extends Properties {

  public static final String SCHEMA = "schema";
  public static final String STRICT_SCHEMA = "strict-schema";
  public static final String MANIFEST_SCHEMA = "manifest-schema";
  public static final String MATHML1_01_SCHEMA = "mathml1.01-schema";
  public static final String MATHML3_SCHEMA = "mathml3-schema";
  public static final String DSIG_SCHEMA = "dsig-schema";
  public static final String PATH = "path";
  public static final String EXCLUDE = "exclude";
  public static final String RECURSIVE = "recursive";
  public static final String FILTER = "filter";

  public static final String VALIDATOR_URL = "validator-url";
  public static final String PROXY_HOST = "proxy-host";
  public static final String PROXY_PORT = "proxy-port";

  /** Creates a new instance of AppProperties */
  public Configuration() {}

  public Configuration(File aConfigFile) throws FileNotFoundException, IOException {
    FileInputStream aInStream = new FileInputStream(aConfigFile);
    loadFromXML(aInStream);
    aInStream.close();
  }

  public void store(File aConfigFile) throws FileNotFoundException, IOException {
    FileOutputStream aOutStream = new FileOutputStream(aConfigFile);
    storeToXML(aOutStream, null);
    aOutStream.close();
  }

  public List<String> getListPropety(String aPropNamePrefix) {
    TreeSet<String> aSortedPropNames = new TreeSet<String>();
    Enumeration aPropNames = propertyNames();
    while (aPropNames.hasMoreElements()) {
      String aPropName = (String) aPropNames.nextElement();
      if (aPropName.startsWith(aPropNamePrefix)) aSortedPropNames.add(aPropName);
    }

    List<String> aValues = new Vector<String>(aSortedPropNames.size());
    Iterator<String> aIter = aSortedPropNames.iterator();
    while (aIter.hasNext()) aValues.add(getProperty(aIter.next()));

    return aValues;
  }
}
