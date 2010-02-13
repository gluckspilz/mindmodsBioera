/* PropertableMapper.java v 1.0.9   11/6/04 7:15 PM
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

import java.util.*;
import java.io.*;
import bioera.debugger.*;
import bioera.*;
import java.lang.reflect.*;
import java.util.zip.*;

/**
 * Insert the type's description here.
 * Creation date: (3/13/2004 9:47:18 AM)
 * @author: Jarek
 */
public class PropertableMapper {
	HashMap classes = new HashMap();
	File inFile;
	File outFile;
	BufferedWriter out;
	public final static String CRLF = "\r\n";
	ArrayList all = new ArrayList();
	protected static boolean debug = true;
/**
 * Debugger constructor comment.
 */
public PropertableMapper() {
	super();
	debug = true;
}
/**
 * Debugger constructor comment.
 */
public void generate(String[] args) throws Exception {
	String outPath = null;
	String srcJar = null;
	for (int i = 0; i < args.length; i++){
		String arg = args[i];
		if ("-src".equals(arg)) {
			srcJar = args[++i];
		} else if ("-out".equals(arg)) {
			outPath = args[++i];
		} else {
			System.out.println("Unknown parameter: " + arg);
			return;
		}
	}

	if (srcJar == null) {
		System.out.println("params not set");
		return;
	}

	if (outPath == null)
		outPath = "default.map";

	outFile = new File(outPath);

	out = new BufferedWriter(new FileWriter(outFile));
	
	inFile = new File(srcJar);
	if (!inFile.exists())
		System.out.println("File not found: " + inFile);

	List list = Tools.getAllClassesFromJar(inFile);

	//for (int i = 0; i < list.size(); i++){
		//System.out.println("class=" + list.get(i));
	//}
	
	//System.out.println("Total " + list.size());

	for (int i = 0; i < list.size(); i++){
		String cn = (String) list.get(i);
		try {
			Class c = Class.forName(cn);
			if (bioera.processing.Propertable.class.isAssignableFrom(c)) {
				//System.out.println("yes class=" + cn);
				processPropertableClass(c, cn);
			} else {
				//System.out.println("not class=" + cn);
			}			
		} catch (Throwable e) {
		}
	}

	out.close();		
}
/**
 * Debugger constructor comment.
 */
public ArrayList getProperties(String className) throws Exception {
	return (ArrayList) classes.get(className);
}
/**
 * Debugger constructor comment.
 */
public void load(File file) throws Exception {
	BufferedReader in = new BufferedReader(new FileReader(file));

	classes.clear();
	ArrayList oneClass = null;
	
	String line = in.readLine();
	while (line != null && line.length() > 0 && !line.startsWith(";")) {
		if (line.startsWith(" ")) {
			oneClass.add(line.trim());
		} else {
			// Class
			oneClass = new ArrayList();
			classes.put(line.trim(), oneClass);
		}
		line = in.readLine();
	}
	in.close();		
}
/**
 * Debugger constructor comment.
 */
public static void main(String[] args) {
	try {
		System.out.println("started");
		new PropertableMapper().generate(new String[]{
			"-src",
			"c:\\projects\\eeg\\release\\bioera.jar",
			"-out",
			"c:\\projects\\eeg\\obfusc\\prop.map",			
		});
		System.out.println("finished");		
	} catch (Exception e) {
		e.printStackTrace();
	}
}
/**
 * Debugger constructor comment.
 */
private void processPropertableClass(Class c, String cname) throws Exception {
	out.write(cname + CRLF);
	Field fields[] = bioera.processing.ProcessingTools.getPublicFields(c);
	//System.out.println("c=" + cname + "  " + fields.length);	
	for (int i = 0; i < fields.length; i++){
		out.write("    " + fields[i].getName() + CRLF);
	}
}
}
