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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfDefaultStyle;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.table.CellStyleHandler;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This class is the default style handler. It provides methods to get the
 * readable style element and the writable style element. It also provides
 * method to get all kinds of style properties elements.
 * 
 * @since 0.5
 */
public class DefaultStyleHandler {
	/**
	 * The style element that will be processed.
	 */
	protected OdfStyleBase mStyleElement;
	/**
	 * The writable style element that will be processed.
	 */
	protected OdfStyle mWritableStyleElement;
	/**
	 * Whether the default style is used.
	 */
	protected boolean isUseDefaultStyle = false;

	/**
	 * The odf element
	 */
	protected OdfStylableElement mOdfElement;
	/**
	 * The document
	 */
	protected Document mDocument;

	/**
	 * The readable text properties element.
	 */
	protected TextProperties mTextProperties;

	/**
	 * The writable text properties element.
	 */
	protected TextProperties mWritableTextProperties;

	/**
	 * The readable table cell properties element.
	 */
	protected TableCellProperties mTableCellProperties;

	/**
	 * The writable table cell properties element.
	 */
	protected TableCellProperties mWritableTableCellProperties;

	/**
	 * The readable paragraph properties element.
	 */
	protected ParagraphProperties mParagraphProperties;

	/**
	 * The writable paragraph properties element.
	 */
	protected ParagraphProperties mWritableParagraphProperties;

	/**
	 * The readable graphics properties element.
	 */
	protected GraphicProperties mGraphicProperties;

	/**
	 * The writable graphics properties element.
	 */
	protected GraphicProperties mWritableGraphicProperties;
	/**
	 * The readable table properties element.
	 */
	protected TableProperties mTableProperties;

	/**
	 * The writable table properties element.
	 */
	protected TableProperties mWritableTableProperties;
	/**
	 * Constructor of DefaultStyleHandler
	 * 
	 * @param element
	 *            - the instance of structure component in an ODF document
	 * 
	 */
	public DefaultStyleHandler(OdfStylableElement element) {
		mOdfElement = element;
		mDocument = ((Document) ((OdfFileDom) mOdfElement.getOwnerDocument()).getDocument());
	}

	/**
	 * This HashMap contains the relationship between OdfStyleFamily and
	 * OdfStylePropertiesSet.
	 */
	protected static HashMap<OdfStyleFamily, EnumSet<OdfStylePropertiesSet>> mFamilyProperties = new HashMap<OdfStyleFamily, EnumSet<OdfStylePropertiesSet>>();

	{
		mFamilyProperties.put(OdfStyleFamily.Text, EnumSet.of(OdfStylePropertiesSet.TextProperties));
		mFamilyProperties.put(OdfStyleFamily.Paragraph, EnumSet.of(OdfStylePropertiesSet.TextProperties,
				OdfStylePropertiesSet.ParagraphProperties));
		mFamilyProperties.put(OdfStyleFamily.TableCell, EnumSet.of(OdfStylePropertiesSet.TextProperties,
				OdfStylePropertiesSet.ParagraphProperties, OdfStylePropertiesSet.TableCellProperties));
		mFamilyProperties.put(OdfStyleFamily.Graphic, EnumSet.of(OdfStylePropertiesSet.TextProperties,
				OdfStylePropertiesSet.ParagraphProperties, OdfStylePropertiesSet.GraphicProperties));
		mFamilyProperties.put(OdfStyleFamily.Presentation, EnumSet.of(OdfStylePropertiesSet.TextProperties,
				OdfStylePropertiesSet.ParagraphProperties, OdfStylePropertiesSet.GraphicProperties));
		mFamilyProperties.put(OdfStyleFamily.Section, EnumSet.of(OdfStylePropertiesSet.SectionProperties));
		mFamilyProperties.put(OdfStyleFamily.Ruby, EnumSet.of(OdfStylePropertiesSet.RubyProperties));
		mFamilyProperties.put(OdfStyleFamily.Table, EnumSet.of(OdfStylePropertiesSet.TableProperties));
		mFamilyProperties.put(OdfStyleFamily.TableRow, EnumSet.of(OdfStylePropertiesSet.TableRowProperties));
		mFamilyProperties.put(OdfStyleFamily.TableColumn, EnumSet.of(OdfStylePropertiesSet.TableColumnProperties));
		mFamilyProperties.put(OdfStyleFamily.DrawingPage, EnumSet.of(OdfStylePropertiesSet.DrawingPageProperties));
		mFamilyProperties.put(OdfStyleFamily.Chart, EnumSet.of(OdfStylePropertiesSet.TextProperties,
				OdfStylePropertiesSet.ParagraphProperties, OdfStylePropertiesSet.GraphicProperties,
				OdfStylePropertiesSet.ChartProperties));
	}

