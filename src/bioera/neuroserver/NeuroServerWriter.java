/* NeuroServerWriter.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.neuroserver;

import bioera.processing.*;
import bioera.processing.impl.*;
import bioera.fft.*;
import bioera.net.*;
import java.net.*;
import java.util.*;
import bioera.graph.designer.*;
import bioera.format.edf.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class NeuroServerWriter extends NetworkClient {
	private final static String propertiesDescriptions[][] = {
	};		
		
	public final static int TIMEOUT = 1000;

	// Current packet counter
	private int packetCounter = -1;
	private int processedPackets = 0;
	
	protected static boolean debug = bioera.Debugger.get("impl.neuroserver.writer");
public String getElementDescription() {
	return "Sends data stream to NeuroServer";
}
public int getInputsCount() {
	return 1;
}
public int getOutputsCount() {
	return 0;
}
public void initNeuroServer() throws Exception {
	if (debug)
		System.out.println("Connected to NeuroServer. Registering EEG");

	sendString("eeg\r\n");

	if (debug)
		System.out.println("Waiting for response");
	
	String s = receiveStringLine(TIMEOUT);
	if (s == null)
		throw new Exception("No response received after 'eeg'");
	if (!"200 OK".equals(s.trim()))
		throw new Exception("Received invalid response after 'eeg': " + s.trim());

	if (debug)
		System.out.println("Registering EDF header");		
	
	sendString("setheader " + new ModularEEGHeader(1).getString() + "\r\n");

	if (debug)
		System.out.println("Waiting for response");
	
	s = receiveStringLine(TIMEOUT);
	if (s == null)
		throw new Exception("No response received after 'setheader'");
	if (!"200 OK".equals(s.trim()))
		throw new Exception("Received invalid response after 'setheader': " + s.trim());
	
	if (debug)
		System.out.println("NeuroServerWriter initialized");
}
public final void process() throws Exception {
	
	int n = elementIn.available();
	if (n == 0)
		return;

	String line, s;
	for (int i = 0; i < n; i++){
		packetCounter = (packetCounter+1) % 256;
		//! <packetCounter> <channelCount> <sample[0]> <sample[1]> ... \n
		line = "! " + packetCounter + " 2 " + inb[i];
		sendString(line + "\r\n");
		flush();
		s = receiveAvailableString();
		if (s.indexOf("400") != -1)
			throw new Exception("Error at NeuroServerWriter: " + s);
		if (debug && (packetCounter == 1)) {
//			System.out.println("Processed " + processedPackets + "  " + line);
		}
	}
		
	processedPackets += n;
	
	elementIn.purgeAll();
}
public void reinit() throws Exception {
	// Validate fields and check connections
	super.reinit();

	// Communicate with Neuroserver

	if (debug)
		System.out.println("Retrieving status from NeuroServer");

	connect();
	initNeuroServer();
	close();
}
private final void searchPacketBegin() throws Exception {
	//// This the first call, lets find where packet begins
	//for (int i = 0; i < 17; i++){
		//if (buffer[i] == HEADER_1 && buffer[i + 1] == HEADER_2 && buffer[i + 2] == VERSION) {
			//// initalize packetCounter
			//in.purge(i);
			//packetCounter = buffer[3];
			//return;
		//}
	//}

	//// Header not found
	//String msg = "ModEEG P2 header not found in " + 17 + " bytes: ";
	//for (int i = 0; i < 18; i++){
		//msg += "" + buffer[i] + " ";
	//}
	//in.purgeAll();
	//throw new Exception(msg);
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
public void start() throws Exception {
	super.start();

	if (debug)
		System.out.println("Starting NeuroServer reader");

	initNeuroServer();
		
	packetCounter = 0;

	elementIn.purgeAll();
}

public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}

public NeuroServerWriter() {
	super();

	setName("NS_Source");

	host = "localhost";
	port = 8336;
}
}
