/* ModEEG_v21.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.device.impl;

import java.io.*;
import bioera.processing.*;
import bioera.fft.*;
import bioera.device.*;
import bioera.processing.impl.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class ModEEG_v21 extends Element {
	public final static int VERSION = 21;	

	private final static String propertiesDescriptions[][] = {
	};	

	
	// Current packet counter
	private int packetCounter = -1;
	private int packetsLost = -1;
	private int processedPackets = 0;
	
	private BufferedScalarPipe in;
	private ScalarPipeDistributor out[];
	private int buffer[];

	protected static boolean debug = bioera.Debugger.get("device.modeeg_v21");
/**
 * Element constructor comment.
 */
public ModEEG_v21() {
	super();

	setName("ModEEG");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	in.setBufferSize(17*256*4);  // Keep up to 4 seconds of data

	buffer = in.getBuffer();

	// 6 channels + switches
	outputs = out = new ScalarPipeDistributor[6];
	for (int i = 0; i < out.length; i++){
		out[i] = new ScalarPipeDistributor(this);
		out[i].setName("ch" + i);
	}
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Translates stream from protocol v21 into data channels";
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
	return 1;
}
/**
 * Element constructor comment.
 */
public int getOutputsCount() {
	return 6;
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
private final void handleLostPacket(String msg, int buffer[]) throws Exception {
	packetsLost++;
	
	// Debug what is wrong
	if (debug) {
		//if (packetsLost > 5) {
			//System.out.println("Too many invalid packets " + packetsLost);
			//setActive(false);
			//throw new Exception("Too many invalid packets " + packetsLost);
			////return;
		//}
		
		System.out.println("Package lost: (" + msg + "), total packets lost in modeeg1: " + packetsLost);
		if (buffer[0] < 128)
			System.out.println("First byte too small");
		if ((buffer[0] & 0xF) != packetCounter)
			System.out.println("Counter " + (buffer[0] & 0xF) + " != " + packetCounter);
		for (int k = 0; k < 17; k++){
			String s = Integer.toBinaryString(buffer[k]);
			while (s.length() < 8)
				s = "0" + s;
			s = s + "  " + Integer.toHexString(buffer[k]);
			System.out.println(" " + s);
		}				
		System.out.println(" ** ok=" + processedPackets);			
	}
	
	System.out.println("total packets lost in modeeg1: " + packetsLost);		
}
/**
 * Element constructor comment.
 */
public final void process() throws Exception {
	int n = in.available();
	if (n < 20)
		return;
		
	//System.out.println("available " + n);
	if (packetCounter == -1) {
		searchPackageBegin();
	}

	start: while (in.available() > 15) {
		// Validate headers
		if (buffer[0] < 128 || (buffer[0] & 0xF) != packetCounter) {
			handleLostPacket("First byte invalid", buffer);
			searchPackageBegin();
			continue start;
		}			

		int channelsCount = ((buffer[0] >> 4) & 7), channel, value;
		
		// Read, validate data and send to connected channels
		for (int i = 0; i < channelsCount; i++){
			n = buffer[1 + (i << 1)];
			value = buffer[2 + (i << 1)];
			if (n >= 128 || value >= 128) {
				handleLostPacket("Data bytes invalid", buffer);
				searchPackageBegin();
				continue start;
			}

			channel = (n >> 4) & 7;			
			if (channel < 6) {
				//System.out.println("ch[" + channel + "]=" + (((n & 3) << 8) | value));
				out[channel].write((((n&7) << 7) | value) - 512);
			} else {
				// This is requested data, do not process it here
			}
		}
		
		// Increase packet counter
		packetCounter = (packetCounter + 1) & 0xF;
		
		processedPackets++;
		
		in.purge((channelsCount << 1) | 1);
	}
}
/**
 * Element constructor comment.
 */
public void reinit() throws Exception {
	packetCounter = -1;

	Element element = getFirstElementConnectedToInput(0);
	if (element == null) {
		reinited = true;
		return;
	} else if (!element.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}

	if ((element instanceof SerialPort)) {
		OutputStream outputStream = ((SerialPort) element).getOutputStream();
		InputStream inputStream = ((SerialPort) element).getInputStream();

		ModEEGBackComm cmd = new ModEEGBackComm(outputStream, inputStream);
		cmd.cmdSetProtocol(21);
		if (debug)
			System.out.println("Protocol 21 set");
		int matrix = 0;
		for (int i = 0; i < 6; i++){
			if ((((PipeDistributor)outputs[i]).isConnected())) {
				matrix |= (1 << i);
			}
		}
		cmd.cmdSetChannelMatrix21(matrix);		
		if (debug)
			System.out.println("Matrix set to " + Integer.toBinaryString(matrix));
		int protocol = cmd.cmdGetProtocol();
		if (protocol != 21) {
			throw new bioera.graph.designer.DesignException("Couldn't properly initialize modularEEG (" + protocol + ")");
		}
	}
	
	super.reinit();
}
/**
 * Element constructor comment.
 */
private final void searchPackageBegin() throws Exception {
	// Lets find where packet begins
	for (int i = 1; i < 18; i++){
		if (buffer[i] >= 128) {
			// initalize packetCounter
			packetCounter = buffer[i] & 0xF;
			in.purge(i);
			return;
		}
	}

	// Header not found
	throw new Exception("ModEEG protocol (version 21) header not found in " + 17 + " bytes");
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
}
