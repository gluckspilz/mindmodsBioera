/* NeuroServerSource.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.properties.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class NeuroServerSource extends NetworkClient {
	public int channel = 0;
	public int deviceNumber = -1;
	
	private ComboProperty device = new ComboProperty(new String[] {
	});

	private final static String propertiesDescriptions[][] = {
//		{"device", "Connect to", ""},
		{"channel", "Receive channel", ""},
		
	};		
		
	public final static int TIMEOUT = 1000;
	
	// Current packet counter
	private int packetCounter = -1;
	private int packetsLost = 0;
	private int processedPackets = 0;

	private StringBuffer buffer = new StringBuffer();

	protected static boolean debug = bioera.Debugger.get("impl.neuroserver.reader");
public String getElementDescription() {
	return "Translates stream from NeuroServer into data stream";
}
public int getInputsCount() {
	return 0;
}
public int getOutputsCount() {
	return 1;
}
public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}
public void initNeuroServer() throws Exception {
	if (debug)
		System.out.println("Connected to NeuroServer. Registering display");

	sendString("display\r\n");
	String s = receiveStringLine(TIMEOUT);
	if (s == null)
		throw new Exception("No response received after 'display'");
	if (!"200 OK".equals(s.trim()))
		throw new Exception("Received invalid response after 'display': " + s.trim());

	if (debug)
		System.out.println("Retrieving status at NeuroServer");
		
	sendString("status\r\n");
	s = receiveStringLine(TIMEOUT);
	if (s == null)
		throw new Exception("No response received after 'status'");
	if (!"200 OK".equals(s.trim()))
		throw new Exception("Received invalid response after 'status': " + s.trim());
		
	Vector v = new Vector(); int i;
	while ((s = receiveStringLine(1000)) != null) {
		//System.out.println("line: " + s);		
		if ((i = s.indexOf(":")) != -1) {
			// Ok, here is the device
			String d = s.trim();
			if (debug)
				System.out.println("NeuroServer device: " + d);

			if (!"display".equals(d.toLowerCase()))
				v.addElement(d);
		}
	}

	String t[] = new String[v.size()];
	for (i = 0; i < t.length; i++){
		t[i] = (String) v.elementAt(i);
	}

	device.setItems(t);

	if (debug)
		System.out.println("NeuroServerSource initialized");
}
public final void process() throws Exception {
	int n = streamIn.available();
	if (n < 20)
		return;

	buffer.append(receiveAvailableString());

	int len = buffer.length();

	start: for (int i = 0; i < len; i++){
		if (buffer.charAt(i) == '\n') {
			processLine(buffer.substring(0, i + 1));
			buffer.delete(0, i + 1);
			len = buffer.length();
			i = 0;
			continue start;
		}
	}

	if (buffer.length() > 200)
		throw new Exception("Invalid data from NeuroServer: " + buffer);
}
public final void processLine(String line) throws Exception {
	int i;
	if (line.charAt(0) != '!') {
		if (packetCounter != -1) {
			packetsLost ++;
			if (packetsLost % 10 == 0) {
				System.out.println("Packet lost from Neuroserver: " + packetsLost);
			}
		}
		if ((i = line.indexOf("!")) != -1) {
			line = line.substring(i);
		} else {
			return;
		}		
	}

	StringTokenizer st = new StringTokenizer(line.trim(), " ");
	st.nextToken(); // !
	st.nextToken(); // device number

	
	String s =  st.nextToken();
	if (s == null)
		throw new Exception("Bad line (1): " + line);
	try {
		i = Integer.parseInt(s);
	} catch (Exception e) {
		throw new Exception("Bad line (2): " + line);
	}
	
	if (i != packetCounter) {
		if (packetCounter != -1) {
			packetsLost ++;
			if (packetsLost % 10 == 0) {
				System.out.println("Packet lost from Neuroserver: " + packetsLost);
			}			
		}
	}
	packetCounter = (i + 1) % 256;

	s = st.nextToken();
	if (s == null)
		throw new Exception("Bad line (3): " + line);

	try {
		i = Integer.parseInt(s);
	} catch (Exception e) {
		throw new Exception("Bad line (4): " + line);
	}

	if (channel >= i) 
		throw new Exception("Channel " + channel + " not found in: " + line);

	// Skip other channels
	for (i = 0; i < channel; i++){
		s = st.nextToken();
		if (s == null)
			throw new Exception("Bad line (5): " + line);

		try {
			i = Integer.parseInt(s);
		} catch (Exception e) {
			throw new Exception("Bad line (6): " + line);
		}
	}

	s = st.nextToken();
	if (s == null)
		throw new Exception("Bad line (7): " + line);

	try {
		i = Integer.parseInt(s);
	} catch (Exception e) {
		throw new Exception("Bad line (8): " + line);
	}
	
	elementOut.write(i);
}
public void reinit() throws Exception {
	// Validate fields and check connections
	super.reinit();

	// Communicate with Neuroserver

	if (debug)
		System.out.println("Connecting to NeuroServer");

	connect();
	initNeuroServer();
	close();
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
public void start() throws Exception {
	super.start();

	if (debug)
		System.out.println("Starting NeuroServer reader");

	initNeuroServer();

	int d = deviceNumber;
	
	if (d == -1) {
		if (debug)
			System.out.println("Recognizing device on Neuroserver");
		
		String dev = null;
		for (int i = 0; i < device.getItems().length; i++){
			String item = device.getItem(i);
			if (debug)
				System.out.println("Device: " + item);			
			if (item.indexOf("EEG") != -1) {
				dev = item;
				break;
			}
		}

		if (dev == null)
			throw new Exception("No EEG device found on Neuroserver");
				
		int i = dev.indexOf(":");

		if (i == -1)
			throw new Exception("Invalid device name selected for NeuroServer: " + dev);

		try {
			d = Integer.parseInt(dev.substring(0, i));
		} catch (Exception e) {
			throw new Exception("Invalid device selected for NeuroServer: " + dev);
		}					
	}
			
		// Search for first available EEG channel
		
		//int i = device.getSelectedIndex();
		//if (i == -1)
			//throw new Exception("No device selected in NeuroServer");

		//String s = device.getItem(i);

		//if (s == null)
			//throw new Exception("No device selected for NeuroServer");

		//i = s.indexOf(":");

		//if (i == -1)
			//throw new Exception("Invalid device name selected for NeuroServer: " + s);

		//try {
			//i = Integer.parseInt(s.substring(0, i));
		//} catch (Exception e) {
			//throw new Exception("Invalid device selected for NeuroServer: " + s);
		//}

	if (debug)
		System.out.println("Sent watch " + d);
	
	sendString("watch " + d + "\r\n");
	flush();
	
	packetCounter = -1;
}

public NeuroServerSource() {
	super();

	setName("NS_Source");
	
	host = "localhost";
	port = 8336;
}
}
