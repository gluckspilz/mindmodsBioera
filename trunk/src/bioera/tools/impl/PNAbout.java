/* PNAbout.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.tools.impl;

import java.io.*;
import bioera.processing.*;
import bioera.fft.*;
import bioera.graph.designer.*;
import bioera.properties.*;
import bioera.storage.*;
import bioera.*;
import bioera.config.*;
import bioera.tools.pn.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class PNAbout extends Programmer {
	private int buffer[] = new int[256];
	private int index;
	private final static String propertiesDescriptions[][] = {
	};

	protected static boolean debug = bioera.Debugger.get("tools.pn.pendant.About");
/**
 * Element constructor comment.
 */
public PNAbout() {
	super();
	setName("About");
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Pendant About";
}
/**
 * Element constructor comment.
 */
public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}
/**
 * Element constructor comment.
 */
public java.io.Reader getReader() throws java.lang.Exception {
	return new Reader() {
		boolean init = true;
		public void close() {
			init = true;
		}
		public int read(char t[], int ofs, int len) {
			if (init && len > 0) {
				init = false;
				t[0] = '@';
				return 1;
			} else {
				return -1;
			}
		}		
	};
}
/**
 * Element constructor comment.
 */
public void reinit() throws Exception {
	super.reinit();
}
/**
 * Element constructor comment.
 */
public void writeToReceivedOutput() throws Exception {
	int n = in.available();
	for (int i = 0; i < n; i++){
		if (inb[i] == '*') {
			outReceived.write('\n');
			outReceived.write('\n');
			outReceived.write(buffer, 0, index);
			//System.out.print("buffer:[");
			//for (int j = 0; j < index; j++){
				//if (buffer[j] == '\n' || buffer[j] == '\r')
					//System.out.print("_");
				//else
					//System.out.print("" + (char) buffer[j]);
			//}
			//System.out.println("]");
			index = 0;
		} else {
			buffer[index++] = inb[i];
			if (index == buffer.length) {
				outReceived.write(buffer, 0, index);
				index = 0;
			}
		}
	}

	//int index = sb.toString().indexOf("MinderLabs");

	//if (index != -1) {
		//outReceived.write('\n');
		//outReceived.write('\n');
		//for (int i = index; i < n; i++){
			//if (inb[i] != '*')
				//outReceived.write(inb[i]);			
		//}
	//} else {
		//for (int i = 0; i < n; i++){
			//if (inb[i] != '*')
				//outReceived.write(inb[i]);			
		//}
	//}
}
}
