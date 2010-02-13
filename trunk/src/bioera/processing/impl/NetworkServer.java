/* NetworkServer.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.processing.impl;

import java.io.*;
import java.net.*;
import bioera.processing.*;
import bioera.fft.*;
import bioera.graph.designer.*;
import bioera.storage.*;
import bioera.*;
import bioera.net.*;


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class NetworkServer extends AbstractSocketElement implements Runnable {
	public int restartTimeout = 20000;
	
	private final static String propertiesDescriptions[][] = {
		{"restartTimeout", "Restart timeout [ms]", ""},
	};	

	private Thread thread = null;
	private ServerSocket sv;
	private boolean shutdown = false;
	long lastActiveTime = 0;

	protected static boolean debug = bioera.Debugger.get("impl.network.server");	
/**
 * Element constructor comment.
 */
public NetworkServer() {
	super();
	setName("N_Server");
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Input/output stream is received/sent through socket";
}
/**
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
public final void process() throws Exception {
	// Check timeout for stream from socket
	if (streamIn != null) {
		int n = streamIn.available();
		if (n == 0) {
			// Check timeout
			if (restartTimeout > 0 && mainProcessingTime - lastActiveTime > restartTimeout) {
				System.out.println("Socket server timeout elapsed, restarting " + new java.util.Date());
				stop();
				start();
				return;
			}
		} else {
			lastActiveTime = mainProcessingTime;
		}
	}


	if (isInput && streamOut == null) {
		elementIn.purgeAll();
	}	

	super.process();
}
/**
 * Element constructor comment.
 */
public void reinit() throws Exception {
	verifyDesignState(port != -1);
	
	//System.out.println("reinitng socket server");
		
	super.reinit();
}
/**
 * Element constructor comment.
 */
public final void run() {
	try {
		if (debug)
			System.out.println("SServer: Waiting for connection");
		// Close in case previous connection still existed
		close();
		if (sv == null)
			sv = new ServerSocket(port);
		Socket s = sv.accept();
		if (debug)			
			System.out.println("SServer: Connected " + new java.util.Date());
		socket = s;
		if (elementIn.isConnected()) {			
			streamOut = socket.getOutputStream();
			if (debug)
				System.out.println("Initialized socket server input");
		}
		if (elementOut.isConnected()) {
			streamIn = socket.getInputStream();
			if (debug)
				System.out.println("Initialized socket server output");
		}
	} catch (Exception e) {
		if (debug)
			System.out.println("ServerSocket error: " + e);
	}
}
/**
 * Element constructor comment.
 */
public final void start() {
	lastActiveTime = Long.MAX_VALUE;

	thread = new Thread(this);
	thread.start();	
}
/**
 * Element constructor comment.
 */
public final void stop() throws Exception {
	close();

	if (thread != null) {
		thread.interrupt();
	}
	
	sv.close();
	sv = null;	
}
}
