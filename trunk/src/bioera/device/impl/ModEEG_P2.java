/* ModEEG_P2.java v 1.0.9   11/6/04 7:15 PM
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

import bioera.processing.*;
import bioera.fft.*;

public final class ModEEG_P2 extends Element {
	private final static String propertiesDescriptions[][] = {
	};


	public final static int HEADER_1 = 0xa5; // decimal 165
	public final static int HEADER_2 = 0x5a; // decimal  90
	public final static int VERSION = 2;

	public final static int SAMPLES_IN_SECOND = 256;
	public final static int MAX_AMPLITUDE = 1024;

	// Current packet counter
	private int packetCounter = -1;
	private int packetsLost = 0;
	private int processedPackets = 0;

	private BufferedScalarPipe in;
	private ScalarPipeDistributor out[];
	private int buffer[];

	protected static boolean debug = bioera.Debugger.get("device.modeeg_p2");
public ModEEG_P2() {
	super();

	setName("ModEEG");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	in.setBufferSize(17*256*4);  // Keep up to 4 seconds of data

	buffer = in.getBuffer();

	// 6 channels + switches
	outputs = out = new ScalarPipeDistributor[7];
	for (int i = 0; i < out.length - 1; i++){
		out[i] = new ScalarPipeDistributor(this);
		out[i].setName("ch" + i);
	}

	out[6] = new ScalarPipeDistributor(this);
	out[6].setName("sw");
}
public String getElementDescription() {
	return "Translates stream from protocol P2 into data channels";
}
public int getInputsCount() {
	return 1;
}
public int getOutputsCount() {
	return 7;
}
/**
 */
public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}
public final void process() throws Exception {
	int n = in.available();
	if (n < 20)	// 17 + 3, so that packet counter is always available
		return;

	//System.out.println("available " + n);
	if (packetCounter == -1) {
		searchPacketBegin();
	}

	while (in.available() >= 20) {
		// Validate headers
		if (buffer[0] != HEADER_1
			|| buffer[1] != HEADER_2
			|| buffer[2] != VERSION
			|| buffer[3] != packetCounter
			) {
			packetsLost++;

			// Debug what is wrong
			if (debug) {
				if (buffer[0] != HEADER_1) System.out.println("modeeg: header1 " + buffer[0]);
				if (buffer[1] != HEADER_2) System.out.println("modeeg: header2 " + buffer[1]);
				if (buffer[2] != VERSION) System.out.println("modeeg: version " + buffer[2]);
				if (buffer[3] != packetCounter) System.out.println("modeeg: counter should be " + packetCounter);
				for (int k = 0; k < 17; k++){
					System.out.print(" " + buffer[k]);
				}
				System.out.println(" ** ok=" + processedPackets);
			}

			searchPacketBegin();

			//if (packetsLost % 1 == 0)
			System.out.println("Total frames lost in modeeg P2: " + packetsLost);

			continue;
		}

		processedPackets++;

		// Send data to connected channels
		for (int i = 0; i < 6; i++){
			if (out[i].isConnected()) {
				n = (buffer[4 + (i<<1)] << 8) + buffer[5 + (i<<1)];
				out[i].write(n - 512);
			}
		}

		// Send switches
		if (out[6].isConnected()) {
			out[6].write(buffer[16]);
		}

		// Increase packet counter
		packetCounter = (packetCounter + 1) % 256;

		in.purge(17);
	}
}
public void reinit() throws Exception {
	packetCounter = -1;

	super.reinit();
}
private final void searchPacketBegin() throws Exception {
	// This the first call, lets find where packet begins
	for (int i = 0; i < 17; i++){
		if (buffer[i] == HEADER_1 && buffer[i + 1] == HEADER_2 && buffer[i + 2] == VERSION) {
			// initalize packetCounter
			in.purge(i);
			packetCounter = buffer[3];
			return;
		}
	}

	// Header not found
	String msg = "ModEEG P2 header not found in " + 17 + " bytes: ";
	for (int i = 0; i < 18; i++){
		msg += "" + buffer[i] + " ";
	}
	in.purgeAll();
	throw new Exception(msg);
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
public void start() throws Exception {
	packetCounter = -1;
	in.purgeAll();
}
}
