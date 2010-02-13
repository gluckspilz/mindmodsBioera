/* XmlConfigSection.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.config;

import java.lang.*;
import bioera.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class XmlConfigSection {
	public XmlConfig config;

	protected static boolean debug = Debugger.get("xml.configsection");
public XmlConfigSection(XmlConfig c) {
	config = c;
}
public XmlConfigSection(java.io.File inFile) throws Exception {
	config = new XmlConfig(inFile);
}
public boolean containsSectionById(String name) throws Exception {
	try {
		if (debug)
			System.out.println("Looking by ID for section with attribute id='" + name + "' inside '" + config.getName() + "'");
		config.getChildConfigByAttribute("id", name);
		if (debug)
			System.out.println("found");
		return true;
	} catch (Exception e) {
		if (debug)
			System.out.println("not found");
		return false;
	}
}
public boolean containsSection(String name) {
	try {
		config.getChildConfigByName(name);
		return true;
	} catch (Exception e) {
		if (debug)
			System.out.println("XmlConfig error: " + e);
		return false;
	}
}
public boolean containsTrueCondition(String conditionThenValue) throws Exception {
	String ifValue = config.getAttribute("if");
	if (ifValue != null) {
		try {
			// Reference was found, return reference to the actual section
			XmlConfigSection s = getTopSection().getSection("Conditions").getSectionById(ifValue);
			String prop = s.getString("property");
			String value = s.getString("value");
			if (value.equals(System.getProperty(prop))) {
				// Condition is true
				if (conditionThenValue.equals(config.getAttribute("then"))) {
					if (debug)
						System.out.println("Condition in section " + config.getName() + " is true");
					return true;
				} else {
					// condition not equal
				}
			} else {
				// property not found
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unexpected error occurred whil processing condition '" + ifValue + "': " + e);
		}
	} else {
		// Condition doesn't exist
	}

	if (debug)
		System.out.println("Condition in section " + config.getName() + " is false");
	return false;
}
public String getAttribute(String attr) {
	return config.getAttribute(attr);
}
public String getContentText() throws Exception {
	return config.getAllText();
}
public java.util.Vector getElementNames() throws Exception {
	return config.elementsNames();
}
public java.util.List getElementSections() throws Exception {
	java.util.List list = config.getChildElementConfigs();
	for (int i = 0; i < list.size(); i++){
		list.set(i, new XmlConfigSection((XmlConfig) list.get(i)));
	}
	return list;
}
public int getInteger(String key, int defaultValue) throws Exception {
	try {
		return Integer.parseInt(getString(key));
	} catch (Exception e) {
		return defaultValue;
	}
}
public int getIntegerThrow(String key) throws Exception {
	return Integer.parseInt(getString(key));
}
public String getName() throws Exception {
	return config.getName();
}
public XmlConfigSection getNextSectionByName() throws Exception {
	try {
		XmlConfig c = config.getNextConfig();
		while (c != null) {
			if (this.config.getName().equals(c.getName()))
				return new XmlConfigSection(c);
			c = c.getNextConfig();
		}
	} catch (Exception e) {
	}

	return null;
}
public XmlConfigSection getParentSection() throws Exception {
	return new XmlConfigSection(config.getParentConfig());
}
public XmlConfigSection getReferencedSection(String name, String rootPath) throws Exception {
	// See if the attribute 'name' exists on parent section
	XmlConfig parent = config.getParentConfig();
	String attrValue = (parent != null ? parent.getAttribute(name) : null);
	if (attrValue == null) {
		if (debug)
			System.out.println("Attribute '"+name+"' doesn't exist in parent section");

		// Attribute doesn't exists, find node here
		XmlConfigSection s = getSection(name);
		// Check if this node is a reference
		if ((attrValue = s.config.getAttribute("ref")) == null) {
			// The node is definition
			return s;
		}
	}

	if (debug)
		System.out.println("Searching referenced section from top in '" + rootPath + "'");

	// Reference was found, return reference to the actual section
	return getTopSection().getSection(rootPath).getSectionById(attrValue);
}
public String getReferenceName() throws Exception {
	return config.getAttribute("ref");
}
public XmlConfigSection getSection(String name) throws Exception {
	return new XmlConfigSection(config.getChildConfigByName(name));
}
public XmlConfigSection getSectionById(String id) throws Exception {
	return new XmlConfigSection(config.getChildConfigByAttribute("id", id));
}
public String getString(String key) throws Exception {
	return config.getString(key);
}
public XmlConfigSection getTopSection() throws Exception {
	return new XmlConfigSection(config.getGreatRootConfig());
}
public String getType() {
	String type = config.getAttribute("type");
	if (type != null)
		return type;
	else
		return config.getName();

}
public boolean isReference() {
	return config.getAttribute("ref") != null;
}
public static void main(String[] args) {
	try {
		XmlConfig c = new XmlConfig(new java.io.File("c:\\projects\\eeg\\design\\default.xml"));
		XmlConfigSection s = new XmlConfigSection(c);
		s = s.getSection("Profiles");
		System.out.println("Next=" + s.getNextSectionByName());
	//	System.out.println("Result:\n" + c);
	} catch (Exception e) {
		System.out.println("error: " + e);
		e.printStackTrace();
	}

}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
public String toString() {
	return config.root.toString();
}

public XmlConfigSection(String s) throws Exception {
	config = new XmlConfig(s);
}
}
