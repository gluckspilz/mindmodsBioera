/* Tools.java v 1.0.9   11/6/04 7:15 PM
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

package bioera;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class Tools {
	public static String arrayEdit[];
/**
 * Tools constructor comment.
 */
public final static String [] addArrays(String t1[], String t2[]) throws Exception {
	if (t1 == null)
		return t2;
	else if (t2 == null)
		return t1;
	String ret[] = new String[t1.length + t2.length];
	System.arraycopy(t1, 0, ret, 0, t1.length);
	System.arraycopy(t2, t1.length, ret, t1.length, t2.length);
	return ret;
}
/**
 * Tools constructor comment.
 */
public final static String [] arrayAdd(String s) throws Exception {
	String temp[] = new String[arrayEdit.length + 1];
	System.arraycopy(arrayEdit, 0, temp, 0, arrayEdit.length);
	temp[arrayEdit.length] = s;
	arrayEdit = temp;
	return arrayEdit;
}
/**
 * Tools constructor comment.
 */
public final static String [] arrayClose(String t[]) throws Exception {
	String ret[] = arrayEdit;
	arrayEdit = null;
	return ret;
}
/**
 * Tools constructor comment.
 */
public final static String [] arrayNew(String t[]) throws Exception {
	if (t == null)
		t = new String[0];

	arrayEdit = t;

	return t;
}
/**
 */
public final static String changeSubstr(String str, String from, String to) {
	if (str == null)
		return null;
	StringBuffer sb = new StringBuffer();
	int ind = str.indexOf(from), j = 0;
	while (ind != -1) {
		sb.append(str.substring(j, ind)).append(to);
		j = ind + from.length();
		ind = str.indexOf(from, j);
	}
	sb.append(str.substring(j));
	return sb.toString();
}
/**
 * Tools constructor comment.
 */
public final static void copyFile(File src, File target) throws Exception {
	InputStream in = new BufferedInputStream(new FileInputStream(src));
	OutputStream out = new BufferedOutputStream(new FileOutputStream(target));
	try {
		int c;
		while ((c = in.read()) != -1) {
			out.write(c);
		}
	} finally {
		in.close();
		out.close();
	}
}
/**
 * Tools constructor comment.
 */
public final static Class createClass(String className) throws Exception {
	if (bioera.obfuscate.Mapper.inited) {
		return bioera.obfuscate.Mapper.createClass(className);
	}

	return Class.forName(className);
}
/**
 * Tools constructor comment.
 */
public final static File createUniqueFilePath(String prefixPath, String postfixPath) throws Exception {
	int i = 1; String number; File f;
	do {
		number = i < 10 ? "00" + i : i < 100 ? "0" + i : Integer.toString(i);
		f = new File (prefixPath + "_" + number + postfixPath);
		i++;
	} while (f.exists());

	return f;
}
/**
 * Tools constructor comment.
 */
public final static DateFormat dateTimeFormatter() {
	return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
}
/**
 * Debugger constructor comment.
 */
public static ArrayList getAllClassesFromJar(File f) throws Exception {
	ArrayList classes = new ArrayList();
	java.util.jar.JarFile jf = new java.util.jar.JarFile(f);
	Enumeration en = jf.entries();
	while (en.hasMoreElements()) {
		ZipEntry entry = (ZipEntry) en.nextElement();
		//System.out.println("url=" + url);
		if (entry == null || (!entry.getName().startsWith("bioera")))
			continue;

		String n = entry.getName();
		//System.out.println("Entry=" + n);
		if (n.endsWith(".class")) {
			n = n.replace('/', '.');
			n = n.substring(0, n.length() - ".class".length());
			if (classes.contains(n)) {
				// Do not load again the same class
				continue;
			}

			// Create name with package
			classes.add(n);
		}
	}

	return classes;
}
/**
 */
public final static String getClassName(Object o) {
	if (bioera.obfuscate.Mapper.inited)
		return bioera.obfuscate.Mapper.getClassName(o);
	return o.getClass().getName();
}
/**
 * Tools constructor comment.
 */
public final static String [] listRecursive(File dir) throws Exception {
	java.util.Vector v = new java.util.Vector();
	String list[] = dir.list();
	for (int i = 0; i < list.length; i++) {
		File f = new File(dir, list[i]);
		if (f.isDirectory()) {
			String ret[] = listRecursive(f);
			for (int j = 0; j < ret.length; j++)
				v.addElement(list[i] + File.pathSeparator + ret[j]);
		} else
			v.addElement(list[i]);
	}
	String retList[] = new String[v.size()];
	v.copyInto(retList);
	return retList;
}
/**
 * Tools constructor comment.
 */
public static void main(String args[]) throws Exception {
	try {
		System.out.println("started");
		Tools t = new Tools();
		System.out.println("Finished");
	} catch (Throwable e) {
		e.printStackTrace();
	}
}
/**
 * Tools constructor comment.
 */
public final static byte[] readFile(File file, int length) throws Exception {
	if (length <= 0)
		length = Integer.MAX_VALUE;
	int len = Math.min((int)file.length(), length);
	byte buf[] = new byte[len];
	FileInputStream in = new FileInputStream(file);
	try {
		in.read(buf);
	} finally {
		in.close();
	}

	return buf;
}
/**
 * Tools constructor comment.
 */
public final static File searchMostRecentFilePath(String prefixPath, String postfixPath) throws Exception {
	if (prefixPath == null || postfixPath == null) {
		throw new Exception("Path not set");
	}
	File ret = new File(prefixPath + postfixPath);
	if (ret.exists())
		return ret;

	// Search for similar files in the same folder
	String name = (new File(prefixPath)).getName();
	String parent = ret.getParent();
	if (parent == null)
		throw new Exception("Parent not found for '" + ret + "'");
	File folder = new File(parent);
	String list[] = folder.list();
	if (list == null)
		throw new Exception("Folder not found '" + folder + "'");
	ret = null;
	for (int i = 0; i < list.length; i++){
		if (list[i].startsWith(name) && list[i].endsWith(postfixPath)) {
			File f = new File(folder, list[i]);
			if (ret == null || ret.lastModified() < f.lastModified()) {
				ret = f;
			}
		}
	}

	return ret;
}
/**
 * Tools constructor comment.
 */
public final static void writeFile(File file, byte b[]) throws Exception {
	FileOutputStream out = new FileOutputStream(file);
	out.write(b);
	out.close();
}

/**
 * Tools constructor comment.
 */
public final static String getClassLastName(Object o) {
	String ret = getClassName(o);
	if (ret != null) {
		if (ret.indexOf('.') != -1)
			ret = ret.substring(ret.lastIndexOf('.') + 1);
		if (ret.indexOf(';') != -1)
			ret = ret.replace(';', '_');
	}
	return ret;
}
}
