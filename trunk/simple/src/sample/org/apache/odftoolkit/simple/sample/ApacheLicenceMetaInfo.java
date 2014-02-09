/**********************************************************************
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 **********************************************************************/
package org.apache.odftoolkit.simple.sample;

import java.io.File;
import java.io.FilenameFilter;

import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.meta.Meta;
import org.odftoolkit.odfdom.dom.attribute.meta.MetaValueTypeAttribute.Value;

/**
 * This is a sample which demos how to add Apache Licence info as user defined
 * mata to ODF documents automatically. This will be a useful function to have
 * in the future. As we add new unit tests and new test files, we will need to
 * add license info to those files. This could be done via code like this.
 * 
 * Maybe even a 2nd class that will test an ODF file to see if it has the
 * license in it, or one that will return the license. In the future, after we
 * release, we could share some code with the Apache RAT project, so they could
 * automate the testing of ODF files in projects, rather than treat them like
 * binaries, as they do today. This would help other projects that use ODF
 * documents, like the OpenOffice project.
 */
public class ApacheLicenceMetaInfo {
	public static void main(String[] args) {
		String patch = "C:\\resources";
		File directry = new File(patch);
		File[] files = directry.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File path, String name) {
				if (name.contains(".od") || name.contains(".ot")) {
					return true;
				} else {
					return false;
				}
			}
		});
		String license = "Licensed to the Apache Software Foundation (ASF) under one \n"
				+ "or more contributor license agreements.  See the NOTICE file \n"
				+ "distributed with this work for additional information \n"
				+ "regarding copyright ownership.  The ASF licenses this file \n"
				+ "to you under the Apache License, Version 2.0 (the \n"
				+ "\"License\"); you may not use this file except in compliance \n"
				+ "with the License.  You may obtain a copy of the License at \n"
				+ "\n"
				+ " http://www.apache.org/licenses/LICENSE-2.0 \n"
				+ "\n"
				+ "Unless required by applicable law or agreed to in writing, \n"
				+ "software distributed under the License is distributed on an \n"
				+ "\"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY \n"
				+ "KIND, either express or implied.  See the License for the \n"
				+ "specific language governing permissions and limitations \n"
				+ "under the License.";
		for (File file : files) {
			try {
				Document doc = Document.loadDocument(file);
				OdfFileDom metadom = doc.getMetaDom();
				Meta metadata = new Meta(metadom);
				String key = "License";
				metadata.removeUserDefinedDataByName(key);
				// org.odftoolkit.odfdom.dom.attribute.meta.MetaValueTypeAttribute.Value
				metadata.setUserDefinedData(key, Value.STRING.toString(),
						license);
				doc.save(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