	/**
	 * Return the text style properties definition for this component, only for
	 * read function.
	 * <p>
	 * Null will be returned if there is no style definition.
	 * <p>
	 * Null will be returned if there is no explicit text style properties
	 * definition for this component.
	 * <p>
	 * Note if you try to write style properties to the returned object, errors
	 * will be met with.
	 * 
	 * @return the text style properties definition for this component, only for
	 *         read function
	 */
	public TextProperties getTextPropertiesForRead() {
		if (!mFamilyProperties.get(mOdfElement.getStyleFamily()).contains(OdfStylePropertiesSet.TextProperties)) {
			Logger.getLogger(CellStyleHandler.class.getName()).log(Level.FINE,
					"Text properties are not supported by style family: " + mOdfElement.getStyleFamily() + "!", "");
			return null;
		}

		if (mWritableTextProperties != null)
			return mWritableTextProperties;
		else if (mTextProperties != null)
			return mTextProperties;

		OdfStyleBase style = getStyleElementForRead();
		if (style == null) {
			Logger.getLogger(CellStyleHandler.class.getName()).log(Level.FINE, "No style definition is found!", "");
			return null;
		}
		mTextProperties = TextProperties.getTextProperties(style);
		if (mTextProperties != null)
			return mTextProperties;
		else {
			Logger.getLogger(CellStyleHandler.class.getName()).log(Level.FINE,
					"No explicit text properties definition is found!", "");
			return null;
		}
	}

	/**
	 * Return the text style properties definition for this component, for read
	 * and write function.
	 * <p>
	 * An empty style definition will be created if there is no style
	 * definition.
	 * <p>
	 * An empty text style properties definition will be created if there is no
	 * explicit text style properties definition.
	 * 
	 * @return the text style properties definition for this component, for read
	 *         and write function
	 */
	public TextProperties getTextPropertiesForWrite() {
		if (!mFamilyProperties.get(mOdfElement.getStyleFamily()).contains(OdfStylePropertiesSet.TextProperties)) {
			Logger.getLogger(CellStyleHandler.class.getName()).log(Level.FINE,
					"Text properties are not supported by style family: " + mOdfElement.getStyleFamily() + "!", "");
			return null;
		}

		if (mWritableTextProperties != null)
			return mWritableTextProperties;
		OdfStyle style = getStyleElementForWrite();
		mWritableTextProperties = TextProperties.getOrCreateTextProperties(style);
		return mWritableTextProperties;
	}

