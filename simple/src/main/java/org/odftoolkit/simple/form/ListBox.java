package org.odftoolkit.simple.form;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.element.form.FormFormElement;
import org.odftoolkit.odfdom.dom.element.form.FormListboxElement;
import org.odftoolkit.odfdom.dom.element.form.FormOptionElement;
import org.odftoolkit.odfdom.dom.element.form.FormPropertiesElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.form.FormTypeDefinition.FormListSourceType;
import org.w3c.dom.NodeList;

/**
 * This class represents the form control of List Box, provides methods to
 * get/set the form properties and the style formatting of this control.
 * 
 * @since 0.8
 */
public class ListBox extends FormControl {

	private ArrayList<String> entries;

	ListBox(FormListboxElement element) {
		this.mElement = element;
		formElement = (FormFormElement) element.getParentNode();
	}

	/**
	 * Get an instance of list box by an instance of FormListboxElement, while
	 * searching the document content to make a bind with the DrawControl which
	 * already reference to this list box.
	 * 
	 * @param element
	 *            - an instance of FormComboboxElement
	 * @return an instance of list box
	 */
	public static ListBox getInstanceOf(FormListboxElement element) {
		ListBox listbox = new ListBox(element);
		try {
			listbox.loadDrawControl(((Document) ((OdfFileDom) element
					.getOwnerDocument()).getDocument()).getContentRoot());
		} catch (Exception e) {
			Logger.getLogger(ListBox.class.getName()).log(Level.WARNING,
					"Cannot load the drawing shape of this listbox.");
		}
		return listbox;
	}

	@Override
	FormPropertiesElement getFormPropertiesElementForWrite() {
		if (mFormProperties == null)
			mFormProperties = ((FormListboxElement) mElement)
					.newFormPropertiesElement();
		return mFormProperties;
	}

	@Override
	public void setControlImplementation(String controlImpl) {
		((FormListboxElement) mElement)
				.setFormControlImplementationAttribute(controlImpl);
	}

	@Override
	public String getId() {
		return ((FormListboxElement) mElement).getFormIdAttribute();
	}

	@Override
	public void setId(String id) {
		((FormListboxElement) mElement).setFormIdAttribute(id);
	}

	@Override
	public String getName() {
		return ((FormListboxElement) mElement).getFormNameAttribute();
	}

	@Override
	public void setName(String name) {
		((FormListboxElement) mElement).setFormNameAttribute(name);
	}

	/**
	 * Set if the list box support multi-selection
	 * 
	 * @param isMultiSelection
	 *            - specify if the list box supports multi-selection
	 */
	public void setFormMultiSelection(boolean isMultiSelection) {
		((FormListboxElement) mElement)
				.setFormMultipleAttribute(isMultiSelection);
	}

	/**
	 * Get if the list box support multi-selection
	 * 
	 * @return true if the list box supports multi-selection; false if not.
	 */
	public boolean getFormMultiSelection() {
		return ((FormListboxElement) mElement).getFormMultipleAttribute();
	}

	/**
	 * Set the visibility of the drop-down list
	 * 
	 * @param isDropDown
	 *            - specify if the drop-down list is visible
	 */
	public void setFormDropdown(boolean isDropDown) {
		((FormListboxElement) mElement).setFormDropdownAttribute(isDropDown);
	}

	/**
	 * Get the visibility of the drop-down list
	 * 
	 * @return true if the drop-down list is visible; false if not.
	 */
	public boolean getFormDropdown() {
		return ((FormListboxElement) mElement).getFormDropdownAttribute();
	}

	/**
	 * Add a list item to this list box.
	 * 
	 * @param item
	 *            - a list item
	 */
	public void addItem(String item) {
		if (item == null)
			return;
		if (entries == null)
			entries = new ArrayList<String>();
		((FormListboxElement) mElement).newFormOptionElement()
				.setFormLabelAttribute(item);
		entries.add(item);
	}

