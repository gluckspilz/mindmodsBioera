/* DebuggerRoutine.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.debugger;

import java.lang.reflect.*;
import java.io.*;

/**
 * Creation date: (3/13/2004 9:47:17 PM)
 * @author: Jarek Foltynski
 */
public class DebuggerRoutine {
/**
 * DebuggerRoutine constructor comment.
 */
public DebuggerRoutine() {
	super();
}
/**
 * DebuggerRoutine constructor comment.
 */
public final static void saveDebugIni(DebuggerFields d) {
	try {
		File dir = new File("config");
		if (!dir.exists())
			return;
		File f = new File(dir, "debug.ini");			
		OutputStream out = new FileOutputStream(f);
		for (int i = 0; i < DebuggerFields.fieldsDescriptions.length; i++){
			String s = d.isSet(i) ? "*" : "";
			s += d.getPrivateProperty(i) + "\r\n";
			out.write(s.getBytes());
		}
		out.close();
	} catch (Exception e) {
		System.out.println("Coudn't save debug.ini file: " + e);
	}
}
/**
 * DebuggerRoutine constructor comment.
 */
public final static void sendChangePropertyEvent(DebuggerFields d, String name) {
	Class cl = DebuggerFields.class;
	String fieldName = null;
	String className = null;
	try {
		int i = Integer.parseInt(name.substring(1));

		boolean value = cl.getField(name).getBoolean(d);

		// Field
		fieldName = d.getPrivateField(i);

		// Class
		className = d.getPrivateClass(i);

		// Class field
		Class c = bioera.Tools.createClass(className);
		Method m = c.getMethod("set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1), new Class[]{boolean.class});

		//f.setBoolean(null, value);
		m.invoke(d, new Object[]{new Boolean(value)});

		System.out.println("Field " + fieldName + " set to " + value);

		saveDebugIni(d);
	} catch (Exception e) {
		System.out.println("Debugger variable set error: " + e + " class: " + className + " field: " +fieldName);
	}
}
/**
 * DebuggerRoutine constructor comment.
 */
public final static void setProperty(String p) {
	try {
		DebuggerFields df = new DebuggerFields();
		for (int i = 0; i < df.fieldsDescriptions.length; i++){
			if (df.getPrivateProperty(i).equals(p)) {
				DebuggerFields.class.getField("v" + i).setBoolean(null, true);
			}
		}
	} catch (Exception e) {
		System.out.println("Coudn't set property: " + p);
	}
}
}
