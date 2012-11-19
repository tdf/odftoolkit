package org.odftoolkit.simple.form;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.xerces.dom.NodeImpl;
import org.odftoolkit.odfdom.dom.element.draw.DrawControlElement;
import org.odftoolkit.odfdom.dom.element.form.FormFormElement;
import org.odftoolkit.odfdom.dom.element.form.FormPropertiesElement;
import org.odftoolkit.odfdom.dom.element.form.FormPropertyElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Component;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.draw.Control;
import org.odftoolkit.simple.draw.ControlContainer;
import org.odftoolkit.simple.draw.FrameRectangle;
import org.odftoolkit.simple.style.StyleTypeDefinitions.AnchorType;
import org.w3c.dom.NodeList;

/**
 *This class represents the form control, which provides the methods to get/set
 * the control properties and style and layout properties of its binding drawing
 * shape.
 * 
 * @since 0.8
 */
public abstract class FormControl extends Component {

	protected Control drawingShape;
	protected FormFormElement formElement;
	protected FormPropertiesElement mFormProperties;
	protected OdfElement mElement;

	/**
	 * Create an instance of a drawing shape(<code>DrawControlElement</code>) as
	 * an visual representation of this form control.
	 * 
	 * @param parent
	 *            - the container where this drawing shape is contained.
	 * @return an instance of drawing shape
	 */
	Control createDrawControl(ControlContainer parent) {
		drawingShape = parent.createDrawControl();
		drawingShape.setControl(getId());
		return drawingShape;
	}

	/**
	 * Remove the form control from the container.
	 * <p>
	 * The resource is removed if it's only used by this object.
	 * 
	 * @see Control#remove()
	 * 
	 * @return true if the form control is successfully removed; false if
	 *         otherwise.
	 */
	public boolean remove() {
		try {
			Document mOwnerDocument = (Document) ((OdfFileDom) mElement
					.getOwnerDocument()).getDocument();
			if (getDrawControl() == null)
				loadDrawControl(mOwnerDocument.getContentRoot());
			getDrawControl().remove();
			formElement.removeChild(getOdfElement());
			mOwnerDocument.removeElementLinkedResource(getOdfElement());
			return true;
		} catch (Exception e) {
			Logger.getLogger(FormControl.class.getName()).log(Level.SEVERE,
					"fail to remove this element.");
			return false;
		}
	}

	/**
	 * Load an instance of drawing shape by searching the DrawControlElement
	 * which contains a reference to this control.
	 * 
	 * @param root
	 *            - root element where to search the DrawControlElement
	 * @return true if an element is found; false no element is found.
	 */
	boolean loadDrawControl(OdfElement root) {
		NodeList controls = root.getElementsByTagName("draw:control");
		for (int i = 0; i < controls.getLength(); i++) {
			DrawControlElement control = (DrawControlElement) controls.item(i);
			if (control.getDrawControlAttribute().equals(getId())) {
				drawingShape = (Control) Component
						.getComponentByElement(control);
				if (drawingShape == null) {
					drawingShape = new Control(control);
					Component.registerComponent(drawingShape, control);
				}
				return true;
			}
		}
		return false;

	}

	/**
	 * Get the OdfElement which represents this control
	 * 
	 * @return the OdfElement which represents this control
	 */
	public OdfElement getOdfElement() {
		return mElement;
	}

	/**
	 * Get the drawing shape binding to this control
	 * 
	 * @return the drawing shape binding to this control
	 */
	public Control getDrawControl() {
		return drawingShape;
	}

	/**
	 * Set the control id.
	 * 
	 * @param id
	 *            -the control id.
	 */
	public abstract void setId(String id);

	/**
	 * Get the control id.
	 * 
	 * @return the control id.
	 */
	public abstract String getId();

	/**
	 * Set the control name.
	 * 
	 * @param name
	 *            - the control name.
	 */
	public abstract void setName(String name);

	/**
	 * Get the control name.
	 * 
	 * @return the control name.
	 */
	public abstract String getName();

	/**
	 * Set the implementation of this control.
	 * 
	 * @param controlImpl
	 *            - the implementation description of this control
	 */
	public abstract void setControlImplementation(String controlImpl);

	/**
	 * Set the anchor position how this form control is bound to a text
	 * document.
	 * 
	 * @param anchorType
	 *            - the anchor position
	 */
	public void setAnchorType(AnchorType anchorType) {
		if (drawingShape == null)
			throw new IllegalStateException(
					"No drawing shape is binding to this control. Please call createDrawControl() or loadDrawControl() first.");
		drawingShape.setAchorType(anchorType);
	}

	/**
	 * Return the rectangle used as the bounding box of this form control
	 * 
	 * @return - the rectangle
	 */
	public FrameRectangle getRectangle() {
		return getDrawControl().getRectangle();
	}

	/**
	 * Set the rectangle used as the bounding box of this form control
	 * 
	 * @param rectangle
	 *            - the rectangle
	 */
	public void setRectangle(FrameRectangle rectangle) {
		getDrawControl().setRectangle(rectangle);

	}

	/**
	 * Get the <code>FormPropertiesElement</code> of this control, which is used
	 * to set the implementation-independent properties. If there's no such
	 * element, create a new one for this control.
	 * 
	 * @return an instance of the <code>FormPropertiesElement</code>
	 */
	abstract FormPropertiesElement getFormPropertiesElementForWrite();

	/**
	 * Get the <code>FormPropertiesElement</code> of this control, which is used
	 * to set the implementation-independent properties. If there's no such
	 * element, null will be returned.
	 * 
	 * @return an instance of the <code>FormPropertiesElement</code>
	 */
	FormPropertiesElement getFormPropertiesElementForRead() {
		return mFormProperties;
	}

	protected void setFormProperty(String formPropertyName,
			String officeValueType, String officeStringValue,
			Boolean officeBooleanValue, String officeDateValue,
			String officeTimeValue, Double officeValue, String officeCurrency) {
		FormPropertiesElement parentEle = getFormPropertiesElementForWrite();

		// find the existing property with the appointed form property name
		NodeList properties = parentEle.getChildNodes();
		FormPropertyElement formProperty = null;
		for (int i = 0; i < properties.getLength(); i++) {
			FormPropertyElement property = (FormPropertyElement) properties
					.item(i);
			if (property.getFormPropertyNameAttribute()
					.equals(formPropertyName)) {
				formProperty = (FormPropertyElement) properties.item(i);
				break;
			}
		}
		// create a new property
		if (formProperty == null) {
			org.w3c.dom.Document ownerDocument = parentEle.getOwnerDocument();
			formProperty = ((OdfFileDom) ownerDocument)
					.newOdfElement(FormPropertyElement.class);
			formProperty.setFormPropertyNameAttribute(formPropertyName);
			formProperty.setOfficeValueTypeAttribute(officeValueType);
		}
		// set the value
		if (officeStringValue != null)
			formProperty.setOfficeStringValueAttribute(officeStringValue);
		if (officeBooleanValue != null)
			formProperty.setOfficeBooleanValueAttribute(officeBooleanValue);
		if (officeDateValue != null)
			formProperty.setOfficeDateValueAttribute(officeDateValue);
		if (officeTimeValue != null)
			formProperty.setOfficeTimeValueAttribute(officeTimeValue);
		if (officeValue != null)
			formProperty.setOfficeValueAttribute(officeValue);
		if (officeCurrency != null)
			formProperty.setOfficeCurrencyAttribute(officeCurrency);

		((NodeImpl) parentEle).appendChild(formProperty);
	}

}
