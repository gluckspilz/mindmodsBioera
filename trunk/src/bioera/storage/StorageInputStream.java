/* StorageInputStream.java v 1.0.9   11/6/04 7:15 PM
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
public class StorageInputStream {
	public final static int commentSeparator = StorageOutputStream.commentSeparator;
	public final static String commentString = "" + (char) commentSeparator;
	public final static int EOL = StorageOutputStream.EOL;

	private String comment = null;	
	private int data = - 1;
	int buffer;
	
	InputStream in;
//	private boolean wroteData = false;	
/**
 * Insert the method's description here.
 * Creation date: (9/20/2003 8:15:20 PM)
 */
public StorageInputStream(InputStream i) throws Exception {
	in = i;
	buffer = in.read();
}
/**
 * Insert the method's description here.
 * Creation date: (9/20/2003 8:15:20 PM)
 */
public void close() throws Exception{
	in.close();
}
/**
 * Insert the method's description here.
 * Creation date: (9/20/2003 8:15:20 PM)
 */
public boolean isEof() throws Exception {
	return buffer == -1;
}
/**
 * Insert the method's description here.
 * Creation date: (9/20/2003 8:15:20 PM)
 */
protected final int read() throws Exception {
	int ret;
	if (data != -1) {
		ret = data;
		data = -1;
		return ret;
	}
	
	if (comment != null)
		return -1;

	if (buffer == commentSeparator) {
		comment = readComment();
		if (data != -1) {
			ret = data;
			data = -1;
			return ret;
		} else {
			return -1;
		}
	}

	ret = buffer;
	buffer = in.read();
	return ret;
}
/**
 * Insert the method's description here.
 * Creation date: (9/20/2003 8:15:20 PM)
 */
public int read2() throws Exception {
	int ret = read();
	if (ret == -1)
		return 0x80000000;
		
	int a = read();
	if (a == -1)
		return 0x80000000;
	else
		return ret + (a << 8) - 32768;			
}
/**
 * Insert the method's description here.
 * Creation date: (9/20/2003 8:15:20 PM)
 */
public String readComment() throws Exception {
	if (comment != null) {
		String ret = comment;
		comment = null;
		return ret;
	}

	if (buffer != commentSeparator)
		return null;
	
	
	int ch = in.read();
	if (ch == commentSeparator) {
		data = ch;
		buffer = in.read();
		return null;
	}
	
	StringBuffer b = new StringBuffer();
	while (ch != -1 && ch != EOL) {
		b.append((char)ch);
		ch = in.read();
	}

	buffer = in.read();
	return b.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (9/20/2003 8:15:20 PM)
 */
public String readLineEol() throws Exception {
	//System.out.println("class=" + getClass());
	//System.out.println("classIN=" + in.getClass());
	StringBuffer b = new StringBuffer();
	if (buffer != -1) {
		b.append((char) buffer);
		buffer = -1;
	}
	int ch;
	while ((ch = in.read()) != -1) {
		//if (ch == '\r')
			//System.out.print("(\\r)");
		//else if (ch == '\n')
			//System.out.print("(\\n)");
		//else
			//System.out.print("(" + (char) ch + ")");
		b.append((char)ch);
		if (ch == '\n')
			break;
	}

	if (b.length() == 0)
		return null;
	return b.toString();
}
}
