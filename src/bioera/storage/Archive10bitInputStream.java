/* Archive10bitInputStream.java v 1.0.9   11/6/04 7:15 PM
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

/**
		This class knows about format of the data stream - decoding 4+1
		All returned values are 10-bit length.

		Packet specification:
		- packet length is 5 bytes
		- the first bit byte contains the least significant bits
		- two lowest 2 bits in the first bit byte are added to first output byte, and so on
		- next 4 bytes contains the most significant bits of output data

 */

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class Archive10bitInputStream extends InputStream {
	InputStream in;
	int buffer[] = new int[4];
	int position = 4;
/**
 * StorageDataStream constructor comment.
 */
public Archive10bitInputStream(InputStream iin) {
	super();
	in = iin;
}
/**
 * StorageDataStream constructor comment.
 */
public void close() throws IOException {
	if (in != null) {
		in.close();
		in = null;
	}
}
/**
 * StorageDataStream constructor comment.
 */
public int read() throws IOException {
	// If buffer is empty read from input stream and set position
	if (position == 4) {
		if (in.available() < 5)
			return -1;

		int lowest = in.read();
		for (int i = 0; i < 4; i++){
			buffer[i] = (in.read() << 2) + (lowest & 3);
			lowest >>= 2;
		}
		
		position = 0;
	}

	return buffer[position++];
}
}
