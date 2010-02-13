/* LogicalMixer.java v 1.0.9   11/6/04 7:15 PM
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
public final class LogicalMixer extends Element {
	public ComboProperty function = new ComboProperty(new String[] {
		"NOT A",
		"NOT B",
		"A OR B",
		"A AND B",
		"A XOR B"
		});

	public static final int NOT_A = 0;
	public static final int NOT_B = 1;
	public static final int A_OR_B = 2;
	public static final int A_AND_B = 3;
	public static final int A_XOR_B = 4;
	
	int type = 0;
		
	private ScalarPipeDistributor out;
	private BufferedScalarPipe inA;
	private BufferedScalarPipe inB;
	private int inbA[], inbB[];
	private int sA = 1, sB = 1;

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
	int n = inA.available();
	int m = inB.available();

	if (n == 0 && m == 0) {
		return;
	}

	if (n > 0) {
		sA = inbA[n - 1];
		inA.purgeAll();
	}

	if (m > 0) {
		sB = inbB[n - 1];
		inB.purgeAll();
	}
	
	switch (type) {
		case NOT_A:
			if (sA == 0)
				out.write(1);
			else
				out.write(0);
			break;
		case A_OR_B:
			if (sA != 0 || sB != 0)
				out.write(1);
			else
				out.write(0);
			break;
		case A_AND_B:
			if (sA != 0 && sB != 0)
				out.write(1);
			else
				out.write(0);
			break;		
		case A_XOR_B:
			if ((sA != 0) ^ (sB != 0))
				out.write(1);
			else
				out.write(0);
			break;		
		case NOT_B:
			if (sB == 0)
				out.write(1);
			else
				out.write(0);
			break;
	}		
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
		
	super.reinit();
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}

/**
 * Element constructor comment.
 */
public LogicalMixer() {
	super();
	setName("L_Mixer");
	inputs[0] = inA = new BufferedLogicalPipe(this);
	inA.setName("A");
	inbA = inA.getBuffer();
	
	inputs[1] = inB = new BufferedLogicalPipe(this);
	inB.setName("B");
	inbB = inB.getBuffer();
	
	outputs[0] = out = new LogicalPipeDistributor(this) ;
	out.setName("OUT");
	
}
}
