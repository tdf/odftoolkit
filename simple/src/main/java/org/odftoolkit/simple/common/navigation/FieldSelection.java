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

package org.odftoolkit.simple.common.navigation;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.text.TextSElement;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextSpan;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.common.TextExtractor;
import org.odftoolkit.simple.common.field.ConditionField;
import org.odftoolkit.simple.common.field.Field;
import org.odftoolkit.simple.common.field.Fields;
import org.odftoolkit.simple.common.field.ReferenceField;
import org.odftoolkit.simple.common.field.VariableField;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This is a decorator class of TextSelection, which help user replace a text
 * content with field.
 * 
 * @see org.odftoolkit.simple.common.field.Fields
 * @see org.odftoolkit.simple.common.field.AuthorField
 * @see org.odftoolkit.simple.common.field.ChapterField
 * @see org.odftoolkit.simple.common.field.ConditionField
 * @see org.odftoolkit.simple.common.field.DateField
 * @see org.odftoolkit.simple.common.field.PageCountField
 * @see org.odftoolkit.simple.common.field.PageNumberField
 * @see org.odftoolkit.simple.common.field.ReferenceField
 * @see org.odftoolkit.simple.common.field.SubjectField
 * @see org.odftoolkit.simple.common.field.TimeField
 * @see org.odftoolkit.simple.common.field.TitleField
 * @see org.odftoolkit.simple.common.field.VariableField
 * 
 * @since 0.5
 */
public class FieldSelection extends Selection {

	private TextSelection textSelection;
	private boolean mIsInserted;
	private OdfTextSpan spanContainer;

	/**
	 * Replace the content with a simple field, such as author field, page
	 * number field, date field and so on.
	 * 
	 * @param fieldType
	 *            the simple field type to replace.
	 */
	public Field replaceWithSimpleField(Field.FieldType fieldType) {
		int leftLength = textSelection.getText().length();
		int index = textSelection.getIndex();
		prepareSpanContainer(leftLength, index);
		Field field = null;
		switch (fieldType) {
		case DATE_FIELD:
			field = Fields.createDateField(spanContainer);
			break;
		case FIXED_DATE_FIELD:
			field = Fields.createFixedDateField(spanContainer);
			break;
		case TIME_FIELD:
			field = Fields.createTimeField(spanContainer);
			break;
		case FIXED_TIME_FIELD:
			field = Fields.createFixedTimeField(spanContainer);
			break;
		case PREVIOUS_PAGE_NUMBER_FIELD:
			field = Fields.createPreviousPageNumberField(spanContainer);
			break;
		case CURRENT_PAGE_NUMBER_FIELD:
			field = Fields.createCurrentPageNumberField(spanContainer);
			break;
		case NEXT_PAGE_NUMBER_FIELD:
			field = Fields.createNextPageNumberField(spanContainer);
			break;
		case PAGE_COUNT_FIELD:
			field = Fields.createPageCountField(spanContainer);
			break;
		case TITLE_FIELD:
			field = Fields.createTitleField(spanContainer);
			break;
		case SUBJECT_FIELD:
			field = Fields.createSubjectField(spanContainer);
			break;
		case AUTHOR_NAME_FIELD:
			field = Fields.createAuthorNameField(spanContainer);
			break;
		case AUTHOR_INITIALS_FIELD:
			field = Fields.createAuthorInitialsField(spanContainer);
			break;
		case CHAPTER_FIELD:
			field = Fields.createChapterField(spanContainer);
			break;
		case REFERENCE_FIELD:
		case SIMPLE_VARIABLE_FIELD:
		case USER_VARIABLE_FIELD:
		case CONDITION_FIELD:
		case HIDDEN_TEXT_FIELD:
			throw new IllegalArgumentException("this is not a vaild simple field type.");
		}
		textSelection.mMatchedText = field.getOdfElement().getTextContent();
		int textLength = textSelection.mMatchedText.length();
		int offset = textLength - leftLength;
		SelectionManager.refresh(textSelection.getContainerElement(), offset, index + textLength);
		return field;
	}

	/**
	 * Replace the content with a reference field.
	 * 
	 * @param field
	 *            the reference field to replace.
	 */
	public void replaceWithReferenceField(ReferenceField field, ReferenceField.DisplayType type) {
		int leftLength = textSelection.getText().length();
		int index = textSelection.getIndex();
		prepareSpanContainer(leftLength, index);
		field.appendReferenceTo(spanContainer, type);
		textSelection.mMatchedText = field.getOdfElement().getTextContent();
		int textLength = textSelection.mMatchedText.length();
		int offset = textLength - leftLength;
		SelectionManager.refresh(textSelection.getContainerElement(), offset, index + textLength);
	}

	/**
	 * Replace the content with a variable field.
	 * 
	 * @param field
	 *            the variable field to replace.
	 */
	public void replaceWithVariableField(VariableField field) {
		int leftLength = textSelection.getText().length();
		int index = textSelection.getIndex();
		prepareSpanContainer(leftLength, index);
		field.displayField(spanContainer);
		textSelection.mMatchedText = field.getOdfElement().getTextContent();
		int textLength = textSelection.mMatchedText.length();
		int offset = textLength - leftLength;
		SelectionManager.refresh(textSelection.getContainerElement(), offset, index + textLength);
	}

