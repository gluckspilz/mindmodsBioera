/* ConfigOld.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.deprecated;

import java.io.*;
import java.util.*;
import javax.comm.*;
import java.text.DateFormat;
import bioera.processing.*;
import bioera.config.*;
import java.lang.reflect.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class ConfigOld {
	private static boolean debug = bioera.Debugger.get("config");
/**
 * Tools constructor comment.
 */
public final static ElementProperty[] importXMLProperties(Propertable element, XmlConfigSection section, boolean printNotFoundMessages) throws Exception {
	ElementProperty ret[] = ProcessingTools.getElementProperties(element);
	for (int i = 0; i < ret.length; i++){
		ElementProperty p = ret[i];
		String name = p.name;
		try {
			p.value = load(p.value, section.getSection(name));
		} catch (Exception e) {
			if (printNotFoundMessages)
				System.out.println("Property '" + name + "' not imported: " + e.getMessage());
			if (debug)
				e.printStackTrace();
		}
	}
	ProcessingTools.setElementProperties(element, ret);

	return ret;
}
/**
 * Tools constructor comment.
 */
public final static Object load(Object o, XmlConfigSection s) throws Exception {
	if (debug)
		System.out.println("xml loading " + o + " from " + s.config.getPathFromTop());

	if (o == null) {
		// Object not set in the original element, lets try to reinstantiate it from description in the config file
		
		// Turn off reinstatiation for now
		o = reinstantiate(s);
		if (debug && o == null)
			System.out.println("! Value is null");
		return o;
//		return null;	
	}

	Object ret = o;
	if (o instanceof Configurable) {
		boolean exit = ((Configurable)o).load(s);
		if (exit)
			return o;
	} 
	
	if (o instanceof Propertable) {
		if (s.getElementSections().size() == 0) {
			try {
				// This can happen if the property was upgraded
				Constructor constr = o.getClass().getConstructor(new Class[]{String.class});
				o = constr.newInstance(new Object[]{s.getContentText()});
			} catch (Exception e1) {
				importXMLProperties((Propertable) o, s, true);
			}				
		} else {					
			importXMLProperties((Propertable) o, s, true);
		}
		return o;
	} else if (o instanceof Boolean) {
		ret = new Boolean(s.getContentText());
	} else if (o instanceof Float) {
		ret = new Float(s.getContentText());
	} else if (o instanceof Double) {
		ret = new Double(s.getContentText());
	} else if (o instanceof Long) {
		ret = new Long(s.getContentText());		
	} else if (o instanceof Integer) {
		ret = new Integer(s.getContentText());		
	} else if (o instanceof java.util.List) {
		List list = (List) o;
		ret = o;
		int size = Integer.parseInt(s.getAttribute("size"));		
		//System.out.println("loading list: " + size + " section: " + s.getName());
		if (size == list.size()) {
			for (int i = 0; i < size; i++){
				list.set(i, load(list.get(i), s.getSection("_" + i)));
			}
		} else {
			ret  = reinstantiate(s);
		}
	} else if (o.getClass().isArray()) {
		//System.out.println("I am in " + s.getName());		
		//System.out.println("I am in " + s.getName());			
		String sStr = s.getAttribute("size");
		if (sStr == null) {
			// This is older design format
			if (debug)
				System.out.println("Old type of table: " + s.getName());
			return bioera.properties.PTools.setArrayFromString(o, s.getContentText());
		} else {
			int size = Integer.parseInt(sStr);
			if (debug)
				System.out.println("loading new array: " + size + " secion: " + s.getName());
			if (size == Array.getLength(o)) {
				for (int i = 0; i < size; i++){
					Array.set(o, i, load(Array.get(o, i), s.getSection("_" + i)));
				}
			} else {
				ret  = reinstantiate(s);
			}
		}
	} else if (o instanceof String) {
		ret = s.getContentText();
	} else if (o instanceof bioera.ColorWrapper) {
		ret = new bioera.ColorWrapper(s.getContentText());
	}

	if (ret == null)
		throw new Exception("Object '" + o.getClass() + "' not loaded");
	
	return ret;	
}
/**
 * Tools constructor comment.
 */
public static void main(String args[]) throws Exception {
	try {
		System.out.println("started");
		//Config t = new Config();

			Class cl = Class.forName("[[Ljava.lang.Object;");
			Object ob = Array.newInstance(cl, 4);
			System.out.println("ob=" + ob);
			Constructor c[] = cl.getConstructors();
			for (int i = 0; i < c.length; i++){
				System.out.println("c=" + c[i]);
			}
//			Object ob = cl.newInstance();
//			System.out.println("ob=" + ob.getClass());
		
		System.out.println("Finished");
	} catch (Throwable e) {
		e.printStackTrace();
	}	
}
/**
 * Tools constructor comment.
 */
public final static Object reinstantiate(XmlConfigSection s) throws Exception {
	Object o = null;
	String className = s.getAttribute("class");
	//System.out.println("instantiating " + className);
	if (className != null && className.length() > 0) {
		// Class info found, lets try to load it
		Class cl = bioera.Tools.createClass(className);
		if (List.class.isAssignableFrom(cl)) {
			//System.out.println("instantiating list");			
			List list = (List) cl.newInstance();
			o = list;
			String str = s.getAttribute("size");
			int size = Integer.parseInt(str);
			//System.out.println("Importing list " + size);			
			for (int i = 0; i < size; i++){
//				list.add(reinstantiate(s.getSection("_" + i)));
				list.add(load(null, s.getSection("_" + i)));
			}
		} else if (cl.isArray()) {
			String str = s.getAttribute("size");
			if (str != null) {
				int size = Integer.parseInt(str);
				o = Array.newInstance(cl.getComponentType(), size);
				for (int i = 0; i < size; i++){
//					Array.set(o, i, reinstantiate(s.getSection("_" + i)));
					Array.set(o, i, load(null, s.getSection("_" + i)));
				}
			}
		} else {
			if ("string".equals(s.getAttribute("constr"))) {
				try {
					// Try load with empty constructor
					Constructor constr = cl.getConstructor(new Class[]{String.class});
					o = constr.newInstance(new Object[]{s.getContentText()});
				} catch (Exception e2) {
					o = null;
					if (debug)
						System.out.println("String constructor not found in class '" + className + "'");
				}				
			} else {
				Constructor constr;
				try {
					// Try load with empty constructor
					constr = cl.getConstructor(new Class[]{});
				} catch (Exception e2) {
					if (debug)
						System.out.println("No-parameter constructor not found in class '" + className + "'");
					return null;
				}
				try {
					o = constr.newInstance(new Object[]{});
				} catch (Exception e) {
					if (debug)
						System.out.println("No-parameter constructor initialization failed in class '" + className + "': " + e);
					return null;					
				}

				try {
					o = load(o, s);
				} catch (Exception e) {
					if (debug)
						System.out.println("No-parameter constructor load failed in class '" + className + "': " + e);
					return null;					
				}

			}
		}
	}

	//if (o != null)
		//System.out.println("Reinstantiated " + s.config.getPathFromTop());
	//else
		//System.out.println("Reinstantiation failed for " + s.config.getPathFromTop());
	return o;	
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
}
