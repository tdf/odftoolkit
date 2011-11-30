/************************************************************************
* 
*  Licensed to the Apache Software Foundation (ASF) under one
*  or more contributor license agreements.  See the NOTICE file
*  distributed with this work for additional information
*  regarding copyright ownership.  The ASF licenses this file
*  to you under the Apache License, Version 2.0 (the
*  "License"); you may not use this file except in compliance
*  with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*
************************************************************************/
package org.odftoolkit.odfdom.incubator.search;

/**
 * Thrown to indicate that the navigation operation can not be processed on
 * selections
 * 
 * @deprecated As of release 0.8.8, replaced by {@link org.odftoolkit.simple.common.navigation.InvalidNavigationException} in Simple API.
 */
public class InvalidNavigationException extends Exception {

	private static final long serialVersionUID = -6139894252732076102L;

	/**
	 * Constructs a <code>InvalidNavigateOperation</code> with the specified
	 * detail message.
	 * 
	 * @param msg
	 *            the detail message.
	 */
	public InvalidNavigationException(String msg) {
		super(msg);
	}
}
