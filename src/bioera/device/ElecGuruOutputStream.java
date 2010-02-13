/* ElecGuruOutputStream.java v 1.0.9   11/6/04 7:15 PM
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
public final class ElecGuruOutputStream {
	OutputStream out;

	int buffer[] = new int[17];

	// Current packet counter
	int packetCounter = 1;
/**
 * ElecGuruDataInputStream constructor comment.
 */
public ElecGuruOutputStream(OutputStream iout) {
	super();
	out = iout;
	buffer[0] = ElecGuruInputStream.HEADER_1;
	buffer[1] = ElecGuruInputStream.HEADER_2;
	buffer[2] = ElecGuruInputStream.VERSION;
	buffer[16] = 16 + 4 + 1; // switches
	
}
/**
 * ElecGuruDataInputStream constructor comment.
 */
public static void main(String args[]) {
	try {
		System.out.println("Starting");
		OutputStream fout = new FileOutputStream("/projects/eeg/elecguru.dat");
		ElecGuruOutputStream s = new ElecGuruOutputStream(fout);
		int t[] = {254, 2, 4, 8, 512, 1011};
		for (int i = 0; i < 100; i++){
			s.writePacket(t);
			t[0] ++;
		}		
		fout.close();
		System.out.println("finsihed");
	} catch (Exception e) {
		e.printStackTrace();
	}
}
/**
 * ElecGuruDataInputStream constructor comment.
 */
public void writePacket(int channelvalues[]) throws IOException {
	packetCounter = (packetCounter + 1) % 256;
	buffer[3] = packetCounter;

	int n = Math.min(channelvalues.length, 6);
	
	for (int i = 0; i < n; i++){
		buffer[4 + i*2] = (channelvalues[i] >> 8);
		buffer[5 + i*2] = (channelvalues[i] & 255);		
	}	

	for (int i = 0; i < 17; i++){
		out.write(buffer[i]);
	}	
}
}
