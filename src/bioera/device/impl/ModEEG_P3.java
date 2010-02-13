/* ModEEG_P3.java v 1.0.9   11/6/04 7:15 PM
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

public final class ModEEG_P3 extends Element {
	private final static String propertiesDescriptions[][] = {
	};	
	
	public final static int VERSION = 3;
	private int pLen = 22;
	private int chNum = 6;

	public final static int SAMPLES_IN_SECOND = 256;
	public final static int MAX_AMPLITUDE = 1024;
	
	// Current packet counter
	private int packetCounter = -1;
	private int packetsLost = 0;
	private int processedPackets = 0;
	
	private BufferedScalarPipe in;
	private ScalarPipeDistributor out[];
	private int inb[];

	protected static boolean debug = bioera.Debugger.get("device.modeeg_p3");
public ModEEG_P3() {
	super();

	setName("ModEEG");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	in.setBufferSize(17*256*4);  // Keep up to 4 seconds of data

	inb = in.getBuffer();

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
	return "Translates stream from protocol P3 into data channels";
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
    if (n < pLen)
        return;

    //System.out.println("available " + n);			
    if (packetCounter == -1) {
        if (!searchPacketBegin())
            return;
    }

    int pCo;
    while (in.available() >= pLen) {
        pCo = (inb[0] >> 1) & 63;
		//System.out.println("pCo=" + pCo + " pLen=" + pLen + " available=" + in.available());        
        if ((inb[10] & 128) == 0 || packetCounter != pCo) {
            packetsLost++;

            // Debug what is wrong
            if (debug) {
                if (pCo != packetCounter)
                    System.out.println("ModEEG P3: counter is " + pCo + " should be " + packetCounter);
                if ((inb[10] & 128) == 0)
                    System.out.println("ModEEG P3: last byte " + Integer.toBinaryString(inb[10]) + " is not synchronized");
            }

            if (in.available() >= 22) {
	            if (!searchPacketBegin())
	                return;
            } else {
	            reset();
	            return;
            }

            continue;
        }

        processedPackets++;

        // Send data to connected channels
        for (int i = 0; i < chNum; i++) {
            if (out[i].isConnected()) {
				// Index	            
	            n = 2 + (i >> 1) * 3;

	            // Value
	            if ((i & 1) == 0)
		            n = inb[n] + ((inb[n + 2] & 0x70) << 3);
		        else
		            n = inb[n+1] + ((inb[n + 2] & 0x7) << 7);
                out[i].write(n - 512);
            }
        }

		// Send switches
        if (out[6].isConnected()) {
			if ((pCo & 7) == 4) {
				n = (inb[0] & 1) << 7 + (inb[1] & 127);
	            out[6].write(n);
			}
		}

        // Increase packet counter
        packetCounter = (packetCounter + 1) % 64;

        in.purge(pLen);
    }
}
public void reinit() throws Exception {
	packetCounter = -1;
		
	super.reinit();
}
public void reset() throws Exception {
	packetCounter = -1;
	pLen = 22;
}
private final boolean searchPacketBegin() throws Exception {
	// Find where the packet begins
	int len = Math.min(in.available() - 11, 11);	
	start: for (int i = 0; i < len; i++){
		if ((inb[i] & 128) != 0) {
			// Check if all remaining bytes have 0 on the highest bit
			for (int j = 1; j <= 10; j++){
				if ((inb[i+j] & 128) != 0) {					
					break start;
				}
			}

			// initalize packetCounter			
			in.purge(i + 1);
			packetCounter = (inb[0] >> 1) & 63;
			pLen = 11;

			// OK
			return true;
		}
	}

	System.out.println("ModEEG P3 synchr errors " + packetsLost);

	if (debug) {
		// Header not found
		String msg = "P3 packet not recognized within " + 11 + " bytes: ";
		for (int i = 0; i < 11; i++) {
			String s = Integer.toBinaryString(inb[i]);
			while (s.length() < 8)
				s = "0" + s;
			msg += s + " ";
		}
		System.out.println(msg);
	}
		
	packetCounter = -1;
	pLen = 22;
	in.purgeAll();

	return false;
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
public void start() throws Exception {
	reset();
}
}
