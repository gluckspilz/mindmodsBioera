/* NetworkClient.java v 1.0.9   11/6/04 7:15 PM
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
public class NetworkClient extends AbstractSocketElement {
	public String host;

	private final static String propertiesDescriptions[][] = {
		{"host", "Host", ""},
		
	};
/**
 * Element constructor comment.
 */
public NetworkClient() {
	super();
	setName("N_Client");
}
/**
 * Element constructor comment.
 */
public void connect() throws Exception {
	// Connect
	if (debug)
		System.out.println("Connecting network client to " + host +":" + port);
	socket = new Socket(host, port);
	streamOut = socket.getOutputStream();
	streamIn = socket.getInputStream();
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Input/output stream is received/sent through network socket";
}
/**
 * Element constructor comment.
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
public final static void main(String s[]) throws Exception {
	StorageInputStream in = new StorageInputStream(new FileInputStream("c:\\projects\\eeg\\fsss"));
	SignalParameters p = new SignalParameters(null);
	//StorageFormat.debug = true;
	java.util.Date ok = new StorageFormat().initInput(in, p);

	if (ok == null)
		System.out.println("Initialization failed");
	int c = 0, ch;
	while ((ch = in.read2()) != -1) {
		c++;
		System.out.print((char) ch);
	}

	System.out.println("c=" + c + "\n\n" + ProcessingTools.propertiesToString(p));
}
/**
 * Element constructor comment.
 */
public void reinit() throws Exception {
	close();
	
	verifyDesignState(port != -1);
	verifyDesignState(!TOOLS.isNone(host));

	// Only check connection and close
	try { 
		socket = new Socket(host, port);
		socket.close();
	} catch (Exception e) {
		throw new Exception("Connection failed to " + host + ":" + port);
	}

	super.reinit();
}
/**
 * Element constructor comment.
 */
public void start() throws Exception {
	connect();
}
/**
 * Element constructor comment.
 */
public void stop() throws Exception {
	close();
}
}
