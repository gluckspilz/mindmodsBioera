/* Timer.java v 1.0.9   11/6/04 7:15 PM
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
public final class Timer extends SingleElement {
	//public ComboProperty function = new ComboProperty(new String[] {
		//"SAMPLES",
		//});

	private final static String propertiesDescriptions[][] = {
//		{"function", "Count", ""},
		
	};	

	//private final static int SAMPLE = 0;
	//private final static int RISING_EDGE = 1;
	//private final static int FALLING_EDGE = 2;

	//private int type = SAMPLE;
		
	private ScalarPipeDistributor out;

	private long time = 0;
	private long startTime = 0;
	
	protected static boolean debug = bioera.Debugger.get("impl.timer");
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Calculates time, writes to output number of seconds since start";
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
	return 0;
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
	if (mainProcessingTime - time > 1000) {
		time = mainProcessingTime;
		out.write((int)((time - mainStartTime) / 1000));
	}
}
/**
 */
public void start()  throws Exception {
	time = 0;
}

/**
 * Element constructor comment.
 */
public Timer() {
	super();
	setName("Timer");
	out = (ScalarPipeDistributor) outputs[0];
	out.setName("OUT");
	
}
}
