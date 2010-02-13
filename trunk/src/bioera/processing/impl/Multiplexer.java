/* Multiplexer.java v 1.0.9   11/6/04 7:15 PM
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
import java.util.*;
import bioera.processing.*;
import bioera.graph.runtime.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class Multiplexer extends SingleElement {
	private final static String propertiesDescriptions[][] = {
	};	

	
	private static final int numberOfInputs = 3;

	private ScalarPipeDistributor out;
	BufferedScalarPipe in[];
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Writes to output stream data from inputs sequencially";
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
	return numberOfInputs;
}
/**
 * Element constructor comment.
 */
public int getOutputsCount() {
	return 1;
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
 * SignalDisplay constructor comment.
 */
private void initInputs() {
	inputs = in = new BufferedScalarPipe[numberOfInputs];
	for (int i = 0; i < numberOfInputs; i++){
		inputs[i] = in[i] = new BufferedScalarPipe(this);
		inputs[i].setName("" + i);
	}
}
/**
 * Element constructor comment.
 */
public final void process() {
	for (int i = 0; i < numberOfInputs; i++){
		int n = in[i].available();
		if (n > 0) {
			//System.out.println("arrived " + n + " data");
			out.write(in[i].getBuffer(), 0, n);
			in[i].purgeAll();
		}
	}	
}
/**
 * SignalDisplay constructor comment.
 */
public void reinit() throws Exception {
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		reinited = true;
		return;
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}

	super.reinit();
}

/**
 * SignalDisplay constructor comment.
 */
public Multiplexer() {
	super();
	setName("Multiplexer");
	initInputs();
	out = (ScalarPipeDistributor) outputs[0];
	outputs[0].setName("O");
	
}
}
