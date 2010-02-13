/* OTable.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.layouts;

import java.util.Vector;

public class OTable implements java.io.Serializable {
	Vector keys;
	Vector values;
/**
 */
public OTable() {
	this(10);
}
/**
 */
public OTable(int size) {
	super();
	keys = new Vector(size);
	values = new Vector(size);
}
/**
 */
public boolean contains(Object value) {
	if (values.indexOf(value) != -1)
		return true;
	return false;
}
/**
 */
public boolean containsKey(Object key) {
	if (keys.indexOf(key) != -1)
		return true;
	return false;
}
/**
 */
public OTable createCopy() throws Exception {
	OTable ret = new OTable();
	ret.keys = (Vector) keys.clone();
	ret.values = (Vector) values.clone();
	return ret;
}
/**
 */
public Object get(Object key) {
	int ind = keys.indexOf(key);
	if (ind == -1)
		return null;
	return values.elementAt(ind);
}
/**
 */
public Object getKeyAt(int index) {
	if (keys.size() > index)
		return keys.elementAt(index);
	return null;
}
/**
 */
public Vector getKeys() {
	return keys;
}
/**
 */
public Object getValueAt(int index) {
	if (values.size() > index)
		return values.elementAt(index);
	return null;
}
/**
 */
public Vector getValues() {
	return values;
}
/**
 */
public java.util.Enumeration keys() {
	return ((Vector)keys.clone()).elements();
}
/**
 */
public Object put(Object key, Object value) {
	Object previous = get(key);
	remove(key);
	keys.addElement(key);
	values.addElement(value);
	return previous;
}
/**
 */
public Object remove(Object key) {
	int ind = keys.indexOf(key);
	Object ret = null;
	if (ind != -1) {
		keys.removeElementAt(ind);
		ret = values.elementAt(ind);
		values.removeElementAt(ind);
	}
	return ret;
}
/**
 */
public void removeAll() {
	keys.removeAllElements();
	values.removeAllElements();
}
/**
 */
public int size() {
	return keys.size();
}
/**
 */
public boolean sort() {
	java.text.Collator c = java.text.Collator.getInstance();

	// Start from the end, since new elements are at the end
	int size = keys.size() - 1;
	int tohere = 0;
	boolean any = true;
	while (any) {
		any = false;
		for (int i = size; i > tohere; i--) {
			String key = (String) keys.elementAt(i);
			String lowerkey = (String) keys.elementAt(i - 1);
			if (c.compare(key, lowerkey) < 0) {			
				// Change them
				Object value = values.elementAt(i);
				keys.removeElementAt(i);
				values.removeElementAt(i);
				keys.insertElementAt(key, i-1);
				values.insertElementAt(value, i-1);
				any = true;
			}
		}		
		tohere++;
	}
	
	return false;
}
/**
 */
public String toString() {
	return toString(this);
}
/**
 */
public String toString(OTable tab) {
	if (tab == null)
		return "[null]";
	StringBuffer sb = new StringBuffer();
	java.util.Enumeration en = tab.keys();
	while (en.hasMoreElements()) {
		if (sb.length() == 0)
			sb.append("[");
		else
			sb.append(", ");		
		String key = (String) en.nextElement();
		sb.append(key + "=");
		Object value = tab.get(key);
		if (value == null)
			sb.append("<null>");
		else if (value instanceof String)
			sb.append((String)value);
		else if (value instanceof OTable)
			sb.append(toString((OTable) value));
		else
			sb.append(value.toString());
	}
	if (sb.length() == 0)
		sb.append("[");
	sb.append("]");
	return sb.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (4/21/2000 11:18:03 AM)
 */
public java.util.Enumeration values() {
	return values.elements();
}
}
