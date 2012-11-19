package org.odftoolkit.simple.form;

import org.odftoolkit.odfdom.dom.element.form.FormFormElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeFormsElement;

/**
 * FormProvider provides the methods to create and get form instance in a way
 * that is implementation-independent.
 * 
 * @since 0.8
 */
public interface FormProvider {

	/**
	 * Create a form in a way that is implementation-independent.
	 * 
	 * @param name
	 *            -the form name
	 * @param parent
	 *            -the container element of this form
	 * @return a form instance
	 */
	public Form createForm(String name, OfficeFormsElement parent);

	/**
	 * Get a form instance by a <code>FormFormElement</code>
	 * 
	 * @param element
	 *            - a <code>FormFormElement</code>
	 * @return a form instance
	 */
	public Form getInstanceOf(FormFormElement element);
}
