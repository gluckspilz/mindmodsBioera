/* StreamOutboundRunnable.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.threaded;

import java.io.*;
import java.util.*;


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class StreamOutboundRunnable extends StreamRunnable {
	InputStream blockingInputStream;
/**
 * OutboundRunnable constructor comment.
 */
public StreamOutboundRunnable(ThreadedStreamElement e) {
	super(e);
}
public void close() throws Exception {
	if (blockingInputStream != null) {
		blockingInputStream.close();
		blockingInputStream = null;
	}	
}
public void run() {
	// This method feeds local buffer only
	try {
		if (debug)
			System.out.println("Reading from blocking input stream");
		while (!shutdown) {
			int ch = blockingInputStream.read();
			if (ch == -1) {
				// Stream was closed, exit
				if (debug)
					System.out.println("Outbound stream closed in threaded element: '" + threadedStreamElement.getName() + "'");
				threadedStreamElement.outbound = null;
				threadedStreamElement = null;
				break;
			}
			synchronized (this) {
				buffer[position++] = ch;
				if (position == buffer.length) {
					System.out.println("Buffer (" + buffer.length + ") overloaded, data lost (StreamOutboundRunnable.run())");
					reset(position);
				}
			}
		}
	} catch (Exception e) {
		System.out.println("Exit. StreamOutboundRunnable thread read error: " + e);
	}
}
}
