/* ScalarInstantTransform.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.fft.*;
import bioera.graph.designer.*;
import bioera.properties.*;
import bioera.processing.*;
import bioera.storage.*;
import bioera.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class ScalarInstantTransform extends SingleElement {
	public ComboProperty function = new ComboProperty(new String[] {
		"INVERTER",
		"BACK DIFFERENCE",
		"ABS",
		"WITHIN DEFAULT RANGE",
		});

	private final static String propertiesDescriptions[][] = {
		{"function", "Processing function", ""},
	};	

	private final static int INVERTER = 0;
	private final static int DIFFERENCE = 1;
	private final static int ABS = 2;
	private final static int WITHIN_RANGE = 3;

	private int type = INVERTER;

	private int tCount;				// Function is performed on this number of samples, until inputLength is reached
		
	private ScalarPipeDistributor out;
	private BufferedScalarPipe in;
	private int inbuffer[];

	private int maxValue, minValue;	
	private int lastDiffWritten = 0;

	protected static boolean debug = bioera.Debugger.get("impl.scalar.singletransform");
/**
 * Element constructor comment.
 */
public ScalarInstantTransform() {
	super();
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Converts input sample according to chosen function";
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
	setName("SS_Transf");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	inbuffer = in.getBuffer();
	out = (ScalarPipeDistributor) outputs[0];
	out.setName("OUT");	
}
/**
 * Element constructor comment.
 */
public final void process() {
	int n = in.available();
	if (n == 0)
		return;

	switch (type) {
		case INVERTER:
			for (int i = 0; i < n; i++){
				out.write(-inbuffer[i]);
			}	
			break;
		case DIFFERENCE:
			for (int i = 0; i < n; i++) {
				out.write(inbuffer[i] - lastDiffWritten);
				lastDiffWritten = inbuffer[i];
			}
			break;
		case ABS:
			for (int i = 0; i < n; i++) {
				out.write(Math.abs(inbuffer[i]));
			}
			break;
		case WITHIN_RANGE:
			for (int i = 0; i < n; i++) {
				if (inbuffer[i] > maxValue)
					out.write(maxValue);
				else if (inbuffer[i] < minValue)
					out.write(minValue);
				else
					out.write(inbuffer[i]);									
			}
			break;			
	}

	in.purgeAll();
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	if (function.getSelectedIndex() == -1)
		function.setSelectedIndex(type);
	else
		type = function.getSelectedIndex();
	
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		reinited = true;
		return;
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}

	maxValue = getSignalParameters().getDigitalMax();
	minValue = getSignalParameters().getDigitalMin();	

	lastDiffWritten = 0;
		
	super.reinit();
}
/**
 * Element constructor comment.
 */
public void start()  throws Exception {
	tCount = 0;
}
}
