/* FileDesc.java v 1.0.9   11/6/04 7:15 PM
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class FileDesc {
	boolean recurse = false;
	boolean cleanOnly = false;
	String srcDir;
	String comment;
	String version;

	int numberOfModifiedFiles = 0;

	protected static boolean debug = Debugger.get("file.desc");
/**
 * Generic constructor comment.
 */
public FileDesc() throws Exception {

}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2000 3:32:02 PM)
 */
private String className(String javaName) throws Exception {
	String name = javaName;
	name = name.substring(0, name.length() - ".java".length());
	return name + ".class";
}
/**
 * Generic constructor comment.
 */
private static void createFolder(File folder) throws Exception {
	if (!folder.mkdir())
		throw new Exception("Couldn't create folder '" + folder + "'");
}
/**
 * Generic constructor comment.
 */
private static void deleteFile(File file) throws Exception {
	if (!file.delete())
		throw new Exception("Couldn't delete file '" + file + "'");
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2000 3:32:02 PM)
 */
private boolean isEmpty(String s) throws Exception {
	return s == null || s.trim().length() == 0;
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2000 3:32:02 PM)
 */
private String javaName(String className) throws Exception {
	String name = className;
	name = name.substring(0, name.length() - ".class".length());
	return name + ".java";
}
/**
 * Insert the method's description here.
 * Creation date: (1/11/2000 8:46:20 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		new FileDesc().start(args);
	} catch (Exception e) {
		e.printStackTrace();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2000 3:32:02 PM)
 */
private void process(File srcFolder) throws Exception {
	File list[] = srcFolder.listFiles();
	if (list != null) {
		for (int i = 0; i < list.length; i++){
			File f = list[i];
			if (f.isFile()) {
				// Only .java files
				if (!f.getName().endsWith(".java")) {
					if (debug)
						System.out.println("Not a java file " + f);
					continue;
				}

				updateFile(f);
			} else {
				if (recurse)
					process(new File(srcFolder, f.getName()));
			}
		}
	}
}
/**
 * Generic constructor comment.
 */
private final static boolean purgeDirectoryFilesSub(String dir) {
	boolean ret = true;
	File d = new File(dir);
	String files[] = d.list();
	for (int i=0; i<files.length; i++) {
		File f = new File(dir, files[i]);
		if (f.isDirectory())
			purgeDirectoryFilesSub(f.toString());
		else // delete only files, not directories
			if (!f.delete()) {
				System.out.println("couldn't remove " + files[i]);
				ret = false;
			}
	}
	return ret;
}
/**
 * Generic constructor comment.
 */
private final static void purgeDirectoryFilesSubThrow(String dir) throws Exception {
	boolean ret = true;
	File d = new File(dir);
	String files[] = d.list();
	for (int i=0; i<files.length; i++) {
		File f = new File(dir, files[i]);
		if (f.isDirectory()) {
			purgeDirectoryFilesSub(f.toString());
		}
		
		if (!f.delete()) {
			throw new Exception("Couldn't remove " + files[i]);
		}
	}
}
/**
 * Generic constructor comment.
 */
public final static String readFile(File f) throws Exception {
	if (f.exists()) {
		byte b[] = new byte[(int) f.length()];
		InputStream in = new FileInputStream(f);
		in.read(b);
		in.close();
		return new String(b);
	} else {
		throw new Exception("File not found " + f);
	}
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
/**
 * Generic constructor comment.
 */
public void start(String args[]) throws Exception {
	//System.out.println("len=" + args.length);
	for (int i = 0; i < args.length; i++){
		String arg = args[i];
		//System.out.println("arg["+i+"]=" + arg);
		if ("-recurse".equals(arg)) {
			this.recurse = true;
		} else if ("-clean_only".equals(arg)) {
			cleanOnly = true;
		} else if ("-version".equals(arg)) {
			version = args[++i];
		} else if ("-src".equals(arg)) {
			srcDir = args[++i];
		} else if ("-comment".equals(arg)) {
			File f = new File(args[++i]);
			if (debug)
				System.out.println("reading comment from '" + f + "'");
			comment = readFile(f);
		} else {
			System.out.println("Unknown parameter: " + arg);
			return;
		}
	}

	if (srcDir == null) {
		System.out.println("folders not set");
		return;
	}

	if (version == null) {
		System.out.println("Version not set");
		return;
	}		
		
	process(new File(srcDir));

	if (numberOfModifiedFiles > 0)
		System.out.println("Number of modified files: " + numberOfModifiedFiles);
		
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2000 3:32:02 PM)
 */
private void updateFile(File f) throws Exception {
	if (debug) 
		System.out.println("Updating file " + f);
	
	File bak = new File(f.getAbsolutePath() + ".bak");
	Tools.copyFile(f, bak);
	
	BufferedReader in = new BufferedReader(new FileReader(bak));
	BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));;

	try {
		// First clean previous comment
		boolean classFound = false;
		String searchName = f.getName();
		searchName = "class " + searchName.substring(0, searchName.length() - ".java".length());
		//System.out.println("sn='" + searchName + "'");
		String searchName1 = "import java";
		String line = in.readLine();
		StringBuffer sb = new StringBuffer();
		while (line != null) {
			//System.out.println("line=" + line);
			if (line.indexOf(searchName) != -1 || line.indexOf(searchName1) != -1) {
				classFound = true;
				//System.out.println("found");
			}
				
			if (!classFound && line.indexOf("/*") != -1) {
				if (debug)
					System.out.println("Cleaning comment in " + f);
				// Read and skip comment
				while ((line = in.readLine()) != null) {
					if (line.indexOf("*/") != -1) {
						line = in.readLine();
						break;					
					}
				}
				if (line == null)
					throw new Exception("EOF not found in " + f);

				if (line != null && line.trim().length() == 0) {
					// Skip empty line just after comment
					line = in.readLine();
				}
					
				continue;
			}
			
			sb.append(line + "\r\n");
			line = in.readLine();
		}

		// Now write to the file
		if (!cleanOnly)
			writeComment(f.getName(), out);
		out.write(sb.toString().getBytes());		
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
 * Generic constructor comment.
 */
public final void writeComment(String filename, OutputStream out) throws Exception {
	if (comment == null) {
		System.out.println("No comment");
		return;
	}

	String s = "/* " + filename + " v "+version+"   " + Tools.dateTimeFormatter().format(new Date()) + "\r\n";
	out.write(s.getBytes());
	out.write(comment.getBytes());
}
}
