/* PatternRunner.java v 1.0.9   11/6/04 7:15 PM
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

import bioera.processing.*;
import bioera.graph.designer.*;
import bioera.fft.*;

public final class PatternRunner extends SingleElement {
	private BufferedVectorPipe in;
	private int buffer[][];
	private BufferedVectorPipe out;
	private int vMin, vMax, vSize;

	private final static String propertiesDescriptions[][] = {
	};	
	
	protected static boolean debug = bioera.Debugger.get("impl.pattern.runner");
public String getElementDescription() {
	return "Pattern recgnizer - runner";
}
/**
 */
public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}
public final void process() {
}
public void reinit()  throws Exception {
	// Not yet implemented
	disactivate("Not implemented yet");
	super.reinit();
}
public PatternRunner() {
	super();
	setName("Pattern-R");
	//inputs = new VectorPipe[1];
	//inputs[0] = in = new BufferedVectorPipe(this);
	//in.setName("IN");
	//buffer = in.getVBuffer();
	//outputs = new VectorPipe[1];
	//out = (BufferedVectorPipe) outputs[0];
	//out.setName("OUT");
}
}
