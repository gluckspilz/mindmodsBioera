/* Mixer.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.graph.designer.*;
import bioera.properties.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class Mixer extends SingleElement {
	public ComboProperty function = new ComboProperty(new String[] {
		"SUM",
		"DIFFERENCE",
		"MULTIPLICATION",
		"AVERAGE",
		"MAX",
		"MIN",
		"ONLY_INPUT_A",
		"ONLY_INPUT_B",
		"DIVISION",
		"ABS DIFFERENCE",
		"ASSYMETRICAL AVERAGE",
	});

	private final static String propertiesDescriptions[][] = {
	};	
	
	private final static int SUM = 0;
	private final static int DIFFERENCE = 1;
	private final static int MULTIPLICATION = 2;
	private final static int AVERAGE = 3;
	private final static int MAX = 4;
	private final static int MIN = 5;
	private final static int ONLY_INPUT_A = 6;
	private final static int ONLY_INPUT_B = 7;
	private final static int DIVISION = 8;
	private final static int ABS_DIFFERENCE = 9;
	private final static int ASYNCHR_AVERAGE = 10;
	
	private int type = SUM;
	
	int bufA[];
	int bufB[];
	private BufferedScalarPipe inA, inB;
	private ScalarPipeDistributor out;
	private int maxLevel;
	private int minLevel;
/**
 * SignalDisplay constructor comment.
 */
public Mixer() {
	super();
	setName("Mixer");
	inA = (BufferedScalarPipe)inputs[0];
	inA.setName("A");
	bufA = inA.getBuffer();
	inB = (BufferedScalarPipe)inputs[1];
	inB.setName("B");
	bufB = inB.getBuffer();
	out = (ScalarPipeDistributor) outputs[0];
	outputs[0].setName("O");	
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Mixes two input signals according to chosen function";
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
	switch (type) {
		case SUM:
			int n = Math.min(inA.available(), inB.available());
			for (int i = 0; i < n; i++) {
				out.write(bufA[i] + bufB[i]);
			}
			inA.purge(n);
			inB.purge(n);				
			break;
		case DIFFERENCE:
			n = Math.min(inA.available(), inB.available());
			for (int i = 0; i < n; i++) {
				out.write(bufA[i] - bufB[i]);
			}
			inA.purge(n);
			inB.purge(n);				
			break;
		case ABS_DIFFERENCE:
			n = Math.min(inA.available(), inB.available());
			for (int i = 0; i < n; i++) {
				out.write(Math.abs(bufA[i] - bufB[i]));
			}
			inA.purge(n);
			inB.purge(n);				
			break;
		case MULTIPLICATION:
			n = Math.min(inA.available(), inB.available());
			for (int i = 0; i < n; i++) {
				out.write(bufA[i] * bufB[i]);
			}
			inA.purge(n);
			inB.purge(n);				
			break;
		case AVERAGE:
			n = Math.min(inA.available(), inB.available());
			for (int i = 0; i < n; i++) {
				out.write((bufA[i] + bufB[i]) / 2);
			}
			inA.purge(n);
			inB.purge(n);				
			break;
		case MAX:
			n = Math.min(inA.available(), inB.available());
			for (int i = 0; i < n; i++) {
				out.write(Math.max(bufA[i], bufB[i]));
			}
			inA.purge(n);
			inB.purge(n);				
			break;
		case MIN:
			n = Math.min(inA.available(), inB.available());
			for (int i = 0; i < n; i++) {
				out.write(Math.min(bufA[i], bufB[i]));
			}
			inA.purge(n);
			inB.purge(n);				
			break;
		case ONLY_INPUT_A:
			out.write(bufA, 0, inA.available());
			inA.purgeAll();
			inB.purgeAll();
			break;
		case ONLY_INPUT_B:
			out.write(bufB, 0, inB.available());
			inA.purgeAll();
			inB.purgeAll();
			break;
		case DIVISION:
			n = Math.min(inA.available(), inB.available());
			for (int i = 0; i < n; i++) {
				if (bufB[i] == 0)
					out.write(Integer.MAX_VALUE);
				else
					out.write(bufA[i] / bufB[i]);
			}
			inA.purge(n);
			inB.purge(n);				
			break;
		case ASYNCHR_AVERAGE:
			n = Math.min(inA.available(), out.minAvailableSpace());
			if (n > 0) {
				int m = Math.min(n, inB.available());
				for (int i = 0; i < m; i++) {
					out.write((bufA[i] + bufB[i]) / 2);
				}
				if (n > m)
					out.write(bufA, m, n - m);
				inB.purge(m);
				inA.purge(n);
			}
			break;
	}
}
/**
 * SignalDisplay constructor comment.
 */
public void reinit() throws Exception {
	if (function.getSelectedIndex() == -1)
		function.setSelectedIndex(type);
	else
		type = function.getSelectedIndex();

	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		if (type != ONLY_INPUT_B)
			throw new DesignException("Input A must be connected");
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}
		
	Element predecessorElement1 = getFirstElementConnectedToInput(1);
	if (predecessorElement1 == null) {
		if (type != ONLY_INPUT_A)
			throw new DesignException("Input B must be connected");
	} else if (!predecessorElement1.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}

	if (predecessorElement == null)
		predecessorElement = predecessorElement1;
	
	maxLevel = getSignalParameters().getDigitalRange() - 1;
	minLevel = 0;

	if (!out.isConnected()) {
		throw new DesignException("Output must be connected too");
	}

	super.reinit();
}
}