	/**
	 * Return the cell style properties definition for this component, only for
	 * read function.
	 * <p>
	 * Null will be returned if there is no style definition.
	 * <p>
	 * Null will be returned if there is no explicit cell style properties
	 * definition for this component.
	 * <p>
	 * Note if you try to write style properties to the returned object, errors
	 * will be met with.
	 * 
	 * @return the cell style properties definition for this component, only for
	 *         read function
	 */
	public TableCellProperties getTableCellPropertiesForRead() {
		if (!mFamilyProperties.get(mOdfElement.getStyleFamily()).contains(OdfStylePropertiesSet.TableCellProperties)) {
			Logger.getLogger(CellStyleHandler.class.getName())
					.log(
							Level.FINE,
							"TableCell properties are not supported by style family: " + mOdfElement.getStyleFamily()
									+ "!", "");
			return null;
		}

		if (mWritableTableCellProperties != null)
			return mWritableTableCellProperties;
		else if (mTableCellProperties != null)
			return mTableCellProperties;

		OdfStyleBase style = getStyleElementForRead();
		if (style == null) {
			Logger.getLogger(CellStyleHandler.class.getName()).log(Level.FINE, "No style definition is found!", "");
			return null;
		}
		mTableCellProperties = TableCellProperties.getTableCellProperties(style);
		if (mTableCellProperties != null)
			return mTableCellProperties;
		else {
			Logger.getLogger(CellStyleHandler.class.getName()).log(Level.FINE,
					"No explicit table cell properties definition is found!", "");
			return null;
		}
	}

	/**
	 * Return the cell style properties definition for this component, for read
	 * and write function.
	 * <p>
	 * An empty style definition will be created if there is no style
	 * definition.
	 * <p>
	 * An empty cell style properties definition will be created if there is no
	 * explicit cell style properties definition.
	 * 
	 * @return the cell style properties definition for this component, for read
	 *         and write function
	 */
	public TableCellProperties getTableCellPropertiesForWrite() {
		if (!mFamilyProperties.get(mOdfElement.getStyleFamily()).contains(OdfStylePropertiesSet.TableCellProperties)) {
			Logger.getLogger(CellStyleHandler.class.getName())
					.log(
							Level.FINE,
							"TableCell properties are not supported by style family: " + mOdfElement.getStyleFamily()
									+ "!", "");
			return null;
		}

		if (mWritableTableCellProperties != null)
			return mWritableTableCellProperties;
		OdfStyle style = getStyleElementForWrite();
		mWritableTableCellProperties = TableCellProperties.getOrCreateTableCellProperties(style);
		return mWritableTableCellProperties;
	}

	/**
	 * Return the paragraph style properties definition for this component, only
	 * for read function.
	 * <p>
	 * Null will be returned if there is no style definition.
	 * <p>
	 * Null will be returned if there is no explicit paragraph style properties
	 * definition for this component.
	 * <p>
	 * Note if you try to write style properties to the returned object, errors
	 * will be met with.
	 * 
	 * @return the paragraph style properties definition for this component,
	 *         only for read function
	 */
	public ParagraphProperties getParagraphPropertiesForRead() {
		if (!mFamilyProperties.get(mOdfElement.getStyleFamily()).contains(OdfStylePropertiesSet.ParagraphProperties)) {
			Logger.getLogger(CellStyleHandler.class.getName())
					.log(
							Level.FINE,
							"Paragraph properties are not supported by style family: " + mOdfElement.getStyleFamily()
									+ "!", "");
			return null;
		}

		if (mWritableParagraphProperties != null)
			return mWritableParagraphProperties;
		else if (mParagraphProperties != null)
			return mParagraphProperties;

		OdfStyleBase style = getStyleElementForRead();
		if (style == null) {
			Logger.getLogger(CellStyleHandler.class.getName()).log(Level.FINE, "No style definition is found!", "");
			return null;
		}
		mParagraphProperties = ParagraphProperties.getParagraphProperties(style);
		if (mParagraphProperties != null)
			return mParagraphProperties;
		else {
			Logger.getLogger(CellStyleHandler.class.getName()).log(Level.FINE,
					"No explicit paragraph properties definition is found!", "");
			return null;
		}
	}

