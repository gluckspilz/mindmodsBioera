/* VectorDebugger.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.diagnostic;

import java.io.*;
import bioera.processing.*;
import bioera.fft.*;
import bioera.graph.designer.*;
import bioera.storage.*;
import bioera.properties.*;
import bioera.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class VectorDebugger extends SingleElement {
	public int printPeriod = 2;
	public boolean printAscii;

	private long lastTime = 1;
	
	int b[][];
	int counter;

	private final static String propertiesDescriptions[][] = {
		{"printPeriod", "Print period [s]", ""},				
	};
	
	private ScalarPipeDistributor out;
	private BufferedVectorPipe in;
	
	protected static boolean debug = bioera.Debugger.get("vector.debugger");
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
	int n = in.available();
	if (n == 0)
		return;

	if (mainProcessingTime - lastTime > (printPeriod * 1000)) {
		lastTime = mainProcessingTime;
		System.out.println(getName() + ": " + ProcessingTools.arrayToString(b[n-1]));
		in.purgeAll();
	}
}
/**
 * Element constructor comment.
 */
public void reinit() throws Exception {
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		disactivate("Not connected");
		reinited = true;
		return;
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}
	
	PipeDistributor pd = getFirstDistributorConnectedToInput(0);
	if (pd == null || !(pd instanceof VectorPipe)) {
		throw new Exception("Element not connected properly");
	}
	
	VectorPipe vp = (VectorPipe) pd;

	System.out.println("-------------------------------------------");
	ProcessingTools.printSignalParameters(predecessorElement);
	//System.out.println("--- debug info about predecessor " + predecessorElement.getName() + " ----");
	//System.out.println("Vector max : " + vp.getSignalParameters().getVectorMax());
	//System.out.println("Vector min : " + vp.getSignalParameters().getVectorMin());
	//System.out.println("Vector length: " + vp.getSignalParameters().getVectorLength());
	//System.out.println("Vector length: " + vp.getSignalParameters().getVectorLength());
	//System.out.println("rate         : " + predecessorElement.getSignalParameters().getSignalRate());
	//System.out.println("digi range   : " + predecessorElement.getSignalParameters().getDigitalRange());
	//System.out.println("phys range   : " + predecessorElement.getSignalParameters().getPhysRange());
	//System.out.println("bits         : " + predecessorElement.getSignalParameters().getSignalResolutionBits());
	System.out.println("-------------------------------------------");
	
	
	super.reinit();
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}

/**
 * Element constructor comment.
 */
public VectorDebugger() {
	super();
	setName("V_Debugger");
	inputs = new BufferedVectorPipe[1];
	inputs[0] = in = new BufferedVectorPipe(this);
	b = in.getVBuffer();
	in.setName("IN");
	out = (ScalarPipeDistributor) outputs[0];
	out.setName("OUT");
	
}
}
