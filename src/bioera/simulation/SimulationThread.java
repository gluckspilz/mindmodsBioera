/* SimulationThread.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.simulation;

import java.io.*;
import bioera.device.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class SimulationThread implements Runnable {
	private boolean shutdown = false;
	ElecGuruOutputStream out;
	int channelValue[] = new int[] { 1, 2, 3, 4, 5, 6};
	int time = 0;
	public final static double PI2 = 2 * Math.PI;
/**
 * SerialThread constructor comment.
 */
public SimulationThread(OutputStream iout) throws Exception {
	super();
	out = new ElecGuruOutputStream(iout);
}
public void process() throws Exception {
	time = (time + 1) % 256;

	double freq[] = {3.7, 6.5, 9.55, 13.5, 17.8, 20.5};
	
	//// Write only to channel 0
	//int v =  512;
	
	//for (int i = 0; i < freq.length; i++){
		//v += (512.0 * Math.sin(freq[i] * PI2 * time / 256) / (10));
	//}

	int i = (int) (System.currentTimeMillis() / 1000) % (freq.length * 4);
	i /= 4;
	int v =  512 + (int) (512.0 * Math.sin(freq[i] * PI2 * time / 256) / (10));
	

	
	if (v < 0 || v > 1023)
		throw new Exception("Simulation out of bounds error: " + v + " (t=" + time + ")");

	// Write only to channel 0		
	channelValue[0] = v;
		
	out.writePacket(channelValue);
}
public void run() {
	System.out.println("Started simulation thread");
	try {
		// Fill buffer quickly so that receiving part can start work immediately
		for (int i = 0; i < 1024; i++){
			process();
		}

		// Continue filling buffer with the same rate as serial device
		while (!shutdown) {
			process();
			Thread.sleep(4); // this will be about 256Hz
		}
	} catch (Exception e) {
		System.out.println("Simulation thread error: " + e);
	}
}
public void shutdown() {
	this.shutdown = true;		
}
}
