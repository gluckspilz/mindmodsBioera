/* StorageOutputStream.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.storage;

import java.io.*;
import bioera.processing.*;
import java.lang.reflect.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class StorageOutputStream {
	public final static char commentSeparator = '#';
	public final static String commentString = "" + (char) commentSeparator;	
	public final static char EOL = '\n';

	OutputStream out;
/**
 * Insert the method's description here.
 * Creation date: (9/20/2003 8:15:20 PM)
 */
public StorageOutputStream(OutputStream o) throws Exception {
	out = o;
}
/**
 * Insert the method's description here.
 * Creation date: (9/20/2003 8:15:20 PM)
 */
public void close() throws Exception{
	out.close();
}
/**
 * Insert the method's description here.
 * Creation date: (9/20/2003 8:15:20 PM)
 */
public void flush() throws Exception{
	out.flush();
}
/**
 * Insert the method's description here.
 * Creation date: (9/20/2003 8:15:20 PM)
 */
private void write(int b) throws Exception{
	if (b == commentSeparator) {
		out.write(commentSeparator);
		out.write(commentSeparator);
	} else {
		out.write(b);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/20/2003 8:15:20 PM)
 */
public final void write2(int b) throws Exception{
	b += 32768;
	if (b < 0)
		b = 0;
	write(b & 0xFF);
	write((b >> 8) & 0xFF);
	//System.out.println("orig=" + (b-32768) + " wrote=" + b + " 1=" + (b & 0xFF) + " 2=" + ((b >> 8) & 0xFF));
}
/**
 * Insert the method's description here.
 * Creation date: (9/20/2003 8:15:20 PM)
 */
public void writeComment(String s) throws Exception {
	if (s.indexOf(EOL) != -1 && s.startsWith(commentString))
		throw new Exception("bad line");

	out.write(commentSeparator);
	out.write(s.getBytes());
	out.write(EOL);
}

/**
 * Insert the method's description here.
 * Creation date: (9/20/2003 8:15:20 PM)
 */
public void writeString(String s) throws Exception {
	out.write(s.getBytes());
}
}