	/**
	 * Return the paragraph style properties definition for this component, for
	 * read and write function.
	 * <p>
	 * An empty style definition will be created if there is no style
	 * definition.
	 * <p>
	 * An empty paragraph style properties definition will be created if there
	 * is no explicit paragraph style properties definition.
	 * 
	 * @return the paragraph style properties definition for this component, for
	 *         read and write function
	 */
	public ParagraphProperties getParagraphPropertiesForWrite() {
		if (!mFamilyProperties.get(mOdfElement.getStyleFamily()).contains(OdfStylePropertiesSet.ParagraphProperties)) {
			Logger.getLogger(CellStyleHandler.class.getName())
					.log(
							Level.FINE,
							"Paragraph properties are not supported by style family: " + mOdfElement.getStyleFamily()
									+ "!", "");
			return null;
		}

		if (mWritableParagraphProperties != null)
			return mWritableParagraphProperties;
		OdfStyle style = getStyleElementForWrite();
		mWritableParagraphProperties = ParagraphProperties.getOrCreateParagraphProperties(style);
		return mWritableParagraphProperties;
	}

	/**
	 * Return the graphic style properties definition for this component, only
	 * for read function.
	 * <p>
	 * Null will be returned if there is no style definition.
	 * <p>
	 * Null will be returned if there is no explicit graphic style properties
	 * definition for this component.
	 * <p>
	 * Note if you try to write style properties to the returned object, errors
	 * will be met with.
	 * 
	 * @return the graphic style properties definition for this component, only
	 *         for read function
	 */
	public GraphicProperties getGraphicPropertiesForRead() {
		if (!mFamilyProperties.get(mOdfElement.getStyleFamily()).contains(OdfStylePropertiesSet.GraphicProperties)) {
			Logger.getLogger(CellStyleHandler.class.getName()).log(Level.FINE,
					"Graphics properties are not supported by style family: " + mOdfElement.getStyleFamily() + "!", "");
			return null;
		}

		if (mWritableGraphicProperties != null)
			return mWritableGraphicProperties;
		else if (mGraphicProperties != null)
			return mGraphicProperties;

		OdfStyleBase style = getStyleElementForRead();
		if (style == null) {
			Logger.getLogger(CellStyleHandler.class.getName()).log(Level.FINE, "No style definition is found!", "");
			return null;
		}
		mGraphicProperties = GraphicProperties.getGraphicProperties(style);
		if (mGraphicProperties != null)
			return mGraphicProperties;
		else {
			Logger.getLogger(CellStyleHandler.class.getName()).log(Level.FINE,
					"No explicit graphic properties definition is found!", "");
			return null;
		}
	}

	/**
	 * Return the text style properties definition for this component, for read
	 * and write function.
	 * <p>
	 * An empty style definition will be created if there is no style
	 * definition.
	 * <p>
	 * An empty text style properties definition will be created if there is no
	 * explicit text style properties definition.
	 * 
	 * @return the text style properties definition for this component, for read
	 *         and write function
	 */
	public GraphicProperties getGraphicPropertiesForWrite() {
		if (!mFamilyProperties.get(mOdfElement.getStyleFamily()).contains(OdfStylePropertiesSet.GraphicProperties)) {
			Logger.getLogger(CellStyleHandler.class.getName()).log(Level.FINE,
					"Graphics properties are not supported by style family: " + mOdfElement.getStyleFamily() + "!", "");
			return null;
		}

		if (mWritableGraphicProperties != null)
			return mWritableGraphicProperties;
		OdfStyle style = getStyleElementForWrite();
		mWritableGraphicProperties = GraphicProperties.getOrCreateGraphicProperties(style);
		return mWritableGraphicProperties;
	}

