/* Version.java v 1.0.9   11/6/04 7:15 PM
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

import bioera.config.*;

public final class Version implements Configurable {
/**
 * Version constructor comment.
 */
public Version() {
	
}
/**
 * Version constructor comment.
 */
public static final String getReleaseDate() {
	return "November 2004";
}
/**
 * Version constructor comment.
 */
public static final String getVersion() {
	return "1.0.9";
}
/**
 * Version constructor comment.
 */
public static final String getYear() {
	return "" + java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2004 1:39:00 PM)
 */
public boolean load(XmlConfigSection section) throws java.lang.Exception {
	return false;
}
/**
 * Version constructor comment.
 */
public static void main(String msg[]) {
	if (msg.length >= 4) {
		if (msg[0].equals("-version")) {
			try {
				updateVersion(msg[1], msg[2], msg[3]);
			} catch (Exception e) {
				System.out.println("Version update failed");
				e.printStackTrace();
			}
		}
		return;
	}
}
/**
 * Version constructor comment.
 */
public final boolean save(XmlCreator config) throws Exception {
	XmlCreator settings = config.addSection("Version");
	settings.addTextValue("number", "" + getVersion());
	settings.addTextValue("date", "" + getReleaseDate());
	return true;
}
/**
 * Version constructor comment.
 */
public static final void updateVersion(String version, String date, String fname) throws Exception {
	java.io.File f = new java.io.File(fname);
	if (!f.exists()) {
		System.out.println("File " + fname + " not found");
		return;
	}

	byte b[] = bioera.Tools.readFile(f, 10000);
	String s = new String(b);
	s = bioera.Tools.changeSubstr(s, "return \"1.0\"", "return \"" + version + "\"");
	s = bioera.Tools.changeSubstr(s, "\"May 2004\"", "\""+date+"\"");
	bioera.Tools.writeFile(f, s.getBytes());
	System.out.println("Updated version " + version);
}
}
