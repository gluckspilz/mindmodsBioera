/* RateLimiter.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.processing.*;
import bioera.fft.*;
import bioera.graph.designer.*;
import bioera.storage.*;
import bioera.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class RateLimiter extends SingleElement {
	public int samplesNumber = 1;
	public int timeFrameMillis = 1000;
	public boolean cleanExcessiveData = true;

	private final static String propertiesDescriptions[][] = {
		{"cleanExcessiveData", "Clean excessive data in input buffer", ""},
		{"samplesNumber", "Number of samples", ""},
		{"timeFrameMillis", "Time range [ms]", ""},
		
	};	

	private int counters[][];  // {timeDifferenceFromStart, count}	
		
	private ScalarPipeDistributor out;
	private BufferedScalarPipe in;
	private int inbuffer[];

	protected static boolean debug = bioera.Debugger.get("impl.Scalar.rate.limiter");
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Limits signal rate up to specified value, unless 0";
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
public final void process() {
	int n = in.available();

	if (n == 0)
		return;

	int elapsed = (int) (mainTimeFromStart - timeFrameMillis);
	int c = 0;
	int t;

	// Clear elapsed times
	for (int i = 0; i < counters.length; i++){
		t = counters[i][0];
		if (t != 0) {
			if (t <= elapsed) {
				counters[i][0] = 0;
			} else {
				c += counters[i][1];
			}
		}
	}

	// Check for maximum rate
	if (c + n > samplesNumber)
		n = samplesNumber - c;
	
	// Set new time and counter
	for (int i = 0; i < counters.length; i++){
		if (counters[i][0] == 0) {
			counters[i][0] = (int) mainTimeFromStart;
			counters[i][1] = n;
			break;
		}
	}
	//System.out.println("3 " + n + "  " + inbuffer.length);
	out.write(inbuffer, 0, n);
	//System.out.println("wrote " + n);

	if (cleanExcessiveData)	
		in.purgeAll();
	else
		in.purge(n);
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	verifyDesignState(samplesNumber > 0 && timeFrameMillis > 0);

	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		reinited = true;
		return;
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}

	
	// Allocate buffer for timers
	counters = new int[timeFrameMillis / DesignSettings.sleepTimeMillis][2];
	for (int i = 0; i < counters.length; i++){
		counters[i][0] = 0;
	}

	setOutputSignalRate(samplesNumber * 1000 / timeFrameMillis);

	//in.setBufferSize(100);
	//inbuffer = in.getBuffer();

	
	super.reinit();
}
/**
 * Element constructor comment.
 */
public void start()  throws Exception {
	in.purgeAll();
}

/**
 * Element constructor comment.
 */
public RateLimiter() {
	super();
	setName("Rate limit");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	inbuffer = in.getBuffer();
	out = (ScalarPipeDistributor) outputs[0];
	out.setName("OUT");
	
}

/**
 * Element constructor comment.
 */
public final void process1() {
	int n = in.available();

	if (n == 0)
		return;

	int elapsed = (int) (mainTimeFromStart - timeFrameMillis);
	int c = 0;
	int t;
	
	// Clear elapsed times
	for (int i = 0; i < counters.length; i++){
		t = counters[i][0];
		if (t != 0) {
			if (t <= elapsed) {
				counters[i][0] = 0;
			} else {
				c += counters[i][1];
			}
		}
	}

	// Check for maximum rate
	if (c + n > samplesNumber)
		n = samplesNumber - c;
	
	// Set new time and counter
	for (int i = 0; i < counters.length; i++){
		if (counters[i][0] == 0) {
			counters[i][0] = (int) mainTimeFromStart;
			counters[i][1] = n;
			break;
		}
	}

	out.write(inbuffer, 0, n);

	if (cleanExcessiveData)	
		in.purgeAll();
	else
		in.purge(n);
}
}
