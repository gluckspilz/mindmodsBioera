/* Mapper.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.obfuscate;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import bioera.processing.*;

/**
 * Creation date: (7/2/2004 3:13:55 PM)
 * @author: Jarek Foltynski
 */
public class Mapper {
	public static boolean inited = false;
	public static PGMapper proguard;
	public static PropertableMapper prop;

	private static final boolean debug = false;
/**
 * Mapper constructor comment.
 */
public Mapper() {
	super();
}
/**
 * Mapper constructor comment.
 */
public static Class createClass(String cName) throws Exception {
	try {
		String nn = proguard.forwardClassName(cName);
		if (nn != null)
			return Class.forName(nn);
	} catch (Exception e) {
	}

	
	return Class.forName(cName);
}
/**
 * Mapper constructor comment.
 */
public final static String getClassName(Object o) {
	try {
		String nn = proguard.backwardClassName(o.getClass().getName());
		if (nn != null)
			return nn;
	} catch (Exception e) {
	}

	return o.getClass().getName();
}
/**
 * Mapper constructor comment.
 */
public static ElementProperty[] getElementProperties(Propertable element) throws Exception {
	String className = element.getClass().getName();
	if (proguard == null || prop == null) {
		if (debug)
			System.out.println("Obfuscation not initialized");
		return null;
	}
	
	String origClassName = proguard.backwardClassName(className);
	if (origClassName == null) {
		if (debug)
			System.out.println("Not found original class name for '" + className);
		return null;
	}

	// Find fields for this element
	ArrayList origPropList = prop.getProperties(origClassName);
	if (origPropList == null) {
		if (debug)
			System.out.println("Not found properties of class '" + origClassName);
		return null;
	}

	int count = 0;
	//System.out.println("class " + element.getClass().getName());	
	Field fields[] = element.getClass().getFields();
	for (int i = 0; i < fields.length; i++){
		//System.out.println("field1 " + fields[i].getName());					
		String obuscName = fields[i].getName();
		String origName = proguard.backwardFieldName(className, obuscName);
		if (origPropList.contains(origName)) {
			count++;
		} else {
			fields[i] = null;
		}
	}
	
	if (debug)
		System.out.println("Found " + count + " fields in class " + origClassName);	

	
	ElementProperty ret[] = new ElementProperty[count];
	count = 0;
	for (int n = 0; n < fields.length; n++){		
		if (fields[n] != null) {
			//System.out.println("field " + fields[n].getName());			
			ElementProperty prop = ret[count++] = new ElementProperty();
			Field field = fields[n];
			//prop.name = field.getName();
			prop.name = proguard.backwardFieldName(className, fields[n].getName());
			prop.value = field.get(element);
			if (prop.value == null) {
				prop.value = "";
			}
			if (debug)
				System.out.println("Obf get: changed from " + fields[n].getName() + " to " + prop.name + " type: " + prop.value.getClass().getName() + " value=" + prop.value);
			if (element.getPropertyDescription(prop.name) != null) {
				Object propertyDescription[] = element.getPropertyDescription(prop.name);
				prop.description = propertyDescription[1];
				if (propertyDescription.length > 3 && "false".equals(propertyDescription[3])) {
					prop.active = false;
				}
			}
		}
	}
	
	return ret;
}
/**
 * Mapper constructor comment.
 */
public static void init(java.io.File rootFolder) throws Exception {
	try {
		Class c = Class.forName("bioera/obfuscate/Mapper".replace('/', '.'));
		
		// Classes were not obfuscated
		if (debug)
			System.out.println("Classes were not obfuscated, maps not loaded");
		return;
	} catch (Exception e) {
	}
	
	File file = new File(rootFolder, "obfusc" + File.separator + "obf.map");
	File file1 = new File(rootFolder, "obfusc" + File.separator + "prop.map");
	if (file.exists() && file1.exists()) {
		bioera.obfuscate.Mapper.init(file, file1);
		if (debug)
			System.out.println("Loaded obfuscation maps");
	} else {
		if (debug)
			System.out.println("Obfuscation files not found");
	}

	inited = true;
}
/**
 * Mapper constructor comment.
 */
private static void init(java.io.File pgFile, java.io.File propFile) throws Exception {
	proguard = new PGMapper();
	proguard.load(pgFile);

	prop = new PropertableMapper();
	prop.load(propFile);
}
/**
 * Mapper constructor comment.
 */
public final static boolean setElementProperties(Propertable element, ElementProperty props[]) throws Exception {
	String className = element.getClass().getName();
	if (proguard == null || prop == null) {
		if (debug)
			System.out.println("Obfuscation not initialized");
		return false;
	}	
	
	String origClassName = proguard.backwardClassName(className);
	if (origClassName == null) {
		if (debug)
			System.out.println("Original class name for '" + className + " not found");
		return false;
	}

	for (int n = 0; n < props.length; n++){
		ElementProperty p = props[n];
		String newName = proguard.forwardFieldName(origClassName, p.name);
		if (newName == null)
			throw new Exception("Obfusc: name not found for class: " + origClassName + " field: " + p.name);
		Field field = null;
		try {			
			field = element.getClass().getField(newName);
			Object o = field.get(element);
			field.set(element, p.value);			
			if (debug) {
				System.out.println("Obfusc set: OrigClass=" + origClassName + ", obfClassName=" + className + ", newName=" + newName + ", OrigName=" + p.name + ", fieldType=" + field.getType() + " field value=" + p.value);
			}

			if (o != null && !o.equals(p.value)) {
				element.sendChangePropertyEvent(p.name, o);
			}
		} catch (Exception e) {
			System.out.println("Can't set property '" + newName + "' to '" + p.value + "'("+p.value.getClass()+") in class '" + element.getClass() + "': " + e);			
			if (bioera.SystemSettings.showStack)
				e.printStackTrace();
			if (debug) {
				System.out.println("OrigClass=" + origClassName + ", obfClassName=" + className + ", newName=" + newName + ", OrigName=" + p.name + ", fieldType=" + field.getType() + " field value=" + p.value);
			}
		}
	}
	
	return true;
}
}