	/**
	 * Get the list entries if they are initiated through a list of string.
	 * 
	 * @return the list entries
	 */
	public ArrayList<String> getEntries() {
		if (entries == null || entries.size() == 0) {
			NodeList items = mElement.getElementsByTagName("form:option");
			if (items != null && items.getLength() > 0) {
				for (int i = 0; i < items.getLength(); i++) {
					if (entries == null)
						entries = new ArrayList<String>();
					entries.add(((FormOptionElement) items.item(i))
							.getFormLabelAttribute());
				}
				return entries;
			}
		}
		return null;
	}

	/**
	 * Add a group of list items to this list box
	 * 
	 * @param items
	 *            -a group of list items
	 */
	public void addItems(String[] items) {
		if (items == null || items.length == 0)
			return;
		for (int i = 0; i < items.length; i++) {
			addItem(items[i]);
		}
	}

	/**
	 * Set the source type of the data list.
	 * 
	 * @param type
	 *            - the source type of this list
	 */
	public void setListSourceType(FormListSourceType type) {
		((FormListboxElement) mElement).setFormListSourceTypeAttribute(type
				.toString());
	}

	/**
	 * Get the source type of the data list.
	 * 
	 * @return the source type of this list
	 */
	public FormListSourceType getListSourceType() {
		String aValue = ((FormListboxElement) mElement)
				.getFormListSourceTypeAttribute();
		return FormListSourceType.enumValueOf(aValue);
	}

	/**
	 * Set the source of this data list.
	 * 
	 * @param listSource
	 *            - the source of this data list.
	 */
	public void setListSource(String listSource) {
		((FormListboxElement) mElement).setFormListSourceAttribute(listSource);
	}

	/**
	 * Get the source of this data list.
	 * 
	 * @return the source of this data list.
	 */
	public String getListSource() {
		return ((FormListboxElement) mElement).getFormListSourceAttribute();
	}

	/**
	 * Set the data field referenced by this combo box
	 * 
	 * @param dataField
	 *            - the data field referenced by this combo box
	 */
	public void setDataField(String dataField) {
		((FormListboxElement) mElement).setFormDataFieldAttribute(dataField);
	}

	/**
	 * Get the data field referenced by this combo box
	 * 
	 * @return the data field
	 */
	public String getDataField() {
		return ((FormListboxElement) mElement).getFormDataFieldAttribute();
	}

	/**
	 * Get a simple iterator for list boxes.
	 * 
	 * @param container
	 *            - an instance of form where to traverse the list boxes
	 */
	public static Iterator<FormControl> getSimpleIterator(Form container) {
		return new SimpleListBoxIterator(container);
	}

	private static class SimpleListBoxIterator implements Iterator<FormControl> {

		private FormFormElement containerElement;
		private ListBox nextElement = null;
		private ListBox tempElement = null;

		private SimpleListBoxIterator(Form container) {
			containerElement = container.getOdfElement();
		}

		public boolean hasNext() {
			tempElement = findNext(nextElement);
			return (tempElement != null);
		}

		public ListBox next() {
			if (tempElement != null) {
				nextElement = tempElement;
				tempElement = null;
			} else {
				nextElement = findNext(nextElement);
			}
			if (nextElement == null) {
				return null;
			} else {
				return nextElement;
			}
		}

		public void remove() {
			if (nextElement == null) {
				throw new IllegalStateException("please call next() first.");
			}
			nextElement.remove();
		}

		private ListBox findNext(ListBox thisListBox) {
			FormListboxElement nextListBox = null;
			if (thisListBox == null) {
				nextListBox = OdfElement.findFirstChildNode(
						FormListboxElement.class, containerElement);
			} else {
				nextListBox = OdfElement.findNextChildNode(
						FormListboxElement.class, thisListBox.getOdfElement());
			}

			if (nextListBox != null) {
				return ListBox.getInstanceOf(nextListBox);
			}
			return null;
		}
	}

}