	/**
	 * Replace the content with a condition field.
	 * 
	 * @param condition
	 *            the condition that determines which of the two text strings is
	 *            displayed.
	 * @param trueText
	 *            the text string to display if a <code>condition</code> is
	 *            true.
	 * @param falseText
	 *            the text string to display if a <code>condition</code> is
	 *            false.
	 * @return the created condition field.
	 */
	public ConditionField replaceWithConditionField(String condition, String trueText, String falseText) {
		int leftLength = textSelection.getText().length();
		int index = textSelection.getIndex();
		prepareSpanContainer(leftLength, index);
		textSelection.mMatchedText = "";
		int offset = -leftLength;
		SelectionManager.refresh(textSelection.getContainerElement(), offset, index);
		return Fields.createConditionField(spanContainer, condition, trueText, falseText);
	}

	/**
	 * Replace the content with a hidden text field.
	 * 
	 * @param condition
	 *            the condition that determines whether the text string is
	 *            displayed or not.
	 * @param text
	 *            the text string to display.
	 * @return the created condition field.
	 */
	public ConditionField replaceWithHiddenTextField(String condition, String text) {
		int leftLength = textSelection.getText().length();
		int index = textSelection.getIndex();
		prepareSpanContainer(leftLength, index);
		textSelection.mMatchedText = "";
		int offset = -leftLength;
		SelectionManager.refresh(textSelection.getContainerElement(), offset, index);
		return Fields.createHiddenTextField(spanContainer, condition, text);
	}

	/**
	 * Apply a style to the selection so that the text style of this selection
	 * will append the specified style.
	 * 
	 * @param style
	 *            the style can be from the current document or user defined
	 * @throws InvalidNavigationException
	 *             if the selection is unavailable.
	 */
	public void applyStyle(OdfStyleBase style) throws InvalidNavigationException {
		if (spanContainer == null) {
			textSelection.applyStyle(style);
		} else {
			spanContainer.setProperties(style.getStyleProperties());
		}
	}

	/**
	 * Construct a FieldSelection with TextSelection. Then user can replace text
	 * content with fields.
	 * 
	 * @param selection
	 *            the TextSelection to be decorated.
	 */
	public FieldSelection(TextSelection selection) {
		textSelection = selection;
		spanContainer = null;
	}

	/**
	 * Delete the selection from the document the other matched selection in the
	 * same container element will be updated automatically because the start
	 * index of the following selections will be changed when the previous
	 * selection has been deleted.
	 * 
	 * @throws InvalidNavigationException
	 *             if the selection is unavailable.
	 */
	@Override
	public void cut() throws InvalidNavigationException {
		textSelection.cut();
	}

	/**
	 * Replace the text content of selection with a new string.
	 * 
	 * @param newText
	 *            the replace text String
	 * @throws InvalidNavigationException
	 *             if the selection is unavailable.
	 */
	public void replaceWith(String newText) throws InvalidNavigationException {
		textSelection.replaceWith(newText);
	}

	/**
	 * Paste this selection just after a specific selection.
	 * 
	 * @param positionItem
	 *            a selection that is used to point out the position
	 * @throws InvalidNavigationException
	 *             if the selection is unavailable.
	 */
	@Override
	public void pasteAtEndOf(Selection positionItem) throws InvalidNavigationException {
		textSelection.pasteAtEndOf(positionItem);
	}

	/**
	 * Paste this selection just before a specific selection.
	 * 
	 * @param positionItem
	 *            a selection that is used to point out the position
	 * @throws InvalidNavigationException
	 *             if the selection is unavailable.
	 */
	@Override
	public void pasteAtFrontOf(Selection positionItem) throws InvalidNavigationException {
		textSelection.pasteAtFrontOf(positionItem);
	}

	protected void refresh(int offset) {
		textSelection.refresh(offset);
	}

	protected void refreshAfterFrontalDelete(Selection deletedItem) {
		textSelection.refreshAfterFrontalDelete(deletedItem);
	}

	protected void refreshAfterFrontalInsert(Selection insertedItem) {
		textSelection.refreshAfterFrontalInsert(insertedItem);
	}

	/*
	 * Initialize or clear the <code>OdfTextSpan<code> element which contains
	 * replaced field.
	 */
	private void prepareSpanContainer(int leftLength, int index) {
		if (spanContainer == null) {
			OdfElement parentElement = textSelection.getContainerElement();
			delete(index, leftLength, parentElement);
			spanContainer = new OdfTextSpan((OdfFileDom) parentElement.getOwnerDocument());
			mIsInserted = false;
			insertSpan(spanContainer, index, parentElement);
		} else {
			Node childNode = spanContainer.getFirstChild();
			while (childNode != null) {
				spanContainer.removeChild(childNode);
				childNode = spanContainer.getFirstChild();
			}
		}
	}

