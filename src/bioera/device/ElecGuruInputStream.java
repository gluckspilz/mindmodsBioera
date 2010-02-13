/* ElecGuruInputStream.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.device;

import java.io.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class ElecGuruInputStream extends ChannelDataStream {
	InputStream in;
	public final static int HEADER_1 = 0xa5; // decimal 165   binary 10100101
	public final static int HEADER_2 = 0x5a; // decimal  90   binary 01011010
	public final static int VERSION = 2;	

	public final static int SAMPLES_IN_SECOND = 256;
	public final static int MAX_AMPLITUDE = 1024;
	
	// Contains 10-bit samples
	int data[] = new int[6];

	// Switches
	int switches;

	// Current packet counter
	int packetCounter = -1;

	protected static boolean debug = bioera.Debugger.get("device.protocol.P2.inputstream");	
/**
 * ElecGuruDataInputStream constructor comment.
 */
public ElecGuruInputStream(InputStream iin) {
	super();
	in = iin;
}
/**
 * ElecGuruDataInputStream constructor comment.
 */
public int getChannelData(int channel) throws IOException {
	return data[channel];
}
/**
 * Insert the method's description here.
 * Creation date: (6/13/2003 10:22:42 AM)
 * @return int
 */
public int getMaxAmplitude() {
	return MAX_AMPLITUDE;
}
/**
 * Insert the method's description here.
 * Creation date: (6/13/2003 10:22:42 AM)
 * @return int
 */
public int getSampleRate() {
	return SAMPLES_IN_SECOND;
}
/**
 * ElecGuruDataInputStream constructor comment.
 */
public static void main(String args[]) throws Exception {
    try {
        System.out.println("Started");
//        SerialInputStream serial = new SerialInputStream();
        ElecGuruInputStream in = new ElecGuruInputStream(new FileInputStream("C:\\projects\\eeg\\elecguru.dat"));
//        ElecGuruInputStream in = new ElecGuruInputStream(serial);
        int sleepTime = 1;
        int t[] = new int[100];
        while (sleepTime < 1000) {
	        for (int i = 0; i < t.length; i++){
	        	in.readPacket();
	        	t[i] = in.packetCounter;
   	        	//Thread.sleep(sleepTime);	        
	        }
	        for (int i = 0; i < t.length; i++){
	        	System.out.print("" + t[i] + " ");
	        }
	        System.out.println();
	        System.out.println("lost packets " + in.getLostPackets());
        	sleepTime <<= 1;	        
        }
//		System.out.println("serial lost bytes " + serial.fifo.fullBuffer);
        System.out.println("finished");
    } catch (Exception e) {
        System.out.println("Error: " + e + "\n\n");
        e.printStackTrace();
    }
}
/**
 * ElecGuruDataInputStream constructor comment.
 */
public void readPacket() throws IOException {
	int localPacketCounter;
	int up, down;

	if (packetCounter == -1)
		searchForSynchronization();
	
	// Read next two bytes and validate them
	if (in.read() == HEADER_1 && in.read() == HEADER_2 && in.read() == VERSION) {
		// Read packet counter
		localPacketCounter = in.read();

		if (localPacketCounter != (packetCounter + 1) % 256) {
			lostPackets ++;
			packetCounter = -1;
			throw new IOException("packet lost ("+1+")");
		} else 
			packetCounter = localPacketCounter;
		
		// Read all 6 channels
		for (int i = 0; i < 6; i++){
			up = in.read();
			down = in.read();
			//System.out.println("up=" + up + ", down=" + down);
			data[i] = (up << 8) + down;
		}

		// Read switches
		switches = in.read();		
	} else {
		// Handle packet error
		lostPackets++;
		packetCounter = -1;
		throw new IOException("Synchronization Lost " + lostPackets + " packets and " + lostBytes + " bytes");
	}
}
/**
 * ElecGuruDataInputStream constructor comment.
 */
private void searchForSynchronization() throws IOException {
	// Search for first byte in packet
	int counter = 17;

	while (in.read() != HEADER_1 && counter-- > 0) {
		lostBytes++;
	}


	// Read next two bytes and validate them
	if (in.read() == HEADER_2 && in.read() == VERSION) {
		// Read packet counter
		packetCounter = in.read();

		// Read all 6 channels
		for (int i = 0; i < 6; i++){
			in.read();
			in.read();
		}

		// Read switches
		in.read();		
	} else {
		throw new IOException("Packet not found ("+1+")");
	}
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
}
