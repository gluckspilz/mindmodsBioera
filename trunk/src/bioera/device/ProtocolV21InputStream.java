/* ProtocolV21InputStream.java v 1.0.9   11/6/04 7:15 PM
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
public final class ProtocolV21InputStream extends ChannelDataStream {
	InputStream in;
	
	// Contains 10-bit samples
	int data[] = new int[6];
	int requestedInfoType;
	int requestedInfoValue;

	// Current packet counter
	int packetCounter = -1;

	protected static boolean debug = bioera.Debugger.get("device.protocol.V21.inputstream");
/**
 * ElecGuruDataInputStream constructor comment.
 */
public ProtocolV21InputStream(InputStream iin) {
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
 * Creation date: (12/12/2003 3:51:16 PM)
 * @return int
 */
public int getMaxAmplitude() {
	return 1024;
}
/**
 * Insert the method's description here.
 * Creation date: (12/12/2003 3:51:48 PM)
 * @return int
 */
public int getRequestedInfoType() {
	return requestedInfoType;
}
/**
 * Insert the method's description here.
 * Creation date: (12/12/2003 3:51:48 PM)
 * @return int
 */
public int getRequestedInfoValue() {
	return requestedInfoValue;
}
/**
 * Insert the method's description here.
 * Creation date: (12/12/2003 3:51:16 PM)
 * @return int
 */
public int getSampleRate() {
	return 256;
}
/**
 * ElecGuruDataInputStream constructor comment.
 */
public static void main(String args[]) throws Exception {
    try {
        System.out.println("Started");
        ProtocolV21InputStream in = new ProtocolV21InputStream(new FileInputStream("C:\\projects\\eeg\\elecguru.dat"));
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
	int pc;
	int up, down;
	int b, count = 15;

	if (packetCounter == -1) {
		b = synchronize();
	} else {
		b = in.read();
	}
		
	if (requestedInfoType != -1)
		requestedInfoType = -1;	
		
	// Read packet counter
	pc = b & 0xF;

	if (pc != (packetCounter + 1) % 16 ) {
		lostPackets ++;
		packetCounter = -1;
		if (debug)
			System.out.println("packet lost (" + 1 + ")");
		throw new IOException("packet lost (" + 1 + ")");
	}

	// Update packet counter
	packetCounter = pc;

	// Get packet length
	count = (b >> 4) & 7;
		
	// Read all channels
	for (int i = 0; i < count; i++){
		up = in.read();
		down = in.read();
		if (up >= 128 || down >= 128) {
			lostPackets ++;
			packetCounter = -1;
			if (debug)
				System.out.println("packet lost (" + 2 + ")");
			throw new IOException("packet lost (" + 2 + ")");
		}

		int channel = (up >> 4) & 7;
		if (channel < 6)
			data[channel] = ((up & 7) << 7) + down;
		else {
			requestedInfoType = up & 0xF;
			requestedInfoValue = down;
			//System.out.println("Requested data arrived: " + (up & 0xF) + " is " + down);	
		}
	}

	if (lostPackets >= 10) {
		IOException exc = new IOException("Lost " + lostPackets + " packets and " + lostBytes + " bytes");
		lostPackets = lostBytes = 0;			
		throw exc;
	}
}
/**
 * ElecGuruDataInputStream constructor comment.
 */
private int synchronize() throws IOException {
	int b, count = 15;

	// Search for start byte that is 128 or larger	
	while ((b = in.read()) < 128 && count-- > 0) {
		lostBytes++;
	}

	if (count <= 0)
		throw new IOException("Synchronization failed, packet not found (" + 1 + ")");			

	// This should be the remaining length of the packet
	count = 2 * ((b >> 4) & 7);

	if (count == 0) {
		// It is unlikely, but it can be empty packet, 
		// Next packet should be the same but with incremented counter, check it
		int c = ((b & 0xF) + 1) & 0xF;

		// Read next
		if ((b = in.read()) >= 128) {
			count = (b >> 4) & 7;
			int c1 = b & 0xF;
			if (count == 0 && (c1 == c)) {
				// ok
				// Get previous pocket counter				
				packetCounter = (c1 + 16 - 1) % 16; ;
				return b;
			} else {
				throw new IOException("Synchronization failed, Packet not found (" + 2 + ")");
			}
		} else {
			throw new IOException("Synchronization failed, Packet not found (" + 3 + ")");
		}
	}
	
	// Get previous pocket counter
	packetCounter = ((b & 0xF) + 16 - 1) % 16;
		
	return b;
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
}
