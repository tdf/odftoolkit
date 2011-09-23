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

package org.odftoolkit.simple.common.field;

import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.dom.element.text.TextSubjectElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Component;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.meta.Meta;

/**
 * A <tt>SubjectField</tt> displays the subject value contained by the document
 * meta data.
 * 
 * @since 0.5
 */
public class SubjectField extends Field {
	private TextSubjectElement subjectElement;

	// package constructor, only called by Fields
	SubjectField(OdfElement odfElement) {
		TextSpanElement spanElement = ((OdfFileDom) odfElement.getOwnerDocument()).newOdfElement(TextSpanElement.class);
		odfElement.appendChild(spanElement);
		subjectElement = spanElement.newTextSubjectElement();
		try {
			OdfFileDom dom = (OdfFileDom) odfElement.getOwnerDocument();
			Meta meta = ((Document) dom.getDocument()).getOfficeMetadata();
			subjectElement.setTextContent(meta.getSubject());
		} catch (Exception e) {
			// get meta info failed, do not set content value. Let editor update
			// it.
		}
		Component.registerComponent(this, getOdfElement());
	}

	/**
	 * Return an instance of <code>TextSubjectElement</code> which represents
	 * this feature.
	 * 
	 * @return an instance of <code>TextSubjectElement</code>
	 */
	public TextSubjectElement getOdfElement() {
		return subjectElement;
	}

	@Override
	public FieldType getFieldType() {
		return FieldType.SUBJECT_FIELD;
	}
}
