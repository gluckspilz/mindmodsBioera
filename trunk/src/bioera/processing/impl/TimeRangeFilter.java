/* TimeRangeFilter.java v 1.0.9   11/6/04 7:15 PM
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
public final class TimeRangeFilter extends SingleElement implements TimeRangeFeature {
	public double startTime = 0;
	public double endTime = 0;

	private final static String propertiesDescriptions[][] = {
		{"startTime", "Start time [s]", ""},
		{"endTime", "End time [s]", ""},
	};	

	private ScalarPipeDistributor out;
	private BufferedScalarPipe in;
	private int inbuffer[];
	private int startIndex, endIndex;
	private int counter;

	protected static boolean debug = bioera.Debugger.get("impl.Scalar.range.filter");
/**
 * Element constructor comment.
 */
public TimeRangeFilter() {
	super();
	setName("Range filter");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	inbuffer = in.getBuffer();
	out = (ScalarPipeDistributor) outputs[0];
	out.setName("OUT");
	
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Writes to output only values that are within defined time range";
}
/**
 */
public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 11:27:12 AM)
 */
public double physicalEnd() {
	return endTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 11:27:12 AM)
 */
public double physicalStart() {
	return startTime;
}
/**
 * Element constructor comment.
 */
public final void process() throws Exception {
	int n = in.available();
	if (n == 0)
		return;

	if (counter >= endIndex) {
		in.purgeAll();
		return;
	}
		
	if (counter + n < startIndex) {
		counter += n;
		in.purgeAll();
		return;
	}

	if (counter < startIndex) {
		in.purge(startIndex - counter);
		n = n - (startIndex - counter);
		counter = startIndex;
	}	
	//System.out.println("counter1=" + counter);
	for (int i = 0; i < n && counter < endIndex; i++){
		out.write(inbuffer[i]);
		counter++;
	}
	//System.out.println("counter=" + counter);
	in.purgeAll();
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	startIndex = (int) Math.round(startTime * getSignalParameters().getSignalRate());
	if (endTime <= 0)
		endIndex = Integer.MAX_VALUE;
	else
		endIndex = (int) Math.round(endTime * getSignalParameters().getSignalRate());

	//System.out.println("sIndex=" + startIndex + " endIndex=" + endIndex);		
		
	super.reinit();
}
/**
 * Element constructor comment.
 */
public void start()  throws Exception {
	counter = 0;
}
}
