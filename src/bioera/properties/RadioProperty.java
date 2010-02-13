/* RadioProperty.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.processing.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class RadioProperty extends ComboProperty {
	private JPanel panel;
public RadioProperty(String iitems[]) {
	super(iitems);
}
public java.awt.Component getComponent() {
	if (panel == null) {
		String t[] = getItems();
		panel= new JPanel();
		for (int k = 0; k < t.length; k++){
			JRadioButton rb = new JRadioButton(t[k]);
			rb.setSelected(k == getSelectedIndex());
			panel.add(rb);
		}
	}

	return panel;
}
}
