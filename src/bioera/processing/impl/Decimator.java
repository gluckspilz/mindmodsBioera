/* Decimator.java v 1.0.9   11/6/04 7:15 PM
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
public final class Decimator extends SingleElement {
	public int sampleOn = 100;
	public int sampleOff = 0;

	private final static String propertiesDescriptions[][] = {
		{"sampleOn", "Pass [samples]", ""},
		{"samplesOff", "Skip [samples]", ""},		
	};	

	private ScalarPipeDistributor out;
	private BufferedScalarPipe in;
	private int inb[], counter;
	boolean on = true;

	protected static boolean debug = bioera.Debugger.get("impl.decimator");
/**
 * Element constructor comment.
 */
public Decimator() {
	super();
	setName("Decimator");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	inb = in.getBuffer();
	out = (ScalarPipeDistributor) outputs[0];
	out.setName("OUT");
	
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Provides undersampling/decimation of data";
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
	
	int i = 0;
	while (i < n) {
		int k = Math.min((n - i), counter);
		counter -= k;
		if (on) {
			out.write(inb, i, k);
			if (counter == 0) {
				on = false;
				counter = sampleOff;
			}
		} else {
			if (counter == 0) {
				on = true;
				counter = sampleOn;
			}			
		}
		i += k;
	}

	in.purgeAll();
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	verifyDesignState(sampleOn >= 0 && sampleOff >= 0);
	counter = sampleOn;
	on = true;
	super.reinit();
}
/**
 * Element constructor comment.
 */
public void start()  throws Exception {
	in.purgeAll();
}
}
