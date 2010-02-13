/* SelectablePane.java v 1.0.9   11/6/04 7:15 PM
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
import java.awt.event.*;

/**
 * Insert the type's description here.
 * Creation date: (5/22/2004 12:05:36 PM)
 * @author: Jarek
 */
public class SelectablePane extends AbstractSComponentProperty implements ActionListener {
	public int selectedIndex = -1;
	private Object chosen;

	public Object props[];
	private JPanel panel;
//	public String name;
	private Object component;

	private String descriptions[];
/**
 * SelectablePane constructor comment.
 */
public SelectablePane(Object p[]) {
	this(p, null);
}
/**
 * SelectablePane constructor comment.
 */
public SelectablePane(Object p[], String[] desc) {
	super();
	props = p;
	descriptions = desc;
	if (props == null || desc == null)
		throw new RuntimeException("Null passed value(s) in SelectablePane");
}
	/**
	 * Invoked when an action occurs.
	 */
public void actionPerformed(java.awt.event.ActionEvent e) {
	int i = ((JComboBox)e.getSource()).getSelectedIndex();
	if (selectedIndex != i) {		
		selectedIndex = i;
		showPane();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2004 3:39:35 PM)
 */
public java.awt.Component getComponent() throws Exception {
	if (panel == null) {
		panel = new JPanel();
		if (props != null && props.length > 0)
			showPane();
	}
	
	return panel;
}
/**
 * SelectablePane constructor comment.
 */
public void save() {
//System.out.println("saving selectable pane");	
	//if (selectedIndex != -1) {
		//props[selectedIndex] = PTools.set(props[selectedIndex], component);
	//}	
}
/**
 * SelectablePane constructor comment.
 */
public void showPane() {
	int i = selectedIndex;
	if (i == -1)
		i = 0;
	try {
		chosen = props[i];
		if ((props[i] instanceof Propertable) && (!(props[i] instanceof SComponent))) {
			component = PTools.createPFields((Propertable) props[i], panel, null);
			PTools.fillContainer(((PFields)component));
		} else {
			panel.removeAll();
			panel.setLayout(new java.awt.BorderLayout());
			component = PTools.create(props[i]);
			panel.add((java.awt.Component) component, java.awt.BorderLayout.CENTER);
		}			
		JDialog d = PTools.getTopJDialog(panel);
		if (d != null) {
			d.pack();
			d.repaint();
		}
		if (descriptions != null && descriptions.length > i && descriptions[i] != null && descriptions[i].length() > 0)
			getPField().label.setText(descriptions[i] + ": ");
		else
			getPField().label.setText("");
	} catch (Exception e) {
		System.out.println("SelectablePane error: " + e);
		e.printStackTrace();
	}
}
}
