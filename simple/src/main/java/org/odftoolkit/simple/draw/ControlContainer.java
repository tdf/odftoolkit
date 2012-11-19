package org.odftoolkit.simple.draw;

import org.odftoolkit.odfdom.pkg.OdfElement;

/**
 * ControlContainer is a container which maintains Control(s) as element(s).
 * 
 * @since 0.8
 */
public interface ControlContainer {
	/**
	 * Create an instance of control and and append it at the end of this
	 * container.
	 * 
	 * @return a control instance
	 */
	public Control createDrawControl();

	/**
	 * Get the ODF element which can have DrawControlElement as child element
	 * directly.
	 * 
	 * @return - an ODF element which can have control as child
	 */
	public OdfElement getDrawControlContainerElement();
}
