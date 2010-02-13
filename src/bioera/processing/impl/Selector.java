/* Selector.java v 1.0.9   11/6/04 7:15 PM
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
public final class Selector extends Element {
	public boolean remember = true;
	public int initialSelection = 0;
	
	private final static String propertiesDescriptions[][] = {
		{"outputsNo", "Outputs number", ""},
		{"remember", "Remember last selection", ""},
		{"initialSelection", "Initial selection", ""},
	};

	private static final int DEFAULT = 0;
	
	private int type = 0;
	
	private LogicalPipeDistributor out[];
	private BufferedScalarPipe in;
	private int inb[], prev = -1;

	protected static boolean debug = bioera.Debugger.get("impl.selector");
/**
 * Element constructor comment.
 */
public Selector() {
	super();
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Selects only one of the logical outputs";
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
	return 1;
}
/**
 * Element constructor comment.
 */
public int getOutputsCount() {
	return 4;
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
public void initialize() {
	setName("Selector");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	inb = in.getBuffer();
	outputs = out = new LogicalPipeDistributor[getOutputsCount()];
	for (int i = 0; i < out.length; i++){
		outputs[i] = out[i] = new LogicalPipeDistributor(this);
		out[i].setName("" + i);			
	}
}
/**
 * Element constructor comment.
 */
public final void process() {
	if (in.isEmpty())
		return;

	int n = inb[in.available() - 1];

	if (n == prev || n < 0 || n >= out.length)
		return;

	switch (type) {
		default:
			if (prev != -1) {
				for (int i = 0; i < out.length; i++){
					out[i].write(false);
				}
			}
			out[prev = n].write(true);
			setStatusChar((char)('0' + n), 2, 1);
			break;
	}

	in.purgeAll();
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	super.reinit();
}
/**
 * Element constructor comment.
 */
public void start()  throws Exception {
	prev = Integer.MIN_VALUE;

	for (int i = 0; i < out.length; i++) {
		if (i == initialSelection)
			out[i].write(true);
		else
			out[i].write(false);
	}	
}
}
