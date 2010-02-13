/* ActivityDetector.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.properties.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class ActivityDetector extends SingleElement {
	public int onLatency = 150;
	public int offLatency = 150;
	public ComboProperty initial = new ComboProperty(new String[] {"ON", "OFF"});
	
	private LogicalPipeDistributor out;
	private BufferedScalarPipe in;
	private int inb[];
	boolean isOff = true;
	long lastTime;

	private final static String propertiesDescriptions[][] = {
		{"onLatency", "ON latency [ms]", ""},
		{"offLatency", "OFF latency [ms]", ""},
		{"initial", "Initial state", ""},
	};
	
	protected static boolean debug = bioera.Debugger.get("impl.activitydetector");
/**
 * Element constructor comment.
 */
public ActivityDetector() {
	super();
	setName("Activity");
	inputs[0] = in = new BufferedScalarPipe(this);
	in.setName("IN");
	inb = in.getBuffer();
	
	outputs[0] = out = new LogicalPipeDistributor(this) ;
	out.setName("OUT");	
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Detects activity changes on input";
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

	if (n == 0 && isOff) {
		//System.out.println("reset n=0");			
		return;
	}
	
	
	if (n > 0 && !isOff) {
		lastTime = mainProcessingTime;
		in.purgeAll();
		//System.out.println("reset n>0");				
		return;
		
	}

	if (isOff) {
		if (mainProcessingTime - lastTime >= onLatency) {
			out.write(1);
			isOff = false;
			//System.out.println("sent " + last + "    " + n);		
			lastTime = mainProcessingTime;
		}		
	} else {
		if (mainProcessingTime - lastTime >= offLatency) {
			out.write(0);
			isOff = true;
			//System.out.println("sent " + last + "    " + n);		
			lastTime = mainProcessingTime;
		}		
		in.purgeAll();
	}
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	if (initial.getSelectedIndex() == -1)
		initial.setSelectedIndex(0);
	
	super.reinit();
}
/**
 * Element constructor comment.
 */
public void start()  throws Exception {
	if (initial.getSelectedIndex() == 0) {
		out.write(1);
	} else {
		out.write(0);		
	}
}
}
