/* ComboProperty.java v 1.0.9   11/6/04 7:15 PM
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
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class ComboProperty extends AbstractSComponentProperty {
	public String name = "";

	protected int index = -1;
	protected int defaultIndex = -1;
	private String items[];

	private JComboBox combo;
public ComboProperty(String iitems[]) {
	super();
	this.items = iitems;
}
public ComboProperty(String iitems[], int dI) {
	super();
	this.items = iitems;
	defaultIndex = dI;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2004 3:34:59 PM)
 */
public void addActionListener(java.awt.event.ActionListener l) {
	getComponent();
	combo.addActionListener(l);
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2004 3:34:59 PM)
 */
public java.awt.Component getComponent() {
	if (combo == null) {
		String t[] = getItems();
		combo = new JComboBox();
		for (int k = 0; k < t.length; k++){
			combo.addItem(t[k]);
		}

		//System.out.println("combo name=" + name);		
		if (getSelectedIndex() != -1)
			combo.setSelectedIndex(getSelectedIndex());
		else if (defaultIndex != -1)
			combo.setSelectedIndex(defaultIndex);
	}
	return combo;
}
public String getItem(int i) {
	if (i >= 0 && i < items.length)
		return items[i];
	return null;
}
public String [] getItems() {
	return items;
}
public int getSelectedIndex() {
	if (index == -1) {
		// search by name
		index = indexOf(name);
	}
	return index;
}
public String getSelectedItem() {
	return name;
}
public int indexOf(String item) {
	if (name == null)
		return defaultIndex;

	for (int i = 0; i < items.length; i++){
		if (items[i].equals(item))
			return i;
	}

	return -1;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2004 3:34:59 PM)
 */
public void save() {
	name = "" + combo.getSelectedItem();
	//System.out.println("set COMBO name to " + name);	
	index = -1;
}
public void setItems(String s[]) {
	if (s == null)
		s = new String[0];
	items = s;
	if (indexOf(name) != -1)
		setSelectedItem(name);
}
/**
 * Insert the method's description here.
 * Creation date: (5/26/2004 3:41:05 PM)
 */
public void setLabel(javax.swing.JLabel name) {}
public void setSelectedIndex(int i) {
	index = i;
	if (i >= 0 && i < items.length)
		name = items[i];
	else
		name = null;
}
public void setSelectedItem(String n) {
	for (int i = 0; i < items.length; i++){
		if (items[i].equals(n)) {
			index = i;
			name = n;
		}
	}
}
public void setSelectedItemThrow(String n) throws Exception {
	for (int i = 0; i < items.length; i++){
		if (items[i].equals(n)) {
			index = i;
			name = n;
			return;
		}
	}

	throw new Exception("Field '" + n + "' not found");
}
public String toString() {
	return ProcessingTools.arrayToString(items);
}
}
