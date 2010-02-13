/* SerialPortImpl.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.serial;

import java.io.*;
import java.lang.reflect.*;

/**
 * Insert the type's description here.
 * Creation date: (5/2/2004 7:01:47 PM)
 * @author: Jarek
 */
public abstract class SerialPortImpl {
	public InputStream in;
	public OutputStream out;

	public String ports[];
	public String dataBits[];
	public String stopBits[];
	public String parity[];
	public String flowControl[];
	public String bauds[] = {"110","300","1200","2400","4800","9600","19200","38400","57600","115200","230400","460800","921600"};
	protected Object serialPortObj;

	public boolean debug = false;
/**
 * SerialPortImpl constructor comment.
 */
SerialPortImpl() {
	super();
}
/**
 * SerialPortImpl constructor comment.
 */
protected String[] addField(String v, String t[]) throws Exception {
	String ret[] = (t == null ? new String[1] : new String[t.length + 1]);
	if (t != null) {
		System.arraycopy(t, 0, ret, 0, t.length);
	}
	ret[ret.length - 1] = v;
	return ret;
}
/**
 * SerialPortImpl constructor comment.
 */
public abstract void close() throws Exception;
/**
 * SerialPortImpl constructor comment.
 */
public abstract String connect(String port, String baud, String dataBits, String stopBits, String parity, String flow) throws Exception;
/**
 * SerialPortImpl constructor comment.
 */
public int fieldToInt(String s) throws Exception {
	try {
		return serialPortObj.getClass().getField(s).getInt(serialPortObj);
	} catch (Exception e) {
		throw new Exception("Field '" + s + "' access error: " + e);
	}
}
/**
 * SerialPortImpl constructor comment.
 */
public abstract void init() throws Exception;
/**
 * SerialPortImpl constructor comment.
 */
protected void init(Class o) throws Exception {
	Object t[][] = {
		{"DATABITS_", dataBits},
		{"STOPBITS_", stopBits},
		{"PARITY_", parity},
		{"FLOWCONTROL_", flowControl},
	};

	Field f[] = o.getFields();
	int count = 0;
	for (int i = 0; i < f.length; i++){
		int modifiers = f[i].getModifiers();
		if (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)) {
			String name = f[i].getName();
			//System.out.println("field " + name);
			for (int p = 0; p < t.length; p++){
				if (name.startsWith((String) t[p][0])) {
					t[p][1] = addField(name.substring(((String) t[p][0]).length()), (String[]) t[p][1]);
				}
			}
		}
	}

	dataBits = (String[]) t[0][1];
	stopBits = (String[]) t[1][1];
	parity = (String[]) t[2][1];
	flowControl = (String[]) t[3][1];
}

/**
 * SerialPortImpl constructor comment.
 */
public abstract void setDTR(boolean s) throws Exception;

/**
 * SerialPortImpl constructor comment.
 */
public abstract void setRTS(boolean s) throws Exception;
}
