/* AbstractSocketElement.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.net;

import java.io.*;
import java.net.*;
import bioera.processing.*;
import bioera.fft.*;
import bioera.graph.designer.*;
import bioera.*;


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class AbstractSocketElement extends AbstractStreamElement {
	public int port = 9789;
	
	protected Socket socket;	
	
	protected static boolean debug = bioera.Debugger.get("net.abstractsocket");
/**
 * Element constructor comment.
 */
public void close() throws Exception {
	if (socket != null) {
		super.close();
		socket.close();
		socket = null;
		if (debug)
			System.out.println("Socket closed");
	}
}
/**
 * Element constructor comment.
 */
public final void streamLost(Exception e, boolean output) throws java.lang.Exception {
	if (debug)
		System.out.println("Socket stream lost in '" + getClass() + ", restarting");
	stop();
	start();
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}

/**
 * Element constructor comment.
 */
public AbstractSocketElement() {
	super();
	setName("Socket element");
}
}