	/**
	 * Return the table style properties definition for this component, only for
	 * read function.
	 * <p>
	 * Null will be returned if there is no style definition.
	 * Note if you try to write style properties to the returned object, errors
	 * will be met with.
	 * 
	 * @return the table style properties definition for this component, only
	 *         for read function
	 */
	public TableProperties getTablePropertiesForRead() {
		if (!mFamilyProperties.get(mOdfElement.getStyleFamily()).contains(
				OdfStylePropertiesSet.TableProperties)) {
			Logger.getLogger(DefaultStyleHandler.class.getName()).log(
					Level.FINE,
					"Table properties are not supported by style family: "
							+ mOdfElement.getStyleFamily() + "!", "");
			return null;
		}

		if (mWritableTableProperties != null)
			return mWritableTableProperties;
		else if (mTableProperties != null)
			return mTableProperties;

		OdfStyleBase style = getStyleElementForRead();
		if (style == null) {
			Logger.getLogger(DefaultStyleHandler.class.getName()).log(
					Level.FINE, "No style definition is found!", "");
			return null;
		}
		mTableProperties = TableProperties.getTableProperties(style);
		if (mTableProperties != null)
			return mTableProperties;
		else {
			Logger.getLogger(DefaultStyleHandler.class.getName()).log(
					Level.FINE,
					"No explicit table properties definition is found!", "");
			return null;
		}
	}

	/**
	 * Return the table style properties definition for this component, for read
	 * and write function.
	 * <p>
	 * An empty style definition will be created if there is no style
	 * An empty table style properties definition will be created if there is no
	 * explicit table style properties definition.
	 * 
	 * @return the table style properties definition for this component, for
	 *         read and write function
	 */
	public TableProperties getTablePropertiesForWrite() {
		if (!mFamilyProperties.get(mOdfElement.getStyleFamily()).contains(
				OdfStylePropertiesSet.TableProperties)) {
			Logger.getLogger(DefaultStyleHandler.class.getName()).log(
					Level.FINE,
					"Table properties are not supported by style family: "
							+ mOdfElement.getStyleFamily() + "!", "");
			return null;
		}

		if (mWritableTableProperties != null)
			return mWritableTableProperties;
		OdfStyle style = getStyleElementForWrite();
		mWritableTableProperties = TableProperties
				.getOrCreateTableProperties(style);
		return mWritableTableProperties;
	}

	/**
	 * Return the used style name of this component.
	 * <p>
	 * This method can be override by sub classes.
	 * <p>
	 * Please note the return of this method might not be same with the
	 * getStyleName() of component. If the style name is shared by multiple
	 * elements, getStyleElementForWrite() will return a copied style element,
	 * which is not same with the return of this method.
	 * 
	 * @return - the used style name of this component
	 */
	protected String getUsedStyleName() {
		return mOdfElement.getStyleName();
	}

	/**
	 * Return a readable style element by style name.
	 * <p>
	 * If the style name is null, the default style will be returned.
	 * 
	 * @param styleName
	 *            - the style name
	 * @return a readable style element
	 */
	protected OdfStyleBase getReadableStyleElementByName(String styleName) {
		OdfDefaultStyle defaultStyleElement = null;
		if (styleName == null || (styleName.equals(""))) {
			// get from default style element
			defaultStyleElement = mDocument.getDocumentStyles().getDefaultStyle(mOdfElement.getStyleFamily());
			isUseDefaultStyle = true;
			return defaultStyleElement;
		}

		OdfStyle styleElement = mOdfElement.getAutomaticStyles().getStyle(styleName, mOdfElement.getStyleFamily());

		if (styleElement == null) {
			styleElement = mDocument.getDocumentStyles().getStyle(styleName, mOdfElement.getStyleFamily());
		}

		if (styleElement == null) {
			styleElement = mOdfElement.getDocumentStyle();
		}

		if (styleElement == null) {
			return null;
		}

		return styleElement;
	}

