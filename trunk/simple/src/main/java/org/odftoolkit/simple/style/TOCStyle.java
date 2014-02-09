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

package org.odftoolkit.simple.style;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class represents the additional paragraph styles list that you want to
 * include in the TOC.
 * 
 * @since 0.8.6
 */
public class TOCStyle {
	HashMap<Integer, String> stylelist=new HashMap<Integer, String>();
	
	/**
	 * Create an instance of TOCStyle
	 * @param doc 
	 */
	public TOCStyle() {
		
	}

	/**
	 * Add an additional paragraph style to the style list that you want to
	 * include in the TOC.
	 * 
	 * @param styleName
	 *            - the style name
	 * @param level
	 *            - the outline level of this style, level value is: [1,10]
	 */
	public void addStyle(String styleName, int level) {
		if ((level >= 1) && (level <= 10)) {
			
			stylelist.put(level, styleName);
		
		} else {
			Logger.getLogger(TOCStyle.class.getName()).log(
					Level.SEVERE,
					"Outline level:" + level + " is out of range[1,10] "
							+ "TOCStyle.addStyle failed.");
			throw new RuntimeException("Outline level:" + level
					+ " is out of range[1,10] " + "TOCStyle.addStyle failed.");
		}
	}
	public HashMap<Integer, String> getStyle(){
		return stylelist;
	}
	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		int size=stylelist.size();
		strBuilder.append("The additional paragraph styles list that you want to include in the TOC.\n");
		for(int i=0;i<size;i++){
			strBuilder.append("Outline Level:"+ (i+1));	
			String styleName=stylelist.get(i+1);
			strBuilder.append(" Style Name:"+styleName);
			strBuilder.append("\n");
		}
		return strBuilder.toString();
	}

}
