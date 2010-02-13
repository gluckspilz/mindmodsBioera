/* ThreadedStreamElement.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.processing.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class ThreadedStreamElement extends SingleElement {
	// From restricted source to pipe
	StreamOutboundRunnable outbound;

	// From pipe to restricted source
	StreamInboundRunnable inbound;

	private ScalarPipeDistributor out;
	private BufferedScalarPipe in;
	private int inbuf[];

	protected static boolean debug = bioera.Debugger.get("threaded.stream");
/**
 * ThreadedStreamSource constructor comment.
 * @param s bioera.processing.SignalParameters
 */
public ThreadedStreamElement() {
	super();
	out = (ScalarPipeDistributor) outputs[0];
	in = (BufferedScalarPipe) inputs[0];
	inbuf = in.getBuffer();
}
/**
 * Element constructor comment.
 */
public void close() throws Exception {
	if (inbound != null) {
		inbound.close();
		inbound = null;
	}
	if (outbound != null) {
		outbound.close();
		outbound = null;
	}
}
/**
 * Element constructor comment.
 */
public void process() throws Exception {
	int n, i;

	// Process Outbound
	if (outbound != null) {
		n = outbound.position;
		if (n > 0) {
			for (i = 0; i < n; i++){
				out.write(outbound.buffer[i]);
			}
			outbound.reset(n);
		}
	}

	// Process Inbound
	if (inbound != null) {
		n = in.available();
		if (n > 0) {
			for (i = 0; i < n; i++){
				inbound.feed(inbuf[i]);
			}
			in.purge(n);
		}
	} else {
		// looks like blocking input stream is not connected
		// Lets clear the buffer so that there is no messages
		in.purgeAll();
	}
}
/**
 * Element constructor comment.
 */
public void setBlockingInputStream(InputStream in) {
	if (outbound == null)
		outbound = new StreamOutboundRunnable(this);
	outbound.blockingInputStream = in;
}
/**
 * Element constructor comment.
 */
public void setBlockingOutputStream(OutputStream out) {
	if (inbound == null)
		inbound = new StreamInboundRunnable(this);
	inbound.blockingOutputStream = out;
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
/**
 * Element constructor comment.
 */
public void start() {
	if (inbound != null)
		inbound.start();
	else if (debug)
		System.out.println("ThreadedInputStream not set");

	if (outbound != null)
		outbound.start();
	else if (debug)
		System.out.println("ThreadedOutputStream not set");
}
/**
 * Element constructor comment.
 */
public void stop() {

	if (outbound != null)
		outbound.stop();
	if (inbound != null)
		inbound.stop();
}
}
