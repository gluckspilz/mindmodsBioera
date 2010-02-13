/* VectorMixer.java v 1.0.9   11/6/04 7:15 PM
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
public final class VectorMixer extends SingleElement {
	public ComboProperty function = new ComboProperty(new String[] {
		"SUM",
		"DIFFERENCE",
		"ABS_DIFFERENCE",
		"MULTIPLICATION",
		"AVERAGE",
		"MAX",
		"MIN",
		"RS",
	});

	private final static String propertiesDescriptions[][] = {
//		{"function", "Processing function", ""},
		
	};	

	
	private final static int SUM = 0;
	private final static int DIFFERENCE = 1;
	private final static int ABS_DIFFERENCE = 2;
	private final static int MULTIPLICATION = 3;
	private final static int AVERAGE = 4;
	private final static int MAX = 5;
	private final static int MIN = 6;
	private final static int RS = 7;
	
	private int type = SUM;
	
	int bufA[][], bufB[][], bA[], bB[], buffer[], vectorSize;
	
	private BufferedVectorPipe inA, inB;
	private VectorPipeDistributor out;
/**
 * SignalDisplay constructor comment.
 */
public VectorMixer() {
	super();
	setName("V_Mixer");
	inputs = new BufferedVectorPipe[2];
	
	inputs[0] = inA = new BufferedVectorPipe(this);
	inA.setName("A");
	bufA = inA.getVBuffer();
	
	inputs[1] = inB = new BufferedVectorPipe(this);
	inB.setName("B");
	bufB = inB.getVBuffer();
	
	outputs = new VectorPipeDistributor[1];
	
	outputs[0] = out = new VectorPipeDistributor(this);
	outputs[0].setName("O");	
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Mixes two input vector streams according to chosen function";
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
	if (inA.isEmpty())
		return;

	int n = Math.min(inA.available(), inB.available());
	if (n == 0)
		return;
	
	switch (type) {
		case SUM:
			for (int i = 0; i < n; i++) {
				bA = bufA[i];
				bB = bufB[i];
				for (int v = 0; v < vectorSize; v++){
					buffer[v] = bA[v] + bB[v];
				}
				out.writeVector(buffer);
			}
			break;
		case DIFFERENCE:
			for (int i = 0; i < n; i++) {
				bA = bufA[i];
				bB = bufB[i];
				for (int v = 0; v < vectorSize; v++){
					buffer[v] = bA[v] - bB[v];
				}
				out.writeVector(buffer);
			}
			break;
		case ABS_DIFFERENCE:
			for (int i = 0; i < n; i++) {
				bA = bufA[i];
				bB = bufB[i];
				for (int v = 0; v < vectorSize; v++){
					buffer[v] = bA[v] - bB[v];
					if (buffer[v] < 0)
						buffer[v] = - buffer[v];
				}
				out.writeVector(buffer);
			}
			break;
		case MULTIPLICATION:
			for (int i = 0; i < n; i++) {
				bA = bufA[i];
				bB = bufB[i];
				for (int v = 0; v < vectorSize; v++){
					buffer[v] = bA[v] * bB[v];
				}
				out.writeVector(buffer);
			}
			break;
		case RS:
			for (int i = 0; i < n; i++) {
				bA = bufA[i];
				bB = bufB[i];
				for (int v = 0; v < vectorSize; v++){
					buffer[v] = bioera.fft.FFTTools.sqrt(bA[v]*bA[v]+ bB[v]*bB[v]);
				}
				out.writeVector(buffer);
			}
			break;
		case AVERAGE:
			for (int i = 0; i < n; i++) {
				bA = bufA[i];
				bB = bufB[i];
				for (int v = 0; v < vectorSize; v++){
					buffer[v] = (bA[v] + bB[v]) / 2;
				}
				out.writeVector(buffer);
			}
			break;
		case MAX:
			for (int i = 0; i < n; i++) {
				bA = bufA[i];
				bB = bufB[i];
				for (int v = 0; v < vectorSize; v++){
					buffer[v] = Math.max(bA[v], bB[v]);
				}
				out.writeVector(buffer);
			}
			break;
		case MIN:
			for (int i = 0; i < n; i++) {
				bA = bufA[i];
				bB = bufB[i];
				for (int v = 0; v < vectorSize; v++){
					buffer[v] = Math.min(bA[v], bB[v]);
				}
				out.writeVector(buffer);
			}
			break;
	}

	inA.purge(n);
	inB.purge(n);	
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
		throw new DesignException("Both inputs must be connected");
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}

	Element predecessorElement2 = getFirstElementConnectedToInput(1);
	if (predecessorElement2 == null) {
		throw new DesignException("Both inputs must be connected");
	} else if (!predecessorElement2.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}

	PipeDistributor pd1 = getFirstDistributorConnectedToInput(0);
	PipeDistributor pd2 = getFirstDistributorConnectedToInput(1);
	if (pd1 == null || !(pd1 instanceof VectorPipe) || pd2 == null || !(pd2 instanceof VectorPipe)) {
		throw new Exception("Element not connected properly");
	}
	
	VectorPipe vp1 = (VectorPipe) pd1;
	VectorPipe vp2 = (VectorPipe) pd2;

	if (vp1.getSignalParameters().getVectorLength() != vp2.getSignalParameters().getVectorLength()) {
		throw new Exception("Input vectors have different sizes");
	}

	if (!out.isConnected()) {
		throw new DesignException("Output must be connected too");
	}
		
	vectorSize = vp1.getSignalParameters().getVectorLength();
	buffer = new int[vectorSize];

	super.reinit();
}
}
