/* PosInputStream.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.sound;

import java.io.*;
import java.net.URL;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class PosInputStream extends BufferedInputStream{

public PosInputStream(InputStream in) throws IOException {
    super(in);
}
public int read2() throws IOException {
	return read() + (read() << 8);
}
public int read4() throws IOException {
	return read() 
		+ (read() << 8)
		+ (read() << 16)
		+ (read() << 24);
}
public long read8() throws IOException {
	long ret = read();
	ret += read() << 8;
	ret += read() << 16;
	ret += read() << 24;
	ret += read() << 32;
	ret += read() << 40;
	ret += read() << 48;
	ret += read() << 56;
	return ret;
}
public String readStr(int len) throws IOException {
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < len; i++){
		sb.append((char) read());
	}
	return sb.toString();
}
}
