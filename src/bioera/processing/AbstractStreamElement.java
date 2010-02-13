/* AbstractStreamElement.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.processing;

import java.io.*;
import java.net.*;
import bioera.processing.*;
import bioera.fft.*;
import bioera.graph.designer.*;
import bioera.storage.*;
import bioera.*;


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class AbstractStreamElement extends SingleElement {
	protected BufferedScalarPipe elementIn;
	protected int inb[];
	protected ScalarPipeDistributor elementOut;
	protected InputStream streamIn;
	protected OutputStream streamOut;

	protected int sInCounter = 0;
	protected boolean isInput, isOutput;

	protected static boolean debug = bioera.Debugger.get("abstractstream");
/**
 * Element constructor comment.
 */
public AbstractStreamElement() {
	super();
	setName("Stream element");
	if (getInputsCount() > 0) {
		elementIn = (BufferedScalarPipe) inputs[0];
		elementIn.setName("IN");
		inb = elementIn.getBuffer();
	}

	if (getOutputsCount() > 0) {
		elementOut = (ScalarPipeDistributor) outputs[0];
		elementOut.setName("OUT");
	}
}
/**
 * Element constructor comment.
 */
public void close() throws java.lang.Exception {
	if (streamOut != null) {
		streamOut.close();
		streamOut = null;
	}

	if (streamIn != null) {
		streamIn.close();
		streamIn = null;
	}
}
/**
 * Element constructor comment.
 */
public void flush() throws java.lang.Exception {
	streamOut.flush();
}
/**
 * Element constructor comment.
 */
public InputStream getInputStream() {
	return streamIn;
}
/**
 * Element constructor comment.
 */
public OutputStream getOutputStream() {
	return streamOut;
}
/**
 * Element constructor comment.
 */
public void process() throws java.lang.Exception {
	if (isInput && streamOut != null) {
		int n = elementIn.available();
		if (n > 0) {
			try {
//				System.out.print("ssent (");
				for (int i = 0; i < n; i++) {
					streamOut.write(inb[i]);
//					System.out.print("" + (char) inb[i]);
				}
//				System.out.println(")");
				streamOut.flush();
			} catch (Exception e) {
				System.out.println("exception: " + e);
				streamLost(e, true);
				return;
			}

			elementIn.purgeAll();
		}
	}

	if (isOutput && streamIn != null) {
		int n = Math.min(streamIn.available(), elementOut.minAvailableSpace() >> 1);
		if (n > 0) {
			sInCounter += n;
			int c;
			try {
//				System.out.print("srecv (");
				for (int i = 0; i < n; i++) {
//					c = streamIn.read();
//					elementOut.write(c);
//					System.out.print((char) c);
					elementOut.write(streamIn.read());
				}
//				System.out.println(")");
			} catch (Exception e) {
				streamLost(e, false);
				return;
			}
		}
	}
}
/**
 * Element constructor comment.
 */
public String receiveAvailableString() throws java.lang.Exception {
	int n = streamIn.available();
	byte b[] = new byte[n];
	streamIn.read(b);
	return new String(b);
}
/**
 * Element constructor comment.
 */
public String receiveStringLine(int timeout) throws java.lang.Exception {
	long time = System.currentTimeMillis();
	StringBuffer sb = new StringBuffer();
	int ch;
	while ((System.currentTimeMillis() - time) < timeout) {
		if (streamIn.available() > 0) {
			ch = streamIn.read();
			sb.append((char) ch);
			if (ch == '\n')
				return sb.toString();
		}
		Thread.sleep(1);
	}

	return null;
}
/**
 * Element constructor comment.
 */
public void reinit() throws java.lang.Exception {
	if (elementIn != null && elementIn.isConnected())
		isInput = true;

	if (elementOut != null && elementOut.isConnected())
		isOutput = true;

	super.reinit();
}
/**
 * Element constructor comment.
 */
public void sendString(String s) throws java.lang.Exception {
	streamOut.write(s.getBytes());
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
/**
 * Element constructor comment.
 */
public void streamLost(Exception e, boolean output) throws java.lang.Exception {
	close();
}
}
