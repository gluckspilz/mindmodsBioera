/* Installer.java v 1.0.9   11/6/04 7:15 PM
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

 import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Insert the type's description here.
 * Creation date: (6/20/2004 3:25:05 PM)
 * @author: Jarek
 */
public class Installer {
	protected static boolean debug = Debugger.get("installator");
/**
 * Installator constructor comment.
 */
public Installer() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (1/11/2000 8:46:20 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		new Installer().start(args);
	} catch (Exception e) {
		e.printStackTrace();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/11/2000 8:46:20 AM)
 * @param args java.lang.String[]
 */
public void start(String[] args) throws Exception {
	ZipEntry ze; byte buffer[] = new byte[128]; int n;
	String filename = args[0];
	
	// First read and create all directories
	if (debug)
		System.out.print("Deploying directories... ");
	ZipInputStream is = new ZipInputStream(new FileInputStream(filename));
	while ((ze = is.getNextEntry()) != null) {
		if (ze.isDirectory()) {
			String name = ze.getName();
			if (name.startsWith("bioera")) {
				File dir = new File(name);
				if (!dir.exists()) {
					if (!dir.mkdirs())
						throw new Exception("Couldn't create directory '" + dir + "'. Installation broken.");				
				}
			}
		}
	}
	is.close();
	if (debug)	
		System.out.println("done");
	
	// Now create files
	if (debug)	
		System.out.print("Deploying files... ");
	is = new ZipInputStream(new FileInputStream(filename));
	while ((ze = is.getNextEntry()) != null) {
		if (!ze.isDirectory()) {
			String name = ze.getName();
			if (name.startsWith("bioera")) {
				File file = new File(name);
				if (file.exists()) {
					if (debug)
						System.out.println("File '" + file + "' was overwritten");
				}
				FileOutputStream out = new FileOutputStream(file);
				while ((n = is.read(buffer, 0, 128)) != -1) {
					out.write(buffer, 0, n);
				}
				out.close();
			}
		}
	}
	
	is.close();
	if (debug)
		System.out.println("done");
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
}
