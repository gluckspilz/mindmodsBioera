/* StreamInboundRunnable.java v 1.0.9   11/6/04 7:15 PM
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
public class StreamInboundRunnable extends StreamRunnable {
	OutputStream blockingOutputStream;
/**
 * OutboundRunnable constructor comment.
 */
public StreamInboundRunnable(ThreadedStreamElement e) {
	super(e);
}
public void close() throws Exception {
	if (blockingOutputStream != null) {
		blockingOutputStream.close();
		blockingOutputStream = null;
	}		
}
public synchronized void feed(int value) {
	buffer[position++] = value;
	if (position == buffer.length) {
		System.out.println("StreamInboundRunnable feed buffer (" + buffer.length + ") overloaded, data was lost");
		reset(position);
	}
}
public void run() {
	try {
		while (!shutdown) {
			while (position == 0)
				Thread.sleep(100);
			for (int i = 0; i < position; i++){
				blockingOutputStream.write(buffer[i]);
			}
			reset(position);
		}
	} catch (java.lang.InterruptedException e) {
		// Stream was closed
	} catch (Exception e) {
		System.out.println("Exit. StreamInboundRunnable thread error: " + e);
	} finally {
		if (debug)
			System.out.println("Inbound stream closed in threaded element: '" + threadedStreamElement.getName() + "'");
		threadedStreamElement.inbound = null;
		threadedStreamElement = null;		
	}
}
}
