/* Debugger.java v 1.0.9   11/6/04 7:15 PM
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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Vector;

import bioera.debugger.DebuggerRecord;
import bioera.debugger.DebuggerRoutine;

/**
 * Insert the type's description here.
 * Creation date: (3/13/2004 9:47:18 AM)
 * @author: Jarek
 */
public class Debugger {
	boolean recurse = true;
	String srcDir;
	String outputFilePath;
	OutputStream out;
	public final static String CR = "\r\n";
	Vector all = new Vector();
	protected static boolean debug = Debugger.get("debugger");
/**
 * Debugger constructor comment.
 */
public Debugger() {
	super();
	debug = true;
}
/**
 * Debugger constructor comment.
 */
private void addDebugMethod(String mn, String fn, File f) throws Exception {
	if (debug) 
		System.out.println("Updating file " + f);
	
	File bak = new File(f.getAbsolutePath() + ".bak");
	Tools.copyFile(f, bak);
	
	BufferedReader in = new BufferedReader(new FileReader(bak));
	BufferedOutputStream out = null;

	Vector lines = new Vector();
	int index = -1, counter = -1;
	try {
		String line;
		while ((line = in.readLine()) != null) {
			lines.addElement(line);
			counter++;
			
			//System.out.println("line=" + line);
			if (line.indexOf("}") != -1) {
				index = counter;
			}

			if (line.equals("public static void " + mn	+ "(boolean newValue) {")) {
				// method already exists
				in.close();
				bak.delete();
				if (debug)
					System.out.println("method " + mn + " already exists in file " + f);				
				return;
			}
		}

		if (debug)
			System.out.println("lines " + counter);

		out = new BufferedOutputStream(new FileOutputStream(f));
	
		// Now write all before index
		for (int i = 0; i < index; i++){
			out.write(("" + lines.elementAt(i) + "\r\n").getBytes());		
		}

		// Insert method
		line = ("" + lines.elementAt(index)).trim();
		int i = line.lastIndexOf('}');
		if (i > 0) {
			out.write(("\r\n" + line.substring(0, i)).getBytes());
		}

		if (debug)
			System.out.println("added method " + mn + " in file " + f);
		
		String s = "public static void " + mn	+ "(boolean newValue) {\r\n"
		+"\t" + fn + " = newValue;\r\n"
		+"}\r\n";

		out.write(s.getBytes());
		
		out.write(line.substring(i).getBytes());
		
		// Now write all after index
		for (i = index + 1; i < lines.size(); i++){
			out.write(("" + lines.elementAt(i) + "\r\n").getBytes());		
		}
	} catch (Exception e) {
		if (out != null)
			out.close();
		f.delete();
		bak.renameTo(f);
		throw e;
	}

	out.close();
	in.close();
	bak.delete();		
}
/**
 * Debugger constructor comment.
 */
private void createFile() throws Exception {
	File f = new File(outputFilePath);
	String CR = "\r\n";
	StringBuffer sb = new StringBuffer();
	sb.append("package bioera.debugger;" + CR);
	sb.append("" + CR);
	sb.append("public class " + f.getName() + " implements bioera.processing.Propertable {" + CR);
	for (int i = 0; i < all.size(); i++){
		DebuggerRecord r = (DebuggerRecord) all.elementAt(i);
		sb.append("\tpublic static boolean v" + i + ";" + CR);
		sb.append("\t\tprivate static String c" + i + "=\""+r.className+"\";" + CR);
		sb.append("\t\tprivate static String p" + i + "=\""+r.property+"\";" + CR);
		sb.append("\t\tprivate static String f" + i + "=\""+r.field+"\";" + CR);
	}

	sb.append("\tfinal static String fieldsDescriptions [][] = {");
	for (int i = 0; i < all.size(); i++){
		DebuggerRecord r = (DebuggerRecord) all.elementAt(i);
		sb.append("\t{\"v" + i + "\", \"" + r.className + " - " + r.field + "\", \"\"}," + CR);
	}
	sb.append("\t};" + CR);
	
	sb.append(CR);
	sb.append("public final Object [] getPropertyDescription(String name) {" + CR);
	sb.append("\treturn bioera.processing.ProcessingTools.searchPropertyDescription(name, fieldsDescriptions);" + CR);
	sb.append("}" + CR);
	sb.append(CR);

	sb.append("public java.lang.String[] getPropertyNames() {" + CR);
	sb.append("\treturn null;" + CR);
	sb.append("}" + CR);
	sb.append(CR);
	
	sb.append("public final void sendChangePropertyEvent(String fieldName, Object oldValue) {" + CR);
	sb.append("\tDebuggerRoutine.sendChangePropertyEvent(this, fieldName);" + CR);	
	sb.append("}" + CR);

	sb.append("public final String getPrivateField(int i) {" + CR);
	sb.append("\tswitch (i) {" + CR);
	for (int i = 0; i < all.size(); i++){
		sb.append("\t\tcase " + i + ": return f" + i + ";" + CR);
	}
	sb.append("\t}" + CR);
	sb.append("\tthrow new RuntimeException(\"Field f\"+i+\" not found\");" + CR);	
	sb.append("}" + CR);
	sb.append(CR);

	sb.append("public final String getPrivateClass(int i) {" + CR);
	sb.append("\tswitch (i) {" + CR);
	for (int i = 0; i < all.size(); i++){
		sb.append("\t\tcase " + i + ": return c" + i + ";" + CR);
	}
	sb.append("\t}" + CR);
	sb.append("\tthrow new RuntimeException(\"Class c\"+i+\" not found\");" + CR);	
	sb.append("}" + CR);
	sb.append(CR);

	sb.append("public final String getPrivateProperty(int i) {" + CR);
	sb.append("\tswitch (i) {" + CR);
	for (int i = 0; i < all.size(); i++){
		sb.append("\t\tcase " + i + ": return p" + i + ";" + CR);
	}
	sb.append("\t}" + CR);
	sb.append("\tthrow new RuntimeException(\"Property p\"+i+\" not found\");" + CR);
	sb.append("}" + CR);

	sb.append("public final boolean isSet(int i) {" + CR);
	sb.append("\tswitch (i) {" + CR);
	for (int i = 0; i < all.size(); i++){
		sb.append("\t\tcase " + i + ": return v" + i + ";" + CR);
	}
	sb.append("\t}" + CR);
	sb.append("\tthrow new RuntimeException(\"Value v\"+i+\" not found\");" + CR);
	sb.append("}" + CR);
		
	sb.append("}" + CR);
	
		
	out = new BufferedOutputStream(new FileOutputStream(f.getPath() + ".java"));
	out.write(sb.toString().getBytes());
	out.close();
}
/**
 * Debugger constructor comment.
 */
public static boolean get(String s) {
	//System.out.println("loading debug " + s);	
	return System.getProperty("bioera.debug." + s) != null;
}
/**
 * Debugger constructor comment.
 */
public static final void loadDebuggerInfo(String rootFolder, String fileName) throws Exception {
	java.io.File f = new java.io.File(rootFolder, fileName);
	if (!f.exists()) {
		f = new java.io.File(fileName);
		if (!f.exists()) {
			return;
		}
	}		

	BufferedReader in = null;
	try {		
		in = new BufferedReader(new FileReader(f));
		String line;
		java.util.Properties props = System.getProperties();
		while ((line = in.readLine()) != null) {
			line = line.trim();
			if (line.length() == 0 || line.startsWith("#") || line.startsWith("//")) {
				// Skip comments
				continue;
			}

			if (line.length() > 1 && (line.startsWith("y ") || line.startsWith("*") || line.startsWith("+"))) {
				line = line.substring(1).trim();
				if (line.indexOf(".debug.") == -1 && line.indexOf("bioera") == -1) {
					props.put("bioera.debug." + line, "true");
					System.out.println("Set debugger property '" + "bioera.debug." +line + "'");
				} else {
					props.put(line, "true");
					System.out.println("Set debugger property '" + line + "'");
				}
				DebuggerRoutine.setProperty(line);				
			}
		}
	} catch (Exception e) {
		System.out.println("Unexpected error while reading debugger properties: " + e);
	} finally {
		if (in != null)
			try {
				in.close();
			} catch (Exception e1) {
			}
	}
}
/**
 * Debugger constructor comment.
 */
public static void main(String[] args) {
	try {
		new Debugger().start(args);
	} catch (Exception e) {
		e.printStackTrace();
	}
}
/**
 * Debugger constructor comment.
 */
private void process(File srcFolder, String path) throws Exception {
	File list[] = srcFolder.listFiles();
	if (list != null) {
		for (int i = 0; i < list.length; i++){
			File f = list[i];
			if (f.isFile()) {
				// Only .java files
				if (!f.getName().endsWith(".java")) {
					//if (debug)
						//System.out.println("Not a java file " + f);
					continue;
				}

				processFile(f, path);
			} else {
				if (recurse)
					process(new File(srcFolder, f.getName()), path + "." + f.getName());
			}
		}
	}
}
/**
 * Debugger constructor comment.
 */
private void processFile(File f, String path) throws Exception {
	if (debug) 
		System.out.println("Processing file " + f);
	
	BufferedReader in = new BufferedReader(new FileReader(f));

	String line; int i;
	while ((line = in.readLine()) != null) {
		//System.out.println("line=" + line);
		if ((i = line.indexOf("Debugger.get(")) != -1 && line.indexOf("i += \"Debugger.get(") == -1 && line.indexOf("line.indexOf") == -1) {
			if (debug) 
				System.out.println("Converting file " + f);
			
			i += "Debugger.get(".length() + 1;
			int j = line.indexOf("\"", i);
			if (j == -1)
				throw new Exception("Error in file '" + f.getName() + "'" + " line: '" + line + "'" );

			String property = line.substring(i, j);

			// Now find the field name
			i = line.indexOf("boolean");
			if (i == -1)
				throw new Exception("Error in file '" + f.getName() + "'" + " line: '" + line + "'" );
			i += "boolean".length();
			j = line.indexOf("=", i);
			if (j == -1)
				throw new Exception("Error in file '" + f.getName() + "'" + " line: '" + line + "'" );
			
			String field = line.substring(i, j).trim();
			
			DebuggerRecord r = new DebuggerRecord();
			r.className = path + "." + f.getName().substring(0, f.getName().length() - ".java".length());			
			r.property = property;
			r.field = field;
			r.isSet = false;
			if (all.contains(r)) {
				System.out.println("already exists");
				continue;
			}
				
			all.addElement(r);

			//System.out.println("Added");
			// check if there is method to set this debug
			Class c = Class.forName(r.className);
			String mn = "set" + Character.toUpperCase(r.field.charAt(0)) + r.field.substring(1);
			try {
				java.lang.reflect.Method m = c.getMethod(mn, new Class[]{boolean.class});
				System.out.println("*** FOUND *** ");
			} catch (Exception e) {
				// method not found
				if (debug)
					System.out.println("No method: " + mn);
				addDebugMethod(mn, r.field, f);
			}
			
		}			
	}

	in.close();
}
/**
 * Debugger constructor comment.
 */
public void start(String[] args) throws Exception {
	for (int i = 0; i < args.length; i++){
		String arg = args[i];
		if ("-!recurse".equals(arg)) {
			this.recurse = false;
		} else if ("-src".equals(arg)) {
			srcDir = args[++i];
		} else if ("-output".equals(arg)) {
			outputFilePath = args[++i];
		} else {
			System.out.println("Unknown parameter: " + arg);
			return;
		}
	}

	if (srcDir == null || outputFilePath == null) {
		System.out.println("params not set");
		return;
	}

	process(new File(srcDir), "bioera");

	if (all.size() == 0) {
		System.out.println("no debugging found");
		return;
	}
		
	System.out.println("Number of debugging info: " + all.size());

	Collections.sort(all);

	createFile();
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
}
