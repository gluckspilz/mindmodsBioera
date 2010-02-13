/* Valve.java v 1.0.9   11/6/04 7:15 PM
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
public final class Valve extends Element {
	private ScalarPipeDistributor out;
	private BufferedScalarPipe in;
	private BufferedScalarPipe tap;
	private int inb[], pG = 1, tapB[];

	private final static String propertiesDescriptions[][] = {
//		{"", "", ""},
	};
	
	protected static boolean debug = bioera.Debugger.get("impl.onoff");
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Limits signal rate up to specified value, unless 0";
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
	return 2;
}
/**
 * Element constructor comment.
 */
public int getOutputsCount() {
	return 1;
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
	if (in.isEmpty() && tap.isEmpty())
		return;

	int m = tap.available();
	if (m > 0) {
		pG = tapB[m-1];
		if (pG != 0)
			setStatusChar('O', 2, 1);
		else
			setStatusChar('C', 2, 0);
	}		
			
	int n = in.available();
	if (n == 0)
		return;

	if (pG != 0) {
		out.write(inb, 0, n);
	}
		
	in.purgeAll();
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	Element e = getFirstElementConnectedToInput(1);
	if (e == null)
		pG = 1;
				
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
public Valve() {
	super();
	setName("Valve");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	inb = in.getBuffer();
	
	inputs[1] = tap = new BufferedLogicalPipe(this);
	tap.setName("Tap");
	tapB = tap.getBuffer();
	
	out = (ScalarPipeDistributor) outputs[0];
	out.setName("OUT");
	
}
}
