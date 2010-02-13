/* FloatRange.java v 1.0.9   11/6/04 7:15 PM
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

import bioera.processing.*;
import javax.swing.*;

/**
 * Creation date: (4/13/2004 5:13:52 PM)
 * @author: Jarek Foltynski
 */
public class FloatRange extends AbstractSComponentProperty {
	public double from;
	public double to;

	private JPanel panel;
	private JTextField fromTF;
	private JTextField toTF;
/**
 * Range constructor comment.
 */
public FloatRange() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 2:36:36 PM)
 */
public java.awt.Component getComponent() {
	if (panel == null) {
		JPanel left = new JPanel(new java.awt.BorderLayout());
		left.add(new JLabel("From: "), java.awt.BorderLayout.WEST);
		left.add(fromTF = new JTextField("" + from), java.awt.BorderLayout.CENTER);
		JPanel right = new JPanel(new java.awt.BorderLayout());
		right.add(new JLabel("To: "), java.awt.BorderLayout.WEST);
		right.add(toTF = new JTextField("" + to), java.awt.BorderLayout.CENTER);
		panel = new JPanel(new java.awt.GridLayout(1, 2));
		panel.setBorder(BorderFactory.createEtchedBorder());
		panel.add(left);
		panel.add(right);
	}
	
	return panel;

}
/**
 * getPropertyDescription method comment.
 */
public java.lang.Object[] getPropertyDescription(java.lang.String name) {
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 2:36:36 PM)
 */
public void save() {
	from = Double.parseDouble(fromTF.getText());
	to = Double.parseDouble(toTF.getText());
}
/**
 * sendChangePropertyEvent method comment.
 */
public void sendChangePropertyEvent(java.lang.String fieldName) {}
}
