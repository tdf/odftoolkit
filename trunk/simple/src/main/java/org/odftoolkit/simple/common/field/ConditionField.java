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

import org.odftoolkit.odfdom.dom.element.text.TextConditionalTextElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Component;

/**
 * ConditionField specifies a condition for display of one text string or
 * another. If the condition is true, one of the text strings is displayed. If
 * the condition is false, the other text string is displayed.
 * 
 * @since 0.5
 */
public class ConditionField extends Field {

	private TextConditionalTextElement conditionalTextElement;
	private final boolean isHiddenTextField;

	// package constructor, only called by Fields
	ConditionField(OdfElement odfElement, String condition, String trueText, String falseText, boolean isHiddenTextField) {
		if (odfElement instanceof TextPElement) {
			conditionalTextElement = ((TextPElement) odfElement).newTextConditionalTextElement("ooow:" + condition,
					falseText, trueText);
		} else if (odfElement instanceof TextSpanElement) {
			conditionalTextElement = ((TextSpanElement) odfElement).newTextConditionalTextElement("ooow:" + condition,
					falseText, trueText);
		} else {
			TextPElement pElement = ((OdfFileDom) odfElement.getOwnerDocument()).newOdfElement(TextPElement.class);
			odfElement.appendChild(pElement);
			conditionalTextElement = pElement.newTextConditionalTextElement("ooow:" + condition, falseText, trueText);
		}
		conditionalTextElement.setTextCurrentValueAttribute(true);
		this.isHiddenTextField = isHiddenTextField;
		Component.registerComponent(this, getOdfElement());
	}

	/**
	 * Update the condition of this field.
	 * 
	 * @param condition
	 *            the new condition of this condition field.
	 */
	public void updateCondition(String condition) {
		conditionalTextElement.setTextConditionAttribute("ooow:" + condition);
	}

	/**
	 * Update the true text of this condition field.
	 * 
	 * @param text
	 *            the new text content.
	 */
	public void updateTrueText(String text) {
		conditionalTextElement.setTextStringValueIfTrueAttribute(text);
	}

	/**
	 * Update the false text of this condition field.
	 * 
	 * @param text
	 *            the new text content.
	 */
	public void updateFalseText(String text) {
		conditionalTextElement.setTextStringValueIfFalseAttribute(text);
	}

	@Override
	public OdfElement getOdfElement() {
		return conditionalTextElement;
	}

	@Override
	public FieldType getFieldType() {
		return isHiddenTextField ? FieldType.HIDDEN_TEXT_FIELD : FieldType.CONDITION_FIELD;
	}

}