	/**
	 * Return a writable style element by style name.
	 * <p>
	 * If the style is shared, a copied style element would be returned.
	 * <p>
	 * If the style name is null, the default style will be copied.
	 * 
	 * @param styleName
	 *            - the style name
	 * @return a writable style element
	 */
	protected OdfStyle getWritableStyleElementByName(String styleName, boolean isShared) {
		boolean createNew = isShared;
		OdfStyle styleElement = null;
		OdfDefaultStyle defaultStyleElement = null;
		if (styleName == null || (styleName.equals(""))) {
			createNew = true;
			// get from default style element
			defaultStyleElement = mDocument.getDocumentStyles().getDefaultStyle(mOdfElement.getStyleFamily());
		} else {
			OdfOfficeAutomaticStyles styles = mOdfElement.getAutomaticStyles();
			styleElement = styles.getStyle(styleName, mOdfElement.getStyleFamily());

			// If not default cell style definition,
			// Try to find if the style is defined in document styles
			if (styleElement == null && defaultStyleElement == null) {
				styleElement = mDocument.getDocumentStyles().getStyle(styleName, mOdfElement.getStyleFamily());
			}

			if (styleElement == null && defaultStyleElement == null) {
				styleElement = mOdfElement.getDocumentStyle();
			}
			if (styleElement == null || styleElement.getStyleUserCount() > 1) {
				createNew = true;
			}
		}
		// if style name is null or this style are used by many users,
		// should create a new one.
		if (createNew) {
			OdfStyle newStyle = mOdfElement.getAutomaticStyles().newStyle(mOdfElement.getStyleFamily());
			if (styleElement != null) {
				newStyle.setProperties(styleElement.getStylePropertiesDeep());
				// copy attributes
				NamedNodeMap attributes = styleElement.getAttributes();
				for (int i = 0; i < attributes.getLength(); i++) {
					Node attr = attributes.item(i);
					if (!attr.getNodeName().equals("style:name")) {
						newStyle.setAttributeNS(attr.getNamespaceURI(), attr.getNodeName(), attr.getNodeValue());
					}
				}// end of copying attributes
			} else if (defaultStyleElement != null) {
				newStyle.setProperties(defaultStyleElement.getStylePropertiesDeep());
				// copy attributes
				NamedNodeMap attributes = defaultStyleElement.getAttributes();
				for (int i = 0; i < attributes.getLength(); i++) {
					Node attr = attributes.item(i);
					if (!attr.getNodeName().equals("style:name")) {
						newStyle.setAttributeNS(attr.getNamespaceURI(), attr.getNodeName(), attr.getNodeValue());
					}
				}// end of copying attributes
				isUseDefaultStyle = true;
			}
			// mCellElement.getAutomaticStyles().appendChild(newStyle);
			String newname = newStyle.getStyleNameAttribute();
			mOdfElement.setStyleName(newname);
			return newStyle;
		}
		return styleElement;
	}

	/**
	 * Return the style element for this component, only for read function. This
	 * method will invode <code>getusedStyleName</code> to get the style name,
	 * and then find the readable style element by name.
	 * <p>
	 * Null will be returned if there is no style definition.
	 * <p>
	 * Note if you try to write style properties to the returned object, errors
	 * will be met with.
	 * 
	 * @return the style element
	 * @see #getUsedStyleName()
	 */
	public OdfStyleBase getStyleElementForRead() {
		// Return current used style
		if (getCurrentUsedStyle() != null)
			return getCurrentUsedStyle();

		String styleName = getUsedStyleName();
		mStyleElement = getReadableStyleElementByName(styleName);
		return mStyleElement;
	}

	/**
	 * Return the style element for this component, for read and write
	 * functions. This method will invode <code>getusedStyleName</code> to get
	 * the style name, and then find the writable style element by name.
	 * <p>
	 * An empty style definition will be created if there is no style
	 * definition.
	 * 
	 * @return the style element
	 * @see #getUsedStyleName()
	 */
	public OdfStyle getStyleElementForWrite() {
		if (mWritableStyleElement != null)
			return mWritableStyleElement;

		String styleName = getUsedStyleName();
		mWritableStyleElement = getWritableStyleElementByName(styleName, false);
		return mWritableStyleElement;
	}

	private OdfStyleBase getCurrentUsedStyle() {
		if (mWritableStyleElement != null)
			return mWritableStyleElement;
		else
			return mStyleElement;
	}

}
