/* PFields.java v 1.0.9   11/6/04 7:15 PM
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

import java.io.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import bioera.processing.*;
import bioera.*;
import bioera.layouts.*;
import bioera.graph.designer.*;


/**
 * Creation date: (5/26/2004 4:09:26 PM)
 * @author: Jarek Foltynski
 */
public class PFields {
	// Contains information about class name (or null if nothing to display)
	PField classInfo;

	// All fields (PField) of the class (or array)
	ArrayList list = new ArrayList();

	// Contains panel with all elements
	Container container;

	// Direct object (propertable class or array) that is being described by this object
	Object classObject;

	// Parent PFields or TabbedPane object
	Object parent;
/**
 * PropertiesLayout constructor comment.
 */
public PFields() {
	super();
}
/**
 * PropertiesLayout constructor comment.
 */
public PFields(JComponent panel) {
	super();
	container = panel;
}
/**
 * PropertiesLayout constructor comment.
 */
public void add(PField f) {
	list.add(f);
}
/**
 * PropertiesLayout constructor comment.
 */
private PField get(int index) {
	if (index < list.size())
		return (PField) list.get(index);
	throw new IndexOutOfBoundsException("index=" + index + " (" + list.size() + ")");
}
/**
 * PropertiesLayout constructor comment.
 */
public Object getComponent(int i) {
	return get(i).graphComponent;
}
/**
 * PropertiesLayout constructor comment.
 */
public Object getComponent(String name) {
	for (int i = 0; i < list.size(); i++){
		//System.out.println("p=" + getProperty(i));		
		if (name.equals(getProperty(i).name))
			return getComponent(i);
	}

	throw new RuntimeException("Property '" + name + "' not found");
}
/**
 * PropertiesLayout constructor comment.
 */
public Component getLabel(int index) {
	return get(index).label;
}
/**
 * PropertiesLayout constructor comment.
 */
public String getLabelText(int index) {
	Component comp = get(index).label;
	String ret;
	if (comp instanceof JLabel)
		ret = ((JLabel)comp).getText();
	else
		ret = comp.getClass().toString();
	if (ret.indexOf(":") != -1)
		ret =  ret.substring(0, ret.indexOf(":"));
	return ret;
}
/**
 * PropertiesLayout constructor comment.
 */
public ElementProperty getProperty(int index) {
	return get(index).property;
}
/**
 * PropertiesLayout constructor comment.
 */
public JTabbedPane getTopTabbedPane() {
	Object ret = parent;
	
	while (ret != null && !(ret instanceof JTabbedPane)) {
		ret = ((PFields) ret).parent;
	}
	if (ret == null)
		System.out.println("Tabbed pane not found");
	return (JTabbedPane) ret;
}
/**
 * PropertiesLayout constructor comment.
 */
public int size() {
	return list.size();
}

/**
 * PropertiesLayout constructor comment.
 */
public PField get(String name) {
	for (int i = 0; i < list.size(); i++){
		//System.out.println("p=" + getProperty(i));		
		if (name.equals(getProperty(i).name))
			return get(i);
	}

	throw new RuntimeException("Property '" + name + "' not found");
}
}
