/* Counter.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.properties.*;
import bioera.storage.*;
import bioera.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class Counter extends SingleElement {
	public int timePeriod = 0;
	public ComboProperty function = new ComboProperty(new String[] {
		"SAMPLES",
		"INVOCATION"
		});

	private final static String propertiesDescriptions[][] = {
		{"function", "Count", ""},
		{"timePeriod", "Time period [s]", ""},
		
	};	

	
	private int counters[][];  // {timeDifferenceFromStart, count}
	private int timePeriodMillis;
	
	private final static int SAMPLE = 0;
	private final static int INVOCATION = 1;
	private final static int RISING_EDGE = 2;
	private final static int FALLING_EDGE = 3;

	private int type = SAMPLE;
		
	private ScalarPipeDistributor out;
	private BufferedScalarPipe in;
	private int inbuffer[];

	private int totalCounter = 0;
	
	protected static boolean debug = bioera.Debugger.get("impl.Scalar.counter");
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Counts occurences according to chosen function";
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
	int n;
	if (type == SAMPLE) {
		n = in.available();
		if (n <= 0)
			return;
	} else if (type == INVOCATION) {
		n = 1;
	} else {
		return;
	}

	if (timePeriod == 0) {
		totalCounter += n;
		out.write(totalCounter);
	} else {
		int now = (int) (mainProcessingTime - mainStartTime);
		int elapsed = now - timePeriodMillis;
		int c = n;
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

		// Set new time and counter
		for (int i = 0; i < counters.length; i++){
			if (counters[i][0] == 0) {
				counters[i][0] = now;
				counters[i][1] = n;
				break;
			}
		}

		out.write(c);
	}
		
	in.purgeAll();
}
/**
 * Element constructor comment.
 */
public void reinit() throws Exception {
	if (function.getSelectedIndex() == -1)
		function.setSelectedIndex(type);
	else
		type = function.getSelectedIndex();

	timePeriodMillis = timePeriod * 1000;

	// Allocate buffer for timers
	counters = new int[timePeriodMillis / DesignSettings.sleepTimeMillis][2];
	for (int i = 0; i < counters.length; i++){
		counters[i][0] = 0;
	}
	
	super.reinit();
}
/**
 * Element constructor comment.
 */
public void start()  throws Exception {
	totalCounter = 0;
	in.purgeAll();
}

/**
 * Element constructor comment.
 */
public Counter() {
	super();
	setName("Counter");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	inbuffer = in.getBuffer();
	out = (ScalarPipeDistributor) outputs[0];
	out.setName("OUT");
	
}
}
