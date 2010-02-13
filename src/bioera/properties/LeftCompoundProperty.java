/* LeftCompoundProperty.java v 1.0.9   11/6/04 7:15 PM
 *
 * BioEra - visual designer for biofeedback (http://www.bioera.net)
 *
 * Copyright (c) 2003-2004 Jarek Foltynski (http://www.foltynski.info)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package bioera.properties;

import javax.swing.*;

/**
 * Creation date: (3/26/2004 2:30:16 PM)
 * @author: Jarek Foltynski
 */
public abstract class LeftCompoundProperty extends AbstractSComponentProperty {

	protected JPanel panel;
	protected JTextField tf1;
/**
 * CompoundProperty constructor comment.
 */
public LeftCompoundProperty() {
}
/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 2:59:10 PM)
 */
public abstract java.awt.Component getC1() throws java.lang.Exception;
/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 2:59:10 PM)
 */
public abstract java.awt.Component getC2() throws java.lang.Exception;
/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 2:59:10 PM)
 */
public java.awt.Component getComponent() throws java.lang.Exception {
	if (panel == null) {
		panel = new JPanel(new java.awt.BorderLayout());
		panel.add(getC1(), java.awt.BorderLayout.CENTER);
		panel.add(getC2(), java.awt.BorderLayout.EAST);
	}
	return panel;
}
}