	/*
	 * Delete the <code>pNode<code> from the <code>fromIndex</code> text, and
	 * delete <code>leftLength</code> text.
	 */
	private void delete(int fromIndex, int leftLength, Node pNode) {
		if ((fromIndex == 0) && (leftLength == 0)) {
			return;
		}
		int nodeLength = 0;
		Node node = pNode.getFirstChild();
		while (node != null) {
			if ((fromIndex == 0) && (leftLength == 0)) {
				return;
			}
			if (node.getNodeType() == Node.TEXT_NODE) {
				nodeLength = node.getNodeValue().length();
			} else if (node.getNodeType() == Node.ELEMENT_NODE) {
				// text:s
				if (node.getLocalName().equals("s")) {
					try {
						nodeLength = Integer.parseInt(((Element) node).getAttributeNS(OdfDocumentNamespace.TEXT
								.getUri(), "c"));
					} catch (Exception e) {
						nodeLength = 1;
					}
				} else if (node.getLocalName().equals("line-break")) {
					nodeLength = 1;
				} else if (node.getLocalName().equals("tab")) {
					nodeLength = 1;
				} else {
					nodeLength = TextExtractor.getText((OdfElement) node).length();
				}
			}
			if (nodeLength <= fromIndex) {
				fromIndex -= nodeLength;
			} else {
				// the start index is in this node
				if (node.getNodeType() == Node.TEXT_NODE) {
					String value = node.getNodeValue();
					StringBuffer buffer = new StringBuffer();
					buffer.append(value.substring(0, fromIndex));
					int endLength = fromIndex + leftLength;
					int nextLength = value.length() - endLength;
					fromIndex = 0;
					if (nextLength >= 0) {
						// delete the result
						buffer.append(value.substring(endLength, value.length()));
						leftLength = 0;
					} else {
						leftLength = endLength - value.length();
					}
					node.setNodeValue(buffer.toString());

				} else if (node.getNodeType() == Node.ELEMENT_NODE) {
					// if text:s?????????
					// text:s
					if (node.getLocalName().equals("s")) {
						// delete space
						((TextSElement) node).setTextCAttribute(new Integer(nodeLength - fromIndex));
						leftLength = leftLength - (nodeLength - fromIndex);
						fromIndex = 0;
					} else if (node.getLocalName().equals("line-break") || node.getLocalName().equals("tab")) {
						fromIndex = 0;
						leftLength--;
					} else {
						delete(fromIndex, leftLength, node);
						int length = (fromIndex + leftLength) - nodeLength;
						leftLength = length > 0 ? length : 0;
						fromIndex = 0;
					}
				}
			}
			node = node.getNextSibling();
		}
	}

	/*
	 * Insert <code>textSpan</code> into the from index of <code>pNode<code>.
	 */
	private void insertSpan(OdfTextSpan textSpan, int fromIndex, Node pNode) {
		if (fromIndex < 0) {
			fromIndex = 0;
		}
		if (fromIndex == 0 && mIsInserted) {
			return;
		}
		int nodeLength = 0;
		Node node = pNode.getFirstChild();
		while (node != null) {
			if (fromIndex <= 0 && mIsInserted) {
				return;
			}
			if (node.getNodeType() == Node.TEXT_NODE) {
				nodeLength = node.getNodeValue().length();
				if ((fromIndex != 0) && (nodeLength < fromIndex)) {
					fromIndex -= nodeLength;
				} else {
					// insert result after node, and insert an new text node
					// after the result node
					String value = node.getNodeValue();
					StringBuffer buffer = new StringBuffer();
					buffer.append(value.substring(0, fromIndex));
					// insert the text span in appropriate position
					node.setNodeValue(buffer.toString());
					Node nextNode = node.getNextSibling();
					Node parNode = node.getParentNode();
					Node newNode = node.cloneNode(true);
					newNode.setNodeValue(value.substring(fromIndex, value.length()));
					if (nextNode != null) {
						parNode.insertBefore(textSpan, nextNode);
						parNode.insertBefore(newNode, nextNode);
					} else {
						parNode.appendChild(textSpan);
						parNode.appendChild(newNode);
					}
					mIsInserted = true;
					return;
				}
			} else if (node.getNodeType() == Node.ELEMENT_NODE) {
				// text:s
				if (node.getLocalName().equals("s")) {
					try {
						nodeLength = Integer.parseInt(((Element) node).getAttributeNS(OdfDocumentNamespace.TEXT
								.getUri(), "c"));
					} catch (Exception e) {
						nodeLength = 1;
					}
					fromIndex -= nodeLength;
				} else if (node.getLocalName().equals("line-break")) {
					nodeLength = 1;
					fromIndex--;
				} else if (node.getLocalName().equals("tab")) {
					nodeLength = 1;
					fromIndex--;
				} else {
					nodeLength = TextExtractor.getText((OdfElement) node).length();
					insertSpan(textSpan, fromIndex, node);
					fromIndex -= nodeLength;
				}
			}
			node = node.getNextSibling();
		}
	}
}
